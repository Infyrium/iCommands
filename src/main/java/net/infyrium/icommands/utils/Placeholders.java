package net.infyrium.icommands.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Placeholders in config texts are written in braces, MiniMessage knows them as tags.
 */
public final class Placeholders {

    private static final Pattern BRACES = Pattern.compile("\\{([a-z0-9_]+)}");

    private static final Pattern TAG = Pattern.compile("(?<!\\\\)<([a-z0-9_]+)>");

    private Placeholders() {
    }

    /**
     * Turns the placeholders in braces into tags the given resolvers know.
     * A tag named like a placeholder is escaped first, so only the brace form is replaced.
     */
    public static String toTags(String input, TagResolver... placeholders) {
        if (placeholders.length == 0) return input;

        TagResolver resolver = TagResolver.resolver(placeholders);
        String escaped = replace(TAG, input, resolver, "\\<");
        return replace(BRACES, escaped, resolver, "<");
    }

    private static String replace(Pattern pattern, String input, TagResolver resolver, String opening) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder(input.length());
        while (matcher.find()) {
            String name = matcher.group(1);
            String replacement = resolver.has(name) ? opening + name + ">" : matcher.group();
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
