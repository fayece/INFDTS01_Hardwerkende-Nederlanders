package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class CssController {

    private final ResourcePatternResolver _resourcePatternResolver;

    public CssController(ResourcePatternResolver resourcePatternResolver) {
        this._resourcePatternResolver = resourcePatternResolver;
    }

    @GetMapping("/css")
    public Set<String> getAllCssPaths() throws IOException {

        Resource[] cssResources = _resourcePatternResolver.getResources("classpath:/static/css/**/*.css");
        var cssSet = new HashSet<String>();
        for (Resource resource: cssResources){
            cssSet.add("css/" + resource.getURL().getPath().split("static/css/")[1]);
        }
        return cssSet;
    }

}