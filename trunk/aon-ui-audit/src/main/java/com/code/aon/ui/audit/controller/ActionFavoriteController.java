package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.controller.ActionMoreUsedController.ActionMoreUsed;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * The Class FavoriteOptionController.
 */
public class ActionFavoriteController implements IAuditConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ActionFavoriteController.class);
	
	private final static int FAVORITE_SIZE = 20;

	private List<ApplicationOption> options;
	
	private List<ApplicationOption> favorites;
	
	private List<ApplicationOption> favoriteAndMoreUsedOptions;
	
	private String template;
	
	private String menuTemplate;
	
	public ActionFavoriteController() {
		initFavorites();
		initFavoriteAndMoreUsedOptions();
	}

	private AuditController getAuditController() {
		return (AuditController) AonUtil.getRegisteredBean(AUDIT_CONTROLLER_NAME);
	}
	
	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}

	private ActionDeniedController getDeniedController() {
		return (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
	}
	
	private List<ITransferObject> getList( IManagerBean bean, int size ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		User user = UserUtils.getInstance().getLoggedUser();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_FAVORITE_USER_ID), user.getId());
		Integer appId = getAuditController().getApplication().getId();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_FAVORITE_ACTION_APPLICATION_ID), appId);
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACTION_FAVORITE_POSITION));
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
		this.options = new ArrayList<ApplicationOption>( getDeniedController().getOptions(false) );
		this.options.removeAll(this.favorites);
		Collection<ApplicationOption> deniedList = getDeniedController().getDeniedOptions();
		this.options.removeAll(deniedList);
	}
	
	public void accept( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionFavorite.class);
			List<ITransferObject> list = getList(bean, this.favorites.size());
			int i = 0;
			for( ApplicationOption option : this.favorites ) {
				ActionFavorite af = (ActionFavorite) list.get(i++);
				Action action = getAuditController().getAction(option.getAction());
				if ( (af.getAction() == null) || (! af.getAction().equals(action)) ) {
					af.setAction(action);
					bean.insertOrUpdate(af);
				}
			}
			initFavoriteAndMoreUsedOptions();
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
	
	public List<ApplicationOption> getFavoriteAndMoreUsedOptions() {
		return favoriteAndMoreUsedOptions;
	}

	public List<ApplicationOption> getOptions() {
		return options;
	}

	public void setOptions(List<ApplicationOption> options) {
		this.options = options;
	}

	private void initFavorites() {
		this.favorites = new ArrayList<ApplicationOption>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionFavorite.class);
			Criteria criteria = new Criteria();
			User user = UserUtils.getInstance().getLoggedUser();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_FAVORITE_USER_ID), user.getId());
			Integer appId = getAuditController().getApplication().getId();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_FAVORITE_ACTION_APPLICATION_ID), appId);
			criteria.addOrder(bean.getFieldName(IEntityAlias.ACTION_FAVORITE_POSITION));
			List<ITransferObject> actionFavorites = bean.getList(criteria);
			if (! actionFavorites.isEmpty() ) {
				Map<String,ApplicationOption> options = getOptionController().getOptionMap();
				for( ITransferObject to : actionFavorites ) {
					String action = ((ActionFavorite) to).getAction().getName();
					ApplicationOption option = options.get(action);
					if ( (option != null) && (!getDeniedController().isDenied(option)) ) {
						if ( option.isRendered() ) {
							this.favorites.add(option);	
						}
					} else {
						bean.remove(to);
						LOGGER.warn( "{} favorite removed", action );
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading favorites", e);
		}
	}

	public String getTemplate() throws IOException {
		if ( this.template == null ) {
			this.template = getOptionController().getTemplate(OPTIONS_TEMPLATE,
					PREFFIX_VM, FAVORITE_PREFFIX,
					OPTIONS_VM, getFavoriteAndMoreUsedOptions());
		}
		return this.template;
	}

	public String getMenuTemplate() throws IOException {
		if ( this.menuTemplate == null ) {
			this.menuTemplate = getOptionController().getTemplate(MENU_ITEM_TEMPLATE,
					PREFFIX_VM, FAVORITE_PREFFIX,
					OPTIONS_VM, getFavoriteAndMoreUsedOptions());
		}
		return this.menuTemplate;
	}
	
	private void initFavoriteAndMoreUsedOptions() {
		this.favoriteAndMoreUsedOptions = new LinkedList<ApplicationOption>(this.favorites);
		ActionMoreUsedController amuc =  (ActionMoreUsedController) AonUtil.getRegisteredBean(ACTION_MORE_USED_CONTROLLER_NAME);
		List<ActionMoreUsed> actions = amuc.getMoreUsed(-1);
		for (ActionMoreUsed amu : actions) {
			ApplicationOption appOption = amu.getOption();
			if (! this.favoriteAndMoreUsedOptions.contains(appOption) ) {
				this.favoriteAndMoreUsedOptions.add(appOption);	
				if ( this.favoriteAndMoreUsedOptions.size() >= FAVORITE_SIZE ) {
					break;
				}
			}
		}
	}	

}