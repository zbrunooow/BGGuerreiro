package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.API;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import me.zbrunooow.bgguerreiro.utils.Mensagens;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Death implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
            if(Manager.get().getParticipantes().contains(e.getEntity().getKiller())) {
                Player killer = e.getEntity().getKiller();
                Player victim = e.getEntity().getPlayer();

                int kills = Integer.parseInt(String.valueOf(killer.getMetadata("warriorKills").get(0).value()));
                kills++;
                killer.setMetadata("warriorKills", new FixedMetadataValue(Core.getInstance(), kills));

                API.get().salvarStatus(victim, false, Integer.parseInt(String.valueOf(victim.getMetadata("warriorKills").get(0).value())));
                Manager.get().getParticipantes().remove(victim);
                victim.removeMetadata("warriorKills", Core.getInstance());

                if(Core.getInstance().sc) {
                    if (Core.getSC().getClanManager().getClanPlayer(victim) != null) {
                        Core.getSC().getClanManager().getClanPlayer(victim).setFriendlyFire(false);
                    }
                }

                if(Config.get().isAbateGeral()) {
                    e.setDeathMessage(Mensagens.get().getAbate().replace("{matou}", killer.getName()).replace("{morreu}", victim.getName()).replace("{kills}", String.valueOf(kills)));
                } else {
                    API.get().broadcastParticipantes(Mensagens.get().getAbate().replace("{matou}", killer.getName()).replace("{morreu}", victim.getName()).replace("{kills}", String.valueOf(kills)));
                }

                Evento.get().verificarFinal();
            }
        }
    }

}
