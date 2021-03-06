/*
 * Copyright (C) 2016 Aaron Madlon-Kay <aaron@madlon-kay.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.madlonkay.history;

import java.util.ArrayList;
import java.util.List;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.data.EntryKey;
import org.omegat.core.data.IProject.DefaultTranslationsIterator;
import org.omegat.core.data.IProject.MultipleTranslationsIterator;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.data.TMXEntry;
import org.omegat.core.events.IEntryEventListener;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.gui.editor.autocompleter.AutoCompleterItem;
import org.omegat.gui.editor.autocompleter.AutoCompleterListView;
import org.omegat.tokenizer.ITokenizer.StemmingMode;
import org.omegat.util.Preferences;

public class HistoryPredictor extends AutoCompleterListView {


    WordPredictor predictor = new WordPredictor();
    private SourceTextEntry currentEntry;
    private TMXEntry currentEntryTranslation;

    public HistoryPredictor() {
        super("History Predictions");
        
        CoreEvents.registerProjectChangeListener(new IProjectEventListener() {
            @Override
            public void onProjectChanged(PROJECT_CHANGE_TYPE eventType) {
                if (eventType == PROJECT_CHANGE_TYPE.LOAD) {
                    train();
                }
            }
        });
        CoreEvents.registerEntryEventListener(new IEntryEventListener() {            
            @Override
            public void onNewFile(String activeFileName) {
            }
            @Override
            public void onEntryActivated(SourceTextEntry newEntry) {
                SourceTextEntry lastEntry = currentEntry;
                TMXEntry lastEntryTranslation = currentEntryTranslation;
                if (lastEntry != null && lastEntryTranslation != null && !lastEntryTranslation.isTranslated()) {
                    TMXEntry newTranslation = Core.getProject().getTranslationInfo(lastEntry);
                    trainString(newTranslation.translation);
                }
                currentEntry = newEntry;
                currentEntryTranslation = Core.getProject().getTranslationInfo(newEntry);
            }
        });
    }
    
    synchronized void train() {
        predictor.reset();
        Core.getProject().iterateByDefaultTranslations(new DefaultTranslationsIterator() {
            @Override
            public void iterate(String source, TMXEntry trans) {
                trainString(trans.translation);
            }
        });
        Core.getProject().iterateByMultipleTranslations(new MultipleTranslationsIterator() {
            @Override
            public void iterate(EntryKey source, TMXEntry trans) {
                trainString(trans.translation);
            }
        });
    }
    
    private void trainString(String text) {
        if (text == null) {
            return;
        }
        String[] tokens = getTokenizer().tokenizeWordsToStrings(text, StemmingMode.NONE);
        
        predictor.trainStringPrediction(text, tokens);
    }

    @Override
    public List<AutoCompleterItem> computeListData(String prevText, boolean contextualOnly) {
        if (prevText == null || prevText.isEmpty()) {
            return new ArrayList<>(1);
        }

        String[] tokens = getTokenizer().tokenizeVerbatimToStrings(prevText);

        List<AutoCompleterItem> predictions = predictor.predictWord(tokens);

        if (predictions.isEmpty()) {
            return predictions;
        }

        // We are starting a new word so all predictions are relevant
        if (tokens[tokens.length - 1].trim().isEmpty()) {
            return predictions;
        }

        // We have context to filter on
        String context = tokens[tokens.length - 1];
        List<AutoCompleterItem> result = new ArrayList<>();
        for (AutoCompleterItem item : predictions) {
            if (item.payload.startsWith(context) && !item.payload.equals(context)) {
                result.add(new AutoCompleterItem(item.payload, item.extras, context.length()));
            }
        }
        return result;
    }

    @Override
    public String itemToString(AutoCompleterItem item) {
        return "<html>" + item.payload + " <font color=\"gray\">(" + item.extras[0] + ")</font></html>";
    }

    @Override
    public boolean shouldPopUp() {
        return Preferences.isPreference(Installer.PREFERENCE_AUTOMATIC)
                && Preferences.isPreference(Installer.PREFERENCE_PREDICTION_ENABLED)
                && super.shouldPopUp();
    }
}
