package tools.dataoutput;

public class ItemDataOutputFactory {

	public static ItemDataOutput getInstance(String type) {
		ItemDataOutput ItemDataOutput = null;
		
		switch (type) {
		case "json":
			ItemDataOutput = new ItemDataOutputJson();
			break;
		case "xml":
			ItemDataOutput = new ItemDataOutputXml();
			break;
		case "csv":
			ItemDataOutput = new ItemDataOutputCsv();
			break;	
		case "tsv":
			ItemDataOutput = new ItemDataOutputTsv();
			break;	
		default:
			break;
		}
		
		return ItemDataOutput;
	}
}
