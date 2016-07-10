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
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryItem;
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
					
					IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_PURCHASE_REFERENCE), ere1c.getNumeroDePedido());
					if( salesBean.getCount(criteria) > 0) {
						AonUtil.addErrorMessage("Ya existe un pedido con la misma referencia de compra " + ere1c.getNumeroDePedido());
					}
					
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
						RegistryItem rItem = searchRegistryItem(ere1l, customer);
						if(rItem!=null) {
							detailController.onReset(event);
							SalesDetail detail = (SalesDetail) detailController.getTo();
							detail.setSales(sales);							
							detail.setItem(rItem.getItem());
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
//							detail.setDiscountExpression(detail.getDiscountExpression().getDiscountExpr());
//							detail.setTaxes(detail.getTaxes());
							detail.setStatus(SalesDetailStatus.PENDING);
//							detail.setOfferDetail(null);
							detail.setDelivered(0.0);
							detailController.onAccept(event);
						}
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
	
	private RegistryItem searchRegistryItem(ERE1L ere1l, Customer customer) {
		try {
			IManagerBean itemBean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE), ere1l.getCodigoDeArticuloEAN_13ODUN_14().trim());
			List<ITransferObject> list = itemBean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				return (RegistryItem) list.get(0);
			} else {
				AonUtil.addErrorMessage("La referencia de producto: " + ere1l.getDescripcionDelArticulo1().trim()
						+ " no existe para el cliente" + customer.getRegistry().getFullName()
						+ " (Cod. EAN: " + ere1l.getCodigoDeArticuloEAN_13ODUN_14().trim() + ")");
				return null;
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	
}
