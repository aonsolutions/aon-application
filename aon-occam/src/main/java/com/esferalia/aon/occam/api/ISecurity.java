package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

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

public interface ISecurity {
	public User getUser(AONContext ctx, UserFilter filter);
	public User getUser(AONContext ctx, String login);
	public LinkedList<User> getUsersByEmail(AONContext ctx, String email);
	public LinkedList<User> getUsersByScope(AONContext ctx, Integer scope);
	public LinkedList<Domain> getCompaniesByScope(AONContext ctx, Integer scope);
	public User getUser(AONContext ctx, Integer userId);
	public String getUserPassword(AONContext ctx, Integer userId);
	public UserScope getUserScope(AONContext ctx, Integer userId, Integer scope);
	public void deleteUserScope(AONContext ctx, Integer userId, Integer scope);
	public void deleteUserScope(AONContext ctx, Integer scope);
	public Integer[] getUserScopes(AONContext ctx, Integer userId);
	public Stream<Scope> getScopeStream(AONContext ctx, ScopeFilter filter);
	public Stream<Scope> getUserScopeStream(AONContext ctx, Integer userId, ScopeFilter filter);
	public Scope insertScope(AONContext ctx, Scope scope);
	public Integer deleteScope(AONContext ctx, Integer scopeId);
	
	public void insertUserScope(AONContext ctx, UserScope userScope);
	
	// SIGNATURE
	public Signature getSignature(AONContext ctx, Integer signatureId);
	public Signature getSignature(AONContext ctx, SignatureFilter filter);
	public LinkedList<Signature> getSignatureList(AONContext ctx, SignatureFilter filter);

	// MAIL ACCOUNT
	public MailAccount getMailAccount(AONContext ctx, MailAccountFilter filter);
	public LinkedList<MailAccount> getMailAccountList(AONContext ctx, MailAccountFilter filter);
	
	// CONTACT
	public Contact getContact(AONContext ctx, ContactFilter filter);
	public LinkedList<Contact> getContactList(AONContext ctx, ContactFilter filter);
	public String getContactEmail(AONContext ctx, Integer contactDataId);
}
