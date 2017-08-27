package org.sgs.stashbot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DbDataLoader {
    private static final Logger LOG = LogManager.getLogger(DbDataLoader.class);
    private DataSource dataSource;
    private DataSource rootDataSource;


    @Autowired
    public DbDataLoader(DataSource dataSource, DataSource rootDataSource) {
        this.dataSource = dataSource;
        this.rootDataSource = rootDataSource;
    }


    public void fireAllScripts() {
        teardownDb();
        bootstrapDb();
        loadDummyStashResults();
        loadScrapedUrls();
    }


    public void bootstrapDb() {
        executeSqlStatements(getBootstrapSqlStatements(), getRootDataSource());
    }


    public void teardownDb() {
        try {
            executeSqlStatements(getTeardownSqlStatements(), getRootDataSource());
        } catch (Exception e) {
            // No-op in the case the user/schema didn't exist before we started
            LOG.info("It appears the stashbot schema was not present: %s", e.getMessage());
        }
    }


    public void loadDummyStashResults() {
        executeSqlStatements(getDummyStashResultSqlStatements(), getDataSource());
    }


    public void loadDummyStashUrls() {
        executeSqlStatements(getDummyUrlSqlStatements(), getDataSource());
    }


    public void loadScrapedUrls() {
        try {
            executeSqlStatements(getScrapedUrlStatements(), getDataSource());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> getScrapedUrlStatements() throws IOException {
        InputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("src/test/resources/sql/scraped_url_t.sql.tgz");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        TarArchiveInputStream tarArchiveInputStream;
        try {
            tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(fileInputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Have to do this to advance to the one and only file in the archive
        tarArchiveInputStream.getNextTarEntry();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tarArchiveInputStream));

        return getSqlStatementsFromFile(bufferedReader);
    }


    private List<String> getTeardownSqlStatements() {
        return getSqlStatementsFromFile("src/main/resources/ddl/teardown.sql");
    }


    private List<String> getBootstrapSqlStatements() {
        return getSqlStatementsFromFile("src/main/resources/ddl/bootstrap.sql");
    }


    private List<String> getDummyStashResultSqlStatements() {
        return getSqlStatementsFromFile("src/test/resources/sql/stash_result_t-dummyData.sql");
    }


    private List<String> getDummyUrlSqlStatements() {
        return getSqlStatementsFromFile("src/test/resources/sql/stash_url_t-dummyData.sql");
    }


    private void executeSqlStatements(List<String> stringStatements, DataSource aDataSource) {
        Connection connection = null;
        try {
            connection = aDataSource.getConnection();
            Statement statement = connection.createStatement();
            for (String stringStatement : stringStatements) {
                statement.executeUpdate(stringStatement);
                LOG.info("Executed: %s", stringStatement);
            }
            statement.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.error("Could not close connection: %s", e.getMessage());
                }
            }
        }
    }


    private List<String> getSqlStatementsFromFile(String filepath) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filepath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return getSqlStatementsFromFile(fileReader);
    }



    private List<String> getSqlStatementsFromFile(Reader reader) {
        Scanner scanner = new Scanner(reader);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (StringUtils.isNotBlank(line) && !line.startsWith("--")) {
                line = line.trim().replaceAll(" +", " "); // replace mutliple spaces with one
                line = line + " ";
                stringBuilder.append(line);
            }
        }

        scanner.close();

        String[] statements = stringBuilder.toString().split(";");
        List<String> goodStatements = new ArrayList<>();
        for (String statement : statements) {
            if (StringUtils.isNotBlank(statement)) {
                // Fencepost for split(";"), which will produce empty token after last ';'
                goodStatements.add(statement);
            }
        }


        return goodStatements;
    }


    private DataSource getDataSource() {
        return dataSource;
    }


    private DataSource getRootDataSource() {
        return rootDataSource;
    }


    public static void main(String... sgs) {
        DbDataLoader dataLoader = SpringContext.getBean(DbDataLoader.class);
        dataLoader.fireAllScripts();
    }


}
