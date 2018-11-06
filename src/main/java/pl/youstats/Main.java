package pl.youstats;



import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.*;


public class Main {
	static
	{
		System.out.println("Hello World - YouStats");
		System.out.println("Youtube API key: " + Settings.getApiKey());
	}


	public static void main(String[] args) throws JSONException, InterruptedException, SQLException, ClassNotFoundException, IOException {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				System.out.println("Crawler shutting down.");
				try {

					CrawlerStateSaver.saveAll(ParserTest.queue, ParserTest.queue_names, ParserTest.odwiedzone);
					Logs.addEngineLog("Crawler shutting down.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});


		File file = new File("queue.tmp");
		if (!file.exists())
		{
			ParserTest.parseGuideBuilder();
		}
		else
		{
			Logs.addEngineLog("Loading previous saved crawler state.");
			System.out.println("Loading previous saved crawler state.");
			CrawlerStateLoader.resumeCrawler();

			String tmp = ParserTest.queue.peek();
			ParserTest.queue.poll();
			ParserTest.queue_names.poll();
			ParserTest.parseChannelQueue(tmp);
		}

		// start robota - parsowanie podstrony /feed/guide_builder
		//ParserTest.parseGuideBuilder();

		//Selenium.dynamicVideosLoad();

		//Youtube.getChannelStatisticsFromHtml("UC-lHJZR3Gqxm24_Vd_AJ5Yw");
		// -> Youtube.getNumberOfVideosFromHtml("UC-lHJZR3Gqxm24_Vd_AJ5Yw");
	}


}

