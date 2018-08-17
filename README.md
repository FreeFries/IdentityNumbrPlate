# IdentityNumbrPlate (Id your Number Plate at Vehicle Enquiry Gov UK service) Author Alexander Pereira Croydon UK

This Java Selenium Application will take a list of Number Plate Regs from a Spreadsheet and then launch Firefox to get it verified at https://vehicleenquiry.service.gov.uk. Using the Selenium framework and Apache POI for excel spreadsheets it will extract matched car regs and store them in the spreadsheet. Those that it cannot match in terms of Colour or Make will all be reported in a results folder called "./results" into a file called RESULTS.CSV which you can pull into your spreadsheet

Make sure when you start that your data is in spreadsheets or .CSV files.

**NUMBER_PLATE	MAKE	     COLOR**
1. LT59 CNZ,	MERCEDES-BENZ,	GREY
2. LF53 KGY,	PORSCHE,    	GREY
3. AX02 AVN,	MERCEDES,   	BLUE

Suitable examples of data are already available in the ./data folder

## Features

* Produces a REPORT.CSV that tells you whether there was a Match or NoMatch for colour & make and even they are not supplied will get it from DVLA

    * LT59 CNZ	MERCEDES-BENZ	GREY	MATCHES_Make 	NoMatchOnColour	OurDataHadColourAs='MACHISMO'
    * LF53 KGY	PORSCHE	        GREY	MATCHES_Make	MATCHES_Colour
    * AX02 AVN	MERCEDES	    BLUE	MATCHES_Make 	NoMatchOnColour	OurDataHadColourAs='FUNKY BLUE'

* Uses a **Service Layer Bean for file handling FileEngine.java**  using a simple Spring Container that does this **SpringContainerDI.java**, used in **SeleniumTest.java**. This could be extended to ExcelEngine.java, CSVEngine.java which all have common interfaces a requirement for correct implementation of Spring's DI patterns. Just means that they can slip in another FileEngine.java as long as it complies with the same interface requirements.

* Scans a data folder and reports on the File Size, File Name and File MimeType in order to decide which files to use and uses Log4J output to tell the user what's happening. It shows all the files and then filters them out based on their mimetype

    * INFO  13:09 FileEngine(244) 8MakeColourMissing.csv Size=270 Typ=CSV MimeTyp=text/csv
    * INFO  13:09 FileEngine(244) 11WrongHeading.csv Size=24 Typ=CSV MimeTyp=text/csv
    * INFO  13:09 FileEngine(244) 9SampleNumbrPlate.csv Size=433 Typ=CSV MimeTyp=text/csv

* Can process a directory of CSV & Excel files numbering more than 10. See the data folder.

* Uses an Enum **ValidMimeTyps** {XLS("application/vnd.ms-excel"),CSV("text/csv"), XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")} to filter the unwanted files. Avoids folders etc. Though have to upgrade the Java7 Files.probeContentType() it does not work properly.

* Uses the Selenium Page Object Pattern for the Starting Page - aka **StartPage.java** and  **NumbrPlateFrmPage.java** which actually tries to get the make and colour for a given number plate read from CSV or Excel file.

* Build executable Jar file used in **runapp.sh** with Maven **build.sh**

* Takes PNG pictures of successful number plate finds and stores them with file name labels stating the XLS file they originate and their number plate

* File Engine does checks for Empty Xls and also for ensuring that the correct headers exist for the input data format elucidated above. Discards invalid files. And, in sample data processes both valid files.

* Runs now on Ubuntu 18.04, uses GeckoDriver0.21, uses Selenium Firefox Driver 3.14 (see runapp.sh - configured to run out of the box in this environment)

* Runs on Windows 7 (see runapp.cmd). Latest GeckoDriver.exe v0.21 dropped in there. But, have not yet tested because no access to Window's machine yet.

* Provided via MAVEN a FAT JAR (shade plugin uber jar) so everything is within a single jar

* Logging Log4J is provided - currently goes out to console. Appender for a file exists. Level set at INFO. You can see TRACE messages by changing if so desired.

* Log4J , Gecko Driver locations all passed via the JVM options command line - see runapp.cmd

* FileEngine only needs to be passed directory - it will do all the checking to see if files exist, filter them so that we only xls etc. No setters all variable data injected in constructors (DI principle maintained).

* Used ActionPerItem as a lambda code holder which contained all the "page" logic to see whether car reg was valid or invalid. Thus prinicple of code as data so I could pass this to my Excel Engine parser. Again, because I use constructors, I was able to inject this webDriver code for my excel engine to use.

* Used JunitRunner for my Junit Selenium Test Case that set up my browser @Before via Selenium and then gracefully shut down and closed @After handles like webdriver, excel etc. and wrote the results back. @Tests with assertions are available to see whether these worked but commented them out because it stops the application








