package pl.youstats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public class Youtube {

    public static String channel_id;
    public static String[] countries = {"PL", "AL","AR","AT","BR","AU","CN","GR","DE","FR","HK","IN","IT","MX","PT","ZA","ES","CH","TR","AE","US"};
    public static  ArrayList<String> categoriesPL = new ArrayList<String>() {{
        add("Motoryzacja");
        add("Rozrywka");
        add("Śmieszne");
        add("Edukacja");
        add("Film i animacja");
        add("Gry");
        add("Poradniki i styl");
        add("Muzyka");
        add("Ludzie i blogi");
        add("Zwierzęta");
        add("Nauka i technika");
        add("Sport");
        add("Wiadomości i polityka");
        add("Podróże i wydarzenia");
    }};





    public static String getIDFromName(String name) throws JSONException, IOException {


        String urlAPI = "https://www.googleapis.com/youtube/v3/channels?key=" + Settings.getApiKey() + "&forUsername=" + name + "&part=id";
        String json = null;
        try {
            URL url = new URL(urlAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);  // wynik json w postaci stringa


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // parsowanie json'a i wydobycie ID kanału

        JSONObject jsonObject = new JSONObject();
        jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");
        JSONObject idObject = items.getJSONObject(0);
        channel_id = idObject.getString("id");


        System.out.println('\n' + "ID: " + channel_id);
        getChannelStatistics(channel_id);

        return channel_id;
    }

    public static void getNameFromID(String id) throws JSONException, IOException, InterruptedException {
            String urlAPI = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=" + id + "&key=" + Settings.getApiKey() + "";

            String json = null;
            try {
                URL url = new URL(urlAPI);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();
                InputStream inStream = connection.getInputStream();
                json = streamToString(inStream);  // wynik json w postaci stringa


            } catch (IOException ex) {
                ex.printStackTrace();
                Thread.sleep(3000);
                getNameFromID(id);
            }



            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(json);
            JSONArray items = jsonObject.getJSONArray("items");
            JSONObject idObject = items.getJSONObject(0);


            String title = idObject.getJSONObject("snippet").getString("title");
            System.out.println("PARSING: " + title);
            Logs.addEngineLog("Parsing channel: " + title);

    }


    public static int getSubscribersFromID(String id) throws JSONException {
        String urlAPI = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=" + id + "&key=" + Settings.getApiKey() + "";

        String json = null;
        try {
            URL url = new URL(urlAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);  // wynik json w postaci stringa


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");
        JSONObject idObject = items.getJSONObject(0);

        String subscriberCount = idObject.getJSONObject("statistics").getString("subscriberCount");
        int subscriberCountInt = Integer.parseInt(subscriberCount);

        return subscriberCountInt;
    }



    public static List<Object> getChannelStatistics(String id) throws JSONException, IOException {
        String urlAPI = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=" + id + "&key=" + Settings.getApiKey() + "";

        List<Object> statisticsList = new ArrayList<Object>();

        String json = null;
        try {
            URL url = new URL(urlAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);  // wynik json w postaci stringa


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(json);
            JSONArray items = jsonObject.getJSONArray("items");
            JSONObject idObject = items.getJSONObject(0);


            String description = idObject.getJSONObject("snippet").getString("description");
            statisticsList.add(0, description);     //  1 element listy - description


            String creationDate = idObject.getJSONObject("snippet").getString("publishedAt");
            statisticsList.add(1, creationDate);  // 2 element listy - creationDate


            try {
                String country = idObject.getJSONObject("snippet").getString("country");

                statisticsList.add(2, country); // 3 element listy - jezyk
            } catch (JSONException e) {
                try {
                    String defaultLanguage = idObject.getJSONObject("snippet").getString("defaultLanguage");
                    statisticsList.add(2, defaultLanguage);  // 3 element listy - jezyk
                } catch (JSONException e1) {
                    statisticsList.add(2, "NULL"); // 3 element listy - jezyk
                }

            }

            String viewCount = idObject.getJSONObject("statistics").getString("viewCount");
            statisticsList.add(3, viewCount); // 4 element listy - laczna liczba wyswietlen

            String subscriberCount = idObject.getJSONObject("statistics").getString("subscriberCount");
            statisticsList.add(4, subscriberCount);  // 5 element listy - laczna liczba subskrypcji
            String videoCount = idObject.getJSONObject("statistics").getString("videoCount");
            statisticsList.add(5, videoCount); // 6 element listy - laczna liczba wrzuconych filmow

        }catch(JSONException jsonEx)
        {
            jsonEx.printStackTrace();
            Logs.updateDatabaseLog("Error: " + jsonEx);
        }
        return statisticsList;
    }


    public static String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    public static String findCategoryOfChannelFromID(String id) throws JSONException, ClassNotFoundException, SQLException, InterruptedException, IOException {

        String urlAPI = "https://www.googleapis.com/youtube/v3/search?key=" + Settings.getApiKey() + "&channelId=" + id + "&part=snippet&order=date&maxResults=30";

        String json = null;
        try {
            URL url = new URL(urlAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);  // wynik json w postaci stringa


        } catch (IOException ex) {
            ex.printStackTrace();
        }

try {
        JSONObject jsonObject = new JSONObject();
        jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");

        String ids = "";
        int counter = 0;
        String videoID;
        for (int i = 0; i < items.length(); i++) {
            JSONObject vidObject = items.getJSONObject(i);
            try {
                videoID = vidObject.getJSONObject("id").getString("videoId");
                counter++;
            } catch (JSONException e) {
                //
                continue;
            }
            ids = ids + videoID + ",";
        }



        ids = ids.substring(0, ids.length() - 1);
        return Youtube.calculateCategoryOfChannel(ids, counter);
        }catch(StringIndexOutOfBoundsException e){
            return null;

        }
        //  calculateCategoryOfChannel(ids);
    }


    public static String calculateCategoryOfChannel(String ids, int counter) throws JSONException {
        String urlAPI = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + ids + "&key=" + Settings.getApiKey() + "";

        String json = null;
        try {
            URL url = new URL(urlAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);  // wynik json w postaci stringa


        } catch (IOException ex) {
            ex.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");

        int[] categories = new int[counter];
        for (int i = 0; i < counter - 1; i++) {
            JSONObject idObject = items.getJSONObject(i);
            String catID = idObject.getJSONObject("snippet").getString("categoryId");
            int intCatID = Integer.parseInt(catID);
            categories[i] = intCatID;
        }


        // szukanie liczby z największą ilością powtórzeń
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i : categories) {
            Integer count = map.get(i);
            map.put(i, count != null ? count + 1 : 0);
        }

        Integer categoryOfChannel = Collections.max(map.entrySet(),
                new Comparator<Map.Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                }).getKey();

        if (categoryOfChannel == 2) {
            return "Autos & Vehicles";
        } else if (categoryOfChannel == 1) {
            return "Film & Animation";
        } else if (categoryOfChannel == 10) {
            return "Music";
        } else if (categoryOfChannel == 15) {
            return "Pets & Animals";
        } else if (categoryOfChannel == 17) {
            return "Sports";
        } else if (categoryOfChannel == 18) {
            return "Short Movies";
        } else if (categoryOfChannel == 19) {
            return "Travel & Events";
        } else if (categoryOfChannel == 20) {
            return "Gaming";
        } else if (categoryOfChannel == 21) {
            return "Videoblogging";
        } else if (categoryOfChannel == 22) {
            return "People & Blogs";
        } else if (categoryOfChannel == 23) {
            return "Comedy";
        } else if (categoryOfChannel == 24) {
            return "Entertainment";
        } else if (categoryOfChannel == 26) {
            return "Howto & Style";
        } else if (categoryOfChannel == 27) {
            return "Education";
        } else if (categoryOfChannel == 28) {
            return "Science & Technology";
        } else if (categoryOfChannel == 29) {
            return "Nonprofits & Activism";
        } else if (categoryOfChannel == 30) {
            return "Movies";
        } else if (categoryOfChannel == 31) {
            return "Anime/Animation";
        } else if (categoryOfChannel == 32) {
            return "Action/Adventure";
        } else if (categoryOfChannel == 33) {
            return "Classics";
        } else if (categoryOfChannel == 34) {
            return "Comedy";
        } else if (categoryOfChannel == 35) {
            return "Documentary";
        } else if (categoryOfChannel == 36) {
            return "Drama";
        } else if (categoryOfChannel == 37) {
            return "Family";
        } else if (categoryOfChannel == 38) {
            return "Foreign";
        } else if (categoryOfChannel == 39) {
            return "Horror";
        } else if (categoryOfChannel == 40) {
            return "Sci-Fi/Fanstasy";
        } else if (categoryOfChannel == 41) {
            return "Thriller";
        } else if (categoryOfChannel == 42) {
            return "Shorts";
        } else if (categoryOfChannel == 43) {
            return "Shows";
        } else {
            return "Trailers";
        }
    }

    public static int getCategoryIdFromCategoryName(String name) {
        if (name.equals("Autos & Vehicles")) {
            return 2;
        } else if (name.equals("Film & Animation")) {
            return 1;
        } else if (name.equals("Music")) {
            return 10;
        } else if (name.equals("Pets & Animals")) {
            return 15;
        } else if (name.equals("Sports")) {
            return 17;
        } else if (name.equals("Short Movies")) {
            return 18;
        } else if (name.equals("Travel & Events")) {
            return 19;
        } else if (name.equals("Gaming")) {
            return 20;
        } else if (name.equals("Videoblogging")) {
            return 21;
        } else if (name.equals("People & Blogs")) {
            return 22;
        } else if (name.equals("Comedy")) {
            return 23;
        } else if (name.equals("Entertainment")) {
            return 24;
        } else if (name.equals("News & Politics")) {
            return 25;
        } else if (name.equals("Howto & Style")) {
            return 26;
        } else if (name.equals("Education")) {
            return 27;
        } else if (name.equals("Science & Technology")) {
            return 28;
        } else if (name.equals("Nonprofits & Activism")) {
            return 29;
        } else if (name.equals("Movies")) {
            return 30;
        } else if (name.equals("Anime/Animation")) {
            return 31;
        } else if (name.equals("Action/Adventure")) {
            return 32;
        } else if (name.equals("Classics")) {
            return 33;
        } else if (name.equals("Comedy")) {
            return 34;
        } else if (name.equals("Documentary")) {
            return 35;
        } else if (name.equals("Drama")) {
            return 36;
        } else if (name.equals("Family")) {
            return 37;
        } else if (name.equals("Foreign")) {
            return 38;
        } else if (name.equals("Horror")) {
            return 39;
        } else if (name.equals("Sci-Fi/Fanstasy")) {
            return 40;
        } else if (name.equals("Thriller")) {
            return 41;
        } else if (name.equals("Shorts")) {
            return 42;
        } else if (name.equals("Shows")) {
            return 43;
        } else {
            return 44;
        }

    }


    public static String getJson(String token, int categoryId) {
        String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&pageToken=" + token + "&chart=mostPopular&maxResults=50&videoCategoryId=" + categoryId + "&key=" + Settings.getApiKey() + "";

        return url;
    }

    public static String generateNextChannel(String category, String table_name) throws JSONException, SQLException, ClassNotFoundException, IOException, InterruptedException {

        Random random = new Random();
        int randomIndex = random.nextInt(((countries.length-1) - 0 + 1) + 0);
        System.out.println("Country code: " + countries[randomIndex]);
        String id = null;
        int categoryId = getCategoryIdFromCategoryName(category);
        String urlAPI = "https://www.googleapis.com/youtube/v3/videos?part=snippet&&regionCode="+countries[randomIndex]+"&chart=mostPopular&maxResults=50&videoCategoryId=" + categoryId + "&key=" + Settings.getApiKey() + "";

        String json = null;
        try {
            URL url = new URL(urlAPI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);  // wynik json w postaci stringa


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject = new JSONObject(json);

        JSONArray items = jsonObject.getJSONArray("items");
       for (int i=0; i<items.length(); i++) {

           JSONObject idObject = items.getJSONObject(i);


           id = idObject.getJSONObject("snippet").getString("channelId");
           String title = idObject.getJSONObject("snippet").getString("channelTitle");



           if (!ParserTest.checkIfChannelExists(title, id)) {
               System.out.println("Sprawdza czy nie istnieje w bazie: " + title);
               int subs = getSubscribersFromID(id);
               if (subs >= Settings.getMinSubsForCategory(category)) {
                   System.out.println("Dodano kanał do bazy: " + title + " - " + "SUB: " + subs);
                   ParserTest.addChannel(title, id);
                   ParserTest.parseChannelQueue(id);
               }
           }
       }


                generateNextChannel(category, table_name);
                return null;
    }


    public static int getCategoryDatabaseId(String channelCategory)
    {

        int category_id = 0;
        if (channelCategory.equals("Comedy")) {
            category_id = 1;
        } else if (channelCategory.equals("Music")) {
            category_id = 2;
        } else if (channelCategory.equals("Sports")) {
            category_id = 3;
        } else if (channelCategory.equals("Science & Technology")) {
            category_id = 4;
        } else if (channelCategory.equals("Entertainment")) {
            category_id = 5;
        } else if (channelCategory.equals("People & Blogs")) {
            category_id = 6;
        } else if (channelCategory.equals("Howto & Style")) {
            category_id = 7;
        } else if (channelCategory.equals("Education")) {
            category_id = 8;
        } else if (channelCategory.equals("Gaming")) {
            category_id = 9;
        } else if (channelCategory.equals("Trailers")) {
            category_id = 10;
        } else if (channelCategory.equals("Film & Animation")) {
            category_id = 11;
        } else if (channelCategory.equals("Pets & Animals")) {
            category_id = 12;
        } else if (channelCategory.equals("Autos & Vehicles")) {
            category_id = 13;
        } else {
            category_id = 14;
        }

        return category_id;
    }


    public static String getCatOfChannelWithPlaylistFromHtml(String input)
    {
        String category = null;


        for(String listItem : categoriesPL){
            if(input.contains(listItem)){
               category = listItem;
            }
        }
        return category;
    }


    public static int getNumberOfVideosFromHtml(String channel_id) throws IOException {
        int numberOfVideos = 0;

        String content =  ParserTest.getHtmlContentOfUrl("https://www.youtube.com/channel/" + channel_id + "/videos");
        content = content.replace("};", "");
        content = content.replace("window[\"ytInitialData\"] =", "");
        content = content + "}";

        System.out.println(content);

        return numberOfVideos;
    }

    public static List<Object> getChannelStatisticsFromHtml(String channel_id) throws IOException, JSONException
    {

        String content =  ParserTest.getHtmlContentOfUrl("https://www.youtube.com/channel/" + channel_id + "/about");
        content = content.replace("};", "");
        content = content.replace("window[\"ytInitialData\"] =", "");
        content = content + "}";

        JSONObject jsonObject = new JSONObject();
        jsonObject = new JSONObject(content);
        JSONArray tabs = jsonObject.getJSONObject("contents")
                .getJSONObject("twoColumnBrowseResultsRenderer")
                .getJSONArray("tabs");
        JSONObject tabRenderer = tabs.getJSONObject(5).getJSONObject("tabRenderer");
        JSONObject sectionListRenderer = tabRenderer.getJSONObject("content").getJSONObject("sectionListRenderer");
        JSONArray contents2 = sectionListRenderer.getJSONArray("contents");
        JSONObject itemSectionRenderer = contents2.getJSONObject(0).getJSONObject("itemSectionRenderer").getJSONArray("contents").getJSONObject(0);
        JSONObject channelAboutFullMetadataRenderer = itemSectionRenderer.getJSONObject("channelAboutFullMetadataRenderer");

        String subs = channelAboutFullMetadataRenderer.getJSONObject("subscriberCountText").getJSONArray("runs").getJSONObject(0).getString("text");
        String total_views = channelAboutFullMetadataRenderer.getJSONObject("viewCountText").getJSONArray("runs").getJSONObject(0).getString("text");
        String channel_created = channelAboutFullMetadataRenderer.getJSONObject("joinedDateText").getString("simpleText");

        List<Object> statisticsList = new ArrayList<Object>();

        statisticsList.add(0, subs);
        statisticsList.add(1, total_views);

        System.out.println(subs);
        System.out.println(total_views);
        System.out.println(channel_created.replace("Data dołączenia:", ""));



        return statisticsList;
        }


}