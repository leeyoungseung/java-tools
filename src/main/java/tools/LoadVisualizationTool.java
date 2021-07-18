package tools;

import java.io.IOException;
import java.text.ParseException;

import tools.loadvisualization.LoadVisualizationJob;
import tools.loadvisualization.LoadVisualizationJobFactory;
import tools.loadvisualization.exception.LoadVisualizationRuntimeException;

public class LoadVisualizationTool {
	
	private LoadVisualizationTool() {}
	
	public static LoadVisualizationTool getInstance() {
		return LoadVisualizationToolHolder.INSTANCE;
	}
	
	private static class LoadVisualizationToolHolder {
		private static final LoadVisualizationTool INSTANCE = new LoadVisualizationTool();
	}
	

	public static void main(String[] args) {
		LoadVisualizationTool vt = LoadVisualizationTool.getInstance();
		
		
		// 시각화툴 시작
		try {
			System.out.println(args[0]);
			System.out.println(args[1]);
			System.exit(vt.visualizationToolExecute(args) ? 0 : 50);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(101);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.exit(102);
		} catch (LoadVisualizationRuntimeException e) {
			e.printStackTrace();
			System.exit(103);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(104);
		}
		
		
	}

	/**
	 * 커맨드 결과시각화 프로그램의 처리 메인
	 * @param args
	 * @return
	 * @throws ParseException 
	 */
	public boolean visualizationToolExecute(String[] args) throws IOException, NullPointerException, ParseException, LoadVisualizationRuntimeException {
		LoadVisualizationJob job = LoadVisualizationJobFactory.getInstance(args[0]);
		
		if (job == null ) {
			throw new NullPointerException();
		}
		
		return job.visualizationProcess(args);
	}
	
	
}
