package com.code.aon.ui.manager.controller;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.dao.IManagerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.manager.converter.TransferObjectConverter;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class DomainDBConnectionController extends LdapBasicController implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainDBConnectionController.class);
	
	private final static String DB_NAME_PREFFIX = "aon-";
	
	private List<SelectItem> dataSources;
	
	private boolean createDB;
	
	private Converter converter;
	
	public DomainDBConnectionController() {
		this.createDB = true;
	}
	
	@Override
	public void updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getDomainBDsDN(domain);
		getLdapDAO().setBaseDN(baseDN);
		updateDataSources();
	}	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DBConnnection> getDBConnnections() throws ManagerBeanException {
		DBConnnection dbc = getManager().getCurrentDBConnection();
		List<DBConnnection> dbcs = (List) getModel().getWrappedData();
		if ( (dbc != null) && (dbcs.size() > 1) ) {
			List<DBConnnection> list = new LinkedList<DBConnnection>();
			for( DBConnnection connection : dbcs ) {
				if (! dbc.equals(connection) ) {
					list.add(connection);
				}
			}
			list.add( dbc );
			return list;
		}
		return dbcs;
	}	
	
	public List<SelectItem> getDataSources() {
		return this.dataSources;
	}

	public void updateDataSources() {
		this.dataSources = new LinkedList<SelectItem>();
		try {
			initializeModel();
			for (DBConnnection dbc : getDBConnnections()) {
				SelectItem item = new SelectItem(dbc, dbc.getCommonName() );
				this.dataSources.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
	
	public DBConnnection getMasterConnection() throws ManagerBeanException {
		Properties properties = getManager().getProperties();
		String masterDataSource = properties.getProperty(IManagerAlias.DB_CONNECTION_COMMON_NAME);
		List<DBConnnection> list = getDBConnnections();
		for (DBConnnection dbc : list ) {
			if ( dbc.getCommonName().equals(masterDataSource) ) {
				return dbc;
			}
		}
		return ( list.isEmpty() ) ? null : list.get(0);
	}
	
	public boolean isCreateDB() {
		return createDB;
	}

	public void setCreateDB(boolean createDB) {
		this.createDB = createDB;
	}

	@Override
	protected boolean isUsed(ILdapTransferObject to) throws ManagerBeanException {
		IController controller = (IController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		IManagerBean bean = controller.getManagerBean();
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IManagerAlias.DOMAIN_APPLICATION_DATA_SOURCE);
		criteria.addEqualExpression(alias, to.getId());
		int count = bean.getCount(criteria);
		return ( count > 0 );
	}

	public Converter getConverter() {
		if ( converter == null ) {
			IController controller = (IController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
			this.converter = new TransferObjectConverter(controller);			
		}
		return converter;
	}	
	
	private String formatDBName( String name ) {
		String dbName = StringUtils.replace(name, ".", "-");
		return StringUtils.left(DB_NAME_PREFFIX + dbName, 64);
	}
	
	public void init( DBConnnection dbc, String name ) {
		Properties properties = getManager().getProperties();
		dbc.setCommonName( properties.getProperty(IManagerAlias.DB_CONNECTION_COMMON_NAME) );
		dbc.setDriverClassName( properties.getProperty(IManagerAlias.DB_CONNECTION_DRIVER_CLASS_NAME) );
		dbc.setUid( properties.getProperty(IManagerAlias.DB_CONNECTION_UID) );
		dbc.setUserPasswordString( properties.getProperty(IManagerAlias.DB_CONNECTION_USER_PASSWORD) );
		String text = properties.getProperty(IManagerAlias.DB_CONNECTION_LABELED_URI);
		String dbName = formatDBName(name);
		String url = MessageFormat.format( text, dbName );
		dbc.setLabeledURI(url);			
	}

	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
}
