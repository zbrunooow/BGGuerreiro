package me.zbrunooow.bgguerreiro.utils;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import me.zbrunooow.bgguerreiro.Core;
import org.bukkit.configuration.file.FileConfiguration;

@Data
public class Mensagens {

    private String noPermission;
    private String kill;
    private String survivorsLeft;
    private String ab1;
    private String ab2;
    private String topValue;
    private String camaroteJoin;
    private String camaroteLeave;
    private List<String> generalCommand;
    private List<String> topHeader;
    private List<String> topFooter;
    private List<String> starting;
    private List<String> cancelled;
    private List<String> status;
    private List<String> waiting;
    private List<String> started;
    private List<String> dmComing;
    private List<String> dmStart;
    private List<String> expired;

    public Mensagens() {
        FileConfiguration config = Core.getInstance().getConfig();
        replaceAndSet(config, this::setNoPermission, "Sem-Permissao");
        replaceAndSet(config, this::setKill, "Abate");
        replaceAndSet(config, this::setSurvivorsLeft, "Restam");
        replaceAndSet(config, this::setAb1, "ActionBar-1");
        replaceAndSet(config, this::setAb2, "ActionBar-2");
        replaceAndSet(config, this::setCamaroteJoin, "Camarote");
        replaceAndSet(config, this::setCamaroteLeave, "Camarote-Saiu");
        replaceAndSet(config, this::setTopValue, "Top.Value");

        replaceAndSetList(config, this::setGeneralCommand, "Geral");
        replaceAndSetList(config, this::setTopHeader, "Top.Header");
        replaceAndSetList(config, this::setTopFooter, "Top.Footer");
        replaceAndSetList(config, this::setStarting, "Iniciando");
        replaceAndSetList(config, this::setCancelled, "Cancelado");
        replaceAndSetList(config, this::setStatus, "Status");
        replaceAndSetList(config, this::setWaiting, "Espera");
        replaceAndSetList(config, this::setStarted, "Valendo");
        replaceAndSetList(config, this::setDmComing, "DM-Chegando");
        replaceAndSetList(config, this::setDmStart, "DM-Start");
        replaceAndSetList(config, this::setExpired, "Finalizado");
    }

    private void replaceAndSet(FileConfiguration config, Setter setter, String linha) {
        setter.set(config.getString("Mensagens." + linha).replace('&', '§'));
    }

    private void replaceAndSetList(FileConfiguration config, ListSetter setter, String linha) {
        setter.set(
                config.getStringList("Mensagens." + linha).stream()
                        .map(str -> str.replace('&', '§'))
                        .collect(Collectors.toList()));
    }

    interface Setter {
        void set(String value);
    }

    interface ListSetter {
        void set(List<String> values);
    }

}
