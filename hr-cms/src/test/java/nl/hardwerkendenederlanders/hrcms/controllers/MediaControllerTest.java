package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import nl.hardwerkendenederlanders.hrcms.models.dtos.media.UploadUrl;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class MediaControllerTest {

    private final MediaService mediaService = mock(MediaService.class);

    private final MediaController mediaController = new MediaController(mediaService);

    @Test
    void upload_success_returnsUrl() {
        MultipartFile file = mock(MultipartFile.class);
        String expectedUrl = "/media/random-uuid_image.png";
        when(mediaService.store(file)).thenReturn(expectedUrl);

        UploadUrl result = mediaController.upload(file);

        assertNotNull(result);
        assertEquals(expectedUrl, result.url());
        verify(mediaService).store(file);
    }
}
