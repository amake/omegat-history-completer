package org.omegat.core.data;

import org.omegat.tokenizer.ITokenizer;

public interface IProject {
    public ITokenizer getSourceTokenizer();
    
    public void iterateByDefaultTranslations(DefaultTranslationsIterator iterator);
    
    public TMXEntry getTranslationInfo(SourceTextEntry ste);
    
    public interface DefaultTranslationsIterator {
        public void iterate(String source, TMXEntry trans);
    }

    public boolean isProjectLoaded();
}
