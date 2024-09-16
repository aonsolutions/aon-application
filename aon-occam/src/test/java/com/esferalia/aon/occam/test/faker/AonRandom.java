package com.esferalia.aon.occam.test.faker;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.CreditorFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IWithholdingTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceMin;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType.AccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingRegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class AonRandom {
	private static Faker faker = new Faker( Locale.of("es"));
	
    public static boolean gt( int threshold) {
		return faker.random().nextInt(0,100) >= threshold;
	}
    public static String uuid( int maxLength ) {
    	return AonStringUtils.substring(faker.internet().uuid(),0 ,maxLength);
    }

    public static String string( int nullThreshold, int minLength, int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(minLength, maxLength)
        		: null;
    }
    public static String string( int maxLength ) {
    	return faker.lorem().characters(0, maxLength);
    }
    public static String string( int nullThreshold,  int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(0, maxLength)
        		:null;
    }
    public static String item( int nullThreshold,  int maxLength ) {
    	return ( gt(nullThreshold) )
        		?AonStringUtils.abbreviate(faker.book().title(),maxLength)
        		:null;
    }
    public static Integer integer( int nullThreshold ) {
    	return integer(nullThreshold, Integer.MAX_VALUE-1 );
    }
    public static Integer integer( int nullThreshold,  int maxLength ) {
    	return ( gt(nullThreshold) )
        		?Integer.valueOf( getInt(0, maxLength) )
        		:null;
    }
    public static String lorem( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(0, maxLength)
        		:null;
    }

    public static String name( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
    		?AonStringUtils.abbreviate( faker.name().fullName(), maxLength)
    		:null;
    }
    public static String alias( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
    		?AonStringUtils.abbreviate( faker.name().username(), maxLength)
    		:null;
    }

    public static Integer number(int nullThreshold, int from, int to) {
    	return ( gt(nullThreshold) )
        		?number(from, to)
        		:null;
    }
    public static int number( int from, int to) {
    	return faker.random().nextInt(from, to);
    }
    public static int getInt( int from, int to) {
    	return number(from, to);
    }
    
    public static double getPercent() {
    	return getDouble(0, 100 , 0);
    }
    public static double getPercent( int proecision) {
    	return getDouble(0, 100 , proecision);
    }
    public static double getDouble( int from, int to) {
    	return getDouble(from, to , 2);
    }
    public static double getDouble( int from, int to, int precision ) {
    	double r = faker.random().nextDouble();
    	return AonMathUtils.round(from + ((to - from) * r), precision);
    }
    public static Double getDouble(int nullThreshold, int from, int to, int precision ) {
    	return ( gt(nullThreshold) )
        		?getDouble(from, to, precision)
        		:null;
    }
    
    public static Date getRandomYearDay( int year ) {
    	return truncate( faker.date().between(AonDateUtils.getYearFirstDay(year),AonDateUtils.getYearLastDay(year)));
    }
    
    public static Date getPastDate( int threshold ) {
    	return ( gt(threshold) )
        		?truncate( faker.date().past(100, TimeUnit.DAYS, new Date()))
        		:null;
    }
    public static Date today( ) {
    	return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date yesterday( ) {
    	return Date.from(LocalDate.now().plusDays(-1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date tomorrow( ) {
    	return Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    public static Date getRangeDate( Date year, Period period ) {
    	return getRangeDate(
			FiscalUtils.getPeriodStart(AonDateUtils.getYear(year),period),
			FiscalUtils.getPeriodEnd(AonDateUtils.getYear(year),period));
    }

    public static Date getRangeDate( Date start, Date end ) {
    	return faker.date().between(start, end);
    }
    public static Date getYearDay( Date date ) {
    	return faker.date().between(AonDateUtils.getYearFirstDay(date), AonDateUtils.getYearLastDay(date));
    }
    public static Date getFutureDate( Date date ) {
    	return getFutureDate(0, new Date());
    }
    public static Date getFutureDate( int threshold ) {
    	return getFutureDate(threshold, new Date());
    }
    public static Date getFutureDate( int threshold, Date date ) {
    	return ( gt(threshold) )
        		?truncate( faker.date().future(100, TimeUnit.DAYS, date))
        		:null;
    }

    private static Date truncate( Date date) {
    	return date == null 
			? null 
			: Date.from(
			date.toInstant()
				.atZone(ZoneId.of("Europe/Madrid"))
				.truncatedTo(ChronoUnit.DAYS)
				.toInstant()		
			);
    }

    public static <T> T random(List<T> list){
    	if (list == null || list.isEmpty()) return null;
        return list.get(faker.random().nextInt(0, (list.size() - 1)));
    }	

//    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
//    	return randomEnum(clazz,0);
//    }	
//    public static <T extends Enum<?>> T randomEnum(Class<T> clazz, int nullThreshold){
//    	// ******
//    	// ¡¡En algún caso puede devolver NULL!!
//    	// Sobre todo si el item del enumerado tienen implementación
//    	// VER --> https://stackoverflow.com/questions/33358616/reflection-on-enums
//    	// *****
//    	
//    	return gt(nullThreshold)
//    			?clazz.getEnumConstants()[faker.random().nextInt(clazz.getEnumConstants().length-1)]
//				:null;
//    }	

	public static Tariff getTariff(AONContext ctx) {
		return getTariff(ctx, 0);
	}
	public static Tariff getTariff(AONContext ctx, int nullThreshold){
		return gt(nullThreshold)
			?TariffDAO.getRandom(ctx, null )
			:null;
	}

	public static GeoZone getGeozone(AONContext ctx) {
		return getGeozone(ctx, 0);
	}
	public static GeoZone getGeozone(AONContext ctx, int nullThreshold){
		return gt(nullThreshold)
			?GeoZoneDAO.getRandom(ctx, null )
			:null;
	}

	public static Registry getRegistry(AONContext ctx) {
		return getRegistry(ctx,null);
	}
	public static Registry getRegistry(AONContext ctx, RegistryFilter filter) {
		return RegistryDAO.getRandom(ctx,filter);
	}
	
	public static RegistryMedia getRegistryMedia(AONContext ctx, RegistryMediaFilter filter) {
		return RegistryMediaDAO.getRandom(ctx, filter );
	}
	
	public static RegistryAddress getRegistryAddress(AONContext ctx, RegistryAddressFilter filter) {
		return RegistryAddressDAO.getRandom(ctx, filter );
	}

	public static Customer ensureCustomer(AONContext ctx) {
		Customer customer = getCustomer(ctx,null);
		if (customer == null) {
			customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx));	
		}
		return customer;
	}
	public static Customer getCustomer(AONContext ctx) {
		return getCustomer(ctx,null);	
	}
	public static Customer getCustomer(AONContext ctx, CustomerFilter filter) {
		return CustomerDAO.getRandom(ctx, filter);
	}

	public static Creditor ensureCreditor(AONContext ctx) {
		Creditor creditor = getCreditor(ctx,null);
		if (creditor == null) {
			creditor = CreditorDAO.save(ctx, AonFaker.getCreditor(ctx));	
		}
		return creditor;
	}
	public static Creditor getCreditor(AONContext ctx) {
		return getCreditor(ctx,null);	
	}
	public static Creditor getCreditor(AONContext ctx, CreditorFilter filter) {
		return CreditorDAO.getRandom(ctx, filter);
	}

	public static Supplier ensureSupplier(AONContext ctx) {
		Supplier supplier = getSupplier(ctx,null);
		if (supplier == null) {
			supplier = SupplierDAO.save(ctx, AonFaker.getSupplier(ctx));	
		}
		return supplier;
	}
	public static Supplier getSupplier(AONContext ctx) {
		return getSupplier(ctx,null);	
	}
	public static Supplier getSupplier(AONContext ctx, SupplierFilter filter) {
		return SupplierDAO.getRandom(ctx, filter);
	}
	
	public static Account getAccount(AONContext ctx) {
		return getAccount(ctx, 0);
	}
	public static Account getAccount(AONContext ctx, int nullThreshold){
		return gt(nullThreshold)
			?AccountDAO.getRandom(ctx, null )
			:null;
	}
	public static Account getAccount(AONContext ctx, int nullThreshold, AccountFilter filter){
		return gt(nullThreshold)
			?AccountDAO.getRandom(ctx, filter )
			:null;
	}
	public static AccountPeriod getAccountPeriod(AONContext ctx) {
		return getAccountPeriod(ctx, 0);
	}
	public static AccountPeriod getAccountPeriod(AONContext ctx, int nullThreshold){
		return gt(nullThreshold)
			?AccountPeriodDAO.getRandom(ctx, null )
			:null;
	}
	
	public static EnterpriseActivity getRandomActivity(AONContext ctx) {
		boolean mainActivity =  gt(85);
		LinkedList<EnterpriseActivity> list = CompanyDAO.getEnterpriseActivities(ctx, ctx.getDomainId(), null)
			.filter(act -> act.isPrincipal() == mainActivity)
			.collect(Collectors.toCollection(LinkedList::new));
		if ( list == null || list.isEmpty()) return new EnterpriseActivity();
		return list.get( AonRandom.getInt(0, list.size() -1));
	}
	
	public static Administration getRandomAdministration() {
		return getRandomAdministration(-1);
	}
	public static Administration getRandomAdministration(int nullThreshold) {
		// **********************
		// No tiene ningún sentido que en OCCAM exista Administration.UNKNOWN
		// cuando en el enumerado de FACES no existía.
		// Si se genera UNKNOWN de manera aleatoria, la aplicacion faces no funciona.
		// **********************
		int length = 0;
		if (Administration.UNKNOWN != null) {
			length =  Administration.values().length - 1;
		} else {
			length =  Administration.values().length;
		}
		// **********************
		// **********************
		// **********************
		
    	return gt(nullThreshold)
    			?Administration.values()[faker.random().nextInt(length)]
    			:null;
	}

	public static TaxType getRandomTaxType() {
		return getRandomTaxType(-1);
	}
	public static TaxType getRandomTaxType(int nullThreshold) {
    	return gt(nullThreshold)
			?TaxType.values()[faker.random().nextInt(TaxType.values().length)]
			:null;
	}

	public static WithholdingType getRandomWithholdingType() {
		return getRandomWithholdingType(-1);
	}
	public static WithholdingType getRandomWithholdingType(int nullThreshold) {
    	return gt(nullThreshold)
			?WithholdingType.values()[faker.random().nextInt(WithholdingType.values().length)]
			:null;
	}

	public static VatDeductionType  getRandomVatDeductionType() {
		return getRandomVatDeductionType(-1);
	}
	public static VatDeductionType getRandomVatDeductionType(int nullThreshold) {
    	return gt(nullThreshold)
			?VatDeductionType.values()[faker.random().nextInt(VatDeductionType.values().length)]
			:null;
	}

	public static BalanceType getRandomBalanceType() {
		return getRandomBalanceType(-1);
	}
	public static BalanceType getRandomBalanceType(int nullThreshold) {
    	return gt(nullThreshold)
    			?BalanceType.values()[faker.random().nextInt(BalanceType.values().length)]
    			:null;
	}
	
	public static String getRandomContractType() {
		return getRandomContractType(-1);
	}
	public static String getRandomContractType(int nullThreshold) {
		Integer  models [] = new ContractType().getContractTypes().keySet().toArray(Integer[]::new);
    	return gt(nullThreshold)
    			? String.format("%03d", models[faker.random().nextInt(models.length)])
    			:null;
	}

	public static String getRandomOccupation() {
		return getRandomQuoteGroup(-1);
	}
	public static String getRandomOccupation(int nullThreshold) {
		String occupations [] = {"a","b","c","d","e","f","g","h","i","v","w","x","y","z"};
    	return gt(nullThreshold) ? occupations [faker.random().nextInt(occupations.length)] :null;
	}

	public static String getRandomQuoteGroup() {
		return getRandomQuoteGroup(-1);
	}
	public static String getRandomQuoteGroup(int nullThreshold) {
    	return gt(nullThreshold)?String.format("%02d", faker.random().nextInt(1,10)):null;
	}

	public static Province getRandomProvince() {
		return getRandomProvince(-1);
	}
	public static Province getRandomProvince(int nullThreshold) {
    	return gt(nullThreshold)
    			?Province.values()[faker.random().nextInt(Province.values().length)]
    			:null;
	}

	public static String getRandomSex() {
		return getRandomGender(-1).getName().substring(0,1).toUpperCase();
	}
	public static Gender getRandomGender() {
		return getRandomGender(-1);
	}
	public static Gender getRandomGender(int nullThreshold) {
    	return gt(nullThreshold)
    			?Gender.values()[faker.random().nextInt(Gender.values().length)]
    			:null;
	}

	public static String getRandomSSRegime() {
		return getRandomSSRegimeType(-1).getCode();
	}
	public static SSRegimeType getRandomSSRegimeType() {
		return getRandomSSRegimeType(-1);
	}
	public static SSRegimeType getRandomSSRegimeType(int nullThreshold) {
    	return gt(nullThreshold)
    			?SSRegimeType.values()[faker.random().nextInt(SSRegimeType.values().length)]
    			:null;
	}

	public static Country getRandomCountry() {
		return getRandomCountry(-1);
	}
	public static Country getRandomCountry(int nullThreshold) {
    	return gt(nullThreshold)
    			?Country.values()[faker.random().nextInt(Country.values().length)]
    			:null;
	}
	
	public static DocumentType getRandomDocumentType() {
		return getRandomDocumentType (-1);
	}
	public static DocumentType getRandomDocumentType(int nullThreshold) {
		return gt(nullThreshold)
			?DocumentType.values()[faker.random().nextInt(DocumentType.values().length)]
			:null;
	}
	
	public static InvoiceTransactionType getRandomInvoiceTransactionType() {
		return getRandomInvoiceTransactionType (-1);
	}
	public static InvoiceTransactionType getRandomInvoiceTransactionType(int nullThreshold) {
		return gt(nullThreshold)
			?InvoiceTransactionType.values()[faker.random().nextInt(InvoiceTransactionType.values().length)]
			:null;
	}
	
	public static InvoiceType getRandomInvoiceType() {
		return getRandomInvoiceType(-1);
	}
	public static InvoiceType getRandomInvoiceType(int nullThreshold) {
		return gt(nullThreshold)
			?InvoiceType.values()[faker.random().nextInt(InvoiceType.values().length)]
			:null;
	}
	
	public static MediaType getRandomMediaType() {
		return getRandomMediaType (-1);
	}
	public static MediaType getRandomMediaType(int nullThreshold) {
		return gt(nullThreshold)
			?MediaType.values()[faker.random().nextInt(MediaType.values().length)]
			:null;
	}
	public static PayMethodType getRandomPayMethodType() {
		return getRandomPayMethodType (-1);
	}
	public static PayMethodType getRandomPayMethodType(int nullThreshold) {
		return gt(nullThreshold)
			?PayMethodType.values()[faker.random().nextInt(PayMethodType.values().length)]
			:null;
	}
	public static RectificationType getRandomRectificationType() {
		return getRandomRectificationType (-1);
	}
	public static RectificationType getRandomRectificationType(int nullThreshold) {
		return gt(nullThreshold)
			?RectificationType.values()[faker.random().nextInt(RectificationType.values().length)]
			:null;
	}
	
	public static VatSummaryType getRandomVatSummaryType() {
		return getRandomVatSummaryType (-1);
	}
	public static VatSummaryType getRandomVatSummaryType(int nullThreshold) {
		return gt(nullThreshold)
			?VatSummaryType.values()[faker.random().nextInt(VatSummaryType.values().length)]
			:null;
	}
	
	public static RegistryStatus getRandomRegistryStatus() {
		return getRandomRegistryStatus (-1);
	}
	public static RegistryStatus getRandomRegistryStatus(int nullThreshold) {
		return gt(nullThreshold)
			?RegistryStatus.values()[faker.random().nextInt(RegistryStatus.values().length)]
			:null;
	}
	
	public static CarrierStatus getRandomCarrierStatus() {
		return getRandomCarrierStatus (-1);
	}
	public static CarrierStatus getRandomCarrierStatus(int nullThreshold) {
		return gt(nullThreshold)
			?CarrierStatus.values()[faker.random().nextInt(CarrierStatus.values().length)]
			:null;
	}
	
	public static SecurityLevel getRandomSecurityLevel() {
		return getRandomSecurityLevel (-1);
	}
	public static SecurityLevel getRandomSecurityLevel(int nullThreshold) {
		return gt(nullThreshold)
			?SecurityLevel.values()[faker.random().nextInt(SecurityLevel.values().length)]
			:null;
	} 
	public static StreetType getRandomStreetType() {
		return getRandomStreetType (-1);
	}
	public static StreetType getRandomStreetType(int nullThreshold) {
		return gt(nullThreshold)
			?StreetType.values()[faker.random().nextInt(StreetType.values().length)]
			:null;
	}
	
	public static ProductStatus getRandomProductStatus() {
		return getProductStatus(-1);
	}
	public static ProductStatus getProductStatus(int nullThreshold) {
		return gt(nullThreshold)
			?ProductStatus.values()[faker.random().nextInt(ProductStatus.values().length)]
			:null;
	}
	
	public static void generateRandomInvoices(final AONContext ctx, Date testDate, int times ) {
		int year = AonDateUtils.getYear( testDate );
		for (int count = 0; count < times; count++) {
			Invoice invoice = null;
			if (AonRandom.gt(90)) {
				invoice = AonRandom.generateRandomRetentionInvoice(ctx,AonRandom.getRandomWithholdingType());
			} else {
				InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( AonRandom.getRandomYearDay( year ) );
				invoice = InvoiceFaker.getRandom(params);
			}
			invoice = AON.insertInvoice(ctx,invoice);
			AccountingInvoiceDAO.saveFinances(ctx, invoice);
			if (invoice.isVatAccrualPayment() && AonCollectionUtils.isNotEmpty(invoice.getFinances()) && AonRandom.gt(40)) {
				Finance finance = AonRandom.get(invoice.getFinances());
				if (finance != null && finance.getId() != null) {
					FinanceTracking tracking = new FinanceTracking()
							.setDomain(invoice.getDomain())
							.setFinance(finance)
							.setTrackingDate( AonRandom.getFutureDate(finance.getDueDate()) )
							.setAmount(finance.getAmount() );
					FinanceTrackingDAO.pay(ctx, tracking);
				}
			}
			System.out.println(MessageFormat.format("\t\t [{0}% {1}{2}]"
					,AonStringUtils.rightPad(AonMathUtils.round( count * 100 / times,2), 6)
					,AonStringUtils.repeat("-", count)
					,AonStringUtils.repeat(" ", times - count)
					));
			count++;
		}
		System.out.println(MessageFormat.format("\t\t [100.00% {0}]"
				,AonStringUtils.repeat("-", times)
				));
	}

	
	

	public static Invoice generateRandomRetentionInvoice(final AONContext ctx, WithholdingType withholdingType) {
		Invoice inv = withholdingType.visit(new IWithholdingTypeVisitor<Invoice>() {

			@Override public Invoice visitProfessional(Invoice i) { return getRetentionInvoice( WithholdingType.PROFESSIONAL);   }
			@Override public Invoice visitRenting(Invoice t) { return getRetentionInvoice( WithholdingType.RENTING);   }
			@Override public Invoice visitMovableCapital(Invoice t) { return getRetentionInvoice( WithholdingType.MOVABLE_CAPITAL);   }
			@Override public Invoice visitTransportOperator(Invoice t) { return getRetentionInvoice( WithholdingType.TRANSPORT_OPERATOR);   }
			@Override public Invoice visitM190G02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_G_02);   }
			@Override public Invoice visitM190G03(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_G_03);   }
			@Override public Invoice visitM190H02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_H_02);   }
			@Override public Invoice visitM190H03(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_H_03);   }
			@Override public Invoice visitM190I01(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_I_01);   }
			@Override public Invoice visitM190I02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_I_02);   }
			@Override public Invoice visitM190J(Invoice t)    { return getRetentionInvoice( WithholdingType.M190_J   );   }
			@Override public Invoice visitM190K01(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_K_01);   }
			@Override public Invoice visitM190K03(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_K_03);   }
			@Override public Invoice visitM190K02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_K_02);   }
			@Override public Invoice visitM193C1(Invoice t)   { return getRetentionInvoice( WithholdingType.M193_C1);     }
			@Override public Invoice visitM193C2(Invoice t)   { return getRetentionInvoice( WithholdingType.M193_C2);     }
			@Override public Invoice visitM193C3(Invoice t)   { return getRetentionInvoice( WithholdingType.M193_C3);     }
			@Override public Invoice visitM190F01(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_F_01);   }
			@Override public Invoice visitM190F021(Invoice t) { return getRetentionInvoice( WithholdingType.M190_F_02_1); }
			@Override public Invoice visitM190F022(Invoice t) { return getRetentionInvoice( WithholdingType.M190_F_02_2); }
			
			@Override
			public Invoice visitFarmer(Invoice t) {
				InvoiceFakerParams params = new InvoiceFakerParams(ctx)
					.setIssueDate(AonRandom.getYearDay(new Date()));
				return InvoiceFaker.getPurchaseFarmerRetention(params);
			}

			private Invoice getRetentionInvoice( final WithholdingType wt) {
				return InvoiceFaker.getRetentionInvoice( ctx, wt);
			}
			
		},null);
		return inv;
	}

	public static Invoice generateRandomSalesRetentionInvoice(final AONContext ctx, final Occam occam, WithholdingType withholdingType) {
		Invoice inv = withholdingType.visit(new IWithholdingTypeVisitor<Invoice>() {

			@Override public Invoice visitProfessional(Invoice i) { return getRetentionInvoice( WithholdingType.PROFESSIONAL);   }
			@Override public Invoice visitRenting(Invoice t) { return getRetentionInvoice( WithholdingType.RENTING);   }
			@Override public Invoice visitMovableCapital(Invoice t) { return getRetentionInvoice( WithholdingType.MOVABLE_CAPITAL);   }
			@Override public Invoice visitTransportOperator(Invoice t) { return getRetentionInvoice( WithholdingType.TRANSPORT_OPERATOR);   }
			@Override public Invoice visitM190G02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_G_02);   }
			@Override public Invoice visitM190G03(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_G_03);   }
			@Override public Invoice visitM190H02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_H_02);   }
			@Override public Invoice visitM190H03(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_H_03);   }
			@Override public Invoice visitM190I01(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_I_01);   }
			@Override public Invoice visitM190I02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_I_02);   }
			@Override public Invoice visitM190J(Invoice t)    { return getRetentionInvoice( WithholdingType.M190_J   );   }
			@Override public Invoice visitM190K01(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_K_01);   }
			@Override public Invoice visitM190K03(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_K_03);   }
			@Override public Invoice visitM190K02(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_K_02);   }
			@Override public Invoice visitM193C1(Invoice t)   { return getRetentionInvoice( WithholdingType.M193_C1);     }
			@Override public Invoice visitM193C2(Invoice t)   { return getRetentionInvoice( WithholdingType.M193_C2);     }
			@Override public Invoice visitM193C3(Invoice t)   { return getRetentionInvoice( WithholdingType.M193_C3);     }
			@Override public Invoice visitM190F01(Invoice t)  { return getRetentionInvoice( WithholdingType.M190_F_01);   }
			@Override public Invoice visitM190F021(Invoice t) { return getRetentionInvoice( WithholdingType.M190_F_02_1); }
			@Override public Invoice visitM190F022(Invoice t) { return getRetentionInvoice( WithholdingType.M190_F_02_2); }
			
			@Override
			public Invoice visitFarmer(Invoice t) {
				InvoiceFakerParams params = new InvoiceFakerParams(ctx)
					.setIssueDate(AonRandom.getYearDay(new Date()));
				return InvoiceFaker.getSalesFarmerRetention(params);
			}

			private Invoice getRetentionInvoice( final WithholdingType wt) {
				return InvoiceFaker.getSalesRetentionInvoice( ctx, occam, wt);
			}
			
		},null);
		return inv;
	}
	
	public static <T> T get(List<T> list) {
		if (AonCollectionUtils.isEmpty(list)) return null;
		return list.get( getInt(0, (list.size() - 1) ) );
	}
	
	public static InvoiceBreakdown getInvoiceBreakdown() {
		return new InvoiceBreakdown()
			.setId( integer( 30 ))
			.setDomain( integer( 30 ))
			.setInvoice(integer( 30 ))
			.setTaxType( getRandomTaxType() )
			.setBase( getDouble( 0, 10000, 4) )
			.setPercentage( getDouble( 0, 100, 2) )
			.setQuota( getDouble( 0, 10000, 2) )
			.setSurchargeQuota( getDouble( 0, 10000, 2) )
			.setDeductibleQuota( getDouble( 0, 10000, 2) )
			.setWithholdingType( getRandomWithholdingType() )
			.setVatDeductionType( getRandomVatDeductionType() )
		;
	}
	
	public static AccountingRegistry getAccountingRegistry(AONContext ctx) {
		AccountingRegistryType i = AccountingRegistryType.values()[faker.random().nextInt(AccountingRegistryType.values().length)];
		MutableObject<AccountingRegistry> ret = new MutableObject<>();
		i.visit(new AccountingRegistryTypeVisitor() {
			
			@Override
			public void visitUndedCreditor() {
				visitCreditor();
			}
			
			@Override
			public void visitSupplier() {
				Supplier c = AonRandom.getSupplier(ctx);
				ret.setValue( 
					AccountingRegistryDAO.getAccountingRegistries(ctx , p -> p.getIdProperty().eq(c.getId()) )
						.findFirst()
						.orElse(null));
			}
			
			@Override
			public void visitCustomer() {
				Customer c = AonRandom.getCustomer(ctx);
				ret.setValue( 
					AccountingRegistryDAO.getAccountingRegistries(ctx , p -> p.getIdProperty().eq(c.getId()) )
						.findFirst()
						.orElse(null));
			}
			
			@Override
			public void visitCreditor() {
				Creditor c = AonRandom.getCreditor(ctx);
				ret.setValue( 
					AccountingRegistryDAO.getAccountingRegistries(ctx , p -> p.getIdProperty().eq(c.getId()) )
						.findFirst()
						.orElse(null));
			}
		});
		return ret.getValue();
	}

	public static Integer getRandomInvoiceRegistry(AONContext ctx, InvoiceType type) {
		return type.visit(null, new IInvoiceTypeVisitor<Integer>() {
			@Override
			public Integer visitPurchase(Invoice invoice) {
				return SupplierDAO.getRandom(ctx, p -> p.getIdProperty().ge(0) ).getId();
			}

			@Override
			public Integer visitSales(Invoice invoice) {
				return CustomerDAO.getRandom(ctx, p -> p.getIdProperty().ge(0) ).getId();
			}

			@Override
			public Integer visitExpenses(Invoice invoice) {
				return CreditorDAO.getRandom(ctx, p -> p.getIdProperty().ge(0) ).getId();
			}

			@Override
			public Integer visitUndeductible(Invoice invoice) {
				return visitExpenses(invoice);
			}
		});
	}
	public static EnterpriseActivity getRandomActivity(AONContext ctx, AonConfiguration configuration) {
		EnterpriseActivity activity = null;
		if (AonCollectionUtils.isNotEmpty( configuration.getActivities())) {
			int w = AonRandom.getInt(0, 100);
			if (w > 10) {
				if (w > 80) {
					activity = Optional.of( configuration.getMainActivity() ).orElse(null);
				} else {
					int i = AonRandom.getInt(0, configuration.getActivities().size() - 1);
					activity = configuration.getActivities().get(i); 
				}
			}
		}
		return activity;
	}
	
	public static InvoiceMin getInvoiceMin(AONContext ctx) {
		return getInvoiceMin(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()));	
	}
	public static InvoiceMin getInvoiceMin(AONContext ctx, InvoiceFilter filter) {
		return InvoiceDAO.getRandom(ctx, filter);
	}
	
}

