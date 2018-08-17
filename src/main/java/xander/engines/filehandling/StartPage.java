package xander.engines.filehandling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * Alexander Pereira Croydon UK 2018 apr
 *
 * Following the Page Object Design Pattern
 * Where all changes can be made to the Web Page in question
 * De-coupled Design Principle
 *
 */
public class StartPage { //implements IPage {

    private final WebDriver driver;
    private final String url ;
    private final String mustBeThere ;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * DI principle
     * Ensures that all data is encapsulated/injected at Construction time
     *
     * @param d
     * @param u
     */
    public StartPage(WebDriver d, String u, String mustBeThereP )
    {
        LOGGER.traceEntry();
        this.driver = d ;
        this.url = u;
        this.mustBeThere = mustBeThereP ;
        LOGGER.traceExit();
    }

    /**
     * Fluent Design Principle
     * @return StartPage
     */
    public StartPage openPageUrl() {
        LOGGER.traceEntry();
        this.driver.get(this.url);
        LOGGER.traceExit();
        return this ;
    }

    public boolean validatePageReached() {

        LOGGER.traceEntry();
        WebElement foundIt = this.driver.findElement(By.cssSelector(".heading-large"));
        String foundItElemAsText = foundIt.getText();
        LOGGER.traceExit();

        return foundItElemAsText.equals(this.mustBeThere);

    }

    public boolean getBackForNextNumbrPlate() {

        this.driver.navigate().to(this.url); // get ready for the next number plate
        return this.validatePageReached();
    }


}
