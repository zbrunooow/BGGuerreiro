package me.zbrunooow.bgguerreiro.commands;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.lang.Language;
import me.zbrunooow.bgguerreiro.lang.LanguageRegistry;
import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.manager.KitManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class WarriorCommand implements CommandExecutor {

  private static Language language;

  private static void sendHelp(CommandSender player) {
    for (String str : language.getGeneralCommand()) {
      if (str.contains("force") || str.contains("set")) {
        if (player.hasPermission("bgguerreiro.admin")) {
          player.sendMessage(str);
        }
      } else {
        player.sendMessage(str);
      }
    }
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command cmd, String lb, String[] args) {
    this.language = LanguageRegistry.getDefined();

    CommandSender sender = commandSender;

    // Insufficient Args
    if (args.length == 0) {
      sendHelp(sender);
      return false;
    }

    String action = args[0].toLowerCase();

    // Subcommand: start
    EventStatus eventStatus = EventManager.getCreated().getStatus();
    if ("iniciar".equals(action) || "forcestart".equals(action) || "start".equals(action)) {
      this.hasPermission(
          sender,
          () -> {
            if (args.length > 1) {
              sender.sendMessage(LanguageRegistry.getDefined().getForcestartUsage());
              return;
            }
            if (Manager.getCreated().getItems() == null) {
              sender.sendMessage(LanguageRegistry.getDefined().getUndefinedKit());
              return;
            }
            if (eventStatus != EventStatus.OFF) {
              sender.sendMessage(LanguageRegistry.getDefined().getAlreadyStarted());
              return;
            }
            EventManager.getCreated().startEvent();
            return;
          },
          language.getNoPermission());
      return false;
    }

    // Subcommand: cancel
    if ("cancelar".equals(action) || "forcestop".equals(action) || "stop".equals(action)) {
      this.hasPermission(
          sender,
          () -> {
            if (args.length > 1) {
              sender.sendMessage(LanguageRegistry.getDefined().getForcestopUsage());
              return;
            }
            if (eventStatus == EventStatus.OFF) {
              sender.sendMessage(LanguageRegistry.getDefined().getNotStarted());
              return;
            }
            EventManager.getCreated().cancelar(true);
          },
          language.getNoPermission());
      return false;
    }

    // Subcommand: forcedm
    if ("forcedm".equals(action) || "forcedeathmatch".equals(action)) {
      this.hasPermission(
          sender,
          () -> {
            if (args.length > 1) {
              sender.sendMessage(LanguageRegistry.getDefined().getForceDeathmatchUsage());
              return;
            }
            if(eventStatus == EventStatus.DEATHMATCH) {
              sender.sendMessage(LanguageRegistry.getDefined().getAlreadyDeathmatch());
              return;
            }
            if (eventStatus != EventStatus.STARTED) {
              sender.sendMessage(LanguageRegistry.getDefined().getCantStartDeathmatch());
              return;
            }
            if(Manager.getCreated().getDeathmatchLocation() == null) {
              sender.sendMessage(LanguageRegistry.getDefined().getUndefinedDeathmatch());
              return;
            }
            EventManager.getCreated().forceDm();
          },
          language.getNoPermission());
      return false;
    }

    // Subcommand: reload
    if ("reload".equals(action)) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      this.hasPermission(
          sender,
          () -> {
            WarriorEngine.getInstance().reloadPlugin();
            sender.sendMessage(LanguageRegistry.getDefined().getConfigReloaded()
                            .replace("{time}", stopwatch.stop().toString()));
            return;
          },
          language.getNoPermission());
      return false;
    }

    // Subcommand: set
    Manager manager = Manager.getCreated();
    if ("set".equals(action)) {
      if(!(sender instanceof Player)) return false;

      Player player = (Player) sender;
      this.hasPermission(
              player,
              () -> {
                if (args.length < 2) {
                  player.sendMessage(LanguageRegistry.getDefined().getSetUsage());
                  return;
                }

                String subAction = args[1];

                if ("entrada".equals(subAction)) {
                  Locations.get()
                          .getLocs()
                          .set("entrada", API.getCreated().unserializeLocation(player.getLocation()));
                  manager.setJoinLocation(player.getLocation());
                  Locations.get().saveLocs();
                  player.sendMessage(LanguageRegistry.getDefined().getSuccessSetJoin());
                  return;
                }

                if ("saida".equals(subAction)) {
                  Locations.get()
                          .getLocs()
                          .set("saida", API.getCreated().unserializeLocation(player.getLocation()));
                  Locations.get().saveLocs();
                  manager.setExitLocation(player.getLocation());
                  player.sendMessage(LanguageRegistry.getDefined().getSuccessSetLeave());
                  return;
                }

                if ("deathmatch".equals(subAction)) {
                  Locations.get()
                          .getLocs()
                          .set("deathmatch", API.getCreated().unserializeLocation(player.getLocation()));
                  Locations.get().saveLocs();
                  manager.setDeathmatchLocation(player.getLocation());
                  player.sendMessage(LanguageRegistry.getDefined().getSuccessSetDeathmatch());
                  return;
                }

                if("kit".equals(subAction)) {
                  for(ItemStack item : player.getInventory().getContents()) {
                    if(item != null && item.getType() != Material.AIR) {
                      ItemMeta im = item.getItemMeta();
                      im.setDisplayName(Config.get().getItems());
                      item.setItemMeta(im);
                    }
                  }
                  for(ItemStack item : player.getInventory().getArmorContents()) {
                    if(item != null && item.getType() != Material.AIR) {
                      ItemMeta im = item.getItemMeta();
                      im.setDisplayName(Config.get().getItems());
                      item.setItemMeta(im);
                    }
                  }

                  KitManager.get().getKitFile().set("armor", API.getCreated().serializeItems(player.getInventory().getArmorContents()));
                  Manager.getCreated().setArmor(API.getCreated().serializeItems(player.getInventory().getArmorContents()));
                  KitManager.get().getKitFile().set("items", API.getCreated().serializeItems(player.getInventory().getContents()));
                  Manager.getCreated().setItems(API.getCreated().serializeItems(player.getInventory().getContents()));
                  KitManager.get().saveKitFile();
                  player.getInventory().clear();
                  player.getInventory().setArmorContents(null);
                  player.sendMessage(LanguageRegistry.getDefined().getSuccessSetKit());
                  return;
                }

                player.sendMessage(LanguageRegistry.getDefined().getSetUsage());
              },
              language.getNoPermission());
      return false;
    }

    // Subcommand: join
    if ("entrar".equals(action) || "participar".equals(action) || "join".equals(action)) {
      if(!(sender instanceof Player)) return false;
      Player player = (Player) sender;
      if (eventStatus == EventStatus.OFF) {
        player.sendMessage(LanguageRegistry.getDefined().getNotStarted());
        return false;
      }

      boolean exitIsValid = manager.getExitLocation() != null;

      if (!exitIsValid) {
        player.sendMessage(LanguageRegistry.getDefined().getUndefinedExit());
        return false;
      }

      boolean entranceIsValid = manager.getJoinLocation() != null;

      if (!entranceIsValid) {
        player.sendMessage(LanguageRegistry.getDefined().getUndefinedJoin());
        return false;
      }

      boolean alreadyStarted =
              eventStatus == EventStatus.WAITING || eventStatus == EventStatus.STARTED || eventStatus == EventStatus.DEATHMATCH;

      if (alreadyStarted) {
        player.sendMessage(LanguageRegistry.getDefined().getClosedJoin());
        return false;
      }

      if (manager.getParticipants().contains(player)) {
        player.sendMessage(LanguageRegistry.getDefined().getAlreadyJoin());
        return false;
      }

      if(Bukkit.getVersion().toString().contains("1.8")) {
        if (API.getCreated().getFreeSlots(player) != 36 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage(LanguageRegistry.getDefined().getClearInventory());
          return false;
        }
      } else {
        if (API.getCreated().getFreeSlots(player) != 41 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage(LanguageRegistry.getDefined().getClearInventory());
          return false;
        }
      }

      player
              .getActivePotionEffects()
              .forEach(effect -> player.removePotionEffect(effect.getType()));
      manager.getParticipants().add(player);

      if (WarriorEngine.getInstance().isSimpleClans()
              && WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player) != null) {
        WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player).setFriendlyFire(true);
      }

      player.setAllowFlight(false);
      player.setFlying(false);

      player.teleport(manager.getJoinLocation());
      player.setMetadata("warriorKills", new FixedMetadataValue(WarriorEngine.getInstance(), 0));
      API.getCreated().equipPlayer(player);
      player.sendMessage(LanguageRegistry.getDefined().getSuccessJoin());
      return true;
    }

    if("resetkit".equals(action)) {
      this.hasPermission(
              sender,
              () -> {
                if (args.length > 1) {
                  sender.sendMessage(LanguageRegistry.getDefined().getResetkitUsage());
                  return;
                }
                KitManager.get().resetKit();
                sender.sendMessage(LanguageRegistry.getDefined().getSuccessResetkit());
                return;
              },
              language.getNoPermission());
      return false;
    }

    // Subcommand: leave
    if ("sair".equals(action) || "quit".equals(action)) {
      if(!(sender instanceof Player)) return false;
      Player player = (Player) sender;
      if (eventStatus == EventStatus.OFF) {
        player.sendMessage(LanguageRegistry.getDefined().getNotStarted());
        return false;
      }

      if (!manager.getParticipants().contains(player)) {
        player.sendMessage(LanguageRegistry.getDefined().getNotJoined());
        return false;
      }

      if (WarriorEngine.getInstance().isSimpleClans()
              && WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player) != null) {
        WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player).setFriendlyFire(true);
      }

      manager.getParticipants().remove(player);
      player.removeMetadata("warriorKills", WarriorEngine.getInstance());
      player.teleport(manager.getExitLocation());
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      player.getActivePotionEffects().clear();
      if (eventStatus == EventStatus.STARTED || eventStatus == EventStatus.DEATHMATCH) {
        EventManager.getCreated().verifyLastDuel();
      }
      player.sendMessage(LanguageRegistry.getDefined().getSuccessLeave());
      return true;
    }

    int participantsSize = manager.getParticipants().size();
    String formattedEventStatus =
            eventStatus == EventStatus.OFF
                    ? LanguageRegistry.getDefined().getInfoNotStarted()
                    : eventStatus == EventStatus.STARTED ? LanguageRegistry.getDefined().getInfoStarted()
                    : eventStatus == EventStatus.DEATHMATCH ? LanguageRegistry.getDefined().getInfoDeathmatch()
                    : LanguageRegistry.getDefined().getInfoStarting();

    // Subcommand: status
    if (action.equalsIgnoreCase("status") || action.equalsIgnoreCase("info")) {
      List<String> messageFormatted =
              language.getStatus().stream()
                      .map(
                              string -> {
                                return string
                                        .replace("{players}", String.valueOf(participantsSize))
                                        .replace("{time}", API.getCreated().formatTime(manager.getEventTime()))
                                        .replace("{status}", formattedEventStatus);
                              })
                      .collect(Collectors.toList());

      messageFormatted.forEach(sender::sendMessage);
      return true;
    }

    // Subcommand: camarote
    if ("camarote".equals(action)) {
      if(!(sender instanceof Player)) return false;
      Player player = (Player) sender;
      String message =
              eventStatus == EventStatus.OFF
                      ? LanguageRegistry.getDefined().getNotStarted()
                      : eventStatus == EventStatus.STARTED || eventStatus == EventStatus.DEATHMATCH
                      ? LanguageRegistry.getDefined().getWaitBox()
                      : LanguageRegistry.getDefined().getWaitStarting();

      if (manager.getParticipants().contains(player)) {
        player.sendMessage(LanguageRegistry.getDefined().getAlreadyJoin());
        return false;
      }

      if(Bukkit.getVersion().toString().contains("1.8")) {
        if (API.getCreated().getFreeSlots(player) != 36 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage(LanguageRegistry.getDefined().getClearInventory());
          return false;
        }
      } else {
        if (API.getCreated().getFreeSlots(player) != 41 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage(LanguageRegistry.getDefined().getClearInventory());
          return false;
        }
      }

      player.sendMessage(message);
      BoxManager.get().joinBox(player);
      return true;
    }

    // Subcommand: top
    if ("top".equals(action)) {
      if (args.length < 2) {
        sender.sendMessage(LanguageRegistry.getDefined().getTopUsage());
        return false;
      }

      String desiredTop = args[1].toLowerCase();


      if (!desiredTop.equals("vitorias") && !desiredTop.equals("abates") && !desiredTop.equals("wins") && !desiredTop.equals("kills")) {
        sender.sendMessage(LanguageRegistry.getDefined().getTopUsage());
        return false;
      }
      String desiredTopFormat = desiredTop.equals("vitorias") ? "Vitórias"
              : desiredTop.equals("wins") ? "Wins"
              : desiredTop.equals("kills") ? "Kills"
              : "Abates";

      AtomicInteger index = new AtomicInteger(1);
      List<String> fetchedRanking = API.getCreated().fetchRanking(desiredTop.equalsIgnoreCase("abates") ? "kills" : desiredTop);
      language.getTopHeader()
              .forEach(string -> sender.sendMessage(string.replace("{type}", desiredTopFormat)));
      fetchedRanking.forEach(
              string -> {
                sender.sendMessage(
                        language.getTopValue()
                                .replace("{type}", Integer.parseInt(string.split("\\(\\)")[1]) > 1 ? desiredTopFormat : desiredTopFormat.substring(0, desiredTopFormat.length()-1))
                                .replace("{position}", String.valueOf(index.get()))
                                .replace("{nick}", string.split("\\(\\)")[0])
                                .replace("{amount}", string.split("\\(\\)")[1]));
                index.set(index.get() + 1);
              });
      language.getTopFooter()
              .forEach(string -> sender.sendMessage(string));

      return true;
    }

    sendHelp(sender);
    return false;
  }

  void hasPermission(CommandSender player, Runnable runnable, String noPermissionMessage) {
    if (player.hasPermission("bgguerreiro.admin")) {
      runnable.run();
    } else {
      player.sendMessage(noPermissionMessage);
    }
  }
}
