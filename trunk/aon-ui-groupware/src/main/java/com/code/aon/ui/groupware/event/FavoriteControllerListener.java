package com.code.aon.ui.groupware.event;

import java.net.URI;
import java.net.URISyntaxException;

import com.code.aon.config.User;
import com.code.aon.groupware.Favorite;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FavoriteControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getInstance().getLoggedUser();
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
		try {
			URI uri = new URI(url);
			if(uri.getScheme() == null){
				url = "http://" + url;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return url;
	}

}