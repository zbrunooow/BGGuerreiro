package me.zbrunooow.bgguerreiro.listener.player;

import me.zbrunooow.bgguerreiro.manager.BoxManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class TotalInteractionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().toString().startsWith("RIGHT")) {
            if(BoxManager.get().getSpectators().contains(e.getPlayer())) {
                if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("SAIR")) {
                    BoxManager.get().leaveCamarote(e.getPlayer());
                }
            }
        }
    }

}
