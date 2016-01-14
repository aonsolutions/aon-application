package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;

public interface ISecurity {
	public User getUser(AONContext ctx, String login);
	public User getUser(AONContext ctx, Integer userId);
	public Integer[] getUserScopes(AONContext ctx, Integer userId);
	public Scope getScope(AONContext ctx, Integer scopeId);
}
