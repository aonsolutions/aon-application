package com.esferalia.aon.gwt.stat;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.AonDateUtils;

public class StatTest {

	private static final int DOMAIN_ID = 400;
	private static final String DOMAIN_NAME = "mac.eirazu.dev";
	private static final String USER_NAME = "mac";
	
	private static AONContext ctx;
	
	@BeforeClass
	public static void beforeClass() {
		ctx = AONContext.getAONContext( DOMAIN_NAME, DOMAIN_ID, USER_NAME );
	}
	
	@Test
	@Ignore
	//primera forma de hacerlo, la "anterior"
	//Saca por pantalla el doc y el nombre del resgistro con dominio 400
	public void testEmptyDomain() throws ClassNotFoundException, SQLException {
		System.out.println( 
		ctx.getDslContext()
			.select( REGISTRY.DOCUMENT, REGISTRY.NAME)
			.from(REGISTRY)
			.where(REGISTRY.DOMAIN.eq(DOMAIN_ID))
			.limit(50)
			.fetch()
			);
	}
	
	@Test
	@Ignore
	//segunda forma, la más eficaz
	//en vez de hacer la sql aquí, la llama de StatDAO
	public void testInvoices2() throws ClassNotFoundException, SQLException {
		StatDAO.getInvoices2(ctx, new InvoiceStatParams()
				.setFrom( AonDateUtils.getYearFirstDay(2014) )
				.setTo( AonDateUtils.getYearFirstDay(2014) ))
		.stream()
		.forEach(is -> System.out.println(is.getIssueDate() + " -- " + is.getTaxableBase() )); 
	}
	
	
	
	
	
	
	@Test
	@Ignore
	//test para sacar datos de ventas, compras, gastos y gastos nr entre dos fechas, de INVOICE --> TYPE, gráfico tarta
	public void testGrafico1() throws ClassNotFoundException, SQLException{
		StatDAO.getInvoicesGrafico1(ctx, new InvoiceStatParams()
				.setFrom( AonDateUtils.getYearFirstDay(2014) )
				.setTo( AonDateUtils.getYearFirstDay(2016) ))
		.stream()
		.forEach(is -> System.out.println(is.getType() + ", " + is.getCount()));
	}

	
	
	@Test
	@Ignore
	//Test para sacar los datos del gráfico simple de barras, ventas en el 2014 por meses
	public void testGrafico2() throws ClassNotFoundException, SQLException{
		StatDAO.getInvoicesGrafico2(ctx)
		.stream()
		.forEach(is -> System.out.println("['" + is.getIssueDateMonth() + "', " + is.getCount() + "], "));
	}
	
	
	@Test
	@Ignore	
	//Test para sacar los datos del gráfico, ventas, compras...por mes, en el 2014.
	//No sale imprimible al HTML...
	public void testGrafico3() throws ClassNotFoundException, SQLException{
		StatDAO.getInvoicesGrafico3(ctx)
		.stream()
		.forEach(is -> System.out.println("[" + is.getType() + ", " + is.getCount() + ", " + is.getIssueDateMonth() + "], "));
	}
	
	
	
	
	
	
	
	
	
	@AfterClass
	public static void afterClass() {
		ctx.close();
	}

	
}
