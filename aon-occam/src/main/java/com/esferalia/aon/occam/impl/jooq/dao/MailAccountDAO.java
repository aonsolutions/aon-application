package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.MailAccountRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailAccountType;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;

public class MailAccountDAO {

	private MailAccountDAO() {}
	
	private static final MailAccountPropertiesDAO MAIL_ACCOUNT_PROPERTIES = new MailAccountPropertiesDAO();

	protected static class MailAccountPropertiesDAO implements MailAccountProperties {
		protected Condition[] getConditions(MailAccountFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.ID);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.NAME);}
		@Override public Property<String> getEmailProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.EMAIL);}
		@Override public Property<Integer> getSignatureProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.SIGNATURE);}
		@Override public Property<Byte> getDefaultAccountProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.DEFAULT_ACCOUNT);}
		@Override public Property<String> getDisplayNameProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.DISPLAY_NAME);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.DOMAIN);}
		@Override public Property<String> getDraftFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.DRAFT_FOLDER);}
		@Override public Property<String> getIncomingHostProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.INCOMING_HOST);}
		@Override public Property<Integer> getIncomingPortProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.INCOMING_PORT);}
		@Override public Property<Byte> getIncomingSecurityProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.INCOMING_SECURITY);}
		@Override public Property<String> getMailUsernameProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.MAIL_USERNAME);}
		@Override public Property<String> getOutgoingHostProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.OUTGOING_HOST);}
		@Override public Property<Integer> getOutgoingPortProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.OUTGOING_PORT);}
		@Override public Property<Byte> getOutgoingSecurityProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.OUTGOING_SECURITY);}
		@Override public Property<Byte> getOutgoingVerificationProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.OUTGOING_VERIFICATION);}
		@Override public Property<String> getPasswordProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.PASSWORD);}
		@Override public Property<String> getProtocolProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.PROTOCOL);}
		@Override public Property<String> getReplytoMailProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.REPLYTO_MAIL);}
		@Override public Property<String> getSentFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.SENT_FOLDER);}
		@Override public Property<String> getSpamFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.SPAM_FOLDER);}
		@Override public Property<String> getTrashFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.TRASH_FOLDER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.TYPE);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.USER_ID);}
	}
	
	public static MailAccount get(AONContext ctx, Integer mailAccountId){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT.ID.eq(mailAccountId))
				.limit(1).fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).findFirst().orElse(new MailAccount());
	}
	
	public static MailAccount get(AONContext ctx, MailAccountFilter filter){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT_PROPERTIES.getConditions(filter))
		.limit(1).fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).findFirst().orElse(new MailAccount());
	}
	
	public static LinkedList<MailAccount> getList(AONContext ctx, MailAccountFilter filter){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT_PROPERTIES.getConditions(filter))
		.fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).collect(Collectors.toCollection(LinkedList::new));
	}

	public static MailAccount save(AONContext ctx, MailAccount mailAccount){
		return mailAccount.getId() != null 
				? update(ctx, mailAccount)
				: insert(ctx, mailAccount);
	}
	
	private static MailAccount update(AONContext ctx, MailAccount mailAccount){
		ctx.getDslContext().update(MAIL_ACCOUNT)
			.set(MAIL_ACCOUNT.NAME, mailAccount.getName())
			.set(MAIL_ACCOUNT.EMAIL, mailAccount.getEmail())
			.set(MAIL_ACCOUNT.REPLYTO_MAIL, mailAccount.getReplytoMail())
			.set(MAIL_ACCOUNT.INCOMING_HOST, mailAccount.getIncomingHost())
			.set(MAIL_ACCOUNT.PROTOCOL, mailAccount.getProtocol())
			.set(MAIL_ACCOUNT.INCOMING_PORT, mailAccount.getIncomingPort())
			.set(MAIL_ACCOUNT.INCOMING_SECURITY, mailAccount.getIncomingSecurity())
			.set(MAIL_ACCOUNT.OUTGOING_VERIFICATION, mailAccount.getOutgoingVerification())
			.set(MAIL_ACCOUNT.OUTGOING_HOST, mailAccount.getOutgoingHost())
			.set(MAIL_ACCOUNT.OUTGOING_PORT, mailAccount.getOutgoingPort())
			.set(MAIL_ACCOUNT.OUTGOING_SECURITY, mailAccount.getOutgoingSecurity())
			.set(MAIL_ACCOUNT.MAIL_USERNAME, mailAccount.getMailUsername())
			.set(MAIL_ACCOUNT.PASSWORD, mailAccount.getPassword())
			.set(MAIL_ACCOUNT.DEFAULT_ACCOUNT, mailAccount.getDefaultAccount())
			.set(MAIL_ACCOUNT.DRAFT_FOLDER, mailAccount.getDraftFolder())
			.set(MAIL_ACCOUNT.SENT_FOLDER, mailAccount.getSentFolder())
			.set(MAIL_ACCOUNT.TRASH_FOLDER, mailAccount.getTrashFolder())
			.set(MAIL_ACCOUNT.SPAM_FOLDER, mailAccount.getSpamFolder())
			.set(MAIL_ACCOUNT.DISPLAY_NAME, mailAccount.getDisplayName())
			.set(MAIL_ACCOUNT.SIGNATURE, mailAccount.getSignatureId())
			.set(MAIL_ACCOUNT.USER_ID, mailAccount.getUserId())
			.set(MAIL_ACCOUNT.TYPE, mailAccount.getType().value())
			.where(MAIL_ACCOUNT.ID.eq(mailAccount.getId()))
			.execute();
		
		ctx.log().debug("UPDATE MAIL ACCOUNT id: " + mailAccount.getId());	
		
		return mailAccount;
	}
	
	private static MailAccount insert(AONContext ctx, MailAccount mailAccount) {
		Integer newId = ctx.getDslContext()
				.insertInto(MAIL_ACCOUNT)
				.set(MAIL_ACCOUNT.DOMAIN, ctx.getDomainId())
				.set(MAIL_ACCOUNT.NAME, mailAccount.getName())
				.set(MAIL_ACCOUNT.EMAIL, mailAccount.getEmail())
				.set(MAIL_ACCOUNT.REPLYTO_MAIL, mailAccount.getReplytoMail())
				.set(MAIL_ACCOUNT.INCOMING_HOST, mailAccount.getIncomingHost())
				.set(MAIL_ACCOUNT.PROTOCOL, mailAccount.getProtocol())
				.set(MAIL_ACCOUNT.INCOMING_PORT, mailAccount.getIncomingPort())
				.set(MAIL_ACCOUNT.INCOMING_SECURITY, mailAccount.getIncomingSecurity())
				.set(MAIL_ACCOUNT.OUTGOING_VERIFICATION, mailAccount.getOutgoingVerification())
				.set(MAIL_ACCOUNT.OUTGOING_HOST, mailAccount.getOutgoingHost())
				.set(MAIL_ACCOUNT.OUTGOING_PORT, mailAccount.getOutgoingPort())
				.set(MAIL_ACCOUNT.OUTGOING_SECURITY, mailAccount.getOutgoingSecurity())
				.set(MAIL_ACCOUNT.MAIL_USERNAME, mailAccount.getMailUsername())
				.set(MAIL_ACCOUNT.PASSWORD, mailAccount.getPassword())
				.set(MAIL_ACCOUNT.DEFAULT_ACCOUNT, mailAccount.getDefaultAccount())
				.set(MAIL_ACCOUNT.DRAFT_FOLDER, mailAccount.getDraftFolder())
				.set(MAIL_ACCOUNT.SENT_FOLDER, mailAccount.getSentFolder())
				.set(MAIL_ACCOUNT.TRASH_FOLDER, mailAccount.getTrashFolder())
				.set(MAIL_ACCOUNT.SPAM_FOLDER, mailAccount.getSpamFolder())
				.set(MAIL_ACCOUNT.DISPLAY_NAME, mailAccount.getDisplayName())
				.set(MAIL_ACCOUNT.SIGNATURE, mailAccount.getSignatureId())
				.set(MAIL_ACCOUNT.USER_ID, mailAccount.getUserId())
				.set(MAIL_ACCOUNT.TYPE, mailAccount.getType().value())
				.returning(MAIL_ACCOUNT.ID)
				.fetchOne()
				.getValue(MAIL_ACCOUNT.ID);
		
		ctx.log().debug("CREATE MAIL ACCOUNT id: " + newId);	
		
		mailAccount.setId(newId);
		
		return mailAccount;
	}
	
	public static void delete(AONContext ctx, Integer deleteId){
		ctx.getDslContext().delete(MAIL_ACCOUNT)
			.where(MAIL_ACCOUNT.ID.eq(deleteId))
			.execute();
		
		ctx.log().debug("DELETE MAIL ACCOUNT id: " + deleteId);	
	}
	
	private static class FullMailAccountFiller implements Function<MailAccountRecord, MailAccount> {
		@Override
		public MailAccount apply(MailAccountRecord r) {
			return new MailAccount()
					.setDefaultAccount(r.getDefaultAccount())
					.setDisplayName(r.getDisplayName())
					.setDomain(r.getDomain())
					.setDraftFolder(r.getDraftFolder())
					.setEmail(r.getEmail())
					.setId(r.getId())
					.setIncomingHost(r.getIncomingHost())
					.setIncomingPort(r.getIncomingPort())
					.setIncomingSecurity(r.getIncomingSecurity())
					.setMailUsername(r.getMailUsername())
					.setName(r.getName())
					.setOutgoingHost(r.getOutgoingHost())
					.setOutgoingPort(r.getOutgoingPort())
					.setOutgoingSecurity(r.getOutgoingSecurity())
					.setOutgoingVerification(r.getOutgoingVerification())
					.setPassword(r.getPassword())
					.setProtocol(r.getProtocol())
					.setReplytoMail(r.getReplytoMail())
					.setSentFolder(r.getSentFolder())
					.setSignatureId(r.getSignature())
					.setSpamFolder(r.getSpamFolder())
					.setTrashFolder(r.getTrashFolder())
					.setType(MailAccountType.safeValueOf(r.getType()))
					.setUserId(r.getUserId());
		}
	}
	
}
