package com.code.aon.ui.company.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the company maintenance.
 */
public class CompanyController extends CompanyParentController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class.getName());
	
	private final int BACKGROUND_WIDTH = 535;
	
	private final int BACKGROUND_HEIGHT = 802;

	/** The uploaded logo file. */
	private AonFile logoFile;

	/** The uploaded signature file. */
	private AonFile signatureFile;
	
	private AonFile saleInvoiceBackgroundFile;
	private AonFile deliveryBackgroundFile;
	private AonFile salesBackgroundFile;
	private AonFile offerBackgroundFile;
	
	private String ediSupport;
	private String ediCompanyCode;
	
	

	public String getEdiSupport() {
		return ediSupport;
	}
	public void setEdiSupport(String ediSupport) {
		this.ediSupport = ediSupport;
	}
	
	public boolean isEdiSupportEnabled() {
		return StringUtils.isNotBlank(AppParamUtil
				.getValue(AppParam.EDI_SUPPORT));
	}
	
	public String getEdiCompanyCode() {
		return ediCompanyCode;
	}
	public void setEdiCompanyCode(String ediCompanyCode) {
		this.ediCompanyCode = ediCompanyCode;
	}
	
	/**
	 * Gets the uploaded logo file.
	 * 
	 * @return the file
	 */
	public AonFile getLogoFile() {
		return this.logoFile;
	}
	public AonFile getAonFile() {
		return this.logoFile;
	}

	/**
	 * Sets the logo file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setLogoFile(AonFile logoFile) {
		if ( this.logoFile != null ) {
			this.logoFile.clean();	
		}		
		this.logoFile = logoFile;
	}

	/**
	 * Gets the uploaded signature file.
	 * 
	 * @return the file
	 */
	public AonFile getSignatureFile() {
		return this.signatureFile;
	}
	
	/**
	 * Sets the signature file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setSignatureFile(AonFile signatureFile) {
		if ( this.signatureFile != null ) {
			this.signatureFile.clean();	
		}				
		this.signatureFile = signatureFile;
	}

	public AonFile getSaleInvoiceBackgroundFile() {
		return saleInvoiceBackgroundFile;
	}
	public void setSaleInvoiceBackgroundFile(AonFile saleInvoiceBackgroundFile) {
		if ( this.saleInvoiceBackgroundFile != null ) {
			this.saleInvoiceBackgroundFile.clean();	
		}
		this.saleInvoiceBackgroundFile = saleInvoiceBackgroundFile;
	}
	public AonFile getDeliveryBackgroundFile() {
		return deliveryBackgroundFile;
	}
	public void setDeliveryBackgroundFile(AonFile deliveryBackgroundFile) {
		if ( this.deliveryBackgroundFile != null ) {
			this.deliveryBackgroundFile.clean();	
		}
		this.deliveryBackgroundFile = deliveryBackgroundFile;
	}
	public AonFile getSalesBackgroundFile() {
		return salesBackgroundFile;
	}
	public void setSalesBackgroundFile(AonFile salesBackgroundFile) {
		if ( this.salesBackgroundFile != null ) {
			this.salesBackgroundFile.clean();	
		}
		this.salesBackgroundFile = salesBackgroundFile;
	}
	public AonFile getOfferBackgroundFile() {
		return offerBackgroundFile;
	}
	public void setOfferBackgroundFile(AonFile offerBackgroundFile) {
		if ( this.offerBackgroundFile != null ) {
			this.offerBackgroundFile.clean();	
		}
		this.offerBackgroundFile = offerBackgroundFile;
	}
	
	public void logoFileUploaded(UploadEvent event) {
		setLogoFile(AttachmentUtil.fileUploaded(event));
	}

	public void signatureFileUploaded(UploadEvent event) {
		setSignatureFile(AttachmentUtil.fileUploaded(event));
	}
	
	public void saleInvoiceBackgroundFileUploaded(UploadEvent event) {
		AonFile aonFile = AttachmentUtil.fileUploaded(event);
		adjustImage(aonFile);
		setSaleInvoiceBackgroundFile(aonFile);
	}
	public void deliveryBackgroundFileUploaded(UploadEvent event) {
		AonFile aonFile = AttachmentUtil.fileUploaded(event);
		adjustImage(aonFile);
		setDeliveryBackgroundFile(aonFile);
	}
	public void salesBackgroundFileUploaded(UploadEvent event) {
		AonFile aonFile = AttachmentUtil.fileUploaded(event);
		adjustImage(aonFile);
		setSalesBackgroundFile(aonFile);
	}
	public void offerBackgroundFileUploaded(UploadEvent event) {
		AonFile aonFile = AttachmentUtil.fileUploaded(event);
		adjustImage(aonFile);
		setOfferBackgroundFile(aonFile);
	}
	
	private void adjustImage(AonFile aonFile) {
		if ((aonFile != null) && aonFile.isDirty() ) {
			BufferedImage image = ImageUtil.getBufferedImage( aonFile.getData() );
			BufferedImage newImage = ImageUtil.scale(image, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(newImage, aonFile.getMimeType().getExtension(), baos);
				aonFile.setData(baos.toByteArray());
			} catch (IOException e) {
				LOGGER.error("Error when trying to adjust report background image", e);
			}
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoFile() != null && (getLogoFile().getSize() >0)) {
			out.write(getLogoFile().getData());
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoAttach() != null) {
			out.write(getLogoAttach().getData());
		}
	}
	
	public String getLogoMimeType() {
		if ( getLogoAttach()!= null && getLogoAttach().getMimeType() != null ) {
			return getLogoAttach().getMimeType().getName();
		}
		return "*";	
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentSignatureContent(OutputStream out, Object data) throws IOException {
		if (getSignatureFile() != null && (getSignatureFile().getSize() > 0) ) {
			out.write(getSignatureFile().getData());
		}
	}
	
	public void createSaleInvoiceBackgroundContent(OutputStream out, Object data) throws IOException {
		if (getSaleInvoiceBackgroundFile() != null && (getSaleInvoiceBackgroundFile().getSize() >0)) {
			out.write(getSaleInvoiceBackgroundFile().getData());
		}
	}
	public void createDeliveryBackgroundContent(OutputStream out, Object data) throws IOException {
		if (getDeliveryBackgroundFile() != null && (getDeliveryBackgroundFile().getSize() >0)) {
			out.write(getDeliveryBackgroundFile().getData());
		}
	}
	public void createSalesBackgroundContent(OutputStream out, Object data) throws IOException {
		if (getSalesBackgroundFile() != null && (getSalesBackgroundFile().getSize() >0)) {
			out.write(getSalesBackgroundFile().getData());
		}
	}
	public void createOfferBackgroundContent(OutputStream out, Object data) throws IOException {
		if (getOfferBackgroundFile() != null && (getOfferBackgroundFile().getSize() >0)) {
			out.write(getOfferBackgroundFile().getData());
		}
	}
	
	public String clearSaleInvoiceBackgroundUploadData() {
		cleanBackground(getSaleInvoiceBackgroundFile(), ICompanyConstants.SALE_INVOICE_REPORT_KEY);
		return null;
	}
	public String clearDeliveryBackgroundUploadData() {
		cleanBackground(getDeliveryBackgroundFile(), ICompanyConstants.DELIVERY_REPORT_KEY);
		return null;
	}
	public String clearSalesBackgroundUploadData() {
		cleanBackground(getSalesBackgroundFile(), ICompanyConstants.SALES_REPORT_KEY);
		return null;
	}
	public String clearOfferBackgroundUploadData() {
		cleanBackground(getOfferBackgroundFile(), ICompanyConstants.OFFER_REPORT_KEY);
		return null;
	}
	private void cleanBackground(AonFile aonFile, String reportKey){
		if ( aonFile != null ) {
			aonFile.clean();	
		}
		try {
			RegistryAttachment attach = obtainReportBackground(reportKey);
			if(attach.getDriveId()!=null){
				DriveUtils.getInstace().deleteBlobs(attach);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error when trying to delete background image from Drive", e);
		}
	}
	
	public RegistryAttachment obtainSaleInvoiceBackground()
			throws ManagerBeanException {
		return obtainReportBackground(ICompanyConstants.SALE_INVOICE_REPORT_KEY);
	}

	public RegistryAttachment obtainOfferBackground()
			throws ManagerBeanException {
		return obtainReportBackground(ICompanyConstants.OFFER_REPORT_KEY);
	}

	public RegistryAttachment obtainDeliveryBackground()
			throws ManagerBeanException {
		return obtainReportBackground(ICompanyConstants.DELIVERY_REPORT_KEY);
	}

	public RegistryAttachment obtainSalesBackground()
			throws ManagerBeanException {
		return obtainReportBackground(ICompanyConstants.SALES_REPORT_KEY);
	}

	public RegistryAttachment obtainReportBackground(String name) 
			throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((Company)this.getTo()).getId());
		alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(alias, RegistryAttachmentType.REPORT_BACKGROUND);
		alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION);
		criteria.addEqualExpression(alias, name);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}

	public static Enterprise addEnterprise(Company company, Scope scope) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
		Enterprise enterprise = new Enterprise();
		enterprise.setDomain(company.getDomain());
		enterprise.setRegistry(company);
		enterprise.setScope(scope);
		return (Enterprise) bean.insert(enterprise);
	}

	public static Scope obtainScope( Integer domain ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SCOPE_DOMAIN), domain);
		criteria.addOrder(bean.getFieldName(IEntityAlias.SCOPE_ID));
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (Scope) list.get(0);
		}
		return null;
	}
	
	public static void insertWorkPlace(RegistryAddress address, Enterprise enterprise) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		WorkPlace workPlace = new WorkPlace();
		workPlace.setDomain(enterprise.getDomain());
		workPlace.setEnterprise(enterprise);
		workPlace.setScope(enterprise.getScope());
		workPlace.setDescription(ICompanyConstants.PRINCIPAL);
		workPlace.setAddress(address);
		workPlace.setActive(true);
		bean.insert(workPlace);
	}	
	
}