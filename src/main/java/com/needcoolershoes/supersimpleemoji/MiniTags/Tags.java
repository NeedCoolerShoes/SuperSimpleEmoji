package com.needcoolershoes.supersimpleemoji.MiniTags;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Pattern;

public class Tags {
  public static final Pattern UNICODE_PATTERN = Pattern.compile("^[0-9a-fA-F]{4}$");

  public static Tag unicode(final ArgumentQueue args, final Context ctx) {
    String key = args.popOr("The <utf> tag requires exactly one argument").value();
    if (UNICODE_PATTERN.matcher(key).matches()) {
      key = StringEscapeUtils.unescapeJava("\\u" + key);
    } else {
      throw ctx.newException("Invalid UTF string");
    }

    return Tag.selfClosingInserting(Component.text(key));
  }
}
