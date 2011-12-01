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
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dbutils.AonSQLFile;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.dao.IManagerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;
import com.code.aon.ui.webmail.converter.LdapTransferObjectConverter;

public class DomainDBConnectionController extends LdapBasicController implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainDBConnectionController.class);
	
	private final static String DB_NAME_PREFFIX = "aon-";
	
	private boolean createDB;
	
	private Converter converter;
	
	private boolean aonDB;
	
	private String selectedTab;
	
	public DomainDBConnectionController() {
		this.createDB = true;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isAonDB() {
		return aonDB;
	}

	public void setAonDB(boolean aonDB) {
		this.aonDB = aonDB;
	}	
	
	public DBConnnection getDBConnnection() {
		return (DBConnnection) getTo();
	}	
		
	@Override
	public void updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getDomainBDsDN(domain);
		getLdapDAO().setBaseDN(baseDN);
	}	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DBConnnection> getDBConnnections() throws ManagerBeanException {
		DBConnnection dbc = getDBManager().getCurrentDBConnection();
		List<DBConnnection> dbcs = (List) getManagerBean().getList(null);
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
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			for (DBConnnection dbc : getDBConnnections()) {
				SelectItem item = new SelectItem(dbc, dbc.getCommonName() );
				list.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return list;
	}	
	
	public DBConnnection getMasterConnection() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IManagerAlias.DB_CONNECTION_COMMON_NAME), AonSQLFile.AON_MASTER);
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (! list.isEmpty() ) {
			return (DBConnnection) list.get(0);
		}
		return null;
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
			this.converter = new LdapTransferObjectConverter(controller);			
		}
		return converter;
	}	
	
	private String formatDBName( String name ) {
		String dbName = StringUtils.replace(name, ".", "-");
		return StringUtils.left(DB_NAME_PREFFIX + dbName, 64);
	}
	
	public void init( DBConnnection dbc, String name ) {
		ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		Properties properties = manager.getProperties();
		dbc.setCommonName( properties.getProperty(IManagerAlias.DB_CONNECTION_COMMON_NAME) );
		dbc.setDriverClassName( properties.getProperty(IManagerAlias.DB_CONNECTION_DRIVER_CLASS_NAME) );
		dbc.setUid( properties.getProperty(IManagerAlias.DB_CONNECTION_UID) );
		dbc.setUserPasswordString( properties.getProperty(IManagerAlias.DB_CONNECTION_USER_PASSWORD) );
		String text = properties.getProperty(IManagerAlias.DB_CONNECTION_LABELED_URI);
		String dbName = formatDBName(name);
		String url = MessageFormat.format( text, dbName );
		dbc.setLabeledURI(url);			
	}

	private DBManagerController getDBManager() {
		return (DBManagerController) AonUtil.getRegisteredBean(DB_MANAGER_CONTROLLER_NAME);
	}
	
	public boolean updateAonDBConnection( DBConnnection dbc ) {
		boolean aonDB = false;
		if ( (dbc != null) && (dbc.getId() != null) ) {
			DBManagerController dbManager = getDBManager();
			if ( dbManager.exists(dbc) ) {
				dbManager.changeDbConnection(dbc);
				if ( dbManager.isAonDB(dbc) ) {
					aonDB = true;
					IController uwg = FormUtil.getController(ConfigConstants.WORK_GROUP);
					uwg.onSearch(null);			
					IController scopes = FormUtil.getController(ConfigConstants.SCOPE);
					scopes.onSearch(null);			
				}
			}
		}		
		return aonDB;
	}		
	
}
