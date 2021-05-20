package tools.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON 데이터를 지정한 클래스(데이터를 담기위한 클래스) 형식으로 파싱, 
 * 또는  지정한 클래스에 담긴 데이터를 JSON형식으로 변환해주는 유틸클래스.
 * 대용량 JSON데이터를 처리하는데 있어 유리한 jackson-databind를 사용했다.
 * 이 코드를 사용하기 위해선 build.gradle에 아래의 의존성을 추가해주어야한다.
 * // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
 * compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.11.2'
 * 
 * @author lee-y
 *
 */
public class JsonUtil {

	public ObjectMapper mapper = new ObjectMapper();
	
	private JsonUtil() {};
	
	public static JsonUtil getInstance() {
		return JsonUtilHolder.INSTANCE;
	}
	
	private static class JsonUtilHolder {
		private static final JsonUtil INSTANCE = new JsonUtil();
	}
	
	/**
	 * Json 데이터를 파싱하기 위한 메소드.
	 * 
	 * @param json     : 파싱할 Json데이터
	 * @param template : 파싱한 데이터를 담기위한 클래스
	 * @return
	 */
	public Object makeObjFromJson(String json, Class<?> template) {
		if (json == null) return null;
		
		Object res = null;
		
		try {
		    res = mapper.readValue(json, template);
		} catch (JsonProcessingException e) { e.printStackTrace(); }
		
		return res;
	}
	
	/**
	 * 지정한 데이터 클래스에 담긴 데이터를 Json형식의 문자열 데이터로 변환해주는 기능.
	 * 
	 * @param obj : : Json으로 변환하기 위한 데이터 클래스
	 * @return : Json 문자열을 리턴한다.
	 */
	public String makeJsonFromObj(Object obj) {
		if (obj == null) return null;
		
		String res = null;
		
		try {
			res =  mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) { e.printStackTrace(); }
		
		return res;
	}
	
	
	/**
	 * 지정한 데이터 클래스에 담긴 데이터를 Json형식의 byte[]데이터로 변환해주는 기능.
	 * 
	 * @param obj : : Json으로 변환하기 위한 데이터 클래스
	 * @return : byte[]를 리턴한다.
	 */
	public byte [] makeJsonByteFromObj(Object obj) {
		if (obj == null) return null;
		
		byte [] res = null;
		
		try {
			res =  mapper.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) { e.printStackTrace(); }
		
		return res;
	}
	
	/**
	 * 지정한 데이터 클래스에 담긴 데이터를 Json형식의 File로 변환해주는 기능.
	 * 
	 * @param obj : : Json으로 변환하기 위한 데이터 클래스
	 */
	public void makeJsonByteFromObj(Object obj, String filePath) {
		if (obj == null || filePath == null || filePath.equals("")) return ;
		
		File f = null;
		
		try {
			f = new File(filePath);
			mapper.writeValue(f, obj);
			
		} catch (IOException ie) { ie.printStackTrace(); }
		
	}
	
}
