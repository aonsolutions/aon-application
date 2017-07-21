package com.code.aon.ui.finance.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.InvestAsset;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.geozone.GeoZone;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;

@WebServlet(name = "DownloadSaleInvoice", urlPatterns = { "/aon_gwt_aio/saleinvoice_download/*", "/sid/*" })
public class SaleInvoiceServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER  = Logger.getLogger(SaleInvoiceServlet.class.getName());
	
	private String domainName = null; 
	private String login = null;
	private String invoiceId = null;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SaleInvoice Servlet - GET METHOD");
		
		initFacesContext(req, resp);
		try {			
			resolveParams(req, resp);
			flushInvoiceData(resp);
		} catch (Throwable e) {
			LOGGER.severe(">>>> SaleInvoiceServlet " + e.getMessage());
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
		} finally {
			releaseFacesContext();
		}	
	}
	
	private void resolveParams(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String value = req.getPathInfo().substring(1);
		String decode = new String(Base64.getDecoder().decode(value.getBytes()), "UTF-8");
		String[] parameters = decode.split("&");
		
		HashMap<String, String> map = new HashMap<String, String>();
		for(String parameter : parameters){
			String[] values = parameter.split("=");
			map.put(values[0], values[1]);
		}
		domainName = map.get("domain");
		login = map.get("login");
		invoiceId = map.get("invoice");
	}
	
	private void flushInvoiceData(HttpServletResponse resp) throws Exception {
		byte[] data = obtainInvoiceData();
		if(data==null){
			throw new ServletException("Invoice data is empty");
		}
		Integer length = data.length;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		
		resp.setHeader("Content-Disposition","inline; filename=\"invoice.pdf" +"\"");
		resp.setContentType("application/pdf");
		resp.setContentLength(data.length);
		
		if (length > 0 && length <= Integer.MAX_VALUE)
			resp.setContentLength((int)length);
		ServletOutputStream out = resp.getOutputStream();
		resp.setBufferSize(32768);
		int bufSize = resp.getBufferSize();
		byte[] buffer = new byte[bufSize];
		BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
		int bytes;
		while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
			out.write(buffer, 0, bytes);
		
		bis.close();
		bais.close();
		out.flush();
		out.close();
	}
	
	@SuppressWarnings("unchecked")
	private byte[] obtainInvoiceData(){
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
		String reportKey = AON.getApplicationParamenter(domain.getName(), domain.getId(), login,
				AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM).getValue();
		reportKey = reportKey==null?"saleInvoice":reportKey;
		// TODO search for custom report template
		
		Invoice inv = AON.getInvoice(domain.getName(), domain.getId(), login, f -> 
				f.getIdProperty().eq(Integer.parseInt(invoiceId))
				.and(f.getDomainProperty().eq(domain.getId())));
		Set<InvoiceDetail> invoiceDetail = AON.getInvoiceDetails(domain.getName(), domain.getId(), login,
				f-> f.getIdProperty().eq(inv.getId()))
				.map(new InvoiceDetailFiller())
				.collect(Collectors.toCollection(HashSet::new));
		Set<Finance> finances = AON.getFinanceStream(domain.getName(), domain.getId(), login, 
				f -> f.getInvoiceProperty().eq(inv.getId()))
				.map(new FinanceFiller())
				.collect(Collectors.toCollection(HashSet::new));
		com.code.aon.finance.Invoice to = invoice(domain, login, inv, invoiceDetail, finances);
		
		
		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( new SingleCollectionProvider(to) );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			reportManager.execute( baos, reportKey);
		} catch (Exception e) {
			LOGGER.severe(">>>> obtainInvoiceData " + e.getMessage());
			return null;
		}
		return baos.toByteArray();
	}
	
	//
	// FACES CONTEXT
	//
	
	private void initFacesContext(HttpServletRequest request, HttpServletResponse response ) {
		ServletContext context = getServletContext();
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext != null) {
				return;
			}
			
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

			Lifecycle lifecycle = lifecycleFactory
					.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			facesContext = facesContextFactory.getFacesContext(context,
					request, response, lifecycle);

			UIViewRoot view = facesContext.getApplication().getViewHandler()
					.createView(facesContext, "/home.jsf");

			facesContext.setViewRoot(view);

		} catch (Throwable throwable) {
			// TODO: Do some usefull with this.
			throwable.printStackTrace();
		}
	}

	private void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}
	
	//
	// MUTE TO OLD ENTITIES
	//
	
	private RegistryAddress registryAddress(RAddress raddress){
		RegistryAddress ra = new RegistryAddress();
		ra.setAddress(raddress.getAddress());
		ra.setAddress2(raddress.getAddress2());
		ra.setAddress3(raddress.getAddress3());
		ra.setAddressType(AddressType.values()[raddress.getType()]);
		ra.setAlias(raddress.getAlias());
		ra.setCity(raddress.getCity());
		ra.setDomain(raddress.getDomain());
		ra.setId(raddress.getId());
		ra.setMunicipalityCode(raddress.getMunicipality_code());
		ra.setNumber(raddress.getNumber());
		ra.setProvince(raddress.getGeozoneName());
		ra.setRecipient(raddress.getRecipient());
		ra.setStreetType(StreetType.valueOf(raddress.getStreet_type()));
		ra.setZip(raddress.getZip());
		
		Registry registry = new Registry();
		registry.setId(raddress.getRegistry());
		ra.setRegistry(registry);

		GeoZone geoZone = new GeoZone();
		geoZone.setId(raddress.getGeozone());
		geoZone.setName(raddress.getGeozoneName());
		ra.setGeozone(geoZone);

		return ra;
	}
		
	private com.code.aon.finance.Invoice invoice(Domain domain, String login, Invoice inv,
			Set<InvoiceDetail> invoiceDetail, Set<Finance> finances){
		com.code.aon.finance.Invoice invoice = new com.code.aon.finance.Invoice();
		
		invoice.setId(inv.getId());
		invoice.setReferenceCode(inv.getReferenceCode());
		Date issueDate = inv.getIssueDate() != null ? inv.getIssueDate() : new Date();
		invoice.setIssueDate(issueDate);
		invoice.setIssueDay(AonDateUtils.getDay(issueDate));
		invoice.setIssueMonth(AonDateUtils.getMonth(issueDate));
		invoice.setIssueYear(AonDateUtils.getYear(issueDate));

		Registry registry = new Registry();
		registry.setId(inv.getRegistry());
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(inv.getRegistryDocument());
		
		invoice.setStatus(InvoiceStatus.values()[inv.getStatus()]);
		invoice.setTransaction(InvoiceTransactionType.values()[inv.getTransaction().ordinal()]);
		invoice.setCreationUser(inv.getCreationUser());
		invoice.setCreationDate(inv.getCreationDate());
		invoice.setModificationUser(inv.getModificationUser());
		invoice.setModificationDate(inv.getModificationDate());
		
		Scope scope = new Scope();
		scope.setId(inv.getScope().getId());
		invoice.setScope(scope);
		
		invoice.setRegistryDocumentCountry(Country.valueOf(inv.getRegistryDocumentCountry().getIso2()));
		invoice.setRegistryDocumentType(DocumentType.values()[inv.getRegistryDocumentType().ordinal()]);
		invoice.setRegistryName(inv.getRegistryName());

		// address
		invoice.setTaxableBase(inv.getTaxableBase());
		invoice.setTaxDate(inv.getTaxDate());
		invoice.setTotal(inv.getTotal());
		invoice.setRetentionQuota(inv.getRetentionQuota());
		invoice.setVatQuota(inv.getVatQuota());
		invoice.setVatAccrualPayment(inv.isVatAccrualPayment());
		invoice.setComments(inv.getComments());
		invoice.setRectificationType(RectificationType.values()[inv.getRectificationType().ordinal()]);
		
		Invoice rInv = AON.getInvoice(domain.getName(), domain.getId(), login, 
				f-> f.getIdProperty().eq(inv.getRectificationInvoice()));
		
		com.code.aon.finance.Invoice rectInv = new com.code.aon.finance.Invoice();
		rectInv.setId(inv.getRectificationInvoice());
		rectInv.setSeries(rInv.getSeries());
		rectInv.setNumber(rInv.getNumber());
		rectInv.setReferenceCode(rInv.getReferenceCode());
		invoice.setRectificationInvoice(rectInv);
		
		Project project = new Project();
		project.setId(inv.getProject());
		invoice.setProject(project);
		
		invoice.setLines(invoiceDetail);
		invoice.setFinances(finances);
		
		invoice.setAdvance(inv.isAdvance());
		invoice.setConfidential(inv.isConfidential());
		invoice.setSeries(inv.getSeries());
		invoice.setNumber(inv.getNumber());
		invoice.setType(InvoiceType.values()[inv.getType().ordinal()]);
		
		invoice.setCreationDate(inv.getCreationDate());
		invoice.setCreationUser(inv.getCreationUser());
		invoice.setDomain(inv.getDomain());

		RegistryAddress registryAddress = registryAddress(AON.getRAddres(domain.getName(), domain.getId(), login, inv.getRegistry()));
		invoice.setRegistryAddress(registryAddress);
		
		return invoice;
	}

	
	//
	// FILLER
	//
	
	public static class InvoiceDetailFiller implements Function<com.esferalia.aon.occam.api.model.finance.InvoiceDetail, InvoiceDetail> {

		@Override
		public InvoiceDetail apply(com.esferalia.aon.occam.api.model.finance.InvoiceDetail id) {
			InvoiceDetail invoiceDetail =  new InvoiceDetail();
			invoiceDetail.setDescription(id.getDescription());

			DiscountExpression de = new DiscountExpression();
			de.setDiscountExpr(id.getDiscountExpression());
			invoiceDetail.setDiscountExpression(de);
			
			invoiceDetail.setDomain(id.getDomain());
			invoiceDetail.setId(id.getId());
			
			InvestAsset ia = new InvestAsset();
			ia.setId(id.getInvestAsset());
			invoiceDetail.setInvestAsset(ia);
			
			if(id.getItem()!=null){
				Item item = new Item();
				item.setId(id.getItem().getId());
				item.setDetail(id.getItem().getDetail());
				item.setDetail2(id.getItem().getDetail2());
				item.setDetail3(id.getItem().getDetail3());
				item.setPurchasePrice(id.getItem().getPurchasePrice());
				item.setPrice(id.getItem().getPrice());
				item.setDescription(id.getItem().getDescription());
				
				Product product = new Product();
				product.setId(id.getItem().getProductId());
				product.setName(id.getItem().getName());
				product.setCode(id.getItem().getCode());
				ProductCategory productCategory = new ProductCategory();
				productCategory.setName(id.getItem().getCategory());
				product.setCategory(productCategory);
				item.setProduct(product);
				
				invoiceDetail.setItem(item);
			}
			
			return invoiceDetail;
		}
	}	
	
	public static class FinanceFiller implements Function<com.esferalia.aon.occam.api.model.finance.Finance, Finance> {

		@Override
		public Finance apply(com.esferalia.aon.occam.api.model.finance.Finance f) {
			Finance finance =  new Finance();
			finance.setAdvance(f.isAdvance());
			finance.setAmount(f.getAmount());
			
			BankAccount bankAccount = new BankAccount();
			bankAccount.setBban1(f.getBankAccount().getBban1());
			bankAccount.setBban2(f.getBankAccount().getBban2());
			bankAccount.setBban3(f.getBankAccount().getBban3());
			bankAccount.setBban4(f.getBankAccount().getBban4());
			bankAccount.setBban5(f.getBankAccount().getBban5());
			bankAccount.setBban6(f.getBankAccount().getBban6());
			bankAccount.setBban7(f.getBankAccount().getBban7());
			bankAccount.setBban8(f.getBankAccount().getBban8());
			bankAccount.setCheck(f.getBankAccount().getCheck());
			if(f.getBankAccount().getCountry()!=null){				
				bankAccount.setCountry(Country.valueOf(f.getBankAccount().getCountry().getIso2()));
			}
			finance.setBankAccount(bankAccount);
			
			finance.setBankAlias(f.getBankAlias());
			finance.setBic(f.getBic());
			finance.setChequeNumber(f.getChequeNumber());
			finance.setConcept(f.getConcept());
			finance.setConfidential(f.isConfidential());
			finance.setCreationDate(f.getCreationDate());
			finance.setCreationUser(f.getCreationUser());
			finance.setDomain(f.getDomain());
			finance.setDueDate(f.getDueDate());
			finance.setExpenses(f.getExpenses());

			Finance financeGroup = new Finance();
			financeGroup.setId(f.getFinanceGroup());
			finance.setFinanceGroup(financeGroup);
			
			finance.setId(f.getId());
			
			com.code.aon.finance.Invoice invoice = new com.code.aon.finance.Invoice();
			invoice.setId(f.getInvoice().getId());
			finance.setInvoice(invoice);
			finance.setManual(f.isManual());
			finance.setModificationDate(f.getModificationDate());
			finance.setModificationUser(f.getModificationUser());
			finance.setPayment(f.isPayment());
			
			PayMethod payMethod = new PayMethod();
			payMethod.setId(f.getPayMethod());
			payMethod.setName(f.getPayMethodName());
			finance.setPayMethod(payMethod);
			
			finance.setPayroll(f.isPayroll());
			finance.setPrepayment(f.isPrepayment());

			Registry registry = new Registry();
			registry.setId(f.getRegistry().getId());
			finance.setRegistry(registry);
			
			finance.setRegistryDocument(f.getRegistryDocument());
			finance.setRegistryDocumentCountry(Country.valueOf(f.getRegistryDocumentCountry().getIso2()));
			finance.setRegistryDocumentType(DocumentType.values()[f.getRegistryDocumentType().ordinal()]);
			finance.setRegistryName(f.getRegistryName());
			finance.setRemarks(f.getRemarks());
			
			Scope scope = new Scope();
			scope.setId(f.getScope().getId());
			scope.setDescription(f.getScope().getDescription());
			finance.setScope(scope);
			
			finance.setSecurityLevel(SecurityLevel.values()[f.getSecurityLevel().ordinal()]);
			finance.setSourceId(f.getSourceId());
			
			return finance;			
		}
	}	

}
