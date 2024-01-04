package me.zbrunooow.bgguerreiro.hooks;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.plugin.Plugin;

public class SimpleClansHook {

    public boolean hookSimpleclans() {
        try {
            byte b;
            int i;
            Plugin[] arrayOfPlugin;
            for (i = (arrayOfPlugin = WarriorEngine.getInstance().getServer().getPluginManager().getPlugins()).length, b = 0; b < i; ) {
                Plugin plugin = arrayOfPlugin[b];
                if (plugin instanceof net.sacredlabyrinth.phaed.simpleclans.SimpleClans) {
                    WarriorEngine.getInstance().setSimpleClans(true);
                    return true;
                }
                b++;
            }
        } catch (NoClassDefFoundError e) {
            WarriorEngine.getInstance().setSimpleClans(false);
            return false;
        }
        return false;
    }
}
