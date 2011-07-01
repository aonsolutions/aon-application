/**
 * 
 */
package com.esferalia.aon.payroll.ctsql2mysql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pais;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.salary.enumeration.PaymentType;


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


	final static Hashtable<Integer, Integer> FIXED_GEOZONES= 
		new Hashtable<Integer, Integer>() {
			{
				put(55,51); // CEUTA(H)
				put(53,11); // JEREZ DE LA FRONTERA
				
			}
		};

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
	
	
	final static DateFormat DATE_FORMAT = 
		new SimpleDateFormat("EEEE, d MMMM yyyy", SPANISH);
	public static String format (Date date) {
		return DATE_FORMAT.format(date);
	}

	public static String format (String format, Object ...args) {
		return format != null ? String.format(format, args) : null;
	}
	
	public  static double toDouble(BigDecimal bigDecimal) {
		return bigDecimal != null  ? bigDecimal.doubleValue() : 0 ;
	}


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

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
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
				if ( map3.containsKey(key3) )
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

	protected static <K1,K2,K3, V> boolean contains( Map<K1, Map<K2,Map<K3,V>>> map, K1 key1, K2 key2, K2 key3  ){
		Map<K2,Map<K3,V>>  map2 = map.get(key1);
		if ( map2 == null ) 
			return false;
		Map<K3,V> map3  = map2.get(key2); 
		if ( map3 == null ) 
			return false;
		
		return map3.containsKey(key3);
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
	
	private Map<Integer, Set<String>> faxes  = 
		new HashMap<Integer, Set<String>>();

	private Map<String, Country> countries = 
		new HashMap<String, Country>();

	private Map<Integer, Set<String>> emails  = 
		new HashMap<Integer, Set<String>>();

	private Map<String, DocumentType> docTypes = 
		new HashMap<String, DocumentType>();

	private Map<Integer, Set<String>> telephones  = 
		new HashMap<Integer, Set<String>>();


	/**
	 * @param mysqlConnection
	 */
	public DefaultMysqlDB(Connection mysqlConnection)  
	throws SQLException{
		super(mysqlConnection);
		initCnaes();
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
	
	protected boolean isCnaeValid( Integer cnae ){
		return cnaes.get(cnae);
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
	
	
	
	protected int insertPerson(String document, DocumentType docType, Country docCountry, String name, String firstSurname,
			String secondSurname, String alias, Date birthDate, Country country , Short gender,
			Short maritalStatus, String socialSecurityNum) throws SQLException {
		Short type = enum2short(RegistryType.NATURAL);
		
		StringBuffer  fullName = new StringBuffer();
		if ( name != null ){
			fullName.append(name);
		}
		if ( firstSurname != null ){
			fullName.append(" ");
			fullName.append(firstSurname);
		}
		if ( secondSurname != null ){
			fullName.append(" ");
			fullName.append(secondSurname);
		}
		Integer registry = super.insertRegistry(
				document,  
				enum2short(docType),
				docCountry != null ? docCountry.getValue() : null,
				fullName.toString(), 
				alias, 
				type ,
				country != null ? country.getValue() : null,
				enum2short(SecurityLevel.OFFICIAL));
		super.insertPerson(registry, birthDate, gender, maritalStatus,socialSecurityNum, name, firstSurname, secondSurname);
		return registry;
	}
	
	
	protected int insertEnterprise(String document, Country docCountry, String name, Country country,  String alias, 
			Integer scope, Short status ) throws SQLException {
		
		Short type = enum2short(RegistryType.LEGAL);
		Integer registry =  super.insertRegistry(document, 
				enum2short(DocumentType.CIF), 
				docCountry != null ? docCountry.getValue() : null, 
				name, 
				alias, 
				type, 
				country != null ? country.getValue() : null,
				enum2short(SecurityLevel.OFFICIAL));
		super.insertEnterprise(registry, 
				scope, 
				null );		// TODO: ¿ Calendar ? 
		
		
		super.insertCustomer(registry,null, false, false,false,null,status,null,  scope,false, true,true);

		super.insertTarget(registry, null, (short) 0, false, false, (short)0, status);
		
		return registry;
	}


	protected void insertFax(Integer registry, Integer raddress, String fax )
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
				false,		// tecnical
				raddress);	
	}	

	protected void insertEmail(Integer registry, Integer raddress, String email)
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
				true,		// tecnical
				raddress);
	}
	
	protected void insertTelephone(Integer registry, Integer raddress, String telephone)
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
				false,		// not tecnical
				raddress);
	}
	
	protected boolean execute (String sql ) 
	throws SQLException {
		Statement stmt = mysqlConnection.createStatement();
		return stmt.execute(sql);
	}
	
	protected Integer getGeoZone(String provincia ){
		if ( provincia == null )
			return null;
		Integer geozone = Integer.valueOf(provincia);
		Integer fixedGeozone = FIXED_GEOZONES.get(geozone);
		return fixedGeozone != null ? fixedGeozone : geozone;
	}
	
	
	protected DocumentType getDocumentType(String oldCdg) {
		return docTypes.get(oldCdg);
	}

	@Override
	public void visitTipdoc(Tipdoc tipdoc) throws SQLException {
		String descripcion = tipdoc.getDescripcion();
		if ( descripcion != null ) {
			descripcion = descripcion.toUpperCase(); 
			if ( descripcion.contains("DNI"))
				docTypes.put(tipdoc.getCdg(), DocumentType.NIF);
			else if ( descripcion.contains("NUMERO")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.NIE);
			else if ( descripcion.contains("CIF")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.CIF);
			else if ( descripcion.contains("PASAPORTE")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.PASSPORT);
			else if ( descripcion.contains("TARJETA")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.COMMUNITY_CARD);
			else if ( descripcion.contains("PERMISO")) 
				docTypes.put(tipdoc.getCdg(), DocumentType.WORK_PERMIT);
		}
	}

		
	protected Country getCountry(String oldCdg) {
		Country country = countries.get(oldCdg);
		return country != null ? country : Country.ES;  
	}

	private Pattern overtimePattern = 
		Pattern.compile("HORAS\\s+EXTRA", Pattern.CASE_INSENSITIVE);
	private Pattern baseSalaryPattern = 
		Pattern.compile("SALARIO\\s+BASE", Pattern.CASE_INSENSITIVE);
	private Pattern compensationPattern = 
		Pattern.compile("INDEMNIZACION", Pattern.CASE_INSENSITIVE);
	private Pattern noticePattern = 
		Pattern.compile("INDEMNIZACION.*AVISO", Pattern.CASE_INSENSITIVE);
	private Pattern movingPattern = 
		Pattern.compile("INDEMNIZACION.*TRASLADO", Pattern.CASE_INSENSITIVE);
	private Pattern dismissalPattern = 
		Pattern.compile("INDEMNIZACION.*DESPIDO", Pattern.CASE_INSENSITIVE);

	
	public PaymentType getPaymentType(String description, String dinEsp, String tipCot) {
		
		if ( "5".equals(tipCot )) {
			return PaymentType.STRUCTURAL_HOURS;
		}

		if ( "6".equals(tipCot )) {
			return PaymentType.NON_STRUCTURAL_HOURS;
		}
		
		if ( "E".equalsIgnoreCase(dinEsp)){
			return  PaymentType.SALARY_IN_KIND;
		}

		if ( description == null  ) {
			return  PaymentType.SALARY_SUPPLEMENTS;
		}
		if (baseSalaryPattern.matcher(description).find()) {
			return  PaymentType.BASE_SALARY;
		}else if (overtimePattern.matcher(description).find()) {
			return  PaymentType.NON_STRUCTURAL_HOURS;
		}else if (noticePattern.matcher(description).find()) {
			return  PaymentType.MOVING_COMPENSATION;
		}else if (movingPattern.matcher(description).find()) {
			return  PaymentType.MOVING_COMPENSATION;
		}else if (dismissalPattern.matcher(description).find()) {
			return  PaymentType.MOVING_COMPENSATION;
		}else if (compensationPattern.matcher(description).find()) {
			return  PaymentType.COMPENSATION_OR_PREPAID_EXPENSES;
		}
		return  PaymentType.SALARY_SUPPLEMENTS;

	}

	public String getFunction(BigDecimal importe, BigDecimal impuni, BigDecimal unidades) {
		if ( impuni != null && impuni.doubleValue() != 0 ) {
			if ( unidades != null ) {
				return String.format("%.3f * %.3f", impuni, unidades );
			} 
		}
		return String.format("%.3f", importe );
	}
	
	public Integer getDeductionConceptId(String code) throws SQLException {
		ResultSet rs = null; 
		PreparedStatement stmt = null ;
		try {
			stmt = mysqlConnection.prepareStatement("SELECT id FROM deduction_concept WHERE code = ?");
			stmt.setString(1, code);
			rs = stmt.executeQuery();
			if ( rs.next() ){
				return rs.getInt("id");
			}
			else {
				return null;
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}
	
	public Integer getPaymentConceptId(String code) throws SQLException {
		ResultSet rs = null; 
		PreparedStatement stmt = null ;
		try {
			stmt = mysqlConnection.prepareStatement("SELECT id FROM payment_concept WHERE code = ?");
			stmt.setString(1, code);
			rs = stmt.executeQuery();
			if ( rs.next() ){
				return rs.getInt("id");
			}
			else {
				return null;
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}

	public Integer getBankId(String code) throws SQLException {
		ResultSet rs = null; 
		PreparedStatement stmt = null ;
		try {
			stmt = mysqlConnection.prepareStatement("SELECT id FROM bank WHERE code = ?");
			stmt.setString(1, code);
			rs = stmt.executeQuery();
			if ( rs.next() ){
				return rs.getInt("id");
			}
			else {
				return null;
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}
	
	public static boolean is9999 ( Date date ) {
		if ( date == null )
			return false;
		int year =  date.getYear() + 1900;
		return year == 9999; 
	}
	
}
