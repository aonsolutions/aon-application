package com.esferalia.aon.gwt.template.server.imports;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.esferalia.aon.gwt.template.server.FeeInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FeeImport extends Import {
	
	public static FeeImport getInstance() {
		return new FeeImport();
	}

	public FeeImport() {

	}
	
	public LinkedList<FeeInfo> importation(Domain domain, String login, byte[] data){
		return importation(domain, login, rowIterator(data));
	}

	public LinkedList<FeeInfo> importationX(Domain domain, String login, byte[] data){
		return importation(domain, login, rowIteratorX(data));
	}
	
	FeeInfo feeInfo; 
	Fee fee;	
	Integer indexTitle;
	
	private LinkedList<FeeInfo> importation(Domain domain, String login, Iterator<Row> rowIterator) {
		LinkedList<FeeInfo> list = new LinkedList<>();
		LinkedList<String> titleList = new LinkedList<>();
		Iterable<Row> rowIterable = () -> rowIterator;
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);

		indexTitle = 0;
		rowStream.forEach(row -> {
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			feeInfo = new FeeInfo();
			feeInfo.setLine(row.getRowNum() + 1);
			fee = new Fee();
			Object obj = Utils.getObjectValue(row.getCell(0));
			if(obj == null || (titleList.isEmpty() && !AonArrayUtils.constainsIgnoreCase(IConstants.FEE_TITLES, obj.toString()))) {
				indexTitle = indexTitle + 1;
			}
			cellStream.forEach(cell -> {
				if(row.getRowNum() == indexTitle) {
					titleList.add(cell.getStringCellValue().trim());
				} else if(row.getRowNum() > indexTitle && cell.getColumnIndex() < titleList.size()){
					String title = titleList.get(cell.getColumnIndex());
					check(domain, login, title, cell);			
				}
			});
			if(row.getRowNum() > indexTitle) {	
				feeInfo.setFee(fee);
				list.add(feeInfo);
			}
		});

		return list;
		
	}
	
	private void check(Domain domain , String login, String title, Cell cell) {
		Object o = Utils.getObjectValue(cell);
		
		if(o == null) return;
	
		if(IConstants.CLIENTE.equalsIgnoreCase(title)) {
			fee.getCustomer().setAlias(o.toString());
			return;
		}
		
		if(IConstants.PRODUCTO.equalsIgnoreCase(title)) {
			fee.getItem().getProduct().setCode(o.toString());
			return;
		}
		
		if(IConstants.CANTIDAD.equalsIgnoreCase(title)) {
			fee.setQuantity(parseDouble(o));
			return;
		}
		if(IConstants.PRECIO.equalsIgnoreCase(title)) {
			fee.setPrice(parseDouble(o));
			return;
		}
		if(IConstants.DESCUENTO.equalsIgnoreCase(title)) {
			fee.setDiscount(parseDouble(o));
			return;
		}
		
		if(IConstants.FECHA_INICIO.equalsIgnoreCase(title)) {
			fee.setStartDate(parseDate(cell, o));
			return;
		}
		
		if(IConstants.FECHA_FIN.equalsIgnoreCase(title)) {
			fee.setEndDate(parseDate(cell, o));
			return;
		}
		
		if(IConstants.FECHA_FACTURACION.equalsIgnoreCase(title) || IConstants.FECHA_FACTURACIÓN.equalsIgnoreCase(title)) {
			fee.setBillingDate(parseDate(cell, o));
			return;
		}
		
		if(IConstants.PERIODO.equalsIgnoreCase(title)) {
			fee.setPeriod(BillingPeriod.safeValueOf(o.toString()));
			return;
		}
		
		if(IConstants.COMERCIAL.equalsIgnoreCase(title)) {
			fee.getSeller().setRegistryAlias(o.toString());
			return;
		}
		
		if(IConstants.CENTRO_DE_TRABAJO.equalsIgnoreCase(title) || IConstants.CENTRO_TRABAJO.equalsIgnoreCase(title)) {
			fee.getWorkplace().setDescription(o.toString());
			return;
		}
		
		if(IConstants.GRUPO_FACTURACION.equalsIgnoreCase(title) || IConstants.GRUPO_FACTURACIÓN.equalsIgnoreCase(title) || IConstants.GRUPO.equalsIgnoreCase(title)
				|| IConstants.GRUPO_DE_FACTURACION.equalsIgnoreCase(title) || IConstants.GRUPO_DE_FACTURACIÓN.equalsIgnoreCase(title)) {
			fee.getInvoicingGroup().setDescription(o.toString());
			return;
		}
		
		if(IConstants.CONFIDENCIAL.equalsIgnoreCase(title)) {
			fee.setConfidential(parseBoolean(o));
			return;
		}
		
		if(IConstants.DESCRIPCION.equalsIgnoreCase(title) || IConstants.DESCRIPCIÓN.equalsIgnoreCase(title)) {
			fee.setDescription(o.toString());
			return;
		}
		
		if(IConstants.EXPEDIENTE.equalsIgnoreCase(title)) {
			fee.getProject().setAlias(o.toString());
			return;
		}
		
		if(IConstants.DETALLE_1.equalsIgnoreCase(title) || IConstants.DETALLE1.equalsIgnoreCase(title) || IConstants.DETALLE.equalsIgnoreCase(title)) {
			fee.getItem().setDetail(o.toString());
			return;
		}
		
		if(IConstants.DETALLE_2.equalsIgnoreCase(title) || IConstants.DETALLE2.equalsIgnoreCase(title)) {
			fee.getItem().setDetail2(o.toString());
			return;
		}
		
		if(IConstants.DETALLE_3.equalsIgnoreCase(title) || IConstants.DETALLE3.equalsIgnoreCase(title)) {
			fee.getItem().setDetail3(o.toString());
			return;
		}
		
		if(IConstants.CODIGO_DE_BARRAS.equalsIgnoreCase(title) || IConstants.CÓDIGO_DE_BARRAS.equalsIgnoreCase(title) || IConstants.BARCODE.equalsIgnoreCase(title)
				|| IConstants.CODIGO_BARRAS.equalsIgnoreCase(title) || IConstants.CÓDIGO_BARRAS.equalsIgnoreCase(title)) {
			fee.getItem().setBarcode(o.toString());
			return;
		}
		
		if(IConstants.NÚMERO_DE_SERIE.equalsIgnoreCase(title) || IConstants.NUMERO_DE_SERIE.equalsIgnoreCase(title) || IConstants.SERIAL_NUMBER.equalsIgnoreCase(title)
				|| IConstants.NÚMERO_SERIE.equalsIgnoreCase(title) || IConstants.NUMERO_SERIE.equalsIgnoreCase(title)) {
			fee.getItem().setSerialNumber(o.toString());
			return;
		}
		
		if(IConstants.LINEA.equalsIgnoreCase(title) || IConstants.LÍNEA.equalsIgnoreCase(title)) {
			fee.setLine(parseShort(o));
			return;
		}
	}

	public static Error insertFees(Domain domain, User user, Integer index, LinkedList<FeeInfo> fees) {
		Error error = new Error().setLine(index).setError(true);
	
		if(index >= fees.size()) {
			return error;
		}
		
		FeeInfo feeInfo = fees.get(index);	
		Fee fee = feeInfo.getFee();
		fee.setDomain(domain);
		if(fee.getPeriod() == null) {
			fee.setPeriod(BillingPeriod.NO_PERIOD);
		}

		// CUSTOMER
		fee.setCustomer(AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> customerFilter(domain, user, feeInfo.getFee().getCustomer(), f)));
		if(fee.getCustomer().getId() == null) {
			error.setError(false);
			error.setTextError("Línea " + feeInfo.getLine() + ": El cliente introducido no existe.");
			return error;
		}
		
		// SELLER
		if(fee.getSeller().getRegistryAlias() != null) {
			AON.getSeller(domain.getName(), domain.getId(), user.getLogin(), f -> sellerFilter(domain, user, feeInfo.getFee().getSeller(), f));
			if(fee.getSeller().getId() == null) {
				error.setError(false);
				error.setTextError("Línea " + feeInfo.getLine() + ": El comercial introducido no existe.");
				return error;
			}
		}
		
		// PROJECT
		if(fee.getProject().getAlias() != null) {
			fee.setProject(AON.getProject(domain.getName(), domain.getId(), user.getLogin(), f -> projectFilter(domain, user, feeInfo.getFee(), f)));			
			if(fee.getProject().getId() == null) {
				error.setError(false);
				error.setTextError("Línea " + feeInfo.getLine() + ": El expediente introducido no existe.");
				return error;
			}
		}
		
		// WORKPLACE
		if(fee.getWorkplace().getDescription() != null){
			fee.setWorkplace(AON.getWorkplace(domain.getName(), domain.getId(), user.getLogin(), f -> workplaceFilter(domain, user, feeInfo.getFee().getWorkplace(), f)));
			if(fee.getWorkplace().getId() == null) {
				error.setError(false);
				error.setTextError("Línea " + feeInfo.getLine() + ": El centro de trabajo introducido no existe.");
				return error;
			}
		} else {
			LinkedList<Workplace> list = AON.getWorkplaceList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
			if(list != null && (list.size() < 1 || list.size() > 1 )) {
				error.setError(false);
				error.setTextError("Línea " + feeInfo.getLine() + ": Hay que introducir el centro de trabajo.");
				return error;
			} else {
				error.setTextWarning("Línea " + feeInfo.getLine() + ": Se ha asignado el centro de trabajo " +list.getFirst().getDescription() + ".");
				fee.setWorkplace(list.getFirst());
			}
		}
		
		// PRODUCT
		String productCode = fee.getItem().getProduct().getCode();
		if(AonStringUtils.isEmpty(productCode) || productCode.length() > 15){
			error.setError(false);
			error.setTextError("Línea " + feeInfo.getLine() + ": El código de producto está vacío o es demasiado largo.");
			return error;
		}
		Product product = AON.getProduct(domain.getName(), domain.getId(), user.getLogin(), f -> productFilter(domain, user, feeInfo.getFee().getItem(), f));			
		if(product.getId() == null) {
			Tax tax = AON.getTax(domain.getName(), domain.getId(), user.getLogin(), f -> vatFilter(domain, user, f));
			
			product = AON.insertProduct(domain.getName(), domain.getId(), user.getLogin(), new Product()
					.setDomain(domain.getId())
					.setName(AonStringUtils.isEmpty(fee.getDescription()) 
							? fee.getItem().getProduct().getCode()
							: fee.getDescription())
					.setCode(fee.getItem().getProduct().getCode())
					.setVat(tax.getId())
					.setKind(ProductKind.SALE.value())
					.setType(ProductType.SERVICE.value())
					.setInventoriable(false));
			AON.insertItem(domain.getName(), domain.getId(), user.getLogin(), new Item()
					.setDomain(domain.getId())
					.setProduct(product)
					.setProductId(product.getId())
					.setPrice(fee.getPrice() != null ? fee.getPrice() : 0.0));
			
			error.setTextWarning("Línea " + feeInfo.getLine() + ": El producto introducido no existe. Se ha creado un nuevo producto con código " + product.getCode() + ".");
		}
		Product p = product;
		// ITEM
		Item item = AON.getItem(domain.getName(), domain.getId(), user.getLogin(), f -> itemFilter(domain, user, p, feeInfo.getFee().getItem(), f));
		if(item.getId() == null) {			
			item = AON.insertItem(domain.getName(), domain.getId(), user.getLogin(), fee.getItem()
					.setDomain(domain.getId())
					.setProduct(product)
					.setProductId(product.getId())
					.setPrice(fee.getPrice() != null ? fee.getPrice() : 0.0));	
			error.setTextWarning("Línea " + feeInfo.getLine() + ": El detalle del producto (detalles, código de barras o número de serie) no existe. Se ha creado un nuevo detalle para el producto " + product.getCode() + ".");	
		}
		fee.setItem(item);
		fee.getItem().setProduct(product);
		
		

		// INVOICING GROUP
		if(fee.getInvoicingGroup().getDescription() != null) {
			InvoicingGroup ig = AON.getInvoicingGroup(domain.getName(), domain.getId(), user.getLogin(), f -> invoicingGroupFilter(domain, user, feeInfo.getFee(), f));
			if(ig.getId() == null) {
				ig = AON.save(domain.getName(), domain.getId(), user.getLogin(), new InvoicingGroup()
						.setDomain(domain.getId())
						.setCustomer(fee.getCustomer().getId())
						.setCustomerGrouped((byte) 1)
						.setDescription(fee.getInvoicingGroup().getDescription()));
				error.setTextWarning("Línea " + feeInfo.getLine() + ": El grupo de facturación introducido no existe. Se ha creado un nuevo grupo " + ig.getDescription() + ".");
			}
			fee.setInvoicingGroup(ig);
		}

		if(AonStringUtils.isEmpty(fee.getDescription())) {
			String str = fee.getItem().getProduct().getName();
			String details = "[";
			if(!AonStringUtils.isEmpty(item.getDetail())) {
				details = details + item.getDetail();
			}
			
			if(!AonStringUtils.isEmpty(item.getDetail())) {
				if(!details.equals("[")) {
					details = details + " / ";
				}
				details = details + item.getDetail();
			}
			if(!AonStringUtils.isEmpty(item.getDetail())) {
				if(!details.equals("[")) {
					details = details + " / ";
				}
				details = details + item.getDetail();
			}

			details = details + "]";
			if(!details.equals("[]")) {
				str = str + " " + details;
			}
			fee.setDescription(str);
		}
		
		if(fee.getQuantity() == null) {
			fee.setQuantity(1.0);
		}
		if(fee.getPrice() == null) {
			fee.setPrice(fee.getItem().getPrice());
		}
		
		fee = AON.save(domain.getName(), domain.getId(), user.getLogin(), fee);
		
		return error;
	}

	private static Filter customerFilter(Domain domain, User user, Customer customer, CustomerProperties f) {
		return f.getDomainProperty().eq(domain.getId())
			.and(f.getDocumentProperty().eq(customer.getAlias()).or(f.getNameProperty().eq(customer.getAlias())));
	}
	
	private static Filter sellerFilter(Domain domain, User user, Seller seller, SellerProperties f) {
		return f.getDomainProperty().eq(domain.getId())
			.and(f.getDocumentProperty().eq(seller.getRegistryAlias()).or(f.getNameProperty().eq(seller.getRegistryAlias())));
	}
	
	private static Filter workplaceFilter(Domain domain, User user, Workplace workplace, WorkplaceProperties f) {
		return f.getDomainProperty().eq(domain.getId())
			.and(f.getDescriptionProperty().eq(workplace.getDescription()));
	}
	
	private static Filter productFilter(Domain domain, User user, Item item, ProductProperties f) {
		return f.getDomainProperty().eq(domain.getId())
				.and(f.getCodeProperty().eq(item.getProduct().getCode()));
	}
	
	private static Filter invoicingGroupFilter(Domain domain, User user, Fee fee, InvoicingGroupProperties f) {
		return f.getDomainProperty().eq(domain.getId())
				.and(f.getDescriptionProperty().eq(fee.getInvoicingGroup().getDescription()));
	}
	
	private static Filter projectFilter(Domain domain, User user, Fee fee, ProjectProperties f) {
		return f.getDomainProperty().eq(domain.getId())
				.and(f.getRegistryProperty().eq(fee.getCustomer().getId()))
				.and(f.getAliasProperty().eq(fee.getProject().getAlias())
					.or(f.getNameProperty().eq(fee.getProject().getAlias())));
	}
	
	private static Filter itemFilter(Domain domain, User user, Product product, Item item, ItemProperties f) {
    	Filter filter =  f.getDomainProperty().eq(domain.getId())
    			.and(f.getProductProperty().eq(product.getId()));
    	
    	if(!AonStringUtils.isEmpty(item.getBarcode())) {
    		filter = filter.and(f.getBarcodeProperty().eq(item.getBarcode()));
    	}
    	
    	if(!AonStringUtils.isEmpty(item.getSerialNumber())) {
    		filter = filter.and(f.getSerialNumberProperty().eq(item.getSerialNumber()));
    	}
    	
    	if(!AonStringUtils.isEmpty(item.getDetail())) {
    		filter = filter.and(f.getDetailProperty().eq(item.getDetail()));
    	}
    	
    	if(!AonStringUtils.isEmpty(item.getDetail2())) {
    		filter = filter.and(f.getDetail2Property().eq(item.getDetail2()));
    	}
    	
    	if(!AonStringUtils.isEmpty(item.getDetail3())) {
    		filter = filter.and(f.getDetail3Property().eq(item.getDetail3()));
    	}
    	
		return filter;
    }
	
	private static Filter vatFilter(Domain domain, User user, TaxProperties f) {
		Filter filter = f.getTaxTypeProperty().eq(TaxType.VAT.value())
				.and(f.getPercentageProperty().eq(21.0));
		if(domain.getParentId() != null) {
			filter = filter.and(f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())));
		} else filter = filter.and(f.getDomainProperty().eq(domain.getId()));
		return filter;
	}
	
}
