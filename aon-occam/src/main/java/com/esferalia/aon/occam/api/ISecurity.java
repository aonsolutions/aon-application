package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;

public interface ISecurity {
	public User getUser(AONContext ctx, String login);
	public User getUser(AONContext ctx, Integer userId);
	public Integer[] getUserScopes(AONContext ctx, Integer userId);
	public Scope getScope(AONContext ctx, Integer scopeId);
	
	// SIGNATURE
	public Signature getSignature(AONContext ctx, Integer signatureId);

	// MAIL ACCOUNT
	public MailAccount getMailAccount(AONContext ctx, MailAccountFilter filter);
	public LinkedList<MailAccount> getMailAccountList(AONContext ctx, MailAccountFilter filter);
	
}
