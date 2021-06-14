package tools.utils;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import tools.models.Item;


public class ItemXmlUtil extends XmlUtil {
	
	/**
	 * XML데이터를 파싱해서 데이터 클래스에 데이터를 담는 메소드.
	 * 
	 * @param elName : XML태그명
	 * @param val    : XML태그의 값
	 * @param model  : 파싱한 데이터를 담기위한 클래스
	 * @return
	 */
	@Override
	public boolean setData(String elName, String val, Object obj) {
		if (val == null) return false;
		
		System.out.println("Element Name : ["+elName+"], Value : ["+val+"]");
		
		// ----- Start 이 부분은 지정한 데이터 클래스에 맞게 재정의를 해줘야함. -----
		Item model = (Item) obj;
		
		if (elName.equals("itemId")) {
			model.setItemId(Integer.parseInt(val));
			
		} else if (elName.equals("itemName")) {
			model.setItemName(val);
			
		} else if (elName.equals("itemDescription")) {
			model.setItemDescription(val);
			
		} else if (elName.equals("makerCode")) {
			model.setMakerCode(val);
			
		} else if (elName.equals("price")) {
			model.setPrice(Integer.parseInt(val));

		} else if (elName.equals("saleStatus")) {
			model.setSaleStatus(Integer.parseInt(val));
		
		} else if (elName.equals("images")) {
			model.getImages().add(val);
			
	    } else {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 지정한 데이터 클래스에 담긴 데이터를 XML형식으로 변환해주는 기능.
	 * 
	 * @param obj  : XML로 변환하기 위한 데이터 클래스
	 * @return
	 */
	@Override
	public String makeXml(Object obj) {
		if (obj == null) return "";
		Item i = (Item)obj;
		
		Element rootEl = new Element("item");
		Document doc = new Document(rootEl);

		doc.getRootElement().addContent(new Element("itemId").addContent(String.valueOf(i.getItemId())));
		doc.getRootElement().addContent(new Element("itemName").addContent(i.getItemName()));
		doc.getRootElement().addContent(new Element("itemDescription").addContent(i.getItemDescription()));
		doc.getRootElement().addContent(new Element("makerCode").addContent(i.getMakerCode()));
		doc.getRootElement().addContent(new Element("price").addContent(String.valueOf(i.getPrice())));
		doc.getRootElement().addContent(new Element("saleStatus").addContent(String.valueOf(i.getSaleStatus())));
		Element images = new Element("images");
		
		for (String img : i.getImages()) {
			images.addContent(new Element("image").addContent(img));
		}

		doc.getRootElement().addContent(images);
		
		Format fm = Format.getPrettyFormat();
		fm.setEncoding("UTF-8");
		XMLOutputter output = new XMLOutputter(fm);
		return output.outputString(doc);
		
	}
	
	
	/**
	 * 지정한 데이터 클래스에 담긴 데이터를 XML형식으로 변환해주는 기능.
	 * 
	 * @param list : XML로 변환하기 위한 데이터 클래스의 리스트
	 * @return
	 */
	public String makeXmlList(List<Item> list) {
		if (list == null) return "";
		
		
		Element rootEl = new Element("items");
		Document doc = new Document(rootEl);
		
		for (Item i : list) {
			Element item = new Element("item");
			item.addContent(new Element("itemId").addContent(String.valueOf(i.getItemId())));
			item.addContent(new Element("itemName").addContent(i.getItemName()));
			item.addContent(new Element("itemDescription").addContent(i.getItemDescription()));
			item.addContent(new Element("makerCode").addContent(i.getMakerCode()));
			item.addContent(new Element("price").addContent(String.valueOf(i.getPrice())));
			item.addContent(new Element("saleStatus").addContent(String.valueOf(i.getSaleStatus())));
			Element images = new Element("images");
			
			for (String img : i.getImages()) {
				images.addContent(new Element("image").addContent(img));
			}
			
			item.addContent(images);
			
			doc.getRootElement().addContent(item);
		}
		
		Format fm = Format.getPrettyFormat();
		fm.setEncoding("UTF-8");
		XMLOutputter output = new XMLOutputter(fm);
		return output.outputString(doc);
		
	}
	
}