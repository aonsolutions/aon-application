package com.esferalia.aon.dsi.test;

import static com.esferalia.aon.dsi.jooq.tables.Fncconce.FNCCONCE;
import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;
import static com.esferalia.aon.dsi.jooq.tables.Fnnominc.FNNOMINC;
import static com.esferalia.aon.dsi.jooq.tables.Fnnominl.FNNOMINL;
import static com.esferalia.aon.dsi.jooq.tables.Fntantig.FNTANTIG;
import static com.esferalia.aon.dsi.jooq.tables.Fntconce.FNTCONCE;
import static com.esferalia.aon.dsi.jooq.tables.Fntrabaj.FNTRABAJ;
import static com.esferalia.aon.dsi.jooq.tables.Fnzantig.FNZANTIG;
import static com.esferalia.aon.dsi.jooq.tables.Fnzconce.FNZCONCE;
import static com.esferalia.aon.dsi.jooq.tables.Fnzpagas.FNZPAGAS;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UpdateSetMoreStep;
import org.jooq.UpdateSetStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.esferalia.aon.dsi.jooq.tables.Fncconce;
import com.esferalia.aon.dsi.jooq.tables.Fnempres;
import com.esferalia.aon.dsi.jooq.tables.Fnnominc;
import com.esferalia.aon.dsi.jooq.tables.Fntantig;
import com.esferalia.aon.dsi.jooq.tables.Fntconce;
import com.esferalia.aon.dsi.jooq.tables.Fnzantig;
import com.esferalia.aon.dsi.jooq.tables.Fnzconce;
import com.esferalia.aon.dsi.jooq.tables.Fnzpagas;
import com.hxtt.sql.paradox.ParadoxDriver;

/**
 * Generic tests for Paradox Driver
 * 
 * @author Leonardo Alves da Costa
 * @since 14/3/2009
 */
@RunWith(JUnit4.class)
public class JOOQTest {

	public static final String CONNECTION_STRING = "jdbc:paradox:/target/test-classes/";
	private Connection conn;

	@BeforeClass
	public static void setUp() throws Exception {
		Class.forName(ParadoxDriver.class.getName());
	}

	@Before
	public void connect() throws Exception {
		conn = DriverManager.getConnection(JOOQTest.CONNECTION_STRING + "db");
	}

	@After
	public void closeConnection() throws Exception {
		if (conn != null) {
			conn.close();
		}
	}
	
	/*
	public void _testTrabaj() throws Exception {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setRenderNameStyle(RenderNameStyle.AS_IS);
		settings.setStatementType(STATIC_STATEMENT);
		DSLContext create = DSL.using(conn, settings);
		create.select().from(FNTRABAJ_2eDB).getSQL();
		Cursor<Record> cursor = create.select().from(FNTRABAJ_2eDB).fetchLazy();
		while (cursor.hasNext()) {
			Fntrabaj_2edbRecord trabaj_2eb = cursor.fetchOneInto(FNTRABAJ_2eDB);
			//@formatter:off
			System.out.println(String.format("%s %s, %s",
					trabaj_2eb.getF20apell2(), 
					trabaj_2eb.getF20apell1(),
					trabaj_2eb.getF20nombre())
					);
			//@formatter:on
		}
	}*/
	
	@Test
	public void testEmpres() throws Exception {
		//showTable(FNEMPRES);
		//showTable(FNEMPREU);
	}

	@Test
	public void tesTraba2() throws Exception {
		//showTable(FNTRABA2);
	}

	@Test
	public void tesTrabaj() throws Exception {
		//showTable(FNTRABAJ);
	}

	@Test
	public void tesNominc() throws Exception {
		//showTable(FNNOMINC);
	}
	
	@Test
	public void tesConven() throws Exception {
		//showTable(FNCONVEN);
	}

	@Test
	public void tesCtolin() throws Exception {
		//showTable(FNCTOLIN);
	}

	@Test
	public void tesCconce() throws Exception {
		//showTable(FNCCONCE); // OK
	}

	@Test
	public void tesDconce() throws Exception {
		//showTable(FNDCONCE); ????
	}

	@Test
	public void tesEconce() throws Exception {
		//showTable(FNECONCE); ????
	}

	@Test
	public void tesTconce() throws Exception {
		//showTable(FNTCONCE); // ContractPayment
	}


	@Test
	public void tesCatego() throws Exception {
		//showTable(FNCATEGO); // AgreementPayment
	}

	@Test
	public void tesZconce() throws Exception {
		//showTable(FNZCONCE); // AgreementPayment
	}

	@Test
	public void tesVarios() throws Exception {
		//showTable(FNVARIOS);
	}

	@Test
	public void tesNominl() throws Exception {
	showTable(FNNOMINL);
	}

	@Test
	public void tesZantig() throws Exception {
		//showTable(FNZANTIG);
	}

	@Test
	public void tesZpagas() throws Exception {
		//showTable(FNZPAGAS);
	}
	
	@Test
	public void tesantig() throws Exception {
		/*
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		DSLContext create = DSL.using(conn, settings);
		
		Cursor<Record> cursor = create
				.select()
				.from(FNNOMINL)
				.join(FNTRABAJ)
				.on(FNNOMINL.F31SSCOD.eq(FNTRABAJ.F20SSCOD)
				.and(FNNOMINL.F31SSNUM.eq(FNTRABAJ.F20SSNUM))
				.and(FNNOMINL.F31FALTA.eq(FNTRABAJ.F20FALTA))
				)
				.join(FNTANTIG)
				.on(FNTANTIG.F21SSCOD.eq(FNTRABAJ.F20SSCOD)
				.and(FNTANTIG.F21SSNUM.eq(FNTRABAJ.F20SSNUM))
				.and(FNTANTIG.F21FALTA.eq(FNTRABAJ.F20FALTA))
				)
				.where(FNNOMINL.F31NOMBRE.in("ANTIGUEDAD", "SALARIO BASE"))
				.orderBy(FNNOMINL.F31SSCOD, FNNOMINL.F31SSNUM, FNNOMINL.F31FALTA, FNNOMINL.F31MES, FNNOMINL.F31NOMBRE )
				.fetchLazy();
		while (cursor.hasNext()) {
			Record record =  cursor.fetchOne();
			if ( record.getValue(FNNOMINL.F31NOMBRE).equals("ANTIGUEDAD"))
				System.out.println(record.getValue(FNTRABAJ.F20FANTIG) + " = " +  record.getValue(FNNOMINL.F31TOTAL ) + "( " + record.getValue( FNTANTIG.F21NORDEN ) + ", " +  record.getValue( FNTANTIG.F21ANNOS ) + ", " +  record.getValue( FNTANTIG.F21VALOR ) + ")");
			else
				System.out.println(record.getValue(FNNOMINL.F31NOMBRE) + " = " + record.getValue(FNNOMINL.F31MES) + " = " +  record.getValue(FNNOMINL.F31TOTAL ) );
		}
		*/
		
	}

	public void showTable(Table<?> table) throws Exception {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		DSLContext create = DSL.using(conn, settings);
		create.select().from(table).getSQL();
		Cursor<Record> cursor = create.select().from(table).fetchLazy();
		while (cursor.hasNext())
			System.out.println(cursor.fetchOneInto(table));
	}
	
}
