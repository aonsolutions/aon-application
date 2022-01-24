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

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletResponse;

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
import com.code.aon.customer.IEdiSupport;
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
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.PrintParametersController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
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
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.seres.writer.udapa.UdapaSaleInvoiceWriter;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiMain;

public class SaleInvoiceController extends InvoiceController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private RegistryValidationManager vm;
	private DeliveryTransferManager deliveryTransferManager;
	private boolean showDeliveryTransferWindow;
	private boolean showDeliveryFilterWindow;
	private boolean showTbaiWindow;
	
	private EdiInvoiceImporterHandler ediImporter;
	@Deprecated
	private UdapaEdiInvoiceImporterHandler udapaImporter;
	
	private FtpSaleInvoiceDownloadHandler ftpEdiDownloader;
	
	private FtpSaleInvoiceUploaderHandler ftpEdiUploader;
	
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
	
	public boolean isTbaiInvoice() {
		return isTbai() && !AonStringUtils.isBlank(getTbaiUrl());
	}
	
	public boolean isTbaiLroe() {
		Invoice invoice = (Invoice) this.getTo();
		return isTbai() && isBizkaia() && invoice.getNumber() > 0;
	}
	
	public String getTbaiUrl() {
		Invoice invoice = (Invoice) this.getTo();
		Integer domainId = DomainManager.getCurrentDomain();
		String domainName = AonUtil.getDomainName();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		return TbaiData.getInstance(getTbaiConfiguration()).getTbaiUrl(domainName, domainId, login, invoice.getId());
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
	
	@Deprecated
	public void onExportUdapaEdiFile(ActionEvent event) {
		FileOutput output = null;
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			Invoice invoice = (Invoice) this.getTo();
			
			String customerEdiMainCode = null;
			String customerEdiOperationCode = null;
			if(invoice.getRegistryAddress()!=null && invoice.getRegistryAddress().getId()!=null){
				CustomerEdiSupportController ediSupport = (CustomerEdiSupportController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
				customerEdiMainCode = ediSupport.getEdiCodes(invoice.getRegistry(), invoice.getRegistryAddress()).get(IEdiSupport.CABECERA);
				customerEdiOperationCode = ediSupport.getEdiCodes(invoice.getRegistry(), invoice.getRegistryAddress()).get(IEdiSupport.FACTURA);
			}
			CompanyController company = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			String companyEdiCode = company.getEdiCompanyCode();
			
			// writer file
			UdapaSaleInvoiceWriter writer = new UdapaSaleInvoiceWriter();
			output = writer.createFile(invoice, getPriceStrategy(), companyEdiCode, customerEdiMainCode, customerEdiOperationCode);
			
			// download file
			String fileName = "inv_" + invoice.getSeries()+"_"+invoice.getNumber();
			byte[] data = output.getContent();
			int size = data.length;
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, fileName + ".edi", null, size);
			InputStream fileIn = new BufferedInputStream( new ByteArrayInputStream(data) );
			IOUtils.copy( fileIn, out );
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
		if(isNevv() && isTbai()) {
			Invoice invoice = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Integer number = AON.getInvoiceMinNumber(domainName, domainId, login, com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, invoice.getSeries());
			invoice.setNumber(number < 0 ? number : -1);
		}
		super.accept();
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
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		JSONObject json = new JSONObject()
				.put(IJsonNames.ID, invoice.getId())
				.put(IJsonNames.SOURCE, "invoice")
				.put("domain_id", domain.getId())
				.put("domain_name", domain.getName())
				.put(IJsonNames.LOGIN, UserUtils.getInstance().getLoggedUser().getLogin());		
		return "/ms/api/download_invoice_pdf?json=" + Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
	}

	@Transient
	public synchronized void issueInvoiceLroe() {
		try {
			checkCertificate();		
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Integer userId = UserUtils.getInstance().getLoggedUser().getId();
			
			com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());

			Byte[] types = new Byte[]{com.esferalia.aon.occam.api.model.type.InvoiceType.SALES.value()};
			if(invoice.getNumber() < 1) {
				Integer number = AON.getInvoiceNextNumber(domainName, invoice.getDomain(), login, types, inv.getSeries());
				invoice.setNumber(number);
				invoice.setReferenceCode(null);
				invoice.setIssueDate(new Date());
				AON.updateInvoice(domainName, invoice.getDomain(), login, invoice, true);
			}

			Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domainName, invoice.getDomain(), login);
			
			tbaiConfiguration.setCertificate(AON.getCertificate(domainName, invoice.getDomain(), login, userId, CertificateType.AEAT.name()));
			if(tbaiConfiguration.isActive()) {
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionLROE(company, invoice, tbaiConfiguration);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
		
	@Transient
	public synchronized void issueInvoice() {
		try {
			checkCertificate();		
			Invoice inv = (Invoice) getTo();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Integer userId = UserUtils.getInstance().getLoggedUser().getId();
			
			com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());

			if(AonStringUtils.isBlank(invoice.getRegistryDocument())) {
				throw new Exception("El Documento del cliente está vacio.");
			}
			
			Byte[] types = new Byte[]{com.esferalia.aon.occam.api.model.type.InvoiceType.SALES.value()};
			if(invoice.getNumber() < 1) {
				Integer number = AON.getInvoiceNextNumber(domainName, invoice.getDomain(), login, types, inv.getSeries());
				invoice.setNumber(number);
				invoice.setReferenceCode(null);
				invoice.setIssueDate(new Date());
			}
			
			AON.updateInvoice(domainName, invoice.getDomain(), login, invoice, true);
			Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domainName, invoice.getDomain(), login);
			
			tbaiConfiguration.setCertificate(AON.getCertificate(domainName, invoice.getDomain(), login, userId, CertificateType.AEAT.name()));
			if(tbaiConfiguration.isActive()) {
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionTBAI(company, invoice, tbaiConfiguration);
			}
		
			// SII
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	private static Certificate checkCertificate() throws Exception {
		Integer domainId = DomainManager.getCurrentDomain();
		String domainName = AonUtil.getDomainName();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Integer userId = UserUtils.getInstance().getLoggedUser().getId();
		Certificate cert = new Certificate();
		try {
			cert =  AON.getCertificate(domainName, domainId, login, userId, CertificateType.AEAT.name());
		} catch (Exception e) {
//			AonUtil.addErrorMessage("Error al obtener el certificado.");
			throw new AbortProcessingException("Error al obtener el certificado.");
		}
		try {
			if(!checkCert(cert.getCertificate(), cert.getPassword())) {
//				AonUtil.addErrorMessage("El certificado o la contraseña no son correctos.");
				throw new AbortProcessingException("El certificado o la contraseña no son correctos.");
			}
		} catch (Exception e) {
//			AonUtil.addErrorMessage("El certificado o la contraseña no son correctos.");
			throw new AbortProcessingException("El certificado o la contraseña no son correctos.");
		}		
		if(cert.isEmpty()) {
//			AonUtil.addErrorMessage("El certificado no existe.");
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
	
	
	@Override
	public void onRemove(ActionEvent event) {
		try {
			if(isTbaiInvoice()) {
				checkCertificate();
				Invoice inv = (Invoice) getTo();
				String domainName = AonUtil.getDomainName();
				String login = UserUtils.getInstance().getLoggedUser().getLogin();
				Integer userId = UserUtils.getInstance().getLoggedUser().getId();
				com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), login, inv.getId());
				Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), login);
				TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domainName, invoice.getDomain(), login);
				tbaiConfiguration.setCertificate(AON.getCertificate(domainName, invoice.getDomain(), login, userId, CertificateType.AEAT.name()));
				TbaiMain tbai = new TbaiMain();
				tbai.createAnulacionTBAI(company, invoice, tbaiConfiguration);
			}
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
}