package com.esferalia.aon.gwt.template.server;


import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.product.Brand;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductTag;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.project.Project;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBFee;
import com.esferalia.aon.gwt.template.jooq.DBProduct;
import com.esferalia.aon.gwt.template.jooq.DBStock;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;



public class TemplatesServlet extends RemoteServiceServlet implements ITemplate{

	private static final long serialVersionUID = 6871016881549113129L;
	
	static final String COMMERCIAL_PRODUCT = ProductType.COMMERCIAL_PRODUCT.getName(new Locale("es_ES")); 
	static final String EXTERNAL_WORK = ProductType.EXTERNAL_WORK.getName(new Locale("es_ES"));
	static final String EXPENSE = ProductType.EXPENSE.getName(new Locale("es_ES"));
	static final String INCREASE = ProductType.INCREASE.getName(new Locale("es_ES"));
	static final String LABOUR = ProductType.LABOUR.getName(new Locale("es_ES"));
	static final String PREPAYMENT = ProductType.PREPAYMENT.getName(new Locale("es_ES"));
	static final String SERVICE = ProductType.SERVICE.getName(new Locale("es_ES"));

	static final String NO_PERIOD = BillingPeriod.NO_PERIOD.getName(new  Locale("es_ES"));
	static final String MONTHLY = BillingPeriod.MONTHLY.getName(new Locale("es_ES"));
	static final String BI_MONTHLY = BillingPeriod.BI_MONTHLY.getName(new Locale("es_ES"));
	static final String THREE_MONTHLY = BillingPeriod.THREE_MONTHLY.getName(new Locale("es_ES"));
	static final String FOUR_MONTHLY = BillingPeriod.FOUR_MONTHLY.getName(new Locale("es_ES"));
	static final String SIX_MONTHLY = BillingPeriod.SIX_MONTHLY.getName(new Locale("es_ES"));
	static final String YEARLY = BillingPeriod.YEARLY.getName(new Locale("es_ES"));
	
	HashMap<String, ProductInfo> map = new HashMap<String, ProductInfo>();
	Integer domainId;
	public static byte[] out;
	static Integer size;
	private static String mimetype;
	
	public static byte[] getOut() {
		return out;
	}

	public static void setOut(byte[] out2) {
		out = out2;
	}
	
	public static String getMimetype() {
		return mimetype;
	}

	public static void setMimetype(String mimetype) {
		TemplatesServlet.mimetype = mimetype;
	}

	public Integer getSize() {
		return size;
	}
	
	public static void setSize(Integer sizea) {
		size = sizea;
	}
	
	void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}
	
	public void initAux(){
		try{
			initFacesContext();
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			domainId = ds.getDomainId();
		}
		finally{releaseFacesContext();}
	}
	
	public TemplatesServlet() {
		//initAux();
	}
	
	public TemplateList getTemplates(){
		String domain = AonUtil.getDomainName();
		TemplateList tl = null;
		try {
			tl = DBConsults.getTemplates(domain, domainId);
			tl.setDomainId(domainId);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return tl;
	}
	

	
	
	public Vector<Warehouse> getWarehouses(){
		Vector<Warehouse> v = new Vector<Warehouse>();
		String domain = AonUtil.getDomainName();
		try {
			v = DBStock.getWarehouse(domain, domainId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return v;
	}
	
	public TemplateInfo newTemplate(TemplateInfo ti ){
		String domain = AonUtil.getDomainName();
		byte[] b = Utils.newXmlFile(ti);
		try {
			Integer id = DBConsults.insertTemplate(domain, ti, b,domainId);
			ti.setId(id);
			ti.setIsParent(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ti;
	}
	
	public TemplateInfo editTemplate(TemplateInfo ti){
		String domain = AonUtil.getDomainName();
		byte[] b = Utils.newXmlFile(ti);
		try {
			DBConsults.updateTemplate(domain, ti, domainId, b);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ti;
	}

	public void deleteTemplate(TemplateInfo ti){
		String domain = AonUtil.getDomainName();
		try {
			DBConsults.removeTemplate(domain, ti.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//-------------------- IMPORTAR FEE
	Vector<FeeInfo> fees;
	FeeInfo fi;
	Vector<Seller> sellers = new Vector<Seller>();
	Vector<WorkPlace> workplaces = new Vector<WorkPlace>();
	Vector<Project> projects = new Vector<Project>();
	Vector<Customer> customers = new Vector<Customer>();
	Vector<InvoicingGroup> invoicingGroups = new Vector<InvoicingGroup>();
	public Integer executeExcel3(TemplateInfo ti){
		long startAll= System.currentTimeMillis();
		String domain = AonUtil.getDomainName();
		error = new Error();
		verror = new Vector<String>();
		error.setTextError(verror);
		textError = "";
		Vector<FeeInfo> fees = new Vector<FeeInfo>();

		try {
			sellers = DBFee.getSellers(domain,domainId);
			workplaces = DBFee.getWorkplaces(domain,domainId);
			projects = DBFee.getProjects(domain,domainId);
			customers = DBFee.getCustomers(domain, domainId);
			invoicingGroups = DBFee.getInvoicingGroups(domain, domainId);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Error error = new Error();
		if(!getMimetype().equals(MimeType.MIME_MS_EXCEL.getName())
				&& !getMimetype().equals(MimeType.MIME_MS_EXCEL_2007.getName())
				&& !getMimetype().equals(MimeType.MIME_STAR_OFFICE_SPREADSHEET.getName())){
				//El archivo no es un fichero Excel.
				error.setError(false);
				verror.add("*El archivo importado no es de tipo excel.");
				error.setTextError(verror);
				this.error = error;
				return -1;
		}
		byte[] data = getOut();
			
		File aux = new File("/tmp/fee.xls");
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileInputStream excel = null;
		try {
			excel = new FileInputStream(aux);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		HSSFWorkbook workbook= null;
		try {
			workbook = new HSSFWorkbook(excel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		rowCount  = sheet.getPhysicalNumberOfRows();
		
		Iterator<Row> rowIterator = sheet.iterator();

		/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		rowStream.forEach(row ->{
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			fi = newFee();
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			rowAux = row;
			cellStream.forEach(cell ->{
                if(cell.getRowIndex() == 0){//Primera fila del fichero Excel.
                	if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue())){
                		 // El archivo no es compatible con la plantilla
             			error.setError(false);
             			verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
             			error.setTextError(verror);
             			this.error = error;
                		rowCount = -1;
                	} 
                }
                else{
                	if(cell.getColumnIndex() !=0){
                		Cell beforeCell = rowAux.getCell(cell.getColumnIndex()-1);
            			if((beforeCell == null || beforeCell.getCellType() == Cell.CELL_TYPE_BLANK) && isRequiredFee(ti.getColumns().get(cell.getColumnIndex()-1))){
            				if(beforeCell == null){
            					verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto");
            					textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto \n";
            				}
            				else if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Nombre") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("C\u00f3digo") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Precio Coste") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Precio Venta Base")){
            					verror.add("*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto");
            					textError= textError + "*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n";
            				}
            			}
                	}

                	if(!ti.getColumns().get(cell.getColumnIndex()).equals("Texto Libre")){
                		fi = checkFee(ti.getColumns().get(cell.getColumnIndex()),fi,cell);
                		if(fi == null){
                			verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto ");
                			textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
                			fi = newFee();	
                		}
                	}
                 }  
			});
			if(row.getLastCellNum() != ti.getColumns().size()){
				if(row.getRowNum() == 0){
					error.setError(false);
            		verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
            		error.setTextError(verror);
            		this.error = error;
            		rowCount = -1;
            		
            	}
            	else{
            		if(row.getLastCellNum() != -1){
            			Short cellnum = row.getLastCellNum();
            			if(row.getLastCellNum() > ti.getColumns().size())cellnum--;
            			if(ti.getColumns().get(cellnum).equals("Nombre") || ti.getColumns().get(cellnum).equals("C\u00f3digo") || ti.getColumns().get(cellnum).equals("Precio Coste") || ti.getColumns().get(cellnum).equals("Precio Venta Base")){
          					verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
          					textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
            			}
            		}
            	}
            }
            
            if(row.getRowNum() > 0 && fi.getProduct()!= null){ 
            	fi.setRow(row.getRowNum());
            	fees.add(fi);
            }
		});
		
		this.fees = fees;

		long time = System.currentTimeMillis() - startAll;
		System.out.println("time: " + (time/1000d));
		System.out.println(rowCount);
		return rowCount;
	}

	public Boolean isRequiredFee(String s){
		return s.equals("Cliente") || s.equals("Producto") || s.equals("Cantidad") || s.equals("Precio")
				|| s.equals("Descuento") || s.equals("Fecha Inicio") || s.equals("Fecha Facturaci\u00f3n" )
				|| s.equals("Centro Trabajo");
	}
	
	public Error insertFee() {
		long startAll= System.currentTimeMillis();
		Vector<String> verror = error.getTextError();
		String domain = AonUtil.getDomainName();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			try {
				//insertar Fee en base de datos.!!
				error = DBFee.insertFee(domain,domainId,fees);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		
		long timeAll = System.currentTimeMillis() - startAll;
			
		System.out.println("ALL    " + (timeAll/1000d));
		return error;
	}
	
	private FeeInfo newFee() {
		FeeInfo feeInfo = new FeeInfo();
		feeInfo.setPeriod(0);
		feeInfo.setDiscount(0.0);
		feeInfo.setConfidential(false);
		feeInfo.setDetail("");
		feeInfo.setDetail2("");
		feeInfo.setDetail3("");
		feeInfo.setDescription("");
		return feeInfo;
	}
	
	private FeeInfo checkFee(String template,FeeInfo fee, Cell cell) {
		Integer type = cell.getCellType();
		switch (template) {
		case "Cliente": case "Client":
			if(type.equals(Cell.CELL_TYPE_STRING) && !cell.getStringCellValue().equals("")){
				String strAux = cell.getStringCellValue();
				Boolean b = true;
				for(Customer s : customers){
					if(strAux.equalsIgnoreCase(s.getRegistry().getDocument()) || strAux.equalsIgnoreCase(s.getRegistry().getAlias()) || strAux.equalsIgnoreCase(s.getRegistry().getName())){
						fee.setClient(cell.getStringCellValue());
						fee.setClientId(s.getId());
						b= false;
					}
				}
				if(b) return null;	
			}
			else return null;
			break;
		case "Producto": case "Product":
			if(type.equals(Cell.CELL_TYPE_STRING) && !cell.getStringCellValue().equals(""))
				fee.setProduct(cell.getStringCellValue());
			else return null;
			break;
		case "Cantidad": case "Quantity":
			if(type.equals(Cell.CELL_TYPE_NUMERIC)){ 
				fee.setQuantity(cell.getNumericCellValue());
			}
			else return null;
			break;
		case "Precio": case "Price":
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				fee.setPrice(cell.getNumericCellValue());
			else return null;
			break;
		case "Descuento": case "Discount":
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				fee.setDiscount(cell.getNumericCellValue());
			else return null;
			break;
		case "Fecha Inicio": case "Start Date":
			if(type.equals(Cell.CELL_TYPE_STRING)){
				Date d = Utils.stringToDate(cell.getStringCellValue());
				if(d != null) fee.setStartDate(d); 
				else return null;
			}
			else if(type.equals(Cell.CELL_TYPE_NUMERIC)){
				fee.setStartDate(cell.getDateCellValue());
			}
		/*	else if(type.equals(Cell.CELL_TYPE_FORMULA)){
				
			}*/
			else return null;
			break;
		case "Fecha Fin": case "End Date":
			if(type.equals(Cell.CELL_TYPE_STRING)){
				Date d = Utils.stringToDate(cell.getStringCellValue());
				if(d != null) fee.setEndDate(d); 
				else return null;
			}
			else if(type.equals(Cell.CELL_TYPE_NUMERIC)){
				fee.setEndDate(cell.getDateCellValue());
			}
		/*	else if(type.equals(Cell.CELL_TYPE_FORMULA)){
				
			}*/
			else return null;
			break;
		case "Fecha Facturaci\u00f3n": case "Billing Date":
			if(type.equals(Cell.CELL_TYPE_STRING)){
				Date d = Utils.stringToDate(cell.getStringCellValue());
				if(d != null) fee.setBillingDate(d); 
				else return null;
			}
			else if(type.equals(Cell.CELL_TYPE_NUMERIC)){
				fee.setBillingDate(cell.getDateCellValue());
			}
		/*	else if(type.equals(Cell.CELL_TYPE_FORMULA)){
				
			}*/
			else return null;
			break;
		case "Periodo": case "period": //enum
		
				String t;
				if(type.equals(Cell.CELL_TYPE_STRING)){
					t = cell.getStringCellValue();
					if (t.equalsIgnoreCase(NO_PERIOD))
						fee.setPeriod(BillingPeriod.NO_PERIOD.ordinal());
					else if(t.equalsIgnoreCase(MONTHLY))
						fee.setPeriod(BillingPeriod.MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(BI_MONTHLY))
						fee.setPeriod(BillingPeriod.BI_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(THREE_MONTHLY))
						fee.setPeriod(BillingPeriod.THREE_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(FOUR_MONTHLY))
						fee.setPeriod(BillingPeriod.FOUR_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(SIX_MONTHLY))
						fee.setPeriod(BillingPeriod.SIX_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(YEARLY))
						fee.setPeriod(BillingPeriod.YEARLY.ordinal());
					else return null;
				}
	
			break;
		case "Comercial": case "Seller": //bd
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = cell.getStringCellValue();
				Boolean b = true;
				for(Seller s : sellers){
					if(strAux.equalsIgnoreCase(s.getRegistryDocument()) || strAux.equalsIgnoreCase(s.getRegistryAlias()) || strAux.equalsIgnoreCase(s.getRegistryName())){
						fee.setSeller(cell.getStringCellValue());
						fee.setSellerId(s.getId());
						b= false;
					}
				}
				if(b) return null;	
			}
			else return null;
			break;
		case "Centro Trabajo": case "Workplace": //bd
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = cell.getStringCellValue();
				Boolean b = true;
				for(WorkPlace s : workplaces){
					if(strAux.equalsIgnoreCase(s.getDescription())){
						fee.setWorkplace(cell.getStringCellValue());
						fee.setWorkplaceId(s.getId());
						b= false;
					}
				}
				if(b) return null;	
			}
			else return null;
			break;
		case "Grupo Facturaci\u00f3n": 
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = cell.getStringCellValue();
				Boolean b = true;
				for(InvoicingGroup s : invoicingGroups){
					if(strAux.equalsIgnoreCase(s.getDescription())){
						fee.setBillingGroup(s.getId());
						b= false;
					}
				}
				if(b) return null;	
			}
			else return null;
			break;
		case "Confidencial": case "Confidential":
			Boolean bool2 = false;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String string = cell.getStringCellValue();
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool2 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool2 = false;
				else return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double num = cell.getNumericCellValue();
				if(num.equals(1.0)) bool2 = true;
				else if(num.equals(0.0)) bool2 = false;
				else return null;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool2 = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:
				return fee;
			default:
				return null;
			}
			fee.setConfidential(bool2);
			break;
		case "Proyecto": case "Project":  //BD
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = cell.getStringCellValue();
				Boolean b = true;
				for(Project s : projects){
					if(strAux.equalsIgnoreCase(s.getName()) || strAux.equalsIgnoreCase(s.getAlias())){
						fee.setProject(cell.getStringCellValue());
						fee.setProjectId(s.getId());
						b= false;
					}
				}
				if(b) return null;	
			}
			else return null;
			break;
		case "Detalle 1": case "Detail 1":
			if(type.equals(Cell.CELL_TYPE_STRING))
				fee.setDetail(cell.getStringCellValue());
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 2": case "Detail 2":
			if(type.equals(Cell.CELL_TYPE_STRING))
				fee.setDetail2(cell.getStringCellValue());
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 3": case "Detail 3":
			if(type.equals(Cell.CELL_TYPE_STRING))
				fee.setDetail3(cell.getStringCellValue());
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Descripci\u00f3n":
			if(type.equals(Cell.CELL_TYPE_STRING))
				fee.setDescription(cell.getStringCellValue());
			break;
		case "Linea": case "Line":
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				fee.setLine(cell.getNumericCellValue());
		default:
			break;
		}
		return fee;
	}
	
	//-------------------- IMPORTAR STOCK
	Vector<StockInfo> stock;
	TransferInfo transferInfo;
	String textError;
	Error error;
	Integer rowCount;
	StockInfo si;
	TemplateInfo ti;
	Row rowAux ;
	public Integer executeExcel(TemplateInfo templateInfo, String warehouse1,String warehouse2 , String series, String comments){
		ti = templateInfo;
		long startAll= System.currentTimeMillis();
		String domain = AonUtil.getDomainName();
		error = new Error();
		Vector<String> verror = new Vector<String>();
		error.setTextError(verror);
		textError = "";
		Vector<StockInfo> stock = new Vector<StockInfo>();
		com.esferalia.aon.gwt.template.shared.Error error = new Error();
		
		if(warehouse1.equals("-") && (warehouse2.equals("-") || warehouse2 == null)){
			error.setError(false);
			textError = textError + "*Error : No ha seleccionado ning\u00fan almac\u00e9n. \n";
    		verror.add("*Error : No ha seleccionado ning\u00fan almac\u00e9n.");
    		error.setTextError(verror);
    		this.error = error;
    		return -1;
		}
		
		Warehouse w = new Warehouse();
		Warehouse w2 = new Warehouse();

		Series s = new Series();
		try {
			Integer domId;
			if (ti.getDomainId().equals(0)) domId = domainId; 
			else domId = ti.getDomainId();
			
			if(!warehouse1.equals("-")) w = DBStock.getWarehouse(warehouse1, domId,domain);
			if(warehouse2 != null && !warehouse2.equals("-")) 	w2 = DBStock.getWarehouse(warehouse2, domId,domain);
			s = DBStock.getSeries(domain, domainId, series);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

    	Boolean b = true;

		transferInfo = new TransferInfo();
		if(!warehouse1.equals("-")) transferInfo.setTargetWarehouse(w);
		if(warehouse2 != null && !warehouse2.equals("-")) transferInfo.setSourceWarehouse(w2);
		transferInfo.setSeries(s);
		transferInfo.setComments(comments);
		
    	if(transferInfo.getSeries() != null && transferInfo.getTargetWarehouse() !=null){
    		
    		
    			try {
    		 		b = DBStock.checkSeries(domain,domainId,transferInfo.getSeries(),transferInfo.getTargetWarehouse());
    	 		} catch (SQLException e) {
    	 			e.printStackTrace();
    	 		}
    		
    	}
    	if(!b){
    		error.setError(false);
    		textError = textError + "*Error : La serie y el almacén no concuerdan. \n";
    		verror.add("*Error : La serie y el almacén no concuerdan.");
    		error.setTextError(verror);
    		this.error = error;
    		return -1;
    	}
		


		if(!getMimetype().equals(MimeType.MIME_MS_EXCEL.getName())
			&& !getMimetype().equals(MimeType.MIME_MS_EXCEL_2007.getName())){
			//El archivo no es un fichero Excel.
			error.setError(false);
			verror.add("*El archivo importado no es de tipo excel.");
			error.setTextError(verror);
			this.error = error;
			return -1;
		}
		byte[] data = getOut();
		
		File aux = new File("/tmp/products.xls");
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileInputStream excel = null;
		try {
			excel = new FileInputStream(aux);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		HSSFWorkbook workbook= null;
		try {
			workbook = new HSSFWorkbook(excel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		rowCount  = sheet.getPhysicalNumberOfRows();
		
		Iterator<Row> rowIterator = sheet.iterator();

	 	/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		rowStream.forEach(row ->{
	
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			si = newStock();
			rowAux = row;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			cellStream.forEach(cell ->{
				Object object = null ;
                switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BLANK:
						break;
					case Cell.CELL_TYPE_BOOLEAN: 
						object = cell.getBooleanCellValue();break;
					case Cell.CELL_TYPE_ERROR:
						object = cell.getErrorCellValue();break;
					case Cell.CELL_TYPE_FORMULA:
						break; 
					case Cell.CELL_TYPE_NUMERIC:
						object = cell.getNumericCellValue();break;
					case Cell.CELL_TYPE_STRING:
						object = cell.getStringCellValue();break;
					default:
						break;
				}
                if(cell.getRowIndex() == 0){//Primera fila del fichero Excel.
                	if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue())){
                		 // El archivo no es compatible con la plantilla
             			error.setError(false);
             			textError =  textError + "*El archivo importado no es compatible con la plantilla seleccionada.\n";
             			verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
             			error.setTextError(verror);
             			this.error = error;
                		rowCount = -1;
                	} 
                }
                else{
                	if(cell.getColumnIndex() !=0){
                		Cell beforeCell = rowAux.getCell(cell.getColumnIndex()-1);
            			if((beforeCell == null || beforeCell.getCellType() == Cell.CELL_TYPE_BLANK) && isRequiredStock(ti.getColumns().get(cell.getColumnIndex()-1))){
            				if(beforeCell == null){
            					textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto \n";
            					verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto");
            					error.setTextError(verror);
            					this.error = error;
            				}
            				else if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Producto") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Almac\u00e9n Destino") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Cantidad") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Series")){
            					textError= textError + "*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n";
            					verror.add("*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n");
            					error.setTextError(verror);
            					this.error = error;
            				}
            			}
                	}
                	if(!ti.getColumns().get(cell.getColumnIndex()).equals("Texto Libre") && !ti.getColumns().get(cell.getColumnIndex()).equals("Nombre")){
                		si = check(ti.getColumns().get(cell.getColumnIndex()),object,si,cell.getCellType());
                		if(si == null){
                			textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
                			verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n");
                			error.setTextError(verror);
                			this.error = error;
                			si = newStock();	
                		}
                	}
                 }  
			});
			//System.out.println(ti.getColumns().size());
			//System.out.println(row.getLastCellNum());
			if(row.getLastCellNum() != ti.getColumns().size()){
				if(row.getRowNum() == 0){
					error.setError(false);
					textError= textError + "*El archivo importado no es compatible con la plantilla seleccionada. \n ";
            		verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
            		error.setTextError(verror);
            		this.error = error;
            		rowCount = -1;
            		
            	}
            	else{
            		if(row.getLastCellNum() != -1){
            			Short cellnum = row.getLastCellNum();
            			if(row.getLastCellNum() > ti.getColumns().size())cellnum--;
            			if(ti.getColumns().get(cellnum).equals("Producto") || ti.getColumns().get(cellnum).equals("Almac\u00e9n Destino") || ti.getColumns().get(cellnum).equals("Cantidad")){
          					verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
          					error.setTextError(verror);
          					this.error = error;
          					textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
            			}
            		}
            	}
            }
            
            if(row.getRowNum() > 0){ 
            	si.setRow(row.getRowNum());
            	stock.add(si);
            }
		});
		this.stock = stock;
		long time = System.currentTimeMillis() - startAll;
		System.out.println("time: " + (time/1000d));
		System.out.println(rowCount);
		return rowCount;
	}
	public Boolean isRequiredStock(String s){
		return s.equals("Producto") ||s.equals("Cantidad");
	}
	public Error insertStock() {
		long startAll= System.currentTimeMillis();
		Vector<String> verror = error.getTextError();
		String domain = AonUtil.getDomainName();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			//String domain = AonUtil.getDomainName();
			try {
				error = DBStock.insertStock2(domain,domainId,stock,transferInfo);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        //insertar STOCK en base de datos.!!
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		
		long timeAll = System.currentTimeMillis() - startAll;
			
		System.out.println("ALL    " + (timeAll/1000d));
		return error;
	}
	
	public Error insertTransferStock(){
		long startAll= System.currentTimeMillis();
		Vector<String> verror = error.getTextError();
		String domain = AonUtil.getDomainName();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			//String domain = AonUtil.getDomainName();
			try {
				error = DBStock.insertTransferStock(domain,domainId,stock,transferInfo);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        //insertar STOCK en base de datos.!!
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		
		long timeAll = System.currentTimeMillis() - startAll;
		System.out.println("ALL    " + (timeAll/1000d));
		return error;
	}

	private StockInfo check(String template, Object value,StockInfo stock, Integer type) {
		
		switch (template) {
		case "Producto": case "Product": 
			if(type.equals(Cell.CELL_TYPE_STRING) && !value.equals("")){
				stock.setProduct((String) value);
			}
			else return null;
			break;
		case "Cantidad": case "Quantity":
			if(type.equals(Cell.CELL_TYPE_NUMERIC)){
				Double n = (Double) value;
				stock.setQuantity(n);
			}
			else return null;
			break;
		case "Detalle 1": case "Detail 1":
			if(type.equals(Cell.CELL_TYPE_STRING))
				stock.setDetail((String)value);
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 2": case "Detail 2":
			if(type.equals(Cell.CELL_TYPE_STRING))
				stock.setDetail2((String)value);
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 3": case "Detail 3":
			if(type.equals(Cell.CELL_TYPE_STRING))
				stock.setDetail3((String)value);
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		/*case "Comentarios": case "Comments":
			if(type.equals(Cell.CELL_TYPE_STRING))
				stock.setComments((String)value);
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;*/
		default:
			break;
		}
		
		return stock;
	}
	
	private StockInfo newStock(){
		StockInfo stock = new StockInfo();
		
		stock.setDetail("");
		stock.setDetail2("");
		stock.setDetail3("");
		//stock.setComments("");
		//stock.setSourceWarehouse(null);
		return stock;
	}
	
	public Vector<String> getSeries(){
		String domain = AonUtil.getDomainName();
		Vector<Series> series = new Vector<Series>();
		try {
			series = DBStock.getSeries(domain, domainId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Vector<String> seriesCode = new Vector<String>();
		series.parallelStream().forEach(s ->{
			seriesCode.add(s.getCode());
		});
		return seriesCode;
	}
	
	//-------------------- IMPORTAR PRODUCTOS
	Vector<String> verror;
	Vector<ProductInfo> products;
	ProductInfo pi;
	public Integer executeExcel2(TemplateInfo ti){
		this.ti = ti;
		error = new Error();
		verror = new Vector<String>();
		error.setTextError(verror);
		textError = "";
		Vector<ProductInfo> products = new Vector<ProductInfo>();
		com.esferalia.aon.gwt.template.shared.Error error = new Error();
    	

		if(!getMimetype().equals(MimeType.MIME_MS_EXCEL.getName())
			&& !getMimetype().equals(MimeType.MIME_MS_EXCEL_2007.getName())){
			//El archivo no es un fichero Excel.
			error.setError(false);
			verror.add("*El archivo importado no es de tipo excel.");
			error.setTextError(verror);
			this.error = error;
			return -1;
		}
		byte[] data = getOut();
		
		File aux = new File("/tmp/products.xls");
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileInputStream excel = null;
		try {
			excel = new FileInputStream(aux);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		HSSFWorkbook workbook= null;
		try {
			workbook = new HSSFWorkbook(excel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		rowCount  = sheet.getPhysicalNumberOfRows();
		
		Iterator<Row> rowIterator = sheet.iterator();

	 	/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		map = new HashMap<String, ProductInfo>();
		rowStream.forEach(row ->{
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			pi = newProduct();
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			cellStream.forEach(cell ->{
				Object object = null ;
                switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BLANK:
						break;
					case Cell.CELL_TYPE_BOOLEAN: 
						object = cell.getBooleanCellValue();break;
					case Cell.CELL_TYPE_ERROR:
						object = cell.getErrorCellValue();break;
					case Cell.CELL_TYPE_FORMULA:
						break; 
					case Cell.CELL_TYPE_NUMERIC:
						object = cell.getNumericCellValue();break;
					case Cell.CELL_TYPE_STRING:
						object = cell.getStringCellValue();break;
					default:
						break;
				}
                if(cell.getRowIndex() == 0){//Primera fila del fichero Excel.
                	if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue())){
                		 // El archivo no es compatible con la plantilla
             			error.setError(false);
             			verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
             			error.setTextError(verror);
             			this.error = error;
                		rowCount = -1;
                	} 
                }
                else{
                	if(cell.getColumnIndex() !=0){
                		Cell beforeCell = row.getCell(cell.getColumnIndex()-1);
            			if((beforeCell == null || beforeCell.getCellType() == Cell.CELL_TYPE_BLANK) && isRequiredProduct(ti.getColumns().get(cell.getColumnIndex()-1))){
            				if(beforeCell == null){
            					verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto");
            					textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto \n";
            				}
            				else if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Nombre") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("C\u00f3digo") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Precio Coste") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Precio Venta Base")){
            					verror.add("*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto");
            					textError= textError + "*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n";
            				}
            			}
                	}

                	if(!ti.getColumns().get(cell.getColumnIndex()).equals("Texto Libre")){
                		long startcheck= System.currentTimeMillis();
                		pi = check(ti.getColumns().get(cell.getColumnIndex()),object,pi,cell.getCellType());
                		long timecheck = System.currentTimeMillis() - startcheck;
                		System.out.println("timecheck: " + (timecheck/1000d));
                		if(pi == null){
                			verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto ");
                			textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
                			pi = newProduct();	
                		}
                	}
                 }  
			});
			if(row.getLastCellNum() != ti.getColumns().size()){
				if(row.getRowNum() == 0){
					error.setError(false);
            		verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
            		error.setTextError(verror);
            		this.error = error;
            		rowCount = -1;
            		
            	}
            	else{
            		if(row.getLastCellNum() != -1){
            			Short cellnum = row.getLastCellNum();
            			if(row.getLastCellNum() > ti.getColumns().size())cellnum--;
            			if(ti.getColumns().get(cellnum).equals("Nombre") || ti.getColumns().get(cellnum).equals("C\u00f3digo") || ti.getColumns().get(cellnum).equals("Precio Coste") || ti.getColumns().get(cellnum).equals("Precio Venta Base")){
          					verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
          					textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
            			}
            		}
            	}
            }
            
            if(row.getRowNum() > 0 && pi.getProduct().getCode()!= null){ 
            	pi.setRow(row.getRowNum());
            	products.add(pi);
            	if(!map.containsKey(pi.getProduct().getCode()))
            		map.put(pi.getProduct().getCode(), pi);
            	else{
            		map.get(pi.getProduct().getCode()).setProduct(pi.getProduct());
            		map.get(pi.getProduct().getCode()).getItem().add(pi.getItem().get(0));
            		Vector<com.esferalia.aon.occam.api.model.product.ProductTag> pts = new Vector<com.esferalia.aon.occam.api.model.product.ProductTag>();
            		if(pi.getProductTag() != null && pi.getProductTag().size() > 0){
            			for (com.esferalia.aon.occam.api.model.product.ProductTag pt : pi.getProductTag()) {
            				if(!esta(pt,map.get(pi.getProduct().getCode()).getProductTag())){
            					pts.add(pt);
            				}	
            			}
            			map.get(pi.getProduct().getCode()).getProductTag().addAll(pts);
            		}
            	}
            }
		});
		this.products = products;
		return rowCount;
	}

	public Boolean esta(com.esferalia.aon.occam.api.model.product.ProductTag pt, Vector<com.esferalia.aon.occam.api.model.product.ProductTag> pts){
		for (com.esferalia.aon.occam.api.model.product.ProductTag productTag : pts) {
			if(productTag.getId().equals(pt.getId())) return true;
		}
		return false;
	}
	
	public Boolean isRequiredProduct(String s) {
		return s.equals("Nombre") || s.equals("C\u00f3digo") || s.equals("Precio Coste") || s.equals("Precio Venta Base");
	}
	
	public Error insertProduct() {
		long startAll= System.currentTimeMillis();
		String domain = AonUtil.getDomainName();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			//String domain = AonUtil.getDomainName();
			
				//error = DBProduct.insertProducts(domain, domainId, products, ti);
				java.util.List<ProductInfo> l = new ArrayList<ProductInfo>(map.values());
				Vector<ProductInfo> v = new Vector<ProductInfo>(l);
 				error = DBProduct.insertProducts2(domain, domainId, v, ti);
		
	        //insertar STOCK en base de datos.!!
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		
		long timeAll = System.currentTimeMillis() - startAll;
			
		System.out.println("ALL    " + (timeAll/1000d));
		return error;
	}

	private ProductInfo check(String template, Object value,ProductInfo product, Integer type) {
		
		String domain = AonUtil.getDomainName();
		switch (template) {
		case "Nombre": 
			if(type.equals(Cell.CELL_TYPE_STRING) && !value.equals("")){
				product.getProduct().setName((String) value);
			}
			else return null;
			break;
		case "C\u00f3digo" : 
			if(type.equals(Cell.CELL_TYPE_STRING) && !value.equals("")){
				product.getProduct().setCode((String) value);		
			}
			else return null;
			break; 
		case "Precio Coste" : 
			if(type.equals(Cell.CELL_TYPE_NUMERIC)){
				product.getItem().get(0).setPurchasePrice((double) value);
			}
			else return null;
			break; 
		case "Precio Venta Base" :
			if(type.equals(Cell.CELL_TYPE_NUMERIC)){
				product.getItem().get(0).setPrice((double) value);
			}
			else return null;
			break; 
		case "Categor\u00eda" :
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = (String) value;
				Vector<ProductCategory> v = null;
				try {
					v = DBProduct.getCategories(domain, domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Boolean b = true;
				for(ProductCategory pc : v){
					if(strAux.equalsIgnoreCase(pc.getName())){
						product.getProduct().setCategory(pc.getId());
						b= false;
					}
				}
				if(b) return null;
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "Marca" : 
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = (String) value;
				Vector<Brand> v = null;
				try {
					v = DBProduct.getBrands(domain, domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Boolean b = true;
				for(Brand brand : v){
					if(strAux.equalsIgnoreCase(brand.getName())){
						product.getProduct().setBrand(brand.getId());
						b= false;
					}
				}
				if(b) return null;
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "Etiqueta" : 
			if(type.equals(Cell.CELL_TYPE_STRING)){
				Vector<ProductTag> tags = null;
				try {
					tags = DBProduct.getTags(domain, domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Vector<String> strings = tags((String)value);
				Set<ProductTag> tags2 = new HashSet<ProductTag>();
				Vector<com.esferalia.aon.occam.api.model.product.ProductTag> pts = new Vector<com.esferalia.aon.occam.api.model.product.ProductTag>();
				for(ProductTag tag : tags){
					for(String s : strings){
						if(s.equalsIgnoreCase(tag.getTag().getName())){
							tags2.add(tag);
							com.esferalia.aon.occam.api.model.product.ProductTag pt = new com.esferalia.aon.occam.api.model.product.ProductTag();
							pt.setDomain(tag.getDomain());
							pt.setId(tag.getId());
							pt.setProduct(tag.getProduct().getId());
							pt.setTag(tag.getTag().getId());
							pts.add(pt);
						}
					}
				}
				if(!tags2.isEmpty()){
					product.setProductTag(pts);
				}
				else return null;
			}
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "Tipo":
			String t;
			if(type.equals(Cell.CELL_TYPE_STRING)){
				t = (String) value;
				if (t.equalsIgnoreCase(COMMERCIAL_PRODUCT)){
					product.getProduct().setType((byte) ProductType.COMMERCIAL_PRODUCT.ordinal());	
				}
				else if(t.equalsIgnoreCase(SERVICE)){
					product.getProduct().setType((byte) ProductType.SERVICE.ordinal());
				}
				else if(t.equalsIgnoreCase(EXTERNAL_WORK)){
					product.getProduct().setType((byte) ProductType.EXTERNAL_WORK.ordinal());
				}
				else if(t.equalsIgnoreCase(LABOUR)){
					product.getProduct().setType((byte) ProductType.LABOUR.ordinal());
				}
				else if(t.equalsIgnoreCase(EXPENSE)){
					product.getProduct().setType((byte) ProductType.EXPENSE.ordinal());
				}	
				else if(t.equalsIgnoreCase(INCREASE)){
					product.getProduct().setType((byte) ProductType.INCREASE.ordinal());
				}	
				else if(t.equalsIgnoreCase(PREPAYMENT)){
					product.getProduct().setType((byte) ProductType.PREPAYMENT.ordinal());
				}
				else return null;
			}
			//else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "IVA" : 
			Tax vat = new Tax();
			Vector<Tax> vats = new Vector<Tax>();
			try {
				vats = DBProduct.getIVA(domain, domainId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String s = (String) value;
				Boolean b = true;
				for(Tax tax : vats){
					if(s.equalsIgnoreCase((tax.getName()))){
						vat = tax;
						b = false;
					}
				}
				if(b) return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double f = (Double) value;
				Boolean b2 = true;
				for(Tax tax : vats){
					if(f.equals(tax.getPercentage())){
						vat = tax;
						b2 = false;
					}
				}
				if(b2) return null;
				break;
			case Cell.CELL_TYPE_BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setVat(vat.getId());
			break; 
		case "IRPF" :
			Tax retention = new Tax();
			Vector<Tax> retentions = new Vector<Tax>();
			try {
				retentions = DBProduct.getRetentions(domain, domainId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String s = (String) value;
				Boolean b3 = true;
				for(Tax tax : retentions){
					if(s.equalsIgnoreCase((tax.getName()))){
						retention = tax;
						b3 = false;
					}
				}
				if(b3) return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double f = (Double) value;
				Boolean b4 = true;
				for(Tax tax : retentions){
					if(f.equals(tax.getPercentage())){
						retention = tax;
						b4 = false;
					}
				}
				if(b4) return null;
				break;
			case Cell.CELL_TYPE_BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setRetention(retention.getId());
			break; 
		case "Inventoriable" : 
			Boolean bool = false;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool = false;
				else return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool = true;
				else if(num.equals(0.0)) bool = false;
				else return null;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool = (Boolean) value;
				break;
			case Cell.CELL_TYPE_BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setInventoriable(bool);
			break; 
		case "Producto Compuesto" : 
			Boolean bool2 = false;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool2 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool2 = false;
				else return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool2 = true;
				else if(num.equals(0.0)) bool2 = false;
				else return null;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool2 = (Boolean) value;
				break;
			case Cell.CELL_TYPE_BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setComposition(bool2);
			break;  
		case "Precio Composici\u00f3n" :
			Boolean bool3 = false;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool3 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool3 = false;
				else return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool3 = true;
				else if(num.equals(0.0)) bool3 = false;
				else return null; 
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool3 = (Boolean) value;
				break;
			case Cell.CELL_TYPE_BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setCompositionPrice(bool3);
			break; 
		case "Estado" : 
	
			ProductStatus status;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String str2 = (String) value;
				if(str2.equalsIgnoreCase("active") || str2.equalsIgnoreCase("activo") || str2.equalsIgnoreCase("activado"))
					status = ProductStatus.ACTIVE;
				else if(str2.equalsIgnoreCase("discontinued") || str2.equalsIgnoreCase("descatalogado"))
					status = ProductStatus.DISCONTINUED;
				else return null;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0))
					status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				Boolean b = (Boolean) value;
				if(b) status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			case Cell.CELL_TYPE_BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setStatus((byte) status.ordinal());
			break; 
		case "C\u00f3digo de Barras" : 
			if(type.equals(Cell.CELL_TYPE_STRING)){
				product.getItem().get(0).setBarcode((String) value);
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "Descripci\u00f3n" :
			if(type.equals(Cell.CELL_TYPE_STRING)){
				product.getItem().get(0).setDescription((String)value);
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 1":
			if(type.equals(Cell.CELL_TYPE_STRING)){
				product.getItem().get(0).setDetail((String) value);
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 2":
			if(type.equals(Cell.CELL_TYPE_STRING)){
				product.getItem().get(0).setDetail2((String) value);
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 3":
			if(type.equals(Cell.CELL_TYPE_STRING)){
				product.getItem().get(0).setDetail3((String) value);
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		default:
			break;
		}
		
		return product;
	}
	
	private ProductInfo newProduct() {
		ProductInfo pi = new ProductInfo();
		com.esferalia.aon.occam.api.model.product.Product p2 = new com.esferalia.aon.occam.api.model.product.Product();
		p2.setDomain(domainId);
		com.esferalia.aon.occam.api.model.product.Item i2 = new com.esferalia.aon.occam.api.model.product.Item();
		i2.setDomain(domainId);
	
		// Tipo (product)
		p2.setType((byte)ProductType.COMMERCIAL_PRODUCT.ordinal());
		
		// IVA (product)
		Tax vat = new Tax();
		vat.setName("GENERAL");
		vat.setType(TaxType.VAT);
		p2.setVat(vat.getId());
		
		// IRPF (product)
		Tax retention = new Tax();
		retention.setName("IRPF");
		retention.setType(TaxType.RETENTION);
		p2.setRetention(retention.getId());
		
		// Inventoriable (product)
		 p2.setInventoriable(false);
		
		 // producto compuesto (product)
		p2.setComposition(false);
		// precio composicion
		p2.setCompositionPrice(false);
		
		// estado (product)
		p2.setStatus((byte)ProductStatus.ACTIVE.ordinal());
		
		// detail
		i2.setDetail("");
		
		// detail2
		i2.setDetail2("");
		
		// detail3
		i2.setDetail3("");
		
		Vector<com.esferalia.aon.occam.api.model.product.Item> is= new Vector<com.esferalia.aon.occam.api.model.product.Item>(); 
		is.add(0, i2);
		pi.setProduct(p2);
		pi.setItem(is);
		return pi;
	}
	
	private Vector<String> tags(String s) {
		Vector<String> v = new Vector<String>();

		if(!s.contains(",")){
			if(!s.equals(""))
				v.add(s);
			return v;
		}
		Integer pos = s.indexOf(",");
		v = tags(s.substring(pos+1));
		v.add(s.substring(0, pos));
		
		return v;
	}
	
	public Vector<TemplateInfo> searchNameTemplate(String searchStr, Vector<TemplateInfo> templates){
		Vector<TemplateInfo> vector = new Vector<TemplateInfo>();
		for (TemplateInfo templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getName(), searchStr)){
				vector.add(templateInfo);
			}
		}
		return vector;
	}
	
	public Vector<TemplateInfo> searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates){
		Vector<TemplateInfo> vector = new Vector<TemplateInfo>();
		for (TemplateInfo templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getType(), searchStr)){
				vector.add(templateInfo);
			}
		}
		return vector;
	}
	
	
	/**
	 * <p>
	 * Checks if CharSequence contains a search CharSequence irrespective of
	 * case, handling {@code null}. Case-insensitivity is defined as by
	 * {@link String#equalsIgnoreCase(String)}.
	 *
	 * <p>
	 * A {@code null} CharSequence will return {@code false}.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.contains(null, *) = false
	 * StringUtils.contains(*, null) = false
	 * StringUtils.contains("", "") = true
	 * StringUtils.contains("abc", "") = true
	 * StringUtils.contains("abc", "a") = true
	 * StringUtils.contains("ábc", "a") = true
	 * StringUtils.contains("abc", "z") = false
	 * StringUtils.contains("abc", "A") = true
	 * StringUtils.contains("ábc", "A") = true
	 * StringUtils.contains("abc", "Z") = false
	 * </pre>
	 * @param str
	 * @param searchStr
	 * @return
	 */
	public static boolean containsIgnoreCase2(String str, String searchStr) {
	    Locale locale = new Locale("es_ES");
		Collator c = Collator.getInstance(locale);
		c.setStrength(Collator.PRIMARY);
	    if (str == null || searchStr == null) {
	        return false;
	    }
	    int len = searchStr.length();
	    int max = str.length() - len;
	    for (int i = 0; i <= max; i++) {   	
	    	if (c.compare(str.substring(i, i+len), searchStr) == 0)
	    		return true;  
	    }
	    return false;
	}
}
