package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISecurity;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;

public class SecurityImpl implements ISecurity {

	@Override
	public User getUser(AONContext ctx, String login) {
		return SecurityDAO.getUser(ctx, login);
	}
	
	@Override
	public User getUser(AONContext ctx, Integer userId) {
		return SecurityDAO.getUser(ctx, userId);
	}

	@Override
	public Integer[] getUserScopes(AONContext ctx, Integer userId) {
		return SecurityDAO.getUserScopes(ctx, userId);
	}
	
	@Override
	public Scope getScope(AONContext ctx, Integer scopeId) {
		return SecurityDAO.getScope(ctx, scopeId);
	}


}
