package xander.beans;

import xander.engines.filehandling.ColumnDescriptor;

import java.util.List;

public interface IFileEngine {
    List<ColumnDescriptor> processBegins();
}
