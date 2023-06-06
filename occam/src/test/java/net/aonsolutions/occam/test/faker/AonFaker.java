package net.aonsolutions.occam.test.faker;

import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.Locale;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.accounting.AccountingPeriod;
import net.aonsolutions.occam.api.config.AccountingConfiguration;
import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.config.RegistryAddress;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.invoicing.Invoice;
import net.aonsolutions.occam.api.invoicing.InvoiceBreakdown;
import net.aonsolutions.occam.api.invoicing.InvoiceDetail;
import net.aonsolutions.occam.api.invoicing.InvoiceTax;
import net.aonsolutions.watson.client.util.AonNumberUtils;

public class AonFaker {
	private static Faker faker = Faker.instance(Locale.of("es"));
	private static final String DOCUMENT_REGEXP = "(\\d|[XYZ])\\d{7}[A-Z]";

	public static Account getAccount(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getAccount():null;
	}
	public static Account getAccount( ) {
		return new Account()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setCode( faker.regexify("\\d{9}") )
			.setDescription( faker.animal().name() )
			.setAlias( faker.animal().name() )
			.setActive( !AonRandom.gt(3) );
	}

	public static AccountingConfiguration getAccountingConfiguration(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getAccountingConfiguration():null;
	}
	public static AccountingConfiguration getAccountingConfiguration() {
		AccountingConfiguration ac = new AccountingConfiguration();
		IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonEnumRandom.getAppParam() )
			.forEach( appParam -> ac.setAccount(appParam, getAccount()));
		return ac;
	}
	
	public static AccountingPeriod getAccountingPeriod() {
		return new AccountingPeriod()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setName( faker.regexify("\\d{9}") )
			.setStartDate( AonRandom.pastDate(-1) )
			.setEndDate( AonRandom.pastDate(-1) )
			.setDefaultPeriod( AonRandom.gt(3) )
			.setAudit( getAudit() );
	}
	

	public static Activity getActivity( ) {
		return  new Activity()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setDescription( faker.animal().name() )
			.setEpigraph( faker.animal().name() );
	}

	public static ApplicationParameter getApplicationParameter( ) {
		return  new ApplicationParameter()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setName( AonEnumRandom.getAppParam(50).orElse(null))
			.setValue( faker.animal().name());
	}

	public static Audit getAudit() {
		return new Audit()
			.setCreationUser(AonRandom.string(50, 10))
			.setCreationDate(AonRandom.pastDate(50))
			.setModificationUser(AonRandom.string(50, 10))
			.setModificationDate(AonRandom.pastDate(50));
	}

	public static Booking getBooking() {
		return new Booking()
			.setOwner(AonRandom.string(AonRandom.REQUIRED,1,50))
			.setExpirationDate(AonRandom.pastDate(50))
			.setDomainManagement(AonRandom.gt(50))
			.setDisableDomainManagement(AonRandom.gt(50))
			.setMaxDefinedUsers(AonRandom.integer(50))
			.setAonCustomer(AonRandom.integer(50))
			.setAonStatus(AonEnumRandom.getAonStatus(20).orElse(null))
		;
	}

	public static Configuration getConfiguration() {
		return new Configuration()
			.setAccounting(getAccountingConfiguration(80));
	}

	public static Domain getDomainStandalone() {
		return getDomain(true)
				.setParent(null)
				.setInheritance(false)
				.setType( DomainType.ENTERPRISE )
				;
	}

	public static Domain getDomain() {
		return getDomain(false); 
	}
	public static Domain getDomain( boolean bookingRequired) {
		return  new Domain()
			.setId(AonRandom.integer(50))
			.setName(AonRandom.domainName())
			.setDescription(AonRandom.lorem(50))
			.setParent( AonRandom.gt(50) ? getDomain() : null)
			.setType(AonEnumRandom.getDomainType(20).orElse(null))
			.setInheritance(AonRandom.gt(50))
			.setActive(AonRandom.gt(50))
			.setId(AonRandom.integer(50))
			.setBooking( bookingRequired || AonRandom.gt(50) ? getBooking() : null)
			.setAudit( AonRandom.gt(50) ? getDomainAudit() : null)
		;
	}

	public static DomainAudit getDomainAudit() {
		return new DomainAudit()
			.setLastAccessUser(AonRandom.string(50, 10))
			.setLastAccessDate(AonRandom.pastDate(50))
			.setCreationUser(AonRandom.string(50, 10))
			.setCreationDate(AonRandom.pastDate(50))
			.setModificationUser(AonRandom.string(50, 10))
			.setModificationDate(AonRandom.pastDate(50));
	}
	
	public static Geozone getGeozone( ) {
		return new Geozone()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer())
			.setName(faker.country().name())
			.setCode(faker.country().countryCode2())
			;
	}

	public static InvoiceBreakdown getInvoiceBreakdown() {
		return new InvoiceBreakdown()
			.setId(AonRandom.integer(50))
			.setTaxType(AonEnumRandom.getTaxType())
			.setBase(AonRandom.getDouble(0, 100))
			.setPercent(AonRandom.getDouble(0, 100))
			.setQuota(AonRandom.getDouble(0, 100))
			.setSurchargePercent(AonRandom.getDouble(0, 100))
			.setSurchargeQuota(AonRandom.getDouble(0, 100))
			.setDeductiblePercent( AonRandom.getDouble(0, 80))
			.setDeductibleQuota(AonRandom.getDouble(0, 100))
			.setWithholdingType(AonEnumRandom.getWithholdingType())
			.setVatDeductionType(AonEnumRandom.getVatDeductionType())
			;
	}

	public static Invoice getInvoice() {
		return new Invoice()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setActivity(AonFaker.getActivity())
			.setSeries(AonRandom.string(50, 10))
			.setNumber(AonRandom.integer(50))
			.setReferenceCode(AonRandom.string(50, 10))
			.setIssueDate(AonRandom.today())
			.setTaxDate(AonRandom.today())
			.setConfidential(AonRandom.gt(98))
			.setRegistry(AonRandom.integer(50))
			.setDocument(AonRandom.string(50, 10))
			.setDocumentType(AonEnumRandom.getDocumentType(40).orElse(null)) 
			.setDocumentCountry(AonEnumRandom.getCountry(30).orElse(null))
			.setName(AonRandom.string(50, 10))
			.setScope(AonFaker.getScope())
			.setType(AonEnumRandom.getInvoiceType(40).orElse(null)) 
			.setTransaction(AonEnumRandom.getTransactionType(40).orElse(null))
			.setSurcharge(AonRandom.gt(50))
			.setWithholding(AonRandom.gt(50))
			.setWithholdingFarmer(AonRandom.gt(50))
			.setVatAccrualPayment(AonRandom.gt(50))
			.setInvestment(AonRandom.gt(50))
			.setService(AonRandom.gt(50))
			.setAnnulled(AonRandom.gt(50))
			.setTotal(AonRandom.getDouble(0, 10))
			.setAudit(AonFaker.getAudit())
			.setDetails( IntStream.range(0, AonRandom.number(1, 10)).mapToObj(i -> AonFaker.getInvoiceDetail() ).toList() )
			.setBreakdown( IntStream.range(0, AonRandom.number(1, 5)).mapToObj(i -> AonFaker.getInvoiceBreakdown() ).toList() )
		;
	}

	public static InvoiceDetail getInvoiceDetail() {
		return new InvoiceDetail()
			.setId(AonRandom.integer(50))
			.setItem(AonRandom.integer(50))
			.setLine(AonRandom.integer(50))
			.setDescription(AonRandom.string(50, 10))
			.setQuantity(AonRandom.getDouble(0, 10))
			.setDiscountExpression( AonRandom.gt(10)?null:AonNumberUtils.toString( AonRandom.getDouble(0, 100)))
			.setPrice(AonRandom.getDouble(0, 100))
			.setTaxableBase(AonRandom.getDouble(0, 100))
			.setSource(AonEnumRandom.getInvoiceSource()) 
			.setPrepayment(AonRandom.gt(98))
			.setSourceId(AonRandom.integer(50))
			.addTax( getInvoiceTax())
		;
	}
	
	public static InvoiceTax getInvoiceTax() {
		return new InvoiceTax()
			.setId(AonRandom.integer(50))
			.setTaxType(AonEnumRandom.getTaxType())
			.setPercent(AonRandom.getDouble(0, 100))
			.setSurchargePercent(AonRandom.getDouble(0, 100))
			.setDeductiblePercent( AonRandom.getDouble(0, 80))
			.setWithholdingType(AonEnumRandom.getWithholdingType())
			.setVatDeductionType(AonEnumRandom.getVatDeductionType())
		;
	}

	public static Registry getRegistry(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getRegistry():null;
	}
	public static Registry getRegistry( ) {
		return  new Registry()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setDocument(faker.regexify(DOCUMENT_REGEXP))
			.setDocumentType( AonEnumRandom.getDocumentType(90).orElse(null) )
			.setDocumentCountry( AonRandom.gt(5) ? Country.ES: AonEnumRandom.getCountry(10).orElse(null))
			.setName( faker.company().name() )
			.setAlias( faker.company().profession() )
			.setNationality( AonRandom.gt(5) ? Country.ES: AonEnumRandom.getCountry(50).orElse(null))
			.setConfidential( !AonRandom.gt(3) );
	}
	
	public static RegistryAddress getRegistryAddress() {
		return new RegistryAddress()
			.setRegistry(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setMain(AonRandom.gt(50))
			.setStreetType(AonEnumRandom.getStreetType(75).orElse(null))
			.setRecipient( AonRandom.name(20, RADDRESS.RECIPIENT.getDataType().length()) )
			.setAddress( AonRandom.gt(10)?faker.address().streetName():null )
			.setNumber( AonRandom.gt(12)?faker.address().streetAddressNumber():null)
			.setAddress2( AonRandom.gt(90)?faker.address().secondaryAddress():null )
			.setAddress3( AonRandom.gt(97)?faker.address().secondaryAddress():null )
			.setZip( AonRandom.gt(10)?faker.address().zipCode():null )
			.setCity( AonRandom.gt(10)?faker.address().city():null )
			.setGeozone( getGeozone() )
			.setRecipient( AonRandom.alias(20, RADDRESS.ALIAS.getDataType().length()) )
			.setMunicipalityCode(AonRandom.gt(30)?faker.address().zipCode():null);
	}

	public static Scope getScope(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getScope():null;
	}
	public static Scope getScope( ) {
		return new Scope()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setDescription(faker.pokemon().name());
	}
	
	public static User getUser( ) {
		return  new User()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setName(faker.pokemon().name())
			.setLogin(AonRandom.string(50, 10))
			.setActive(AonRandom.gt(50));
	}
	
}
