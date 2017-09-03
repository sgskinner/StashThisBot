package org.sgs.stashbot.util;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class BlacklistedSubredditSqlGenerator {
    private static final String INSERT_FORMAT = "insert into blacklisted_subreddit_t (name, date_created, note) values('%s', now(), '/u/BotBust moderator list');";


    public static void generateUrlInsertFile() throws IOException {
        Scanner scanner = new Scanner(new FileReader("src/main/resources/data/botbust_moderator_list.txt"));

        FileWriter fileWriter = new FileWriter("src/main/resources/sql/blacklisted_subreddit_t.sql");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter writer = new PrintWriter(bufferedWriter);

        while (scanner.hasNext()) {
            String subredditName = scanner.nextLine().trim();
            if (StringUtils.isNotBlank(subredditName)) {
                String insertStatment = String.format(INSERT_FORMAT, subredditName);
                writer.println(insertStatment);
            }

        }

        writer.flush();
        IOUtils.closeQuietly(writer);
    }


    public static void main(String... sgs) throws IOException {
        BlacklistedSubredditSqlGenerator.generateUrlInsertFile();
    }

}
