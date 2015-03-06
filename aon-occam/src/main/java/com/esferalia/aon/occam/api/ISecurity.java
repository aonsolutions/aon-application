package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.security.User;

public interface ISecurity {

	public User getUser(AONContext ctx, String login);

	public Integer[] getUserScopes(AONContext ctx, Integer userId);
	
}
