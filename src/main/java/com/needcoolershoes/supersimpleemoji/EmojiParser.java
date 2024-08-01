package com.needcoolershoes.supersimpleemoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiParser {
  public static String parse(HashMap<Pattern, String> patterns, String message) {

    ArrayList<MessageWrapper> messages = new ArrayList<>();
    messages.add(MessageWrapper.unresolved(message));

    for (Map.Entry<Pattern, String> entry : patterns.entrySet()) {
      Pattern pattern = entry.getKey();
      String replacement = entry.getValue();

      messages = parseElement(messages, pattern, replacement);
    }

    StringBuilder output = new StringBuilder();
    for (MessageWrapper messagePart : messages) {
      output.append(messagePart.message);
    }

    return output.toString();
  }

  static ArrayList<MessageWrapper> parseElement(ArrayList<MessageWrapper> mesaages, Pattern pattern, String replacement) {
    ArrayList<MessageWrapper> output = new ArrayList<>();

    mesaages.forEach((element) -> {
      if (element.resolved) {
        output.add(element.resolve());
        return;
      }

      String contents = element.message;
      Matcher matcher = pattern.matcher(contents);

      while (matcher.find()) {
        if (matcher.start() > 0) {
          output.add(MessageWrapper.unresolved(contents.substring(0, matcher.start())));
        }

        output.add(MessageWrapper.resolved(replacement));
        contents = contents.substring(matcher.end());
        matcher = pattern.matcher(contents);
      }

      if (!contents.isEmpty()) {
        output.add(MessageWrapper.unresolved(contents));
      }
    });
    return output;
  }
}

class MessageWrapper {
  public boolean resolved;
  public final String message;

  public static MessageWrapper unresolved(String message) {
    return new MessageWrapper(message, false);
  }

  public static MessageWrapper resolved(String message) {
    return new MessageWrapper(message, true);
  }

  public MessageWrapper(String message, boolean resolved) {
    this.resolved = resolved;
    this.message = message;
  }

  public MessageWrapper resolve() {
    this.resolved = true;
    return this;
  }
}
