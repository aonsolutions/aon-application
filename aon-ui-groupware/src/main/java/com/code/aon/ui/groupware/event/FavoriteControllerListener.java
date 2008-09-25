package com.code.aon.ui.groupware.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Favorite;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.FavoriteController;

public class FavoriteControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		Favorite favorite = (Favorite)event.getController().getTo();
		favorite.setUser(user);
		favorite.setUrl(validateUrl(favorite.getUrl()));
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Favorite favorite = (Favorite)event.getController().getTo();
		favorite.setUrl(validateUrl(favorite.getUrl()));
	}

	private String validateUrl(String url) {
		if(!url.startsWith("http://")){
			url = "http://" + url;
		}
		return url;
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		FavoriteController favoriteController = (FavoriteController)event.getController();
		try {
			Criteria criteria = favoriteController.getCriteria();
			criteria.addEqualExpression(favoriteController.getFieldName(IGroupWareAlias.FAVORITE_USER_ID), user.getId());
			criteria.addOrder(favoriteController.getFieldName(IGroupWareAlias.FAVORITE_FAVORITE_CATEGORY_ID));
			criteria.addOrder(favoriteController.getFieldName(IGroupWareAlias.FAVORITE_DESCRIPTION));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining favorites for user=" + user.getLogin(), e);
		}
	}
}