package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ServalInvoiceImport extends ImportUtils{

	public static ServalInvoiceImport getInstance() {
		return new ServalInvoiceImport();
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
	
	private boolean isTipoOperacion(String value) {
		return compare(value, IConstants.TIPO_DE_OPERACION, IConstants.TIPO_OPERACION);
	}
	
	private boolean isTipoFactura(String value) {
		return compare(value, IConstants.TIPO_FACTURA);
	}
	
	private boolean isFecha(String value) {
		return compare(value, IConstants.FECHA);
	}
	
	private boolean isFechaIva(String value) {
		return compare(value, IConstants.FECHA_IVA);
	}
	
	private boolean isSerie(String value) {
		return compare(value, IConstants.SERIE, IConstants.SERIES);
	}
	
	private boolean isNumero(String value) {
		return compare(value, IConstants.NUMERO);
	}
	
	private boolean isReference(String value) {
		return compare(value, IConstants.REFERENCIA, IConstants.NUMERO_DE_FACTURA, 
			IConstants.NUMERO_FACTURA, IConstants.CODIGO_REFERENCIA,
			IConstants.CODIGO_DE_REFERENCIA);
	}

	private boolean isNif(String value) {
		return compare(value, IConstants.NIF, IConstants.DOCUMENTO);
	}
	
	private boolean isNombre(String value) {
		return compare(value, IConstants.NOMBRE, IConstants.RAZON_SOCIAL,
			IConstants.RAZON_SOCIAL2);
	}
	
	private boolean isCuentaContraparte(String value) {
		return compare(value, IConstants.CUENTA_CONTRAPARTE);
	}
	
	private boolean isTercero(String value) {
		return compare(IConstants.TERCERO, value);
	}
	
	private boolean isObservaciones(String value) {
		return compare(value, IConstants.OBSERVACIONES, IConstants.CONCEPTO);
	}
	
	private boolean isDireccion(String value) {
		return compare(IConstants.DIRECCION, value);
	}
	
	private boolean isCiudad(String value) {
		return compare(value, IConstants.CIUDAD);
	}
	
	private boolean isProvincia(String value) {
		return compare(value, IConstants.PROVINCIA);
	}
	
	private boolean isCodigoPostal(String value) {
		return compare(value, IConstants.CODIGO_POSTAL);
	}
	
	private boolean isPais(String value) {
		return compare(value, IConstants.PAIS);
	}
	
	private boolean isCuentaExplotacion(String value) {
		return compare(value, IConstants.CUENTA_BASE, IConstants.CUENTA_CONTABLE, 
			IConstants.CUENTA_EXPLOTACION, IConstants.CUENTA_DE_EXPLOTACION);
	}
	
	private boolean isPorcentajeImpuesto(String value) {
		value = value.replace(" ", "");
		return compare(value, "%" + IConstants.IMPUESTO, "%" + IConstants.IVA);
	}
	
	private boolean isCuotaImpuesto(String value) {
		return compare(value, IConstants.CUOTA_IMPUESTO, IConstants.CUOTA_IVA);
	}
	
	private boolean isPorcentajeRe(String value) {
		value = value.replace(" ", "");
		return compare(value, "%" + IConstants.RE);
	}
	
	private boolean isCuotaRe(String value) {
		return compare(value, IConstants.CUOTA_RE);
	}
	
	private boolean isPorcentajeRetencion(String value) {
		value = value.replace(" ", "");
		return compare(value, "%" + IConstants.RETENCION, "%" + IConstants.IRPF);
	}
	
	private boolean isCuotaRetencion(String value) {
		return compare(value, IConstants.CUOTA_RETENCION, IConstants.CUOTA_IRPF);
	}
	
	private boolean isDescripcionCuenta(String value) {
		return compare(value, IConstants.DESCRIPCION_CUENTA);
	}
	
	private boolean isCodigoProducto(String value) {
		return compare(value, IConstants.CODIGO_PRODUCTO, IConstants.CODIGO_PRODUCTO2);
	}
	
	private boolean isNombreProducto(String value) {
		return compare(value, IConstants.NOMBRE_PRODUCTO);
	}
	
	private boolean isCodigoBarras(String value) {
		return compare(value, IConstants.CODIGO_BARRAS, IConstants.CODIGO_BARRAS2);
	}
	
	private boolean isSerialNumber(String value) {
		return compare(value, IConstants.NUMERO_DE_SERIE, IConstants.NUMERO_DE_SERIE2
				, IConstants.NUMERO_SERIE, IConstants.NUMERO_SERIE2, IConstants.SERIAL_NUMBER);
	}
	
	private boolean isSuplido(String value) {
		return compare(value, IConstants.SUPLIDO);
	}
	
	private boolean isCantidad(String value) {
		return compare(value, IConstants.CANTIDAD);
	}
	
	private boolean isConceptoDetalle(String value) {
		return compare(value, IConstants.CONCEPTO_DETALLE);
	}
	
	private boolean isBase(String value) {
		return compare(value, IConstants.BASE, IConstants.BASE_IMPONIBLE);
	}
	
	private boolean isTotal(String value) {
		return compare(value, IConstants.TOTAL, IConstants.TOTAL_FACTURA);
	}
	
	private boolean isClaveRetencion(String value) {
		return compare(value, IConstants.CLAVE_RETENCION);
	}
	
	private boolean isSubclaveRetencion(String value) {
		return compare(value, IConstants.SUBCLAVE_RETENCION);
	}
	
	private boolean isCuentaTesoreria(String value) {
		return compare(value, IConstants.CUENTA_TESORERIA, IConstants.PAGO_POR_CAJA);
	}
	
	private boolean isActivity(String value) {
		return compare(value, IConstants.ACTIVIDAD );
	}
	
 	private void check(String title, Cell cell) {
		Object o = Utils.getObjectValue(cell);
		if(o == null) return;
	
		if(isActivity(title)) {
			if(CellType.NUMERIC == cell.getCellTypeEnum()) { 
				inv.setActivity(Integer.toString(Utils.parseDouble(o.toString()).intValue()));
			}  else inv.setActivity(o.toString());
		}
		
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
		
		if(isFechaIva(title)) {
			Date taxDate = new Date();
			try{
				taxDate = cell.getDateCellValue();
			} catch (Exception e) {
				taxDate = AonDateUtils.simpleParse(o.toString());
			}
			inv.setTaxDate(taxDate);
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
		
		if(isCodigoProducto(title)) {
			inv.setProductCode(o.toString());
			return;
		}

		if(isNombreProducto(title)) {
			inv.setProductName(o.toString());
			return;
		}
		
		if(isCodigoBarras(title)) {
			inv.setProductBarcode(o.toString());
			return;
		}
		
		if(isSerialNumber(title)) {
			inv.setProductSerialNumber(o.toString());
			return;
		}
		
		if(isCantidad(title)) {
			inv.setQuantity(Utils.parseDouble(o));
			return;
		}
		
		if(isConceptoDetalle(title)) {
			inv.setConceptDetail(o.toString());
			return;
		}
		
		if(isSuplido(title)) {
			//TODO SUPLIDO
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



	private static void validate(InvoiceImportClass iic, boolean record) throws Exception {
		if(record && (iic.getAccount() == null || iic.getAccount().isBlank())) {
			throw new Exception("La cuenta contable es un dato obligatorio.");
		}
		
		if(iic.getRef() == null && iic.getNumber() == null) {
			throw new Exception("La Referencia o Serie/Número son incorrectas.");
		}
		
		if(iic.getSerie() != null && iic.getSerie().length() > 5) {
			throw new Exception("La serie no puede tener más de 5 carácteres");
		}
	}
	
	private static Invoice buildInvoice(AonConfiguration aonCtx, Domain domain, User user, InvoiceImportClass iic) {
		Invoice invoice = new Invoice();
		invoice.setScope(getScope(domain, user));
		invoice.setService(InvoiceOpType.PIS.equals(iic.getType())|| InvoiceOpType.AIS.equals(iic.getType()));
		invoice.setTransaction(getTransaction(iic));
		invoice.setInvestment(iic.isInvestment() != null && iic.isInvestment());
		invoice.setDomain(domain.getId());
		invoice.setIssueDate(iic.getDate());
		invoice.setTaxDate(iic.getTaxDate() != null ? iic.getTaxDate() : iic.getDate());
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
		
		EnterpriseActivity ea = aonCtx.getMainActivity();		
		if(!AonStringUtils.isBlank(iic.getActivity()) && AonStringUtils.isNumeric(iic.getActivity())) {
			List<EnterpriseActivity> list = AON.getEnterpriseActivities(domain.getName(), domain.getId(), user.getLogin()).toList();
			for(EnterpriseActivity act : list) {
				if(iic.getActivity().equalsIgnoreCase(act.getCnaeCode()))
					ea = act;
			}
		}
		invoice.setActivity(ea);
		return invoice;
	}

	private static RegistryAddress buildAddress(AonConfiguration aonCtx, Domain domain, InvoiceImportClass iic) {
		RegistryAddress ra = new RegistryAddress()
				.setDomain(domain.getId())
				.setAddress(iic.getAddress())
				.setCity(iic.getCity())
				.setZip(iic.getZip() != null && iic.getZip().length() < 5 
					? "0" + iic.getZip() : iic.getZip());

		if(iic.getProvince() != null || iic.getZip() != null ) {
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
	

	
	public static Error insertInvoice(Domain domain, User user, Integer i, InvoiceImportClass iic, boolean record) {
		Error error = new Error().setError(true);

		AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), user.getLogin());
	
		PayMethod pm = aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty() ? aonCtx.getPayMethods().get(0) : new PayMethod(); 
		Account outputAccount = aonCtx.accounting().getDefaultChargedVatAccount();
		Account inputAccount = aonCtx.accounting().getDefaultPaidVatAccount();
		Account adjAccount = aonCtx.accounting().getVatNegativeAdjustAccount();
		
		try {
			validate(iic, record);
			Invoice invoice = buildInvoice(aonCtx, domain, user, iic);
			RegistryAddress address = buildAddress(aonCtx, domain, iic);
			buildRegistry(domain, user, invoice, iic, address);			
			
			Double total = 0.0;
			Double base = 0.0;
			Double retBase = 0.0;
			Double retQuota = 0.0;
			Double retPercentage = 0.0;
	
			Account retentionAccount = (invoice.isSales() )
					? aonCtx.accounting().getDefaultPaidRetAccount()
					: aonCtx.accounting().getDefaultChargedRetAccount();
			
			for(InvoiceImportClass aux : iic.getLines()) {
				if(aux.getRetentionQuota() != null) {
					retBase = retBase + aux.getBase();
					retPercentage = aux.getRetentionPercentage();
					retQuota = retQuota + aux.getRetentionQuota();
					invoice.setWithholding(true);
				}
				
				Account expAccount = getAccount(domain, user, aux.getAccount(), aux.getAccountDescription());

				Item item = null;
				if(aux.getProductCode() != null) {
					item = AON.getItem(domain, user.getLogin(), f -> f.getDomainProperty().eq(domain.getId())
							.and(f.getProductCodeProperty().eq(aux.getProductCode())));
					if(item.isEmpty()) {
						item = new Item()
							.setProduct(new Product()
								.setCode(aux.getProductCode())
								.setName(aux.getProductName()));
					}
				}
				
				double quantity = aux.getQuantity() != null ? aux.getQuantity() : 1.0;
				double taxableBase = aux.getBase() != null ? aux.getBase() : 0.0;
				InvoiceDetail detail =  new InvoiceDetail()
						.setDomain(invoice.getDomain())
						.setDescription(aux.getConceptDetail() != null 
								? aux.getConceptDetail()
								: item.getDescription()) 
						.setItem(item)
						.setAccount(expAccount.getId())
						.setAccountCode(expAccount.getCode())
						.setAccountDescription(expAccount.getDescription())
	
						.setQuantity(quantity)
						.setPrice(AonMathUtils.round((taxableBase / quantity) , 4))
						.setDiscountExpression("0.0")
						.setTaxableBase(taxableBase)
						.setPrepayment("5600".equals(aux.getAccount().substring(0, 4)) || "5660".equals(aux.getAccount().substring(0, 4)))
						.setSource(InvoiceSource.DIRECT_INVOICE);
				
				InvoiceTax vat = new InvoiceTax()
						.setDomain(detail.getDomain())
						.setTaxType(TaxType.VAT)
						.setBase(aux.getBase() != null ? aux.getBase() : 0.0)
						.setPercentage(aux.getPercentage() != null ? aux.getPercentage() : 0.0)
						.setQuota(aux.getQuota() != null ? aux.getQuota() : 0.0)
						.setSurcharge(aux.getRePercentage() != null ? aux.getRePercentage() : 0.0)
						.setSurchargeQuota(aux.getReQuota() != null ? aux.getReQuota() : 0.0)
						.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						.setDeductiblePercent(100.0)
						.setDeductibleQuota(aux.getQuota() != null ? aux.getQuota() : 0.0);
				
				if(invoice.isUndeductible()) {
					vat.setBase(aux.getTotal());
					vat.setPercentage(0.0);
					vat.setQuota(0.0);
					vat.setDeductibleQuota(0.0);
					vat.setSurcharge(0.0);
					vat.setSurchargeQuota(0.0);
				}
				detail.addInvoiceTax(vat);
				
				if(invoice.isWithholding()) {
					InvoiceTax wh = new InvoiceTax()
						.setDomain(invoice.getDomain())
						.setTaxType(TaxType.RETENTION)
						.setWithholding(true)
						.setWithholdingType(getWithholdingType(iic.getRetentionKey(), iic.getAccount()))
						.setBase(retBase)
						.setPercentage(retPercentage)
						.setQuota(retQuota)
						.setAccount(retentionAccount.getId());

					detail.addInvoiceTax(wh);
				}
				
				double retentionQuota = aux.getRetentionQuota() != null ? aux.getRetentionQuota() : 0.0;
				
				total = total + (invoice.isIsp() 
						? aux.getBase() - retentionQuota
						: aux.getTotal());
				base = base + aux.getBase();
				checkCuotas(domain, aux);
				invoice.getDetails().add(detail);
			}

			invoice.setTotal(total);
			invoice.setTaxableBase(base);
			invoice.setVatQuota(total - base);
		
			Account financeAccount = new Account();
			if(iic.getFinanceAccount() != null && !iic.getFinanceAccount().isBlank() ) {
				financeAccount = getAccount(domain, user, iic.getFinanceAccount(), "");
			}
				
			invoice.setFinances(new LinkedList<Finance>());
			if(iic.getFinances().isEmpty()) {
				
				Finance f = new Finance()
					.setAmount(invoice.getTotal())
					.setDueDate(iic.getFinanceDate() != null ? iic.getFinanceDate() : invoice.getIssueDate())
					.setPayMethod(pm.getId())
					.setPayment(!invoice.isSales())
					.setDomain(domain.getId())
					.setRegistry(new Registry().setId(invoice.getRegistry()))
					.setRegistryDocument(invoice.getRegistryDocument())
					.setRegistryDocumentType(invoice.getRegistryDocumentType())
					.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
					.setRegistryName(invoice.getRegistryName())
					.setScope(invoice.getScope())
					.setSecurityLevel(invoice.getSecurityLevel())
					.setConcept(invoice.getDocumentNumber())
					.setFinanceStatus(FinanceStatus.PENDING);
				
				invoice.addFinance(f);
			} else {
				for (Finance fin : iic.getFinances()) {
					if(aonCtx.getPayMethods() != null && !aonCtx.getPayMethods().isEmpty()) {
						PayMethod pmAux = aonCtx.getPayMethods().stream().filter(p -> p.getName().equals(fin.getPayMethodName())).findFirst().orElse(pm);
						fin.setPayMethod(pmAux.getId());
					} else fin.setPayMethod(pm.getId());
					fin.setPayment(!invoice.isSales())
						.setDomain(domain.getId())
						.setRegistry(new Registry().setId(invoice.getRegistry()))
						.setRegistryDocument(invoice.getRegistryDocument())
						.setRegistryDocumentType(invoice.getRegistryDocumentType())
						.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
						.setRegistryName(invoice.getRegistryName())
						.setScope(invoice.getScope())
						.setSecurityLevel(invoice.getSecurityLevel())
						.setConcept(invoice.getDocumentNumber())
						.setFinanceStatus(FinanceStatus.PENDING);
					if(financeAccount != null && financeAccount.getId() != null) {
						invoice.addFinance(fin);
					}
				} 
			}

			AON_SOLUTIONS.acceptInvoice(domain, user, invoice);
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

	private static void buildRegistry(Domain domain, User user, Invoice invoice, InvoiceImportClass iic, RegistryAddress address) {		
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
		if(AonStringUtils.isBlank(reg.getName())) {
			AON.save(domain.getName(), domain.getId(), user.getLogin(), reg.setName(name));
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
					.setScope(getScope(domain, user));
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
			invoice.setRegistryAccount(new Account().setId(customer.getAccount()));
			Integer rid = customer.getId();
			RegistryAddress ra = AON.get(domain, user, (RegistryAddressFilter) 
					f -> f.getRegistryProperty().eq(rid));
			if(ra == null || ra.getId() == null) {
				address.setRegistry(customer.getId());
				AON.save(domain, user.getLogin(), address);
			}
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
						.setScope(getScope(domain, user))
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
			invoice.setRegistryAccount(new Account().setId(supplier.getAccount()));
			Integer rid = supplier.getId();
			RegistryAddress ra = AON.get(domain, user, (RegistryAddressFilter) 
					f -> f.getRegistryProperty().eq(rid));
			if(ra == null || ra.getId() == null) {
				address.setRegistry(supplier.getId());
				AON.save(domain, user.getLogin(), address);
			}
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
						.setScope(getScope(domain, user));
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
			invoice.setRegistryAccount(new Account().setId(creditor.getAccount()));
			Integer rid = creditor.getId();
			RegistryAddress ra = AON.get(domain, user, (RegistryAddressFilter) 
					f -> f.getRegistryProperty().eq(rid));
			if(ra == null || ra.getId() == null) {
				address.setRegistry(creditor.getId());
				AON.save(domain, user.getLogin(), address);
			}
		} 
		
		invoice.setRegistry(reg.getId())
		.setRegistryName(reg.getName())
		.setRegistryDocument(reg.getDocument())
		.setRegistryData(reg)
		.setRegistryDocumentCountry(reg.getDocumentCountry())
		.setRegistryDocumentType(reg.getDocumentType());

	}

	private static Scope getScope(Domain domain, User user) {
		Scope scope = new Scope();
		if(domain.getScope() == null) {
			scope = AON.getUserScopeStream(domain.getName(), domain.getId(), user.getLogin(), user.getId(), 
					f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(new Scope());
			if(scope.isEmpty()) {
				Integer[] scopes = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
				if(scopes != null && scopes.length > 0)
					scope = AON.getScope(domain.getName(), domain.getId(), user.getLogin(), scopes[0]);
				else {
					scope = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),  f ->
						f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Scope());
					if(scope.isEmpty()) {
						scope = AON.insertScope(domain.getName(), domain.getId(), user.getLogin(), new Scope()
							.setDescription("GENERAL")
							.setDomain(domain.getId()));
					}
				}
			}
		} else scope = AON.getScope(domain.getName(), domain.getId(), user.getLogin(), domain.getScope());
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
