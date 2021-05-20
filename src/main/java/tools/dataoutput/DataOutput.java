package tools.dataoutput;

import java.util.List;

import tools.models.Item;

public interface DataOutput {

	Integer getDataCount();
	boolean outputDataProcess();
	List<Item> getDataList(int start, int end);
	
}
