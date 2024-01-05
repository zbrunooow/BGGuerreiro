package me.zbrunooow.bgguerreiro.manager;

import java.util.ArrayList;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import me.zbrunooow.bgguerreiro.util.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class BoxManager {

  private final ArrayList<Player> spectators;
  private final ItemStack leaveBox = new ItemStack(XMaterial.RED_DYE.parseItem());

  public BoxManager() {
    this.spectators = new ArrayList<>();
    ItemMeta im = leaveBox.getItemMeta();
    im.setDisplayName("§c§lSAIR");
    im.addEnchant(XEnchantment.DURABILITY.getEnchant(), 10, true);
    leaveBox.setItemMeta(im);
  }

  public static BoxManager get() {
    return WarriorEngine.getInstance().getBoxManager();
  }

  public void joinBox(Player player) {
    EventStatus eventStatus = EventManager.getCreated().getStatus();
    if (eventStatus != EventStatus.STARTED) return;

    player.getPlayer().teleport(Manager.getCreated().getJoinLocation());
    player.getPlayer().getInventory().setItem(4, leaveBox.clone());

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

  public void leaveBox(Player player) {
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
