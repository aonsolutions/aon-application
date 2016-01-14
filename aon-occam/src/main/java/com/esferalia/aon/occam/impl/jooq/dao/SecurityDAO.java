package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ApplicationRole.APPLICATION_ROLE;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.ProfileRole.PROFILE_ROLE;
import static com.esferalia.aon.jooq.tables.Role.ROLE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record5;

import com.esferalia.aon.jooq.tables.records.MailAccountRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.SignatureRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class SecurityDAO {
	
	public static Domain getDomain(AONContext ctx, int domain) {
		final Domain dom = new Domain();
		ctx.getDslContext()
			.select(DOMAIN.ID
					,DOMAIN.NAME
					,DOMAIN.PARENT
					,DOMAIN.TYPE
					,DOMAIN.DOMAINMANAGEMENT
					,DOMAIN.ACTIVE)
			.from(DOMAIN)
			.where(DOMAIN.ID.equal(domain))
			.fetch()
			.stream()
			.forEach( record -> {
				dom.setId(record.getValue(DOMAIN.ID));
				dom.setName(record.getValue(DOMAIN.NAME));
				dom.setActive(AonEnumUtils.getBoolean(record.getValue(DOMAIN.ACTIVE)));
				dom.setParentId(record.getValue(DOMAIN.PARENT));
				boolean parent = (record.getValue(DOMAIN.PARENT) == null);
				boolean domainManagement = AonEnumUtils.getBoolean(record.getValue(DOMAIN.DOMAINMANAGEMENT));
				if (!parent) {
					dom.setChild(true);
					dom.setStandalone(false);
					dom.setParent(false);
				} else {
					if (domainManagement) {
						dom.setParent(true);
						dom.setStandalone(false);
						dom.setChild(false);
					} else {
						dom.setParent(false);
						dom.setStandalone(true);
						dom.setChild(false);
					}
				}
			});
		return dom;
	}
	
	public static User getUser(AONContext ctx, Integer userId) {
		ctx.checkRead();
		Record5<Integer, Integer, String, String, Byte> record = 
			ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE)
				.from(USER)
				.where(USER.ID.equal(userId))
				.fetchOne();
		User user = null;
		if (record != null) {
			user = new User();
			user.setId(record.getValue(USER.ID));
			user.setDomain(record.getValue(USER.DOMAIN));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN)); 
			user.setActive(AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}

	public static User getUser(AONContext ctx, String login) {
		ctx.checkRead();
		Record5<Integer, Integer, String, String, Byte> record = 
			ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE)
				.from(USER)
				.where(USER.DOMAIN.equal(ctx.getDomainId()))
				.and(USER.LOGIN.equal(login))
				.fetchOne();
		if (record == null) {
			com.esferalia.aon.jooq.tables.Domain PARENT_DOMAIN = DOMAIN.as("PARENT_DOMAIN");
			record = ctx.getDslContext()
						.select(USER.ID, 
								USER.DOMAIN, 
								USER.NAME, 
								USER.LOGIN,
								USER.ACTIVE)
						.from(DOMAIN)
						.join(PARENT_DOMAIN).on(DOMAIN.PARENT.equal(PARENT_DOMAIN.ID))
						.join(USER).on(USER.DOMAIN.equal(PARENT_DOMAIN.ID))
						.where(DOMAIN.ID.equal(ctx.getDomainId()))
						.and(USER.LOGIN.equal(login))
						.fetchOne();			
		}
		User user = new User();
		if (record != null) {
			user.setId(record.getValue(USER.ID));
			user.setDomain(record.getValue(USER.DOMAIN));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN)); 
			user.setActive(AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}
	
	public static AonRole[] getUserRoles(AONContext ctx, Integer userId) {
		final List<AonRole> list = new ArrayList<AonRole>();
		ctx.getDslContext()
			.selectDistinct(ROLE.NAME)
			.from(USER)
			.join(APPLICATION_USER).on(APPLICATION_USER.USER_ID.equal(USER.ID))
			.join(APPLICATION_USER_PROFILE).on(APPLICATION_USER_PROFILE.APPLICATION_USER.equal(APPLICATION_USER.ID))
			.join(PROFILE).on(APPLICATION_USER_PROFILE.PROFILE.equal(PROFILE.ID))
			.join(PROFILE_ROLE).on(PROFILE_ROLE.PROFILE.equal(PROFILE.ID))
			.join(APPLICATION_ROLE).on(APPLICATION_ROLE.ID.equal(PROFILE_ROLE.APPLICATION_ROLE))
			.join(ROLE).on(APPLICATION_ROLE.ROLE.equal(ROLE.ID))
			.join(DOMAIN).on(DOMAIN.ID.equal(USER.DOMAIN).or(DOMAIN.PARENT.equal(USER.DOMAIN)))
			.and(USER.ID.equal(userId))
			.fetch()
			.stream()
			.forEach(rec -> {
				String role = rec.getValue(ROLE.NAME);
				list.add( AonRole.valueOfBDValue( role) );
				} );
		AonRole[] roles = new AonRole[list.size()];
		list.toArray(roles);
		return roles;
	}

	public static Integer[] getUserScopes (AONContext ctx, Integer userId) {
		ctx.checkRead();
		User user = getUser(ctx, userId);
		if (user == null) {
			// ??
			throw new IllegalAccessError("Usario no encontrado.");
		}
		// Es un usuario del dominio, por lo que es 
		// consultar los scopes del dominio
		if ( user.getDomain() == ctx.getDomainId() ) {
			final List<Integer> list = new ArrayList<Integer>();
			ctx.getDslContext()
				.select(USER_SCOPE.SCOPE)
					.from(USER_SCOPE)
					.where(USER_SCOPE.USER_ID.equal(userId))
					.fetch()
				.stream()
				.forEach(rec -> list.add( rec.getValue(USER_SCOPE.SCOPE) ) );
			Integer[] scopes = new Integer[list.size()];
			list.toArray(scopes);
			return scopes;
		} 
		
		// Comprabamos si es un usuario del dominio padre.
		Domain domain = getDomain(ctx, ctx.getDomainId());
		if ( user.getDomain() == domain.getParentId() ) {
			// Se trata de un usuario del cominio padre, por 
			// lo que tiene acceso a todos los scopes, se devuelve 
			// NULL, por lo que no hay que cruzar la tabla user_scope.
			return null;
		} else {
			// NO DEBE PASAR. Es un usuario que no pertenece 
			// al dominio en curso ni al dominio padre. 
			// Si ha llegado aqui es un error.
			throw new IllegalAccessError("Usario sin permisos.");
		}
	}
	
	public static Scope getScope(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().select().from(SCOPE)
				.where(SCOPE.ID.eq(scopeId)).limit(1).fetchInto(SCOPE)
				.stream().map(new FullScopeFiller()).findFirst().orElse(new Scope());
	}
	
	private static class FullScopeFiller implements Function<ScopeRecord, Scope> {
		@Override
		public Scope apply(ScopeRecord r) {
			return new Scope()
					.setDescription(r.getDescription())
					.setDomain(r.getDomain())
					.setId(r.getId());
		}
	}

	public static Signature getSignature(AONContext ctx, Integer signatureId){
		return ctx.getDslContext()
				.select(SIGNATURE.SIGNATURE_).from(SIGNATURE)
				.where(SIGNATURE.ID.eq(signatureId)).limit(1).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).findFirst().orElse(new Signature());
	}
	
	private static class FullSignatureFiller implements Function<SignatureRecord, Signature> {
		@Override
		public Signature apply(SignatureRecord r) {
			return new Signature()
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setSignature(r.getSignature())
					.setUserId(r.getUserId())
					;
		}
	}
	
	public static MailAccount getMailAccount(AONContext ctx, MailAccountFilter filter){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT_PROPERTIES.getConditions(filter))
		.limit(1).fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).findFirst().orElse(new MailAccount());
	}
	
	public static LinkedList<MailAccount> getMailAccountList(AONContext ctx, MailAccountFilter filter){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT_PROPERTIES.getConditions(filter))
		.fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
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
					.setType(r.getType())
					.setUserId(r.getUserId());
		}
	}
	
	public static Integer[] getUserScopes(String domainName, int domainId,
			Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
}

