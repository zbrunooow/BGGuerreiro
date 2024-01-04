package me.zbrunooow.bgguerreiro.hooks;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import me.zbrunooow.bgguerreiro.Core;

public class LegendChatHook {

    public void hookLegendChat() {
        try {
            if (Core.getInstance().getServer().getPluginManager().getPlugin("Legendchat") != null) {
                ChannelManager channelManager = Legendchat.getChannelManager();
            }
        } catch (NoClassDefFoundError ignored) {
        }
    }

}
