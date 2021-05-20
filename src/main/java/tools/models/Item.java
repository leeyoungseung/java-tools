package tools.models;

import java.util.ArrayList;
import java.util.List;

public class Item {
	private Integer itemId;
	
	private String itemName;
	
	private String itemDescription;
	
	private String makerCode;
	
	private Integer price;
	
	private Integer saleStatus;
	
	private List<String> images;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getMakerCode() {
		return makerCode;
	}

	public void setMakerCode(String makerCode) {
		this.makerCode = makerCode;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(Integer saleStatus) {
		this.saleStatus = saleStatus;
	}

	public List<String> getImages() {
		if (images == null) images = new ArrayList<String>();
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "Item [itemId=" + itemId + ", itemName=" + itemName + ", itemDescription=" + itemDescription
				+ ", makerCode=" + makerCode + ", price=" + price + ", saleStatus=" + saleStatus + ", images=" + images
				+ "]";
	}
	
	
	
}
