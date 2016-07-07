package com.code.aon.ui.sales.udapa;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.customer.Customer;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryNote;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesController;
import com.code.aon.ui.sales.controller.SalesDetailController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1C;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1L;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1T;
import com.esferalia.aon.file.seres.util.reader.udapa.UdapaSalesReader;

public class EdiSalesImporterHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 210023208960242355L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EdiSalesImporterHandler.class);
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private IController controller;
	private AonFile aonFile;
	private boolean showImportFileWindow;
			
	
	public EdiSalesImporterHandler(IController controller) {
		this.controller = controller;
	}
	
	public AonFile getAonFile() {
		return aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	public boolean isShowImportFileWindow() {
		return showImportFileWindow;
	}
	public void setShowImportFileWindow(boolean value) {
		this.showImportFileWindow = value;
	}
	
	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}

	
	public void onImportFileShow(ActionEvent event) {
		controller.onReset(event);
		setAonFile(null);
	}
	
	public void onImportFile(ActionEvent event) {
		UdapaSalesReader reader = new UdapaSalesReader();
		ERE1C ere1c = null;
		try {
			ere1c = reader.readFile(aonFile.openStream());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		createSales(event, ere1c);
		setAonFile(null);
	}
	
	public void createSales(ActionEvent event, ERE1C ere1c) {
		Sales sales = (Sales) controller.getTo();
		try {
			if(ere1c.getCodigoPuntoDeEntrega_DP_()==null){
				AonUtil.addErrorMessage("Imposible continuar, el fichero no contiene codigo de punto de entrega.");
				AonUtil.addErrorMessage("Comprador: " + ere1c.getCodigoComprador_BY_());
			} else {
				Customer customer = searchCustomer(ere1c.getCodigoPuntoDeEntrega_DP_().trim());
				if(customer==null){
					AonUtil.addErrorMessage("No existe el cliente con el codigo de punto de entrega " + ere1c.getCodigoPuntoDeEntrega_DP_());
				} else {
					SalesController salesController = (SalesController) controller;
					sales.setCustomer(customer);
					sales.setShippingAddress(null);
					sales.setScope(customer.getScope());
					salesController.loadAddresses(customer.getId());
					salesController.loadProjects(customer.getId());
					salesController.loadCommercial(customer.getId());
					salesController.loadDefaultPayMethod(customer.getRegistry(), true);
					
					try {
						sales.setIssueDate(dateFormatter.parse(ere1c
								.getFechaDelDocumento_137__102_().toString()));
					} catch (ParseException e) {
						AonUtil.addErrorMessage("No se ha podido convertir la fecha: " + ere1c.getFechaDelDocumento_137__102_());
						throw new AbortProcessingException(e.getMessage(), e);
					}
					if(ere1c.getTipoDePedido_220_221_224_226_22E_().equals(ERE1C.C1001T.CANCELACION_DE_PEDID_226.getValue())){
						sales.setDocumentType(DocumentType.ITEM_RETURN);
					} else {
						sales.setDocumentType(DocumentType.NORMAL);
					}
					sales.setSecurityLevel(SecurityLevel.OFFICIAL);
					sales.setStatus(SalesStatus.PENDING);
					sales.setPurchaseGenerated(false);
					sales.setPurchaseReference(ere1c.getNumeroDePedido());
					
					String remarks = "Pedido: " + ere1c.getNumeroDePedido();
					remarks += System.getProperty("line.separator");
					for(ERE1T value: ere1c.ere1tList){
						remarks += (StringUtils.isNotBlank(value.getTexto1())?value.getTexto1().trim():"") +
								(StringUtils.isNotBlank(value.getTexto2())?", " + value.getTexto2().trim():"") +
								(StringUtils.isNotBlank(value.getTexto3())?", " + value.getTexto3().trim():"") +
								(StringUtils.isNotBlank(value.getTexto4())?", " + value.getTexto4().trim():"") +
								(StringUtils.isNotBlank(value.getTexto5())?", " + value.getTexto5().trim():"") + 
								System.getProperty("line.separator");
					}
					sales.setRemarks(remarks);
					sales.setComments("");
					
					salesController.accept(event);
					
					SalesDetailController detailController = (SalesDetailController) FormUtil.getController(ISalesConstants.SALES_DETAIL_CONTROLLER_NAME);
					for(ERE1L ere1l: ere1c.ere1lList){
						detailController.onReset(event);
						SalesDetail detail = (SalesDetail) detailController.getTo();
						detail.setSales(sales);
						Item item = searchItem(ere1l);
						detail.setItem(item);
						detail.setLine(Integer.valueOf(ere1l.getNumeroDeLineaArticulo()));
						String description = String.format("%s. %s. %s.",
								StringUtils.trimToEmpty(ere1l
										.getDescripcionDelArticulo1()), StringUtils
										.trimToEmpty(ere1l
												.getDescripcionDelArticulo2()),
												StringUtils.trimToEmpty(ere1l
														.getDescripcionDelModelo_BRN_()));
						detail.setDescription(description);
						detail.setQuantity(Double.valueOf(ere1l.getCantidadPedida_21_()));
						detail.setPrice(Double.valueOf(ere1l.getPrecioBrutoUnitario_AAB_()));
//					detail.setDiscountExpression(detail.getDiscountExpression().getDiscountExpr());
//					detail.setTaxes(detail.getTaxes());
						detail.setStatus(SalesDetailStatus.PENDING);
//					detail.setOfferDetail(null);
						detail.setDelivered(0.0);
						detailController.onAccept(event);
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}

	}

	private Customer searchCustomer(String customerCode) {
		Integer registryId = null;
		try {
			IManagerBean rnoteBean = BeanManager.getManagerBean(RegistryNote.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getLikeExpression(
					rnoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_COMMENTS),
					"%" + CustomerEdiSupportController.PTO_ENTREGA + "="
							+ customerCode + ";%"));
			List<ITransferObject> list = rnoteBean.getList(criteria);
			registryId = list != null && !list.isEmpty() ? ((RegistryNote) list
					.get(0)).getRegistry().getId() : null;
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
		
		if(registryId!=null){
			try {
				IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
				return (Customer) customerBean.get(registryId);
			} catch (ManagerBeanException ex) {
				AonUtil.addErrorMessage(ex.getMessage());
				throw new AbortProcessingException(ex.getMessage());
			}
		}
		return null;
	}
	
	private Item searchItem(ERE1L ere1l) {
		try {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_BARCODE), ere1l.getCodigoDeArticuloEAN_13ODUN_14().trim());
			List<ITransferObject> list = itemBean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				return (Item) list.get(0);
			} else {
				return createItem(ere1l);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	private Item createItem(ERE1L ere1l) throws ManagerBeanException {
		
		Product product = searchProduct(ere1l);
		
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Item item = new Item();
		item.setProduct(product);
//		detail` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle del Articulo',
//		detail2` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 2 del Articulo',
//		detail3` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Detalle 3 del Articulo',
		String description = String.format("%s. %s.",
				StringUtils.trimToEmpty(ere1l.getDescripcionDelArticulo1()),
				StringUtils.trimToEmpty(ere1l.getDescripcionDelArticulo2()));
		item.setDescription(description);
//		serial_number` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de serie',
//		serial_date` date DEFAULT NULL COMMENT 'Fecha de serializacion',
		item.setPrice(Double.valueOf(ere1l.getPrecioBrutoUnitario_AAB_()));
		item.setStatus(ProductStatus.ACTIVE);
//		expenses_percent` double DEFAULT '0' COMMENT 'Gastos porcentuales del Articulo',
//		expenses_fixed` double DEFAULT '0' COMMENT 'Gastos fijos del Articulo',
//		profit_percent` double DEFAULT '0' COMMENT 'Porcentaje de beneficio del Articulo',
//		purchase_price` double DEFAULT '0' COMMENT 'Precio de compra del Articulo',
		item.setInternet(false);
		item.setBarcode(ere1l.getCodigoDeArticuloEAN_13ODUN_14().trim());
//		pack_format_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de formato',
//		pack_units` int(4) DEFAULT '0' COMMENT 'Numero de unidades por formato',
//		pack_units_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de envase',
//		pack_measurement` double DEFAULT '0' COMMENT 'Medida envasada',
//		pack_measurement_tag` int(4) DEFAULT NULL COMMENT 'Identificador de la Etiqueta de unidad de medida',
		item = (Item) itemBean.insert(item);
		
		return item;
	}
	
	private Product searchProduct(ERE1L ere1l) throws ManagerBeanException {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_CODE), StringUtils.trimToNull(ere1l.getCodigoInternoArticuloCliente_IN_()));
			List<ITransferObject> list = productBean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				return (Product) list.get(0);
			} else {
				return createProduct(ere1l);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}
	
	private Product createProduct(ERE1L ere1l) throws ManagerBeanException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Product product = new Product();
		product.setCode(StringUtils.trimToEmpty(ere1l.getCodigoInternoArticuloCliente_IN_()));
		product.setName(StringUtils.abbreviate(ere1l.getDescripcionDelArticulo1().trim(), 64));
		product.setKind(ProductKind.SALE);
//		brand` int(4) DEFAULT NULL COMMENT 'Marca Comercial del Producto',
//		category` int(4) DEFAULT NULL COMMENT 'Categoria del Producto',
//		inventoriable` tinyint(1) DEFAULT NULL COMMENT 'Indica si el Producto es inventariable',
		product.setSerializable(false);
		product.setLotable(false);
		product.setStatus(ProductStatus.ACTIVE);
//		vat` int(4) DEFAULT NULL COMMENT 'IVA del Producto',
//		retention` int(4) DEFAULT NULL COMMENT 'Retencion del Producto',
		product.setType(ProductType.COMMERCIAL_PRODUCT);
		product.setManufactured(true);
		product.setComposition(false);
		product.setCompositionPrice(false);
		product.setPackaged(false);
		product = (Product) productBean.insert(product);
		return product;
	}
	
}
