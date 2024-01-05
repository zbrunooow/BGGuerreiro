package me.zbrunooow.bgguerreiro.util;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Kit {

    private File fileKit = null;
    private FileConfiguration fileConfigurationKit = null;

    public Kit() {
        File verificarKit = new File(WarriorEngine.getInstance().getDataFolder(), "kit.yml");

        if (!verificarKit.exists()) {
            WarriorEngine.getInstance().saveResource("kit.yml", false);
        }

        reloadInventoryFile();
    }

    public FileConfiguration getKitFile() {
        if (this.fileConfigurationKit == null) {
            this.fileKit = new File(WarriorEngine.getInstance().getDataFolder(), "kit.yml");
            this.fileConfigurationKit = (FileConfiguration) YamlConfiguration.loadConfiguration(this.fileKit);
        }
        return this.fileConfigurationKit;
    }

    public void resetKit() {
        File kitFile = new File(WarriorEngine.getInstance().getDataFolder(), "kit.yml");
        kitFile.delete();
        WarriorEngine.getInstance().saveResource("kit.yml", false);
        WarriorEngine.getInstance().reloadPlugin();

        Manager.getCreated().setItems(Kit.get().getKitFile().getString("items"));
        Manager.getCreated().setArmor(Kit.get().getKitFile().getString("armor"));
    }

    public void saveKitFile() {
        try {
            getKitFile().save(this.fileKit);
        } catch (Exception exception) {}
    }

    public void reloadInventoryFile() {
        if (this.fileKit == null)
            this.fileKit = new File(WarriorEngine.getInstance().getDataFolder(), "kit.yml");
        this.fileConfigurationKit = (FileConfiguration)YamlConfiguration.loadConfiguration(this.fileKit);
        if (this.fileConfigurationKit != null) {
            YamlConfiguration inv = YamlConfiguration.loadConfiguration(this.fileKit);
            this.fileConfigurationKit.setDefaults((Configuration) inv);
        }
    }

    public static Kit get() {
        return WarriorEngine.getInstance().getKit();
    }

}
