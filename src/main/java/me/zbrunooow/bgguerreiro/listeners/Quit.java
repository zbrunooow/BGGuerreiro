package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.API;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        if(Manager.get().getParticipantes().contains(e.getPlayer())) {
            Player p = e.getPlayer();
            int kills = Integer.parseInt(String.valueOf(p.getMetadata("warriorKills").get(0).value()));
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getActivePotionEffects().clear();
            p.removeMetadata("warriorKills", Core.getInstance());
            if(Core.getInstance().sc) {
                if (Core.getSC().getClanManager().getClanPlayer(p) != null) {
                    Core.getSC().getClanManager().getClanPlayer(p).setFriendlyFire(false);
                }
            }
            Manager.get().getParticipantes().remove(p);
            API.get().salvarStatus(p, false, kills);

            if(Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
                Evento.get().verificarFinal();
            }
        }

        if(Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
            if (CamaroteManager.get().getSpectators().contains(e.getPlayer())) {
                CamaroteManager.get().leaveCamarote(e.getPlayer());
            }
        }

    }

}
