package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NormalChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!Evento.get().getStartado().equalsIgnoreCase("off")) {
            if(CamaroteManager.get().getSpectators().contains(e.getPlayer())) {
                e.setCancelled(true);
                return;
            }

            if(Manager.get().getParticipantes().contains(e.getPlayer())) {
                if(!Config.get().isChat()) {
                    if(!e.getPlayer().isOp() && !e.getPlayer().hasPermission("bgguerreiro.admin")) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cO chat está bloqueado durante o evento Guerreiro.");
                    }
                }
            }
        }
    }

}
