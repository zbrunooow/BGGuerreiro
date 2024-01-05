package me.zbrunooow.bgguerreiro.manager;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.configuration.file.FileConfiguration;

@Data
public class MessageManager {

    private String noPermission;
    private String kill;
    private String survivorsLeft;
    private String statusActionbar;
    private String informationActionbar;
    private String topValue;
    private String boxJoin;
    private String boxLeave;
    private String deathWithoutReason;
    private List<String> youWin;
    private List<String> generalCommand;
    private List<String> topHeader;
    private List<String> topFooter;
    private List<String> starting;
    private List<String> cancelled;
    private List<String> status;
    private List<String> waiting;
    private List<String> started;
    private List<String> deathmatchComing;
    private List<String> deathmatchStart;
    private List<String> expired;

    public MessageManager() {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        replaceAndSet(config, this::setNoPermission, "Sem-Permissao");
        replaceAndSet(config, this::setKill, "Abate");
        replaceAndSet(config, this::setSurvivorsLeft, "Restam");
        replaceAndSet(config, this::setStatusActionbar, "Status-AB");
        replaceAndSet(config, this::setInformationActionbar, "Informacoes-AB");
        replaceAndSet(config, this::setBoxJoin, "Camarote");
        replaceAndSet(config, this::setBoxLeave, "Camarote-Saiu");
        replaceAndSet(config, this::setTopValue, "Top.Value");
        replaceAndSet(config, this::setDeathWithoutReason, "Morreu-Sem-Player");

        replaceAndSetList(config, this::setGeneralCommand, "Geral");
        replaceAndSetList(config, this::setYouWin, "Venceu");
        replaceAndSetList(config, this::setTopHeader, "Top.Header");
        replaceAndSetList(config, this::setTopFooter, "Top.Footer");
        replaceAndSetList(config, this::setStarting, "Iniciando");
        replaceAndSetList(config, this::setCancelled, "Cancelado");
        replaceAndSetList(config, this::setStatus, "Status");
        replaceAndSetList(config, this::setWaiting, "Espera");
        replaceAndSetList(config, this::setStarted, "Valendo");
        replaceAndSetList(config, this::setDeathmatchComing, "DM-Chegando");
        replaceAndSetList(config, this::setDeathmatchStart, "DM-Start");
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
