package com.code.aon.ui.company.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.BasicAttachment;
import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class CompanyDisplay {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDisplay.class.getName());
	
	private String companyLabel;
	
	private String logoKey;
	
	private IAttachment logo;
	
	private boolean bigLogo;
	
	private boolean init;

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
		if (! this.init ) {
			init(DataSourceUtil.getDBProperties(), AonUtil.getServerName());
		}
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

	private Connection getConnection( Properties properties ) throws AonException {
		Connection connection = null;
		if ( (properties != null) && (!properties.isEmpty()) ) {
			connection =  ConnectionProvider.getConnection(properties);
		}
		return connection;
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
	
	private void update( String companyName, IAttachment attachment ) {
		this.companyLabel = companyName;
		if ( attachment != null ) {
			this.logo = attachment;
			this.bigLogo = calculateBigLog();
			this.logoKey = attachment.getId().toString();			
		}
	}
	
	public void update( ActionEvent event ) {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);  
		update( controller.obtainCompany().getName(), controller.getLogoAttach());
	}
	
	private Object[] getCompany( Connection connection, Integer domainId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object[]> h = new ArrayHandler();
			return run.query( connection, 
				    "SELECT r.id, r.name FROM company as c, registry as r WHERE c.domain=? AND c.registry = r.id LIMIT 1",
				    h, domainId); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}			
	
	public static BasicAttachment convert( Object[] values ) {
		if (! ArrayUtils.isEmpty(values) ) {
			BasicAttachment logo = new BasicAttachment();
			logo.setId( (Integer) values[0] );
			logo.setDescription( (String) values[1] );
			if ( values[2] != null ) {
				logo.setMimeType( MimeType.values()[(Integer) values[2]] );	
			}
			logo.setData( (byte[]) values[3] );
			return logo;
		}
		return null;
	}
	
	public static BasicAttachment getLogo( Connection connection, Integer domainId, Integer companyId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object[]> h = new ArrayHandler();
			Object[] values = run.query( connection, 
				    "SELECT id, description, mimeType, data FROM rattach WHERE domain = ? and registry =? and type=0 and data is not null LIMIT 1",
				    h, domainId, companyId);
			return convert(values);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}		
	
	public void init( Properties dbProperties, String host ) {
		Connection connection = null;
		try {
			connection = getConnection(dbProperties);
			if ( connection != null ) {
				Integer domainId = DataSourceUtil.getDomain(connection, host);
				if (domainId != null) {
					Object[] values = getCompany(connection, domainId);
					Integer companyId = (Integer) values[0];
					if ( companyId != null ) {
						IAttachment logo = getLogo(connection, domainId, companyId);
						update( (String) values[1], logo);
					}
				}			
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting company name and logo", th );
		} finally {
			DbUtils.closeQuietly(connection);
			this.init = true;
		}
	}
	
	public String getLogoStyle() {
		if ( isBigLogo() ) {
			return "width:200px;";
		}
		return null;
	}
	
}
