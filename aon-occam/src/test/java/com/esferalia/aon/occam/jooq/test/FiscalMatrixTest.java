package com.esferalia.aon.occam.jooq.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMatrixDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;


public class FiscalMatrixTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static Integer DOMAIN_ID = 553;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	@FunctionalInterface
	private interface IPeriodTypeAccepter {
		boolean accept(Period mod);
	}
	
	private enum PeriodType {
		 MONTHLY	("M",12,period -> period.isMonthPeriod())
		,QUARTERLY	("Q", 4,period -> period.isQuarterPeriod())
		,YEARLY		("Y", 1,period -> period == Period.YEAR)
		;
		private String value;
		private int arraySize;
		private IPeriodTypeAccepter accepter;
		private PeriodType( String value, int arraySize, IPeriodTypeAccepter accepter) {
			this.value = value;
			this.arraySize = arraySize;
			this.accepter = accepter;
		}
		private String getValue() {
			return value;
		}
		private int getArraySize() {
			return arraySize;
		}
		private int getIndex(Period period) {
			if (period == Period.YEAR) return 0;
			if (period.isMonthPeriod()) return period.ordinal();
			return (period.ordinal() - 12);
		}
		private static PeriodType getPeriodType(Period period) {
			for (PeriodType type : PeriodType.values()) {
				if (type.accepter.accept(period)) return type;
			}
			return null;
		}
	}

	// @Test
	public void testMatrix() throws IOException {
		User user = SecurityDAO.getUser(ctx, USER);
		FiscalMatrixParams p = new FiscalMatrixParams();
		p.setYear(2012);
		p.setYear(2016);
		p.setYear(2017);
		
		LinkedList<IFiscalModel> list = FiscalMatrixDAO.getModelsPanel(ctx, DOMAIN_ID, p, user.getId());
		TreeMap<String, LinkedList<IFiscalModel>> map = new TreeMap<String, LinkedList<IFiscalModel>>();
		
		for (IFiscalModel mod : list ) {
			PeriodType type = PeriodType.getPeriodType(mod.getPeriod());
			String key = mod.getDomainName() + ";" + mod.getModel().getValue() + ";" + mod.getAdministration() + ";" + type.getValue(); 
			if (!map.containsKey(key)) {
				map.put(key, new LinkedList<IFiscalModel>());
				for (int i = 0; i < type.getArraySize(); i++) {
					map.get(key).add(null);
				}
			}
			
			map.get(key).set(type.getIndex(mod.getPeriod()), mod);
		}
		
		for (String key : map.keySet()) {
			boolean first = true;
			int i = 0;
			System.out.println( AonStringUtils.repeat("-", 120));
			for (IFiscalModel mod : map.get(key) ) {
				if (mod != null) {
					if (first) {
						first = false;
						System.out.println(
							AonStringUtils.rightPad(AonStringUtils.abbreviate(mod.getDomainName(), 45),46)
						 + AonStringUtils.rightPad(AonStringUtils.abbreviate(mod.getName(), 25),26)  
						 + AonStringUtils.rightPad(mod.getDocument(), 10) 
						  
						 + AonStringUtils.rightPad(mod.getDocument(), 10) 
						 + AonStringUtils.leftPad(""+mod.getDomain(), 8)
						 + AonStringUtils.center(""+mod.getYear(), 6) 
						 + AonStringUtils.rightPad(""+mod.getModel(), 8)
						 + AonStringUtils.rightPad(AonStringUtils.abbreviate((mod.getAdministration()==null?"---------------":mod.getAdministration().getDescription()), 15),16)
						);
					}
					
					System.out.println( AonStringUtils.repeat(" ", i * 20)
					  + AonStringUtils.rightPad(mod.getPeriod().getName(), 8)
					  + AonStringUtils.leftPad(""+mod.getId(), 12)
					  + AonStringUtils.rightPad(mod.getStatus().getName(), 12)
					  );
				}
				i++;
			}		
			System.out.println( AonStringUtils.repeat("-", 120));
			System.out.println();
		}
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

}
