package me.zbrunooow.bgguerreiro.comandos;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.utils.API;
import me.zbrunooow.bgguerreiro.utils.Locations;
import me.zbrunooow.bgguerreiro.utils.Manager;
import me.zbrunooow.bgguerreiro.utils.Mensagens;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Guerreiro implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lb, String[] args) {
        if(cmd.getName().equalsIgnoreCase("guerreiro")) {
            if(!(s instanceof Player)) {
                for(String str : Mensagens.get().getComandoGeral()) {
                    s.sendMessage(str);
                }
                return false;
            }
            Player p = (Player) s;
            if(args.length == 0) {
                for(String str : Mensagens.get().getComandoGeral()) {
                    if(str.contains("force") || str.contains("set")) {
                        if(p.hasPermission("bgguerreiro.admin")) {
                            p.sendMessage(str);
                        }
                    } else {
                        p.sendMessage(str);
                    }
                }
                return false;
            }
            if(args[0].equalsIgnoreCase("iniciar") || args[0].equalsIgnoreCase("forcestart") || args[0].equalsIgnoreCase("start")) {
                if(!p.hasPermission("bgguerreiro.admin")) {
                    p.sendMessage(Mensagens.get().getSemPerm());
                    return false;
                }
                if(args.length > 1) {
                    p.sendMessage("§cUse /guerreiro forcestart");
                    return false;
                }
                if(!Evento.get().getStartado().equalsIgnoreCase("off")) {
                    p.sendMessage("§cO evento guerreiro já está começando!");
                    return false;
                }
                Evento.get().iniciar();
                return true;
            } else if(args[0].equalsIgnoreCase("cancelar") || args[0].equalsIgnoreCase("forcestop") || args[0].equalsIgnoreCase("stop")) {
                if (!p.hasPermission("bgguerreiro.admin")) {
                    p.sendMessage(Mensagens.get().getSemPerm());
                    return false;
                }
                if (args.length > 1) {
                    p.sendMessage("§cUse /guerreiro forcestop");
                    return false;
                }
                if (Evento.get().getStartado().equalsIgnoreCase("off")) {
                    p.sendMessage("§cO evento guerreiro não está começando!");
                    return false;
                }
                Evento.get().cancelar(true);
                return true;
            } else if(args[0].equalsIgnoreCase("forcedm")) {
                if (!p.hasPermission("bgguerreiro.admin")) {
                    p.sendMessage(Mensagens.get().getSemPerm());
                    return false;
                }
                if (args.length > 1) {
                    p.sendMessage("§cUse /guerreiro forcedm");
                    return false;
                }
                if (!Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
                    p.sendMessage("§cO evento guerreiro não está acontecendo!");
                    return false;
                }
                Evento.get().forceDm();
                return true;
            } else if(args[0].equalsIgnoreCase("reload")) {
                if (!p.hasPermission("bgguerreiro.admin")) {
                    p.sendMessage(Mensagens.get().getSemPerm());
                    return false;
                }
                if (args.length > 1) {
                    p.sendMessage("§cUse /guerreiro reload");
                    return false;
                }
                Core.getInstance().reloadPlugin();
                p.sendMessage("§aA configuração do plugin foi recarregada com sucesso!");
                return true;
            } else if(args[0].equalsIgnoreCase("set")) {
                if (!p.hasPermission("bgguerreiro.admin")) {
                    p.sendMessage(Mensagens.get().getSemPerm());
                    return false;
                }
                if (args.length != 2) {
                    p.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch)");
                    return false;
                }
                if(args[1].equalsIgnoreCase("entrada")) {
                    Locations.get().getLocs().set("entrada", API.get().unserializeLocation(p.getLocation()));
                    Manager.get().setEntrada(p.getLocation());
                    p.sendMessage("§aA entrada do evento Guerreiro foi setada com sucesso!");
                }
                else if(args[1].equalsIgnoreCase("saida")) {
                    Locations.get().getLocs().set("saida", API.get().unserializeLocation(p.getLocation()));
                    Manager.get().setSaida(p.getLocation());
                    p.sendMessage("§aA saída do evento Guerreiro foi setada com sucesso!");
                }
                else if(args[1].equalsIgnoreCase("deathmatch")) {
                    Locations.get().getLocs().set("deathmatch", API.get().unserializeLocation(p.getLocation()));
                    Manager.get().setDeathmatch(p.getLocation());
                    p.sendMessage("§aO deathmatch do evento Guerreiro foi setada com sucesso!");
                } else {
                    p.sendMessage("§cUse /guerreiro set (entrada/saida/deathmatch)");
                    return false;
                }
                Locations.get().saveLocs();
                return true;
            } else if(args[0].equalsIgnoreCase("entrar") || args[0].equalsIgnoreCase("participar")) {
                if (args.length > 1) {
                    p.sendMessage("§cUse /guerreiro participar");
                    return false;
                }
                if (Evento.get().getStartado().equalsIgnoreCase("off")) {
                    p.sendMessage("§cO evento guerreiro não acontecendo!");
                    return false;
                }
                if(Manager.get().getSaida() == null || Manager.get().getEntrada() == null) {
                    p.sendMessage("§cA entrada ou a saída do evento Guerreiro não foi setada!");
                    return false;
                }
                if (Evento.get().getStartado().equalsIgnoreCase("espera") || Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
                    p.sendMessage("§cA entrada para o evento guerreiro já fechou!");
                    return false;
                }
                if(Manager.get().getParticipantes().contains(p)) {
                    p.sendMessage("§cVocê já está participando do evento Guerreiro!");
                    return false;
                }
                if(API.get().getFreeSlots(p) != 36) {
                    p.sendMessage("§cEsvazie o inventário para entrar no evento!");
                    return false;
                }
                if(API.get().getArmor(p) != 0) {
                    p.sendMessage("§cEsvazie o inventário para entrar no evento!");
                    return false;
                }
                p.getActivePotionEffects().clear();
                Manager.get().getParticipantes().add(p);
                if(Core.getInstance().sc) {
                    if (Core.getSC().getClanManager().getClanPlayer(p) != null) {
                        Core.getSC().getClanManager().getClanPlayer(p).setFriendlyFire(true);
                    }
                }
                p.teleport(Manager.get().getEntrada());
                p.setMetadata("gabates", new FixedMetadataValue(Core.getInstance(), 0));
                API.get().equipPlayer(p);
                p.sendMessage("§aVocê entrou no evento guerreiro!");
                return true;
            } else if(args[0].equalsIgnoreCase("sair")) {
                if (args.length > 1) {
                    p.sendMessage("§cUse /guerreiro sair");
                    return false;
                }
                if (Evento.get().getStartado().equalsIgnoreCase("off")) {
                    p.sendMessage("§cO evento guerreiro não acontecendo!");
                    return false;
                }
                if(!Manager.get().getParticipantes().contains(p)) {
                    p.sendMessage("§cVocê não está participando do evento Guerreiro!");
                    return false;
                }
                if(Core.getInstance().sc) {
                    if (Core.getSC().getClanManager().getClanPlayer(p) != null) {
                        Core.getSC().getClanManager().getClanPlayer(p).setFriendlyFire(false);
                    }
                }
                Manager.get().getParticipantes().remove(p);
                p.removeMetadata("gabates", Core.getInstance());
                p.teleport(Manager.get().getSaida());
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.sendMessage("§aVocê saiu do evento guerreiro!");
                return true;
            } else if(args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("info")) {
                if(args.length > 1) {
                    p.sendMessage("§cUse /guerreiro status");
                    return false;
                }
                for(String str : Mensagens.get().getStatus()) {
                    if(str.contains("{players}") || str.contains("{tempo}")) {
                        if(!Evento.get().getStartado().equals("off")) {
                            p.sendMessage(str.replace("{tempo}", API.get().formatTime(Manager.get().getTempoEvento())).replace("{players}", String.valueOf(Manager.get().getParticipantes().size())));
                        }
                    }else {
                        p.sendMessage(str.replace("{status}", Evento.get().getStartado().equals("off") ? "§cNão acontecendo" : Evento.get().getStartado().equals("iniciado") ? "§aAcontecendo" : "§eIniciando..."));
                    }
                }
                return true;
            }  else if(args[0].equalsIgnoreCase("camarote")) {
                if (args.length > 1) {
                    p.sendMessage("§cUse /guerreiro camarote");
                    return false;
                }
                if(Evento.get().getStartado().equalsIgnoreCase("off")) {
                    p.sendMessage("§cO evento Guerreiro não está acontecendo!");
                    return false;
                }
                if(!Evento.get().getStartado().equalsIgnoreCase("iniciado")) {
                    p.sendMessage("§cAguarde enquanto o evento Guerreiro inicia!");
                    return false;
                }
                if(Manager.get().getParticipantes().contains(p)) {
                    p.sendMessage("§cVocê já está participando do evento Guerreiro!");
                    return false;
                }
                if(API.get().getFreeSlots(p) != 36) {
                    p.sendMessage("§cEsvazie o inventário para entrar no evento!");
                    return false;
                }
                if(API.get().getArmor(p) != 0) {
                    p.sendMessage("§cEsvazie o inventário para entrar no evento!");
                    return false;
                }
                CamaroteManager.get().joinCamarote(p);
                return true;
            }else if(args[0].equalsIgnoreCase("top")) {
                if (args.length != 2) {
                    p.sendMessage("§cUse /guerreiro top (vitorias/abates)");
                    return false;
                }
                if(args[1].equalsIgnoreCase("vitorias")) {
                    for(String str : Mensagens.get().getTopHeader()) {
                        p.sendMessage(str.replace("{type}", "Vitórias"));
                    }
                    int i = 1;
                    for(String str : API.get().listaTop("vitorias")) {
                        p.sendMessage(Mensagens.get().getTopValue().replace("{type}", "vitorias").replace("{posicao}", String.valueOf(i)).replace("{nick}", str.split("\\(\\)")[0]).replace("{amount}", str.split("\\(\\)")[1]));
                        i++;
                    }
                    for(String str : Mensagens.get().getTopFooter()) {
                        p.sendMessage(str);
                    }
                } else if (args[1].equalsIgnoreCase("kills") || args[1].equalsIgnoreCase("abates")) {
                    for (String str : Mensagens.get().getTopHeader()) {
                        p.sendMessage(str.replace("{type}", "Abates"));
                    }
                    int i = 1;
                    for (String str : API.get().listaTop("kills")) {
                        p.sendMessage(Mensagens.get().getTopValue().replace("{type}", "abates").replace("{posicao}", String.valueOf(i)).replace("{nick}", str.split("\\(\\)")[0]).replace("{amount}", str.split("\\(\\)")[1]));
                        i++;
                    }
                    for (String str : Mensagens.get().getTopFooter()) {
                        p.sendMessage(str);
                    }
                }
                return true;
            } else {
                for(String str : Mensagens.get().getComandoGeral()) {
                    if(str.contains("force") || str.contains("set")) {
                        if(p.hasPermission("bgguerreiro.admin")) {
                            p.sendMessage(str);
                        }
                    } else {
                        p.sendMessage(str);
                    }
                }
                return false;
            }
        }

        return false;
    }
}

