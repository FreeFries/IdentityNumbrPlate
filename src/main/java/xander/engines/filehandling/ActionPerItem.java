package xander.engines.filehandling;



import java.util.Map;

@FunctionalInterface
public interface ActionPerItem {

    Map<String,String> doActionPerItem(String numbrPlateP) ;

}
