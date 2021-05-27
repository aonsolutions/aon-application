/**
 * 
 */
package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.enumeration.Toolbar;
import com.code.aon.master.VersionManager;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Concept;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.AonSQLFile;

/**
 * @author rtrepiana
 * 
 */
@SuppressWarnings("serial")
public class DefaultMysqlDB extends AbstractDomainMysqlDB {

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

	protected static class CustomQuoteExpressionException extends Exception {
	}

	final static Locale SPANISH = new Locale("es");

	final static Hashtable<Integer, Integer> FIXED_GEOZONES = new Hashtable<Integer, Integer>() {
		{
			put(55, 51); // CEUTA(H)
			put(53, 11); // JEREZ DE LA FRONTERA

		}
	};

	final static Hashtable<String, String> NEW_GEOZONES = new Hashtable<String, String>() {
		{
			put("ORENSE", "OURENSE");
			put("CORUÑA", "A CORUÑA");
			put("LERIDA", "LLEIDA");
			put("BALEARES", "ILLES BALEARS");
		}
	};

	final static HashMap<String, Gender> GENDER = new HashMap<String, Gender>() {
		{
			put("V", Gender.MALE);
			put("H", Gender.FEMALE);
		}
	};

	final static HashMap<String, MaritalStatus> MARITAL_STATUS = new HashMap<String, MaritalStatus>() {
		{
			put("S", MaritalStatus.SINGLE);
			put("C", MaritalStatus.MARRIED);
			put("V", MaritalStatus.WIDOWED);
			put("D", MaritalStatus.SEPARATED);
			put("R", MaritalStatus.SINGLE);
			put("T", MaritalStatus.SINGLE);
		}
	};

	final static DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEEE, d MMMM yyyy", SPANISH);

	public static String format(Date date) {
		return DATE_FORMAT.format(date);
	}

	public static String format(String format, Object... args) {
		return format != null ? String.format(format, args) : null;
	}

	public static double toDouble(BigDecimal bigDecimal) {
		return bigDecimal != null ? bigDecimal.doubleValue() : 0;
	}

	public static double toDouble(BigDecimal... bigDecimals) {
		double ret = 0.00;
		for (BigDecimal bigDecimal : bigDecimals) {
			ret += bigDecimal != null ? bigDecimal.doubleValue() : 0.00;
		}
		return ret;
	}

	protected static <K, V> boolean save(Map<K, Set<V>> map, K key, V value) {
		Set<V> set;
		set = map.get(key);
		if (set != null) {
			if (set.contains(value))
				return false;
		} else {
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

	protected static <K1, K2, V> V get(Map<K1, Map<K2, V>> map, K1 key1, K2 key2) {
		Map<K2, V> map2 = map.get(key1);
		if (map2 == null)
			return null;
		return map2.get(key2);
	}

	protected static <K1, K2, V> int count(Map<K1, Map<K2, V>> map, K1 key1) {
		Map<K2, V> map2 = map.get(key1);
		return (map2 == null) ? 0 : map.size();
	}

	protected static <K1, K2, K3, V> boolean save(
			Map<K1, Map<K2, Map<K3, V>>> map, K1 key1, K2 key2, K3 key3, V value) {
		Map<K3, V> map3;
		Map<K2, Map<K3, V>> map2;

		map2 = map.get(key1);

		if (map2 != null) {
			map3 = map2.get(key2);
			if (map3 != null) {
				if (map3.containsKey(key3))
					return false;
			} else {
				map3 = new HashMap<K3, V>();
				map2.put(key2, map3);
			}
		} else {
			map2 = new HashMap<K2, Map<K3, V>>();
			map.put(key1, map2);
			map3 = new HashMap<K3, V>();
			map2.put(key2, map3);
		}

		map3.put(key3, value);
		return true;
	}

	protected static <K1, K2, K3, V> V get(Map<K1, Map<K2, Map<K3, V>>> map,
			K1 key1, K2 key2, K2 key3) {
		Map<K2, Map<K3, V>> map2 = map.get(key1);
		if (map2 == null)
			return null;
		Map<K3, V> map3 = map2.get(key2);
		if (map3 == null)
			return null;

		return map3.get(key3);
	}

	protected static <K1, K2, K3, V> boolean contains(
			Map<K1, Map<K2, Map<K3, V>>> map, K1 key1, K2 key2, K2 key3) {
		Map<K2, Map<K3, V>> map2 = map.get(key1);
		if (map2 == null)
			return false;
		Map<K3, V> map3 = map2.get(key2);
		if (map3 == null)
			return false;

		return map3.containsKey(key3);
	}

	protected static <K1, K2, V> boolean save(Map<K1, Map<K2, V>> map, K1 key1,
			K2 key2, V value) {
		Map<K2, V> map2;
		map2 = map.get(key1);
		if (map2 != null) {
			if (map2.containsKey(key2))
				return false;
		} else {
			map2 = new HashMap<K2, V>();
			map.put(key1, map2);
		}
		map2.put(key2, value);
		return true;
	}

	private BitSet cnaes;

	private Map<Integer, Set<String>> faxes = new HashMap<Integer, Set<String>>();

	private Map<String, Country> countries = new HashMap<String, Country>();

	private Map<Integer, Set<String>> emails = new HashMap<Integer, Set<String>>();

	private Map<String, DocumentType> docTypes = new HashMap<String, DocumentType>();

	private Map<Integer, Set<String>> telephones = new HashMap<Integer, Set<String>>();

	private Integer defaultDomain;

	/**
	 * @param mysqlConnection
	 */
	public DefaultMysqlDB(Connection mysqlConnection) throws SQLException {
		super(mysqlConnection);
		initCnaes();
	}

	private void initCnaes() throws SQLException {
		cnaes = new BitSet();
		Statement stmt = mysqlConnection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM cnae");
		while (rs.next()) {
			Integer id = rs.getInt("id");
			cnaes.set(id);
		}
		rs.close();
	}

	protected Integer getSystemDomain() {
		return 0;
	}

	@Override
	protected Integer getDefaultDomain() {
		return defaultDomain;
	}

	public void setDefaultDomain(Integer defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	protected boolean isCnaeValid(Integer cnae) {
		return cnaes.get(cnae);
	}

	protected Integer getCnae2009Id(String cnae2009) throws NullCNAEException,
			CNAENotFoundException {
		Integer cnae2009Id = null;

		if (cnae2009 == null || cnae2009.length() == 0) {
			throw new NullCNAEException();
		}

		try {
			cnae2009Id = Integer.parseInt(cnae2009);
			if (!isCnaeValid(cnae2009Id))
				throw new CNAENotFoundException();
		} catch (NumberFormatException e) {
			throw new CNAENotFoundException();
		}

		return cnae2009Id;
	}

	protected StreetType getStreetType(String tipoVia)
			throws NullStreetTypeException, StreetTypeNotFoundException {
		if (tipoVia == null) {
			throw new NullStreetTypeException();
		}
		StreetType streetTypes[] = StreetType.values();
		for (StreetType streetType : streetTypes) {
			String streeTypeName = streetType.getName(SPANISH);
			if (tipoVia.trim().equalsIgnoreCase(streeTypeName))
				return streetType;
		}
		throw new StreetTypeNotFoundException();
	}

	protected static Short enum2short(Enum<?> type) {
		return type == null ? null : (short) type.ordinal();
	}

	protected Gender getGender(String gender) throws NullGenderException,
			GenderNotFoundException {
		if (gender == null) {
			throw new NullGenderException();
		}
		String key = gender.trim().toUpperCase();
		if (!GENDER.containsKey(key)) {
			throw new GenderNotFoundException();
		}
		return GENDER.get(key);
	}

	protected MaritalStatus getMaritalStatus(String maritalStatus)
			throws NullMaritalStatusException, MaritalStatusNotFoundException {
		if (maritalStatus == null) {
			throw new NullMaritalStatusException();
		}
		String key = maritalStatus.trim().toUpperCase();
		if (!MARITAL_STATUS.containsKey(key)) {
			throw new MaritalStatusNotFoundException();
		}
		return MARITAL_STATUS.get(key);
	}

	protected int insertPerson(String document, DocumentType docType,
			Country docCountry, String name, String firstSurname,
			String secondSurname, String alias, Date birthDate,
			Country country, Short gender, Short maritalStatus,
			String socialSecurityNum) throws SQLException {
		Short type = enum2short(RegistryType.NATURAL);

		StringBuffer fullName = new StringBuffer();
		if (name != null) {
			fullName.append(name);
		}
		if (firstSurname != null) {
			fullName.append(" ");
			fullName.append(firstSurname);
		}
		if (secondSurname != null) {
			fullName.append(" ");
			fullName.append(secondSurname);
		}
		Integer registry = super.insertRegistry(document, enum2short(docType),
				docCountry != null ? docCountry.getValue() : null,
				fullName.toString(), alias, type,
				country != null ? country.getValue() : null,
				enum2short(SecurityLevel.OFFICIAL));
		super.insertPerson(registry, birthDate, gender, maritalStatus,
				socialSecurityNum, name, firstSurname, secondSurname);
		return registry;
	}

	protected int insertEnterprise(Integer domain, String document,
			Country docCountry, String name, Country country, String alias,
			Integer scope, Short status, DocumentType docType ) throws SQLException {

		Short type = enum2short(RegistryType.LEGAL);
		Integer registry = super.insertRegistry(domain, document,
				enum2short(docType),
				docCountry != null ? docCountry.getValue() : null, name, alias,
				type, country != null ? country.getValue() : null,
				enum2short(SecurityLevel.OFFICIAL));
		super.insertEnterprise(registry, domain, scope, null); // TODO: ¿
																// Calendar ?
		super.insertCustomer(
				registry, 
				domain, 
				null, 			//tariff, 
				false,			//surcharge, 
				false,			//withholding, 
				null,			//transaction, 
				status, 
				scope, 
				false,			//e_invoice, 
				null,			//invoicing_group, 
				true,			//project_grouped, 
				true,			//delivery_grouped, 
				true,			//delivery_valuated, 
				null,			//account, 
				null,			//TODO: creation_user, 
				null,			//TODO: creation_date, 
				null,			//modification_user, 
				null			//modification_date
				);
		
		super.insertTarget(
				registry, 
				domain, 
				null,			//tariff, 
				(short)0,		//advertising, 
				false,			//surcharge, 
				false,			//withholding, 
				(short)0,		//0transaction, 
				status, 
				scope, 
				null,			//TODO: creation_user, 
				null,			//TODO: creation_date, 
				null,			//modification_user, 
				null			//modification_date
				);


		return registry;
	}

	protected void insertFax(Integer registry, Integer raddress, String fax)
			throws SQLException, InvalidFaxException {
		if (fax == null)
			return;

		if (!fax.matches("[0-9]+"))
			throw new InvalidFaxException();

		if (!save(faxes, registry, fax))
			return;

		insertRmedia(registry, enum2short(MediaType.FAX), fax, null, true, // administrative
				false, // not commercial
				false, // tecnical
				raddress);
	}

	protected void insertEmail(Integer registry, Integer raddress, String email)
			throws SQLException, InvalidEmailException {
		if (email == null)
			return;

		if (!email.matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}"))
			throw new InvalidEmailException();

		if (!save(emails, registry, email))
			return;

		insertRmedia(registry, enum2short(MediaType.EMAIL), email, null, true, // administrative
				false, // not commercial
				true, // tecnical
				raddress);
	}

	protected void insertTelephone(Integer registry, Integer raddress,
			String telephone) throws SQLException, InvalidTelephoneException {
		if (telephone == null)
			return;

		if (!telephone.matches("[0-9]+"))
			throw new InvalidTelephoneException();

		if (!save(telephones, registry, telephone))
			return;

		short mediaType;
		if (telephone.trim().startsWith("9"))
			mediaType = (short) MediaType.FIXED_PHONE.ordinal();
		else
			mediaType = (short) MediaType.CELLULAR.ordinal();

		insertRmedia(registry, mediaType, telephone, null, true, // administrative
				false, // not commercial
				false, // not tecnical
				raddress);
	}

	protected boolean execute(String sql) throws SQLException {
		Statement stmt = mysqlConnection.createStatement();
		return stmt.execute(sql);
	}

	protected DocumentType getDocumentType(String oldCdg) {
		return docTypes.get(oldCdg);
	}

	@Override
	public void visitTipdoc(Tipdoc tipdoc) throws SQLException {
		String descripcion = tipdoc.getDescripcion();
		if (descripcion != null) {
			descripcion = descripcion.toUpperCase();
			if (descripcion.contains("DNI"))
				docTypes.put(tipdoc.getCdg(), DocumentType.NIF);
			else if (descripcion.contains("NUMERO"))
				docTypes.put(tipdoc.getCdg(), DocumentType.NIE);
			else if (descripcion.contains("CIF"))
				docTypes.put(tipdoc.getCdg(), DocumentType.CIF);
			else if (descripcion.contains("PASAPORTE"))
				docTypes.put(tipdoc.getCdg(), DocumentType.PASSPORT);
			else if (descripcion.contains("TARJETA"))
				docTypes.put(tipdoc.getCdg(), DocumentType.COMMUNITY_CARD);
			else if (descripcion.contains("PERMISO"))
				docTypes.put(tipdoc.getCdg(), DocumentType.WORK_PERMIT);
		}
	}

	protected Country getCountry(String oldCdg) {
		Country country = countries.get(oldCdg);
		return country != null ? country : Country.ES;
	}

	private Pattern overtimePattern = Pattern.compile("HORAS\\s+EXTRA",
			Pattern.CASE_INSENSITIVE);
	private Pattern baseSalaryPattern = Pattern.compile("SALARIO\\s+BASE",
			Pattern.CASE_INSENSITIVE);
	private Pattern compensationPattern = Pattern.compile("INDEMNIZACION",
			Pattern.CASE_INSENSITIVE);
	private Pattern noticePattern = Pattern.compile("INDEMNIZACION.*AVISO",
			Pattern.CASE_INSENSITIVE);
	private Pattern movingPattern = Pattern.compile("INDEMNIZACION.*TRASLADO",
			Pattern.CASE_INSENSITIVE);
	private Pattern dismissalPattern = Pattern.compile(
			"INDEMNIZACION.*DESPIDO", Pattern.CASE_INSENSITIVE);
	
	
	
//	public PaymentType getPaymentType(String description, String dinEsp,
//			String tipCot) {
//
//		if ("5".equals(tipCot)) {
//			return PaymentType.STRUCTURAL_HOURS;
//		}
//
//		if ("6".equals(tipCot)) {
//			return PaymentType.NON_STRUCTURAL_HOURS;
//		}
//
//		if ("E".equalsIgnoreCase(dinEsp)) {
//			return PaymentType.CRA_0013;
//		}
//
//		if (description == null) {
//			return PaymentType.CRA_0001;
//		}
//		if (baseSalaryPattern.matcher(description).find()) {
//			return PaymentType.CRA_0001;
//		} else if (overtimePattern.matcher(description).find()) {
//			return PaymentType.NON_STRUCTURAL_HOURS;
//		} else if (noticePattern.matcher(description).find()) {
//			return PaymentType.MOVING_COMPENSATION;
//		} else if (movingPattern.matcher(description).find()) {
//			return PaymentType.MOVING_COMPENSATION;
//		} else if (dismissalPattern.matcher(description).find()) {
//			return PaymentType.MOVING_COMPENSATION;
//		} else if (compensationPattern.matcher(description).find()) {
//			return PaymentType.CRA_0001;
//		}
//		return PaymentType.CRA_0001;
//
//	}

	public String getFunction(BigDecimal importe, BigDecimal impuni,
			BigDecimal unidades) {
		if (impuni != null && impuni.doubleValue() != 0) {
			if (unidades != null) {
				return String.format("%.3f * %.3f", impuni, unidades);
			}
		}
		return String.format("%.3f", importe);
	}

	public Integer getGeoZone(String provincia) throws SQLException {
		return getGeoZone(getDefaultDomain(), provincia);
	}

	public Integer getGeoZone(Integer domain, String provincia)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		flushGeozone();

		try {
			stmt = mysqlConnection
					.prepareStatement("SELECT id FROM geozone WHERE code = ? AND domain= ?");
			stmt.setString(1, provincia);
			stmt.setInt(2, domain);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getDeductionConceptId(Integer domain, ContextVariable var) 
			throws SQLException {
		return getDeductionConceptId(domain, var.getName());
	}

	public Integer getDeductionConceptId(Integer domain, String code)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		flushDeduction_concept();

		try {
			stmt = mysqlConnection
					.prepareStatement("SELECT id FROM deduction_concept WHERE code = ? AND domain= ?");
			stmt.setString(1, code);
			stmt.setInt(2, domain);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	

	public PaymentType getPaymentType(Nominadev nominadev, PaymentType def) throws SQLException{
		PaymentType paymentType =  getPaymentType(nominadev.getConcepto(), null);
		if ( paymentType != null)
			return paymentType;
		return paymentType != null ? paymentType : def ;
	}

	public PaymentType getPaymentType(Complemento complemento, PaymentType def) throws SQLException{
		PaymentType paymentType =  getPaymentType(complemento.getConcepto(), null);
		if ( paymentType != null && paymentType != PaymentType.CRA_0001  )
			return paymentType;
		return paymentType != null ? paymentType : def ;
	}

	public PaymentType getPaymentType(Percep percep, PaymentType def) throws SQLException{
		PaymentType paymentType =  getPaymentType(percep.getConcepto(), null);
		if ( paymentType != null && paymentType != PaymentType.CRA_0001  )
			return paymentType;
		paymentType = getPaymentType(percep.getCalculo(),  percep.getIndcom(), percep.getMes(), paymentType);
		return paymentType != null ? paymentType : def ;
	}

	public PaymentType getPaymentType(Percniv percniv, PaymentType def) throws SQLException{
		PaymentType paymentType =  getPaymentType(percniv.getConcepto(), null);
		if ( paymentType != null && paymentType != PaymentType.CRA_0001  )
			return paymentType;
		paymentType = getPaymentType(percniv.getCalculo(),  percniv.getIndcom(), percniv.getMes(), paymentType);
		return paymentType != null ? paymentType : def ;
	}

	private PaymentType getPaymentType(String concepto, PaymentType def) {
		if ( AonStringUtils.isBlank(concepto) )
			return def;
		
		PaymentType paymentType = 
				PaymentType.valueOf(String.format("CRA_%s", concepto));
		if ( paymentType == null )
			return def;
		return paymentType;
	}
	
	private PaymentType getPaymentType(String calculo, String indcom, Integer mes, PaymentType def){
		if ( "6".equals(calculo) && "P".equals(indcom) && mes != null && mes >= 1 && mes <= 12)
			return PaymentType.CRA_0004;
		
		return def;	
	}
	
	public String getQuote(Percniv percniv, Concept<PaymentType> concept) throws SQLException, CustomQuoteExpressionException{
		PaymentType paymentType = getPaymentType(percniv.getConcepto(), null);
		return getQuote(paymentType, concept);
	}

	public String getQuote(Percep percep, Concept<PaymentType> concept) throws SQLException, CustomQuoteExpressionException{
		PaymentType paymentType = getPaymentType(percep.getConcepto(), null);
		return getQuote(paymentType, concept);
	}

	private String getQuote(PaymentType paymentType, Concept<PaymentType> concept)
			throws CustomQuoteExpressionException {
		if ( paymentType == concept.type  )
			if ( paymentType.isBBCCExcluded() != paymentType.isBBCCIncluded() )
				return null; // PaymentType same as Concept and quote expression can't be customize, so use the one at Concept.
			else 
				throw new CustomQuoteExpressionException();
		
		if ( paymentType.isBBCCIncluded() &&  !paymentType.isBBCCExcluded() )
			return ContextVariable.ALL;
		
		if ( !paymentType.isBBCCIncluded() &&  paymentType.isBBCCExcluded() )
			return null;
		
		throw new CustomQuoteExpressionException();
	}

	public Concept<PaymentType> getSystemPaymentConcept(Complemento complemento)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		PaymentType paymentType = getPaymentType(complemento.getConcepto(), null);
		if ( paymentType == null )
			return null;
		
		try {
			stmt = mysqlConnection
					.prepareStatement("SELECT * FROM payment_concept WHERE domain= ? AND type = ? ");
			stmt.setInt(1, getSystemDomain());
			stmt.setInt(2, paymentType.ordinal());
			rs = stmt.executeQuery();
			if ( !rs.next() )
				return null;
			
			Concept<PaymentType>  concept = 
					new Concept<PaymentType>(
							rs.getInt("id"),
							rs.getString("code"),
							paymentType,
							rs.getString("description"),
							complemento.getTipcot()
							);
			
			if ( !rs.next() )
				return concept;

			return null;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getPaymentConceptId(Integer domain, String code)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		flushPayment_concept();

		try {
			stmt = mysqlConnection
					.prepareStatement("SELECT id FROM payment_concept WHERE code = ? AND domain= ?");
			stmt.setString(1, code);
			stmt.setInt(2, domain);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getBankId(Integer domain, String code) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
// TODO: Bank ?
//		flushBank();

		try {
			stmt = mysqlConnection
					.prepareStatement("SELECT id FROM bank WHERE code = ? AND domain=?");
			stmt.setString(1, code);
			stmt.setInt(2, domain);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getApplicationId(String application) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = mysqlConnection.prepareStatement("SELECT id "
					+ " FROM application" + " WHERE name=? ");
			stmt.setString(1, application);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getDomainApplicationId(Integer domain, Integer application)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		flushDomain_application();

		try {
			stmt = mysqlConnection.prepareStatement("SELECT id "
					+ " FROM domain_application"
					+ " WHERE application = ? AND domain = ? ");
			stmt.setInt(1, application);
			stmt.setInt(2, domain);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getProfileId(Integer domain, Integer application,
			String profile) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		flushProfile();

		try {
			stmt = mysqlConnection.prepareStatement("SELECT id "
					+ " FROM profile" + " WHERE domain IS NULL"
					+ " AND application = ?" + " AND name=?");
			stmt.setInt(1, application);
			stmt.setString(2, profile);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public Integer getApplicationRole(String role) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		flushProfile();

		try {
			stmt = mysqlConnection.prepareStatement("SELECT id "
					+ " FROM application_role"
					+ " WHERE role = (SELECT id FROM role WHERE name = ?) ");
			stmt.setString(1, role);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				return null;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	static final String APPS[] = { "aon-aio" };

	static final Module MODULES[] = {
	// "marketing"
	// ,"commercial"
	// ,"management"
	// ,"treasury"
	// ,"warehouse"
	// ,"groupware"
	// ,"accounting"
	// ,"fiscal"
	Module.PAYROLL
	// ,Module.DOCUMENT
	// ,"garage"
	};

	static final String PROFILES[] = { "Administrador" };

	public Integer newEnterpriseDomain(String name, String description, Integer parent, Integer scope)
			throws IOException, InterruptedException, SQLException {

		DomainType type = DomainType.ENTERPRISE;
		String subDomainSuffix = null;
		String owner = "ctsql2mysql";
		boolean userManagement = true;
		boolean enableHeredity = true;
		boolean domainManagement = false;
		boolean disableDomainManagement = !domainManagement;
		Integer maxDocumentSize = 0;
		Integer maxTotalDocumentSize = 0;
		Integer maxDefinedUsers = 0;
		boolean active = true;
		Integer domain = 
		insertDomain(
				name, 
				description, 
				parent, 
				enum2short(type), 
				scope, 
				subDomainSuffix, 
				enableHeredity, 
				domainManagement, 
				disableDomainManagement, 
				maxDocumentSize, 
				maxTotalDocumentSize, 
				maxDefinedUsers, 
				active, 
				owner, 
				null,			//TODO: creation_user, 
				null,			//TODO: creation_date, 
				null,			//modification_user, 
				null,			//modification_date
				null,			//expirationDate, 
				null,			//lastAccess_user, 
				null			//lastAccess_date
				);
		

		AuditLevel audit_level = AuditLevel.NONE;

		for (String app : APPS) {
			Integer application = getApplicationId(app);
			Integer domain_application = insertDomain_application(domain,
					application, active, enum2short(audit_level));
			for (Module module : MODULES) {
				insertDomain_application_module(domain, domain_application,
						enum2short(module));
			}

		}

		return domain;
	}

	public Integer newConsultancyDomain(String name, String user, String passwd, Integer scope)
			throws IOException, InterruptedException, SQLException,
			AonSQLException {

		Integer parent = null;
		DomainType type = DomainType.CONSULTANCY;
		String description = name;
		String subDomainSuffix = null;
		String owner = "ctsql2mysql";
		boolean userManagement = true;
		boolean enableHeredity = true;
		boolean domainManagement = true;
		boolean disableDomainManagement = !domainManagement;
		Integer maxDocumentSize = 0;
		Integer maxTotalDocumentSize = 0;
		Integer maxDefinedUsers = 0;
		boolean active = true;

		Integer domain = 
		insertDomain(
				name, 
				description, 
				parent, 
				enum2short(type), 
				scope, 
				subDomainSuffix, 
				enableHeredity, 
				domainManagement, 
				disableDomainManagement, 
				maxDocumentSize, 
				maxTotalDocumentSize, 
				maxDefinedUsers, 
				active, 
				owner, 
				null,			//TODO: creation_user, 
				null,			//TODO: creation_date, 
				null,			//modification_user, 
				null,			//modification_date
				null,			//expirationDate, 
				null,			//lastAccess_user, 
				null			//lastAccess_date
				);

		String login = user;
		Integer enterprise = null;
		Integer registry = null;
		Toolbar toolbar = Toolbar.GOOGLE;
		String password = digestPasswd(passwd);
		Date passwordExpiration = null;
		Integer user_id = 
		insertUser(
				domain, 
				name, 
				login, 
				enterprise, 
				registry, 
				active, 
				false,				//allowConcurrent, 
				password, 
				passwordExpiration, 
				enum2short(toolbar), 
				null,				//locale, 
				null,				//pageLimit, 
				null,				//linesPageLimit, 
				null,				//initAction, 
				null				//lastAccess
				);
		

		AuditLevel audit_level = AuditLevel.NONE;

		for (String app : APPS) {
			Integer application = getApplicationId(app);
			Integer domain_application = insertDomain_application(domain,
					application, active, enum2short(audit_level));
			for (Module module : MODULES) {

				insertDomain_application_module(domain, domain_application,
						enum2short(module));
			}

			Integer application_user = insertApplication_user(domain, user_id,
					domain_application, active);
			for (String prof : PROFILES) {
				Integer profile = getProfileId(null, application, prof);
				insertApplication_user_profile(domain, application_user,
						profile);
			}
		}

		// loads domain's default values.
		VersionManager versionManager = new VersionManager();
		URL sqlUrl = versionManager.getInsertScript("aon.domain");
		AonSQLFile sqlFile = new AonSQLFile(sqlUrl.openStream(),
				CharEncoding.ISO_8859_1);

		mysqlConnection.createStatement().execute(
				String.format("SET @Domain=%d", domain));

		while (sqlFile.ready()) {
			String stmt = sqlFile.getStatement();
			if (stmt == null)
				continue;
			if (stmt.matches("^\\s*COMMIT.*"))
				continue;
			if (stmt.matches("^\\s*BEGIN.*"))
				continue;
			if (stmt.matches("^\\s*SET\\s+FOREIGN_KEY_CHECKS\\s*=\\s*1.*"))
				continue;

			info(stmt);
			Statement s = mysqlConnection.createStatement();
			int result = s.executeUpdate(stmt);

			debug(result + "row(s) updated/inserted.");
		}

		syncIds();

		return domain;
	}

	public static boolean is9999(Date date) {
		if (date == null)
			return false;
		int year = date.getYear() + 1900;
		return year == 9999;
	}

	public static java.sql.Date getEndDate(java.sql.Date fecFin)
			throws SQLException {
		return DefaultMysqlDB.is9999(fecFin) ? null : fecFin;
	}

	public static String encode(String str) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.update(str.getBytes("UTF-8"));
		byte raw[] = digest.digest();
		return new String(Base64.encodeBase64(raw), "UTF-8"); // step 5
	}

	public static String digestPasswd(String passwd) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(passwd.getBytes("UTF-8"));
			byte raw[] = digest.digest();
			return new String(Base64.encodeBase64(raw), "UTF-8"); // step 5
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private static class OutputStreamThread extends Thread {
		InputStream is;
		OutputStream os;

		public OutputStreamThread(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}

		@Override
		public void run() {
			try {
				byte buf[] = new byte[256];
				int read;
				while ((read = is.read(buf)) != -1) {
					os.write(buf, 0, read);
				}
			} catch (IOException e) {
				// TODO make a callback on exception.
			}
		}
	}
	
	
	private static String normalize(String str) {
		
		return str
		.toUpperCase()
		.replaceAll("\\s","")
		.replaceAll("\\[([^\\]])*\\]","") 
		;
	}
	

}
