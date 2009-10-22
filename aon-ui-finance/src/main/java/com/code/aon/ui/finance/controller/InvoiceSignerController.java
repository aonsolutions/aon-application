package com.code.aon.ui.finance.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import net.esle.sinadura.core.certificado.Certificado;
import net.esle.sinadura.core.firma.DocumentFactory;
import net.esle.sinadura.core.firma.DocumentIFace;
import net.esle.sinadura.core.firma.SignStoreFactory;
import net.esle.sinadura.core.firma.SignStoreIFace;
import net.esle.sinadura.core.firma.exceptions.SinaduraCoreException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.util.SingleCollectionProvider;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.SecurityInfo;

public class InvoiceSignerController extends BasicController{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceSignerController.class.getName());
	
	private static final String DEFAULT_TSA_URL = "http://ocsp.izenpe.com:8093"; 

	private static final String SIGN_IMAGE_PATH = "sign.png";
	
	private static final String SALE_INVOICE_REPORT = "saleInvoice";
	
	private RegistryAttachment keystore;
	
	private String password;
	
	private String certificatePassword;
	
	private SignStoreIFace signStore;
	
	private Certificado certificado;		
	
	private String alias;
	
	private boolean showCertificatePassword;
	
	private boolean showSignWindow;
	
	private List<SelectItem> digitalCertificates;
	
	private List<SelectItem> certificates;

	private Set<Integer> checks = new HashSet<Integer>();
	
	private IManagerBean invoiceAttachBean;
	
	public InvoiceSignerController() {
		try {
			invoiceAttachBean = BeanManager.getManagerBean(InvoiceAttachment.class);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public RegistryAttachment getKeystore() {
		return keystore;
	}

	public void setKeystore(RegistryAttachment keystore) {
		this.keystore = keystore;
	}

	public SignStoreIFace getSignStore() {
		return signStore;
	}

	public void setSignStore(SignStoreIFace signStore) {
		this.signStore = signStore;
	}

	public Certificado getCertificado() {
		return certificado;
	}

	public void setCertificado(Certificado certificado) {
		this.certificado = certificado;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<SelectItem> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<SelectItem> certificates) {
		this.certificates = certificates;
	}

	public boolean isShowCertificatePassword() {
		return showCertificatePassword;
	}

	public void setShowCertificatePassword(boolean showCertificatePassword) {
		this.showCertificatePassword = showCertificatePassword;
	}

	public boolean isShowSignWindow() {
		return showSignWindow;
	}
	
	public String getCertificatePassword() {
		return certificatePassword;
	}

	public void setCertificatePassword(String certificatePassword) {
		this.certificatePassword = certificatePassword;
	}

	public void setShowSignWindow(boolean value) {
		this.showSignWindow = value;
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Invoice invoice = (Invoice)iter.next();
			checks.add( invoice.getId() );
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedInvoices();
	}

	public boolean getRowChecked() {
		Invoice to = (Invoice) model.getRowData();
		return checks.contains(to.getId());
	}

	public void setRowChecked(boolean rowChecked) {
		Integer id = ((Invoice) model.getRowData()).getId();		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
	}

	public Set<Integer> getCheckedInvoices() {
		return checks;
	}

	public void clearCheckedInvoices() {
		checks = new HashSet<Integer>();
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		clearCheckedInvoices();
	}
	
	public boolean isModelToSigned() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			Invoice invoice = (Invoice)this.getModel().getRowData();
			return checks.contains(invoice);
		}
		return true;
	}

	public boolean isSelectionEmpty() {
		return this.checks.isEmpty();
	}
	
	public void onSignSelected(ActionEvent event){
		if (! resolveCertificado() ) {
			return;
		}
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );

			Iterator<Integer> iter = getCheckedInvoices().iterator();
			while(iter.hasNext()){
				Integer id = iter.next();
				try {
					Invoice invoice = (Invoice) getManagerBean().get(id);
					if (! invoice.isSigned() ) {
						byte[] pdfData = getInvoicePDF(invoice);
						
						HibernateUtil.beginTransaction(sessionName);
						
						signInvoice(invoice, pdfData);

						HibernateUtil.getSession(sessionName).flush();					
						HibernateUtil.commitTransaction(sessionName);
					}
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.log(Level.SEVERE, msg, e);
					}
					String msg =  "Error recording invoice:  " + id;
					LOGGER.log(Level.SEVERE, msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			}
			clearCheckedInvoices();
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			setShowSignWindow(false);
		}
	}
	
	public InvoiceAttachment getSignedInvoice( Invoice invoice ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String type = invoiceAttachBean.getFieldName( IFinanceAlias.INVOICE_ATTACHMENT_MIME_TYPE );
		criteria.addEqualExpression( type, MimeType.MIME_PDF );
		String invoiceField = invoiceAttachBean.getFieldName( IFinanceAlias.INVOICE_ATTACHMENT_INVOICE_ID );
		criteria.addEqualExpression( invoiceField, invoice.getId() );		
		List<ITransferObject> list = invoiceAttachBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (InvoiceAttachment) list.get(0);
		}
		return null;
	}	
		
	public void cancelSignInvoice( Invoice invoice ) throws ManagerBeanException { 
		InvoiceAttachment ia = getSignedInvoice( invoice );
		if ( ia != null ) {
			invoiceAttachBean.remove( ia );
		}
		invoice.setSigned( false );
		getManagerBean().update( invoice );
	}
	
	public byte[] getInvoicePDF( Invoice invoice ) throws IOException, ReportException, ManagerBeanException {
		ReportManager report = new ReportManager();
		report.setCollectionProvider( new SingleCollectionProvider(invoice) );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		report.execute( out, SALE_INVOICE_REPORT);
		return out.toByteArray();
	}	
		
	private byte[] getSignedFileData( byte[] in, SignStoreIFace signStore, Certificado certificado, boolean visible ) throws SinaduraCoreException, IOException {
		DocumentIFace document = DocumentFactory.buildPDFDocument( in );
		document.setTSURL( DEFAULT_TSA_URL );
		InputStream imageIS = SaleInvoiceController.class.getResourceAsStream(SIGN_IMAGE_PATH);
		byte[] imageData = IOUtils.toByteArray( imageIS );
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.firmar( signStore, certificado, out, visible, 
				company.getAlias(), companyController.getMainAddress().getCity(), imageData,
				305, 745, 405, 795 );		
		return out.toByteArray();		
	}	

	public void signInvoice( Invoice invoice ) throws ManagerBeanException, SinaduraCoreException, ReportException, IOException { 
		signInvoice(invoice, getInvoicePDF(invoice));
	}

	private void signInvoice( Invoice invoice, byte[] pdfData ) throws ManagerBeanException, SinaduraCoreException, ReportException, IOException { 
		byte[] signedFileData = getSignedFileData( pdfData, signStore, certificado, true );
		
		InvoiceAttachment ia = new InvoiceAttachment();
		ia.setData( signedFileData );
		ia.setInvoice( invoice );
		ia.setMimeType( MimeType.MIME_PDF );
		IManagerBean bean = BeanManager.getManagerBean(InvoiceAttachment.class);
		bean.insert( ia );
		
		invoice.setSigned( true );
		getManagerBean().update( invoice );
	}
	
	public void onCancelSignSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );

			Iterator<Integer> iter = getCheckedInvoices().iterator();
			while(iter.hasNext()){
				Integer id = iter.next();
				try {
					HibernateUtil.beginTransaction(sessionName);

					Invoice invoice = (Invoice) getManagerBean().get(id);
					cancelSignInvoice(invoice);

					HibernateUtil.getSession(sessionName).flush();					
					HibernateUtil.commitTransaction(sessionName);
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.log(Level.SEVERE, msg, e);
					}
					String msg =  "Error recording invoice:  " + id;
					LOGGER.log(Level.SEVERE, msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			}
			clearCheckedInvoices();
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public String onReport( Invoice invoice ) throws ManagerBeanException, IOException, ReportException { 
		if ( invoice.isSigned() ) {
			InvoiceAttachment ia = getSignedInvoice( invoice );
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) ctx.getExternalContext().getResponse();
			res.setContentType( MimeType.MIME_PDF.getName() );
			IOUtils.write( ia.getData(), res.getOutputStream() );
			res.flushBuffer();
			ctx.responseComplete();
			return null;
		} else {
			ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
			return report.onExecute();
		}
	}

	public void onShowSignWindow( ActionEvent event ) throws ManagerBeanException {
		setShowSignWindow(true);
		setShowCertificatePassword(false);
		setPassword(null);
		setSignStore(null);
		setCertificado(null);
		setAlias(null);
		setCertificates(null);
		loadDigitalCertificates();
	}
	
	public List<SelectItem> getDigitalCertificates() {
		return digitalCertificates;
	}

	public void loadDigitalCertificates() throws ManagerBeanException {
		digitalCertificates = new LinkedList<SelectItem>();
		
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();

		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DIGITAL_CERTIFICATE);
		Iterator<ITransferObject> iterator = rattachBean.getList(criteria).iterator();
		while ( iterator.hasNext() ) {
			RegistryAttachment ra = (RegistryAttachment) iterator.next();
			SelectItem item = new SelectItem(ra, ra.getDescription());
			digitalCertificates.add(item);
		} 
	}	
	
	public boolean resolveCertificado() {
		boolean resolved = false;
		try {
			if ( this.signStore == null ) {
				ByteArrayInputStream in = new ByteArrayInputStream( keystore.getData() );
				this.signStore = SignStoreFactory.buildSingStorePKSC12( in, password );
				List<Certificado> certificados = signStore.getCertificados();
				if ( certificados.size() == 1 ) {
					certificado = certificados.get(0);
					certificado.setPassword( password );
					resolved = signStore.verifyAliasPassword(certificado);
					this.showCertificatePassword = resolved;
				} else {
					certificates = new LinkedList<SelectItem>();
					for( Certificado c : certificados ) {
						SelectItem item = new SelectItem(c.getAlias(), c.getAlias() );
						certificates.add(item);					
					}
				}				
			} else {
				this.certificado = this.signStore.getCertificado(alias);
				if (! StringUtils.isEmpty(certificatePassword) ) {
					certificado.setPassword( certificatePassword );
				} else {
					certificado.setPassword( password );
				}
				resolved = signStore.verifyAliasPassword(certificado);
				this.showCertificatePassword = resolved;
			}
		} catch ( SinaduraCoreException e ) {		
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
		return resolved;
	}	
	public SecurityInfo getSecurityInfo() {
		SecurityInfo si = new SecurityInfo( signStore.getKeySore(), certificado.getAlias(), password);
		return si;
	}
	
	
}