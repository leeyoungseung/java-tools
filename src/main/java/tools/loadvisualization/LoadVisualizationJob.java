package tools.loadvisualization;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface LoadVisualizationJob {

	boolean visualizationProcess(String... args) throws NullPointerException, IOException, ParseException;
	boolean validationParam(String... args);
	List getDataFromTargetFile(String path) throws IOException;
	
	
}
