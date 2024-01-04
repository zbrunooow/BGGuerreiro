package me.zbrunooow.bgguerreiro.listeners;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LegendChatTagEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(ChatMessageEvent e) {
        if(!Evento.get().getStartado().equalsIgnoreCase("off")) {
            if(CamaroteManager.get().getSpectators().contains(e.getSender())) {
                e.setCancelled(true);
                return;
            }

            if(Manager.get().getParticipantes().contains(e.getSender())) {
                if(!Config.get().isChat()) {
                    if(!e.getSender().isOp() && !e.getSender().hasPermission("bgguerreiro.admin")) {
                        e.setCancelled(true);
                        e.getSender().sendMessage("§cO chat está bloqueado durante o evento Guerreiro.");
                    }
                }
            }
        }

        if (e.getTags().contains("bgguerreiro") && e.getSender().getName().equals(Manager.get().getLastWinner())) {
            e.setTagValue("bgguerreiro", Config.get().getTag() + " ");
        }
    }
}
