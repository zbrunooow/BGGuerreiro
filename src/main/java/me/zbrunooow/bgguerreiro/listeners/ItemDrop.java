package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDrop implements Listener {

    @EventHandler
    public void aoDropar(PlayerDropItemEvent e) {
        if (!Evento.get().getStartado().equalsIgnoreCase("off")) {
            if(CamaroteManager.get().getSpectators().contains(e.getPlayer())) {
                e.setCancelled(true);
                return;
            }

            if (Manager.get().getParticipantes().contains(e.getPlayer())) {
                if(!e.getPlayer().isOp() && !e.getPlayer().hasPermission("bgguerreiro.admin")) {
                    if (!Config.get().isDrop()) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage("§cO drop de itens está bloqueado durante o evento Guerreiro.");
                    }
                }
            }
        }
    }

}
