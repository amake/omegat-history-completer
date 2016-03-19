package org.omegat.core.data;

import org.omegat.tokenizer.ITokenizer;

public interface IProject {
    public ITokenizer getSourceTokenizer();
    
    public void iterateByDefaultTranslations(DefaultTranslationsIterator iterator);
    
    void iterateByMultipleTranslations(MultipleTranslationsIterator it);

    public TMXEntry getTranslationInfo(SourceTextEntry ste);
    
    public interface DefaultTranslationsIterator {
        public void iterate(String source, TMXEntry trans);
    }

    public interface MultipleTranslationsIterator {
        public void iterate(EntryKey source, TMXEntry trans);
    }

    public boolean isProjectLoaded();
}
