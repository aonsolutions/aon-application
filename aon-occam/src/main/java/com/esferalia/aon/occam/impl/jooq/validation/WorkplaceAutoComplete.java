package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class WorkplaceAutoComplete {


	private WorkplaceAutoComplete() {}
	
	public static final BiConsumer<AONContext, Workplace> COMPLETE_DOMAIN = (ctx, workplace) -> {
		if(workplace.getDomain() == null) {
			workplace.setDomain(ctx.getDomainId());
		}
	};

	public static final BiConsumer<AONContext, Workplace> COMPLETE_SCOPE = (ctx, workplace) -> {
		if(workplace.getScope() == null) {
			Integer scope;
			User user = SecurityDAO.getUser(ctx);	
			Scope s = SecurityDAO.getUserScopeStream(ctx, user.getId(), f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(null);
			
			if(s == null) {
				Integer[] scopes = user!=null && user.getId()!=null ? SecurityDAO.getUserScopes(ctx, user.getId()) : null;
				if(scopes != null && scopes.length > 0)
					scope = scopes[0];
				else {
					s = SecurityDAO.getScopeStream(ctx,  f ->
						f.getDomainProperty().eq(workplace.getDomain())).findFirst().orElse(null);
					if(s == null) {
						s = SecurityDAO.insertScope(ctx, new Scope()
							.setDescription("GENERAL")
							.setDomain(workplace.getDomain()));
					}
					scope = s.getId();
				}
			} else scope = s.getId();
			workplace.setScope(scope);
		}
	};
	
	public static void completeWorkplace(AONContext ctx, Workplace workplace) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_SCOPE)
		.accept(ctx, workplace);

	}

}
