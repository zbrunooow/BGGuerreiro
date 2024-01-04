package me.zbrunooow.bgguerreiro.utils;

import com.cryptomorin.xseries.messages.ActionBar;
import me.zbrunooow.bgguerreiro.Core;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class API {

    public List<String> listaTop(String type) {
        List<String> topVitorias = new ArrayList<>();

        try {
            MySQL.get().openConnection();
            Connection conn = MySQL.get().getConnection();

            String query = "SELECT nick, vitorias FROM guerreiro_jogadores ORDER BY vitorias DESC LIMIT " + Config.get().getTopLimite();
            if(type.equalsIgnoreCase("kills")) {
                query = "SELECT nick, kills FROM guerreiro_jogadores ORDER BY kills DESC LIMIT " + Config.get().getTopLimite();
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String nick = rs.getString("nick");
                int valor = 0;
                if(type.equalsIgnoreCase("kills")) {
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

    public void broadcastACParticipantes(String message) {
        for (Player player : Manager.get().getParticipantes()) {
            ActionBar.sendActionBar(player, message.replace("&", "§").replace("{abates}", String.valueOf(player.getMetadata("gabates").get(0).value())));
        }
    }

    public void sendActionBarMessage(Player player, String message) {
        ActionBar.sendActionBar(player, message.replace("&", "§"));
    }

    public void broadcastMessage(String msg){
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
        }
    }

    public String formatTime(int segundos) {
        long HH = segundos / 3600;
        long MM = (segundos % 3600) / 60;
        long SS = segundos % 60;
        String data = " ";
        if (HH > 0) data+=" "+HH+"h";
        if (MM > 0) data+=" "+MM+"m";
        if (SS > 0) data+=" "+SS+"s";
        while(data.startsWith(" ")) {
            data = data.replaceFirst(" ", new String());
        }
        return data.length() > 0 ? data : "0s";
    }

    public void broadcastParticipantes(String msg) {
        for(Player p : Manager.get().getParticipantes()) {
            p.sendMessage(msg);
        }
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
        if(player.getInventory().getHelmet() != null) freeslots++;
        if(player.getInventory().getChestplate() != null) freeslots++;
        if(player.getInventory().getLeggings() != null) freeslots++;
        if(player.getInventory().getBoots() != null) freeslots++;

        return freeslots;
    }

    public void removerLW() {
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

            if(rs.next()) {
                nick = rs.getString(1);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nick;
    }

    public void salvarStatus(Player player, boolean venceu, int kills) {
        try {
            MySQL.get().openConnection();
            Connection conn = MySQL.get().getConnection();

            PreparedStatement ps;

            ps = conn.prepareStatement("SELECT * FROM guerreiro_jogadores WHERE nick = '" + player.getName() + "'");
            ResultSet rs = ps.executeQuery();

            if(rs == null || !rs.next()) {
                conn.prepareStatement("INSERT INTO guerreiro_jogadores VALUES ('" + player.getName() + "', " + kills + ", " + (venceu ? 1 : 0) + ", " + (venceu ? 1 : 0) + ")").execute();
            } else {
                conn.prepareStatement("UPDATE guerreiro_jogadores SET kills = kills + " + kills + " WHERE nick = '" + player.getName() + "'").execute();
                if(venceu) {
                    conn.prepareStatement("UPDATE guerreiro_jogadores SET vitorias = vitorias + 1 WHERE nick = '" + player.getName() + "'").execute();
                    conn.prepareStatement("UPDATE guerreiro_jogadores SET lw = 1 WHERE nick = '" + player.getName() + "'").execute();
                }
            }

            conn.close();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
        }
    }

    public String unserializeLocation(Location loc) {
        return loc.getWorld().getName() + "()" + loc.getX() + "()" + loc.getY() + "()" + loc.getZ() + "()" + loc.getYaw() + "()" + loc.getPitch();
    }

    public void equipPlayer(Player p) {
        p.getInventory().setHelmet(Manager.get().getHelmet().build());
        p.getInventory().setChestplate(Manager.get().getChestplate().build());
        p.getInventory().setLeggings(Manager.get().getLeggins().build());
        p.getInventory().setBoots(Manager.get().getBoots().build());
        p.getInventory().addItem(Manager.get().getSword().build());
        p.getInventory().addItem(Manager.get().getGapple().build());
        p.getInventory().addItem(Manager.get().getBow().build());
        p.getInventory().addItem(Manager.get().getArrow().build());
    }

    public Location serializeLocation(String str) {
        World world = Core.getInstance().getServer().getWorld(str.split("\\(\\)")[0]);
        double x = Double.parseDouble(str.split("\\(\\)")[1]);
        double y = Double.parseDouble(str.split("\\(\\)")[2]);
        double z = Double.parseDouble(str.split("\\(\\)")[3]);
        float yaw = Float.parseFloat(str.split("\\(\\)")[4]);
        float pitch = Float.parseFloat(str.split("\\(\\)")[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static API get() {
        return Core.getInstance().getApi();
    }

}
