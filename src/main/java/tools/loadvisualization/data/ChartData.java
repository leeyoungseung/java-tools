package tools.loadvisualization.data;

import java.util.ArrayList;
import java.util.List;

public class ChartData {
    private final String sheetName;
    private final String chartTitle;
    private final String chartLegendPosition;
    private final String categoryAxisPosition;
    private final String categoryAxisTitle;
	private final String valueAxisPosition;
	private final String valueAxisTitle;
	private final List<String> chartDataType;
	private final List<int []> chartDataRange;
	private final List<String> chartDataTitle;
	private final List<String> chartDataMarker;
	private final String chartType;
	
	public static class Builder {
		private String sheetName;
		private String chartTitle;
		private String chartLegendPosition;
		private String categoryAxisPosition;
		private String categoryAxisTitle;
		private String valueAxisPosition;
		private String valueAxisTitle;
		private List<String> chartDataType = new ArrayList<String>();
		private List<int []> chartDataRange = new ArrayList<int[]>();
		private List<String> chartDataTitle = new ArrayList<String>();
		private List<String> chartDataMarker = new ArrayList<String>();
		private String chartType;
		
		public Builder (String sheetName, String chartTitle) {
			this.sheetName = sheetName;
			this.chartTitle = chartTitle;
		}
		
		public Builder chartLegendPosition(String chartLegendPosition) {
			this.chartLegendPosition = chartLegendPosition;
			return this;
		}
		
		public Builder categoryAxisPosition(String categoryAxisPosition) {
			this.categoryAxisPosition = categoryAxisPosition;
			return this;
		}
		
		public Builder categoryAxisTitle(String categoryAxisTitle) {
			this.categoryAxisTitle = categoryAxisTitle;
			return this;
		}
		
		public Builder valueAxisPosition(String valueAxisPosition) {
			this.valueAxisPosition = valueAxisPosition;
			return this;
		}
		
		public Builder valueAxisTitle(String valueAxisTitle) {
			this.valueAxisTitle = valueAxisTitle;
			return this;
		}
		
		public Builder chartDataType(List<String> chartDataType) {
			this.chartDataType = chartDataType;
			return this;
		}
		
		public Builder chartDataRange(List<int []> chartDataRange) {
			this.chartDataRange = chartDataRange;
			return this;
		}
		
		public Builder chartDataTitle(List<String> chartDataTitle) {
			this.chartDataTitle = chartDataTitle;
			return this;
		}
		
		public Builder chartDataMarker(List<String> chartDataMarker) {
			this.chartDataMarker = chartDataMarker;
			return this;
		}
		
		public Builder chartType(String chartType) {
			this.chartType = chartType;
			return this;
		}
		
		public ChartData build() {
			return new ChartData(this);
		}
	}
	
	private ChartData(Builder builder) {
		this.sheetName = builder.sheetName;
		this.chartTitle = builder.chartTitle;
		this.chartLegendPosition = builder.chartLegendPosition;
		this.categoryAxisPosition = builder.categoryAxisPosition;
		this.categoryAxisTitle = builder.categoryAxisTitle;
		this.valueAxisPosition = builder.valueAxisPosition;
		this.valueAxisTitle = builder.valueAxisTitle;
		this.chartDataType  = builder.chartDataType;
		this.chartDataRange  = builder.chartDataRange;
		this.chartDataTitle  = builder.chartDataTitle;
		this.chartDataMarker  = builder.chartDataMarker;
		this.chartType = builder.chartType;
	}

	public String getSheetName() {
		return sheetName;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public String getChartLegendPosition() {
		return chartLegendPosition;
	}

	public String getCategoryAxisPosition() {
		return categoryAxisPosition;
	}

	public String getCategoryAxisTitle() {
		return categoryAxisTitle;
	}

	public String getValueAxisPosition() {
		return valueAxisPosition;
	}

	public String getValueAxisTitle() {
		return valueAxisTitle;
	}

	public List<String> getChartDataType() {
		return chartDataType;
	}

	public List<int[]> getChartDataRange() {
		return chartDataRange;
	}

	public List<String> getChartDataTitle() {
		return chartDataTitle;
	}

	public List<String> getChartDataMarker() {
		return chartDataMarker;
	}

	public String getChartType() {
		return chartType;
	}
	
	
}
