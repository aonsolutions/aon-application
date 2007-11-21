/**
 * 
 */
package com.code.aon.bridge.plugin;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/11/2007
 *
 * @deprecated
 */
public class UserUtils {

	/**
	 * Sharing user between contexts.
	 * 
	 * @param res
	 * @param principal
	 * @deprecated
	 */
	public static final void addSharingUserCookies(HttpServletResponse res, Principal principal) {
//        AuthPrincipal auth = null;
//        if ( principal instanceof AuthPrincipal )
//        	auth = (AuthPrincipal) principal;
//        else
//        	auth = new AuthPrincipal( principal.getName() );
//        Cookie cookie = new Cookie( "j_username", auth.getShortName() );
//        cookie.setPath( "/" );
//        res.addCookie( cookie );
//        cookie = new Cookie( "j_domain", auth.getDomain() );
//        cookie.setPath( "/" );
//        res.addCookie( cookie );
//        cookie = new Cookie( "j_context", auth.getContext() );
//        cookie.setPath( "/" );
//        res.addCookie( cookie );
	}

}
