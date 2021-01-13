package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;


public class RegistryImport {

	public class RegistryImportClass {
		private Registry registry;
		private Account account;
		private String iban;
		private String bic;
		private String type;
		private Integer line;
		private LinkedList<RegistryMedia> rmediaList;
		private PayMethod paymethod;

		
		public RegistryImportClass() {
			this.registry = new Registry()
				.setMainAddress(new RAddress());
			this.account = new Account();
			this.paymethod = new PayMethod();
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

		public String getBic() {
			return bic;
		}

		public void setBic(String bic) {
			this.bic = bic;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Integer getLine() {
			return line;
		}

		public void setLine(Integer line) {
			this.line = line;
		}

		public LinkedList<RegistryMedia> getRmediaList() {
			if(rmediaList == null) {
				this.rmediaList = new LinkedList<RegistryMedia>();
			}
			return rmediaList;
		}

		public void setRmediaList(LinkedList<RegistryMedia> rmediaList) {
			this.rmediaList = rmediaList;
		}

		public PayMethod getPaymethod() {
			return paymethod;
		}

		public void setPaymethod(PayMethod paymethod) {
			this.paymethod = paymethod;
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

		public String getAccountPrefix() {
			if(isCustomer()) return "430";
			if(isSupplier()) return "400";
			if(isCreditor()) return "410";
			return null;
		}
	}

	public static RegistryImport getInstance() {
		return new RegistryImport();
	}

	public RegistryImport() {

	}

	RegistryImportClass reg;
	Integer indexTitle;
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
			indexTitle = 0;
			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				reg = new RegistryImportClass();
				reg.setLine(row.getRowNum() + 1);

				Object obj = Utils.getObjectValue(row.getCell(0));
				if(obj == null || (titleList.isEmpty() && !AonArrayUtils.constainsIgnoreCase(IConstants.REGISTRY_TITLES, obj.toString()))) {
					indexTitle = indexTitle + 1;
				} else if(titleList.isEmpty()) {
					indexTitle = row.getRowNum();
				}
				cellStream.forEach(cell -> {
					if(row.getRowNum() == indexTitle) {
						Object title = Utils.getObjectValue(cell);
						titleList.add(title != null ? title.toString().trim() :  "");
					} else if(row.getRowNum() > indexTitle && cell.getColumnIndex() < titleList.size()) {
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
			indexTitle = 0;
			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				reg = new RegistryImportClass();
				reg.setLine(row.getRowNum() + 1);
				
				Object obj = Utils.getObjectValue(row.getCell(0));
				if(obj == null || (titleList.isEmpty() && !AonArrayUtils.constainsIgnoreCase(IConstants.REGISTRY_TITLES, obj.toString()))) {
					indexTitle = indexTitle + 1;
				}
				cellStream.forEach(cell -> {
					if(row.getRowNum() == indexTitle) {
						String title = Utils.getObjectValue(cell).toString().trim();
						titleList.add(title);
					} else if(row.getRowNum() > indexTitle && cell.getColumnIndex() < titleList.size()){
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

		if(IConstants.TIPO.equalsIgnoreCase(title)) {
			reg.setType(o.toString());
			return;
		}

		if(IConstants.CUENTA.equalsIgnoreCase(title) || IConstants.CUENTA_CONTABLE.equalsIgnoreCase(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			reg.getAccount().setCode(Utils.calculateAccount(acc));
			return;
		}

		if(IConstants.CIF.equalsIgnoreCase(title)) {
			reg.getRegistry().setDocument(o.toString());
			reg.getAccount().setAlias(o.toString());
			return;
		}

		if(IConstants.NOMBRE.equalsIgnoreCase(title)) {
			if(o.toString().length() > 63) {
				reg.getRegistry().setName(o.toString().substring(0,63));
				reg.getAccount().setDescription(o.toString().substring(0, 63));
			} else {
				reg.getRegistry().setName(o.toString());
				reg.getAccount().setDescription(o.toString());
			}
			return ;
		}
		if(IConstants.DOMICILIO.equalsIgnoreCase(title)
				|| IConstants.DIRECCION.equalsIgnoreCase(title)
				|| IConstants.DIRECCIÓN.equalsIgnoreCase(title)) {
			reg.getRegistry().getMainAddress().setAddress(o.toString());
			
			if(AonStringUtils.isBlank(reg.getRegistry().getMainAddress().getZip())) {
				reg.getRegistry().getMainAddress().setZip(getZip(o.toString()));
			}
			
			if(reg.getRegistry().getMainAddress().getGeozone() == null) {
				GeoZone gz = getGeoZone(aonCtx, o.toString());
				reg.getRegistry().getMainAddress().setGeozone(gz.getId());
				reg.getRegistry().getMainAddress().setGeozoneCode(gz.getCode());
				reg.getRegistry().getMainAddress().setGeozoneName(gz.getName());
			}
			
			return;
		}

		if(IConstants.CP.equalsIgnoreCase(title)
				|| IConstants.CODIGO_POSTAL.equalsIgnoreCase(title)
				|| IConstants.CÓDIGO_POSTAL.equalsIgnoreCase(title)) {
			String zip = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				zip = Integer.toString(Utils.parseDouble(zip).intValue());
			}
			reg.getRegistry().getMainAddress().setZip(zip.length() < 5 ? "0" + zip : zip);
			return;
		}

		if(IConstants.POBLACIÓN.equalsIgnoreCase(title) || IConstants.POBLACION.equalsIgnoreCase(title)
				|| IConstants.CIUDAD.equalsIgnoreCase(title)) {
			reg.getRegistry().getMainAddress().setCity(o.toString());
			return ;
		}
		if(IConstants.PROVINCIA.equalsIgnoreCase(title)) {
			Provinces pr = Provinces.getProvince(o.toString());
			if(pr == null && reg.getRegistry().getMainAddress().getZip() != null ) {
				pr = Provinces.getProvinceById(reg.getRegistry().getMainAddress().getZip().substring(0,2));
 			}
			for(GeoZone gz : aonCtx.getGeozones()) {
				if(pr != null && pr.getId() != null && gz.getCode().equals(pr.getId())) {
					reg.getRegistry().getMainAddress().setGeozone(gz.getId());
					reg.getRegistry().getMainAddress().setGeozoneCode(gz.getCode());
					reg.getRegistry().getMainAddress().setGeozoneName(gz.getName());
				}
			}
			return;
		}

		if(IConstants.IBAN.equalsIgnoreCase(title)) {
			reg.setIban(o.toString().trim().replace(".", ""));
			return;
		}
		
		if(IConstants.BIC.equalsIgnoreCase(title) || IConstants.BIC_SWIFT.equalsIgnoreCase(title)) {
			reg.setBic(o.toString().trim());
			return;
		}

		if(IConstants.PAÍS.equalsIgnoreCase(title) || IConstants.PAIS.equalsIgnoreCase(title)) {
			reg.getRegistry().setNationality(Country.safeValueOf(o.toString()));
			return;
		}
		
		if(IConstants.MAIL.equalsIgnoreCase(title) || IConstants.EMAIL.equalsIgnoreCase(title)
				|| IConstants.CORREO_ELECTRONICO.equalsIgnoreCase(title) || IConstants.CORREO_ELECTRÓNICO.equalsIgnoreCase(title)) {
			String[] mails = o.toString().split(",");
			for(int i = 0; i < mails.length; i++) {
				if(!AonStringUtils.isBlank(mails[i])) {
					RegistryMedia rm = new RegistryMedia()
							.setMedia(MediaType.EMAIL.value())
							.setDomain(domain.getId())
							.setValue(mails[i])
							.setAdministrative((byte) 0)
							.setComment("")
							.setCommercial((byte) 0)
							.setTechnical((byte) 0);
					reg.getRmediaList().add(rm);
				}
			}
			return;
		}
		if(IConstants.TELEFONO.equalsIgnoreCase(title) || IConstants.TELÉFONO.equalsIgnoreCase(title)
				|| IConstants.MOVIL.equalsIgnoreCase(title) || IConstants.MÓVIL.equalsIgnoreCase(title)) {
			String[] phones = o.toString().split(",");
			for(int i = 0; i < phones.length; i++) {
				if(!AonStringUtils.isBlank(phones[i])) {
					String p = phones[i].trim().replace("-", "");
					RegistryMedia rm =  new RegistryMedia()
						.setMedia(p.charAt(0) == '6' || p.charAt(0) == '7' ? MediaType.CELLULAR.value() : MediaType.FIXED_PHONE.value())
						.setDomain(domain.getId())
						.setValue(p)
						.setAdministrative((byte) 0)
						.setComment("")
						.setCommercial((byte) 0)
						.setTechnical((byte) 0);
					reg.getRmediaList().add(rm);
				}
			}
			return;
		}
		
		if(IConstants.FORMA_DE_PAGO.equalsIgnoreCase(title) || IConstants.FORMA_PAGO.equalsIgnoreCase(title)) {
			reg.getPaymethod().setName(o.toString());
			return;
		}
		
		if(IConstants.TIPO_DE_PAGO.equalsIgnoreCase(title) || IConstants.TIPO_PAGO.equalsIgnoreCase(title)) {
			reg.getPaymethod().setType(PayMethodType.safeValueOf(o.toString()));
			return;
		}
		
		
	}

	public static Error insertRegistries(Domain domain, User user, Integer index, LinkedList<RegistryImportClass> rvs) {
		Error error = new Error().setError(true);
		
		if(index >= rvs.size()) {
			error.setLine(index);
			return error;
		}
		RegistryImportClass r = rvs.get(index);
		try {
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
					.setDomain(domain)
					.setDocumentType(getDocumentType(r.getRegistry().getDocument()));
				reg = AON.insertRegistry(domain.getName(), domain.getId(), user.getLogin(), r.getRegistry());
				r.getRegistry().getMainAddress()
					.setType((byte) 0)
					.setDomain(domain.getId())
					.setRegistry(reg.getId());
				AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), r.getRegistry().getMainAddress());
				
				for(RegistryMedia rm : r.getRmediaList()) {
					rm.setRegistry(reg);
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}
				
				RegistryBank rbank = new RegistryBank();
				if(!AonStringUtils.isBlank(r.getIban())) {
					rbank = new RegistryBank()
							.setDomain(domain.getId())
							.setRegistry(reg.getId())
							.setActive(true)
							.setBankAccount(new BankAccount(r.getIban()))
							.setBic(r.getBic());
					rbank = AON.insertRBank(domain.getName(), domain.getId(), user.getLogin(), rbank);
				}
				
				if(r.getPaymethod().getName() != null || r.getPaymethod().getType() != null) {
					PayMethod p = new PayMethod();
					if(!AonStringUtils.isEmpty(r.getPaymethod().getName())) {
						p = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod().getName());
						if(p == null || p.getId() == null) {
							r.getPaymethod().setDomain(domain.getId());
							if(r.getPaymethod().getType() == null) r.getPaymethod().setType(PayMethodType.OTHER);
							p =AON.insertPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod());
						}
					} else if(r.getPaymethod().getType() != null) {
						p = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod().getType().getDescription().toUpperCase());
						if(p == null || p.getId() == null) {
							r.getPaymethod().setDomain(domain.getId());
							r.getPaymethod().setName(r.getPaymethod().getType().getDescription().toUpperCase());
							p = AON.insertPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod());
						}
					}	
					RegistryPayMethod rpaymethod = new RegistryPayMethod()
							.setDomain(domain.getId())
							.setRegistry(reg.getId())
							.setPayMethod(p.getId())
							.setRbank(rbank.getId());
					AON.insertRPayMethod(domain.getName(), domain.getId(), user.getLogin(), rpaymethod);
				}
			}
			if(r.getAccountPrefix() != null && (r.getAccount().getCode() == null || r.getAccount().getCode().isBlank())) {
				String code = ACCOUNTING.getAccountNextCode(domain.getName(), domain.getId(), user.getLogin(), r.getAccountPrefix());
				r.getAccount().setCode(code);
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
							.setRegistryData(reg)
							.setScope(domain.getScope() != null ? domain.getScope() : s.getId())
							.setStatus(RegistryStatus.ACTIVE);
					c.setDomain(domain);
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
							.setStatus(RegistryStatus.ACTIVE);
					sup.setId(registryId);
					sup.setDomain(domain);
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
							.setStatus(RegistryStatus.ACTIVE);
					cre.setId(registryId);
					cre.setRegistry(reg);
					cre.setDomain(domain.getId());
					AON.insertCreditor(domain.getName(), domain.getId(), user.getLogin(), cre);
				}
			}
		} catch (Exception e) {
			error.setError(false);
			error.setTextError("Línea " + r.getLine() + ": " + e.getMessage());
		}
	
		error.setLine(index);
		return error;
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
	
	private static String getZip(String address) {
		String cp = null;
		try {
			Pattern p = Pattern.compile("0[1-9][0-9]{3}|[1-4][0-9]{4}|5[0-2][0-9]{3}");
			Matcher zip = p.matcher(address);
			zip.find();
			cp = zip.group();
		} catch (Exception e) {}
		return cp;
	}
	
	private static GeoZone getGeoZone(AonConfiguration aonCtx, String address) {
		Provinces pr = null;
		String zip = getZip(address);
		if(zip != null) {
			pr = Provinces.getProvinceById(zip.substring(0,2));
		} else {
			for (Provinces p : Provinces.values()) {
				if(AonStringUtils.containsIgnoreCase(address, p.getName())) {
					pr = p;
				}
			}
		}
		GeoZone geoZone = new GeoZone();
		for(GeoZone gz : aonCtx.getGeozones()) {
			if(pr != null && pr.getId() != null && gz.getCode().equals(pr.getId())) {
				geoZone = gz;
			}
		}
		
		return geoZone;
	}
	
	public static void main(String[] args) {
		
	}
}
