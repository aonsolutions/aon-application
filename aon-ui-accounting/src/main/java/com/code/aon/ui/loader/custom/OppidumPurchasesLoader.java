package com.code.aon.ui.loader.custom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.controller.AonLoaderController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class OppidumPurchasesLoader implements Serializable, ICustomLoaderFactory {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(OppidumPurchasesLoader.class.getName());
	
	private boolean skipLoad;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat excelFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private Workbook workbook;
	private int rowOffset;
	private final Pattern invoiceNumberPattern = Pattern.compile("(\\d{1})\\.(\\d+)E\\.{0,1}(\\d{1,2})");
	private Matcher matcher = null;
	private ArrayList<String> headers;
	private Map<String, String> supplierAccount;
	private Map<String, String> supplierNames;
	private Map<String, String> supplierPaymethods;
	
	private final String SUPPLIER_DOCUMENT = "CIFEmisora";
	private final String CONSUMER_NAME = "Nombre";
	private final String SUPPLIER_CODE = "CodigoEmisora";
	private final String INVOICE_DOCUMENT = "NumeroFactura";
	private final String INVOICE_DATE = "FechaFactura";
	private final String INVOICE_VAT_PERCENT = "PorcentIVA";
	private final String INVOICE_VAT_BASE = "BaseImpIVA";
	private final String INVOICE_VAT_AMOUNT = "ImporteIVA";
	private final String INVOICE_TOTAL = "ImporteTotalFact";
	
	private final String INVOICE_BASE_ENERG_ACT = "ImporteTotalEnergAct";
	private final String INVOICE_BASE_ENERG_REACT = "ImporteTotalEnergReact";
	private final String INVOICE_BASE_TERM_POT = "ImporteTotalTermPot";
	private final String INVOICE_BASE_EXCESOS = "ImporteTotalExcesos";
	private final String INVOICE_BASE_ALQUILERES = "ImporteAlquileres";
	
	private final String[] SUPPORTED_COLUMNS = {
			SUPPLIER_DOCUMENT,
			CONSUMER_NAME,
			SUPPLIER_CODE,
			INVOICE_DOCUMENT,
			INVOICE_DATE,
			INVOICE_VAT_PERCENT,
			INVOICE_VAT_BASE,
			INVOICE_VAT_AMOUNT,
			INVOICE_TOTAL,
			
			INVOICE_BASE_ENERG_ACT,
			INVOICE_BASE_ENERG_REACT,
			INVOICE_BASE_TERM_POT,
			INVOICE_BASE_EXCESOS,
			INVOICE_BASE_ALQUILERES
	};
	
	@Override
	public void load(InputStream file){
		LogPanelController logPanel = LogPanelController.getInstance();
		logPanel.info("Inicio de la carga de datos.");
		logPanel.info("Fichero de COMPRAS detectado.");
		try {
	    	Sheet sheet = workbook.getSheetAt(0);
	        
	        Iterator<Row> rowIterator = sheet.iterator();
	        Row row = null;
	        
	        for(int i=0; i<rowOffset || i==0; i++){
	        	row = rowIterator.next();
	        }
	        
	        File defaultImportFile = File.createTempFile("oppidum-purchase-data", ".tmp");
	        PrintWriter writer = new PrintWriter(defaultImportFile);
	        
	        writer.print("1;FRACTB|");
	        writer.print("id|");
	        writer.print("referencia|");
	        writer.print("cuenta|");
	        writer.print("documento|");
	        writer.print("tipoDocumento|");
	        writer.print("paisDocumento|");
	        writer.print("razonSocial|");
	        writer.print("fechaFactura|");
	        writer.print("tipo|");
	        
	        // 1
	        writer.print("baseImponible1|");
	        writer.print("iva1|");
	        writer.print("cuotaIVA1|");
	        writer.print("cuentaExplotacion|");
	        // 2
	        writer.print("baseImponible2|");
	        writer.print("iva2|");
	        writer.print("cuotaIVA2|");
	        writer.print("cuentaExplotacion2|");
	        // 3
	        writer.print("baseImponible3|");
	        writer.print("iva3|");
	        writer.print("cuotaIVA3|");
	        writer.print("cuentaExplotacion3|");
	        
	        writer.print("totalFactura|");
	        writer.print("formaPago");
	        writer.println();
	        
	        String defaultPaymethod = getDefaultPaymethod();
	        supplierAccount = new HashMap<>();
	        supplierNames = new HashMap<>();
	        supplierPaymethods = new HashMap<>();
	    	skipLoad = false;
	        
        	int lineCount=0;
        	while(rowIterator.hasNext()){
        		row = rowIterator.next();
        		
        		String supplierDocument = getStringCellValue(row.getCell(headers.indexOf(SUPPLIER_DOCUMENT)));
        		if(supplierDocument!=null && StringUtils.length(supplierDocument)>9){
        			supplierDocument = supplierDocument.substring(0, 9);
        		}
        		String consumerName = getStringCellValue(row.getCell(headers.indexOf(CONSUMER_NAME)));
        		String supplierCode = getStringCellValue(row.getCell(headers.indexOf(SUPPLIER_CODE)));
        		
        		String supplierName = supplierNames.get(supplierDocument);
        		if(supplierName==null || StringUtils.isBlank(supplierName)){
        			loadSupplierName(supplierDocument, consumerName);
        			supplierName = supplierNames.get(supplierDocument);
        		}
        		
        		if(supplierNames.containsKey(supplierDocument)){
        			String account = supplierAccount.get(supplierDocument);
        			if(account==null || StringUtils.isBlank(account)){
        				loadSupplierAccount(supplierDocument);
        				account = String.valueOf( 400000000 + Double.valueOf(supplierCode).intValue() );
        				supplierAccount.put(supplierDocument, account);
        				logPanel.warn("El proveedor " + supplierName + " ("+supplierDocument+")" + " no tiene cuenta asignada. Se le asigna la cuenta " + account);
        			}
        			
        			if(account!=null && StringUtils.isNotBlank(account)){
        				
        				String invoiceNumber = getStringCellValue(row.getCell(headers.indexOf(INVOICE_DOCUMENT)));
        				Date invoiceDate = getDateCellValue(row.getCell(headers.indexOf(INVOICE_DATE)));
        				double vatPercent = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_VAT_PERCENT)));
        				double vatBase = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_VAT_BASE)));
        				double vatAmount = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_VAT_AMOUNT)));
        				double invoiceTotal = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_TOTAL)));
        				
        				double baseTerminoPotencia = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_TERM_POT)));
        				double baseExcesos = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_EXCESOS)));
        				double baseAlquileres = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_ALQUILERES)));
        				
        				vatBase -= (baseTerminoPotencia + baseExcesos 
        						+ baseAlquileres);
        				vatAmount -= (CommonUtil.round((baseTerminoPotencia + baseExcesos) * vatPercent / 100) 
        						+ CommonUtil.round(baseAlquileres * vatPercent / 100));
        				
        				
        				writer.print("FRACTB|");
        				writer.print(lineCount + "|");
        				writer.print(getFormatInvoiceNumber(invoiceNumber) + "|");
        				writer.print(account + "|");
        				writer.print(supplierDocument + "|");
        				writer.print("1|");
        				writer.print("ES|");
        				writer.print(supplierName + "|");
        				writer.print(dateFormat.format(invoiceDate) + "|");
        				writer.print("0|");
        				
        				// 1
        				writer.print(CommonUtil.round(vatBase) + "|");
        				writer.print(CommonUtil.round(vatPercent) + "|");
        				writer.print(CommonUtil.round(vatAmount) + "|");
        				writer.print("600000001|");
        				// 2
        				writer.print(CommonUtil.round(baseTerminoPotencia + baseExcesos) + "|");
        				writer.print(CommonUtil.round(vatPercent) + "|");
        				writer.print(CommonUtil.round((baseTerminoPotencia + baseExcesos) * vatPercent / 100) + "|");
        				writer.print("600000002|");
        				// 3
        				writer.print(CommonUtil.round(baseAlquileres) + "|");
        				writer.print(CommonUtil.round(vatPercent) + "|");
        				writer.print(CommonUtil.round(baseAlquileres * vatPercent / 100) + "|");
        				writer.print("600000003|");
        				
        				writer.print(invoiceTotal + "|");
        				String supplierPaymethod = supplierPaymethods.get(supplierDocument);
        				writer.print(supplierPaymethod!=null?supplierPaymethod:defaultPaymethod);
        				writer.println();
        				
        				lineCount++;
        			}
        		}
        		
        	}
        	
        	logPanel.info("Total facturas a procesar:" + lineCount);
        	
        	writer.flush();
        	
        	if(skipLoad){
        		logPanel.info("Se han detectado problemas en los valores del fichero.");
        		logPanel.info("Carga de datos abortada.");
        	} else {
        		AonLoaderController controller = (AonLoaderController) AonUtil.getRegisteredBean("aonLoader");
        		callAonLoader(IOUtils.toByteArray(new FileInputStream(defaultImportFile)), controller.getParams());
        		
        		workbook.close();
        		file.close();
        		
        		writer.close();
        		defaultImportFile.delete();
        		
        		logPanel.info("El fichero se ha procesado completamente.");
        		logPanel.info("Carga de datos finalizada.");
        	}
			
		} catch (IOException e) {
			String msg = "Error durante la carga de datos. ";
			logPanel.error(msg  + e.getMessage());
	    } catch (Exception e) {
	    	String msg = "Error durante la carga de datos. ";
	    	logPanel.error(msg  + e.getMessage());
	    } finally {
	    	
	    }
	}
	
	private String getDefaultPaymethod() throws ManagerBeanException{
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		RegistryPayMethod rpm = ((Company)controller.getTo()).getPayMethod();
		if(rpm!=null && rpm.getPayment()!=null && StringUtils.isNotBlank(rpm.getPayment().getName())){
			return rpm.getPayment().getName();
		}
		return "";
		
	}
	
	private String getStringCellValue(Cell cell) {
		if(cell!=null && cell.getCellType()==Cell.CELL_TYPE_STRING){
			return cell.getStringCellValue();
		} else if(cell!=null && cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			return String.valueOf(cell.getNumericCellValue());
		}
		return "";
	}
	
	private Double getNumericCellValue(Cell cell) {
		if(cell!=null && cell.getCellType()==Cell.CELL_TYPE_STRING){
			if(NumberUtils.isNumber(cell.getStringCellValue())){
				return Double.parseDouble(cell.getStringCellValue());
			}
		} else if(cell!=null && cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			return cell.getNumericCellValue();
		}
		return 0.0;
	}
	
	private Date getDateCellValue(Cell cell) {
		if(cell!=null && cell.getCellType()==Cell.CELL_TYPE_STRING){
			try {
				return excelFormat.parse(cell.getStringCellValue());
			} catch (ParseException e) {
				LOGGER.error(e.getMessage());
				throw new AbortProcessingException(e.getMessage());
			}
		} else if(cell!=null && cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			return cell.getDateCellValue();
		}
		return null;
	}

	@Override
	public boolean accept(byte[] data){
		try {
			workbook = WorkbookFactory.create(new ByteArrayInputStream(data));
			Sheet sheet = workbook.getSheetAt(0);
			
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = null;
			headers = new ArrayList<>();
			
			rowOffset=0;
			for(int i=0; i<5 && rowIterator.hasNext() && !headers.containsAll(Arrays.asList(SUPPORTED_COLUMNS)); i++){
				row = rowIterator.next();
				rowOffset++;
				headers.clear();
				for(int col=0;col<row.getLastCellNum();col++){
					Cell cell = row.getCell(col);
					String name = getStringCellValue(cell);
					headers.add(StringUtils.isBlank(name)?"empty":name);
				}
			}
			
			return headers.containsAll(Arrays.asList(SUPPORTED_COLUMNS));
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (InvalidFormatException e) {
			LOGGER.error(e.getMessage());
		}
        
		return false;
	}
	
	private void loadSupplierName(String supplierDocument, String name) throws ManagerBeanException {
		supplierDocument = StringUtils.trim(supplierDocument);
		name = StringUtils.trim(name);
		if(supplierNames==null){
			supplierNames = new HashMap<>();
		}
		if(!supplierNames.containsKey(supplierDocument)){
			LogPanelController logPanel = LogPanelController.getInstance();
			Supplier supplier = obtainSupplier(supplierDocument);
			if(supplier==null || supplier.getId()==null){
				logPanel.warn("Proveedor no existente, se debe crear para poder continuar (" + supplierDocument +")");
				skipLoad = true;
			} else {
				supplierNames.put(supplierDocument, supplier.getRegistry().getFullName());
				if(supplier.getAccount()!=null && StringUtils.isNotBlank(supplier.getAccount().getCode())){
					supplierAccount.put(supplierDocument, supplier.getAccount().getCode());
				}
				if(supplier.getRegistry().getPayMethod()!=null 
						&& supplier.getRegistry().getPayMethod().getPayment()!=null 
						&& StringUtils.isNotBlank(supplier.getRegistry().getPayMethod().getPayment().getName())){
					supplierPaymethods.put(supplierDocument, supplier.getRegistry().getPayMethod().getPayment().getName());
				}
			}
		}
	}
	
	private void loadSupplierAccount(String supplierDocument) throws ManagerBeanException {
		supplierDocument = StringUtils.trim(supplierDocument);
		if(supplierAccount==null){
			supplierAccount = new HashMap<>();
		}
		if(!supplierAccount.containsKey(supplierDocument)){
			Supplier supplier = obtainSupplier(supplierDocument);
			if(supplier!=null && supplier.getId()!=null){
				if(supplier.getAccount()!=null && StringUtils.isNotBlank(supplier.getAccount().getCode())){
					supplierAccount.put(supplierDocument, supplier.getAccount().getCode());
				}
			}
		}
	}
	
	private Supplier obtainSupplier(String supplierDocument) throws ManagerBeanException {
		if(StringUtils.isNotBlank(supplierDocument)){
			LogPanelController logPanel = LogPanelController.getInstance();
			IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_DOCUMENT), supplierDocument);
			List<ITransferObject> list = bean.getList(criteria);
			if(list!=null && list.size()>0){
				if(list.size()>1){
					logPanel.error("Existen varios registros de proveedor con el documento " + supplierDocument);
				}
				return (Supplier) list.get(0);
			}
		}
		return null;
	}
	
	private String getFormatInvoiceNumber(String value) {
		matcher = invoiceNumberPattern.matcher(value);
		String match = null;
		while (matcher.find()) {
			match = matcher.group(2);
			while(match.length()<Integer.parseInt(matcher.group(3))){
				match += 0;
			}
			match = matcher.group(1) + match;
		}
		if(match.length()>4){
			match = match.substring(4, match.length());
		}
		return match!=null?match:value;
	}
	
	private void callAonLoader(byte[] data, LoaderParams params){
		LogPanelController logger = LogPanelController.getInstance();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		Loader loader = new Loader(params);
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			ByteArrayInputStream input = new ByteArrayInputStream(data);
			loader.loadMetadata(input);
			input = new ByteArrayInputStream(data);
			loader.validate(input);
			input = new ByteArrayInputStream(data);
			loader.load(input,session);
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			String msg = "Error durante la carga de datos. ";
			logger.error(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
				logger.info("Se deshacen las inserciones realizadas.");
			} catch (DAOException daoe) {
				LOGGER.error("Unable to rollback transaction!", e);
			}
			LOGGER.error(msg, e);
			logger.finish();			
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			loader.setFactoryManager(null);
			AonLoaderController controller = (AonLoaderController) AonUtil.getRegisteredBean("aonLoader");
			controller.setLoadPressed(false);
		}
	}
	
	
	
	

	///////////////////////////////
	///////////////////////////////
	
	public static void main(String[] args) {
    	try {
    		OppidumSalesLoader loader = new OppidumSalesLoader();
			FileInputStream file = new FileInputStream(new File("/home/eagirrezabal/Descargas/compras_reduced.xlsx"));
			loader.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	    
	}
	
}
