package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CssDiscoveryServiceTest {

    @Autowired
    private CssDiscoveryService _discoveryChannel;

    @Test
    public void mainCssIsReturned() throws IOException {
        Set<String> all_paths = _discoveryChannel.getAllCssPaths();
        assertTrue(all_paths.contains("css/main.css"));
    }
}
