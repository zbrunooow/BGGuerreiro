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

  public String items;
  public String armor;

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

    if(Kit.get().getKitFile().getString("armor") != null && Kit.get().getKitFile().getString("armor").length() > 5) {
      armor = Kit.get().getKitFile().getString("armor");
    }
    if(Kit.get().getKitFile().getString("items") != null && Kit.get().getKitFile().getString("items").length() > 5) {
      items = Kit.get().getKitFile().getString("items");
    }

  }

  public static Manager getCreated() {
    return WarriorEngine.getInstance().getManager();
  }

}
