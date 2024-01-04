package me.zbrunooow.bgguerreiro.listener.server;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.API;
import me.zbrunooow.bgguerreiro.util.Manager;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

  private static void handleLeavePlaying(PlayerQuitEvent event) {
    EventStatus eventStatus = EventManager.getCreated().getStatus();
    Manager manager = Manager.getCreated();
    Player player = event.getPlayer();
    boolean isParticipant = manager.getParticipants().contains(player);

    if (isParticipant) {
      String unparsedWarriorKills =
              String.valueOf(player.getMetadata("warriorKills").get(0).value());
      int kills = Integer.parseInt(unparsedWarriorKills);

      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      player.getActivePotionEffects().clear();
      player.removeMetadata("warriorKills", WarriorEngine.getInstance());

      if (WarriorEngine.getInstance().isSimpleClans()) {
        ClanPlayer clanPlayer =
                WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(player);
        if (clanPlayer != null) {
          clanPlayer.setFriendlyFire(false);
        }
      }

      manager.getParticipants().remove(player);
      API.getCreated().salvarStatus(player, false, kills);

      if (eventStatus == EventStatus.STARTED) {
        EventManager.getCreated().verifyLastDuel();
      }
    }

    if (eventStatus == EventStatus.STARTED) {
      if (BoxManager.get().getSpectators().contains(event.getPlayer())) {
        BoxManager.get().leaveCamarote(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void whenConnect(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (!player.hasPermission("bgguerreiro.admin")) return;
    this.notifyUpdates(player);

    if (EventManager.getCreated().getStatus() == EventStatus.STARTED) {
      if (BoxManager.get().getSpectators().isEmpty()) return;

      for (Player spec : BoxManager.get().getSpectators()) {
        player.hidePlayer(spec);
      }
    }
  }

  @EventHandler
  public void whenDisconnect(PlayerQuitEvent event) {
      handleLeavePlaying(event);
  }

  void notifyUpdates(Player player) {
    if (isUpdated()) {
      player.sendMessage(
          WarriorEngine.getInstance().getPrefix()
              + "§cAtualização disponível! Versão: "
              + WarriorEngine.getInstance().getUpdateVersion()
              + " já existente!");
      player.sendMessage(WarriorEngine.getInstance().getPrefix() + "§cPara pegar a nova versão:");
      player.sendMessage(
          WarriorEngine.getInstance().getPrefix()
              + "§cAcesse a GB: https://gamersboard.com.br/profile/41269-zbrunooow/");
    } else {
      player.sendMessage(
          WarriorEngine.getInstance().getPrefix()
              + "§eNenhuma atualização disponível, versão mais recente já instalada! §a("
              + WarriorEngine.getInstance().getUpdateVersion()
              + ")");
    }
  }

  boolean isUpdated() {
    return !(WarriorEngine.getInstance().isUpdate());
  }
}
