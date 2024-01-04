package me.zbrunooow.bgguerreiro.listeners;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UltimateChatListener implements Listener {

    @EventHandler
    public void onUChat(SendChannelMessageEvent e) {
        if(!(e.getSender() instanceof Player)) return;

        Player p = (Player) e.getSender();

        if(!Evento.get().getStartado().equalsIgnoreCase("off")) {
            if(CamaroteManager.get().getSpectators().contains(p)) {
                e.setCancelled(true);
                return;
            }

            if(Manager.get().getParticipantes().contains(p)) {
                if(!Config.get().isChat()) {
                    if(!p.isOp() && !p.hasPermission("bgguerreiro.admin")) {
                        e.setCancelled(true);
                        p.sendMessage("§cO chat está bloqueado durante o evento Guerreiro.");
                    }
                }
            }
        }

        if(Manager.get().getLastWinner().equalsIgnoreCase(p.getName())) {
            e.addTag("{bgguerreiro}", Config.get().getTag() + " ");
        }
    }

}
