package tools.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * XML 데이터를 지정한 클래스(데이터를 담기위한 클래스) 형식으로 파싱, 
 * 또는  지정한 클래스에 담긴 데이터를 XML형식으로 변환해주는 유틸클래스.
 * 대용량 XML데이터를 처리하는데 있어 유리한 SAXBuilder를 사용했다.
 * 이 코드를 사용하기 위해선 build.gradle에 아래의 의존성을 추가해주어야한다.
 * // https://mvnrepository.com/artifact/org.jdom/jdom
 * compile group: 'org.jdom', name: 'jdom', version: '2.0.2'
 * 
 * @author lee-y
 *
 */
public abstract class XmlUtil {
	
	int count = 0;
	
	/**
	 * xml 데이터를 받아오는 부분 
	 * 
	 * @param target : 파싱 대상 XML
	 * @param obj    : 파싱한 데이터를 담기위한 클래스
	 * @return
	 */
	public Object parser(String target, Object obj) {
		
		SAXBuilder builder = new SAXBuilder();
		InputStream xmlInput = new ByteArrayInputStream(target.getBytes());
		
		Document doc;
		Element el;
		try {
			doc = builder.build(xmlInput, "UTF-8");
			el = doc.getRootElement();
			getObject(el, obj);
			
		} catch (JDOMException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); 
		} catch (Exception e) { e.printStackTrace(); }
		 
		return obj;
	}
	
	/**
	 * XML데이터를 파싱하기 위한 메소드.
	 * 재귀적인 방식으로 동작하도록 설계함.
	 * 
	 * @param el  : String 형식의 XML데이터를 Element형식으로 변환한것.
	 * @param obj : 파싱한 데이터를 담기위한 클래스
	 */
	public void getObject(Element el, Object obj) {
		
		count++;
		int countInFirstRoop = 0;
		List<?> childList = el.getChildren();
		Iterator<?> childItr = childList.iterator();
		
		while(childItr.hasNext()) {
			countInFirstRoop++;
			Element childEl = (Element)childItr.next();
			String elName = new String(childEl.getName());
			List<?> grandChildList = childEl.getChildren();
			Iterator<?> grandChildItr = grandChildList.iterator();
			
			if (grandChildItr.hasNext()) {
				System.out.println("("+count+") ("+countInFirstRoop+") [ "+ elName + " ] has child");
				getObject(childEl, obj);
				
			} else {
				List<?> contentList = childEl.getContent();
				Iterator<?> contentItr = contentList.iterator();
				
				if (contentItr.hasNext()) {
					Content content = (Content) contentItr.next();
					System.out.println("("+count+") ("+countInFirstRoop+") "+ elName + " : " + content.getValue());
					
					if (!setData(elName, content.getValue(), obj)) {
						System.err.println("Error ["+ elName+"], ["+content.getValue()+"]");
					}
					
				} else {
					System.out.println("("+count+") ("+countInFirstRoop+") "+elName + " : Null value");
				}
			}
			
		}
	}
	
	/**
	 * XML데이터를 파싱해서 데이터 클래스에 데이터를 담는 메소드.
	 * 
	 * @param elName : XML태그명
	 * @param val    : XML태그의 값
	 * @param model  : 파싱한 데이터를 담기위한 클래스
	 * @return
	 */
	public abstract boolean setData(String elName, String val, Object obj);
	
	
	/**
	 * 지정한 데이터 클래스에 담긴 데이터를 XML형식으로 변환해주는 기능.
	 * 
	 * @param obj  : XML로 변환하기 위한 데이터 클래스
	 * @return
	 */
	public abstract String makeXml(Object obj);
	
	
}
