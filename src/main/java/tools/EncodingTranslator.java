package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.mozilla.universalchardet.UniversalDetector;

public class EncodingTranslator {
	
	private String OUTPUT_DIR = "";
	private String NEW_LINE = "";
	private String SEPARATOR = "";
	private String REGEX_EXT_FORMAT = "";
	
	private EncodingTranslator() {}
	
	public static EncodingTranslator getInstance() {
		return EncodingTranslatorHolder.INSTANCE;
	}
	
	private static class EncodingTranslatorHolder {
		private static final EncodingTranslator INSTANCE = new EncodingTranslator();
	}
	

	public static void main(String[] args) {
		EncodingTranslator et = EncodingTranslator.getInstance();

		// 인코딩 변환기 프로그램 시작
		try {
			et.encodingTranslatorExecute(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(101);
		}
		
		System.exit(0);
	}

	/**
	 * 파일 인코딩 프로그램 실행옵션 분기
	 * @param args
	 * @return boolean
	 */
	public boolean encodingTranslatorExecute(String ...args) throws IOException {
		boolean result = false;
		Properties prop = new Properties();
		InputStream is = Calculator.class.getClassLoader().getResourceAsStream("config.properties");
		prop.load(is);
		OUTPUT_DIR = prop.getProperty("output.dir.encodingtranslator");
		NEW_LINE = prop.getProperty("newline.encodingtranslator");
		SEPARATOR = prop.getProperty("separator.encodingtranslator");
		REGEX_EXT_FORMAT = prop.getProperty("regex.ext.format.encodingtranslator");
		
		String executeType = "";
		String target = "";
		String newEncodingType = "";
		
		try {
			executeType = args[0];
			target = args[1];
			newEncodingType = args[2];
		} catch (ArrayIndexOutOfBoundsException aoe) {
			System.out.println("Somthing Parameter is null");
		}
		
		if (isNullOrEmpty(executeType) || isNullOrEmpty(target)) {
			System.err.println("Required Parameter is null");
			return false;
		}
		
		switch (executeType) {
		case "-f":
			result = encodingTranslatorFileMain(target, newEncodingType);
			break;
		case "-l":
			result = encodingTranslatorFileListMain(target, newEncodingType);
			break;
		case "-d":
			result = encodingTranslatorFilesFromDir(target, newEncodingType);
			break;
		default:
			System.out.println("Inputed parameter $1 invalid. Please Input prameter $1 Ex) something like -f, -l, -d");
		}
		
		return result;
	}
	
	/**
	 * 지정한 디렉토리안의 파일들의 인코딩을 일괄 변환한다.
	 * 
	 * @param dirPath  : 변환할 파일들이 들어있는 디렉토리 
	 * @param encoding : 변환할 인코딩
	 * @return boolean
	 */
	public boolean encodingTranslatorFilesFromDir(String dirPath, String encoding) {
		File [] files;
		
		try {
			File dir = new File(dirPath);
			
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(REGEX_EXT_FORMAT);
                }
            };
			
			files = dir.listFiles(filter);
            
			for (File f : files) {
				if(encodingTranslatorFileMain(f.getAbsolutePath(), encoding)) {
					System.out.println(f+" translator Success!!");
				} else {
					System.out.println(f+" translator Failure!!");
				}
			}
			
			return true;
			
		} catch (Exception e) { e.printStackTrace(); }

		return false;
	}
	

	/**
	 * 파일 리스트 (.csv)에 기재된 파일의 인코딩을 변환한다.
	 * @param filePath : 파일 리스트(.csv) 경로
	 *  .csv[0] : 파일의 절대경로
	 *  .csv[1] : 변환할 인코딩 
	 * @param encoding : 변환할 인코딩
	 * @return
	 */
	public boolean encodingTranslatorFileListMain(String filePath, String encoding) {
		File fileList = new File(filePath);
		
		if (fileList.isFile()) {	
			try (BufferedReader br = Files.newBufferedReader(Paths.get(fileList.getAbsolutePath()))) {
				String line = "";
				while((line = br.readLine()) != null ) {
					String [] target = makeArrayFromStr(line);
					String newEncoding = "";
					try {
						newEncoding = target[1];
						
					} catch (Exception e) { newEncoding = null; }
					
					if(encodingTranslatorFileMain(target[0], newEncoding)) {
						System.out.println(target[0]+" translator Success!!");
					} else {
						System.out.println(target[0]+" translator Failure!!");
					}
				}
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
				
			return true;
		} 

		return false;
	}
	
	/**
	 * 파일 인코딩 변환프로그램 메인처리 
	 * @param filePath : 대상 파일
	 * @param encoding : 변환 인코딩
	 */
	public boolean encodingTranslatorFileMain(String filePath, String encoding) {
		String targetFilePath = filePath;
		String newEncodingType = encoding;
		String nowEncodingType = "";
		
		// null 체크
		if (isNullOrEmpty(targetFilePath)) {
			System.out.println("Please make sure Input parameter $2. Maybe String inputed is null.");
			System.exit(102);
		}
		
		// 인코딩이 지정되지 않으면 기본 인코딩으로 UTF-8지정
		if (isNullOrEmpty(newEncodingType)) {
			System.out.println("Set Default encoding UTF-8");
			newEncodingType = "UTF-8";
		}
		
		File file = new File(targetFilePath);
		try {
			// 파일의 현재 인코딩확인
			nowEncodingType = getNowEncoding(file);
			System.out.println("Encoding Type Currently : ["+nowEncodingType+"]");
			
			// 파일의 인코딩 변환
			return encodingTranslatorFile(file, nowEncodingType, newEncodingType);
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(103);
		}

		return false;
	}
	
	/**
	 * 파일을 지정한 타입의 인코딩으로 변환하여 출력한다.
	 * 원본파일은 그대로 보존한다.
	 * @param f : 변환 대상 파일
	 * @param nowEncodingType : 파일의 현재 인코딩
	 * @param newEncodingType : 변환하려는 인코딩 
	 * @return
	 * @throws IOException
	 */
	public boolean encodingTranslatorFile(File f, String nowEncodingType, String newEncodingType) throws IOException {
		System.out.println("encodingTranslator "+f.getName()+" ["+nowEncodingType+"] -> ["+newEncodingType+"]");

		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String fileName = f.getName();
		int pos = fileName.lastIndexOf(".");
		String extension = fileName.substring(pos + 1);
		String fileNameWithoutExtension = fileName.substring(0, pos);
		String outputFilePath = OUTPUT_DIR+"/"+fileNameWithoutExtension+"_"+newEncodingType+"_"+dateFormat.format(date)+"."+extension;

		if (f.isFile()) {	
			try (BufferedReader br = Files.newBufferedReader(Paths.get(f.getAbsolutePath()), Charset.forName(nowEncodingType))) {
				String line = "";
		        
				try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFilePath), Charset.forName(newEncodingType),
						StandardOpenOption.CREATE_NEW)) {
					
					while((line = br.readLine()) != null ) {
						String translatedStr = translatorStr(line, nowEncodingType, newEncodingType);
						//System.out.println( "    ->"+translatedStr );
						bw.write(translatedStr);
						bw.write(NEW_LINE);
					}
				
				}
				
				return true;
			} 
			
		}
		return false;
		
	}
	
	
	/**
	 * 파일의 현재 인코딩을 확인
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public String getNowEncoding(File f) throws IOException {
		String encoding = "";
		byte[] buf = new byte[4096];
		UniversalDetector detector = null;
		
		if (f.exists()) {
			try (FileInputStream is = new FileInputStream(f)) {
			    detector = new UniversalDetector(null);
			
			    int read;
			
				while ( (read = is.read(buf)) > 0 && !detector.isDone() ) {
					//System.out.println(read);
					detector.handleData(buf, 0, read);
				}
				
				detector.dataEnd();
				encoding = detector.getDetectedCharset();
				return encoding;		
			} 
		} 
		
		return encoding;
	}
	
	/**
	 * 문자열의 인코딩을 지정한 방식으로 변환한다.
	 * @param target : 인코딩을 변환하려는 문자열
	 * @param oldEncodingType : 현재의 인코딩 타입
	 * @param newEncodingType : 변환할 인코딩 타입
	 * @return
	 */
	public String translatorStr(String target, String oldEncodingType, String newEncodingType) {
		String originTypeStr = decodeStr(target, oldEncodingType);
		return decodeStr(originTypeStr, newEncodingType);
	}
	
	
	/**
	 * 문자열을 현재 인코딩되어있는 타입으로 디코딩하여 byte배열로 만든 후 byte 배열을 문자열로 변환하여 
	 * 변환된 문자열을 리턴한다. 
	 * @param target
	 * @param type
	 * @return
	 */
	public String decodeStr(String target, String type) {
		String decodedStr = null;
		try {
			decodedStr = new String(target.getBytes(type), type);
			
		} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		
		return decodedStr;
	}
	
	
	/**
	 * Null과 공백 확인
	 * @param str 확인할 문자열
	 * @return Null 또는 공백이면 true를 리턴한다.
	 */
	public boolean isNullOrEmpty(String str) {
		return (str == null || str.equals(""));
	}
	
	
	/**
	 * 구분자를 기준으로 입력한 문자열을 자른 문자열의 배열을 리턴하는 메소드
	 * This method return an Array of String that split String entered based on separator.
	 * @param targetStr
	 * @return
	 */
	public String [] makeArrayFromStr(String targetStr) {
		if (isNullOrEmpty(targetStr)) return null;
		
		String [] res = targetStr.split(SEPARATOR);
		
		if (res != null && 0 < res.length)  return res;
		else return null;
	}
}
