package com.needcoolershoes.supersimpleemoji;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
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
    private boolean placeholderApi = false;
    static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                .resolver(StandardTags.color())
                .resolver(StandardTags.decorations())
                .resolver(StandardTags.font())
                .resolver(StandardTags.rainbow())
                .resolver(StandardTags.gradient())
                .resolver(StandardTags.transition())
                .resolver(TagResolver.resolver("utf", Renderer::unicodeTag))
            .build()
        ).build();

    private static Tag unicodeTag(final ArgumentQueue args, final Context ctx) {
        String key = args.popOr("The <utf> tag requires exactly one argument").value();
        key = "\"\\u" + key + "\"";

        return Tag.selfClosingInserting(JSONComponentSerializer.json().deserialize(key));
    }

    public Renderer() {
        PLUGIN = SuperSimpleEmoji.getInstance();
        Server server = PLUGIN.getServer();
        PluginManager pluginManager = server.getPluginManager();
        ServicesManager servicesManager = server.getServicesManager();

        if (pluginManager.isPluginEnabled("LuckPerms")) {
            luckPerms = Optional.of(
                servicesManager.load(LuckPerms.class)
            );
        }
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            placeholderApi = true;
        }
    }

    // Based on LPC by wikmor
    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        Component emojiMessage = message;
        if (message instanceof TextComponent) {
            String emojiText = EmojiParser.parse(SuperSimpleEmoji.EMOJIS, ((TextComponent) message).content());
            emojiMessage = MINI_MESSAGE.deserialize(emojiText);
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

        return new MiniTagParser(formatter.format).deserialize(template);
    }
}

class FormatHelper {
    HashMap<String, Component> format = new HashMap<>();

    public FormatHelper() {}


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

class MiniTagParser {
    final HashMap<String, Component> argsHash;
    final MiniMessage extendedInstance;

    public MiniTagParser(HashMap<String, Component> argsHash) {
        this.argsHash = argsHash;

        TagResolver resolver = TagResolver.resolver("sse", this::createSSE);
        this.extendedInstance = MiniMessage.builder().tags(
            TagResolver.builder()
                .resolver(TagResolver.standard())
                .resolver(resolver)
                .build()
        ).build();
    }

    Tag createSSE(final ArgumentQueue args, final Context ctx) {
        final String key = args.popOr("The <sse> tag requires exactly one argument").value();

        Component component = argsHash.get(key);
        if (component == null) { component = Component.empty(); }

        return Tag.selfClosingInserting(component);
    }

    Component deserialize(String message) {
        return extendedInstance.deserialize(message);
    }
}