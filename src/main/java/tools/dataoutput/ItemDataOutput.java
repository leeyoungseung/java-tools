package tools.dataoutput;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import tools.models.Item;
import tools.utils.DBUtil;

public abstract class ItemDataOutput implements DataOutput {
	
	// Param
	private DBUtil dbUtil = DBUtil.getInstance();
	private Connection con = null;
	private final String COUNT_ITEM = "SELECT COUNT(*) AS ALL_COUNT FROM ITEM ";
	private final String SELECT_ITEM = "SELECT * FROM ITEM ";
	protected String OUTPUT_DIR = "";
	protected String OUTPUT_FILE_NAME = "";
	protected String EXTENTION = "";
	protected String ENCODING_TYPE = "";
	private int countOneTimeProcess = 0;
	
	
	/**
	 * 생성자에서 프로퍼티를 가져온다.
	 */
	public ItemDataOutput() {
		dbUtil.setDBParam();
		dbUtil.initConnection();
		con = dbUtil.getConnection();
		
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = ItemDataOutput.class.getClassLoader().getResourceAsStream("config.dataoutput.properties");
			prop.load(is);
			countOneTimeProcess = Integer.parseInt(prop.getProperty("count.onetime.process"));
			
		} catch (Exception e) {
			System.out.println("Properties load fail!!");
		}
	}
	
	
	/**
	 * 추출할 전체 데이터의 수를 가져오기
	 */
	@Override
	public Integer getDataCount() {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(COUNT_ITEM);
			ResultSet rs = ps.executeQuery();
			
			int res = 0;
			while (rs.next()) { res = rs.getInt("ALL_COUNT"); }
			
			return res;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * start에서 end까지의 데이터를 추출
	 * @param start 추출할 데이터의 시작
	 * @param end   추출할 데이터의 끝
	 */
	@Override
	public List<Item> getDataList(int start, int end) {
		List<Item> list = new ArrayList<Item>();
		PreparedStatement ps = null;
		try {
			System.out.println(SELECT_ITEM+ "LIMIT "+start+" , "+end+"");
			ps = con.prepareStatement(SELECT_ITEM+ "LIMIT "+start+" , "+end+"");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Item i = new Item();
				i.setItemId(rs.getInt("ITEM_ID"));
				i.setItemName(rs.getString("ITEM_NAME"));
				i.setItemDescription(rs.getString("ITEM_DESCRIPTION"));
				i.setMakerCode(rs.getString("MAKER_CODE"));
				i.setPrice(rs.getInt("PRICE"));
				i.setSaleStatus(rs.getInt("SALE_STATUS"));
				list.add(i);
			}
			
			return list;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * 데이터 출력의 메인 처리
	 */
	@Override
	public boolean outputDataProcess() {
		// (2) 추출해야하는 데이터의 전체 건수를 확인한다.
		int countAll = getDataCount();
		int countProcessTime = 1;
		
		// (3) 1회 최대 출력 건수 < 전체건수
		if (countOneTimeProcess < countAll) {
			countProcessTime = (int) Math.ceil((double) countAll / countOneTimeProcess);
		}
		
		
		int start = 0;
		int end = countOneTimeProcess;
		for (int i=0; i < countProcessTime; i++) {
			outputDataToFile(getDataList(start, end), i);
			start = start + countOneTimeProcess;
			end = end + countOneTimeProcess;
		}
		
		return false;
	}
	
	/**
	 * 파일명을 생성
	 * @param fileNum
	 * @return
	 */
	protected String makeFileName(int fileNum) {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
		String outputFilePath = OUTPUT_DIR+"/"+OUTPUT_FILE_NAME+"_"+dateFormat.format(date)+"_"+fileNum+EXTENTION;
		return outputFilePath;
	}

	/**
	 * 추출한 데이터를 지정한 데이터형식의 파일로 출력
	 * 
	 * @param list       : 추출데이터가 담기는 리스트
	 * @param processNum : 출력되는 파일번호
	 */
	public abstract void outputDataToFile(List<Item> list, int processNum);

}
