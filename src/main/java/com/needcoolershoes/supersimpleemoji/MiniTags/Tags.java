package com.needcoolershoes.supersimpleemoji.MiniTags;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public class Tags {
  public static Tag unicode(final ArgumentQueue args, final Context ctx) {
    String key = args.popOr("The <utf> tag requires exactly one argument").value();
    key = "\"\\u" + key + "\"";

    return Tag.selfClosingInserting(JSONComponentSerializer.json().deserialize(key));
  }
}
