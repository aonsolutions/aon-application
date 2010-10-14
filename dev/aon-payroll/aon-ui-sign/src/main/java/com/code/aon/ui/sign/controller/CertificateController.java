package com.code.aon.ui.sign.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import net.esle.sinadura.core.certificado.Certificado;
import net.esle.sinadura.core.firma.DocumentFactory;
import net.esle.sinadura.core.firma.DocumentIFace;
import net.esle.sinadura.core.firma.SignStoreFactory;
import net.esle.sinadura.core.firma.SignStoreIFace;
import net.esle.sinadura.core.firma.exceptions.SinaduraCoreException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.event.ScopeFilterListener;
import com.code.aon.ui.util.AonUtil;

public class CertificateController {

	private static final String DEFAULT_TSA_URL = "http://ocsp.izenpe.com:8093"; 

	private static final String SIGN_IMAGE_PATH = "sign.png";
	
	private static final String SOFTWARE_TAB_ID = "software";
	
	private static final String SMART_CARD_TAB_ID = "smartCard";
	
	private CompanyController companyController;
	
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
	
	private boolean storeCertificate;
	
	private boolean sessionCertificateStored;
	
	private String selectedTab;
	
	private String pdf64Data;
	
	private String pdf64SignedData;	
	
	private IAttachment attachment;
	
	private SignerController signerController;
	
	public CertificateController() {
		this.companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
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
	
	public boolean isStoreCertificate() {
		return storeCertificate;
	}

	public void setStoreCertificate(boolean storeCertificate) {
		this.storeCertificate = storeCertificate;
	}
	
	public boolean isSessionCertificateStored() {
		return sessionCertificateStored;
	}

	public void setSessionCertificateStored(boolean sessionCertificateStored) {
		this.sessionCertificateStored = sessionCertificateStored;
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getPdf64Data() {
		if ( getAttachment() == null ) {
			ISignatureController signatureController = getSignerController().getSignatureController();
			ITransferObject to = signatureController.getTo();
			setAttachment( signatureController.generateReportAttachment(to) );
			if (! ArrayUtils.isEmpty(getAttachment().getData()) ) {
				byte[] base64Data = Base64.encodeBase64(getAttachment().getData());
				this.pdf64Data = new String( base64Data );	
			}			
		}
		return pdf64Data;
	}

	public void setPdf64Data(String pdf64Data) {
		this.pdf64Data = pdf64Data;
	}

	public String getPdf64SignedData() {
		return pdf64SignedData;
	}

	public void setPdf64SignedData(String pdf64SignedData) {
		this.pdf64SignedData = pdf64SignedData;
	}
	
	public IAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(IAttachment attachment) {
		this.attachment = attachment;
	}

	public SignerController getSignerController() {
		return signerController;
	}

	public void setSignerController(SignerController signerController) {
		this.signerController = signerController;
	}
	
	public boolean isUsingSmartCard() {
		return StringUtils.equals(this.selectedTab, SMART_CARD_TAB_ID);
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
		if ( getDigitalCertificates().isEmpty() ) {
			setSelectedTab(SMART_CARD_TAB_ID);
		} else {
			setSelectedTab(SOFTWARE_TAB_ID);
		}
		setAttachment(null);
		setPdf64Data(null);
		setPdf64SignedData(null);
	}

	public List<SelectItem> getDigitalCertificates() {
		return digitalCertificates;
	}
	
	private Criteria getCertificateCriteria( IManagerBean bean ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		Serializable id = companyController.obtainCompany().getId();
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), id);
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DIGITAL_CERTIFICATE);
		String scopeAlias = bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_SCOPE_ID);
		criteria.addExpression( ScopeFilterListener.getExpression(scopeAlias) );
		return criteria;
	}
	
	public void loadDigitalCertificates() throws ManagerBeanException {
		this.digitalCertificates = new LinkedList<SelectItem>();
		
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = getCertificateCriteria(rattachBean);
		Iterator<ITransferObject> iterator = rattachBean.getList(criteria).iterator();
		while ( iterator.hasNext() ) {
			RegistryAttachment ra = (RegistryAttachment) iterator.next();
			SelectItem item = new SelectItem(ra, ra.getDescription());
			digitalCertificates.add(item);
		} 
	}	

	public int getCertificateCount() throws ManagerBeanException {
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = getCertificateCriteria(rattachBean);
		return rattachBean.getCount(criteria);
	}	
	
	public boolean isSignable() throws ManagerBeanException {
		return companyController.isSmartCard() || (getCertificateCount() > 0);
	}
	
	public byte[] getSignedFileData( byte[] in, boolean visible ) throws SinaduraCoreException, IOException {
		DocumentIFace document = DocumentFactory.buildPDFDocument( in );
		document.setTSURL( DEFAULT_TSA_URL );
		InputStream imageIS = SignerController.class.getResourceAsStream(SIGN_IMAGE_PATH);
		byte[] imageData = IOUtils.toByteArray( imageIS );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.firmar( signStore, certificado, out, visible, 
				companyController.obtainCompany().getAlias(), companyController.getMainAddress().getCity(), imageData,
				305, 745, 405, 795 );		
		return out.toByteArray();		
	}	
	
	public boolean resolveCertificado() {
		if ( sessionCertificateStored ) {
			return true;
		}
		if ( isUsingSmartCard() ) {
			if (! StringUtils.isEmpty(this.pdf64SignedData) ) {
				byte[] data = Base64.decodeBase64(this.pdf64SignedData.getBytes());
				attachment.setData( data );
				attachment.setMimeType( MimeType.MIME_SIGNED_PDF );
				return true;
			}			
			return false;
		}
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
		if ( resolved && storeCertificate ) {
			sessionCertificateStored = true;
		}
		return resolved;
	}	

}