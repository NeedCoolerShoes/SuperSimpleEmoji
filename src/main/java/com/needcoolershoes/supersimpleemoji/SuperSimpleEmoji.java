package com.needcoolershoes.supersimpleemoji;

import com.mojang.brigadier.Command;
import com.needcoolershoes.supersimpleemoji.MiniTags.StaticResolvers;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class SuperSimpleEmoji extends JavaPlugin {
  public static JavaPlugin PLUGIN;
  public static Logger LOGGER;
  public static HashMap<String, Emoji> EMOJIS = new HashMap<>();

  public static JavaPlugin getInstance() {
    return PLUGIN;
  }

  @Override
  public void onEnable() {
    // Plugin startup logic
    PLUGIN = this;
    LOGGER = this.getLogger();
    saveDefaultConfig();
    getServer().getPluginManager().registerEvents(new PluginListeners(), this);
    refreshConfig();

    LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
    manager.registerEventHandler(LifecycleEvents.COMMANDS, this::registerCommands);
  }

  public void refreshConfig() {
    reloadConfig();
    setupEmojis();
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  void registerCommands(ReloadableRegistrarEvent<Commands> event) {
    final Commands commands = event.registrar();
    commands.register(
      Commands.literal("sse").then(Commands.literal("reload").executes(
          ctx -> {
            ctx.getSource().getSender().sendPlainMessage("[SSE] Reloading Config.");
            this.refreshConfig();
            return Command.SINGLE_SUCCESS;
          }))
        .build(),
      "Manage Super Simple Emoji"
    );
  }

  void setupEmojis() {
    ConfigurationSection emojiConf = getConfig().getConfigurationSection("emojis");
    if (emojiConf == null) {
      return;
    }

    EMOJIS = new HashMap<>();
    MiniMessage messageParser = MiniMessage.builder().tags(StaticResolvers.ADMIN_DEFAULT).build();

    Set<String> keys = emojiConf.getKeys(false);
    for (String key : keys) {
      ConfigurationSection emoji = emojiConf.getConfigurationSection(key);
      if (emoji == null) {
        continue;
      }

      String pattern = emoji.getString("pattern");
      String text = emoji.getString("text");

      if (pattern == null || text == null) {
        continue;
      }

      Emoji newEmoji = new Emoji(key, Pattern.compile(pattern), messageParser.deserialize(text));

      EMOJIS.put(key, newEmoji);
    }
  }
}
