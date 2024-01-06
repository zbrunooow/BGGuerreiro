package me.zbrunooow.bgguerreiro.lang;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageRegistry {

  private static final Map<String, Language> languages = new HashMap<>();
  private static final File languagesFolder =
      new File(WarriorEngine.getInstance().getDataFolder(), "lang");

  public static Language getLanguage(String identifier) {
    return languages.get(identifier);
  }

  public static void saveDefaults() {
    String[] languages = {"en", "es", "pt-br", "ru", "zh"};

    for (String language : languages) {
      InputStream resourceAsStream =
          WarriorEngine.class.getResourceAsStream("/lang/" + language + ".yml");

      if (resourceAsStream != null) {
        if (!languagesFolder.exists()) {
          languagesFolder.mkdir();
        }
        File file = new File(languagesFolder, language + ".yml");
        if (!file.exists()) {
          try {
            file.createNewFile();
            YamlConfiguration.loadConfiguration(resourceAsStream).save(file);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public static void registerAll() {
    if (!(languagesFolder.exists()) || !languagesFolder.isDirectory()) {
      languagesFolder.mkdir();
    }

    File[] files = languagesFolder.listFiles();

    if (files == null || files.length == 0) {
      Bukkit.getConsoleSender().sendMessage("§c[BGGuerreiro] Nenhuma linguagem encontrada!");
      return;
    }

    for (File file : files) {
      String fileName = file.getName();
      if (!fileName.endsWith(".yml")) continue;
      languages.put(
          fileName.replace(".yml", ""),
          new Language() {
            @Override
            public String getIdentifier() {
              return fileName.replace(".yml", "");
            }

            @Override
            public FileConfiguration getFile() {
              return YamlConfiguration.loadConfiguration(file);
            }

            @Override
            public String getNoPermission() {
              return getFile().getString("no-permission").replace("&", "§");
            }

            @Override
            public String getKill() {
              return getFile().getString("kill").replace("&", "§");
            }

            @Override
            public String getSurvivorsLeft() {
              return getFile().getString("survivors-left").replace("&", "§");
            }

            @Override
            public String getStatusActionbar() {
              return getFile().getString("status-actionbar").replace("&", "§");
            }

            @Override
            public String getInfoActionbar() {
              return getFile().getString("info-actionbar").replace("&", "§");
            }

            @Override
            public String getTopValue() {
              return getFile().getString("ranking.value").replace("&", "§");
            }

            @Override
            public String getBoxJoin() {
              return getFile().getString("box-join").replace("&", "§");
            }

            @Override
            public String getBoxLeave() {
              return getFile().getString("box-leave").replace("&", "§");
            }

            @Override
            public String getDeathWithoutReason() {
              return getFile().getString("kill-without-player").replace("&", "§");
            }

            @Override
            public List<String> getYouWin() {
              return getFile().getStringList("you-won").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getGeneralCommand() {
              return getFile().getStringList("general").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getTopHeader() {
              return getFile().getStringList("ranking.header").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getTopFooter() {
              return getFile().getStringList("ranking.footer").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getStarting() {
              return getFile().getStringList("starting").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getCancelled() {
              return getFile().getStringList("cancelled").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getStatus() {
              return getFile().getStringList("status").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getWaiting() {
              return getFile().getStringList("waiting").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getStarted() {
              return getFile().getStringList("started").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getDeathmatchComing() {
              return getFile().getStringList("deathmatch-coming").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getDeathmatchStart() {
              return getFile().getStringList("deathmatch-started").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getExpired() {
              return getFile().getStringList("finalized").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }
          });
    }
  }

  public static Language getDefined() {
    String language = WarriorEngine.getInstance().getConfiguration().getLanguage();
    return language.isEmpty() ? getLanguage("pt-br") : getLanguage(language);
  }
}
