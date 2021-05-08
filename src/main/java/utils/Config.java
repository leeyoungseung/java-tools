package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 이 클래스는 테스트에 사용되는 결과 값, 설정값을 Property파일에서 불러와 저장하는 클래스로
 * 테스트 케이스에서 필요시 저장된 값을 불러와 사용 할 수 있다.
 * It is Class that save value of result and setting from Property File.
 * When is needed value, Can use value saved.
 * @author Lee
 */
public class Config {

	//Property파일의 내용을 다루는 객체
	private Properties properties = new Properties();
	
	//생성자; 싱글튼 객체를 생성하기위해 접근제한자를 private로 설정함
	private Config(File file){
		try{ // 지정된 값이 없을 경우 Resource폴더로 지정된 config.properties파일의 내용을 불러온다
			if(file==null){
				String filename = Config.class.getClassLoader().getResource("config.properties").getPath();
				file = new File(filename);
			}
			// 파일의 내용을 불러와 properties 객체에 담기
			InputStream inputStream = new FileInputStream(file);
			properties.load(inputStream);
			inputStream.close();
			
			// config 파일을 변수단위 로 구별하여 저장하는 로직 
			String [] files = file.getParentFile().list();
			for(String f : file.getParentFile().list()){
				File fi = new File(file.getParent()+"\\"+f);
				int lastDotPosition = fi.getName().lastIndexOf(".");
				if(lastDotPosition != 1){
					if(fi.getName().substring(lastDotPosition + 1).contains("properties")){
						InputStream ifs = new FileInputStream(fi);
						properties.load(ifs);
						ifs.close();
					}
				}
			}
		}catch(IOException e){e.printStackTrace();}
	}
	
	/**
	 * config파일에 저장된 id값에 해당하는 값을 리턴한다. 
	 * @param id : config파일에 저장된 변수
	 * @return 변수에 해당하는 값을 String값으로 리턴한다.
	 */
	public String getProperty(String id){
		return properties.getProperty(id);
	}
	
	/**
	 * 싱글튼 패턴으로 Config Class의 객체를 생성
	 */
	public static final Config config = new Config(null);
	
	/**
	 * Properties객체에 저장된 id값에 해당하는 값을 리턴한다. 
	 * @param id : config파일에 저장된 변수
	 * @return 변수에 해당하는 값을 String값으로 리턴한다.
	 */
	public static String getProperties(String id){
		return config.getProperty(id);
	}
	
}