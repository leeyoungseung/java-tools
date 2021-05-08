package practice;

public class P02_Operator {

	public static void main(String[] args) {
		int a = 100;
		int b = 10;
		int c = 5;
		
		// 사칙연산
		System.out.println(a+b);
		System.out.println(a-b);
		System.out.println(a*b);
		System.out.println(a/b);
		System.out.println(a%b);
		
		// 관계연산 
		System.out.println(a<b);
		System.out.println(a<=b);
		System.out.println(a>b);
		System.out.println(a>=b);
		System.out.println(a==b);
		System.out.println(a!=b);
		
		// 논리연산 
		System.out.println((a>b) && (b>c));
		System.out.println((a<b) || (b>c));
		System.out.println(!(a!=b));
	}
}
