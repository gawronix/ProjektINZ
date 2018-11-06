package pl.youstats;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

public class CrawlerStateLoader {

    public static void resumeCrawler() throws IOException, ClassNotFoundException {
        loadQueue();
        loadQueueNames();
        loadOdwiedzone();
    }


    public static void loadQueue() throws IOException, ClassNotFoundException {
        FileInputStream fis;
        fis = new FileInputStream("queue.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ParserTest.queue = (Queue<String>) ois.readObject();
        ois.close();
    }

    public static void loadQueueNames() throws IOException, ClassNotFoundException {
        FileInputStream fis;
        fis = new FileInputStream("queueNames.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ParserTest.queue_names = (Queue<String>) ois.readObject();
        ois.close();
    }

    public static void loadOdwiedzone() throws IOException, ClassNotFoundException {
        FileInputStream fis;
        fis = new FileInputStream("odwiedzone.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ParserTest.odwiedzone = (Set<String>) ois.readObject();
        ois.close();
    }

}
