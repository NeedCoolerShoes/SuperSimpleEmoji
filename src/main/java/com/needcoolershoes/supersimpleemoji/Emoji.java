package com.needcoolershoes.supersimpleemoji;

import net.kyori.adventure.text.Component;

import java.util.regex.Pattern;

public class Emoji {
  public final String name;
  public final Pattern pattern;
  public final Component format;

  public Emoji(String name, Pattern pattern, Component format) {
    this.name = name;
    this.pattern = pattern;
    this.format = format;
  }

  public String toTag() {
    return "<emoji:" + this.name + ">";
  }
}
