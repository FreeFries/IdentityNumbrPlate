package xander.engines.filehandling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xander.beans.IFileEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

//import static java.nio.file.Files.exists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * todo Could be adapted to follow the Fluent Design
 */
public class FileEngine implements IFileEngine {

    private static final Logger LOGGER = LogManager.getLogger();

    private String dirToProcessFullPath ;
    private List<String> fileExtensionsToChk ;
    private volatile int totalFilesExtensionsLookdAt = 0 ;


    // todo convert to an enum
    public static final String EXTENSIONS_TO_CHK = ".csv,.xls,.xlsx,.xlsm" ; // danger of missing the . <== dot but, it is flexible esp. for files which have no extension that need to be filtered


    /**
     * Expose your Constructor publicly because .... this is where you collect all the input necessary for your FileEngine
     *
     * @param p_dirToProcessFullPath
     */
    public FileEngine(String p_dirToProcessFullPath /*, String p_commaSeparatedListOfExtensions*/) throws Exception
    {
        this.dirToProcessFullPath = p_dirToProcessFullPath ;

//        if (p_commaSeparatedListOfExtensions.length() > 0)
//        {
//            this.fileExtensionsToChk = Arrays.asList(p_commaSeparatedListOfExtensions.split(","));
//        }
//        else
//        {
//            LOGGER.error("Comma separated list of Extensions not provided");
//            throw new Exception("Comma separated list of Extensions not provided");
//        }

    }

    private ColumnDescriptor getMeANormalFileAsPerMyFilter(Path path) throws Exception
    {
        ColumnDescriptor rtnValue = null ;

        if ( Files.isSymbolicLink(path) || Files.isDirectory(path,LinkOption.NOFOLLOW_LINKS) )
        {
            LOGGER.warn(path + " ignored because is a directory or file/directory is a symbolicLink");
        }
        else
        {
            if (Files.isReadable(path) && !Files.isHidden(path)) {

                String tst = path.getFileName().toString().toLowerCase();
                String mimetypeS =  Files.probeContentType(path); // fixme DOES NOT REPORT CORRECTLY AT ALL - looks at extension !!! ha! https://stackoverflow.com/questions/51438/getting-a-files-mime-type-in-java
                long sizeInBytes = Files.size(path) ;
                LOGGER.info("File=" + tst + " mimeType=" + mimetypeS + " Size=" + sizeInBytes + " bytes =========" );

                if (ValidMimeTyps.isValidType(mimetypeS) && Files.size(path) > 0L)
                {
                    try {
                        ColumnDescriptor tmpCD = new ColumnDescriptor(path.toAbsolutePath().toString(), sizeInBytes, ValidMimeTyps.getValidType(mimetypeS));
                        rtnValue = tmpCD ;
                        return rtnValue ;

                    }
                    catch (Exception notValidType)
                    {
                        LOGGER.warn(notValidType.getMessage());
                    }

                }


//requirement stipulates using mime type
//
//                this.totalFilesExtensionsLookdAt = this.totalFilesExtensionsLookdAt + 1 ;
//
//                for (String extnsn : this.fileExtensionsToChk)
//                {
//
//                    if (tst.endsWith(extnsn))
//                    {
//                        // filter condition met
//                        rtnValue = true ;
//                        LOGGER.info(tst + " SELECTED this file for further processing <~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                        return rtnValue ; // better than break
//                    }
//                }

                if (rtnValue == null)
                {
                    LOGGER.warn(path + " file does not have the correct content-type OR the size was ZERO bytes aka empty");
                }
            }
            else
            {
                LOGGER.warn(path + " not readable and cannot be hidden");
            }
        }

        if (rtnValue instanceof ColumnDescriptor)
        {
            LOGGER.error(path + " WARNING: logic error .... should NEVER be TRUE in this location for this file");
        }
        return rtnValue ;
    }

    private List<ColumnDescriptor> fileList(Path directory) {
        List<ColumnDescriptor> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                LOGGER.trace("Processing " + path);

                ColumnDescriptor cdx = this.getMeANormalFileAsPerMyFilter(path);

                if (cdx != null) {

                    LOGGER.trace("Included in our list " + path);

                    fileNames.add(cdx);
                }
            }
        }
        catch (Exception ex)
        {
            LOGGER.trace(ex); // possible IOException or any other exectipn
        }
        return fileNames;
    }

    private Path useOfRealPath(Path path)
    {
        Path tmppath = Paths.get("");
        try {
            tmppath = path.toRealPath(new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
            //LOGGER.trace("RealPath=" +tmppath);
        }
        catch (NoSuchFileException nsfe) {

            LOGGER.error(path + ": No such" + " file or directory exists ", nsfe);
            // Logic for case when file doesn't exist.
        }
        catch (IOException ioe) {
            LOGGER.error(ioe.getMessage(), ioe);
            // Logic for other sort of file error.
        }
        catch(Exception anyOtherError)
        {
            LOGGER.error(anyOtherError.getMessage());
        }
        finally
        {
            if (tmppath.toString().isEmpty())
            {
                LOGGER.error("Warning: Empty Path Rtnd for - path not accessible or not there " + path);
            }
            else
            {
                LOGGER.info("Computed RealPath as " + tmppath);
            }
            return tmppath ;
        }
    }

    /**
     * Expose your ActionMethod publicly so that your users can start the filter process and all the other checks needed to extract a List of Excel/CSV files
     *
     */
     public  List<ColumnDescriptor> processBegins()
     {
        LOGGER.info("==================== PROCESS BEGINS LIST ALL FILES IN INPUT_DATA FOLDER ==============");

        String dir = this.dirToProcessFullPath ;
        List<ColumnDescriptor> lst = new ArrayList<ColumnDescriptor>() ;
        Path path = Paths.get(dir);
        LOGGER.trace("Checking Input Directory path data as " + dir + " is made up of " + path.getNameCount() + " parts");
        LOGGER.trace("AbsolutePath " + path.toAbsolutePath());

        this.useOfRealPath(path);

        boolean pathExists = Files.exists(path);
        if (pathExists)
        {
            boolean pathIsADirectory = Files.isDirectory(path,LinkOption.NOFOLLOW_LINKS) && !Files.isSymbolicLink(path) ;
            if (pathIsADirectory)
            {
                lst = this.fileList(path);
                if (lst.size() > 0)
                {
                    LOGGER.info("Proceeding to DVLA website to check these number plates in " + lst.size() + " files out of a total of " + this.totalFilesExtensionsLookdAt + " whose extensions were examined");
                }
                else
                {
                    LOGGER.warn("There were no csv or xls files to process");
                }

            }
            else
            {
                LOGGER.warn(path.toAbsolutePath() + " exists but is NOT a directory or is a symbolic link to another directory");
            }

        }
        else
        {
            LOGGER.warn(path.toAbsolutePath() + " submitted possible directory path does not exist");
        }
        LOGGER.info("==================== PROCESS ENDS ==============");

        //this.yourFilteredSelectionOfFiles = lst ; // todo should I make a copy instead of pointing it to this internal list ?
        return lst ;
     }

    /**
     *
     * todo Possible Concurrency issue over here ? Shd it be synchronized
     * wishlist this could be a very good STREAM.MAP.LIST EXCEL + CSV use Case and streams can also be parallel
     *
     * @param p_lst
     */
     public static void listFilteredFilesFound(List<ColumnDescriptor> p_lst) //, String subFilterDelimited, String delimiterUsed)
     {
         LOGGER.info(" ================ ONLY THESE FILTERED FILES WILL BE ACTED UPON ++++++++++++");

         List<String> subFilterList = new ArrayList<String>();

         for (ColumnDescriptor t : p_lst)
         {
                 String tmp = new File(t.getFilePathName()).getName();

                 LOGGER.info(tmp + " Size=" + t.getFileSize() + " Typ=" + t.getFileType().name() + " MimeTyp=" + t.getFileType().getRealValueOfEnum() );
         }
         LOGGER.info("================================= FINISHED ++++++++++++");

     }


}
