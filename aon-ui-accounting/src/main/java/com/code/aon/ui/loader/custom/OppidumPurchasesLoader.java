package com.code.aon.ui.loader.custom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.controller.AonLoaderController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class OppidumPurchasesLoader implements Serializable, ICustomLoaderFactory {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(OppidumPurchasesLoader.class.getName());
	
	private Workbook workbook;
	private int rowOffset;
	private final Pattern invoiceNumberPattern = Pattern.compile("(\\d{4})(\\d+)");
	private Matcher matcher = null;
	private ArrayList<String> headers;
	private Map<String, String> customerAccount = new HashMap<>();
	
//	private final String SUPPLIER_DOCUMENT = "Nif_RazonSocial";
	private final String SUPPLIER_NAME = "Nombre";
	private final String INVOICE_DOCUMENT = "NumeroFactura";
	private final String INVOICE_DATE = "FechaFactura";
	private final String INVOICE_VAT_PERCENT = "PorcentIVA";
	private final String INVOICE_VAT_BASE = "BaseImpIVA";
	private final String INVOICE_VAT_AMOUNT = "ImporteIVA";
	private final String INVOICE_TOTAL = "ImporteTotal";
	
	private final String[] SUPPORTED_COLUMNS = {
//			CUSTOMER_DOCUMENT,
			SUPPLIER_NAME,
			INVOICE_DOCUMENT,
			INVOICE_DATE,
			INVOICE_VAT_PERCENT,
			INVOICE_VAT_BASE,
			INVOICE_VAT_AMOUNT,
			INVOICE_TOTAL
	};
	
	@Override
	public void load(InputStream file){
		LogPanelController logPanel = LogPanelController.getInstance();
		logPanel.info("Inicio de la carga de datos.");
		logPanel.info("Fichero de compras detectado.");
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
	        writer.print("serie|");
	        writer.print("numero|");
	        writer.print("cuenta|");
	        writer.print("documento|");
	        writer.print("tipoDocumento|");
	        writer.print("paisDocumento|");
	        writer.print("razonSocial|");
	        writer.print("fechaFactura|");
	        writer.print("tipo|");
	        writer.print("baseImponible1|");
	        writer.print("iva1|");
	        writer.print("cuotaIVA1|");
	        writer.print("cuentaExplotacion|");
	        writer.print("totalFactura");
	        writer.println();
	        
	        String maxAccountCode = obtainMaxAccountCode();
	        int emptyAccountCount = 0;
	        
        	int lineCount=1;
        	while(rowIterator.hasNext()){
        		
//        		String supplierDocument = row.getCell(headers.indexOf(SUPPLIER_DOCUMENT)).getStringCellValue();
        		String supplierDocument = "";
        		String supplierName = row.getCell(headers.indexOf(SUPPLIER_NAME)).getStringCellValue();
        		String account = obtainSupplierAccount(supplierDocument, supplierName);
        		
        		if(account==null || StringUtils.isBlank(account)){
        			emptyAccountCount++;
        			account = String.valueOf(Integer.valueOf(maxAccountCode)+emptyAccountCount);
        			customerAccount.put(supplierDocument, account);
        			logPanel.warn("El cliente " + supplierDocument + " no tiene cuenta asignada. Se le asigna la siguiente libre");
        		}
        		
        		if(account!=null && StringUtils.isNotBlank(account)){
        			
        			String invoiceNumber = row.getCell(headers.indexOf(INVOICE_DOCUMENT)).getStringCellValue();
        			String serie = obtainInvoiceSeries(invoiceNumber);
        			String num = obtainInvoiceNumber(invoiceNumber);
        			String invoiceDate = row.getCell(headers.indexOf(INVOICE_DATE)).getStringCellValue();
        			double vatPercent = row.getCell(headers.indexOf(INVOICE_VAT_PERCENT)).getNumericCellValue();
        			double vatBase = row.getCell(headers.indexOf(INVOICE_VAT_BASE)).getNumericCellValue();
        			double vatAmount = row.getCell(headers.indexOf(INVOICE_VAT_AMOUNT)).getNumericCellValue();
        			double invoiceTotal = row.getCell(headers.indexOf(INVOICE_TOTAL)).getNumericCellValue();
        			
        			writer.print("FRACTB|");
        			writer.print(lineCount+"|");
        			writer.print(serie+"|");
        			writer.print(num+"|");
        			writer.print(account+"|");
        			writer.print(supplierDocument+"|");
        			writer.print("1|");
        			writer.print("ES|");
        			writer.print(supplierName+"|");
        			writer.print(invoiceDate+"|");
        			writer.print("0|");
        			writer.print(CommonUtil.round(vatBase)+"|");
					writer.print(CommonUtil.round(vatPercent)+"|");
					writer.print(CommonUtil.round(vatAmount)+"|");
					writer.print("600000000|");
					writer.print(invoiceTotal);
        			writer.println();
					
        			logPanel.info("Factura procesada: " + invoiceNumber);		
        			lineCount++;
        		}
        		
        		row = rowIterator.next();
        	}
        	
        	writer.flush();
        	
        	AonLoaderController controller = (AonLoaderController) AonUtil.getRegisteredBean("aonLoader");
			callAonLoader(IOUtils.toByteArray(new FileInputStream(defaultImportFile)), controller.getParams());
	        
	        workbook.close();
	        file.close();
	        
	        writer.close();
	        defaultImportFile.delete();
	        
	        logPanel.info("El fichero se ha procesado completamente.");
	        logPanel.info("Carga de datos finalizada.");
			
		} catch (IOException e) {
			String msg = "Error durante la carga de datos. ";
			logPanel.error(msg  + e.getMessage());
	    } catch (Exception e) {
	    	String msg = "Error durante la carga de datos. ";
	    	logPanel.error(msg  + e.getMessage());
	    } finally {
	    	
	    }
	}
	
	@Override
	public boolean accept(byte[] data){
		try {
			workbook = WorkbookFactory.create(new ByteArrayInputStream(data));
			Sheet sheet = workbook.getSheetAt(0);
			
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = null;
			headers = new ArrayList<>();
			
			rowOffset=1;
			for(int i=0; i<5 && !headers.containsAll(Arrays.asList(SUPPORTED_COLUMNS)); i++){
				cellIterator = row.cellIterator();
				headers.clear();
				while(cellIterator.hasNext()){
					Cell cell = cellIterator.next();
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						headers.add(cell.getStringCellValue());
					}
				}
				row = rowIterator.next();
				rowOffset++;
			}
			
			return headers.containsAll(Arrays.asList(SUPPORTED_COLUMNS));
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (InvalidFormatException e) {
			LOGGER.error(e.getMessage());
		}
        
		return false;
	}
	
	private String obtainSupplierAccount(String supplierDocument, String supplierName) throws ManagerBeanException {
		supplierName = StringUtils.trim(supplierName);
		String account = null;
		if(customerAccount!=null && customerAccount.containsKey(supplierName)){
			account = customerAccount.get(supplierName);
		} else {
			if(StringUtils.isNotBlank(supplierName)){
				LogPanelController logPanel = LogPanelController.getInstance();
				IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_NAME), supplierName);
				List<ITransferObject> list = bean.getList(criteria);
				if(list==null || list.size()==0){
					logPanel.warn("Se procede a crear un nuevo cliente " + supplierName + " (" + supplierDocument +")" );
				} else if(list!=null && list.size()==1){
					Supplier supplier = (Supplier) list.get(0);
					if(supplier.getAccount()!=null && StringUtils.isNotBlank(supplier.getAccount().getCode())){
						account = supplier.getAccount().getCode();
						customerAccount.put(supplierName, account);
					}
				} else {
					logPanel.error("Existen varios registros de cliente con el documento " + supplierDocument);
				}
			}
		}
		return account;
	}

	private String obtainMaxAccountCode() throws ManagerBeanException {
		String code = null;
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ACTIVE), Boolean.TRUE);
		c.addExpression(ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), "430%"));
		ProjectionList pl = new ProjectionList();
		pl.add(Projection.max(bean.getFieldName(IEntityAlias.ACCOUNT_CODE)));
		List<?> list = bean.getList(pl, c);
		if (list != null && list.size() > 0 && list.get(0) != null) {
			code = (String) list.get(0);	
		}
		return code;
	}
	
	private String obtainInvoiceSeries(String value){
		matcher = invoiceNumberPattern.matcher(value);
		String match;
		while (matcher.find()) {
			match = matcher.group(1);
			if (match != null) {
				return match;
			}
		}
		return "";
	}

	private String obtainInvoiceNumber(String value){
		matcher = invoiceNumberPattern.matcher(value);
		String match;
		while (matcher.find()) {
			match = matcher.group(2);
			if (match != null) {
				return match;
			}
		}
		return "";
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
