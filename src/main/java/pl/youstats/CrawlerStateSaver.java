package pl.youstats;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Queue;
import java.util.Set;


public class CrawlerStateSaver implements Serializable {



    public static void saveAll(Queue<String> queue, Queue<String> queueNames, Set<String> odwiedzone) throws IOException {
        saveQueue(queue);
        saveQueueNames(queueNames);
        saveOdwiedzone(odwiedzone);

        Logs.addEngineLog("Crawler state has been saved.");
        System.out.println("Crawler state has been saved.");
    }

    public static void saveQueue(Queue<String> queue) throws IOException {
        FileOutputStream fos = new FileOutputStream("queue.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(queue);
        oos.close();
    }

    public static void saveQueueNames(Queue<String> queueNames) throws IOException {
        FileOutputStream fos = new FileOutputStream("queueNames.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(queueNames);
        oos.close();
    }

    public static void saveOdwiedzone(Set<String> odwiedzone) throws IOException {
        FileOutputStream fos = new FileOutputStream("odwiedzone.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(odwiedzone);
        oos.close();
    }

}
