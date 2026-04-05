package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcMediaRepository;
import nl.hardwerkendenederlanders.hrcms.models.MediaItem;
import nl.hardwerkendenederlanders.hrcms.models.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class JdbcMediaRepositoryTest {

    @Autowired
    private JdbcMediaRepository jdbcMediaRepository;

    @Test
    void insertMediaItem_withValidMediaItem_shouldPersistAndRetrieve() {
        MediaItem mediaItem = MediaItem.builder()
                .url("https://example.com/image.jpg")
                .mediaType(MediaType.IMAGE)
                .build();

        jdbcMediaRepository.insert(mediaItem);

        MediaItem retrieved = jdbcMediaRepository.findById(mediaItem.getId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals(mediaItem.getId(), retrieved.getId());
        assertEquals(mediaItem.getUrl(), retrieved.getUrl());
        assertEquals(mediaItem.getMediaType(), retrieved.getMediaType());
    }

    @Test
    void updateMediaItem_withModifiedFields_shouldReflectChanges() {
        MediaItem mediaItem = MediaItem.builder()
                .url("https://example.com/image.jpg")
                .mediaType(MediaType.IMAGE)
                .build();

        jdbcMediaRepository.insert(mediaItem);

        String newUrl = "https://example.com/new-video.mp4";
        MediaType newMediaType = MediaType.VIDEO;

        mediaItem.setUrl(newUrl);
        mediaItem.setMediaType(newMediaType);
        jdbcMediaRepository.update(mediaItem);

        MediaItem retrieved = jdbcMediaRepository.findById(mediaItem.getId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals(mediaItem.getId(), retrieved.getId());
        assertEquals(newUrl, retrieved.getUrl());
        assertEquals(newMediaType, retrieved.getMediaType());
    }

    @Test
    void findMediaItemById_withExistingId_shouldReturnMediaItem() {
        MediaItem mediaItem = MediaItem.builder()
                .url("https://example.com/image.jpg")
                .mediaType(MediaType.IMAGE)
                .build();

        jdbcMediaRepository.insert(mediaItem);

        MediaItem retrieved = jdbcMediaRepository.findById(mediaItem.getId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals(mediaItem.getId(), retrieved.getId());
        assertEquals(mediaItem.getUrl(), retrieved.getUrl());
        assertEquals(mediaItem.getMediaType(), retrieved.getMediaType());
    }

    @Test
    void findMediaItemById_withNonExistingId_shouldReturnNull() {
        assertThrows(Exception.class, () -> jdbcMediaRepository.findById(UUID.randomUUID()));
    }

    @Test
    void deleteMediaItem_withExistingId_shouldRemoveMediaItem() {
        MediaItem mediaItem = MediaItem.builder()
                .url("https://example.com/image.jpg")
                .mediaType(MediaType.IMAGE)
                .build();

        jdbcMediaRepository.insert(mediaItem);

        jdbcMediaRepository.delete(mediaItem.getId());

        assertThrows(Exception.class, () -> jdbcMediaRepository.findById(mediaItem.getId()));
    }

    @Test
    void findAllMediaItemsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            MediaItem mediaItem = MediaItem.builder()
                    .url("https://example.com/image.jpg")
                    .mediaType(MediaType.IMAGE)
                    .build();
            jdbcMediaRepository.insert(mediaItem);
        }

        var page1 = jdbcMediaRepository.findAllPaged(1, 10);
        var page2 = jdbcMediaRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllMediaItemsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> jdbcMediaRepository.findAllPaged(offset, limit));
    }
}
