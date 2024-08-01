package com.needcoolershoes.supersimpleemoji;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MiniMessageEmojiParser {
  public static String parse(MiniMessage base, HashMap<String, Emoji> emojis, String input) {
    return new MiniMessageEmojiParser(base, emojis, input).parse();
  }

  private final static char TAG_OPEN = '<';
  private final static char TAG_CLOSE = '>';
  private final static char TAG_ARG_SEP = ':';
  private final static char TAG_SLASH = '/';
  private final static char ESCAPE = '\\';

  final MiniMessage base;
  final HashMap<String, Emoji> emojis;
  final String input;

  int index;
  boolean escaped = false;
  char quote = '\0';
  ArrayList<Token> stack = new ArrayList<>();

  private MiniMessageEmojiParser(MiniMessage base, HashMap<String, Emoji> emojis, String input) {
    this.base = base;
    this.emojis = emojis;
    this.input = input;
  }

  public String parse() {
    while (inRange()) {
      Token currentToken = stackTop();
      switch (currentToken.kind) {
        case TAG -> parseTag();
        case TEXT -> parseText();
      }
    }

    return stack.stream().map(element -> switch (element.kind) {
      case TEXT -> EmojiParser.parse(emojis, element.value);
      case TAG -> element.value;
    }).collect(Collectors.joining());
  }

  private void logStack() {
    SuperSimpleEmoji.LOGGER.info(stack.stream().map(e -> e.kind.name() + ": " + e.value).collect(Collectors.joining(", ")));
  }

  private void parseTag() {
    StringBuilder tagName = new StringBuilder();
    while (inRange() && current() != TAG_ARG_SEP && current() != TAG_CLOSE) {
      tagName.append(current());
      consume();
    }

    String tag = tagName.toString();
    if (tag.charAt(0) == TAG_SLASH) {
      tag = tag.substring(1);
    }

    if (!base.tags().has(tag)) {
      if (index < input.length()) {
        consume();
      }
      stackTop().setKind(TokenKind.TEXT);
      push(Token.text());
      return;
    }

    while (inRange() && checkTagBody()) {
      consume();
    }

    if (index < input.length()) {
      consume();
    }
    push(Token.text());
  }

  private void parseText() {
    if (current() == TAG_OPEN && !escaped) {
      if (stackTop().value.isEmpty()) {
        stackTop().setKind(TokenKind.TAG);
      } else {
        push(Token.tag());
      }
      consume();
      return;
    }
    consume();
  }

  private boolean inRange() {
    return index < input.length();
  }

  private boolean checkTagBody() {
    char current = current();
    if (!escaped) {
      if (quote == current) {
        quote = '\0';
        return true;
      }
      if (current == '\'' || current == '"') {
        quote = current;
        return true;
      }
    }
    if (quote != '\0') {
      return true;
    }
    return current != TAG_CLOSE;
  }

  private char current() {
    return input.charAt(index);
  }

  private Token stackTop() {
    if (stack.isEmpty()) {
      stack.add(Token.text());
    }
    return stack.getLast();
  }

  private char consume() {
    if (stack.isEmpty()) {
      stack.add(Token.text());
    }
    char current = current();
    stackTop().append(current);
    index += 1;

    if (current == ESCAPE && !escaped) {
      escaped = true;
      return consume();
    }

    escaped = false;
    return current;
  }

  private void push(Token token) {
    stack.add(token);
  }
}

enum TokenKind {TEXT, TAG}

class Token {
  public static Token text() {
    return new Token(TokenKind.TEXT);
  }

  public static Token tag() {
    return new Token(TokenKind.TAG);
  }

  public TokenKind kind;
  public String value = "";

  private Token(TokenKind kind) {
    this.kind = kind;
  }

  public void setKind(TokenKind kind) {
    this.kind = kind;
  }

  public String append(char character) {
    value = value + character;
    return value;
  }
}