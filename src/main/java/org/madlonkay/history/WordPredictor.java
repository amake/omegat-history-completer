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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omegat.gui.editor.autocompleter.AutoCompleterItem;

public class WordPredictor {
    final static double MIN_FREQUENCY = 5d;

    private Map<String, FrequencyStrings> data;

    void reset() {
        data = new HashMap<>();
    }

    void trainStringPrediction(String text, String[] tokens) {
        for (int i = 0; i < tokens.length - 1; i++) {
            String token = tokens[i];
            FrequencyStrings strings = data.get(token);
            if (strings == null) {
                strings = new FrequencyStrings();
                data.put(token, strings);
            }
            strings.encounter(tokens[i + 1]);
        }
    }

    List<AutoCompleterItem> predictWord(String[] tokens) {
        String seed = lastWordToken(tokens);
        if (data == null || seed == null) {
            return new ArrayList<>(1);
        }

        List<AutoCompleterItem> result = new ArrayList<AutoCompleterItem>();
        FrequencyStrings predictions = data.get(seed);
        if (predictions == null) {
            return new ArrayList<>(1);
        }
        List<Entry<String, Integer>> entries = predictions.getSortedEntries();
        int total = 0;
        for (Entry<String, Integer> e : entries) {
            total += e.getValue();
        }
        for (Entry<String, Integer> e : entries) {
            double percent = ((double) e.getValue() / total) * 100;
            if (percent >= MIN_FREQUENCY) {
                result.add(new AutoCompleterItem(e.getKey(), new String[] { String.valueOf(Math.round(percent)) + "%" },
                        0));
            }
        }
        return result;
    }

    private String lastWordToken(String[] tokens) {
        for (int i = tokens.length - 1; i >= 0; i--) {
            String token = tokens[i];
            if (!token.trim().isEmpty()) {
                return token;
            }
        }
        return null;
    }

    static class FrequencyStrings {
        final private Map<String, Integer> map = new HashMap<>();

        public void encounter(String string) {
            synchronized (map) {
                Integer count = map.get(string);
                map.put(string, count == null ? 0 : count + 1);
            }
        }

        public List<Entry<String, Integer>> getSortedEntries() {
            List<Entry<String, Integer>> entries;
            synchronized (map) {
                entries = new ArrayList<>(map.entrySet());
            }
            Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
                @Override
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                    return -Integer.compare(o1.getValue(), o2.getValue());
                }
            });
            return entries;
        }
    }
}
