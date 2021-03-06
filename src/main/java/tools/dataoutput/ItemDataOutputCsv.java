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

public class ItemDataOutputCsv extends ItemDataOutput {

	private String NEW_LINE = "";
	private String SEPARATOR = "";

	public ItemDataOutputCsv() {
		super();
		
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = ItemDataOutput.class.getClassLoader().getResourceAsStream("config.dataoutput.properties");
			prop.load(is);
			super.OUTPUT_DIR = prop.getProperty("output.dir.dataoutput.csv");
			super.OUTPUT_FILE_NAME = prop.getProperty("output.filename.dataoutput.csv");
			super.ENCODING_TYPE = prop.getProperty("encodingtype.dataoutput.csv");
			super.EXTENTION = ".csv";
			NEW_LINE = prop.getProperty("newline.dataoutput.csv");
			SEPARATOR = prop.getProperty("separator.dataoutput.csv");
			
			
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
			
			for (int i=0; i<list.size(); i++) {
				Item item = list.get(i);
				bw.write(item.getItemId()+SEPARATOR+item.getItemName()+SEPARATOR+item.getItemDescription()+SEPARATOR+item.getMakerCode()+SEPARATOR+item.getPrice()+SEPARATOR+item.getSaleStatus());
				bw.write(NEW_LINE);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
	

}
