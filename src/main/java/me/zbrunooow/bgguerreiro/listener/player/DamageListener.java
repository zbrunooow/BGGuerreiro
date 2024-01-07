package me.zbrunooow.bgguerreiro.listener.player;

import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) e.getEntity();
    if (BoxManager.get().getSpectators().contains(victim)) {
      e.setCancelled(true);
      return;
    }
    if (!(e.getDamager() instanceof Player)) {
      return;
    }
    Player attacker = (Player) e.getDamager();
    if (BoxManager.get().getSpectators().contains(attacker)) {
      e.setCancelled(true);
      return;
    }

    if (Manager.getCreated().getParticipants().contains(victim)) {
      if (!EventManager.getCreated().isPvp()) {
        e.setCancelled(true);
        attacker.sendMessage("§cO PvP está desativado no momento!");
      } else {
      }
    }
  }

  @EventHandler
  public void onDamageAlone(EntityDamageEvent e) {
    if (EventManager.getCreated().getStatus() != EventStatus.OFF) {
      if (e.getEntity() instanceof Player) {
        Player victim = (Player) e.getEntity();
        if(victim.hasMetadata("waitingLeave")) {
          e.setCancelled(true);
          return;
        }

        if (BoxManager.get().getSpectators().contains(victim)) {
          e.setCancelled(true);
          return;
        }

        if (Manager.getCreated().getParticipants().contains(victim)) {
          if (EventManager.getCreated().getStatus() != EventStatus.STARTED && EventManager.getCreated().getStatus() != EventStatus.DEATHMATCH) {
            e.setCancelled(true);
          }
        }
      }
    }
  }
}
