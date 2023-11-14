package com.code.aon.facturae;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.facturae.enumeration.PaymentMeans;
import com.code.aon.facturae.enumeration.TaxTypeCode;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.purchase.Purchase;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.sales.Sales;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

import es.mityc.facturae.FacturaeVersion;
import es.mityc.facturae.utils.MarshallerUtil;
import es.mityc.facturae32.AccountType;
import es.mityc.facturae32.AddressType;
import es.mityc.facturae32.AdministrativeCentreType;
import es.mityc.facturae32.AdministrativeCentresType;
import es.mityc.facturae32.AmountType;
import es.mityc.facturae32.BatchType;
import es.mityc.facturae32.BusinessType;
import es.mityc.facturae32.ContactDetailsType;
import es.mityc.facturae32.CountryType;
import es.mityc.facturae32.CurrencyCodeType;
import es.mityc.facturae32.DeliveryNoteType;
import es.mityc.facturae32.DeliveryNotesReferencesType;
import es.mityc.facturae32.DiscountType;
import es.mityc.facturae32.DiscountsAndRebatesType;
import es.mityc.facturae32.Facturae;
import es.mityc.facturae32.FileHeaderType;
import es.mityc.facturae32.IndividualType;
import es.mityc.facturae32.InstallmentType;
import es.mityc.facturae32.InstallmentsType;
import es.mityc.facturae32.InvoiceClassType;
import es.mityc.facturae32.InvoiceDocumentTypeType;
import es.mityc.facturae32.InvoiceHeaderType;
import es.mityc.facturae32.InvoiceIssueDataType;
import es.mityc.facturae32.InvoiceIssuerTypeType;
import es.mityc.facturae32.InvoiceLineType;
import es.mityc.facturae32.InvoiceTotalsType;
import es.mityc.facturae32.InvoiceType;
import es.mityc.facturae32.InvoiceType.TaxesOutputs;
import es.mityc.facturae32.InvoicesType;
import es.mityc.facturae32.ItemsType;
import es.mityc.facturae32.LanguageCodeType;
import es.mityc.facturae32.LegalEntityType;
import es.mityc.facturae32.LegalLiteralsType;
import es.mityc.facturae32.ModalityType;
import es.mityc.facturae32.OverseasAddressType;
import es.mityc.facturae32.PartiesType;
import es.mityc.facturae32.PeriodDates;
import es.mityc.facturae32.PersonTypeCodeType;
import es.mityc.facturae32.RegistrationDataType;
import es.mityc.facturae32.ResidenceTypeCodeType;
import es.mityc.facturae32.TaxIdentificationType;
import es.mityc.facturae32.TaxOutputType;
import es.mityc.facturae32.TaxType;
import es.mityc.facturae32.TaxesType;

public class FacturaeWriter {
	
	private static final String RETENTION_TAX_TYPE_CODE = "04";

	private static final Logger LOGGER = LoggerFactory.getLogger(FacturaeWriter.class.getName());
	
	public static final String FACTURAE_EXTENSION = ".xsig";
	
	private static final String VAT_ACCRUAL_PAYMENT_TEXT = "R\u00E9gimen especial del criterio de caja";
	
	private Invoice invoice;
	
	private WorkPlace workPlace;
	
	private Enterprise enterprise;
	
	private InvoicePriceStrategy priceStrategy; 
	
	private AmountType totalPrice;
	
	private List<TaxBreakDown> taxBreakDowns;
	
	private Locale locale;
	
	private int numberOfDecimals;
	
	private int registryDecimals;
	
	public FacturaeWriter( Locale locale ) {
		this.locale = locale;
	}

	private BatchType getBatchType() {
		BatchType batchType = new BatchType();
		batchType.setBatchIdentifier( Util.getBatchIdentifier(invoice, enterprise) );
		batchType.setInvoicesCount(1);
		batchType.setTotalInvoicesAmount( totalPrice );
		batchType.setTotalOutstandingAmount( totalPrice );
		batchType.setTotalExecutableAmount( totalPrice );
		batchType.setInvoiceCurrencyCode(CurrencyCodeType.EUR);
		return batchType;
	}
	
	private PersonTypeCodeType getPersonTypeCode( Registry registry ) {
		if ( registry.getType() == RegistryType.NATURAL ) {
			return PersonTypeCodeType.F;
		}
		return PersonTypeCodeType.J;
	}

	private ResidenceTypeCodeType getResidenceTypeCode( Registry registry ) {
		ResidenceTypeCodeType result = ResidenceTypeCodeType.R;
		if (! registry.getId().equals(enterprise.getId()) ) {
			if ( invoice.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY) {
				result = ResidenceTypeCodeType.U;
			} else if ( invoice.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY) {
				result = ResidenceTypeCodeType.E;
			}
		}
		return result;
	}
	
	private String getProvince( IAddress address ) {
		String province = null;
		if (! StringUtils.isEmpty(address.getProvince()) ) {
			province = address.getProvince();	
		} else if ( address.getGeozone() != null ) {
			province = address.getGeozone().getName();
		}
		return Util.toTextMax20Type(province);
	}
	
	private AddressType getAddress( IAddress address, CountryType country ) {
		AddressType addressType = new AddressType();
		addressType.setAddress( Util.toTextMax80Type(address.getFullAddress()) );
		addressType.setPostCode( Util.toPostCodeType(address.getZip()) );
		addressType.setTown( Util.toTextMax50Type(address.getCity()) );
		addressType.setProvince( getProvince(address) );	
		addressType.setCountryCode( country );
		return addressType;
	}
	
	private OverseasAddressType getOverseasAddress( IAddress address, CountryType country ) {
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
	
	private CountryType getCountryType( String code ) {
		for( CountryType country : CountryType.values() ) {
			if ( StringUtils.equalsIgnoreCase(code, country.value()) ) {
				return country;
			}
		}
		return null;
	}
	
	private CountryType getCountry( GeoZone gz ) throws ManagerBeanException {
		CountryType country = null;
		if ( gz != null ) {
			GeoZone geozone = gz.getGeoZoneCountry();
			if ( geozone != null ) {
				String name = geozone.getName();
				if (! StringUtils.isEmpty(geozone.getCode()) ) {
					country = getCountryType(geozone.getCode());
				}
				if ( country == null ) {
					for( Locale aLocale : Locale.getAvailableLocales() ) {
						if ( StringUtils.equalsIgnoreCase(name, aLocale.getDisplayCountry(this.locale)) ) {
							country = getCountryType(aLocale.getISO3Country());
							break;
						}
					}				
				}
			}			
		}
		return (country != null) ? country : CountryType.ESP;
	}
	
	private RegistryMedia getRegistryMedia(Registry registry, MediaType type) throws ManagerBeanException{
		RegistryMedia result = null;
		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
		List<ITransferObject> list = rMediaBean.getList(criteria);
		if(! list.isEmpty()) {
			for( ITransferObject to : list ) {
				RegistryMedia rm = (RegistryMedia) to;
				if (! StringUtils.isEmpty(rm.getValue()) ) {
					if ( result == null ) {
						result = rm;
					}
					if ( rm.isAdministrative() ) {
						result = rm;
						break;
					}					
				}
			}
		}
		return result;
	}	
	
	private ContactDetailsType getContactDetails( Registry registry ) throws ManagerBeanException {
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
	
	private IndividualType getIndividual( Registry registry, String name, IAddress address ) throws ManagerBeanException {
		IndividualType individualType = new IndividualType();
		String[] nameSplited = getNameSplited(name);
		individualType.setName( Util.toTextMax40Type(nameSplited[0]) );
		individualType.setFirstSurname( Util.toTextMax40Type(nameSplited[1]) );
		if ( address != null ) {
			CountryType country = getCountry(address.getGeozone());
			if ( CountryType.ESP.equals(country) ) {
				individualType.setAddressInSpain( getAddress(address, country) );	
			} else {
				individualType.setOverseasAddress( getOverseasAddress(address, country) );
			}			
		}
		individualType.setContactDetails( getContactDetails(registry) );
		return individualType;
	}

	private RecordData getRecordData(Registry registry) {
	    try {
			IManagerBean bean = BeanManager.getManagerBean(RecordData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RECORD_DATA_REGISTRY_ID), registry.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(! list.isEmpty()) {
				return (RecordData) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}	
		return null;
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
	
	private LegalEntityType getLegalEntity( Registry registry, String name, String tradeName, IAddress address ) throws ManagerBeanException {
		LegalEntityType legalEntityType = new LegalEntityType();
		legalEntityType.setCorporateName(Util.toTextMax80Type(name));
		legalEntityType.setTradeName(Util.toTextMax40Type(tradeName));
		RecordData recordData = getRecordData(registry);
		if ( recordData != null ) {
			legalEntityType.setRegistrationData( getRegistrationData(recordData) );
		}
		if ( address != null ) {
			CountryType country = getCountry(address.getGeozone());
			if ( CountryType.ESP.equals(country) ) {
				legalEntityType.setAddressInSpain( getAddress(address, country) );	
			} else {
				legalEntityType.setOverseasAddress( getOverseasAddress(address, country) );
			}			
		}
		legalEntityType.setContactDetails( getContactDetails(registry) );
		return legalEntityType;
	}
	
	private BusinessType getBusinessEnterpriseType( Registry registry, String name, String tradeName, String document, IAddress address ) throws ManagerBeanException {
		BusinessType party = new BusinessType();
		TaxIdentificationType taxIdentification = new TaxIdentificationType();
		PersonTypeCodeType personType = DocumentType.CIF.equals(registry.getDocumentType()) 
			? PersonTypeCodeType.J : PersonTypeCodeType.F;
		taxIdentification.setPersonTypeCode( personType );
		taxIdentification.setResidenceTypeCode( getResidenceTypeCode(registry) );
		taxIdentification.setTaxIdentificationNumber( Util.toTextMax30Type(document) );
		party.setTaxIdentification(taxIdentification);
		party.setPartyIdentification( Util.toTextMax10Type(String.valueOf(registry.getId())) );
		if ( personType == PersonTypeCodeType.F ) {
			party.setIndividual( getIndividual(registry, name, address) );
		} else {
			party.setLegalEntity( getLegalEntity(registry, name, tradeName, address) );
		}
		return party;
	}
	
	private BusinessType getBusinessType( Registry registry, String name, String tradeName, String document, IAddress address ) throws ManagerBeanException {
		BusinessType party = new BusinessType();
		TaxIdentificationType taxIdentification = new TaxIdentificationType();
		PersonTypeCodeType personType = getPersonTypeCode(registry);
		taxIdentification.setPersonTypeCode( personType );
		taxIdentification.setResidenceTypeCode( getResidenceTypeCode(registry) );
		taxIdentification.setTaxIdentificationNumber( Util.toTextMax30Type(document) );
		party.setTaxIdentification(taxIdentification);
		party.setPartyIdentification( Util.toTextMax10Type(String.valueOf(registry.getId())) );
		if ( personType == PersonTypeCodeType.F ) {
			party.setIndividual( getIndividual(registry, name, address) );
		} else {
			party.setLegalEntity( getLegalEntity(registry, name, tradeName, address) );
		}
		return party;
	}
	
	private WorkPlace getWorkPlace() {
		for( InvoiceDetail id : invoice.getLines() ) {
			if ( id.getWorkPlace()!=null && id.getWorkPlace().getId() != null ) {
				return id.getWorkPlace();
			}
		}
		return null;
	}
	
	private Enterprise getEnterprise() {
		InvoiceDetail invoiceDetail = invoice.getLines().iterator().next();
		return FinanceUtil.getEnterprise(invoiceDetail);
	}
	
	private void getFACeAdministrativeCentres(AdministrativeCentresType centres) throws ManagerBeanException {
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
			String centreDescription ) throws ManagerBeanException {
		String centreCode = FACeUtil.getValue(centreCodeKey, invoice);
		if (! StringUtils.isEmpty(centreCode) ) {
			AdministrativeCentreType centre = new AdministrativeCentreType();
			centre.setCentreCode(Util.toTextMax10Type(centreCode));
			centre.setRoleTypeCode( roleTypeCode );
			RegistryAddress address = FACeUtil.getAddress(addressKey, invoice);
			CountryType country = getCountry(address.getGeozone());
			if ( CountryType.ESP.equals(country) ) {
				centre.setAddressInSpain( getAddress(address, country) );	
			} else {
				centre.setOverseasAddress( getOverseasAddress(address, country) );
			}
			centre.setCentreDescription(Util.toTextMax2500Type(centreDescription));
			centres.getAdministrativeCentre().add(centre);
		}
	}
	
	private AdministrativeCentreType getAdministrativeCentre( WorkPlace workPlace ) throws ManagerBeanException {
		AdministrativeCentreType centre = new AdministrativeCentreType();
		centre.setCentreCode(Util.toTextMax10Type(String.valueOf(workPlace.getId())));
		RegistryAddress address = workPlace.getAddress();
		CountryType country = getCountry(address.getGeozone());
		if ( CountryType.ESP.equals(country) ) {
			centre.setAddressInSpain( getAddress(address, country) );	
		} else {
			centre.setOverseasAddress( getOverseasAddress(address, country) );
		}		
		return centre;
	}

	private BusinessType getEnterpriseParty() throws ManagerBeanException {
		Registry registry = enterprise.getRegistry();
		String name = registry.getName();
		String document = registry.getDocument();
		IAddress address = registry.getDefaultAddress();
		String tradeName = StringUtils.defaultIfEmpty(registry.getAlias(), workPlace.getDescription() );
		BusinessType party = getBusinessEnterpriseType(registry, name, tradeName, document, address);
		AdministrativeCentresType centres = new AdministrativeCentresType();
		centres.getAdministrativeCentre().add( getAdministrativeCentre(workPlace) );
		party.setAdministrativeCentres(centres);
		return party;
	}
	
	private BusinessType getInvoiceRegistryParty() throws ManagerBeanException {
		Registry registry = invoice.getRegistry();
		String name = StringUtils.defaultIfEmpty(invoice.getRegistryName(), registry.getName());
		String document = StringUtils.defaultIfEmpty(invoice.getRegistryDocument(), registry.getDocument());
		IAddress address = invoice.getAddress();
		if ( address == null ) {
			address = registry.getDefaultAddress();
		}
		String tradeName = registry.getAlias();
		BusinessType registryParty = getBusinessType(registry, name, tradeName, document, address);
		if ( FACeUtil.isDefined(invoice) ) {
			if (registryParty.getAdministrativeCentres() == null) {
				registryParty.setAdministrativeCentres(new AdministrativeCentresType());			
			}
			getFACeAdministrativeCentres(registryParty.getAdministrativeCentres());
		}
		if (address != null && StringUtils.isNotBlank(address.getAlias())) {
			if (registryParty.getAdministrativeCentres() == null) {
				registryParty.setAdministrativeCentres(new AdministrativeCentresType());			
			}
			AdministrativeCentreType centre = new AdministrativeCentreType();
			centre.setCentreCode(address.getAlias());
			centre.setRoleTypeCode(FACeUtil.FACE_VENDEDOR_ROLE_TYPE_CODE);
			CountryType country = getCountry(address.getGeozone());
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
	
	private PartiesType getParties() throws ManagerBeanException {
		PartiesType parties = new PartiesType();
		
		BusinessType enterpriseParty = getEnterpriseParty();
		BusinessType registryParty = getInvoiceRegistryParty();
		if ( invoice.getType() == com.code.aon.finance.enumeration.InvoiceType.SALES ) {
			parties.setSellerParty(enterpriseParty);			
			parties.setBuyerParty(registryParty);			
		} else {
			parties.setSellerParty(registryParty);			
			parties.setBuyerParty(enterpriseParty);						
		}
		
		return parties;
	}
	
	private FileHeaderType getFileHeader() {
		FileHeaderType fileHeader = new FileHeaderType();
		fileHeader.setSchemaVersion("3.2");
		// Modalidad: Individual
		fileHeader.setModality(ModalityType.I);
		// Tipo Emisor: Proveedor (Emisor)
		fileHeader.setInvoiceIssuerType(InvoiceIssuerTypeType.EM);
		
		fileHeader.setBatch( getBatchType() );
		
		return fileHeader;
	}
	
	private InvoiceHeaderType getInvoiceHeader() {
		InvoiceHeaderType invoiceHeader = new InvoiceHeaderType();
		invoiceHeader.setInvoiceNumber(Util.toTextMax20Type(String.valueOf(invoice.getNumber())) );
		invoiceHeader.setInvoiceSeriesCode(Util.toTextMax20Type(String.valueOf(invoice.getSeries())));
		// Factura Completa
		invoiceHeader.setInvoiceDocumentType( InvoiceDocumentTypeType.FC );
		// Original
		invoiceHeader.setInvoiceClass( InvoiceClassType.OO );
		return invoiceHeader;
	}	
	
	private InvoiceIssueDataType getInvoiceIssueData() {
		InvoiceIssueDataType invoiceIssueData = new InvoiceIssueDataType();
		XMLGregorianCalendar issuedDate = Util.toXMLCalendar(invoice.getIssueDate());
		invoiceIssueData.setIssueDate( issuedDate );
		PeriodDates period = new PeriodDates();
		period.setStartDate(issuedDate);
		period.setEndDate(issuedDate);
		invoiceIssueData.setInvoicingPeriod(period);
		invoiceIssueData.setInvoiceCurrencyCode(CurrencyCodeType.EUR);
		invoiceIssueData.setTaxCurrencyCode(CurrencyCodeType.EUR);
		invoiceIssueData.setLanguageName(LanguageCodeType.ES);
		return invoiceIssueData;
	}	
	
	private double getTaxQuota( TaxBreakDown tbd ) {
		return CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100, numberOfDecimals);
	}
	
	private TaxType getTax( TaxBreakDown tbd, boolean lineTax ) {
		TaxType tax = new TaxType();
		tax.setTaxTypeCode(RETENTION_TAX_TYPE_CODE);
		tax.setTaxRate( tbd.getTaxPercent() );
		tax.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && tbd.getTaxQuota() == 0 ) {
			double quota = getTaxQuota(tbd);
			tax.setTaxAmount( Util.getAmount(quota) );
		} else {
			tax.setTaxAmount( Util.getAmount(tbd.getTaxQuota()) );	
		}
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
	
	private TaxOutputType getTaxOutput( TaxBreakDown tdb ) {
		TaxOutputType taxOutput = new TaxOutputType();
		initTaxOutput(taxOutput, tdb, false);
		return taxOutput;
	}

	private void initTaxOutput( TaxOutputType taxOutput, TaxBreakDown tbd, boolean lineTax ) {
		TaxTypeCode code = TaxTypeCode.getTaxTypeCode(tbd.getTaxType());
		taxOutput.setTaxTypeCode( code != null ? code.getValue() : TaxTypeCode.IVA.getValue() );
		taxOutput.setTaxRate( tbd.getTaxPercent() );
		taxOutput.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && tbd.getTaxQuota()==0 ) {
			double quota = getTaxQuota(tbd);
			taxOutput.setTaxAmount( Util.getAmount(quota) );
		} else {
			taxOutput.setTaxAmount( Util.getAmount(tbd.getTaxQuota()) );	
		}
		if ( tbd.getSurchargePercent() != 0 ) {
			taxOutput.setEquivalenceSurcharge( tbd.getSurchargePercent() );
		}
		if ( tbd.getSurchargeQuota() != 0 ) {
			taxOutput.setEquivalenceSurchargeAmount( Util.getAmount(tbd.getSurchargeQuota()) );
		}
	}
	
	private void addTaxes( InvoiceType invoiceType ) {
		TaxesOutputs taxesOutputs = new TaxesOutputs();
		TaxesType taxesWithHeld = new TaxesType();
		for( TaxBreakDown tbd : taxBreakDowns ) {
			if ( tbd.getTaxType() == com.code.aon.config.enumeration.TaxType.RETENTION ) {
				taxesWithHeld.getTax().add( getTax(tbd, false) );
			} else {
				taxesOutputs.getTax().add( getTaxOutput(tbd) );
			}
		}
		if ( taxBreakDowns.isEmpty() ) {
			taxesOutputs.getTax().add( getEmptyTax() );
		}		
		if (! taxesOutputs.getTax().isEmpty() ) {
			invoiceType.setTaxesOutputs( taxesOutputs );
		}
		if (! taxesWithHeld.getTax().isEmpty() ) {
			invoiceType.setTaxesWithheld( taxesWithHeld );
		}
	}
	
	private InvoiceTotalsType getInvoiceTotals() {
		InvoiceTotalsType invoiceTotals = new InvoiceTotalsType();
		double taxableBase = priceStrategy.getTaxableBase(invoice);
		invoiceTotals.setTotalGrossAmount( taxableBase );
		invoiceTotals.setTotalGrossAmountBeforeTaxes( taxableBase );
		double totalTaxOutputs = priceStrategy.getTotalVatQuota(invoice, invoice);
		invoiceTotals.setTotalTaxOutputs( totalTaxOutputs );
		double totalTaxesWithheld = Math.abs( priceStrategy.getTotalRetentionQuota(invoice, invoice) );
		invoiceTotals.setTotalTaxesWithheld( totalTaxesWithheld );
		invoiceTotals.setInvoiceTotal( totalPrice.getTotalAmount() );
		invoiceTotals.setTotalOutstandingAmount( totalPrice.getTotalAmount() );
		invoiceTotals.setTotalExecutableAmount( totalPrice.getTotalAmount() );
		return invoiceTotals;
	}	
	
	private LegalLiteralsType getLegalLiterals() {
		LegalLiteralsType legalLiterals = new LegalLiteralsType();
		legalLiterals.getLegalReference().add( VAT_ACCRUAL_PAYMENT_TEXT );
		return legalLiterals;
	}	
	
	private InvoiceLineType.TaxesOutputs.Tax getLineTax( TaxBreakDown tdb ) {
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
	
	private void addLinesTaxes( InvoiceType invoiceType, InvoiceLineType invoiceLine, InvoiceDetail line ) {
		InvoiceLineType.TaxesOutputs taxesOutputs = new InvoiceLineType.TaxesOutputs();
		TaxesType taxesWithHeld = new TaxesType();
		List<TaxBreakDown> list = line.getTaxBreakDowns();
		for( TaxBreakDown tbd : list ) {
			if ( tbd.getTaxType() == com.code.aon.config.enumeration.TaxType.RETENTION ) {
				taxesWithHeld.getTax().add( getTax(tbd, true) );
			} else {
				taxesOutputs.getTax().add( getLineTax(tbd) );
			}
		}
		if ( list.isEmpty() ) {
			taxesOutputs.getTax().add( getEmptyLineTax() );
		}
		if (! taxesOutputs.getTax().isEmpty() ) {
			invoiceLine.setTaxesOutputs( taxesOutputs );
		}
		if (! taxesWithHeld.getTax().isEmpty() ) {
			invoiceLine.setTaxesWithheld( taxesWithHeld );
		}
	}
	
	private DiscountsAndRebatesType getDiscountsAndRebates( InvoiceDetail line, double totalCost ) {
		DiscountsAndRebatesType dar = new DiscountsAndRebatesType();
		double[] discounts = line.getDiscountExpression().getDiscounts();
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
	
	private IHeaderObject getHeaderObject( InvoiceDetail detail ) {
		try {
			ITransferObject header = detail.getSourceTo();
			if ( header != null ) {
				return (IHeaderObject)header;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	private String getIssuerContractReference( InvoiceDetail detail, boolean purchaseReference ) {
		if ( detail.getSource() == InvoiceSource.DELIVERY ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail dd = (DeliveryDetail)bean.get(detail.getSourceId());
				if ( (dd != null) && (dd.getSalesDetail() != null) ) {
					if (purchaseReference)
						return dd.getSalesDetail().getSales().getPurchaseReference();
					else
						return dd.getSalesDetail().getSales().getReferenceCode();
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else if ( detail.getSource() == InvoiceSource.INCOME ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(IncomeDetail.class);
				IncomeDetail id = (IncomeDetail)bean.get(detail.getSourceId());
				if ( (id != null) && (id.getPurchaseDetail() != null) ) {
					if (purchaseReference)
						return id.getPurchaseDetail().getPurchase().getPurchaseReference();
					else
						return id.getPurchaseDetail().getPurchase().getReferenceCode();
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}			
		} else if ( detail.getSource() == InvoiceSource.SALES ) {
			try {
				Sales sales = (Sales)detail.getSourceTo();
				if (purchaseReference)
					return sales.getPurchaseReference();
				else
					return sales.getReferenceCode();
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}			
		} else if ( detail.getSource() == InvoiceSource.PURCHASE ) {
			try {
				Purchase purchase = (Purchase)detail.getSourceTo();
				if (purchaseReference)
					return purchase.getPurchaseReference();
				else
					return purchase.getReferenceCode();
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}			
		}
		return null;
	}
	
	private IHeaderObject getDelivery( InvoiceDetail detail ) {
		switch ( detail.getSource() ) {
			case DELIVERY:
			case INCOME:
				return getHeaderObject(detail);
			default:
				return null;
		}
	}
	
	private InvoiceLineType getInvoiceLine( InvoiceDetail line, InvoiceType invoiceType ) {
		InvoiceLineType invoiceLine = new InvoiceLineType();
		invoiceLine.setIssuerTransactionReference(Util.toTextMax20Type(String.valueOf(line.getId())));
		if (line.getProject() != null) {
			invoiceLine.setReceiverContractReference(line.getProject().getName());
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
		String issuerContractReferenceOption = FACeUtil.getValue(FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE, invoice);
		if (! StringUtils.isEmpty(issuerContractReferenceOption)) {
			String issuerContractReference = getIssuerContractReference(line, Boolean.parseBoolean(issuerContractReferenceOption));
			if (! StringUtils.isEmpty(issuerContractReference) ) {
				invoiceLine.setIssuerContractReference(Util.toTextMax20Type(issuerContractReference));
			}		
		}
		String sequenceNumber = FACeUtil.getValue(FACeUtil.FACE_INVOICE_SEQUENCE_NUMBER, invoice);
		if (! StringUtils.isEmpty(sequenceNumber) && NumberUtils.isNumber(sequenceNumber) ) {
			invoiceLine.setSequenceNumber(Double.parseDouble(sequenceNumber));
		}
		String deliveryNoteNumberOption = FACeUtil.getValue(FACeUtil.FACE_INVOICE_DELIVERY_NUMBER, invoice);
		if (Boolean.parseBoolean(deliveryNoteNumberOption)) {
			IHeaderObject delivery = getDelivery(line);
			if (delivery != null) {
				DeliveryNotesReferencesType notes = new DeliveryNotesReferencesType();
				DeliveryNoteType noteType = new DeliveryNoteType();
				noteType.setDeliveryNoteNumber(delivery.getReferenceCode());
				try {
					GregorianCalendar deliveryNoteDate = new GregorianCalendar();
					deliveryNoteDate.setTime(delivery.getDate());
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
		if (registryDecimals != -1) {
			amount = CommonUtil.round(amount, numberOfDecimals);
		}
		return amount;
	}

	private ItemsType getItems( InvoiceType invoiceType ) {
		ItemsType items = new ItemsType();
		for( InvoiceDetail line : invoice.getLines() ) {
			items.getInvoiceLine().add( getInvoiceLine(line, invoiceType) );
		}		
		return items;
	}

	private PaymentMeans getPaymentMeans( PayMethod payMethod ) {
		PaymentMeans paymentMeans = null;
		if ( payMethod != null ) {
			paymentMeans = PaymentMeans.getPaymentMeans(payMethod.getType());	
		}
		return (paymentMeans != null) ? paymentMeans : PaymentMeans.AL_CONTADO;
	}
	
	private InstallmentType getInstallment( Finance finance ) {
		InstallmentType installment = new InstallmentType();
		installment.setInstallmentDueDate( Util.toXMLCalendar(finance.getDueDate()) );
		installment.setInstallmentAmount( finance.getAmount() );
		PaymentMeans paymentMeans = null;
		paymentMeans = getPaymentMeans( finance.getPayMethod() );
		installment.setPaymentMeans( paymentMeans.getValue() );			
		if ( finance.getBankAccount() != null ) {
			AccountType account = new AccountType();
			account.setIBAN( finance.getBankAccount().getIban() );
			if ( paymentMeans != PaymentMeans.TRANSFERENCIA ) {
				installment.setAccountToBeDebited(account);
			} else {
				installment.setAccountToBeCredited(account);
			}
		}
		return installment;
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
	
	private InvoiceType getInvoice() {
		InvoiceType invoiceType = new InvoiceType();
		invoiceType.setInvoiceHeader( getInvoiceHeader() );
		invoiceType.setInvoiceIssueData( getInvoiceIssueData() );
		addTaxes( invoiceType );
		invoiceType.setInvoiceTotals( getInvoiceTotals() );
		if ( this.invoice.isVatAccrualPayment() ) {
			invoiceType.setLegalLiterals( getLegalLiterals() );			
		}
		invoiceType.setItems( getItems(invoiceType) );
		addPaymentDetails( invoiceType );
		return invoiceType;
	}
	
	private InvoicesType getInvoices() {
		InvoicesType invoices = new InvoicesType();
		invoices.getInvoice().add( getInvoice() );
		return invoices;
	}

	private Facturae getFacturae() throws ManagerBeanException {
		Facturae facturae = new Facturae();
    	facturae.setFileHeader( getFileHeader() );
    	facturae.setParties( getParties() );
    	facturae.setInvoices( getInvoices() );
		return facturae;
	}
	
	private void init( Invoice invoice ) {
		this.invoice = invoice;
		this.priceStrategy = new InvoicePriceStrategy();
		this.totalPrice = Util.getAmount(priceStrategy.getTotalPrice(invoice, invoice));
		this.taxBreakDowns = priceStrategy.getTaxBreakDowns(invoice, invoice);
		this.workPlace = getWorkPlace();
		this.enterprise = getEnterprise();
		this.numberOfDecimals = DecimalUtil.getNumberOfDecimals(invoice);
		this.registryDecimals = DecimalUtil.getRegistryDecimals(invoice.getRegistry());
	}
	
	public void serialize( Invoice invoice, String fileName ) throws AonException {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
			Invoice _invoice = (Invoice) bean.get(invoice.getId());
			init( _invoice );
			Facturae facturae = getFacturae();  	
			MarshallerUtil marshallerUtil32 = MarshallerUtil.getInstance(FacturaeVersion.FACTURAE_32);
			marshallerUtil32.marshal( facturae, fileName );
			String realName = fileName + FACTURAE_EXTENSION;
	    	if ( numberOfDecimals != DecimalUtil.DEFAULT_DECIMALS ) {
	    		new DecimalUtil(numberOfDecimals).transform(facturae, realName);
	    	}
	    	HibernateUtil.getSession(sessionFactoryName).evict(_invoice);
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		    throw new AonException( t.getMessage(), t);
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
	}
	
}
