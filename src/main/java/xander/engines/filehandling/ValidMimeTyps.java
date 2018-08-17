package xander.engines.filehandling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * .xls 	application/excel
 * .xls 	application/vnd.ms-excel
 * .xls 	application/x-excel
 * .xls 	application/x-msexcel
 * .xlsx   application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 * .csv    text/csv
 *
 * Excel Add-in file	application/vnd.ms-excel	xla
 * Excel chart     	application/vnd.ms-excel	xlc
 * Excel macro	        application/vnd.ms-excel	xlm
 * Excel spreadsheet	application/vnd.ms-excel	xls
 * Excel template	    application/vnd.ms-excel	xlt
 * Excel worspace	    application/vnd.ms-excel	xlw
 *
 * .xls 	Microsoft Excel 	        application/vnd.ms-excel
 * .xlsx 	Microsoft Excel (OpenXML) 	application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 *
 */
public enum ValidMimeTyps {



    XLS("application/vnd.ms-excel"),CSV("text/csv"), XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ;

    private String mimeTypeValue ;
    private static final Logger LOGGER = LogManager.getLogger();

    private ValidMimeTyps(String mimeTypeValueP)
    {
        this.mimeTypeValue = mimeTypeValueP ;
    }

    public String getRealValueOfEnum()
    {
        return this.mimeTypeValue ;
    }

    public static boolean isValidType(String internalValToChk)
    {
        for (ValidMimeTyps v :ValidMimeTyps.values())
        {
            if (v.getRealValueOfEnum().equals(internalValToChk))
            {
                LOGGER.trace("Identified mimetype is" + v.name() + " " + v.ordinal() + " " + v.getRealValueOfEnum());
                return true ;
            }
        }

        return false ;

    }

    public static ValidMimeTyps getValidType(String mimeTypeValue) throws Exception
    {
        for (ValidMimeTyps v :ValidMimeTyps.values())
        {
            if (v.getRealValueOfEnum().equals(mimeTypeValue))
            {
                LOGGER.trace("Identified mimetype is" + v.name() + " " + v.ordinal() + " " + v.getRealValueOfEnum());
                return v ;
            }
        }

        throw new Exception("NOT A VALID MIME-TYPE to PROCESS for " + mimeTypeValue);

    }

    public static void main(String[] args) {
        System.out.println(ValidMimeTyps.XLSX);
        System.out.println(ValidMimeTyps.XLSX.name());
        System.out.println(ValidMimeTyps.XLSX.ordinal());
        System.out.println(ValidMimeTyps.XLSX.getRealValueOfEnum());

        for (ValidMimeTyps v :ValidMimeTyps.values())
        {
            System.out.println(v + " " + v.getRealValueOfEnum() + " " + v.name() + " " + v.ordinal());
        }
    }
}
