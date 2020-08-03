package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISecurity;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainAppFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.ScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.UserAppRoleFilter;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.Filter.UserScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.UserWorkgroupFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;

public class SecurityImpl implements ISecurity {

	@Override
	public Auth getAuth(AONContext ctx, String email) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getAuth(ctx, email));
	}
	
	@Override
	public Auth getAuth(AONContext ctx, byte[] auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getAuth(ctx, auth));
	}
	
	@Override
	public byte[] unHexUuid(AONContext ctx, String uuid) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.unHexUuid(ctx, uuid));
	}
	
	@Override
	public Auth insertAuth(AONContext ctx, Auth auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.insertAuth(ctx, auth));
	}
	
	@Override
	public Stream<User> getUserStream(AONContext ctx, UserFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getUserStream(ctx, filter));
	}
	
	@Override
	public User getUser(AONContext ctx, UserFilter filter) {
		return SecurityDAO.getUser(ctx, filter);
	}
	
	@Override
	public User getUser(AONContext ctx, String login) {
		return SecurityDAO.getUser(ctx, login);
	}
	
	@Override
	public User insertUser(AONContext ctx, User user) {
		return SecurityDAO.insertUser(ctx, user);
	}
	
	@Override
	public LinkedList<User> getUsersByEmail(AONContext ctx, String email) {
		return SecurityDAO.getUsersByEmail(ctx, email);
	}
	
	@Override
	public LinkedList<User> getUsersByScope(AONContext ctx, Integer scope) {
		return SecurityDAO.getUsersByScope(ctx, scope);
	}
	
	@Override
	public LinkedList<Domain> getCompaniesByScope(AONContext ctx, Integer scope) {
		return SecurityDAO.getCompaniesByScope(ctx, scope);
	}
	
	@Override
	public User getUser(AONContext ctx, Integer userId) {
		return SecurityDAO.getUser(ctx, userId);
	}

	
	@Override
	public String getUserPassword(AONContext ctx, Integer userId) {
		return SecurityDAO.getUserPassword(ctx, userId);
	}

	@Override
	public UserScope getUserScope(AONContext ctx, Integer userId, Integer scope) {
		return SecurityDAO.getUserScope(ctx, userId, scope);
	}
	
	@Override
	public Integer[] getUserScopes(AONContext ctx, Integer userId) {
		return SecurityDAO.getUserScopes(ctx, userId);
	}
	
	@Override
	public Stream<Scope> getScopeStream(AONContext ctx, ScopeFilter filter) {
		return SecurityDAO.getScopeStream(ctx, filter);
	}
	
	@Override
	public Stream<Scope> getUserScopeStream(AONContext ctx, Integer userId, ScopeFilter filter) {
		return SecurityDAO.getUserScopeStream(ctx, userId, filter);
	}

	@Override
	public Scope insertScope(AONContext ctx, Scope scope) {
		return SecurityDAO.insertScope(ctx, scope);
	}
	
	@Override
	public Integer deleteScope(AONContext ctx, Integer scopeId) {
		return SecurityDAO.deleteScope(ctx, scopeId);
	}
	
	@Override
	public void insertUserScope(AONContext ctx, UserScope userScope) {
		SecurityDAO.insertUserScope(ctx, userScope);
	}
	
	@Override
	public void deleteUserScope(AONContext ctx, UserScopeFilter filter) {
		SecurityDAO.deleteUserScope(ctx, filter);
	}

	@Override
	public void assignAuthToUser(AONContext ctx, User user, byte[] auth) {
		SecurityDAO.assignAuthToUser(ctx, user, auth);
	}
	
	// ------------------ SIGNATURE
	
		@Override
		public Signature getSignature(AONContext ctx, Integer signatureId) {
			return ctx.getDslContext().transactionResult(
					Configuration -> SecurityDAO.getSignature(ctx, signatureId));
		}
		
		@Override
		public Signature getSignature(AONContext ctx, SignatureFilter filter) {
			return ctx.getDslContext().transactionResult(
					Configuration -> SecurityDAO.getSignature(ctx, filter));
		}
		
		@Override
		public LinkedList<Signature> getSignatureList(AONContext ctx, SignatureFilter filter) {
			return ctx.getDslContext().transactionResult(
					Configuration -> SecurityDAO.getSignatureList(ctx, filter));
		}
		
	// ------------------ MAIL ACCOUNT

	@Override
	public MailAccount getMailAccount(AONContext ctx, MailAccountFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getMailAccount(ctx, filter));
	}
	
	@Override
	public LinkedList<MailAccount> getMailAccountList(AONContext ctx, MailAccountFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getMailAccountList(ctx, filter));
	}
	
	// ------------------ CONTACT

	@Override
	public Contact getContact(AONContext ctx, ContactFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getContact(ctx, filter));
	}

	@Override
	public LinkedList<Contact> getContactList(AONContext ctx, ContactFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getContactList(ctx, filter));
	}

	@Override
	public String getContactEmail(AONContext ctx, Integer contactDataId) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getContactEmail(ctx, contactDataId));
	}

	@Override
	public Stream<UserWorkgroup> getUserWorkgroupStream(AONContext ctx, UserWorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getUserWorkgroupStream(ctx, filter));
	}

	@Override
	public Stream<DomainApp> getDomainAppStream(AONContext ctx, DomainAppFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getDomainAppStream(ctx, filter));
	}

	@Override
	public DomainApp insertDomainApp(AONContext ctx, DomainApp domainApp) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.insertDomainApp(ctx, domainApp));
	}

	@Override
	public DomainApp updateDomainApp(AONContext ctx, DomainApp domainApp) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.updateDomainApp(ctx, domainApp));
	}
	
	@Override
	public Stream<UserAppRole> getUserAppRoleStream(AONContext ctx, UserAppRoleFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getUserAppRoleStream(ctx, filter));
	}

	@Override
	public UserAppRole insertUserAppRole(AONContext ctx, UserAppRole userAppRole) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.insertUserAppRole(ctx, userAppRole));
	}

	@Override
	public UserAppRole updateUserAppRole(AONContext ctx, UserAppRole userAppRole) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.updateUserAppRole(ctx, userAppRole));
	}
	
	@Override
	public UserAppRole deleteUserAppRole(AONContext ctx, UserAppRoleFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.deleteUserAppRole(ctx, filter));
	}

}
