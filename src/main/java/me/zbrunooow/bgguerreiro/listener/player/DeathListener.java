package me.zbrunooow.bgguerreiro.listener.player;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.API;
import me.zbrunooow.bgguerreiro.util.Config;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class DeathListener implements Listener {

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    if (EventManager.getCreated().getStatus() == EventStatus.STARTED) {
      if (Manager.getCreated().getParticipants().contains(e.getEntity().getKiller())) {
        Player killer = e.getEntity().getKiller();
        Player victim = e.getEntity().getPlayer();

        int kills =
            Integer.parseInt(String.valueOf(killer.getMetadata("warriorKills").get(0).value()));
        kills++;
        killer.setMetadata(
            "warriorKills", new FixedMetadataValue(WarriorEngine.getInstance(), kills));

        API.getCreated()
            .salvarStatus(
                victim,
                false,
                Integer.parseInt(
                    String.valueOf(victim.getMetadata("warriorKills").get(0).value())));
        Manager.getCreated().getParticipants().remove(victim);
        victim.removeMetadata("warriorKills", WarriorEngine.getInstance());

        if (WarriorEngine.getInstance().isSimpleClans()) {
          if (WarriorEngine.getSimpleClans().getClanManager().getClanPlayer(victim) != null) {
            WarriorEngine.getSimpleClans()
                .getClanManager()
                .getClanPlayer(victim)
                .setFriendlyFire(false);
          }
        }

        if (Config.get().isAbateGeral()) {
          e.setDeathMessage(
              WarriorEngine.getMessages()
                  .getKill()
                  .replace("{matou}", killer.getName())
                  .replace("{morreu}", victim.getName())
                  .replace("{kills}", String.valueOf(kills)));
        } else {
          API.getCreated()
              .broadcastMessageToParticipants(
                  WarriorEngine.getMessages()
                      .getKill()
                      .replace("{matou}", killer.getName())
                      .replace("{morreu}", victim.getName())
                      .replace("{kills}", String.valueOf(kills)));
        }

        EventManager.getCreated().verifyLastDuel();
      }
    }
  }
}
