package com.code.aon.ui.finance.file.edi;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.customer.Customer;
import com.code.aon.customer.IEdiSupport;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.finance.controller.SaleInvoiceDetailController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCL;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCP;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCT;
import com.esferalia.aon.seres.reader.connect.ConnectInvoiceReader;

public class EdiInvoiceImporterHandler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EdiInvoiceImporterHandler.class);
	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyyMMddhhmm");
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private final String SERIE_NUMBER_PATTERN = "^(\\w*)[\\W]?(\\d+)$";

	private IController controller;
	private AonFile aonFile;
	private boolean showImportFileWindow;

	public EdiInvoiceImporterHandler(IController controller) {
		this.controller = controller;
	}
	
	public SimpleDateFormat getDateTimeFormatter() {
		return dateTimeFormatter;
	}
	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
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
		setShowImportFileWindow(false);
		ConnectInvoiceReader reader = new ConnectInvoiceReader();
		RECTL rectl = null;
		try {
			rectl = reader.readFile(aonFile.openStream());
			getLogPanel().info("Fichero leido correctamente");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		getLogPanel().info("Inicio del proceso de importacion");
		createInvoice(event, rectl, testing);
		getLogPanel().info("Proceso finalizado correctamente");
		setAonFile(null);
	}
	
	public String obtainCustomerCode(RECTL rectl) {
		return rectl.sincpList.stream()
				.filter(o -> SINCP.SINCP_2.COMPRADOR_BY.getValue().equals(o.getCalificadorDelInterlocutor()))
				.map(SINCP::getCodigoInterlocutor)
				.findFirst()
				.orElse(rectl.getCodigoEmisor());
	}

	public void createInvoice(ActionEvent event, RECTL rectl, boolean testing) {
		Invoice invoice = (Invoice) controller.getTo();

		String customerCode = obtainCustomerCode(rectl);
		if (customerCode == null) {
			getLogPanel()
					.error("Imposible continuar, el fichero no contiene codigo de punto de entrega.");
			getLogPanel().error("Comprador: " + customerCode);
		} else {
			RegistryNote customerRegistryNote = searchCustomerRNote(customerCode);
			if(customerRegistryNote==null 
					|| customerRegistryNote.getRegistry()==null 
					|| customerRegistryNote.getRegistry().getId()==null){
				getLogPanel().error("No existe el cliente con CodigoEmisor " + customerCode);
				getLogPanel().info("PROCESO ABORTADO");
				LOGGER.error("No existe el cliente con CodigoEmisor " + customerCode);
			} else {
				Customer customer = obtainCustomer(customerRegistryNote.getRegistry().getId());
				getLogPanel().info(
						"Cliente detectado con el codigo de punto de entrega "
								+ customerCode);

				if(rectl.sincc.getNumeroDeFactura()!=null){
					String serie = null;
					String number = null;
					if(rectl.sincc.getNumeroDeFactura().matches(SERIE_NUMBER_PATTERN)){
						serie = rectl.sincc.getNumeroDeFactura().replaceAll(SERIE_NUMBER_PATTERN, "$1");
						number = rectl.sincc.getNumeroDeFactura().replaceAll(SERIE_NUMBER_PATTERN, "$2");
					}
					if(serie!=null && serie.length()>5){
						invoice.setSeries("SERES");
						invoice.setNumber(0);
						invoice.setComments("Nº Factura original " + rectl.sincc.getNumeroDeFactura());
					} else {
						invoice.setSeries(serie);
						invoice.setNumber(Integer.parseInt(number));
					}
				}
				invoice.setType(InvoiceType.SALES);
				invoice.setRegistry(customer.getRegistry());
				invoice.setScope(customer.getScope());

				try {
					invoice.setIssueDate(dateFormatter.parse(rectl.sincc
							.getFechaDeFactura().toString()));
				} catch (ParseException e) {
					getLogPanel().error(
							"No se ha podido convertir la fecha: "
									+ rectl.sincc.getFechaDeFactura());
					invoice.setIssueDate(null);
				}

				invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
				invoice.setStatus(InvoiceStatus.PENDING);

				String remarks = "Pedido: " + rectl.sincc.getNumeroDePedido_ON_();
				remarks += System.getProperty("line.separator");
				for (SINCT value : rectl.sinctList) {
					remarks += (StringUtils.isNotBlank(value.getTexto1()) ? value
							.getTexto1().trim() : "")
							+ (StringUtils.isNotBlank(value.getTexto2()) ? ", "
									+ value.getTexto2().trim() : "")
							+ (StringUtils.isNotBlank(value.getTexto3()) ? ", "
									+ value.getTexto3().trim() : "")
							+ (StringUtils.isNotBlank(value.getTexto4()) ? ", "
									+ value.getTexto4().trim() : "")
							+ (StringUtils.isNotBlank(value.getTexto5()) ? ", "
									+ value.getTexto5().trim() : "")
							+ System.getProperty("line.separator");
				}
				invoice.setRemarks(remarks);
				if(rectl.sincc.getDocumentoRectificado_Sustituido()!=null){
					invoice.setComments(invoice.getComments() + "\nFactura rectificativa de " + rectl.sincc.getDocumentoRectificado_Sustituido());
				}

				List<InvoiceDetail> detailList = new LinkedList<InvoiceDetail>();
				for (SINCL line : rectl.sinclList) {
					RegistryItem rItem = searchRegistryItem(line, customer);
					if (rItem == null) {
						getLogPanel()
								.error("Linea "
										+ line.getNumeroDeLinea()
										+ " omitida: La referencia de producto: "
										+ line.getDescripcionDelArticulo()
										+ " (Cod. referencia: "
										+ obtainItemCustomerCode(line)
										+ ")" + " no existe para el cliente "
										+ customer.getRegistry().getFullName());
					} else {
						InvoiceDetail detail;
						try {
							detail = (InvoiceDetail) BeanManager
									.getManagerBean(InvoiceDetail.class)
									.createNewTo();
							detail.setItem(rItem.getItem());
							detail.setLine(Integer.valueOf(line
									.getNumeroDeLinea()));
							String description = String.format("%s. %s.",
									StringUtils.trimToEmpty(line
											.getDescripcionDelArticulo()),
									StringUtils.trimToEmpty(line
											.getDescripcionDelArticulo()));
							detail.setDescription(description);
							Double quantity = (-1)
									* Math.abs(Double.valueOf(line
											.getUnidadesEntregadas()));
							detail.setQuantity(quantity);
							Double price = Double.valueOf(line
									.getPrecioNetoUnitario());
							if (price.equals(0.0)) {
								price = rItem.getPrice();
								getLogPanel().warn(
										"Precio no definido en la linea "
												+ detail.getLine() + " de "
												+ detail.getQuantity()
												+ " unidades de "
												+ detail.getDescription());
							}
							detail.setPrice(price);
							detailList.add(detail);
						} catch (ManagerBeanException e) {
							LOGGER.error(e.getMessage());
						}
					}
				}

				SaleInvoiceController saleInvoiceController = (SaleInvoiceController) controller;
				if (!testing) {
					saleInvoiceController.accept(event);
				}
				getLogPanel().info("Factura creada: " + invoice.getReferenceCode());
				SaleInvoiceDetailController detailController = (SaleInvoiceDetailController) FormUtil
						.getController(IFinanceConstants.SALE_INVOICE_DETAIL_CONTROLLER_NAME);
				for (InvoiceDetail _detail : detailList) {
					if (!testing) {
						detailController.onReset(event);
						InvoiceDetail detail = (InvoiceDetail) detailController.getTo();
						detail.setInvoice(invoice);
						detail.setItem(_detail.getItem());
						detail.setLine(_detail.getLine());
						detail.setDescription(_detail.getDescription());
						detail.setQuantity(_detail.getQuantity());
						detail.setPrice(_detail.getPrice());
						detailController.onAccept(event);
					}

					getLogPanel()
							.info("Linea de factura " + _detail.getLine()
									+ " creada: " + _detail.getQuantity()
									+ " unidades de " + _detail.getDescription());
				}

			}
		}

	}

	protected RegistryNote searchCustomerRNote(String customerCode) {
		return searchCustomerRNote(customerCode, IEdiSupport.PTO_ENTREGA);
	}
	
	private RegistryNote searchCustomerRNote(String customerCode, String type) {
		RegistryNote rNote = null;
		try {
			IManagerBean rnoteBean = BeanManager
					.getManagerBean(RegistryNote.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					rnoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE),
					NoteType.FACTURAE);
			criteria.addExpression(ExpressionUtilities.getLikeExpression(
					rnoteBean.getFieldName(IEntityAlias.REGISTRY_NOTE_COMMENTS),
					"%" + type + "=" + customerCode + ";%"));
			List<ITransferObject> list = rnoteBean.getList(criteria);
			rNote = list != null && !list.isEmpty() ? ((RegistryNote) list
					.get(0)) : null;
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		return rNote;
	}
	
	protected Customer obtainCustomer(Integer registryId) {		
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
	
	protected RegistryAddress obtainAddress(Integer addressId) {		
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

	private String obtainItemCustomerCode(SINCL sincl) {
		String itemCustomerCode = StringUtils.trimToNull(sincl
				.getCodigoInternoArticuloCliente_IN_());
		if (itemCustomerCode == null) {
			itemCustomerCode = StringUtils.trimToNull(sincl
					.getCodigoUnidadDeExpedicion_EN_());
		}
		if (itemCustomerCode == null) {
			itemCustomerCode = StringUtils.trimToNull(sincl
					.getCodigoArticulo());
		}
		return itemCustomerCode;
	}
	
	private RegistryItem searchRegistryItem(SINCL sincl, Customer customer) {
		try {
			String itemCustomerCode = obtainItemCustomerCode(sincl);
			if (itemCustomerCode != null) {
				IManagerBean itemBean = BeanManager
						.getManagerBean(RegistryItem.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(
						itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID),
						customer.getId());
				criteria.addEqualExpression(
						itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE),
						itemCustomerCode);
				List<ITransferObject> list = itemBean.getList(criteria);
				if (list != null && !list.isEmpty()) {
					return (RegistryItem) list.get(0);
				}
			}
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		return null;
	}

	private LogPanelController getLogPanel() {
		return LogPanelController.getInstance();
	}

}
