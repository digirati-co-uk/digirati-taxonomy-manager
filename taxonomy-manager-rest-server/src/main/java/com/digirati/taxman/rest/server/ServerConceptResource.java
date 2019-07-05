package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxman.rest.taxonomy.ConceptPath;
import com.digirati.taxman.rest.taxonomy.ConceptResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ServerConceptResource implements ConceptResource {

    @Inject
    ConceptModelRepository concepts;

    @Override
    public Response createConcept(@Valid ConceptModel model) {
        var updatedModel = concepts.create(model);
        var uri = updatedModel.getUri();

        return Response.created(uri).entity(updatedModel).build();
    }

    @Override
    public Response getConcept(@BeanParam ConceptPath params) {
        var model = concepts.find(params.getUuid());

        return Response.ok(model).build();
    }

    @Override
    public Response getConceptsByPartialLabel(String partialLabel) {
        CollectionModel matches = concepts.findByPartialLabel(partialLabel);

        return Response.ok(matches).build();
    }

    @Override
    public Response updateConcept(@BeanParam ConceptPath params, @Valid ConceptModel model) {
        model.setUuid(params.getUuid());
        concepts.update(model);

        return Response.noContent().build();
    }
}
