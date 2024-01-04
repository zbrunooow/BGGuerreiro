package me.zbrunooow.bgguerreiro.listener.player;

import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemInteractionListener implements Listener {

  @EventHandler
  public void whenTryDrop(PlayerDropItemEvent e) {
    if (!(EventManager.getCreated().getStatus() == EventStatus.OFF)) {
      if (BoxManager.get().getSpectators().contains(e.getPlayer())) {
        e.setCancelled(true);
        return;
      }

      if (Manager.getCreated().getParticipants().contains(e.getPlayer())) {
        if (!e.getPlayer().hasPermission("bgguerreiro.admin")) {
          if (!Config.get().isDrop()) {
            e.setCancelled(true);
            e.getPlayer()
                .sendMessage("§cO drop de itens está bloqueado durante o evento Guerreiro.");
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    if (BoxManager.get().getSpectators().contains(player)) {
      event.setCancelled(true);
    }
  }
}
