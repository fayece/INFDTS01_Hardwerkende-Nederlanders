package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Map;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

class MediaControllerTest {

    private final MediaService mediaService = mock(MediaService.class);

    private final MediaController mediaController = new MediaController(mediaService);

    @Test
    void upload_success_returnsUrl() {
        MultipartFile file = mock(MultipartFile.class);
        String expectedUrl = "/media/random-uuid_image.png";
        when(mediaService.store(file)).thenReturn(expectedUrl);

        ResponseEntity<Map<String, String>> result = mediaController.upload(file);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(expectedUrl, result.getBody().get("url"));
        verify(mediaService).store(file);
    }
}
