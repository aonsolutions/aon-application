package com.code.aon.ui.finance.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
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
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.report.IReportConstants;
import com.code.aon.ui.finance.invoice.print.InvoiceDetailByDeliveryPrinter;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

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
		String reportKey = AON.getApplicationParameter(domain.getName(), domain.getId(), login,
				AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM).getValue();
		reportKey = reportKey==null?"saleInvoice":reportKey;
		// TODO search for custom report template
		
		Invoice inv = AON.getInvoice(domain.getName(), domain.getId(), login, f -> 
				f.getIdProperty().eq(Integer.parseInt(invoiceId)));
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
		reportManager.setCollectionProvider( new InvoiceSingleCollectionProvider(to) );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			reportManager.execute( baos, reportKey);
		} catch (Exception e) {
			LOGGER.severe(">>>> obtainInvoiceData " + e.getMessage());
			return null;
		}
		return baos.toByteArray();
	}
	
	public class InvoiceSingleCollectionProvider implements ICollectionProvider {
		
		private ITransferObject to;
		
		public InvoiceSingleCollectionProvider(ITransferObject to) {
			this.to = to;
		}

		@SuppressWarnings("rawtypes")
		public Collection getCollection() {
			return getCollection(false);
		}

		@SuppressWarnings("rawtypes")	
		public Collection getCollection(boolean forceRefresh) {
			List<ITransferObject> l = new LinkedList<ITransferObject>();
			l.add( to );
			return l;
		}	
		
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
	
	private static RegistryAddress registryAddress(RAddress raddress){
		RegistryAddress ra = new RegistryAddress();
		if(raddress!=null && raddress.getId()!=null){
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
		}
		return ra;
	}
		
	private static com.code.aon.finance.Invoice invoice(Domain domain, String login, Invoice inv,
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
		
		invoice.setStatus(inv.isRecorded() ? InvoiceStatus.SCORED : InvoiceStatus.PENDING);
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
		
		com.code.aon.finance.Invoice rectInv = new com.code.aon.finance.Invoice();
		inv.getRectificationInvoice()
		.ifPresent(rInv -> {
			rectInv.setId(rInv.getId());
			rectInv.setSeries(rInv.getSeries());
			rectInv.setNumber(rInv.getNumber());
			rectInv.setReferenceCode(rInv.getReferenceCode());
		});
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
			de.setDiscountExpr(id.getDiscountExpression().getDiscountExpr());
			invoiceDetail.setDiscountExpression(de);
			
			invoiceDetail.setDomain(id.getDomain());
			invoiceDetail.setId(id.getId());
			
			InvestAsset ia = new InvestAsset();
			ia.setId(id.getInvestAsset().map( ias -> ias.getId()).orElse(null));
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
				product.setId(id.getItem().getProduct().getId());
				product.setName(id.getItem().getProduct().getName());
				product.setCode(id.getItem().getProduct().getCode());
				ProductCategory productCategory = new ProductCategory();
				productCategory.setName(id.getItem().getProduct().getCategory().getName());
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
	
	public class SaleInvoiceDetailByDeliveryPrinter extends InvoiceDetailByDeliveryPrinter {
		@Override
		public SaleInvoiceDetailByDeliveryPrinter getInstance() {
			return new SaleInvoiceDetailByDeliveryPrinter();
		}

		@Override
		public Collection<InvoiceDetail> getCollection(Integer invoiceId, boolean productTypeOrder,
				Boolean searchPrepayments, Boolean searchIncrease, ProductType type) {		
			List<InvoiceDetail> invoiceDetailList = new LinkedList<InvoiceDetail>();
			return invoiceDetailList;
		}
		
		@Override
		public Double getTotalFinanceAdvance(Integer invoiceId) {
			return null;
		}
		
		@Override
		protected Delivery obtainDelivery(InvoiceDetail invoiceDetail) {
			return null;
		}

		@Override
		protected List<InvoiceDetail> getListOrdered(Map<Integer, List<InvoiceDetail>> deliveryMap) throws 
			ExpressionException {
			List<InvoiceDetail> invoiceDetailList = new LinkedList<InvoiceDetail>();
			return invoiceDetailList;
		}
	}
	
	public static final String SALE_INVOICE_REPORT_PATH = "/com/code/aon/ui/finance/report/";
	
	public static void main(String[] args) {
		
		HashMap<String, Object> map = new HashMap<>();
		JasperPrint jasperPrint = null;
		Connection connection = null;
		CloseableAONContext ctx = null;
		String domainName = "ibaigane.esferalia.net";
		int domainId = 2013;
		String login = "admin";
		String invoiceId = "10683995";
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			connection = ctx.getDslContext().configuration().connectionProvider().acquire();
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
		map.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.TRUE);
		// TODO obtain locale
//		map.put(JRParameter.REPORT_LOCALE, Locale.US);
		map.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.getBundle("com.code.aon.common.i18n.messages"));

//		Map<String, JasperReport> nested = new HashMap<String, JasperReport>();
//		try {
//			nested.put("invoiceDetailDefault", JRReportFactory.getJRReport("invoiceDetailDefault").getJasperReport());
//			nested.put("invoiceTaxBreakDown", JRReportFactory.getJRReport("invoiceTaxBreakDown").getJasperReport());
//			nested.put("invoicePrepaymentsDefault", JRReportFactory.getJRReport("invoicePrepaymentsDefault").getJasperReport());
//			nested.put("invoiceFinancesDefault", JRReportFactory.getJRReport("invoiceFinancesDefault").getJasperReport());
//		} catch (ReportException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		map.put(IReportConstants.NESTED_REPORTS, nested);
		
		
		InputStream reportStream = SaleInvoiceServlet.class.getResourceAsStream(SALE_INVOICE_REPORT_PATH+"saleInvoice.jasper");
		reportStream = SaleInvoiceServlet.class.getResourceAsStream(SALE_INVOICE_REPORT_PATH+"saleInvoiceTemplate1.jasper");
		reportStream = SaleInvoiceServlet.class.getResourceAsStream(SALE_INVOICE_REPORT_PATH+"saleInvoiceTemplate5.jasper");
		
		try {
			Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
			String reportKey = AON.getApplicationParameter(domain.getName(), domain.getId(), login,
					AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM).getValue();
			reportKey = reportKey==null?"saleInvoice":reportKey;
			// TODO search for custom report template
			
			Invoice inv = AON.getInvoice(domain.getName(), domain.getId(), login, f -> 
					f.getIdProperty().eq(Integer.parseInt(invoiceId)));
			Set<InvoiceDetail> invoiceDetail = AON.getInvoiceDetails(domain.getName(), domain.getId(), login,
					f-> f.getIdProperty().eq(inv.getId()))
					.map(new InvoiceDetailFiller())
					.collect(Collectors.toCollection(HashSet::new));
			Set<Finance> finances = AON.getFinanceStream(domain.getName(), domain.getId(), login, 
					f -> f.getInvoiceProperty().eq(inv.getId()))
					.map(new FinanceFiller())
					.collect(Collectors.toCollection(HashSet::new));
			com.code.aon.finance.Invoice to = invoice(domain, login, inv, invoiceDetail, finances);
			List<ITransferObject> list = new LinkedList<ITransferObject>();
			list.add( to );
			JRDataSource jrds = new com.code.aon.report.jr.JRBeanCollectionDataSource(list);
			jasperPrint = JasperFillManager.fillReport(reportStream,map,jrds);
//			jasperPrint = JasperFillManager.fillReport(reportStream,map,connection);
			OutputStream output = new FileOutputStream(new File("/tmp/invoice_"+inv.getReferenceCode()+".pdf")); 
			JasperExportManager.exportReportToPdfStream(jasperPrint, output); 
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
