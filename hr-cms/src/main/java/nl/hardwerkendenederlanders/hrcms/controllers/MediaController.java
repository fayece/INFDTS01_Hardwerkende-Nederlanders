package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.models.dtos.media.UploadUrl;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.MediaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public UploadUrl upload(@RequestParam("file") MultipartFile file) {
        String url = mediaService.store(file);
        return new UploadUrl(url);
    }
}
