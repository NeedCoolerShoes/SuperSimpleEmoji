package com.needcoolershoes.supersimpleemoji;

import com.needcoolershoes.supersimpleemoji.MiniTags.DynamicResolvers;
import com.needcoolershoes.supersimpleemoji.MiniTags.StaticResolvers;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class Renderer implements ChatRenderer {
  private static final Renderer instance = new Renderer();
  static JavaPlugin PLUGIN;

  public static Renderer getInstance() {
    return instance;
  }

  private Optional<LuckPerms> luckPerms = Optional.empty();

  public Renderer() {
    PLUGIN = SuperSimpleEmoji.getInstance();
    Server server = PLUGIN.getServer();
    PluginManager pluginManager = server.getPluginManager();
    ServicesManager servicesManager = server.getServicesManager();

    if (pluginManager.isPluginEnabled("LuckPerms")) {
      luckPerms = Optional.ofNullable(
        servicesManager.load(LuckPerms.class)
      );
    }
  }

  // Based on LPC by wikmor
  @Override
  public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
    Component emojiMessage = message;
    if (message instanceof TextComponent) {
      final MiniMessage miniMessage = MiniMessage.builder().tags(
        TagResolver.builder()
          .resolver(DynamicResolvers.emoji(SuperSimpleEmoji.EMOJIS))
          .resolver(StaticResolvers.USER_DEFAULT)
          .build()
      ).build();

      String messageText = ((TextComponent) message).content();
      String emojiText = MiniMessageEmojiParser.parse(miniMessage, SuperSimpleEmoji.EMOJIS, messageText);
      emojiMessage = miniMessage.deserialize(emojiText);
    }

    FormatHelper formatter = new FormatHelper();
    formatter
      .add("displayname", sourceDisplayName)
      .add("message", emojiMessage)
      .add("name", source.getName())
      .add("world", source.getWorld().getName());

    String template = PLUGIN.getConfig().getString("chat-format");

    if (luckPerms.isPresent()) {
      LuckPerms perms = luckPerms.get();

      final CachedMetaData metaData = perms.getPlayerAdapter(Player.class).getMetaData(source);
      final String group = metaData.getPrimaryGroup();

      final String section = PLUGIN.getConfig().getString("group-formats." + group) != null ? "group-formats." + group : "chat-format";
      template = PLUGIN.getConfig().getString(section);

      formatter
        .addLegacy("prefix", metaData.getPrefix())
        .addLegacy("suffix", metaData.getSuffix())
        .addLegacy("prefixes", metaData.getPrefixes().keySet().stream().map(
          key -> metaData.getPrefixes().get(key)).collect(Collectors.joining()
        ))
        .addLegacy("suffixes", metaData.getSuffixes().keySet().stream().map(
          key -> metaData.getSuffixes().get(key)).collect(Collectors.joining()
        ));
    }

    if (template == null) {
      template = "<sse:username>: <sse:message>";
    }

    final MiniMessage tagParser = MiniMessage.builder().tags(
      TagResolver.builder()
        .resolver(StaticResolvers.ADMIN_DEFAULT)
        .resolver(DynamicResolvers.superSimpleEmoji(formatter.format))
        .resolver(DynamicResolvers.placeholderApi(source))
        .resolver(DynamicResolvers.emoji(SuperSimpleEmoji.EMOJIS))
        .build()
    ).build();

    return tagParser.deserialize(template);
  }
}

class FormatHelper {
  HashMap<String, Component> format = new HashMap<>();

  public FormatHelper() {
  }

  public FormatHelper addLegacy(String key, String text) {
    Component component;
    if (text == null) {
      component = Component.empty();
    } else {
      component = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
    return add(key, component);
  }

  public FormatHelper add(String key, String component) {
    return add(key, Component.text(component));
  }

  public FormatHelper add(String key, Component component) {
    format.put(key, component);
    return this;
  }
}