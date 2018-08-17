package xander.engines.filehandling;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import xander.beans.IParserEngine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *


 * Created by APereira JavaDeveloper,Croydon, UK
 */
public class CSVEngine implements IParserEngine {

    private String fileXlsPath ;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FIELD_HEADINGS = "NUMBER_PLATE,MAKE,COLOUR" ;
    private static final String reportFile = "./result/RESULT.CSV" ;

    private ActionPerItem webDriverCode ;

    public CSVEngine(String filePathP, ActionPerItem a)
    {
        this.fileXlsPath = filePathP ;
        this.webDriverCode = a ;
    }


    public void readFile()
    {

        Reader in =null;
        Writer out=null ;

        try {
            Map<String,String> result ;

            String onlyFileName = new File(this.fileXlsPath).getName();

            String starttitle = "REPORT START for " + onlyFileName + " =======\n" + CSVEngine.FIELD_HEADINGS  + "\n";
            out = new BufferedWriter(new FileWriter(CSVEngine.reportFile,true));
            out.write(starttitle);
            out.flush();
            LOGGER.info("Processing " + this.fileXlsPath);

            in = new FileReader( this.fileXlsPath);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("NUMBER_PLATE","MAKE","COLOUR").parse(in);
            int cnt = 0 ;

            for (CSVRecord record : records) {
                String id = record.get("NUMBER_PLATE").trim();
                String make = record.get("MAKE").trim();
                String colour = record.get("COLOUR").trim();
                LOGGER.info(id);
                cnt = cnt + 1 ;

                if (cnt > 1)  /* skip the header */
                {
                    result = this.webDriverCode.doActionPerItem(id); // injected lambda code for NumbrPlateFrmPage Executed here

                    String stuffToWrite = "";

                    if (result.get("Status").equals("Ok")) {

                        stuffToWrite = result.get("NumberPlate") + "," + result.get("Make") + "," + result.get("Colour") ;
                        out.write(stuffToWrite);
                        LOGGER.info(stuffToWrite);

                        if (make.equals(result.get("Make")))
                        {
                            out.write(",MATCHES_Make");
                        }
                        else
                        {
                            out.write(",NoMatchOnMake,OurDataHadMakeAs='" + make + "'");
                        }

                        if (colour.equals(result.get("Colour")))
                        {
                            out.write(",MATCHES_Colour\n");
                        }
                        else
                        {
                            out.write(" ,NoMatchOnColour,OurDataHadColourAs='" + colour + "'\n");
                        }

                    } else {

                        stuffToWrite = id +  ",NUMBER_PLATE_NOT_LOCATED\n";
                        out.write(stuffToWrite);

                    }
                }
            }
            String endtitle = "REPORT end Records processed = " + (cnt - 1) + " =======\n"; // decrease counter coz of header
            out.write(endtitle);
            out.flush();
        }
        catch(FileNotFoundException fnfe)
        {
            LOGGER.error(fnfe.getMessage());
        }
        catch (IOException ioe)
        {
            LOGGER.error(ioe.getMessage());
        }
        catch (Exception anyE)
        {
            LOGGER.error(anyE.getMessage());
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                    LOGGER.info("CLOSED " + this.fileXlsPath);
                }
            }
            catch (Exception anyE)
            {
                LOGGER.error("CLOSING ISSUES of devices " + this.fileXlsPath + " " + anyE.getMessage());
            }

            try {
                if (out != null) {
                    out.write("Closed report for " + this.fileXlsPath + "\n\n");
                    out.flush();
                    out.close(); LOGGER.info("CLOSED " + CSVEngine.reportFile);
                }
            }
            catch(Exception ioe)
            {
                LOGGER.error("CLOSING ISSUES of devices " + CSVEngine.reportFile + " " + ioe.getMessage());
            }


        }
    }
}
