package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISecurity;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.ScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;

public class SecurityImpl implements ISecurity {

	@Override
	public User getUser(AONContext ctx, UserFilter filter) {
		return SecurityDAO.getUser(ctx, filter);
	}
	
	@Override
	public User getUser(AONContext ctx, String login) {
		return SecurityDAO.getUser(ctx, login);
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
	public void deleteUserScope(AONContext ctx, Integer userId, Integer scope) {
		SecurityDAO.deleteUserScope(ctx, userId, scope);
	}
	
	@Override
	public void deleteUserScope(AONContext ctx, Integer scope) {
		SecurityDAO.deleteUserScope(ctx, scope);
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
}
