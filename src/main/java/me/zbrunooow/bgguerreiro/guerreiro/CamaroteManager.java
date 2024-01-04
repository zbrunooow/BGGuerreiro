package me.zbrunooow.bgguerreiro.guerreiro;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.utils.Item;
import me.zbrunooow.bgguerreiro.utils.Manager;
import me.zbrunooow.bgguerreiro.utils.Mensagens;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CamaroteManager {

    Item item = new Item(Material.REDSTONE);

    private ArrayList<Player> spectators;

    public CamaroteManager() {
        spectators = new ArrayList<>();
        item.setDisplayName("§c§lSAIR");
        item.addEnchantment(34, 1);
    }

    public void joinCamarote(Player p) {
        p.getPlayer().teleport(Manager.get().getEntrada());
        p.getPlayer().getInventory().setItem(4, item.build());

        for(Player online : Bukkit.getOnlinePlayers()) {
            online.hidePlayer(p);
        }

        if(Evento.get().isDm()) {
            p.getPlayer().teleport(Manager.get().getDeathmatch());
        }

        p.setAllowFlight(true);
        p.sendMessage(Mensagens.get().getCamaroteJoin());
        spectators.add(p);
    }

    public void leaveCamarote(Player p) {
        p.getPlayer().teleport(Manager.get().getSaida());
        p.getPlayer().getInventory().clear();

        for(Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(p);
        }

        p.setAllowFlight(false);
        p.sendMessage(Mensagens.get().getCamaroteLeave());
        spectators.remove(p);
    }

    public static CamaroteManager get() {
        return Core.getInstance().getCM();
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }
}
