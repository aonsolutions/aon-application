package com.code.aon.ui.company.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class CompanyDisplay {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDisplay.class.getName());
	
	public static final String HIBERNATE_CONFIGURATION_FILE = "/hibernate.company.cfg.xml";
	
	private String companyLabel;
	
	private String logoKey;
	
	private IAttachment logo;
	
	private boolean bigLogo;
	
	public CompanyDisplay() {
		this(AonUtil.getServerName(), AonUtil.isSkipLdap(), null );
	}

	public CompanyDisplay( String host, boolean skipLdap, Properties dbProperties ) {
		init(host, skipLdap, dbProperties);
	}
	
	public String getCompanyLabel() {
		return companyLabel;
	}

	public void setCompanyLabel(String companyLabel) {
		this.companyLabel = companyLabel;
	}

	public byte[] getCompanyLogo() {
		return logo.getData();
	}

	public boolean isLogoDefined() {
		return (getLogo() != null) && (! ArrayUtils.isEmpty(getCompanyLogo()));
	}
	
	public IAttachment getLogo() {
		return logo;
	}

	public String getLogoKey() {
		return logoKey;
	}

	public void setLogoKey(String logoKey) {
		this.logoKey = logoKey;
	}

	public boolean isShow() {
		return !StringUtils.isEmpty(this.companyLabel) || isLogoDefined();
	}
	
	public boolean isBigLogo() {
		return bigLogo;
	}

	public void setBigLogo(boolean bigLogo) {
		this.bigLogo = bigLogo;
	}

	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if ( isLogoDefined() ) {
			out.write( getCompanyLogo() );
		}
	}

	private Configuration getConfiguration( Properties dbs ) {
		AnnotationConfiguration configuration = null;
		Properties properties = (dbs != null) ? dbs : DataSourceUtil.getDBProperties();
		if ( (properties != null) && (!properties.isEmpty()) ) {
			configuration = new AnnotationConfiguration();
			configuration.addProperties(properties);
			configuration.configure(HIBERNATE_CONFIGURATION_FILE);			
		}
		return configuration;
	}
	
	private boolean calculateBigLog() {
		if ( isLogoDefined() ) {
			InputStream in = new ByteArrayInputStream( getCompanyLogo() );
			try {
				BufferedImage image = ImageIO.read(in);
				return (image.getWidth() > 200);
			} catch (Throwable th) {
				LOGGER.error( "Error reading logo", th);
			}
		}
		return true;
	}
	
	public void update( Company company, RegistryAttachment attachment ) {
		this.companyLabel = company.getName();
		if ( attachment != null ) {
			this.logo = attachment;
			this.bigLogo = calculateBigLog();
			this.logoKey = attachment.getId().toString();			
		}
	}
	
	public void update( ActionEvent event ) {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		update( (Company) controller.getTo(), controller.getLogoAttach());
	}
	
	private void init( String host, boolean skipLdap, Properties dbProperties ) {
		SessionFactory factory = null;
		try {
			Configuration configuration = getConfiguration(dbProperties);
			if ( configuration != null ) {
				factory = configuration.buildSessionFactory();
				StatelessSession session = factory.openStatelessSession();
				Criteria companyCriteria = session.createCriteria(Company.class);
				Integer domainId = DataSourceUtil.getDomain(session.connection(), host, skipLdap);
				if (domainId != null) {
					companyCriteria.add(Restrictions.eq("domain", domainId));	
					List<?> companyList = companyCriteria.list();
					if (! companyList.isEmpty() ) {
						Company company = (Company) companyList.get(0); 
						Criteria logoCriteria = session.createCriteria(RegistryAttachment.class);
						logoCriteria.add(Restrictions.eq("domain", domainId));
						logoCriteria.add(Restrictions.eq("registry.id", company.getId()));
						logoCriteria.add(Restrictions.eq("registryAttachmentType", RegistryAttachmentType.LOGO));
						logoCriteria.add(Restrictions.isNotNull("data"));
						List<?> logoList = logoCriteria.list();
						RegistryAttachment logo = logoList.isEmpty() ? null : (RegistryAttachment) logoList.get(0);
						update(company, logo);
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
	
}
