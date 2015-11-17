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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

public class OppidumSalesLoader implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(OppidumSalesLoader.class.getName());
	
	private ArrayList<String> headers;
	
	private final String CUSTOMER_DOCUMENT = "Nif_RazonSocial";
	private final String CUSTOMER_NAME = "Nombre_RazonSocial";
	private final String INVOICE_DOCUMENT = "Factura ML";
	private final String INVOICE_DATE = "Fecha_Facturacion";
	private final String INVOICE_VAT_PERCENT = "%_iva";
	private final String INVOICE_VAT_BASE = "Base_Iva";
	private final String INVOICE_VAT_AMOUNT = "Importe_Iva";
	private final String INVOICE_IEE_BASE = "Importe_Iee";
	private final String INVOICE_TOTAL = "Total Final";
	
	private final String[] SUPPORTED_COLUMNS = {
			CUSTOMER_DOCUMENT,
			CUSTOMER_NAME,
			INVOICE_DOCUMENT,
			INVOICE_DATE,
			INVOICE_VAT_PERCENT,
			INVOICE_VAT_BASE,
			INVOICE_VAT_AMOUNT,
			INVOICE_IEE_BASE,
			INVOICE_TOTAL
	};
	
//	Te mando las dos hojas EXCEL para la carga en la contabilidad de aonSolutions.
//	Empieza por la de ventas.
//	Hay que crear una pantalla donde se pregunta el fichero a importar, por la estructura hay que determinar si es compra o venta:
//	
//	Lo primero que hay que hacer es comprobar si existe el cliente a través del documento (=EXCEL.Nif_RazonSocial), en ese caso recuperar la CuentaContable
//	Crear la estructura de FACTB con los datos de la excel de ventas: Factura_ML - Fecha_facturacion - Base_Iva - %_ivaImporte_iva - Total_final
//
//	En compras es similar.
//	Empieza a mirarlo y lo comentamos el jueves por la mañana.
	
	
//	Hay que restar el importe de la columna AL de la base imponible y insertar una línea con la cuenta 473000560  e importe restado.
	
	
	
//	Carga de Facturas de Venta:
//
//		430* => Cuenta Cliente (EXCEL.Total_Factura)
//		Base 1 = Columna EXCEL.AL
//		Cuota IVA1 = Columan EXCEL.AL * 0.21 Redondeado a dos decimales
//		Cuenta_explotación1 = 473????
//		Base 2 = Base Imponible - Base 1
//		Cuota IVA 2 = Cuota Iva - Cuota IVA1
//		Cuenta_explotación2 = 700000001
//
//
//		Carga de Facturas de Compra:
//
//		400* => Cuenta Proveedor(EXCEL.Total_Factura)
//		Base 1 = EXCEL.Base Imponible
//		Cuota IVA 2 = EXCEL.Base_Imponible
//		Cuenta_explotación2 = 600000001
	
	
	public void load(InputStream file){
		LogPanelController logPanel = LogPanelController.getInstance();
		logPanel.info("Inicio de la carga de datos.");
		logPanel.info("Fichero de ventas detectado.");
		try {
	        //Create Workbook instance holding reference to .xlsx file
//	    	XSSFWorkbook workbook = new XSSFWorkbook(file);
	    	HSSFWorkbook workbook = new HSSFWorkbook(file);

	        HSSFSheet sheet = workbook.getSheetAt(0);

	        int offset = prepareSheet(sheet);
	        
	        Iterator<Row> rowIterator = sheet.iterator();
	        Row row = null;
	        
	        for(int i=0; i<offset || i==0; i++){
	        	row = rowIterator.next();
	        }
	        
	        File defaultImportFile = File.createTempFile("oppidum-data", ".tmp");
	        PrintWriter writer = new PrintWriter(defaultImportFile);
//	        PrintWriter out = new PrintWriter(
//					new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("out.txt")), "UTF-8"));
	        
	        writer.println("1;FRACTB|id|serie|numero|cuenta|documento|tipoDocumento|paisDocumento|razonSocial|fechaFactura|tipo|baseImponible1|iva1|cuotaIVA1|baseImponible2|iva2|cuotaIVA2|totalFactura|cuentaExplotacion");
	        
	        String maxAccountCode = obtainMaxAccountCode();
	        int emptyAccountCount = 0;
	        
        	int i=1;
        	while(rowIterator.hasNext()){
        		
        		String customerDocument = row.getCell(headers.indexOf(CUSTOMER_DOCUMENT)).getStringCellValue();
        		String customerName = row.getCell(headers.indexOf(CUSTOMER_NAME)).getStringCellValue();
        		String account = validateCustomer(customerDocument, customerName);
        		
        		if(account==null || StringUtils.isBlank(account)){
        			emptyAccountCount++;
        			account = String.valueOf(Integer.valueOf(maxAccountCode)+emptyAccountCount);
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
        			
        			double ieeBase = row.getCell(headers.indexOf(INVOICE_IEE_BASE)).getNumericCellValue();
        			double ieeAmount = ieeBase * 0.21;
        			vatBase -= ieeBase;
        			vatAmount -= ieeAmount; 
        
					writer.println("FRACTB|"+i+"|"+serie+"|"+num+"|"+account+"|"+customerDocument+"|1|ES|"+customerName+"|"+invoiceDate+"|1|"
							+CommonUtil.round(vatBase)+"|"+CommonUtil.round(vatPercent)+"|"+CommonUtil.round(vatAmount)+"|"
							+CommonUtil.round(ieeBase)+"|"+CommonUtil.round(vatPercent)+"|"+CommonUtil.round(ieeAmount)+"|"
							+invoiceTotal+"|700000000");
					
        			logPanel.info("Factura procesada: " + invoiceNumber);		
        			i++;
        		}
        		
//        		cellIterator = row.cellIterator();
//        		while(cellIterator.hasNext()){
//        			Cell cell = cellIterator.next();
//        			if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
//        				System.out.print(cell.getNumericCellValue() + "\t");
//        			} else if(cell.getCellType() == Cell.CELL_TYPE_STRING){
//        				System.out.print(cell.getStringCellValue() + "\t");
//        			} else {
//        				System.out.print("Invalid..." + "\t");
//        			}
//        		}
//        		i++;
        		row = rowIterator.next();
        	}
        	
        	writer.flush();
        	
        	AonLoaderController controller = (AonLoaderController) AonUtil.getRegisteredBean("aonLoader");
			loadDefault(IOUtils.toByteArray(new FileInputStream(defaultImportFile)), controller.getParams());
	        
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
	
	private int prepareSheet(HSSFSheet sheet) {
        Iterator<Row> rowIterator = sheet.iterator();
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = null;
        headers = new ArrayList<>();

        int rowIndex=1;
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
        	rowIndex++;
        }
        
        if(!headers.containsAll(Arrays.asList(SUPPORTED_COLUMNS))){
        	LogPanelController logPanel = LogPanelController.getInstance();
        	logPanel.error("No se han encontrado cabeceras de columnas aceptadas entre las 5 primeras filas.");
        	logPanel.error("Proceso abortado.");
        	LOGGER.error("El formato del fichero no es correcto.");
        	throw new AbortProcessingException("El formato del fichero no es correcto");
        }
        
        return rowIndex;
	}
	

	private String validateCustomer(String customerDocument, String customerName) throws ManagerBeanException {
		customerDocument = StringUtils.trim(customerDocument);
		String account = null;
		if(StringUtils.isNotBlank(customerDocument)){
			LogPanelController logPanel = LogPanelController.getInstance();
			IManagerBean bean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_DOCUMENT), customerDocument);
			List<ITransferObject> list = bean.getList(criteria);
			if(list==null || list.size()==0){
				// TODO: create customer
				logPanel.error("No existe el cliente con el documento " + customerDocument);
			} else if(list!=null && list.size()==1){
				Customer customer = (Customer) list.get(0);
				if(customer.getAccount()==null || StringUtils.isBlank(customer.getAccount().getCode())){
					// TODO: create account
					logPanel.error("El cliente con documento " + customerDocument + " no tiene cuenta asignada");
				} else {
					account = customer.getAccount().getCode();
				}
			} else {
				logPanel.error("Existen varios registros de cliente con el documento " + customerDocument);
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
	
	private final Pattern invoiceNumberPattern = Pattern.compile("(\\d{4})-\\d{1}-(\\d+)[-A-Za-z]");
	private Matcher matcher = null;
	
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
	
	private void loadDefault(byte[] data, LoaderParams params){
//		System.out.println(new String(data));
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
//			byte[] data = getAonFile().getData();
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
//			setLoadPressed(false);
			AonLoaderController controller = (AonLoaderController) AonUtil.getRegisteredBean("aonLoader");
			controller.setLoadPressed(false);
		}
	}





	///////////////////////////////
	///////////////////////////////
	
	public static void main(String[] args) {
    	try {
    		OppidumSalesLoader loader = new OppidumSalesLoader();
			FileInputStream file = new FileInputStream(new File("/home/eagirrezabal/Descargas/FACTURACION JULIO AGOSTO (VENTAS).xls"));
			loader.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	    
	}
}
