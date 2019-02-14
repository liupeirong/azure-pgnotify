package com.paigeliu.azure_pgnotify;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.jdbc.PGDataSource;    
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgsqlConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(PgsqlConnection.class);

    public PGConnection createConnection(Configurations config) throws Exception {
        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost(config.getProperty("postgresql.host"));
        dataSource.setPort(Integer.parseInt(config.getProperty("postgresql.port")));
        dataSource.setDatabaseName(config.getProperty("postgresql.database"));
        dataSource.setUser(config.getProperty("postgresql.user"));
        dataSource.setPassword(config.getProperty("postgresql.password"));
        dataSource.setSslMode(config.getProperty("postgresql.sslmode"));

        PGConnection connection = (PGConnection) dataSource.getConnection();
        String pgnotify_channel = config.getProperty("pgnotify.channel");
        Statement statement = connection.createStatement();
        statement.execute("LISTEN " + pgnotify_channel);
        statement.close();
        return connection;
    }
}
