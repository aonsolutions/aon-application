package com.code.aon.webservice.stat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.issues.Label;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.AonUrlApi;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DownloadTaskStatExcel", urlPatterns = {"/aon_gwt_aio/download_task_stat_excel/*"})
public class DownloadTaskStatExcelServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer cont;
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
		System.out.println("GET METHOD");
		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String userName = parameters.get("login");
		
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));		
	 	
        HSSFWorkbook libro = new HSSFWorkbook();
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        HSSFSheet hoja = libro.createSheet("Tareas");
                
        Row fila = hoja.createRow(0);
        
        fila.setHeightInPoints(16);
        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM); 
       	
        CellStyle style2 = libro.createCellStyle();
        Font font2 = libro.createFont();
        font.setFontHeightInPoints((short)12);
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.RIGHT);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		
        CellStyle style3 = libro.createCellStyle();
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		style3.setBorderTop(BorderStyle.THIN);
		
		Cell c0 = fila.createCell(0);
    	c0.setCellValue("Numero");
    	c0.setCellStyle(style);

    	Cell c1 = fila.createCell(1);
    	c1.setCellValue("Tabla");
    	c1.setCellStyle(style);
    	
    	Cell c2 = fila.createCell(2);
    	c2.setCellValue("Titulo");
    	c2.setCellStyle(style);
    	
    	Cell c3 = fila.createCell(3);
    	c3.setCellValue("Descripcion");
    	c3.setCellStyle(style);
    	
    	Cell c4 = fila.createCell(4);
    	c4.setCellValue("Estado");
    	c4.setCellStyle(style);
    	
    	Cell c5 = fila.createCell(5);
    	c5.setCellValue("Fecha Inicio");
    	c5.setCellStyle(style);
    	
    	Cell c6 = fila.createCell(6);
    	c6.setCellValue("Fecha Fin");
    	c6.setCellStyle(style);
    	
    	Cell c7 = fila.createCell(7);
    	c7.setCellValue("Empresa");
    	c7.setCellStyle(style);
    
    	Cell c8 = fila.createCell(8);
    	c8.setCellValue("Prioridad");
    	c8.setCellStyle(style);
    	
    	Cell c9 = fila.createCell(9);
    	c9.setCellValue("Tipo");
    	c9.setCellStyle(style);
    	
    	Cell c10 = fila.createCell(10);
    	c10.setCellValue("Grupo Trabajo");
    	c10.setCellStyle(style);
    	
    	Cell c11 = fila.createCell(11);
    	c11.setCellValue("Operario");
    	c11.setCellStyle(style);

    	Cell c12 = fila.createCell(12);
    	c12.setCellValue("Etiquetas");
    	c12.setCellStyle(style);
    	
    	cont = 1;
    	
    	// TASK HOLDER MAP
    	Map<Integer, String> taskHolderMap = AON.getTaskHolderStream(domain.getName(), domain.getId(), userName, f -> f.getDomainProperty().eq(domain.getId()))
    			.collect(Collectors.toMap(TaskHolder::getId, TaskHolder::getName));
    	StatParams statParams = new StatParams().setIssueFilter(Utils.getFilter(parameters));
    	statParams.setFrom(statParams.getIssueFilter().getFrom());
    	statParams.setTo(statParams.getIssueFilter().getTo());
    	AON.getStatTaskStream(domain.getName(), domain.getId(), userName, statParams).forEach(task -> {
			Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), userName, task.getRegistry());
			Workgroup workgroup = AON.getWorkgroup(domain.getName(), domain.getId(), userName, task.getWorkgroup());
			
			LinkedList<Tag> label = AON.getTaskLabelList(domain.getName(), domain.getId(), userName, f->f.getTaskProperty().eq(task.getId()));
			LinkedList<Label> labels = label.stream().filter(l -> TagType.TASK_TYPE.equals(l.getType())).map(new TagToLabelFiller(domain, userName))
					.collect(Collectors.toCollection(LinkedList::new)); 
			Label type = label.stream().filter(l -> TagType.TASK_TYPE.equals(l.getType())).map(new TagToLabelFiller(domain, userName))
					.findFirst().orElse(new Label());
			
	        Row taskrow = hoja.createRow(cont++);
	        
	        
	        
			Cell ct0 = taskrow.createCell(0);
	    	ct0.setCellValue("#"+task.getNumber());  //"Numero"
	    	ct0.setCellStyle(style3);
	    	
			Cell ct1 = taskrow.createCell(1);
	    	ct1.setCellValue("task");  //"Tabla"
	    	ct1.setCellStyle(style3);
	    	
	    	Cell ct2 = taskrow.createCell(2);
	    	ct2.setCellValue(task.getDescription());	//"Titulo"
	    	ct2.setCellStyle(style3);
	    	
	    	Cell ct3 = taskrow.createCell(3);
	    	ct3.setCellValue(task.getComments());	//"description"
	    	ct3.setCellStyle(style3);
	    	
	    	Cell ct4 = taskrow.createCell(4);
	    	ct4.setCellValue(TaskStatus.values()[task.getStatus()].getESName());	//"Estado"
	    	ct4.setCellStyle(style3);
	    	
	    	Cell ct5 = taskrow.createCell(5);
	    	ct5.setCellValue(task.getStartDate().toString());	//"Fecha inicio"
	    	ct5.setCellStyle(style3);
	    	
	    	Cell ct6 = taskrow.createCell(6);
	    	ct6.setCellValue(task.getEndDate() != null ? task.getEndDate().toString() : "" );	//"Fecha fin"
	    	ct6.setCellStyle(style3);
	    	
	    	Cell ct7 = taskrow.createCell(7);
	    	ct7.setCellValue(enterprise.getName());	//"Empresa"
	    	ct7.setCellStyle(style3);	    	
	    	
	    	Cell ct8 = taskrow.createCell(8);
	    	ct8.setCellValue(Priority.values()[task.getPriority()].getName());	//"Prioridad"
	    	ct8.setCellStyle(style3);
	    	
	    	Cell ct9 = taskrow.createCell(9);
	    	ct9.setCellValue(type.getName()); 	//"Tipo"
	    	ct9.setCellStyle(style3);
	    	
	    	Cell ct10 = taskrow.createCell(10);
	    	ct10.setCellValue(workgroup.getDescription()); 	//"Grupo Trabajo"
	    	ct10.setCellStyle(style3);
	    	
	    	Cell ct11 = taskrow.createCell(11);
	    	ct11.setCellValue(taskHolderMap.get(task.getTaskHolder())); //"Operario"
	    	ct11.setCellStyle(style3);

	    	for (Integer h = 0; h < labels.size(); h++) {
		    	Cell ct12 = taskrow.createCell(h+12);
		    	ct12.setCellValue(labels.get(h).getName());	//"Etiquetas"
		    	ct12.setCellStyle(style3);				
			}
	    	
	    	AON.getTaskEventStream(domain.getName(), domain.getId(), userName, task.getId()).forEach(event ->{
	    		Row eventrow = hoja.createRow(cont++);
	    		
	    		Cell ce0 = eventrow.createCell(0);
		    	ce0.setCellValue("#"+task.getNumber());  //"Numero"
		    	ce0.setCellStyle(style3);
		    	
				Cell ce1 = eventrow.createCell(1);
		    	ce1.setCellValue("event");  //"Tabla"
		    	ce1.setCellStyle(style3);
		    
		    	Cell ce3 = eventrow.createCell(3);
		    	ce3.setCellValue(event.getEvent());	//"description"
		    	ce3.setCellStyle(style3);
		    
		    	Cell ce5 = eventrow.createCell(5);
		    	ce5.setCellValue(event.getCreationDate().toString());	//"Fecha inicio"
		    	ce5.setCellStyle(style3);
	    	});
	    	
	    	AON.getTaskCommentStream(domain.getName(), domain.getId(), userName, task.getId()).forEach(comment ->{
		        Row commentrow = hoja.createRow(cont++);
	    		
	    		Cell cc0 = commentrow.createCell(0);
		    	cc0.setCellValue("#"+task.getNumber());  //"Numero"
		    	cc0.setCellStyle(style3);
		    	
				Cell cc1 = commentrow.createCell(1);
		    	cc1.setCellValue("comment");  //"Tabla"
		    	cc1.setCellStyle(style3);
		    
		    	Cell cc3 = commentrow.createCell(3);
		    	cc3.setCellValue(comment.getComment());	//"description"
		    	cc3.setCellStyle(style3);
		    
		    	Cell cc5 = commentrow.createCell(5);
		    	cc5.setCellValue(comment.getCreationDate().toString());	//"Fecha inicio"
		    	cc5.setCellStyle(style3);
	    	});
		});
    	
    	for(Integer i = 0; i < 24; i++){
    		hoja.autoSizeColumn(i);
    	}
     
        libro.write(archivo);  
        byte[] data = archivo.toByteArray();
        archivo.close();
        libro.close();

        Utils.giveBackData(resp, data , "tareas.xls");
    }
	
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	
	
	private static class TagToLabelFiller implements Function<Tag, Label> {
		private Domain domain;
		private String userName;
		
		public TagToLabelFiller(Domain domain, String userName) {
			this.domain = domain;
			this.userName = userName;
		}
		
		@Override
		public Label apply(Tag r) {
			return new Label().setId(r.getId())
					.setName(r.getName())
					.setColor(r.getColor())
					.setUrl(AonUrlApi.AONTEST.getUrl() + "ms/repos/" + userName + "/" + domain.getName() + "/labels/" + r.getName());  
		}
	}
}
