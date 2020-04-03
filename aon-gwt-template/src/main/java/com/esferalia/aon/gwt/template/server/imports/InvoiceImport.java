package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
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

import com.esferalia.aon.gwt.template.server.imports.InvoiceImportClass.InvoiceClaveRetencion;
import com.esferalia.aon.gwt.template.server.imports.InvoiceImportClass.InvoiceOpType;
import com.esferalia.aon.gwt.template.server.imports.InvoiceImportClass.InvoiceSubClaveRetencion;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SupplierStatus;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class InvoiceImport {

	public static InvoiceImport getInstance() {
		return new InvoiceImport();
	}

	public InvoiceImport() {

	}
	
	InvoiceImportClass inv; 
	
	public LinkedList<InvoiceImportClass> importation(Domain domain, String login, byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<InvoiceImportClass> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				inv = new InvoiceImportClass();
				inv.setLine(row.getRowNum() + 1);	
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0) {
					
					list.add(inv);
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

	public LinkedList<InvoiceImportClass> importationX(Domain domain, String login, byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);

			XSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<InvoiceImportClass> list = new LinkedList<>();
			
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				inv = new InvoiceImportClass();
				inv.setLine(row.getRowNum() + 1);	
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());
					} else {
						String title = titleList.get(cell.getColumnIndex());
						check(domain, login, title, cell);			
					}
				});
				if(row.getRowNum() != 0 && (inv.getSerie()!= null
					|| inv.getNumber() != null || inv.getRef() != null)) {
					list.add(inv);
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

 	private void check(Domain domain , String login, String title, Cell cell) {
		Object o = Utils.getObjectValue(cell);
		if(o == null) return;
	
		if("TIPO OPERACIÓN".equalsIgnoreCase(title)
				|| "TIPO OPERACION".equalsIgnoreCase(title)) {
			inv.setType(InvoiceOpType.safeValueOf(o.toString()));
			return;
		}
		
		if("TIPO FACTURA".equalsIgnoreCase(title)) {
			inv.setInvoiceType(InvoiceType.safeValueOf(o.toString()));
			return;
		}

		if("FECHA".equalsIgnoreCase(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			inv.setDate(date);
			return;
		}
		
		if("SERIE".equalsIgnoreCase(title)) {
			String serie = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				serie = Integer.toString(AonNumberUtils.toDouble(o.toString()).intValue());
			} 
			inv.setSerie(serie);
			return;
		}
		
		if("NUMERO".equalsIgnoreCase(title)
				|| "NÚMERO".equalsIgnoreCase(title)) {
			inv.setNumber(AonNumberUtils.toDouble(o.toString()).intValue());
			return;
		}
		
		if("NUMERO DE FACTURA".equalsIgnoreCase(title)
				|| "NÚMERO DE FACTURA".equalsIgnoreCase(title)
				|| "REFERENCIA".equalsIgnoreCase(title)) {
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				inv.setRef(NumberToTextConverter.toText(cell.getNumericCellValue()));
			} else inv.setRef(o.toString());
			return ;
		}
		if("NIF".equalsIgnoreCase(title)) {
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				inv.setNif(NumberToTextConverter.toText(cell.getNumericCellValue()));
			} else inv.setNif(o.toString());
			return ;
		}
		
		if("NOMBRE".equalsIgnoreCase(title)) {
			inv.setName(o.toString());
			return ;
		}
		
		if("TERCERO".equalsIgnoreCase(title)) {
			inv.setThird(o.toString());
			return;
		}
		
		if("CONCEPTO".equalsIgnoreCase(title)
				|| "OBSERVACIONES".equalsIgnoreCase(title)) {
			inv.setConcept(o.toString());
			return;
		}
		
		if("DIRECCIÓN".equalsIgnoreCase(title)
				|| "DIRECCION".equalsIgnoreCase(title)) {
			inv.setAddress(o.toString());
			return;
		}
		
		if("CIUDAD".equalsIgnoreCase(title)) {
			inv.setCity(o.toString());
			return;
		}
		
		if("PROVINCIA".equalsIgnoreCase(title)) {
			inv.setProvince(o.toString());
			return;
		}
		
		if("CODIGO POSTAL".equalsIgnoreCase(title)
				|| "CÓDIGO POSTAL".equalsIgnoreCase(title)) {
			String zip = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				zip = Integer.toString(AonNumberUtils.toDouble(zip).intValue());
			}
			inv.setZip(zip.length() < 5 ? "0" + zip : zip);
			return;
		}
		
		if("PAIS".equalsIgnoreCase(title)
				|| "PAÍS".equalsIgnoreCase(title)) {
			inv.setCountry(Country.safeValueOf(o.toString()));
			return;
		}
		
		if("CUENTA BASE".equalsIgnoreCase(title)
				|| "CUENTA CONTABLE".equalsIgnoreCase(title)
				|| "CUENTA EXPLOTACIÓN".equalsIgnoreCase(title)
				|| "CUENTA EXPLOTACION".equalsIgnoreCase(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			inv.setAccount(Utils.calculateAccount(acc));
			return;
		}
		
		if("DESCRIPCIÓN CUENTA".equalsIgnoreCase(title)
				|| "DESCRIPCION CUENTA".equalsIgnoreCase(title)){
			inv.setAccountDescription(o.toString());
			return;
		}
		if("BASE".equalsIgnoreCase(title)
				|| "BASE IMPONIBLE".equalsIgnoreCase(title)) {
			inv.setBase(Double.parseDouble(o.toString()));
			return;
		}
		if("%Impuesto".equalsIgnoreCase(title)
				|| "%IVA".equalsIgnoreCase(title)) {
			inv.setPercentage(Double.parseDouble(o.toString()));
			return;
		}
		if("CUOTA Impuesto".equalsIgnoreCase(title)
				|| "CUOTA IVA".equalsIgnoreCase(title)) {
			inv.setQuota(Double.parseDouble(o.toString()));
			return;
		}
		if("%RE".equalsIgnoreCase(title)) {
			inv.setRePercentage(Double.parseDouble(o.toString()));
			return;
		}
		
		if("CUOTA RE".equalsIgnoreCase(title)) {
			inv.setReQuota(Double.parseDouble(o.toString()));
			return;
		}
		
		if("%RETENCIÓN".equalsIgnoreCase(title)) {
			inv.setRetentionPercentage(Double.parseDouble(o.toString()));
			return;
		}
		
		if("CUOTA RETENCIÓN".equalsIgnoreCase(title)) {
			inv.setRetentionQuota(Double.parseDouble(o.toString()));
			return;
		}
		
		if("TOTAL FACTURA".equalsIgnoreCase(title)
				|| "TOTAL".equalsIgnoreCase(title)) {
			inv.setTotal(Double.parseDouble(o.toString()));
			return;
		}
		
		if("CLAVE RETENCIÓN".equalsIgnoreCase(title)) {
			inv.setRetentionKey(InvoiceClaveRetencion.safeValueOf(o.toString()));
			return;
		}
		
		if("SUBCLAVE RETENCIÓN".equalsIgnoreCase(title)) {
			inv.setRetentionSubKey(InvoiceSubClaveRetencion.safeValueOf(o.toString()));
			return;
		}	
		
		if("VENCIMIENTO".equalsIgnoreCase(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			inv.setFinanceDate(date);
			return;
		}	
		
		if("CUENTA TESORERÍA".equalsIgnoreCase(title)
				|| "CUENTA TESORERIA".equalsIgnoreCase(title)
				|| "PAGO POR CAJA".equalsIgnoreCase(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			inv.setFinanceAccount(Utils.calculateAccount(acc));
			return;
		}
	}

	public static Error insertInvoices(Domain domain, User user,LinkedList<InvoiceImportClass> ivs) {
		Error error = new Error().setError(true);
		LinkedList<String> verror = new LinkedList<String>();
		
		AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), user.getLogin());
	
		PayMethod pm = aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty() ? aonCtx.getPayMethods().get(0) : new PayMethod(); 
		Account outputAccount = aonCtx.getDefaultChargedVatAccount();
		Account inputAccount = aonCtx.getDefaultPaidVatAccount();
		Account adjAccount = aonCtx.getVatNegativeAdjustAccount();
		
		for(Integer i = 0; i < ivs.size(); i++) {
			try {
				if(ivs.get(i).getAccount() == null || ivs.get(i).getAccount().isBlank()) {
					throw new Exception("La cuenta contable es un dato obligatorio.");
				}
				
				AccountingInvoice ai = new AccountingInvoice();
				ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());

				Invoice invoice = new Invoice();
				invoice.setScope(new Scope().setId(getScopeId(domain, user)));
				invoice.setService(InvoiceOpType.PIS.equals(ivs.get(i).getType())|| InvoiceOpType.AIS.equals(ivs.get(i).getType()));
				invoice.setTransaction(getTransaction(ivs.get(i)));
				invoice.setDomain(domain.getId());
				invoice.setIssueDate(ivs.get(i).getDate());
				invoice.setTaxDate(ivs.get(i).getDate());
				invoice.setType(ivs.get(i).getInvoiceType() != null
					? ivs.get(i).getInvoiceType()
					: getInvoiceType(ivs.get(i).getAccount()));
				if(ivs.get(i).getSerie() != null) {
					invoice.setSeries(ivs.get(i).getSerie());
				} 
				if(ivs.get(i).getNumber() != null) {
					invoice.setNumber(ivs.get(i).getNumber());	
				}
				invoice.setReferenceCode(ivs.get(i).getRef());
				invoice.setWithholding(ivs.get(i).getRetentionQuota() != null 
					&& ivs.get(i).getRetentionQuota() != 0);
				invoice.setRemarks(ivs.get(i).getConcept());
				invoice.setSurcharge(ivs.get(i).getRePercentage() != null && ivs.get(i).getPercentage() > 0);
				if(ivs.get(i).getTotal() < 0) {
					invoice.setRectificationType(RectificationType.NORMAL_RECTIFIER);
				}

			
				RAddress address = new RAddress();
				address.setDomain(domain.getId());
				address.setType((byte) 0);
				address.setAddress(ivs.get(i).getAddress());
				address.setCity(ivs.get(i).getCity());
				address.setZip(ivs.get(i).getZip() != null && ivs.get(i).getZip().length() < 5 
					? "0" + ivs.get(i).getZip() : ivs.get(i).getZip());
				if(ivs.get(i).getProvince() != null) {
					GeoZone prgz = null;
					GeoZone crgz = null;
					Provinces pr = Provinces.getProvince(ivs.get(i).getProvince());
					if(pr == null && address.getZip() != null ) {
						pr = Provinces.getProvinceById(address.getZip().substring(0,2));
 					}
					for(GeoZone gz : aonCtx.getGeozones()) {
						if(gz.getCode().equals(ivs.get(i).getCountry().getIso2())) {
							crgz = gz;
						}
						if(gz.getCode().equals(pr.getId())) {
							prgz = gz;
						}
					}
				
					if(prgz != null) {
						address.setGeozone(prgz.getId());
						address.setGeozoneCode(prgz.getCode());
						address.setGeozoneName(prgz.getName());
					} else if(crgz != null) {
						address.setGeozone(crgz.getId());
						address.setGeozoneCode(crgz.getCode());
						address.setGeozoneName(crgz.getName());
					}
				}

				String nif = ivs.get(i).getNif();
				String name = ivs.get(i).getName();
			
				AccountingRegistry ar = getRegistry(domain, user, invoice.getType(), nif, name, invoice.getTransaction(), address);			
				invoice.setRegistry(ar.getId());
				ai.setRegistry(ar);
				ai.setInvoice(invoice);

				String reference = ivs.get(i).getRef();
				String serie = ivs.get(i).getSerie();
				Integer number = ivs.get(i).getNumber();
				Double total = 0.0;
				Double retBase = 0.0;
				Double retQuota = 0.0;
				Double retPercentage = 0.0;
				Integer j = i;
				while(ivs.size() > j && isSameReference(reference, serie, number, ivs.get(j))) {
					if(ivs.get(i).getRetentionQuota() != null) {
						retBase = retBase + ivs.get(j).getBase();
						retPercentage = ivs.get(j).getRetentionPercentage();
						retQuota = retQuota + ivs.get(j).getRetentionQuota();
						ai.getInvoice().setWithholding(true);
					}
					
					Account expAccount = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), ivs.get(j).getAccount());
					if(expAccount == null) {
						expAccount = new Account()
							.setCode(ivs.get(j).getAccount())
							.setDescription(ivs.get(i).getAccountDescription() != null 
									? ivs.get(i).getAccountDescription()
									: "SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE FACTURAS)")
							.setAlias(ivs.get(i).getAccountDescription() != null 
									? ivs.get(i).getAccountDescription()
									:"SIN DESCRIPCIÓN")
							.setDomain(domain.getId())
							.setActive(true);
						expAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), expAccount);
					}
				
					InvoiceVAT vat = new InvoiceVAT()
						.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						.setBase(ivs.get(j).getBase() != null 
								? ivs.get(j).getBase() : 0.0)
						.setPercentage(ivs.get(j).getPercentage() != null
								? ivs.get(j).getPercentage() : 0.0)
						.setQuota(ivs.get(j).getQuota() != null
								? ivs.get(j).getQuota() : 0.0)
						.setSurcharge(ivs.get(j).getRePercentage() != null
								? ivs.get(j).getRePercentage() : 0.0)
						.setSurchargeQuota(ivs.get(j).getReQuota() != null
								? ivs.get(j).getReQuota() : 0.0)
						//.setInvestAsset(ivs.get(j).getInvestAsset())
						.setDeductiblePercent(100.0)
						.setDeductibleQuota(ivs.get(j).getQuota() != null
								? ivs.get(j).getQuota() : 0.0)
						.setWithholding(ivs.get(j).getRetentionQuota() != null
								&& ivs.get(j).getRetentionQuota() != 0)
						
						.setExpAccountId(expAccount.getId())
						.setExpAccountCode(expAccount.getCode())
						.setExpAccountDescription(expAccount.getDescription())
						
						.setOutputAccountCode(outputAccount.getCode())
						.setOutputAccountDescription(outputAccount.getDescription())
						.setOutputAccountId(outputAccount.getId())
						
						.setInputAccountCode(inputAccount.getCode())
						.setInputAccountDescription(inputAccount.getDescription())
						.setInputAccountId(inputAccount.getId())

						.setAdjAccountCode(adjAccount != null ? adjAccount.getCode(): null)
						.setAdjAccountDescription(adjAccount != null ? adjAccount.getDescription(): null)
						.setAdjAccountId(adjAccount != null ? adjAccount.getId() : null);
					ai.addVat(vat);
					total = total + (invoice.mustApplyISP() ? ivs.get(j).getBase() : ivs.get(j).getTotal());

					j++;
				}
				Integer cci = i;
				i = j-1;
				for(Integer k = cci; k < j; k++) {
					checkCuotas(domain, ivs.get(k));
				}

				ai.getInvoice().setTotal(total);
			
				if(ai.getInvoice().isWithholding()) {
					Account retentionAccount = (invoice.isSales() )
							?aonCtx.getDefaultPaidRetAccount()
							:aonCtx.getDefaultChargedRetAccount();
			
					InvoiceWithholding iw = new InvoiceWithholding()
						.setWithholdingType(getWithholdingType(ivs.get(i).getRetentionKey(), ivs.get(i).getAccount()))
						.setBase(retBase)
						.setPercentage(retPercentage)
						.setQuota(retQuota)
						.setAccountCode(retentionAccount.getCode())
						.setAccountDescription(retentionAccount.getDescription())
						.setAccountId(retentionAccount.getId());
					ai.setWithholdingData(iw);
				}
				ai.setAccountEntry(getEntryBase(domain, user.getLogin(), aonCtx, ai));
				Account financeAccount = new Account();
				if(ivs.get(i).getFinanceAccount() != null && !ivs.get(i).getFinanceAccount().isBlank() ) {
					financeAccount = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), ivs.get(i).getFinanceAccount());
					if(financeAccount == null || financeAccount.getId() == null) {
						financeAccount = new Account()
							.setCode(ivs.get(i).getFinanceAccount())
							.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE FACTURAS)")
							.setAlias("SIN DESCRIPCIÓN")
							.setDomain(domain.getId())
							.setActive(true);
						financeAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), financeAccount);
					}
				}
				
				ai.setPayAccountId(financeAccount.getId())
				  .setPayAccountCode(financeAccount.getCode())
				  .setPayAccountDescription(financeAccount.getDescription());
				
				Finance f = new Finance()
						.setAmount(ai.getInvoice().getTotal())
						.setDueDate(ivs.get(i).getFinanceDate() != null ? ivs.get(i).getFinanceDate() : ai.getInvoice().getIssueDate())
						.setPayMethod(pm.getId());

				
				ai.getInvoice().setFinances(new LinkedList<Finance>());
				if(financeAccount != null && financeAccount.getId() != null) {
					ai.getInvoice().addFinance(f);
				}

				ai = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ai);
				
				if(financeAccount == null || financeAccount.getId() == null){
					f.setInvoice(new Invoice().setId(ai.getInvoice().getId()))
						.setDomain(domain.getId())
						.setRegistry(new Registry().setId(ai.getInvoice().getRegistry()))
						.setRegistryDocument(ai.getInvoice().getRegistryDocument())
						.setRegistryDocumentType(ai.getInvoice().getRegistryDocumentType())
						.setRegistryDocumentCountry(ai.getInvoice().getRegistryDocumentCountry())
						.setRegistryName(ai.getInvoice().getRegistryName())
						.setScope(ai.getInvoice().getScope())
						.setSecurityLevel(ai.getInvoice().getSecurityLevel())
						.setConcept(ai.getInvoice().getDocumentNumber())
						.setFinanceStatus(FinanceStatus.PENDING);
					AON.insertFinance(domain.getName(), domain.getId(), user.getLogin(), f);
				}
			} catch (Exception e) {
				error.setError(false);
				verror.add("Línea " + ivs.get(i).getLine() + ": " + e.getMessage());
			}
		}
		error.setTextError(verror);
		return error;
	}
	
	
	private static void checkCuotas(Domain domain, InvoiceImportClass iic) throws Exception {
		if(iic.getBase() != null && iic.getPercentage() != null && iic.getQuota() != null) {
			Double cuota = iic.getBase()*iic.getPercentage() / 100;
			if(!iic.getQuota().equals(AonMathUtils.round(cuota))) {
				throw new Exception("% IVA y Cuota IVA no coinciden.");
			}
		}

		if(iic.getBase() != null && iic.getRetentionPercentage() != null && iic.getRetentionQuota() != null) {
			Double retentionQuota = iic.getBase()*iic.getRetentionPercentage() / 100;
			if(!iic.getRetentionQuota().equals(AonMathUtils.round(retentionQuota))) {
				throw new Exception("% Retención y Cuota Retención no coinciden.");
			}
		}
		
		if(Utils.isAyudaT(domain.getName()) && AonDateUtils.getYear(iic.getDate()) < 2020) {
			throw new Exception("La Factura es del ejercicio " + AonDateUtils.getYear(iic.getDate()));
		}
	}
	
	private static WithholdingType getWithholdingType(InvoiceClaveRetencion icr, String account) {
		if(InvoiceClaveRetencion.PR.equals(icr)
			|| InvoiceClaveRetencion.G.equals(icr)) {
			return WithholdingType.PROFESSIONAL;
		} else if(InvoiceClaveRetencion.AR.equals(icr)
				|| account.substring(0, 3).equals("621")
				|| account.substring(0, 3).equals("752")) {
			return WithholdingType.RENTING;
		} else if(InvoiceClaveRetencion.CM.equals(icr)
			|| InvoiceClaveRetencion.C.equals(icr)) {
			return WithholdingType.MOVABLE_CAPITAL;
		} else if(InvoiceClaveRetencion.AG.equals(icr)
			|| InvoiceClaveRetencion.H.equals(icr)) {
			return WithholdingType.FARMER;
		} else if(InvoiceClaveRetencion.TA.equals(icr)
				|| account.substring(0, 3).equals("624")) {
			return WithholdingType.TRANSPORT_OPERATOR;
		}
		return WithholdingType.PROFESSIONAL;
	}
	private static Registry getDomainRegistry(Domain domain, String login, String nif) {
		Integer[] rdomains = new Integer[] {domain.getId(), domain.getParentId(), 0};
		LinkedList<Registry> r = AON.getRegistryStream(domain.getName(), domain.getId(), login, f ->
			f.getDomainProperty().in(rdomains)
			.and(f.getDocumentProperty().eq(nif)))
			.collect(Collectors.toCollection(LinkedList::new));
		if(r.stream().filter(f-> f.getDomain().equals(domain.getId())).count() > 0) {
			return r.stream().filter(f-> f.getDomain().equals(domain.getId())).findFirst().get();
		} else if(domain.getParentId() != null && r.stream().filter(f-> f.getDomain().equals(domain.getParentId())).count() > 0) {
			return r.stream().filter(f-> f.getDomain().equals(domain.getParentId())).findFirst().get();
		} else if(r.stream().filter(f-> f.getDomain().equals(0)).count() > 0) {
			return r.stream().filter(f-> f.getDomain().equals(0)).findFirst().get();
		} 
		return null;
	}
	
	private static AccountingRegistry getRegistry(Domain domain, User user, InvoiceType type, String nif, String name, InvoiceTransactionType transaction, RAddress address) {
		// TODO Auto-generated method stub
		if(InvoiceType.SALES.equals(type)) {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif)));
			
			if(customer == null || customer.getId() == null) {
				Registry reg = getDomainRegistry(domain, user.getLogin(), nif);
				if(reg == null || !reg.getDomain().equals(domain.getId())) {
					reg = reg != null ? reg : new Registry();
					reg = AON.insertRegistry(domain.getName(), domain.getId(), user.getLogin(), reg
						.setDomain(domain.getId())
						.setDocument(nif)
						.setName(name));
				}	
				customer = new Customer()
					.setDomain(domain.getId())
					.setRegistry(reg)
					.setStatus(CustomerStatus.ACTIVE)
					.setTransaction(transaction.value())
					.setScope(getScopeId(domain, user));
				customer.setName(reg.getName());
				customer.setId(reg.getId());
				AON.insertCustomer(domain.getName(), domain.getId(), user.getLogin(), customer);
			}
			RAddress ra = AON.getRAddres(domain.getName(), domain.getId(), user.getLogin(), customer.getId());
			if(ra == null || ra.getId() == null) {
				address.setRegistry(customer.getId());
				AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), address);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.CUSTOMER)
					.setId(customer.getId())
					.setName(customer.getName())
					.setAccountId(customer.getAccount());
		} else if(InvoiceType.PURCHASE.equals(type)) {
			Supplier supplier = AON.getSupplier(domain.getName(), domain.getId(), user.getLogin(), f ->
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif))).orElse(new Supplier());
			if(supplier == null || supplier.getId() == null) {
				Registry reg = getDomainRegistry(domain, user.getLogin(), nif);
				if(reg == null || !reg.getDomain().equals(domain.getId())) {
					reg = reg != null ? reg : new Registry();
					reg = AON.insertRegistry(domain.getName(), domain.getId(), user.getLogin(), reg
						.setDomain(domain.getId())
						.setDocument(nif)
						.setName(name));
				}			
				Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());
				
				supplier = new Supplier()
						.setTransaction((short) transaction.ordinal())
						.setStatus(SupplierStatus.ACTIVE)
						.setScope(getScopeId(domain, user))
						.setAccount(acc.getId());
				supplier.setDomain(domain.getId());
				supplier.setId(reg.getId());
				supplier.setName(reg.getName());
				AON.insertSupplier(domain.getName(), domain.getId(), user.getLogin(), supplier);
			}
			RAddress ra = AON.getRAddres(domain.getName(), domain.getId(), user.getLogin(), supplier.getId());
			if(ra == null || ra.getId() == null) {
				address.setRegistry(supplier.getId());
				AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), address);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.SUPPLIER)
					.setId(supplier.getId())
					.setName(supplier.getName())
					.setAccountId(supplier.getAccount());
		} else if(InvoiceType.EXPENSES.equals(type)) {
			Creditor creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif))).orElse(new Creditor());
			if(creditor == null || creditor.getId() == null) {
				Registry reg = getDomainRegistry(domain, user.getLogin(), nif);
				if(reg == null || !reg.getDomain().equals(domain.getId())) {
					reg = reg != null ? reg : new Registry();
					reg = AON.insertRegistry(domain.getName(), domain.getId(), user.getLogin(), reg
						.setDomain(domain.getId())
						.setDocument(nif)
						.setName(name));
				}			
				Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());
			
				creditor = new Creditor()
						.setAccount(acc)
						.setTransaction(transaction)
						.setRegistry(reg)
						.setStatus(CreditorStatus.ACTIVE)
						.setScope(getScopeId(domain, user));
				creditor.setDomain(domain.getId());
				creditor.setId(reg.getId());
				AON.insertCreditor(domain.getName(), domain.getId(), user.getLogin(), creditor);
			}
			RAddress ra = AON.getRAddres(domain.getName(), domain.getId(), user.getLogin(), creditor.getId());
			if(ra == null || ra.getId() == null) {
				address.setRegistry(creditor.getId());
				AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), address);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.CREDITOR)
					.setId(creditor.getId())
					.setName(creditor.getRegistry().getName())
					.setAccountId(creditor.getAccount().getId());
		} 
		return null;
	}

	private static Integer getScopeId(Domain domain, User user) {
		Integer scope = domain.getScope();
		if(domain.getScope() == null) {

			Scope s = AON.getUserScopeStream(domain.getName(), domain.getId(), user.getLogin(), user.getId(), 
					f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(null);
			if(s == null) {
				Integer[] scopes = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
				if(scopes != null && scopes.length > 0)
					scope = scopes[0];
				else {
					s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),  f ->
						f.getDomainProperty().eq(domain.getId())).findFirst().orElse(null);
					if(s == null) {
						s = AON.insertScope(domain.getName(), domain.getId(), user.getLogin(), new Scope()
							.setDescription("GENERAL")
							.setDomain(domain.getId()));
					}
					scope = s.getId();
				}
			} else scope = s.getId();
		}
		return scope;
	}
	
	private static InvoiceType getInvoiceType(String account) {
		if("7".equals(account.substring(0, 1))) {
			return InvoiceType.SALES;
		} else if("60".equals(account.substring(0, 2))) {
			return InvoiceType.PURCHASE;
		}
		return InvoiceType.EXPENSES;
	}
	
	private static InvoiceTransactionType getTransaction(InvoiceImportClass iic) {
		if(InvoiceOpType.EX.equals(iic.getType()) 
				|| InvoiceOpType.EXT.equals(iic.getType())){
			return InvoiceTransactionType.EXTRACOMMUNITY;
		} else if(InvoiceOpType.VI.equals(iic.getType()) 
				|| InvoiceOpType.AI.equals(iic.getType())
				|| InvoiceOpType.NAC.equals(iic.getType())) {
			return InvoiceTransactionType.NATIONAL;
		} else if(InvoiceOpType.ISP.equals(iic.getType())
				|| InvoiceOpType.GISP.equals(iic.getType())) {
			return InvoiceTransactionType.OTHER_ISP;
		} else if(InvoiceOpType.AIB.equals(iic.getType())
				|| InvoiceOpType.AIS.equals(iic.getType())
				|| InvoiceOpType.PIS.equals(iic.getType())
				|| InvoiceOpType.EIB.equals(iic.getType())
				|| InvoiceOpType.INT.equals(iic.getType())) {
			return InvoiceTransactionType.INTRACOMMUNITY;
		} else if(InvoiceOpType.CCM.equals(iic.getType())) {
			return InvoiceTransactionType.CAN_CEU_MEL;
		}
		return InvoiceTransactionType.NATIONAL;
	}
	
	private static AccountEntry getEntryBase(Domain domain, String login, AonConfiguration aonCtx,AccountingInvoice ai) {
		EnterpriseActivity ea = aonCtx.getMainActivity();
		Integer activity = (ea==null?null:ea.getId());
		Integer periodId = null;
		if (ai.getInvoice().getIssueDate() != null) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
				AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, ai.getInvoice().getIssueDate());
				periodId = (period == null? null : period.getId());
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(ai.getInvoice().getDomain())
				.setConfidential(false)
				.setEntryDate(ai.getInvoice().getIssueDate())
				.setActivity(activity)
				.setComments(ai.getInvoice().getComments())
				.setDirty(false);
		ai.getInvoice().getType().visit(ai.getInvoice(),  new IInvoiceTypeVisitor() {
			@Override public void visitUndeductible(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(true);
			}
			@Override public void visitSales(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);}
			@Override public void visitPurchase(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);}
			@Override public void visitExpenses(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(false);
			}
		});
		return accountEntry;
	}

	private static Boolean isSameReference(String reference, String serie, Integer number, InvoiceImportClass iic) {
		Boolean snBool = false;
		if(serie != null && number != null) {
			snBool = serie.equals(iic.getSerie()) && number.equals(iic.getNumber());
		} else if( serie == null && number != null) {
			snBool = number.equals(iic.getNumber());
		}
		return snBool || (reference != null && reference.equals(iic.getRef()));
	}
	

}
