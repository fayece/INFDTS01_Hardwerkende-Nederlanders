package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    @NotNull
    String store(MultipartFile file);
}
