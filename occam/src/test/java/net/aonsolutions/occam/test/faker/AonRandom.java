package net.aonsolutions.occam.test.faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.occam.api.constants.Administration;
import net.aonsolutions.occam.api.constants.AonApp;
import net.aonsolutions.occam.api.constants.AonLanguage;
import net.aonsolutions.occam.api.constants.AonModule;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.RectificationType;
import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.constants.WithholdingTypeGroup;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonDateUtils;

public class AonRandom {
	
	private static Faker faker = Faker.instance(Locale.of("es"));
	private static final int REQUIRED = -1;
	
    public static boolean gt( int threshold) {
		return faker.random().nextInt(0,100) >= threshold;
	}
    public static String uuid( int maxLength ) {
    	return AonStringUtils.substring(faker.internet().uuid(),0 ,maxLength);
    }
    public static String domainName() {
    	return faker.internet().domainName();
    }

    public static String string( int nullThreshold, int minLength, int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(minLength, maxLength)
        		: null;
    }
    public static String string( int maxLength ) {
    	return faker.lorem().characters(REQUIRED, maxLength);
    }
    public static String string( int nullThreshold,  int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(0, maxLength)
        		:null;
    }
    public static Integer integer( ) {
    	return integer(REQUIRED);
    }

    	
    public static Integer integer( int nullThreshold ) {
    	return ( gt(nullThreshold) )
        		?Integer.valueOf( getInt(0, Integer.MAX_VALUE-1) )
        		:null;
    }
    public static Integer integer( int nullThreshold,  int max ) {
    	return ( gt(nullThreshold) )
        		?Integer.valueOf( getInt(0, 20) )
        		:null;
    }
    public static String lorem( int maxLength ) {
    	return lorem(REQUIRED, maxLength);
    }
    public static String lorem( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(1, maxLength)
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
    	return AonNumberUtils.round(from + ((to - from) * r), precision);
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
    	return Date.from(LocalDate.now().plusDays(REQUIRED).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date tomorrow( ) {
    	return Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date getRangeDate( Date start, Date end ) {
    	return faker.date().between(start, end);
    }
    public static Date getYearDay() {
    	return getYearDay(today());
    }
    public static Date getYearDay( Date date ) {
    	return faker.date().between(AonDateUtils.getYearFirstDay(date), AonDateUtils.getYearLastDay(date));
    }
    public static Date getFutureDate( ) {
    	return getFutureDate(new Date());
    }
    public static Date getFutureDate( Date date ) {
    	return getFutureDate(REQUIRED, new Date());
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

	public static <T> T get(List<T> list) {
		if (AonCollectionUtils.isEmpty(list)) return null;
		return list.get( getInt(0, (list.size() - 1) ) );
	}
	
	public static Administration getAdministration() {
		return getAdministration(REQUIRED).get();
	}
	public static Optional<Administration> getAdministration(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(Administration.values()[faker.random().nextInt(Administration.values().length)])
			:Optional.empty();
	}

	public static DomainType getDomainType() {
		return getDomainType(REQUIRED).get();
	}
	public static Optional<DomainType> getDomainType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(DomainType.values()[faker.random().nextInt(DomainType.values().length)])
			:Optional.empty();
	}
	
	public static AonStatus getAonStatus() {
		return getAonStatus(REQUIRED).get();
	}
	public static Optional<AonStatus> getAonStatus(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AonStatus.values()[faker.random().nextInt(AonStatus.values().length)])
			:Optional.empty();
	}
	
	public static Country getCountry() {
		return getCountry(REQUIRED).get();
	}
	public static Optional<Country> getCountry(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(Country.values()[faker.random().nextInt(Country.values().length)])
			:Optional.empty();
	}

	public static DocumentType getDocumentType() {
		return getDocumentType(REQUIRED).get();
	}
	public static Optional<DocumentType> getDocumentType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(DocumentType.values()[faker.random().nextInt(DocumentType.values().length)])
			:Optional.empty();
	}

	public static InvoiceType getInvoiceType() {
		return getInvoiceType(REQUIRED).get();
	}
	public static Optional<InvoiceType> getInvoiceType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(InvoiceType.values()[faker.random().nextInt(InvoiceType.values().length)])
			:Optional.empty();
	}

	public static SecurityLevel getSecurityLevel() {
		return getSecurityLevel(REQUIRED).get();
	}
	public static Optional<SecurityLevel> getSecurityLevel(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(SecurityLevel.values()[faker.random().nextInt(SecurityLevel.values().length)])
			:Optional.empty();
	}
	
	public static StreetType getStreetType() {
		return getStreetType(REQUIRED).get();
	}
	public static Optional<StreetType> getStreetType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(StreetType.values()[faker.random().nextInt(StreetType.values().length)])
			:Optional.empty();
	}

	public static AonApp getAonApp() {
		return getAonApp(REQUIRED).get();
	}
	public static Optional<AonApp> getAonApp(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AonApp.values()[faker.random().nextInt(AonApp.values().length)])
			:Optional.empty();
	}

	public static AonModule getAonModule() {
		return getAonModule(REQUIRED).get();
	}
	public static Optional<AonModule> getAonModule(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AonModule.values()[faker.random().nextInt(AonModule.values().length)])
			:Optional.empty();
	}

	public static AonLanguage getAonLanguage() {
		return getAonLanguage(REQUIRED).get();
	}
	public static Optional<AonLanguage> getAonLanguage(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AonLanguage.values()[faker.random().nextInt(AonLanguage.values().length)])
			:Optional.empty();
	}

	public static AppParam getAppParam() {
		return getAppParam(REQUIRED).get();
	}
	public static Optional<AppParam> getAppParam(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AppParam.values()[faker.random().nextInt(AppParam.values().length)])
			:Optional.empty();
	}
	
	public static AccountingPeriodStatus getAccountingPeriodStatus() {
		return getAccountingPeriodStatus(REQUIRED).get();
	}
	public static Optional<AccountingPeriodStatus> getAccountingPeriodStatus(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AccountingPeriodStatus.values()[faker.random().nextInt(AccountingPeriodStatus.values().length)])
			:Optional.empty();
	}
	
	public static RectificationType getRectificationType() {
		return getRectificationType(REQUIRED).get();
	}
	public static Optional<RectificationType> getRectificationType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(RectificationType.values()[faker.random().nextInt(RectificationType.values().length)])
			:Optional.empty();
	}

	public static TransactionType getTransactionType() {
		return getTransactionType(REQUIRED).get();
	}
	public static Optional<TransactionType> getTransactionType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(TransactionType.values()[faker.random().nextInt(TransactionType.values().length)])
			:Optional.empty();
	}

	public static TaxType getTaxType() {
		return getTaxType(REQUIRED).get();
	}
	public static Optional<TaxType> getTaxType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(TaxType.values()[faker.random().nextInt(TaxType.values().length)])
			:Optional.empty();
	}

	public static WithholdingTypeGroup getWithholdingTypeGroup() {
		return getWithholdingTypeGroup(REQUIRED).get();
	}
	public static Optional<WithholdingTypeGroup> getWithholdingTypeGroup(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(WithholdingTypeGroup.values()[faker.random().nextInt(WithholdingTypeGroup.values().length)])
			:Optional.empty();
	}

	public static WithholdingType getWithholdingType() {
		return getWithholdingType(REQUIRED).get();
	}
	public static Optional<WithholdingType> getWithholdingType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(WithholdingType.values()[faker.random().nextInt(WithholdingType.values().length)])
			:Optional.empty();
	}

	public static VatDeductionType getVatDeductionType() {
		return getVatDeductionType(REQUIRED).get();
	}
	public static Optional<VatDeductionType> getVatDeductionType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(VatDeductionType.values()[faker.random().nextInt(VatDeductionType.values().length)])
			:Optional.empty();
	}

	public static InvoiceSource getInvoiceSource() {
		return getInvoiceSource(REQUIRED).get();
	}
	public static Optional<InvoiceSource> getInvoiceSource(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(InvoiceSource.values()[faker.random().nextInt(InvoiceSource.values().length)])
			:Optional.empty();
	}
}


