package com.code.aon.ui.sales.udapa;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.code.aon.config.Tag;
import com.code.aon.customer.Customer;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
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
		getLogPanel().reset();
	}
	
	public void onImportFileHide(ActionEvent event) {
		getLogPanel().finish();
	}
	
	public void onImportFile(ActionEvent event) {
		importFile(event, false);
	}
	
	public void importFile(ActionEvent event, boolean testing) {
		UdapaSalesReader reader = new UdapaSalesReader();
		ERE1C ere1c = null;
		try {
			ere1c = reader.readFile(aonFile.openStream());
			getLogPanel().info("Fichero leido correctamente");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		getLogPanel().info("Inicio del proceso.");
		createSales(event, ere1c, testing);
		getLogPanel().info("Proceso finalizado.");
		setAonFile(null);
		setShowImportFileWindow(false);
	}
	
	public void createSales(ActionEvent event, ERE1C ere1c, boolean testing) {
//		System.out.println(ere1c.toString());
//		System.out.println(ere1c.ere1lList.get(0).toString());
		Sales sales = (Sales) controller.getTo();
		try {
			if(ere1c.getCodigoPuntoDeEntrega_DP_()==null){
				getLogPanel().error("Imposible continuar, el fichero no contiene codigo de punto de entrega.");
				getLogPanel().error("Comprador: " + ere1c.getCodigoComprador_BY_());
			} else {
				RegistryNote customerRegistryNote = searchCustomerNote(ere1c.getCodigoPuntoDeEntrega_DP_().trim());
				if(customerRegistryNote==null 
						|| customerRegistryNote.getRegistry()==null 
						|| customerRegistryNote.getRegistry().getId()==null){
					getLogPanel().error("No existe el cliente con el codigo de punto de entrega " + ere1c.getCodigoPuntoDeEntrega_DP_());
				} else {
					Customer customer = obtainCustomer(customerRegistryNote.getRegistry().getId());
					getLogPanel().info("Cliente detectado con el codigo de punto de entrega " + ere1c.getCodigoPuntoDeEntrega_DP_());
					RegistryAddress address = obtainAddress(Integer.valueOf(customerRegistryNote.getDescription()));
					getLogPanel().info("Dirección localizada: " + address.getFullAddress());
					
					IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_PURCHASE_REFERENCE), ere1c.getNumeroDePedido());
					if( salesBean.getCount(criteria) > 0) {
						getLogPanel().error("Ya existe un pedido con la misma referencia de compra " + ere1c.getNumeroDePedido());
					}
					
					List<ERE1L> undefinedItems = new LinkedList<ERE1L>();
					ere1c.ere1lList.forEach(ere1l -> {
						RegistryItem rItem = searchRegistryItem(ere1l, customer);
						if(rItem==null) {
							undefinedItems.add(ere1l);
						}
					});
					
					if(!undefinedItems.isEmpty()){
						undefinedItems.forEach(ere1l -> {
							getLogPanel().error("Linea " + ere1l.getNumeroDeLineaArticulo() 
									+ " : La referencia de producto: " + StringUtils.trimToEmpty(ere1l.getDescripcionDelArticulo1())
									+ " (Cod. cliente: " + StringUtils.trimToEmpty(ere1l.getCodigoInternoArticuloCliente_IN_()) + ")"
									+ " (Cod. unidad exp.: " + StringUtils.trimToEmpty(ere1l.getCodigoUnidadDeExpedicion_1__EN_()) + ")"
									+ " (Cod. EAN: " + StringUtils.trimToEmpty(ere1l.getCodigoDeArticuloEAN_13ODUN_14()) + ")"
									+ " no existe para el cliente " + customer.getRegistry().getFullName());
						});
						getLogPanel().info("PROCESO ABORTADO");
					} else {
						SalesController salesController = (SalesController) controller;
						sales.setCustomer(customer);
						sales.setShippingAddress(null);
						sales.setScope(customer.getScope());
						salesController.loadAddresses(customer.getId());
						salesController.loadProjects(customer.getId());
						salesController.loadCommercial(customer.getId());
						salesController.loadDefaultPayMethod(customer.getRegistry(), true);
						
						if(address==null || address.getId()==null){
							getLogPanel().error("No se ha podido localizar la dirección (plataforma) para el pedido " + sales.getReferenceCode());
							sales.setShippingAddress((RegistryAddress) salesController.getAddresses().get(0).getValue());
						} else {
							sales.setShippingAddress(address);
						}
						
						try {
							sales.setIssueDate(dateFormatter.parse(ere1c
									.getFechaDeServicio1().toString()));
						} catch (ParseException e) {
							getLogPanel().error("No se ha podido convertir la fecha: " + ere1c.getFechaDelDocumento_137__102_());
							sales.setIssueDate(null);
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
						
						if (testing) {
							salesController.accept(event);
						}
						
						getLogPanel().info("Pedido creado: " + sales.getReferenceCode());
						
						SalesDetailController detailController = (SalesDetailController) FormUtil.getController(ISalesConstants.SALES_DETAIL_CONTROLLER_NAME);
						for(ERE1L ere1l: ere1c.ere1lList){
							RegistryItem rItem = searchRegistryItem(ere1l, customer);
							if(rItem==null) {
								getLogPanel().error("Linea " + ere1l.getNumeroDeLineaArticulo() 
										+ " omitida: La referencia de producto: " + ere1l.getDescripcionDelArticulo1().trim()
										+ " no existe para el cliente " + customer.getRegistry().getFullName()
										+ " (CodigoInternoArticuloCliente: " + StringUtils.trimToNull(ere1l.getCodigoInternoArticuloCliente_IN_())
										+ ", CodigoUnidadDeExpedicion: " + StringUtils.trimToNull(ere1l.getCodigoUnidadDeExpedicion_1__EN_())
										+ ", CodigoDeArticuloEAN: " + StringUtils.trimToNull(ere1l.getCodigoDeArticuloEAN_13ODUN_14())
										+ ")");
							} else {
								Integer line = Integer.valueOf(ere1l.getNumeroDeLineaArticulo());
								String description = String.format("%s. %s. %s.",
										StringUtils.trimToEmpty(ere1l
												.getDescripcionDelArticulo1()), StringUtils
												.trimToEmpty(ere1l
														.getDescripcionDelArticulo2()),
														StringUtils.trimToEmpty(ere1l
																.getDescripcionDelModelo_BRN_()));
								Double ediLineQuantity = ere1l.getCantidadPedida_21_();
								Tag customerPackingTag = searchPackingTag(customerRegistryNote);
								Double quantity = obtainQuantity(ediLineQuantity, customerPackingTag, rItem);
								Double price = rItem.getPrice();
								if (testing) {
									detailController.onReset(event);
									SalesDetail detail = (SalesDetail) detailController.getTo();
									detail.setSales(sales);
									detail.setItem(rItem.getItem());
									detail.setLine(line);
									detail.setDescription(description);
									detail.setQuantity(quantity);
									detail.setPrice(price);
//									detail.setDiscountExpression(detail.getDiscountExpression().getDiscountExpr());
									detail.setTaxes(0.0);
									detail.setStatus(SalesDetailStatus.PENDING);
									detail.setOfferDetail(null);
									detail.setDelivered(0.0);
									detailController.onAccept(event);
								}
								Tag itemPackMeasurementTag = rItem.getItem().getPackMeasurementTag();
								Tag itemPackingTag = rItem.getItem().getPackUnitsTag();
								Tag itemPackFormatTag = rItem.getItem().getPackFormatTag();
								double itemPackMeasurement = rItem.getItem().getPackMeasurement();
								int itemPackUnits = rItem.getItem().getPackUnits();
								getLogPanel().info(String.format("Linea de pedido %1$d creada: %2$.2f unidades ", line, quantity)
										+ (quantity.equals(ediLineQuantity)
												?String.format("(%1$.2f %2$s)", ediLineQuantity, itemPackMeasurementTag.getName()):"")
										+ (quantity.equals(ediLineQuantity * itemPackMeasurement)
												?String.format("(%1$.2f %2$s x %3$.2f %4$s)", ediLineQuantity, itemPackingTag.getName(), itemPackMeasurement, itemPackMeasurementTag.getName()):"")
										+ (quantity.equals(ediLineQuantity * itemPackMeasurement * itemPackUnits)
												?String.format("(%1$.2f %2$s x %3$d %4$s x %5$.2f %6$s)", ediLineQuantity, itemPackFormatTag.getName(), itemPackUnits, itemPackingTag.getName(), itemPackMeasurement, itemPackMeasurementTag.getName()):"")
										+ " de " + description);
							}
						}
					}
					
				}
			}
		} catch (ManagerBeanException e) {
			getLogPanel().error(e.getMessage());
			LOGGER.error(e.getMessage());
		}

	}
	
	private Double obtainQuantity(Double quantity, Tag customerPackingTag,
			RegistryItem rItem) {
		Tag itemPackFormatTag = rItem.getItem().getPackFormatTag();
		Tag itemPackMeasurementTag = rItem.getItem().getPackMeasurementTag();
		Tag itemPackingTag = rItem.getItem().getPackUnitsTag();
		double itemPackMeasurement = rItem.getItem().getPackMeasurement();
		int itemPackUnits = rItem.getItem().getPackUnits();
		if (customerPackingTag != null && itemPackingTag != null
				&& itemPackFormatTag != null && itemPackMeasurementTag != null) {
			if (customerPackingTag.getId().equals(itemPackMeasurementTag.getId())) {
				return quantity;
			} else if (customerPackingTag.getId().equals(
					itemPackingTag.getId())) {
				return quantity * itemPackMeasurement;
			} else if (customerPackingTag.getId().equals(
					itemPackFormatTag.getId())) {
				return quantity * itemPackMeasurement * itemPackUnits;
			}
		}
		return quantity;
	}

	private RegistryNote searchCustomerNote(String customerCode) {
		RegistryNote rNote = null;
		try {
			IManagerBean rnoteBean = BeanManager.getManagerBean(RegistryNote.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rnoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.FACTURAE);
			criteria.addExpression(ExpressionUtilities.getLikeExpression(
					rnoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_COMMENTS),
					"%" + CustomerEdiSupportController.PTO_ENTREGA + "="
							+ customerCode + ";%"));
			List<ITransferObject> list = rnoteBean.getList(criteria);
			rNote = list != null && !list.isEmpty() ? ((RegistryNote) list
					.get(0)) : null;
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		return rNote;
	}
	
	private Tag searchPackingTag(RegistryNote rNote) {
		String value = rNote.getComments();
		Matcher m;
		Pattern p = Pattern.compile(CustomerEdiSupportController.MEDIDA + "=([^;]*);");
		try {
			if (value != null && (m = p.matcher(value)).find()) {
				IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
				return (Tag) tagBean.get(Integer.valueOf(m.group(1)));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private Customer obtainCustomer(Integer registryId) {		
		if(registryId!=null){
			try {
				IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
				return (Customer) customerBean.get(registryId);
			} catch (ManagerBeanException ex) {
				getLogPanel().error(ex.getMessage());
				LOGGER.error(ex.getMessage());
			}
		}
		return null;
	}

	private RegistryAddress obtainAddress(Integer addressId) {		
		if(addressId!=null){
			try {
				IManagerBean addressBean = BeanManager.getManagerBean(RegistryAddress.class);
				return (RegistryAddress) addressBean.get(addressId);
			} catch (ManagerBeanException ex) {
				getLogPanel().error(ex.getMessage());
				LOGGER.error(ex.getMessage());
			}
		}
		return null;
	}
	
	private RegistryItem searchRegistryItem(ERE1L ere1l, Customer customer) {
		try {			
			String itemCustomerCode = StringUtils.trimToNull(ere1l.getCodigoInternoArticuloCliente_IN_());
			if(itemCustomerCode==null){
				itemCustomerCode = StringUtils.trimToNull(ere1l.getCodigoUnidadDeExpedicion_1__EN_());
			}
			if(itemCustomerCode==null){
				itemCustomerCode = StringUtils.trimToNull(ere1l.getCodigoDeArticuloEAN_13ODUN_14());
			}
			if(itemCustomerCode!=null){
				IManagerBean itemBean = BeanManager.getManagerBean(RegistryItem.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE), itemCustomerCode);
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), customer.getRegistry().getId());
				List<ITransferObject> list = itemBean.getList(criteria);
				if(list!=null && !list.isEmpty()){
					return (RegistryItem) list.get(0);
				}
			}
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		return null;
	}
	
	private LogPanelController getLogPanel(){
		return LogPanelController.getInstance();
	}

	
}
