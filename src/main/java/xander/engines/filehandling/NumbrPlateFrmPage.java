package xander.engines.filehandling;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

public class NumbrPlateFrmPage implements ActionPerItem {

    private final WebDriver webDriverHolder;
    private final StartPage startPage ;
    private static final Logger LOGGER = LogManager.getLogger();
    private String dirLocationForScrShots = null ;
    private String inputDataFileOrigin = null ;

    /**
     * DI principle
     * Ensures that all data is encapsulated/injected at Construction time
     *
     * @param d <==== WebDriver Instance
     *
     */
    public NumbrPlateFrmPage(WebDriver d , StartPage s, String dirP, String excelCsvFileOrigin)
    {
        this.webDriverHolder = d ;
        this.startPage = s ;
        this.dirLocationForScrShots = dirP ;
        this.inputDataFileOrigin = excelCsvFileOrigin ;
    }

    public boolean validateResultPageReached() {
        String confirmFoundSomething = this.webDriverHolder.getCurrentUrl();
        LOGGER.debug(confirmFoundSomething);
        if (confirmFoundSomething.equals("https://vehicleenquiry.service.gov.uk/ConfirmVehicle"))
        {
            return true ;
        }
        else
        {
            return false ;
        }
    }

    public String getError()
    {
        WebElement chkErr = webDriverHolder.findElement(By.id("Vrm-error"));
        return chkErr==null?"":chkErr.getText();
    }



    @Override
    public Map<String, String> doActionPerItem(String numbrplate) {

        Map<String,String> result = new HashMap<>();
        result.put("NumberPlate",numbrplate);


        // Enter number plate
        WebElement numberPlateTxtbox = webDriverHolder.findElement(By.id("Vrm"));
        numberPlateTxtbox.sendKeys(numbrplate);

        // Click continue
        WebElement goButton = webDriverHolder.findElement(By.name("Continue"));
        goButton.click();

        if (validateResultPageReached())
        {
            WebElement foundIt = this.webDriverHolder.findElement(By.cssSelector(".heading-large"));
            String foundItElemAsText = foundIt.getText();
            if (foundItElemAsText.equals("Vehicle details could not be found")) {

                LOGGER.warn("Failed to find msg extracted from web screen is " + foundItElemAsText );
                result.put("Status","Vehicle details could not be found");
            }
            else
            {
                WebElement make = webDriverHolder.findElement(By.id("Make"));
                String makeTxt = make.getAttribute("value");
                LOGGER.debug(makeTxt);
                result.put("Make",makeTxt);

                // Confirm colour
                WebElement colour = webDriverHolder.findElement(By.id("Colour"));
                String colourTxt = colour.getAttribute("value");
                LOGGER.debug(colourTxt);
                result.put("Colour",colourTxt);

                result.put("Status","Ok");

                int slashPos = this.inputDataFileOrigin.lastIndexOf(File.separator); // studynote path separator is ; : for win linux resp. separator is \ / resp.
                String fileOrigin = this.inputDataFileOrigin.substring(slashPos+1);
                this.takeScreenPic(fileOrigin +"."+numbrplate);


            }
        }
        else
        {
            LOGGER.warn(this.webDriverHolder.getCurrentUrl() + " " + this.getError() ); // prints out format error if it is found
            result.put("Status",this.webDriverHolder.getCurrentUrl() + " " + this.getError());
        }

        this.startPage.getBackForNextNumbrPlate();

        return result ;





    }

    private void takeScreenPic(String numbrPlateFound ) {
        LOGGER.trace("FileOrigin+NumberPlate {}",() -> {return numbrPlateFound;});
        try
        {
            LOGGER.trace("about to do {}",() -> {return "TakeAScreenShot storing to temp file";});
            File picFoundNumbrPlate = ((TakesScreenshot) webDriverHolder).getScreenshotAs(OutputType.FILE); // studynote parameter was OutputType<File> wishlist why does it always default to .png ?

            LOGGER.trace("about to copy temp file to {}",() -> {return this.dirLocationForScrShots;});
            FileUtils.copyFile(picFoundNumbrPlate, new File( this.dirLocationForScrShots+"/" + numbrPlateFound+".png"));

            LOGGER.trace("{} {}",  ()->{return "Successfully created ..";},
                                            ()->{return this.dirLocationForScrShots+"/" + numbrPlateFound+".png";}
                                            );

        } catch (Exception anyE) {
            LOGGER.error("Error in creating ScreenPic of Successful Find {}",() -> anyE.getMessage());

        }
        finally
        {
            LOGGER.trace(() -> { return "Finally block .... Proceeding because data wld have been captured to xls file"; });
        }
    }
}


