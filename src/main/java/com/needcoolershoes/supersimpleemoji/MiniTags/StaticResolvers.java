package com.needcoolershoes.supersimpleemoji.MiniTags;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public class StaticResolvers {
  public static TagResolver USER_DEFAULT = buildUserDefault();
  public static TagResolver ADMIN_DEFAULT = buildAdminDefault();

  private static TagResolver buildUserDefault() {
    return TagResolver.builder()
      .resolver(StandardTags.color())
      .resolver(StandardTags.decorations())
      .resolver(StandardTags.font())
      .resolver(StandardTags.rainbow())
      .resolver(StandardTags.gradient())
      .resolver(StandardTags.transition())
      .resolver(TagResolver.resolver("utf", Tags::unicode))
      .build();
  }

  private static TagResolver buildAdminDefault() {
    return TagResolver.builder()
      .resolver(TagResolver.standard())
      .resolver(TagResolver.resolver("utf", Tags::unicode))
      .build();
  }
}