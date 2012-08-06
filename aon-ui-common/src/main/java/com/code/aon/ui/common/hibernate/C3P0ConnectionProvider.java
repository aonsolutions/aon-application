package com.code.aon.ui.common.hibernate;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;

/**
 * A strategy for obtaining JDBC connections.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27/03/2008
 * 
 */

public class C3P0ConnectionProvider extends com.code.aon.common.dao.hibernate.C3P0ConnectionProvider {

	@Override
	protected AuthPrincipal getAuthPrincipal() {
		return AonUtil.getAuthPrincipal();
	}
	
}
