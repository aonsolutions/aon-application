package com.esferalia.aon.gwt.template.server.projectCommercial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.security.User;

public class CustomerIbanImport {

	public static CustomerIbanImport getInstance() {
		return new CustomerIbanImport();
	}

	public CustomerIbanImport() {

	}
	CustomerIban ci;
	public LinkedList<CustomerIban> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet sheet = workbook.getSheetAt(0);

			// CUSTOMER

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<CustomerIban> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row -> {
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				ci = new CustomerIban();
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0) {
					list.add(ci);
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
	
	public LinkedList<CustomerIban> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);

			XSSFSheet sheet = workbook.getSheetAt(0);

			// CUSTOMER

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<CustomerIban> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row -> {
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				ci = new CustomerIban();
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0) {
					list.add(ci);
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

	private void check(Domain domain , String login, String title, Cell cell) {
		Object o = getObjectValue(cell);
		if(o == null) return;
		if("Nombre".equalsIgnoreCase(title)) {
			ci.setName( o.toString() == null || "".equals(o.toString()) ? "-" : o.toString());
			return;
		}

		if("N.I.F.".equalsIgnoreCase(title)) {
			ci.setDocument(o.toString());
			return;
		}

		if("cif".equalsIgnoreCase(title)) {
			ci.setCif(o.toString());
			return;
		}
		
		if("cuenta bancaria".equalsIgnoreCase(title)) {
			ci.setIban(o.toString() == null || "".equals(o.toString()) ? null : o.toString().replace(" ", ""));
			return;
		}
	}

	public void insertCustomerIban(Domain domain, User user,LinkedList<CustomerIban> cis) {
		PayMethod paymethod = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), "GIRO");
		cis.stream().forEach(ci -> {	
			Customer c = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(ci.getCif())));
			if(c != null && c.getId() != null) {
				if(ci.getIban() != null) {
					String bic = BankBic11.getBankBic11(ci.getIban().substring(4,8)).getBic();
					RegistryBank rbank = new RegistryBank()
							.setDomain(domain.getId())
							.setRegistry(c.getId())
							.setBankAccount(ci.getIban())
							.setActive(true)
							.setBic(bic);
					rbank = AON.insertRBank(domain.getName(), domain.getId(), user.getLogin(), rbank);
					RegistryPayMethod rpaymethod = new RegistryPayMethod()
							.setDomain(domain.getId())
							.setRegistry(c.getId())
							.setRbank(rbank.getId())
							.setPayMethod(paymethod.getId())
							.setNumberOfPymnts((short) 1)
							.setDaysBetwenPymnts((short)0)
							.setDaysToFirstPymnt((short) 0)
							.setPymnt_days("");				
					AON.insertRPayMethod(domain.getName(), domain.getId(), user.getLogin(), rpaymethod);
				}
				RDirStaff rdirstaff = new RDirStaff()
						.setDomain(domain.getId())
						.setRegistry(c.getId())
						.setDocument(ci.getDocument())
						.setName(ci.getName());
				AON.insertRDirStaff(domain.getName(), domain.getId(), user.getLogin(), rdirstaff);
			}
		});
		
	}
}
