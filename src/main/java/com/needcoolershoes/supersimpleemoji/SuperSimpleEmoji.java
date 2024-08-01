package com.needcoolershoes.supersimpleemoji;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

public final class SuperSimpleEmoji extends JavaPlugin {
    static JavaPlugin PLUGIN;
    static HashMap<Pattern, String> EMOJIS = new HashMap<>();
    static JavaPlugin getInstance() {
        return PLUGIN;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PLUGIN = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PluginListeners(), this);
        setupEmojis();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    void setupEmojis() {
        ConfigurationSection emojiConf = getConfig().getConfigurationSection("emojis");
        if (emojiConf == null) {
            return;
        }

        EMOJIS = new HashMap<>();

        Set<String> keys = emojiConf.getKeys(false);
        for (String key : keys) {
            ConfigurationSection emoji = emojiConf.getConfigurationSection(key);
            if (emoji == null) { continue; }

            String pattern = emoji.getString("pattern");
            String text = emoji.getString("text");

            if (pattern == null || text == null) { continue; }

            EMOJIS.put(Pattern.compile(pattern), text);
        }
    }
}
