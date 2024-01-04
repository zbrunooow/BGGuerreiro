package me.zbrunooow.bgguerreiro.hooks;

import me.zbrunooow.bgguerreiro.Core;
import org.bukkit.plugin.Plugin;

public class SimpleClansHook {

    public boolean hookSimpleclans() {
        try {
            byte b;
            int i;
            Plugin[] arrayOfPlugin;
            for (i = (arrayOfPlugin = Core.getInstance().getServer().getPluginManager().getPlugins()).length, b = 0; b < i; ) {
                Plugin plugin = arrayOfPlugin[b];
                if (plugin instanceof net.sacredlabyrinth.phaed.simpleclans.SimpleClans) {
                    Core.getInstance().sc = true;
                    return true;
                }
                b++;
            }
        } catch (NoClassDefFoundError e) {
            Core.getInstance().sc = false;
            return false;
        }
        return false;
    }
}
