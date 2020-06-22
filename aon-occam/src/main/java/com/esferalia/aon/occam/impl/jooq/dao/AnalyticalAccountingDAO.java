package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport.AccountingAnalyticalColumn;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport.AccountingAnalyticalStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical.ANALYTICAL;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical.JAXBAnalytical;
import com.esferalia.aon.watson.error.AonCoreException;


public class AnalyticalAccountingDAO {
	
	private static File FILE = new File("/home/ecastellano/TRABAJO/analytical/config.xml");

	private static void serialize( Analytical analytical , Writer writer) {
		try {
			JAXBAnalytical jaxbAnalytical = ANALYTICAL.getJAXBAnalytical(analytical);
			JAXBContext context = JAXBContext.newInstance(JAXBAnalytical.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(jaxbAnalytical,writer);
			writer.flush();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en serializacion XML",e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en serializacion XML",e);
		}				
	}
	
//	private static String getXMLModel( Analytical analytical ) {
//		StringWriter writer = new StringWriter();
//		serialize(analytical, writer);
//		return writer.toString();
//	}
	
	public static Analytical save(AONContext ctx, Analytical analytical )  {
		try (FileWriter writer = new FileWriter( FILE )) {
			serialize(analytical,writer);
			return analytical;
		} catch (IOException e) {
			throw new AonCoreException(e );
		}
		
	}

	private static Analytical deserialize( Reader reader) {
		try {
			JAXBContext context = JAXBContext.newInstance(JAXBAnalytical.class);
			Unmarshaller um = context.createUnmarshaller();
			JAXBAnalytical jaxbAnalytical = (JAXBAnalytical) um.unmarshal(reader);
			return ANALYTICAL.getAnalytical(jaxbAnalytical);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en serializacion XML",e);
		}				
	}

	public static Analytical get(AONContext ctx)  {
		try (FileReader reader = new FileReader(FILE)) {
			return deserialize(reader);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AonCoreException("XML PROBLEM",e);
		}
	}
	
	public static AccountingAnalyticalReport analyticalReport(AONContext ctx , final AccountingReportParams params ) {
		Analytical analytical = AnalyticalAccountingDAO.get(ctx);
		AccountOperatingReport operating = AccountStatementDAO.operatingReport(ctx, params);
		AccountingAnalyticalReport report = new AccountingAnalyticalReport()
			.setParams(params)
			.setAnalytical(analytical)
			.setAccounts(operating.getAccounts());
		for (DateInterval inter : operating.getIntervals()) {
			report.ensureColumn( 
				new AccountingAnalyticalColumn()
					.setName(inter.getName())
					.setTotalColumn(true)
					.setDefaultColumn(false)
					.setPercent(-1));
			for (AnalyticalCostCenter costCenter : analytical.getCostCenters().values()) {
				report.ensureColumn(
						new AccountingAnalyticalColumn()
						.setName(costCenter.getName())
						.setTotalColumn(false)
						.setDefaultColumn(costCenter.isMain())
						.setPercent(costCenter.getPercent())
						);
			}
			for (AccountOperatingAccount account : operating.getAccounts()) {
				AccountOperatingStatement acc = operating.get(account.getCode(), inter);
				for (AccountingAnalyticalColumn column : report.getColumns()) {
					if (!acc.getAccount().getType().isCalculated()) {
						double percent = column.getPercent();
						AccountingAnalyticalStatement aas = new  AccountingAnalyticalStatement()
								.setAccount(new AccountOperatingAccount()
										.setType(acc.getAccount().getType())
										.setCode(acc.getAccount().getCode())
										.setDescription(acc.getAccount().getDescription()))
								.setDebit(acc.getDebit())
								.setCredit(acc.getCredit())
								.setPercent(percent);
						report.put(column, aas);
					}
				}
			}
		}
		report.calculate();
		return report;
	}
}
