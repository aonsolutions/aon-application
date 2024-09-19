package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Invoice implements Serializable, HasAudit {
	
	private static final long serialVersionUID = 8897444490096530091L;
	private static final double REG_IMPORT_MAX_VALUE  = 150.0;
	
	private boolean selected;
	
	private List<InvoiceError> messages;

	private Integer id;
	private Integer domain;
	private EnterpriseActivity activity;
	private Integer investAsset;
	private Integer project;
	private String series;
	private int number;
	private String referenceCode;
	private Date issueDate;
	private Date taxDate;
	
	private RectificationType rectificationType;
	private InvoiceMin rectificationInvoice;
	
	private SecurityLevel securityLevel;
	
	private Integer registryAddress;
	private RegistryAddress address;
	
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Account registryAccount;
	
	private Scope scope;
	private InvoiceType type;
	private InvoiceTransactionType transaction;
	private boolean recorded;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private boolean investment;
	private boolean service;
	private boolean advance;
	private boolean signed;
	private boolean annulled;
	private double taxableBase;
	private double vatQuota;
	private double retentionQuota;
	private double total;
	private Integer posShift;
	private Integer seller;
	private String sellerName;
	
	private String comments;
	private String remarks;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;


	private List<InvoiceDetail> details;
	private TaxBreakdown taxBreakdown;
	private List<Finance> finances;
	private InvoiceFiscal fiscal;
	private InvoiceInfo invoiceInfo;
	private Attach attach;
	
	private Integer rawdocId;

	// ***************************
	// ATRIBUTOS CON DUDOSO FUTURO
	// ***************************
	private Registry registryData;
	private boolean recordable;
	private String fileUrl;
	private String tediCategory;
	private String siiStatus;
	// ***************************

	public Integer getId() {
		return id;
	}
	public Invoice setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Invoice setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Optional<EnterpriseActivity> getActivity() {
		return Optional.ofNullable(activity);
	}
	public Invoice setActivity(EnterpriseActivity activity) {
		this.activity = activity;
		return this;
	}
	
	public String getEpigraph() {
		return getActivity().map(a -> a.getEpigraph()).orElse(null);
	}
	public String getFullEpigraph() {
		return getActivity().map(a -> a.getFullEpigraph()).orElse(null);
	}
	@Deprecated
	public Invoice setEpigraph(String epigraph) {
		getActivity().ifPresent(a -> a.setEpigraph(epigraph));
		return this;
	}
	
	public Integer getInvestAsset() {
		return investAsset;
	}
	public Invoice setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public Invoice setProject(Integer project) {
		this.project = project;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Invoice setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Invoice setNumber(int number) {
		this.number = number;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public Invoice setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Invoice setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public Invoice setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public Invoice setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
		return this;
	}
	public boolean isRectified() {
		return (getRectificationType() == RectificationType.RECTIFIED);
	}
	public boolean isRectifier() {
		return (getRectificationType() == RectificationType.NORMAL_RECTIFIER 
			|| getRectificationType() == RectificationType.SPECIAL_RECTIFIER);
	}
	public void setNormalRectifier( boolean normalRectifier) {
		setRectificationType(normalRectifier?RectificationType.NORMAL_RECTIFIER:null);
	}
	
	public Integer getRectificationInvoiceId() {
		return getRectificationInvoice().map( InvoiceMin::getId ).orElse(null);	
	}
	public Optional<InvoiceMin> getRectificationInvoice() {
		return Optional.ofNullable( rectificationInvoice );
	}
	public Invoice setRectificationInvoice(InvoiceMin rectificationInvoice) {
		this.rectificationInvoice = rectificationInvoice;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Invoice setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Invoice setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Invoice setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Invoice setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Invoice setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}

	public RegistryAddress getAddress() {
		if(address == null) {
			address = new RegistryAddress();
		}
		return address;
	}
	public Invoice setAddress(RegistryAddress address) {
		this.address = address;
		return this;
	}

	public Integer getRegistryAddress() {
		return registryAddress;
	}
	
	public Invoice setRegistryAddress(Integer registryAddress) {
		this.registryAddress = registryAddress;
		return this;
	}
	
	
	
	public Scope getScope() {
		return scope;
	}
	public Invoice setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		this.type = type;
		return this;
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public Invoice setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	public boolean isRecorded() {
		return recorded;
	}
	public Invoice setRecorded(boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public Invoice setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public boolean isWithholding() {
		return withholding;
	}
	public Invoice setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public Invoice setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Invoice setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	public boolean isInvestment() {
		return investment;
	}
	public Invoice setInvestment(boolean investment) {
		this.investment = investment;
		return this;
	}
	public boolean isService() {
		return service;
	}
	public Invoice setService(boolean service) {
		this.service = service;
		return this;
	}
	public boolean isAdvance() {
		return advance;
	}
	public Invoice setAdvance(boolean advance) {
		this.advance = advance;
		return this;
	}
	public boolean isSigned() {
		return signed;
	}
	public Invoice setSigned(boolean signed) {
		this.signed = signed;
		return this;
	}
	
	public boolean isAnnulled() {
		return annulled;
	}
	
	public Invoice setAnnulled(boolean annulled) {
		this.annulled = annulled;
		return this;
	}
	
	public double getTaxableBase() {
		return taxableBase;
	}
	public Invoice setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	public double getVatQuota() {
		return vatQuota;
	}
	public Invoice setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
		return this;
	}
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public Invoice setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public Invoice setTotal(double total) {
		this.total = total;
		return this;
	}
	public Integer getSeller() {
		return seller;
	}
	public Invoice setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	public String getSellerName() {
		return sellerName;
	}
	public Invoice setSellerName(String sellerName) {
		this.sellerName = sellerName;
		return this;
	}
	public Integer getPosShift() {
		return posShift;
	}
	public Invoice setPosShift(Integer posShift) {
		this.posShift = posShift;
		return this;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Invoice setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Invoice setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	public String getDocumentNumber() {
		return FinanceUtil.getDocumentNumber(type, series, number);
	}
	
	public String getSeriesNumber() {
		return FinanceUtil.getSeriesNumber(series, number);
	}
	public String getComments() {
		return comments;
	}
	public Invoice setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}
	public Invoice setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Invoice setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Invoice setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Invoice setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Invoice setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public List<InvoiceDetail> getDetails() {
		if(details == null) {
			details = new LinkedList<>();
		}
		return details;
	}
	public Invoice setDetails(List<InvoiceDetail> details) {
		this.details = details;
		return this;
	}
	public Invoice addDetail(InvoiceDetail detail) {
		getDetails().add(detail);
		return this;
	}
	public Optional<InvoiceDetail> getFirstDetail() {
		return getDetails().stream().findFirst();
	}
	

	/**
	 * @deprecated This method will be removed use getBreakdowns(), getVats() or getWithHolding()
	 */
	@Deprecated
	public List<InvoiceBreakdown> getBreakdown() {
		return getBreakdowns();
	}
	/**
	 * @deprecated This method will be removed use setTaxBreakdown()
	 */
	@Deprecated
	public Invoice setBreakdown(List<InvoiceBreakdown> breakdown) {
		this.taxBreakdown = new TaxBreakdown();
		AonCollectionUtils.stream(breakdown)
			.forEach(ib -> this.taxBreakdown.add(ib));
		return this;
	}

	public List<Finance> getFinances() {
		return finances;
	}
	public Invoice setFinances(List<Finance> finances) {
		this.finances = finances;
		return this;
	}
	
	public Invoice addFinance(Finance finance) {
		if (getFinances() == null) {
			setFinances(new LinkedList<>());
		}
		getFinances().add(finance);
		return this;
	}
	public boolean hasFinances() {
		return getFinances() != null && !getFinances().isEmpty(); 
	}
	
	public InvoiceFiscal getFiscal() {
		return fiscal;
	}
	public InvoiceFiscal ensureFiscal() {
		if (getFiscal() == null) {
			setFiscal(new InvoiceFiscal());
		}
		return getFiscal();
	}
	public Invoice setFiscal(InvoiceFiscal fiscal) {
		this.fiscal = fiscal;
		return this;
	}
	
	public Optional<Attach> getAttach() {
		return Optional.ofNullable( attach );
	}
	public Invoice setAttach(Attach attach) {
		this.attach = attach;
		return this;
	}

	public Account getRegistryAccount() {
		if(registryAccount == null) {
			registryAccount = new Account();
		}
		return registryAccount;
	}
	public Invoice setRegistryAccount(Account registryAccount) {
		this.registryAccount = registryAccount;
		return this;
	}
	
	// ***************************
	// ATRIBUTOS CON DUDOSO FUTURO
	// ***************************
	public Registry getRegistryData() {
		return registryData;
	}
	public Invoice setRegistryData(Registry registryData) {
		this.registryData = registryData;
		return this;
	}
	public boolean isRecordable() {
		return recordable;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public Invoice setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
		return this;
	}
	public Invoice setRecordable(boolean recordable) {
		this.recordable = recordable;
		return this;
	}
	public String getTediCategory() {
		return tediCategory;
	}
	public Invoice setTediCategory(String category) {
		this.tediCategory = category;
		return this;
	}
	// ***************************
	// ***************************
	
	// ---------------------------------------------------------- UTIL
	public boolean isNational() {
		return getTransaction() == InvoiceTransactionType.NATIONAL;
	}
	public boolean isNotNational() {
		return isIntracommunity() || isExtracommunity() || isCanCeuMel();
	}
	public boolean isIntracommunity() {
		return getTransaction() == InvoiceTransactionType.INTRACOMMUNITY;
	}
	public boolean isExtracommunity() {
		return getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY;
	}
	public boolean isDUAAllowed() {
		return isNational() 
			&& !isUndeductible() 
			&& (isPurchase() || isExpenses());
	}
	public boolean isDUALinkAllowed() {
		return !isUndeductible() 
			&& (isPurchase() || isExpenses()) 
			&& (isExtracommunity() || isCanCeuMel()) ;
	}
	public boolean isCanCeuMel() {
		return getTransaction() == InvoiceTransactionType.CAN_CEU_MEL;
	}
	public boolean isIsp() {
		return getTransaction() == InvoiceTransactionType.OTHER_ISP;
	}
	public boolean isSales() {
		return getType() == InvoiceType.SALES;
	}
	public boolean isNotSales() {
		return !isSales();
	}
	public boolean isPurchase() {
		return getType() == InvoiceType.PURCHASE;
	}
	public boolean isExpenses() {
		return getType() == InvoiceType.EXPENSES;
	}
	public boolean isUndeductible() {
		return getType() == InvoiceType.UNDEDUCTIBLE;
	}
	
	public boolean mustApplyISP() {
		return (isPurchase() && isIntracommunity())					// Compra intracomunitaria
			|| (isPurchase() && isIsp())							// Compra Inversion Sujeto Pasivo
			|| (isPurchase() && isExtracommunity() && isService())	// Compra extracomunitaria de servicio
			|| (isPurchase() && isCanCeuMel() && isService())		// Compra Canarias de servicio
			|| (isExpenses() && isIntracommunity())					// Gasto intracomunitario
			|| (isExpenses() && isIsp())							// Gasto Inversion Sujeto Pasivo
			|| (isExpenses() && isExtracommunity())					// Gasto extracomunitario
			|| (isExpenses() && isCanCeuMel());						// Gasto Canarias
	}
	
	public boolean isOutputVatEnabled() {
		return !isUndeductible() && (
			(isSales() && isNational())		// Venta Nacional
			|| mustApplyISP());					// Aplicar la inversión de sujeto pasivo.	
	}
	public boolean isVatImportationAvailable() {
		return (isExtracommunity() || isCanCeuMel()) 
				&& (isPurchase() || isExpenses()) 	 
				&& !isService()
			;
	}
	
	@Deprecated
	public boolean _isInputVatEnabled() {
		return !isUndeductible() && (
			  (isPurchase() && isNational())						// Compra nacional 
			|| (isExpenses() && isNational())						// Gasto nacional
			|| (isVatImportationAvailable() && isVatImportation()	// Regimen importacioon
				&& isVatImportationAmountValid())
//				&& AonMathUtils.isLessThan(getTotal(), REG_IMPORT_MAX_VALUE0 ))	
			|| mustApplyISP());										// Aplicar la inversión de sujeto pasivo.	
	}
	
	
	public boolean isVatEnabled() {
		return (isInputVatEnabled() != isOutputVatEnabled());
	}
	
	public boolean isInputVatEnabled() {
		return !isUndeductible() 
			&& ((isPurchase() && isNational())						// Compra nacional 
			|| (isExpenses() && isNational())						// Gasto nacional
			|| (isVatImportationAvailable() && isVatImportation()	// Regimen importacioon
				&& isVatImportationAmountValid())
			|| mustApplyISP());										// Aplicar la inversión de sujeto pasivo.	
	}
	
	public boolean isVatImportationAmountValid() {
		if (this.getTaxBreakdown().isPresent() 
			&& AonCollectionUtils.isNotEmpty( this.getTaxBreakdown().get().getVats())) {
			 return AonMathUtils.isLessThan( 
				  AonCollectionUtils.stream(getVats()).mapToDouble( InvoiceBreakdown::getBase ).sum() 
				 ,Invoice.REG_IMPORT_MAX_VALUE );
		}
		return getDetails() == null 
			|| getDetails().isEmpty()
			|| AonMathUtils.isLessThan( getDetails()
					.stream()
					.filter( d -> !d.isPrepayment() )
					.mapToDouble( InvoiceDetail::getTaxableBase )
					.sum() 
				, REG_IMPORT_MAX_VALUE );
	}
	
	public String getSiiStatus() {
		if(siiStatus == null) siiStatus = "Pendiente";
		return siiStatus;
	}
	public Invoice setSiiStatus(String siiStatus) {
		this.siiStatus = siiStatus;
		return this;
	}
	
	
	// ----------- VAT REGIMES
	
	public boolean isVatImportation() {
		return getFiscal() != null && getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION);
	}
	public Invoice setVatImportation(boolean value) {
		ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, value);
		return this;
	}
	
	public boolean isSimplified() {
		return AonStringUtils.isBlank(getRegistryDocument()) || AonStringUtils.isBlank(getRegistryName())
				|| (AonStringUtils.isBlank(getAddress().getZip()) && getRegistryAddress() == null);
	}
	
	public boolean isProforma() {
		return getNumber() <= 0;
	}
	
	public InvoiceInfo getInvoiceInfo() {
		if(invoiceInfo == null) {
			invoiceInfo = new InvoiceInfo();
		}
		return invoiceInfo;
	}

	public Invoice setInvoiceInfo(InvoiceInfo invoiceInfo) {
		this.invoiceInfo = invoiceInfo;
		return this;
	}
	
	public List<InvoiceError> getMessages() {
	    if ( messages == null ) {
	    	messages = new LinkedList<>();
	    }
	    return messages;
	}
	public boolean hasMessages() {
		return AonCollectionUtils.isNotEmpty(messages);
	}
	public Invoice setMessages(LinkedList<InvoiceError> messages) {
	    this.messages = messages;
	    return this;
	}
	public Invoice addMessage(InvoiceError message) {
	    getMessages().add(message);
	    return this;
	}
	public void clearMessages() {
		this.messages = new LinkedList<>();		
	}
	
	
	public boolean isSelected() {
		return selected;
	}
	public Invoice setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public Optional<TaxBreakdown> getTaxBreakdown() {
		return Optional.ofNullable(taxBreakdown);
	}
	public Invoice setTaxBreakdown(TaxBreakdown taxBreakdown) {
		this.taxBreakdown = taxBreakdown;
		return this;
	}
	public Invoice calculateTaxBreakdown() {
		ensureTaxBreakdown().calculate();
		return this;
	}
	private TaxBreakdown ensureTaxBreakdown() {
		if (this.taxBreakdown == null) {
			setTaxBreakdown( new TaxBreakdown());
		}
		return this.taxBreakdown;
	}
	public Invoice addTax(InvoiceTax it) {
		ensureTaxBreakdown().add(it);
		return this;
	}
	public Invoice addBreakdown(InvoiceBreakdown ib) {
		ensureTaxBreakdown().add(ib);
		return this;
	}
	
	public List<InvoiceBreakdown> getBreakdowns() {
		return this.getTaxBreakdown().map(itb -> itb.getBreakdown() ).orElse(Collections.emptyList());
	}
	public List<InvoiceBreakdown> getVats() {
		return this.getTaxBreakdown().map(itb -> itb.getVats() ).orElse(Collections.emptyList());
	}
	public Optional<InvoiceWithholding> getWithholding() {
		return this.getTaxBreakdown().flatMap( itb -> itb.getInvoiceWithholding() );
	}
	public InvoiceWithholding ensureWithholdingData() {
		return getWithholding()
			.orElseGet( () -> {
				addBreakdown(new InvoiceBreakdown()
						.setTaxType(TaxType.RETENTION)
						.setWithholdingType(WithholdingType.PROFESSIONAL)
					)
					.setWithholding(true);
					AonCollectionUtils.stream(getDetails())
						.forEach(d -> d.ensureWithholdingTax( Invoice.this ));
				return getWithholding().orElse(null); 
			});
	}
	
	public Integer getRawdocId() {
		return rawdocId;
	}
	public Invoice setRawdocId(Integer rawdocId) {
		this.rawdocId = rawdocId;
		return this;
	}
	public boolean isFromRawdoc() {
		return this.rawdocId != null;
	}
	
}

