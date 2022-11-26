package net.aonsolutions.aon.api.excel;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.task.TaskFilter;

public class TaskExcel {
	
	public static void buildExcel(OutputStream outputstream, AonApiData api) throws Exception  {
			
		
		List<Task> tasks = AON_SOLUTIONS.getTaskParentOrChildStream(api.getDomain(), api.getUser(), f -> TaskFilter.task(api, f, api.getDomain(), new Customer())).collect(Collectors.toList());

		Workbook workbook = new XSSFWorkbook();

		setColumn(tasks, workbook);

        workbook.write(outputstream);
        outputstream.close();

	}
	
	private static void setColumn(List<Task> tasks, Workbook workbook){
		String group = "MONTH";

		String[] columns = {
				"Fecha",
				"Numero",
				"Vinculo", 
				"Asunto", 
				"Descripcion", 
				"Empresa", 
				"Asignado", 
				"Ultima actualizacion",
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
		//String[] startDateFormat = UtilsExcel.dateString(startDate);
		//cellFirst.setCellValue("RESUMEN AÑO " + startDateFormat[2]);
		cellFirst.setCellStyle(headerCellStyle);
		//create header
		Row headerRow = sheet.createRow(rowFirst.getRowNum()+1);
		for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
		
		int index = headerRow.getRowNum();
		Map<String, Long> listValues = new HashMap<String, Long>();
		for (Task task : tasks) {
			Row row = sheet.createRow(++index);
			int e = UtilsExcel.indexOf(columns, "Trabajador");
			
			//row.createCell(e).setCellValue(tc.getTaskHolder().getName());
			
			Cell cellAnio = UtilsExcel.createCellDouble(row, ++e, null, doubleCellStyle);
			
			//LinkedList<TimeControl> dts = details(tc, startDate, endDate, timeCG);
			//double totalAnio = 0;
			//for(TimeControl dt: dts) {
			//	double value = UtilsExcel.timeDecimals(dt.getTime());
			//	Cell cell = UtilsExcel.createCellDouble(row, ++e, value, doubleCellStyle);
			//	listValues.put(CellReference.convertNumToColString(cell.getColumnIndex())+""+(index+1), dt.getTime());
			//	totalAnio = totalAnio + value;
			//}
			//cellAnio.setCellValue(totalAnio);
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
