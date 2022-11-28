package net.aonsolutions.aon.api.excel;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TimeControlExcel {
	
	public static void excelTimeControl(Domain domain, OutputStream outputstream, Date startDate, Date endDate, Boolean active) throws Exception  {
		
		Collection<TimeControl> tcList = AON_SOLUTIONS.getTimeControlStream(domain, "", startDate, endDate)
		.filter(f-> f.getTaskHolder()!=null && f.getTaskHolder().isActive().equals(active))
		.collect(Collectors.toCollection(LinkedList::new));

		Workbook workbook = new XSSFWorkbook();

		sheetMonth(tcList, workbook, startDate, endDate);

		sheetWeek(tcList, workbook, startDate, endDate);
		
		sheetDay(tcList,  workbook, startDate, endDate, domain);

        workbook.write(outputstream);
        outputstream.close();

	}
	
	private static void sheetMonth(Collection<TimeControl> tcList, Workbook workbook, Date startDate, Date endDate){
		String group = "MONTH";
		TimeControlGroup timeCG = TimeControlGroup.safeValueOf(group);
		String[] columns = {
				"Trabajador",
				"Año",
				"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", 
				"Agosto", "Septiembre","Octubre", "Noviembre", "Diciembre"
		};
		
		Sheet sheet = workbook.createSheet(group);
		
		//FONT
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		
		//STYLE
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		
		CellStyle formulaCellStyle = workbook.createCellStyle();
		formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		formulaCellStyle.setBorderBottom(BorderStyle.THIN);
		formulaCellStyle.setBorderTop(BorderStyle.THIN);
		formulaCellStyle.setBorderLeft(BorderStyle.THIN);
		formulaCellStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle doubleCellStyle = workbook.createCellStyle();
		doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
		doubleCellStyle.setBorderTop(BorderStyle.THIN);
		doubleCellStyle.setBorderLeft(BorderStyle.THIN);
		doubleCellStyle.setBorderRight(BorderStyle.THIN);

		
		Row rowFirst = sheet.createRow(0);
		Cell cellFirst = rowFirst.createCell(0);
		String[] startDateFormat = UtilsExcel.dateString(startDate);
		cellFirst.setCellValue("RESUMEN AÑO " + startDateFormat[2]);
		cellFirst.setCellStyle(headerCellStyle);
		//create header
		Row headerRow = sheet.createRow(rowFirst.getRowNum()+1);
		for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
		
		int index = headerRow.getRowNum();
		Map<String, Long> listValues = new HashMap<>();
		for (TimeControl tc : tcList) {
			Row row = sheet.createRow(++index);
			int e = UtilsExcel.indexOf(columns, "Trabajador");
			
			row.createCell(e).setCellValue(tc.getTaskHolder().getName());
			
			Cell cellAnio = UtilsExcel.createCellDouble(row, ++e, null, doubleCellStyle);
			
			LinkedList<TimeControl> dts = details(tc, startDate, endDate, timeCG);
			double totalAnio = 0;
			for(TimeControl dt: dts) {
				double value = UtilsExcel.timeDecimals(dt.getTime());
				Cell cell = UtilsExcel.createCellDouble(row, ++e, value, doubleCellStyle);
				listValues.put(CellReference.convertNumToColString(cell.getColumnIndex())+""+(index+1), dt.getTime());
				totalAnio = totalAnio + value;
			}
			cellAnio.setCellValue(totalAnio);
		}
		

		int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
		Row row = sheet.createRow(sheet.getLastRowNum()+1);
		
		Cell totalCell = row.createCell(0);
		totalCell.setCellType(CellType.STRING);
		totalCell.setCellValue("TOTAL");
		totalCell.setCellStyle(headerCellStyle);

		
		for (int i = 1; i < lastColumn; i++) {
			Cell cell = UtilsExcel.createCellDouble(row, i, null, formulaCellStyle);
			cell.setCellType(CellType.FORMULA);
			cell.setCellFormula("sum("+CellReference.convertNumToColString(i)+1+":"+CellReference.convertNumToColString(i)+row.getRowNum()+")");
		}

        for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }
	}
	
	private static void sheetWeek(Collection<TimeControl> tcList, Workbook workbook, Date startDate, Date endDate){
		String group = "WEEK";
		TimeControlGroup timeCG = TimeControlGroup.safeValueOf(group);
		List<Date> startDateList = new ArrayList<Date>();

		Sheet sheet = workbook.createSheet(group);
	
		Row rowFirst = sheet.createRow(0);
		Cell cellFirst = rowFirst.createCell(0);
		cellFirst.setCellValue("RESUMEN POR SEMANAS");
	
		
		Row rowTwo = sheet.createRow(rowFirst.getRowNum()+1);
		Cell cellTwo = rowTwo.createCell(0);
		cellTwo.setCellValue("Trabajador");

		//FONT
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		
		//STYLE
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

		
		CellStyle formulaCellStyle = workbook.createCellStyle();
		formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		formulaCellStyle.setBorderBottom(BorderStyle.THIN);
		formulaCellStyle.setBorderTop(BorderStyle.THIN);
		formulaCellStyle.setBorderLeft(BorderStyle.THIN);
		formulaCellStyle.setBorderRight(BorderStyle.THIN);
		CellStyle doubleCellStyle = workbook.createCellStyle();
		doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
		doubleCellStyle.setBorderTop(BorderStyle.THIN);
		doubleCellStyle.setBorderLeft(BorderStyle.THIN);
		doubleCellStyle.setBorderRight(BorderStyle.THIN);
		
		cellFirst.setCellStyle(headerCellStyle);

		cellTwo.setCellStyle(headerCellStyle);


		//create header
		Row headerRow = sheet.createRow(rowTwo.getRowNum()+1);
	
		int index = headerRow.getRowNum();

		for (TimeControl tc : tcList) {
			Row row = sheet.createRow(index++);
			int e = 0;
			row.createCell(e).setCellValue(tc.getTaskHolder().getName());
			LinkedList<TimeControl> dts = details(tc, startDate, endDate, timeCG);
			for(TimeControl dt: dts) {
				startDateList.add(dt.getStartDate());
				double value = UtilsExcel.timeDecimals(dt.getTime());
				UtilsExcel.createCellDouble(row, ++e, value, doubleCellStyle);
			}
		}
		
		startDateList = (ArrayList<Date>) startDateList.stream().distinct().collect(Collectors.toList());

		for(int i = 0; i < startDateList.size(); i++) {
			int weekNumber = UtilsExcel.getWeekOfYear( startDateList.get(i) );
			Date[] dat = AonDateUtils.getWeekDateRange(startDateList.get(i));
			
            Cell cell = rowFirst.createCell(i+1);
            cell.setCellValue("SEMANA " + weekNumber);
            cell.setCellStyle(headerCellStyle);
            
            Cell cell2 = rowTwo.createCell(i+1);
            cell2.setCellValue( startDateList.get(i).toString() );
            cell2.setCellValue(AonDateUtils.simpleFormat(dat[0]) +" - "+ AonDateUtils.simpleFormat(dat[1]));
            cell2.setCellStyle(headerCellStyle);
        }
		
		int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
		Row row = sheet.createRow(sheet.getLastRowNum()+1);
		Cell totalCell = row.createCell(0);
		totalCell.setCellType(CellType.STRING);
		totalCell.setCellValue("TOTAL");
		totalCell.setCellStyle(headerCellStyle);

		for (int i = 1; i < lastColumn; i++) {
			Cell cell = UtilsExcel.createCellDouble(row, i, null, formulaCellStyle);
			cell.setCellType(CellType.FORMULA);
			cell.setCellFormula("sum("+CellReference.convertNumToColString(i)+1+":"+CellReference.convertNumToColString(i)+row.getRowNum()+")");
		}
		
        for(int i = 0; i < rowFirst.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }
	}
	
	private static void sheetDay(Collection<TimeControl> tcList, Workbook workbook, Date startDate, Date endDate, Domain domain){
		String[] columns = {"Fecha", "Hora","Estado","Ubicaci\u00F3n"};
		Timestamp startTimestamp = new Timestamp(startDate.getTime());
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		//FONT
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		
		//STYLE
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		
		CellStyle formulaCellStyle = workbook.createCellStyle();
		formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		formulaCellStyle.setBorderBottom(BorderStyle.THIN);
		formulaCellStyle.setBorderTop(BorderStyle.THIN);
		formulaCellStyle.setBorderLeft(BorderStyle.THIN);
		formulaCellStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle doubleCellStyle = workbook.createCellStyle();
		doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
		doubleCellStyle.setBorderTop(BorderStyle.THIN);
		doubleCellStyle.setBorderLeft(BorderStyle.THIN);
		doubleCellStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle cellStyleDate = workbook.createCellStyle();
		cellStyleDate.setAlignment(HorizontalAlignment.CENTER);
		
		CellStyle cellStyleTime = workbook.createCellStyle();
		cellStyleTime.setAlignment(HorizontalAlignment.RIGHT);
		
		//LINK STYLE
		Font hlink_font = workbook.createFont();
		hlink_font.setUnderline(Font.U_SINGLE);
		hlink_font.setColor(IndexedColors.BLUE.getIndex());
		CellStyle hlink_style = workbook.createCellStyle();
		hlink_style.setFont(hlink_font);
		hlink_style.setAlignment(HorizontalAlignment.CENTER);
				
				
		CreationHelper createHelper = workbook.getCreationHelper();
		
		Hyperlink hrefCoordinate = createHelper.createHyperlink(HyperlinkType.URL);
		
//		cellStyleDate.setDataFormat(
//			    createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
		
		for (TimeControl tc : tcList) {
			Sheet sheet = workbook.getSheet(tc.getTaskHolder().getName());
			if(sheet == null)
			    sheet = workbook.createSheet(tc.getTaskHolder().getName());
			
			Row rowFirst = sheet.createRow(0);
			Cell cellFirst = rowFirst.createCell(0);
//			String[] startDateFormat = UtilsExcel.dateString(startDate);
			cellFirst.setCellValue("DETALLE POR D\u00EDA".toUpperCase());
			cellFirst.setCellStyle(headerCellStyle);
			//create header
			Row headerRow = sheet.createRow(rowFirst.getRowNum()+1);
			for(int i = 0; i < columns.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(columns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
//			
			int index = headerRow.getRowNum();
//			Map<String, Long> listValues = new HashMap<String, Long>();

			Collection<TimeControlDetail> dts = AON_SOLUTIONS.getTimeControlDetailStream(domain, "", f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getDateProperty().ge(startTimestamp))
			.and(f.getDateProperty().le(endTimestamp))
			.and(f.getTaskHolderProperty().eq(tc.getTaskHolder().getId()))).collect(Collectors.toCollection(LinkedList::new));

		
			for(TimeControlDetail dt: dts) {
				int cells = 0;
				Location location = dt.getLocation();
				String textLocation = dt.getLocation().getDescription();
				if(textLocation==null && location.getCoordinates().getLatitude()!=null) {
					textLocation = "https://maps.google.es/maps?q="+location.getCoordinates().getLatitude()+","+location.getCoordinates().getLongitude()+"&z=16&output=embed&hl=es";
					hrefCoordinate.setAddress(textLocation);
				}
					
				if(dt.getDate()!=null) {
					String[] dateString = UtilsExcel.dateString(new Date(dt.getDate().getTime()));
					Row row = sheet.createRow(++index);
					Cell cellDate = row.createCell(cells++);
					cellDate.setCellValue(dateString[0]+"/"+dateString[1]+"/"+dateString[2]);
					cellDate.setCellStyle(cellStyleDate);
					Cell cellTime = row.createCell(cells++);
					cellTime.setCellValue(dateString[3]+":"+dateString[4]);
					cellTime.setCellStyle(cellStyleTime);
					Cell cellStatus = row.createCell(cells++);
					cellStatus.setCellValue(getStatusText(dt.getStatus().name().toLowerCase()));
					cellStatus.setCellStyle(cellStyleDate);
					Cell cellLocation = row.createCell(cells++);
					
					if(hrefCoordinate.getAddress()!=null) {
						cellLocation.setCellStyle(hlink_style);
						cellLocation.setHyperlink(hrefCoordinate);
						cellLocation.setCellValue("Ubicaci\u00F3n desconocida");
					} else {
						cellLocation.setCellValue(textLocation);
					}
				}
			}

	        for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
	            sheet.autoSizeColumn(i);
	        }
        }
	}
	
	private static LinkedList<TimeControl> details(TimeControl timeControl, Date startDate, Date endDate, TimeControlGroup group) {
		LinkedList<TimeControl> tcList = new LinkedList<>();
		if(TimeControlGroup.DAY.equals(group)) {
			Date date = startDate;
			while(date.compareTo(endDate) <= 0 ) {
				Date aDate = AonDateUtils.getDateWithoutTime(date);
				Date bDate = AonDateUtils.addDays(aDate, 1);
				Date cDate = AonDateUtils.addSeconds(bDate, -1);
				TimeControl tc = buildTimeControl(timeControl.getDetail().stream().filter(f -> (f.getDate().compareTo(aDate) >= 0 && f.getDate().compareTo(cDate) <= 0)));
				tc.setStartDate(aDate);
				tcList.add(tc);
				date = AonDateUtils.addDays(date, 1);
			}
		} else if(TimeControlGroup.WEEK.equals(group)) {
			Date date = AonDateUtils.getFirstDayOfWeek(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				Date aDate = date;
				Date bDate = AonDateUtils.addDays(date, 7);
				Date cDate = AonDateUtils.addSeconds(bDate, -1);
				TimeControl tc = buildTimeControl(timeControl.getDetail().stream().filter(f -> (f.getDate().compareTo(aDate) >= 0 && f.getDate().compareTo(cDate) <= 0)));
				tc.setStartDate(aDate);
				tcList.add(tc);
				date = AonDateUtils.addWeeks(date, 1);
			}
		} else if(TimeControlGroup.MONTH.equals(group)) {
			Date date = AonDateUtils.getMonthFirstDay(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				int month = AonDateUtils.getMonth(date);
				int year = AonDateUtils.getYear(date);
				TimeControl tc = buildTimeControl(timeControl.getDetail().stream().filter(f -> (
						AonDateUtils.getMonth(f.getDate()) == month
						&& AonDateUtils.getYear(f.getDate()) == year)));
				tcList.add(tc);
				date = AonDateUtils.addMonths(date, 1);
			};
		} else if(TimeControlGroup.YEAR.equals(group)) {
			Date date = AonDateUtils.getYearFirstDay(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				int year = AonDateUtils.getYear(date);
				TimeControl tc = buildTimeControl(timeControl.getDetail().stream().filter(f -> AonDateUtils.getYear(f.getDate()) == year));
				tcList.add(tc);
				date = AonDateUtils.addYears(date, 1);
			}
		}
		return tcList;
	}
	
	private static TimeControl buildTimeControl(Stream<TimeControlDetail> details) {
		TimeControl tc = new TimeControl().setTime(0L);
		details.forEach(r -> {
			if(tc.getStatus() == null) {
				tc.setInDate(AonDateUtils.getDateWithoutTime(r.getDate()));
			}
			if(TimeControlStatus.IN.equals(r.getStatus())) {
				tc.setInDate(r.getDate());
				tc.setStatus(r.getStatus());
			} else if(tc.getInDate() != null){
				tc.setTime(tc.getTime() + r.getDate().getTime() - tc.getInDate().getTime());
				tc.setInDate(null);
				tc.setStatus(r.getStatus());
			}

			if(tc.getTaskHolder() == null || tc.getTaskHolder().getId() == null) {
				tc.setTaskHolder(r.getTaskHolder());
			}
			tc.getDetail().add(r);
		});
		return tc;
	}

	private static String getStatusText(String status) {
		String newStatus ="";
		switch (status) {
		case "in":
			newStatus  = "Entrada";
			break;
		case "out":
			newStatus  = "Salida";
			break;
		case "pause":
			newStatus  = "Pausa";
			break;
		}
		return newStatus;
	}
}
