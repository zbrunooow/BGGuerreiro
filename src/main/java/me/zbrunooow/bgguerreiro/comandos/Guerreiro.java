package me.zbrunooow.bgguerreiro.comandos;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.stream.Collectors;
import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.API;
import me.zbrunooow.bgguerreiro.utils.Locations;
import me.zbrunooow.bgguerreiro.utils.Manager;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Guerreiro implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender commandSender, Command cmd, String lb, String[] args) {
    if (!(commandSender instanceof Player)) {
      Core.getMessages().getGeneralCommand().forEach(commandSender::sendMessage);
      return false;
    }

    Player player = (Player) commandSender;

    // Insufficient Args
    if (args.length == 0) {
      for (String str : Core.getMessages().getGeneralCommand()) {
        if (str.contains("force") || str.contains("set")) {
          if (player.hasPermission("bgguerreiro.admin")) {
            player.sendMessage(str);
          }
        } else {
          player.sendMessage(str);
        }
      }
      return false;
    }

    String action = args[0].toLowerCase();

    // Subcommand: start
    String eventStatus = Evento.get().getStartado();
    if ("iniciar".equals(action) || "forcestart".equals(action) || "start".equals(action)) {
      this.hasPermission(
          player,
          "bgguerreiro.admin",
          () -> {
            if (args.length > 1) {
              player.sendMessage("§cUse /guerreiro forcestart");
              return;
            }
            if (!eventStatus.equalsIgnoreCase("off")) {
              player.sendMessage("§cO evento guerreiro já está começando!");
              return;
            }
            Evento.get().iniciar();
            return;
          },
          Core.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: cancel
    if ("cancelar".equals(action) || "forcestop".equals(action) || "stop".equals(action)) {
      this.hasPermission(
          player,
          "bgguerreiro.admin",
          () -> {
            if (eventStatus.equalsIgnoreCase("off")) {
              player.sendMessage(eventStatus);
              player.sendMessage("§cO evento guerreiro não está acontecendo!");
              return;
            }
            Evento.get().cancelar(true);
          },
          Core.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: forcedm
    if ("forcedm".equals(action)) {
      this.hasPermission(
          player,
          "bgguerreiro.admin",
          () -> {
            if (!eventStatus.equalsIgnoreCase("iniciado")) {
              player.sendMessage("§cO evento guerreiro não está acontecendo!");
              return;
            }
            Evento.get().forceDm();
          },
          Core.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: reload
    if ("reload".equals(action)) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      this.hasPermission(
          player,
          "bgguerreiro.admin",
          () -> {
            Core.getInstance().reloadPlugin();
            player.sendMessage(
                "§aA configuração do plugin foi recarregada em "
                    + stopwatch.stop().toString()
                    + "!");
            return;
          },
          Core.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: set
    Manager manager = Manager.get();
    if ("set".equals(action)) {
      this.hasPermission(
          player,
          "bgguerreiro.admin",
          () -> {
            if (args.length < 2) {
              player.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch)");
              return;
            }

            String subAction = args[1];

            if ("entrada".equals(subAction)) {
              Locations.get()
                  .getLocs()
                  .set("entrada", API.get().unserializeLocation(player.getLocation()));
              manager.setEntrada(player.getLocation());
              player.sendMessage("§aA entrada do evento Guerreiro foi setada com sucesso!");
              return;
            }

            if ("saida".equals(subAction)) {
              Locations.get()
                  .getLocs()
                  .set("saida", API.get().unserializeLocation(player.getLocation()));
              manager.setSaida(player.getLocation());
              player.sendMessage("§aA saída do evento Guerreiro foi setada com sucesso!");
              return;
            }

            if ("deathmatch".equals(subAction)) {
              Locations.get()
                  .getLocs()
                  .set("deathmatch", API.get().unserializeLocation(player.getLocation()));
              manager.setDeathmatch(player.getLocation());
              player.sendMessage("§aO deathmatch do evento Guerreiro foi setada com sucesso!");
              return;
            }

            player.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch)");
          },
          Core.getMessages().getNoPermission());
      return false;
    }

    // Subcommand: join
    ClanPlayer clanPlayer = Core.getSC().getClanManager().getClanPlayer(player);
    if ("entrar".equals(action) || "participar".equals(action) || "join".equals(action)) {
      if (eventStatus.equalsIgnoreCase("off")) {
        player.sendMessage("§cO evento guerreiro não está acontecendo!");
        return false;
      }

      boolean exitIsValid = manager.getSaida() != null;

      if (!exitIsValid) {
        player.sendMessage("§cA saída do evento Guerreiro não foi setada!");
        return false;
      }

      boolean entranceIsValid = manager.getEntrada() != null;

      if (!entranceIsValid) {
        player.sendMessage("§cA entrada do evento Guerreiro não foi setada!");
        return false;
      }

      boolean alreadyStarted =
          eventStatus.equalsIgnoreCase("espera") || eventStatus.equalsIgnoreCase("iniciado");

      if (alreadyStarted) {
        player.sendMessage("§cA entrada para o evento guerreiro já fechou!");
        return false;
      }

      if (manager.getParticipantes().contains(player)) {
        player.sendMessage("§cVocê está participando do evento Guerreiro!");
        return false;
      }

      if (API.get().getFreeSlots(player) != 36 || API.get().getArmor(player) != 0) {
        player.sendMessage("§cEsvazie o inventário para entrar no evento!");
        return false;
      }

      player
          .getActivePotionEffects()
          .forEach(effect -> player.removePotionEffect(effect.getType()));
      manager.getParticipantes().add(player);

      if (Core.getInstance().sc && clanPlayer != null) {
        clanPlayer.setFriendlyFire(true);
      }

      player.teleport(manager.getEntrada());
      player.setMetadata("warriorKills", new FixedMetadataValue(Core.getInstance(), 0));
      API.get().equipPlayer(player);
      player.sendMessage("§aVocê entrou no evento guerreiro!");
      return true;
    }

    // Subcommand: leave
    if ("sair".equals(action) || "quit".equals(action)) {
      if (eventStatus.equalsIgnoreCase("off")) {
        player.sendMessage("§cO evento guerreiro não está acontecendo!");
        return false;
      }

      if (!manager.getParticipantes().contains(player)) {
        player.sendMessage("§cVocê não está participando do evento Guerreiro!");
        return false;
      }

      if (Core.getInstance().sc && clanPlayer != null) {
        clanPlayer.setFriendlyFire(true);
      }

      manager.getParticipantes().remove(player);
      player.removeMetadata("warriorKills", Core.getInstance());
      player.teleport(manager.getSaida());
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      if(eventStatus.equalsIgnoreCase("iniciado")) {
        Evento.get().verificarFinal();
      }
      player.sendMessage("§aVocê saiu do evento guerreiro!");
      return true;
    }

    int participantsSize = manager.getParticipantes().size();
    String formattedEventStatus =
        eventStatus.equals("off")
            ? "§cNão acontecendo"
            : eventStatus.equals("iniciado") ? "§aAcontecendo" : "§eIniciando...";

    // Subcommand: status
    if (action.equalsIgnoreCase("status") || action.equalsIgnoreCase("info")) {
      List<String> messageFormatted =
          Core.getMessages().getStatus().stream()
              .map(
                  string -> {
                    return string
                        .replace("{players}", String.valueOf(participantsSize))
                        .replace("{tempo}", API.get().formatTime(manager.getTempoEvento()))
                        .replace("", formattedEventStatus);
                  })
              .collect(Collectors.toList());

      messageFormatted.forEach(player::sendMessage);
      return true;
    }

    // Subcommand: camarote
    if ("camarote".equals(action)) {
      String message =
          eventStatus.equals("off")
              ? "§cO evento Guerreiro não está acontecendo!"
              : eventStatus.equals("iniciado")
                  ? "§cAguarde enquanto estamos te enviando para o camarote!"
                  : "§cAguarde enquanto o evento Guerreiro inicia!";

      if (manager.getParticipantes().contains(player)) {
        player.sendMessage("§cVocê já está participando do evento Guerreiro!");
        return false;
      }

      if (API.get().getFreeSlots(player) != 36 || API.get().getArmor(player) != 0) {
        player.sendMessage("§cEsvazie o inventário para entrar no evento!");
        return false;
      }

      CamaroteManager.get().joinCamarote(player);
      player.sendMessage(message);
      return true;
    }

    if (action.equalsIgnoreCase("top")) {
      if (args.length != 2) {
        player.sendMessage("§cUse /guerreiro top (vitorias/abates)");
        return false;
      }
      if (args[1].equalsIgnoreCase("vitorias")) {
        for (String str : Core.getMessages().getTopHeader()) {
          player.sendMessage(str.replace("{type}", "Vitórias"));
        }
        int i = 1;
        for (String str : API.get().listaTop("vitorias")) {
          player.sendMessage(
              Core.getMessages()
                  .getTopValue()
                  .replace("{type}", "vitorias")
                  .replace("{posicao}", String.valueOf(i))
                  .replace("{nick}", str.split("\\(\\)")[0])
                  .replace("{amount}", str.split("\\(\\)")[1]));
          i++;
        }
        for (String str : Core.getMessages().getTopFooter()) {
          player.sendMessage(str);
        }
      } else if (args[1].equalsIgnoreCase("kills") || args[1].equalsIgnoreCase("abates")) {
        for (String str : Core.getMessages().getTopHeader()) {
          player.sendMessage(str.replace("{type}", "Abates"));
        }
        int i = 1;
        for (String str : API.get().listaTop("kills")) {
          player.sendMessage(
              Core.getMessages()
                  .getTopValue()
                  .replace("{type}", "abates")
                  .replace("{posicao}", String.valueOf(i))
                  .replace("{nick}", str.split("\\(\\)")[0])
                  .replace("{amount}", str.split("\\(\\)")[1]));
          i++;
        }
        for (String str : Core.getMessages().getTopFooter()) {
          player.sendMessage(str);
        }
      }
      return true;
    } else {
      for (String str : Core.getMessages().getGeneralCommand()) {
        if (str.contains("force") || str.contains("set")) {
          if (player.hasPermission("bgguerreiro.admin")) {
            player.sendMessage(str);
          }
        } else {
          player.sendMessage(str);
        }
      }
      return false;
    }
  }

  void hasPermission(
      Player player, String permission, Runnable runnable, String noPermissionMessage) {
    if (player.hasPermission(permission)) {
      runnable.run();
    } else {
      player.sendMessage(noPermissionMessage);
    }
  }
}
