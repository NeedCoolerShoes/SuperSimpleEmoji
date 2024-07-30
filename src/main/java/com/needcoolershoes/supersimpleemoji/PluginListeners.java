package com.needcoolershoes.supersimpleemoji;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PluginListeners implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        event.message(
                SuperSimpleEmoji.PARSER.parse(event.signedMessage().message())
        );
    }
}
