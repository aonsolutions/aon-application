package com.code.aon.ui.sign.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class SignerController {

	private static final Logger LOGGER = Logger.getLogger(SignerController.class.getName());
	
	private static final String DEFAULT_TSA_URL = "http://ocsp.izenpe.com:8093"; 

	private static final String SIGN_IMAGE_PATH = "sign.png";
	
	private Company company;
	
	private RegistryAttachment keystore;
	
	private String password;

	private String certificatePassword;

	private String alias;
	
	private List<SelectItem> certificates;
	
	private List<SelectItem> digitalCertificates;
	
	private SignStoreIFace signStore;
	
	private Certificado certificado;		
	
	private boolean showCertificatePassword;

	private boolean showSignWindow;

	private ISignatureController signatureController;
	
	private String reportKey;
	
	private IManagerBean attachmentBean;
	
	public SignerController() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		this.company = companyController.obtainCompany();
	}

	public ISignatureController getSignatureController() {
		return signatureController;
	}

	public void setSignatureController(ISignatureController signatureController) {
		this.signatureController = signatureController;
		this.attachmentBean = signatureController.getAttachmentBean();
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
	
	public String getReportKey() {
		return reportKey;
	}

	public void setReportKey(String reportKey) {
		this.reportKey = reportKey;
	}

	public void setShowSignWindow(boolean value) {
		this.showSignWindow = value;
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
		this.digitalCertificates = new LinkedList<SelectItem>();
		
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

	public int getCertificateCount() throws ManagerBeanException {
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DIGITAL_CERTIFICATE);
		return rattachBean.getCount(criteria);
	}	
	
	public byte[] getReport( ITransferObject to ) throws ReportException {
		return getReport(to, getReportKey(), null);
	}	

	public byte[] getReport( ITransferObject to, String report, OutputFormat outputFormat ) throws ReportException {
		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( new SingleCollectionProvider(to) );
		if ( outputFormat != null ) {
			reportManager.setOutputFormat( outputFormat );	
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		reportManager.execute( out, report);
		return out.toByteArray();
	}	

	private byte[] getSignedFileData( byte[] in, SignStoreIFace signStore, Certificado certificado, boolean visible ) throws SinaduraCoreException, IOException {
		DocumentIFace document = DocumentFactory.buildPDFDocument( in );
		document.setTSURL( DEFAULT_TSA_URL );
		InputStream imageIS = SignerController.class.getResourceAsStream(SIGN_IMAGE_PATH);
		byte[] imageData = IOUtils.toByteArray( imageIS );
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.firmar( signStore, certificado, out, visible, 
				company.getAlias(), companyController.getMainAddress().getCity(), imageData,
				305, 745, 405, 795 );		
		return out.toByteArray();		
	}	

	public IAttachment getSignedAttachment( Serializable parentId ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String type = attachmentBean.getFieldName( signatureController.getAttchmentMimeTypeAlias() );
		criteria.addEqualExpression( type, MimeType.MIME_SIGNED_PDF );
		String parentAlias = attachmentBean.getFieldName( signatureController.getAttchmentParentAlias() );
		criteria.addEqualExpression( parentAlias, parentId );		
		List<ITransferObject> list = attachmentBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (IAttachment) list.get(0);
		}
		return null;
	}	
	
	public String onReport() {
		try {
			ITransferObject to = signatureController.getTo();
			if ( signatureController.isSigned(to) ) {
				Serializable id = signatureController.getManagerBean().getId(to);
				IAttachment ia = getSignedAttachment( id );
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
		} catch (Throwable e) {
			LOGGER.severe(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
	}

	public void cancelSign( ITransferObject to ) throws ManagerBeanException { 
		Serializable id = signatureController.getManagerBean().getId(to);
		IAttachment attachment = getSignedAttachment( id );
		if ( attachment != null ) {
			attachmentBean.remove( attachment );
		}
		updateSigned(to, false);
	}

	public void sign( ITransferObject to ) throws ManagerBeanException, SinaduraCoreException, ReportException, IOException { 
		sign(to, getReport(to) );
	}

	private void sign( ITransferObject to, byte[] pdfData ) throws ManagerBeanException, SinaduraCoreException, ReportException, IOException { 
		byte[] signedFileData = getSignedFileData( pdfData, signStore, certificado, true );
		
		IAttachment attachment = signatureController.newAttachment(to);
		attachment.setData( signedFileData );
		attachment.setMimeType( MimeType.MIME_SIGNED_PDF );
		attachmentBean.insert( attachment );

		updateSigned(to, true);
	}
	
	private void updateSigned( ITransferObject to, boolean value ) throws ManagerBeanException {
		signatureController.setSigned(to, value);
		try {
			signatureController.getManagerBean().restoreNullSubPOJOs(to);
			signatureController.getManagerBean().update(to);
		} finally {
			signatureController.getManagerBean().initializePOJO(to);
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
	
	public void onSign( ActionEvent event ) {
		if (! resolveCertificado() ) {
			return;
		}
		try {
			sign( signatureController.getTo() );
		} catch (Throwable e) {
			LOGGER.severe(">>>> onSign " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			setShowSignWindow(false);
		}
	}
	
	public void onCancelSign( ActionEvent event ) {
		try {
			cancelSign( signatureController.getTo() );
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onCancelSign " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}