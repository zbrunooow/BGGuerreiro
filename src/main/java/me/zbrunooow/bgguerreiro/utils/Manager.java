package me.zbrunooow.bgguerreiro.utils;

import me.zbrunooow.bgguerreiro.Core;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Manager {

    public ArrayList<Player> participantes;

    public Location entrada;
    public Location saida;
    public Location deathmatch;

    public String lastWinner;

    public int tempoEvento;

    public Item helmet = new Item(Material.IRON_HELMET);
    public Item chestplate =  new Item(Material.IRON_CHESTPLATE);
    public Item leggins = new Item(Material.IRON_LEGGINGS);
    public Item boots = new Item(Material.IRON_BOOTS);
    public Item sword = new Item(Material.IRON_SWORD);
    public Item gapple = new Item(Material.GOLDEN_APPLE);
    public Item bow = new Item(Material.BOW);
    public Item arrow = new Item(Material.ARROW);

    public Manager() {
        participantes = new ArrayList<>();

        entrada = null;
        saida = null;
        deathmatch = null;

        if(Locations.get().getLocs().getString("entrada") != null && Locations.get().getLocs().getString("entrada").length() > 5) {
            entrada = API.get().serializeLocation(Locations.get().getLocs().getString("entrada"));
        }
        if(Locations.get().getLocs().getString("saida") != null && Locations.get().getLocs().getString("saida").length() > 5) {
            saida = API.get().serializeLocation(Locations.get().getLocs().getString("saida"));
        }
        if(Locations.get().getLocs().getString("deathmatch") != null && Locations.get().getLocs().getString("deathmatch").length() > 5) {
            deathmatch = API.get().serializeLocation(Locations.get().getLocs().getString("deathmatch"));
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

    public ArrayList<Player> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(ArrayList<Player> participantes) {
        this.participantes = participantes;
    }

    public Location getEntrada() {
        return entrada;
    }

    public void setEntrada(Location entrada) {
        this.entrada = entrada;
    }

    public Location getSaida() {
        return saida;
    }

    public void setSaida(Location saida) {
        this.saida = saida;
    }

    public Item getHelmet() {
        return helmet;
    }

    public Item getChestplate() {
        return chestplate;
    }

    public Item getLeggins() {
        return leggins;
    }

    public Item getBoots() {
        return boots;
    }

    public Item getSword() {
        return sword;
    }

    public Item getGapple() {
        return gapple;
    }

    public Item getBow() {
        return bow;
    }

    public Item getArrow() {
        return arrow;
    }

    public Location getDeathmatch() {
        return deathmatch;
    }

    public void setDeathmatch(Location deathmatch) {
        this.deathmatch = deathmatch;
    }

    public int getTempoEvento() {
        return tempoEvento;
    }

    public void setTempoEvento(int tempoEvento) {
        this.tempoEvento = tempoEvento;
    }

    public static Manager get(){
        return Core.getInstance().getManager();
    }

    public String getLastWinner() {
        return lastWinner;
    }

    public void setLastWinner(String lastWinner) {
        this.lastWinner = lastWinner;
    }
}
