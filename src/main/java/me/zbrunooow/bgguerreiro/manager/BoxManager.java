package me.zbrunooow.bgguerreiro.manager;

import java.util.ArrayList;

import lombok.Getter;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Item;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter
public class BoxManager {

  private final ArrayList<Player> spectators;
  private final Item item = new Item(Material.REDSTONE);

  public BoxManager() {
    this.spectators = new ArrayList<>();
    item.setDisplayName("§c§lSAIR");
    item.addEnchantment(34, 1);
  }

  public static BoxManager get() {
    return WarriorEngine.getInstance().getBoxManager();
  }

  public void joinCamarote(Player player) {
    EventStatus eventStatus = EventManager.getCreated().getStatus();
    if (eventStatus != EventStatus.STARTED) return;

    player.getPlayer().teleport(Manager.getCreated().getJoinLocation());
    player.getPlayer().getInventory().setItem(4, item.build());

    for (Player online : Bukkit.getOnlinePlayers()) {
      online.hidePlayer(player);
    }

    if (EventManager.getCreated().isDeathMatch()) {
      player.getPlayer().teleport(Manager.getCreated().getDeathmatchLocation());
    }

    player.setAllowFlight(true);
    player.sendMessage(WarriorEngine.getMessages().getBoxJoin());
    spectators.add(player);
  }

  public void leaveCamarote(Player player) {
    player.getPlayer().teleport(Manager.getCreated().getExitLocation());
    player.getPlayer().getInventory().clear();

    for (Player online : Bukkit.getOnlinePlayers()) {
      online.showPlayer(player);
    }

    player.setAllowFlight(false);
    player.sendMessage(WarriorEngine.getMessages().getBoxLeave());
    spectators.remove(player);
  }

}
