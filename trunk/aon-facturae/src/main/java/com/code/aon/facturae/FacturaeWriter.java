package com.code.aon.facturae;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.facturae.enumeration.PaymentMeans;
import com.code.aon.facturae.enumeration.TaxTypeCode;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;

import es.mityc.facturae.utils.MarshallerUtil;
import es.mityc.facturae31.AccountType;
import es.mityc.facturae31.AddressType;
import es.mityc.facturae31.AmountType;
import es.mityc.facturae31.BatchType;
import es.mityc.facturae31.BusinessType;
import es.mityc.facturae31.ContactDetailsType;
import es.mityc.facturae31.CountryType;
import es.mityc.facturae31.CurrencyCodeType;
import es.mityc.facturae31.DiscountType;
import es.mityc.facturae31.DiscountsAndRebatesType;
import es.mityc.facturae31.Facturae;
import es.mityc.facturae31.FileHeaderType;
import es.mityc.facturae31.IndividualType;
import es.mityc.facturae31.InstallmentType;
import es.mityc.facturae31.InstallmentsType;
import es.mityc.facturae31.InvoiceClassType;
import es.mityc.facturae31.InvoiceDocumentTypeType;
import es.mityc.facturae31.InvoiceHeaderType;
import es.mityc.facturae31.InvoiceIssueDataType;
import es.mityc.facturae31.InvoiceIssuerTypeType;
import es.mityc.facturae31.InvoiceLineType;
import es.mityc.facturae31.InvoiceTotalsType;
import es.mityc.facturae31.InvoiceType;
import es.mityc.facturae31.InvoicesType;
import es.mityc.facturae31.ItemsType;
import es.mityc.facturae31.LanguageCodeType;
import es.mityc.facturae31.LegalEntityType;
import es.mityc.facturae31.ModalityType;
import es.mityc.facturae31.OverseasAddressType;
import es.mityc.facturae31.PartiesType;
import es.mityc.facturae31.PersonTypeCodeType;
import es.mityc.facturae31.RegistrationDataType;
import es.mityc.facturae31.ResidenceTypeCodeType;
import es.mityc.facturae31.TaxIdentificationType;
import es.mityc.facturae31.TaxOutputType;
import es.mityc.facturae31.TaxType;
import es.mityc.facturae31.TaxesType;
import es.mityc.facturae31.InvoiceType.TaxesOutputs;

public class FacturaeWriter {
	
	private static final String RETENTION_TAX_TYPE_CODE = "04";

	private static final Logger LOGGER = Logger.getLogger(FacturaeWriter.class.getName());
	
	private Company company;
	
	private Invoice invoice;
	
	private InvoicePriceStrategy priceStrategy; 
	
	private AmountType totalPrice;
	
	private List<TaxBreakDown> taxBreakDowns;
	
	public FacturaeWriter(Company company) {
		this.company = company;
	}
	
	private BatchType getBatchType() {
		BatchType batchType = new BatchType();
		batchType.setBatchIdentifier( Util.getBatchIdentifier(invoice, company) );
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
		if (! registry.getId().equals(company.getId()) ) {
			if ( invoice.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY) {
				result = ResidenceTypeCodeType.U;
			} else if ( invoice.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY) {
				result = ResidenceTypeCodeType.E;
			}
		}
		return result;
	}
	
	private String getAddress( RegistryAddress registryAddress ) {
		StringBuffer value = new StringBuffer();
		value.append( registryAddress.getAddress() );
		if (! StringUtils.isEmpty(registryAddress.getAddress2()) ) {
			value.append(" ").append( registryAddress.getAddress2() );
		}
		if (! StringUtils.isEmpty(registryAddress.getAddress3()) ) {
			value.append(" ").append( registryAddress.getAddress3() );
		}
		return value.toString();
	}
	
	private AddressType getAddress( RegistryAddress registryAddress, CountryType country ) {
		AddressType address = new AddressType();
		address.setAddress( getAddress(registryAddress) );
		address.setPostCode( Util.toPostCodeType(registryAddress.getZip()) );
		address.setTown( registryAddress.getCity() );
		address.setProvince( registryAddress.getGeozone().getName() );
		address.setCountryCode( country );
		return address;
	}
	
	private OverseasAddressType getOverseasAddress( RegistryAddress registryAddress, CountryType country ) {
		OverseasAddressType overseasAddress = new OverseasAddressType();
		overseasAddress.setAddress( getAddress(registryAddress) );
		overseasAddress.setPostCodeAndTown( registryAddress.getZip() + " " + registryAddress.getCity() );
		overseasAddress.setProvince( registryAddress.getGeozone().getName() );
		overseasAddress.setCountryCode( country );
		return overseasAddress;
	}
	
	private GeoTree getGeoTree( GeoZone geozone ) throws ManagerBeanException {
		IManagerBean rMediaBean = BeanManager.getManagerBean(GeoTree.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IGeoZoneAlias.GEO_TREE_CHILD_ID), geozone.getId());
		List<ITransferObject> list = rMediaBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (GeoTree) list.get(0);
		}
		return null;
	}
	
	private GeoZone getGeoZoneCountry( GeoZone gz ) throws ManagerBeanException {
		if ( gz != null ) {
			GeoZone geozone = gz;
			while ( geozone != null ) {
				GeoTree geoTree = getGeoTree(geozone);
				if ( geoTree != null ) {
					if ( geoTree.getParent() == null ) {
						return geozone;
					} else {
						geozone = geoTree.getParent();
					}
				} else {
					break;		
				}
			}
		}
		return null;		
	}
	
	private CountryType getCountry( GeoZone gz ) throws ManagerBeanException {
		GeoZone geozone = getGeoZoneCountry(gz);
		if ( geozone != null ) {
			Locale countryLocale =null;
			String name = geozone.getName();
			for( Locale locale : Locale.getAvailableLocales() ) {
				if ( StringUtils.equalsIgnoreCase(name, locale.getDisplayCountry()) ) {
					countryLocale = locale;
					break;
				}
			}
			if ( countryLocale != null ) {
				String code = countryLocale.getISO3Country();
				for( CountryType country : CountryType.values() ) {
					if ( StringUtils.equalsIgnoreCase(code, country.value()) ) {
						return country;
					}
				}
			}
		}
		return null;
	}
	
	private RegistryMedia getRegistryMedia(Registry registry, MediaType type) throws ManagerBeanException{
		RegistryMedia result = null;
		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
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
			contactDetails.setTelephone( phone.getValue() );
		}
		RegistryMedia fax = getRegistryMedia(registry, MediaType.FAX);
		if ( fax != null ) {
			contactDetails.setTeleFax( fax.getValue() );
		}
		RegistryMedia email = getRegistryMedia(registry, MediaType.EMAIL);
		if ( email != null ) {
			contactDetails.setElectronicMail( email.getValue() );
		}
		RegistryMedia web = getRegistryMedia(registry, MediaType.WEB);
		if ( web != null ) {
			contactDetails.setWebAddress( web.getValue() );
		}
		return contactDetails;
	}
	
	private IndividualType getIndividual( Registry registry, String name, RegistryAddress registryAddress ) throws ManagerBeanException {
		IndividualType individualType = new IndividualType();
		individualType.setName( name );
		individualType.setFirstSurname( StringUtils.defaultString(registry.getSurname()) );
		CountryType country = getCountry(registryAddress.getGeozone());
		if ( CountryType.ESP.equals(country) ) {
			individualType.setAddressInSpain( getAddress(registryAddress, country) );	
		} else {
			individualType.setOverseasAddress( getOverseasAddress(registryAddress, country) );
		}
		individualType.setContactDetails( getContactDetails(registry) );
		return individualType;
	}

	private RecordData getRecordData(Registry registry) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(RecordData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.RECORD_DATA_REGISTRY_ID), registry.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if(! list.isEmpty()) {
			return (RecordData) list.get(0);
		}
		return null;
	}	
	
	private RegistrationDataType getRegistrationData( RecordData recordData ) {
		RegistrationDataType registrationData = new RegistrationDataType();
		registrationData.setRegisterOfCompaniesLocation( StringUtils.left(recordData.getDescription(), 20) );
		registrationData.setSheet( recordData.getSheet() );
		registrationData.setFolio( recordData.getPage() );
		registrationData.setSection( recordData.getSection() );
		registrationData.setVolume( recordData.getVolume() );
		registrationData.setAdditionalRegistrationData( StringUtils.left(recordData.getNotary(), 20) );
		return registrationData;
	}
	
	private LegalEntityType getLegalEntity( Registry registry, String name, RegistryAddress registryAddress ) throws ManagerBeanException {
		LegalEntityType legalEntityType = new LegalEntityType();
		legalEntityType.setCorporateName(name);
		legalEntityType.setTradeName(registry.getAlias());
		RecordData recordData = getRecordData(registry);
		if ( recordData != null ) {
			legalEntityType.setRegistrationData( getRegistrationData(recordData) );
		}
		CountryType country = getCountry(registryAddress.getGeozone());
		if ( CountryType.ESP.equals(country) ) {
			legalEntityType.setAddressInSpain( getAddress(registryAddress, country) );	
		} else {
			legalEntityType.setOverseasAddress( getOverseasAddress(registryAddress, country) );
		}
		legalEntityType.setContactDetails( getContactDetails(registry) );
		return legalEntityType;
	}
	
	private BusinessType getBusinessType( Registry registry, String name, String document, RegistryAddress registryAddress ) throws ManagerBeanException {
		BusinessType party = new BusinessType();
		TaxIdentificationType taxIdentification = new TaxIdentificationType();
		PersonTypeCodeType personType = getPersonTypeCode(registry);
		taxIdentification.setPersonTypeCode( personType );
		taxIdentification.setResidenceTypeCode( getResidenceTypeCode(registry) );
		taxIdentification.setTaxIdentificationNumber( document );
		party.setTaxIdentification(taxIdentification);
		party.setPartyIdentification( String.valueOf(registry.getId()) );
		if ( personType == PersonTypeCodeType.F ) {
			party.setIndividual( getIndividual(registry, name, registryAddress) );
		} else {
			party.setLegalEntity( getLegalEntity(registry, name, registryAddress) );
		}
		return party;
	}

	private BusinessType getCompanyParty() throws ManagerBeanException {
		return getBusinessType(company, company.getName(), company.getDocument(), company.getDefaultAddress());
	}
	
	private BusinessType getInvoiceRegistryParty() throws ManagerBeanException {
		Registry registry = invoice.getRegistry();
		String name = StringUtils.defaultIfEmpty(invoice.getRegistryName(), registry.getName());
		String document = StringUtils.defaultIfEmpty(invoice.getRegistryDocument(), registry.getDocument());
		RegistryAddress registryAddress = invoice.getRegistryAddress();
		if ( registryAddress == null ) {
			registryAddress = registry.getDefaultAddress();
		}
		return getBusinessType(registry, name, document, registryAddress);
	}
	
	private PartiesType getParties() throws ManagerBeanException {
		PartiesType parties = new PartiesType();
		
		BusinessType companyParty = getCompanyParty();
		BusinessType registryParty = getInvoiceRegistryParty();
		if ( invoice.getType() == com.code.aon.finance.enumeration.InvoiceType.SALES ) {
			parties.setSellerParty( companyParty );			
			parties.setBuyerParty( registryParty );			
		} else {
			parties.setSellerParty( registryParty );			
			parties.setBuyerParty(companyParty  );						
		}
		
		return parties;
	}
	
	private FileHeaderType getFileHeader() {
		FileHeaderType fileHeader = new FileHeaderType();
		fileHeader.setSchemaVersion("3.1");
		// Modalidad: Individual
		fileHeader.setModality(ModalityType.I);
		// Tipo Emisor: Proveedor (Emisor)
		fileHeader.setInvoiceIssuerType(InvoiceIssuerTypeType.EM);
		
		fileHeader.setBatch( getBatchType() );
		
		return fileHeader;
	}
	
	private InvoiceHeaderType getInvoiceHeader() {
		InvoiceHeaderType invoiceHeader = new InvoiceHeaderType();
		invoiceHeader.setInvoiceNumber( String.valueOf(invoice.getNumber()) );
		invoiceHeader.setInvoiceSeriesCode( String.valueOf(invoice.getSeries()) );
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
		invoiceIssueData.setInvoiceCurrencyCode(CurrencyCodeType.EUR);
		invoiceIssueData.setTaxCurrencyCode(CurrencyCodeType.EUR);
		invoiceIssueData.setLanguageName(LanguageCodeType.ES);
		return invoiceIssueData;
	}	
	
	private TaxType getTax( TaxBreakDown tbd, boolean lineTax ) {
		TaxType tax = new TaxType();
		tax.setTaxTypeCode(RETENTION_TAX_TYPE_CODE);
		tax.setTaxRate( tbd.getTaxPercent() );
		tax.setTaxableBase( Util.getAmount(tbd.getBase()) );
		if( lineTax && (tbd.getTaxQuota() == 0) ) {
			double quota = CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100);
			tax.setTaxAmount( Util.getAmount(quota) );
		} else {
			tax.setTaxAmount( Util.getAmount(tbd.getTaxQuota()) );	
		}
		return tax;
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
		if( lineTax && (tbd.getTaxQuota() == 0) ) {
			double quota = CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100);
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
	
	private InvoiceLineType.TaxesOutputs.Tax getLineTax( TaxBreakDown tdb ) {
		InvoiceLineType.TaxesOutputs.Tax tax = new InvoiceLineType.TaxesOutputs.Tax();
		initTaxOutput( tax, tdb, true );
		return tax;
	}

	private InvoiceLineType.TaxesOutputs.Tax getEmptyLineTax( InvoiceType invoiceType ) {
		InvoiceLineType.TaxesOutputs.Tax tax = new InvoiceLineType.TaxesOutputs.Tax();
		List<TaxOutputType> list = invoiceType.getTaxesOutputs().getTax();
		TaxOutputType taxOutput = list.get(0);
		tax.setTaxTypeCode( taxOutput.getTaxTypeCode() );
		tax.setTaxRate( taxOutput.getTaxRate() );
		tax.setTaxableBase( Util.getAmount(0) );
		tax.setTaxAmount( Util.getAmount(0) );
		return tax;
	}
	
	@SuppressWarnings("unchecked")
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
			taxesOutputs.getTax().add( getEmptyLineTax(invoiceType) );
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
	
	private InvoiceLineType getInvoiceLine( InvoiceDetail line, InvoiceType invoiceType ) {
		InvoiceLineType invoiceLine = new InvoiceLineType();
		invoiceLine.setIssuerTransactionReference( String.valueOf(line.getId()) );
		invoiceLine.setItemDescription( line.getDescription() );
		invoiceLine.setQuantity( line.getQuantity() );
		invoiceLine.setUnitPriceWithoutTax( line.getPrice() );
		double totalCost = line.getQuantity() * line.getPrice();
		invoiceLine.setTotalCost( totalCost );
		invoiceLine.setGrossAmount( line.getTaxableBase() );
		if ( line.getDiscountExpression() != null ) {
			invoiceLine.setDiscountsAndRebates( getDiscountsAndRebates(line, totalCost) );
		}
		addLinesTaxes( invoiceType, invoiceLine, line );
		return invoiceLine;
	}
	
	private ItemsType getItems( InvoiceType invoiceType ) {
		ItemsType items = new ItemsType();
		for( InvoiceDetail line : invoice.getLines() ) {
			items.getInvoiceLine().add( getInvoiceLine(line, invoiceType) );
		}		
		return items;
	}

	private PaymentMeans getPaymentMeans( PayMethod payMethod ) {
		PaymentMeans paymentMeans = PaymentMeans.getPaymentMeans(payMethod.getType());
		return (paymentMeans != null) ? paymentMeans : PaymentMeans.ESPECIALES;
	}
	
	private InstallmentType getInstallment( Finance finance ) {
		InstallmentType installment = new InstallmentType();
		installment.setInstallmentDueDate( Util.toXMLCalendar(finance.getDueDate()) );
		installment.setInstallmentAmount( finance.getAmount() );
		PaymentMeans paymentMeans = getPaymentMeans( finance.getPayMethod() );
		installment.setPaymentMeans( paymentMeans.getValue() );
		if ( finance.getBankAccount() != null ) {
			AccountType account = new AccountType();
			account.setIBAN( finance.getBankAccount().toString() );
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
	}
	
	public void serialize( Invoice invoice, String fileName ) {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			HibernateUtil.getSession(sessionFactoryName).refresh(invoice);
			init( invoice );
			Facturae facturae = getFacturae();  	
	    	MarshallerUtil.marshal( facturae, fileName );		
		} catch (Throwable t ){
		    try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
	}
	
	public static Invoice getAonInvoice( Integer ... id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		int offset = 0;
		Criteria criteria = new Criteria();
		String status = bean.getFieldName(IFinanceAlias.INVOICE_STATUS);
		criteria.addEqualExpression( status, InvoiceStatus.SCORED);
		String type = bean.getFieldName(IFinanceAlias.INVOICE_TYPE);
		criteria.addEqualExpression( type, com.code.aon.finance.enumeration.InvoiceType.SALES);
		if ( id.length > 0 ) {
			String idField = bean.getFieldName(IFinanceAlias.INVOICE_ID);
			criteria.addEqualExpression( idField, id[0] );			
		} else {
			int count = bean.getCount(criteria);
			offset = RandomUtils.nextInt(count);
		}
		List<ITransferObject> list = bean.getList(criteria, offset, 1);
		if (! list.isEmpty() ) {
			return (Invoice) list.get(0);
		}
		return null;
	}

	public static List<ITransferObject> getAonInvoices() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName(IFinanceAlias.INVOICE_TYPE);
		criteria.addEqualExpression( type, com.code.aon.finance.enumeration.InvoiceType.SALES);
		return bean.getList(criteria);
	}
	
	public static Company getCompany() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		List<ITransferObject> list = bean.getList(null, 0, 1);
		if (! list.isEmpty() ) {
			return (Company) list.get(0);
		}
		return null;
	}
    
}