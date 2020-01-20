package com.esferalia.aon.gwt.template.server.projectCommercial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectCommercialImport {

	public static ProjectCommercialImport getInstance() {
		return new ProjectCommercialImport();
	}

	public ProjectCommercialImport() {

	}
	ProjectCommercial pc; 
	LinkedList<ProjectType> types;
	LinkedList<Seller> sellers;
	public LinkedList<ProjectCommercial> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet sheet = workbook.getSheet("OPERACIONES COMERCIALES");

			// CUSTOMER

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<ProjectCommercial> list = new LinkedList<>();
			types = new LinkedList<>();
			sellers = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				pc = new ProjectCommercial();
				pc.setDomain(domain.getId());
				pc.setCommercial(true);
				pc.setActive(true);
				
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0) {
					list.add(pc);
				}
			});

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private Object getObjectValue(Cell cell){
		if(CellType.STRING == cell.getCellTypeEnum())
			return cell.getStringCellValue();
		if(CellType.NUMERIC == cell.getCellTypeEnum())
			return cell.getNumericCellValue();
		if(CellType.FORMULA == cell.getCellTypeEnum())
			return cell.getCellFormula();
		if(CellType.BOOLEAN == cell.getCellTypeEnum()) {
			return cell.getBooleanCellValue() ? 1.0 : 0.0;
		}
		return null;
	}

	
	private Byte getSource(String source ){
		if("Call center".equalsIgnoreCase(source))
			return 0;
		else if("Visita comercial".equalsIgnoreCase(source))
			return 1;
		else if("Prescripcion".equalsIgnoreCase(source))
			return 2;
		else if("web".equalsIgnoreCase(source))
			return 3;
		else if("anuncio".equalsIgnoreCase(source))
			return 4;
		else if("email".equalsIgnoreCase(source))
			return 5;
		else if("presentacion".equalsIgnoreCase(source))
			return 6;
		else if("recomendacion".equalsIgnoreCase(source))
			return 7;
		return null;
		
	}
	
	private Byte getStatus(String status ){
		if("Pendiente".equalsIgnoreCase(status))
			return 0;
		else if("Aprobado".equalsIgnoreCase(status))
			return 1;
		else if("Rechazado".equalsIgnoreCase(status))
			return 2;
		else if("Cerrado".equalsIgnoreCase(status))
			return 3;
		return null;		
	}
	

 	private void check(Domain domain , String login, String title, Cell cell) {
		Object o = getObjectValue(cell);
		if(o == null) return;
		String column = getColumn(cell.getColumnIndex());
		Integer row = cell.getRowIndex() + 1;
		if("Nombre".equalsIgnoreCase(title)) {
			if(o.toString() == null || "".equals(o.toString())) {
				System.out.println("A" + o.toString() + "A");
			}
			pc.setName( o.toString() == null || "".equals(o.toString()) ? "-" : o.toString());
			return;
		}

		if("Origen".equalsIgnoreCase(title)) {
			pc.setSource(getSource(o.toString()));
			return;
		}
		
		if("Tipo expediente".equalsIgnoreCase(title)) {
			for (ProjectType projectType : types) {
				if(o.toString().equals(projectType.getDescription())) {
					pc.setProjectTypeId(projectType.getId());
					return ;
				}
			}
			ProjectType pt = AON.getProjectType(domain.getName(), domain.getId(), login, o.toString());
			types.add(pt);
			pc.setProjectTypeId(pt.getId());
			return ;
		}
		if("Cliente Potencial".equalsIgnoreCase(title)) {
			Target target = AON.getTarget(domain.getName(), domain.getId(), login, f -> f.getDocumentProperty().eq(o.toString())).get();
			pc.setTarget(target.getId());
			pc.setRegistryId(target.getId());
			return ;
		}
		
		if("comercial".equalsIgnoreCase(title)) {
			for (Seller seller : sellers) {
				if(o.toString().equals(seller.getRegistryName())) {
					pc.setSeller(seller.getId());
					return ;
				}
			}
			Seller seller = AON.getSeller(domain.getName(), domain.getId(), login, f -> f.getNameProperty().eq(o.toString()));
			sellers.add(seller);
			pc.setSeller(seller.getId());
			return ;
		}
		if("fecha".equalsIgnoreCase(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			pc.setDate(date);
			return;
		}
		
		if("estado".equalsIgnoreCase(title)) {
			pc.setStatus(getStatus(o.toString()));
			return;
		}
		
		if("comentarios".equalsIgnoreCase(title)) {
			pc.setComments(pc.getComments() != null ? pc.getComments() + " " + o.toString() : o.toString());
			return;
		}
		
		if("fecha estado".equalsIgnoreCase(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			pc.setStatusDate(date);
			return;
		}
	}

	public void insertProjectCommercial(Domain domain, User user,LinkedList<ProjectCommercial> pcs) {
		pcs.stream().forEach(pc -> {
			System.out.println("PROJECT COMMERCIAL: ");
			System.out.println("Name: " + pc.getName());
			System.out.println("Domain: " + pc.getDomain());
			
			if(pc.getName() == null) {
				pc.setName("-");
			}
			pc.setId(AON.insertProject(domain.getName(), domain.getId(), user.getLogin(), pc));
			AON.insertProjectCommercial(domain.getName(), domain.getId(), user.getLogin(), pc);
		});
	}

	private String getColumn(Integer c) {
		if(c == 0) return "A";
		if(c == 1) return "B";
		if(c == 2) return "C";
		if(c == 3) return "D";
		if(c == 4) return "E";
		if(c == 5) return "F";
		if(c == 6) return "G";
		if(c == 7) return "H";
		if(c == 8) return "I";
		if(c == 9) return "J";
		if(c == 10) return "K";
		if(c == 11) return "L";
		if(c == 12) return "M";
		if(c == 13) return "N";
		if(c == 14) return "O";
		if(c == 15) return "P";
		if(c == 16) return "Q";
		if(c == 17) return "R";
		if(c == 18) return "S";
		if(c == 19) return "T";
		if(c == 20) return "U";
		if(c == 21) return "V";
		if(c == 22) return "W";
		if(c == 23) return "X";
		if(c == 24) return "Y";
		if(c == 25) return "Z";
		if(c == 26) return "AA";
		if(c == 27) return "AB";
		if(c == 28) return "AC";
		if(c == 29) return "AD";
		if(c == 30) return "AE";
		if(c == 31) return "AF";
		if(c == 32) return "AG";
		if(c == 33) return "AH";
		if(c == 34) return "AI";
		if(c == 35) return "AJ";
		if(c == 36) return "AK";
		if(c == 37) return "AL";
		if(c == 38) return "AM";
		if(c == 39) return "AN";
		if(c == 40) return "AO";
		if(c == 41) return "AP";
		if(c == 42) return "AQ";
		if(c == 43) return "AR";
		if(c == 44) return "AS";
		if(c == 45) return "AT";
		if(c == 46) return "AU";
		if(c == 47) return "AV";
		if(c == 48) return "AW";
		if(c == 49) return "AX";
		if(c == 50) return "AY";
		if(c == 51) return "AZ";
		return c.toString();
	}
}
