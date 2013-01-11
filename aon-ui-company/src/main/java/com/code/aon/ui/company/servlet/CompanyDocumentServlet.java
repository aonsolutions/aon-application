package com.code.aon.ui.company.servlet;

import static com.code.aon.ui.common.ICommonConstants.SKIP_LDAP;
import static com.code.aon.ui.company.controller.CompanyDisplay.HIBERNATE_CONFIGURATION_FILE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.DataSourceUtil;
import com.code.aon.ui.util.DownloadUtil;

public class CompanyDocumentServlet extends HttpServlet {

	private static final long serialVersionUID = -7223720894588071587L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDocumentServlet.class.getName());
	
	private static final String COMPANY_LOGO = "company.logo";
	
	private boolean skipLdap;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
		this.skipLdap = BooleanUtils.toBoolean(getServletContext().getInitParameter(SKIP_LDAP));        
    }
	
	private void finishDownload( HttpServletResponse response, OutputStream out ) {
		IOUtils.closeQuietly(out);
		if ( response != null ) {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e );
			}	
		}
	}
	
	private MimeType getMimeType( IAttachment attachment ) {
		MimeType mt = MimeResolver.getMimeTypeByExtension(attachment.getDescription());
		if ( mt == null ) {
			mt =  MimeResolver.getMimeType(attachment.getData());
		}
		return mt;		
	}

	private String getName( IAttachment attachment, MimeType type ) {
		String name = attachment.getDescription();
		if ( StringUtils.isEmpty(name) ) {
			name = "image-" + attachment.getId();
		}
		String extension = FilenameUtils.getExtension(name);
		if ( StringUtils.isEmpty(extension) && (type != null) ) {
			name += "." + type.getExtension();
		}
		return name;		
	}
	
	private Properties getConnectionProperties( HttpServletRequest req ) {
		String server = req.getServerName();
		String context = req.getContextPath(); 
		return DataSourceUtil.getDBProperties(server, context, skipLdap);
	}
	
	private Configuration getConfiguration( HttpServletRequest req ) {
		AnnotationConfiguration configuration = null;
		Properties properties = getConnectionProperties(req);
		if ( (properties != null) && (!properties.isEmpty()) ) {
			configuration = new AnnotationConfiguration();
			configuration.addProperties(properties);
			configuration.configure(HIBERNATE_CONFIGURATION_FILE);			
		}
		return configuration;
	}	
	
	public static Integer getCompanyId( StatelessSession session, Integer domainId ) {
		Query query = session.createQuery("SELECT id FROM Company c WHERE c.domain = ?");
		return (Integer) query.setInteger(0, domainId).uniqueResult();
	}
	
	private RegistryAttachment getAttachment( HttpServletRequest req ) {
		RegistryAttachment attachment = null;
		boolean companyLogo = false;
		Integer attachmentId = null;
		String uri = StringUtils.substringBefore(req.getRequestURI(), ";");
		String value = StringUtils.substringAfterLast(uri, "/");
		if ( StringUtils.equals(COMPANY_LOGO, value) ) {
			companyLogo = true;
		} else {
			String idValue = StringUtils.substringBefore(value, "-");
			if ( NumberUtils.isNumber(idValue) ) {
				attachmentId = NumberUtils.toInt(idValue);
			}
		}
		if ( companyLogo || (attachmentId != null) ) {		
			SessionFactory factory = null;
			try {
				Configuration configuration = getConfiguration(req);
				if ( configuration != null ) {
					factory = configuration.buildSessionFactory();
					StatelessSession session = factory.openStatelessSession();
					Criteria criteria = session.createCriteria(RegistryAttachment.class);
					if ( companyLogo ) {
						Integer domainId = DataSourceUtil.getDomain(session.connection(), req.getServerName(), skipLdap);
						Integer companyId = getCompanyId(session, domainId);
						criteria.add(Restrictions.eq("registry.id", companyId));
						criteria.add(Restrictions.eq("registryAttachmentType", RegistryAttachmentType.LOGO));
					} else {
						criteria.add(Restrictions.eq("id", attachmentId));
					}
					criteria.add(Restrictions.isNotNull("data"));
					attachment = (RegistryAttachment) criteria.uniqueResult();
					if ( attachmentId != null ) {
						String md5Value = StringUtils.substringAfter(value, "-");
						if (! StringUtils.equals(attachment.getMD5(), md5Value) ) {
							attachment = null;
						}
					}
					session.close();				
				}				
			} catch ( Throwable th ) {
				LOGGER.error( "Error getting company name and logo", th );
			} finally {
				if ( factory != null ) {
					factory.close();	
				}
			}
		}
		return attachment;
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
		OutputStream out = null;
		try {
			RegistryAttachment attachment = getAttachment(req);
			if ( attachment != null ) {
				MimeType type = getMimeType(attachment);
				String name = getName(attachment, type);
				out = DownloadUtil.initDownload(res, name, type, attachment.getSize());
				DownloadUtil.setCacheable(res);
				InputStream in = new ByteArrayInputStream(attachment.getData());
				IOUtils.copyLarge(in, out);
			}
		} catch (Throwable th) {
			LOGGER.error( th.getMessage(), th );
			throw new ServletException(th.getMessage(), th);
		} finally {
			finishDownload(res, out);
		}
	}

}