package me.zbrunooow.bgguerreiro.util;

import java.util.ArrayList;
import lombok.Data;
import lombok.Getter;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter
@Data
public class Manager {

  public ArrayList<Player> participants;

  public Location joinLocation;
  public Location exitLocation;
  public Location deathmatchLocation;
  public String lastWinner;
  public int eventTime;

  public Item helmet = new Item(Material.IRON_HELMET);
  public Item chestplate = new Item(Material.IRON_CHESTPLATE);
  public Item leggins = new Item(Material.IRON_LEGGINGS);
  public Item boots = new Item(Material.IRON_BOOTS);
  public Item sword = new Item(Material.IRON_SWORD);
  public Item gapple = new Item(Material.GOLDEN_APPLE);
  public Item bow = new Item(Material.BOW);
  public Item arrow = new Item(Material.ARROW);

  public Manager() {
    participants = new ArrayList<>();

    joinLocation = null;
    exitLocation = null;
    deathmatchLocation = null;

    if (Locations.get().getLocs().getString("entrada") != null
        && Locations.get().getLocs().getString("entrada").length() > 5) {
      joinLocation = API.getCreated().serializeLocation(Locations.get().getLocs().getString("entrada"));
    }
    if (Locations.get().getLocs().getString("saida") != null
        && Locations.get().getLocs().getString("saida").length() > 5) {
      exitLocation = API.getCreated().serializeLocation(Locations.get().getLocs().getString("saida"));
    }
    if (Locations.get().getLocs().getString("deathmatch") != null
        && Locations.get().getLocs().getString("deathmatch").length() > 5) {
      deathmatchLocation =
          API.getCreated().serializeLocation(Locations.get().getLocs().getString("deathmatch"));
    }

    helmet.setDisplayName("§a§lGUERREIRO");
    chestplate.setDisplayName("§a§lGUERREIRO");
    leggins.setDisplayName("§a§lGUERREIRO");
    boots.setDisplayName("§a§lGUERREIRO");
    sword.setDisplayName("§a§lGUERREIRO");
    gapple.setDisplayName("§a§lGUERREIRO");
    gapple.setAmount(2);
    bow.setDisplayName("§a§lGUERREIRO");
    arrow.setDisplayName("§a§lGUERREIRO");
    arrow.setAmount(10);
  }

  public static Manager getCreated() {
    return WarriorEngine.getInstance().getManager();
  }

}
