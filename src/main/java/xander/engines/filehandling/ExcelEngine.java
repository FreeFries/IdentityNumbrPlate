package xander.engines.filehandling;



import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import xander.beans.IParserEngine;

import java.io.*;
import java.util.HashMap;

import java.util.Map;

/**
 *


 * Created by APereira JavaDeveloper,Croydon, UK
 */
public class ExcelEngine implements IParserEngine {

    private String fileXlsPath ;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FIELD_HEADINGS = "Row=1\tNUMBER_PLATE\tMAKE\tCOLOR\t" ;
    private static final String reportFile = "./result/RESULT.CSV" ;

    private ActionPerItem webDriverCode ;
    
    public ExcelEngine(String filePathP, ActionPerItem a)
    {
        this.fileXlsPath = filePathP ;
        this.webDriverCode = a ;
    }

    /**
     * Assumption that all the NUMBER PLATES ARE STORED IN SHEET 1
     * Assumption that it is a single column with the Number plates in it and the Column is the first one
     * Asumption that the 1st field in that column is the title = "NUMBER_PLATE"
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void readFile() {

        Workbook inputExcelData = null ;
        //FileOutputStream outResultExcel = null ;
        Writer reportTxt = null ;
        try {
            String onlyFileName = new File(this.fileXlsPath).getName();

            reportTxt = new BufferedWriter(new FileWriter(ExcelEngine.reportFile,true));
            String starttitle = "REPORT START for " + onlyFileName + " =======\nNUMBER_PLATE,MAKE,COLOUR\n";
            reportTxt.write(starttitle);

            LOGGER.info("Creating a Workbook from an Excel file (.xls or .xlsx) " + this.fileXlsPath);
            inputExcelData = WorkbookFactory.create(new File(this.fileXlsPath));
            // Retrieving the number of sheets in the Workbook
            LOGGER.debug("Workbook has " + inputExcelData.getNumberOfSheets() + " Sheets : ");

            // Getting the Sheet at index zero
            Sheet sheet = inputExcelData.getSheetAt(0);
            LOGGER.trace("GOT FIRST SHEET zero .....");

            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();

            // 2. Or you can use a for-each loop to iterate over the rows and columns
            LOGGER.trace("Iterating over Rows and Columns using for-each loop");

            int rowcnt = 0;
            boolean endOfRows = false;
            StringBuilder sb = new StringBuilder();
            for (Row row : sheet) {
                rowcnt = rowcnt + 1;
                sb.delete(0, sb.length());
                sb.append("Row=" + rowcnt + "\t");
                int fldcnt = 0;
                Map<String, String> result = new HashMap<>();
                for (Cell cell : row) {
                    fldcnt = fldcnt + 1;
                    if (fldcnt == 4) {
                        break;
                    }

                    String cellValue = ExcelEngine.getCellValueAsString(cell).trim();    //dataFormatter.formatCellValue(cell);

                    sb.append(/*"Fld_" + fldcnt+"=" +*/cellValue + "\t");


                    if (fldcnt == 1 && rowcnt > 1) {

                        if (cellValue == null || cellValue.isEmpty() )
                        {
                            String stuffToWrite22 = "VALID EXCEL CELL but has NULL/EMPTY/BLANKS for a number plate\n";
                            reportTxt.write(stuffToWrite22 );
                            LOGGER.warn(stuffToWrite22);
                            break ;
                        }

                        result = this.webDriverCode.doActionPerItem(cellValue); // injected lambda code for NumbrPlateFrmPage Executed here
                        if (result.get("Status").equals("Ok")) {

                            String stuffToWrite = result.get("NumberPlate") + "," + result.get("Make") + "," + result.get("Colour") ;
                            reportTxt.write(stuffToWrite);

                            LOGGER.info(stuffToWrite);

                        }
                        else
                        {
                            String stuffToWrite = cellValue + ",NUMBER_PLATE_NOT_LOCATED\n";
                            reportTxt.write(stuffToWrite);
                        }

                    } else if (fldcnt == 2 && rowcnt > 1) {
                        if (result.get("Status").equals("Ok")) {

                            if (this.TestMakeMatch(cellValue,result))
                            {
                                reportTxt.write(",MATCHES_Make");
                            }
                            else
                            {
                                reportTxt.write(",NoMatchOnMake,OurDataHadMakeAs='" + cellValue+"'");
                            }
                            //cell.setCellValue(result.get("Make"));
                        } else {
                            //cell.setCellValue("Not Found"); // Not found

                        }
                    } else if (fldcnt == 3 && rowcnt > 1) {
                        if (result.get("Status").equals("Ok")) {


                            if (this.TestColourMatch(cellValue,result))
                            {
                                reportTxt.write(",MATCHES_Colour\n");
                            }
                            else
                            {
                                reportTxt.write(" ,NoMatchOnColour,OurDataHadColourAs='" + cellValue + "'\n");
                            }


                            //cell.setCellValue(result.get("Colour"));
                        } else {
                            //cell.setCellValue("Not Found"); // Not found
                        }

                    }

                }
                LOGGER.debug(sb.toString());


                if (rowcnt == 1) {
                    if (sb.toString().equalsIgnoreCase(ExcelEngine.FIELD_HEADINGS)) {
                        // you are in a spreadsheet that is in the expected format for your input data
                    } else {
                        throw new Exception("Expected column headings should be='" + ExcelEngine.FIELD_HEADINGS + "' But Got='" + sb.toString() + "'");
                    }
                } else {
                    // continuing for further data
                }


                LOGGER.debug(rowcnt + " Row finished");
            }

            String endtitle = "REPORT end Records processed = " + (rowcnt - 1) + " =======\n"; // decrease counter coz of header
            reportTxt.write(endtitle);
            reportTxt.flush();




        }
        catch (Exception anyE)
        {

            LOGGER.error(anyE) ;
        }
        finally
        {
            try {
//                if (outResultExcel != null) {
//                    outResultExcel.close(); // todo REALLY ODD JUST BY WRITING THE SAME XLS OUT TO A ANOTHER RESULT FILE SEEMS TO UPDATE THE ORIGINAL FILE AS WELL
//                }

                if (inputExcelData != null) {
                    inputExcelData.close();
                    LOGGER.info("CLOSED " + this.fileXlsPath);
                }
            }
            catch (Exception anyE) {
                LOGGER.error("Closing issues with file   " + anyE.getMessage());
            }

            try
            {

                if (reportTxt != null) {
                    reportTxt.write("CLOSED report for " + this.fileXlsPath + "\n\n");
                    reportTxt.flush();
                    reportTxt.close(); LOGGER.info ( "CLOSED " + ExcelEngine.reportFile) ;
                }
            }
            catch (Exception anyE)
            {
                LOGGER.error("Closing issues with file   " + anyE.getMessage());
            }
        }

    }

    //@Test // this part of the application so you cannot construct a test out of it but you could use the assertions below
    private boolean TestMakeMatch(String valueFrmTstData,Map<String,String> rsultFrmWebSite)
    {

        //assertEquals("Matched",valueFrmTstData,rsultFrmWebSite.get("Make"));
        return valueFrmTstData.equals(rsultFrmWebSite.get("Make"));
    }

    //@Test // this part of the application so you cannot construct a test out of it but you could use the assertions below
    private boolean TestColourMatch(String valueFrmTstData,Map<String,String> rsultFrmWebSite)
    {
        //assertEquals("Matched",valueFrmTstData,rsultFrmWebSite.get("Colour"));
        return valueFrmTstData.equals(rsultFrmWebSite.get("Colour"));
    }



    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                return String.valueOf( cell.getBooleanCellValue() );
            case STRING:
                return String.valueOf(cell.getRichStringCellValue().getString());

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return String.valueOf(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }

            case FORMULA:
                return String.valueOf(cell.getCellFormula());

            case BLANK:
                return String.valueOf("");

            default:
                return String.valueOf("");
        }

    }

}
