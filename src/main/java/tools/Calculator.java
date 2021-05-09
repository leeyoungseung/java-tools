package tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class Calculator {

	public static final String REGEX_ALL = "^[0-9|(|)|\\-|+|*|/]*$";
	public static final String REGEX_OPEN = "^[0-9|(]*$";
	public static final String REGEX_CLOSE = "^[0-9|)]*$";
	
	private Calculator() {} 
	
	public static Calculator getInstance() {
		return CalculatorHolder.INSTANCE;
	}
	
	private static class CalculatorHolder {
		private static final Calculator INSTANCE = new Calculator();
	}
	
	public static void main(String[] args) {

		//Calculator calc = new Calculator();
		Calculator calc = Calculator.getInstance();
		
		// 계산기 프로그램 시작
		try {
			calc.calculator();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(101);
		}
		System.exit(0);
	}
	
	
	/**
	 * 계산기 프로그램 메인 로직
	 * @throws IOException
	 */
	public void calculator () throws IOException {
		Properties prop = new Properties();
		InputStream is = null;
		String calcFilePath = "";
		String jsEngine = "";
		try {
			is = Calculator.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(is);
			
			calcFilePath = prop.getProperty("calc.file.path");
			jsEngine =  prop.getProperty("js.engine");
			System.out.println(calcFilePath);
			System.out.println(jsEngine);
		} catch (Exception e) {
			System.out.println("Properties load fail!!");
		}
		
		String inputStr="";
		boolean isSaveMode = false;
		Scanner sc = new Scanner(System.in);
		
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
		Path path = Paths.get(calcFilePath+"/calc-log_"+dateFormat.format(date)+".txt");
		
		System.out.println("Please input calculation formula. (Ex) 10+20, (5+2)*7+2/1 etc...");
		System.out.println("If you want to save your calculation log, input save.");
		System.out.println("If you don't want to save your calculation log, input clear.");
		System.out.println("If you want to finish your calculation work, Please input exit.");
		
		while (true) {
			// 사용자에게 값을 입력받는다.
			inputStr = sc.nextLine();
			
			// null과 공백 체크
			if (isNullOrEmpty(inputStr)) {
				System.out.println("Please make sure Input value. Maybe input value is null.");
				continue;
			}
			
			// 공백제거 
			inputStr = inputStr.replaceAll(" ", "");
			
			// 종료, 계산로그의 저장여부 확인
			if (inputStr.equals("exit")) {
				System.out.println("To terminate Calculator");
				break;
			
			} else if (inputStr.equals("save")) {
				isSaveMode = true;
				System.out.println("Output mode Change to save, Please input calculation formula. (Ex) 10+20, (5+2)*7+2/1 etc...");
				continue;

			} else if (inputStr.equals("clear")) {
				isSaveMode = false;
				System.out.println("Output mode Change to clear, Please input calculation formula. (Ex) 10+20, (5+2)*7+2/1 etc...");
				continue;
			}
			
			// 유효성 확인 
			if (!validation(inputStr)) {
				System.out.println("Please make sure Input value. Not suitable calculation formula.");
				continue;
			}
	
			// 계산 후 표준 출력
			String calcResult = calcProcess(inputStr, jsEngine);
			System.out.println(calcResult);
			
			// 파일에 계산 결과 저장
			if (isSaveMode) {
				if (!Files.exists(path)){
				    System.out.println("Not exist File.");
				    Files.createFile(path);
				}
				
				String writeStr = inputStr+"="+calcResult+"\n";
				Files.write(path, writeStr.getBytes(), StandardOpenOption.APPEND);
			}
		}
	}
	
	
	/**
	 * ScriptEngine을 사용하여 계산식을 처리한다.
	 * @param inputFormula : 계산식
	 * @return
	 */
	public String calcProcess(String inputFormula, String jsEngine) {

		String result = "";
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine en = sem.getEngineByName(jsEngine);

		try {
			result = en.eval(inputFormula).toString();
		} catch (ScriptException e) {  
			System.out.println("Please make sure Input value. Not suitable calculation formula.");
			e.printStackTrace();
		}
		
        return result;		
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
	 * 입력 문자열이 정규표현식과 일치하는지 확인
	 * @param contion 정규표현식
	 * @param target 확인할 문자열
	 * @return 일치하면 true를 리턴한다.
	 */
	public boolean isMatch(String contion, String target) {
		return Pattern.compile(contion).matcher(target).matches();
	}
	
	
	/**
	 * 입력한 식의 유효성을 확인한다.
	 * @param str
	 * @return
	 */
	public boolean validation(String str) {
		// Pattern check
		if ( !isMatch(REGEX_ALL, str) ) {
			System.out.println("Pattern check Fail");
			return false;
		}
		
		// 식의 시작과 끝 확인 
		char [] transChars = str.toCharArray();
		
		// 식의 시작은 숫자 또는 여는 괄호여야한다. 
		if ( !isMatch(REGEX_OPEN, (""+transChars[0])) ) {
			System.out.println("REGEX_OPEN check Fail");
			return false;
		}
		
		// 식의 마지막은 숫자 또는 닫는 괄호여야한다.
		if ( !isMatch(REGEX_CLOSE, (""+transChars[(transChars.length-1)])) ) {
			System.out.println("REGEX_CLOSE check Fail");
			return false;
		}
		
		return true;
	}

}