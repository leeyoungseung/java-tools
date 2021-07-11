package tools.loadvisualization.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ContextVmstatAMI {
	
	// # procs,-----------memory----------,---swap--,-----io----,-system--,------cpu-----
	private static final String [] dataFormat00 = new String[] {
			"0,1,0,0,time: (yyyy-MM-dd HH:mm:ss),0,0",
			"0,0,1,2,procs,0,1",
			"0,0,3,6,memory,0,3",
			"0,0,7,8,swap,0,7",
			"0,0,9,10,io,0,9",
			"0,0,11,12,system,0,11",
			"0,0,13,17,cpu,0,13"
	};
	
	// # r,b,swpd,free,buff,cache,si,so,bi,bo,in,cs,us,sy,id,wa,st
	private static final String [] dataFormat01 = new String[] {
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
	private static final String [] memoryChartDatas = new String[] {
			// type,range,title,marker
			"string,2,0,0,null,null",
			"double,2,3,3,Swpd,STAR",
			"double,2,4,4,Free,SQUARE",
			"double,2,5,5,Buff,CIRCLE",
			"double,2,6,6,Cache,TRIANGLE"
	};
	
	// --- Data Sheet
	public static List<int[]> D_00_FORMAT_LOCATIONS = new ArrayList<int[]>();
	public static List<String> D_00_VALUES = new ArrayList<String>();
	public static List<int[]> D_00_VALUE_LOCATIONS = new ArrayList<int[]>();
	
	public static List<String> D_01_VALUES = new ArrayList<String>();
	public static List<int[]> D_01_VALUE_LOCATIONS = new ArrayList<int[]>();
	
	
	// --- Memory Chart Sheet
	public static List<String> M_CHART_DATA_TYPE = new ArrayList<String>();
	public static List<int []> M_CHART_DATA_RANGE = new ArrayList<int[]>();
	public static List<String> M_CHART_DATA_TITLE = new ArrayList<String>();
	public static List<String> M_CHART_DATA_MARKER = new ArrayList<String>();
	
	
	static {
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
					D_00_VALUES.add(tmp[j]);
				}
				else if (4<j) {
					tmpValueLocations[tmpValueLocationsCnt] = Integer.parseInt(tmp[j]);
					tmpValueLocationsCnt++;
				}
			}

			D_00_FORMAT_LOCATIONS.add(tmpLocations);
			D_00_VALUE_LOCATIONS.add(tmpValueLocations);
		}
		
		for (int i=0; i<dataFormat01.length; i++) {
			String [] tmp = dataFormat01[i].split(",");
			int [] tmpLocations = new int[2];
			
			for (int j=0; j<tmp.length; j++) {
				if (j<=1) {
					tmpLocations[j] = Integer.parseInt(tmp[j]);
				}
				else if (j==2) {
					D_01_VALUES.add(tmp[j]);
				}
			}
			
			D_01_VALUE_LOCATIONS.add(tmpLocations);
			
		}
		
		for (int i=0; i<memoryChartDatas.length; i++) {
			
			String [] tmp = memoryChartDatas[i].split(",");
			int [] tmpRange = new int[3];
			int tmpRangeCnt = 0;

			for (int j=0; j<tmp.length; j++) {
				if (j==0) {
					M_CHART_DATA_TYPE.add(tmp[j]);
				}
				else if (1 <= j && j <= 3) {
					//System.out.println("["+j+"],["+tmpRangeCnt+"] : "+tmp[j]);
					tmpRange[tmpRangeCnt] = Integer.parseInt(tmp[j]);
					tmpRangeCnt++;
				}
				else if (4==j) {
					M_CHART_DATA_TITLE.add(tmp[j]);
				}
				else if (5==j) {
					M_CHART_DATA_MARKER.add(tmp[j]);
				}
			}
			
			M_CHART_DATA_RANGE.add(tmpRange);

		}
	}
	

}


