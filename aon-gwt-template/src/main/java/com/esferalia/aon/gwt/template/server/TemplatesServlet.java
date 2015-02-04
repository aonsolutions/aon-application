package com.esferalia.aon.gwt.template.server;


import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.code.aon.common.enumeration.MimeType;
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
	
	public TemplateInfo newTemplate(TemplateInfo ti ){
		String domain = AonUtil.getDomainName();
		byte[] b = Utils.newXmlFile(ti);
		try {
			Integer id = DBConsults.insertTemplate(domain, ti, b,domainId);
			ti.setId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ti;
	}
	
	public void editTemplate(TemplateInfo ti){
		String domain = AonUtil.getDomainName();
		byte[] b = Utils.newXmlFile(ti);
		try {
			DBConsults.updateTemplate(domain, ti, domainId, b);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteTemplate(TemplateInfo ti){
		String domain = AonUtil.getDomainName();
		try {
			DBConsults.removeTemplate(domain, ti.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public com.esferalia.aon.gwt.template.shared.Error insertProducts(TemplateInfo ti) {
		//Archivo Excel
		com.esferalia.aon.gwt.template.shared.Error error = new Error();
		if(!getMimetype().equals(MimeType.MIME_MS_EXCEL.getName())
			&& !getMimetype().equals(MimeType.MIME_MS_EXCEL_2007.getName())){
			//El archivo no es un fichero Excel.
			error.setError(false);
			error.setTextError("*El archivo importado nos es de tipo excel.");
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
                	 if(ti.getColumns().get(j) == null || !ti.getColumns().get(j).equalsIgnoreCase(cell.getStringCellValue())){
                		 // El archivo no es compatible con la plantilla
             			error.setError(false);
             			error.setTextError("*El archivo importado no es compatible con la plantilla seleccionada.");
             			return error;
                	 } 
                 }
                 else{
                	 pi = check(ti.getColumns().get(j),o,pi,cell.getCellType());
                	 if(pi == null){
                		 textError="*Fila "+i+", Columna "+j+" : Dato Incorrecto \n";
                	 }
                 }
                 j++;
             }
             // AÑADIR A PI ITEM --> %BENEFICIO SOBRE COSTE, %BENEFICIO SOBRE COMPRA Y PVP
             pi.getItem().setProfitPercent(0);
             pi.getItem().setExpensesPercent(0);
             pi.getItem().setPrice(0);
             if(i>0) products.add(pi);
             i++;
		 }
		
		if(textError.equals("")){
			error.setError(true);
			error.setTextError("");
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
 			error.setTextError(textError);
		}
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
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getProduct().setName((String) value);
			else return null;
			break;
		case "C\u00f3digo" : 
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getProduct().setCode((String) value);
			else return null;
			break; 
		case "Precio Coste" : 
			//TODO 
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				product.getItem().setExpensesFixed((double) value);
			else return null;
			break; 
		case "Precio Venta Base" :
			if(type.equals(Cell.CELL_TYPE_NUMERIC))
				product.getItem().setPurchasePrice((double) value);
			else return null;
			break; 
		case "Categor\u00eda" :
			if(type.equals(Cell.CELL_TYPE_STRING)){
				ProductCategory category = new ProductCategory();
				category.setName((String)value);
				product.getProduct().setCategory(category);
			}
			else return null;
			break; 
		case "Marca" : 
			if(type.equals(Cell.CELL_TYPE_STRING)){
				Brand brand = new Brand();
				brand.setName((String) value);
				product.getProduct().setBrand(brand);
			}
			else return null;
			break; 
		case "Etiqueta" : 
			//TODO product.getProduct().setTags(tags);
			break; 
		case "Tipo":
			String t;
			if(type.equals(Cell.CELL_TYPE_STRING))
				t = (String) value;
			else return null;
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
				for(Tax tax : vats){
					if(s.equalsIgnoreCase((tax.getName()))){
						vat = tax;
					}
					else return null;
				}
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Float f = (Float) value;
				for(Tax tax : vats){
					if(f.equals(tax.getPercentage())){
						vat = tax;
					}
					else return null;
				}
				break;
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
				for(Tax tax : retentions){
					if(s.equalsIgnoreCase((tax.getName()))){
						retention = tax;
					}
					else return null;
				}
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Float f = (Float) value;
				for(Tax tax : retentions){
					if(f.equals(tax.getPercentage())){
						retention = tax;
					}
					else return null;
				}
				break;
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
				bool = string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true");
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Integer num = (Integer) value;
				bool = num.equals(1);
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool = (Boolean) value;
				break;
			default:
				return null;
			}
			product.getProduct().setInventoriable(bool);
			break; 
		case "Producto Compuesto" : 
			Boolean bool2 = false;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String str = (String) value;
				bool2 = str.equalsIgnoreCase("si") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true");
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Integer num = (Integer) value;
				bool2 = num.equals(1);
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool2 = (Boolean) value;
				break;
			default:
				return null;
			}
			product.getProduct().setComposition(bool2);
			break;  
		case "Precio Composici\u00f3n" :
			Boolean bool3 = false;
			switch (type) {
			case Cell.CELL_TYPE_STRING:
				String str = (String) value;
				bool3 = str.equalsIgnoreCase("si") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true");
				break;
			case Cell.CELL_TYPE_NUMERIC:
				Integer num = (Integer) value;
				bool3 = num.equals(1); 
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				bool3 = (Boolean) value;
				break;
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
				Integer num = (Integer) value;
				if(num.equals(1))
					status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				Boolean b = (Boolean) value;
				if(b) status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			default:
				return null;
			}
			product.getProduct().setStatus(status);
			break; 
		case "C\u00f3digo de Barras" : 
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setBarcode((String) value);
			else return null;
			break; 
		case "Descripci\u00f3n" :
			if(type.equals(Cell.CELL_TYPE_STRING))
				product.getItem().setDescription((String)value);
			else return null;
			break;
		default:
			break;
		}
		//TODO 
		
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
		
		pi.setItem(i);
		pi.setProduct(p);
		return pi;
	}
}
