package com.code.aon.facturae.v322;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.facturae.enumeration.PaymentMeans2;
import com.code.aon.facturae.enumeration.TaxTypeCode;
import com.code.aon.product.util.DiscountExpression;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.gob.facturae.formato.versiones.facturaev3_2_2.AccountType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.AddressType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.AdministrativeCentreType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.AdministrativeCentresType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.AmountType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.BatchType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.BusinessType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.ContactDetailsType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.CountryType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.CurrencyCodeType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.DeliveryNoteType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.DeliveryNotesReferencesType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.DiscountType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.DiscountsAndRebatesType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.Facturae;
import es.gob.facturae.formato.versiones.facturaev3_2_2.FileHeaderType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.IndividualType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InstallmentType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InstallmentsType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceClassType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceDocumentTypeType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceHeaderType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceIssueDataType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceIssuerTypeType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceLineType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceTotalsType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoiceType.TaxesOutputs;
import es.gob.facturae.formato.versiones.facturaev3_2_2.InvoicesType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.ItemsType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.LanguageCodeType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.LegalEntityType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.LegalLiteralsType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.ModalityType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.OverseasAddressType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.PartiesType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.PeriodDates;
import es.gob.facturae.formato.versiones.facturaev3_2_2.PersonTypeCodeType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.RegistrationDataType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.ResidenceTypeCodeType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.TaxIdentificationType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.TaxOutputType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.TaxType;
import es.gob.facturae.formato.versiones.facturaev3_2_2.TaxesType;

public class FacturaeWriter {
	
	private static final String RETENTION_TAX_TYPE_CODE = "04";

	private static final String SCHEMA_VERSION = "3.2.2";
	public static final String FACTURAE_EXTENSION = ".xsig";
	
	private static final String VAT_ACCRUAL_PAYMENT_TEXT = "R\u00E9gimen especial del criterio de caja";
	
	private Domain domain;
	private User user;
	private Invoice invoice;
	private CompanyFull company;
	private Person person;
	private Workplace workplace;
	private String legalLiterals;
	private BillingPeriod billingPeriod;
	
	public FacturaeWriter(Domain domain, User user, CompanyFull company, Person person, Workplace workplace, Invoice invoice) {
		this.domain = domain;
		this.user = user;
		this.company = company;
		this.person = person;
		this.workplace = workplace;
		this.invoice = invoice;
	}
	
	public FacturaeWriter(Domain domain, User user, CompanyFull company, Person person, Workplace workplace, Invoice invoice, String legalLiterals, BillingPeriod billingPeriod) {
		this.domain = domain;
		this.user = user;
		this.company = company;
		this.person = person;
		this.workplace = workplace;
		this.invoice = invoice;
		this.legalLiterals = legalLiterals;
		this.billingPeriod = billingPeriod;
	}
	
	// ----- GETTERS & SETTERS
	
	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public CompanyFull getCompany() {
		return company;
	}
	
	public void setCompany(CompanyFull company) {
		this.company = company;
	}

	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Workplace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Workplace workplace) {
		this.workplace = workplace;
	}
	
	// ----- GENERATE FACTURAE 

	public byte[] generate() {
		try {
			return marshal(getFacturae(), Facturae.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] marshal(Object object, Class<?> clazz) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(object, bos);
		return bos.toByteArray();
	}
	
	private Facturae getFacturae() {
		Facturae facturae = new Facturae();
    	facturae.setFileHeader(getFileHeader());
    	facturae.setParties(getParties());
    	facturae.setInvoices(getInvoices());
		return facturae;
	}
	
	// ----- FILE HEADER
	
	private FileHeaderType getFileHeader() {
		FileHeaderType fileHeader = new FileHeaderType();
		fileHeader.setSchemaVersion(SCHEMA_VERSION);
		fileHeader.setModality(ModalityType.I);
		fileHeader.setInvoiceIssuerType(InvoiceIssuerTypeType.EM);
		fileHeader.setBatch(getBatch());
		return fileHeader;
	}
	
	private BatchType getBatch() {
		AmountType amount = getAmountType();
		BatchType batchType = new BatchType();
		batchType.setBatchIdentifier( Util.getBatchIdentifier(getInvoice(), getCompany().getRegistry().getDocument()));
		batchType.setInvoicesCount(1);
		batchType.setTotalInvoicesAmount(amount);
		batchType.setTotalOutstandingAmount(amount);
		batchType.setTotalExecutableAmount(amount);
		batchType.setInvoiceCurrencyCode(CurrencyCodeType.EUR);
		return batchType;
	}
	
	private AmountType getAmountType() {
		AmountType amount = new AmountType();
		amount.setTotalAmount(getInvoice().getTotal());
		amount.setEquivalentInEuros(getInvoice().getTotal());
		return amount;
	}
	
	// ----- PARTIES
	
	private PartiesType getParties() {
		PartiesType parties = new PartiesType();
		BusinessType companyParty = getCompanyParty();
		BusinessType invoiceParty = getInvoiceParty();
		if(getInvoice().isSales()) {
			parties.setSellerParty(companyParty);			
			parties.setBuyerParty(invoiceParty);			
		} else {
			parties.setSellerParty(invoiceParty);			
			parties.setBuyerParty(companyParty);						
		}
		return parties;
	}
	
	private BusinessType getCompanyParty() {
		String name = getCompany().getRegistry().getName();
		String document = getCompany().getRegistry().getDocument();
		RegistryAddress address = getCompany().getMainAddress();
		String tradeName = StringUtils.defaultIfEmpty(getCompany().getRegistry().getAlias(), getWorkplace().getDescription());
		BusinessType party = getBusinessType(getCompany().getRegistry(), name, tradeName, document, address);
		AdministrativeCentresType centres = new AdministrativeCentresType();
		centres.getAdministrativeCentre().add(getAdministrativeCentre());
		party.setAdministrativeCentres(centres);
		return party;
	}
	
	private BusinessType getInvoiceParty() {
		Registry registry = invoice.getRegistryData();
		String name = StringUtils.defaultIfEmpty(invoice.getRegistryName(), registry.getName());
		String document = StringUtils.defaultIfEmpty(invoice.getRegistryDocument(), registry.getDocument());
		
		String tradeName = registry.getAlias();
		BusinessType registryParty = getBusinessType(registry, name, tradeName, document, invoice.getAddress());
		if ( FACeUtil.isDefined(domain, user, invoice) ) {
			if (registryParty.getAdministrativeCentres() == null) {
				registryParty.setAdministrativeCentres(new AdministrativeCentresType());			
			}
			getFACeAdministrativeCentres(registryParty.getAdministrativeCentres());
		}
		RegistryAddress address = invoice.getAddress();
		if (address != null && StringUtils.isNotBlank(address.getAlias())) {
			if (registryParty.getAdministrativeCentres() == null) {
				registryParty.setAdministrativeCentres(new AdministrativeCentresType());			
			}
			AdministrativeCentreType centre = new AdministrativeCentreType();
			centre.setCentreCode(Util.toTextMax10Type(address.getAlias()));
			centre.setRoleTypeCode(FACeUtil.FACE_VENDEDOR_ROLE_TYPE_CODE);
			CountryType country = getCountry(address.getCountry());
			if ( CountryType.ESP.equals(country) ) {
				centre.setAddressInSpain( getAddress(address, country) );	
			} else {
				centre.setOverseasAddress( getOverseasAddress(address, country) );
			}
			centre.setCentreDescription(FACeUtil.FACE_VENDEDOR_DESCRIPTION);
			registryParty.getAdministrativeCentres().getAdministrativeCentre().add(centre);
		}
		return registryParty;
	}
	
	private AdministrativeCentreType getAdministrativeCentre() {
		AdministrativeCentreType centre = new AdministrativeCentreType();
		centre.setCentreCode(Util.toTextMax10Type(String.valueOf(getWorkplace().getId())));
		RegistryAddress address = AON.getRegistryAddress(getDomain(), getUser(), f -> f.getIdProperty().eq(getWorkplace().getAddress() != null ? getWorkplace().getAddress().getId() : null));
		CountryType country = getCountry(address.getCountry());
		if(CountryType.ESP.equals(country) ) {
			centre.setAddressInSpain(getAddress(address, country));	
		} else {
			centre.setOverseasAddress(getOverseasAddress(address, country));
		}		
		return centre;
	}
	
	private void getFACeAdministrativeCentres(AdministrativeCentresType centres) {
		addAdministrativeCentre( centres,
				FACeUtil.FACE_FISCAL_CENTRE_CODE,
				FACeUtil.FACE_FISCAL_ROLE_TYPE_CODE,
				FACeUtil.FACE_FISCAL_ADDRESS,
				FACeUtil.FACE_FISCAL_DESCRIPTION);
		addAdministrativeCentre( centres,
				FACeUtil.FACE_RECEPTOR_CENTRE_CODE,
				FACeUtil.FACE_RECEPTOR_ROLE_TYPE_CODE,
				FACeUtil.FACE_RECEPTOR_ADDRESS,
				FACeUtil.FACE_RECEPTOR_DESCRIPTION);
		addAdministrativeCentre( centres,
				FACeUtil.FACE_PAGADOR_CENTRE_CODE,
				FACeUtil.FACE_PAGADOR_ROLE_TYPE_CODE,
				FACeUtil.FACE_PAGADOR_ADDRESS,
				FACeUtil.FACE_PAGADOR_DESCRIPTION);
		addAdministrativeCentre( centres,
				FACeUtil.FACE_COMPRADOR_CENTRE_CODE,
				FACeUtil.FACE_COMPRADOR_ROLE_TYPE_CODE,
				FACeUtil.FACE_COMPRADOR_ADDRESS,
				FACeUtil.FACE_COMPRADOR_DESCRIPTION);
	}
	
	private void addAdministrativeCentre( AdministrativeCentresType centres,
			String centreCodeKey, String roleTypeCode, String addressKey,
			String centreDescription ) {
		String centreCode = FACeUtil.getValue(domain, user, centreCodeKey, invoice);
		if (! StringUtils.isEmpty(centreCode) ) {
			AdministrativeCentreType centre = new AdministrativeCentreType();
			centre.setCentreCode(Util.toTextMax10Type(centreCode));
			centre.setRoleTypeCode( roleTypeCode );
			RegistryAddress address = invoice.getAddress();
			CountryType country = getCountry(address.getCountry());
			if ( CountryType.ESP.equals(country) ) {
				centre.setAddressInSpain( getAddress(address, country) );	
			} else {
				centre.setOverseasAddress( getOverseasAddress(address, country) );
			}
			centre.setCentreDescription(Util.toTextMax2500Type(centreDescription));
			centres.getAdministrativeCentre().add(centre);
		}
	}
	
	private AddressType getAddress(RegistryAddress address, CountryType country ) {
		AddressType addressType = new AddressType();
		addressType.setAddress( Util.toTextMax80Type(address.getFullAddress()) );
		addressType.setPostCode( Util.toPostCodeType(address.getZip()) );
		addressType.setTown( Util.toTextMax50Type(address.getCity()) );
		addressType.setProvince( getProvince(address) );	
		addressType.setCountryCode( country );
		return addressType;
	}
	
	private OverseasAddressType getOverseasAddress(RegistryAddress address, CountryType country ) {
		OverseasAddressType overseasAddress = new OverseasAddressType();
		overseasAddress.setAddress( Util.toTextMax80Type(address.getFullAddress()) );
		String pct = null;
		if (! StringUtils.isEmpty(address.getZip()) ) {
			pct = address.getZip() + " ";
		}
		if (! StringUtils.isEmpty(address.getCity()) ) {
			pct = StringUtils.defaultString(pct) + address.getCity(); 
		}
		overseasAddress.setPostCodeAndTown( Util.toTextMax50Type(StringUtils.trim(pct)) );
		overseasAddress.setProvince( getProvince(address) );
		overseasAddress.setCountryCode( country );
		return overseasAddress;
	}
	
	private String getProvince(RegistryAddress address) {
		return Util.toTextMax20Type(address.getProvince());
	}
	
	private BusinessType getBusinessType( Registry registry, String name, String tradeName, String document, RegistryAddress address ) {
		BusinessType party = new BusinessType();
		TaxIdentificationType taxIdentification = new TaxIdentificationType();
		PersonTypeCodeType personType = getPersonTypeCode(registry);
		taxIdentification.setPersonTypeCode( personType );
		taxIdentification.setResidenceTypeCode( getResidenceTypeCode(registry) );
		taxIdentification.setTaxIdentificationNumber( Util.toTextMax30Type(document) );
		party.setTaxIdentification(taxIdentification);
		party.setPartyIdentification( Util.toTextMax10Type(String.valueOf(registry.getId())) );
		if(personType == PersonTypeCodeType.F) {
			party.setIndividual( getIndividual(registry, name, address) );
		} else {
			party.setLegalEntity( getLegalEntity(registry, name, tradeName, address) );
		}
		return party;
	}
	
	private IndividualType getIndividual( Registry registry, String name, RegistryAddress address ) {
		IndividualType individualType = new IndividualType();
		if(getPerson() != null) {
			individualType.setName( Util.toTextMax40Type(getPerson().getFirstName()) );
			individualType.setFirstSurname( Util.toTextMax40Type(getPerson().getFirstSurname()) );			
			individualType.setSecondSurname( Util.toTextMax40Type(getPerson().getSecondSurname()) );
		} else {
			String[] nameSplited = getNameSplited(name);
			individualType.setName( Util.toTextMax40Type(nameSplited[0]) );
			individualType.setFirstSurname( Util.toTextMax40Type(nameSplited[1]) );			
		}
		if ( address != null ) {
			CountryType country = getCountry(address.getCountry());
			if ( CountryType.ESP.equals(country) ) {
				individualType.setAddressInSpain( getAddress(address, country) );	
			} else {
				individualType.setOverseasAddress( getOverseasAddress(address, country) );
			}			
		}
		individualType.setContactDetails( getContactDetails(registry) );
		return individualType;
	}
	
	private ContactDetailsType getContactDetails( Registry registry ) {
		ContactDetailsType contactDetails = new ContactDetailsType();
		RegistryMedia phone = getRegistryMedia(registry, MediaType.FIXED_PHONE);
		if ( phone != null ) {
			contactDetails.setTelephone(Util.toTextMax15Type(phone.getValue()) );
		}
		RegistryMedia fax = getRegistryMedia(registry, MediaType.FAX);
		if ( fax != null ) {
			contactDetails.setTeleFax(Util.toTextMax15Type(fax.getValue()) );
		}
		RegistryMedia email = getRegistryMedia(registry, MediaType.EMAIL);
		if ( email != null ) {
			contactDetails.setElectronicMail(Util.toTextMax60Type(email.getValue()) );
		}
		RegistryMedia web = getRegistryMedia(registry, MediaType.WEB);
		if ( web != null ) {
			contactDetails.setWebAddress(Util.toTextMax60Type(web.getValue()) );
		}
		return contactDetails;
	}
	
	private RegistryMedia getRegistryMedia(Registry registry, MediaType type) {
		RegistryMedia media= AON.getRegistryMedia(domain, user, f -> f.getRegistryProperty().eq(registry.getId())
				.and(f.getMediaProperty().eq(type.value()))
				.and(f.getAdministrativeProperty().eq((byte) 1)));
		if(media == null || media.isEmpty()) {
			media= AON.getRegistryMedia(domain, user, f -> f.getRegistryProperty().eq(registry.getId())
					.and(f.getMediaProperty().eq(type.value())));
		}
		return media;
	}	
	
	
	private String[] getNameSplited( String name ) {
		String[] splits = new String[2];
		String _name = StringUtils.trimToNull(name);
		int index = StringUtils.lastIndexOf(_name, " ");
		if ( index != -1 ) {
			splits[0] = StringUtils.substring(_name, 0, index);
			splits[1] = StringUtils.substring(_name, index+1);
		} else {
			splits[0] = StringUtils.defaultIfEmpty(_name, "...");
			splits[1] = "...";			
		}
		return splits;
	}
	
	private LegalEntityType getLegalEntity( Registry registry, String name, String tradeName, RegistryAddress address ) {
		LegalEntityType legalEntityType = new LegalEntityType();
		legalEntityType.setCorporateName(Util.toTextMax80Type(name));
		legalEntityType.setTradeName(Util.toTextMax40Type(tradeName));
		RecordData recordData = getRecordData(registry);
		if ( recordData != null ) {
			legalEntityType.setRegistrationData( getRegistrationData(recordData) );
		}
		if ( address != null ) {
			CountryType country = getCountry(address.getCountry());
			if ( CountryType.ESP.equals(country) ) {
				legalEntityType.setAddressInSpain( getAddress(address, country) );	
			} else {
				legalEntityType.setOverseasAddress( getOverseasAddress(address, country) );
			}			
		}
		legalEntityType.setContactDetails( getContactDetails(registry) );
		return legalEntityType;
	}
	
	private RegistrationDataType getRegistrationData( RecordData recordData ) {
		RegistrationDataType registrationData = new RegistrationDataType();
		registrationData.setRegisterOfCompaniesLocation( Util.toTextMax20Type(recordData.getDescription()) );
		registrationData.setSheet( Util.toTextMax20Type(recordData.getSheet()) );
		registrationData.setFolio( Util.toTextMax20Type(recordData.getPage()) );
		registrationData.setSection( Util.toTextMax20Type(recordData.getSection()) );
		registrationData.setVolume( Util.toTextMax20Type(recordData.getVolume()) );
		registrationData.setAdditionalRegistrationData( Util.toTextMax20Type(recordData.getNotary()) );
		return registrationData;
	}
	
	private RecordData getRecordData(Registry registry) {
		return AON.getRecordData(domain.getName(), domain.getId(), user.getLogin(), f -> f.getRegistryProperty().eq(registry.getId()));
	}
	
	private PersonTypeCodeType getPersonTypeCode( Registry registry ) {
		return registry.isLegalPerson() ? PersonTypeCodeType.J : PersonTypeCodeType.F;
	}
	
	private ResidenceTypeCodeType getResidenceTypeCode( Registry registry ) {
		if(getCompany().getRegistry().getId().equals(registry.getId())) return ResidenceTypeCodeType.R;
		if(getInvoice().isIntracommunity()) return ResidenceTypeCodeType.U;
		if(getInvoice().isExtracommunity()) return ResidenceTypeCodeType.E;
		return  ResidenceTypeCodeType.R;
	}

	private CountryType getCountry(String code) {
		if(code == null) return null;
		return CountryType.fromValue(code);
	}
	
	private CountryType getCountry(Country country) {
		return (country != null) ? getCountry(country.getIso3()) : CountryType.ESP;
	}
	
	// ----- INVOICE
	
	private InvoicesType getInvoices() {
		InvoicesType invoices = new InvoicesType();
		invoices.getInvoice().add( getInvoiceType() );
		return invoices;
	}
	
	private InvoiceType getInvoiceType() {
		InvoiceType invoiceType = new InvoiceType();
		invoiceType.setInvoiceHeader( getInvoiceHeader() );
		invoiceType.setInvoiceIssueData( getInvoiceIssueData() );
		addTaxes(invoiceType);
		invoiceType.setInvoiceTotals( getInvoiceTotals() );
		if ( this.invoice.isVatAccrualPayment() ) {
			invoiceType.setLegalLiterals( getLegalLiterals() );			
		} else if(!AonStringUtils.isBlank(legalLiterals)) {
			LegalLiteralsType llt = new LegalLiteralsType();
			llt.getLegalReference().add(legalLiterals);
			invoiceType.setLegalLiterals(llt);
		}
		invoiceType.setItems( getItems(invoiceType) );
		addPaymentDetails( invoiceType );
		return invoiceType;
	}
	
	private void addPaymentDetails( InvoiceType invoiceType ) {
		InstallmentsType installments = new InstallmentsType();
		for( Finance finance : invoice.getFinances() ) {
			installments.getInstallment().add( getInstallment(finance) );
		}
		if (! installments.getInstallment().isEmpty() ) {
			invoiceType.setPaymentDetails( installments );
		}
	}
	
	private InstallmentType getInstallment( Finance finance ) {
		InstallmentType installment = new InstallmentType();
		installment.setInstallmentDueDate( Util.toXMLCalendar(finance.getDueDate()) );
		installment.setInstallmentAmount( finance.getAmount() );
		PaymentMeans2 paymentMeans = null;
		paymentMeans = getPaymentMeans( finance.getPayMethodType() );
		installment.setPaymentMeans( paymentMeans.getValue() );			
		if ( finance.getBankAccount() != null ) {
			AccountType account = new AccountType();
			account.setIBAN( finance.getBankAccount().getIban() );
			if ( paymentMeans != PaymentMeans2.TRANSFERENCIA ) {
				installment.setAccountToBeDebited(account);
			} else {
				installment.setAccountToBeCredited(account);
			}
		}
		return installment;
	}
	

	private PaymentMeans2 getPaymentMeans(PayMethodType payMethodType ) {
		PaymentMeans2 paymentMeans = null;
		if ( payMethodType != null ) {
			paymentMeans = PaymentMeans2.getPaymentMeans(payMethodType);	
		}
		return (paymentMeans != null) ? paymentMeans : PaymentMeans2.AL_CONTADO;
	}
	
	private InvoiceHeaderType getInvoiceHeader() {
		InvoiceHeaderType invoiceHeader = new InvoiceHeaderType();
		invoiceHeader.setInvoiceNumber(Util.toTextMax20Type(String.valueOf(getInvoice().getNumber())) );
		invoiceHeader.setInvoiceSeriesCode(Util.toTextMax20Type(String.valueOf(getInvoice().getSeries())));
		// Factura Completa
		invoiceHeader.setInvoiceDocumentType( InvoiceDocumentTypeType.FC );
		// Original
		invoiceHeader.setInvoiceClass( InvoiceClassType.OO );
		return invoiceHeader;
	}	
	
	private InvoiceIssueDataType getInvoiceIssueData() {
		InvoiceIssueDataType invoiceIssueData = new InvoiceIssueDataType();
		XMLGregorianCalendar issuedDate = Util.toXMLCalendar(getInvoice().getIssueDate());
		invoiceIssueData.setIssueDate( issuedDate );
		invoiceIssueData.setOperationDate(issuedDate);
		PeriodDates period = new PeriodDates();
		period.setStartDate(issuedDate);
		period.setEndDate(Util.toXMLCalendar(getEndDate(getInvoice().getIssueDate(), billingPeriod)));
		invoiceIssueData.setInvoicingPeriod(period);
		invoiceIssueData.setInvoiceCurrencyCode(CurrencyCodeType.EUR);
		invoiceIssueData.setTaxCurrencyCode(CurrencyCodeType.EUR);
		invoiceIssueData.setLanguageName(LanguageCodeType.ES);

		String filereference = null;
		Integer i = 0;
		while(AonStringUtils.isBlank(filereference) && i < invoice.getDetails().size()) {
			filereference = getIssuerContractReference(invoice.getDetails().get(i), true);
			i++;	
		}
		if(!AonStringUtils.isBlank(filereference)) {
			invoiceIssueData.setFileReference(filereference);
		}
		return invoiceIssueData;
	}
	
	private Date getEndDate(Date date, BillingPeriod period) {
		if(period == null || BillingPeriod.NO_PERIOD == period) return date;
		else if(BillingPeriod.MONTHLY == period ) return AonDateUtils.addMonths(date, 1);
		else if(BillingPeriod.BI_MONTHLY == period ) return AonDateUtils.addMonths(date, 2);
		else if(BillingPeriod.THREE_MONTHLY == period ) return AonDateUtils.addMonths(date, 3);
		else if(BillingPeriod.FOUR_MONTHLY == period ) return AonDateUtils.addMonths(date, 4);
		else if(BillingPeriod.SIX_MONTHLY == period ) return AonDateUtils.addMonths(date, 6);
		else if(BillingPeriod.YEARLY == period ) return AonDateUtils.addYears(date, 1);		
		else return date;
	}
	
	private void addTaxes(InvoiceType invoiceType) {
		TaxesOutputs taxesOutputs = new TaxesOutputs();
		TaxesType taxesWithHeld = new TaxesType();
		invoice.getBreakdown().stream().forEach(tbd -> {
			if (tbd.getTaxType().equals(com.esferalia.aon.occam.api.model.type.TaxType.RETENTION)) {
				taxesWithHeld.getTax().add( getTax(tbd, false) );
			} else {
				taxesOutputs.getTax().add( getTaxOutput(tbd) );
			}
		});
		
		if ( invoice.getBreakdown().isEmpty() ) {
			taxesOutputs.getTax().add( getEmptyTax() );
		}		
		if (! taxesOutputs.getTax().isEmpty() ) {
			invoiceType.setTaxesOutputs( taxesOutputs );
		}
		if (! taxesWithHeld.getTax().isEmpty() ) {
			invoiceType.setTaxesWithheld( taxesWithHeld );
		}
	}
	
	private double getTaxQuota( InvoiceBreakdown tbd ) {
		return CommonUtil.round(tbd.getBase() * tbd.getPercentage()/100, 2);
	}
	
	private double getTaxQuota( InvoiceTax tbd ) {
		return CommonUtil.round(tbd.getBase() * tbd.getPercentage()/100, 2);
	}
	
	private TaxType getTax( InvoiceBreakdown tbd, boolean lineTax ) {
		TaxType tax = new TaxType();
		tax.setTaxTypeCode(RETENTION_TAX_TYPE_CODE);
		tax.setTaxRate( tbd.getPercentage() );
		tax.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && tbd.getQuota() == 0.0 ) {
			double quota = getTaxQuota(tbd);
			tax.setTaxAmount( Util.getAmount(quota) );
		} else {
			tax.setTaxAmount( Util.getAmount(tbd.getQuota()) );	
		}
		return tax;
	}
	
	private TaxType getTax( InvoiceTax tbd, boolean lineTax ) {
		TaxType tax = new TaxType();
		tax.setTaxTypeCode(RETENTION_TAX_TYPE_CODE);
		tax.setTaxRate( tbd.getPercentage() );
		tax.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && tbd.getQuota() == 0.0 ) {
			double quota = getTaxQuota(tbd);
			tax.setTaxAmount( Util.getAmount(quota) );
		} else {
			tax.setTaxAmount( Util.getAmount(tbd.getQuota()) );	
		}
		return tax;
	}
	
	private InvoiceLineType.TaxesOutputs.Tax getLineTax(InvoiceTax tdb ) {
		InvoiceLineType.TaxesOutputs.Tax tax = new InvoiceLineType.TaxesOutputs.Tax();
		initTaxOutput( tax, tdb, true );
		return tax;
	}
	
	private InvoiceLineType.TaxesOutputs.Tax getEmptyLineTax() {
		InvoiceLineType.TaxesOutputs.Tax tax = new InvoiceLineType.TaxesOutputs.Tax();
		tax.setTaxTypeCode( TaxTypeCode.IVA.getValue() );
		tax.setTaxRate( 0.0 );
		tax.setTaxableBase( Util.getAmount(0) );
		tax.setTaxAmount( Util.getAmount(0) );
		return tax;
	}

	private TaxOutputType getEmptyTax() {
		TaxOutputType taxOutput = new TaxOutputType();
		taxOutput.setTaxTypeCode( TaxTypeCode.IVA.getValue() );
		taxOutput.setTaxRate( 0.0 );
		taxOutput.setTaxableBase( Util.getAmount(0) );
		taxOutput.setTaxAmount( Util.getAmount(0) );		
		return taxOutput;
	}
	
	private TaxOutputType getTaxOutput( InvoiceBreakdown tdb ) {
		TaxOutputType taxOutput = new TaxOutputType();
		initTaxOutput(taxOutput, tdb, false);
		return taxOutput;
	}

	private void initTaxOutput( TaxOutputType taxOutput, InvoiceBreakdown tbd, boolean lineTax ) {
		taxOutput.setTaxTypeCode(TaxTypeCode.IVA.getValue());
		taxOutput.setTaxRate( tbd.getPercentage() );
		taxOutput.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && tbd.getQuota()==0.0 ) {
			double quota = getTaxQuota(tbd);
			taxOutput.setTaxAmount( Util.getAmount(quota) );
		} else {
			taxOutput.setTaxAmount( Util.getAmount(tbd.getQuota()) );	
		}
		if ( tbd.getSurcharge() != 0 ) {
			taxOutput.setEquivalenceSurcharge( tbd.getSurcharge() );
		}
		if ( tbd.getSurchargeQuota() != 0 ) {
			taxOutput.setEquivalenceSurchargeAmount( Util.getAmount(tbd.getSurchargeQuota()) );
		}
	}
	
	private void initTaxOutput( TaxOutputType taxOutput, InvoiceTax tbd, boolean lineTax ) {
		taxOutput.setTaxTypeCode(TaxTypeCode.IVA.getValue());
		taxOutput.setTaxRate( tbd.getPercentage() );
		taxOutput.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && tbd.getQuota()==0.0 ) {
			double quota = getTaxQuota(tbd);
			taxOutput.setTaxAmount( Util.getAmount(quota) );
		} else {
			taxOutput.setTaxAmount( Util.getAmount(tbd.getQuota()) );	
		}
		if ( tbd.getSurcharge() != 0 ) {
			taxOutput.setEquivalenceSurcharge( tbd.getSurcharge() );
		}
		if ( tbd.getSurchargeQuota() != 0 ) {
			taxOutput.setEquivalenceSurchargeAmount( Util.getAmount(tbd.getSurchargeQuota()) );
		}
	}
	
	private InvoiceTotalsType getInvoiceTotals() {
		InvoiceTotalsType invoiceTotals = new InvoiceTotalsType();
		double taxableBase = getInvoice().getBreakdown().stream().filter(f -> 
			f.getTaxType().equals(com.esferalia.aon.occam.api.model.type.TaxType.VAT))
			.mapToDouble(InvoiceBreakdown::getBase).sum();
		taxableBase = AonMathUtils.round(taxableBase);
		invoiceTotals.setTotalGrossAmount(taxableBase);
		invoiceTotals.setTotalGrossAmountBeforeTaxes( taxableBase );
		double totalTaxOutputs = getInvoice().getBreakdown().stream().filter(f -> 
			f.getTaxType().equals(com.esferalia.aon.occam.api.model.type.TaxType.VAT))
			.mapToDouble(r -> r.getQuota() + r.getSurchargeQuota()).sum();

		invoiceTotals.setTotalTaxOutputs( totalTaxOutputs );
		double totalTaxesWithheld = getInvoice().getBreakdown().stream().filter(f -> 
			f.getTaxType().equals(com.esferalia.aon.occam.api.model.type.TaxType.RETENTION))
			.mapToDouble(r -> r.getQuota()).sum();
		invoiceTotals.setTotalTaxesWithheld( totalTaxesWithheld );

		invoiceTotals.setInvoiceTotal(getInvoice().getTotal());
		invoiceTotals.setTotalOutstandingAmount(getInvoice().getTotal());
		invoiceTotals.setTotalExecutableAmount(getInvoice().getTotal());
		return invoiceTotals;
	}
	
	private LegalLiteralsType getLegalLiterals() {
		LegalLiteralsType legalLiterals = new LegalLiteralsType();
		legalLiterals.getLegalReference().add( VAT_ACCRUAL_PAYMENT_TEXT );
		return legalLiterals;
	}	
	
	private ItemsType getItems( InvoiceType invoiceType ) {
		ItemsType items = new ItemsType();
		for( InvoiceDetail line : getInvoice().getDetails() ) {
			items.getInvoiceLine().add( getInvoiceLine(line, invoiceType) );
		}		
		return items;
	}
	
	private InvoiceLineType getInvoiceLine(InvoiceDetail line, InvoiceType invoiceType) {
		InvoiceLineType invoiceLine = new InvoiceLineType();
		// invoiceLine.setIssuerTransactionReference(Util.toTextMax20Type(String.valueOf(line.getId())));
		if (line.getProject() != null) {
			Project project = AON.getProject(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
					f.getIdProperty().eq(line.getProject()));
			invoiceLine.setReceiverContractReference(project.getName());
		}
		invoiceLine.setItemDescription(Util.toTextMax2500Type(line.getDescription()) );
		invoiceLine.setQuantity( line.getQuantity() );
		invoiceLine.setUnitPriceWithoutTax( getLineAmount(line.getPrice()) );
		double totalCost = getLineAmount( line.getQuantity() * line.getPrice() );
		invoiceLine.setTotalCost( totalCost );
		invoiceLine.setGrossAmount( getLineAmount(line.getTaxableBase()) );
		if ( line.getDiscountExpression() != null ) {
			invoiceLine.setDiscountsAndRebates( getDiscountsAndRebates(line, totalCost) );
		}
		String issuerContractReferenceOption = FACeUtil.getValue(getDomain(), getUser(), FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE, invoice);
		if (! StringUtils.isEmpty(issuerContractReferenceOption)) {
			boolean purchaseReference = Boolean.parseBoolean(issuerContractReferenceOption);
			String issuerContractReference = getIssuerContractReference(line, purchaseReference);
			if (! StringUtils.isEmpty(issuerContractReference) && purchaseReference) {
				invoiceLine.setReceiverTransactionReference(Util.toTextMax20Type(issuerContractReference));
			} else if(!StringUtils.isEmpty(issuerContractReference)) {
				invoiceLine.setIssuerContractReference(Util.toTextMax20Type(issuerContractReference));
			}		
		}
		String sequenceNumber = FACeUtil.getValue(getDomain(), getUser(), FACeUtil.FACE_INVOICE_SEQUENCE_NUMBER, invoice);
		if (! StringUtils.isEmpty(sequenceNumber) && NumberUtils.isNumber(sequenceNumber) ) {
			invoiceLine.setSequenceNumber(Double.parseDouble(sequenceNumber));
		}
		String deliveryNoteNumberOption = FACeUtil.getValue(getDomain(), getUser(), FACeUtil.FACE_INVOICE_DELIVERY_NUMBER, invoice);
		if (Boolean.parseBoolean(deliveryNoteNumberOption)) {
			String referenceCode = null;
			Date date = null;
			if(InvoiceSource.DELIVERY.equals(line.getSource())) {
				DeliveryDetail dd = AON.getDeliveryDetail(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f 
						-> f.getIdProperty().eq(line.getSourceId()));
				referenceCode = dd.getDelivery().getReferenceCode();
				date = dd.getDelivery().getDate();
			} else if(InvoiceSource.INCOME.equals(line.getSource())) {
				IncomeDetail dd = AON.getIncomeDetail(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f 
						-> f.getIdProperty().eq(line.getSourceId())).get();
				referenceCode = dd.getIncome().getReferenceCode();
				date = dd.getIncome().getIssueDate();
			}
			if(referenceCode != null && date != null) {
				DeliveryNotesReferencesType notes = new DeliveryNotesReferencesType();
				DeliveryNoteType noteType = new DeliveryNoteType();
				noteType.setDeliveryNoteNumber(referenceCode);
				try {
					GregorianCalendar deliveryNoteDate = new GregorianCalendar();
					deliveryNoteDate.setTime(date);
					noteType.setDeliveryNoteDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(deliveryNoteDate));
				} catch (DatatypeConfigurationException ex) {
				}
				notes.getDeliveryNote().add(noteType);
				invoiceLine.setDeliveryNotesReferences(notes);
			}
		}
		addLinesTaxes( invoiceType, invoiceLine, line );
		return invoiceLine;
	}
	
	private double getLineAmount(double amount) {
		return CommonUtil.round(amount, 2);
	}
	
	private DiscountsAndRebatesType getDiscountsAndRebates( InvoiceDetail line, double totalCost ) {
		DiscountsAndRebatesType dar = new DiscountsAndRebatesType();
		DiscountExpression dis = new DiscountExpression(line.getDiscountExpression().getDiscountExpr());
		double[] discounts = dis.getDiscounts();
		for( int i = 0;i<discounts.length;i++ ) {
			DiscountType discount = new DiscountType();
			discount.setDiscountReason( "Dto " + (i+1) );
			discount.setDiscountRate( discounts[i] );
			double amount = totalCost * ( discounts[i] / 100 );
			discount.setDiscountAmount( amount );
			dar.getDiscount().add( discount );
		}
		return dar;
	}
	
	private String getIssuerContractReference( InvoiceDetail detail, boolean purchaseReference ) {
		if ( detail.getSource() == InvoiceSource.DELIVERY ) {
			DeliveryDetail dd = AON.getDeliveryDetail(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f 
					-> f.getIdProperty().eq(detail.getSourceId()));
			if ( (dd != null) && (dd.getSalesDetail() != null) ) {
				SalesDetail sd = AON.getSalesDetailStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f
						-> f.getIdProperty().eq(dd.getSalesDetail())).findFirst().orElse(new SalesDetail());
				if (purchaseReference) {
					return sd.getSales().getPurchaseReference();
				} else return sd.getSales().getReferenceCode();
			}
		} else if ( detail.getSource() == InvoiceSource.INCOME ) {
			IncomeDetail id = AON.getIncomeDetail(getDomain().getName(), getDomain().getId(),
					getUser().getLogin(), f -> f.getIdProperty().eq(detail.getSourceId())).get();
			if ( (id != null) && (id.getPurchaseDetail() != null) ) {
				PurchaseDetail pd = AON.getPurchaseDetail(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
						f -> f.getIdProperty().eq(id.getPurchaseDetail()));
				if (purchaseReference)
					return pd.getPurchase().getPurchaseReference();
				else return pd.getPurchase().getReferenceCode();
			}
		} else if ( detail.getSource() == InvoiceSource.SALES ) {
			SalesDetail sd = AON.getSalesDetailStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f
					-> f.getIdProperty().eq(detail.getSourceId())).findFirst().orElse(new SalesDetail());
			if (purchaseReference)
				return sd.getSales().getPurchaseReference();
			else
				return sd.getSales().getReferenceCode();		
		} else if ( detail.getSource() == InvoiceSource.PURCHASE ) {
			PurchaseDetail pd = AON.getPurchaseDetail(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
				f -> f.getIdProperty().eq(detail.getSourceId()));
			if (purchaseReference)
				return pd.getPurchase().getPurchaseReference();
			else
				return pd.getPurchase().getReferenceCode();		
		}
		return null;
	}
	
	private void addLinesTaxes( InvoiceType invoiceType, InvoiceLineType invoiceLine, InvoiceDetail line ) {
		InvoiceLineType.TaxesOutputs taxesOutputs = new InvoiceLineType.TaxesOutputs();
		TaxesType taxesWithHeld = new TaxesType();
		line.getInvoiceTaxes().stream().forEach(r ->{
			if(com.esferalia.aon.occam.api.model.type.TaxType.RETENTION.equals(r.getTaxType())) {
				taxesWithHeld.getTax().add( getTax(r, true) );
			} else taxesOutputs.getTax().add( getLineTax(r) );
		});
	
		if ( line.getInvoiceTaxes().isEmpty() ) {
			taxesOutputs.getTax().add( getEmptyLineTax() );
		}
		if (! taxesOutputs.getTax().isEmpty() ) {
			invoiceLine.setTaxesOutputs( taxesOutputs );
		}
		if (! taxesWithHeld.getTax().isEmpty() ) {
			invoiceLine.setTaxesWithheld( taxesWithHeld );
		}
	}
}
