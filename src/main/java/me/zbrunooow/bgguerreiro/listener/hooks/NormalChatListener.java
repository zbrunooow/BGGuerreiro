package me.zbrunooow.bgguerreiro.listener.hooks;

import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NormalChatListener implements Listener {

  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    if (EventManager.getCreated().getStatus() != EventStatus.OFF) {
      if (BoxManager.get().getSpectators().contains(e.getPlayer())) {
        e.setCancelled(true);
        return;
      }

      if (Manager.getCreated().getParticipants().contains(e.getPlayer())) {
        if (!Config.get().isChat()) {
          if (!e.getPlayer().isOp() && !e.getPlayer().hasPermission("bgguerreiro.admin")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cO chat está bloqueado durante o evento Guerreiro.");
          }
        }
      }
    }
  }
}
