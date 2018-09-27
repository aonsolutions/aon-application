package com.code.aon.ui.sales.importer.edi;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import com.code.aon.config.Tag;
import com.code.aon.customer.Customer;
import com.code.aon.customer.IEdiSupport;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.product.util.DiscountExpression;
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
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesController;
import com.code.aon.ui.sales.controller.SalesDetailController;
import com.code.aon.ui.sales.importer.edi.FtpSalesDownloadHandler.FtpFileItem;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1L;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1P;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1T;
import com.esferalia.aon.file.seres.connect.sales.v2.data.RECTL;

public class EdiSalesImporterHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(EdiSalesImporterHandler.class);
	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyyMMddhhmm");
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private IController controller;
	private AonFile aonFile;
	private boolean showImportFileWindow;
	private boolean success;
			
	
	public EdiSalesImporterHandler(IController controller) {
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
	public boolean isSuccess() {
		return success;
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
		importFile(event, null, false);
	}
		
	public void importFile(ActionEvent event, FtpFileItem ftpFileItem, boolean testing) {
		success = true;
		String series = ((Sales) controller.getTo()).getSeries();
		getLogPanel().info("Inicio del proceso.");
		if(ftpFileItem.getOrders()!=null && ftpFileItem.getOrders().size()>0) {
			for(int idx = 0; idx<ftpFileItem.getOrders().size(); idx++) {
				RECTL rectl = ftpFileItem.getOrders().get(idx).getRectl();
				RegistryNote customerRegistryNote = ftpFileItem.getOrders().get(idx).getRegistryNote();
				if(ftpFileItem.getOrders().size()>1)
					getLogPanel().info("Procesando pedido " + (idx+1) + " de " + ftpFileItem.getOrders().size());
				createSales(event, rectl, customerRegistryNote, series, testing);
			}
			if(testing)
				getLogPanel().warn("TEST activado: el pedido no se ha creado.");
			else
				controller.onSelect(event);
		} else {
			getLogPanel().info("No se han detectado pedidos.");	
		}
		getLogPanel().info("Proceso finalizado.");
		setAonFile(null);
		setShowImportFileWindow(false);
	}
	
	public String obtainCustomerCodeSales(RECTL rectl) {
		return rectl.ere1pList.stream()
//				.filter(o -> ERE1P.ERE1P_2.COMPRADOR_BY.getValue().equals(o.getCalificadorDelInterlocutor()))
				.filter(o -> ERE1P.ERE1P_2.PUNTO_DESTINO_DE_LA_MERCANCIA_DP.getValue().equals(o.getCalificadorDelInterlocutor()))
				.map(ERE1P::getCodigoInterlocutor)
				.findFirst()
				.orElse(rectl.getCodigoEmisor());	
	}
	
	public void createSales(ActionEvent event, RECTL rectl, RegistryNote customerRegistryNote, String series, boolean testing) {
//		System.out.println(ere1c.toString());
//		System.out.println(ere1c.ere1lList.get(0).toString());
		
		if(customerRegistryNote==null){
			String customerCode = obtainCustomerCodeSales(rectl);
			if(customerCode==null){
				success = false;
				getLogPanel().error("Imposible continuar, el fichero no contiene el codigo de cliente.");
				getLogPanel().error("CodigoEmisor: " + customerCode);
				getLogPanel().info("PROCESO ABORTADO");
				LOGGER.error("Imposible continuar, el fichero no contiene el codigo de cliente.");
				LOGGER.error("Codigo de cliente: " + customerCode);
			} else {
				List<RegistryNote> customerRegistryNoteList = searchCustomerRNote(customerCode);
				if(customerRegistryNoteList!=null){
					if(customerRegistryNoteList.size()==1){
						customerRegistryNote = customerRegistryNoteList.get(0);
					} else if(customerRegistryNoteList.size()>1){
						success = false;
						getLogPanel().error("Se han encontrado varias direcciones para el codigo " + customerCode);
						getLogPanel().info("PROCESO ABORTADO");
					} else {
						success = false;
						getLogPanel().error("No existe el cliente con el codigo " + customerCode);
						getLogPanel().info("PROCESO ABORTADO");
						LOGGER.error("No existe el cliente con el codigo " + customerCode);
					}
				}
				if(customerRegistryNote==null 
						|| customerRegistryNote.getRegistry()==null 
						|| customerRegistryNote.getRegistry().getId()==null){
					success = false;
					getLogPanel().error("No existe el cliente con el codigo " + customerCode);
					getLogPanel().info("PROCESO ABORTADO");
					LOGGER.error("No existe el cliente con el codigo " + customerCode);
				}
			}
		}
		
		if(customerRegistryNote!=null
			&& customerRegistryNote.getRegistry()!=null 
				&& customerRegistryNote.getRegistry().getId()!=null){
		
			controller.onReset(event);
			Sales sales = (Sales) controller.getTo();
			sales.setSeries(series);
			try {
				Customer customer = obtainCustomer(customerRegistryNote.getRegistry().getId());
				getLogPanel().info("Cliente detectado: " + customer.getRegistry().getFullName());
				RegistryAddress address = obtainAddress(Integer.valueOf(customerRegistryNote.getDescription()));
				getLogPanel().info("Dirección localizada: " + address.getFullAddress());
				
				IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_PURCHASE_REFERENCE), rectl.ere1c.getNumeroDePedido());
				if( salesBean.getCount(criteria) > 0) {
					getLogPanel().error("Ya existe un pedido con la misma referencia de compra " + rectl.ere1c.getNumeroDePedido());
				}
				
				List<ERE1L> undefinedItems = new LinkedList<ERE1L>();
				rectl.ere1lList.forEach(ere1l -> {
					RegistryItem rItem = searchRegistryItem(ere1l, customer);
					if(rItem==null) {
						undefinedItems.add(ere1l);
					}
				});
				
				if(!undefinedItems.isEmpty()){
					getLogPanel().info("Productos no localizados: " + undefinedItems.size());
					undefinedItems.forEach(ere1l -> {
						getLogPanel().error(obtainProductErrorLabel(ere1l, customer));
					});
					success = false;
					getLogPanel().info("PROCESO ABORTADO");
					LOGGER.error("Se han encontrado items que no existen: " + undefinedItems.size());
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
						sales.setIssueDate(dateTimeFormatter.parse(rectl.ere1c
								.getFecha_horaDocumento_137__102_203_().toString()));
					} catch (ParseException e1) {
						try {
							sales.setIssueDate(dateFormatter.parse(rectl.ere1c
									.getFecha_horaDocumento_137__102_203_().toString()));
						} catch (ParseException e2) {
							getLogPanel().error("No se ha podido convertir la fecha: " + rectl.ere1c.getFecha_horaDocumento_137__102_203_());
							sales.setIssueDate(null);
						}
					}
					sales.setDocumentType(DocumentType.NORMAL);
					sales.setSecurityLevel(SecurityLevel.OFFICIAL);
					sales.setStatus(SalesStatus.PENDING);
					sales.setPurchaseGenerated(false);
					sales.setPurchaseReference(rectl.ere1c.getNumeroDePedido());
					
					String remarks = "";
					for(ERE1T value: rectl.ere1tList){
						remarks += (StringUtils.isNotBlank(value.getTexto1())?value.getTexto1().trim():"") +
								(StringUtils.isNotBlank(value.getTexto2())?", " + value.getTexto2().trim():"") +
								(StringUtils.isNotBlank(value.getTexto3())?", " + value.getTexto3().trim():"") +
								(StringUtils.isNotBlank(value.getTexto4())?", " + value.getTexto4().trim():"") +
								(StringUtils.isNotBlank(value.getTexto5())?", " + value.getTexto5().trim():"") + 
								System.getProperty("line.separator");
					}
					sales.setRemarks(remarks);
					sales.setComments("");
					
					if (!testing) {
						salesController.accept(event);
					}
					
					getLogPanel().info("Pedido creado: " + sales.getReferenceCode());
					
					List<SalesDetail> detailList = new LinkedList<>();
					for(ERE1L ere1l: rectl.ere1lList) {
						RegistryItem rItem = searchRegistryItem(ere1l, customer);
						if(rItem==null) {
							getLogPanel().error(obtainProductErrorLabel(ere1l, customer));
						} else {
							Integer line = Integer.valueOf(ere1l.getNumeroDeLineaArticulo());
							String description = rItem.getItem().getProduct().getName();
							Double ediLineQuantity = ere1l.getCantidadPedida_21_();
							Tag customerPackingTag = searchPackingTag(customerRegistryNote);
							Double quantity = obtainQuantity(ediLineQuantity, customerPackingTag, rItem);
							Double price = rItem.getPrice();
							DiscountExpression discount = rItem.getDiscountExpression();
							
							SalesDetail detail = new SalesDetail();
							detail.setSales(sales);
							detail.setItem(rItem.getItem());
							detail.setLine(line);
							detail.setDescription(description);
							detail.setQuantity(quantity);
							detail.setPrice(price);
							detail.setDiscountExpression(discount);
							detail.setTaxes(0.0);
							detail.setStatus(SalesDetailStatus.PENDING);
							detail.setOfferDetail(null);
							detail.setDelivered(0.0);
							detailList.add(detail);
							
							Tag itemPackMeasurementTag = rItem.getItem().getPackMeasurementTag();
							Tag itemPackingTag = rItem.getItem().getPackUnitsTag();
							Tag itemPackFormatTag = rItem.getItem().getPackFormatTag();
							double itemPackMeasurement = rItem.getItem().getPackMeasurement();
							int itemPackUnits = rItem.getItem().getPackUnits();
							getLogPanel().info(String.format("Linea %1$d reconocida: %2$.2f unidades ", line, quantity)
									+ (quantity.equals(ediLineQuantity)
											?String.format("(%1$.2f %2$s)", ediLineQuantity, itemPackMeasurementTag.getName()):"")
									+ (quantity.equals(ediLineQuantity * itemPackMeasurement)
											?String.format("(%1$.2f %2$s x %3$.2f %4$s)", ediLineQuantity, itemPackingTag.getName(), itemPackMeasurement, itemPackMeasurementTag.getName()):"")
									+ (quantity.equals(ediLineQuantity * itemPackMeasurement * itemPackUnits)
											?String.format("(%1$.2f %2$s x %3$d %4$s x %5$.2f %6$s)", ediLineQuantity, itemPackFormatTag.getName(), itemPackUnits, itemPackingTag.getName(), itemPackMeasurement, itemPackMeasurementTag.getName()):"")
									+ " de " + description);
						}
					}
					if (!testing) {
						Map<Integer, SalesDetail> detailGroupMap = new HashMap<>();
						detailList.stream()
							.forEach(d -> {
								if(detailGroupMap.containsKey(d.getItem().getId())){
									SalesDetail detail = detailGroupMap.get(d.getItem().getId());
									detail.setQuantity(detail.getQuantity() + d.getQuantity());
								} else {									
									detailGroupMap.put(d.getItem().getId(), d);
								}
							});
						
						SalesDetailController detailController = (SalesDetailController) FormUtil.getController(ISalesConstants.SALES_DETAIL_CONTROLLER_NAME);
						int line = 0;
						for(SalesDetail detail: detailGroupMap.values()){
							detailController.onReset(event);
							SalesDetail newDetail = (SalesDetail) detailController.getTo();
							newDetail.setSales(detail.getSales());
							newDetail.setItem(detail.getItem());
							newDetail.setLine(++line);
							newDetail.setDescription(detail.getDescription());
							newDetail.setQuantity(detail.getQuantity());
							newDetail.setPrice(detail.getPrice());
							newDetail.setDiscountExpression(detail.getDiscountExpression());
							newDetail.setTaxes(detail.getTaxes());
							newDetail.setStatus(detail.getStatus());
							newDetail.setDelivered(detail.getDelivered());
							detailController.onAccept(event);
							
							getLogPanel().info(String.format("Linea de pedido %1$d creada: %2$.2f unidades ", newDetail.getLine(), newDetail.getQuantity())
									+ " de " + newDetail.getDescription());
						}
					}
					
				}
					
			} catch (ManagerBeanException e) {
				success = false;
				getLogPanel().error(e.getMessage());
				LOGGER.error(e.getMessage());
			}
		}

	}
	
	private String obtainProductErrorLabel(ERE1L ere1l, Customer customer) {
		return String.format("Linea %d omitida (%.2f uds.); la referencia de %s no existe para %s. "
				+ "[CodigoEANDelArticuloAdicional_1__EN=%s], [CodigoEAN_13_DUN_14DelArticulo=%s], [NumeroArticuloProveedor_SA=%s], "
				+ "[NumeroArticuloComprador_IN_BP=%s], [CodigoClienteFinal=%s], [CodigoGrupoArticuloComprador_GB=%s]",
				ere1l.getNumeroDeLineaArticulo(), ere1l.getCantidadPedida_21_(),
				ere1l.getDescripcion1Articulo().trim(), customer.getRegistry().getFullName(),
				StringUtils.trimToNull(ere1l.getCodigoEANDelArticuloAdicional_1__EN_()),
				StringUtils.trimToNull(ere1l.getCodigoEAN_13_DUN_14DelArticulo()),
				StringUtils.trimToNull(ere1l.getNumeroArticuloProveedor_SA_()),
				StringUtils.trimToNull(ere1l.getNumeroArticuloComprador_IN_BP_()),
				StringUtils.trimToNull(ere1l.getCodigoClienteFinal()),
				StringUtils.trimToNull(ere1l.getCodigoGrupoArticuloComprador_GB_()));
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

	protected List<RegistryNote> searchCustomerRNote(String customerCode) {
//		return searchCustomerNote(customerCode, IEdiSupport.PEDIDOS);
		return searchCustomerNote(customerCode, IEdiSupport.PTO_ENTREGA);
	}
	
	private List<RegistryNote> searchCustomerNote(String customerCode,
			String type) {
		List<RegistryNote> rNoteList = null;
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
			rNoteList = list != null && !list.isEmpty() ? (list.stream().map(
					to -> (RegistryNote) to).collect(Collectors.toList()))
					: null;
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		return rNoteList;
	}
	
	private Tag searchPackingTag(RegistryNote rNote) {
		String value = rNote.getComments();
		Matcher m;
		Pattern p = Pattern.compile(IEdiSupport.MEDIDA + "=([^;]*);");
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
	
	private RegistryItem searchRegistryItem(ERE1L ere1l, Customer customer) {
		RegistryItem rItem = null;
		try {
			String itemCustomerSalesCode = StringUtils.trimToNull(ere1l.getCodigoEANDelArticuloAdicional_1__EN_());
			String itemCustomerCode = StringUtils.trimToNull(ere1l.getCodigoEAN_13_DUN_14DelArticulo());
			rItem = obtainRegistryItem(customer, itemCustomerCode, itemCustomerSalesCode);
			if(rItem==null){
				itemCustomerCode = StringUtils.trimToNull(ere1l.getNumeroArticuloProveedor_SA_());
				rItem = obtainRegistryItem(customer, itemCustomerCode);
			}
			if(rItem==null){
				itemCustomerCode = StringUtils.trimToNull(ere1l.getNumeroArticuloComprador_IN_BP_());
				rItem = obtainRegistryItem(customer, itemCustomerCode);
			}
			if(rItem==null){
				itemCustomerCode = StringUtils.trimToNull(ere1l.getCodigoClienteFinal());
				rItem = obtainRegistryItem(customer, itemCustomerCode);
			}
			if(rItem==null){
				itemCustomerCode = StringUtils.trimToNull(ere1l.getCodigoGrupoArticuloComprador_GB_());
				rItem = obtainRegistryItem(customer, itemCustomerCode);
			}
		} catch (ManagerBeanException ex) {
			getLogPanel().error(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		return rItem;
	}
	
	private RegistryItem obtainRegistryItem(Customer customer, String itemCustomerCode) throws ManagerBeanException{
		return obtainRegistryItem(customer, itemCustomerCode, null);
	}
	private RegistryItem obtainRegistryItem(Customer customer, String itemCustomerCode, String itemCustomerSalesCode) throws ManagerBeanException{
		if(itemCustomerCode!=null && !"".equals(itemCustomerCode)){
			IManagerBean itemBean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE), itemCustomerCode);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), customer.getRegistry().getId());
			List<ITransferObject> list = itemBean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				if(list.size()>1 && itemCustomerSalesCode!=null) {
					return list.stream().map(to->(RegistryItem)to)
							.filter(ri->itemCustomerSalesCode.equals(ri.getEdiSalesCode()))
							.findFirst().orElse(null);
				}
				return (RegistryItem) list.get(0);
			}
		}
		return null;
	}
	
	private LogPanelController getLogPanel(){
		return LogPanelController.getInstance();
	}

	
}
