package com.code.aon.ui.company.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompanyDocumentServlet extends HttpServlet {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDocumentServlet.class.getName());
	
	private static final String COMPANY_LOGO = "company.logo";
	
	
	
	private Integer getCompanyId( Connection connection, Integer domainId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			return run.query( connection, 
				    "SELECT registry FROM company WHERE domain=? LIMIT 1", h, domainId); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}
	
	private Integer getCompanyId( HttpServletRequest req) {
		String queryString = req.getQueryString();
		if ( AonStringUtils.isBlank(queryString))
			return null;
		try {
			return Integer.parseInt(queryString);
		} catch ( NumberFormatException e){
			return null;
		}
		
	}
	
	public String generateMD5(byte[] b) {
		return DigestUtils.md5Hex(b);
	}
	
	private Attach getAttachment( HttpServletRequest req ) {		
		Attach attach = new Attach();
		boolean companyLogo = false;
		Integer attachmentId = null;
		String uri = StringUtils.substringBefore(req.getRequestURI(), ";");
		String value = StringUtils.substringAfterLast(uri, "/");
		if ( StringUtils.equals(COMPANY_LOGO, value) ) {
			companyLogo = true;
		} else {
			String idValue = StringUtils.substringBeforeLast(value, "-");
			if ( NumberUtils.isNumber(idValue) ) {
				attachmentId = NumberUtils.toInt(idValue);
			}
		}
		if ( companyLogo || (attachmentId != null) ) {		
			Connection connection = null;
			try {
				String domainName = AonUtil.getServerName(req);
				connection = DatabaseUtil.getConnection(domainName);
				if ( connection != null ) {
					Integer reqDomainId = getCompanyId(req);
					Integer domainId = reqDomainId != null ? reqDomainId : DatabaseUtil.getDomain(connection, domainName);
					
					if (companyLogo) {
						Integer companyId = getCompanyId(connection, domainId);
						attach = AON.getAttach(domainName, domainId, "",
								f -> f.getDomainProperty().eq(domainId)
								.and(f.getAttachModuleProperty().eq(companyId))
								.and(f.getTypeProperty().eq((byte) 0)),
								AttachType.REGISTRY);
						//attachment = getLogo(connection, domainName, domainId, companyId);
					} else {
						final Integer attachId = attachmentId;
						attach = AON.getAttach(domainName, domainId, "",
								f -> f.getIdProperty().eq(attachId),
								AttachType.REGISTRY);
						
						//attachment = getAttachment(connection, domainName, attachmentId);
					}
					if(attach.getDriveId() != null){
						attach.setData(DriveUtils.getByteFile(domainName, domainId, "", attach.getDriveId(), attach.getId()));
					}
					if ( (attachmentId != null) && (attach.getId() != null) && attach.getData() != null ) {
						String md5Value = StringUtils.substringAfterLast(value, "-");
						if (!md5Value.equals(generateMD5(attach.getData()))) {
							attach = null;
						}
					}			
				}				
			} catch ( Throwable th ) {
				LOGGER.error( "Error getting company name and logo", th );
			} finally {
				DatabaseUtil.closeQuietly(connection);
			}
		}
		return attach;
	}
	
	/**
	 * Retrieves the required RegistryAttachment from the database
	 * 
	 * @param req the req
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		Attach attach = getAttachment(req);
		if ( attach != null && attach.getData() != null) {
			Integer length = attach.getData().length;
			ByteArrayInputStream bais = new ByteArrayInputStream(attach.getData());
		    res.addHeader("Content-Disposition","attachment; filename=\"" + attach.getDescription() + "." + attach.getMimeType().getExtension() +"\"");
		    //p_response.setContentType("application/octet-stream");
		    res.setContentType(attach.getMimeType().getName());

		    if (length > 0 && length <= Integer.MAX_VALUE)	
		    	res.setContentLength((int)length);
		    
	        ServletOutputStream out = res.getOutputStream();
	        res.setBufferSize(32768);
	        int bufSize = res.getBufferSize();
	        byte[] buffer = new byte[bufSize];
	        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
	        int bytes;
	        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
	        	out.write(buffer, 0, bytes);
		        	
		        
	        bis.close();
	        bais.close();
	        out.flush();
	        out.close();
		}
	}
}