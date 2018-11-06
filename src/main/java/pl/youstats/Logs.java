package pl.youstats;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {

    public static void addEngineLog(String str) throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        File file = new File("logs/engine_log-" + dateFormat.format(date) + ".log");
        file.createNewFile();

        DateFormat dateFormatHour = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        BufferedWriter writer = new BufferedWriter(new FileWriter("logs/engine_log-" + dateFormat.format(date) + ".log", true));
        writer.append(dateFormatHour.format(date) + " - " + str + "\n");
        writer.close();
    }

    public static void updateDatabaseLog(String str) throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        File file = new File("logs/update_log-" + dateFormat.format(date) + ".log");
        file.createNewFile();

        DateFormat dateFormatHour = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        BufferedWriter writer = new BufferedWriter(new FileWriter("logs/update_log-" + dateFormat.format(date) + ".log", true));
        writer.append(dateFormatHour.format(date) + " - " + str + "\n");
        writer.close();
    }

}
