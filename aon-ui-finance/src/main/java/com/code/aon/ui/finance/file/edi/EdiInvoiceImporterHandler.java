package com.code.aon.ui.finance.file.edi;

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
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.finance.controller.SaleInvoiceDetailController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCC;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCL;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCT;
import com.esferalia.aon.file.seres.util.reader.udapa.UdapaInvoiceReader;

public class EdiInvoiceImporterHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 210023208960242355L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EdiInvoiceImporterHandler.class);
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private IController controller;
	private AonFile aonFile;
	private boolean showImportFileWindow;
			
	
	public EdiInvoiceImporterHandler(IController controller) {
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
		UdapaInvoiceReader reader = new UdapaInvoiceReader();
		SINCC sincc = null;
		try {
			sincc = reader.readFile(aonFile.openStream());
			getLogPanel().info("Fichero leido correctamente");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		getLogPanel().info("Inicio del proceso de importacion");
		createInvoice(event, sincc);
		getLogPanel().info("Proceso finalizado correctamente");
		setAonFile(null);
		setShowImportFileWindow(false);
	}
	
	public void createInvoice(ActionEvent event, SINCC sincc) {
		Invoice invoice = (Invoice) controller.getTo();
		
		String customerInvoiceCode = sincc.getCodigoReceptorDeLaFactura_aQuienSeFactura_();
		if(customerInvoiceCode==null){
			getLogPanel().error("Imposible continuar, el fichero no contiene codigo de punto de entrega.");
			getLogPanel().error("Comprador: " + customerInvoiceCode);
		} else {
			Customer customer = searchCustomer(customerInvoiceCode.trim());
			if(customer==null){
				getLogPanel().error("No existe el cliente con el codigo de punto de entrega " + customerInvoiceCode);
			} else {
				getLogPanel().info("Cliente detectado con el codigo de punto de entrega " + customerInvoiceCode);
				
				SaleInvoiceController saleInvoiceController = (SaleInvoiceController) controller;
				invoice.setType(InvoiceType.SALES);
				invoice.setRegistry(customer.getRegistry());
				invoice.setScope(customer.getScope());
				
				try {
					invoice.setIssueDate(dateFormatter.parse(sincc
							.getFechaFactura().toString()));
				} catch (ParseException e) {
					getLogPanel().error(
							"No se ha podido convertir la fecha: "
									+ sincc.getFechaFactura());
					invoice.setIssueDate(null);
				}
				
				invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
				invoice.setStatus(InvoiceStatus.PENDING);
				
				String remarks = "Pedido: " + sincc.getNumeroDePedido_ON_();
				remarks += System.getProperty("line.separator");
				for(SINCT value: sincc.sinctList){
					remarks += (StringUtils.isNotBlank(value.getTexto1())?value.getTexto1().trim():"") +
							(StringUtils.isNotBlank(value.getTexto2())?", " + value.getTexto2().trim():"") +
							(StringUtils.isNotBlank(value.getTexto3())?", " + value.getTexto3().trim():"") +
							(StringUtils.isNotBlank(value.getTexto4())?", " + value.getTexto4().trim():"") +
							(StringUtils.isNotBlank(value.getTexto5())?", " + value.getTexto5().trim():"") + 
							System.getProperty("line.separator");
				}
				invoice.setRemarks(remarks);
				invoice.setComments("");
				
				saleInvoiceController.accept(event);
				
				getLogPanel().info("Factura creado: " + invoice.getReferenceCode());
				
				SaleInvoiceDetailController detailController = (SaleInvoiceDetailController) FormUtil.getController(IFinanceConstants.SALE_INVOICE_DETAIL_CONTROLLER_NAME);
				for(SINCL line: sincc.sinclList){
					RegistryItem rItem = searchRegistryItem(line, customer);
					if(rItem==null) {
						getLogPanel().error("Linea " + line.getNumeroDeLinea() 
								+ " omitida: La referencia de producto: " + line.getDescripcionDelArticulo().trim()
								+ " (Cod. referencia: " + line.getCodigoUnidadDeExpedicion_EN_().trim() + ")"
								+ " no existe para el cliente " + customer.getRegistry().getFullName());
					} else {
						detailController.onReset(event);
						InvoiceDetail detail = (InvoiceDetail) detailController.getTo();
						detail.setInvoice(invoice);							
						detail.setItem(rItem.getItem());
						detail.setLine(Integer.valueOf(line.getNumeroDeLinea()));
						String description = String.format("%s. %s.",
								StringUtils.trimToEmpty(line
										.getDescripcionDelArticulo()), StringUtils
										.trimToEmpty(line
												.getDescripcionDelArticulo()));
						detail.setDescription(description);
						detail.setQuantity(Double.valueOf(line.getCantidadFacturada_47_()));
						Double price = Double.valueOf(line.getPrecioBrutoUnitario());
						if(price.equals(0.0)){
							price = rItem.getPrice();
							getLogPanel().warn("Precio no definido en la linea " + detail.getLine() 
									+ " de " + detail.getQuantity() + " unidades de " + detail.getDescription());
						}
						detail.setPrice(price);
						detailController.onAccept(event);
						getLogPanel().info("Linea de factura " + detail.getLine() 
								+ " creada: " + detail.getQuantity() + " unidades de " + detail.getDescription());
					}
				}
				
			}
		}
		
	}
	
	private Customer searchCustomer(String customerCode) {
		Integer registryId = null;
		try {
			IManagerBean rnoteBean = BeanManager.getManagerBean(RegistryNote.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getLikeExpression(
					rnoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_COMMENTS),
					"%" + CustomerEdiSupportController.FACTURA + "="
							+ customerCode + ";%"));
			List<ITransferObject> list = rnoteBean.getList(criteria);
			registryId = list != null && !list.isEmpty() ? ((RegistryNote) list
					.get(0)).getRegistry().getId() : null;
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		
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

	
	private RegistryItem searchRegistryItem(SINCL sincl, Customer customer) {
		try {
			String itemCustomerCode = StringUtils.trimToNull(sincl.getCodigoUnidadDeExpedicion_EN_());
			if(itemCustomerCode==null){
				itemCustomerCode = StringUtils.trimToNull(sincl.getCodigoArticulo());
			}
			if(itemCustomerCode!=null){
				IManagerBean itemBean = BeanManager.getManagerBean(RegistryItem.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE), itemCustomerCode);
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
