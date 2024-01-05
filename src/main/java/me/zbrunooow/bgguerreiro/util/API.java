package me.zbrunooow.bgguerreiro.util;

import com.cryptomorin.xseries.messages.ActionBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class API {

  public static API getCreated() {
    return WarriorEngine.getInstance().getApi();
  }

  public List<String> fetchRanking(String type) {
    List<String> topVitorias = new ArrayList<>();

    try {
      MySQL.get().openConnection();
      Connection conn = MySQL.get().getConnection();

      String query =
          "SELECT nick, vitorias FROM guerreiro_jogadores ORDER BY vitorias DESC LIMIT "
              + Config.get().getTopLimite();
      if (type.equalsIgnoreCase("kills")) {
        query =
            "SELECT nick, kills FROM guerreiro_jogadores ORDER BY kills DESC LIMIT "
                + Config.get().getTopLimite();
      }

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);

      while (rs.next()) {
        String nick = rs.getString("nick");
        int valor = 0;
        if (type.equalsIgnoreCase("kills")) {
          valor = rs.getInt("kills");
        } else {
          valor = rs.getInt("vitorias");
        }
        topVitorias.add(nick + "()" + valor);
      }

      rs.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return topVitorias;
  }

  public void broadcastActionbarToParticipants(String message) {
    for (Player player : Manager.getCreated().getParticipants()) {

      ActionBar.sendActionBar(
          player,
          message
              .replace("&", "§")
              .replace(
                  "{abates}", String.valueOf(player.getMetadata("warriorKills").get(0).value())));
    }
  }

  public void sendActionBarMessage(Player player, String message) {
    ActionBar.sendActionBar(player, message.replace("&", "§"));
  }

  public void broadcastMessage(String msg) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.sendMessage(msg);
    }
  }

  public String formatTime(int segundos) {
    long HH = segundos / 3600;
    long MM = (segundos % 3600) / 60;
    long SS = segundos % 60;
    String data = " ";
    if (HH > 0) data += " " + HH + "h";
    if (MM > 0) data += " " + MM + "m";
    if (SS > 0) data += " " + SS + "s";
    while (data.startsWith(" ")) {
      data = data.replaceFirst(" ", new String());
    }
    return data.length() > 0 ? data : "0s";
  }

  public void broadcastMessageToParticipants(String msg) {
    for (Player p : Manager.getCreated().getParticipants()) {
      p.sendMessage(msg);
    }
  }

  public static String formatPrize(Double number) {

    String nb = String.valueOf(number);
    if(nb.contains("E")) {
      int i = Integer.parseInt(nb.split("E")[1]);
      int i2 = Integer.parseInt(nb.split("E")[1]);
      while(i > 0) {
        nb = nb + "0";
        i--;
      }
      nb = nb.replace(".0E" + i2, "");
    }
    if(nb.endsWith(".0")) { nb = nb.replace(".0", ""); }
    int nbL = nb.length();
    nb = nb.substring(0, nbL%3 == 0 ? 3 : nbL%3);

    if(nbL >= 65){return nb + "UVG";}
    if(nbL >= 62){return nb + "VG";}
    if(nbL >= 59){return nb + "ND";}
    if(nbL >= 56){return nb + "OD";}
    if(nbL >= 53){return nb + "SPD";}
    if(nbL >= 51){return nb + "SD";}
    if(nbL >= 49){return nb + "QD";}
    if(nbL >= 46){return nb + "QR";}
    if(nbL >= 43){return nb + "TR";}
    if(nbL >= 40){return nb + "DD";}
    if(nbL >= 37){return nb + "UN";}
    if(nbL >= 34){return nb + "D";}
    if(nbL >= 31){return nb + "N";}
    if(nbL >= 28){return nb + "OC";}
    if(nbL >= 25){return nb + "SS";}
    if(nbL >= 22){return nb + "S";}
    if(nbL >= 19){return nb + "QQ";}
    if(nbL >= 16){return nb + "Q";}
    if(nbL >= 13){return nb + "T";}
    if(nbL >= 10){return nb + "B";}
    if(nbL >= 7){return nb + "M";}
    if(nbL >= 4){return nb + "K";}
    return String.valueOf(number);

  }

  public int getFreeSlots(Player player) {
    int freeslots = 0;
    for (ItemStack it : player.getInventory().getContents()) {
      if (it == null || it.getType() == Material.AIR) {
        freeslots++;
      }
    }

    return freeslots;
  }

  public int getArmor(Player player) {
    int freeslots = 0;
    if (player.getInventory().getHelmet() != null) freeslots++;
    if (player.getInventory().getChestplate() != null) freeslots++;
    if (player.getInventory().getLeggings() != null) freeslots++;
    if (player.getInventory().getBoots() != null) freeslots++;

    return freeslots;
  }

  public void removeLastWinner() {
    try {
      MySQL.get().openConnection();
      Connection conn = MySQL.get().getConnection();

      conn.prepareStatement("UPDATE guerreiro_jogadores SET lw = 0 WHERE lw = 1").execute();
      conn.close();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public String getLW() {
    String nick = "";

    try {
      MySQL.get().openConnection();
      Connection conn = MySQL.get().getConnection();

      PreparedStatement ps;

      ps = conn.prepareStatement("SELECT * FROM guerreiro_jogadores WHERE lw = 1");
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        nick = rs.getString(1);
      }

      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return nick;
  }

  public ItemStack[] unserializeItems(String data) {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      BukkitObjectInputStream dataInput = null;
      try {
        dataInput = new BukkitObjectInputStream(inputStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
      ItemStack[] items = null;
      try {
        items = new ItemStack[dataInput.readInt()];
      } catch (IOException e) {
        e.printStackTrace();
      }

      for (int i = 0; i < items.length; i++) {
        try {
          items[i] = (ItemStack) dataInput.readObject();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      try {
        dataInput.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      return items.clone();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String serializeItems(ItemStack[] items) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

      // Write the size of the inventory
      dataOutput.writeInt(items.clone().length);

      // Save every element in the list
      for (int i = 0; i < items.length; i++) {
        dataOutput.writeObject(items[i]);
      }

      // Serialize that array
      dataOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (Exception e) {
      return null;
    }
  }



  public void salvarStatus(Player player, boolean venceu, int kills) {
    try {
      MySQL.get().openConnection();
      Connection conn = MySQL.get().getConnection();

      PreparedStatement ps;

      ps =
          conn.prepareStatement(
              "SELECT * FROM guerreiro_jogadores WHERE nick = '" + player.getName() + "'");
      ResultSet rs = ps.executeQuery();

      if (rs == null || !rs.next()) {
        conn.prepareStatement(
                "INSERT INTO guerreiro_jogadores VALUES ('"
                    + player.getName()
                    + "', "
                    + kills
                    + ", "
                    + (venceu ? 1 : 0)
                    + ", "
                    + (venceu ? 1 : 0)
                    + ")")
            .execute();
      } else {
        conn.prepareStatement(
                "UPDATE guerreiro_jogadores SET kills = kills + "
                    + kills
                    + " WHERE nick = '"
                    + player.getName()
                    + "'")
            .execute();
        if (venceu) {
          conn.prepareStatement(
                  "UPDATE guerreiro_jogadores SET vitorias = vitorias + 1 WHERE nick = '"
                      + player.getName()
                      + "'")
              .execute();
          conn.prepareStatement(
                  "UPDATE guerreiro_jogadores SET lw = 1 WHERE nick = '" + player.getName() + "'")
              .execute();
        }
      }

      conn.close();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public String unserializeLocation(Location loc) {
    return loc.getWorld().getName()
        + "()"
        + loc.getX()
        + "()"
        + loc.getY()
        + "()"
        + loc.getZ()
        + "()"
        + loc.getYaw()
        + "()"
        + loc.getPitch();
  }

  public void equipPlayer(Player p) {
    p.getInventory().setArmorContents(API.getCreated().unserializeItems(Manager.getCreated().getArmor()));
    p.getInventory().setContents(API.getCreated().unserializeItems(Manager.getCreated().getItems()));
  }


  public Location serializeLocation(String str) {
    World world = WarriorEngine.getInstance().getServer().getWorld(str.split("\\(\\)")[0]);
    double x = Double.parseDouble(str.split("\\(\\)")[1]);
    double y = Double.parseDouble(str.split("\\(\\)")[2]);
    double z = Double.parseDouble(str.split("\\(\\)")[3]);
    float yaw = Float.parseFloat(str.split("\\(\\)")[4]);
    float pitch = Float.parseFloat(str.split("\\(\\)")[5]);

    return new Location(world, x, y, z, yaw, pitch);
  }
}
