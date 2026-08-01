package com.esferalia.aon.occam.impl.jooq;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.ISecurity;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.Filter.AuthDeviceFilter;
import com.esferalia.aon.occam.api.model.Filter.AuthFilter;
import com.esferalia.aon.occam.api.model.Filter.CertificateFilter;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainAppFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.ScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.UserAppRoleFilter;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.Filter.UserScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.UserWorkgroupFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.scope.UserScopeAssign;
import com.esferalia.aon.occam.api.model.scope.UserScopeAuthorization;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDeviceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.BookingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainCustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryRelationshipDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ScopeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;

public class SecurityImpl implements ISecurity {

	@Override
	public Stream<Auth> getAuthStream(AONContext ctx, AuthFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.getAuthStream(ctx, filter));
	}
	
	@Override
	public Auth getAuth(AONContext ctx, String email) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.getAuth(ctx, email));
	}
	
	@Override
	public Auth getAuthByDocument(AONContext ctx, String document) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.getAuthByDocument(ctx, document));
	}
	
	@Override
	public Auth getAuth(AONContext ctx, byte[] auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.getAuth(ctx, auth));
	}
	
	@Override
	public byte[] unHexUuid(AONContext ctx, String uuid) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.unHexUuid(ctx, uuid));
	}

	@Override
	public Auth saveAuth(AONContext ctx, Auth auth) {
		return ctx.getDslContext().transactionResult(
				configuration -> AuthDAO.saveAuth(ctx, auth));
	}
	
	@Override
	public Auth insertAuth(AONContext ctx, Auth auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.insertAuth(ctx, auth));
	}
	
	@Override
	public Auth updateAuth(AONContext ctx, Auth auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.updateAuth(ctx, auth));
	}
	
	@Override
	public Auth updateAuthPassword(AONContext ctx, Auth auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.updateAuthPassword(ctx, auth));
	}
	
	@Override
	public Auth updateUserPassword(AONContext ctx, Auth auth) {
		return ctx.getDslContext().transactionResult( 
				configuration -> AuthDAO.updateUserPassword(ctx, auth));
	}
	

	@Override
	public Stream<User> getDomainUserStream(AONContext ctx) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getDomainUserStream(ctx));
	}
	
	@Override
	public Stream<User> getDomainUserStream(AONContext ctx, UserFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getDomainUserStream(ctx, filter));
	}
	
	@Override
	public Stream<User> getDomainUserStream(AONContext ctx, Integer page, Integer perPage, UserFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getDomainUserStream(ctx, page, perPage, filter));
	}
	
	@Override
	public Stream<User> getUserStream(AONContext ctx, UserFilter filter, Options... options) {
		return ctx.getDslContext().transactionResult( 
				configuration -> UserDAO.getStream(ctx, filter, options));
	}
	

	@Override
	public User getUserToken(CloseableAONContext ctx, Domain domain, AonToken aonToken) {
		return ctx.getDslContext().transactionResult( 
				configuration -> UserDAO.get(ctx, domain, aonToken));
	}
	
	@Override
	public User getUser(AONContext ctx, UserFilter filter, Options...options) {
		return UserDAO.get(ctx, filter, options);
	}
	
	@Override
	public User getUser(AONContext ctx, String login) {
		return SecurityDAO.getUser(ctx, login);
	}
	
	
	@Override
	public User save(AONContext ctx, User user) {
		return ctx.getDslContext().transactionResult(
				Configuration -> SecurityDAO.save(ctx, user));
	}
	
	@Override
	public User insertUser(AONContext ctx, User user) {
		return SecurityDAO.insertUser(ctx, user);
	}
	
	@Override
	public User delete(AONContext ctx, User user) {
		return ctx.getDslContext().transactionResult(
				Configuration -> SecurityDAO.delete(ctx, user));
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
	public void updateUserPassword(AONContext ctx, Integer userId, String password) {
		SecurityDAO.updateUserPassword(ctx, userId, password);
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
	public Scope saveScope(AONContext ctx, Scope scope) {
		return SecurityDAO.saveScope(ctx, scope);
	}
	
	@Override
	public Scope saveScopeAndAssign(AONContext ctx, Scope scope, boolean assignAllUsers, ArrayList<User> selectedUsers) {
		return ctx.getDslContext().transactionResult( configuration -> SecurityDAO.saveScopeAndAssign(ctx, scope, assignAllUsers, selectedUsers) );
	}

	@Override
	public boolean canScopeBeDeleted(AONContext ctx, Integer domainId, Integer scopeId) {
		return ScopeDAO.canBeDeleted(ctx, domainId, scopeId);
	}
	@Override
	public void reassignScope(AONContext ctx, Integer domainId, Integer fromScopeId, Integer toScopeId) {
		ctx.getDslContext().transaction( 
			configuration -> ScopeDAO.reassign(ctx, domainId, fromScopeId, toScopeId));
	}
	
	@Override
	public void reassignAndDeleteScope(AONContext ctx, Integer domainId, Integer fromScopeId, Integer toScopeId) {
		ctx.getDslContext().transaction( 
			configuration -> ScopeDAO.reassignAndDelete(ctx, domainId, fromScopeId, toScopeId));
	}

	@Override
	public Stream<Scope> getUsedScopesInDomain(CloseableAONContext ctx, int domain) {
		return ctx.getDslContext().transactionResult( configuration -> ScopeDAO.getUsedScopesInDomain(ctx, domain) );
	}
	
	@Override
	public Integer deleteScope(AONContext ctx, Integer scopeId) {
		return SecurityDAO.deleteScope(ctx, scopeId);
	}
	
	@Override
	public List<Scope> getScopeList(CloseableAONContext ctx, ScopeParams params) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getScopeList(ctx, params));
	}

	@Override
	public Integer getScopesCount(CloseableAONContext ctx, ScopeParams params) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getScopesCount(ctx, params));
	}

	@Override
	public List<UserScopeFull> getUserScopesByUserList(CloseableAONContext ctx, Integer userId) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getUserScopesByUserList(ctx, userId));
	}

	@Override
	public void authorizateUserScopes(CloseableAONContext ctx, int domain, String user, UserScopeAuthorization userScopeAuthorization) {
		ctx.getDslContext().transaction( 
				configuration -> SecurityDAO.authorizateUserScopes(ctx, domain, user, userScopeAuthorization));
	}
	
	@Override
	public void closeUserScopeAuthorizations(CloseableAONContext ctx, int domain, String user, List<UserScopeFull> authUserScopes, Date endDate) {
		ctx.getDslContext().transaction( 
				configuration -> SecurityDAO.closeUserScopeAuthorizations(ctx, domain, user, authUserScopes, endDate));
	}
	
	@Override
	public void assignSellerUserScopes(CloseableAONContext ctx, int domain, String user, UserScopeAssign userScopeAssign) {
		ctx.getDslContext().transaction( 
				configuration -> SecurityDAO.assignSellerUserScopes(ctx, domain, user, userScopeAssign));
	}
	
	@Override
	public List<UserScopeFull> getUserScopeFullList(CloseableAONContext ctx, Integer scopeId) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getUserScopeFullList(ctx, scopeId));
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

	@Override
	public void addUserScope(AONContext ctx, Integer userId, List<Integer> scopes) {
		ctx.getDslContext().transaction(
			Configuration -> SecurityDAO.addUserScope(ctx, userId, scopes));
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
	public void saveUserWorkgroups(AONContext ctx, User user) {
		ctx.getDslContext().transaction( 
			configuration -> UserDAO.saveUserWorkgroups(ctx, user));
	}
	
	@Override
	public void deleteUserWorkgroup(AONContext ctx, User user, Workgroup workgroup) {
		ctx.getDslContext().transaction( 
			configuration -> UserDAO.deleteUserWorkgroup(ctx, user, workgroup));
	}
	
	@Override
	public Stream<DomainApp> getDomainAppStream(AONContext ctx, DomainAppFilter filter) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getDomainAppStream(ctx, filter));
	}

	@Override
	public DomainApp saveDomainApp(AONContext ctx, DomainApp domainApp, boolean old) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.saveDomainApp(ctx, domainApp, old));
	}

	@Override
	public boolean isOCRActive(AONContext ctx, int domain) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.isOCRActive(ctx, domain));
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

	@Override
	public Stream<Module> getDomainModules(AONContext ctx) {
		return ctx.getDslContext().transactionResult( 
				configuration -> SecurityDAO.getDomainModules(ctx));
	}
	
	@Override
	public Stream<Certificate> getCertificates(AONContext ctx, CertificateFilter filter) {
		return CertificateDAO.getStream(ctx, filter);
	}
	
	@Override
	public Certificate getCertificate(AONContext ctx, Integer userId, String certificateType) {
		return SecurityDAO.getCertificate(ctx, userId, certificateType);
	}
	
	@Override
	public Certificate getCertificate(AONContext ctx, UserFilter userFilter) {
		return ctx.getDslContext().transactionResult(
				configuration -> SecurityDAO.getCertificate(ctx, userFilter))
		.orElseThrow(CertificateNotFoundException::new);
	}
	
	@Override
	public Certificate insertCertificate(AONContext ctx, UserFilter userFilter, Certificate certificate) {
		return ctx.getDslContext().transactionResult(
				configuration -> SecurityDAO.insertCertificate(ctx, userFilter, certificate));
	}

	@Override
	public Certificate getCertificateSEPE(AONContext ctx, Integer domainId) {
		return ctx.getDslContext().transactionResult(
				configuration -> SecurityDAO.getCertificateSEPE(ctx, domainId))
		.orElseThrow(CertificateNotFoundException::new);
	}

	@Override
	public DomainUserRoles getDomainUserRoles(AONContext ctx, Integer userId) {
		return ctx.getDslContext().transactionResult(
				configuration -> SecurityDAO.getDomainUserRoles(ctx, userId));
	}


	@Override
	public AuthDevice saveAuthDevice(AONContext ctx, AuthDevice ad) {
	    return  ctx.getDslContext().transactionResult(
	            configuration -> AuthDeviceDAO.saveAuthDevice(ctx, ad));
	}
	
	@Override
	public void deleteAuthDevice(AONContext ctx, AuthDeviceFilter filter) {
	    ctx.getDslContext().transaction(
	    		configuration -> AuthDeviceDAO.delete(ctx, filter)
	    );
	}
	
	@Override
	public AuthDevice getAuthDevice(AONContext ctx, AuthDeviceFilter adf) {
	    return  ctx.getDslContext().transactionResult(
	            configuration -> AuthDeviceDAO.getAuthDevice(ctx, adf));
	}
	
	@Override
	public LinkedList<AuthDevice> getAuthDevices(AONContext ctx, AuthDeviceFilter adf) {
	    return  ctx.getDslContext().transactionResult(
	            configuration -> AuthDeviceDAO.getAuthDevices(ctx, adf));
	}

	@Override @Deprecated
	public void saveUserFinancePortal(AONContext ctx, Integer userId) {
		ctx.getDslContext().transaction(
	            configuration -> SecurityDAO.saveUserFinancePortal(ctx, userId)
	    );
	}

	@Override
	public void saveDomainMaxDefinedUser(AONContext ctx, Integer maxDefinedUser) {
		ctx.getDslContext().transaction(configuration -> SecurityDAO.saveDomainMaxDefinedUser(ctx, maxDefinedUser));
	}

	@Override
	public Booking getBooking(AONContext ctx, Domain domain) {
	    return  ctx.getDslContext().transactionResult(
	            configuration -> BookingDAO.get(ctx, domain));
	}
	
	@Override
	public Booking saveBooking(AONContext ctx, Booking booking) {
	    return  ctx.getDslContext().transactionResult(
	            configuration -> BookingDAO.save(ctx, booking));
	}

	@Override
	public void saveBookingApp(CloseableAONContext ctx, DomainApp aonApp, boolean active) {
		ctx.getDslContext().transaction(configuration -> SecurityDAO.saveDomainApp(ctx, aonApp, active));
	}

	@Override
	public void deleteBookingApp(CloseableAONContext ctx, DomainApp aonApp) {
		ctx.getDslContext().transaction(configuration -> SecurityDAO.deleteDomainApp(ctx, aonApp));
	}

	@Override
	public List<RegistryRelationship> getRegistryRelationships(CloseableAONContext ctx, int domainId) {
		return  ctx.getDslContext().transactionResult(
	            configuration -> RegistryRelationshipDAO.getRegistryRelationships(ctx, domainId));
	}

	@Override
	public Stream<DomainCompany> getAviableDomainsForSync(CloseableAONContext ctx, boolean isSig, DomainFilter filter) {
		return  ctx.getDslContext().transactionResult(
	            configuration -> DomainCustomerDAO.getAviableDomainsForSync(ctx, isSig, filter));
	}
	
}
