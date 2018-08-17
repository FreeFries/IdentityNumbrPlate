package xander.run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import xander.beans.SpringContainerDI;
import xander.engines.filehandling.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by APereira JavaDeveloper,Croydon, UK
 *
 * Effectively a JUnit annotated class which you can use your IDE or JUnitRunner to launch
 */
public class SeleniumTest
{
    private static final Logger LOGGER = LogManager.getLogger();
    private List<ColumnDescriptor> listOfExcelCSVfiles ;

    private WebDriver webDriverHolder ;
    private JavascriptExecutor webDriverJSExec ;
    private StartPage startPage ;

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringContainerDI.class);


    @Before
    public void setupSelenium() throws Exception {

        System.out.println("ENSURE THAT ALL YOUR DATA FILES csv,xls,xlsx & RESULT.csv are CLOSED");
        System.out.println("PRESS ENTER Key to Continue - ctrl+C to abort");
        System.in.read();

        FileEngine beanFE = this.context.getBean(FileEngine.class);
        List<ColumnDescriptor> tmp4 = beanFE.processBegins();
        beanFE.listFilteredFilesFound(tmp4);

        this.listOfExcelCSVfiles = tmp4 ;

        System.out.println("Launching Selenium Firefox to start manipulating website");
        System.out.println("PRESS ENTER Key to Continue - ctrl+C to abort");
        System.in.read();


        this.webDriverHolder = new FirefoxDriver(); // took 15 long seconds to do that

        this.webDriverHolder.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        this.webDriverJSExec = (JavascriptExecutor)this.webDriverHolder ;

        // Open the start page
        // this.webDriverHolder.get("https://vehicleenquiry.service.gov.uk/"); // took 5 seconds to do that on my machine

        this.startPage = new StartPage(this.webDriverHolder,"https://vehicleenquiry.service.gov.uk/","Enter the registration number of the vehicle");
        if (!this.startPage.openPageUrl().validatePageReached())
        {
            Exception faildToLoadStartPage = new Exception("Unable to Load START Page for VehicleEnquiry Service GOV Uk");
            LOGGER.error("faildToLoadStartPage",faildToLoadStartPage);
            throw faildToLoadStartPage ;
        }
        else
        {
            LOGGER.trace("Start Page landed and validated - going to next step");
        }


    }

    @After
    public void closeSelenium() {

        LOGGER.traceEntry();
        if (webDriverHolder != null) {
            webDriverHolder.close();
            LOGGER.info("Closed Browser Window");
        }
        else
        {
            LOGGER.warn("webDriverHolder was NULL - CLOSE not acted upon");
        }


        if (webDriverHolder != null) {
            //webDriverHolder.quit();
            LOGGER.info("Blockd .quit() for now"); //  //Closed all Browser Windows to Exit Firefox calling .Dispose() internally
        }
        else
        {
            LOGGER.warn("webDriverHolder was NULL - .quit() not acted upon");
        }

    }

    @Test
    public void runTest() throws Exception {


        /**
         *  1. ActionPerItem is the lambda web page becoz implements functionalInterface that you can use to extract the Make + Colour
         *  2. Then the results are stored in a Map
         *  3. Because ActionPerItem is INJECTED into the constructor of the ExcelEngine Parser
         *  4. The Map Results can then be handed over to Apache POI within the Engine to be written to the spread sheet
         */


        for (ColumnDescriptor excelCsvFile : this.listOfExcelCSVfiles)
        {

            try {
                ActionPerItem apiCode =  new NumbrPlateFrmPage(this.webDriverHolder,this.startPage,"./result",excelCsvFile.getFilePathName());

                if (excelCsvFile.getFileType() == ValidMimeTyps.XLS || excelCsvFile.getFileType() == ValidMimeTyps.XLSX )
                {
                    ExcelEngine parser = new ExcelEngine(excelCsvFile.getFilePathName(),apiCode); // <====== Code as Data aka Lambda Injected over here
                    parser.readFile();

                }
                else if (excelCsvFile.getFileType() == ValidMimeTyps.CSV)
                {
                    CSVEngine parser = new CSVEngine(excelCsvFile.getFilePathName(),apiCode); // <====== Code as Data aka Lambda Injected over here
                    parser.readFile();

                }
                else
                {
                    LOGGER.error("!!!!!!!!!!!!!!!! UNCATERED TYPE OF FILE Encountered " + excelCsvFile.getFileType().getRealValueOfEnum());
                }

            }
            catch ( Exception fileE)
            {
                LOGGER.error("Problem reading file " + excelCsvFile, fileE);
            }
            finally
            {
                LOGGER.info("Finally block - shd proceed to next file in list or should come out ...");
            }
        }
        LOGGER.info("Finished processing all files in your data folder");


        //this.closeSelenium(); // not sure why the @After clause is not called hence hardcoded it over here


    }

    /**
     * Ensure these parameters are sent thru - so that you have logging
     *
     * -Dlog4j2.debug
     * -Dlog4jDefaultStatusLevelDONTUSE
     * -Dlog4j.configurationFile=./contacts/config/log4j2.xml
     * -Dwebdriver.gecko.driver=./gecko/geckodriver.exe
     *
     * See runapp.cmd
     *
     * @param args
     */
    public static void main(String[] args) throws Exception
    {

        Result result = org.junit.runner.JUnitCore.runClasses(SeleniumTest.class);

        // todo .getRunTime() could be helpful
        for (Failure failure : result.getFailures()) {
            LOGGER.info("failure.getMessage()=" +failure.getMessage() + "!" + "failure.getTestHeader()="+failure.getTestHeader());
        }
    }
}

