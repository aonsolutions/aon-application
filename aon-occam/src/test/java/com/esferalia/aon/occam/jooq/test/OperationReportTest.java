package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.impl.jooq.dao.OperationDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;


public class OperationReportTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "localhost";
	private static Integer DOMAIN_ID = 9253;
	private static String LOGIN = "admin";

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( com.mysql.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
	private void print(String s, int len) {
		if (s==null)
			System.out.print(AonStringUtils.repeat(' ', len)+" ");
		else if (s.length()>len)
			System.out.print(AonStringUtils.rightPad(s.substring(0, len),len)+" ");
		else System.out.print(AonStringUtils.rightPad(s,len)+" ");		
	}
	
	private void print(Date d) {
		if (d==null)
			System.out.print(AonStringUtils.repeat(' ', 10)+" ");
		else System.out.print(d+" ");
	}
	
	private void print(Double d) {
		print(d,10);
	}
	
	private void print(Double d, int len) {		
		if (d==null)
			System.out.print(AonStringUtils.repeat(' ', len)+" ");
		else System.out.print(AonStringUtils.leftPad(d.toString(),len)+" ");
	}
	
	double totalBase = 0;
	double totalImp = 0;
	double totalTotal = 0;	
	double totalIVA = 0;
	double totalREq = 0;
	double totalDed = 0;	
	
	@Test
	public void testStreamingIRPF() throws IOException {
		
		System.out.println("--- INICIO TEST IRPF ---");
		System.out.println();
		final Date fromDate = AonDateUtils.getYearFirstDay(2018);
		final Date toDate = AonDateUtils.getYearLastDay(2018);		
		Integer activity = 363;
		
		OperationParams params = new OperationParams();
        params.setFromDate(fromDate);
        params.setToDate(toDate);
        params.setActivity(activity.intValue());
        
        boolean[] exp = {true,false};
		
		for (boolean expenses : exp) {
			params.setExpenses(expenses);
			params.setIrpf(true);
			Stream<OperationBreakdown> stream = OperationDAO.getOperationBreakdown(ctx, DOMAIN_ID, params);
			
			System.out.println(AonStringUtils.repeat('-',220));
			System.out.println("LISTADO DE "+(expenses?"COMPRAS Y GASTOS":"VENTAS E INGRESOS")+" IRPF");
			System.out.println(AonStringUtils.repeat('-',220));
			print("ACTIVIDAD",30);
			print("FECHA APU",10);
			print("FECHA IVA",10);
			print("CUENTA",9);
			print("CONCEPTO",60);					
			print("Nº DOCUMENTO",15);			
			print("TITULAR",46);
			print("  BASE IMP",10);
			print(" IMPUESTOS",10);
			print("     TOTAL",10);
			System.out.println();
			System.out.println(AonStringUtils.repeat('-',220));
			
			totalBase = 0;
			totalImp = 0;
			totalTotal = 0;
			
			stream.forEach(
					
					p -> {
						
						totalBase = AonMathUtils.round(totalBase + p.getBase());
						totalImp = AonMathUtils.round(totalImp + p.getDeductibleQuota()+p.getSurchargeQuota());
						totalTotal = AonMathUtils.round(totalTotal + p.getTotal()); 
						
//						print(AonStringUtils.isBlank(p.getActivityDescription())?"< Sin Actividad >":p.getActivityDescription(),30);
						print(p.getEntryDate());
						print(p.getTaxDate());
						print(p.getAccount(),9);
						print(p.getFullConcept(),60);					
						print(p.getDocNumber(),15);
						print(p.getFullDocumentName(),46);
						if (p.getInvoice()==null)
							print("",10);
						else print(p.getBase());
						if (p.getInvoice()==null)
							print("",10);
						else print(AonMathUtils.round(p.getDeductibleQuota()+p.getSurchargeQuota()));
						print(p.getTotal());
						System.out.println();
					}
					
					);
	
			System.out.println(AonStringUtils.leftPad(AonStringUtils.repeat('-',33),220));
			print(totalBase,197);
			print(totalImp,10);
			print(totalTotal,10);
			System.out.println();			
		}		
			
		System.out.println("--- FIN TEST IRPF ---");
		System.out.println();
	}
	
	@Test
	public void testStreamingIVA() throws IOException {
		
		System.out.println("--- INICIO TEST IVA ---");
		System.out.println();
		final Date fromDate = AonDateUtils.getYearFirstDay(2018);
		final Date toDate = AonDateUtils.getYearLastDay(2018);		
		Integer activity = 363;
		
		OperationParams params = new OperationParams();
        params.setFromDate(fromDate);
        params.setToDate(toDate);
        params.setActivity(activity.intValue());
		
		boolean[] exp = {true,false};
		
		for (boolean expenses : exp) {
			params.setExpenses(expenses);
			params.setIrpf(true);
			Stream<OperationBreakdown> stream = OperationDAO.getOperationBreakdown(ctx, DOMAIN_ID, params);
			
			int dash = 244;
			
			System.out.println(AonStringUtils.repeat('-',dash));
			System.out.println("LISTADO DE "+(expenses?"COMPRAS Y GASTOS":"VENTAS E INGRESOS")+" IVA");
			System.out.println(AonStringUtils.repeat('-',dash));
			print("ACTIVIDAD",20);
			print("FECHA APU",10);
			print("FECHA IVA",10);
			print("CUENTA",9);
			print("CONCEPTO",60);					
			print("Nº DOCUMENTO",15);			
			print("TITULAR",46);
			print("  BASE IMP",10);
			print( "%IVA",5);
			print(" CUOTA IVA",10);
			print(" CUOTA DED",10);
			print(" %REQ",5);
			print(" CUOTA REQ",10);
			print("     TOTAL",10);
			System.out.println();
			System.out.println(AonStringUtils.repeat('-',dash));
			
			totalBase = 0;
			totalIVA = 0;
			totalDed = 0;
			totalREq = 0;
			totalTotal = 0;
			
			stream.forEach(
					
					p -> {
						
						totalBase = AonMathUtils.round(totalBase + p.getBase());
						totalIVA = AonMathUtils.round(totalIVA + p.getQuota());
						totalDed = AonMathUtils.round(totalDed + p.getDeductibleQuota());
						totalREq = AonMathUtils.round(totalREq + p.getSurchargeQuota());
						totalTotal = AonMathUtils.round(totalTotal + p.getTotal()); 
						
//						print(AonStringUtils.isBlank(p.getActivityDescription())?"< Sin Actividad >":p.getActivityDescription(),20);
						print(p.getEntryDate());
						print(p.getTaxDate());
						print(p.getAccount(),9);
						print(p.getFullConcept(),60);					
						print(p.getDocNumber(),15);
						print(p.getFullDocumentName(),46);						
						print(p.getBase());
						print(p.getPercent(),5);
						print(p.getQuota());
						print(p.getDeductibleQuota());
						print(p.getSurchargePercent(),5);
						print(p.getSurchargeQuota());						
						print(p.getTotal());
						System.out.println();
					}
					
					);
	
			System.out.println(AonStringUtils.leftPad(AonStringUtils.repeat('-',67),dash));
			print(totalBase,dash-57);
			print(totalIVA,16);
			print(totalDed,10);
			print(totalREq,16);
			print(totalTotal,10);
			System.out.println();			
		}		
			
		System.out.println("--- FIN TEST IVA ---");
		System.out.println();
	}
	
}
