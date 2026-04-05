package nl.hardwerkendenederlanders.hrcms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class MarkdownSanitizer {

    private static final Parser PARSER = Parser.builder()
            .extensions(List.of(StrikethroughExtension.create()))
            .build();

    public static String sanitizeComment(String markdown) {
        return sanitize(
                markdown,
                Set.of(
                        Document.class,
                        Paragraph.class,
                        Text.class,
                        Emphasis.class,
                        StrongEmphasis.class,
                        Strikethrough.class,
                        Image.class,
                        SoftLineBreak.class,
                        HardLineBreak.class));
    }

    private static String sanitize(String markdown, Set<Class<? extends Node>> allowedNodes) {
        Node document = PARSER.parse(markdown);
        stripDisallowed(document, allowedNodes);
        return MarkdownRenderer.builder().build().render(document);
    }

    private static void stripDisallowed(Node node, Set<Class<? extends Node>> allowedNodes) {
        Node child = node.getFirstChild();
        while (child != null) {
            Node next = child.getNext();

            if (allowedNodes.contains(child.getClass())) {
                stripDisallowed(child, allowedNodes);
            } else {
                Node finalChild = child;
                extractText(child).forEach(text -> finalChild.insertBefore(new Text(text)));
                child.unlink();
            }

            child = next;
        }
    }

    private static List<String> extractText(Node node) {
        List<String> texts = new ArrayList<>();
        Node child = node.getFirstChild();
        while (child != null) {
            if (child instanceof Text t) texts.add(t.getLiteral());
            else texts.addAll(extractText(child));
            child = child.getNext();
        }
        return texts;
    }
}
