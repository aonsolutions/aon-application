package com.esferalia.aon.gwt.template.server.imports;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;

import com.esferalia.aon.gwt.template.server.projectCommercial.BankBic11;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.RegistryImportClass;
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
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
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


public class RegistryImport extends Import {

	RegistryImportClass reg;
	Integer indexTitle;
	
	public static RegistryImport getInstance() {
		return new RegistryImport();
	}

	public RegistryImport() {

	}

	public LinkedList<RegistryImportClass> importation(Domain domain, String login, byte[] data){
		try {
			return importation(domain, login, rowIterator(data));
		} catch (OfficeXmlFileException e){
			return importationX(domain, login, data);
		} 
	}

	public LinkedList<RegistryImportClass> importationX(Domain domain, String login, byte[] data){
		return importation(domain, login, rowIteratorX(data));
	}

	public LinkedList<RegistryImportClass> importation(Domain domain, String login,Iterator<Row> rowIterator){
		LinkedList<String> titleList = new LinkedList<>();
		LinkedList<RegistryImportClass> list = new LinkedList<>();
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
			if(obj == null || (titleList.isEmpty() && !AonArrayUtils.constainsIgnoreCase(IConstants.REGISTRY_TITLES, obj.toString().trim()))) {
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
				|| IConstants.DIRECCION2.equalsIgnoreCase(title)) {
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
				|| IConstants.CODIGO_POSTAL2.equalsIgnoreCase(title)) {
			String zip = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				zip = Integer.toString(Utils.parseDouble(zip).intValue());
			}
			reg.getRegistry().getMainAddress().setZip(zip.length() < 5 ? "0" + zip : zip);
			return;
		}

		if(IConstants.POBLACION2.equalsIgnoreCase(title) || IConstants.POBLACION.equalsIgnoreCase(title)
				|| IConstants.CIUDAD.equalsIgnoreCase(title)) {
			reg.getRegistry().getMainAddress().setCity(o.toString());
			return ;
		}
		if(IConstants.PROVINCIA.equalsIgnoreCase(title)) {
			Provinces pr = Provinces.getProvince(o.toString());
			if(pr == null && reg.getRegistry().getMainAddress().getZip() != null
					&& reg.getRegistry().getMainAddress().getZip().length() > 2) {
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
			reg.setIban(o.toString().replace(" ", "").replace(".", ""));
			return;
		}
		
		if(IConstants.BBAN.equalsIgnoreCase(title) || IConstants.CCC.equalsIgnoreCase(title)) {
			reg.setCcc(o.toString().replace(" ", "").replace(".", ""));
			return;
		}
		
		if(IConstants.BIC.equalsIgnoreCase(title) || IConstants.BIC_SWIFT.equalsIgnoreCase(title)) {
			reg.setBic(o.toString().trim());
			return;
		}

		if(IConstants.PAIS2.equalsIgnoreCase(title) || IConstants.PAIS.equalsIgnoreCase(title)) {
			reg.getRegistry().setNationality(Country.safeValueOf(o.toString()));
			return;
		}
		
		if(IConstants.MAIL.equalsIgnoreCase(title) || IConstants.EMAIL.equalsIgnoreCase(title)
				|| IConstants.CORREO_ELECTRONICO.equalsIgnoreCase(title) || IConstants.CORREO_ELECTRONICO2.equalsIgnoreCase(title)) {
			String[] mails = o.toString().split(",");
			for(int i = 0; i < mails.length; i++) {
				if(!AonStringUtils.isBlank(mails[i])) {
					RegistryMedia rm = new RegistryMedia()
							.setMedia(MediaType.EMAIL)
							.setDomain(domain.getId())
							.setValue(mails[i])
							.setAdministrative(true)
							.setComment("")
							.setCommercial(true)
							.setTechnical(true);
					reg.getRmediaList().add(rm);
				}
			}
			return;
		}
		if(IConstants.TELEFONO.equalsIgnoreCase(title) || IConstants.TELEFONO2.equalsIgnoreCase(title)
				|| IConstants.MOVIL.equalsIgnoreCase(title) || IConstants.MOVIL2.equalsIgnoreCase(title)) {
			String telephones = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				telephones = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			String[] phones = telephones.split(",");
			for(int i = 0; i < phones.length; i++) {
				if(!AonStringUtils.isBlank(phones[i])) {
					String p = phones[i].trim().replace("-", "");
					RegistryMedia rm =  new RegistryMedia()
						.setMedia(p.charAt(0) == '6' || p.charAt(0) == '7' ? MediaType.CELLULAR : MediaType.FIXED_PHONE)
						.setDomain(domain.getId())
						.setValue(p)
						.setAdministrative(true)
						.setComment("")
						.setCommercial(true)
						.setTechnical(true);
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
		return insertRegistry(domain, user, index, r);
	}
	
	public static Error insertRegistry(Domain domain, User user, Integer index, RegistryImportClass r) {
		Error error = new Error().setError(true);
	
		try {
			if(r.getRegistry() != null && AonStringUtils.isBlank(r.getRegistry().getDocument()) && AonStringUtils.isBlank(r.getRegistry().getName())) {
				throw new Exception("El Documento y la Razón Social no pueden estar vacíos.");
			}
			
			LinkedList<Registry> regList = AON.getRegistryStream(domain.getName(), domain.getId(), user.getLogin(), f ->
				f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(r.getRegistry().getDocument()))).collect(Collectors.toCollection(LinkedList::new));
			Registry reg = new Registry();
			if(regList.stream().filter(f -> f.getDomain().getId().equals(domain.getId())).count() > 0) {
				reg = regList.stream().filter(f -> f.getDomain().getId().equals(domain.getId())).findFirst().get();
			} else if(domain.getParentId() != null
					&& regList.stream().filter(f -> f.getDomain().getId().equals(domain.getParentId())).count() > 0) {
				reg = regList.stream().filter(f -> f.getDomain().getId().equals(domain.getParentId())).findFirst().get();
			} else if(regList.stream().filter(f -> f.getDomain().getId().equals(0)).count() > 0) {
				reg = regList.stream().filter(f -> f.getDomain().getId().equals(0)).findFirst().get();
			} else {
				DocumentType d = getDocumentType(r.getRegistry().getDocument());
				r.getRegistry()
					.setDomain(domain)
					.setDocumentType(getDocumentType(r.getRegistry().getDocument()))
					.setLegalPerson(DocumentType.CIF.equals(d));
				reg = AON.save(domain.getName(), domain.getId(), user.getLogin(), r.getRegistry());
				r.getRegistry().getMainAddress()
					.setType((byte) 0)
					.setDomain(domain.getId())
					.setRegistry(reg.getId());
				AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), r.getRegistry().getMainAddress());
			}
			Integer registryId = reg.getId();
			for(RegistryMedia rm : r.getRmediaList()) {
				rm.setRegistry(reg.getId());
				RegistryMedia rm2 = AON.getRMedia(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getRegistryProperty().eq(registryId))
					.and(f.getMediaProperty().eq(rm.getMedia().value()))
					.and(f.getValueProperty().eq(rm.getValue())));
				
				if(rm2 == null || rm2.getId() == null) {
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}
			}
			
			RegistryBank rbank = new RegistryBank();
			if(!AonStringUtils.isBlank(r.getIban()) || !AonStringUtils.isBlank(r.getCcc())) {
				BankAccount ba = !AonStringUtils.isBlank(r.getIban())
						? new BankAccount(r.getIban())
						: new BankAccount(r.getCcc(), true);
				
				if(!ba.isValidIban() && !ba.isValidBban()) {
					error.setTextWarning("Línea " + r.getLine() + ": El IBAN o CCC introducido no es correcto. se ha omitido");
				} else {
					rbank = AON.getRBank(domain.getName(), domain.getId(), user.getLogin(), f -> 
						f.getDomainProperty().eq(domain.getId())
						.and(f.getRegistryProperty().eq(registryId))
						.and(f.getBankAccountProperty().eq(ba.getIban())));
					if(rbank == null || rbank.getId() == null) {
						BankBic11 bb = BankBic11.getBankBic11(ba.getBankCode());
						if(bb == null) {
							bb = new BankBic11(ba.getBankCode(), r.getBic(), "");
						}
						String alias = bb.getDescription().length() > 24
								? bb.getDescription().substring(0, 24)
								: bb.getDescription();
						rbank = new RegistryBank()
							.setDomain(domain.getId())
							.setRegistry(reg.getId())
							.setActive(true)
							.setBankAccount(ba)
							.setBic(bb.getBic())
							.setAlias(alias);
						
						rbank = AON.insertRBank(domain.getName(), domain.getId(), user.getLogin(), rbank);
					}
				}
			}
			
			if(!AonStringUtils.isEmpty(r.getPaymethod().getName()) || r.getPaymethod().getType() != null) {
				PayMethod p = new PayMethod();
				if(!AonStringUtils.isEmpty(r.getPaymethod().getName())) {
					p = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod().getName());
					
					if(domain.isEnableHeredity() && (p == null || p.getId() == null)) {
						p = AON.getPayMethod(domain.getName(), domain.getParentId(), user.getLogin(), r.getPaymethod().getName());
					}
					
					if(p == null || p.getId() == null) {
						r.getPaymethod().setDomain(domain.getId());
						if(r.getPaymethod().getType() == null) r.getPaymethod().setType(PayMethodType.OTHER);
						p = AON.savePayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod());
					}
				} else if(r.getPaymethod().getType() != null) {
					p = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod().getType().getDescription().toUpperCase());
					if(p == null || p.getId() == null) {
						r.getPaymethod().setDomain(domain.getId());
						r.getPaymethod().setName(r.getPaymethod().getType().getDescription().toUpperCase());
						p = AON.savePayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getPaymethod());
					}
				}	
				Integer pId = p.getId();
				RegistryPayMethod rpaymethod = AON.getRegistryPayMethod(domain, user, f -> 
						f.getDomainProperty().eq(domain.getId())
						.and(f.getRegistryProperty().eq(registryId))
						.and(f.getPayMethodProperty().eq(pId)));
				if(rpaymethod == null || rpaymethod.getId() == null) {
					rpaymethod = new RegistryPayMethod()
						.setDomain(domain.getId())
						.setRegistry(reg.getId())
						.setPayMethod(p)
						.setRbank(rbank);
					AON.saveRegistryPayMethod(domain, user, rpaymethod);
				}
			}
			
			if(r.getAccountPrefix() != null && (r.getAccount().getCode() == null || r.getAccount().getCode().isBlank())) {
				String code = ACCOUNTING.getAccountNextCode(domain.getName(), domain.getId(), user.getLogin(), r.getAccountPrefix());
				r.getAccount().setCode(code);
			}

			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), r.getAccount().getCode());
			if(acc == null) {
				String rdocument = reg.getDocument();
				acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId()).and(f.getAliasProperty().eq(rdocument)))
					.findFirst().orElse(null);
			}
			
			if(acc == null) {
				acc = r.getAccount()
					.setDomain(domain.getId())
					.setActive(true);
			}
			
			Scope s = AON.getUserScopeStream(domain.getName(), domain.getId(), user.getLogin(), user.getId(), f -> 
					f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Scope());
			if(s.getId() == null && domain.isEnableHeredity() && domain.getParentId() != null) {
				s = AON.getUserScopeStream(domain.getName(), domain.getId(), user.getLogin(), user.getId(), f -> 
					f.getDomainProperty().eq(domain.getParentId())).findFirst().orElse(new Scope());
			}
			
			if(s.getId() == null && !domain.isEnableHeredity() && domain.getId() != null) {
				s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Scope());
			}

			if(r.isCustomer()) {
				Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getRegistryProperty().eq( registryId )));
				if(customer == null || customer.getId() == null) {
					acc = getAccount(domain, user, acc);
					Customer c = new Customer()
							.setAccount(acc.getId())
							.copy(reg)
							.setScope(new Scope().setId(domain.getScope() != null ? domain.getScope() : s.getId()))
							.setStatus(RegistryStatus.ACTIVE);
					c.setDomain(domain);
					AON.saveCustomer(domain.getName(), domain.getId(), user.getLogin(), c);
				}
				Optional<Target> target = AON.getTarget(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(registryId));
				if(target.isEmpty()) {
					Target t = new Target()
						.copy(reg)
						.setScope(s);
					AON.insertTarget(domain.getName(), domain.getId(), user.getLogin(), t);
				}
			}

			if(!Utils.isAyudaT(domain) && r.isSupplier()) {
				Optional<Supplier> supplier = AON.getSupplier(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq( registryId )));
				if(!supplier.isPresent()) {
					acc = getAccount(domain, user, acc);
					Supplier sup = new Supplier()
							.copy(reg)
							.setAccount(acc.getId())
							.setScope(new Scope().setId(domain.getScope() != null ? domain.getScope() : s.getId()))
							.setStatus(RegistryStatus.ACTIVE);
					sup.setId(registryId);
					sup.setDomain(domain);
					AON.saveSupplier(domain.getName(), domain.getId(), user.getLogin(), sup);
				}
			}

			if(!Utils.isAyudaT(domain) && r.isCreditor()) {
				Optional<Creditor> creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f->
					f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq( registryId )));
				if(!creditor.isPresent()) {
					acc = getAccount(domain, user, acc);
					Creditor cre = new Creditor()
							.copy(reg)
							.setAccount(acc==null?null:acc.getId())
							.setScope( new Scope().setId(domain.getScope() != null ? domain.getScope() : s.getId()))
							.setStatus(RegistryStatus.ACTIVE);
					cre.setId(registryId);
					cre.copy(reg);
					cre.setDomain(domain);
					AON.saveCreditor(domain.getName(), domain.getId(), user.getLogin(), cre);
				}
			}
		} catch (Exception e) {
			error.setError(false);
			error.setTextError("Línea " + r.getLine() + ": " + e.getMessage());
		}
	
		error.setLine(index);
		return error;
	}
	
	public static Account getAccount(Domain domain , User user, Account acc) {
		if(acc.getId() == null) {
			acc = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), acc);
		}
		return acc;
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
	

}
