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
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tools.loadvisualization.dto.VmstatParamDTO;

public class VmstatVisualizationJob implements LoadVisualizationJob {

	private Properties prop = null;
	private String command;
	private String targetFilePath;
	private String startDateTime;
	private int interval = 1;
	private SimpleDateFormat sdfForm1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	VmstatParamDTO dto; 
	
	public VmstatVisualizationJob() {
	}

	@Override
	public boolean visualizationProcess(String... args) throws NullPointerException, IOException, ParseException {
		
		
		// (1) 기동 변수의 유효성 확인
		if (args == null || args.length == 0 || !validationParam(args)) {
			System.out.println("Paramaters Error");
			return false;
		}
		
		// 전역변수 설정
		setProperties();
		dto = new VmstatParamDTO(this.prop);
		
		// (2) vmstat결과값 파일을 읽어오기 
		List<String[]> fileValues = getDataFromTargetFile(args[1]);
		
		
		// Make Excel 
		String version = "xlsx";
		XSSFWorkbook workbook = (XSSFWorkbook) createWorkbook(version);
		
		// (3) (2)의 프로세스에서 읽어온 값을 바탕으로 Data시트 생성하기
		Sheet dataSheet = makeDataSheet(workbook, fileValues);
		
		// (4) Memory Chart 생성
		makeChartSheet(workbook, dataSheet, fileValues.size());
		
		writeExcel(workbook,
		"C:\\Users\\leeyoungseung\\project_source\\java-tools\\files\\result_poi\\vmstat-res." + version);
		
		return true;
	}
	
	private void setProperties() {
		this.prop = new Properties();
		InputStream is = null;
		try {
			is = VmstatVisualizationJob.class.getClassLoader().getResourceAsStream("config.vmstat.properties");
			prop.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * args[0] : commad 
	 * args[1] : FilePath
	 * args[2] : Start Datetime
	 * args[3] : Interval of repeat time
	 */
	@Override
	public boolean validationParam(String... args) {
		
		this.command = args[0];
		this.targetFilePath = args[1];
		this.startDateTime = args[2];
		this.interval = Integer.parseInt(args[3]);
		
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
		
		int su = 1;
		for (String [] line : fileValues) {
			StringBuilder sb = new StringBuilder();
			
			for (String str : line) {
				sb.append(str);
				sb.append(",");
			}
			System.out.println("["+su+"] "+sb.toString());
			sb = null;
			su++;
		}
		

		return fileValues;
	}

	/**
	 * 
	 * @param fileValues : Values for make Data sheet.
	 * @throws ParseException 
	 */
	public Sheet makeDataSheet(Workbook excelWorkbook, List<String[]> fileValues) throws ParseException {
		XSSFWorkbook workbook = (XSSFWorkbook) excelWorkbook;

		// Make Data Sheet
		XSSFSheet sheet = workbook.createSheet("Data");

		// Generate First Row
		// procs,-----------memory----------,---swap--,-----io----,-system--,------cpu-----
		List<int[]> listDataFormat00Location = dto.getDataFormat00Location();
		for (int[] address : listDataFormat00Location) {
			sheet.addMergedRegion(new CellRangeAddress(address[0], address[1], address[2], address[3]));
		}
		
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		
		Cell cell = null;
		List<String> listDataFormat00Val = dto.getDataFormat00Value();
		List<int[]> listDataFormat00ValLocation = dto.getDataFormat00ValueLocation();
		
		for (int i = 0; i < listDataFormat00Val.size(); i++) {
			cell = getCell(sheet, listDataFormat00ValLocation.get(i)[0], listDataFormat00ValLocation.get(i)[1]);
			cell.setCellValue(listDataFormat00Val.get(i));
			cell.setCellStyle(headerStyle);
		}
		
		// Generate Second Row
		// r,b,swpd,free,buff,cache,si,so,bi,bo,in,cs,us,sy,id,wa,st
		List<String> listDataFormat01Val = dto.getDataFormat01Value();
		List<int[]> listDataFormat01ValLocation = dto.getDataFormat01Location();
		
		for (int i = 0; i < listDataFormat01Val.size(); i++) {
			getCell(sheet, listDataFormat01ValLocation.get(i)[0], listDataFormat01ValLocation.get(i)[1])
			  .setCellValue(listDataFormat01Val.get(i));
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
	
	
	public void makeChartSheet(Workbook excelWorkbook, Sheet dataSheet, int dataLength) {
		XSSFWorkbook workbook = (XSSFWorkbook) excelWorkbook;
		XSSFSheet sheet = (XSSFSheet) dataSheet;
		
		// Make Data Sheet
		XSSFSheet memorySheet = workbook.createSheet(dto.getMemorySheetName());
		
		// make LineChart
		XSSFDrawing drawing = memorySheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, dataLength, dataLength);
		
		XSSFChart chart = drawing.createChart(anchor);
		chart.setTitleText(dto.getMemoryChartTitle());
		chart.setTitleOverlay(false);
		
		XDDFChartLegend legend = chart.getOrAddLegend();
		legend.setPosition(LegendPosition.TOP_RIGHT);

		XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
		bottomAxis.setTitle(dto.getMemoryChartCategoryAxisTitle());
		
		XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
		leftAxis.setTitle(dto.getMemoryChartValueAxisTitle());
		
		XDDFDataSource<String> time = XDDFDataSourcesFactory.fromStringCellRange(sheet,
				new CellRangeAddress(2, dataLength, 0, 0));
		
		XDDFNumericalDataSource<Double> swpd = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
				new CellRangeAddress(2, dataLength, 3, 3));
		
		XDDFNumericalDataSource<Double> free = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
				new CellRangeAddress(2, dataLength, 4, 4));

		XDDFNumericalDataSource<Double> buff = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
				new CellRangeAddress(2, dataLength, 5, 5));
		
		XDDFNumericalDataSource<Double> cache = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
				new CellRangeAddress(2, dataLength, 6, 6));

		XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

		
		XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(time, swpd);
		series1.setTitle("Swpd", null);
		series1.setSmooth(false);
		series1.setMarkerStyle(MarkerStyle.STAR);
		
		XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) data.addSeries(time, free);
		series2.setTitle("Free", null);
		series2.setSmooth(true);
		series2.setMarkerStyle(MarkerStyle.SQUARE);
		
		XDDFLineChartData.Series series3 = (XDDFLineChartData.Series) data.addSeries(time, buff);
		series3.setTitle("Buff", null);
		series3.setSmooth(true);
		series3.setMarkerStyle(MarkerStyle.CIRCLE);
		
		XDDFLineChartData.Series series4 = (XDDFLineChartData.Series) data.addSeries(time, cache);
		series4.setTitle("Cache", null);
		series4.setSmooth(true);
		series4.setMarkerStyle(MarkerStyle.TRIANGLE);
		
		chart.plot(data);
	}
	
	

	/**
	 * 
	 * @param version
	 * @return Workbook
	 */
	public Workbook createWorkbook(String version) {
		// 표준 xls 버젼
		if ("xls".equals(version)) {
			return new HSSFWorkbook();

			// 확장 xlsx 버젼
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

	// Sheet로 부터 Row를 취득, 생성하기
	public Row getRow(Sheet sheet, int rownum) {
		Row row = sheet.getRow(rownum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}
		return row;
	}

	// Row로 부터 Cell를 취득, 생성하기
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
