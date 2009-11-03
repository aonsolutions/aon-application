package com.code.aon.ui.sign.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
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
	
	private boolean storeCertificate;
	
	private boolean sessionCertificateStored;
	
	public CertificateController() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		this.company = companyController.obtainCompany();
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
	
	private Criteria getCertificateCriteria( IManagerBean bean ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
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
	
	public byte[] getSignedFileData( byte[] in, boolean visible ) throws SinaduraCoreException, IOException {
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
	
	public boolean resolveCertificado() {
		if ( sessionCertificateStored ) {
			return true;
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