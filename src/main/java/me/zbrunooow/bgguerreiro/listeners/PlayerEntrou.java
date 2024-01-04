package me.zbrunooow.bgguerreiro.listeners;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEntrou implements Listener {

    @EventHandler
    public void aoEntrar(PlayerJoinEvent e) {
        if(e.getPlayer().isOp() || e.getPlayer().hasPermission("bgguerreiro.admin")) {
            if (Core.getInstance().update) {
                e.getPlayer().sendMessage(Core.getInstance().prefix + "§cAtualização disponível! Versão: " + Core.getInstance().updateversion + " já existente!");
                e.getPlayer().sendMessage(Core.getInstance().prefix + "§cPara pegar a nova versão:");
                e.getPlayer().sendMessage(Core.getInstance().prefix + "§cAcesse a GB: https://gamersboard.com.br/profile/41269-zbrunooow/");
            } else {
                e.getPlayer().sendMessage(Core.getInstance().prefix + "§eNenhuma atualização disponível, versão mais recente já instalada! §a(" + Core.getInstance().updateversion + ")");
            }
        }
        if(Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
            if(CamaroteManager.get().getSpectators().size() <= 0) {
                return;
            }
            for(Player spec : CamaroteManager.get().getSpectators()) {
                e.getPlayer().hidePlayer(spec);
            }
        }
    }

}
