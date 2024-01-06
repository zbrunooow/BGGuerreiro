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
    String[] languages = {"en", "es", "pt-br"};

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
            public String getConfigReloaded() { return getFile().getString("config-reloaded").replace("&", "§"); }

            @Override
            public String getBoxLeave() {
              return getFile().getString("box-leave").replace("&", "§");
            }

            @Override
            public String getReactivatingDeathMatchPvP() { return getFile().getString("deathmatch-reactivating-pvp").replace("&", "§"); }

            @Override
            public String getDeathWithoutReason() {
              return getFile().getString("kill-without-player").replace("&", "§");
            }

            @Override
            public String getAlreadyJoin() {
              return getFile().getString("undefined.already-joined").replace("&", "§");
            }

            @Override
            public String getNotJoined() {
              return getFile().getString("undefined.not-joined").replace("&", "§");
            }

            @Override
            public String getClearInventory() {
              return getFile().getString("undefined.clear-inventory").replace("&", "§");
            }

            @Override
            public String getNotStarted() {
                return getFile().getString("undefined.not-started").replace("&", "§");
            }

            @Override
            public String getAlreadyStarted() {
                return getFile().getString("undefined.already-started").replace("&", "§");
            }

            @Override
            public String getAlreadyDeathmatch() {
                return getFile().getString("undefined.already-deathmmatch").replace("&", "§");
            }

            @Override
            public String getClosedJoin() {
                return getFile().getString("undefined.closed-join").replace("&", "§");
            }

            @Override
            public String getCantStartDeathmatch() {
                return getFile().getString("undefined.cant-start-deathmatch").replace("&", "§");
            }

            @Override
            public String getUndefinedJoin() {
                return getFile().getString("undefined.join").replace("&", "§");
            }

            @Override
            public String getUndefinedExit() {
                return getFile().getString("undefined.exit").replace("&", "§");
            }

            @Override
            public String getUndefinedDeathmatch() {
                return getFile().getString("undefined.deathmatch").replace("&", "§");
            }

            @Override
            public String getUndefinedKit() {
                return getFile().getString("undefined.kit").replace("&", "§");
            }

            @Override
            public String getSetUsage() {
                return getFile().getString("correct-usage.set").replace("&", "§");
            }

            @Override
            public String getForcestartUsage() {
                return getFile().getString("correct-usage.forcestart").replace("&", "§");
            }

            @Override
            public String getForcestopUsage() {
                return getFile().getString("correct-usage.forcestop").replace("&", "§");
            }

            @Override
            public String getForceDeathmatchUsage() {
                return getFile().getString("correct-usage.forcedm").replace("&", "§");
            }

            @Override
            public String getResetkitUsage() {
                return getFile().getString("correct-usage.resetkit").replace("&", "§");
            }

            @Override
            public String getTopUsage() {
                return getFile().getString("correct-usage.top").replace("&", "§");
            }

            @Override
            public String getSuccessJoin() {
                return getFile().getString("success.joined").replace("&", "§");
            }

            @Override
            public String getSuccessLeave() {
                return getFile().getString("success.leave").replace("&", "§");
            }

            @Override
            public String getSuccessSetJoin() {
                return getFile().getString("success.set-join").replace("&", "§");
            }

            @Override
            public String getSuccessSetLeave() {
                return getFile().getString("success.set-exit").replace("&", "§");
            }

            @Override
            public String getSuccessSetKit() {
                return getFile().getString("success.set-kit").replace("&", "§");
            }

            @Override
            public String getSuccessSetDeathmatch() {
                return getFile().getString("success.set-deathmatch").replace("&", "§");
            }

            @Override
            public String getSuccessResetkit() {
                return getFile().getString("success.resetkit").replace("&", "§");
            }

            @Override
            public String getWaitBox() {
                return getFile().getString("wait.box").replace("&", "§");
            }

            @Override
            public String getWaitStarting() {
                return getFile().getString("wait.event-starting").replace("&", "§");
            }

            @Override
            public String getInfoStarting() {
                return getFile().getString("info.starting").replace("&", "§");
            }

            @Override
            public String getInfoStarted() {
                return getFile().getString("info.started").replace("&", "§");
            }

            @Override
            public String getInfoNotStarted() {
                return getFile().getString("info.not-started").replace("&", "§");
            }

            @Override
            public String getInfoDeathmatch() {
                return getFile().getString("info.deathmatch").replace("&", "§");
            }

            @Override
            public List<String> getYouWin() {
              return getFile().getStringList("you-won").stream()
                  .map(s -> s.replace("&", "§"))
                  .collect(Collectors.toList());
            }

            @Override
            public List<String> getPvpEnabledDeathMatch() {
                return getFile().getStringList("deathmatch-pvp-enabled").stream()
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
