package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;


public class CtsqlResultSetMetaDataTest extends TestCase {

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

	/**
	 * Objeto que almacenará los resultados obtenidos tras la ejecución de una query
	 * y que será el que se utilice como base para el testeo de los diferentes métodos.
	 */
	protected ResultSet resultSet;

	protected ResultSetMetaData metaData;

	/**
	 * Constructor por defecto que debe tener esta clase por ser implementación de
	 * la clase TestCase. En este constructor se hace una llamada al método que
	 * inicializa la tabla con las diferentes conexiones que se pueden usar.
	 */
	public CtsqlResultSetMetaDataTest(String name) {
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

	public void testGetColumnCount()
	{
		int i=-1;
		try
		{
		 executeStatement("select * from basic");
		 i=metaData.getColumnCount();
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetColumnCount: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnCount: A Throwable Exception shouldn't be thrown " + th.getMessage());
		}
		  assertEquals("CtsqlResultSetMetaData. TestGetColumnCount:Result isn't correct", new Integer(i),new Integer(6));
	}

	public void testGetColumnType()
	{
		int i = -1;

		try
		{
		 executeStatement("select * from basic");
		 i=metaData.getColumnType(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetColumnType: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnType: A Throwable Exception shouldn't be thrown " + th.getMessage());
		}
		  assertEquals("CtsqlResultSetMetaData. TestGetColumnType:Result isn't correct", new Integer(i),new Integer(1));

		try
		{
		 i=metaData.getColumnType(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetColumnType: A SQLException should be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnType: A Throwable Exception shouldn't be thrown " + th.getMessage());
		}
	}

	public void testGetColumnName()
	{
		String cad=null;

		try
		{
			 executeStatement("select * from basic");
			 cad=metaData.getColumnName(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetColumnName: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
		  assertEquals("CtsqlResultSetMetaData. TestGetColumnName:Result isn't correct", new String(cad),new String("sm"));

			try
		{
			 cad=metaData.getColumnName(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetColumnName: A SQLException should be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testGetColumnLabel()
	{
		String cad=null;

		try
		{
			 executeStatement("select * from basic");
			 cad=metaData.getColumnLabel(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetColumnLabel: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnLabel: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
		  assertEquals("CtsqlResultSetMetaData. TestGetColumnLabel:Result isn't correct", new String(cad),new String("Smallint"));

		try
		{
			 cad=metaData.getColumnLabel(0);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetColumnLabel: A SQLException should be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnLabel: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}


	public void testGetColumnTypeName()
	{
		String cad=null;

		try
		{
			 executeStatement("select * from basic");
			 cad=metaData.getColumnTypeName(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetColumnTypeName: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnTypeName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
		  assertEquals("CtsqlResultSetMetaData. TestGetColumnTypeName:Result isn't correct", new String(cad),new String("SMALLINT"));

		try
		{
			 cad=metaData.getColumnTypeName(0);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetColumnTypeName: A SQLException should be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnTypeName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}

	}

	public void testGetColumnDisplaySize()
	{
		int i = -2;

		try
		{
			 executeStatement("select * from basic");
			 i=metaData.getColumnDisplaySize(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetColumnDisplaySize: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnDisplaySize: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
		  assertEquals("CtsqlResultSetMetaData. TestGetColumnDisplaySize:Result isn't correct", new Integer(i),new Integer(8));

		try
		{
			 i=metaData.getColumnDisplaySize(21);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetColumnDisplaySize: A SQLException should be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetColumnDisplaySize: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}

	}


	public void testIsAutoIncrement()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isAutoIncrement(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsAutoIncrement: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsAutoIncrement: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsAutoIncrement:Result isn't correct b="+b,b);


		executeStatement("select * from basic");
		try
		{
			 b=metaData.isAutoIncrement(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsAutoIncrement: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsAutoIncrement: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsCaseSensitive()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isCaseSensitive(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsCaseSensitive: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsCaseSensitive: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsCaseSensitive:Result isn't correct b="+b,(!b));


		executeStatement("select * from basic");
		try
		{
			 b=metaData.isCaseSensitive(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsCaseSensitive: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsCaseSensitive: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsSearchable()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isSearchable(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsSearchable: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsSearchable: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsSearchable:Result isn't correct b="+b,b);


		executeStatement("select * from basic");
		try
		{
			 b=metaData.isSearchable(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsSearchable: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsSearchable: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsCurrency()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isCurrency(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsCurrency: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsCurrency: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsCurrency:Result isn't correct b="+b,(!b));


		executeStatement("select * from basic");
		try
		{
			 b=metaData.isCurrency(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsCurrency: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsCurrency: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsSigned()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isSigned(1);	// la columna 1 es un smallint
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsSigned: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsSigned: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsSigned:Result isn't correct b="+b,b);

		executeStatement("select * from basic");
		try
		{
			 b=metaData.isSigned(2);	// la columna 2 es un char
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsSigned: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsSigned: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsSigned:Result isn't correct b="+b,(!b));

		executeStatement("select * from basic");
		try
		{
			 b=metaData.isSigned(3);	// la columna 3 es un integer
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsSigned: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsSigned: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertTrue("CtsqlResultSetMetaData. TestIsSigned:Result isn't correct b="+b,b);

		executeStatement("select * from basic");
		try
		{
			 b=metaData.isSigned(10);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsSigned: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsSigned: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testGetSchemaName()
	{
		String result;

		try
		{
			 executeStatement("select * from basic");
			 result=metaData.getSchemaName(1);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetSchemaName: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetSchemaName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testGetTableName()
	{
		String result;

		try
		{
			 executeStatement("select * from basic");
			 result=metaData.getTableName(1);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetTableName: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetTableName: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsReadOnly()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isReadOnly(1);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsReadOnly: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsReadOnly: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsWritable()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isWritable(1);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsWritable: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsWritable: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsDefinitelyWritable()
	{
		boolean b=false;

		try
		{
			 executeStatement("select * from basic");
			 b=metaData.isDefinitelyWritable(1);
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestIsDefinitelyWritable: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsDefinitelyWritable: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	}

	public void testIsNullable()
	{
		int i=-1;

		try
		{
			 executeStatement("select * from basic");
			 i=metaData.isNullable(1);
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestIsNullable: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestIsNullable: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
		assertEquals("CtsqlResultSetMetaData. TestIsNullable:Result isn't correct b=",new Integer(i),new Integer(1));
	}

	public void testGetPrecision()
	{
		int i=-1;

		try
		{
			 executeStatement("select * from basic");
			 i=metaData.getPrecision(1);  // la columna 1 es smallint
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetPrecision:Result isn't correct i="+i,new Integer(i),new Integer(2));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getPrecision(2);	// la columna 2 es char
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetPrecision:Result isn't correct i="+i,new Integer(i),new Integer(20));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getPrecision(1);  // la columna 3 es integer
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetPrecision:Result isn't correct i="+i,new Integer(i),new Integer(2));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getPrecision(4);	// la columna 4 es decimal
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetPrecision:Result isn't correct i="+i,new Integer(i),new Integer(16));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getPrecision(5);  // la columna 5 es date
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetPrecision:Result isn't correct i="+i,new Integer(i),new Integer(4));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getPrecision(6);	// la columna 6 es time
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetPrecision:Result isn't correct i="+i,new Integer(i),new Integer(4));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getPrecision(10);	// la columna no existe
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetPrecision: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetPrecision: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}

	}

	public void testGetScale()
	{
		int i=-1;

		try
		{
			 executeStatement("select * from basic");
			 i=metaData.getScale(1);  // la columna 1 es smallint
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetScale:Result isn't correct i="+i,new Integer(i),new Integer(0));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getScale(2);	// la columna 2 es char
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetScale:Result isn't correct i="+i,new Integer(i),new Integer(0));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getScale(1);  // la columna 3 es integer
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetScale:Result isn't correct i="+i,new Integer(i),new Integer(0));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getScale(4);	// la columna 4 es decimal
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetScale:Result isn't correct i="+i,new Integer(i),new Integer(2));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getScale(5);  // la columna 5 es date
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetScale:Result isn't correct i="+i,new Integer(i),new Integer(0));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getScale(6);	// la columna 6 es time
		}catch (SQLException ex)
		{
				fail("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown " + ex.getMessage());
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}
	  assertEquals("CtsqlResultSetMetaData. TestGetScale:Result isn't correct i="+i,new Integer(i),new Integer(0));

		executeStatement("select * from basic");
		try
		{
			 i=metaData.getScale(10);	// la columna no existe
		}catch (SQLException ex)
		{
				assertNotNull("CtsqlResultSetMetaData. TestGetScale: A SQLException shouldn't be thrown ",ex);
		}catch (Throwable th)
		{
			  fail("CtsqlResultSetMetaData. TestGetScale: A Throwable Exception shouldn't be thrown " + th.getClass() + "  " + th.getMessage());
		}

	}

	private void close_resultSet()
	{
		try {
			resultSet.close();
		} catch (SQLException ex) {
			fail("CtsqlResultSet. Method: tearDown. The resultSet can not be closed");
		}
		catch (Throwable th) {
			fail("CtsqlResultSet. Method: tearDown. A Throwable exception shouldn't be thrown " +
				th.getClass() + " with message " + th.getMessage());
		}
	}

	protected void tearDown() {
		try {

			ResultSet rs;
			connection.close();
		} catch (SQLException ex) {
			fail("CtsqlResultSet. Method: tearDown. The connection can not be closed"+ex.getMessage());
		}
		catch (Throwable th) {
			fail("CtsqlResultSet. Method: tearDown. A Throwable exception shouldn't be thrown " +
				th.getClass() + " with message " + th.getMessage());
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
				connection =
				DriverManager.getConnection(url, user, password);
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

		private String createUrl(String host, String protocol,
								String path, String name) {
			return new String("jdbc:"+protocol + "://" + host + ":" +
							port + ";" + path + ";" + name);
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
				metaData = resultSet.getMetaData();
			} catch (SQLException se) {
				fail("Class: ResultSetTest. Method: executeStatement. A SQLException has been thrown with message " +
					se.getMessage());
			}
			catch (Throwable ex) {
				fail("Class: ResultSetTest. Method: executeStatement. Inexpected exception with message " +
					ex.getMessage());
			}
		}
	}

