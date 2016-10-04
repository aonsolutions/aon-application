package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISecurity;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
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
