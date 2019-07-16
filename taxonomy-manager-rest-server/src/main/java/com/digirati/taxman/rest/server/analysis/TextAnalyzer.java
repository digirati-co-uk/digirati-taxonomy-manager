package com.digirati.taxman.rest.server.analysis;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.analysis.TextAnalysisInput;
import com.digirati.taxman.rest.server.infrastructure.config.TextLookupServiceProvider;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxonomy.manager.lookup.TextLookupService;
import org.apache.jena.vocabulary.SKOS;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.stream.Collectors;

@ApplicationScoped
public class TextAnalyzer {

    @Inject
    TextLookupServiceProvider lookupServiceProvider;

    @Inject
    RdfModelFactory modelFactory;

    @Inject
    ConceptModelRepository concepts;

    /**
     * Run the auto-tagger implementation on the given {@code input} and return a collection of {@link ConceptModel}s that
     * are found in the input.
     *
     * @param input The document to tag.
     * @return A list of {@link ConceptModel}s appearing as tags.
     */
    public CollectionModel tagDocument(TextAnalysisInput input) {
        TextLookupService textLookupService = lookupServiceProvider.getTextLookupService();
        var ctx = textLookupService.search(input.getText());
        var matches = ctx.getMatchedConcepts();

        try {
            var builder = modelFactory.createBuilder(CollectionModel.class);
            builder.setUri(URI.create("urn:collection"));

            var matchedConceptUuids = matches.parallelStream()
                    .flatMap(match -> match.getConceptIds().stream())
                    .collect(Collectors.toList());

            var matchedConcepts = concepts.findAll(matchedConceptUuids);
            matchedConcepts.forEach(concept -> builder.addEmbeddedModel(SKOS.member, concept));

            return builder.build();
        } catch (RdfModelException ex) {
            throw new WebApplicationException("Produced invalid RDF model tagging document", ex);
        }
    }
}
