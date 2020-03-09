package com.esferalia.aon.gwt.template.server.invoice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;


public class RegistryImport {
	
	public class RegistryImportClass {
		private Registry registry;
		private Account account;
		private String iban;
		
		public RegistryImportClass() {
			this.registry = new Registry()
				.setAddress(new RAddress());
			this.account = new Account();
		}

		public Registry getRegistry() {
			return registry;
		}

		public void setRegistry(Registry registry) {
			this.registry = registry;
		}

		public Account getAccount() {
			return account;
		}

		public void setAccount(Account account) {
			this.account = account;
		}

		public String getIban() {
			return iban;
		}

		public void setIban(String iban) {
			this.iban = iban;
		}
		
	}
	
	public static RegistryImport getInstance() {
		return new RegistryImport();
	}

	public RegistryImport() {

	}
	
	RegistryImportClass reg; 
	
	public LinkedList<RegistryImportClass> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<RegistryImportClass> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				reg = new RegistryImportClass();
				cellStream.forEach(cell -> {
					if(row.getRowNum() < 3) {
						
					} else if(row.getRowNum() == 3) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() > 4) {
					
					list.add(reg);
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

	public LinkedList<RegistryImportClass> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);

			XSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<RegistryImportClass> list = new LinkedList<>();
			
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				reg = new RegistryImportClass();
				
				cellStream.forEach(cell -> {
					if(row.getRowNum()< 3) {
						
					} else if(row.getRowNum() == 3) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() > 4) {
					list.add(reg);
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
		if(CellType.STRING == cell.getCellTypeEnum()) {
			return cell.getStringCellValue();
		}
		if(CellType.NUMERIC == cell.getCellTypeEnum()) {
			return cell.getNumericCellValue();
		}
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
	
		if("CUENTA".equalsIgnoreCase(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			reg.getAccount().setCode(acc.substring(0,4) + acc.substring(7));
			return;
		}

		if("CIF".equalsIgnoreCase(title)) {
			reg.getRegistry().setDocument(o.toString());
			reg.getAccount().setAlias(o.toString());
			return;
		}
		
		if("NOMBRE".equalsIgnoreCase(title)) {
			if(o.toString().length() > 63) {
				reg.getRegistry().setName(o.toString().substring(0,63));
				reg.getAccount().setDescription(o.toString().substring(0, 63));
			} else {
				reg.getRegistry().setName(o.toString());
				reg.getAccount().setDescription(o.toString());
			}
			return ;
		}
		if("DOMICILIO".equalsIgnoreCase(title)) {
			reg.getRegistry().getAddress().setAddress(o.toString());
			return;
		}
		
		if("C.P.".equalsIgnoreCase(title)) {
			reg.getRegistry().getAddress().setZip(o.toString());
			return;
		}
		
		if("Población".equalsIgnoreCase(title) || "POBLACION".equalsIgnoreCase(title)) {	
			reg.getRegistry().getAddress().setCity(o.toString());
			return ;
		}
		if("PROVINCIA".equalsIgnoreCase(title)) {
			Provinces pr = Provinces.getProvince(o.toString());
			reg.getRegistry().getAddress().setGeozoneCode(pr.getId());
			reg.getRegistry().getAddress().setGeozoneName(pr.getName());
			return;
		}
		
		if("IBAN".equalsIgnoreCase(title)) {
			reg.setIban(o.toString());
			return;
		}
		
		if("País".equalsIgnoreCase(title) || "PAIS".equalsIgnoreCase(title)) {
			reg.getRegistry().setNationality(Country.safeValueOf(o.toString()));
			return;
		}
	}

	public static void insertRegistries(Domain domain, User user,LinkedList<RegistryImportClass> rvs) {
		for (RegistryImportClass r : rvs) {
			LinkedList<Registry> regList = AON.getRegistryStream(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(r.getRegistry().getDocument()))).collect(Collectors.toCollection(LinkedList::new));
			Registry reg = new Registry();
			if(regList.stream().filter(f -> f.getDomain().equals(domain.getId())).count() > 0) {
				reg = regList.stream().filter(f -> f.getDomain().equals(domain.getId())).findFirst().get();
			} else if(domain.getParentId() != null 
				&& regList.stream().filter(f -> f.getDomain().equals(domain.getParentId())).count() > 0) {
				reg = regList.stream().filter(f -> f.getDomain().equals(domain.getParentId())).findFirst().get();
			} else if(regList.stream().filter(f -> f.getDomain().equals(0)).count() > 0) {
				reg = regList.stream().filter(f -> f.getDomain().equals(0)).findFirst().get();
			} else {
				r.getRegistry()
					.setDomain(domain.getId())
					.setDocumentType(getDocumentType(r.getRegistry().getDocument()));
				reg = AON.insertRegistry(domain.getName(), domain.getId(), user.getLogin(), r.getRegistry());
			}
			
			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), r.getAccount().getCode());
			if(acc == null) {
				Account account = r.getAccount()
						.setDomain(domain.getId())
						.setActive(true);
				acc = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), account);
			}
			
			if("430".equals(r.getAccount().getCode().substring(0, 3))) {
				Integer registryId = reg.getId();
				Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getRegistryProperty().eq( registryId )));
				if(customer == null || customer.getId() == null) {
					Scope s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(), f ->
					f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
						.findFirst().orElse(new Scope());
					Customer c = new Customer()
						.setAccount(acc.getId())
						.setDomain(domain.getId())
						.setRegistry(reg)
						.setScope(domain.getScope() != null ? domain.getScope() : s.getId())
						.setStatus(CustomerStatus.ACTIVE);
					AON.insertCustomer(domain.getName(), domain.getId(), user.getLogin(), c);
				}
			}
		}
	}

	private static DocumentType getDocumentType(String document) {
		if(isValidCIF(document)) {
			return DocumentType.CIF;
		} else if(isValidNIE(document)) {
			return DocumentType.NIE;
		} else if(isValidNIF(document)) {
			return DocumentType.NIF;
		} else return DocumentType.OTHER;
	}
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
			'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	private static final char[] NIF_LETTERS = { 'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

	public static boolean isValidNIE(String document) {
		char[] doc = document.toCharArray();
		if (doc == null || doc.length != 9) {
			return false;
		}
		doc[0] = (doc[0] == 'X') ? '0' : doc[0];
		doc[0] = (doc[0] == 'Y') ? '1' : doc[0];
		doc[0] = (doc[0] == 'Z') ? '2' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!AonStringUtils.isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}

	public static boolean isValidNIF(String document) {
		char[] doc = document.toCharArray();
		if (doc == null || doc.length != 9) {
			return false;
		}
		doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!AonStringUtils.isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}

	public static boolean isValidCIF(String document) {
		char[] doc = document.toCharArray();
		if (doc == null || doc.length != 9) {
			return false;
		}
		int lInDC = 0;
		for (int i = 1; i < 8; ++i) {
			String strDigit = new String(doc, i, 1);
			if (!AonStringUtils.isNumeric(strDigit)) {
				return false;
			}
			int digit = Integer.parseInt(strDigit);
			if ((i % 2) != 0) {
				digit *= 2;
				if (digit >= 10) {
					digit -= 9;
				}
			}
			lInDC += digit;
		}
		// Buscamos el multiplo de diez mas cercano mayor al numero calculado.
		lInDC = (((lInDC / 10) + 1) * 10) - lInDC;
		if (lInDC == 10) {
			lInDC = 0;
		}
		String first = new String(doc,0,1);
		if (first.matches("[P|N|S|Q|R|W]")) {
			return (NIF_LETTERS[lInDC] == doc[8]);
		}
		String strDC = new String(doc, 8, 1);
		if (!AonStringUtils.isNumeric(strDC)) {
			return false;
		}
		return (Integer.parseInt(strDC) == lInDC);
	}
	
}
