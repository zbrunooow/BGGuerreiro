package me.zbrunooow.bgguerreiro.listener.hooks;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LegendChatTagListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  private void onChat(ChatMessageEvent e) {
    if (EventManager.getCreated().getStatus() != EventStatus.OFF) {
      if (BoxManager.get().getSpectators().contains(e.getSender())) {
        e.setCancelled(true);
        return;
      }

      if (Manager.getCreated().getParticipants().contains(e.getSender())) {
        if (!Config.get().isChat()) {
          if (!e.getSender().isOp() && !e.getSender().hasPermission("bgguerreiro.admin")) {
            e.setCancelled(true);
            e.getSender().sendMessage("§cO chat está bloqueado durante o evento Guerreiro.");
          }
        }
      }
    }

    if (e.getTags().contains("bgguerreiro")
        && e.getSender().getName().equals(Manager.getCreated().getLastWinner())) {
      e.setTagValue("bgguerreiro", Config.get().getTag() + " ");
    }
  }
}
