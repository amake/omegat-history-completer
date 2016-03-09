/*
 * Copyright (C) 2015 Aaron Madlon-Kay <aaron@madlon-kay.com>
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
import org.omegat.core.data.IProject.DefaultTranslationsIterator;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.data.TMXEntry;
import org.omegat.core.events.IEntryEventListener;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.gui.editor.autocompleter.AutoCompleterItem;
import org.omegat.gui.editor.autocompleter.AutoCompleterListView;
import org.omegat.tokenizer.ITokenizer;
import org.omegat.tokenizer.ITokenizer.StemmingMode;
import org.omegat.util.Preferences;
import org.omegat.util.Token;
import org.trie4j.patricia.PatriciaTrie;

public class HistoryCompleter extends AutoCompleterListView {

    private static final int MIN_SEED_LENGTH = 3;

    private PatriciaTrie data;
    private SourceTextEntry currentEntry;
    private TMXEntry currentEntryTranslation;
    
    public HistoryCompleter() {
        super("History Completer");
        
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
    
    private void train() {
        data = new PatriciaTrie();
        Core.getProject().iterateByDefaultTranslations(new DefaultTranslationsIterator() {
            @Override
            public void iterate(String source, TMXEntry trans) {
                trainString(trans.translation);
            }
        });
    }
    
    private void trainString(String text) {
        if (text == null) {
            return;
        }
        if (text.codePointCount(0, text.length()) < MIN_SEED_LENGTH + 1) {
            return;
        }
        ITokenizer tokenizer = getTokenizer();
        for (String token : tokenizer.tokenizeWordsToStrings(text, StemmingMode.NONE)) {
            if (token.codePointCount(0, token.length()) > MIN_SEED_LENGTH) {
                data.insert(token);
            }
        }
    }
    
    protected String getLastToken(String text) {
        ITokenizer tokenizer = getTokenizer();
        Token[] tokens = tokenizer.tokenizeVerbatim(text);
        
        for (int i = tokens.length - 1; i >= 0; i--) {
            Token lastToken = tokens[i];
            String lastString = text.substring(lastToken.getOffset(), text.length()).trim();
            if (lastString.codePointCount(0, lastString.length()) >= MIN_SEED_LENGTH) {
                return lastString;
            }
        }
        return null;
    }
    
    private List<AutoCompleterItem> generate(String prevText) {
        String seed = getLastToken(prevText);
        if (seed == null) {
            seed = prevText;
        }
        if (data == null || seed.codePointCount(0, seed.length()) < MIN_SEED_LENGTH) {
            return new ArrayList<AutoCompleterItem>(1);
        }

        List<AutoCompleterItem> result = new ArrayList<AutoCompleterItem>();
        for (String s : data.predictiveSearch(seed)) {
            if (!s.equalsIgnoreCase(seed)) {
                result.add(new AutoCompleterItem(s, null, seed.length()));
            }
        }
        return result;
    }
    
    @Override
    public List<AutoCompleterItem> computeListData(String prevText,
            boolean contextualOnly) {
        if (prevText.codePointCount(0, prevText.length()) < MIN_SEED_LENGTH) {
            return new ArrayList<AutoCompleterItem>(1);
        }
        return generate(prevText);
    }

    @Override
    public String itemToString(AutoCompleterItem item) {
        return item.payload;
    }

    @Override
    public boolean shouldPopUp() {
        return Preferences.isPreference(HistoryInstaller.HISTORY_COMPLETER_PREFERENCE)
                && super.shouldPopUp();
    }
}
