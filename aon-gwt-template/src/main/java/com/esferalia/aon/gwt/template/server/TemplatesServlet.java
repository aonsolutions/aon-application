package com.esferalia.aon.gwt.template.server;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.jooq.DBCatalogue;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.jooq.DBConsumption;
import com.esferalia.aon.gwt.template.jooq.DBFee;
import com.esferalia.aon.gwt.template.jooq.DBProduct;
import com.esferalia.aon.gwt.template.jooq.DBStock;
import com.esferalia.aon.gwt.template.server.delivery.DeliveryImport;
import com.esferalia.aon.gwt.template.server.delivery.DeliveryInfo;
import com.esferalia.aon.gwt.template.server.imports.DiaryImport;
import com.esferalia.aon.gwt.template.server.imports.InvoiceImport;
import com.esferalia.aon.gwt.template.server.imports.InvoiceImportClass;
import com.esferalia.aon.gwt.template.server.imports.PGCImport;
import com.esferalia.aon.gwt.template.server.imports.RegistryImport;
import com.esferalia.aon.gwt.template.server.imports.DiaryImport.AccountEntryImportClass;
import com.esferalia.aon.gwt.template.server.imports.PGCImport.AccountImportClass;
import com.esferalia.aon.gwt.template.server.imports.RegistryImport.RegistryImportClass;
import com.esferalia.aon.gwt.template.server.marketplace.XMLUtils;
import com.esferalia.aon.gwt.template.server.projectCommercial.CustomerIban;
import com.esferalia.aon.gwt.template.server.projectCommercial.CustomerIbanImport;
import com.esferalia.aon.gwt.template.server.projectCommercial.ProjectCommercialImport;
import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct.ProductData;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct.ProductData.Ecommerce.PresetValues;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct.Template;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Hotel;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.io.AonIOUtils;



public class TemplatesServlet extends AonStatelessRemoteServiceServlet implements ITemplate{

	private static final long serialVersionUID = 6871016881549113129L;

	static final String COMMERCIAL_PRODUCT = ProductType.COMMERCIAL_PRODUCT.getName();
	static final String EXTERNAL_WORK = ProductType.EXTERNAL_WORK.getName();
	static final String EXPENSE = ProductType.EXPENSE.getName();
	static final String INCREASE = ProductType.INCREASE.getName();
	static final String LABOUR = ProductType.LABOUR.getName();
	static final String PREPAYMENT = ProductType.PREPAYMENT.getName();
	static final String SERVICE = ProductType.SERVICE.getName();

	static final String NO_PERIOD = "Sin Periodo" ;//BillingPeriod.NO_PERIOD.getName();
	static final String MONTHLY = "Mensual";// BillingPeriod.MONTHLY.getName();
	static final String BI_MONTHLY = "Bimestral";// BillingPeriod.BI_MONTHLY.getName();
	static final String THREE_MONTHLY ="Trimestral";// com.code.aon.finance.enumeration.BillingPeriod.THREE_MONTHLY.getName();
	static final String FOUR_MONTHLY = "Cuatrimestral";//BillingPeriod.FOUR_MONTHLY.getName();
	static final String SIX_MONTHLY = "Semestral";///BillingPeriod.SIX_MONTHLY.getName();
	static final String YEARLY = "Anual";//BillingPeriod.YEARLY.getName();

	HashMap<String, ProductInfo> map = new HashMap<String, ProductInfo>();
	public static byte[] out;
	static Integer size;
	private static String mimetype;

	public static TemplatesServlet getInstance() {
		return new TemplatesServlet();
	}

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

	@Override
	public LinkedList<TemplateInfo> getTemplates(Domain domain, User user){
		return DBConsults.getTemplates(domain, user.getLogin());
	}
	
	@Override
	public TemplateInfo newTemplate(Domain domain, User user, TemplateInfo ti ){
		byte[] b = Utils.newXmlFile(ti);
		Integer id = DBConsults.insertTemplate(domain, ti, b, user.getLogin());
		ti.setId(id);
		ti.setIsParent(false);

		return ti;
	}

	@Override
	public TemplateInfo editTemplate(Domain domain, User user, TemplateInfo ti){
		byte[] b = Utils.newXmlFile(ti);
		DBConsults.updateTemplate(domain, ti, b, user.getLogin());
		return ti;
	}

	@Override
	public void deleteTemplate(Domain domain, User user, TemplateInfo ti){
		DBConsults.removeTemplate(domain, user, ti.getId());
	}
	
	@Override
	public LinkedList<Hotel> getHotelsToConsumption(Domain domain, User user){
		return DBConsults.getHotels(domain, user);
	}

	@Override
	public LinkedList<Hotel> getWorkplacesToConsumption(Domain domain, User user){
		return DBConsults.getWorkplaces(domain, user);
	}

	@Override
	public LinkedList<Warehouse> getWarehousesToConsumption(Domain domain, User user, Integer workplaceId){
		return DBStock.getInstance().getWarehouse(domain, user, workplaceId);
	}

	public LinkedList<Warehouse> getWarehousesToConsumption(Domain domain, User user){
		LinkedList<Warehouse> v = DBStock.getInstance().getWarehouse(domain, user);
		LinkedList<Warehouse> v2 = new LinkedList<Warehouse>();
		for (Warehouse w : v) {
			ConsumptionItem ci = DBConsumption.getTwoLastInventory(domain, w.getId(), user.getLogin());
			if(ci.getInitialId() != null && ci.getFinalId() != null)
				v2.add(w);
		}
		return v2;
	}

	public LinkedList<Warehouse> getWarehouses(Domain domain, User user){
		return DBStock.getInstance().getWarehouse(domain, user);
	}


	//-------------------- IMPORTAR
	LinkedList<ProjectCommercial> pcs;
	LinkedList<CustomerIban> cis;
	LinkedList<InvoiceImportClass> ivs;
	LinkedList<RegistryImportClass> rvs;
	LinkedList<AccountEntryImportClass> dvs;
	LinkedList<AccountImportClass> accounts;
 	DeliveryInfo di;
	LinkedList<String> verror;
	public Integer executeExcel(Domain domain, User user, TemplateInfo ti, ImportType importType, Boolean ignoreInactiveClient,
		Integer inventory, String warehouse1,String warehouse2 , String series, String comments,Boolean istransfer ,Integer number){

		this.ti = ti;
		error = new Error();
		verror = new LinkedList<String>();
		error.setTextError(verror);
		textError = "";

		com.esferalia.aon.gwt.template.shared.Error error = new Error();

		if(getOut() == null){
			error.setError(false);
 			textError =  textError + "*No ha importado ning�n archivo.\n";
			verror.add("*No ha importado ning�n archivo.");
			error.setTextError(verror);
			this.error = error;
			return -1;
		}

		Iterator<Row> rowIterator;
		try {
			byte[] data = getOut();
			ByteArrayInputStream bais = new ByteArrayInputStream(data);

			HSSFWorkbook workbook = new HSSFWorkbook(bais);
			HSSFSheet sheet = workbook.getSheetAt(0);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			rowCount  = sheet.getPhysicalNumberOfRows();
			rowIterator = sheet.iterator();

			if(importType.equals(ImportType.PRODUCT))
				executeExcelProduct(domain, user, rowIterator, error, evaluator);
			else if(importType.equals(ImportType.FEE))
				executeExcelFee(domain, user, rowIterator, error, ignoreInactiveClient, evaluator);
			else if(importType.equals(ImportType.PROPOSAL))
				executeExcelProposal(domain, user, rowIterator, error);
			else if(importType.equals(ImportType.STOCK))
				executeExcelStock(domain, user, rowIterator, error, inventory, warehouse1, warehouse2, series, comments, istransfer, number);
			else if(ImportType.DELIVERY.equals(importType)) {
				di = DeliveryImport.getInstance().importation(data);
			}
			else if(ImportType.PROJECT_COMMERCIAL.equals(importType)) {
				pcs = ProjectCommercialImport.getInstance().importation(domain, user.getLogin(), data);
			}
			else if(ImportType.CUSTOMER_IBAN.equals(importType)) {
				cis = CustomerIbanImport.getInstance().importation(domain, user.getLogin(), data);
			}
			else if(ImportType.INVOICE.equals(importType)) {
				ivs = InvoiceImport.getInstance().importation(domain, user.getLogin(), data);
			}
			else if(ImportType.REGISTRY.equals(importType)) {
				rvs = RegistryImport.getInstance().importation(domain, user.getLogin(), data);
			}
			else if(ImportType.DIARY.equals(importType)) {
				dvs = DiaryImport.getInstance().importation(domain, user.getLogin(), data);
			}
			else if(ImportType.PGC.equals(importType)) {
				accounts = PGCImport.getInstance().importation(domain, user.getLogin(), data);
			}

			workbook.close();
		} catch (IOException e) {
			//El archivo no es un fichero Excel.
			error.setError(false);
 			textError =  textError + "*El archivo importado no es de tipo excel.\n";
			verror.add("*El archivo importado no es de tipo excel.");
			error.setTextError(verror);
			this.error = error;
			e.printStackTrace();
			return -1;
		} catch (OfficeXmlFileException e){
			try {
				byte[] data = getOut();
				ByteArrayInputStream bais = new ByteArrayInputStream(data);

				XSSFWorkbook workbook = new XSSFWorkbook(bais);
				XSSFSheet sheet = workbook.getSheetAt(0);
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				rowCount  = sheet.getPhysicalNumberOfRows();
				rowIterator = sheet.iterator();

				if(importType.equals(ImportType.PRODUCT))
					executeExcelProduct(domain, user,  rowIterator, error, evaluator);
				else if(importType.equals(ImportType.FEE))
					executeExcelFee(domain, user, rowIterator, error, ignoreInactiveClient, evaluator);
				else if(importType.equals(ImportType.PROPOSAL))
					executeExcelProposal(domain, user, rowIterator, error);
				else if(importType.equals(ImportType.STOCK))
					executeExcelStock(domain, user, rowIterator, error, inventory, warehouse1, warehouse2, series, comments, istransfer, number);
				else if(ImportType.DELIVERY.equals(importType)) {
					di = DeliveryImport.getInstance().importationX(data);
				}
				else if(ImportType.PROJECT_COMMERCIAL.equals(importType)) {
					pcs = ProjectCommercialImport.getInstance().importationX(domain, user.getLogin(), data);
				}
				else if(ImportType.CUSTOMER_IBAN.equals(importType)) {
					cis = CustomerIbanImport.getInstance().importationX(domain, user.getLogin(), data);
				}
				else if(ImportType.INVOICE.equals(importType)) {
					ivs = InvoiceImport.getInstance().importationX(domain, user.getLogin(), data);
				}
				else if(ImportType.REGISTRY.equals(importType)) {
					rvs = RegistryImport.getInstance().importationX(domain, user.getLogin(), data);
				}
				else if(ImportType.DIARY.equals(importType)) {
					dvs = DiaryImport.getInstance().importationX(domain, user.getLogin(), data);
				}
				else if(ImportType.PGC.equals(importType)) {
					accounts = PGCImport.getInstance().importationX(domain, user.getLogin(), data);
				}
				
				workbook.close();
			} catch (IOException e1) {
					//El archivo no es un fichero Excel.
					error.setError(false);
		 			textError =  textError + "*El archivo importado no es de tipo excel.\n";
					verror.add("*El archivo importado no es de tipo excel.");
					error.setTextError(verror);
					this.error = error;
					e.printStackTrace();
					return -1;
			}
		}
		return rowCount;
	}

	//-------------------- IMPORTAR FEE
	LinkedList<FeeInfo> fees;
	FeeInfo fi;
	List<Seller> sellers = new LinkedList<Seller>();
	LinkedList<Workplace> workplaces = new LinkedList<Workplace>();
	LinkedList<InvoicingGroup> invoicingGroupList = new LinkedList<InvoicingGroup>();
	Boolean feeBool;
	private void executeExcelFee(final Domain domain, User user, Iterator<Row> rowIterator, Error error, Boolean ignoreInactiveClient, FormulaEvaluator evaluator){
		LinkedList<FeeInfo> fees = new LinkedList<FeeInfo>();

		sellers = DBFee.getInstance().getSellers(domain, user.getLogin());
		workplaces = DBFee.getInstance().getWorkplaceList(domain, user);
		invoicingGroupList = DBFee.getInstance().getInvoicingGroupList(domain, user);

		feeBool = true;
		/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		rowStream.forEach(row ->{
			if(row.getRowNum() !=0){
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				fi = newFee();
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				rowAux = row;
				cellStream.forEach(cell ->{
					if(cell.getColumnIndex() != ti.getColumns().size()){
						if(cell.getRowIndex() == 1){//Primera fila del fichero Excel.
							if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue())){
								// El archivo no es compatible con la plantilla
								error.setError(false);
								if(verror.isEmpty()) verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
								textError= textError +"*El archivo importado no es compatible con la plantilla seleccionada.";
								error.setTextError(verror);
								this.error = error;
								rowCount = -1;
								feeBool = false;
							}
						}
						else if(feeBool){
							if(cell.getColumnIndex() !=0){
								Cell beforeCell = rowAux.getCell(cell.getColumnIndex()-1);
								if((beforeCell == null || beforeCell.getCellTypeEnum() == CellType.BLANK) && isRequiredFee(ti.getColumns().get(cell.getColumnIndex()-1))){
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
								fi = checkFee(domain, user, ti.getColumns().get(cell.getColumnIndex()),fi,cell, ignoreInactiveClient, evaluator);
								if(fi == null){
									verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto ");
									textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
									fi = newFee();
								}
							}
						}
					}
				});
				if(row.getLastCellNum() != ti.getColumns().size()+1){
					if(row.getRowNum() == 1){
						error.setError(false);
						if(verror.isEmpty()) verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
						textError= textError +"*El archivo importado no es compatible con la plantilla seleccionada.";
						error.setTextError(verror);
						this.error = error;
						rowCount = -1;
						feeBool = false;
					}
					else if(feeBool){
						if(row.getLastCellNum() != -1){
							Short cellnum = row.getLastCellNum();
							if(row.getLastCellNum() == ti.getColumns().size())cellnum--;
							if(ti.getColumns().get(cellnum).equals("Nombre") || ti.getColumns().get(cellnum).equals("C\u00f3digo") || ti.getColumns().get(cellnum).equals("Precio Coste") || ti.getColumns().get(cellnum).equals("Precio Venta Base")){
								verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
								textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
							}
						}
					}
				}

				if(row.getRowNum() > 1 && fi.getProduct()!= null){
					fi.setRow(row.getRowNum());
					fees.add(fi);
				}
			}
		});

		this.fees = fees;
		if(rowCount != -1) rowCount = fees.size();
		setOut(null);
		setMimetype(null);

	}

	public Boolean isRequiredFee(String s){
		return s.equals("Cliente") || s.equals("Producto") || s.equals("Cantidad") || s.equals("Precio")
				|| s.equals("Descuento") || s.equals("Fecha Inicio") || s.equals("Fecha Facturaci\u00f3n" )
				|| s.equals("Centro de Trabajo") ||  s.equals("Centro Trabajo") ;
	}

	public Error insertFee(Domain domain, User user) {
		LinkedList<String> verror = error.getTextError();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			error = DBFee.getInstance().insertFee(domain, user.getLogin(), fees);
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
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

	private FeeInfo checkFee(Domain domain, User user, String template,FeeInfo fee, Cell cell, Boolean ignoreInactiveCliente, FormulaEvaluator evaluator) {
		Integer row = cell.getRowIndex()+1;
		String column = Utils.getColumn(cell.getColumnIndex());
		String username = user.getLogin();
		CellType type = cell.getCellTypeEnum();
		Object value = getObjectValue(cell, evaluator);
		switch (template) {
		case "Cliente": case "Client":
			if(type.equals(CellType.STRING) && !cell.getStringCellValue().equals("")){
				Customer customer = DBFee.getInstance().getCustomer(domain, username, cell.getStringCellValue(), ignoreInactiveCliente);
				if(customer != null){
					fee.setClient(cell.getStringCellValue());
					fee.setClientId(customer.getId());
				}
				else return null;
			}
			else return null;
			break;
		case "Producto": case "Product":
			if((type.equals(CellType.STRING) && !cell.getStringCellValue().equals("")) || type.equals(CellType.NUMERIC)){
				fee.setProduct(toString(value));
				if(fee.getProduct().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			break;
		case "Cantidad": case "Quantity":
			if(type.equals(CellType.NUMERIC)){
				fee.setQuantity(cell.getNumericCellValue());
			}
			else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Precio": case "Price":
			if(type.equals(CellType.NUMERIC))
				fee.setPrice(cell.getNumericCellValue());
			else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}			break;
		case "Descuento": case "Discount":
			if(type.equals(CellType.NUMERIC))
				fee.setDiscount(cell.getNumericCellValue());
			else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Fecha Inicio": case "Start Date":
			if(type.equals(CellType.STRING)){
				Date d = Utils.stringToDate(cell.getStringCellValue());
				if(d != null) fee.setStartDate(d);
				else return null;
			}
			else if(type.equals(CellType.NUMERIC)){
				fee.setStartDate(cell.getDateCellValue());
			}
			else return null;
			break;
		case "Fecha Fin": case "End Date":
			if(type.equals(CellType.STRING)){
				Date d = Utils.stringToDate(cell.getStringCellValue());
				if(d != null) fee.setEndDate(d);
			}
			else if(type.equals(CellType.NUMERIC)){
				fee.setEndDate(cell.getDateCellValue());
			}
			break;
		case "Fecha Facturaci\u00f3n": case "Billing Date":
			if(type.equals(CellType.STRING)){
				Date d = Utils.stringToDateBilling(cell.getStringCellValue());
				if(d != null) fee.setBillingDate(d);
				else return null;
			}
			else if(type.equals(CellType.NUMERIC)){
				fee.setBillingDate(cell.getDateCellValue());
			}
			else return null;
			break;
		case "Periodo": case "period": //enum
				String t = toString(value);
				if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
					if (t.equalsIgnoreCase(NO_PERIOD) || t.equals("0"))
						fee.setPeriod(BillingPeriod.NO_PERIOD.ordinal());
					else if(t.equalsIgnoreCase(MONTHLY)  || t.equals("1"))
						fee.setPeriod(BillingPeriod.MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(BI_MONTHLY)  || t.equals("2"))
						fee.setPeriod(BillingPeriod.BI_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(THREE_MONTHLY) || t.equals("3"))
						fee.setPeriod(BillingPeriod.THREE_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(FOUR_MONTHLY)  || t.equals("4"))
						fee.setPeriod(BillingPeriod.FOUR_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(SIX_MONTHLY)  || t.equals("5"))
						fee.setPeriod(BillingPeriod.SIX_MONTHLY.ordinal());
					else if(t.equalsIgnoreCase(YEARLY)  || t.equals("6"))
						fee.setPeriod(BillingPeriod.YEARLY.ordinal());
					else return null;
				}

			break;
		case "Comercial": case "Seller": //bd
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Boolean b = true;
				for(Seller s : sellers){
					if(toString(value).equalsIgnoreCase(s.getRegistryDocument()) || toString(value).equalsIgnoreCase(s.getRegistryAlias()) || toString(value).equalsIgnoreCase(s.getRegistryName())){
						fee.setSeller(cell.getStringCellValue());
						fee.setSellerId(s.getId());
						b= false;
					}
				}
				if(b) return null;
			}
			else return null;
			break;
		case "Centro de Trabajo": case "Workplace": case "Centro Trabajo": //bd
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Boolean b = true;
				for(Workplace s : workplaces){
					if(toString(value).equalsIgnoreCase(s.getDescription())){
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
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Boolean b = true;
				for(InvoicingGroup s : invoicingGroupList){
					if(toString(value).equalsIgnoreCase(s.getDescription())){
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
			case STRING:
				String string = cell.getStringCellValue();
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool2 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool2 = false;
				else return null;
				break;
			case NUMERIC:
				Double num = cell.getNumericCellValue();
				if(num.equals(1.0)) bool2 = true;
				else if(num.equals(0.0)) bool2 = false;
				else return null;
				break;
			case BOOLEAN:
				bool2 = cell.getBooleanCellValue();
				break;
			case BLANK:
				return fee;
			default:
				return null;
			}
			fee.setConfidential(bool2);
			break;
		case "Expediente": case "Record":  //BD
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Project project = DBFee.getInstance().getProject(domain, toString(value), fee.getClientId(), user.getLogin());
				if(project == null && fee.getClientId() != null){
					project = new Project().setId(DBFee.getInstance().insertProject(domain, user, toString(value), fee.getClientId()));
				}
				if(project != null){
					fee.setProject(cell.getStringCellValue());
					fee.setProjectId(project.getId());
				}
			}
			else return null;
			break;
		case "Detalle 1": case "Detail 1":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC))
				fee.setDetail(toString(value));
			break;
		case "Detalle 2": case "Detail 2":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC))
				fee.setDetail2(toString(value));
			break;
		case "Detalle 3": case "Detail 3":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC))
				fee.setDetail3(toString(value));
			break;
		case "Descripci\u00f3n":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC))
				fee.setDescription(toString(value));
			break;
		case "L\u00EDnea": case "Line":
			if(type.equals(CellType.NUMERIC))
				fee.setLine(cell.getNumericCellValue());
			else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
		default:
			break;
		}
		return fee;
	}

	//-------------------- IMPORTAR STOCK
	LinkedList<StockInfo> stock;
	TransferInfo transferInfo;
	String textError;
	Error error;
	Integer rowCount;
	StockInfo si;
	TemplateInfo ti;
	Row rowAux ;
	Boolean proposalBool;
	public Integer executeExcelProposal(Domain domain, User user, Iterator<Row> rowIterator, Error error){
		LinkedList<StockInfo> stock = new LinkedList<StockInfo>();
		proposalBool = true;
		/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		rowStream.forEach(row ->{
			if(row.getRowNum() !=0){
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				si = newStock();
				rowAux = row;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				cellStream.forEach(cell ->{
					if(cell.getColumnIndex() != ti.getColumns().size()){

						Object object = null ;
						switch (cell.getCellTypeEnum()) {
							case BLANK:
								break;
							case BOOLEAN:
								object = cell.getBooleanCellValue();break;
							case ERROR:
								object = cell.getErrorCellValue();break;
							case FORMULA:
								break;
							case NUMERIC:
								object = cell.getNumericCellValue();break;
							case STRING:
								object = cell.getStringCellValue();break;
							default:
								break;
						}
						if(cell.getRowIndex() == 1){//Primera fila del fichero Excel.
							if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue())){
								// El archivo no es compatible con la plantilla
								error.setError(false);
								textError =  textError + "*El archivo importado no es compatible con la plantilla seleccionada.\n";
								if(verror.isEmpty()) verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
								error.setTextError(verror);
								this.error = error;
								rowCount = -1;
								proposalBool = false;
							}
						}
						else if(proposalBool){
							if(cell.getColumnIndex() !=0){
								Cell beforeCell = rowAux.getCell(cell.getColumnIndex()-1);
								if((beforeCell == null || beforeCell.getCellTypeEnum() == CellType.BLANK) && isRequiredStock(ti.getColumns().get(cell.getColumnIndex()-1))){
									if(beforeCell == null){
										textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto \n";
										verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto");
										error.setTextError(verror);
										this.error = error;
									}
									else if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Producto") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Almac\u00e9n Destino") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Series")){
										textError= textError + "*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n";
										verror.add("*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n");
										error.setTextError(verror);
										this.error = error;
									}
								}
							}
							if(ti.getColumns().get(cell.getColumnIndex()).equals("Cantidad") || ti.getColumns().get(cell.getColumnIndex()).equals("Producto")
									|| ti.getColumns().get(cell.getColumnIndex()).equals("Detalle 1") || ti.getColumns().get(cell.getColumnIndex()).equals("Detalle 2")
									|| ti.getColumns().get(cell.getColumnIndex()).equals("Detalle 3")){
								si = check(domain, user, cell.getRowIndex()+1, Utils.getColumn(cell.getColumnIndex()),ti.getColumns().get(cell.getColumnIndex()),object,si,cell.getCellTypeEnum());
								if(si == null){
									textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
									verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n");
									error.setTextError(verror);
									this.error = error;
									si = newStock();
								}
							}
						}
					}
				});

				if(row.getLastCellNum() != ti.getColumns().size()+1){
					if(row.getRowNum() == 1){
						error.setError(false);
						textError= textError + "*El archivo importado no es compatible con la plantilla seleccionada. \n ";
	            		if(verror.isEmpty()) verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
	            		error.setTextError(verror);
	            		this.error = error;
	            		rowCount = -1;
	            		proposalBool = false;
	            	}
	           		else if(proposalBool){
	           			if(row.getLastCellNum() != -1){
	           				Short cellnum = row.getLastCellNum();
	           				if(row.getLastCellNum() == ti.getColumns().size())cellnum--;
	           				if(ti.getColumns().get(cellnum).equals("Producto") || ti.getColumns().get(cellnum).equals("Almac\u00e9n Destino")){
	           					verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
	          					error.setTextError(verror);
	          					this.error = error;
	          					textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
	           				}
	           			}
	           		}
				}

				if(row.getRowNum() > 1){
					if(si.getProduct() != null && si.getQuantity() != null
							&& si.getQuantity() != 0){
						si.setRow(row.getRowNum());
	           			stock.add(si);
					}
	        	}
			}
		});
		this.stock = stock;
		if(rowCount != -1) rowCount = stock.size();
		setOut(null);setMimetype(null);
		return rowCount;
	}

	public Error insertProposal(Domain domain, User user, Integer proposal, Integer workplace){
		LinkedList<String> verror = error.getTextError();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			error = DBStock.getInstance().insertProposal(domain, user.getLogin(), stock,proposal,workplace);
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		return error;
	}

	Integer inventoryId ;
	Boolean transfer;
	Map<String, StockInfo> stockMap;
	Boolean stockBool;
	public Integer executeExcelStock(Domain domain, User user, Iterator<Row> rowIterator, Error error, Integer inventory, String warehouse1,String warehouse2 , String series, String comments,Boolean istransfer ,Integer number){
		transfer = istransfer;
		inventoryId = inventory;
		LinkedList<StockInfo> stock = new LinkedList<StockInfo>();
		Map<String, StockInfo> stockMap =  new HashMap<String, StockInfo>();

		if((warehouse1 == null || warehouse1.equals("-")) && (warehouse2 == null || warehouse2.equals("-"))){
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

		Integer domId;
		if (ti.getDomainId().equals(0)) domId = domain.getId();
		else domId = ti.getDomainId();
		// DBSTOCK GET WAREHOUSE TARDA!! EL 1
		if(warehouse1 != null && !warehouse1.equals("-")) w = DBStock.getInstance().getWarehouse(new Domain().setId(domId).setName(domain.getName()), user, warehouse1);
		if(warehouse2 != null && !warehouse2.equals("-")) 	w2 = DBStock.getInstance().getWarehouse(new Domain().setId(domId).setName(domain.getName()), user, warehouse2);
		s = DBStock.getInstance().getSeries(domain, series, user.getLogin());

    	//Boolean b = true;

		transferInfo = new TransferInfo();
		if(!warehouse1.equals("-")) transferInfo.setTargetWarehouse(w); else transferInfo.setTargetWarehouse(null);
		if(warehouse2 != null && !warehouse2.equals("-")) transferInfo.setSourceWarehouse(w2); else transferInfo.setSourceWarehouse(null);
		transferInfo.setSeries(s);
		transferInfo.setComments(comments);
		if(istransfer) transferInfo.setNumber(number);

		stockBool = true;
	 	/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		rowStream.forEach(row ->{
			if(row.getRowNum() !=0){
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				si = newStock();
				rowAux = row;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				cellStream.forEach(cell ->{
					if(cell.getColumnIndex() != ti.getColumns().size()){

						Object object = null ;
						switch (cell.getCellTypeEnum()) {
							case BLANK:
								break;
							case BOOLEAN:
								object = cell.getBooleanCellValue();break;
							case ERROR:
								object = cell.getErrorCellValue();break;
							case FORMULA:
								break;
							case NUMERIC:
								object = cell.getNumericCellValue();break;
							case STRING:
								object = cell.getStringCellValue();break;
							default:
								break;
						}
						if(cell.getRowIndex() == 1){//Primera fila del fichero Excel.
							if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue())){
								// El archivo no es compatible con la plantilla
								error.setError(false);
								textError =  textError + "*El archivo importado no es compatible con la plantilla seleccionada.\n";
								if(verror.isEmpty())verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
								error.setTextError(verror);
								this.error = error;
								rowCount = -1;
								stockBool = false;
							}
						}
						else if(stockBool && ti.getColumns().size() >= cell.getColumnIndex()){
							if(cell.getColumnIndex() !=0){
								if(cell.getColumnIndex()> 2) {
									System.out.println("VA A FALLAR!");
								}
								Cell beforeCell = rowAux.getCell(cell.getColumnIndex()-1);
								if((beforeCell == null || beforeCell.getCellTypeEnum() == CellType.BLANK) && isRequiredStock(ti.getColumns().get(cell.getColumnIndex()-1))){
									if(beforeCell == null){
										if(ti.getColumns().get(cell.getColumnIndex()-1).equals("Producto") || ti.getColumns().get(cell.getColumnIndex()-1).equals("Almac\u00e9n Destino") ||  ti.getColumns().get(cell.getColumnIndex()-1).equals("Series")){
											textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto \n";
											verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn((cell.getColumnIndex()-1))+" : Dato Incorrecto");
											error.setTextError(verror);
											this.error = error;
										}
									}
									else if(ti.getColumns().get(beforeCell.getColumnIndex()).equals("Producto") || ti.getColumns().get(beforeCell.getColumnIndex()).equals("Almac\u00e9n Destino") ||  ti.getColumns().get(beforeCell.getColumnIndex()).equals("Series")){
										textError= textError + "*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n";
										verror.add("*Fila "+(beforeCell.getRowIndex()+1)+", Columna "+Utils.getColumn(beforeCell.getColumnIndex())+" : Dato Incorrecto \n");
										error.setTextError(verror);
										this.error = error;
									}

								}
							}
							if(!ti.getColumns().get(cell.getColumnIndex()).equals("Texto Libre") && !ti.getColumns().get(cell.getColumnIndex()).equals("Nombre")){
								si = check(domain, user, cell.getRowIndex()+1, Utils.getColumn(cell.getColumnIndex()),ti.getColumns().get(cell.getColumnIndex()),object,si,cell.getCellTypeEnum());
								if(si == null){
									textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
									verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n");
									error.setTextError(verror);
									this.error = error;
									si = newStock();
								}
							}
						}
					}
				});
				if(row.getLastCellNum() != ti.getColumns().size()+1){
					if(row.getRowNum() == 1){
						error.setError(false);
						textError= textError + "*El archivo importado no es compatible con la plantilla seleccionada. \n ";
						if(verror.isEmpty())verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
						error.setTextError(verror);
						this.error = error;
						rowCount = -1;
						stockBool = false;
					}
					else if(stockBool && ti.getColumns().size() >= row.getLastCellNum()){
						if(row.getLastCellNum() != -1){
							Short cellnum = row.getLastCellNum();
							if(row.getLastCellNum() == ti.getColumns().size())cellnum--;
							if(ti.getColumns().get(cellnum).equals("Producto") || ti.getColumns().get(cellnum).equals("Almac\u00e9n Destino") ){
								verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
								error.setTextError(verror);
								this.error = error;
								textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
							}
						}
					}
				}

				if(row.getRowNum() > 1){
					si.setRow(row.getRowNum());
					if((si.getQuantity() != null && !transfer)
						|| (transfer && si.getQuantity() != null && si.getQuantity()!= 0)){
						String key = si.getProduct() + si.getItem().getDetails()
							+ (si.getItem().getSerialNumber() != null ? si.getItem().getSerialNumber() : "")
							+ (si.getItem().getDetail() != null ? si.getItem().getDetail() : "")
							+ (si.getItem().getDetail2() != null ? si.getItem().getDetail2() : "")
							+ (si.getItem().getDetail3() != null ? si.getItem().getDetail3() : "");
						stock.add(si);
						if(stockMap.containsKey(key))
							stockMap.get(key).setQuantity(si.getQuantity() + stockMap.get(key).getQuantity());
						else stockMap.put(key, si);
					}
				}
			}
		});
		this.stock = stock;
		this.stockMap = stockMap;
		if(rowCount != -1) rowCount = stock.size();
		setOut(null);setMimetype(null);

		return rowCount;
	}
	public Boolean isRequiredStock(String s){
		return s.equals("Producto") ||s.equals("Cantidad");
	}
	public Error insertStock(Domain domain, User user) {
		LinkedList<String> verror = error.getTextError();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			error = DBStock.getInstance().insertStock(domain.getName(), domain.getId(), user.getLogin(), new LinkedList<>(stockMap.values()) ,transferInfo, inventoryId);
		}
		else{
			error.setError(false);
 			error.setTextError(verror);
		}
		return error;
	}

	public Error insertTransferStock(Domain domain, User user){
		LinkedList<String> verror = error.getTextError();
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);
			LinkedList<StockInfo> v = new LinkedList<StockInfo>();
			v.addAll(stockMap.values());
			error = DBStock.getInstance().insertTransferStock(domain, user.getLogin(), v,transferInfo);
		} else{
			error.setError(false);
 			error.setTextError(verror);
		}

		return error;
	}

	private StockInfo check(Domain domain, User user, Integer row, String column, String template, Object value,StockInfo stock, CellType type) {

		switch (template) {
		case "Producto": case "Product":
			if((type.equals(CellType.STRING) && !value.equals(""))){
				stock.setProduct(value.toString());
				if(stock.getProduct().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}

			else if( type.equals(CellType.NUMERIC)){

				stock.setProduct(String.format("%.0f",value ));
				if(stock.getProduct().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			else return null;
			break;
		case "Cantidad": case "Quantity":
			if(type.equals(CellType.NUMERIC)){
				Double n = (Double) value;
				if(n < 0) return null;
				stock.setQuantity(n);
			} else if(type.equals(CellType.STRING) && !value.equals("")) {
				Double n = Double.parseDouble(value.toString());
				if(n < 0) return null;
				stock.setQuantity(n);
			}
			break;
		case "Detalle 1": case "Detail 1":
			if(type.equals(CellType.STRING))
				stock.getItem().setDetail(value.toString());
			else if(type.equals(CellType.NUMERIC))
				stock.getItem().setDetail(toString(value));
			break;
		case "Detalle 2": case "Detail 2":
			if(type.equals(CellType.STRING))
				stock.getItem().setDetail2(value.toString());
			else if(type.equals(CellType.NUMERIC))
				stock.getItem().setDetail2(toString(value));
			break;
		case "Detalle 3": case "Detail 3":
			if(type.equals(CellType.STRING))
				stock.getItem().setDetail3(value.toString());
			else if(type.equals(CellType.NUMERIC))
				stock.getItem().setDetail3(toString(value));
			break;
		case "N\u00FAmero Serie": case "Serial Number":
			if(type.equals(CellType.STRING))
				stock.getItem().setSerialNumber(value.toString());
			else if(type.equals(CellType.NUMERIC))
				stock.getItem().setSerialNumber(toString(value));
			break;
		case "Formato":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Tag tag = DBConsults.getTag(domain, user.getLogin(), toString(value), TagType.PACKING);
				if(tag.getId() != null) stock.getItem().setPackFormatTag(tag);
				else{
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAG_NOT_EXIST.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAG_NOT_EXIST.getMessage() +"\n";
				}
			}
			break;
		case "Unidades":
			if(type.equals(CellType.NUMERIC)){
				stock.getItem().setPackUnits((Double)value);
			}else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Formato Unidades":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Tag tag = DBConsults.getTag(domain, user.getLogin(), toString(value), TagType.PACKING);
				if(tag.getId() != null)stock.getItem().setPackUnitsTag(tag);
				else{
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAG_NOT_EXIST.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAG_NOT_EXIST.getMessage() +"\n";
				}
			}
			break;
		case "Medida":
			if(type.equals(CellType.NUMERIC)){
				stock.getItem().setPackMeasurement((Double)value);
			}else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Formato Medida":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Tag tag = DBConsults.getTag(domain, user.getLogin(), toString(value), TagType.PACKING);
				if(tag.getId() != null) stock.getItem().setPackMeasurementTag(tag);
				else{
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAG_NOT_EXIST.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAG_NOT_EXIST.getMessage() +"\n";
				}
			}
			break;
		default:
			break;
		}
		return stock;
	}

	private StockInfo newStock(){
		StockInfo stock = new StockInfo();
		stock.setItem(new Item().setDetail("")
				.setDetail2("")
				.setDetail3(""));
		stock.setQuantity(null);
		return stock;
	}

	public LinkedList<com.esferalia.aon.gwt.template.shared.Series> getSeries(Domain domain, User user, String warehouse){
		Warehouse w = DBStock.getInstance().getWarehouse(domain, user, warehouse);
		Workplace workplace = DBCatalogue.getWorkplace(domain, user, w.getWorkplace());
		LinkedList<Series> series = DBStock.getInstance().getSeries(domain,w, workplace, user.getLogin());

		LinkedList<com.esferalia.aon.gwt.template.shared.Series> seriesCode = new LinkedList<com.esferalia.aon.gwt.template.shared.Series>();
		series.parallelStream().forEach(s ->{
			com.esferalia.aon.gwt.template.shared.Series serie = new com.esferalia.aon.gwt.template.shared.Series();
			serie.setId(s.getId());
			serie.setName(s.getCode());
			seriesCode.add(serie);
		});
		return seriesCode;
	}

	public LinkedList<com.esferalia.aon.gwt.template.shared.Series> getSeries(Domain domain, User user){
		LinkedList<Series> series = new LinkedList<Series>();
		series = DBStock.getInstance().getSeries(domain, user.getLogin());
		LinkedList<com.esferalia.aon.gwt.template.shared.Series> seriesCode = new LinkedList<com.esferalia.aon.gwt.template.shared.Series>();
		series.parallelStream().forEach(s ->{
			com.esferalia.aon.gwt.template.shared.Series serie = new com.esferalia.aon.gwt.template.shared.Series();
			serie.setId(s.getId());
			serie.setName(s.getCode());
			seriesCode.add(serie);
		});
		return seriesCode;
	}

	//-------------------- IMPORTAR PRODUCTOS
	Boolean productBool;
	LinkedList<ProductInfo> products;
	ProductInfo pi;

	private Boolean checkInventariable(Cell cell){
		return cell.getStringCellValue().equalsIgnoreCase("inventoriable")
			&& ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase("inventariable");
	}
	private void executeExcelProduct(Domain domain, User user, Iterator<Row> rowIterator, com.esferalia.aon.gwt.template.shared.Error error, FormulaEvaluator evaluator) {
		LinkedList<ProductInfo> products = new LinkedList<ProductInfo>();
		/* LAMBDA java 1.8 */
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		map = new HashMap<String, ProductInfo>();
		productBool = true;

		LinkedList<ProductCategory> productCategoryList = AON.getProductCategoryList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
		LinkedList<Brand> brandList = AON.getBrandStream(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId())).collect(Collectors.toCollection(LinkedList::new));

		Domain d = AON.getDomain(domain.getName(), domain.getId(), user.getLogin());
		LinkedList<Tag> tagList = AON.getTagList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
		LinkedList<Tax> taxList = d.getParentId() != null ? AON.getTaxList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(d.getParentId())))
				: AON.getTaxList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
		rowStream.forEach(row ->{
			if(row.getRowNum() !=0 && row.getPhysicalNumberOfCells()> 3){
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				pi = newProduct(domain, user);
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				cellStream.forEach(cell ->{
					if(cell.getColumnIndex() != ti.getColumns().size()){
						Object object = getObjectValue(cell, evaluator);
						if(cell.getRowIndex() == 1){//Primera fila del fichero Excel
							if(ti.getColumns().size()<= cell.getColumnIndex() || ti.getColumns().get(cell.getColumnIndex()) == null || cell.getCellTypeEnum() != CellType.STRING || (!checkInventariable(cell) && !ti.getColumns().get(cell.getColumnIndex()).equalsIgnoreCase(cell.getStringCellValue()))){
								// El archivo no es compatible con la plantilla
								error.setError(false);
								if(verror.isEmpty()) verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
								textError= textError +"*El archivo importado no es compatible con la plantilla seleccionada.";
								error.setTextError(verror);
								this.error = error;
								rowCount = -1;
								productBool = false;
							}
						}
						else if(productBool){
							if(cell.getColumnIndex() <= ti.getColumns().size()) {
								if(cell.getColumnIndex() !=0){
									Cell beforeCell = row.getCell(cell.getColumnIndex()-1);
									if((beforeCell == null || beforeCell.getCellTypeEnum() == CellType.BLANK) && isRequiredProduct(ti.getColumns().get(cell.getColumnIndex()-1))){
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
									pi = check(domain, user, cell.getRowIndex()+1, Utils.getColumn(cell.getColumnIndex()), ti.getColumns().get(cell.getColumnIndex()),object,pi,cell.getCellTypeEnum()
										, productCategoryList, brandList, tagList, taxList);
									if(pi == null){
										verror.add("*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto ");
										textError= textError + "*Fila "+(cell.getRowIndex()+1)+", Columna "+Utils.getColumn(cell.getColumnIndex())+" : Dato Incorrecto \n";
										pi = newProduct(domain, user);
									}
								}
							}
						}
					}
				});
				if(row.getLastCellNum() < ti.getColumns().size()){
					if(row.getRowNum() == 1){
						error.setError(false);
            			if(verror.isEmpty()) verror.add("*El archivo importado no es compatible con la plantilla seleccionada.");
						textError= textError +"*El archivo importado no es compatible con la plantilla seleccionada.";
            			error.setTextError(verror);
            			this.error = error;
            			rowCount = -1;
            			productBool = false;
            		}
            		else if(productBool){
            			if(row.getLastCellNum() != -1){
            				Short cellnum = row.getLastCellNum();
            				if(ti.getColumns().get(cellnum).equals("Nombre") || ti.getColumns().get(cellnum).equals("C\u00f3digo") || ti.getColumns().get(cellnum).equals("Precio Coste") || ti.getColumns().get(cellnum).equals("Precio Venta Base")){
          						verror.add("*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n");
          						textError= textError + "*Fila "+(row.getRowNum()+1)+", Columna "+Utils.getColumn(row.getLastCellNum())+" : Dato Incorrecto \n";
            				}
            			}
            		}
            	}

            	if(row.getRowNum() > 1 && pi.getProduct().getCode()!= null){
            		pi.setRow(row.getRowNum());
            		if(pi.getItem().get(0).getPurchasePrice() == 0) pi.getItem().get(0).setProfitPercent(0);
            		else pi.getItem().get(0).setProfitPercent(((pi.getItem().get(0).getPrice()-pi.getItem().get(0).getPurchasePrice())/pi.getItem().get(0).getPurchasePrice())*100);
            		products.add(pi);
            		if(!map.containsKey(pi.getProduct().getCode()))
            			map.put(pi.getProduct().getCode(), pi);
            		else{
            			map.get(pi.getProduct().getCode()).setProduct(pi.getProduct());
            			map.get(pi.getProduct().getCode()).getItem().add(pi.getItem().get(0));

            			pi.getTagList().stream().forEach(tag -> {
            				if(!map.get(pi.getProduct().getCode()).getTagList().contains(tag)){
                    			map.get(pi.getProduct().getCode()).getTagList().add(tag);
            				}
            			});
            		}
            	}
			}
		});
		this.products = products;
		if(products == null || products.size() <0){
			error.setError(false);
			verror.add("*El archivo no es v�lido.");
			textError= textError +"*El archivo no es v�lido.";
			error.setTextError(verror);
			this.error = error;
			rowCount = -1;
		}
		if(rowCount != -1) rowCount = products.size();
		setOut(null);setMimetype(null);
	}

	public Boolean esta(com.esferalia.aon.occam.api.model.product.ProductTag pt, LinkedList<com.esferalia.aon.occam.api.model.product.ProductTag> pts){
		for (com.esferalia.aon.occam.api.model.product.ProductTag productTag : pts) {
			if(productTag.getId().equals(pt.getId())) return true;
		}
		return false;
	}

	public Boolean isRequiredProduct(String s) {
		return s.equals("Nombre") || s.equals("C\u00f3digo") || s.equals("Precio Coste") || s.equals("Precio Venta Base");
	}

	public Error insertProduct(Domain domain, User user, String kind) {
		Error error = new Error();
		if(textError.equals("")){
			error.setError(true);
			verror.add("");
			error.setTextError(verror);

			java.util.List<ProductInfo> l = new ArrayList<ProductInfo>(map.values());
			LinkedList<ProductInfo> v = new LinkedList<ProductInfo>(l);
 			error = DBProduct.insertProducts2(domain, user.getLogin(), v, ti, kind);
		}
		else{
			//Alguna de las filas contiene datos erroneos.
			error.setError(false);
 			error.setTextError(verror);
		}
		return error;
	}

	public Error insertDelivery(Domain domain, User user) {
		domain = AON.getDomain(domain.getName(), domain.getId(), user.getLogin());
		if(di.getError().getTextError().isEmpty()) {
 			di = DeliveryImport.getInstance().insertDelivery(domain, user, di, error);
		}
		return di.getError();
	}

	private ProductInfo check(Domain domain, User user, Integer row, String column, String template, Object value,ProductInfo product, CellType type
			, LinkedList<ProductCategory> productCategoryList, LinkedList<Brand> brandList, LinkedList<Tag> tagList, LinkedList<Tax> taxList) {
		switch (template) {
		case "Nombre":
			if((type.equals(CellType.STRING) && !value.equals("")) || type.equals(CellType.NUMERIC)){
				product.getProduct().setName(toString(value));
				if(product.getProduct().getName().length() > 64){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			else return null;
			break;
		case "C\u00f3digo" :
			if((type.equals(CellType.STRING) && !value.equals("")) || type.equals(CellType.NUMERIC)){// TODO
				product.getProduct().setCode(toString(value, type));
				if(product.getProduct().getCode().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			else return null;
			break;
		case "Precio Coste" :
			if(type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setPurchasePrice((double) value);
			} else if(type.equals(CellType.FORMULA)) {
				System.out.println(value);
				product.getItem().get(0).setPurchasePrice(Double.parseDouble(value.toString()));
			} else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Precio Venta Base" :
			if(type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setPrice((double) value);
			} else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Categor\u00eda" :
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				String strAux = toString(value);
				if(strAux.length() > 32){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				} else {
					ProductCategory pc = productCategoryList.stream().filter(c -> strAux.equalsIgnoreCase(c.getName())).findFirst().orElse(new ProductCategory());
					if(pc.getId() == null){
						Long c = ti.getColumns().stream().filter(f -> f.contains("Detalle")).count();
						ProductCategory productCategory = AON.insertProductCategory(domain.getName(), domain.getId(), user.getLogin(),
							new ProductCategory().setDomain(domain.getId()).setName(strAux)
							.setDetail(c==1 || c==2 || c==3 ? " " : null)
							.setDetail2(c==2 || c==3 ? " " : null)
							.setDetail3(c==3 ? " " : null));
						product.getProduct().setCategory(productCategory.getId());
						productCategoryList.add(productCategory);
					} else product.getProduct().setCategory(pc.getId());
				}
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Marca" :
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				String strAux = toString(value);
				if(strAux.length() > 63){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				} else {
					Brand brand = brandList.stream().filter(b -> strAux.equalsIgnoreCase(b.getName())).findFirst().orElse(new Brand());
					if(brand.getId() == null){
						brand = AON.insertBrand(domain.getName(), domain.getId(), user.getLogin(),
								new Brand().setDomain(domain.getId()).setName(strAux));
						product.getProduct().setBrand(brand.getId());
						brandList.add(brand);
					} else product.getProduct().setBrand(brand.getId());
				}
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Etiqueta" :
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				String str = toString(value, type);
				String[] array = str.split(",");
				LinkedList<Tag> pts = new LinkedList<>();
				for(Integer i = 0; i < array.length; i++){
					String s = array[i];
					if(!s.isEmpty() && !" ".equals(s)){
						Tag tag = tagList.stream().filter(t -> t.getType() == TagType.PRODUCT.value() && t.getName().equals(s)).findFirst().orElse(new Tag());
						if(tag.getId() == null){
							tag = AON.insertTag(domain.getName(), domain.getId(), user.getLogin(),
								new Tag().setDomain(domain.getId()).setName(s).setType(TagType.PRODUCT.value()));
							tagList.add(tag);
						}
						pts.add(tag);
					}
				}
				if(array.length > 0) product.setTagList(pts);
			}
			break;
		case "Tipo":
			String t;
			if(type.equals(CellType.STRING)){
				t = (String) value;
				for(ProductType productType : ProductType.values()){
					if (t.equalsIgnoreCase(productType.getName())){
						product.getProduct().setType(productType.value());
					}
				}
			}
			break;
		case "IVA" :
			Tax vat = new Tax();
			switch (type) {
			case STRING:
				String s = (String) value;
				vat = taxList.stream().filter(tax -> tax.getType().equals(TaxType.VAT) && tax.getName().equalsIgnoreCase(s)).findFirst().orElse(new Tax());
				break;
			case NUMERIC:
				Double f = (Double) value;
				vat = taxList.stream().filter(tax -> tax.getType().equals(TaxType.VAT) && f.equals(tax.getPercentage())).findFirst().orElse(new Tax());
				break;
			case BLANK:
				return product;
			default:
				return null;
			}
			if(vat.getId() == null){
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAX_NOT_EXIST.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAX_NOT_EXIST.getMessage() +"\n";
			}
			product.getProduct().setVat(vat.getId());
			break;
		case "IRPF" :
			Tax retention = new Tax();
			switch (type) {
			case STRING:
				String s = (String) value;
				vat = taxList.stream().filter(tax -> tax.getType().equals(TaxType.RETENTION) && tax.getName().equalsIgnoreCase(s)).findFirst().orElse(new Tax());
				break;
			case NUMERIC:
				Double f = (Double) value;
				vat = taxList.stream().filter(tax -> tax.getType().equals(TaxType.RETENTION) && f.equals(tax.getPercentage())).findFirst().orElse(new Tax());
				break;
			case BLANK:
				return product;
			default:
				return null;
			}
			if(vat.getId() == null){
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAX_NOT_EXIST.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAX_NOT_EXIST.getMessage() +"\n";
			}
			product.getProduct().setRetention(retention.getId());
			break;
		case "Inventoriable" :
		case "Inventariable" :
			Boolean bool = false;
			switch (type) {
			case STRING:
				String string = (String) value;
 				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool = false;
				else return null;
				break;
			case NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool = true;
				else if(num.equals(0.0)) bool = false;
				else return null;
				break;
			case BOOLEAN:
				bool = (Boolean) value;
				break;
			case BLANK:
				return product;
			case FORMULA:
				bool = value.toString().equalsIgnoreCase("true");
				break;
			default:
				return null;
			}
			product.getProduct().setInventoriable(bool);
			break;
		case "Producto Compuesto" :
			Boolean bool2 = false;
			switch (type) {
			case STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool2 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool2 = false;
				else return null;
				break;
			case NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool2 = true;
				else if(num.equals(0.0)) bool2 = false;
				else return null;
				break;
			case BOOLEAN:
				bool2 = (Boolean) value;
				break;
			case BLANK:
				return product;
			case FORMULA:
				bool2 = value.toString().equalsIgnoreCase("true");
				break;
			default:
				return null;
			}
			product.getProduct().setComposition(bool2);
			break;
		case "Precio Composici\u00f3n" :
			Boolean bool3 = false;
			switch (type) {
			case STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool3 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool3 = false;
				else return null;
				break;
			case NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool3 = true;
				else if(num.equals(0.0)) bool3 = false;
				else return null;
				break;
			case BOOLEAN:
				bool3 = (Boolean) value;
				break;
			case BLANK:
				return product;
			case FORMULA:
				bool3 = value.toString().equalsIgnoreCase("true");
				break;
			default:
				return null;
			}
			product.getProduct().setCompositionPrice(bool3);
			break;
		case "Estado" :
			ProductStatus status;
			switch (type) {
			case STRING:
				String str2 = (String) value;
				if(str2.equalsIgnoreCase("active") || str2.equalsIgnoreCase("activo") || str2.equalsIgnoreCase("activado"))
					status = ProductStatus.ACTIVE;
				else if(str2.equalsIgnoreCase("discontinued") || str2.equalsIgnoreCase("descatalogado"))
					status = ProductStatus.DISCONTINUED;
				else return null;
				break;
			case NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0))
					status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			case BOOLEAN:
				Boolean b = (Boolean) value;
				if(b) status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			case FORMULA:
				Boolean b1 = value.toString().equalsIgnoreCase("true");
				if(b1) status = ProductStatus.ACTIVE;
				else status = ProductStatus.DISCONTINUED;
				break;
			case BLANK:
				return product;
			default:
				return null;
			}
			product.getProduct().setStatus((byte) status.ordinal());
			break;
		case "C\u00f3digo de Barras" :
			if(type.equals(CellType.STRING)){
				product.getItem().get(0).setBarcode((String) value);
			} else if(type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setBarcode(String.format("%.0f", value));
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Descripci\u00f3n" :
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setDescription(toString(value));
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Detalle 1":case "Detail 1":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setDetail(toString(value));
				if(product.getItem().get(0).getDetail().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Detalle 2":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setDetail2(toString(value));
				if(product.getItem().get(0).getDetail2().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Detalle 3":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setDetail3(toString(value));
				if(product.getItem().get(0).getDetail3().length() > 15){
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TOO_LARGE.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TOO_LARGE.getMessage() +"\n";
				}
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "N\u00FAmero Serie":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setSerialNumber(toString(value));
			}
			else if(!type.equals(CellType.BLANK)) return null;
			break;
		case "Serializable" :
			Boolean bool4 = false;
			switch (type) {
			case STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool4 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool4 = false;
				else return null;
				break;
			case NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool4 = true;
				else if(num.equals(0.0)) bool4 = false;
				else return null;
				break;
			case BOOLEAN:
				bool4 = (Boolean) value;
				break;
			case BLANK:
				return product;
			case FORMULA:
				bool4 = value.toString().equalsIgnoreCase("true");
				break;

			default:
				return null;
			}
			product.getProduct().setSerializable(bool4);
			break;
		case "Loteable" :
			Boolean bool5 = false;
			switch (type) {
			case STRING:
				String string = (String) value;
				if(string.equalsIgnoreCase("si") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"))
					bool5 = true;
				else if( string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false"))
					bool5 = false;
				else return null;
				break;
			case NUMERIC:
				Double num = (Double) value;
				if(num.equals(1.0)) bool5 = true;
				else if(num.equals(0.0)) bool5 = false;
				else return null;
				break;
			case BOOLEAN:
				bool5 = (Boolean) value;
				break;
			case BLANK:
				return product;
			case FORMULA:
				bool5 = value.toString().equalsIgnoreCase("true");
				break;

			default:
				return null;
			}
			product.getProduct().setLotable(bool5);
			break;
		case "Envasado" :
			switch (type) {
			case STRING:
				String string = (String) value;
				product.getProduct().setPackaged(string.equalsIgnoreCase("si") ||
						string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true"));
				break;
			case NUMERIC:
				Double num = (Double) value;
				product.getProduct().setPackaged(num.equals(1.0));
				break;
			case BOOLEAN:
				product.getProduct().setPackaged((Boolean) value);
				break;
			case BLANK:
				return product;
			case FORMULA:
				product.getProduct().setPackaged(value.toString().equalsIgnoreCase("true"));
				break;
			default:
				return null;
			}
			break;
		case "Formato":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Tag tag = tagList.stream().filter(tt -> tt.getType() == TagType.PACKING.value() && tt.getName().equals(toString(value))).findFirst().orElse(new Tag());
				if(tag.getId() != null)product.getItem().get(0).setPackFormatTag(tag);
				else{
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAG_NOT_EXIST.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAG_NOT_EXIST.getMessage() +"\n";
				}
			}
			break;
		case "Unidades":
			if(type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setPackUnits((Double)value);
			}else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;

		case "Formato Unidades":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Tag tag = tagList.stream().filter(tt -> tt.getType() == TagType.PACKING.value() && tt.getName().equals(toString(value))).findFirst().orElse(new Tag());
				if(tag.getId() != null)product.getItem().get(0).setPackUnitsTag(tag);
				else{
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAG_NOT_EXIST.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAG_NOT_EXIST.getMessage() +"\n";
				}
			}
			break;
		case "Medida":
			if(type.equals(CellType.NUMERIC)){
				product.getItem().get(0).setPackMeasurement((Double)value);
			}else {
				verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.NOT_NUMERIC.getMessage());
				textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.NOT_NUMERIC.getMessage() +"\n";
			}
			break;
		case "Formato Medida":
			if(type.equals(CellType.STRING) || type.equals(CellType.NUMERIC)){
				Tag tag = tagList.stream().filter(tt -> tt.getType() == TagType.PACKING.value() && tt.getName().equals(toString(value))).findFirst().orElse(new Tag());
				if(tag.getId() != null) product.getItem().get(0).setPackMeasurementTag(tag);
				else{
					verror.add("*Fila "+ row +", Columna "+ column +" : "+ ErrorMessage.TAG_NOT_EXIST.getMessage());
					textError= textError + "*Fila "+ row +", Columna "+ column +" : "+  ErrorMessage.TAG_NOT_EXIST.getMessage() +"\n";
				}
			}
			break;
		default:
			break;
		}

		return product;
	}

	public ProductInfo newProduct(Domain domain, User user) {
		ProductInfo pi = new ProductInfo();
		com.esferalia.aon.occam.api.model.product.Product p2 = new com.esferalia.aon.occam.api.model.product.Product();
		p2.setDomain(domain.getId());
		com.esferalia.aon.occam.api.model.product.Item i2 = new com.esferalia.aon.occam.api.model.product.Item();
		i2.setDomain(domain.getId());

		// Tipo (product)
		p2.setType((byte)ProductType.COMMERCIAL_PRODUCT.ordinal());

		// IVA (product)
		Tax vat = DBProduct.getIVAName(domain.getName(), domain.getId(),"GENERAL", user.getLogin());
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
		// Envasado
		p2.setPackaged(false);

		// estado (product)
		p2.setStatus((byte)ProductStatus.ACTIVE.ordinal());

		// detail
		i2.setDetail("");

		// detail2
		i2.setDetail2("");

		// detail3
		i2.setDetail3("");
		i2.setStatus((byte) 0);
		LinkedList<com.esferalia.aon.occam.api.model.product.Item> is= new LinkedList<com.esferalia.aon.occam.api.model.product.Item>();
		is.add(0, i2);
		pi.setProduct(p2);
		pi.setItem(is);
		pi.setTagList(new LinkedList<>());
		return pi;
	}
	
	@Override
	public LinkedList<TemplateInfo> searchNameTemplate(String searchStr, LinkedList<TemplateInfo> templates){
		LinkedList<TemplateInfo> list = new LinkedList<TemplateInfo>();
		for (TemplateInfo templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getName(), searchStr)){
				list.add(templateInfo);
			}
		}
		return list;
	}

	@Override
	public LinkedList<TemplateInfo> searchTypeTemplate(String searchStr, LinkedList<TemplateInfo> templates){
		LinkedList<TemplateInfo> list = new LinkedList<TemplateInfo>();
		for (TemplateInfo templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getType(), searchStr)){
				list.add(templateInfo);
			}
		}
		return list;
	}


	public LinkedList<com.esferalia.aon.gwt.template.shared.WorkPlace> getWorkplaces(Domain domain, User user){
		return DBCatalogue.getWorkplaces(domain, user);
	}
	public LinkedList<Department> getDepartments(Domain domain, User user, String workplaceDescription){
		Workplace w = DBCatalogue.getWorkplace(domain, user, workplaceDescription);
		return DBCatalogue.getDepartments(domain, w.getId(), user.getLogin());
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
	 * StringUtils.contains("�bc", "a") = true
	 * StringUtils.contains("abc", "z") = false
	 * StringUtils.contains("abc", "A") = true
	 * StringUtils.contains("�bc", "A") = true
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

	@Override
	public LinkedList<com.esferalia.aon.gwt.template.shared.ProductCategory> getProductCategories(Domain domain, User user) {
		return DBProduct.getCategoriesShared(domain.getName(), domain.getId(), user.getLogin());
	}

	@Override
	public Error executeExcelEcommerce(Domain domain, User user, Ecommerce ecommerce, Seller seller, String type, Tag tag) {
		Error error = new Error();
		error.setError(true);
    	if(getOut() == null){
			error.setError(false);
			LinkedList<String> verror = new LinkedList<String>();
			verror.add("*No ha importado ning�n archivo.");
			error.setTextError(verror);
			return error;
		}
    	byte[] data = getOut();
		byte[] xml = null;

    	if(getMimetype().equals(MimeType.CSV.getName()) && (ecommerce.equals(Ecommerce.EBAY) || ecommerce.equals(Ecommerce.GENERIC))){
    		xml = csvToXmlEbay(data, ecommerce.getName(), type, tag.getName(), seller.getRegistryName());
    	}

		if(xml == null && ecommerce.equals(Ecommerce.AMAZON))
			xml = excelToXmlAmazonXXX(data, ecommerce.getName(), type, tag.getName(), seller.getRegistryName());
		else if(xml == null && (ecommerce.equals(Ecommerce.EBAY) || ecommerce.equals(Ecommerce.GENERIC)))
			xml = excelToXmlEbayXXX(data, ecommerce.getName(), type, tag.getName(), seller.getRegistryName());

		if(xml != null){
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),
					filter -> filter.getDescriptionProperty().eq(ecommerce.getName()+"-"+type)
					.and(filter.getTypeProperty().eq(((byte) RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.ordinal())))
					, AttachType.REGISTRY);

			if(attach != null && attach.getId() != null){
				attach.setData(xml);
				attach.setModificationDate(Calendar.getInstance().getTime());
				attach.setModificationUser(user.getLogin());
				AON.updateAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
			}
			else{
				attach = new Attach(AttachType.REGISTRY);
				attach.setDescription(ecommerce.getName()+"-"+type);
				attach.setData(xml);

				attach.setConfidential(true);
				attach.setType(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value());
				attach.setDate(Calendar.getInstance().getTime());

				attach.setCreationDate(Calendar.getInstance().getTime());
				attach.setCreationUser(user.getLogin());

				attach.setModificationDate(Calendar.getInstance().getTime());
				attach.setModificationUser(user.getLogin());

				attach.setDomain(domain);
				attach.setMimeType(MimeType.XML);

				if(seller.getRegistryName().equals("-"))
					attach.setAttachModule(getCompany(domain, user).getId());
				else attach.setAttachModule(seller.getId());

				attach.setId(AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach));
				AON.insertRegistryAttachTag(domain.getName(), domain.getId(), user.getLogin(),
						attach.getId(), tag.getId());
			}
		}
		else{
			error.setError(false);
			LinkedList<String> verror = new LinkedList<String>();
			verror.add("*El archivo importado no es correcto.");
			error.setTextError(verror);
		}
		return error;
	}

	public byte[] excelToXmlAmazonXXX(byte[] data, String ec, String type, String tag, String seller){
		try {
			File aux = File.createTempFile("ecommerceTemplate", ".xls");
			FileUtils.writeByteArrayToFile(aux, data);
			FileInputStream excel = null;
			excel = new FileInputStream(aux);

			HSSFWorkbook workbook= new HSSFWorkbook(excel);
			HSSFSheet sheet = null;
			HSSFSheet sheet2 = null;
			try{
				sheet = workbook.getSheetAt(3);
				sheet2 = workbook.getSheetAt(5);
			} catch (IllegalArgumentException e){
				workbook.close();
				return null;
			}
			if(sheet == null){
				workbook.close();
				return null;
			}
			EcommerceProduct ep =  excelToXmlAmazonH(ec, type, tag, seller, sheet, sheet2);
			workbook.close();
			return XMLUtils.writeXml(ep);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (OfficeXmlFileException e){
			try{
				File aux = File.createTempFile("ecommerceTemplate", ".xlsx");
				FileUtils.writeByteArrayToFile(aux, data);
				FileInputStream excel = null;
				excel = new FileInputStream(aux);
				XSSFWorkbook workbook= new XSSFWorkbook(excel);
				if(workbook.getCTWorkbook().getSheets().sizeOfSheetArray()<6){
					workbook.close();
					return null;
				}
				XSSFSheet sheet = workbook.getSheetAt(3);
				XSSFSheet sheet2 = workbook.getSheetAt(5);
				if(sheet == null){
					workbook.close();
					return null;
				}
				EcommerceProduct ep = excelToXmlAmazonX(ec, type, tag, seller, sheet, sheet2);
				workbook.close();
				return XMLUtils.writeXml(ep);
			} catch (JAXBException e1) {
				e1.printStackTrace();
				return null;
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public EcommerceProduct excelToXmlAmazonH(String ec, String type, String tag, String seller, HSSFSheet sheet, HSSFSheet sheet2){
			Row row = sheet.getRow(0);
			Cell cell1 = row.getCell(0);
			Cell cell2 = row.getCell(1);
			Template template = new Template();
			template.setCategory(tag);
			template.setTag(tag);
			template.setEcommerce(ec);
			template.setType(type);
			template.setAmazonTemplateType(cell1.getStringCellValue());
			template.setAmazonVersion(cell2.getStringCellValue());
			template.setSeller(seller);

			Row row1 = sheet.getRow(1);
			Row row2 = sheet.getRow(2);

			ProductData pd = new ProductData();
			pd.setEcommerce(new ArrayList<EcommerceProduct.ProductData.Ecommerce>());
			Iterator<Cell> cellIterator1 = row1.cellIterator();
			Iterator<Cell> cellIterator2 = row2.cellIterator();

			Iterable<Cell> cellIterable1 = () -> cellIterator1;
			Stream<Cell> cellStream1 = StreamSupport.stream(cellIterable1.spliterator(),false);
			cellStream1.forEach(cell->{
				EcommerceProduct.ProductData.Ecommerce ecommerce = new EcommerceProduct.ProductData.Ecommerce();
				ecommerce.setName(cell.getStringCellValue());
				pd.getEcommerce().add(ecommerce);
			});

			Iterable<Cell> cellIterable2 = () -> cellIterator2;
			Stream<Cell> cellStream2 = StreamSupport.stream(cellIterable2.spliterator(),false);
		    final AtomicInteger count = new AtomicInteger();
		    count.set(0);
			cellStream2.forEach(cell ->{
				pd.getEcommerce().get(cell.getColumnIndex()).setCode(cell.getStringCellValue());
				Cell cellv = sheet2.getRow(1).getCell(count.get());
				if(cellv.getStringCellValue().equals(cell.getStringCellValue())){
					Integer j = 2;
					Row rowx = sheet2.getRow(j);
					Cell cellx = null;
					if(rowx != null) cellx = rowx.getCell(count.get());
					String strx = null;
					if(cellx != null) strx = cellx.getStringCellValue();
					LinkedList<String> list = new LinkedList<String>();
					while(strx != null && !strx.equals("")){
						list.add(strx);
						j++;
						rowx = sheet2.getRow(j);
						cellx = null;
						if(rowx != null) cellx = rowx.getCell(count.get());
						strx = null;
						if(cellx != null) strx = cellx.getStringCellValue();
					}
					PresetValues pv = new PresetValues();
					pv.setPresetValue(list);
					pd.getEcommerce().get(cell.getColumnIndex()).setPresetValues(pv);
					count.getAndIncrement();
				}
			});
			EcommerceProduct ep =  new EcommerceProduct();
			ep.setProductData(pd);
			ep.setTemplate(template);

			return ep;
	}

	public EcommerceProduct excelToXmlAmazonX(String ec, String type, String tag, String seller, XSSFSheet sheet, XSSFSheet sheet2){
		Row row = sheet.getRow(0);
		Cell cell1 = row.getCell(0);
		Cell cell2 = row.getCell(1);
		Template template = new Template();
		template.setCategory(tag);
		template.setTag(tag);
		template.setEcommerce(ec);
		template.setType(type);
		template.setAmazonTemplateType(cell1.getStringCellValue());
		template.setAmazonVersion(cell2.getStringCellValue());
		template.setSeller(seller);

		Row row1 = sheet.getRow(1);
		Row row2 = sheet.getRow(2);

		ProductData pd = new ProductData();
		pd.setEcommerce(new ArrayList<EcommerceProduct.ProductData.Ecommerce>());
		Iterator<Cell> cellIterator1 = row1.cellIterator();
		Iterator<Cell> cellIterator2 = row2.cellIterator();

		Iterable<Cell> cellIterable1 = () -> cellIterator1;
		Stream<Cell> cellStream1 = StreamSupport.stream(cellIterable1.spliterator(),false);
		cellStream1.forEach(cell->{
			EcommerceProduct.ProductData.Ecommerce ecommerce = new EcommerceProduct.ProductData.Ecommerce();
			ecommerce.setName(cell.getStringCellValue());
			pd.getEcommerce().add(ecommerce);
		});

		Iterable<Cell> cellIterable2 = () -> cellIterator2;
		Stream<Cell> cellStream2 = StreamSupport.stream(cellIterable2.spliterator(),false);
	    final AtomicInteger count = new AtomicInteger();
	    count.set(0);
		cellStream2.forEach(cell ->{
			pd.getEcommerce().get(cell.getColumnIndex()).setCode(cell.getStringCellValue());
			Cell cellv = sheet2.getRow(1).getCell(count.get());
			if(cellv.getStringCellValue().equals(cell.getStringCellValue())){
				Integer j = 2;
				Row rowx = sheet2.getRow(j);
				Cell cellx = null;
				if(rowx != null) cellx = rowx.getCell(count.get());
				String strx = null;
				if(cellx != null) strx = cellx.getStringCellValue();
				LinkedList<String> list = new LinkedList<String>();
				while(strx != null && !strx.equals("")){
					list.add(strx);
					j++;
					rowx = sheet2.getRow(j);
					cellx = null;
					if(rowx != null) cellx = rowx.getCell(count.get());
					strx = null;
					if(cellx != null) strx = cellx.getStringCellValue();
				}
				PresetValues pv = new PresetValues();
				pv.setPresetValue(list);
				pd.getEcommerce().get(cell.getColumnIndex()).setPresetValues(pv);
				count.getAndIncrement();
			}
		});
		EcommerceProduct ep =  new EcommerceProduct();
		ep.setProductData(pd);
		ep.setTemplate(template);

		return ep;
	}


	public byte[] excelToXmlAmazon(byte[] data, String ec, String type, String tag, String seller){
		try {
			File aux = File.createTempFile("ecommerceTemplate", ".xls");
			FileUtils.writeByteArrayToFile(aux, data);
			FileInputStream excel = null;
			excel = new FileInputStream(aux);
			HSSFWorkbook workbook= new HSSFWorkbook(excel);
			HSSFSheet sheet = workbook.getSheetAt(3);
			HSSFSheet sheet2 = workbook.getSheetAt(5);
			if(sheet == null){
				workbook.close();
				return null;
			}
			Row row = sheet.getRow(0);
			Cell cell1 = row.getCell(0);
			Cell cell2 = row.getCell(1);
			Template template = new Template();
			template.setCategory(tag);
			template.setTag(tag);
			template.setEcommerce(ec);
			template.setType(type);
			template.setAmazonTemplateType(cell1.getStringCellValue());
			template.setAmazonVersion(cell2.getStringCellValue());
			template.setSeller(seller);

			Row row1 = sheet.getRow(1);
			Row row2 = sheet.getRow(2);

			ProductData pd = new ProductData();
			pd.setEcommerce(new ArrayList<EcommerceProduct.ProductData.Ecommerce>());
			Iterator<Cell> cellIterator1 = row1.cellIterator();
			Iterator<Cell> cellIterator2 = row2.cellIterator();

			Iterable<Cell> cellIterable1 = () -> cellIterator1;
			Stream<Cell> cellStream1 = StreamSupport.stream(cellIterable1.spliterator(),false);
			cellStream1.forEach(cell->{
				EcommerceProduct.ProductData.Ecommerce ecommerce = new EcommerceProduct.ProductData.Ecommerce();
				ecommerce.setName(cell.getStringCellValue());
				pd.getEcommerce().add(ecommerce);
			});

			Iterable<Cell> cellIterable2 = () -> cellIterator2;
			Stream<Cell> cellStream2 = StreamSupport.stream(cellIterable2.spliterator(),false);
		    final AtomicInteger count = new AtomicInteger();
		    count.set(0);
			cellStream2.forEach(cell ->{
				pd.getEcommerce().get(cell.getColumnIndex()).setCode(cell.getStringCellValue());
				Cell cellv = sheet2.getRow(1).getCell(count.get());
				if(cellv.getStringCellValue().equals(cell.getStringCellValue())){
					Integer j = 2;
					Row rowx = sheet2.getRow(j);
					Cell cellx = null;
					if(rowx != null) cellx = rowx.getCell(count.get());
					String strx = null;
					if(cellx != null) strx = cellx.getStringCellValue();
					LinkedList<String> list = new LinkedList<String>();
					while(strx != null && !strx.equals("")){
						list.add(strx);
						j++;
						rowx = sheet2.getRow(j);
						cellx = null;
						if(rowx != null) cellx = rowx.getCell(count.get());
						strx = null;
						if(cellx != null) strx = cellx.getStringCellValue();
					}
					PresetValues pv = new PresetValues();
					pv.setPresetValue(list);
					pd.getEcommerce().get(cell.getColumnIndex()).setPresetValues(pv);
					count.getAndIncrement();
				}
			});

			workbook.close();
			EcommerceProduct ep =  new EcommerceProduct();
			ep.setProductData(pd);
			ep.setTemplate(template);

			return XMLUtils.writeXml(ep);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] excelToXmlEbayXXX(byte[] data, String ec, String type, String tag, String seller){
		try {
			File aux = File.createTempFile("ecommerceTemplate", ".xls");
			FileUtils.writeByteArrayToFile(aux, data);
			FileInputStream excel = null;
			excel = new FileInputStream(aux);

			HSSFWorkbook workbook= new HSSFWorkbook(excel);
			HSSFSheet sheet = workbook.getSheetAt(0);
			if(sheet == null){
				workbook.close();
				return null;
			}
			EcommerceProduct ep = excelToXmlEbayH(ec, type, tag, seller, sheet.getRow(0));
			workbook.close();
			return XMLUtils.writeXml(ep);
		} catch (OfficeXmlFileException e){
			try {
				File aux = File.createTempFile("ecommerceTemplate", ".xlsx");
				FileUtils.writeByteArrayToFile(aux, data);
				FileInputStream excel = null;
				excel = new FileInputStream(aux);

				XSSFWorkbook workbook= new XSSFWorkbook(excel);
				XSSFSheet sheet = workbook.getSheetAt(0);
				if(sheet == null){
					workbook.close();
					return null;
				}
				EcommerceProduct ep = excelToXmlEbayH(ec, type, tag, seller, sheet.getRow(0));
				workbook.close();

				return XMLUtils.writeXml(ep);
			} catch (JAXBException | IOException e1) {
				e1.printStackTrace();
				return null;
			}

		} catch (JAXBException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public EcommerceProduct excelToXmlEbayH(String ec, String type, String tag, String seller, Row row){
		Template template = new Template();
		template.setCategory(tag);
		template.setEcommerce(ec);
		template.setType(type);
		template.setTag(tag);
		template.setSeller(seller);

		ProductData pd = new ProductData();
		pd.setEcommerce(new ArrayList<EcommerceProduct.ProductData.Ecommerce>());
		Iterator<Cell> cellIterator = row.cellIterator();

		Iterable<Cell> cellIterable = () -> cellIterator;
		Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
		cellStream.forEach(cell->{
			EcommerceProduct.ProductData.Ecommerce ecommerce = new EcommerceProduct.ProductData.Ecommerce();
			ecommerce.setName(cell.getStringCellValue());
			ecommerce.setCode(cell.getStringCellValue());
			pd.getEcommerce().add(ecommerce);
		});
		EcommerceProduct ep =  new EcommerceProduct();
		ep.setProductData(pd);
		ep.setTemplate(template);
		return ep;
	}

	public byte[] excelToXmlEbay(byte[] data, String ec, String type, String tag, String seller){
		try {
			File aux = File.createTempFile("ecommerceTemplate", ".xls");
			FileUtils.writeByteArrayToFile(aux, data);
			FileInputStream excel = null;
			excel = new FileInputStream(aux);

			HSSFWorkbook workbook= new HSSFWorkbook(excel);
			HSSFSheet sheet = workbook.getSheetAt(0);
			if(sheet == null){
				workbook.close();
				return null;
			}
			Row row = sheet.getRow(0);

			Template template = new Template();
			template.setCategory(tag);
			template.setEcommerce(ec);
			template.setType(type);
			template.setTag(tag);
			template.setSeller(seller);

			ProductData pd = new ProductData();
			pd.setEcommerce(new ArrayList<EcommerceProduct.ProductData.Ecommerce>());
			Iterator<Cell> cellIterator = row.cellIterator();


			Iterable<Cell> cellIterable = () -> cellIterator;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			cellStream.forEach(cell->{
				EcommerceProduct.ProductData.Ecommerce ecommerce = new EcommerceProduct.ProductData.Ecommerce();
				ecommerce.setName(cell.getStringCellValue());
				ecommerce.setCode(cell.getStringCellValue());
				pd.getEcommerce().add(ecommerce);
			});
			workbook.close();
			EcommerceProduct ep =  new EcommerceProduct();
			ep.setProductData(pd);
			ep.setTemplate(template);
			return XMLUtils.writeXml(ep);
		} catch (OfficeXmlFileException e){
			return null;
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] csvToXmlEbay(byte[] data, String ec, String type, String tag, String seller){
		try {
			String csvSplitBy = ",";
			String csvLineSplitBy = "\n";
			String csv = new String(data, "UTF-8");
			String[] lineArray = csv.split(csvLineSplitBy);
			String csvFirstLine = lineArray[0];
			String[] array = csvFirstLine.split(csvSplitBy);
			ProductData pd = new ProductData();
			pd.setEcommerce(new ArrayList<EcommerceProduct.ProductData.Ecommerce>());
			for(String s : array){
				EcommerceProduct.ProductData.Ecommerce ecommerce = new EcommerceProduct.ProductData.Ecommerce();
				ecommerce.setName(s);
				ecommerce.setCode(s);
				pd.getEcommerce().add(ecommerce);
			}
			if(lineArray.length > 1){
				for(Integer i = 0; i < pd.getEcommerce().size(); i++){
					Integer j = 1;
					String csvline = lineArray[j];
					String[] csvLine = csvline.split(csvSplitBy);
					if(i<csvLine.length){
						String value = csvLine[i];
						List<String> list = new ArrayList<String>();
						while(j<lineArray.length && value != null && value != ""){
							list.add(value);
							j++;
							if(j<lineArray.length){
								csvline = lineArray[j];
								csvLine = csvline.split(csvSplitBy);
								if(i < csvLine.length) value = csvLine[i];
							}
						}

						PresetValues pv = new PresetValues();
						pv.setPresetValue(list);
						pd.getEcommerce().get(i).setPresetValues(pv);
					}
				}
			}
			Template template = new Template();
			template.setCategory(tag);
			template.setEcommerce(ec);
			template.setType(type);
			template.setTag(tag);
			template.setSeller(seller);

			EcommerceProduct ep =  new EcommerceProduct();
			ep.setProductData(pd);
			ep.setTemplate(template);
			return XMLUtils.writeXml(ep);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String generateConsumptionExcel(Domain domain, User user, LinkedList<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail,
								 Integer size, Boolean packaged, Boolean withoutInv, Integer category, Boolean dif) {
		File file = null;
		try {
			file = ConsumptionUtil.generateConsumption(domain, warehouses, type, onlyNegative, detail, size, user.getLogin(), packaged, withoutInv, category, dif);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		return saveFile(domain, file);
	}

	public String saveFile(Domain domain, File file) {
		String key = null;
		try {
			InputStream fis = new FileInputStream(file);
			Attach attach = new Attach().setDescription("consumo")
					.setMimeType(MimeType.MS_EXCEL)
					.setDomain(domain)
					.setType((byte) 0)
					.setSourceType((byte) 0)
					.setData(AonIOUtils.toByteArray(fis))
					.setAttachType(AttachType.DATA);
			Integer id = AON.insertAttach(domain.getName(), domain.getId(), "", attach);
			key = id.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return key;
	}

	public String generateConsumptionExcel(Domain domain, User user, LinkedList<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail,
			 Integer size, Date startDate, Date endDate, Boolean packaged, Integer category, Boolean dif) {
		File file = null;
		try {
			file = ConsumptionUtil.generateConsumption(domain, warehouses, type, onlyNegative, detail, size, user.getLogin()
					, startDate, endDate, packaged, category, dif);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		return saveFile(domain, file);
	}

	public Integer excelRowNumber(){
		if(getOut() != null){
			try{
				byte[] data = getOut();
				ByteArrayInputStream bais = new ByteArrayInputStream(data);

				HSSFWorkbook workbook = new HSSFWorkbook(bais);
				HSSFSheet sheet = workbook.getSheetAt(0);
				Integer size = sheet.getPhysicalNumberOfRows();
				workbook.close();
				return size;
			} catch (IOException e) {
				e.printStackTrace();
				return 1;
			} catch (OfficeXmlFileException e){
				try {
					byte[] data = getOut();
					ByteArrayInputStream bais = new ByteArrayInputStream(data);

					XSSFWorkbook workbook = new XSSFWorkbook(bais);
					XSSFSheet sheet = workbook.getSheetAt(0);
					Integer size = sheet.getPhysicalNumberOfRows();
					workbook.close();
					return size;
				} catch (IOException e1) {
					e1.printStackTrace();
					return 1;
				}
			}
		}
		return 1;
	}

	public Company getCompany(Domain domain, User user){
		return AON.getCompanyForDomain(domain.getName(), domain.getId(), user.getLogin());
	}

	public LinkedList<Seller> getSellerList(Domain domain, User user){
		return DBFee.getInstance().getSellers(domain, user.getLogin());
	}

	public LinkedList<String> getProductRoles(Domain domain, User user) {
		LinkedList<String> list = new LinkedList<String>();
		for (AonRole role : user.getUserRoles()) {
			if(role.equals(AonRole.PURCHASE))
				list.add("Compra");
			if(role.equals(AonRole.SALE))
				list.add("Venta");
		}
		return list;
	}

	public LinkedList<String> getTypeList(Domain domain, User user){
		return AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDomainProperty().eq(domain.getId())
				.and(filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())),
				AttachType.REGISTRY, false).map(r -> r.getDescription())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private Object getObjectValue(Cell cell, FormulaEvaluator evaluator){
		switch (cell.getCellTypeEnum()) {
			case BLANK:
				return null;
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case ERROR:
				return cell.getErrorCellValue();
			case FORMULA:
				return evaluator.evaluate(cell).getNumberValue();
				//return cell.getCellFormula();
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				return cell.getStringCellValue();
			default:
				return null;
		}
	}

	public String toString(Object value, CellType type){
		if(type.equals(CellType.STRING) && !value.equals("")){
			return value.toString();
		} else if(type.equals(CellType.NUMERIC)){
			Double d = Double.parseDouble(value.toString());
			Integer i = d.intValue();
			if((d - i.doubleValue()) ==  0)
				return i.toString();
			return d.toString();
		}
		return value.toString();
	}

	public String toString(Object value){
		try{
			Double d = Double.parseDouble(value.toString());
			Integer i = d.intValue();
			if((d - i.doubleValue()) ==  0)
				return i.toString();
			return d.toString();
		} catch (NumberFormatException e){
			return value.toString();
		}
	}

	public void print(String text){
		System.out.println(text);
	}

	@Override
	public Error insertProjectCommercial(Domain domain, User user) {
		try {
			ProjectCommercialImport.getInstance().insertProjectCommercial(domain, user, pcs);
		} catch (Exception e) {
			return new Error()
				.setError(false)
				.setTextError(e.getMessage());
		}
		return new Error();
	}

	@Override
	public Error insertCustomerIban(Domain domain, User user) {
		try {
			CustomerIbanImport.getInstance().insertCustomerIban(domain, user, cis);
		} catch (Exception e) {
			return new Error()
				.setError(false)
				.setTextError(e.getMessage());
		}
		return new Error();
	}
	
	@Override
	public Error insertInvoices(Domain domain, User user, Integer index) {
		return InvoiceImport.insertInvoices(domain, user, index, ivs);
	}
	
	@Override
	public Error insertRegistries(Domain domain, User user, Integer index) {
		return RegistryImport.insertRegistries(domain, user, index, rvs);			
	}
	
	@Override
	public Error insertDiary(Domain domain, User user, Integer index) {
		return DiaryImport.insertDiary(domain, user, index, dvs);
	}

	@Override
	public Error insertPGC(Domain domain, User user, Integer index) {
		return PGCImport.insertPGC(domain, user, index, accounts);			
	}

	
}
