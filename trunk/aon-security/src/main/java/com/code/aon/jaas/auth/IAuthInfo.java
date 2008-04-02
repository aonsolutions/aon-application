package com.code.aon.jaas.auth;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IRelation;

public interface IAuthInfo {

	/**
	 * Return true if user exist in the domain.
	 * 
	 * @param domainName
	 * @param context
	 * @param name
	 * @return
	 */
	public boolean hasUser(String domainName, String context, String name)
			throws AuthenticationLoginException;

	/**
	 * Return Profile relations with its Roles.
	 * 
	 * @param domainName
	 * @param context
	 * @param name
	 * @return IRelation
	 */
	public IRelation getProfileRelation(String domainName, String context,
			String name) throws AuthenticationLoginException;

	/**
	 * Return User relation with its Profiles.
	 * 
	 * @param domainName
	 * @param context
	 * @param name
	 * @return IRelation
	 */
	public IRelation getUserRelation(String domainName, String context,
			String name) throws AuthenticationLoginException;

	/**
	 * Return user Password.
	 * 
	 * @param domainName
	 * @param name
	 * @return
	 */
	public String getUserPassword(String domainName, String name)
			throws AuthenticationLoginException;

	/**
	 * Return domain access policy.
	 * 
	 * @param domainName
	 * @return
	 */
	public IAccessPolicy getAccessPolicy(String domainName)
			throws AuthenticationLoginException;

}