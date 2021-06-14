package tools.loadvisualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

public class VmstatVisualizationJob implements LoadVisualizationJob {

	private String command;
	private String targetFilePath;
	private String startDateTime;
	private int interval = 1;
	
	
	public VmstatVisualizationJob() {
	}

	@Override
	public boolean visualizationProcess(String... args) throws NullPointerException, IOException, ParseException {

		// (1) 기동 변수의 유효성 확인
		if (args == null || args.length == 0 || !validationParam(args)) {
			System.out.println("Paramaters Error");
			return false;
		}
		
		
		// (2) vmstat결과값 파일을 읽어오기 
		List<String[]> fileValues = getDataFromTargetFile(args[1]);
		
		// (3) (2)의 프로세스에서 읽어온 값을 바탕으로 Data시트 생성하기
		makeDataSheet(fileValues);
		
		return true;
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

		return fileValues;
	}

	/**
	 * 
	 * @param fileValues : Values for make Data sheet.
	 * @throws ParseException 
	 */
	public void makeDataSheet(List<String[]> fileValues) throws ParseException {
		String version = "xls";

		// Make Excel
		Workbook workbook = createWorkbook(version);

		// Make Data Sheet
		Sheet sheet = workbook.createSheet("Data");

		
		// Generate First Row
		// procs,-----------memory----------,---swap--,-----io----,-system--,------cpu-----
		
		sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,2));
		sheet.addMergedRegion(new CellRangeAddress(0,0,3,6));
		sheet.addMergedRegion(new CellRangeAddress(0,0,7,8));
		sheet.addMergedRegion(new CellRangeAddress(0,0,9,10));
		sheet.addMergedRegion(new CellRangeAddress(0,0,11,12));
		sheet.addMergedRegion(new CellRangeAddress(0,0,13,17));
		
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
		
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		Cell cell = getCell(sheet, 0, 0);
		cell.setCellValue("time: (yyyy-MM-dd HH:mm:ss)");
		cell.setCellStyle(headerStyle);
		
		cell = getCell(sheet, 0, 1);
		cell.setCellValue("procs");
		cell.setCellStyle(headerStyle);
		
		cell = getCell(sheet, 0, 3);
		cell.setCellValue("memory");
		cell.setCellStyle(headerStyle);
		
		cell = getCell(sheet, 0, 7);
		cell.setCellValue("swap");
		cell.setCellStyle(headerStyle);
		
		cell = getCell(sheet, 0, 9);
		cell.setCellValue("io");
		cell.setCellStyle(headerStyle);
		
		cell = getCell(sheet, 0, 11);
		cell.setCellValue("system");
		cell.setCellStyle(headerStyle);
		
		cell = getCell(sheet, 0, 13);
		cell.setCellValue("cpu");
		cell.setCellStyle(headerStyle);
		
		// Generate Second Row
		// r,b,swpd,free,buff,cache,si,so,bi,bo,in,cs,us,sy,id,wa,st
		getCell(sheet, 1, 1).setCellValue("r");
		getCell(sheet, 1, 2).setCellValue("b");
		getCell(sheet, 1, 3).setCellValue("swpd");
		getCell(sheet, 1, 4).setCellValue("free");
		getCell(sheet, 1, 5).setCellValue("buff");
		getCell(sheet, 1, 6).setCellValue("cache");
		getCell(sheet, 1, 7).setCellValue("si");
		getCell(sheet, 1, 8).setCellValue("so");
		getCell(sheet, 1, 9).setCellValue("bi");
		getCell(sheet, 1, 10).setCellValue("bo");
		getCell(sheet, 1, 11).setCellValue("in");
		getCell(sheet, 1, 12).setCellValue("cs");
		getCell(sheet, 1, 13).setCellValue("us");
		getCell(sheet, 1, 14).setCellValue("sy");
		getCell(sheet, 1, 15).setCellValue("id");
		getCell(sheet, 1, 16).setCellValue("wa");
		getCell(sheet, 1, 17).setCellValue("st");
		
		
		int addDatetime = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			
		Date date = sdf.parse(startDateTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// Generate Data Row
		for (int rowNum = 2; rowNum < fileValues.size(); rowNum++) {
			
			String[] line = fileValues.get(rowNum);
			// step.1 Output Datetime
			cal.add(Calendar.SECOND, addDatetime + interval);
			getCell(sheet, rowNum, 0).setCellValue(sdf.format(cal.getTime()));

			
			// step.2 Output result value.
			for (int cellNum = 1; cellNum <= line.length; cellNum++) {
				getCell(sheet, rowNum, cellNum).setCellValue(line[cellNum-1]);
			}

		}

		writeExcel(workbook,
				"C:\\Users\\leeyoungseung\\project_source\\java-tools\\files\\result_poi\\vmstat-res." + version);

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
			return new HSSFWorkbook();

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
