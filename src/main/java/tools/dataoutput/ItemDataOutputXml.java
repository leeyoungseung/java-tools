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
import tools.utils.ItemXmlUtil;

public class ItemDataOutputXml extends ItemDataOutput {

	private ItemXmlUtil xmlUtil = new ItemXmlUtil();
	
	public ItemDataOutputXml() {
		super();
		
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = ItemDataOutput.class.getClassLoader().getResourceAsStream("config.dataoutput.properties");
			prop.load(is);
			super.OUTPUT_DIR = prop.getProperty("output.dir.dataoutput.xml");
			super.OUTPUT_FILE_NAME = prop.getProperty("output.filename.dataoutput.xml");
			super.EXTENTION = ".xml";
			super.ENCODING_TYPE = prop.getProperty("encodingtype.dataoutput.xml");
			
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

			bw.write(xmlUtil.makeXmlList(list));
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}


}
