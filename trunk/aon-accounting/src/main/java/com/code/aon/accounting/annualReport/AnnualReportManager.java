package com.code.aon.accounting.annualReport;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AnnualReportManager {

	public void resolve(Reader reader, AnnualReportParameters spp, Writer writer) {
		try {
			Velocity.init();
			VelocityContext ctx = new VelocityContext();
			AnnualReportContext actx = new AnnualReportContext( spp );
			ctx.put("conta", actx );
			EventCartridge ec = new EventCartridge();
			ec.addEventHandler(new AnnualReportEventHandler());
			ec.addReferenceInsertionEventHandler(new AnnualReportFormatterEventHandler());
			ctx.attachEventCartridge(ec);
			
			Velocity.evaluate(ctx, writer, "Go!", reader);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
	}
	public static void main(String[] args) throws IOException, ManagerBeanException {
		AnnualReportManager manager = new AnnualReportManager();

		URL url = manager.getClass().getResource("memoria.html");
		FileReader reader = new FileReader(url.getFile());
		FileWriter writer = new FileWriter("/home/ecastellano/Escritorio/memoria.html");

		manager.resolve(reader, getParams(), writer);
		
		writer.flush();
		writer.close();
		System.out.println( writer.toString());
	}
	private static AnnualReportParameters getParams() throws ManagerBeanException {
		AnnualReportParameters params = new AnnualReportParameters();
		params.setParams(new SummaryProviderParameters());
		IManagerBean bean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PERIOD_ID),"2010");
		List<ITransferObject> list = bean.getList(criteria);
		if (list != null && list.size() > 0 ) {
			ITransferObject to = list.get(0);
			params.getParams().setPeriod((Period) to);
		}
		params.getParams().setFromDate(null);
		params.getParams().setToDate(null);
		params.getParams().setDate(new Date());
		params.getParams().setAccountExpression(null);
		params.getParams().setLowerLevelVisible(false);
		params.getParams().setNoTouchedAccountVisible(false);
		params.getParams().setRowsPerPage(20);
		params.getParams().setAccountLevel(5);
		params.getParams().setPreviousPeriodVisible(true);
		params.getParams().setExcludeOperatingEntry(true);
		return params;
	}

	
}
