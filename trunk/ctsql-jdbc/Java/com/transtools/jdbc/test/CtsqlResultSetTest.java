package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Calendar;

import junit.framework.TestCase;


public class CtsqlResultSetTest extends TestCase {

	protected Connection connection;
	protected String driver;
	protected String protocol;
	protected String host;
	protected String dbpath;
	protected String dbname;
	protected String url;
	protected String user;
	protected String password;
	private int port = 20010;

	private static final int SMALLINT = 1;
	private static final int CHAR = 2;
	private static final int INTEGER = 3;
	private static final int DECIMAL = 4;
	private static final int DATE = 5;
	private static final int TIME = 6;
	private static final int ERROR = 30;

	private static final String SMALLINT_NAME = "sm";
	private static final String CHAR_NAME = "ch";
	private static final String INTEGER_NAME = "int";
	private static final String DECIMAL_NAME = "dec";
	private static final String DATE_NAME = "dt";
	private static final String TIME_NAME = "tm";
	private static final String ERROR_NAME = "no exist";


	/**
	 * Objeto que almacenará los resultados obtenidos tras la ejecución de una query
	 * y que será el que se utilice como base para el testeo de los diferentes métodos.
	 */
	protected ResultSet resultSet;


	/**
	 * Constructor por defecto que debe tener esta clase por ser implementación de
	 * la clase TestCase. En este constructor se hace una llamada al método que
	 * inicializa la tabla con las diferentes conexiones que se pueden usar.
	 */
	public CtsqlResultSetTest(String name) {
		super(name);
	}


	/**
	   * Método perteneciente a la clase TestCase que es rescrito para definir los datos
	   * que se tomarán para la conexión con la base de datos.
	   */
	protected void setUp() {

		Data datos=new Data();

		driver = datos.driver;
		protocol = datos.protocol;
		host = datos.host;
		dbpath = datos.dbpath;
		dbname = datos.dbname;
		user = datos.user;
		password = datos.password;
		registerDriver();
		openConnection();
	}


	private void close_resultSet()
	{
		try{
				resultSet.close();
		}catch(SQLException ex){
			fail("CtsqlResultSet. Method: tearDown. The resultSet can not be closed");
		}catch(Throwable th){
			fail("CtsqlResultSet. Method: tearDown. A Throwable exception shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	protected void tearDown(){
		try{
				connection.close();
		}catch(SQLException ex){
			fail("CtsqlResultSet. Method: tearDown. The connection can not be closed");
		}catch(Throwable th){
			fail("CtsqlResultSet. Method: tearDown. A Throwable exception shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}


	/**
	 * Método que testea la recuperación de tipos de datos Short o Smallint del Ctsql.
	 */
	public void testGetShort() {
		Statement statement=null;
		ResultSet rs = null;
		short result = -1;

		try {
		statement = connection.createStatement();
		executeStatement("select * from basic");
			if (resultSet != null)
				result = resultSet.getShort(SMALLINT);
		} catch (SQLException ex) {
				fail("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown " +
							ex.getMessage());
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}

		assertEquals("Class: ResultSetTest. Method: testGetShort. The retrieved value is not correct ",
						new Short(result), new Short((short) 110));

//    Prueba: se accede por segunda vez al mismo dato
		try {
			if (resultSet != null)
				result = resultSet.getShort(SMALLINT);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getShort(ERROR);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getShort(INTEGER);  // la columna 3 es un entero
		} catch (SQLException ex) {
				fail("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown " +
							ex.getMessage());
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetShort. The retrieved value is not correct ",
						new Short(result), new Short((short) 1));

 // falta probar con columnas decimal y char



//		close_resultSet();
//		executeStatement("select * from basic");
//		try {
//			if (resultSet != null)
//				result = resultSet.getShort(DECIMAL);  // la columna 4 es un decimal
//		} catch (SQLException ex) {
//				fail("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown " +
//							ex.getMessage());
//		}catch (Throwable th) {
//			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
//     				th.getClass() + " has been thrown " + th.getMessage());
//		}
//		assertEquals("Class: ResultSetTest. Method: testGetShort. The retrieved value is not correct ",
//             			new Short(result), new Short((short) 22.56));
//


	}

//	Prueba: el parámetro de getShort ahora es un String
  public void testGetStringShort()
	{
		ResultSet rs = null;
		short result = -1;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getShort(SMALLINT_NAME);
		} catch (SQLException ex) {
				fail("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown " +
							ex.getMessage());
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}

		assertEquals("Class: ResultSetTest. Method: testGetShort. The retrieved value is not correct ",
						new Short(result), new Short((short) 110));

//    Prueba: se accede por segunda vez al mismo dato
		try {
			if (resultSet != null)
				result = resultSet.getShort(SMALLINT_NAME);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getShort(ERROR_NAME);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getShort(INTEGER_NAME);  // la columna 3 es un entero
		} catch (SQLException ex) {
				fail("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown " +
							ex.getMessage());
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetShort. The retrieved value is not correct ",
						new Short(result), new Short((short) 1));

 // falta probar con columnas decimal y char



//		close_resultSet();
//		executeStatement("select * from basic");
//		try {
//			if (resultSet != null)
//				result = resultSet.getShort(DECIMAL_NAME);  // la columna 4 es un decimal
//		} catch (SQLException ex) {
//				fail("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown " +
//							ex.getMessage());
//		}catch (Throwable th) {
//			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
//     				th.getClass() + " has been thrown " + th.getMessage());
//		}
//		assertEquals("Class: ResultSetTest. Method: testGetShort. The retrieved value is not correct ",
//             			new Short(result), new Short((short) 22.56));
//

	}
	/**
	 * Método que testea la recuperación de cadenas de la base de datos.
	 * No funciona.
	 */

	public void testGetString() {
		String result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getString(CHAR);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetString. An SQL Exception has been thrown " +
					ex.getMessage());
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetString. An inexpected Exception " +
					 th.getClass() + " has been thrown " + th.getMessage());
		}

		assertEquals("Class: ResultSetTest. Method: testGetString. The retrieved value is not correct ",
						result, new String("Maria"));
	}

  public void testGetStringString() {
		String result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getString(CHAR_NAME);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetString. An SQL Exception has been thrown " +
					ex.getMessage());
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetString. An inexpected Exception " +
					 th.getClass() + " has been thrown " + th.getMessage());
		}

		assertEquals("Class: ResultSetTest. Method: testGetString. The retrieved value is not correct ",
						result, new String("Maria"));
	}

	/**
	 *	Método que testea la recuperación de enteros de la base de datos.
	 */
	public void testGetInteger() {
		int result = -1;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getInt(INTEGER);

		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetInt. The retrieved value is not correct ",
					new Integer(result), new Integer(-2147483647));
//		Prueba: se accede por segunda vez al dato
	try {
			if (resultSet != null)
				result = resultSet.getInt(INTEGER);

		} catch (SQLException ex) {
			assertNotNull("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown ",ex);
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getInt(ERROR);

		} catch (SQLException ex) {
			assertNotNull("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown ",ex);
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getInt(SMALLINT); // La columna 1 es un smallint

		} catch (SQLException ex) {
		  fail("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetInt. The retrieved value is not correct ",
					new Integer(result), new Integer(110));
	}

	public void testGetStringInteger() {
		int result = -1;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getInt(INTEGER_NAME);

		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetInt. The retrieved value is not correct ",
					new Integer(result), new Integer(-2147483647));
//		Prueba: se accede por segunda vez al dato
	try {
			if (resultSet != null)
				result = resultSet.getInt(INTEGER_NAME);

		} catch (SQLException ex) {
			assertNotNull("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown ",ex);
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getInt(ERROR_NAME);

		} catch (SQLException ex) {
			assertNotNull("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown ",ex);
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		close_resultSet();
		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getInt(SMALLINT_NAME); // La columna 1 es un smallint

		} catch (SQLException ex) {
		  fail("Class: ResultSetTest. Method: testGetInt. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetInt. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetInt. The retrieved value is not correct ",
					new Integer(result), new Integer(110));
	}


	/**
	 * Método que testea la recuperación de decimales de la base de datos.
	 */
	public void testGetDecimal() {
		double result = -1.0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getDouble(DECIMAL);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetDouble. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetDouble. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetDouble. The retrieved value is not correct ",
					new Double(result), new Double(22.56));
	}

	public void testGetStringDecimal() {
		double result = -1.0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getDouble(DECIMAL_NAME);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetDouble. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetDouble. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
		assertEquals("Class: ResultSetTest. Method: testGetDouble. The retrieved value is not correct ",
					new Double(result), new Double(22.56));
	}

	/**
	 * Método que testea la recuperación de bigDecimal de la base de datos.
	 */
	public void testGetBigDecimal() {
		java.math.BigDecimal result = new java.math.BigDecimal(-1.0);

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getBigDecimal(DECIMAL);
		} catch (SQLException ex) {
			assertNotNull("Class: ResultSetTest. Method: testGetDouble. An SQL Exception has been thrown ",ex);
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetDouble. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetStringBigDecimal() {
		java.math.BigDecimal result = new java.math.BigDecimal(-1.0);

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getBigDecimal(DECIMAL_NAME);
		} catch (SQLException ex) {
			assertNotNull("Class: ResultSetTest. Method: testGetDouble. An SQL Exception has been thrown ",ex);
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetDouble. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	/**
	 * Método que recupera fechas de la base de datos.
	 */
	public void testGetDate() {
		java.sql.Date result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getDate(DATE);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetDate. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetDate. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(result);

		assertEquals("Class: ResultSetTest. Method: testGetDate. The retrieved day value is not correct ",new Integer(cal.get(Calendar.DATE)),new Integer(14));
		assertEquals("Class: ResultSetTest. Method: testGetDate. The retrieved month value is not correct ",new Integer(cal.get(Calendar.MONTH)),new Integer(5));
		assertEquals("Class: ResultSetTest. Method: testGetDate. The retrieved year value is not correct ",new Integer(cal.get(Calendar.YEAR)),new Integer(2000));

	}

	public void testGetStringDate() {
		java.sql.Date result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getDate(DATE_NAME);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetDate. An SQL Exception has been thrown " +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetDate. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(result);

		assertEquals("Class: ResultSetTest. Method: testGetDate. The retrieved day value is not correct ",new Integer(cal.get(Calendar.DATE)),new Integer(14));
		assertEquals("Class: ResultSetTest. Method: testGetDate. The retrieved month value is not correct ",new Integer(cal.get(Calendar.MONTH)),new Integer(5));
		assertEquals("Class: ResultSetTest. Method: testGetDate. The retrieved year value is not correct ",new Integer(cal.get(Calendar.YEAR)),new Integer(2000));

	}

	/**
	 * Método que chequea la recuperación de tipos time de la base de datos.
	 */
	public void testGetTime() {
		java.sql.Time result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getTime(TIME);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetTime. An SQL Exception has been thrown " +
				ex.getMessage());
		}	catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetTime. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(result);

		assertEquals("Class: ResultSetTest. Method: testGetTime. The retrieved hour value is not correct ",new Integer(cal.get(Calendar.HOUR)),new Integer(4));
		assertEquals("Class: ResultSetTest. Method: testGetTime. The retrieved minutes value is not correct ",new Integer(cal.get(Calendar.MINUTE)),new Integer(30));
		assertEquals("Class: ResultSetTest. Method: testGetTime. The retrieved seconds value is not correct ",new Integer(cal.get(Calendar.SECOND)),new Integer(00));
	}

	public void testGetStringTime() {
		java.sql.Time result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getTime(TIME_NAME);
		} catch (SQLException ex) {
			fail("Class: ResultSetTest. Method: testGetTime. An SQL Exception has been thrown " +
				ex.getMessage());
		}	catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetTime. An inexpected Exception " +
				th.getClass() + " has been thrown " + th.getMessage());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(result);

		assertEquals("Class: ResultSetTest. Method: testGetTime. The retrieved hour value is not correct ",new Integer(cal.get(Calendar.HOUR)),new Integer(4));
		assertEquals("Class: ResultSetTest. Method: testGetTime. The retrieved minutes value is not correct ",new Integer(cal.get(Calendar.MINUTE)),new Integer(30));
		assertEquals("Class: ResultSetTest. Method: testGetTime. The retrieved seconds value is not correct ",new Integer(cal.get(Calendar.SECOND)),new Integer(00));
	}

	/**
	 * Método que testea la recuperación de tipos de datos Boolean del Ctsql.
	 */
	public void testGetBoolean() {
		ResultSet rs = null;
		boolean result = false;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getBoolean(0);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetShort. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetShort. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetStringBoolean() {
		ResultSet rs = null;
		boolean result = false;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getBoolean("bool");
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetBoolean. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetBoolean. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	/**
	 * Método que testea la recuperación de tipos de datos Byte del Ctsql.
	 */
	public void testGetByte() {
		ResultSet rs = null;
		byte result = 0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getByte(0);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetByte. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetByte. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetStringByte() {
		ResultSet rs = null;
		byte result = 0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getByte("byte");
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetByte. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetByte. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	/**
	 * Método que testea la recuperación de tipos de datos Long del Ctsql.
	 */
	public void testGetLong() {
		ResultSet rs = null;
		long result = 0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getLong(0);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetLong. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetLong. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetStringLong() {
		ResultSet rs = null;
		long result = 0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getLong("lon");
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetLong. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetLong. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	/**
	 * Método que testea la recuperación de tipos de datos Float del Ctsql.
	 */
	public void testGetFloat() {
		ResultSet rs = null;
		float result = 0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getFloat(0);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetFloat. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetFloat. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetStringFloat() {
		ResultSet rs = null;
		float result = 0;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getFloat("flo");
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetFloat. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetFloat. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	/**
	 * Método que testea la recuperación de tipos de datos Bytes del Ctsql.
	 */
	public void testGetBytes() {
		ResultSet rs = null;
		byte[] result = null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getBytes(0);
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetBytes. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetBytes. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetStringBytes() {
		ResultSet rs = null;
		byte[] result =null;

		executeStatement("select * from basic");
		try {
			if (resultSet != null)
				result = resultSet.getBytes("bytes");
		} catch (SQLException ex) {
				assertNotNull("Class: ResultSetTest. Method: testGetBytes. An SQL Exception has been thrown ",ex);
		}catch (Throwable th) {
			fail("Class: ResultSetTest. Method: testGetBytes. An inexpected Exception " +
					th.getClass() + " has been thrown " + th.getMessage());
		}
	}

	public void testGetWarnings()
	{
		int i=-2;

		SQLWarning result;

		executeStatement("select * from basic");
		try
		{
			 i=resultSet.findColumn("sm");
			 result=resultSet.getWarnings();
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSet. TestGetWarnings: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSet. TestGetWarnings: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testClearWarnings()
	{
		int i=-2;

		executeStatement("select * from basic");
		try
		{
			 i=resultSet.findColumn("sm");
			 resultSet.clearWarnings();
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSet. TestClearWarnings: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSet. TestClearWarnings: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testGetCursorName()
	{
		int i=-2;
		String result=" ";

		executeStatement("select * from basic");
		try
		{
			 i=resultSet.findColumn("sm");
			 result=resultSet.getCursorName();
		}catch (SQLException ex)
		{
				fail("CtsqlResultSet. TestGetCursorName: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSet. TestGetCursorName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testFindColumn()
	{
		int i=-2;

		executeStatement("select * from basic");
		try
		{
			 i=resultSet.findColumn("sm");
		}catch (SQLException ex)
		{
				fail("CtsqlResultSet. TestFindColumn: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSet. TestFindColumn: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
		  assertEquals("CtsqlResultSet. TestFindColumn:Result isn't correct", new Integer(1),new Integer(i));

		try
		{
			 i=resultSet.findColumn("sme");
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSet. TestFindColumn: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSet. TestFindColumn: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}

	}

	private Class registerDriver() {
		java.lang.Class obj = null;
		try {
			obj = Class.forName(driver);
		} catch (ClassNotFoundException ex) {
			fail("CtsqlResultTest. RegisterDriver method. Driver not found ");
		}
		return obj;
	}

	private void openConnection() {
		try {
			String url = createUrl(host, protocol, dbpath, dbname);
			connection = DriverManager.getConnection(url, user, password);
			assertNotNull(connection);
		} catch (SQLException ex) {
			fail("SqlStatementTest. OpenConnection private method. Unexpected SQLException" +
				ex.getMessage());
		}
		catch (Throwable th) {
			fail("SqlStatementTest. OpenConnection private method. Unexpected Exception" +
				th.getMessage());
		}
	}

	private String createUrl(String host, String protocol, String path,
							String name) {
		return new String("jdbc:"+protocol + "://" + host + ":" +	port + ";" + path + ";" + name);
	}

	/**
	   * Método privado que permite la ejecución de la sentencia pasada como parámetro.
	   * Este método almacena en la variable resultSet definida en esta clase el resultado
	   * de la ejecución de la sentencia.
	   * En este caso el com.transtools.jdbc.test fallará si se produce alguna excepción, con lo cual se
	   * realiza la llamada al método fail.
	   *
	   * @param		stmt, sentencia que se desea ejecutar.
	   */
	private void executeStatement(String stmt) {
		Statement statement=null;
		boolean exist = false;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(stmt);
			exist = resultSet.next();
		} catch (SQLException se) {
			fail("Class: ResultSetTest. Method: executeStatement. A SQLException has been thrown with message " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("Class: ResultSetTest. Method: executeStatement. Inexpected exception with message " +
				ex.getMessage());
		}

		assertTrue("Class: ResultSetTest. Method: executeStatement. There aren't values in resultSet ",
			exist);
	}

}

