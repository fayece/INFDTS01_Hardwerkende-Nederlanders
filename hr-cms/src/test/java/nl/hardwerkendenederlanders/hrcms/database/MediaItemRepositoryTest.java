package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.MediaItemRepository;
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
public class MediaItemRepositoryTest {

    @Autowired
    private MediaItemRepository mediaItemRepository;

    @Test
    void insertMediaItem_withValidMediaItem_shouldPersistAndRetrieve() {
        MediaItem mediaItem = new MediaItem("https://example.com/image.jpg", MediaType.IMAGE);

        mediaItemRepository.insert(mediaItem);

        MediaItem retrieved = mediaItemRepository.findById(mediaItem.getId());
        assertNotNull(retrieved);
        assertEquals(mediaItem.getId(), retrieved.getId());
        assertEquals(mediaItem.getUrl(), retrieved.getUrl());
        assertEquals(mediaItem.getMediaType(), retrieved.getMediaType());
    }

    @Test
    void updateMediaItem_withModifiedFields_shouldReflectChanges() {
        MediaItem mediaItem = new MediaItem("https://example.com/image.jpg", MediaType.IMAGE);

        mediaItemRepository.insert(mediaItem);

        String newUrl = "https://example.com/new-video.mp4";
        MediaType newMediaType = MediaType.VIDEO;

        mediaItem.setUrl(newUrl);
        mediaItem.setMediaType(newMediaType);
        mediaItemRepository.update(mediaItem);

        MediaItem retrieved = mediaItemRepository.findById(mediaItem.getId());
        assertNotNull(retrieved);
        assertEquals(mediaItem.getId(), retrieved.getId());
        assertEquals(newUrl, retrieved.getUrl());
        assertEquals(newMediaType, retrieved.getMediaType());
    }

    @Test
    void findMediaItemById_withExistingId_shouldReturnMediaItem() {
        MediaItem mediaItem = new MediaItem("https://example.com/image.jpg", MediaType.IMAGE);

        mediaItemRepository.insert(mediaItem);

        MediaItem retrieved = mediaItemRepository.findById(mediaItem.getId());
        assertNotNull(retrieved);
        assertEquals(mediaItem.getId(), retrieved.getId());
        assertEquals(mediaItem.getUrl(), retrieved.getUrl());
        assertEquals(mediaItem.getMediaType(), retrieved.getMediaType());
    }

    @Test
    void findMediaItemById_withNonExistingId_shouldReturnNull() {
        assertThrows(Exception.class, () -> mediaItemRepository.findById(UUID.randomUUID()));
    }

    @Test
    void deleteMediaItem_withExistingId_shouldRemoveMediaItem() {
        MediaItem mediaItem = new MediaItem("https://example.com/image.jpg", MediaType.IMAGE);

        mediaItemRepository.insert(mediaItem);

        mediaItemRepository.delete(mediaItem.getId());

        assertThrows(Exception.class, () -> mediaItemRepository.findById(mediaItem.getId()));
    }

    @Test
    void findAllMediaItemsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            MediaItem mediaItem = new MediaItem("https://example.com/image" + i + ".jpg", MediaType.IMAGE);
            mediaItemRepository.insert(mediaItem);
        }

        var page1 = mediaItemRepository.findAllPaged(1, 10);
        var page2 = mediaItemRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllMediaItemsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> mediaItemRepository.findAllPaged(offset, limit));
    }
}
