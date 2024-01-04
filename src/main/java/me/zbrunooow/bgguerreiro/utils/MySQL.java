package me.zbrunooow.bgguerreiro.utils;

import me.zbrunooow.bgguerreiro.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    private Connection connection;

    private final String user;
    private final String pass;
    private final String host;
    private final int port;
    private final String db;
    private int query;

    public MySQL(String user, String pass, String url, int port, String db) {
        this.user = user;
        this.pass = pass;
        this.host = url;
        this.port = port;
        this.db = db;
        this.query = 0;
        loadDB();
    }

    public void openConnection() {
        if(Config.get().getArmazenamento().equalsIgnoreCase("MYSQL")) {
            try {
                query++;
                if ((connection != null) && (!connection.isClosed()))
                    return;

                Class.forName("com.mysql.jdbc.Driver");

                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false", user, pass);
            } catch (ClassNotFoundException | SQLException e) {
                query--;
                e.getStackTrace();
                System.out.println(
                        "Ocorreu um erro ao abrir a conexão!");
            }
        } else {
            openConnectionSQLite();
        }

    }

    public void openConnectionSQLite() {
        File file = new File(Core.getInstance().getDataFolder(), "database.db");
        String URL = "jdbc:sqlite:" + file;

        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        query--;
        if (query <= 0) {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (Exception e) {
                System.out.println(
                        "Houve um erro ao fechar a conexão (MySQL)!");
            }
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void loadDB() {
        openConnection();
        criarTabelas();
        closeConnection();
    }

    private void criarTabela(String tabela, String colunas) {
        try {
            if ((connection != null) && (!connection.isClosed())) {
                Statement stm = connection.createStatement();
                stm.executeUpdate("CREATE TABLE IF NOT EXISTS " + tabela + " (" + colunas + ");");
            }
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro ao salvar os dados (MySQL)!");
        }
    }

    private void criarTabelas() {
        criarTabela("guerreiro_jogadores", "nick varchar(16), vitorias int, kills int, lw int");
    }

    public static MySQL get() {
        return Core.getInstance().getMySQL();
    }

}
