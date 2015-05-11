package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod180 Certificate Print", urlPatterns = { "/aon_gwt_fiscal/Model180CertificatePrint" })
public class Mod180CertificatePrint extends HttpServlet {
	
	public final String REPORT_TEMPLATE 		= "/com/code/aon/ui/fiscal/report/mod180_retentionCertificate.jasper";
	
	public final String MESSAGES_RESOURCE_BUNDLE 		= "com.esferalia.aon.gwt.fiscal.client.FiscalMessages";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			int id = Integer.parseInt(req.getParameter("mod180"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			Mod180 mod180 = AON.getMod180(domainName, domainId, id);

			commit(conn);

			byte[] data = null;
			if(mod180!=null){
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("mod180", mod180);
				ResourceBundle rb = null;
				try {
					rb = ResourceBundle.getBundle(MESSAGES_RESOURCE_BUNDLE, req.getLocale());
				} catch (Throwable th) {
					// DO NOTHING
				}
				params.put("messages", rb);
				data = createReport(JRReport.class.getResourceAsStream(REPORT_TEMPLATE), params, mod180.getDetails());
			}
			
			resp.setContentType(MimeType.PDF.getName());
			
			String s = mod180.getName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		
			
		    String fileName = "CertificadoRetenciones_" 
					+ "_" + mod180.getYear() 
					+ "_" + sb.toString();
			
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			AonIOUtils.copy(new ByteArrayInputStream(data), resp.getOutputStream());
			
			resp.flushBuffer();
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			AonDatabaseUtil.closeQuietly(conn);
		}
	}
	
	public byte[] createReport(InputStream inputStream, HashMap<String, Object> params, Collection<Mod180Detail> values) throws JRException {
		byte[] data = null; 
		if(values!=null && values.size()>0){
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					inputStream,
					params,
					new JRBeanCollectionDataSource(values));
			data = JasperExportManager.exportReportToPdf(jasperPrint);
		}
		return data;
	}

	

}

