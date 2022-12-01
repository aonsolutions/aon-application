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
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.task.TaskFilter;
import net.aonsolutions.aon.api.servlet.task.TaskUtils;

public class TaskExcel {
	
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	private static final String START_DATE = "startDate"; 
	private static final String END_DATE = "endDate"; 
	
	 private TaskExcel() {
		  throw new IllegalStateException("TaskExcel class");
	 }

	 private static String[] columns = {
		"Fecha",
		"N\u00famero",
		"V\u00ednculo", 
		"Tipo", 
		"Etiquetas", 
		"Estado", 
		"Asignado", 
		"Empresa",
		"Asunto", 
		"\u00daltima actualizaci\u00f3n",
	 };
	
	public static void buildExcel(OutputStream outputstream, AonApiData api) throws Exception  {
		
		JSONObject params = api.getData();
		
		onValidate(params);
		
		List<Task> tasks = AON_SOLUTIONS.getTaskParentOrChildStream(api.getDomain(), api.getUser(), 
			f -> TaskFilter.task(api, f, api.getDomain(), new Customer()),
			true
		)
		.collect(Collectors.toCollection(LinkedList::new));
		
		Date startDate = AonDateUtils.parse(params.optString(START_DATE), FORMAT_DATE);
		
		Date endDate = AonDateUtils.parse(params.optString(END_DATE), FORMAT_DATE);

		buildExcel(outputstream, tasks, startDate, endDate);
	}

	public static void buildExcel(OutputStream outputstream, List<Task> tasks, Date startDate, Date endDate) throws Exception {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = buildHeaders(workbook, startDate, endDate);
		
		tasks.forEach(task->{
			buildColumn(sheet, task, null);
		});		
		
	    for(int i = 0; i < sheet.getRow(1).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }
	    
        workbook.write(outputstream);
        outputstream.close();
	}
	
	private static Sheet buildHeaders(Workbook workbook, Date startDate, Date endDate){
		
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
		
		Cell cellFirst = sheet.createRow(0).createCell(0);
		
		cellFirst.setCellValue("Fecha: "+getDateBetweenString(startDate, endDate));
		cellFirst.setCellStyle(headerCellStyle);
		
		Row headerRow = sheet.createRow(1);
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
		
		row.createCell(cell++).setCellValue(getTags(task.getTags())); // TAGS
		
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
	
	private static String getDateBetweenString(Date startDate, Date endDate) {
		 return AonDateUtils.format(startDate, "dd/MM/yyyy")+ " - "+AonDateUtils.format(endDate, "dd/MM/yyyy");
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
	
	private static String getTags(List<Tag> tags) {
		return !tags.isEmpty() 
		? tags.stream().filter(t-> t.getId()!=null).map(Tag::getName).collect(Collectors.joining(", ")) 
		: "";
	}
	
	/**
	 * 
	 * @param validate data required
	 * @throws Exception
	 */
	private static void onValidate(JSONObject params) throws Exception {
		String error = null;
		if(params.isNull(START_DATE)) {
			error = "Fecha Inicio requerida";
		} else if(params.isNull(END_DATE)) {
			error = "Fecha fin requerida";
		} 
		
		if(error!=null) {
			throw new Exception(error);
		}
	}
}
