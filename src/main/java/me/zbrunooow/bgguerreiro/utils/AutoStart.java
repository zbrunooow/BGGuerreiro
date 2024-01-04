package me.zbrunooow.bgguerreiro.utils;

import me.zbrunooow.bgguerreiro.Core;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;

public class AutoStart {
    public static void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String hora : Config.get().getHorarios()) {
                    if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == Integer.parseInt(hora.substring(0, 2)) &&
                            Calendar.getInstance().get(Calendar.MINUTE) == Integer.parseInt(hora.substring(3, 5)) && Evento.get().getStartado().equalsIgnoreCase("off")) {
                        Evento.get().iniciar();
                    }
                }
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0L, 1000L);
    }
}
