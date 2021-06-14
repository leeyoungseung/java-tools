package tools.loadvisualization;

import java.io.IOException;

public class LoadVisualizationJobFactory {
	
	public static LoadVisualizationJob getInstance(String... args) throws IOException, NullPointerException{
		LoadVisualizationJob loadVisualizationJob = null;
		
		switch (args[0]) {
		case "vmstat":
			loadVisualizationJob = new VmstatVisualizationJob(); 
			break;

		default:
			break;
		}
		
		return loadVisualizationJob;
		
	}

}
