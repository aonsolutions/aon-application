package net.aonsolutions.aon.api.excel;

import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.task.TaskFilter;
import net.aonsolutions.aon.api.servlet.task.TaskUtils;

public class TaskExcel {
	
	 private TaskExcel() {
		  throw new IllegalStateException("TaskExcel class");
	 }

	private static String[] columns = {
			"Fecha",
			"N\u00famero",
			"V\u00ednculo", 
			"Tipo", 
			"Estado", 
			"Asignado", 
			"Empresa",
			"Asunto", 
			"\u00daltima actualizaci\u00f3n",
	};
	
	public static void buildExcel(OutputStream outputstream, AonApiData api) throws Exception  {
		List<Task> tasks = AON_SOLUTIONS.getTaskParentOrChildStream(api.getDomain(), api.getUser(), 
			f -> TaskFilter.task(api, f, api.getDomain(), new Customer())
		)
		.collect(Collectors.toCollection(LinkedList::new));
		
		buildExcel(outputstream, tasks);
	}

	public static void buildExcel(OutputStream outputstream, List<Task> tasks) throws Exception {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = buildHeaders(workbook);
		
		tasks.forEach(task->{
			buildColumn(sheet, task, null);
		});		
		
	    for(int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(outputstream);
        outputstream.close();
	}
	
	private static Sheet buildHeaders(Workbook workbook){
		
		Sheet sheet = workbook.createSheet();
		
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
		
		Row headerRow = sheet.createRow(0);
		for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
		
		return sheet;
	}
	
	private static void buildColumn(Sheet sheet, Task task, Integer parentNumber){		
		int cell = 0;
		
		Row row = sheet.createRow(sheet.getLastRowNum()+1);
		
		row.createCell(cell++).setCellValue(getDateString(task.getStartDate())); // DATE

		row.createCell(cell++).setCellValue(TaskUtils.parseNumber(task.getNumber()));// NUMBER
		
		Cell parentRow = row.createCell(cell++);
		
		if(parentNumber!=null) {
			parentRow.setCellValue(TaskUtils.parseNumber(parentNumber)); // NUMBER PARENT
		}

		row.createCell(cell++).setCellValue(task.getSource().getESName()); // SOURCE
		
		row.createCell(cell++).setCellValue(task.getStatus().getESName()); // STATUS
		
		row.createCell(cell++).setCellValue(getAssigned(task)); // TASK_HOLDER
		
		row.createCell(cell++).setCellValue(getEnterpriseName(task.getRegistry())); // ENTERPRISE
		
		row.createCell(cell++).setCellValue(getTitle(task.getTitle())); // ISSUE
		
		row.createCell(cell++).setCellValue(getDateString(task.getModificationDate()!=null ? task.getModificationDate() : task.getCreationDate() )); // LAST UPDATE
		
		if(task.childExist()) { // CHILD
			task.getChilds().forEach(child ->{
				buildColumn(sheet, child, task.getNumber());
			});
		}
	}
	
	private static String getTitle(String title) {
		 return title!=null ? title : "";
	}
	
	private static String getDateString(Date date) {
		 return AonDateUtils.format(date, "dd/MM/yyyy HH:mm");
	}
	
	private static String getAssigned(Task task) {
		 if(task.getTaskHolder().getId()!=null) {
			 return task.getTaskHolder().getName();
		 }  else if(task.getWorkgroup()!=null && task.getWorkgroup().getId()!=null) {
			 return task.getWorkgroup().getDescription();
		 }
		 return "";
	}
	
	private static String getEnterpriseName(Registry registry) {
		 return registry!=null ? registry.getName() : "";
	}
	
}
