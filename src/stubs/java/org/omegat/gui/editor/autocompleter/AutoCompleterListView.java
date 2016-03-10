package org.omegat.gui.editor.autocompleter;

import java.util.List;

public abstract class AutoCompleterListView extends AbstractAutoCompleterView {
    public AutoCompleterListView(String name) {
        super(name);
    }
    
    public abstract List<AutoCompleterItem> computeListData(String prevText, boolean contextualOnly);
    public abstract String itemToString(AutoCompleterItem item);
    public boolean shouldPopUp() {
        return false;
    }

    protected String getLastToken(String text) {
        return null;
    }
}
