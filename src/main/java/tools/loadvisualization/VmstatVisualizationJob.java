package tools.loadvisualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFDateAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFLine3DChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tools.loadvisualization.context.ContextVmstatAMI;
import tools.loadvisualization.data.ChartData;
import tools.loadvisualization.data.ChartData.Builder;
import tools.loadvisualization.exception.LoadVisualizationRuntimeException;

public class VmstatVisualizationJob implements LoadVisualizationJob {
	
	private Properties prop = null;
	private String command;
	private String targetFilePath;
	private String startDateTime;
	private int interval = 1;
	private SimpleDateFormat sdfForm1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdfForm2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public VmstatVisualizationJob() {
	}

	@Override
	public boolean visualizationProcess(String... args) throws NullPointerException, IOException, ParseException {
		
		// (1) ?????? ????????? ????????? ??????
		if (args == null || args.length == 0 || !validationParam(args)) {
			System.out.println("Paramaters Error");
			return false;
		}
		
		// ???????????? ??????
		setProperties();
		
		
		// (2) vmstat????????? ????????? ???????????? 
		List<String[]> fileValues = getDataFromTargetFile(args[1]);
		
		
		// Make Excel 
		String version = prop.getProperty("excel.version.type");
		XSSFWorkbook workbook = (XSSFWorkbook) createWorkbook(version);
		
		// (3) (2)??? ?????????????????? ????????? ?????? ???????????? Data?????? ????????????
		Sheet dataSheet = makeDataSheet(workbook, fileValues);
		
		// (4) Chart ??????
		ChartData chartDataMemory = new ChartData
				.Builder(prop.getProperty("memory.sheetname"),
						prop.getProperty("memory.chart.title"))
				.chartLegendPosition(prop.getProperty("memory.chart.legend.position"))
				.categoryAxisPosition(prop.getProperty("memory.chart.categoryaxis.position"))
				.categoryAxisTitle(prop.getProperty("memory.chart.categoryaxis.title"))
				.valueAxisPosition(prop.getProperty("memory.chart.valueaxis.position"))
				.valueAxisTitle(prop.getProperty("memory.chart.valueaxis.title"))
				.chartDataType(ContextVmstatAMI.M_CHART_DATA_TYPE)
		        .chartDataRange(ContextVmstatAMI.M_CHART_DATA_RANGE)
		        .chartDataTitle(ContextVmstatAMI.M_CHART_DATA_TITLE)
		        .chartDataMarker(ContextVmstatAMI.M_CHART_DATA_MARKER)
				.chartType(prop.getProperty("memory.chart.type"))
				.build();
		
		ChartData chartDataProc = new ChartData
				.Builder(prop.getProperty("procs.sheetname"),
						prop.getProperty("procs.chart.title"))
				.chartLegendPosition(prop.getProperty("procs.chart.legend.position"))
				.categoryAxisPosition(prop.getProperty("procs.chart.categoryaxis.position"))
				.categoryAxisTitle(prop.getProperty("procs.chart.categoryaxis.title"))
				.valueAxisPosition(prop.getProperty("procs.chart.valueaxis.position"))
				.valueAxisTitle(prop.getProperty("procs.chart.valueaxis.title"))
				.chartDataType(ContextVmstatAMI.PROC_CHART_DATA_TYPE)
		        .chartDataRange(ContextVmstatAMI.PROC_CHART_DATA_RANGE)
		        .chartDataTitle(ContextVmstatAMI.PROC_CHART_DATA_TITLE)
		        .chartDataMarker(ContextVmstatAMI.PROC_CHART_DATA_MARKER)
				.chartType(prop.getProperty("procs.chart.type"))
				.build();
		
		
		try {
			makeChart3DLineChartSheet(workbook, dataSheet, fileValues.size(), chartDataProc);
			makeChartLineChartSheet(workbook, dataSheet, fileValues.size(), chartDataMemory);
			
		} catch (LoadVisualizationRuntimeException e) {
			e.printStackTrace();
		}
		
		
		// (5) ?????? ???????????? ??????
		String now = sdfForm2.format(new Date());
		String resultDir = prop.getProperty("result.dir");
		String resultFileName = prop.getProperty("result.filename");
		String resultPath = resultDir + resultFileName + "_" + now + "." + version;
		
		writeExcel(workbook, resultPath);
		return true;
	}
	
	private void setProperties() {
		this.prop = new Properties();
		InputStream is = null;
		try {
			is = VmstatVisualizationJob.class.getClassLoader().getResourceAsStream("config.vmstat.properties");
			prop.load(is);
			//vmstatContext = new ContextVmstatAMI();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * args[0] : commad                   Ex) vmstat
	 * args[1] : FilePath                 Ex) /path/to/java-tools/files/input/20210525_vmstat.txt
	 * args[2] : Start Datetime           Ex) 2021-06-14 12:00:00
	 * args[3] : Interval of repeat time  Ex) 1 (default value 1)
	 */
	@Override
	public boolean validationParam(String... args) {
		this.command = args[0];
		this.targetFilePath = args[1];
		this.startDateTime = args[2];
		this.interval = Integer.parseInt(args[3]);
		
		if (command == null || command.equals("")) return false; 
		if (targetFilePath == null || targetFilePath.equals("") || ! new File(targetFilePath).exists()) return false;
		if (startDateTime == null || startDateTime.equals("")) return false;
		if (interval < 1) return false;
		
		return true;
	}

	/**
	 * filePath : Target File Path
	 * 
	 * @throws IOException
	 */
	@Override
	public List<String[]> getDataFromTargetFile(String filePath) throws IOException {
		// (1) Target file Object generate.
		File f = new File(filePath);
		List<String[]> fileValues = new ArrayList<>();

		if (f.isFile()) {
			try (BufferedReader br = Files.newBufferedReader(Paths.get(f.getAbsolutePath()),
					Charset.forName("UTF-8"))) {
				String line = "";

				while ((line = br.readLine()) != null) {
					// procs,-----------memory----------,---swap--,-----io----,-system--,------cpu-----
					// r,b,swpd,free,buff,cache,si,so,bi,bo,in,cs,us,sy,id,wa,st
					// 0,0,0,217084,0,696440,0,0,0,0,23,44,0,0,100,0,0
					if (Pattern.compile("[0-9\s]+").matcher(line).matches()) {
						fileValues.add(line.trim().split("\s+"));
					}

				}
			}
		}
		
//		int su = 1;
//		for (String [] line : fileValues) {
//			StringBuilder sb = new StringBuilder();
//			
//			for (String str : line) {
//				sb.append(str);
//				sb.append(",");
//			}
//			//System.out.println("["+su+"] "+sb.toString());
//			sb = null;
//			su++;
//		}
		

		return fileValues;
	}

	/**
	 * Make Data Sheet for making Chart
	 * @param fileValues : Values for make Data sheet.
	 * @throws ParseException 
	 */
	public Sheet makeDataSheet(Workbook excelWorkbook, List<String[]> fileValues) throws ParseException {
		XSSFWorkbook workbook = (XSSFWorkbook) excelWorkbook;

		// Make Data Sheet
		XSSFSheet sheet = workbook.createSheet("Data");

		// Generate First Row
		// procs,-----------memory----------,---swap--,-----io----,-system--,------cpu-----
		List<int[]> locations = ContextVmstatAMI.D_00_FORMAT_LOCATIONS;
		for (int[] location : locations) {
			sheet.addMergedRegion(new CellRangeAddress(location[0], location[1], location[2], location[3]));
		}
		
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		
		Cell cell = null;
		List<String> values = ContextVmstatAMI.D_00_VALUES;
		List<int[]> valueLocations = ContextVmstatAMI.D_00_VALUE_LOCATIONS;
		
		for (int i = 0; i < values.size(); i++) {
			cell = getCell(sheet, valueLocations.get(i)[0], valueLocations.get(i)[1]);
			cell.setCellValue(values.get(i));
			cell.setCellStyle(headerStyle);
		}
		
		// Generate Second Row
		// r,b,swpd,free,buff,cache,si,so,bi,bo,in,cs,us,sy,id,wa,st
		List<String> valuesSecond = ContextVmstatAMI.D_01_VALUES;
		List<int[]> locationsSecond = ContextVmstatAMI.D_01_VALUE_LOCATIONS;
		
		for (int i = 0; i < valuesSecond.size(); i++) {
			getCell(sheet, locationsSecond.get(i)[0], locationsSecond.get(i)[1])
			  .setCellValue(valuesSecond.get(i));
		}
		
		
		// Generate Data Row
		int addDatetime = 0;
		Date date = sdfForm1.parse(startDateTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int fileValueCount = fileValues.size();
		int startNumOfResultValueDatas = 2; // Because 'result value datas' are going to input from the second row.
		
		for (int rowNum = 0; rowNum < fileValueCount; rowNum++) {
			String[] line = fileValues.get(rowNum);
			
			// step.1 Output Datetime
			cal.add(Calendar.SECOND, addDatetime + interval);
			getCell(sheet, startNumOfResultValueDatas, 0).setCellValue(sdfForm1.format(cal.getTime()));

			// step.2 Output result value.
			for (int cellNum = 1; cellNum <= line.length; cellNum++) {
				getCell(sheet, startNumOfResultValueDatas, cellNum).setCellValue(Double.parseDouble(line[cellNum-1]));
			}
			startNumOfResultValueDatas++;
		}
		
        return sheet;
	}
	
	/**
	 * Make Line Chart Sheet
	 * @param excelWorkbook
	 * @param dataSheet
	 * @param dataLength
	 */
	public void makeChartLineChartSheet(Workbook excelWorkbook, Sheet dataSheet, int dataLength, ChartData cd) throws LoadVisualizationRuntimeException {
		XSSFWorkbook workbook = (XSSFWorkbook) excelWorkbook;
		XSSFSheet sheet = (XSSFSheet) dataSheet;
		
		// Make Chart Sheet
		XSSFSheet memorySheet = workbook.createSheet(cd.getSheetName());
		
		// Make Chart
		// 1) ?????? ????????? ??????
		XSSFDrawing drawing = memorySheet.createDrawingPatriarch();
		
		// 2) ?????? ?????? ??????
		XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, dataLength, dataLength);
		
		// 3) ?????? ?????? ??????
		XSSFChart chart = drawing.createChart(anchor);
		
		// 4) ?????? ????????? ??????
		chart.setTitleText(cd.getChartTitle()); 
		chart.setTitleOverlay(false);
		
		// 5) ????????? ???????????? ??????
		XDDFChartLegend legend = chart.getOrAddLegend();
		legend.setPosition(LegendPosition.valueOf(cd.getChartLegendPosition()));

		// 6) ????????????????????? ??? ?????? ?????? 
		XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.valueOf(cd.getCategoryAxisPosition()));
		bottomAxis.setTitle(cd.getCategoryAxisTitle());
		
		// 7) ????????????????????? ??? ?????? ?????? 
		XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.valueOf(cd.getValueAxisPosition()));
		leftAxis.setTitle(cd.getValueAxisTitle());
		
		// 8) ?????? ????????? ?????? ???????????? ???????????? ????????????. 
		// ????????? ????????? ??????, ?????? ??????????????? ??? ??????, ?????? ????????? ?????? ??? ????????? ??????????????? ?????????. 
		XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.valueOf(cd.getChartType()), bottomAxis, leftAxis);
		
		// 9) ????????? ?????????????????? ????????????????????? ????????? ???????????? ????????? ????????????.
		XDDFDataSource<String> time = null;                       // # ????????? ????????? ???????????? ?????? ????????? ??????
		XDDFNumericalDataSource<Double> commandResultData = null; // # ????????? ????????? ?????? ?????? ????????? ??????
		List<String> chartDataTypeList = cd.getChartDataType();
		List<int[]> chartDataRange = cd.getChartDataRange();
		List<XDDFNumericalDataSource<Double>> dataSourceList = new ArrayList<XDDFNumericalDataSource<Double>>();
		
		int cnt = 0;
		for (String chartDataType : chartDataTypeList) {
			// ???????????? ?????? ???????????? ????????? ??????. ????????? ????????? ???????????? ?????????????????? ??????.
			if (chartDataType.equals("string")) { 
				time = XDDFDataSourcesFactory.fromStringCellRange(sheet,
						new CellRangeAddress(chartDataRange.get(cnt)[0], dataLength, chartDataRange.get(cnt)[1], chartDataRange.get(cnt)[2]));
			
			// ??????????????? ?????? ???????????? ????????? ??????. ?????? ????????? ???????????? ???????????? ?????? ??????.
			} else if (chartDataType.equals("double") && time != null) {
				commandResultData = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
						new CellRangeAddress(chartDataRange.get(cnt)[0], dataLength, chartDataRange.get(cnt)[1], chartDataRange.get(cnt)[2]));
				
				XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(time, commandResultData);
				series.setTitle(cd.getChartDataTitle().get(cnt), null);
				series.setMarkerStyle(MarkerStyle.valueOf(cd.getChartDataMarker().get(cnt)));
				series.setSmooth(false);
				
			} else {
				throw new LoadVisualizationRuntimeException("Occur runtime error. When it set dataSource range..");
				
			}
			
			cnt++;
		}
		
		chart.plot(data);
	}
	
	
	/**
	 * Make 3D Line Chart Sheet
	 * @param excelWorkbook
	 * @param dataSheet
	 * @param dataLength
	 */
	public void makeChart3DLineChartSheet(Workbook excelWorkbook, Sheet dataSheet, int dataLength, ChartData cd) throws LoadVisualizationRuntimeException {
		XSSFWorkbook workbook = (XSSFWorkbook) excelWorkbook;
		XSSFSheet sheet = (XSSFSheet) dataSheet;
		
		// Make Chart Sheet
		XSSFSheet memorySheet = workbook.createSheet(cd.getSheetName());
		
		// Make Chart
		// 1) ?????? ????????? ??????
		XSSFDrawing drawing = memorySheet.createDrawingPatriarch();
		
		// 2) ?????? ?????? ??????
		XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, dataLength, dataLength);
		
		// 3) ?????? ?????? ??????
		XSSFChart chart = drawing.createChart(anchor);
		
		// 4) ?????? ????????? ??????
		chart.setTitleText(cd.getChartTitle()); 
		chart.setTitleOverlay(false);
		
		// 5) ????????? ???????????? ??????
		XDDFChartLegend legend = chart.getOrAddLegend();
		legend.setPosition(LegendPosition.valueOf(cd.getChartLegendPosition()));

		// 6) ????????????????????? ??? ?????? ?????? 
		XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.valueOf(cd.getCategoryAxisPosition()));
		bottomAxis.setTitle(cd.getCategoryAxisTitle());
		
		// 7) ????????????????????? ??? ?????? ?????? 
		XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.valueOf(cd.getValueAxisPosition()));
		leftAxis.setTitle(cd.getValueAxisTitle());
		
		// 8) ?????? ????????? ?????? ???????????? ???????????? ????????????. 
		// ????????? ????????? ??????, ?????? ??????????????? ??? ??????, ?????? ????????? ?????? ??? ????????? ??????????????? ?????????. 
		XDDFLine3DChartData data = (XDDFLine3DChartData) chart.createData(ChartTypes.valueOf(cd.getChartType()), bottomAxis, leftAxis);
		
		// 9) ????????? ?????????????????? ????????????????????? ????????? ???????????? ????????? ????????????.
		XDDFDataSource<String> time = null;                       // # ????????? ????????? ???????????? ?????? ????????? ??????
		XDDFNumericalDataSource<Double> commandResultData = null; // # ????????? ????????? ?????? ?????? ????????? ??????
		List<String> chartDataTypeList = cd.getChartDataType();
		List<int[]> chartDataRange = cd.getChartDataRange();
		//List<XDDFNumericalDataSource<Double>> dataSourceList = new ArrayList<XDDFNumericalDataSource<Double>>();
		
		int cnt = 0;
		for (String chartDataType : chartDataTypeList) {
			// ???????????? ?????? ???????????? ????????? ??????. ????????? ????????? ???????????? ?????????????????? ??????.
			if (chartDataType.equals("string")) { 
				time = XDDFDataSourcesFactory.fromStringCellRange(sheet,
						new CellRangeAddress(chartDataRange.get(cnt)[0], dataLength, chartDataRange.get(cnt)[1], chartDataRange.get(cnt)[2]));
			
			// ??????????????? ?????? ???????????? ????????? ??????. ?????? ????????? ???????????? ???????????? ?????? ??????.
			} else if (chartDataType.equals("double") && time != null) {
				commandResultData = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
						new CellRangeAddress(chartDataRange.get(cnt)[0], dataLength, chartDataRange.get(cnt)[1], chartDataRange.get(cnt)[2]));
				
				XDDFLine3DChartData.Series series = (XDDFLine3DChartData.Series) data.addSeries(time, commandResultData);
				series.setTitle(cd.getChartDataTitle().get(cnt), null);
				series.setMarkerStyle(MarkerStyle.valueOf(cd.getChartDataMarker().get(cnt)));
				series.setSmooth(false);
				
			} else {
				throw new LoadVisualizationRuntimeException("Occur runtime error. When it set dataSource range..");
				
			}
			
			cnt++;
		}
		
		chart.plot(data);
	}
	

	/**
	 * 
	 * @param version
	 * @return Workbook
	 */
	public Workbook createWorkbook(String version) {
		// ?????? xls ??????
		if ("xls".equals(version)) {
			return new HSSFWorkbook();

			// ?????? xlsx ??????
		} else if ("xlsx".equals(version)) {
			return new XSSFWorkbook();

		}

		throw new NoClassDefFoundError();
	}

	/**
	 * 
	 * @param workbook
	 * @param filePath
	 */
	public void writeExcel(Workbook workbook, String filePath) {
		try (FileOutputStream stream = new FileOutputStream(filePath)) {
			workbook.write(stream);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Sheet??? ?????? Row??? ??????, ????????????
	public Row getRow(Sheet sheet, int rownum) {
		Row row = sheet.getRow(rownum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}
		return row;
	}

	// Row??? ?????? Cell??? ??????, ????????????
	public Cell getCell(Row row, int cellnum) {
		Cell cell = row.getCell(cellnum);
		if (cell == null) {
			cell = row.createCell(cellnum);
		}
		return cell;
	}

	public Cell getCell(Sheet sheet, int rownum, int cellnum) {
		Row row = getRow(sheet, rownum);
		return getCell(row, cellnum);
	}

}
