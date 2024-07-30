package com.needcoolershoes.supersimpleemoji;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatParser {
    HashSet<Emoji> emojis;

    public ChatParser(HashSet<Emoji> emojis) {
        this.emojis = emojis;
    }

    public Component parse(String message) {
        ArrayList<ChatElement> input = new ArrayList<>();
        input.add(new ChatElement(ElementKind.TEXT, message));

        for (Emoji emoji : emojis) {
            input = parseElements(emoji, input);
        }

        Component component = Component.text("");
        for (ChatElement element : input) {
            if (element.kind == ElementKind.EMOJI) {
                final Component fromJson = JSONComponentSerializer.json().deserialize(element.value);
                component = component.append(fromJson);
            } else {
                component = component.append(Component.text(element.value));
            }
        }

        return component;
    }

    private ArrayList<ChatElement> parseElements(Emoji emoji, ArrayList<ChatElement> elements) {
        ArrayList<ChatElement> output = new ArrayList<>();
        Pattern regex = emoji.pattern;

        elements.forEach((element) -> {
            if (element.kind == ElementKind.EMOJI) {
                output.add(element);
                return;
            }

            String contents = element.value;
            Matcher matcher = regex.matcher(contents);

            while (matcher.find()) {
                if (matcher.start() > 0) {
                    output.add(new ChatElement(ElementKind.TEXT, contents.substring(0, matcher.start())));
                }

                output.add(new ChatElement(ElementKind.EMOJI, emoji.jsonText));
                contents = contents.substring(matcher.end());
                matcher = regex.matcher(contents);
            }

            if (!contents.isEmpty()) {
                output.add(new ChatElement(ElementKind.TEXT, contents));
            }
        });
        return output;
    }
}

enum ElementKind {TEXT, EMOJI}

class ChatElement {
    public ElementKind kind;
    public String value;

    public ChatElement(ElementKind kind, String value) {
        this.kind = kind;
        this.value = value;
    }
}