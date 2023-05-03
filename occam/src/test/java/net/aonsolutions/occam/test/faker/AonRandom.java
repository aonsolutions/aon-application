package net.aonsolutions.occam.test.faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.SecurityLevel;

public class AonRandom {
	private static Faker faker = Faker.instance(Locale.of("es"));
	
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
    	return faker.lorem().characters(-1, maxLength);
    }
    public static String string( int nullThreshold,  int maxLength ) {
    	return ( gt(nullThreshold) )
        		?faker.lorem().characters(0, maxLength)
        		:null;
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
    public static Date getRangeDate( Date start, Date end ) {
    	return faker.date().between(start, end);
    }
    public static Date getYearDay( Date date ) {
    	return faker.date().between(AonDateUtils.getYearFirstDay(date), AonDateUtils.getYearLastDay(date));
    }
    public static Date getFutureDate( ) {
    	return getFutureDate(new Date());
    }
    public static Date getFutureDate( Date date ) {
    	return getFutureDate(-1, new Date());
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
	
	public static Optional<DomainType> getDomainType() {
		return getDomainType(-1);
	}
	public static Optional<DomainType> getDomainType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(DomainType.values()[faker.random().nextInt(DomainType.values().length)])
			:Optional.empty();
	}
	
	public static Optional<AonStatus> getAonStatus() {
		return getAonStatus(-1);
	}
	public static Optional<AonStatus> getAonStatus(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(AonStatus.values()[faker.random().nextInt(AonStatus.values().length)])
			:Optional.empty();
	}
	
	public static Optional<DocumentType> getDocumentType() {
		return getDocumentType(-1);
	}
	public static Optional<DocumentType> getDocumentType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(DocumentType.values()[faker.random().nextInt(DocumentType.values().length)])
			:Optional.empty();
	}

	public static Optional<InvoiceType> getInvoiceType() {
		return getInvoiceType(-1);
	}
	public static Optional<InvoiceType> getInvoiceType(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(InvoiceType.values()[faker.random().nextInt(InvoiceType.values().length)])
			:Optional.empty();
	}

	public static Optional<SecurityLevel> getSecurityLevel() {
		return getSecurityLevel(-1);
	}
	public static Optional<SecurityLevel> getSecurityLevel(int nullThreshold) {
    	return gt(nullThreshold)
			?Optional.of(SecurityLevel.values()[faker.random().nextInt(SecurityLevel.values().length)])
			:Optional.empty();
	}
}

