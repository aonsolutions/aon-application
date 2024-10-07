package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.VATTaxRegime;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Invoice implements Serializable, HasAudit {
	
	public interface InvoiceTypeVisitor<T> {
		T visitPurchase(Invoice invoice);
		T visitSales(Invoice invoice);
		T visitExpenses(Invoice invoice);
		T visitUndeductible(Invoice invoice);
	}

	private static final long serialVersionUID = 8897444490096530091L;
	private static final double REG_IMPORT_MAX_VALUE  = 150.0;
	
	private boolean selected;
	
	private List<InvoiceError> messages;

	private Integer id;
	private Integer domain;
	private EnterpriseActivity activity;
	private InvestAsset investAsset;
	private Integer project;
	
	private InvoiceType type;
	private String series;
	private int number;
	private String referenceCode;
	
	private Date issueDate;
	private Date taxDate;
	
	private RectificationType rectificationType;
	private InvoiceMin rectificationInvoice;
	
	private SecurityLevel securityLevel;
	
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private RegistryAddress registryAddress;
	private Account registryAccount;
	
	private Scope scope;
	private InvoiceTransactionType transaction;
	private boolean recorded;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private boolean investment;
	private boolean service;
	private boolean signed;
	private boolean annulled;
	private double taxableBase;
	private double vatQuota;
	private double retentionQuota;
	private double total;
	private Seller seller;
	
	private String comments;
	private String remarks;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;


	private LinkedList<InvoiceDetail> details;
	private TaxBreakdown taxBreakdown;
	
	private List<Finance> finances;
	private InvoiceFiscal fiscal;
	private InvoiceInfo invoiceInfo;
	private Attach attach;
	
	private Integer rawdocId;

	public boolean isSelected() {
		return selected;
	}
	public Invoice setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

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
	
	public Optional<InvestAsset> getInvestAsset() {
		return Optional.ofNullable(investAsset);
	}
	public Invoice setInvestAsset(InvestAsset investAsset) {
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
	
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		this.type = type;
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
	
	public String getDocumentNumber() {
		return InvoiceUtil.getDocumentNumber(type, series, number);
	}
	
	public String getSeriesNumber() {
		return InvoiceUtil.getSeriesNumber(series, number);
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
	
	public Optional<RegistryAddress> getRegistryAddress() {
		return Optional.ofNullable(registryAddress);
	}
	public Invoice setRegistryAddress(RegistryAddress registryAddress) {
		this.registryAddress = registryAddress;
		return this;
	}

	public Optional<Account> getRegistryAccount() {
		return Optional.ofNullable(registryAccount);
	}
	public Invoice setRegistryAccount(Account registryAccount) {
		this.registryAccount = registryAccount;
		return this;
	}

	public Scope getScope() {
		return scope;
	}
	public Invoice setScope(Scope scope) {
		this.scope = scope;
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
	
	public Optional<Seller> getSeller() {
		return Optional.ofNullable(seller);
	}
	public Invoice setSeller(Seller seller) {
		this.seller = seller;
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

	public Stream<InvoiceDetail> getDeletedDetails() {
		return AonCollectionUtils.stream(details)
			.filter(InvoiceDetail::isDeleted);
	}
	public Stream<InvoiceDetail> getDetails() {
		return AonCollectionUtils.stream(details)
			.filter(InvoiceDetail::isNotDeleted);
	}
	public Invoice deleteDetails() {
		getDetails().forEach( d -> d.setDeleted(true));
		details = getDetails()
			.filter( d -> d.getId() != null)
			.collect(Collectors.toCollection(LinkedList::new));
		refreshTaxBreakdown();
		return this;
	}
	public Invoice addDetail(InvoiceDetail detail) {
		if (details == null) details = new LinkedList<>();
		details.add(detail);
		refreshTaxBreakdown();
		return calculate();
	}
	
	public Invoice calculate() {
		return InvoiceCalculator.calculate(this);
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
	
	public Optional<InvoiceFiscal> getFiscal() {
		return Optional.ofNullable(fiscal);
	}
	public InvoiceFiscal ensureFiscal() {
		if (this.fiscal == null) {
			setFiscal(new InvoiceFiscal());
		}
		return this.fiscal;
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
	
	public Optional<InvoiceInfo> getInvoiceInfo() {
		return Optional.ofNullable(invoiceInfo);
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
	public Invoice addMessage(InvoiceError message) {
	    getMessages().add(message);
	    return this;
	}
	public void clearMessages() {
		this.messages = new LinkedList<>();		
	}

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
		if (this.getTaxBreakdown().isPresent()) {
			return AonMathUtils.isLessThan(
				this.getTaxBreakdown()
					.map( tb -> tb.getVats())
					.map( vats -> vats.mapToDouble( InvoiceBreakdown::getBase ).sum())
					.orElse(Double.MAX_VALUE)
				,Invoice.REG_IMPORT_MAX_VALUE );
		}
		return AonMathUtils.isLessThan( 
			getDetails()
				.filter( d -> !d.isPrepayment() )
				.mapToDouble( InvoiceDetail::getTaxableBase )
				.sum() 
			, REG_IMPORT_MAX_VALUE );
	}
	
	// ----------- VAT REGIMES
	
	public boolean isVatImportation() {
		return getFiscal().map( f -> f.isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION)).isPresent();
	}
	public Invoice setVatImportation(boolean value) {
		ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, value);
		return this;
	}
	
	public boolean isSimplified() {
		return AonStringUtils.isBlank(getRegistryDocument()) 
			|| AonStringUtils.isBlank(getRegistryName())
			|| (AonStringUtils.isBlank(getRegistryAddress().map( a -> a.getZip()).orElse(null)));
	}
	public boolean isProforma() {
		return getNumber() <= 0;
	}
	
	// ---------------------------------------------------- [TaxBreakdown]
	public Optional<TaxBreakdown> getTaxBreakdown() {
		return Optional.ofNullable(taxBreakdown);
	}
	public Invoice refreshTaxBreakdown() {
		if (this.taxBreakdown == null) {
			this.taxBreakdown = new TaxBreakdown();
			this.taxBreakdown.refresh( this );	
		}
		return this;		
	}
	private TaxBreakdown ensureTaxBreakdown() {
		if (this.taxBreakdown == null) {
			this.taxBreakdown = new TaxBreakdown();
			this.taxBreakdown.refresh( this );	
		}
		return this.taxBreakdown;
	}
	
	public Stream<InvoiceBreakdown> getVats() {
		return this.getTaxBreakdown()
			.map(itb -> itb.getVats() )
			.orElse(Stream.empty());
	}
	public Optional<InvoiceWithholding> getWithholding() {
		return this.getTaxBreakdown().flatMap( itb -> itb.getInvoiceWithholding() );
	}
	
	public Invoice setWithholding(InvoiceWithholding iw) {
		return setWithholding(iw, false);
	}
	
	public Invoice setWithholding(InvoiceWithholding iw, boolean farmer) {
		if (iw == null) {
			setWithholding(false);
			setWithholdingFarmer(false);
			ensureTaxBreakdown().setInvoiceWithholding(this, null);
		} else {
			setWithholding(true);
			setWithholdingFarmer(farmer);
			ensureTaxBreakdown().setInvoiceWithholding(this, iw);
		}
		getDetails()
			.forEach(invDet -> invDet.enableWithholding(this));
		calculate();
		return this;
	}
}

