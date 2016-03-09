package org.omegat.tokenizer;

import org.omegat.util.Token;

public interface ITokenizer {
    enum StemmingMode {
        NONE, MATCHING, GLOSSARY
    }

    public Token[] tokenizeVerbatim(String string);

    String[] tokenizeWordsToStrings(String str, StemmingMode stemmingMode);
}
