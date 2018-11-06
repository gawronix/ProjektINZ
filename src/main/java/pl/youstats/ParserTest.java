package pl.youstats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParserTest {


    static Queue<String> queue = new LinkedList<String>();
    static Queue<String> queue_names = new LinkedList<String>();
    static Set<String> odwiedzone = new HashSet<String>();

    public static void main(String[] args) throws JSONException, IOException, InterruptedException, SQLException, ClassNotFoundException {

    }


    public static void parseGuideBuilder() throws IOException, JSONException, InterruptedException, SQLException, ClassNotFoundException {

        try {
            Logs.addEngineLog("Crawler has been started.");


           // Document doc = Jsoup.connect("https://youtube.com/feed/guide_builder").get();
            Document doc = Jsoup.connect("https://youtube.com/feed/guide_builder").header("Accept", "text/javascript")
                    .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();

            String wynik = "";
            Scanner scanner = new Scanner(doc.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("window[\"ytInitialData\"]")) {
                    wynik = line;
                }

            }
            scanner.close();

            wynik = wynik.substring(wynik.indexOf("\"itemSectionRenderer\":{") + 1);
            wynik = wynik.substring(wynik.indexOf("\"itemSectionRenderer\":{") + 1);
            wynik = wynik.substring(wynik.indexOf("\"itemSectionRenderer\":{") + 1);
            wynik = "{\"" + wynik;
            wynik = wynik.split("\"desktopTopbarRenderer\":")[0];
            wynik = wynik.split("\"topbar\"")[0];
            wynik = wynik.split("\"tabIdentifier\":\"']]guide_builder\",")[0];
            wynik = wynik.substring(0, wynik.lastIndexOf("\"trackingParams\""));
            wynik = wynik.substring(0, wynik.length() - 3);
            wynik = wynik + "}";
            wynik = "{\"items\":[" + wynik;
            wynik = wynik + "],\"string\": \"string\"}";

            System.out.println(wynik);


            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(wynik);
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONArray itemSectionRenderer = items.getJSONObject(i).getJSONObject("itemSectionRenderer").getJSONArray("contents");
                JSONArray contents = itemSectionRenderer.getJSONObject(0).getJSONObject("shelfRenderer").getJSONObject("content").getJSONObject("horizontalListRenderer").getJSONArray("items");
                for (int j = 0; j < contents.length(); j++) {
                    String itemsObject = contents.getJSONObject(0).getJSONObject("gridChannelRenderer").getString("channelId");
                    System.out.println(itemsObject);
                    String itemsObjectTitle = contents.getJSONObject(j).getJSONObject("gridChannelRenderer").getJSONObject("title").getString("simpleText");
                    System.out.println(itemsObjectTitle);
                    System.out.println();
                    ParserTest.parseChannelQueue(itemsObject);

                }
            }

        }catch (SocketTimeoutException e)
        {
            Thread.sleep(500);
            parseGuideBuilder();
        }
        catch(SocketException e)
        {
            Thread.sleep(500);
            parseGuideBuilder();
        }
        catch (IOException e)
        {
            Thread.sleep(500);
            parseGuideBuilder();
        }
    }


    public static void parseChannelQueue(String channel) throws JSONException, IOException, InterruptedException, SQLException, ClassNotFoundException {

        try {
        String kategoria = "";
        System.out.println("QUEUE: " + queue_names);
        Logs.addEngineLog("Queue: " + queue_names);

        Thread.sleep(400);
        Youtube.getNameFromID(channel);


            Document doc = Jsoup.connect("https://youtube.com/channel/" + channel + "").header("Accept", "text/javascript")
                    .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();

            String wynik = null;
        Scanner scanner = new Scanner(doc.toString());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("window[\"ytInitialData\"]")) {
                wynik = line;
            }

        }
        scanner.close();

        try {


            String relatedChannels = wynik.substring(wynik.lastIndexOf("\"verticalChannelSectionRenderer\"") + 1);
            String fixedRelatedChannels = "\"" + relatedChannels;
            String text = fixedRelatedChannels.substring(0, fixedRelatedChannels.indexOf("\"tabs\":[{"));
            text = "{" + text;
            text = text.replace("\"verticalChannelSectionRenderer\":{", "");
            text = text.substring(0, text.length() - 5);

            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(text);
            JSONArray items = jsonObject.getJSONArray("items");

            String url = "";
            ArrayList<String> channels = new ArrayList<>();
            ArrayList<String> channels_ids = new ArrayList<>();

            int counter = 0;

            for (int i = 0; i < items.length(); i++) {
                JSONObject miniChannelRenderer = items.getJSONObject(i).getJSONObject("miniChannelRenderer").getJSONObject("title");
                JSONArray runs = miniChannelRenderer.getJSONArray("runs");
                JSONObject miniChannelRenderer1 = runs.getJSONObject(0);

                String id = miniChannelRenderer1.getString("text");

                url = miniChannelRenderer1.getJSONObject("navigationEndpoint").getJSONObject("commandMetadata").getJSONObject("webCommandMetadata").getString("url");
                url = url.substring(9);


                kategoria = getCatOfChannelFromHtml(url);
                int minSubs = Settings.getMinSubsForCategory(kategoria);

                if (!odwiedzone.contains(url)) {
                    if (getSubsFromHtml(url) >= minSubs) {

                        queue.add(url);
                        queue_names.add(id);
                        odwiedzone.add(url);

                        Logs.addEngineLog("Channel visited: " + id + " - " + url + " - Category: " + kategoria);
                        System.out.println(id + " - " + url + " - Channel category: " + kategoria);
                    }

                }
            }

            System.out.println("Checking channel: " + queue_names.peek() + " id: " + queue.peek());

            if (!checkIfChannelExists("name", queue.peek())) {
                if (!queue.peek().equals(null)) {
                    addChannel(queue_names.peek(), queue.peek());
                    System.out.println("Channel added to database: " + queue_names.peek());
                    Logs.addEngineLog("Channel added to database: " + queue_names.peek());
                }

                String tmp = queue.peek();
                queue.poll();
                queue_names.poll();

                parseChannelQueue(tmp);
            } else {
                String tmp = queue.peek();
                queue.poll();
                queue_names.poll();

                parseChannelQueue(tmp);
            }

        } catch (NullPointerException e) {
            //
        }catch (SocketTimeoutException e)
        {
            Thread.sleep(300);
            parseChannelQueue(channel);
        }
        catch (JSONException e) {
            // parseChannelQueue(Youtube.generateNextChannel(category, table_name), category, table_name);
        } catch (StringIndexOutOfBoundsException e) { //parseChannelQueue(Youtube.generateNextChannel(category, table_name), category, table_name);
        }



    }catch(HttpStatusException http)
    {
        System.out.println("HTTP 500 when getting subs. Trying again...");
        http.printStackTrace();

        Thread.sleep(500);
        parseChannelQueue(channel);
    }

    }


    public static void test() throws IOException {
        Document doc = Jsoup.connect("https://youtube.com/channel/UCxMAbVFmxKUVGAll0WVGpFw/videos").header("Accept", "text/javascript")
                .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();




        String wynik = "";
        String kategoria = "";
        Scanner scanner = new Scanner(doc.toString());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("window[\"ytInitialData\"]")) {
                wynik = line;

            }

        }
        scanner.close();

        System.out.println(wynik);
    }


    public static int getSubsFromHtml(String url) throws IOException, JSONException, InterruptedException {
    try {
        Document doc = Jsoup.connect("https://youtube.com/channel/" + url + "/videos").header("Accept", "text/javascript")
                .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();

        String wynik = "";
        String kategoria = "";
        Scanner scanner = new Scanner(doc.toString());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("window[\"ytInitialData\"]")) {
                wynik = line;

            }

        }
        scanner.close();

        String subJSON = wynik;
        subJSON = subJSON.substring(wynik.indexOf("\"subscriberCountText\":{") + 1);
        subJSON = subJSON.split("\"tvBanner\":")[0];
        subJSON = subJSON.replace(",", "");
        subJSON = "{\"" + subJSON;
        subJSON = subJSON + "}";
        subJSON = subJSON.replaceAll("[^0-9]", "");
        int subs = 0;
        try {
            subs = Integer.parseInt(subJSON);
            System.out.println("Subs downloaded from HTML.");
        }catch(NumberFormatException e)
        {
            subs = Youtube.getSubscribersFromID(url);
            System.out.println("Subs downloaded from Youtube API.");
        }

        return subs;
    }catch(HttpStatusException http)
    {
        System.out.println("HTTP 500 when getting subs. Trying again...");
        http.printStackTrace();

        Thread.sleep(500);
        getSubsFromHtml(url);
    }catch (SocketException http ) {
        System.out.println("HTTP 500 when getting subs. Trying again...");
        http.printStackTrace();

        Thread.sleep(500);
        getSubsFromHtml(url);
    }
    return 0;
    }

    public static String getVideoCatFromUrl(String id) throws IOException, InterruptedException {
        try {
            Document doc = Jsoup.connect("https://www.youtube.com/watch?v=" + id + "").header("Accept", "text/javascript")
                    .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();
            String wynik = "";
            Scanner scanner = new Scanner(doc.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("window[\"ytInitialData\"]")) {
                    wynik = line;
                }
            }

            scanner.close();

            wynik = wynik.substring(wynik.indexOf("\"metadataRowContainerRenderer\":{") + 1);
            wynik = wynik.substring(wynik.indexOf("\"simpleText\":\"Kategoria\"") + 1);

            wynik = wynik.substring(0, wynik.lastIndexOf("\"showMoreText\":{"));
            wynik = wynik.substring(wynik.indexOf("[{\"text\":") + 1);

            // System.out.println("wynik: " + wynik);
            wynik = wynik.substring(wynik.indexOf(":\"") + 1);
            wynik = wynik.substring(wynik.indexOf("\"") + 1);
            wynik = wynik.split("\",\"")[0];


            return translatePLtoEN(wynik);
        }catch (SocketTimeoutException ex)
        {
            Thread.sleep(1000);
            getVideoCatFromUrl(id);
            return null;
        }

    }





    public static String getCatOfChannelFromHtml(String url) throws IOException, JSONException, InterruptedException, SQLException, ClassNotFoundException {


            Document doc = Jsoup.connect("https://youtube.com/channel/" + url + "/videos").header("Accept", "text/javascript")
                    .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();
            String wynik = "";
            String kategoria = "";
            Scanner scanner = new Scanner(doc.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("window[\"ytInitialData\"]")) {
                    wynik = line;

                }

            }
            scanner.close();
        try {

            
            wynik = wynik.substring(wynik.indexOf("\"itemSectionRenderer\":{") + 1);
            wynik = wynik.substring(wynik.indexOf("\"itemSectionRenderer\":{") + 1);
            wynik = wynik.substring(wynik.indexOf("\"itemSectionRenderer\":{") + 1);
            wynik = "{\"" + wynik;
            wynik = wynik.split("\"desktopTopbarRenderer\":")[0];
            wynik = wynik.split("\"topbar\"")[0];
            wynik = wynik.split("\"tabIdentifier\":\"FEguide_builder\",")[0];
            wynik = wynik.substring(0, wynik.lastIndexOf("\"trackingParams\""));
            wynik = wynik.substring(0, wynik.length() - 3);
            wynik = wynik + "}";
            wynik = "{\"items\":[" + wynik;
            wynik = wynik + "],\"string\": \"string\"}";
            wynik = wynik.substring(0, wynik.lastIndexOf("\"continuations\":["));
            wynik = wynik.substring(0, wynik.length() - 1);
            wynik = wynik + "}}]}}]}";


            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(wynik);
            JSONArray items = jsonObject.getJSONArray("items");


            JSONArray itemSectionRenderer = items.getJSONObject(0).getJSONObject("itemSectionRenderer").getJSONArray("contents");
            JSONArray contents = itemSectionRenderer.getJSONObject(0).getJSONObject("gridRenderer").getJSONArray("items");

            int size = 0;
           if (contents.length() <= 10)
           {
               size = contents.length();
           }
           else
           {
               size = 10;
           }


            ArrayList<String> list = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                String videoId = contents.getJSONObject(j).getJSONObject("gridVideoRenderer").getString("videoId");
                list.add(getVideoCatFromUrl(videoId));
               // System.out.println(videoId);

            }

            kategoria = list.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Comparator.comparing(Map.Entry::getValue))
                    .get()
                    .getKey();

        System.out.println("HTML calculated category - " + kategoria + " for channel " + url);
        } catch (StringIndexOutOfBoundsException e) {

            kategoria = Youtube.getCatOfChannelWithPlaylistFromHtml(wynik);
            if (kategoria != null && !kategoria.isEmpty()) {
                System.out.println("HTML calculated category by playlist calculation method - " + translatePLtoEN(kategoria) + " for channel " + url);
                return translatePLtoEN(kategoria);
            }
            else
            {
                System.out.println("Youtube API used to calculate the category. HTML failed for channel " + url);
                return Youtube.findCategoryOfChannelFromID(url);
            }
        }


        return kategoria;
    }



    public static boolean checkIfChannelExists(String title, String id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = (Connection) DriverManager.getConnection(
                "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
                "wmiuam");
        PreparedStatement pst;
        pst = (PreparedStatement) con.prepareStatement("SELECT id FROM channels WHERE c_id  LIKE '"+id+"'");
        ResultSet rs = pst.executeQuery();
        if(rs.next()) {

            con.close();
            return true;

        }
        else
        {
            con.close();
            return false;
        }


    }

    public static void addChannel(String title, String channel_id) throws ClassNotFoundException, SQLException, JSONException, InterruptedException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = (Connection) DriverManager.getConnection(
                "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
                "wmiuam");




        int category_id = Youtube.getCategoryDatabaseId(ParserTest.getCatOfChannelFromHtml(channel_id));

        String query = "INSERT INTO youstats.channels(title, c_id, category_id) VALUES ( ?, ?, ?)";
        PreparedStatement statement= con.prepareStatement   (query);
        statement.setString(1, title);
        statement.setString(2, channel_id);
        statement.setInt(3, category_id);
        statement.executeUpdate();

        con.close();

    }



    public static String translatePLtoEN(String kategoria)
    {

        if (kategoria.equals("Motoryzacja"))
        {
            return "Autos & Vehicles";
        }
        else if (kategoria.equals("Rozrywka"))
        {
            return "Entertainment";
        }
        else if (kategoria.equals("Śmieszne"))
        {
            return "Comedy";
        }
        else if (kategoria.equals("Edukacja"))
        {
            return "Education";
        }
        else if (kategoria.equals("Film i animacja"))
        {
            return "Film & Animation";
        }
        else if (kategoria.equals("Gry"))
        {
            return "Gaming";
        }
        else if (kategoria.equals("Poradniki i styl"))
        {
            return "Howto & Style";
        }
        else if (kategoria.equals("Muzyka"))
        {
            return "Music";
        }
        else if (kategoria.equals("Ludzie i blogi"))
        {
            return "People & Blogs";
        }
        else if (kategoria.equals("Zwierzęta"))
        {
            return "Pets & Animals";
        }
        else if (kategoria.equals("Nauka i technika"))
        {
            return "Science & Technology";
        }
        else if (kategoria.equals("Sport"))
        {
            return "Sports";
        }
        else if (kategoria.equals("Wiadomości i polityka"))
        {
            return "Trailers";
        }
        else if (kategoria.equals("Podróże i wydarzenia"))
        {
            return "Travel & Events";
        }
        else
        {
            return null;
        }


    }

    public static String getHtmlContentOfUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).header("Accept", "text/javascript")
                .header("Accept-Encoding", "gzip, deflate").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").maxBodySize(0).timeout(0).get();
        String wynik = "";
        String kategoria = "";
        Scanner scanner = new Scanner(doc.toString());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("window[\"ytInitialData\"]")) {
                wynik = line;

            }

        }
        scanner.close();


        return wynik;
    }


}
