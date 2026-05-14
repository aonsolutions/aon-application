package com.code.aon.ui.finance.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.Date;
import java.util.LinkedList;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.ProgressionState;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.Segment;
import com.code.aon.ui.common.LongProcessThread;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.FeeInvoicingProcess;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Properties.CertificateProperties;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;

public class FeeInvoicingController implements IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private InvoicingParameters invoicingParams;

	private Integer[] invoiceIds;
	
	private ProgressionState progressionState;
	
	private InvoiceCommunicationConfiguration icc;
	
	private boolean showCertFeeWindow;

	LinkedList<SelectItem> digitalCertificates;
	LinkedList<Certificate> certificates;
	Integer certificate;
	String password;
	
	public Integer getCertificate() {
		return certificate;
	}
	
	public void setCertificate(Integer certificate) {
		this.certificate = certificate;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public InvoicingParameters getParams() {
		return invoicingParams;
	}

	public void setParams(InvoicingParameters invoicingParams) {
		this.invoicingParams = invoicingParams;
	}
	
	private SaleInvoiceController getSaleInvoiceController() {
		return (SaleInvoiceController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME);
	}			
	
	public boolean isShowCertFeeWindow() {
		return showCertFeeWindow;
	}

	public void setShowCertFeeWindow(boolean showCertFeeWindow) {
		this.showCertFeeWindow = showCertFeeWindow;
	}
	
	public void onInitialize(ActionEvent event) throws ManagerBeanException {
		InvoicingParameters params = new InvoicingParameters();
		params.initializeParams();
		params.setScopes(UserUtils.getInstance().getCurrentUserScopes());
		SaleInvoiceController controller = getSaleInvoiceController();
		controller.onCancel(event);
		Series series = SeriesUtil.getSeries(controller.initSeries(false));
		params.setInvoiceSeries(series);
		params.setConfidential(isSeriesConfidential(series));
		params.setInvoiceNumber(0);
		params.setInvoiceDate(new Date());
		params.setInvoiceRecordable(AonUtil.getRoleManager().isAccountingOperator());
		setParams(params);
		setProgressionState(new ProgressionState());
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = (Series) event.getNewValue();
		if ( getSaleInvoiceController().isNumberEditable() ) {
			updateInvoiceNumber(series);
		}
		getParams().setConfidential(isSeriesConfidential(series));
	}

	public void onInvoiceNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateInvoiceNumber(getParams().getInvoiceSeries());		
	}			
	
	private void updateInvoiceNumber(Series series) throws ManagerBeanException {
		getParams().setInvoiceNumber(obtainMaxNumber(series));
	}
	
	private int obtainMaxNumber(Series series) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if ((series == null) ||StringUtils.isBlank(series.getCode()) ) {
			criteria.addNullExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), series.getCode());
		}
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean.getFieldName(IEntityAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}
	
	public void updateSeries() throws ManagerBeanException {
		Series series = getParams().getInvoiceSeries();
        if ( (series!=null) && StringUtils.isBlank(series.getCode()) ) {
        	series = null;
        	getParams().setInvoiceSeries(null);
        }
        if ( getParams().getInvoiceNumber() == 0 ) {
        	getParams().setInvoiceNumber(obtainMaxNumber(series));
        }        
	}	

	private boolean isSeriesConfidential(Series series) {
		if (series != null) {
			return (series.getSecurityLevel() == SecurityLevel.CONFIDENTIAL);
		}
		return false;
	}
	
	public Certificate getCert() {
		if(certificates == null) {
			getDigitalCertificates();
		}
		return certificates.stream().filter(f -> f.getId().equals(certificate)).findFirst().orElse(new Certificate());	
	}
	
	public boolean isPass() {	
		return getCert().hasPassword();
	}
	
	
	public LinkedList<SelectItem> getDigitalCertificates() {
		if(digitalCertificates == null) {
			digitalCertificates = new LinkedList<>();
			certificates = new LinkedList<>();
			Domain domain = getDomain();
			User user = getUser();
			AON.getCertificates(domain, user, f -> certificateFilter(domain, user, f)).forEach(certificate -> {
				SelectItem item = new SelectItem(certificate.getId(), certificate.getDescription());
				digitalCertificates.add(item);
				certificates.add(certificate);
			});
			
			if(!certificates.isEmpty())
				certificate = certificates.getFirst().getId();
		}
		return digitalCertificates;
	}	
	
	public Filter certificateFilter(Domain domain, User user, CertificateProperties f) {
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), user.getLogin());
		
		Filter filter;
		if(domain.getParentId() != null) {
			if(!user.getDomain().getId().equals(domain.getParentId())) {
				filter = (f.getDomainProperty().eq(domain.getId()).or(
						f.getDomainProperty().eq(domain.getParentId())
						.and(f.getSecurityLevelProperty().eq(com.esferalia.aon.occam.api.model.type.SecurityLevel.OFFICIAL.value())))
					);
			} else {
				Integer[] domains = {domain.getId(), domain.getParentId()};
				filter = f.getDomainProperty().in(domains);
			}
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
	
	public void onInvoice(ActionEvent event) throws Exception {
		Series series = getParams().getInvoiceSeries();
		checkSerie(getParams().getInvoiceDate(), series != null ? series.getCode() : null);
		getProgressionState().start();
		if(getInvoiceCommunicationConfiguration().isTbai()) {
			try {
				checkCertificate(getCertificate());
			} catch (Exception e) {
				getProgressionState().setProgressionErrorMessage(e.getMessage());
				getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
				throw(e);	
			}
		}

		FeeInvoicingProcess fip = new FeeInvoicingProcess(this, UserUtils.getInstance().getLoggedUser());
		LongProcessThread thread = new LongProcessThread(fip); 
		thread.start();				
	}

	private void checkSerie(Date date, String serie) throws Exception {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		
		com.esferalia.aon.occam.api.model.finance.Invoice lastInvoice = AON.getLastSaleInvoice(domainName, domainId, login, serie);
		if(lastInvoice.getIssueDate() != null && date.compareTo(lastInvoice.getIssueDate()) < 0
				&& !isTbai()) {
			getProgressionState().finish();
			AonUtil.addErrorMessage("Existe una factura con la misma serie y fecha posterior.");
			throw new Exception("Existe una factura con la misma serie y fecha posterior.");
		}
	}
	
	private static Certificate checkCertificate(Certificate certificate) throws Exception {
		try {
			if(!checkCert(certificate.getData(), certificate.getPassword())) {
				AonUtil.addErrorMessage("El certificado o la contraseña no son correctos.");
				throw new Exception("El certificado o la contraseña no son correctos.");
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage("El certificado o la contraseña no son correctos.");
			throw new Exception("El certificado o la contraseña no son correctos.");			
		}		
		if(certificate.isEmpty()) {
			AonUtil.addErrorMessage("El certificado no existe.");
			throw new Exception("El certificado no existe.");
		}
		return certificate;
		
	}
	
	private static Certificate checkCertificate(Integer certId) throws Exception {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Integer userId = UserUtils.getInstance().getLoggedUser().getId();
		Certificate cert = new Certificate();
		try {
			if(certId != null) {
				cert = AON.getCertificate(domainName, domainId, login, f -> f.getIdProperty().eq(certId));
			} else cert =  AON.getCertificate(domainName, domainId, login, userId, CertificateType.AEAT.name());
		} catch (Exception e) {
			throw new Exception("Error al obtener el certificado.");
		}
		return checkCertificate(cert);
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

	public String invoiceAction() {
		return (getInvoiceIds() != null) ? IFinanceConstants.SALE_INVOICE_LIST_NAME : null;
	}

	public void onShowPanel(ActionEvent event) throws Exception {
		try {
			if	(getInvoiceCommunicationConfiguration().isCertificateNeeded()) {
				checkCertificate(getCert());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AbortProcessingException(e.getMessage(),e);
		}
		getProgressionState().start(false);
	}

	private void loadInvoices( ActionEvent event ) {
		if ( getInvoiceIds() != null ) {
			try {
				IController controller = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
				Criteria criteria = new Criteria();
				criteria.addBetweenExpression(controller.getFieldName(IEntityAlias.INVOICE_ID), getInvoiceIds()[0], getInvoiceIds()[1]);
				controller.onEditSearch(event);
				controller.setCriteria(criteria);
				controller.onSearch(event);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(),e);
			}				
		}		
	}
	
	public void onClosePanel(ActionEvent event) {
		if ( getProgressionState().isFinish() ) {
			loadInvoices(event);
		}
		getProgressionState().finish();
		getParams().setInvoiceNumber(0);
	}

	public ProgressionState getProgressionState() {
		return progressionState;
	}

	public void setProgressionState(ProgressionState progressionState) {
		this.progressionState = progressionState;
	}

	public Integer[] getInvoiceIds() {
		return invoiceIds;
	}

	public void setInvoiceIds(Integer[] invoiceIds) {
		this.invoiceIds = invoiceIds;
	}	
	
	public boolean needCertificate() {
		return getInvoiceCommunicationConfiguration().isCertificateNeeded();
	}
	
	public boolean isLroe() 		{ return getInvoiceCommunicationConfiguration().isLroe(); }
	public boolean isTbai() 		{ return getInvoiceCommunicationConfiguration().isTbai(); }
	public boolean isVerifactu() 	{ return getInvoiceCommunicationConfiguration().isVerifactu(); }
	public boolean isNoVerifactu() 	{ return getInvoiceCommunicationConfiguration().isNoVerifactu(); }
	public boolean isSif() 			{ return getInvoiceCommunicationConfiguration().isSif(); }
	public boolean isNoSif() 		{ return getInvoiceCommunicationConfiguration().isNoSif(); }
	public boolean isSii() 			{ return getInvoiceCommunicationConfiguration().isSii(); }
	
	public boolean isAraba() {
		return getInvoiceCommunicationConfiguration().isAraba();
	}
	
	public boolean isBizkaia() {
		return getInvoiceCommunicationConfiguration().isBizkaia();
	}
	
	public boolean isGipuzkoa() {
		return getInvoiceCommunicationConfiguration().isGipuzkoa();
	}
	
	public InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration() {
		if(icc == null) {
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(login);
			icc = AON.getInvoiceCommunicationConfiguration(occam);
		}
		return icc;
	}
	
	public void setInvoiceCommunicationConfiguration(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
	}

	public void onAddSegment(ActionEvent event) {
		this.getParams().setSegments((Segment[]) ArrayUtils.add(this.getParams().getSegments(), new Segment()));	}
	
	public void onRemoveSegment(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.getParams().setSegments((Segment[]) ArrayUtils.remove(this.getParams().getSegments(), index));
		if ( ArrayUtils.isEmpty(this.getParams().getSegments()) ) {
			this.getParams().setSegments(new Segment[]{new Segment()});
		}
	}
	
}