package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().toString().startsWith("RIGHT")) {
            if(CamaroteManager.get().getSpectators().contains(e.getPlayer())) {
                if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("SAIR")) {
                    CamaroteManager.get().leaveCamarote(e.getPlayer());
                }
            }
        }
    }

}
