package me.zbrunooow.bgguerreiro.commands;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.API;
import me.zbrunooow.bgguerreiro.util.Locations;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class WarriorCommand implements CommandExecutor {

  private static void sendHelp(Player player) {
    for (String str : WarriorEngine.getMessages().getGeneralCommand()) {
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
    if (!(commandSender instanceof Player)) {
      WarriorEngine.getMessages().getGeneralCommand().forEach(commandSender::sendMessage);
      return false;
    }

    Player player = (Player) commandSender;

    // Insufficient Args
    if (args.length == 0) {
      sendHelp(player);
      return false;
    }

    String action = args[0].toLowerCase();

    // Subcommand: start
    EventStatus eventStatus = EventManager.getCreated().getStatus();
    if ("iniciar".equals(action) || "forcestart".equals(action) || "start".equals(action)) {
      this.hasPermission(
          player,
          () -> {
            if (args.length > 1) {
              player.sendMessage("§cUse /guerreiro forcestart");
              return;
            }
            if (eventStatus != EventStatus.OFF) {
              player.sendMessage("§cO evento guerreiro já está começando!");
              return;
            }
            EventManager.getCreated().startEvent();
            return;
          },
          WarriorEngine.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: cancel
    if ("cancelar".equals(action) || "forcestop".equals(action) || "stop".equals(action)) {
      this.hasPermission(
          player,
          () -> {
            if (eventStatus == EventStatus.OFF) {
              player.sendMessage("§cO evento guerreiro não está acontecendo!");
              return;
            }
            EventManager.getCreated().cancelar(true);
          },
          WarriorEngine.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: forcedm
    if ("forcedm".equals(action) || "forcedeathmatch".equals(action)) {
      this.hasPermission(
          player,
          () -> {
            if (eventStatus != EventStatus.STARTED) {
              player.sendMessage("§cO evento guerreiro não está acontecendo!");
              return;
            }
            EventManager.getCreated().forceDm();
          },
          WarriorEngine.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: reload
    if ("reload".equals(action)) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      this.hasPermission(
          player,
          () -> {
            WarriorEngine.getInstance().reloadPlugin();
            player.sendMessage(
                "§aA configuração do plugin foi recarregada em "
                    + stopwatch.stop().toString()
                    + "!");
            return;
          },
          WarriorEngine.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: set
    Manager manager = Manager.getCreated();
    if ("set".equals(action)) {
      this.hasPermission(
          player,
          () -> {
            if (args.length < 2) {
              player.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch)");
              return;
            }

            String subAction = args[1];

            if ("entrada".equals(subAction)) {
              Locations.get()
                  .getLocs()
                  .set("entrada", API.getCreated().unserializeLocation(player.getLocation()));
              manager.setJoinLocation(player.getLocation());
              player.sendMessage("§aA entrada do evento Guerreiro foi setada com sucesso!");
              return;
            }

            if ("saida".equals(subAction)) {
              Locations.get()
                  .getLocs()
                  .set("saida", API.getCreated().unserializeLocation(player.getLocation()));
              manager.setExitLocation(player.getLocation());
              player.sendMessage("§aA saída do evento Guerreiro foi setada com sucesso!");
              return;
            }

            if ("deathmatch".equals(subAction)) {
              Locations.get()
                  .getLocs()
                  .set("deathmatch", API.getCreated().unserializeLocation(player.getLocation()));
              manager.setDeathmatchLocation(player.getLocation());
              player.sendMessage("§aO deathmatch do evento Guerreiro foi setada com sucesso!");
              return;
            }

            player.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch)");
          },
          WarriorEngine.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: join
    if ("entrar".equals(action) || "participar".equals(action) || "join".equals(action)) {
      if (eventStatus == EventStatus.OFF) {
        player.sendMessage("§cO evento guerreiro não está acontecendo!");
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
          eventStatus == EventStatus.WAITING || eventStatus == EventStatus.STARTED;

      if (alreadyStarted) {
        player.sendMessage("§cA entrada para o evento guerreiro já fechou!");
        return false;
      }

      if (manager.getParticipants().contains(player)) {
        player.sendMessage("§cVocê está participando do evento Guerreiro!");
        return false;
      }

      if (API.getCreated().getFreeSlots(player) != 36 || API.getCreated().getArmor(player) != 0) {
        player.sendMessage("§cEsvazie o inventário para entrar no evento!");
        return false;
      }

      player
          .getActivePotionEffects()
          .forEach(effect -> player.removePotionEffect(effect.getType()));
      manager.getParticipants().add(player);

      if (WarriorEngine.getInstance().isSimpleClans()
          && WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player) != null) {
        WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player).setFriendlyFire(true);
      }

      player.teleport(manager.getJoinLocation());
      player.setMetadata("warriorKills", new FixedMetadataValue(WarriorEngine.getInstance(), 0));
      API.getCreated().equipPlayer(player);
      player.sendMessage("§aVocê entrou no evento guerreiro!");
      return true;
    }

    // Subcommand: leave
    if ("sair".equals(action) || "quit".equals(action)) {
      if (eventStatus == EventStatus.OFF) {
        player.sendMessage("§cO evento guerreiro não está acontecendo!");
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
      if (eventStatus == EventStatus.STARTED) {
        EventManager.getCreated().verifyLastDuel();
      }
      player.sendMessage("§aVocê saiu do evento guerreiro!");
      return true;
    }

    int participantsSize = manager.getParticipants().size();
    String formattedEventStatus =
        eventStatus == EventStatus.OFF
            ? "§cNão acontecendo"
            : eventStatus == EventStatus.STARTED ? "§aAcontecendo" : "§eIniciando...";

    // Subcommand: status
    if (action.equalsIgnoreCase("status") || action.equalsIgnoreCase("info")) {
      List<String> messageFormatted =
          WarriorEngine.getMessages().getStatus().stream()
              .map(
                  string -> {
                    return string
                        .replace("{players}", String.valueOf(participantsSize))
                        .replace("{tempo}", API.getCreated().formatTime(manager.getEventTime()))
                        .replace("{status}", formattedEventStatus);
                  })
              .collect(Collectors.toList());

      messageFormatted.forEach(player::sendMessage);
      return true;
    }

    // Subcommand: camarote
    if ("camarote".equals(action)) {
      String message =
          eventStatus == EventStatus.OFF
              ? "§cO evento Guerreiro não está acontecendo!"
              : eventStatus == EventStatus.STARTED
                  ? "§cAguarde enquanto estamos te enviando para o camarote!"
                  : "§cAguarde enquanto o evento Guerreiro inicia!";

      if (manager.getParticipants().contains(player)) {
        player.sendMessage("§cVocê já está participando do evento Guerreiro!");
        return false;
      }

      if (API.getCreated().getFreeSlots(player) != 36 || API.getCreated().getArmor(player) != 0) {
        player.sendMessage("§cEsvazie o inventário para entrar no evento!");
        return false;
      }

      BoxManager.get().joinCamarote(player);
      player.sendMessage(message);
      return true;
    }

    // Subcommand: top
    if ("top".equals(action)) {
      if (args.length < 2) {
        player.sendMessage("§cUse /guerreiro top (vitorias/abates)");
        return false;
      }

      String desiredTop = args[1].toLowerCase();

      if (!desiredTop.equals("vitorias") && !desiredTop.equals("abates")) {
        player.sendMessage("§cUse /guerreiro top (vitorias/abates)");
        return false;
      }
      String desiredTopFormat = desiredTop.equals("vitorias") ? "Vitórias" : "Abates";

      AtomicInteger index = new AtomicInteger(1);
      List<String> fetchedRanking = API.getCreated().fetchRanking(desiredTop);
      WarriorEngine.getMessages()
          .getTopHeader()
          .forEach(string -> player.sendMessage(string.replace("{type}", desiredTopFormat)));
      fetchedRanking.forEach(
          string -> {
            player.sendMessage(
                WarriorEngine.getMessages()
                    .getTopValue()
                    .replace("{type}", desiredTopFormat)
                    .replace("{posicao}", String.valueOf(index.get()))
                    .replace("{nick}", string.split("\\(\\)")[0])
                    .replace("{amount}", string.split("\\(\\)")[1]));
            index.set(index.get() + 1);
          });

      return true;
    }

    sendHelp(player);
    return false;
  }

  void hasPermission(Player player, Runnable runnable, String noPermissionMessage) {
    if (player.hasPermission("bgguerreiro.admin")) {
      runnable.run();
    } else {
      player.sendMessage(noPermissionMessage);
    }
  }
}
