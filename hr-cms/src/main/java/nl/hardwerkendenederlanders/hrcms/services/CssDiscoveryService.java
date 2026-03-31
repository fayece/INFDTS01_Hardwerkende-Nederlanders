package nl.hardwerkendenederlanders.hrcms.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class CssDiscoveryService {
    private final ResourcePatternResolver _resourcePatternResolver;
    public CssDiscoveryService(ResourcePatternResolver resourcePatternResolver) {
        this._resourcePatternResolver = resourcePatternResolver;
    }

    public Set<String> getAllCssPaths() throws IOException {

        Resource[] cssResources = _resourcePatternResolver.getResources("classpath:/static/css/**/*.css");
        var cssSet = new HashSet<String>();
        for (Resource resource: cssResources){
            cssSet.add("css/" + resource.getURL().getPath().split("static/css/")[1]);
        }

        return cssSet;
    }
}


