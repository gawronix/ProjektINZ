package pl.youstats;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium {
    public static void dynamicVideosLoad() throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "/Users/dymitrsoltysiak/Desktop/chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.youtube.com/user/ImagineDragons/videos?sort=dd&view=0&shelf_id=0");


        JavascriptExecutor js = ((JavascriptExecutor) driver);


        long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

        while (true) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000);



            long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

           System.out.println(lastHeight + " // " + newHeight);
            if (newHeight == lastHeight) {
              break;
              }
             lastHeight = newHeight;


        }




    }
}
