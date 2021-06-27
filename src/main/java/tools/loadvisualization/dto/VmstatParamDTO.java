package tools.loadvisualization.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class VmstatParamDTO {
	
	// # procs,-----------memory----------,---swap--,-----io----,-system--,------cpu-----
	String [] dataFormat00 = new String[] {
			"0,1,0,0,time: (yyyy-MM-dd HH:mm:ss),0,0",
			"0,0,1,2,procs,0,1",
			"0,0,3,6,memory,0,3",
			"0,0,7,8,swap,0,7",
			"0,0,9,10,io,0,9",
			"0,0,11,12,system,0,11",
			"0,0,13,17,cpu,0,13"
	};
	
	// # r,b,swpd,free,buff,cache,si,so,bi,bo,in,cs,us,sy,id,wa,st
	String [] dataFormat01 = new String[] {
			"1,1,r",
			"1,2,b",
			"1,3,swpd",
			"1,4,free",
			"1,5,buff",
			"1,6,cache",
			"1,7,si",
			"1,8,so",
			"1,9,bi",
			"1,10,bo",
			"1,11,in",
			"1,12,cs",
			"1,13,us",
			"1,14,sy",
			"1,15,id",
			"1,16,wa",
			"1,17,st"
	};
	
	// # For Memory Chart
	String [] memoryChartDatas = new String[] {
			// type,range,title,marker
			"string,2,0,0,null,null",
			"double,2,3,3,Swpd,star",
			"double,2,4,4,Free,square",
			"double,2,5,5,Buff,circle",
			"double,2,6,6,Cache,triangle"
	};
	
	
	
	public VmstatParamDTO(Properties prop) {
		for (int i=0; i<dataFormat00.length; i++) {
			
			String [] tmp = dataFormat00[i].split(",");
			int [] tmpLocations = new int[4];
			int [] tmpValueLocations = new int[2];
			int tmpValueLocationsCnt = 0;

			for (int j=0; j<tmp.length; j++) {
				if (j<=3) {
					tmpLocations[j] = Integer.parseInt(tmp[j]);
				}
				else if (j==4) {
					dataFormat00Value.add(tmp[j]);
				}
				else if (4<j) {
					tmpValueLocations[tmpValueLocationsCnt] = Integer.parseInt(tmp[j]);
					tmpValueLocationsCnt++;
				}
			}

			dataFormat00Location.add(tmpLocations);
			dataFormat00ValueLocation.add(tmpValueLocations);
		}
		
		for (int i=0; i<dataFormat01.length; i++) {
			String [] tmp = dataFormat01[i].split(",");
			int [] tmpLocations = new int[2];
			
			for (int j=0; j<tmp.length; j++) {
				if (j<=1) {
					tmpLocations[j] = Integer.parseInt(tmp[j]);
				}
				else if (j==2) {
					dataFormat01Value.add(tmp[j]);
				}
			}
			
			dataFormat01Location.add(tmpLocations);
			
		}
		
		for (int i=0; i<memoryChartDatas.length; i++) {
			
			String [] tmp = memoryChartDatas[i].split(",");
			int [] tmpRange = new int[3];
			int tmpRangeCnt = 0;

			for (int j=0; j<tmp.length; j++) {
				if (j==0) {
					memoryChartDataType.add(tmp[j]);
				}
				else if (1 <= j && j <= 3) {
					System.out.println("["+j+"],["+tmpRangeCnt+"] : "+tmp[j]);
					tmpRange[tmpRangeCnt] = Integer.parseInt(tmp[j]);
					tmpRangeCnt++;
				}
				else if (4==j) {
					memoryChartDataTitle.add(tmp[j]);
				}
				else if (5==j) {
					memoryChartDataMarker.add(tmp[j]);
				}
			}
			
			memoryChartDataRange.add(tmpRange);

		}
		
		this.memorySheetName = prop.getProperty("memory.sheetname");
		this.memoryChartTitle = prop.getProperty("memory.chart.title");
		this.memoryChartLegendPosition = prop.getProperty("memory.chart.legend.position");
		this.memoryChartCategoryAxisPosition = prop.getProperty("memory.chart.categoryaxis.position");
		this.memoryChartCategoryAxisTitle = prop.getProperty("memory.chart.categoryaxis.title");
		this.memoryChartValueAxisPosition = prop.getProperty("memory.chart.valueaxis.position");
		this.memoryChartValueAxisTitle = prop.getProperty("memory.chart.valueaxis.title");
		this.memoryChartType = prop.getProperty("memory.chart.type");
		
	}
	
	// --- Data Sheet
	private List<int[]> dataFormat00Location = new ArrayList<int[]>();
	private List<String> dataFormat00Value = new ArrayList<String>();
	private List<int[]> dataFormat00ValueLocation = new ArrayList<int[]>();
	
	private List<int[]> dataFormat01Location = new ArrayList<int[]>();
	private List<String> dataFormat01Value = new ArrayList<String>();
	
	
	// --- Memory Chart Sheet
	private String memorySheetName;
	private String memoryChartTitle;
	private String memoryChartLegendPosition;
	private String memoryChartCategoryAxisPosition;
	private String memoryChartCategoryAxisTitle;
	private String memoryChartValueAxisPosition;
	private String memoryChartValueAxisTitle;
	private String memoryChartType;
	private List<String> memoryChartDataType = new ArrayList<String>();
	private List<int []> memoryChartDataRange = new ArrayList<int[]>();
	private List<String> memoryChartDataTitle = new ArrayList<String>();
	private List<String> memoryChartDataMarker = new ArrayList<String>();



	public String[] getDataFormat00() {
		return dataFormat00;
	}
	public String[] getDataFormat01() {
		return dataFormat01;
	}
	public String[] getMemoryChartDatas() {
		return memoryChartDatas;
	}
	public List<int[]> getDataFormat00Location() {
		return dataFormat00Location;
	}
	public List<String> getDataFormat00Value() {
		return dataFormat00Value;
	}
	public List<int[]> getDataFormat00ValueLocation() {
		return dataFormat00ValueLocation;
	}
	public List<int[]> getDataFormat01Location() {
		return dataFormat01Location;
	}
	public List<String> getDataFormat01Value() {
		return dataFormat01Value;
	}
	public String getMemorySheetName() {
		return memorySheetName;
	}
	public String getMemoryChartTitle() {
		return memoryChartTitle;
	}
	public String getMemoryChartLegendPosition() {
		return memoryChartLegendPosition;
	}
	public String getMemoryChartCategoryAxisPosition() {
		return memoryChartCategoryAxisPosition;
	}
	public String getMemoryChartCategoryAxisTitle() {
		return memoryChartCategoryAxisTitle;
	}
	public String getMemoryChartValueAxisPosition() {
		return memoryChartValueAxisPosition;
	}
	public String getMemoryChartValueAxisTitle() {
		return memoryChartValueAxisTitle;
	}
	public String getMemoryChartType() {
		return memoryChartType;
	}
	public List<String> getMemoryChartDataType() {
		return memoryChartDataType;
	}
	public List<int[]> getMemoryChartDataRange() {
		return memoryChartDataRange;
	}
	public List<String> getMemoryChartDataTitle() {
		return memoryChartDataTitle;
	}
	public List<String> getMemoryChartDataMarker() {
		return memoryChartDataMarker;
	}

}
