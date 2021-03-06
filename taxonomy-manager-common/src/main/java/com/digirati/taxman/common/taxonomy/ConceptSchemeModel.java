package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.PersistentModel;
import com.digirati.taxman.common.rdf.PersistentProjectScopedModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelContext;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RdfType("http://www.w3.org/2004/02/skos/core#ConceptScheme")
@RdfContext(value = {"skos=" + SKOS.uri, "dcterms=" + DCTerms.NS}, template = "/v0.1/concept-scheme/:id:")
public final class ConceptSchemeModel implements RdfModel, PersistentProjectScopedModel {

    private final RdfModelContext context;
    private UUID uuid;
    private String projectId;
    private List<ConceptModel> models;

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
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public String getSource() {
        Resource sourceResource = getResource().getPropertyResourceValue(DCTerms.source);
        return sourceResource == null ? null : sourceResource.getURI();
    }

    public Multimap<String, String> getTitle() {
        return getPlainLiteral(DCTerms.title);
    }

    public void setTopConcepts(List<ConceptModel> models) {
        this.models = models;
    }

    /**
     * Get a stream of all the concepts that are related to this concept scheme.
     */
    public Stream<ConceptModel> getTopConcepts() {
        return models != null ? models.stream() : getResources(ConceptModel.class, SKOS.hasTopConcept);
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(String id) {
        this.projectId = id;
    }
}
