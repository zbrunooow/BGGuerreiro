package me.zbrunooow.bgguerreiro.manager;

import lombok.Getter;
import lombok.Setter;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.hooks.VaultHook;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.API;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class EventManager {

  private final Manager manager = Manager.getCreated();
  private int pvpDeathMatch = 0;
  private EventStatus status = EventStatus.OFF;
  private boolean pvp = false;
  private boolean deathMatch = false;
  private boolean forcedDeathmatch = false;

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
        for (String str : WarriorEngine.getMessages().getStarting()) {
          API.getCreated().broadcastMessage(formatAnnouncementMessage(str));
        }
      }

      private String formatAnnouncementMessage(String message) {
        return message
            .replace("{seconds}", String.valueOf(seconds))
            .replace("{tag}", Config.get().getTag())
            .replace("{valor}", String.valueOf(Config.get().getPremio()))
            .replace("{jogadores}", String.valueOf(Manager.getCreated().getParticipants().size()));
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
        for (String str : WarriorEngine.getMessages().getWaiting()) {
          API.getCreated()
              .broadcastMessageToParticipants(
                  str.replace("{tempo}", String.valueOf(Config.get().getTempoEspera())));
        }
      }

      private void handleWaiting() {
        if (Config.get().isDmHabilitado() || forcedDeathmatch) {
          handleDeathMatch();
        }

        if (waitTime > 0) {
          if (waitTime == next) {
            for (String str : WarriorEngine.getMessages().getWaiting()) {
              API.getCreated()
                  .broadcastMessageToParticipants(str.replace("{tempo}", String.valueOf(waitTime)));
            }
            next = waitTime - 5;
          }
        } else {
          setStatus(EventStatus.STARTED);
          setPvp(true);
          for (String str : WarriorEngine.getMessages().getStarted()) {
            API.getCreated().broadcastMessageToParticipants(str);
          }
        }
        waitTime--;
      }

      private void handleDeathMatch() {
        if (Config.get().getDmTempo() != manager.getEventTime()) return;
        setPvp(false);

        int newValue = Manager.getCreated().getEventTime() + Config.get().getDmSemPvP();
        setPvpDeathMatch(newValue);

        for (String str : WarriorEngine.getMessages().getDeathmatchStart()) {
          API.getCreated()
              .broadcastMessageToParticipants(
                  str.replace("{segundos}", String.valueOf(Config.get().getDmSemPvP())));
        }

        for (Player alive : manager.getParticipants()) {
          alive.teleport(manager.getDeathmatchLocation());
        }

        for (Player spectators : BoxManager.get().getSpectators()) {
          spectators.teleport(manager.getDeathmatchLocation());
        }

        setDeathMatch(true);
      }

      private void handleStarted() {
        Manager.getCreated().setEventTime(Manager.getCreated().getEventTime() + 1);
        broadcastActionbar();
      }

      private void broadcastActionbar() {
        String actionbarMessage =
            Manager.getCreated().getEventTime() <= 10
                ? WarriorEngine.getMessages().getStatusActionbar()
                : WarriorEngine.getMessages().getInformationActionbar();

        API.getCreated().broadcastActionbarToParticipants(formatActionbarMessage(actionbarMessage));
      }

      private String formatActionbarMessage(String message) {
        return message
            .replace("{players}", String.valueOf(Manager.getCreated().getParticipants().size()))
            .replace("{tempo}", API.getCreated().formatTime(Manager.getCreated().getEventTime()))
            .replace("{abates}", "0")
            .replace(
                "{status}",
                (!(getStatus() == EventStatus.STARTED) ? "&cIniciando..." : "&aValendo"));
      }
    }.runTaskTimerAsynchronously(WarriorEngine.getInstance(), 0L, 20L);
  }

  public void forceDm() {
    forcedDeathmatch = true;
    setPvp(false);
    pvpDeathMatch = Manager.getCreated().getEventTime() + Config.get().getDmSemPvP();
    broadcastDeathmatchStartMessages();
    teleportParticipantsToDeathmatchLocation();
    setDeathMatch(true);
  }

  private void broadcastDeathmatchStartMessages() {
    for (String str : WarriorEngine.getMessages().getDeathmatchStart()) {
      API.getCreated()
          .broadcastMessageToParticipants(
              str.replace("{segundos}", String.valueOf(Config.get().getDmSemPvP())));
    }
  }

  private void teleportParticipantsToDeathmatchLocation() {
    for (Player vivos : Manager.getCreated().getParticipants()) {
      vivos.teleport(Manager.getCreated().getDeathmatchLocation());
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
  }

  private void broadcastCancellationMessages() {
    for (String str : WarriorEngine.getMessages().getCancelled()) {
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
            WarriorEngine.getMessages()
                .getSurvivorsLeft()
                .replace("{restam}", String.valueOf(participantsCount)));
  }

  private void handleWinner(Manager manager) {
    Player vencedor = manager.getParticipants().get(0);
    int kills = getWarriorKillsMetadata(vencedor);
    broadcastWinnerMessages(vencedor, kills);
    clearEffectsAndMetadata(vencedor);
    savePlayerStatus(vencedor, true, kills);
    handleParticipantsAndEconomy(vencedor);
  }

  private int getWarriorKillsMetadata(Player player) {
    return Integer.parseInt(String.valueOf(player.getMetadata("warriorKills").get(0).value()));
  }

  private void broadcastWinnerMessages(Player winner, int kills) {
    for (String str : WarriorEngine.getMessages().getExpired()) {
      API.getCreated().broadcastMessage(formatWinnerMessage(str, winner, kills));
    }
  }

  private String formatWinnerMessage(String message, Player winner, int kills) {
    return message
        .replace("{abates}", String.valueOf(kills))
        .replace("{duração}", API.getCreated().formatTime(Manager.getCreated().getEventTime()))
        .replace("{tag}", Config.get().getTag())
        .replace("{valor}", String.valueOf(Config.get().getPremio()))
        .replace("{vencedor}", winner.getName());
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
    winner.teleport(Manager.getCreated().getExitLocation());
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
