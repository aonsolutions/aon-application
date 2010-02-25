package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.dao.IAuditAlias;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class FavoriteOptionController.
 */
public class ActionFavoriteController implements IAuditConstants {

	private static final String OPTIONS_ATTRIBUTE = "options";
	
	private static final String ACTIONS_ATTRIBUTE = "actions";

	private final static Logger LOGGER = LoggerFactory.getLogger(ActionFavoriteController.class);
	
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/audit/controller/";
	
	private static final String FAVORITES_TEMPLATE = "favorites.xhtml.vm";
	
	private static final String FAVORITES_MENU_TEMPLATE = "favoritesMenu.xhtml.vm";
	
	private static final String RECENTS_TEMPLATE = "recents.xhtml.vm";
	
	private List<ApplicationOption> options;
	
	private List<ApplicationOption> favorites;
	
	private VelocityHelper velocityHelper;
	
	private String template;
	
	private String menuTemplate;
	
	public ActionFavoriteController() {
		this.favorites = loadFavorites();
	}

	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}
	
	private List<ITransferObject> getList( IManagerBean bean, int size ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		User user = UserUtils.getInstance().getLoggedUser();
		criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_FAVORITE_USER_ID), user.getId());
		Integer appId = getOptionController().getApplication().getId();
		criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_FAVORITE_ACTION_APPLICATION_ID), appId);
		criteria.addOrder(bean.getFieldName(IAuditAlias.ACTION_FAVORITE_POSITION));
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.size() > size ) {
			for( int i = list.size()-1; i+1 > size; i-- ) {
				bean.remove( list.get(i) );
			}
		} else if ( list.size() < size ) {
			for( int i = list.size(); i < size; i++ ) {
				ActionFavorite af = new ActionFavorite();
				af.setUser(user);
				af.setPosition(i);
				list.add( af );
			}			
		}
		return list;
	}
	
	public void onInit( ActionEvent event ) {
		this.options = new ArrayList<ApplicationOption>( getOptionController().getOptions() );
		this.options.removeAll(this.favorites);
	}
	
	public void accept( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionFavorite.class);
			List<ITransferObject> list = getList(bean, this.favorites.size());
			int i = 0;
			for( ApplicationOption option : this.favorites ) {
				ActionFavorite af = (ActionFavorite) list.get(i++);
				Action action = getOptionController().getAction(option.getAction());
				if ( (af.getAction() == null) || (! af.getAction().equals(action)) ) {
					af.setAction(action);
					bean.insertOrUpdate(af);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating favorite action list", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
		this.template = null;
		this.menuTemplate = null;
	}
	
	public void setFavorites(List<ApplicationOption> favorites) {
		this.favorites = favorites;
	}

	public List<ApplicationOption> getFavorites() {
		return this.favorites;
	}
	
	public List<ApplicationOption> getOptions() {
		return options;
	}

	public void setOptions(List<ApplicationOption> options) {
		this.options = options;
	}

	private List<ApplicationOption> loadFavorites() {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionFavorite.class);
			Criteria criteria = new Criteria();
			User user = UserUtils.getInstance().getLoggedUser();
			criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_FAVORITE_USER_ID), user.getId());
			Integer appId = getOptionController().getApplication().getId();
			criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_FAVORITE_ACTION_APPLICATION_ID), appId);
			criteria.addOrder(bean.getFieldName(IAuditAlias.ACTION_FAVORITE_POSITION));
			List<ITransferObject> actionFavorites = bean.getList(criteria);
			if (! actionFavorites.isEmpty() ) {
				Map<String,ApplicationOption> options = getOptionController().getOptionMap();
				for( ITransferObject to : actionFavorites ) {
					String action = ((ActionFavorite) to).getAction().getName();
					ApplicationOption option = options.get(action);
					if ( option != null ) {
						list.add(option);
					} else {
						LOGGER.warn( "Action {} not found in the menu", action );
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading favorites", e);
		}
		return list;		
	}

	@SuppressWarnings("unchecked")
	private List<ActionEntry> getLastExecutedActions( int count ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionEntry.class);
			Criteria criteria = new Criteria();
			User user = UserUtils.getInstance().getLoggedUser();
			criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_ENTRY_SESSION_USER_ID), user.getId());
			Integer appId = getOptionController().getApplication().getId();
			criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_ENTRY_SESSION_APPLICATION_ID), appId);			
			criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_ENTRY_ACTION_MENU), true);
			criteria.addOrder(bean.getFieldName(IAuditAlias.ACTION_ENTRY_EXECUTION_DATE), false);
			return (List) bean.getList(criteria, 0, count);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading favorites", e);
		}
		return null;		
	}

	private List<ApplicationOption> getLastExecuted( List<ActionEntry> actions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! actions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( ITransferObject to : actions ) {
				String action = ((ActionEntry) to).getAction().getName();
				ApplicationOption option = options.get(action);
				if ( option != null ) {
					list.add(option);
				} else {
					LOGGER.warn( "Action {} not found in the menu", action );
				}
			}
		}
		return list;		
	}
	
	private VelocityHelper getVelocityHelper() {
		if ( this.velocityHelper == null ) {
			this.velocityHelper = new VelocityHelper();
			try {
				this.velocityHelper.init( VM_PATH_DEFAULT );
			} catch (Exception e) {
				LOGGER.error( "Velocity engine could not be initialized", e );
			}
		}
		return this.velocityHelper;
	}
	
	private String getTemplate( String template, Object ... objects  ) throws IOException {
		try {
			TemplateHelper th = getVelocityHelper().getTemplateHelper();
			for( int i = 0; i < objects.length; i++ ) {
				th.putInContext( (String) objects[i++], objects[i]);
			}
			return th.processTemplate(template);
		} catch (AonException e) {
			LOGGER.error( e.getMessage(), e);
		}		
		return null;
	}
	
	public String getTemplate() throws IOException {
		if ( this.template == null ) {
			this.template = getTemplate(FAVORITES_TEMPLATE, OPTIONS_ATTRIBUTE, getFavorites());
		}
		return this.template;
	}

	public String getMenuTemplate() throws IOException {
		if ( this.menuTemplate == null ) {
			this.menuTemplate = getTemplate(FAVORITES_MENU_TEMPLATE, OPTIONS_ATTRIBUTE, getFavorites());
		}
		return this.menuTemplate;
	}
	
	public String getLastExecutedsTemplate() throws IOException {
		List<ActionEntry> actions = getLastExecutedActions(10);
		List<ApplicationOption> options = getLastExecuted(actions);
		return getTemplate(RECENTS_TEMPLATE, OPTIONS_ATTRIBUTE, options, ACTIONS_ATTRIBUTE, actions );
	}
	
}
