package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.console.IsolateDomain;
import com.mchange.v2.c3p0.DataSources;

import net.aonsolutions.core.pool.ConnectionInfo;

@WebServlet(name = "Console Domain Isolate Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/DomainIsolateServlet" })
public class DomainIsolateServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(DomainIsolateServlet.class.getName());
	
	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintStream out = null;
		try {
			out = new PrintStream(resp.getOutputStream());
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String newDomainName = req.getParameter(IRequestParamsNames.NEW_DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domain = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			LOGGER.log(Level.INFO, "DomainIsolateServlet domain \"{0}\" to \"{1}\"", new String[] {domainName,newDomainName});
			String schemaName = null;
			try ( CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
				Result<Record> result = ctx.getDslContext().fetch("SELECT DATABASE();");
				Record rec = result.get(0);
				schemaName = (String) rec.get(0);
			}
			if (schemaName != null) {
				ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
				Class.forName(ci.getDriverClass(schemaName));
				Properties properties = new Properties();
				properties.setProperty("user", ci.getUser(schemaName));
				properties.setProperty("password", ci.getPassword(schemaName));
				properties.setProperty("useSSL", ci.getUseSSL(schemaName));
				properties.setProperty("serverTimezone", ci.getTimeZone(schemaName));
				DataSource dsUnpooled = DataSources.unpooledDataSource(ci.getSchemaUrl(schemaName),properties);
				Settings settings = new Settings();
				settings.setRenderSchema(false);
				settings.setParamType( ParamType.INLINED );
				DSLContext dslContext = DSL.using( dsUnpooled.getConnection(), settings);
				ConsoleParams params = new ConsoleParams()
						.setDomainName(domainName)
						.setNewDomainName(newDomainName)
						.setUser(user)
						.setDslContext(dslContext)
						.setPrinter(out);
				IsolateDomain.isolate(params);
				
//				CheckDomainIntegrity.check(params);
			} else {
				String m = "[ERROR] Schema not found!";
				out.println(m);
				LOGGER.log(Level.SEVERE, "DomainIsolateServlet {0}!",m);
			}
			resp.flushBuffer();
		} catch (Exception e) {
			if(out != null) {
				try {
					out.println(e.getMessage());
					out.println();
					resp.flushBuffer();
				} catch (IOException ioe) {
					// Nothing
				}
			}
			LOGGER.log(Level.SEVERE, "DomainIsolateServlet {0}!",e.getMessage());
		} finally {
			if(out != null) {
				try {
					out.println("Request ended.");
					out.println();
					resp.flushBuffer();
				} catch (IOException ioe) {
					// Nothing
				}
			}
			LOGGER.log(Level.INFO, "DomainIsolateServlet finished!");
		}

	}
	
}
