package tools;


import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import utils.Config;

public class Calculator {

	public static final String REGEX_ALL = "^[0-9|(|)|\\-|+|*|/]*$";
	public static final String REGEX_OPEN = "^[0-9|(]*$";
	public static final String REGEX_CLOSE = "^[0-9|)]*$";
	public static final String JS_ENGINE = Config.getProperties("js.engine");
	
	public static void main(String[] args) {
		// 입력된 식 확인.
		String inputStr = args[0];
		System.out.println("Input Value : "+ inputStr);
		
		if (isNullOrEmpty(inputStr)) {
			System.out.println("Please make sure Input value. Maybe input value is null.");
			System.exit(101);
		}
		
		// 공백제거 
		inputStr = inputStr.replaceAll(" ", "");
		
		// 유효성 확인 
		if (!validation(inputStr)) {
			System.out.println("Please make sure Input value. Not suitable calculation formula.");
			System.exit(102);
		}

		System.out.println(calc(inputStr));
		
	}
	
	
	/**
	 * ScriptEngine을 사용하여 계산식을 처리한다.
	 * @param inputFormula : 계산식
	 * @return
	 */
	public static String calc(String inputFormula) {

		String result = "";
		ScriptEngineManager sem = new ScriptEngineManager();
		System.out.println("Process Engine : "+JS_ENGINE);
		ScriptEngine en = sem.getEngineByName(JS_ENGINE);

		try {
			result = en.eval(inputFormula).toString();
		} catch (ScriptException e) {  e.printStackTrace(); }
		
        return result;		
	}
	
	
	/**
	 * Null과 공백 확인
	 * @param str 확인할 문자열
	 * @return Null 또는 공백이면 true를 리턴한다.
	 */
	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.equals(""));
	}
	
	
	/**
	 * 입력 문자열이 정규표현식과 일치하는지 확인
	 * @param contion 정규표현식
	 * @param target 확인할 문자열
	 * @return 일치하면 true를 리턴한다.
	 */
	public static boolean isMatch(String contion, String target) {
		return Pattern.compile(contion).matcher(target).matches();
	}
	
	/**
	 * 입력한 식의 유효성을 확인한다.
	 * @param str
	 * @return
	 */
	public static boolean validation(String str) {
		
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
