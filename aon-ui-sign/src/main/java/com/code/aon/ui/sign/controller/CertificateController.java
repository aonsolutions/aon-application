package com.code.aon.ui.sign.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.facturae.FacturaeSigner;
import com.code.aon.facturae.KeyStoreData;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CertificateProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;

import net.esle.sinadura.core.certificado.Certificado;
import net.esle.sinadura.core.firma.DocumentFactory;
import net.esle.sinadura.core.firma.DocumentIFace;
import net.esle.sinadura.core.firma.SignStoreFactory;
import net.esle.sinadura.core.firma.SignStoreIFace;
import net.esle.sinadura.core.firma.exceptions.SinaduraCoreException;

public class CertificateController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;	
	
	private static final String IZENPE = "Izenpe";
	private static final String IZENPE_TSA_URL = "http://ocsp.izenpe.com:8093"; 

	private static final String ACCV = "ACCV";
	private static final String ACCV_TSA_URL = "http://tss.accv.es:8318/tsa";

	private static final String CATCERT = "Catcert";
	private static final String CATCERT_TSA_URL = "http://psis.catcert.net/psis/catcert/tsp";

	private static final String SIGN_IMAGE_PATH = "sign.png";
	
	private static final String SOFTWARE_TAB_ID = "software";
	
	private static final String SMART_CARD_TAB_ID = "smartCard";
	
	private CompanyController companyController;
	
	private Integer keystore;
	
	private String entity;
	
	private List<SelectItem> entities;
	
	private String password;

	private String certificatePassword;

	private String alias;
	
	private List<SelectItem> certificates;
	
	private List<SelectItem> digitalCertificates;
	
	private String mainPassword;
	
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
	
	public Integer getKeystore() {
		return keystore;
	}

	public void setKeystore(Integer keystore) {
		this.keystore = keystore;
	}

	public SignStoreIFace getSignStore() {
		Domain domain = getDomain();
		User user = getUser();
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(keystore), AttachType.REGISTRY);
		ByteArrayInputStream in = new ByteArrayInputStream( attach.getData() );
		try {
			return SignStoreFactory.buildSingStorePKSC12( in, password );
		} catch (SinaduraCoreException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public Certificado getCertificado() {
		return new Certificado(getAlias(), getMainPassword());
	}

	public void setCertificado(Certificado certificado) {
		setAlias(certificado.getAlias());
		setMainPassword(certificado.getPassword());		
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	private String getMainPassword() {
		return mainPassword;
	}

	private void setMainPassword(String mainPassword) {
		this.mainPassword = mainPassword;
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
	

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
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
		setAlias(null);
		setMainPassword(null);
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
	
//	private Criteria getCertificateCriteria( IManagerBean bean ) throws ManagerBeanException {
//		Criteria criteria = new Criteria();
//		Serializable id = companyController.obtainCompany().getId();
//		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), id);
//		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DIGITAL_CERTIFICATE);
//		String scopeAlias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_SCOPE_ID);
//		UserUtils.getInstance().addNullableScopeExpression(criteria, scopeAlias);
//		return criteria;
//	}
	
	public void loadDigitalCertificates() {
		this.digitalCertificates = new LinkedList<SelectItem>();
		Domain domain = getDomain();
		User user = getUser();
		AON.getCertificates(domain, user, f -> certificateFilter(domain, user, f)).forEach(certificate -> {
			SelectItem item = new SelectItem(certificate.getId(), certificate.getDescription());
			digitalCertificates.add(item);
		});
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
	
	public static Filter certificateFilter(Domain domain, User user, CertificateProperties f) {
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), user.getLogin());
		
		Filter filter;
		if(domain.getParentId() != null) {
			Integer[] domains = {domain.getId(), domain.getParentId()};
			filter = f.getDomainProperty().in(domains);
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

	public int getCertificateCount() throws ManagerBeanException {
//		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
//		Criteria criteria = getCertificateCriteria(rattachBean);
		return 1; //rattachBean.getCount(criteria);
	}	
	
	public List<SelectItem> getEntities(){
		SelectItem izenpe = new SelectItem(IZENPE_TSA_URL, IZENPE);
		SelectItem accv = new SelectItem(ACCV_TSA_URL, ACCV);
		SelectItem catcert = new SelectItem(CATCERT_TSA_URL, CATCERT);
		entities = new LinkedList<SelectItem>();
		entities.add(izenpe);
		entities.add(accv);
		entities.add(catcert);

		return entities;
	}
	
	public boolean isSignable() throws ManagerBeanException {
		return companyController.isSmartCard() || getCertificateCount()>0;
	}
	
	private KeyStoreData getKeyStoreData() throws AonCoreException {
		return new KeyStoreData(getSignStore().getKeySore(),
				getCertificado().getAlias(), getCertificado().getPassword().toCharArray());
	}
	
	public void signAttachment( IAttachment attachment ) throws AonCoreException {
		byte[] signedData = null;
		MimeType type = attachment.getMimeType();
		if ( type == MimeType.MIME_PDF ) {
			signedData = getSignedFileData( attachment.getData() );
			attachment.setMimeType(MimeType.MIME_SIGNED_PDF);
		} else if ( (type == MimeType.MIME_XML) || (type == MimeType.MIME_XSIG) ) {
			FacturaeSigner signer = new FacturaeSigner();
			signedData = signer.sign(getKeyStoreData(), attachment.getData());
			attachment.setMimeType(MimeType.MIME_SIGNED_FACTURAE);
		}
		attachment.setData( signedData );
	}
	
	private byte[] getSignedFileData( byte[] in ) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			DocumentIFace document = DocumentFactory.buildPDFDocument( in );
			document.setTSURL(getEntity());
			InputStream imageIS = SignerController.class.getResourceAsStream(SIGN_IMAGE_PATH);
			byte[] imageData = IOUtils.toByteArray( imageIS );
//			document.firmar( getSignStore(), getCertificado(), out, true, 
//					companyController.obtainCompany().getAlias(), companyController.getMainAddress().getCity(), imageData,
//					305, 745, 405, 795);
			document.firmar(getSignStore(), getCertificado(), out, companyController.obtainCompany().getAlias(), companyController.getMainAddress().getCity(), imageData);
		} catch ( IOException e ) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage(), e);
		} catch (SinaduraCoreException e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage(), e);
		}
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
			SignStoreIFace signStore = getSignStore();
			if ( certificates == null ) {
				List<Certificado> certificados = signStore.getCertificados();
				if ( certificados.size() == 1 ) {
					setCertificado(certificados.get(0));
					this.mainPassword = password;
					resolved = signStore.verifyAliasPassword(getCertificado());
					this.showCertificatePassword = resolved;
				} else {
					certificates = new LinkedList<SelectItem>();
					for( Certificado c : certificados ) {
						SelectItem item = new SelectItem(c.getAlias(), c.getAlias() );
						certificates.add(item);					
					}
				}				
			} else {
				setCertificado(signStore.getCertificado(getAlias()));
				if (! StringUtils.isEmpty(certificatePassword) ) {
					this.mainPassword = certificatePassword;
				} else {
					this.mainPassword = password;
				}
				resolved = signStore.verifyAliasPassword(getCertificado());
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