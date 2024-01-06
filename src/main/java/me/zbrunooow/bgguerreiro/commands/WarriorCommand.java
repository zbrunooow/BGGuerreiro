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
              sender.sendMessage("§cUse /guerreiro forcestart");
              return;
            }
            if (Manager.getCreated().getItems() == null) {
              sender.sendMessage("§cO kit do evento Guerreiro não foi definido!");
              return;
            }
            if (eventStatus != EventStatus.OFF) {
              sender.sendMessage("§cO evento Guerreiro já está acontecendo!");
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
            if (eventStatus == EventStatus.OFF) {
              sender.sendMessage("§cO evento Guerreiro não está acontecendo!");
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
            if(eventStatus == EventStatus.DEATHMATCH) {
              sender.sendMessage("§cO evento Guerreiro já está no DeathMatch!");
              return;
            }
            if (eventStatus != EventStatus.STARTED) {
              sender.sendMessage("§cO evento Guerreiro não está acontecendo!");
              return;
            }
            if(Manager.getCreated().getDeathmatchLocation() == null) {
              sender.sendMessage("§cO DeathMatch do evento Guerreiro não foi setado!!");
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
            sender.sendMessage(
                "§aA configuração do plugin foi recarregada em "
                    + stopwatch.stop().toString()
                    + "!");
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
                  player.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch/kit)");
                  return;
                }

                String subAction = args[1];

                if ("entrada".equals(subAction)) {
                  Locations.get()
                          .getLocs()
                          .set("entrada", API.getCreated().unserializeLocation(player.getLocation()));
                  manager.setJoinLocation(player.getLocation());
                  Locations.get().saveLocs();
                  player.sendMessage("§aA entrada do evento Guerreiro foi setada com sucesso!");
                  return;
                }

                if ("saida".equals(subAction)) {
                  Locations.get()
                          .getLocs()
                          .set("saida", API.getCreated().unserializeLocation(player.getLocation()));
                  Locations.get().saveLocs();
                  manager.setExitLocation(player.getLocation());
                  player.sendMessage("§aA saída do evento Guerreiro foi setada com sucesso!");
                  return;
                }

                if ("deathmatch".equals(subAction)) {
                  Locations.get()
                          .getLocs()
                          .set("deathmatch", API.getCreated().unserializeLocation(player.getLocation()));
                  Locations.get().saveLocs();
                  manager.setDeathmatchLocation(player.getLocation());
                  player.sendMessage("§aO deathmatch do evento Guerreiro foi setada com sucesso!");
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

                  KitManager.get().getKitFile().set("armor", API.getCreated().serializeItems(player.getInventory().getArmorContents()));
                  Manager.getCreated().setArmor(API.getCreated().serializeItems(player.getInventory().getArmorContents()));
                  KitManager.get().getKitFile().set("items", API.getCreated().serializeItems(player.getInventory().getContents()));
                  Manager.getCreated().setItems(API.getCreated().serializeItems(player.getInventory().getContents()));
                  KitManager.get().saveKitFile();
                  player.getInventory().clear();
                  player.getInventory().setArmorContents(null);
                  player.sendMessage("§aVocê setou os itens do evento Guerreiro!");
                  return;
                }

                player.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch/kit)");
              },
              language.getNoPermission());
      return false;
    }

    // Subcommand: join
    if ("entrar".equals(action) || "participar".equals(action) || "join".equals(action)) {
      if(!(sender instanceof Player)) return false;
      Player player = (Player) sender;
      if (eventStatus == EventStatus.OFF) {
        player.sendMessage("§cO evento Guerreiro não está acontecendo!");
        return false;
      }

      boolean exitIsValid = manager.getExitLocation() != null;

      if (!exitIsValid) {
        player.sendMessage("§cA saída do evento Guerreiro não foi setada!");
        return false;
      }

      boolean entranceIsValid = manager.getJoinLocation() != null;

      if (!entranceIsValid) {
        player.sendMessage("§cA entrada do evento Guerreiro não foi setada!");
        return false;
      }

      boolean alreadyStarted =
              eventStatus == EventStatus.WAITING || eventStatus == EventStatus.STARTED || eventStatus == EventStatus.DEATHMATCH;

      if (alreadyStarted) {
        player.sendMessage("§cA entrada para o evento Guerreiro já fechou!");
        return false;
      }

      if (manager.getParticipants().contains(player)) {
        player.sendMessage("§cVocê está participando do evento Guerreiro!");
        return false;
      }

      if(Bukkit.getVersion().toString().contains("1.8")) {
        if (API.getCreated().getFreeSlots(player) != 36 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage("§cEsvazie o inventário para entrar no evento!");
          return false;
        }
      } else {
        if (API.getCreated().getFreeSlots(player) != 41 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage("§cEsvazie o inventário para entrar no evento!");
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
      player.sendMessage("§aVocê entrou no evento Guerreiro!");
      return true;
    }

    if("resetkit".equals(action)) {
      this.hasPermission(
              sender,
              () -> {
                if (args.length > 1) {
                  sender.sendMessage("§cUse /guerreiro resetkit");
                  return;
                }
                KitManager.get().resetKit();
                sender.sendMessage("§aVocê redefiniu o kit do evento Guerreiro!");
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
        player.sendMessage("§cO evento Guerreiro não está acontecendo!");
        return false;
      }

      if (!manager.getParticipants().contains(player)) {
        player.sendMessage("§cVocê não está participando do evento Guerreiro!");
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
      player.sendMessage("§aVocê saiu do evento Guerreiro!");
      return true;
    }

    int participantsSize = manager.getParticipants().size();
    String formattedEventStatus =
            eventStatus == EventStatus.OFF
                    ? "§cNão acontecendo"
                    : eventStatus == EventStatus.STARTED ? "§aAcontecendo" : eventStatus == EventStatus.DEATHMATCH ? "§4DeathMatch" : "§eIniciando...";

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
                      ? "§cO evento Guerreiro não está acontecendo!"
                      : eventStatus == EventStatus.STARTED || eventStatus == EventStatus.DEATHMATCH
                      ? "§cAguarde enquanto estamos te enviando para o camarote!"
                      : "§cAguarde enquanto o evento Guerreiro inicia!";

      if (manager.getParticipants().contains(player)) {
        player.sendMessage("§cVocê já está participando do evento Guerreiro!");
        return false;
      }

      if(Bukkit.getVersion().toString().contains("1.8")) {
        if (API.getCreated().getFreeSlots(player) != 36 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage("§cEsvazie o inventário para entrar no camarote evento!");
          return false;
        }
      } else {
        if (API.getCreated().getFreeSlots(player) != 41 || API.getCreated().getArmor(player) != 0) {
          player.sendMessage("§cEsvazie o inventário para entrar no camarote evento!");
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
        sender.sendMessage("§cUse /guerreiro top (vitorias/abates)");
        return false;
      }

      String desiredTop = args[1].toLowerCase();

      if (!desiredTop.equals("vitorias") && !desiredTop.equals("abates")) {
        sender.sendMessage("§cUse /guerreiro top (vitorias/abates)");
        return false;
      }
      String desiredTopFormat = desiredTop.equals("vitorias") ? "Vitórias" : "Abates";

      AtomicInteger index = new AtomicInteger(1);
      List<String> fetchedRanking = API.getCreated().fetchRanking(desiredTop);
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
