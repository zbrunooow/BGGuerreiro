package me.zbrunooow.bgguerreiro.listener.hooks;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UltimateChatListener implements Listener {

    @EventHandler
    public void onUChat(SendChannelMessageEvent e) {
        if(!(e.getSender() instanceof Player)) return;

        Player p = (Player) e.getSender();

        if((EventManager.getCreated().getStatus() != EventStatus.OFF)) {
            if(BoxManager.get().getSpectators().contains(p)) {
                e.setCancelled(true);
                return;
            }

            if(Manager.getCreated().getParticipants().contains(p)) {
                if(!Config.get().isChat()) {
                    if(!p.isOp() && !p.hasPermission("bgguerreiro.admin")) {
                        e.setCancelled(true);
                        p.sendMessage("§cO chat está bloqueado durante o evento Guerreiro.");
                    }
                }
            }
        }

        if(Manager.getCreated().getLastWinner().equalsIgnoreCase(p.getName())) {
            e.addTag("{bgguerreiro}", Config.get().getTag() + " ");
        }
    }

}
