package me.zbrunooow.bgguerreiro.manager;

import lombok.Getter;
import lombok.Setter;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.hooks.VaultHook;
import me.zbrunooow.bgguerreiro.lang.Language;
import me.zbrunooow.bgguerreiro.lang.LanguageRegistry;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.API;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Console;

@Getter
@Setter
public class EventManager {

  private final Manager manager = Manager.getCreated();
  private int pvpDeathMatch = 0;
  private EventStatus status = EventStatus.OFF;
  private boolean pvp = false;
  private boolean deathMatch = false;
  private boolean forcedDeathmatch = false;
  private Language language = LanguageRegistry.getDefined();

  public static EventManager getCreated() {
    return WarriorEngine.getInstance().getEventManager();
  }

  public void startEvent() {
    API.getCreated().removeLastWinner();

    setStatus(EventStatus.ANNOUNCING);
    setPvp(false);
    setDeathMatch(false);
    setPvpDeathMatch(0);
    setForcedDeathmatch(false);
    Manager.getCreated().setEventTime(0);

    new BukkitRunnable() {
      int seconds = Config.get().getTempoAnuncios();
      int next = seconds;
      int waitTime = Config.get().getTempoEspera();

      @Override
      public void run() {
        EventStatus eventStatus = getStatus();
        if (eventStatus == EventStatus.OFF) {
          cancel();
          return;
        }

        switch (eventStatus) {
          case ANNOUNCING:
            handleAnnouncing();
            break;
          case WAITING:
            handleWaiting();
            break;
          case STARTED:
            handleStarted();
            break;
          case DEATHMATCH:
            handleDeathMatch();
            break;
        }
      }

      private void handleAnnouncing() {
        if (seconds > 0) {
          announceCountdown();
        } else {
          startWaiting();
        }
      }

      private void announceCountdown() {
        if (seconds == next) {
          broadcastAnnouncementMessages();
          next -= Config.get().getTempoEntreAnuncios();
        }
        seconds--;
      }

      private void broadcastAnnouncementMessages() {
        for (String str : language.getStarting()) {
          API.getCreated().broadcastMessage(formatAnnouncementMessage(str));
        }
      }

      private String formatAnnouncementMessage(String message) {
        return message
            .replace("{seconds}", String.valueOf(seconds))
            .replace("{tag}", Config.get().getTag())
            .replace(
                "{prize}",
                Config.get().isFormatPrize()
                    ? API.formatPrize(Config.get().getPremio())
                    : String.valueOf(Config.get().getPremio()))
            .replace("{players}", String.valueOf(Manager.getCreated().getParticipants().size()));
      }

      private void startWaiting() {
        if (Manager.getCreated().getParticipants().size() < Config.get().getMinimoParticipantes()) {
          handleInsufficientParticipants();
        } else {
          setStatus(EventStatus.WAITING);
          broadcastWaitingMessages();
        }
        next = Config.get().getTempoEspera() - 5;
      }

      private void handleInsufficientParticipants() {
        API.getCreated()
            .broadcastMessage(
                "§cO evento Guerreiro foi cancelado por falta de participantes. (Mínimo "
                    + Config.get().getMinimoParticipantes()
                    + ")");
        cancelar(false);
        cancel();
      }

      private void broadcastWaitingMessages() {
        for (String str : language.getWaiting()) {
          API.getCreated()
              .broadcastMessageToParticipants(
                  str.replace("{time}", String.valueOf(Config.get().getTempoEspera())));
        }
      }

      private void handleWaiting() {
        if (waitTime > 0) {
          if (waitTime == next) {
            for (String str : language.getWaiting()) {
              API.getCreated()
                  .broadcastMessageToParticipants(str.replace("{time}", String.valueOf(waitTime)));
            }
            next = waitTime - 5;
          }
        } else {
          setStatus(EventStatus.STARTED);
          setPvp(true);
          for (String str : language.getStarted()) {
            API.getCreated().broadcastMessageToParticipants(str);
          }
        }
        waitTime--;
      }

      private void handleDeathMatch() {
        Manager.getCreated().setEventTime(Manager.getCreated().getEventTime() + 1);
        broadcastActionbar();

        int now = Manager.getCreated().getEventTime();
        if (getPvpDeathMatch() - now > 0 && getPvpDeathMatch() - now <= 3) {
          API.getCreated().broadcastMessageToParticipants(LanguageRegistry.getDefined().getReactivatingDeathMatchPvP().replace("{seconds}", String.valueOf(getPvpDeathMatch()-now)));
        }
        if(getPvpDeathMatch() == Manager.getCreated().getEventTime()) {
          for(String str : LanguageRegistry.getDefined().getPvpEnabledDeathMatch()) {
            API.getCreated().broadcastMessageToParticipants(str);
          }
          setPvp(true);
        }
      }

      private void handleStarted() {
        Manager.getCreated().setEventTime(Manager.getCreated().getEventTime() + 1);
        broadcastActionbar();

        if(Config.get().isDmHabilitado()) {
          int now = Manager.getCreated().getEventTime();
          int dmTime = Config.get().getDmTempo();
          if(dmTime == now+120 || dmTime == now+60) {
            for(String str : LanguageRegistry.getDefined().getDeathmatchComing()) {
              API.getCreated().broadcastMessageToParticipants(str.replace("{minutes}", dmTime == now+120 ? "2" : "1").replace("minutos", dmTime == now+60 ? "minuto" : "minutos"));
            }
          }
          if(dmTime == now) {
            EventManager.getCreated().forceDm();
          }
        }
      }

      private void broadcastActionbar() {
        String actionbarMessage =
            Manager.getCreated().getEventTime() <= 10
                ? language.getStatusActionbar()
                : language.getInfoActionbar();

        API.getCreated().broadcastActionbarToParticipants(formatActionbarMessage(actionbarMessage));
      }

      private String formatActionbarMessage(String message) {
        return message
            .replace("{players}", String.valueOf(Manager.getCreated().getParticipants().size()))
            .replace("{time}", API.getCreated().formatTime(Manager.getCreated().getEventTime()))
            .replace(
                "{status}",
                (!(getStatus() == EventStatus.STARTED) ? "&cIniciando..." : "&aValendo"));
      }
    }.runTaskTimerAsynchronously(WarriorEngine.getInstance(), 0L, 20L);
  }

  public void forceDm() {
    setForcedDeathmatch(true);
    setDeathMatch(true);
    setPvp(false);
    setPvpDeathMatch(Manager.getCreated().getEventTime() + Config.get().getDmSemPvP());
    broadcastDeathmatchStartMessages();
    teleportParticipantsToDeathmatchLocation();
    teleportSpectatorsToDeathatchLocation();
    EventManager.getCreated().setStatus(EventStatus.DEATHMATCH);
  }

  private void broadcastDeathmatchStartMessages() {
    for (String str : language.getDeathmatchStart()) {
      API.getCreated()
          .broadcastMessageToParticipants(
              str.replace("{seconds}", String.valueOf(Config.get().getDmSemPvP())));
    }
  }

  private void teleportParticipantsToDeathmatchLocation() {
    for (Player vivos : Manager.getCreated().getParticipants()) {
      vivos.teleport(Manager.getCreated().getDeathmatchLocation());
    }
  }

  private void teleportSpectatorsToDeathatchLocation() {
    for(Player spectator : BoxManager.get().getSpectators()) {
      spectator.teleport(Manager.getCreated().getDeathmatchLocation());
    }
  }

  public void cancelar(boolean broadcast) {
    forcedDeathmatch = false;
    setStatus(EventStatus.OFF);
    setPvp(false);
    clearParticipantsInventoryAndTeleportToExitLocation();
    clearParticipantsList();

    if (broadcast) {
      broadcastCancellationMessages();
    }
  }

  private void clearParticipantsInventoryAndTeleportToExitLocation() {
    for (Player p : Manager.getCreated().getParticipants()) {
      p.getInventory().clear();
      p.getInventory().setArmorContents(null);
      p.teleport(Manager.getCreated().getExitLocation());
    }
  }

  private void clearParticipantsList() {
    Manager.getCreated().getParticipants().clear();
    Manager.getCreated().setEventTime(0);
  }

  private void broadcastCancellationMessages() {
    for (String str : language.getCancelled()) {
      API.getCreated().broadcastMessage(str);
    }
  }

  public void verifyLastDuel() {
    Manager manager = Manager.getCreated();
    if (manager.getParticipants().size() >= 2) {
      broadcastSurvivorsLeftMessage(manager.getParticipants().size());
      return;
    }

    handleWinner(manager);
  }

  private void broadcastSurvivorsLeftMessage(int participantsCount) {
    API.getCreated()
        .broadcastMessageToParticipants(
            language.getSurvivorsLeft().replace("{remaining}", String.valueOf(participantsCount)));
  }

  private void handleWinner(Manager manager) {
    Player winner = manager.getParticipants().get(0);
    int kills = getWarriorKillsMetadata(winner);
    broadcastWinnerMessages(winner, kills);
    clearEffectsAndMetadata(winner);
    savePlayerStatus(winner, true, kills);
    handleParticipantsAndEconomy(winner);
    winner.setMetadata("waitingLeave", new FixedMetadataValue(WarriorEngine.getInstance(), true));
    new BukkitRunnable() {
      @Override
      public void run() {
        if (winner.isOnline()) {
          winner.getInventory().clear();
          winner.getInventory().setArmorContents(null);
          winner.updateInventory();
          winner.teleport(manager.getExitLocation());
          winner.removeMetadata("waitingLeave", WarriorEngine.getInstance());

          for(String cmd : Config.get().getWonCommands()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replace("{player}", winner.getName()));
          }
        }
        Manager.getCreated().setEventTime(0);
        setStatus(EventStatus.OFF);
      }
    }.runTaskLater(WarriorEngine.getInstance(), 20 * Config.get().getTempoFinal());
  }

  private int getWarriorKillsMetadata(Player player) {
    return Integer.parseInt(String.valueOf(player.getMetadata("warriorKills").get(0).value()));
  }

  private void broadcastWinnerMessages(Player winner, int kills) {
    for (String str : language.getYouWin()) {
      winner.sendMessage(str.replace("{time}", String.valueOf(Config.get().getTempoFinal())));
    }
    for (String str : language.getExpired()) {
      API.getCreated()
          .broadcastMessage(formatWinnerMessage(str, winner, kills, Config.get().isFormatPrize()));
    }
  }

  private String formatWinnerMessage(
      String message, Player winner, int kills, boolean formatPrize) {
    return message
        .replace("{kills}", String.valueOf(kills))
        .replace("{duration}", API.getCreated().formatTime(Manager.getCreated().getEventTime()))
        .replace("{tag}", Config.get().getTag())
        .replace("{winner}", winner.getName())
        .replace(
            "{prize}",
            formatPrize
                ? API.formatPrize(Config.get().getPremio())
                : String.valueOf(Config.get().getPremio()));
  }

  private void clearEffectsAndMetadata(Player player) {
    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }
    player.removeMetadata("warriorKills", WarriorEngine.getInstance());
  }

  private void savePlayerStatus(Player player, boolean winner, int kills) {
    API.getCreated().salvarStatus(player, winner, kills);
  }

  private void handleParticipantsAndEconomy(Player winner) {
    clearParticipantsListAndEconomy(winner);
    handleClanFriendlyFire(winner);
  }

  private void clearParticipantsListAndEconomy(Player winner) {
    Manager.getCreated().getParticipants().clear();
    VaultHook.eco.depositPlayer(winner, Config.get().getPremio());
    manager.setLastWinner(winner.getName());
  }

  private void handleClanFriendlyFire(Player winner) {
    if (WarriorEngine.getInstance().isSimpleClans()) {
      handleSimpleClans(winner);
    }
  }

  private void handleSimpleClans(Player winner) {
    if (WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(winner) != null) {
      WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(winner).setFriendlyFire(false);
    }
  }
}
