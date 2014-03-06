package com.code.aon.ui.groupware.event;

import com.code.aon.common.AonVersion;
import com.code.aon.config.User;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FavoriteCategoryControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FavoriteCategory category = (FavoriteCategory)event.getController().getTo();
		User user = UserUtils.getInstance().getLoggedUser();
		category.setUser(user);
	}

}