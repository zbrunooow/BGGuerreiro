package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) e.getEntity();
        if(CamaroteManager.get().getSpectators().contains(victim)) {
            e.setCancelled(true);
            return;
        }
        if(!(e.getDamager() instanceof Player)) {
            return;
        }
        Player attacker = (Player) e.getDamager();
        if(CamaroteManager.get().getSpectators().contains(attacker)) {
            e.setCancelled(true);
            return;
        }

        if (Manager.get().getParticipantes().contains(victim)) {
            if (!Evento.get().isPvp()) {
                e.setCancelled(true);
                attacker.sendMessage("§cO PvP está desativado no momento!");
            }
        }
    }

    @EventHandler
    public void onDamageAlone(EntityDamageEvent e) {
        if(!Evento.get().getStartado().equalsIgnoreCase("off")) {
            if(e.getEntity() instanceof Player) {
                Player victim = (Player) e.getEntity();
                if(CamaroteManager.get().getSpectators().contains(victim)) {
                    e.setCancelled(true);
                    return;
                }

                if (Manager.get().getParticipantes().contains(victim)) {
                    if(!Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}
