package com.esferalia.aon.gwt.template.server;


import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Collator;
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
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.Brand;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductTag;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;



public class TemplatesServlet extends RemoteServiceServlet implements ITemplate{

	private static final long serialVersionUID = 6871016881549113129L;

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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tl;
	}
	
	public Vector<Warehouse> getWarehouses(){
		Vector<Warehouse> v = new Vector<Warehouse>();
		String domain = AonUtil.getDomainName();
		try {
			v = DBConsults.getWarehouse(domain, domainId);
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
	
	//-------------------- IMPORTAR STOCK
	Vector<StockInfo> stock;
	TransferInfo transferInfo;
	String textError;
	Error error;
	Integer rowCount;
	StockInfo si;
	public Integer executeExcel(TemplateInfo ti, String warehouse, String series, String comments){
		long startAll= System.currentTimeMillis();
		String domain = AonUtil.getDomainName();
		error = new Error();
		Vector<String> verror = new Vector<String>();
		error.setTextError(verror);
		textError = "";
		Vector<StockInfo> stock = new Vector<StockInfo>();
		com.esferalia.aon.gwt.template.shared.Error error = new Error();
		
		Warehouse w = new Warehouse();
		Series s = new Series();
		try {
			w = DBConsults.getWarehouse(warehouse, ti.getDomainId(),domain);
			s = DBConsults.getSeries(domain, domainId, series);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

    	Boolean b = true;

		transferInfo = new TransferInfo();
		transferInfo.setTargetWarehouse(w);
		transferInfo.setSeries(s);
		transferInfo.setComments(comments);
		
    	if(transferInfo.getSeries() != null && transferInfo.getTargetWarehouse() !=null){
    		
    		
    			try {
    		 		b = DBConsults.checkSeries(domain,domainId,transferInfo.getSeries(),transferInfo.getTargetWarehouse());
    	 		} catch (SQLException e) {
    	 			e.printStackTrace();
    	 		}
    		
    	}
    	if(!b){
    		verror.add("*Error : La serie y el almacén no concuerdan.");

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
			long startRow= System.currentTimeMillis();
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			si = newStock();
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
            			if(beforeCell == null || beforeCell.getCellType() == Cell.CELL_TYPE_BLANK){
            				if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Producto") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Almac\u00e9n Destino") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Cantidad") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Series")){
            					textError= textError + "*Fila "+beforeCell.getRowIndex()+", Columna "+beforeCell.getColumnIndex()+" : Dato Incorrecto \n";
            				}
            			}
                	}
                	if(!ti.getColumns().get(cell.getColumnIndex()).equals("Texto Libre")){
                		si = check(ti.getColumns().get(cell.getColumnIndex()),object,si,cell.getCellType());
                		if(si == null){
                			textError= textError + "*Fila "+cell.getRowIndex()+", Columna "+cell.getColumnIndex()+" : Dato Incorrecto \n";
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
          					verror.add("*Fila "+row.getRowNum()+", Columna "+row.getLastCellNum()+" : Dato Incorrecto \n");
          					textError= textError + "*Fila "+row.getRowNum()+", Columna "+row.getLastCellNum()+" : Dato Incorrecto \n";
            			}
            		}
            	}
            }
            
            if(row.getRowNum() > 0){ 
            	si.setRow(row.getRowNum());
            	stock.add(si);
            }
    		long timeRow = System.currentTimeMillis() - startRow;
    		//System.out.println("row time: " + (timeRow/1000d));
		});
		this.stock = stock;
		long time = System.currentTimeMillis() - startAll;
		System.out.println("time: " + (time/1000d));
		System.out.println(rowCount);
		return rowCount;
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
				error = DBConsults.insertStock2(domain,domainId,stock,transferInfo);
				
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
				/*Boolean b = false;
				try {
					b = DBConsults.isItem(domain, domainId, (String) value);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(b)*/ 
				stock.setProduct((String) value);
				//else return null;
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
			series = DBConsults.getSeries(domain, domainId);
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
		long startAll= System.currentTimeMillis();
		String domain = AonUtil.getDomainName();
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
		rowStream.forEach(row ->{
			long startRow= System.currentTimeMillis();
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
            			if(beforeCell == null || beforeCell.getCellType() == Cell.CELL_TYPE_BLANK){
            				if(beforeCell == null){
            					verror.add("*Fila "+cell.getRowIndex()+", Columna "+0+" : Dato Incorrecto");
            					textError= textError + "*Fila "+cell.getRowIndex()+", Columna "+0+" : Dato Incorrecto \n";
            				}
            				else if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Nombre") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("C\u00f3digo") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Precio Coste") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Precio Venta Base")){
            					verror.add("*Fila "+beforeCell.getRowIndex()+", Columna "+beforeCell.getColumnIndex()+" : Dato Incorrecto");
            					textError= textError + "*Fila "+beforeCell.getRowIndex()+", Columna "+beforeCell.getColumnIndex()+" : Dato Incorrecto \n";
            				}
            			}
                	}

                	if(!ti.getColumns().get(cell.getColumnIndex()).equals("Texto Libre")){
                		long startcheck= System.currentTimeMillis();
                		pi = check(ti.getColumns().get(cell.getColumnIndex()),object,pi,cell.getCellType());
                		long timecheck = System.currentTimeMillis() - startcheck;
                		System.out.println("timecheck: " + (timecheck/1000d));
                		if(pi == null){
                			verror.add("*Fila "+cell.getRowIndex()+", Columna "+cell.getColumnIndex()+" : Dato Incorrecto ");
                			textError= textError + "*Fila "+cell.getRowIndex()+", Columna "+cell.getColumnIndex()+" : Dato Incorrecto \n";
                			pi = newProduct();	
                		}
                	}
                 }  
			});
			//System.out.println(ti.getColumns().size());
			//System.out.println(row.getLastCellNum());
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
          					verror.add("*Fila "+row.getRowNum()+", Columna "+row.getLastCellNum()+" : Dato Incorrecto \n");
          					textError= textError + "*Fila "+row.getRowNum()+", Columna "+row.getLastCellNum()+" : Dato Incorrecto \n";
            			}
            		}
            	}
            }
            
            if(row.getRowNum() > 0 && pi.getProduct().getCode()!= null){ 
            	pi.setRow(row.getRowNum());
            	products.add(pi);
            }
    		long timeRow = System.currentTimeMillis() - startRow;
    		//System.out.println("row time: " + (timeRow/1000d));
		});
		
		this.products = products;
		long time = System.currentTimeMillis() - startAll;
		System.out.println("time: " + (time/1000d));
		System.out.println(rowCount);
		return rowCount;
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
			try {
				error = DBConsults.insertProducts(domain, domainId, products);
				
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
	
	public com.esferalia.aon.gwt.template.shared.Error insertProducts(TemplateInfo ti) {
		//Archivo Excel
		Vector<String> verror = new Vector<String>();
		com.esferalia.aon.gwt.template.shared.Error error = new Error();
		if(!getMimetype().equals(MimeType.MIME_MS_EXCEL.getName())
			&& !getMimetype().equals(MimeType.MIME_MS_EXCEL_2007.getName())){
			//El archivo no es un fichero Excel.
			error.setError(false);
			verror.add("*El archivo importado no es de tipo excel.");
			error.setTextError(verror);
			return error;
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
		Iterator<Row> rowIterator = sheet.iterator();
		Integer i = 0;
		String textError = "";
		Vector<ProductInfo> products = new Vector<ProductInfo>();
		
		while(rowIterator.hasNext()) {
			 Row row = rowIterator.next();
			 Iterator<Cell> cellIterator = row.cellIterator();
			 ProductInfo pi = newProduct();
			 Integer j = 0;
             while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                Object o = null ;
                switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK:
					//
					break;
				case Cell.CELL_TYPE_BOOLEAN: 
					o = cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_ERROR:
					o = cell.getErrorCellValue();
					break;
				case Cell.CELL_TYPE_FORMULA:
					//
					break;
				case Cell.CELL_TYPE_NUMERIC:
					o = cell.getNumericCellValue();
					break;
				case Cell.CELL_TYPE_STRING:
					o = cell.getStringCellValue();
					break;
				default:
					break;
				}
                	 
                 
                
                 if(i == 0){//Primera fila del fichero Excel.
                	 if(ti.getColumns().size()<=j ||ti.getColumns().get(j) == null || !ti.getColumns().get(j).equalsIgnoreCase(cell.getStringCellValue())){
                		 // El archivo no es compatible con la plantilla
             			error.setError(false);
             			verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
             			error.setTextError(verror);
             			return error;
                	 } 
                 }
                 else{
                	 if(!ti.getColumns().get(j).equals("Texto Libre")){
                	 	if(j!= cell.getColumnIndex()){
                	 		if(ti.getColumns().get(j).equals("Nombre") || ti.getColumns().get(j).equals("C\u00f3digo") || ti.getColumns().get(j).equals("Precio Coste") || ti.getColumns().get(j).equals("Precio Venta Base")){
                	 			Integer fila = i+1;
                	 			Integer columna = j+1;
                	 			verror.add("*Fila "+fila+", Columna "+columna+" : Dato Incorrecto \n");
                				textError= textError + "*Fila "+fila+", Columna "+columna+" : Dato Incorrecto \n";
                	 		}
                		 	j++;
                	 	}
                	 
                	 	pi = check(ti.getColumns().get(j),o,pi,cell.getCellType());
                	 	if(pi == null){
             				Integer fila = i+1;
             				Integer columna = j+1;
             				verror.add("*Fila "+fila+", Columna "+columna+" : Dato Incorrecto \n");
             				textError= textError + "*Fila "+fila+", Columna "+columna+" : Dato Incorrecto \n";
                			pi = newProduct();	
                	 	}
                	 }
                 }
                 j++;
             }
             if(j != ti.getColumns().size()){
            	 if(i == 0){
            		 error.setError(false);
            		 verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
            		 error.setTextError(verror);
            		 return error;
            	 }
            	 else{
            		 if(ti.getColumns().get(j).equals("Nombre") || ti.getColumns().get(j).equals("C\u00f3digo") || ti.getColumns().get(j).equals("Precio Coste") || ti.getColumns().get(j).equals("Precio Venta Base")){
            			Integer fila = i+1;
          				Integer columna = j+1;
          				verror.add("*Fila "+fila+", Columna "+columna+" : Dato Incorrecto \n");
          				textError= textError + "*Fila "+fila+", Columna "+columna+" : Dato Incorrecto \n";
            		 }
            	 }
             }
             // AÑADIR A PI ITEM --> %BENEFICIO SOBRE COSTE, %BENEFICIO SOBRE COMPRA Y PVP
           
             //pi.getItem().setExpensesPercent(0);
             //pi.getItem().setPrice(0);
            // checkPrices();
             if(i>0){ 
            	 if(textError.equals("")){ 	
            		 double profitPercent =((pi.getItem().getPrice()-pi.getItem().getPurchasePrice())/pi.getItem().getPurchasePrice())*100.00;
            		 pi.getItem().setProfitPercent(profitPercent);
            		 products.add(pi);
            	 }
             }
             i++;
		 }
		
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			String domain = AonUtil.getDomainName();
			try {
				DBConsults.insertProducts(domain,domainId,products);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        //insertar productos en base de datos.!!
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		System.out.println(error.getTextError());
		
		return error;
	}
	
	static final String COMMERCIAL_PRODUCT = ProductType.COMMERCIAL_PRODUCT.getName(new Locale("es_ES")); 
	static final String EXTERNAL_WORK = ProductType.EXTERNAL_WORK.getName(new Locale("es_ES"));
	static final String EXPENSE = ProductType.EXPENSE.getName(new Locale("es_ES"));
	static final String INCREASE = ProductType.INCREASE.getName(new Locale("es_ES"));
	static final String LABOUR = ProductType.LABOUR.getName(new Locale("es_ES"));
	static final String PREPAYMENT = ProductType.PREPAYMENT.getName(new Locale("es_ES"));
	static final String SERVICE = ProductType.SERVICE.getName(new Locale("es_ES"));

	private ProductInfo check(String template, Object value,ProductInfo product, Integer type) {
		
		String domain = AonUtil.getDomainName();
		switch (template) {
		case "Nombre": 
			if(type.equals(Cell.CELL_TYPE_STRING) && !value.equals(""))
				product.getProduct().setName((String) value);
			else return null;
			break;
		case "C\u00f3digo" : 
			if(type.equals(Cell.CELL_TYPE_STRING) && !value.equals(""))
				product.getProduct().setCode((String) value);
			else return null;
			break; 
		case "Precio Coste" : 
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				product.getItem().setPurchasePrice((double) value);
			else return null;
			break; 
		case "Precio Venta Base" :
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				product.getItem().setPrice((double) value);
			else return null;
			break; 
		case "Categor\u00eda" :
			if(type.equals(Cell.CELL_TYPE_STRING)){
				String strAux = (String) value;
				Vector<ProductCategory> v = null;
				try {
					v = DBConsults.getCategories(domain, domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Boolean b = true;
				for(ProductCategory pc : v){
					if(strAux.equalsIgnoreCase(pc.getName())){
						product.getProduct().setCategory(pc);
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
					v = DBConsults.getBrands(domain, domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Boolean b = true;
				for(Brand brand : v){
					if(strAux.equalsIgnoreCase(brand.getName())){
						product.getProduct().setBrand(brand);
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
					tags = DBConsults.getTags(domain, domainId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Vector<String> strings = tags((String)value);
				Set<ProductTag> tags2 = new HashSet<ProductTag>();
				for(ProductTag tag : tags){
					for(String s : strings){
						if(s.equalsIgnoreCase(tag.getTag().getName())){
							tags2.add(tag);
						}
					}
				}
				if(!tags2.isEmpty())
					product.getProduct().setTags(tags2);
				else return null;
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "Tipo":
			String t;
			if(type.equals(Cell.CELL_TYPE_STRING)){
				t = (String) value;
				if (t.equalsIgnoreCase(COMMERCIAL_PRODUCT))
					product.getProduct().setType(ProductType.COMMERCIAL_PRODUCT);
				else if(t.equalsIgnoreCase(SERVICE))
					product.getProduct().setType(ProductType.SERVICE);
				else if(t.equalsIgnoreCase(EXTERNAL_WORK))
					product.getProduct().setType(ProductType.EXTERNAL_WORK);
				else if(t.equalsIgnoreCase(LABOUR))
					product.getProduct().setType(ProductType.LABOUR);
				else if(t.equalsIgnoreCase(EXPENSE))
					product.getProduct().setType(ProductType.EXPENSE);
				else if(t.equalsIgnoreCase(INCREASE))
					product.getProduct().setType(ProductType.INCREASE);
				else if(t.equalsIgnoreCase(PREPAYMENT))
					product.getProduct().setType(ProductType.PREPAYMENT);
				else return null;
			}
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "IVA" : 
			Tax vat = new Tax();
			Vector<Tax> vats = new Vector<Tax>();
			try {
				vats = DBConsults.getIVA(domain, domainId);
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
			product.getProduct().setVat(vat);
			break; 
		case "IRPF" :
			Tax retention = new Tax();
			Vector<Tax> retentions = new Vector<Tax>();
			try {
				retentions = DBConsults.getRetentions(domain, domainId);
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
			product.getProduct().setRetention(retention);
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
			product.getProduct().setStatus(status);
			break; 
		case "C\u00f3digo de Barras" : 
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setBarcode((String) value);
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break; 
		case "Descripci\u00f3n" :
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setDescription((String)value);
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 1":
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setDetail((String)value);
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 2":
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setDetail2((String)value);
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		case "Detalle 3":
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setDetail3((String)value);
			else if(!type.equals(Cell.CELL_TYPE_BLANK)) return null;
			break;
		default:
			break;
		}
		
		return product;
	}
	
	private ProductInfo newProduct() {
		ProductInfo pi = new ProductInfo();
		Product p = new Product();
		Item i = new Item();
	
		// Categoría (product)
		p.setCategory(null);
		 
		// Marca (product)
		p.setBrand(null);
	
		// Etiqueta (product)
		p.setTags(null);
		
		// Tipo (product)
		p.setType(ProductType.COMMERCIAL_PRODUCT);
		
		// IVA (product)
		Tax vat = new Tax();
		vat.setName("GENERAL");
		vat.setType(TaxType.VAT);
		p.setVat(vat);
		
		// IRPF (product)
		Tax retention = new Tax();
		retention.setName("IRPF");
		retention.setType(TaxType.RETENTION);
		p.setRetention(retention);
		
		// Inventoriable (product)
		 p.setInventoriable(false);
		
		 // producto compuesto (product)
		p.setComposition(false);
		
		// precio composicion
		p.setCompositionPrice(false);
		
		// estado (product)
		p.setStatus(ProductStatus.ACTIVE);
		
		// barcode (item)
		i.setBarcode(null);
		
		// description (item)
		i.setDescription(null);
		
		// detail
		i.setDetail("");
		
		// detail2
		i.setDetail2("");
		
		// detail3
		i.setDetail3("");
		
		pi.setItem(i);
		pi.setProduct(p);
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
