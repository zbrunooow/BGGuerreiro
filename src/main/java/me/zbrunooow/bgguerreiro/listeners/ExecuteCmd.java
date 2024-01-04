package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ExecuteCmd implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(!e.getPlayer().isOp() && !e.getPlayer().hasPermission("bgguerreiro.admin")) {
            if(!Evento.get().getStartado().equalsIgnoreCase("off")) {
                if(CamaroteManager.get().getSpectators().contains(e.getPlayer())) {
                    if(!e.getMessage().startsWith("/guerreiro")) {
                        e.setCancelled(true);
                    }
                    return;
                }

                if(Manager.get().getParticipantes().contains(e.getPlayer())) {
                    if(!Config.get().isComandos()) {
                        if(!e.getMessage().startsWith("/guerreiro")) {
                            e.getPlayer().sendMessage("§cOs comandos estão bloqueados durante o evento Guerreiro.");
                        }
                    }
                }
            }
        }
        return;
    }
}