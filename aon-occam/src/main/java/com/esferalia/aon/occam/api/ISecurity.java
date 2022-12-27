package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AuthDeviceFilter;
import com.esferalia.aon.occam.api.model.Filter.AuthFilter;
import com.esferalia.aon.occam.api.model.Filter.CertificateFilter;
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
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.type.MimeType;

public interface ISecurity {
   
    public Auth getAuthByUuid(String uuid);
    public Auth getAuthByEmail(String email);
    public Auth getAuthByPhone(String phone);
    public Auth getAuthByDocument(String document);
    
    public List<Auth> getAuthList(List<String> uuids);
    public Stream<Auth> getAuthStream(List<String> uuids);
    public Auth saveAuth(Auth auth);
    public void saveAuthPassword(String uuid, String password);
    public void saveAuthAvatar(String uuid, byte[] data, MimeType mimetype);
    public void backup(String email);
    
    @Deprecated
    public Stream<Auth> getAuthStream(AONContext ctx, AuthFilter filter);
    @Deprecated
    public Auth getAuth(AONContext ctx, String email);
    @Deprecated
    public Auth getAuthByDocument(AONContext ctx, String document);
    @Deprecated
	public Auth getAuth(AONContext ctx, byte[] auth);
	public byte[] unHexUuid(AONContext ctx, String uuid);
	@Deprecated
	public Auth insertAuth(AONContext ctx, Auth auth);
	@Deprecated
	public Auth updateAuth(AONContext ctx, Auth auth);
	@Deprecated
	public Auth updateAuthPassword(AONContext ctx, Auth auth);
	
	public DomainUserRoles getDomainUserRoles(AONContext ctx, Integer userId);
	
	public User getUser(AONContext ctx, UserFilter filter, Options...options);
	public Stream<User> getUserStream(AONContext ctx, UserFilter filter, Options...options);

	public User save(AONContext ctx, User user);
	public User insertUser(AONContext ctx, User user);
	public User delete(AONContext ctx, User user);
	public Stream<User> getDomainUserStream(AONContext ctx);
	public Stream<User> getDomainUserStream(AONContext ctx, UserFilter filter);
	public Stream<User> getDomainUserStream(AONContext ctx, Integer page, Integer perPage, UserFilter filter);
	public User getUser(AONContext ctx, String login);
	public LinkedList<User> getUsersByEmail(AONContext ctx, String email);
	public LinkedList<User> getUsersByScope(AONContext ctx, Integer scope);
	public LinkedList<Domain> getCompaniesByScope(AONContext ctx, Integer scope);
	public User getUser(AONContext ctx, Integer userId);
	public String getUserPassword(AONContext ctx, Integer userId);
	public void updateUserPassword(AONContext ctx, Integer userId, String password);
	public UserScope getUserScope(AONContext ctx, Integer userId, Integer scope);
	public void deleteUserScope(AONContext ctx, UserScopeFilter filter);
	public Integer[] getUserScopes(AONContext ctx, Integer userId);
	public Stream<Scope> getScopeStream(AONContext ctx, ScopeFilter filter);
	public Stream<Scope> getUserScopeStream(AONContext ctx, Integer userId, ScopeFilter filter);
	public Scope insertScope(AONContext ctx, Scope scope);
	public Integer deleteScope(AONContext ctx, Integer scopeId);
	public void assignAuthToUser(AONContext ctx, User user, byte[] auth);
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
	
	// USER WORKGROUP
	
	public Stream<UserWorkgroup> getUserWorkgroupStream(AONContext ctx, UserWorkgroupFilter filter); 
	public void saveUserWorkgroups(AONContext ctx, User user);
	public void deleteUserWorkgroup(AONContext ctx, User user, Workgroup workgroup);

	
	// DOMAIN APP
	
	public Stream<DomainApp> getDomainAppStream(AONContext ctx, DomainAppFilter filter);
	public DomainApp saveDomainApp(AONContext ctx, DomainApp domainApp, boolean old);
	public boolean isOCRActive(AONContext ctx,int domain); 
	
	// USER APP ROLE
	
	public Stream<UserAppRole> getUserAppRoleStream(AONContext ctx, UserAppRoleFilter filter);
	public UserAppRole insertUserAppRole(AONContext ctx, UserAppRole userAppRole);
	public UserAppRole updateUserAppRole(AONContext ctx, UserAppRole userAppRole);
	public UserAppRole deleteUserAppRole(AONContext ctx, UserAppRoleFilter filter);
	
	// DOMAIN MODULES
	public Stream<com.esferalia.aon.occam.api.model.Module> getDomainModules(AONContext ctx);
	
	
	// CERTIFICATE
	public Stream<Certificate> getCertificates(AONContext ctx, CertificateFilter filter);
	public Certificate getCertificate(AONContext ctx, Integer userId, String certificateType);
	public Certificate getCertificate(AONContext ctx, UserFilter userFilter) ;
	public Certificate insertCertificate(AONContext ctx, UserFilter userFilter , Certificate certificate) ;
	public Certificate getCertificateSEPE(AONContext ctx, Integer domainId);
	
	
	public AuthDevice saveAuthDevice(AONContext ctx, AuthDevice ad);
	public void deleteAuthDevice(AONContext ctx, AuthDeviceFilter filter);
	public AuthDevice getAuthDevice(AONContext ctx, AuthDeviceFilter adf);
	public LinkedList<AuthDevice> getAuthDevices(AONContext ctx, AuthDeviceFilter adf);
	
	public void saveDomainMaxDefinedUser(AONContext ctx, Integer maxDefinedUser);
	
	@Deprecated
	public void saveUserFinancePortal(AONContext ctx, Integer userId);

	public Booking getBooking(AONContext ctx, Domain domain);
	public Booking saveBooking(AONContext ctx, Booking booking);

}
