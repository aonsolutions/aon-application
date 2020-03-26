package com.esferalia.aon.gwt.template.server.invoice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
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
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SupplierStatus;
import com.esferalia.aon.watson.util.AonDocumentUtil;


public class RegistryImport {
	
	public class RegistryImportClass {
		private Registry registry;
		private Account account;
		private String iban;
		private String type;
		
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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public Boolean isCustomer() {
			return (this.type != null && (this.type.equalsIgnoreCase("C") || this.type.equalsIgnoreCase("CUSTOMER")))
					|| (this.account != null && this.account.getCode() != null 
						&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("430"));
		}
		
		public Boolean isSupplier() {
			return (this.type != null && (this.type.equalsIgnoreCase("P") || this.type.equalsIgnoreCase("PROVEEDOR")))
					|| (this.account != null && this.account.getCode() != null 
						&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("400"));
		}
		
		public Boolean isCreditor() {
			return (this.type != null && (this.type.equalsIgnoreCase("A") || this.type.equalsIgnoreCase("ACREEDOR")))
					|| (this.account != null && this.account.getCode() != null 
						&& this.account.getCode().length() > 2 && this.account.getCode().substring(0, 3).equals("410"));
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
			AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), login);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				reg = new RegistryImportClass();
				Integer indexTitle = Utils.isAyudaT(domain.getName()) ? 3 : 0;
				cellStream.forEach(cell -> {
					if(row.getRowNum() == indexTitle) {
						titleList.add(cell.getStringCellValue());
					} else if(row.getRowNum() > indexTitle) {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell, aonCtx);			
					}
				});
				if(row.getRowNum() > indexTitle) {
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
			AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), login);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				reg = new RegistryImportClass();
				Integer indexTitle = Utils.isAyudaT(domain.getName()) ? 3 : 0;
				cellStream.forEach(cell -> {
					if(row.getRowNum() == indexTitle) {
						titleList.add(cell.getStringCellValue());
					} else if(row.getRowNum() > indexTitle){
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell, aonCtx);			
					}
				});
				if(row.getRowNum() > indexTitle) {
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

 	private void check(Domain domain , String login, String title, Cell cell, AonConfiguration aonCtx) {
		Object o = Utils.getObjectValue(cell);
		if(o == null) return;
	
		if("CUENTA".equalsIgnoreCase(title)
				|| "CUENTA CONTABLE".equalsIgnoreCase(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			reg.getAccount().setCode(Utils.calculateAccount(acc));
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
		if("DOMICILIO".equalsIgnoreCase(title)
				|| "DIRECCION".equalsIgnoreCase(title)
				|| "DIRECCIÓN".equalsIgnoreCase(title)) {
			reg.getRegistry().getAddress().setAddress(o.toString());
			return;
		}
		
		if("C.P.".equalsIgnoreCase(title)
				|| "CODIGO POSTAL".equalsIgnoreCase(title)
				|| "CÓDIGO POSTAL".equalsIgnoreCase(title)) {
			reg.getRegistry().getAddress().setZip(o.toString().length() < 5 ? "0" + o.toString() : o.toString());
			return;
		}
		
		if("Población".equalsIgnoreCase(title) || "POBLACION".equalsIgnoreCase(title)
				|| "CIUDAD".equalsIgnoreCase(title)) {	
			reg.getRegistry().getAddress().setCity(o.toString());
			return ;
		}
		if("PROVINCIA".equalsIgnoreCase(title)) {
			Provinces pr = Provinces.getProvince(o.toString());
			if(pr == null && reg.getRegistry().getAddress().getZip() != null ) {
				pr = Provinces.getProvinceById(reg.getRegistry().getAddress().getZip().substring(0,2));
 			}
			for(GeoZone gz : aonCtx.getGeozones()) {
				if(gz.getCode().equals(pr.getId())) {
					reg.getRegistry().getAddress().setGeozone(gz.getId());
					reg.getRegistry().getAddress().setGeozoneCode(gz.getCode());
					reg.getRegistry().getAddress().setGeozoneName(gz.getName());
				}
			}
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
				r.getRegistry().getAddress()
					.setType((byte) 0)
					.setDomain(domain.getId())
					.setRegistry(reg.getId());
				AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), r.getRegistry().getAddress());
			}
			
			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), r.getAccount().getCode());
			if(acc == null) {
				Account account = r.getAccount()
						.setDomain(domain.getId())
						.setActive(true);
				acc = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), account);
			}
			
			Integer registryId = reg.getId();
			Scope s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(), f ->
			f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.findFirst().orElse(new Scope());
			
			if(r.isCustomer()) {
				Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getRegistryProperty().eq( registryId )));
				if(customer == null || customer.getId() == null) {
					Customer c = new Customer()
						.setAccount(acc.getId())
						.setDomain(domain.getId())
						.setRegistry(reg)
						.setScope(domain.getScope() != null ? domain.getScope() : s.getId())
						.setStatus(CustomerStatus.ACTIVE);
					AON.insertCustomer(domain.getName(), domain.getId(), user.getLogin(), c);
				}
			} 
			
			if(!Utils.isAyudaT(domain.getName()) && r.isSupplier()) {
				Optional<Supplier> supplier = AON.getSupplier(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq( registryId )));
				if(!supplier.isPresent()) {
					Supplier sup = new Supplier()
						.setAccount(acc.getId())
						.setScope(domain.getScope() != null ? domain.getScope() : s.getId())
						.setStatus(SupplierStatus.ACTIVE);
					sup.setId(registryId);
					sup.setDomain(domain.getId());
					AON.insertSupplier(domain.getName(), domain.getId(), user.getLogin(), sup);

				}
			}
			
			if(!Utils.isAyudaT(domain.getName()) && r.isCreditor()) {
				
				Optional<Creditor> creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq( registryId )));
				if(!creditor.isPresent()) {
					Creditor cre = new Creditor()
						.setAccount(acc)
						.setScope(domain.getScope() != null ? domain.getScope() : s.getId())
						.setStatus(CreditorStatus.ACTIVE);
					cre.setId(registryId);
					cre.setRegistry(reg);
					cre.setDomain(domain.getId());
					AON.insertCreditor(domain.getName(), domain.getId(), user.getLogin(), cre);
				}
			}
		}
	}

	private static DocumentType getDocumentType(String document) {
		if(document == null) {
			return DocumentType.CIF;
		} else if(AonDocumentUtil.isValidCIF(document.toCharArray())){
			return DocumentType.CIF;
		} else if(AonDocumentUtil.isValidNIE(document.toCharArray())) {
			return DocumentType.NIE;
		} else if(AonDocumentUtil.isValidNIF(document.toCharArray())) {
			return DocumentType.NIF;
		} else return DocumentType.OTHER;
	}

}
