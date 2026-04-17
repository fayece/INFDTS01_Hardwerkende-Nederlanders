package nl.hardwerkendenederlanders.hrcms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.MediaRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.MediaUploadException;
import nl.hardwerkendenederlanders.hrcms.models.MediaItem;
import nl.hardwerkendenederlanders.hrcms.models.MediaType;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.MediaService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final Path uploadDir = Paths.get("uploads/media");

    public MediaServiceImpl(MediaRepository mediaRepository) throws IOException {
        this.mediaRepository = mediaRepository;
        Files.createDirectories(uploadDir);
    }

    @NotNull
    public String store(MultipartFile file) {
        try {
            System.out.println("Uploading to: " + uploadDir.toAbsolutePath());

            String mimeType = file.getContentType();
            validateMimeType(mimeType);
            MediaType mediaType = resolveMediaType(mimeType);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = uploadDir.resolve(fileName);
            file.transferTo(destination);

            String url = "/media/" + fileName;
            mediaRepository.insert(
                    MediaItem.builder().url(url).mediaType(mediaType).build());

            return url;
        } catch (IOException e) {
            throw new MediaUploadException("could not write file to disk", e);
        }
    }

    private void validateMimeType(String mimeType) {
        if (mimeType == null || !ALLOWED_MIME_TYPES.containsKey(mimeType)) {
            throw new MediaUploadException("unsupported file type: " + mimeType);
        }
    }

    private MediaType resolveMediaType(String mimeType) {
        return ALLOWED_MIME_TYPES.get(mimeType);
    }

    private static final Map<String, MediaType> ALLOWED_MIME_TYPES = Map.of(
            "image/jpeg", MediaType.IMAGE,
            "image/png", MediaType.IMAGE,
            "image/webp", MediaType.IMAGE,
            "image/gif", MediaType.GIF,
            "video/mp4", MediaType.VIDEO,
            "video/webm", MediaType.VIDEO,
            "audio/mpeg", MediaType.AUDIO,
            "audio/wav", MediaType.AUDIO,
            "application/pdf", MediaType.DOCUMENT);
}
