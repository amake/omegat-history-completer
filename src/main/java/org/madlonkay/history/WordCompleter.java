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

import org.omegat.gui.editor.autocompleter.AutoCompleterItem;
import org.omegat.util.Preferences;
import org.trie4j.patricia.PatriciaTrie;

public class WordCompleter {

    static final int DEFAULT_MIN_CHARS = 3;
    int minSeedLength = Preferences.getPreferenceDefault(Installer.PREFERENCE_COMPLETION_MIN_CHARS,
            DEFAULT_MIN_CHARS);

    private PatriciaTrie data;

    void reset() {
        data = new PatriciaTrie();
    }

    void train(String text, String[] tokens) {
        if (text.codePointCount(0, text.length()) >= minSeedLength + 1) {
            for (String token : tokens) {
                if (token.codePointCount(0, token.length()) > minSeedLength) {
                    data.insert(token);
                }
            }
        }
    }

    List<AutoCompleterItem> completeWord(String[] tokens) {
        String seed = tokens[tokens.length - 1];
        if (data == null || seed.codePointCount(0, seed.length()) < minSeedLength) {
            return new ArrayList<>(1);
        }

        List<AutoCompleterItem> result = new ArrayList<AutoCompleterItem>();
        for (String s : data.predictiveSearch(seed)) {
            if (!s.equalsIgnoreCase(seed)) {
                result.add(new AutoCompleterItem(s, null, seed.length()));
            }
        }
        return result;
    }
}
