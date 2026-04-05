package nl.hardwerkendenederlanders.hrcms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class MarkdownSanitizer {

    private static final List<Extension> EXTENSIONS = List.of(StrikethroughExtension.create());

    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

    private static final MarkdownRenderer RENDERER =
            MarkdownRenderer.builder().extensions(EXTENSIONS).build();

    public static String sanitizeComment(String markdown) {
        if (markdown == null) return "";

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
        return RENDERER.render(document);
    }

    private static void stripDisallowed(Node node, Set<Class<? extends Node>> allowedNodes) {
        Node child = node.getFirstChild();
        while (child != null) {
            Node next = child.getNext();

            if (allowedNodes.contains(child.getClass())) {
                stripDisallowed(child, allowedNodes);
            } else {
                List<String> texts = extractText(child);
                for (String text : texts) child.insertBefore(new Text(text));

                if (child instanceof Block) child.insertBefore(new SoftLineBreak());
                child.unlink();
            }

            child = next;
        }
    }

    private static List<String> extractText(Node node) {
        List<String> texts = new ArrayList<>();

        if (node instanceof Text t) texts.add(t.getLiteral());
        else if (node instanceof Code c) texts.add(c.getLiteral());
        else if (node instanceof IndentedCodeBlock icb) texts.add(icb.getLiteral());
        else if (node instanceof FencedCodeBlock fcb) texts.add(fcb.getLiteral());

        Node child = node.getFirstChild();
        while (child != null) {
            texts.addAll(extractText(child));
            child = child.getNext();
        }
        return texts;
    }
}
