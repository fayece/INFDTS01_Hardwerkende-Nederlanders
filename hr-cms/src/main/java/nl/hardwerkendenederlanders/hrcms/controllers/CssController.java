package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CssController {

    @GetMapping("/css")
    public List<Path> getAllCssPaths() {
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
            Resource[] resources = resolver.getResources("classpath*:/*.css");
            String[] uriPaths = Arrays.stream(resources).map(resource -> resource.getURI());
            for (Resource resource : resources) {
                resource.getURI();
            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}