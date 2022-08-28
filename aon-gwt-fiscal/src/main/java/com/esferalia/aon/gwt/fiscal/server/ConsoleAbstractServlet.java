package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Schema;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.mchange.v2.c3p0.DataSources;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public abstract class ConsoleAbstractServlet extends HttpServlet {

	private static final long serialVersionUID = 7709579567806482167L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleAbstractServlet.class.getName());
	
	protected ConsoleParams getConsoleParams(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ConsoleParams params = new ConsoleParams()
			.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME))
			.setUser(req.getParameter(IRequestParamsNames.USER));
		try {
			params.setDomain( Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
		} catch (NumberFormatException e) {
			throw new ServletException("El parámetro domain no es un número válido");
		}
		return params.setPrinter(new PrintStream(resp.getOutputStream()));
	}
	
	protected Schema resolveSchema(ConsoleConnectionParams params)  {
		Result<Record> result = params.getDslContext().fetch("SELECT DATABASE();");
		Record rec = result.get(0);
		String schemaName = (String) rec.get(0);
		Schema schema = 
		params.getDslContext().meta()
			.getSchemas()
			.stream()
			.filter(sc -> sc.getName().equals(schemaName))
			.findFirst()
			.orElse(null);
		LOGGER.log(Level.SEVERE, "Schema selected: {0}", (schema == null?"NO SCHEMA":schema.getName()));
		return schema;
	}

	protected ConsoleConnectionParams resolveConnection(ConsoleParams params) throws ClassNotFoundException, AonConnectionException, SQLException {
		String schemaName = null;
		try ( CloseableAONContext ctx = AONContext.getAONContext(params.getOccam())) {
			Result<Record> result = ctx.getDslContext().fetch("SELECT DATABASE();");
			Record rec = result.get(0);
			schemaName = (String) rec.get(0);
		}
		if (schemaName == null) {
			String m = "[ERROR] Schema not found!";
			LOGGER.log(Level.SEVERE, "{0} {1}!",new Object[] {this.getClass().getName(),m});
			throw new IllegalArgumentException( m );
		}
		ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
		Class.forName(ci.getDriverClass(schemaName));
		Properties properties = new Properties();
		properties.setProperty("url", ci.getSchemaUrl(schemaName));
		properties.setProperty("user", ci.getUser(schemaName));
		properties.setProperty("password", ci.getPassword(schemaName));
		properties.setProperty("useSSL", ci.getUseSSL(schemaName));
		properties.setProperty("serverTimezone", ci.getTimeZone(schemaName));
		DataSource dsUnpooled = DataSources.unpooledDataSource(ci.getSchemaUrl(schemaName),properties);
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType( ParamType.INLINED );
		DSLContext dslContext = DSL.using( dsUnpooled.getConnection(), settings);
		ConsoleConnectionParams connectionParams = new ConsoleConnectionParams()
			.setDslContext(dslContext)
			.setConnectionProperties(properties);
		connectionParams.setSchema(resolveSchema(connectionParams));
		return connectionParams;
	}
	
}
