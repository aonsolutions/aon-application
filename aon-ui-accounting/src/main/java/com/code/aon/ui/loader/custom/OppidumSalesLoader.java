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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.customer.Customer;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.controller.AonLoaderController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class OppidumSalesLoader implements Serializable, ICustomLoaderFactory {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(OppidumSalesLoader.class.getName());
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat excelFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private Workbook workbook;
	private int rowOffset;
	private final Pattern invoiceNumberPattern = Pattern.compile("(\\d{4})-\\d{1}-(\\d+)[-A-Za-z]");
	private final Pattern ibanPattern = Pattern.compile("ES\\d+");
	private final Pattern cccPattern = Pattern.compile("(\\d{1})\\.(\\d+)E\\.{0,1}\\d{1,2}");
	private Matcher matcher = null;
	private ArrayList<String> headers;
	private Map<String, String> customerAccount;
	
	private final String CUSTOMER_DOCUMENT = "Nif_RazonSocial";
	private final String CUSTOMER_NAME = "Nombre_RazonSocial";
	private final String INVOICE_DOCUMENT = "Factura ML";
	private final String INVOICE_DATE = "Fecha_Facturacion";
	private final String INVOICE_PAYMENT_DATE = "Fecha Vencimiento";
	private final String INVOICE_VAT_PERCENT = "%_iva";
	private final String INVOICE_VAT_BASE = "Base_Iva";
	private final String INVOICE_VAT_AMOUNT = "Importe_Iva";
	private final String INVOICE_IEE_BASE = "Importe_Iee";
	private final String INVOICE_TOTAL = "Total Final";
	private final String INVOICE_BANK_ACCOUNT = "Domiciliacion Bancaria";
	
	private final String INVOICE_BASE_TERMINO_POTENCIA = "Termino_Potencia";
	private final String INVOICE_BASE_EXCESOS_POTENCIA = "Excesos_Potencia";
	private final String INVOICE_BASE_ALQUILER = "Alquiler";
	private final String INVOICE_BASE_GASTOS_GESTION_COBRO = "Gastos_Gestion_Cobro";
	
	private final String[] SUPPORTED_COLUMNS = {
			CUSTOMER_DOCUMENT,
			CUSTOMER_NAME,
			INVOICE_DOCUMENT,
			INVOICE_DATE,
			INVOICE_PAYMENT_DATE,
			
			INVOICE_BASE_TERMINO_POTENCIA,
			INVOICE_BASE_EXCESOS_POTENCIA,
			INVOICE_BASE_ALQUILER,
			INVOICE_BASE_GASTOS_GESTION_COBRO,
			
			INVOICE_VAT_PERCENT,
			INVOICE_VAT_BASE,
			INVOICE_VAT_AMOUNT,
			INVOICE_IEE_BASE,
			INVOICE_TOTAL
	};
	
	@Override
	public void load(InputStream file){
		LogPanelController logPanel = LogPanelController.getInstance();
		logPanel.info("Inicio de la carga de datos.");
		logPanel.info("Fichero de VENTAS detectado.");
		try {
	    	Sheet sheet = workbook.getSheetAt(0);
	        
	        Iterator<Row> rowIterator = sheet.iterator();
	        Row row = null;
	        
	        for(int i=0; i<rowOffset || i==0; i++){
	        	row = rowIterator.next();
	        }
	        
	        File defaultImportFile = File.createTempFile("oppidum-sales-data", ".tmp");
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
	        // 4
	        writer.print("baseImponible4|");
	        writer.print("iva4|");
	        writer.print("cuotaIVA4|");
	        writer.print("cuentaExplotacion4|");
	        
	        writer.print("totalFactura|");
	        writer.print("fechaVto|");
	        writer.print("formaPago|");
	        writer.print("cuentaBanco");
	        writer.println();
	        
	        String maxAccountCode = obtainMaxAccountCode();
	        String negotiablePaymethod = getNegotiablePaymethod();
	        int emptyAccountCount = 0;
	        customerAccount = new HashMap<>();
	        
        	int lineCount=0;
        	while(rowIterator.hasNext()){
        		row = rowIterator.next();
        		
        		String customerDocument = getStringCellValue(row.getCell(headers.indexOf(CUSTOMER_DOCUMENT)));
        		String customerName = getStringCellValue(row.getCell(headers.indexOf(CUSTOMER_NAME)));
        		String account = obtainCustomerAccount(customerDocument, customerName);
        		
        		if(account==null || StringUtils.isBlank(account)){
        			emptyAccountCount++;
        			account = String.valueOf(Integer.valueOf(maxAccountCode)+emptyAccountCount);
        			customerAccount.put(customerDocument, account);
        			logPanel.warn("El cliente " + customerDocument + " no tiene cuenta asignada. Se le asigna la siguiente libre" + " (" + account + ")");
        		}
        		
        		if(account!=null && StringUtils.isNotBlank(account)){
        			
        			String invoiceNumber = getStringCellValue(row.getCell(headers.indexOf(INVOICE_DOCUMENT)));
        			String serie = obtainInvoiceSeries(invoiceNumber);
        			String num = obtainInvoiceNumber(invoiceNumber);
        			Date invoiceDate = getDateCellValue(row.getCell(headers.indexOf(INVOICE_DATE)));
        			Date invoicePaymentDate = getDateCellValue(row.getCell(headers.indexOf(INVOICE_PAYMENT_DATE)));
        			String bankAccount = null;
        			if(headers.contains(INVOICE_BANK_ACCOUNT)){
        				bankAccount = getStringCellValue(row.getCell(headers.indexOf(INVOICE_BANK_ACCOUNT)));
        			}
        			double vatPercent = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_VAT_PERCENT)));
        			double vatBase = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_VAT_BASE)));
        			double vatAmount = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_VAT_AMOUNT)));
        			double invoiceTotal = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_TOTAL)));
        			
        			double baseTerminoPotencia = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_TERMINO_POTENCIA)));
        			double baseExcesosPotencia = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_EXCESOS_POTENCIA)));
        			double baseAlquiler = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_ALQUILER)));
        			double baseGastosGestionCobro = getNumericCellValue(row.getCell(headers.indexOf(INVOICE_BASE_GASTOS_GESTION_COBRO)));
        			
        			vatBase -= (baseTerminoPotencia + baseExcesosPotencia 
        					+ baseAlquiler 
        					+ baseGastosGestionCobro);
        			vatAmount -= (CommonUtil.round((baseTerminoPotencia + baseExcesosPotencia) * vatPercent / 100) 
        					+ CommonUtil.round(baseAlquiler * vatPercent / 100)
        					+ CommonUtil.round(baseGastosGestionCobro * vatPercent / 100));
        			
        			writer.print("FRACTB|");
        			writer.print(lineCount + "|");
        			writer.print(serie + "|");
        			writer.print(num + "|");
        			writer.print(account + "|");
        			writer.print(customerDocument + "|");
        			writer.print("1|");
        			writer.print("ES|");
        			writer.print(customerName + "|");
        			writer.print(dateFormat.format(invoiceDate) + "|");
        			writer.print("1|");
        			// 1
					writer.print(CommonUtil.round(vatBase) + "|");
					writer.print(CommonUtil.round(vatPercent) + "|");
					writer.print(CommonUtil.round(vatAmount) + "|");
					writer.print("700000001|");
					// 2
					writer.print(CommonUtil.round(baseTerminoPotencia + baseExcesosPotencia) + "|");
					writer.print(CommonUtil.round(vatPercent) + "|");
					writer.print(CommonUtil.round((baseTerminoPotencia + baseExcesosPotencia) * vatPercent / 100) + "|");
					writer.print("700000002|");
					// 3
					writer.print(CommonUtil.round(baseAlquiler) + "|");
					writer.print(CommonUtil.round(vatPercent) + "|");
					writer.print(CommonUtil.round(baseAlquiler * vatPercent / 100) + "|");
					writer.print("700000003|");
					// 4
					writer.print(CommonUtil.round(baseGastosGestionCobro) + "|");
					writer.print(CommonUtil.round(vatPercent) + "|");
					writer.print(CommonUtil.round(baseGastosGestionCobro * vatPercent / 100) + "|");
					writer.print("606000001|");
					
					writer.print(CommonUtil.round(invoiceTotal) + "|");
					writer.print( dateFormat.format(invoicePaymentDate) + "|");
					String ccc = getFormatBankAccount(bankAccount);
					writer.print((StringUtils.isNotBlank(ccc)?negotiablePaymethod:"") + "|");
					writer.print(ccc);
					writer.println();
							
        			lineCount++;
        		}
        		
        	}
        	
        	logPanel.info("Total facturas a procesar:" + lineCount);
        	
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
	
	private String getFormatBankAccount(String bankAccount) {
		if(bankAccount!=null){
			matcher = ibanPattern.matcher(bankAccount);
			if(matcher.matches()){
				return bankAccount;
			}
			matcher = cccPattern.matcher(bankAccount);
			String match = null;
			while (matcher.find()) {
				match = matcher.group(1);
				match += matcher.group(2);
			}
			if(match!=null){
				while(match.length()<20){
					match += 0;
				}
				return match.substring(0, 4) + "." + match.substring(4, 8) + "." + match.substring(8, 10) + "." + match.substring(10, 20);
			}
		}
		return "";
	}
	
	private String getNegotiablePaymethod() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.NEGOTIABLE_DOCUMENT);
		c.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_DOMAIN), DomainManager.getCurrentDomain());
		List<ITransferObject> list = bean.getList(c);
		if(list==null || list.isEmpty()){
			c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.NEGOTIABLE_DOCUMENT);
			c.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_DOMAIN), DomainManager.getParentDomain());
			list = bean.getList(c);
		}
		String name = "";
		if(list!=null && !list.isEmpty()){
			name = ((PayMethod)list.get(0)).getName();
		}
		return name;
		
	}

	private String getStringCellValue(Cell cell) {
		if(cell!=null && cell.getCellType()==CellType.STRING){
			return cell.getStringCellValue();
		} else if(cell!=null && cell.getCellType()==CellType.NUMERIC){
			return String.valueOf(cell.getNumericCellValue());
		}
		return "";
	}
	
	private Double getNumericCellValue(Cell cell) {
		if(cell!=null && cell.getCellType()==CellType.STRING){
			if(NumberUtils.isNumber(cell.getStringCellValue())){
				return Double.parseDouble(cell.getStringCellValue());
			}
		} else if(cell!=null && cell.getCellType()==CellType.NUMERIC){
			return cell.getNumericCellValue();
		}
		return 0.0;
	}
	
	private Date getDateCellValue(Cell cell) {
		if(cell!=null && cell.getCellType()==CellType.STRING){
			try {
				return excelFormat.parse(cell.getStringCellValue());
			} catch (ParseException e) {
				LOGGER.error(e.getMessage());
				throw new AbortProcessingException(e.getMessage());
			}
		} else if(cell!=null && cell.getCellType()==CellType.NUMERIC){
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
		} 
//		catch (InvalidFormatException e) {
//			LOGGER.error(e.getMessage());
//		}
        
		return false;
	}
	
	private String obtainCustomerAccount(String customerDocument, String customerName) throws ManagerBeanException {
		customerDocument = StringUtils.trim(customerDocument);
		String account = null;
		if(customerAccount!=null && customerAccount.containsKey(customerDocument)){
			account = customerAccount.get(customerDocument);
		} else {
			if(StringUtils.isNotBlank(customerDocument)){
				LogPanelController logPanel = LogPanelController.getInstance();
				IManagerBean bean = BeanManager.getManagerBean(Customer.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_DOCUMENT), customerDocument);
				List<ITransferObject> list = bean.getList(criteria);
				if(list==null || list.size()==0){
					logPanel.warn("Se procede a crear un nuevo cliente " + customerName + " (" + customerDocument +")" );
				} else if(list!=null && list.size()==1){
					Customer customer = (Customer) list.get(0);
					if(customer.getAccount()!=null && StringUtils.isNotBlank(customer.getAccount().getCode())){
						account = customer.getAccount().getCode();
						customerAccount.put(customerDocument, account);
					}
				} else {
					logPanel.error("Existen varios registros de cliente con el documento " + customerDocument);
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
		c.addExpression(ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), "4300%"));
		ProjectionList pl = new ProjectionList();
		pl.add(Projection.max(bean.getFieldName(IEntityAlias.ACCOUNT_CODE)));
		List<?> list = bean.getList(pl, c);
		if (list != null && list.size() > 0 && list.get(0) != null) {
			code = (String) list.get(0);	
		}
		if(code==null) {
			code = "430000000";
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
			FileInputStream file = new FileInputStream(new File("/home/eagirrezabal/Descargas/ventas_reduced.xls"));
			loader.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	    
	}
}
