package com.needcoolershoes.supersimpleemoji.MiniTags;

import com.needcoolershoes.supersimpleemoji.Emoji;
import com.needcoolershoes.supersimpleemoji.SuperSimpleEmoji;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DynamicResolvers {
  public static TagResolver superSimpleEmoji(HashMap<String, Component> argsHash) {
    return new SSETag(argsHash).resolver;
  }

  public static TagResolver placeholderApi(Player player) {
    return new PlaceholderApiTag(player).resolver;
  }

  public static TagResolver emoji(HashMap<String, Emoji> emojis) {
    return new EmojiTag(emojis).resolver;
  }
}

class SSETag {
  final HashMap<String, Component> argsHash;
  final TagResolver resolver;

  public SSETag(HashMap<String, Component> argsHash) {
    this.argsHash = argsHash;

    resolver = TagResolver.resolver("sse", this::sseTag);
  }

  Tag sseTag(final ArgumentQueue args, final Context ctx) {
    final String key = args.popOr("The <sse> tag requires exactly one argument").value();

    Component component = argsHash.get(key);
    if (component == null) {
      component = Component.empty();
    }

    return Tag.selfClosingInserting(component);
  }
}

class PlaceholderApiTag {
  final Player player;
  final TagResolver resolver;

  public PlaceholderApiTag(Player player) {
    this.player = player;

    resolver = TagResolver.resolver("ph", this::placeholderApiTag);
  }

  Tag placeholderApiTag(final ArgumentQueue args, final Context ctx) {
    if (!(SuperSimpleEmoji.PLUGIN.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))) {
      return Tag.selfClosingInserting(Component.empty());
    }

    final String key = args.popOr("The <sse> tag requires exactly one argument").value();

    String formattedText = PlaceholderAPI.setPlaceholders(player, key);

    return Tag.selfClosingInserting(LegacyComponentSerializer.legacyAmpersand().deserialize(formattedText));
  }
}

class EmojiTag {
  final HashMap<String, Emoji> emojis;
  final TagResolver resolver;

  public EmojiTag(HashMap<String, Emoji> emojis) {
    this.emojis = emojis;

    resolver = TagResolver.resolver("emoji", this::emojiTag);
  }

  Tag emojiTag(final ArgumentQueue args, final Context ctx) {
    final String key = args.popOr("The <emoji> tag requires exactly one argument").value();

    Emoji emoji = emojis.get(key);
    final Component component;

    if (emoji == null) {
      component = Component.empty();
    } else {
      component = emoji.format;
    }

    return Tag.selfClosingInserting(component);
  }
}