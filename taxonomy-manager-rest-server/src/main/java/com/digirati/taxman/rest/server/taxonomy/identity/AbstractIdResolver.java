package com.digirati.taxman.rest.server.taxonomy.identity;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;
import java.util.regex.Pattern;

abstract class AbstractIdResolver {
    private final Pattern pattern;
    private final String template;

    AbstractIdResolver(String template) {
        if (!template.contains(":id:")) {
            throw new IllegalArgumentException("No :id: variable found in template: " + template);
        }

        this.pattern = Pattern.compile(template.replace(":id:", "([^/]+)"));
        this.template = template.replace(":id:", "{id}");
    }

    public UUID resolve(URI uri) {
        var match = pattern.matcher(uri.getPath());
        if (!match.find()) {
            throw new IllegalArgumentException("No UUID found in URI");
        }

        var uuid = match.group(1);
        return UUID.fromString(uuid);
    }

    public URI resolve(UUID uuid) {
        var uri = getUriInfo().getRequestUri();

        return UriBuilder.fromUri(template)
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .resolveTemplate("id", uuid.toString())
                .build();
    }

    protected abstract UriInfo getUriInfo();
}