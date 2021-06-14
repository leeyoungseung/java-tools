package tools;

import java.io.IOException;

import tools.dataoutput.ItemDataOutput;
import tools.dataoutput.ItemDataOutputFactory;

public class ItemDataOutputTool {

	
	private ItemDataOutputTool() {}
	
	public static ItemDataOutputTool getInstance() {
		return ItemDataOutputToolHolder.INSTANCE;
	}
	
	private static class ItemDataOutputToolHolder {
		private static final ItemDataOutputTool INSTANCE = new ItemDataOutputTool();
	}
	
	
	public static void main(String[] args) {
		ItemDataOutputTool idl = ItemDataOutputTool.getInstance();
		
		// 데이터 출력 프로그램 시작
		try {
			System.exit(idl.outputDataToolExecute(args[0]) ? 0 : 50);
		
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(101);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.exit(102);
		}
	}
	
	/**
	 * 추출데이터를 파일로 출력하는 처리의 메인
	 * 
	 * @param outputType : 출력할 파일의 형식(확장자)
	 * @return
	 * @throws IOException
	 */
	public boolean outputDataToolExecute(String outputType) throws IOException, NullPointerException{
		// (1) 출력 타입별로 다른 인스턴스를 생성한다.
		//ItemDataOutputFactory factory = new ItemDataOutputFactory();
		ItemDataOutput job = ItemDataOutputFactory.getInstance(outputType);
		
		if (job == null) {
			throw new NullPointerException();
		}
		
		// (2) 추출해야하는 데이터의 전체 건수를 확인한다.
		// (3) 'A: 데이터의 전체 건수 / B: 1회 데이터 최대 건수' 의 값만큼 아래(4~6)처리를 반복한다.
		// (4) 1회 데이터 출력 최대건수를 데이터를 DB로부터 가져온다.
		// (5) 데이터를 가공한다.
		// (6) 가공한 데이터를 지정한 경로에 출력한다.
		return job.outputDataProcess();
	}
	
}
