package practice;

public class P02_Operator {

	public static void main(String[] args) {
		int a = 100;
		int b = 10;
		int c = 5;
		
		// ��Ģ����
		System.out.println(a+b);
		System.out.println(a-b);
		System.out.println(a*b);
		System.out.println(a/b);
		System.out.println(a%b);
		
		// ���迬�� 
		System.out.println(a<b);
		System.out.println(a<=b);
		System.out.println(a>b);
		System.out.println(a>=b);
		System.out.println(a==b);
		System.out.println(a!=b);
		
		// ������ 
		System.out.println((a>b) && (b>c));
		System.out.println((a<b) || (b>c));
		System.out.println(!(a!=b));
	}
}
