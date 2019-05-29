package com.digirati.taxonomy.manager.normalisation;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Collection;
import java.util.Properties;

/**
 * Provides a mechanism for normalising any text inputs, including the terms to search for and the
 * text in which to search.
 */
public class TextNormaliser {

    private final Collection<String> stopwords;

    private final StanfordCoreNLP pipeline;

    /**
     * Constructs a {@link TextNormaliser} capable of correctly lemmatising English text
     *
     * @param stopwords a collection of common boilerplate words to remove from the text (e.g.
     *     "the", "a", etc.)
     */
    public TextNormaliser(Collection<String> stopwords) {
        this(stopwords, "en", "English");
    }

    /**
     * Constructs a {@link TextNormaliser} for normalising any input language. Currently, full
     * lemmatisation is only supported for English due to a restriction of the NLP library that
     * we're using, but language-specific tokenisation should still work
     *
     * @param stopwords a collection of common boilerplate words to remove from the text (e.g.
     *     "the", "a", etc.)
     * @param languageKey the ISO 639-1 language code for the text to be normalised
     * @param languageName the name of the language of the text to be normalised
     */
    public TextNormaliser(Collection<String> stopwords, String languageKey, String languageName) {
        this.stopwords = stopwords;
        this.pipeline = createPipeline(languageKey, languageName);
    }

    /**
     * Applies the NLP pipeline to a piece of input text to produce a normalised version of it. This
     * includes tokenisation, sentence splitting, part of speech tagging, and lemmatisation (if this
     * is supported for the language in question).
     *
     * @param text a non-normalised piece of text
     * @return the normalised form of the input text
     */
    public String normalise(String text) {
        StringBuilder normalisedText = new StringBuilder();
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        document.tokens().stream()
                .filter(this::isContentWord)
                .forEach(coreLabel -> normalisedText.append(coreLabel.lemma()).append(" "));
        return normalisedText.toString().trim();
    }

    private StanfordCoreNLP createPipeline(String languageKey, String languageName) {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        properties.setProperty("tokenize.language", languageKey);
        properties.setProperty("language", languageName);
        return new StanfordCoreNLP(properties);
    }

    private boolean isContentWord(CoreLabel label) {
        return !stopwords.contains(label.originalText()) && !stopwords.contains(label.lemma());
    }
}
