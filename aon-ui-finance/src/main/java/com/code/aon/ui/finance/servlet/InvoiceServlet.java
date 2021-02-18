package com.code.aon.ui.finance.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.company.InvestAsset;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.geozone.GeoZone;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.report.ReportException;
import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.jr.JRBeanCollectionDataSource;
import com.code.aon.report.jr.JRReport;
import com.code.aon.report.jr.JRReportFactory;
import com.code.aon.ui.finance.invoice.print.InvoiceDetailByDeliveryPrinter;
import com.code.aon.ui.finance.util.print.InvoiceReportScriptlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.JRLoader;

@WebServlet(name = "DownloadIncoice", urlPatterns = {"/aon_gwt_aio/invoice_download/*",
																"/invoice_download/*"})
public class InvoiceServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());
	
	public final String SALE_INVOICE = "/com/code/aon/ui/finance/report/saleInvoice.jasper";
	public final String SALE_INVOICE_GTA = "/com/code/aon/ui/finance/report/saleInvoiceGta.jasper";
	public final String SALE_INVOICE_TEMPLATE1 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate1.jasper";
	public final String SALE_INVOICE_TEMPLATE2 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate2.jasper";
	public final String SALE_INVOICE_TEMPLATE3 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate3.jasper";
	public final String SALE_INVOICE_TEMPLATE4 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate4.jasper";
	public final String SALE_INVOICE_TEMPLATE5 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate5.jasper";
	public final String SALE_INVOICE_TEMPLATE6 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate6.jasper";
	public final String SALE_INVOICE_TEMPLATE7 = "/com/code/aon/ui/finance/report/saleInvoiceTemplate7.jasper";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

			LOGGER.info("Invoice Servlet - GET METHOD");
			
			HashMap<String, String> parameters = getParameters(req.getPathInfo().substring(1));

			String domainName = parameters.get("domain");
			String domainId = parameters.get("domain_id");
			String login = parameters.get("login");
			String invoice = parameters.get("invoice");
			String registry = parameters.get("registry");
			
			Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
			
			String reportKey = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM).getValue();
			
			Company company = company(AON.getCompanyForDomain(domain.getName(), domain.getId(), login));
			
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> 
				f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
				.and(f.getDomainProperty().eq(domain.getId())), AttachType.REGISTRY);

			com.esferalia.aon.occam.api.model.registry.RecordData recordData = AON.getRecordData(domain.getName(), domain.getId(), login, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getRegistryProperty().eq(company.getId())));
			StringBuilder leftSideText = new StringBuilder("");
			
			if(company.getName() != null){
				leftSideText.append(company.getName()).append(" ");
			}
			if(recordData.getRegistration() != null){
				leftSideText.append(recordData.getRegistration()).append(" ");
			}
			if(recordData.getVolume() != null){
				leftSideText.append("Tomo ").append(recordData.getVolume()).append(" ");
			}
			if(recordData.getSection() != null){
				leftSideText.append("Sección ").append(recordData.getSection()).append(" ");
			}
			if(recordData.getPage() != null){
				leftSideText.append("Folio ").append(recordData.getPage()).append(" ");
			}
			if(recordData.getSheet() != null){
				leftSideText.append("Hoja ").append(recordData.getSheet()).append(" ");
			}
			if(recordData.getRecordDate() != null){
				leftSideText.append("con Fecha ").append(new SimpleDateFormat("dd/MM/yyyy").format(recordData.getRecordDate()));
			}			
			if(company.getDocument() != null && company.getDocumentCountry() != null){
				leftSideText.append(leftSideText.length()>0?", ":"");
				leftSideText.append("N.I.F.").append(": ");
				leftSideText.append(company.getDocumentCountry()).append("-");
				leftSideText.append(company.getDocument());
			}

			RegistryAddress registryAddress = registryAddress(AON.getRAddres(domain.getName(), domain.getId(), login, company.getId()));
			
			String invoiceFooterText="De conformidad con la Ley Orgánica 15/1999, le informamos que sus datos se hallan incorporados a un fichero titularidad de "+ company.getName() +" con la finalidad de cumplir con nuestra relación comercial. Puede ejercer los derechos de acceso, rectificación, cancelación y oposición en cualquier momento, mediante escrito, acompañado de copia de documento oficial que le identifique, dirigido a "+ company.getName() +", "+ 
					registryAddress.getFullAddress()
					+".";

			
			RegistryMedia phone = registryMedia(AON.getRMedia(domain.getName(), domain.getId(), login, f -> 
				f.getMediaProperty().eq((byte) MediaType.FIXED_PHONE.ordinal())
				.and(f.getRegistryProperty().eq(company.getId()))));
			
			RegistryMedia fax = registryMedia(AON.getRMedia(domain.getName(), domain.getId(), login, f -> 
				f.getMediaProperty().eq((byte) MediaType.FAX.ordinal())
				.and(f.getRegistryProperty().eq(company.getId()))));
			
			Invoice inv = AON.getInvoice(domain.getName(), domain.getId(), login, f -> 
					f.getIdProperty().eq(Integer.parseInt(invoice))
					.and(f.getDomainProperty().eq(domain.getId()))
					.and(f.getRegistryProperty().eq(Integer.parseInt(registry))));
		
			
			Set<InvoiceDetail> invoiceDetail = AON.getInvoiceDetails(domain.getName(), domain.getId(), login,
					f-> f.getIdProperty().eq(inv.getId()))
					.map(new InvoiceDetailFiller()).collect(Collectors.toCollection(HashSet::new));
			
			Boolean TRUE = true;
			Boolean FALSE = true;
		
			
			HashMap<String, Object> params = new HashMap<>();
			//params.put("REPORT_TIMER_ZONE", "");		
			params.put("invoiceDetailPrinter", new InvoiceDetailByDeliveryPrinter());
			params.put("printDiscountPriceApplied", TRUE);
			params.put("printHeader", TRUE);
			params.put("printLogo", TRUE);
			params.put("PriceStrategy",  new InvoicePriceStrategy());
			params.put("company", company);
			params.put("logoImageFile", new ByteArrayInputStream(attach.getData()));
			params.put("leftSideText", leftSideText.toString());
			params.put("address", registryAddress);
			params.put("phone", phone);
			params.put("fax", fax);
			params.put("invoiceFooterText",  invoiceFooterText);
			params.put("IS_IGNORE_PAGINATION", FALSE);	
			params.put("IsIgnorePagination", FALSE);	
			params.put("printNif", ReportPrintOption.LEFT_SIDE); 
			params.put("printHeaders", TRUE); 
			params.put("printRecordData", TRUE); 
			params.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.getBundle("com.code.aon.common.i18n.messages"));
			params.put("REPORT_LOCALE", new Locale("es_ES")); 
			
			params.put("printAddress", ReportPrintOption.FOOTER);
	// GET RMEDIO NO DB CONNECTOR -	params.put("printInternetData", ReportPrintOption.FOOTER);
			
			params.put("invoiceReportScriptlet_SCRIPTLET", new InvoiceReportScriptlet());
			params.put("REPORT_FORMAT_FACTORY", new DefaultFormatFactory());
			params.put("printName", ReportPrintOption.FOOTER);
			
			
			byte[] data = null;
			try {
				Collection<com.code.aon.finance.Invoice> l = new LinkedList<>();
				l.add(invoice(domain, login, inv, invoiceDetail));
				JRReport report = JRReportFactory.getJRReport(reportKey).setCurrentDomain(domain.getId());
				params.put("JASPER_REPORT", getJasperReport(domain, login, report));
				
				params.put("REPORT_PARAMETERS_MAP", params);
				
				Map<String, JasperReport> map = new HashMap<String, JasperReport>();
				map.put("invoiceDetail", getJasperReport(domain, login, JRReportFactory.getJRReport("invoiceDetail").setCurrentDomain(domain.getId())));
				map.put("invoiceTaxBreakDown", getJasperReport(domain, login, JRReportFactory.getJRReport("invoiceTaxBreakDown").setCurrentDomain(domain.getId())));
				map.put("invoicePrepayments", getJasperReport(domain, login, JRReportFactory.getJRReport("invoicePrepayments").setCurrentDomain(domain.getId())));
				map.put("invoiceFinances", getJasperReport(domain, login, JRReportFactory.getJRReport("invoiceFinances").setCurrentDomain(domain.getId())));
				params.put("nestedReports", map);
				
				data = createReport(report, params, l);
			} catch ( ReportException | JRException e) {
				e.printStackTrace();
			}
			

			/*	ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.PDF);
			reportManager.setCollectionProvider( new SingleCollectionProvider(invoice(inv)) );
			ByteArrayOutputStream out1 = new ByteArrayOutputStream();
			try {
				reportManager.execute( out1, reportKey, params);
			} catch (ReportException e) {
				e.printStackTrace();
			}

			data = out1.toByteArray();
			*/
			Integer length = data.length;
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
		        
			resp.addHeader("Content-Disposition","attachment; filename=\"asdasd.pdf" +"\"");
			resp.setContentType("application/msexcel");
			
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
		/*}finally{
			releaseFacesContext();
		}*/
	}

	public byte[] createReport(JRReport report, HashMap<String, Object> params, Collection<com.code.aon.finance.Invoice> values) throws JRException, ReportException {
		byte[] data = null; 
		if(values!=null && values.size()>0){
			JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(values);
			params.put("REPORT_DATA_SOURCE", jrbcds);
			JasperReport jr = (JasperReport) params.get("JASPER_REPORT");
			JasperPrint jasperPrint = JasperFillManager.fillReport(
				jr,params, jrbcds);

	
//			jasperPrint.setPageHeight(842);
//			jasperPrint.setPageWidth(595);
			
			data = JasperExportManager.exportReportToPdf(jasperPrint);
		}
		return data;
	}
	
	public HashMap<String, String> getParameters(String value){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] parameters = decode(value.getBytes()).split("&");
		for(String parameter : parameters){
			String[] values = parameter.split("=");
			map.put(values[0], values[1]);
		}
		return map;
	}
	
	public String decode(byte[] value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	} 
	
	public Company company(com.esferalia.aon.occam.api.model.Company company) {
		Company c = new Company();
		
		// Company
		c.setActive(company.isActive());
		c.setVatAccrualPayment(company.isVatAccrualPayment());
		c.setDomain(company.getDomain().getId());
		c.setSurcharge(company.isSurcharge());
		c.setWithholding(company.isWithholding());
		c.setEInvoice(company.iseInvoice());

		// Registry
		c.setId(company.getId());
		c.setDocument(company.getDocument());
		c.setName(company.getName());
		c.setAlias(company.getAlias());
		c.setConfidential(company.isConfidential());
		c.setDocumentType(DocumentType.values()[company.getDocumentType().ordinal()]);
		c.setSecurityLevel(SecurityLevel.values()[company.getSecurityLevel().ordinal()]);
		if(company.isLegalPerson()){
			c.setType(RegistryType.LEGAL);
		}		
		c.setDocumentCountry(Country.valueOf(company.getDocumentCountry().getIso2()));
		c.setNationality(Country.valueOf(company.getNationality().getIso2()));
		
		//c.getWeb();
		//c.getEmail();
		
		return c;
	}
	
	public RecordData recordData(com.esferalia.aon.occam.api.model.registry.RecordData recordData) {
		if(recordData.getId() == null){
			return new RecordData();
		}
		RecordData rd = new RecordData();
		rd.setCreationDate(recordData.getCreationDate());
		rd.setDescription(recordData.getDescription());
		rd.setDomain(recordData.getDomain());
		rd.setId(recordData.getId());
		rd.setNotary(recordData.getNotary());
		rd.setNumber(recordData.getNumber());
		rd.setPage(recordData.getPage());
		rd.setRecordDate(recordData.getRecordDate());
		rd.setRegistration(recordData.getRegistration());
		rd.setSection(recordData.getSection());
		rd.setSheet(recordData.getSheet());
		rd.setVolume(recordData.getVolume());

		Registry registry = new Registry();
		registry.setId(recordData.getRegistry());
		rd.setRegistry(registry);
		
		RegistryAttachment attach = new RegistryAttachment();
		attach.setId(recordData.getAttach());
		rd.setAttach(attach);

		return rd;
	}
	
	public RegistryAddress registryAddress(RAddress raddress){
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
	
	public RegistryMedia registryMedia(com.esferalia.aon.occam.api.model.registry.RegistryMedia rmedia){
		if(rmedia.getRegistry() == null){
			return new RegistryMedia();
		}
		RegistryMedia rm = new RegistryMedia();
		rm.setMediaType(MediaType.values()[rmedia.getMedia().value()]);
		rm.setDomain(rmedia.getDomain());
		rm.setId(rmedia.getId());
		rm.setValue(rmedia.getValue());
		rm.setComment(rmedia.getComment());
		rm.setAdministrative(rmedia.isAdministrative());
		rm.setCommercial(rmedia.isCommercial());
		rm.setTechnical(rmedia.isTechnical());
		Registry registry = new Registry();
		registry.setId(rmedia.getRegistry());
		rm.setRegistry(registry);
		return rm;
	}
	
	public com.code.aon.finance.Invoice invoice(Domain domain, String login, Invoice inv, Set<InvoiceDetail> invoiceDetail){
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
		
		Set<Finance> finances = AON.getFinanceStream(domain.getName(), domain.getId(), login, 
				f -> f.getInvoiceProperty().eq(invoice.getId())).map(new FinanceFiller())
				.collect(Collectors.toSet());
	
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
	
	protected void initFacesContext(HttpServletRequest request, HttpServletResponse response ) {
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

	protected void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}
	
	private static String REPORT_PATH = "/home/COMMON-RESOURCES/aon-report";

	private InputStream getTemplateInputStream(Domain domain, String login, ReportConfig config ) {
		InputStream input = null;
		String customTemplate = AON.getApplicationParameter(domain.getName(), domain.getId(), login, "REPORT_"+ config.getId()).getValue();
		if (! StringUtils.isEmpty(customTemplate) ) {
			File file = new File( config.getTemplate() );
			File customDirectory = new File( REPORT_PATH, customTemplate );
			if ( customDirectory.exists() && customDirectory.canRead() ) {
				File customFile = new File( customDirectory, file.getName() );
				if ( customFile.exists() && customFile.canRead() ) {
					try {
						input = new BufferedInputStream( new FileInputStream(customFile) );
					} catch (FileNotFoundException e) {
						//LOGGER.error( "Custome template not found: " + customFile, e);
					}
				}
			}
		} 
		if ( input == null ) {
			input = JRReport.class.getResourceAsStream(config.getTemplate());
		}
		return input;
	}
	
	/**
	 * Returns the JasperReport object that this object represents.
	 * 
	 * @return The JasperReport object that this object represents.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	public JasperReport getJasperReport(Domain domain, String login, JRReport report) throws ReportException {
		InputStream input = getTemplateInputStream(domain, login, report.getReportConfig());
		if (input == null) {
			throw new ReportException("Can not load report template!"); //$NON-NLS-1$
		}
		try {
			Object o = JRLoader.loadObject(input);
			input.close();
			return (JasperReport) o;
		} catch (JRException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}
	

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
			
			//invoiceDetail.setInvoice(invoice);
			
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
			bankAccount.setCountry(Country.valueOf(f.getBankAccount().getCountry().getIso2()));
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
		//	scope.setDomain(f.getScope().getDomain());
			finance.setScope(scope);
			
			finance.setSecurityLevel(SecurityLevel.values()[f.getSecurityLevel().ordinal()]);
			finance.setSourceId(f.getSourceId());
			
			return finance;			
		}
	}	

}
