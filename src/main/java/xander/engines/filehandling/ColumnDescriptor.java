package xander.engines.filehandling;

public class ColumnDescriptor {

    private String filePathName ;
    private long fileSize ;
    private ValidMimeTyps fileType ;

    ColumnDescriptor(String fpn, long fs, ValidMimeTyps m)
    {
        this.filePathName = fpn ;
        this.fileSize = fs ;
        this.fileType = m ;
    }

    public String getFilePathName() {
        return filePathName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public ValidMimeTyps getFileType() {
        return fileType;
    }


}
