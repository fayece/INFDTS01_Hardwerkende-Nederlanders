package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import nl.hardwerkendenederlanders.hrcms.database.MediaRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.MediaUploadException;
import nl.hardwerkendenederlanders.hrcms.models.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

class MediaServiceImplTest {

    // region Setup
    private MediaRepository mediaRepository;
    private MediaServiceImpl mediaService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        mediaRepository = mock(MediaRepository.class);
        mediaService = new MediaServiceImpl(mediaRepository);
        ReflectionTestUtils.setField(mediaService, "uploadDir", tempDir);
    }
    // endregion

    // region Store method tests
    @Test
    void store_validImage_savesToDiskAndDatabase() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        String resultUrl = mediaService.store(file);

        assertAll(
                "Verify URL format",
                () -> assertTrue(resultUrl.startsWith("/media/")),
                () -> assertTrue(resultUrl.endsWith("test.jpg")),
                () -> assertTrue(resultUrl.length() > "/media/test.jpg".length()));

        verify(file).transferTo(any(Path.class));
        verify(mediaRepository)
                .insert(argThat(item ->
                        item.getMediaType() == MediaType.IMAGE && item.getUrl().equals(resultUrl)));
    }

    @Test
    void store_unsupportedMimeType_throwsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/x-msdownload"); // .exe file
        when(file.getOriginalFilename()).thenReturn("malware.exe");

        MediaUploadException ex = assertThrows(MediaUploadException.class, () -> mediaService.store(file));

        assertTrue(ex.getMessage().contains("unsupported file type: application/x-msdownload"));
        verifyNoInteractions(mediaRepository);
    }

    @Test
    void store_ioException_wrapsCorrectly() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        doThrow(new IOException("Disk full")).when(file).transferTo(any(Path.class));

        MediaUploadException ex = assertThrows(MediaUploadException.class, () -> mediaService.store(file));

        assertTrue(ex.getMessage().contains("could not write file to disk"));
        assertInstanceOf(IOException.class, ex.getCause());
        assertEquals("Disk full", ex.getCause().getMessage());
    }

    @Test
    void store_verifiesMediaTypeMapping() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        mediaService.store(file);

        verify(mediaRepository)
                .insert(argThat(item -> item.getMediaType() == MediaType.DOCUMENT
                        && item.getUrl().endsWith("test.pdf")));
    }
    // endregion
}
