package com.code.aon.ui.finance.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.customer.Customer;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.DeliveryInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceAttachmentType;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistrySeller;
import com.code.aon.registry.enumeration.RegistrySellerStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.PrintParametersController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.finance.file.edi.EdiInvoiceImporterHandler;
import com.code.aon.ui.finance.file.edi.FtpSaleInvoiceDownloadHandler;
import com.code.aon.ui.finance.file.edi.FtpSaleInvoiceUploaderHandler;
import com.code.aon.ui.finance.file.edi.UdapaEdiInvoiceImporterHandler;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.bridge.DeliveryTransferManager;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Properties.CertificateProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus.InvoiceCommunicationStatusVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorException;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.sii.SIIManager;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiMain;

public class SaleInvoiceController extends InvoiceController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private RegistryValidationManager vm;
	private DeliveryTransferManager deliveryTransferManager;
	private boolean showDeliveryTransferWindow;
	private boolean showDeliveryFilterWindow;
	private boolean showTbaiWindow;
	private boolean showCertTbaiWindow;
	private boolean showCertTbaiAnularWindow;
	private boolean showFacturaeInfoWindow;
	private boolean showTbaiAccept;
	
	private boolean showCertVerifactuWindow;
	private boolean showCertVerifactuAnularWindow;
	private boolean showVerifactuAccept;
	
	private EdiInvoiceImporterHandler ediImporter;
	@Deprecated
	private UdapaEdiInvoiceImporterHandler udapaImporter;
	
	private FtpSaleInvoiceDownloadHandler ftpEdiDownloader;
	
	private FtpSaleInvoiceUploaderHandler ftpEdiUploader;
	private InvoiceCommunicationStatus communicationStatus;
	
	private boolean lroe;
	private boolean anular;
	
	
	public SaleInvoiceController() {
		setInvoiceAddressControllerName(SALE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(SALE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new CustomerValidationManager(); 
		}
		return vm;
	}

	public DeliveryTransferManager getDeliveryTransferManager() {
		if (deliveryTransferManager == null) {
			deliveryTransferManager = new DeliveryTransferManager(); 
		}
		return deliveryTransferManager;
	}
	
	public boolean isTbaiLroe() {
		Invoice invoice = (Invoice) this.getTo();
		return  (isTbai() || isLroe()) && isBizkaia() && invoice.getNumber() > 0;
	}

	public void setDeliveryTransferManager(DeliveryTransferManager deliveryTransferManager) {
		this.deliveryTransferManager = deliveryTransferManager;
	}

	public boolean isShowDeliveryTransferWindow() {
		return showDeliveryTransferWindow;
	}

	public void setShowDeliveryTransferWindow(boolean value) {
		this.showDeliveryTransferWindow = value;
	}
	
	public boolean isShowDeliveryFilterWindow() {
		return showDeliveryFilterWindow;
	}

	public void setShowDeliveryFilterWindow(boolean showDeliveryFilterWindow) {
		this.showDeliveryFilterWindow = showDeliveryFilterWindow;
	}
	
	public boolean isShowTbaiWindow() {
		return showTbaiWindow;
	}

	public void setShowTbaiWindow(boolean showTbaiWindow) {
		this.showTbaiWindow = showTbaiWindow;
	}
	
	public boolean isShowCertTbaiWindow() {
		return showCertTbaiWindow;
	}

	public void setShowCertTbaiWindow(boolean showCertTbaiWindow) {
		this.showCertTbaiWindow = showCertTbaiWindow;
		if(showCertTbaiWindow)
			setShowTbaiAccept(showCertTbaiWindow);
	}
	
	public boolean isShowCertVerifactuWindow() {
		return showCertVerifactuWindow;
	}

	public void setShowCertVerifactuWindow(boolean showCertVerifactuWindow) {
		this.showCertVerifactuWindow = showCertVerifactuWindow;
		if(showCertVerifactuWindow)
			setShowVerifactuAccept(showCertVerifactuWindow);
	}
	
	public boolean isShowCertTbaiAnularWindow() {
		return showCertTbaiAnularWindow;
	}

	public void setShowCertTbaiAnularWindow(boolean showCertTbaiAnularWindow) {
		this.showCertTbaiAnularWindow = showCertTbaiAnularWindow;
	}
	
	public boolean isShowCertVerifactuAnularWindow() {
		return showCertVerifactuAnularWindow;
	}

	public void setShowCertVerifactuAnularWindow(boolean showCertVerifactuAnularWindow) {
		this.showCertVerifactuAnularWindow = showCertVerifactuAnularWindow;
	}
	
	public boolean isShowFacturaeInfoWindow() {
		return showFacturaeInfoWindow;
	}

	public void setShowFacturaeInfoWindow(boolean showFacturaeInfoWindow) {
		this.showFacturaeInfoWindow = showFacturaeInfoWindow;
	}
	
	public EdiInvoiceImporterHandler getEdiImporter() {
		if(ediImporter==null){
			ediImporter = new EdiInvoiceImporterHandler(this);
		}
		return ediImporter;
	}

	@Deprecated
	public UdapaEdiInvoiceImporterHandler getUdapaImporter() {
		if(udapaImporter==null){
			udapaImporter = new UdapaEdiInvoiceImporterHandler(this);
		}
		return udapaImporter;
	}

	public FtpSaleInvoiceDownloadHandler getFtpEdiDownloader() {
		if(ftpEdiDownloader==null){
			ftpEdiDownloader = new FtpSaleInvoiceDownloadHandler(this);
		}
		return ftpEdiDownloader;
	}
	
	public FtpSaleInvoiceUploaderHandler getFtpEdiUploader() {
		if(ftpEdiUploader==null){
			ftpEdiUploader = new FtpSaleInvoiceUploaderHandler(this);
		}
		return ftpEdiUploader;
	}

	public boolean isSeriesValid() throws ManagerBeanException {
		String seriesCode = getInvoice().getSeries();
		return (StringUtils.isEmpty(seriesCode)) ? true : seriesCode.equals(SeriesUtil.ensureInvoiceSeries(seriesCode));
	}

	public boolean isProforma() {
		return getInvoice().isProforma();
	}
	
	public void onFindNextFreeNumber(ActionEvent event) throws ManagerBeanException {
		int number = (getInvoice().getNumber() == 0 ? 1 : getInvoice().getNumber());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		while (true) {
			Criteria criteria = new Criteria();
			if (StringUtils.isBlank(getInvoice().getSeries())) {
				criteria.addNullExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES));
			} else {
				criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), getInvoice().getSeries());
			}
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_NUMBER), number);
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			if (invoiceBean.getCount(criteria) == 0) {
				break;
			}
			number++;
		}

		getInvoice().setNumber(number);
	}

	public void onCustomerChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			customerChanged(customer);
		} else {
			Invoice invoice = getInvoice();
			invoice.setRegistryAddress(null);
			invoice.setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
			invoice.setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());

			setAddresses(null);
			setProjects(null);
		}
	}

	public void customerChanged(Customer customer) throws ManagerBeanException {
		isBlocked(customer); // Saca el mensaje de bloqueo.
		Invoice invoice = getInvoice();
		invoice.setRegistryName(customer.getRegistry().getFullName());
		invoice.setRegistryDocument(customer.getRegistry().getDocument());
		invoice.setRegistryDocumentType(customer.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry());
		invoice.setRegistry(customer.getRegistry());
		invoice.setTransaction(customer.getTransaction());
		invoice.setSurcharge(!invoice.isVatFree() && customer.isSurcharge());
		invoice.setWithholding(customer.isWithholding() && getCompanyController().isWithholding());
		invoice.setWithholdingFarmer(customer.isWithholding() && getCompanyController().isWithholdingFarmer());
		invoice.setScope(customer.getScope());
		loadAddresses(customer.getId());
		loadProjects(customer.getId());
		loadCommercial(customer.getId());

		if (isNevv()) {
			InvoiceFinanceController financeController = (InvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
			Finance finance = (Finance)financeController.getTo();
			if (finance != null) {
				RegistryPayMethod rPayMethod = customer.getRegistry().getPayMethod();
				finance.setPayMethod((rPayMethod==null) ? new PayMethod() : rPayMethod.getPayment());
				finance.setBankAccount((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? new BankAccount() : rPayMethod.getBankAccount());
				finance.setBankAlias((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBankAlias());
				finance.setBic((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBic());

				financeController.setRegistryBank((rPayMethod==null) ? null : rPayMethod.getRegistryBank());
				financeController.setShowBankManualInput(false);
			}
		}
	}

	private boolean isBlocked(Customer customer) {
		return getRegistryValidationManager().isBlocked(customer);
	}

	public void loadCommercial(Integer id) throws ManagerBeanException {
		if (id != null) {
			Invoice invoice = getInvoice();
			IManagerBean registrySellerBean = BeanManager.getManagerBean(RegistrySeller.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_REGISTRY_ID), id);
			criteria.addEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_STATUS), RegistrySellerStatus.ACTIVE);
			criteria.addLessThanOrEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_START_DATE), invoice.getIssueDate());
			Expression endDateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_END_DATE), invoice.getIssueDate());
			Expression endNullExpr = ExpressionUtilities.getNullExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(endDateExpr, endNullExpr));
			criteria.addOrder(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_START_DATE));
			Iterator<ITransferObject> iter = registrySellerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				invoice.setSeller(((RegistrySeller)iter.next()).getSeller());
			} else {
				invoice.setSeller(new Seller());
				invoice.getSeller().setRegistry(new Registry());
			}
		}
	}

	public void onSellerChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Seller seller = (Seller)event.getNewValue();
			getInvoice().setSeller(seller);
		}
	}

	public void onDeliveryTransferShow(ActionEvent event) throws ManagerBeanException {
		getDeliveryTransferManager().clearCheckedDelivery();
		loadDeliveryTransferModel();
	}
	
	private void loadDeliveryTransferModel() throws ManagerBeanException {
		List<ITransferObject> invoicedDeliveryList = new LinkedList<ITransferObject>();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
			if (!invoicedDeliveryList.contains(deliveryDetail.getDelivery())) {
				invoicedDeliveryList.add(deliveryDetail.getDelivery());
			}
		}
		getDeliveryTransferManager().setInvoicedDeliveryList(invoicedDeliveryList);
		
		List<ITransferObject> deliveryList = new LinkedList<ITransferObject>();
		if (!isReadOnly()) {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			criteria = new Criteria();
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), getInvoice().getRegistry().getId());
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SECURITY_LEVEL), getInvoice().getSecurityLevel());
			if(getDeliveryTransferManager().getFilterParams().getFromDate()!=null){
				criteria.addGreaterThanOrEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME), getDeliveryTransferManager().getFilterParams().getFromDate());
			}
			if(getDeliveryTransferManager().getFilterParams().getToDate()!=null){
				criteria.addLessThanOrEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME), getDeliveryTransferManager().getFilterParams().getToDate());
			}
			if(getDeliveryTransferManager().getCheckedDeliveryCount() > 0){
				for(Delivery delivery: getDeliveryTransferManager().getCheckedDelivery()){
					criteria.addNotEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ID), delivery.getId());
				}
			}
			if(getDeliveryTransferManager().getInvoicedDeliveryCount() > 0){
				for(ITransferObject to: getDeliveryTransferManager().getInvoicedDeliveryList()){
					criteria.addNotEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ID), ((Delivery)to).getId());
				}
			}
			criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME));
			criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SERIES));
			criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER));
			deliveryList.addAll(deliveryBean.getList(criteria));
		}
		getDeliveryTransferManager().setDeliveryList(deliveryList);
	}

	public void onFilterTransferModel(ActionEvent event) throws ManagerBeanException {
		loadDeliveryTransferModel();
	}
	
	public void onDeliveryTransfer(ActionEvent event) throws ManagerBeanException {
		try {
			DeliveryInvoicingManager invoicingManager = new DeliveryInvoicingManager();
			List<Delivery> transferDeliveryList = new LinkedList<>();
			getDeliveryTransferManager().getInvoicedDeliveryList().forEach(to -> 
				{if(!getDeliveryTransferManager().getCheckedRestoreInvoicedDelivery().contains(to))
					transferDeliveryList.add((Delivery)to);
				});
			transferDeliveryList.addAll(getDeliveryTransferManager().getCheckedDelivery());
			invoicingManager.transferDeliveries(getInvoice(), transferDeliveryList, getDeliveryTransferManager().getInvoicedDeliveryList());

			refresh(null);
			FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME).onSearch(null);

			autoGenerateIncreases();
			autoGenerateFinances();
			resetListTotals();
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public String navigationRedirect() {
		return SALE_INVOICE_FORM_NAME;
	}

	public List<SelectItem> getInvoiceAttachmentTypes() {
		List<SelectItem> invoiceAttachmentTypes = new LinkedList<SelectItem>();
		InvoiceAttachmentType type = InvoiceAttachmentType.RECEIPT;
		String name = type.getName(AonUtil.getCurrentLocale());
		invoiceAttachmentTypes.add( new SelectItem(type, name) );
		return invoiceAttachmentTypes;
	}

	@Override
	public IAttachment generateReportAttachment(ITransferObject to) {
		PrintParametersController printParams = (PrintParametersController) AonUtil
				.getRegisteredBean(ICompanyConstants.PRINT_PARAMETERS_CONTROLLER_NAME);
		printParams.onInit(null);
		getSignerController().setReportKey(printParams.getSaleInvoiceParams().getSaleInvoiceTemplateValue());
		return getSignerController().getReport(to);
	}
	
	public void onExportEdiFile(ActionEvent event) {
		FileOutput output = null;
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			Invoice invoice = (Invoice) this.getTo();
			output = getFtpEdiUploader().exportEdiFile(invoice);

			// download file
			String fileName = "inv_" + invoice.getSeries()+"_"+invoice.getNumber();
			String fileExt = output.getErrors().size()>0?"err":"edi";
			byte[] data = output.getContent();
			int size = data.length;
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, fileName + "." + fileExt,
					null, size);
			InputStream fileIn = new BufferedInputStream(
					new ByteArrayInputStream(data));
			IOUtils.copy(fileIn, out);
			IOUtils.closeQuietly(fileIn);
		} catch (IOException e) {
        	AonUtil.addErrorMessage(e.getMessage());
        	throw new AbortProcessingException(e.getMessage(), e);
        } catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
	}
	
	@Override
	public synchronized ITransferObject getTo() {
		return super.getTo();
	}
	
	@Override
	protected synchronized void accept() {
		if(isNevv() && (hasCommunication())) {
			Invoice invoice = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Integer number = AON.getInvoiceMinNumber(domainName, domainId, login, com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, invoice.getSeries());
			invoice.setNumber(number < 0 ? number : -1);
		}
		setTbaiUrl(null);
		if(!isTbaiInvoice()) {
			super.accept();
		} else AonUtil.addErrorMessage("La factura ya se ha emitido a TicketBAI");
	}
	
	@Override
	public synchronized void accept(ActionEvent event) {
		String series = getInvoice().getSeries();
		super.accept(event);
		getInvoice().setSeries(series);
	}

	@Override
	protected synchronized void resetTo() {

		super.resetTo();
	}
	
	@Override
	protected synchronized void setTo(ITransferObject value) {
		super.setTo(value);
	}
	
	@Override
	protected synchronized  void synchronizeAddedPojo() throws ManagerBeanException {
		super.synchronizeAddedPojo();
	}
	
	public String getDownloadURL() {
		Invoice invoice = (Invoice) getTo();
		if (invoice != null && !isNevv() && invoice.getId() != null) {
			return AON.getInvoiceDoc(getOccam(), invoice.getDomain(), invoice.getId())
				.map( InvoiceDoc::getUrl )
				.orElseGet( () -> {
					com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
					JSONObject json = new JSONObject()
						.put(IJsonNames.ID, invoice.getId())
						.put(IJsonNames.SOURCE, "invoice")
						.put("domain_id", domain.getId())
						.put("domain_name", domain.getName())
						.put(IJsonNames.LOGIN, UserUtils.getInstance().getLoggedUser().getLogin());		
					return "/ms/api/download_invoice_pdf?json=" + Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
				});
		}
		return null;
	}

	@Transient
	public synchronized void issueInvoiceLroe() {
		try {
			checkCertificate();		
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			
			com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
			tbaiValidation(invoice);

			Byte[] types = new Byte[]{com.esferalia.aon.occam.api.model.type.InvoiceType.SALES.value()};
			if(invoice.getNumber() < 1) {
				Integer number = AON.getInvoiceNextNumber(domainName, invoice.getDomain(), login, types, inv.getSeries());
				invoice.setNumber(number);
				invoice.setReferenceCode(null);
				invoice.setIssueDate(new Date());
				invoice.setTaxDate(invoice.getTaxDate() != null && invoice.getTaxDate().after(new Date()) ? invoice.getTaxDate() : new Date());
				inv.setIssueDate(new Date());
				inv.setTaxDate(invoice.getTaxDate() != null && invoice.getTaxDate().after(new Date()) ? invoice.getTaxDate() : new Date());
				AON.updateInvoice(domainName, invoice.getDomain(), login, invoice, true);
			}

			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(invoice.getDomain())
				.setUser(login);
			InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(occam);
			if(icc.isTbai() || icc.isLroe()) {
				Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
				icc.setCertificate(getCertData());
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionLROE(company, invoice, icc);
			}
			setCommunicationStatus( null ); // Refresca el estado de la comunicacion
			refresh( null );
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	@Transient
	public synchronized void modifyInvoice() {
		try {
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Company company = AON.getCompanyForDomain(domainName, inv.getDomain(), login);
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(inv.getDomain())
				.setUser(login);
			InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(occam);
			icc.setCertificate(getCertData());
			if(icc.isTbai()) {
				TbaiMain tbai = new TbaiMain();
				com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
				tbai.zuzenduTBAI(company, invoice, icc, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@Transient
	public synchronized void subsanarInvoice() {
		try {
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Company company = AON.getCompanyForDomain(domainName, inv.getDomain(), login);
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(inv.getDomain())
				.setUser(login);
			InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(occam);
			icc.setCertificate(getCertData());
			if(icc.isTbai()) {
				TbaiMain tbai = new TbaiMain();
				com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
				tbai.zuzenduTBAI(company, invoice, icc, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@Transient
	public synchronized void issueInvoice() {
		if(!isShowTbaiAccept() && !isShowVerifactuAccept()) return;
		try {
			setShowTbaiAccept(false);
			if(lroe) {
				issueInvoiceLroe();
			} else if(anular) {
				 anularInvoice();
			} else {
				Invoice inv = (Invoice) getTo();
				String domainName = AonUtil.getDomainName();
				String login = UserUtils.getInstance().getLoggedUser().getLogin();
				com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
				communicationValidation(invoice);
				
				Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
				Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(inv.getDomain())
					.setUser(login);
				InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(occam);
				if (config.isVerifactu() || config.isNoVerifactu() || config.isSif()) {
					communicateInvoice(occam, config, company, invoice, getCertificate());
					return;
				}

				checkCertificate();
				if(invoice.getNumber() < 1) {
					Byte[] types = new Byte[]{com.esferalia.aon.occam.api.model.type.InvoiceType.SALES.value()};
					Integer number = AON.getInvoiceNextNumber(domainName, invoice.getDomain(), login, types, inv.getSeries());
					invoice.setNumber(number);
					invoice.setReferenceCode(null);
					inv.setNumber(number);
					
					invoice = AON.updateInvoice(domainName, invoice.getDomain(), login, invoice, true);
					inv.setReferenceCode(invoice.getReferenceCode());
					updateFinances(domainName, login, invoice);
				}

				config.setCertificate(getCertData());
				if(config.isTbai() || config.isLroe()) {
					TbaiMain tbai = new TbaiMain();
					tbai.createEmisionTBAI(company, invoice, config);
					try (CloseableAONContext ctx =  AONContext.getAONContext(company.getDomain(), "")) {
						setTbaiUrl(TbaiData.getInstance(ctx, config).getTbaiUrl(company.getDomain().getId(), invoice.getId()));
					}
				}
				
				if(config.isSii()) {
					try {
						SIIManager manager = SIIManager.getInstance(config);
							
						AccountingReportParams params = new AccountingReportParams();
						params.setDomain(inv.getDomain());
						params.setInvoices(new Integer[] {invoice.getId()});
						LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(getOccam(), params, "")
								.collect(Collectors.toCollection(LinkedList::new));		
						manager.suministroFacturas(getDomain(), login, company, invoice, contextList, null);
					} catch (Exception e) {
						if (e instanceof InvoiceCommunicationException ice) {
							throw ice;
						} else {
							throw new InvoiceCommunicationException( e );
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onCommunicateInvoice(ActionEvent event) {
		try {
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Occam occam = getOccam()
				.setDomainName(domainName)
				.setDomain(inv.getDomain())
				.setUser(login);
			InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(occam);
			if (config.isVerifactu() || config.isNoVerifactu() || config.isSif() || config.isSii()) {
				com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
				Company company = AON.getCompanyForDomain(domainName, inv.getDomain(), login);			
				communicateInvoice( occam, config, company , invoice , getCertificate() );
			}
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	private void communicateInvoice(Occam occam
		, InvoiceCommunicationConfiguration config
		, Company company
		, com.esferalia.aon.occam.api.model.finance.Invoice invoice
		, Integer certId) throws Exception {
		communicationValidation(invoice);
		List<com.esferalia.aon.occam.api.model.finance.Invoice> invoices = AonCollectionUtils.toList(invoice);
		Domain domain = AON.getDomain(occam, invoice.getDomain());
		User user = AON.getUser(occam.getDomainName(), occam.getDomain(), occam.getUser());
		InvoiceCommunicatorContext communicator = new InvoiceCommunicatorContext(domain, user, certId, invoices)
			.setConfig(config)
			.setCompany(company);
		
		try {
			InvoiceCommunicator.issueInvoice(communicator);
			setCommunicationStatus( null );
			setVerifactuUrl( null );
			refresh( null );
		} catch (Exception e) {
			
			try {
				InvoiceCommunicator.throwRightException(e, invoice);
			} catch (InvoiceErrorException e1) {
				AonCollectionUtils.stream(e1.getMessages())
					.forEach( m -> AonUtil.addErrorMessage(m.getMessage() ));
			}
		}
	}

	private void updateFinances(String domainName, String login, com.esferalia.aon.occam.api.model.finance.Invoice invoice) {
		invoice.financeStream()
			.forEach(finance -> {
				finance.setConcept(invoice.getDocumentNumber());
				AON.saveFinance(domainName, invoice.getDomain(), login, finance);
			}
		);
	}
	
	private void tbaiValidation(com.esferalia.aon.occam.api.model.finance.Invoice invoice) throws Exception {
		checkCertificate();
		communicationValidation(invoice);
	}
	
	private void communicationValidation(com.esferalia.aon.occam.api.model.finance.Invoice invoice) throws Exception {
		checkInvoice(invoice);
		checkRegistry(invoice);
		checkDetails(invoice);
	}
	
	private void checkInvoice(com.esferalia.aon.occam.api.model.finance.Invoice invoice) {
		if(invoice.isRectifier() && AonStringUtils.isBlank(invoice.getSeries())) {
			throw new AbortProcessingException("Las Facturas rectificativas tienen que tener serie.");
		}
		
		Date date = AonDateUtils.getDateWithoutTime(invoice.getIssueDate());
		if(date.after(new Date())) {
			throw new AbortProcessingException("La Fecha de la factura no puede ser superior a la fecha actual.");
		}
	}
	
	private void checkRegistry(com.esferalia.aon.occam.api.model.finance.Invoice invoice) {
		if(AonStringUtils.isBlank(invoice.getRegistryDocument()) 
				&& !invoice.isSimplified()) {
			throw new AbortProcessingException("El Documento del cliente está vacio.");
		}
			
		if(Country.ES.equals(invoice.getRegistryDocumentCountry()) 
				&& !AonDocumentUtil.isValid(invoice.getRegistryDocument())
				&& !invoice.isSimplified()) {
			throw new AbortProcessingException("El Documento del cliente no es válido.");
		}
	}
	
	private void checkDetails(com.esferalia.aon.occam.api.model.finance.Invoice invoice) {
		for (com.esferalia.aon.occam.api.model.finance.InvoiceDetail detail : invoice.getDetails()) {
			if(Double.toString(detail.getQuantity())
					.substring(Double.toString(detail.getQuantity()).indexOf(".") + 1)
					.length() > 2){
//				throw new Exception("La cantidad '"+ detail.getQuantity() + "' no puede tener más de 2 decimales");
			}
			
			if(Double.toString(detail.getPrice())
					.substring(Double.toString(detail.getPrice()).indexOf(".") + 1)
					.length() > 2){
//				throw new Exception("El cantidad '"+ detail.getPrice() + "' no puede tener más de 2 decimales");
			}
			
			if(detail.getDescription().length() >= 249) {
//				throw new Exception("El concepto no puede tener más de 250 carácteres: " + detail.getDescription());
			}
		}	
	}
	
	private Certificate checkCertificate() throws Exception {
		Certificate cert = getCertData();
		try {
			if(!checkCert(cert.getData(), cert.getPassword())) {
				throw new AbortProcessingException("El certificado o la contraseña no son correctos.");
			}
		} catch (Exception e) {
			throw new AbortProcessingException("El certificado o la contraseña no son correctos.");
		}		
		if(cert.isEmpty()) {
			throw new AbortProcessingException("El certificado no existe.");
		}
		return cert;		
	}
	
	public static boolean checkCert(byte[] cert, String password) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cert);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, password.toCharArray());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void tbai() {
		lroe = false;
		anular = false;
	}
	
	public void verifactu() {
		lroe = false;
		anular = false;
	}

	public void lroe() {
		lroe = true;
		anular = false;
	}

	public void anular() {
		lroe = false;
		anular = true;
	}
	
	public void onAnular(ActionEvent event) {
		anularInvoice();
		initializeModel();
		resetTo();
	}
	
	public String onAnular() {
		if (isVerifactuInvoice() || isNoVerifactuInvoice() || isSifInvoice()) {
			return onDoAnular();
		} else {
			throw new AbortProcessingException("La factura no se puede anular");
		}
	}
	
	private String onDoAnular() {
		Invoice inv = (Invoice) getTo();
		String domainName = AonUtil.getDomainName();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
		Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
		Occam occam = new Occam()
			.setDomainName(domainName)
			.setDomain(inv.getDomain())
			.setUser(login);
		InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(occam);
		List<com.esferalia.aon.occam.api.model.finance.Invoice> invoices = AonCollectionUtils.toList(invoice);
		Domain domain = AON.getDomain(occam, invoice.getDomain());
		User user = AON.getUser(occam.getDomainName(), occam.getDomain(), occam.getUser());
		InvoiceCommunicatorContext communicator = new InvoiceCommunicatorContext(domain, user, getCertificate(), invoices)
			.setConfig(config)
			.setCompany(company);
		try {
			InvoiceCommunicator.cancelInvoice(communicator);
			setCommunicationStatus( null );
			initializeModel();
			resetTo();
			return backAction();
		} catch (Exception e) {
			try {
				InvoiceCommunicator.throwRightException(e, invoice);
			} catch (InvoiceErrorException e1) {
				AonCollectionUtils.stream(e1.getMessages())
					.forEach( m -> AonUtil.addErrorMessage(m.getMessage() ));
			}
			return null;
		}
	}
	
	private void anularInvoice() {
		if(isTbaiInvoice() || isSiiInvoice()) {
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
			Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
			Domain domain = new Domain()
					.setName(domainName)
					.setId(invoice.getDomain());
	
			Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(invoice.getDomain())
					.setUser(login);
			InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(occam);
			config.setCertificate(getCertData());
			if(isTbaiInvoice()) {
				try {
					checkCertificate();
					TbaiMain tbai = new TbaiMain();
					tbai.createAnulacionTBAI(company, invoice, config);
					// NUEVO ANULAR
					// TBAI.getInstance().cancel(config, company, invoice);
					AON.deleteInvoice(domainName, invoice.getDomain(), login, invoice.getId());
				} catch (Exception e) {
					boolean test = config.getTbaiData().map(tbaiData -> tbaiData.isTest()).orElse(false);
					if(test) {
						AON.deleteInvoice(domainName, invoice.getDomain(), login, invoice.getId());
					} else {
						e.printStackTrace();
						AonUtil.addErrorMessage(e.getMessage());
					}
				}
			} else if(isSiiInvoice()) {
				try {
					SIIManager manager = SIIManager.getInstance(config);
				
					AccountingReportParams params = new AccountingReportParams();
					params.setDomain(inv.getDomain());
					params.setInvoices(new Integer[] {invoice.getId()});
					LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(occam, params, "")
							.collect(Collectors.toCollection(LinkedList::new));
					manager.bajaFacturas(domain, login, company, invoice, contextList, null);
				} catch (Exception e) {
					e.printStackTrace();
					AonUtil.addErrorMessage(e.getMessage());
				}
			}
		}
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		try {
			if(!isTbaiInvoice()) 
				super.onRemove(event);
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	public String getRemoveConfirmMessage() {
		return this.isTbaiInvoice() 
			? "La factura está enviada a TicketBAI. Al borrarla quedará anulada en TicketBai."
			: "¿Borrar?";
				
	}
	LinkedList<SelectItem> digitalCertificates;
	LinkedList<Certificate> certificates;
	Integer certificate;
	String password;
	
	public Integer getCertificate() {
		return certificate;
	}
	
	public void setCertificate(Integer certificate) {
		this.certificate = certificate;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	private Certificate getCertData() {
		Certificate cert = getCert();
		if(!cert.hasPassword()) {
			cert.setPassword(password);
		}
		Domain domain = getDomain();
		User user = getUser();
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(getCertificate()), AttachType.REGISTRY);
		cert.setData(attach.getData());
		return cert;
	}
	
	private Certificate getCert() {
		if(certificates == null) {
			getDigitalCertificates();
		}
		return certificates.stream().filter(f -> f.getId().equals(certificate)).findFirst().orElse(new Certificate());	
	}
	
	
	public boolean isShowTbaiAccept() {
		return showTbaiAccept;
	}
	
	public void setShowTbaiAccept(boolean showTbaiAccept) {
		this.showTbaiAccept = showTbaiAccept;
	}
	
	public boolean isShowVerifactuAccept() {
		return showVerifactuAccept;
	}
	
	public void setShowVerifactuAccept(boolean showVerifactuAccept) {
		this.showVerifactuAccept = showVerifactuAccept;
	}
	
	public boolean isInvoiceTbaiAccepted() {
		Invoice inv = (Invoice) getTo();
		String domainName = AonUtil.getDomainName();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Occam occam = new Occam()
			.setDomainName(domainName)
			.setDomain(inv.getDomain())
			.setUser(login);
		return AON.getInvoiceInfo(occam, inv.getId(), InvoiceCommunicationType.LROE)
			.map( InvoiceInfo::getStatus )
			.map( InvoiceCommunicationStatus::isAccepted )
			.orElse(false);
	}
	
//	public InvoiceCommunicationStatus getVerifactuStatus() { return getCommunicationStatus(InvoiceCommunicationType.VERIFACTU); }
//	public InvoiceCommunicationStatus getNoVerifactuStatus() { return getCommunicationStatus(InvoiceCommunicationType.NO_VERIFACTU); }
//	public InvoiceCommunicationStatus getSifStatus() { return getCommunicationStatus(InvoiceCommunicationType.SIF); }
	
	private InvoiceCommunicationStatus getCommunicationStatus() {
		if (isVerifactuInvoice()) {
			return getCommunicationStatus(InvoiceCommunicationType.VERIFACTU);
		} else if (isNoVerifactuInvoice()) {
			return getCommunicationStatus(InvoiceCommunicationType.NO_VERIFACTU);
		} else if (isSifInvoice()) {
			return getCommunicationStatus(InvoiceCommunicationType.SIF);
		} else {
			return InvoiceCommunicationStatus.PENDING;
		}
	}
	
	private InvoiceCommunicationStatus getCommunicationStatus(InvoiceCommunicationType type) {
		if (communicationStatus == null) {
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(inv.getDomain())
				.setUser(login);
			AON.getInvoiceInfo(occam, inv.getId(), type)
				.ifPresentOrElse( 
					info -> setCommunicationStatus( info.getStatus() ),
					()  -> setCommunicationStatus( null )
				);
		}
		return communicationStatus;
	}
	public void setCommunicationStatus(InvoiceCommunicationStatus invoiceCommunicationStatus) {
		this.communicationStatus = invoiceCommunicationStatus;
	}
	
	public String getAdmonIcon() {
		if (getInvoiceCommunicationConfiguration() != null) {
			Administration admon = getInvoiceCommunicationConfiguration()
				.getAdministration()
				.orElse(Administration.UNKNOWN);
			return admon.visit(new IAdministrationVisitor<String>() {
				@Override public String visitAlava() 			{ return "aon-icon-araba-bw"; }
				@Override public String visitBizkaia() 			{ return "aon-icon-bizkaia-bw"; }
				@Override public String visitGipuzkoa() 		{ return "aon-icon-gipuzkoa-bw"; }
				@Override public String visitNavarra() 			{ return "aon-icon-navarra-bw"; }
				@Override public String visitCommonTerritory() 	{ return "aon-icon-aeat-bw"; }
				@Override public String visitUnknown() 			{ return "aon-icon-sif"; }
				@Override public String visitCanarias() 		{ return "aon-icon-canarias-bw"; }
			});
		}
		return "aon-icon-sif";
	}
	
	public String getSendLabel() {
		if (isVerifactu()) return "Emitir / Enviar";
		if (isNoVerifactu()) return "Emitir / Archivar"; 
		if (isSif()) return "Emitir / Archivar";
		if (isSii()) return "Emitir / Enviar";
		return "Aceptar/Enviar";
	}
	
	@Override
	public boolean isSii() {
		if(isTbai()) return false;
		return super.isSii();
	}
	
	public String getSendIcon() {
		if (isNoVerifactu()) return "aon-icon-send-archive"; 
		if (isSif()) return "aon-icon-send-sif";
		return "aon-icon-send";
	}
	
	public String getCommunicationStatusDescription() {
		if ((isSif() 				&& getCommunicationStatus() == InvoiceCommunicationStatus.ACCEPTED ) 
		 || (isNoVerifactuInvoice() && getCommunicationStatus() == InvoiceCommunicationStatus.PENDING  )   ) {
			return StringUtils.upperCase("ARCHIVADA");
		}
		return getCommunicationStatus() == null 
			? StringUtils.upperCase(InvoiceCommunicationStatus.PENDING.getDescription()) 
			: StringUtils.upperCase(getCommunicationStatus().getDescription());
	}
	public String getCommunicationStatusIcon() {
		if (getCommunicationStatus() == null) return "aon-icon-point-orange";
		MutableObject<String> ret = new MutableObject<>();
		getCommunicationStatus().accept(new InvoiceCommunicationStatusVisitor() {
			@Override
			public void visitPending() {
				if ( isNoVerifactuInvoice() ) {
					ret.setValue("aon-icon-point-light-green");
				} else {
					ret.setValue("aon-icon-point-orange");
				}
			}

			@Override public void visitAccepted() { ret.setValue("aon-icon-point-green"); }
			@Override public void visitAcceptedWithErrors() { ret.setValue("aon-icon-point-yellow"); }
			@Override public void visitWrong() { ret.setValue("aon-icon-point-red"); }
			@Override public void visitCancelled() { ret.setValue("aon-icon-point-gray"); }
			@Override public void visitExternallyCommunicated() {ret.setValue("aon-icon-point-blue"); }
		});
		return ret.getValue();
	}
	
	public boolean isIssueable() {
		return !isNevv()
			&& (isVerifactu( ) || isNoVerifactu() ||  isSif() ||  isSii())
			&& (getInvoice().getNumber() <= 0 || isUniqueNumberOfSeries());
	}
	
	public boolean isCommunicationAvailable() {
		return !isInvoiceCommunicationAccepted()
			&& (isCommunicationPending() || isCommunicationWrong());
	}
	
	public boolean isCommunicationPending() {return getCommunicationStatus() == null || getCommunicationStatus() == InvoiceCommunicationStatus.PENDING;}
	public boolean isCommunicationAccepted() { return getCommunicationStatus() == InvoiceCommunicationStatus.ACCEPTED; }
	public boolean isCommunicationAcceptedWithErrors() {return getCommunicationStatus() == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;}
	public boolean isCommunicationWrong() { return getCommunicationStatus() == InvoiceCommunicationStatus.WRONG; }
	public boolean isCommunicationExternal() { return getCommunicationStatus() == InvoiceCommunicationStatus.EXTERNALLY_COMMUNICATED; }
	public boolean isInvoiceCommunicationAccepted() { return isCommunicationAccepted() || isCommunicationAcceptedWithErrors(); }
	
	public boolean isPass() {	
		return getCert().hasPassword();
	}

	public LinkedList<SelectItem> getDigitalCertificates() {
		if(digitalCertificates == null) {
			digitalCertificates = new LinkedList<>();
			certificates = new LinkedList<>();
			Domain domain = getDomain();
			User user = getUser();
			AON.getCertificates(domain, user, f -> certificateFilter(domain, user, f)).forEach(certificate -> {
				SelectItem item = new SelectItem(certificate.getId(), certificate.getDescription());
				digitalCertificates.add(item);
				certificates.add(certificate);
			});
			
			if(!certificates.isEmpty())
				certificate = certificates.getFirst().getId();
		}
		return digitalCertificates;
	}	

	public Filter certificateFilter(Domain domain, User user, CertificateProperties f) {
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), user.getLogin());
		
		Filter filter;
		if(domain.getParentId() != null) {
			if(!user.getDomain().getId().equals(domain.getParentId())) {
				filter = (f.getDomainProperty().eq(domain.getId()).or(
						f.getDomainProperty().eq(domain.getParentId())
						.and(f.getSecurityLevelProperty().eq(SecurityLevel.OFFICIAL.value())))
					);
			} else {
				Integer[] domains = {domain.getId(), domain.getParentId()};
				filter = f.getDomainProperty().in(domains);
			}
		} else filter = f.getDomainProperty().eq(domain.getId());
    	
		if(!user.getRegistry().isEmpty() && domain.getParentId() != null) {
			Company parentCompany = AON.getCompanyForDomain(domain.getName(), domain.getParentId(), user.getLogin());
			Integer[] registries = {user.getRegistry().getId(), company.getId(), parentCompany.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else if(!user.getRegistry().isEmpty()) {
			Integer[] registries = {user.getRegistry().getId(), company.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else if(domain.getParentId() != null) {
			Company parentCompany = AON.getCompanyForDomain(domain.getName(), domain.getParentId(), user.getLogin());
			Integer[] registries = {company.getId(), parentCompany.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else filter = filter.and(f.getRegistryProperty().eq(company.getId()));
		
		filter = filter.and(f.getTypeProperty().eq(CertificateType.AEAT.name()).or(f.getTypeProperty().isNull()));
		
    	return filter;
    }
	
	public Domain getDomain() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		return AON.getDomain(domainName, domainId, login);
	}
	
	public User getUser( ) {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Integer userId = UserUtils.getInstance().getLoggedUser().getId();	
		return AON.getUser(domainName, domainId, login, f -> f.getIdProperty().eq(userId));
	}
	
	public boolean isAonUser() {
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		return "aon".equalsIgnoreCase(login);
	}
	String codeAsignacion;
	
	public String getCodeAsignacion() {
		if(AonStringUtils.isBlank(codeAsignacion) && getInvoice().getDetailList().size() > 0) {
			InvoiceDetail det = (InvoiceDetail) getInvoice().getDetailList().get(0);
			codeAsignacion = det.getProject() != null 
					? det.getProject().getName() : "";
		}
		return codeAsignacion;
	}
	
	public void setCodeAsignacion(String codeAsignacion) {
		this.codeAsignacion = codeAsignacion;
	}
	
	public void saveFacturaeInfo() {
		if(!AonStringUtils.isBlank(codeAsignacion)) {
			
			Domain domain = getDomain();
			User user = getUser();
			AON.saveFacturaeCodeAsignacion(domain, user, getInvoice().getId(), getInvoice().getRegistry().getId(), codeAsignacion);
		}
	}
}
