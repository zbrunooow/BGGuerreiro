package me.zbrunooow.bgguerreiro.listener.server;

import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandExecutionListener implements Listener {

  @EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
  public void onCommand(PlayerCommandPreprocessEvent e) {
    if (!e.getPlayer().hasPermission("bgguerreiro.admin")) {
      if (EventManager.getCreated().getStatus() != EventStatus.OFF) {
        if (BoxManager.get().getSpectators().contains(e.getPlayer())) {
          if (!(e.getMessage().startsWith("/guerreiro") || e.getMessage().startsWith("/bgguerreiro") || e.getMessage().startsWith("/warrior"))) {
            e.setCancelled(true);
            return;
          }
        }

        if (Manager.getCreated().getParticipants().contains(e.getPlayer())) {
          if (!Config.get().isComandos()) {
            if (!(e.getMessage().startsWith("/guerreiro") || e.getMessage().startsWith("/bgguerreiro") || e.getMessage().startsWith("/warrior"))) {
              e.getPlayer()
                  .sendMessage("§cOs comandos estão bloqueados durante o evento Guerreiro.");
              e.setCancelled(true);
              return;
            }
          }
        }
      }
    }
  }
}
