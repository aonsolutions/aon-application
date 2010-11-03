/**
 * 
 */
package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.code.aon.employee.enumeration.PaymentType;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;


/**
 * @author rtrepiana
 *
 */
@SuppressWarnings("serial")
public class DefaultMysqlDB extends AbstractMysqlDB {

	
	protected static class CNAENotFoundException extends Exception {
	}

	protected static class NullCNAEException extends Exception {
	}
	
	protected static class NullGeoZoneException extends Exception {
	}

	protected static class GeoZoneNotFoundException extends Exception {
	}

	protected static class NullStreetTypeException extends Exception {
	}

	protected static class StreetTypeNotFoundException extends Exception {
	}

	protected static class NullGenderException extends Exception {
	}

	protected static class GenderNotFoundException extends Exception {
	}

	protected static class NullMaritalStatusException extends Exception {
	}

	protected static class MaritalStatusNotFoundException extends Exception {
	}

	protected static class InvalidFaxException extends Exception {
	}

	protected static class InvalidEmailException extends Exception {
	}

	protected static class InvalidTelephoneException extends Exception {
	}

	final static Locale SPANISH	= new Locale("es");

	final static Hashtable<String, String> NEW_GEOZONES= 
		new Hashtable<String, String>() {
			{
				put("ORENSE","OURENSE");
				put("CORUÑA","A CORUÑA");
				put("LERIDA","LLEIDA");
				put("BALEARES","ILLES BALEARS");
			}
		};

	final static HashMap<String, Gender> GENDER= 
		new HashMap<String, Gender>() {
			{
				put("V",Gender.MALE);
				put("H",Gender.FEMALE);
			}
		};
			
	final static HashMap<String, MaritalStatus> MARITAL_STATUS= 
		new HashMap<String, MaritalStatus>() {
			{
				put("S",MaritalStatus.SINGLE);
				put("C",MaritalStatus.MARRIED);
				put("V",MaritalStatus.WIDOWED);
				put("D",MaritalStatus.SEPARATED);
				put("R",MaritalStatus.SINGLE);
				put("T",MaritalStatus.SINGLE); 
			}
		};


	protected static <K,V> boolean save( Map<K, Set<V>> map, K key, V value){
		Set<V> set ; 
		set = map.get(key);
		if ( set != null ){
			if ( set.contains(value) )
				return false;
		}
		else {
			set = new HashSet<V>();
			map.put(key, set);
		}
		set.add(value);
		return true;
	}


	protected static <K1,K2,V> V get( Map<K1, Map<K2,V>> map, K1 key1, K2 key2 ){
		Map<K2,V>  map2 = map.get(key1);
		if ( map2 == null ) 
			return null;
		return map2.get(key2);
	}

	protected static <K1,K2,K3,V> boolean save( Map<K1, Map<K2,Map<K3,V>>> map , K1 key1, K2 key2, K3 key3, V value){
		Map<K3,V> map3;
		Map<K2,Map<K3,V>> map2 ; 
		
		map2 = map.get(key1);
		
		if ( map2 != null ){
			map3 = map2.get(key2);
			if ( map3 != null ) {
				if ( map3.containsKey(key2) )
					return false;
			}
			else {
				map3 = new HashMap<K3, V>();
				map2.put(key2, map3);
			}
		}
		else {
			map2 = new HashMap<K2,Map<K3,V>>();
			map.put(key1, map2);
			map3 = new HashMap<K3, V>();
			map2.put(key2, map3);
		}
		
		map3.put(key3, value);
		return true;
	}

	protected static <K1,K2,K3, V> V get( Map<K1, Map<K2,Map<K3,V>>> map, K1 key1, K2 key2, K2 key3  ){
		Map<K2,Map<K3,V>>  map2 = map.get(key1);
		if ( map2 == null ) 
			return null;
		Map<K3,V> map3  = map2.get(key2); 
		if ( map3 == null ) 
			return null;
		
		return map3.get(key3);
	}

	protected static <K1,K2,V> boolean save( Map<K1, Map<K2,V>> map, K1 key1, K2 key2, V value){
		Map<K2,V> map2 ; 
		map2 = map.get(key1);
		if ( map2 != null ){
			if ( map2.containsKey(key2) )
				return false;
		}
		else {
			map2 = new HashMap<K2,V>();
			map.put(key1, map2);
		}
		map2.put(key2, value);
		return true;
	}

	private BitSet cnaes ;
	
	private Map<String, Integer> geozoneIds;
	
	private Map<Integer, Set<String>> faxes  = 
		new HashMap<Integer, Set<String>>();

	private Map<Integer, Set<String>> emails  = 
		new HashMap<Integer, Set<String>>();

	private Map<Integer, Set<String>> telephones  = 
		new HashMap<Integer, Set<String>>();

	/**
	 * @param mysqlConnection
	 */
	public DefaultMysqlDB(Connection mysqlConnection)  
	throws SQLException{
		super(mysqlConnection);
		initCnaes();
		initGeoZones();
	}
	
	private void initCnaes() 
	throws SQLException{
		cnaes = new BitSet();
		Statement stmt = mysqlConnection.createStatement();
		ResultSet rs  = stmt.executeQuery("SELECT id FROM cnae");
		while ( rs.next() ) {
			Integer id = rs.getInt("id");
			cnaes.set(id);
		}
		rs.close();
	}

	private void initGeoZones() 
	throws SQLException{
		geozoneIds = new HashMap<String, Integer>();
		Statement stmt = mysqlConnection.createStatement();
		ResultSet rs  = stmt.executeQuery("SELECT id, name FROM geozone");
		while ( rs.next() ) {
			Integer id = rs.getInt("id");
			String name = rs.getString("name");
			String key = name.trim().toUpperCase();
			geozoneIds.put(key, id);
		}
		rs.close();
	}
	
	
	protected boolean isCnaeValid( Integer cnae ){
		return cnaes.get(cnae);
	}
	
	protected Integer getGeoZoneId(String province) 
	throws NullGeoZoneException, GeoZoneNotFoundException {
		if ( province == null ) {
			throw new NullGeoZoneException();
		}
		String key = province.trim().toUpperCase();
		if ( NEW_GEOZONES.containsKey(key) ) {
			key = NEW_GEOZONES.get(key);
		}
		if ( ! geozoneIds.containsKey(key) ) {
			throw new GeoZoneNotFoundException();
		}
		return geozoneIds.get(key);
	}

	protected void debug(String format, Object ... args){
		LOGGER.debug(format, args);
	}

	protected void info(String format, Object ... args){
		LOGGER.info(format, args);
	}

	protected void warn(String format, Object ... args){
		LOGGER.warn(format, args);
	}

	protected void error(String format, Object ... args){
		LOGGER.error(format, args);
	}

	
	protected Integer getCnae2009Id ( String cnae2009) 
	throws NullCNAEException, CNAENotFoundException
	{
		Integer cnae2009Id = null ;

		if ( cnae2009 == null || cnae2009.length() == 0 ){
			throw new NullCNAEException();
		}
		
		try {
			cnae2009Id = Integer.parseInt(cnae2009);
			if ( ! isCnaeValid(cnae2009Id) ) 
				throw new CNAENotFoundException();
		}
		catch ( NumberFormatException e) {
			throw new CNAENotFoundException();
		}
		

		return cnae2009Id;
	}
	
	
	protected StreetType getStreetType(String tipoVia) 
	throws NullStreetTypeException, StreetTypeNotFoundException{
		if ( tipoVia == null ) {
			throw new NullStreetTypeException();
		}
		StreetType  streetTypes [] = StreetType.values();
		for (StreetType streetType : streetTypes) {
			String streeTypeName = streetType.getName(SPANISH);
			if ( tipoVia.trim().equalsIgnoreCase(streeTypeName) )
				return streetType;
		}
		throw new StreetTypeNotFoundException();
	}
	

	protected static Short enum2short(Enum<?> type) {
		return type == null ? null : (short ) type.ordinal();
	}

	protected Gender getGender(String gender) 
	throws NullGenderException, GenderNotFoundException{
		if ( gender == null ) {
			throw new NullGenderException();
		}
		String key = gender.trim().toUpperCase();
		if ( ! GENDER.containsKey(key) ) {
			throw new GenderNotFoundException();
		}
		return GENDER.get(key);
	}
	
	protected MaritalStatus getMaritalStatus(String maritalStatus) 
	throws NullMaritalStatusException, MaritalStatusNotFoundException{
		if ( maritalStatus == null ) {
			throw new NullMaritalStatusException();
		}
		String key = maritalStatus.trim().toUpperCase();
		if ( ! MARITAL_STATUS.containsKey(key) ) {
			throw new MaritalStatusNotFoundException();
		}
		return MARITAL_STATUS.get(key);
	}
	
	
	
	protected int insertPerson(String document, String name, String surname,
			String alias, Date birthDate, Short gender,
			Short maritalStatus, String socialSecurityNum) throws SQLException {
		
		Short type = enum2short(RegistryType.NATURAL);
		Integer registry = super.insertRegistry(document, name, surname, alias, type );
		super.insertPerson(registry, birthDate, gender, maritalStatus,socialSecurityNum);
		return registry;
	}
	
	
	protected int insertEnterprise(String document, String name, String alias, 
			Integer scope, Short status ) throws SQLException {
		
		Short type = enum2short(RegistryType.LEGAL);
		Integer registry =  super.insertRegistry(document, name, null, alias, type);
		super.insertEnterprise(registry, scope);
		super.insertCustomer(registry,null, false, false,false,null,status,null,  scope,false, true,true);
		
		return registry;
	}


	protected void insertFax(Integer registry, String fax)
	throws SQLException, InvalidFaxException
	{
		if ( fax == null )
			return;
		
		if ( !fax.matches("[0-9]+"))
			throw new InvalidFaxException();
		
		if ( ! save(faxes, registry, fax) ) 
			return;
		
		insertRmedia(registry, 
				enum2short(MediaType.FAX), 
				fax, 
				null, 
				true,		// administrative 
				false, 		// not commercial
				false);		// tecnical
	}

	protected void insertEmail(Integer registry, String email)
	throws SQLException, InvalidEmailException
	{
		if ( email == null )
			return;
		
		if ( ! email.matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}"))
			throw new InvalidEmailException();
		
		if ( ! save(emails, registry, email) ) 
			return;

		insertRmedia(registry, 
				enum2short(MediaType.EMAIL), 
				email, 
				null, 
				true,		// administrative 
				false, 		// not commercial
				true);		// tecnical
	}
	
	protected void insertTelephone(Integer registry, String telephone)
	throws SQLException, InvalidTelephoneException
	{
		if ( telephone == null )
			return;

		if ( !telephone.matches("[0-9]+"))
			throw new InvalidTelephoneException();
		
		if ( ! save(telephones, registry, telephone) ) 
			return;

		short mediaType ;
		if ( telephone.trim().startsWith("9"))
			mediaType = (short)MediaType.FIXED_PHONE.ordinal();
		else 
			mediaType = (short)MediaType.CELLULAR.ordinal();
		
		insertRmedia(registry, 
				mediaType, 
				telephone, 
				null, 
				true,		// administrative 
				false, 		// not commercial
				false);		// not tecnical
	}
	
	protected boolean execute (String sql ) 
	throws SQLException {
		Statement stmt = mysqlConnection.createStatement();
		return stmt.execute(sql);
	}
	
}
