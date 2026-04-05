package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    String store(MultipartFile file);
}
