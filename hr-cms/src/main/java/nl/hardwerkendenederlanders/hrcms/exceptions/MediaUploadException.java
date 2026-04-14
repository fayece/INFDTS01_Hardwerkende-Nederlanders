package nl.hardwerkendenederlanders.hrcms.exceptions;

import lombok.Getter;

@Getter
public class MediaUploadException extends RuntimeException {

    public MediaUploadException(String reason) {
        super("Media upload failed: " + reason);
    }

    public MediaUploadException(String reason, Throwable cause) {
        super("Media upload failed: " + reason, cause);
    }
}
