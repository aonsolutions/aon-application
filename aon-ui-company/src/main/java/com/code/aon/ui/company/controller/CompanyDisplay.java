package com.code.aon.ui.company.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.company.Company;
import com.code.aon.dao.ldap.util.AonLdapUtil;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;

public class CompanyDisplay {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDisplay.class.getName());
	
	private static final String DOMAIN_RESOLVER = "domainResolver";
	
	private static final String HIBERNATE_CONFIGURATION_FILE = "/hibernate.company.cfg.xml";
	
	private String companyLabel;
	
	private String logoKey;
	
	private byte[] companyLogo;
	
	private boolean show;
	
	private boolean bigLogo;
	
	public CompanyDisplay() {
		show = init();
	}

	public String getCompanyLabel() {
		return companyLabel;
	}

	public void setCompanyLabel(String companyLabel) {
		this.companyLabel = companyLabel;
	}

	public byte[] getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(byte[] companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getLogoKey() {
		return logoKey;
	}

	public void setLogoKey(String logoKey) {
		this.logoKey = logoKey;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	
	public boolean isBigLogo() {
		return bigLogo;
	}

	public void setBigLogo(boolean bigLogo) {
		this.bigLogo = bigLogo;
	}

	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if (! ArrayUtils.isEmpty(companyLogo) ) {
			out.write( companyLogo );
		}
	}

	private Configuration getConfiguration() {
    	DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER);
    	String domain = resolver.getDomain();
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	String context = ctx.getExternalContext().getRequestContextPath();
		Properties properties = AonLdapUtil.getDBProperties(domain, context);
		
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addProperties(properties);
		configuration.configure(HIBERNATE_CONFIGURATION_FILE);
		
		return configuration;
	}
	
	private boolean calculateBigLog() {
		if ( ! ArrayUtils.isEmpty(this.companyLogo) ) {
			InputStream in = new ByteArrayInputStream(this.companyLogo);
			try {
				BufferedImage image = ImageIO.read(in);
				return (image.getWidth() > 200);
			} catch (Throwable th) {
				LOGGER.error( "Error reading logo", th);
			}
		}
		return true;
	}
	
	@SuppressWarnings({ "unchecked"})
	private boolean init() {
		try {
			Configuration configuration = getConfiguration();
			SessionFactory factory =  configuration.buildSessionFactory();
			
			StatelessSession session = factory.openStatelessSession();
			Criteria companyCriteria = session.createCriteria(Company.class);
			List companyList = companyCriteria.list();
			if (! companyList.isEmpty() ) {
				Company company = (Company) companyList.get(0); 
				this.companyLabel = company.getName();
				Criteria logoCriteria = session.createCriteria(RegistryAttachment.class);
				logoCriteria.add(Restrictions.eq("registry.id", company.getId()));
				logoCriteria.add(Restrictions.eq("registryAttachmentType", RegistryAttachmentType.LOGO));
				logoCriteria.add(Restrictions.isNotNull("data"));
				List logoList = logoCriteria.list();
				if (! logoList.isEmpty() ) {
					RegistryAttachment logo = (RegistryAttachment) logoList.get(0);
					this.companyLogo = logo.getData();
					this.bigLogo = calculateBigLog();
					this.logoKey = logo.getId().toString();
				}
			}
			session.close();
			return true;
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting company name and logo", th );
		}
		return false;
	}
	
}
