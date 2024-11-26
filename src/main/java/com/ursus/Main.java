package com.ursus;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {

  public static void main(String[] args) {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();

    try {
      // Read URLs from file
      List<String> links = Files.readAllLines(Paths.get("links.txt"));

      // HTML output setup
      StringBuilder csv = new StringBuilder();

      for (String link : links) {
        try {
          String[] split = link.split("/");
          driver.get(link);

          String iFrameId = String.format("telegram-post-%s-%s", split[split.length - 2],
              split[split.length - 1]);
          new WebDriverWait(driver,
              Duration.ofMillis(3000)).until(
              ExpectedConditions.presenceOfElementLocated(By.id(iFrameId)));

          WebElement iframe = driver.findElement(By.id(iFrameId));
          driver.switchTo().frame(iframe);
          WebElement variable = driver.findElement(
              By.className("tgme_widget_message_views"));
          String count = variable.getText();
          String csvRow = link + ";" + count;
          System.out.println(csvRow);
          csv.append(csvRow);


        } catch (Exception e) {
          System.out.println("Failed to process link: " + link);
          e.printStackTrace();
        }
      }

      Files.write(Paths.get("report.csv"), csv.toString().getBytes());

      System.out.println("Report generated successfully: report.csv");

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      driver.quit();
    }
  }

}
