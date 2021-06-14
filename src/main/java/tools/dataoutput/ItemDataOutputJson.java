package tools.dataoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

import tools.models.Item;
import tools.utils.JsonUtil;

public class ItemDataOutputJson extends ItemDataOutput {
	
	private JsonUtil jsonUtil = JsonUtil.getInstance();
	
	public ItemDataOutputJson() {
		super();
		
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = ItemDataOutput.class.getClassLoader().getResourceAsStream("config.dataoutput.properties");
			prop.load(is);
			super.OUTPUT_DIR = prop.getProperty("output.dir.dataoutput.json");
			super.OUTPUT_FILE_NAME = prop.getProperty("output.filename.dataoutput.json");
			super.EXTENTION = ".json";
			super.ENCODING_TYPE = prop.getProperty("encodingtype.dataoutput.json");
			
		} catch (Exception e) {
			System.out.println("Properties load fail!!");
		}
	}
	
	
	@Override
	public void outputDataToFile(List<Item> list, int processNum) {
		String outputFilePath = makeFileName(processNum);
		
		try (BufferedWriter bw = Files.newBufferedWriter(
				Paths.get(outputFilePath), 
				Charset.forName(ENCODING_TYPE),
				StandardOpenOption.CREATE_NEW)) {
			
			StringBuffer sb = new StringBuffer();
			int size = list.size();
			
			sb.append("[");
			for (int i=0; i < size; i++) {
				Item item = list.get(i);
				sb.append(jsonUtil.makeJsonFromObj(item));
				sb.append( (i == size-1) ? "" : ",");
			}
			sb.append("]");
			
			bw.write(sb.toString());
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
	


}
