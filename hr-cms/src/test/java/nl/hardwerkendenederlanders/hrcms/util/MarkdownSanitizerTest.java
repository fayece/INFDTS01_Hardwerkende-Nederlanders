package nl.hardwerkendenederlanders.hrcms.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MarkdownSanitizerTest {

    @Test
    void sanitizeComment_preservesAllowedElements() {
        String input = "This is **bold**, *italic*, ~~strikethrough~~, and an ![image](src).";

        String result = MarkdownSanitizer.sanitizeComment(input);

        assertEquals("This is **bold**, *italic*, ~~strikethrough~~, and an ![image](src).", result.trim());
    }

    @Test
    void sanitizeComment_removesHeaders_butKeepsText() {
        String input = "# Header 1\n## Header 2\nNormal text";
        String result = MarkdownSanitizer.sanitizeComment(input);

        assertEquals("Header 1\nHeader 2\nNormal text", result.trim());
    }

    @Test
    void sanitizeComment_removesLinks_butKeepsAnchorText() {
        String input = "Check this [link](https://google.com).";
        String result = MarkdownSanitizer.sanitizeComment(input);

        assertEquals("Check this link.", result.trim());
    }

    @Test
    void sanitizeComment_stripsCodeBlocks() {
        String input = "Here is code: `java code` and block:\n```java\nint x = 5;\n```";
        String result = MarkdownSanitizer.sanitizeComment(input);

        String sanitized = result.trim();
        assertTrue(sanitized.contains("java code"));
        assertTrue(sanitized.contains("int x = 5;"));
    }

    @ParameterizedTest
    @CsvSource({
        "'**Bold text**', '**Bold text**'",
        "'*Italic text*', '*Italic text*'",
        "'~~Strikethrough~~', '~~Strikethrough~~'",
        "'> Blockquote', 'Blockquote'"
    })
    void sanitizeComment_parameterizedCheck(String input, String expected) {
        assertEquals(expected, MarkdownSanitizer.sanitizeComment(input).trim());
    }

    @Test
    void sanitizeComment_handlesNestedFormatting() {
        String input = "**Bold and ~~strikethrough~~ combined**";
        String result = MarkdownSanitizer.sanitizeComment(input);

        assertEquals("**Bold and ~~strikethrough~~ combined**", result.trim());
    }

    @Test
    void sanitizeComment_handlesEmptyInput() {
        assertEquals("", MarkdownSanitizer.sanitizeComment(null).trim());
        assertEquals("", MarkdownSanitizer.sanitizeComment("").trim());
    }
}
