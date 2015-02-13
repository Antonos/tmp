import com.google.common.base.Predicate;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.joda.time.LocalDateTime.now;

/**
 * Created by anton on 13.02.2015.
 */
public class PolandRegisterBot {

    public static PropertiesConfiguration config;

    public static void main(String[] args) throws InterruptedException, IOException, ParseException, GeneralSecurityException, ConfigurationException {

        config = new PropertiesConfiguration("D:/Dropbox/Bots/PolandVisa/poland-register-bot.properties");

        WebElement submitBtn;

        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = allProfiles.getProfile("selenium");

        WebDriver driver = new FirefoxDriver(profile);

        // And now use this to visit Google
        driver.get("http://www.polandvisa-ukraine.com/scheduleappointment_2.html");

        try {
            WebElement frame1 = driver.findElement(By.tagName("iframe"));
            driver.get(frame1.getAttribute("src"));

            WebElement link1 = driver.findElement(By.id("ctl00_plhMain_lnkSchApp"));
            link1.click();

            comboSelect(driver,"ctl00_plhMain_cboVAC","Польщі " + args[0]);

            comboSelect(driver,"ctl00_plhMain_cboPurpose","Подача документів");

            submitBtn = driver.findElement(By.id("ctl00_plhMain_btnSubmit"));
            submitBtn.click();

            comboSelect(driver,"ctl00_plhMain_cboVisaCategory","Національна Віза");

            Thread.sleep(config.getLong("BROWSER_DATA_LOAD_DELAY"));

            WebElement result = driver.findElement(By.id("ctl00_plhMain_lblMsg"));
            String regionDate = result.getText();

            if(regionDate.length()>30) throw new NoSuchElementException("no date");

            DateTime regionDateTime = DateParser.parse(regionDate);

            if (regionDateTime.getMonthOfYear() < 7) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
                System.out.println(sdf.format(regionDateTime.toDate()));

                submitBtn = driver.findElement(By.id("ctl00_plhMain_btnSubmit"));
                submitBtn.click();

                inputType(driver, "ctl00_plhMain_repAppReceiptDetails_ctl01_txtReceiptNumber", config.getString("VISA_DETAILS_RECEIPT_NUMBER"));

                submitBtn = driver.findElement(By.id("ctl00_plhMain_btnSubmit"));
                submitBtn.click();

                inputType(driver, "ctl00_plhMain_txtEmailID", config.getString("ACCOUNT_EMAIL"));
                inputType(driver, "ctl00_plhMain_txtPassword", config.getString("ACCOUNT_PASSWORD"));

                submitBtn = driver.findElement(By.id("ctl00_plhMain_btnSubmitDetails"));
                submitBtn.click();

                inputType(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_tbxPPTEXPDT", config.getString("VISA_DETAILS_PASSPORT_EXPIRE_DATE"));
                comboSelect(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_cboTitle", config.getString("VISA_DETAILS_TITLE"));
                inputType(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_tbxFName", config.getString("VISA_DETAILS_FIRST_NAME"));
                inputType(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_tbxLName", config.getString("VISA_DETAILS_LAST_NAME"));
                inputType(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_tbxDOB", config.getString("VISA_DETAILS_BIRTH_DATE"));
                inputType(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_tbxReturn", config.getString("VISA_DETAILS_RETURN_DATE"));
                comboSelect(driver, "ctl00_plhMain_repAppVisaDetails_ctl01_cboNationality", config.getString("VISA_DETAILS_NATIONALITY"));

                System.exit(0);
            }

        }catch (NoSuchElementException e){
            e.printStackTrace(); //ignore
        }

        driver.quit();
    }

    public static void inputType(WebDriver driver, String inputId, String value) {
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor)driver).executeScript("document.getElementById('" + inputId + "').setAttribute('value', '" + value + "')");
        }
    }

    public static void comboSelect(WebDriver driver, String comboId, String option) {
        Select select = new Select(driver.findElement(By.id(comboId)));
        select.selectByVisibleText(option);
    }

}
