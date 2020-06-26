package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalColumn;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccount;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccountLevel;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical.ANALYTICAL;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical.JAXBAnalytical;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class AnalyticalAccountingDAO {
	
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
	
	public static Analytical save(AONContext ctx, Analytical analytical )  {
		ctx.checkWrite();
		
		DataResponse dataResponse = DataResponseDAO.getDataResponseStream(ctx,DataResponseSource.ANALYTIC_ACCOUNTING, 
				p -> p.getDomainProperty().eq( ctx.getDomainId() )
				.and(p.getCodeProperty().eq( DataResponseSource.ANALYTIC_ACCOUNTING.toString()) )
				)
				.findFirst()
				.orElse(null);

		DataResponseDetail detail = null;
		if (dataResponse == null) {
			dataResponse = new DataResponse()
				.setDomain(ctx.getDomainId())
				.setSource(DataResponseSource.ANALYTIC_ACCOUNTING)
				.setCode(DataResponseSource.ANALYTIC_ACCOUNTING.toString());
			dataResponse = DataResponseDAO.insertDataResponse(ctx, dataResponse);	
		} else {
			detail = DataResponseDAO.getLastDataResponseDetail(ctx, dataResponse.getId());	
		}
		 
		StringWriter writer = new StringWriter();
		serialize(analytical,writer);
		if (detail == null) {
			detail = new DataResponseDetail()
				.setDomain(dataResponse.getDomain())
				.setDataResponse(dataResponse.getId())
				.setDataVariable(DataResponseSource.ANALYTIC_ACCOUNTING.toString())
				.setDataValue(writer.toString());
			detail = DataResponseDAO.insertDataResponseDetail(ctx, detail);
		} else {
			detail.setDataValue(writer.toString());
			Integer id = detail.getId();
			detail = DataResponseDAO.updateDataResponseDetail(ctx, detail, p -> p.getIdProperty().eq( id ));
		}
		return get(ctx);
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
		ctx.checkRead();
		DataResponseDetail detail = DataResponseDAO.getLastDataResponseDetailStream(ctx, 
				p -> p.getDomainProperty().eq( ctx.getDomainId() )
					.and(p.getSourceProperty().eq(DataResponseSource.ANALYTIC_ACCOUNTING.value())) 
				).findFirst()
				.orElse(null);
		if (detail != null) {
			StringReader reader = new  StringReader(detail.getDataValue());
			return deserialize(reader);
		}
		return null;
	}
	
	public static AccountingAnalyticalReport analyticalReport(AONContext ctx , final AccountingReportParams params) {
		Analytical analytical = AnalyticalAccountingDAO.get(ctx);
		return analyticalReport(ctx, params, analytical);
	}
	
	public static AccountingAnalyticalReport analyticalReport(AONContext ctx , final AccountingReportParams params , Analytical analytical ) {
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
			if (analytical != null) {
				for (AnalyticalCostCenter costCenter : analytical.getCostCenters().values()) {
					report.ensureColumn(
							new AccountingAnalyticalColumn()
							.setName(costCenter.getName())
							.setTotalColumn(false)
							.setDefaultColumn(costCenter.isMain())
							.setPercent(costCenter.getPercent())
							);
				}
			}
			for (AccountOperatingAccount account : operating.getAccounts()) {
				AccountOperatingStatement acc = operating.get(account.getCode(), inter);
				for (AccountingAnalyticalColumn column : report.getColumns()) {
					if (!acc.getAccount().getType().isCalculated()) {
						double percent = column.getPercent();
						AnalyticalAccountLevel level = null;
						AnalyticalAccountLevel percentSource = null;
						String sourceCode = null;
						if (!column.isTotalColumn()) {
							level = AnalyticalAccountLevel.safeValueOf(account.getCode());
							AnalyticalCostCenter costCenter = analytical.getCostCenters().get( column.getName() );
							for (int i = level.ordinal(); i < AnalyticalAccountLevel.values().length; i++) {
								String code = AonStringUtils.substring(account.getCode(), 0, AnalyticalAccountLevel.values()[i].getAccountCodeLength());
								AnalyticalAccount analyticalAccount = costCenter.getAccounts().get(code);
								if (analyticalAccount != null) {
									percent = analyticalAccount.getPercent();
									percentSource = AnalyticalAccountLevel.values()[i];
									sourceCode = (percentSource!=level)?code:null;
									break;
								}
							}
							if (percentSource == null) {
								percentSource = AnalyticalAccountLevel.COST_CENTER;
							}
						}
						AccountingAnalyticalStatement aas = new  AccountingAnalyticalStatement()
								.setAccount(new AccountOperatingAccount()
										.setType(acc.getAccount().getType())
										.setCode(acc.getAccount().getCode())
										.setDescription(acc.getAccount().getDescription()))
								.setDebit(acc.getDebit())
								.setCredit(acc.getCredit())
								.setPercent(percent)
								.setLevel(level)
								.setPercentSource(percentSource)
								.setSourceCode(sourceCode)
								;
						report.put(column, aas);
					}
				}
			}
		}
		calculate(report);
		return report;
	}
	
	public static void calculate(AccountingAnalyticalReport report) {
		for (AccountOperatingAccount account : report.getAccounts() ) {
			for (AccountingAnalyticalColumn column : report.getColumns() ) {
				AccountingAnalyticalStatement aas = report.get(account.getCode(), column);
				if (aas != null && aas.modifies()) {
					AccountOperatingStatementType modifies = aas.getAccount().getType().modifies();
					if (modifies != null) {
						AccountingAnalyticalStatement total = new  AccountingAnalyticalStatement()
								.setAccount(new AccountOperatingAccount()
										.setType(modifies)
										.setCode(modifies.toString())
										.setDescription(modifies.getDescription()))
								.setDebit((aas.isCalculated() || column.isTotalColumn())?aas.getDebit():aas.getPercentDebit())
								.setCredit((aas.isCalculated() || column.isTotalColumn())?aas.getCredit():aas.getPercentCredit())
								.setPercent(-1);
						report.put(column, total);
					}
				}
			}
		}
	}
	
}
