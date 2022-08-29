package com.esferalia.aon.gwt.fiscal.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.jooq.Schema;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;

public abstract class ConsoleAbstractServlet extends HttpServlet {

	private static final long serialVersionUID = 7709579567806482167L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleAbstractServlet.class.getName());
	
	public ConsoleConnectionParams resolveConnection(String schemaName, String domainName) {
		AONContext ctx = AONContext.getAONContext(schemaName);
		Schema schema = ctx.getDslContext().meta()
			.getSchemas()
			.stream()
			.filter(sc -> sc.getName().equals(schemaName))
			.findFirst()
			.orElse(null);
		if ( schema == null) {
			String m = "[ERROR] Schema not found!";
			LOGGER.log(Level.SEVERE, "{0} {1}!",new Object[] {this.getClass().getName(),m});
			throw new IllegalArgumentException( m );
		}
		return new ConsoleConnectionParams()
				.setSchema(schema)
				.setAONContext(ctx);
	}

	protected Schema resolveSchema(AONContext ctx)  {
		String schemaName = (String) ctx.getDslContext()
			.fetch("SELECT DATABASE();")
			.get(0)
			.get(0);
		Schema schema = ctx.getDslContext().meta()
			.getSchemas()
			.stream()
			.filter(sc -> sc.getName().equals(schemaName))
			.findFirst()
			.orElse(null);
		LOGGER.log(Level.SEVERE, "Schema selected: {0}", (schema == null?"NO SCHEMA":schema.getName()));
		return schema;
	}

/*
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

	protected ConsoleConnectionParams resolveConnections(ConsoleParams params) throws ClassNotFoundException, AonConnectionException, SQLException {
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
*/
}
