package me.zbrunooow.bgguerreiro.guerreiro;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.hooks.VaultHook;
import me.zbrunooow.bgguerreiro.utils.API;
import me.zbrunooow.bgguerreiro.utils.Config;
import me.zbrunooow.bgguerreiro.utils.Manager;
import me.zbrunooow.bgguerreiro.utils.Mensagens;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Evento {

    private String startado = "off";
    private boolean pvp = false;
    private boolean dm = false;
    private boolean dmForcado = false;

    int pvpDm = 0;

    public void iniciar() {
        API.get().removerLW();
        setStartado("anunciando");
        setPvp(false);
        setDm(false);
        pvpDm = 0;
        dmForcado = false;

        new BukkitRunnable() {
            int segundos = Config.get().getTempoAnuncios();
            int proximo = Config.get().getTempoAnuncios();
            int espera = Config.get().getTempoEspera();
            @Override
            public void run() {
                if(getStartado().equalsIgnoreCase("off")) {
                   cancel();
                   return;
                }
                if(getStartado().equalsIgnoreCase("anunciando")) {
                    if(segundos > 0) {
                        if (segundos == proximo) {
                            for (String str : Mensagens.get().getIniciando()) {
                                API.get().broadcastMessage(str.replace("{seconds}", String.valueOf(segundos)).replace("{tag}", Config.get().getTag()).replace("{valor}", String.valueOf(Config.get().getPremio())).replace("{jogadores}", String.valueOf(Manager.get().getParticipantes().size())));
                            }
                            proximo = proximo - Config.get().getTempoEntreAnuncios();
                        }
                        segundos--;
                    } else {
                        if(Manager.get().getParticipantes().size() < Config.get().getMinimoParticipantes()) {
                            API.get().broadcastMessage("§cO evento Guerreiro foi cancelado por falta de participantes. (Mínimo " + Config.get().getMinimoParticipantes() + ")");

                            cancelar(false);
                            cancel();
                            return;
                        }

                        setStartado("espera");
                        for(String str : Mensagens.get().getEspera()) {
                            API.get().broadcastParticipantes(str.replace("{tempo}", String.valueOf(espera)));
                        }
                        proximo = espera-5;
                    }
                }
                if(getStartado().equalsIgnoreCase("espera")) {
                    if(espera > 0) {
                        if(espera == proximo) {
                            for(String str : Mensagens.get().getEspera()) {
                                API.get().broadcastParticipantes(str.replace("{tempo}", String.valueOf(espera)));
                            }
                            proximo = espera-5;
                        }
                    } else {
                        setStartado("iniciado");
                        setPvp(true);
                        for(String str : Mensagens.get().getValendo()) {
                            API.get().broadcastParticipantes(str);
                        }
                    }
                    espera--;
                }
                if(getStartado().equalsIgnoreCase("iniciado")) {
                    Manager.get().setTempoEvento(Manager.get().getTempoEvento() + 1);
                }
                if(Manager.get().getTempoEvento() <= 10) {
                    API.get().broadcastACParticipantes(Mensagens.get().getAb1().replace("{players}", String.valueOf(Manager.get().getParticipantes().size())).replace("{tempo}", API.get().formatTime(Manager.get().getTempoEvento())).replace("{abates}", "0").replace("{status}", (!getStartado().equalsIgnoreCase("iniciado") ? "&cIniciando..." : "&aValendo")));
                } else {
                    API.get().broadcastACParticipantes(Mensagens.get().getAb2().replace("{players}", String.valueOf(Manager.get().getParticipantes().size())).replace("{tempo}", API.get().formatTime(Manager.get().getTempoEvento())).replace("{status}", (!getStartado().equalsIgnoreCase("iniciado") ? "&cIniciando..." : "&aValendo")));
                }
                if(Config.get().isDmHabilitado() || dmForcado) {
                    if (!isDm()) {
                        if(Config.get().getDmTempo()-120 == Manager.get().getTempoEvento() || Config.get().getDmTempo()-60 == Manager.get().getTempoEvento()) {
                            for(String str : Mensagens.get().getDmChegando()) {
                                API.get().broadcastParticipantes(str.replace("{minutos}", Config.get().getDmTempo()-120 == Manager.get().getTempoEvento() ? "2" : "1"));
                            }
                        }
                    }
                    if(Config.get().getDmTempo() == Manager.get().getTempoEvento()) {
                        setPvp(false);
                        pvpDm = Manager.get().getTempoEvento()+Config.get().getDmSemPvP();
                        for(String str : Mensagens.get().getDmStart()) {
                            API.get().broadcastParticipantes(str.replace("{segundos}", String.valueOf(Config.get().getDmSemPvP())));
                        }
                        for(Player vivos : Manager.get().getParticipantes()) {
                            vivos.teleport(Manager.get().getDeathmatch());
                        }
                        for(Player spec : CamaroteManager.get().getSpectators()) {
                            spec.teleport(Manager.get().getDeathmatch());
                        }
                        setDm(true);
                    }
                    if(isDm() && !isPvp()) {
                        if(Manager.get().getTempoEvento() >= pvpDm) {
                            setPvp(true);
                            API.get().broadcastParticipantes("§aO §cPvP §afoi reativado, boa sorte!");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0L, 20L);
    }

    public void forceDm() {
        dmForcado = true;
        setPvp(false);
        pvpDm = Manager.get().getTempoEvento()+Config.get().getDmSemPvP();
        for(String str : Mensagens.get().getDmStart()) {
            API.get().broadcastParticipantes(str.replace("{segundos}", String.valueOf(Config.get().getDmSemPvP())));
        }
        for(Player vivos : Manager.get().getParticipantes()) {
            vivos.teleport(Manager.get().getDeathmatch());
        }
        setDm(true);
    }

    public void cancelar(boolean broadcast) {
        dmForcado = false;
        setStartado("off");
        setPvp(false);
        for(Player p : Manager.get().getParticipantes()) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.teleport(Manager.get().getSaida());
        }
        Manager.get().getParticipantes().clear();

        if(broadcast) {
            for(String str : Mensagens.get().getCancelado()) {
                API.get().broadcastMessage(str);
            }
        }
    }

    public void verificarFinal() {
        if(Manager.get().getParticipantes().size() >= 2) {
            API.get().broadcastParticipantes(Mensagens.get().getRestam().replace("{restam}", String.valueOf(Manager.get().getParticipantes().size())));
        } else {
            Player vencedor = Manager.get().getParticipantes().get(0);
            vencedor.getInventory().clear();
            vencedor.getInventory().setArmorContents(null);
            int kills = Integer.parseInt(String.valueOf(vencedor.getMetadata("warriorKills").get(0).value()));
            for(String str : Mensagens.get().getFinalizado()) {
                API.get().broadcastMessage(str.replace("{abates}", String.valueOf(kills)).replace("{duração}", API.get().formatTime(Manager.get().getTempoEvento())).replace("{tag}", Config.get().getTag()).replace("{valor}", String.valueOf(Config.get().getPremio())).replace("{vencedor}", vencedor.getName()));
            }
            for (PotionEffect effect : vencedor.getActivePotionEffects()) {
                vencedor.removePotionEffect(effect.getType());
            }
            vencedor.removeMetadata("warriorKills", Core.getInstance());
            API.get().salvarStatus(vencedor, true, kills);
            Manager.get().getParticipantes().clear();
            setStartado("off");
            setPvp(false);
            setDm(false);
            dmForcado = false;
            vencedor.teleport(Manager.get().getSaida());
            VaultHook.eco.depositPlayer(vencedor, Config.get().getPremio());
            Manager.get().setLastWinner(vencedor.getName());
            if(Core.getInstance().sc) {
                if (Core.getSC().getClanManager().getClanPlayer(vencedor) != null) {
                    Core.getSC().getClanManager().getClanPlayer(vencedor).setFriendlyFire(false);
                }
            }
        }
    }

    public String getStartado() {return startado;}
    public void setStartado(String startado) {this.startado = startado;}
    public boolean isPvp() {return pvp;}
    public void setPvp(boolean pvp) {this.pvp = pvp;}

    public boolean isDm() {
        return dm;
    }

    public void setDm(boolean dm) {
        this.dm = dm;
    }

    public static Evento get() {
        return Core.getInstance().getEvento();
    }

}
