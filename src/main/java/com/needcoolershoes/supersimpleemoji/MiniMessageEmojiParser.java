package com.needcoolershoes.supersimpleemoji;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MiniMessageEmojiParser {
  public static String parse(MiniMessage base, HashMap<Pattern, String> emojis, String input) {
    return new MiniMessageEmojiParser(base, emojis, input).parse();
  }

  private final static char TAG_OPEN = '<';
  private final static char TAG_CLOSE = '>';
  private final static char TAG_ARG_SEP = ':';
  private final static char TAG_SLASH = ':';

  final MiniMessage base;
  final HashMap<Pattern, String> emojis;
  final String input;

  int index;
  boolean isTag = false;
  ArrayList<Token> stack = new ArrayList<>();

  private MiniMessageEmojiParser(MiniMessage base, HashMap<Pattern, String> emojis, String input) {
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

  private void parseTag() {
    StringBuilder tagName = new StringBuilder();
    while (inRange() && current() != TAG_ARG_SEP && current() != TAG_CLOSE) {
      tagName.append(current());
      consume();
    }

    String tag = tagName.toString();
    if (tag.startsWith("/")) {
      tag = tag.substring(1);
    }

    if (!base.tags().has(tag)) {
      consume();
      stackTop().setKind(TokenKind.TEXT);
      push(Token.text());
      return;
    }

    while (inRange() && current() != TAG_CLOSE) {
      consume();
    }

    consume();
    push(Token.text());
  }

  private void parseText() {
    if (current() == TAG_OPEN) {
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