package net.aonsolutions.occam.api.model;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class AonRandom {
	
	public static final Faker FAKER = new Faker( Locale.of("es"));
	private static final String DOCUMENT_REGEXP = "(\\d|[XYZ])\\d{7}[A-Z]";
	private static final int NOT_NULL = -1;
	
	private AonRandom() {
	}
	
	public static boolean bool()  {
		return gt(50);
	}
	public static boolean gt(int i)  {
		return integer(0, 100) > i;
	}
	
	public static int integer(int from , int to) {
		return FAKER.random().nextInt( from, to);
	}
    public static double percent() {
    	return getDouble(0, 100 , 0);
    }
    public static double percent( int proecision) {
    	return getDouble(0, 100 , proecision);
    }
    public static double getDouble( int from, int to) {
    	return getDouble(from, to , 2);
    }
    public static double getDouble( int from, int to, int precision ) {
    	double r = FAKER.random().nextDouble();
    	return AonMathUtils.round(from + ((to - from) * r), precision);
    }
    public static Double getDouble(int nullThreshold, int from, int to, int precision ) {
    	return ( gt(nullThreshold) )
        		?getDouble(from, to, precision)
        		:null;
    }
	
	
    public static String string( int maxLength ) {
    	return string(NOT_NULL,0,maxLength); 
    }
    public static String string( int nullThreshold, int maxLength ) {
    	return string(nullThreshold,0,maxLength);
    }
    public static String string( int nullThreshold, int minLength, int maxLength ) {
    	return ( gt(nullThreshold) )
       		?FAKER.lorem().characters(minLength, maxLength)
       		: null;
    }

    public static String uuid( int maxLength ) {
    	return AonStringUtils.substring(FAKER.internet().uuid(),0 ,maxLength);
    }
    
	public static String document() {
		return document(NOT_NULL);
	}
	public static String document(int nullThreshold) {
    	return ( gt(nullThreshold) )
        		?FAKER.regexify(DOCUMENT_REGEXP)
        		:null;
	}
    
	public static String companyName() {
		return FAKER.company().name();
	}
	
	public static String name( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
    		?AonStringUtils.abbreviate( FAKER.name().fullName(), maxLength)
    		:null;
    }
	
	public static String companyAlias() {
		return companyAlias(NOT_NULL); 
	}
	public static String companyAlias(int nullThreshold) {
    	return ( gt(nullThreshold) )
    		?FAKER.company().profession()
    		:null;
	}
	
    public static String alias( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
    		?AonStringUtils.abbreviate( FAKER.name().username(), maxLength)
    		:null;
    }
    
    public static String lorem( int nullThreshold, int maxLength ) {
    	return ( gt(nullThreshold) )
       		?FAKER.lorem().characters(0, maxLength)
       		:null;
    }
    
    public static String item( int nullThreshold,  int maxLength ) {
    	return ( gt(nullThreshold) )
       		?AonStringUtils.abbreviate(FAKER.book().title(),maxLength)
       		:null;
    }
    
	public static String internetUrl() {
		return FAKER.internet().url();
	}

	public static String phoneNumber() {
		return address(NOT_NULL);
	}
	public static String phoneNumber(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.phoneNumber().phoneNumber():null; 
	}
	
	public static String cellPhone() {
		return address(NOT_NULL);
	}
	public static String cellPhone(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.phoneNumber().cellPhone():null; 
	}

	public static String email() {
		return email(NOT_NULL);
	}
	public static String email(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.internet().safeEmailAddress():null; 
	}
    
    // ---------------------------------------------------
    // ------------------------------------------- ADDRESS
    // ---------------------------------------------------
	public static String address() {
		return address(NOT_NULL);
	}
	public static String address(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.address().streetName():null;
	}
	
	public static String addressNumber() {
		return addressNumber(NOT_NULL);
	}
	public static String addressNumber(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.address().streetAddressNumber():null;
	}
	
	public static String address2() {
		return address2(NOT_NULL);
	}
	public static String address2(int nullThreshold) {
		return AonRandom.gt(nullThreshold)?FAKER.address().secondaryAddress():null;
	}
	
	public static String zipCode() {
		return zipCode(NOT_NULL);
	}
	public static String zipCode(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.address().zipCode():null;
	}
	
	public static String city() {
		return city(NOT_NULL);
	}
	public static String city(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.address().city():null;
	}
	
	public static String streetSuffix() {
		return streetSuffix(NOT_NULL);
	}
	public static String streetSuffix(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.address().streetSuffix():null;
	}
	
	public static String citySuffix() {
		return citySuffix(NOT_NULL);
	}
	public static String citySuffix(int nullThreshold){
		return AonRandom.gt(nullThreshold)?FAKER.address().citySuffix():null;
	}
	
	public static String bic() {
		return bic(NOT_NULL);
	}
	public static String bic(int nullThreshold) {
		return AonRandom.gt(nullThreshold)?FAKER.address().city():null;
	}
	
    // ---------------------------------------------------
    // --------------------------------------------- DATES
    // ---------------------------------------------------
    public static Date getFutureDate( int threshold ) {
    	return getFutureDate(threshold, new Date());
    }
    public static Date getFutureDate( Date date ) {
    	return getFutureDate(0, date);
    }
    public static Date getFutureDate( int threshold, Date date ) {
    	return ( gt(threshold) )
    		?truncate( FAKER.date().future(100, TimeUnit.DAYS, date))
    		:null;
    }
    public static Date getPastDate( int threshold ) {
    	return ( gt(threshold) )
       		?truncate( FAKER.date().past(100, TimeUnit.DAYS, new Date()))
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
    
    // ---------------------------------------------------------- 
    // -------------------- COLLECTIONS, LISTS ,ARRAYS ----------
    // ---------------------------------------------------------- 
    public static <T> Optional<T> random(List<T> list){
    	if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.ofNullable(list.get(FAKER.random().nextInt(0, (list.size() - 1))));
    }	

    // ---------------------------------------------------------- 
    // ---------------------------------- RANDOM ENUMS ----------
    // ---------------------------------------------------------- 
	public static <E extends Enum<E>> E getEnum(Class<E> clazz) {
		return getEnum( clazz, false, null, e -> true);
	}
	public static <E extends Enum<E>> E getEnum(Class<E> clazz,int threshold) {
		return getEnum( clazz, gt(threshold), null, e -> true);
	}
	public static <E extends Enum<E>> E getEnumFiltered(Class<E> clazz,int threshold, Predicate<E> filter) {
		return getEnum( clazz, gt(threshold), null, filter );
	}
	public static <E extends Enum<E>> E getEnum(Class<E> clazz,boolean nullable) {
		return getEnum( clazz, nullable?gt(50):false , null, e -> true);
	}
	public static <E extends Enum<E>> E getEnum(Class<E> clazz, int threshold, E defaultValue) {
		return getEnum( clazz, gt(threshold), defaultValue, e -> true);
	}
	public static <E extends Enum<E>> E getEnum(Class<E> clazz,boolean nullable, E defaultValue, Predicate<E> filter) {
		if (nullable) return defaultValue;
		LinkedList<E> values = Arrays.stream(clazz.getEnumConstants())
			.filter(filter)
			.collect(Collectors.toCollection(LinkedList<E>::new));
		return AonCollectionUtils.isNotEmpty(values)
				? values.get(FAKER.random().nextInt(values.size()))
				:null;
	}

}
