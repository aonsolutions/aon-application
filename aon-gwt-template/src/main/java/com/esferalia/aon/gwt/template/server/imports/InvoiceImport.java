package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.InvoiceImportClass;
import com.esferalia.aon.gwt.template.shared.InvoiceImportClass.InvoiceClaveRetencion;
import com.esferalia.aon.gwt.template.shared.InvoiceImportClass.InvoiceOpType;
import com.esferalia.aon.gwt.template.shared.InvoiceImportClass.InvoiceSubClaveRetencion;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
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
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceImport {

	public static InvoiceImport getInstance() {
		return new InvoiceImport();
	}

	InvoiceImportClass inv; 
	InvoiceImportClass ant;

	public List<InvoiceImportClass> importation(byte[] data){
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		try (HSSFWorkbook workbook = new HSSFWorkbook(bais)){
			HSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<InvoiceImportClass> list = new LinkedList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
			ant = null;
			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				inv = new InvoiceImportClass();
				inv.setLine(row.getRowNum() + 1);	
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue().trim());
					} else if(cell.getColumnIndex() < titleList.size()){
						String title = titleList.get(cell.getColumnIndex());
						check(title, cell);			
					}
				});
				if(ant != null && isSameReference(ant, inv)) {
					list.getLast().getLines().add(inv);
				} else if(row.getRowNum() != 0 && !inv.isEmpty()) {
					inv.getLines().add(inv);
					ant = inv;
					list.add(inv);
				}
			});

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OfficeXmlFileException e){
			return importationX(data);
		}
		return new LinkedList<>();
	}

	public List<InvoiceImportClass> importationX(byte[] data){
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		try(XSSFWorkbook workbook = new XSSFWorkbook(bais)) {
			XSSFSheet sheet = workbook.getSheetAt(0);

			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<InvoiceImportClass> list = new LinkedList<>();
			
			Iterator<Row> rowIterator = sheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
			ant = null;
			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				inv = new InvoiceImportClass();
				inv.setLine(row.getRowNum() + 1);	
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue().trim());
					} else if(cell.getColumnIndex() < titleList.size()){
						String title = titleList.get(cell.getColumnIndex());
						check(title, cell);			
					}
				});
				
				if(ant != null && isSameReference(ant, inv)) {
					list.getLast().getLines().add(inv);
				} else if(row.getRowNum() != 0 && !inv.isEmpty()) {
					inv.getLines().add(inv);
					ant = inv;
					list.add(inv);
				}
			});

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new LinkedList<>();
	}
	
	private boolean compare(String value1, String value2) {
		if(value1 == null || value2 == null) return false;
		Collator c = Collator.getInstance(new Locale("es"));
		c.setStrength(Collator.PRIMARY);
		return c.equals(value1, value2);
	}
	
	private boolean isTipoOperacion(String value) {
		return compare(IConstants.TIPO_DE_OPERACION, value)
			|| compare(IConstants.TIPO_OPERACION, value);
	}
	
	private boolean isTipoFactura(String value) {
		return compare(IConstants.TIPO_FACTURA, value);
	}
	
	private boolean isFecha(String value) {
		return compare(IConstants.FECHA, value);
	}
	
	private boolean isSerie(String value) {
		return compare(IConstants.SERIE, value)
			|| compare(IConstants.SERIES, value);
	}
	
	private boolean isNumero(String value) {
		return compare(IConstants.NUMERO, value);
	}
	
	private boolean isReference(String value) {
		return compare(IConstants.REFERENCIA, value)
			|| compare(IConstants.NUMERO_DE_FACTURA, value)
			|| compare(IConstants.NUMERO_FACTURA, value)
			|| compare(IConstants.CODIGO_REFERENCIA, value)
			|| compare(IConstants.CODIGO_DE_REFERENCIA, value);
	}

	private boolean isNif(String value) {
		return compare(IConstants.NIF, value);
	}
	
	private boolean isNombre(String value) {
		return compare(IConstants.NOMBRE, value);
	}
	
	private boolean isCuentaContraparte(String value) {
		return compare(IConstants.CUENTA_CONTRAPARTE, value);
	}
	
	private boolean isTercero(String value) {
		return compare(IConstants.TERCERO, value);
	}
	
	private boolean isObservaciones(String value) {
		return compare(IConstants.OBSERVACIONES, value)
			|| compare(IConstants.CONCEPTO, value);
	}
	
	private boolean isDireccion(String value) {
		return compare(IConstants.DIRECCION, value);
	}
	
	private boolean isCiudad(String value) {
		return compare(IConstants.CIUDAD, value);
	}
	
	private boolean isProvincia(String value) {
		return compare(IConstants.PROVINCIA, value);
	}
	
	private boolean isCodigoPostal(String value) {
		return compare(IConstants.CODIGO_POSTAL, value);
	}
	
	private boolean isPais(String value) {
		return compare(IConstants.PAIS, value);
	}
	
	private boolean isCuentaExplotacion(String value) {
		return compare(IConstants.CUENTA_BASE, value)
			|| compare(IConstants.CUENTA_CONTABLE, value)
			|| compare(IConstants.CUENTA_EXPLOTACION, value)
			|| compare(IConstants.CUENTA_DE_EXPLOTACION, value);
	}
	
	private boolean isPorcentajeImpuesto(String value) {
		value = value.replace(" ", "");
		return compare("%" + IConstants.IMPUESTO, value)
			|| compare("%" + IConstants.IVA, value);
	}
	
	private boolean isCuotaImpuesto(String value) {
		return compare(IConstants.CUOTA_IMPUESTO, value)
			|| compare(IConstants.CUOTA_IVA, value);
	}
	
	private boolean isPorcentajeRe(String value) {
		value = value.replace(" ", "");
		return compare("%" + IConstants.RE, value);
	}
	
	private boolean isCuotaRe(String value) {
		return compare(IConstants.CUOTA_RE, value);
	}
	
	private boolean isPorcentajeRetencion(String value) {
		value = value.replace(" ", "");
		return compare("%" + IConstants.RETENCION, value)
			|| compare("%" + IConstants.IRPF, value);
	}
	
	private boolean isCuotaRetencion(String value) {
		return compare(IConstants.CUOTA_RETENCION, value)
			|| compare(IConstants.CUOTA_IRPF, value);
	}
	
	private boolean isDescripcionCuenta(String value) {
		return compare(IConstants.DESCRIPCION_CUENTA, value);
	}
	
	private boolean isConceptoDetalle(String value) {
		return compare(IConstants.CONCEPTO_DETALLE, value);
	}
	
	private boolean isBase(String value) {
		return compare(IConstants.BASE, value)
			|| compare(IConstants.BASE_IMPONIBLE, value);
	}
	
	private boolean isTotal(String value) {
		return compare(IConstants.TOTAL, value)
			|| compare(IConstants.TOTAL_FACTURA, value);
	}
	
	private boolean isClaveRetencion(String value) {
		return compare(IConstants.CLAVE_RETENCION, value);
	}
	
	private boolean isSubclaveRetencion(String value) {
		return compare(IConstants.SUBCLAVE_RETENCION, value);
	}
	
	private boolean isCuentaTesoreria(String value) {
		return compare(IConstants.CUENTA_TESORERIA, value)
			|| compare(IConstants.PAGO_POR_CAJA, value);
	}
	
 	private void check(String title, Cell cell) {
		Object o = Utils.getObjectValue(cell);
		if(o == null) return;
	
		if(isTipoOperacion(title)) {
			inv.setType(InvoiceOpType.safeValueOf(o.toString().trim()));
			return;
		}
		
		if(isTipoFactura(title)) {
			inv.setInvoiceType(InvoiceType.safeValueOf(o.toString()));
			return;
		}

		if(isFecha(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.simpleParse(o.toString());
			}
			inv.setDate(date);
			return;
		}
		
		if(isSerie(title)) {
			String serie = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				serie = Integer.toString(Utils.parseDouble(o.toString()).intValue());
			} 
			inv.setSerie(serie);
			return;
		}
		
		if(isNumero(title)) {
			Double d = Utils.parseDouble(o.toString());
			inv.setNumber(d != null ? d.intValue() : null);
			return;
		}
		
		if(isReference(title)) {
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				inv.setRef(NumberToTextConverter.toText(cell.getNumericCellValue()));
			} else inv.setRef(o.toString());
			return ;
		}
		if(isNif(title)) {
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				inv.setNif(NumberToTextConverter.toText(cell.getNumericCellValue()));
			} else inv.setNif(o.toString());
			return ;
		}
		
		if(isNombre(title)) {
			inv.setName(o.toString());
			return ;
		}
		
		if(isCuentaContraparte(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			inv.setRegistryAccount(Utils.calculateAccount(acc));
			return ;
		}
		
		if(isTercero(title)) {
			inv.setThird(o.toString());
			return;
		}
		
		if(isObservaciones(title)) {
			inv.setConcept(o.toString());
			return;
		}
		
		if(isDireccion(title)) {
			inv.setAddress(o.toString());
			return;
		}
		
		if(isCiudad(title)) {
			inv.setCity(o.toString());
			return;
		}
		
		if(isProvincia(title)) {
			inv.setProvince(o.toString());
			return;
		}
		
		if(isCodigoPostal(title)) {
			String zip = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				zip = Integer.toString(Utils.parseDouble(zip).intValue());
			}
			inv.setZip(zip.length() < 5 ? "0" + zip : zip);
			return;
		}
		
		if(isPais(title)) {
			inv.setCountry(Country.safeValueOf(o.toString()));
			return;
		}
		
		if(isCuentaExplotacion(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			inv.setAccount(Utils.calculateAccount(acc));
			return;
		}
		
		if(isDescripcionCuenta(title)){
			inv.setAccountDescription(o.toString());
			return;
		}
		if(isConceptoDetalle(title)) {
			// TODO CONCEPTO DETALLE
			return;
		}
		if(isBase(title)) {
			inv.setBase(Utils.parseDouble(o));
			return;
		}
		if(isPorcentajeImpuesto(title)) {
			Double percent = Utils.parseDouble(o);
			if(percent == null) percent = 0.0;
			inv.setPercentage(percent);
			return;
		}
		if(isCuotaImpuesto(title)) {
			inv.setQuota(Utils.parseDouble(o));
			return;
		}
		if(isPorcentajeRe(title)) {
			Double percent = Utils.parseDouble(o);
			if(percent == null) percent = 0.0;
			inv.setRePercentage(percent);
			return;
		}
		
		if(isCuotaRe(title)) {
			inv.setReQuota(Utils.parseDouble(o));
			return;
		}
		
		if(isPorcentajeRetencion(title)) {
			Double percent = Utils.parseDouble(o);
			if(percent == null) percent = 0.0;
			inv.setRetentionPercentage(percent);
			return;
		}
		
		if(isCuotaRetencion(title)) {
			inv.setRetentionQuota(Utils.parseDouble(o));
			return;
		}
		
		if(isTotal(title)) {
			inv.setTotal(Utils.parseDouble(o));
			return;
		}
		
		if(isClaveRetencion(title)) {
			inv.setRetentionKey(InvoiceClaveRetencion.safeValueOf(o.toString()));
			return;
		}
		
		if(isSubclaveRetencion(title)) {
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
		
		if(title.contains("FECHA VTO")) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			
			String numberStr = title.substring(title.length()-1);
			Integer number = AonNumberUtils.toint(numberStr);
			if(inv.getFinances().size() > number) {
				inv.getFinances().get(number).setDueDate(date);
			} else {
				Finance finance = new Finance()
						.setDueDate(date);
				inv.getFinances().add(finance);
			}
		}
		
		if(title.contains("IMPORTE VTO")) {
			Double amount = Utils.parseDouble(o);
			
			String numberStr = title.substring(title.length()-1);
			Integer number = AonNumberUtils.toint(numberStr);
			if(inv.getFinances().size() > number) {
				inv.getFinances().get(number).setAmount(amount);
			} else {
				Finance finance = new Finance()
						.setAmount(amount);
				inv.getFinances().add(finance);
			}
		}
		
		if(title.contains("FORMA PAGO VTO")) {
			String paymethod = o.toString();
			
			String numberStr = title.substring(title.length()-1);
			Integer number = AonNumberUtils.toint(numberStr);
			if(inv.getFinances().size() > number) {
				inv.getFinances().get(number).setPayMethodName(paymethod);
			} else {
				Finance finance = new Finance()
						.setPayMethodName(paymethod);
				inv.getFinances().add(finance);
			}
		}
		
		if(isCuentaTesoreria(title)) {
			String acc = o.toString();
			if(CellType.NUMERIC == cell.getCellTypeEnum()) {
				acc = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
			inv.setFinanceAccount(Utils.calculateAccount(acc));
			return;
		}
		
		if("INVERSION".equalsIgnoreCase(title)
				|| "INVERSIÓN".equalsIgnoreCase(title)) {
			String val = o.toString();
			inv.setInvestment(IConstants.TRUE.equalsIgnoreCase(val) || IConstants.SI.equalsIgnoreCase(val));
			return;
		}
		
		// TODO SUPLIDO, CUENTA CONTRAPARTE
	}

	public static Error insertInvoices(Domain domain, User user, Integer i, List<InvoiceImportClass> ivs) {
		Error error = new Error().setError(true);
		if(i >= ivs.size()) {
			error.setLine(i);
			return error;
		}

		AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), user.getLogin());
	
		PayMethod pm = aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty() ? aonCtx.getPayMethods().get(0) : new PayMethod(); 
		Account outputAccount = aonCtx.accounting().getDefaultChargedVatAccount();
		Account inputAccount = aonCtx.accounting().getDefaultPaidVatAccount();
		Account adjAccount = aonCtx.accounting().getVatNegativeAdjustAccount();
		
		try {
			validate(ivs.get(i));
			AccountingInvoice ai = new AccountingInvoice();
			ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());

			RegistryAddress address = buildAddress(aonCtx, domain, ivs.get(i));
			Invoice invoice = buildInvoice(domain, user, ivs.get(i));
			AccountingRegistry ar = getRegistry(domain, user, invoice, ivs.get(i), address);			
			invoice.setRegistry(ar.getId());
			ai.setRegistry(ar);
			ai.setInvoice(invoice);

			String reference = ivs.get(i).getRef();
			String serie = ivs.get(i).getSerie();
			Integer number = ivs.get(i).getNumber();
			Double total = 0.0;
			Double base = 0.0;
			Double retBase = 0.0;
			Double retQuota = 0.0;
			Double retPercentage = 0.0;
			Integer j = i;
			while(ivs.size() > j && isSameReference(ivs.get(i), ivs.get(j))) {
				if(ivs.get(j).getRetentionQuota() != null) {
					retBase = retBase + ivs.get(j).getBase();
					retPercentage = ivs.get(j).getRetentionPercentage();
					retQuota = retQuota + ivs.get(j).getRetentionQuota();
					ai.getInvoice().setWithholding(true);
				}
				
				Account expAccount = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), ivs.get(j).getAccount());
				if(expAccount == null) {
					expAccount = new Account()
						.setCode(ivs.get(j).getAccount())
						.setDescription(ivs.get(j).getAccountDescription() != null 
								? ivs.get(j).getAccountDescription()
								: "SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE FACTURAS)")
						.setAlias(ivs.get(j).getAccountDescription() != null 
								? ivs.get(j).getAccountDescription()
								:"SIN DESCRIPCIÓN")
						.setDomain(domain.getId())
						.setActive(true);
					expAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), expAccount);
				}
				
				
				InvoiceVAT vat = new InvoiceVAT()
					.setPrepayment("5600".equals(ivs.get(i).getAccount().substring(0, 4)) || "5660".equals(ivs.get(i).getAccount().substring(0, 4)))
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
				if(invoice.isUndeductible()) {
					vat.setBase(ivs.get(j).getTotal());
					vat.setPercentage(0.0);
					vat.setQuota(0.0);
					vat.setDeductibleQuota(0.0);
					vat.setSurcharge(0.0);
					vat.setSurchargeQuota(0.0);
				}

				ai.addVat(vat);
				total = total + (invoice.mustApplyISP() ? ivs.get(j).getBase() : ivs.get(j).getTotal());
				base = base + ivs.get(j).getBase();
				j++;
			}
			Integer cci = i;
			i = !i.equals(j) ? j-1 : i;
			for(Integer k = cci; k < j; k++) {
				checkCuotas(domain, ivs.get(k));
			}
			ai.getInvoice().setTotal(total);
			ai.getInvoice().setTaxableBase(base);
			ai.getInvoice().setVatQuota(total - base);
		
			if(ai.getInvoice().isWithholding()) {
				Account retentionAccount = (invoice.isSales() )
						?aonCtx.accounting().getDefaultPaidRetAccount()
						:aonCtx.accounting().getDefaultChargedRetAccount();
		
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

			ai.getInvoice().setFinances(new LinkedList<>());
			if(ivs.get(i).getFinances().isEmpty()) {
				Finance f = new Finance()
					.setAmount(ai.getInvoice().getTotal())
					.setDueDate(ivs.get(i).getFinanceDate() != null ? ivs.get(i).getFinanceDate() : ai.getInvoice().getIssueDate())
					.setPayMethod(pm.getId())
					.setPayment(!invoice.isSales());
				ai.getInvoice().getFinances().add(f);
			} else {
				for (Finance fin : ivs.get(i).getFinances()) {
					if(aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty()) {
						PayMethod pmAux = aonCtx.getPayMethods().stream().filter(p -> p.getName().equals(fin.getPayMethodName())).findFirst().orElse(pm);
						fin.setPayMethod(pmAux.getId());
					} else fin.setPayMethod(pm.getId());
					fin.setPayment(!invoice.isSales());
					if(financeAccount != null && financeAccount.getId() != null) {
						ai.getInvoice().addFinance(fin);
					}

				} 
			}

			ai = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ai);

			if(financeAccount == null || financeAccount.getId() == null){
				if(ivs.get(i).getFinances().isEmpty()) {
					Finance f = new Finance()
						.setAmount(ai.getInvoice().getTotal())
						.setDueDate(ivs.get(i).getFinanceDate() != null ? ivs.get(i).getFinanceDate() : ai.getInvoice().getIssueDate())
						.setPayMethod(pm.getId())
						.setPayment(!invoice.isSales());
					insertFinances(domain, user.getLogin(), ai, f);
				} else {
					for (Finance fin : ivs.get(i).getFinances()) {
						if(aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty()) {
							PayMethod pmAux = aonCtx.getPayMethods().stream().filter(p -> p.getName().equals(fin.getPayMethodName())).findFirst().orElse(pm);
							fin.setPayMethod(pmAux.getId());
						} else fin.setPayMethod(pm.getId());
						fin.setPayment(!invoice.isSales());
						insertFinances(domain, user.getLogin(), ai, fin);						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError(false);
			error.setTextError("Línea " + ivs.get(i).getLine() + ": " + e.getMessage());
		}
		error.setLine(i);
		return error;
	}

	private static void validate(InvoiceImportClass iic) throws Exception {
		if(iic.getAccount() == null || iic.getAccount().isBlank()) {
			throw new Exception("La cuenta contable es un dato obligatorio.");
		}
		
		if(iic.getRef() == null && iic.getNumber() == null) {
			throw new Exception("La Referencia o Serie/Número son incorrectas.");
		}
		
		if(iic.getSerie() != null && iic.getSerie().length() > 5) {
			throw new Exception("La serie no puede tener más de 5 carácteres");
		}
	}
	
	private static Invoice buildInvoice(Domain domain, User user, InvoiceImportClass iic) {
		Invoice invoice = new Invoice();
		invoice.setScope(new Scope().setId(getScopeId(domain, user)));
		invoice.setService(InvoiceOpType.PIS.equals(iic.getType())|| InvoiceOpType.AIS.equals(iic.getType()));
		invoice.setTransaction(getTransaction(iic));
		invoice.setInvestment(iic.isInvestment() != null && iic.isInvestment());
		invoice.setDomain(domain.getId());
		invoice.setIssueDate(iic.getDate());
		invoice.setTaxDate(iic.getDate());
		invoice.setType(iic.getInvoiceType() != null
			? iic.getInvoiceType()
			: getInvoiceType(iic.getAccount()));
		if(iic.getSerie() != null && invoice.isSales()) {
			invoice.setSeries(iic.getSerie());
		} 
		
		if(iic.getNumber() != null && invoice.isSales()) {
			invoice.setNumber(iic.getNumber());	
		}
		
		invoice.setReferenceCode(iic.getRef() != null ? iic.getRef() 
				: (invoice.getSeries() != null ? invoice.getSeries() + "/" : "") 
				+ AonStringUtils.leftPad(Integer.toString(invoice.getNumber()), 6, "0"));
		
		invoice.setWithholding(iic.getRetentionQuota() != null 
			&& iic.getRetentionQuota() != 0);
		invoice.setRemarks(iic.getConcept());
		invoice.setSurcharge(iic.getRePercentage() != null && iic.getPercentage() > 0);
		if(iic.getTotal() < 0) {
			invoice.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		}
		return invoice;
	}

	private static RegistryAddress buildAddress(AonConfiguration aonCtx, Domain domain, InvoiceImportClass iic) {
		RegistryAddress ra = new RegistryAddress()
				.setDomain(domain.getId())
				.setAddress(iic.getAddress())
				.setCity(iic.getCity())
				.setZip(iic.getZip() != null && iic.getZip().length() < 5 
					? "0" + iic.getZip() : iic.getZip());

		if(iic.getProvince() != null) {
			GeoZone prgz = null;
			GeoZone crgz = null;
			Provinces pr = Provinces.getProvince(iic.getProvince());
			if(pr == null && ra.getZip() != null ) {
				pr = Provinces.getProvinceById(ra.getZip().substring(0,2));
			}
			for(GeoZone gz : aonCtx.getGeozones()) {
				if(gz.getCode().equals(iic.getCountry().getIso2())) {
					crgz = gz;
				}
				if(pr != null && gz.getCode().equals(pr.getId())) {
					prgz = gz;
				}
			}
			
			if(prgz != null) {
				ra.setGeozone(prgz.getId());
				ra.setGeozoneCode(prgz.getCode());
				ra.setGeozoneName(prgz.getName());
			} else if(crgz != null) {
				ra.setGeozone(crgz.getId());
				ra.setGeozoneCode(crgz.getCode());
				ra.setGeozoneName(crgz.getName());
			}
		}
		return ra;
	}
	
	private static void insertFinances(Domain domain, String login, AccountingInvoice ai, Finance f) {
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
		AON.insertFinance(domain.getName(), domain.getId(), login, f);
	}
	
	public static Error insertInvoice(Domain domain, User user, Integer i, InvoiceImportClass iic) {
		Error error = new Error().setError(true);

		AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), user.getLogin());
	
		PayMethod pm = aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty() ? aonCtx.getPayMethods().get(0) : new PayMethod(); 
		Account outputAccount = aonCtx.accounting().getDefaultChargedVatAccount();
		Account inputAccount = aonCtx.accounting().getDefaultPaidVatAccount();
		Account adjAccount = aonCtx.accounting().getVatNegativeAdjustAccount();
		
		try {
			if(iic.getAccount() == null || iic.getAccount().isBlank()) {
				throw new Exception("La cuenta contable es un dato obligatorio.");
			}
			
			if(iic.getRef() == null && iic.getNumber() == null) {
				throw new Exception("La Referencia o Serie/Número son incorrectas.");
			}
			
			if(iic.getSerie() != null && iic.getSerie().length() > 5) {
				throw new Exception("La serie no puede tener más de 5 carácteres");
			}
			
			AccountingInvoice ai = new AccountingInvoice();
			ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());

			Invoice invoice = new Invoice();
			invoice.setScope(new Scope().setId(getScopeId(domain, user)));
			invoice.setService(InvoiceOpType.PIS.equals(iic.getType())|| InvoiceOpType.AIS.equals(iic.getType()));
			invoice.setTransaction(getTransaction(iic));
			invoice.setInvestment(iic.isInvestment() != null && iic.isInvestment());
			invoice.setDomain(domain.getId());
			invoice.setIssueDate(iic.getDate());
			invoice.setTaxDate(iic.getDate());
			invoice.setType(iic.getInvoiceType() != null
				? iic.getInvoiceType()
				: getInvoiceType(iic.getAccount()));
			if(iic.getSerie() != null && invoice.isSales()) {
				invoice.setSeries(iic.getSerie());
			} 
			
			if(iic.getNumber() != null && invoice.isSales()) {
				invoice.setNumber(iic.getNumber());	
			}
			
			invoice.setReferenceCode(iic.getRef() != null ? iic.getRef() 
					: (invoice.getSeries() != null ? invoice.getSeries() + "/" : "") 
					+ AonStringUtils.leftPad(Integer.toString(invoice.getNumber()), 6, "0"));
			
			invoice.setWithholding(iic.getRetentionQuota() != null 
				&& iic.getRetentionQuota() != 0);
			invoice.setRemarks(iic.getConcept());
			invoice.setSurcharge(iic.getRePercentage() != null && iic.getPercentage() > 0);
			if(iic.getTotal() < 0) {
				invoice.setRectificationType(RectificationType.NORMAL_RECTIFIER);
			}
		
			RegistryAddress address = buildAddress(aonCtx, domain, iic);

//			String nif = iic.getNif();
//			String name = iic.getName();
			
			AccountingRegistry ar = getRegistry(domain, user, invoice, iic, address);			
			invoice.setRegistry(ar.getId());
			ai.setRegistry(ar);
			ai.setInvoice(invoice);

			Double total = 0.0;
			Double base = 0.0;
			Double retBase = 0.0;
			Double retQuota = 0.0;
			Double retPercentage = 0.0;
	
			for(InvoiceImportClass aux : iic.getLines()) {
				if(aux.getRetentionQuota() != null) {
					retBase = retBase + aux.getBase();
					retPercentage = aux.getRetentionPercentage();
					retQuota = retQuota + aux.getRetentionQuota();
					ai.getInvoice().setWithholding(true);
				}
				
				Account expAccount = getAccount(domain, user, aux.getAccount(), aux.getAccountDescription());
				
				InvoiceVAT vat = new InvoiceVAT()
					.setPrepayment("5600".equals(iic.getAccount().substring(0, 4)) || "5660".equals(iic.getAccount().substring(0, 4)))
					.setVatDeductionType(VatDeductionType.WITH_RIGHT)
					.setBase(aux.getBase() != null 
							? aux.getBase() : 0.0)
					.setPercentage(aux.getPercentage() != null
							? aux.getPercentage() : 0.0)
					.setQuota(aux.getQuota() != null
							? aux.getQuota() : 0.0)
					.setSurcharge(aux.getRePercentage() != null
							? aux.getRePercentage() : 0.0)
					.setSurchargeQuota(aux.getReQuota() != null
							? aux.getReQuota() : 0.0)
					//.setInvestAsset(ivs.get(j).getInvestAsset())
					.setDeductiblePercent(100.0)
					.setDeductibleQuota(aux.getQuota() != null
							? aux.getQuota() : 0.0)
					.setWithholding(aux.getRetentionQuota() != null
							&& aux.getRetentionQuota() != 0)
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
				if(invoice.isUndeductible()) {
					vat.setBase(aux.getTotal());
					vat.setPercentage(0.0);
					vat.setQuota(0.0);
					vat.setDeductibleQuota(0.0);
					vat.setSurcharge(0.0);
					vat.setSurchargeQuota(0.0);
				}
				ai.addVat(vat);
				total = total + (invoice.isIsp() ? aux.getBase() : aux.getTotal());
				base = base + aux.getBase();
				checkCuotas(domain, aux);
			}

			ai.getInvoice().setTotal(total);
			ai.getInvoice().setTaxableBase(base);
			ai.getInvoice().setVatQuota(total - base);
		
			if(ai.getInvoice().isWithholding()) {
				Account retentionAccount = (invoice.isSales() )
						?aonCtx.accounting().getDefaultPaidRetAccount()
						:aonCtx.accounting().getDefaultChargedRetAccount();
		
				InvoiceWithholding iw = new InvoiceWithholding()
					.setWithholdingType(getWithholdingType(iic.getRetentionKey(), iic.getAccount()))
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
			if(iic.getFinanceAccount() != null && !iic.getFinanceAccount().isBlank() ) {
				financeAccount = getAccount(domain, user, iic.getFinanceAccount(), "");
			}
				
			ai.setPayAccountId(financeAccount.getId())
			  .setPayAccountCode(financeAccount.getCode())
			  .setPayAccountDescription(financeAccount.getDescription());

			ai.getInvoice().setFinances(new LinkedList<Finance>());
			if(iic.getFinances().isEmpty()) {
				Finance f = new Finance()
					.setAmount(ai.getInvoice().getTotal())
					.setDueDate(iic.getFinanceDate() != null ? iic.getFinanceDate() : ai.getInvoice().getIssueDate())
					.setPayMethod(pm.getId())
					.setPayment(!invoice.isSales());
			
				if(financeAccount != null && financeAccount.getId() != null) {
					ai.getInvoice().addFinance(f);
				}
			} else {
				for (Finance fin : iic.getFinances()) {
					if(aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty()) {
						PayMethod pmAux = aonCtx.getPayMethods().stream().filter(p -> p.getName().equals(fin.getPayMethodName())).findFirst().orElse(pm);
						fin.setPayMethod(pmAux.getId());
					} else fin.setPayMethod(pm.getId());
					fin.setPayment(!invoice.isSales());
					if(financeAccount != null && financeAccount.getId() != null) {
						ai.getInvoice().addFinance(fin);
					}
				} 
			}

			ai = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ai);

			if(financeAccount == null || financeAccount.getId() == null) {
				if(iic.getFinances().isEmpty()) {
					Finance f = new Finance()
						.setAmount(ai.getInvoice().getTotal())
						.setDueDate(iic.getFinanceDate() != null ? iic.getFinanceDate() : ai.getInvoice().getIssueDate())
						.setPayMethod(pm.getId())
						.setPayment(!invoice.isSales());
					insertFinances(domain, user.getLogin(), ai, f);
				} else {
					for (Finance fin : iic.getFinances()) {
						if(aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty()) {
							PayMethod pmAux = aonCtx.getPayMethods().stream().filter(p -> p.getName().equals(fin.getPayMethodName())).findFirst().orElse(pm);
							fin.setPayMethod(pmAux.getId());
						} else fin.setPayMethod(pm.getId());
						fin.setPayment(!invoice.isSales());
						insertFinances(domain, user.getLogin(), ai, fin);						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError(false);
			error.setTextError("Línea " + iic.getLine() + ": " + e.getMessage());
		}
		error.setLine(i);
		return error;
	}
	
	
	private static Account getAccount(Domain domain, User user, String accountCode, String accountName) {
		Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), accountCode);
		if(account == null) {
			String alias = !AonStringUtils.isBlank(accountName) && accountName.length()> 32 
				? accountName.substring(0, 32) : accountName;
					
			account = new Account()
				.setCode(accountCode)
				.setDescription(!AonStringUtils.isBlank(accountName) 
						? accountName : "SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE FACTURAS)")
				.setAlias(!AonStringUtils.isBlank(accountName) 
						? alias : "SIN DESCRIPCIÓN")
				.setDomain(domain.getId())
				.setActive(true);
			account = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), account);
		}
		return account;
	}
	
	private static void checkCuotas(Domain domain, InvoiceImportClass iic) throws Exception {
		if(iic.getBase() != null && iic.getPercentage() != null && iic.getQuota() != null) {
			Double cuota = AonMathUtils.round(iic.getBase()*iic.getPercentage() / 100);
			Double iicQuota = AonMathUtils.round(iic.getQuota());
			Double dif = iicQuota - cuota;
			if(!iicQuota.equals(cuota) && (dif < -0.015 || dif > 0.015)) {
				throw new Exception("% IVA y Cuota IVA no coinciden.");
			}
		}

		if(iic.getBase() != null && iic.getRetentionPercentage() != null && iic.getRetentionQuota() != null) {
			Double retentionQuota = iic.getBase()*iic.getRetentionPercentage() / 100;
			Double dif = iic.getRetentionQuota() - retentionQuota;
			if(!iic.getRetentionQuota().equals(AonMathUtils.round(retentionQuota)) && (dif < -0.01 || dif > 0.01)) {
				throw new Exception("% Retención y Cuota Retención no coinciden.");
			}
		}

		AccountPeriod period = ACCOUNTING.getAccountPeriod(domain.getName(), domain.getId(), "", iic.getDate());
		if(period.isClosed()) {
			throw new Exception("El Ejercicio " + period.getName() + " de la factura está cerrado.");
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

	private static AccountingRegistry getRegistry(Domain domain, User user, Invoice invoice, InvoiceImportClass iic, RegistryAddress address) {		
		InvoiceType type = invoice.getType();
		InvoiceTransactionType transaction = invoice.getTransaction();
		String nif = iic.getNif();
		String name = iic.getName();
		Country country = iic.getCountry();
		
		Registry reg = AON.getRegistry(domain, user, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif)));
		if(reg == null || reg.isEmpty()) {
			reg = reg != null ? reg : new Registry();
			DocumentType dtype = DocumentType.OTHER;
			if(Country.ES.equals(country) && AonDocumentUtil.isValidCIF(nif)) {
				dtype = DocumentType.CIF;
			} else if(Country.ES.equals(country) && AonDocumentUtil.isValidDNI(nif)) {
				dtype = DocumentType.NIF;
			}
			reg.setDomain(domain)
				.setDocument(nif)
				.setDocumentCountry(country)
				.setDocumentType(dtype)
				.setName(name)
				.setNationality(country);
		}
		
		if(InvoiceType.SALES.equals(type)) {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif)));
			if(customer.isEmpty()) {	
				customer = new Customer()
					.copy(reg)
					.setStatus(RegistryStatus.ACTIVE)
					.setTransaction( transaction )
					.setScope(getScopeId(domain, user));
				customer.setDomain(domain);
				customer.setName(reg.getName());
				customer.setId(reg.getId());
				if(!AonStringUtils.isBlank(iic.getRegistryAccount())) {
					Account account = getAccount(domain, user, iic.getRegistryAccount(), reg.getName());
					customer.setAccount(account.getId());
				}
				AON.saveCustomer(domain.getName(), domain.getId(), user.getLogin(), customer);
			}
			if(customer.getAccount() == null && !AonStringUtils.isBlank(iic.getRegistryAccount())) {
				Account account = getAccount(domain, user, iic.getRegistryAccount(), reg.getName());
				customer.setAccount(account.getId());
				AON.saveCustomer(domain.getName(), domain.getId(), user.getLogin(), customer);
			}
			
			Integer rid = customer.getId();
			RegistryAddress ra = AON.get(domain, user, (RegistryAddressFilter) 
					f -> f.getRegistryProperty().eq(rid));
			if(ra == null || ra.getId() == null) {
				address.setRegistry(customer.getId());
				AON.save(domain, user.getLogin(), address);
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
				Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());
				
				supplier = new Supplier()
						.copy(reg)
						.setTransaction(transaction)
						.setStatus(RegistryStatus.ACTIVE)
						.setScope(getScopeId(domain, user))
						.setAccount(acc.getId());
				if(!AonStringUtils.isBlank(iic.getRegistryAccount())) {
					Account account = getAccount(domain, user, iic.getRegistryAccount(), reg.getName());
					supplier.setAccount(account.getId());
				}
				AON.saveSupplier(domain.getName(), domain.getId(), user.getLogin(), supplier);
			}
			if(supplier.getAccount() == null && !AonStringUtils.isBlank(iic.getRegistryAccount())) {
				Account account = getAccount(domain, user, iic.getRegistryAccount(), reg.getName());
				supplier.setAccount(account.getId());
				AON.saveSupplier(domain.getName(), domain.getId(), user.getLogin(), supplier);
			}
			
			Integer rid = supplier.getId();
			RegistryAddress ra = AON.get(domain, user, (RegistryAddressFilter) 
					f -> f.getRegistryProperty().eq(rid));
			if(ra == null || ra.getId() == null) {
				address.setRegistry(supplier.getId());
				AON.save(domain, user.getLogin(), address);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.SUPPLIER)
					.setId(supplier.getId())
					.setName(supplier.getName())
					.setAccountId(supplier.getAccount());
		} else if(InvoiceType.EXPENSES.equals(type) || InvoiceType.UNDEDUCTIBLE.equals(type)) {
			Creditor creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif))).orElse(new Creditor());
			if(creditor == null || creditor.getId() == null) {
				Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());
			
				creditor = new Creditor()
						.copy(reg)
						.setAccount(acc==null?null:acc.getId())
						.setTransaction(transaction)
						.setStatus(RegistryStatus.ACTIVE)
						.setScope(getScopeId(domain, user));
				creditor.setDomain(domain);
				creditor.setId(reg.getId());
				if(!AonStringUtils.isBlank(iic.getRegistryAccount())) {
					Account account = getAccount(domain, user, iic.getRegistryAccount(), reg.getName());
					creditor.setAccount(account.getId());
				}
				AON.saveCreditor(domain.getName(), domain.getId(), user.getLogin(), creditor);
			}
			if(creditor.getAccount() == null && !AonStringUtils.isBlank(iic.getRegistryAccount())) {
				Account account = getAccount(domain, user, iic.getRegistryAccount(), reg.getName());
				creditor.setAccount(account.getId());
				AON.saveCreditor(domain.getName(), domain.getId(), user.getLogin(), creditor);
			}
			
			Integer rid = creditor.getId();
			RegistryAddress ra = AON.get(domain, user, (RegistryAddressFilter) 
					f -> f.getRegistryProperty().eq(rid));
			if(ra == null || ra.getId() == null) {
				address.setRegistry(creditor.getId());
				AON.save(domain, user.getLogin(), address);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.CREDITOR)
					.setId(creditor.getId())
					.setName(creditor.getName())
					.setAccountId(creditor.getAccount());
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
		if(AonStringUtils.isBlank(account)) return InvoiceType.EXPENSES;
		if("7".equals(account.substring(0, 1)) || "5600".equals(account.substring(0, 4))) {
			return InvoiceType.SALES;
		} else if("60".equals(account.substring(0, 2)) || "5660".equals(account.substring(0, 4))) {
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
			CloseableAONContext ctx = null;
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

	private static Boolean isSameReference(InvoiceImportClass ant, InvoiceImportClass iic) {
		if(!isSales(ant, iic) && !isSameRegistry(ant, iic)) return false;
		
		String reference = ant.getRef();
		String serie = ant.getSerie();
		Integer number = ant.getNumber();

		Boolean snBool = false;
		if(serie != null && number != null) {
			snBool = serie.equals(iic.getSerie()) && number.equals(iic.getNumber());
		} else if( serie == null && number != null) {
			snBool = number.equals(iic.getNumber());
		}
		return snBool || (AonStringUtils.isNotBlank(reference) && reference.equals(iic.getRef()));
	}

	public static boolean isSales(InvoiceImportClass ant, InvoiceImportClass act) {
		if(ant.getInvoiceType() == null) ant.setInvoiceType(getInvoiceType(ant.getAccount()));
		if(act.getInvoiceType() == null) act.setInvoiceType(getInvoiceType(ant.getAccount()));
		return (ant.getInvoiceType() != null && InvoiceType.SALES.equals(ant.getInvoiceType()))
			|| (act.getInvoiceType() != null && InvoiceType.SALES.equals(act.getInvoiceType()));
	}
	
	public static boolean isSameRegistry(InvoiceImportClass ant, InvoiceImportClass act) {
		return AonStringUtils.isNotBlank(ant.getNif()) && AonStringUtils.isNotBlank(act.getNif())
			&& ant.getNif().equalsIgnoreCase(act.getNif());
	}
}
