package me.zbrunooow.bgguerreiro.util;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Locations {

    private File filelocs = null;
    private FileConfiguration fileConfigurationlocs = null;

    public Locations() {
        File verificarLocs = new File(WarriorEngine.getInstance().getDataFolder(), "locs.yml");
        if (!verificarLocs.exists()) {
            WarriorEngine.getInstance().saveResource("locs.yml", false);
        }

        reloadLocs();
    }

    public FileConfiguration getLocs() {
        if (this.fileConfigurationlocs == null) {
            this.filelocs = new File(WarriorEngine.getInstance().getDataFolder(), "locs.yml");
            this.fileConfigurationlocs = (FileConfiguration) YamlConfiguration.loadConfiguration(this.filelocs);
        }
        return this.fileConfigurationlocs;
    }

    public void saveLocs() {
        try {
            getLocs().save(this.filelocs);
        } catch (Exception exception) {}
    }

    public void reloadLocs() {
        if (this.filelocs == null)
            this.filelocs = new File(WarriorEngine.getInstance().getDataFolder(), "locs.yml");
        this.fileConfigurationlocs = (FileConfiguration)YamlConfiguration.loadConfiguration(this.filelocs);
        if (this.fileConfigurationlocs != null) {
            YamlConfiguration locs = YamlConfiguration.loadConfiguration(this.filelocs);
            this.fileConfigurationlocs.setDefaults((Configuration) locs);
        }
    }

    public static Locations get() {
        return WarriorEngine.getInstance().getLocations();
    }

}
