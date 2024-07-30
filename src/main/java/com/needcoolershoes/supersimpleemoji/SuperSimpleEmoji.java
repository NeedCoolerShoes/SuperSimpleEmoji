package com.needcoolershoes.supersimpleemoji;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class SuperSimpleEmoji extends JavaPlugin {
    static JavaPlugin PLUGIN;
    static ChatParser PARSER;

    @Override
    public void onEnable() {
        // Plugin startup logic
        PLUGIN = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PluginListeners(), this);
        setupParser();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    void setupParser() {
        ConfigurationSection emojiConf = getConfig().getConfigurationSection("emojis");
        HashSet<Emoji> emojis = new HashSet<>();

        if (emojiConf == null) {
            PARSER = new ChatParser(emojis);
            return;
        }

        Set<String> keys = emojiConf.getKeys(false);
        for (String key : keys) {
            ConfigurationSection emoji = emojiConf.getConfigurationSection(key);
            if (emoji == null) { continue; }

            String pattern = emoji.getString("pattern");
            String text = emoji.getString("text");

            emojis.add(new Emoji(key, pattern, text));
        }

        PARSER = new ChatParser(emojis);
    }
}
