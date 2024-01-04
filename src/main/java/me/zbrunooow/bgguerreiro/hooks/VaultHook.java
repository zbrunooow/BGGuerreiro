package me.zbrunooow.bgguerreiro.hooks;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    public static Economy eco = null;
    public boolean setupEconomy() {
        try {
            if (WarriorEngine.getInstance().getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Economy> economyProvider = WarriorEngine.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
                if (economyProvider != null)
                    eco = (Economy) economyProvider.getProvider();
                return (eco != null);
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

}
