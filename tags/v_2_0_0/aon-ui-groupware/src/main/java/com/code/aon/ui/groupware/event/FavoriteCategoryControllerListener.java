package com.code.aon.ui.groupware.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FavoriteCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		FavoriteCategory category = (FavoriteCategory)event.getController().getTo();
		category.setUser(user);
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		IController favoriteCategoryController = event.getController();
		try {
			Criteria criteria = favoriteCategoryController.getCriteria();
			criteria.addEqualExpression(favoriteCategoryController.getFieldName(IGroupWareAlias.FAVORITE_CATEGORY_USER_ID), user.getId());
			criteria.addOrder(favoriteCategoryController.getFieldName(IGroupWareAlias.FAVORITE_CATEGORY_DESCRIPTION));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining favorite categories for user=" + user.getLogin(), e);
		}
	}
}