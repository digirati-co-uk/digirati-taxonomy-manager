package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.PersistentModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelContext;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@RdfType("http://www.w3.org/2004/02/skos/core#ConceptScheme")
@RdfContext({"skos=" + SKOS.uri, "dcterms=" + DCTerms.NS})
public class ConceptSchemeModel implements RdfModel, PersistentModel {

    private final RdfModelContext context;
    private UUID uuid;

    @RdfConstructor
    public ConceptSchemeModel(RdfModelContext context) {
        this(null, context);
    }

    public ConceptSchemeModel(UUID uuid, RdfModelContext context) {
        this.uuid = uuid;
        this.context = context;
    }

    public RdfModelContext getContext() {
        return context;
    }

    @Override
    public UUID getUuid() {
        if (uuid == null) {
            throw new IllegalStateException("Concept schemes must have a UUID.");
        }
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public String getSource() {
        Resource sourceResource = getResource().getPropertyResourceValue(DCTerms.source);
        return sourceResource == null ? null : sourceResource.getURI();
    }

    public Map<String, String> getTitle() {
        return getPlainLiteral(DCTerms.title);
    }



    /**
     * Get a stream of all the concepts that are related to this concept scheme.
     */
    public Stream<ConceptModel> getTopConcepts() {
        return getResources(ConceptModel.class, SKOS.hasTopConcept);
    }
}
