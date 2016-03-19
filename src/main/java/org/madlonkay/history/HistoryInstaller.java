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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.util.Preferences;

public class HistoryInstaller {
    
    static final String PREFERENCE_ENABLED = "allow_history_completer";
    static final String PREFERENCE_MIN_CHARS = "history_completer_min_chars";
    static final String MENU_TITLE = "History Completer";
    static final String MENU_ITEM_ONOFF = "Enabled";
    static final String MENU_ITEM_CHARS_LABEL = "Complete after:";
    static final String MENU_ITEM_NCHARS_ONE = "%d character";
    static final String MENU_ITEM_NCHARS_OTHER = "%d characters";
    static final int MIN_CHARS_MIN = 1;
    static final int MIN_CHARS_MAX = 5;

    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                install();
            }

            @Override
            public void onApplicationShutdown() {
            }
        });
    }

    private static void install() {
        final HistoryCompleter completer = new HistoryCompleter();
        Core.getEditor().getAutoCompleter().addView(completer);
        JMenu menu = new JMenu(MENU_TITLE);

        JMenuItem item = new JCheckBoxMenuItem(MENU_ITEM_ONOFF);
        item.setSelected(Preferences.isPreference(PREFERENCE_ENABLED));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.setPreference(PREFERENCE_ENABLED, ((AbstractButton) e.getSource()).isSelected());
            }
        });
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem(MENU_ITEM_CHARS_LABEL);
        item.setEnabled(false);
        menu.add(item);
        int minChars = Preferences.getPreferenceDefault(PREFERENCE_MIN_CHARS,
                HistoryCompleter.DEFAULT_MIN_CHARS);
        ButtonGroup group = new ButtonGroup();
        for (int i = MIN_CHARS_MIN; i <= MIN_CHARS_MAX; i++) {
            final int chars = i;
            item = new JRadioButtonMenuItem(
                    String.format(chars == 1 ? MENU_ITEM_NCHARS_ONE : MENU_ITEM_NCHARS_OTHER, chars));
            item.setSelected(chars == minChars);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Preferences.setPreference(PREFERENCE_MIN_CHARS, chars);
                    completer.minSeedLength = chars;
                    if (Core.getProject().isProjectLoaded()) {
                        completer.train();
                    }
                }
            });
            group.add(item);
            menu.add(item);
        }


        Core.getMainWindow().getMainMenu().getAutoCompletionMenu().add(menu);
    }

    public static void unloadPlugins() {
    }
}
