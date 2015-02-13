import com.google.common.base.Predicate;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.joda.time.LocalDateTime.now;

/**
 * Created by anton on 26.01.2015.
 */
public class PolandCheckBot {

    public static PropertiesConfiguration config;

    public static void main(String[] args) throws InterruptedException, IOException, ParseException, GeneralSecurityException, ConfigurationException {

        config = new PropertiesConfiguration("poland-check-bot.properties");

        if(now().getHourOfDay()<7 || now().getHourOfDay()>22) return;

        System.setOut(new PrintStream(new FileOutputStream(config.getString("LOG_FILE_PATH"),true), true, "UTF-8"));

        // Create a new instance of the html unit driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.

        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = allProfiles.getProfile("selenium");

        WebDriver driver = new FirefoxDriver(profile);

        // And now use this to visit Google
        driver.get("http://www.polandvisa-ukraine.com/scheduleappointment_2.html");

        try {
            WebElement frame1 = driver.findElement(By.tagName("iframe"));
            driver.get(frame1.getAttribute("src"));

            WebElement link1 = driver.findElement(By.id("ctl00_plhMain_lnkChkAppmntAvailability"));
            link1.click();

            Select regionSelect = new Select(driver.findElement(By.id("ctl00_plhMain_cboVAC")));

            waitUntilRowPopulates(driver, regionSelect, 17);

            System.out.print("\n" + now().toString(DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")) + " ");

            checkDate(driver, "21", "чв");
            checkDate(driver, "15", "хм");
            checkDate(driver, "16", "жт");
            checkDate(driver, "17", "вн");
        }catch (NoSuchElementException e){
            //ignore
        }

        driver.quit();
    }

    public static void checkDate(WebDriver driver, String regionValue, String regionLabel) throws InterruptedException, IOException, ParseException, GeneralSecurityException {
        Select regionSelect = new Select(driver.findElement(By.id("ctl00_plhMain_cboVAC")));
        regionSelect.selectByValue(regionValue);

        Select visaSelect = new Select(driver.findElement(By.id("ctl00_plhMain_cboVisaCategory")));

        waitUntilRowPopulates(driver, visaSelect, 3);

        Thread.sleep(config.getLong("BROWSER_DATA_LOAD_DELAY"));

        visaSelect.selectByValue("235");

        WebElement btn = driver.findElement(By.id("ctl00_plhMain_btnSubmit"));
        btn.click();

        Thread.sleep(config.getLong("BROWSER_DATA_LOAD_DELAY"));

        WebElement result = driver.findElement(By.id("ctl00_plhMain_lblAvailableDateMsg"));
        if(result.getText().length()>40) {
            String regionDate = result.getText().substring(41);
            DateTime regionDateTime = DateParser.parse(regionDate);

            if (regionDateTime.getMonthOfYear() < 4) {
                if(NotificationService.checkNotificationInfo(regionLabel)){
                    NotificationService.refresh();
                    NotificationService.send(regionLabel, regionDate);
                    NotificationService.storeNotificationInfo(regionLabel);
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
            System.out.print(regionLabel + "_" + sdf.format(regionDateTime.toDate()) + " | ");
        }
    }

    public static void waitUntilRowPopulates(WebDriver driver, Select element, final int rowCount) {
        final Select select = element;

        new FluentWait<WebDriver>(driver)
                .withTimeout(60, TimeUnit.SECONDS)
                .pollingEvery(10, TimeUnit.MILLISECONDS)
                .until(new Predicate<WebDriver>() {

                    public boolean apply(WebDriver d) {
                        List<WebElement> rawList = select.getOptions();
                        return (rawList.size() >= rowCount);
                    }
                });
    }

}
