package com.code.aon.jaas.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.UserAlreadyExistException;
import com.code.aon.jaas.client.ast.core.Application;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.client.ast.core.Domain;
import com.code.aon.jaas.client.ast.core.DomainApplication;
import com.code.aon.jaas.client.xml.DomainRenderer;
import com.code.aon.jaas.deployment.IDeployer;
import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.util.FileUtils;

/**
 * This class implements all security operations, that should be invoked by the application 
 * server definitions.  
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2006
 * @since 1.0
 *  
 */
public class StorageSupport implements IOperation {

    /** StorageSupport Logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(StorageSupport.class);

    /** Applications storage manager. */
	public ApplicationsStorage as;

	@Override
	public Collection<IApplication> applications() {
		return Collections.unmodifiableCollection( this.as.applications().values() );
	}

	@Override
	public Collection<IApplication> getSDApplications(String securityDomain) {
        List<IApplication> apps = new LinkedList<IApplication>();
        Iterator<IApplication> iter = this.as.applications().values().iterator();
        while (iter.hasNext()) {
			IApplication app = iter.next();
			if ( app.getSecurityDomain().equals(securityDomain) ) {
				apps.add(app);
			}
		}
        return Collections.unmodifiableCollection(apps);
	}

	@Override
	public List<IApplication> getUserApplications(String domainId, String userId) {
        List<IApplication> apps = new LinkedList<IApplication>();
        Iterator<IApplication> iter = this.as.applications().values().iterator();
        while (iter.hasNext()) {
			IApplication app = iter.next();
			IDomain domain = app.getDomain( domainId );
			if ( domain != null ) {
				Iterator<IDomainApplication> domainApps = domain.applications().iterator();
				while (domainApps.hasNext()) {
					IDomainApplication ida = domainApps.next();
					IApplication _app = this.as.getApplication( ida.getId() );
					if ( ida.getUser( userId ) != null && !apps.contains( _app ) ) {
						apps.add( _app );
					}
				}
			}
		}
        return Collections.unmodifiableList( apps );
	}

	@Override
	public IApplication getApplication(String appId) {
        return this.as.getApplication(appId);
	}

	@Override
	public IApplication getApplication4Ctx(String ctx) {
        return this.as.getApplication4Ctx(ctx);
	}

	@Override
	public Properties getDSMDProperties(Principal principal) {
		AuthPrincipal p = (AuthPrincipal) principal;
		IApplication app = this.as.getApplication4Ctx( p.getContext() );
		IDomain domain = (IDomain) app.getDomain( p.getDomain() );
		IDataSourceMetaData dsmt = 
			domain.getDomainApplication( app.getId() ).getDataSourceMetaData();
		if ( dsmt == null )
			dsmt = domain.getDataSourceMetaData();
		return dsmt.getProperties();
	}

	@Override
	public List<String> getDomainNames2Import(String appId) {
        IApplication app = this.as.getApplication( appId );
        List<String> domainNames2Exclude = new ArrayList<String>();
        // Extract excluded domain names.
        domainNames2Exclude.add( IDeployer.RESOURCE_NAME );
        domainNames2Exclude.add( IDomain.DEFAULT_DOMAIN_NAME + "." + IStorage.XML );
        Iterator<IDomain> iter = app.domains().iterator();
        while (iter.hasNext()) {
			IDomain domain = iter.next();
			domainNames2Exclude.add( domain.getId() + "." + IStorage.XML );
		}
        // Find domain names to import.
        List<String> domainNames = new ArrayList<String>();
		String[] files = this.as.getStorageDir().list();
		for (int i = 0; i < files.length; i++) {
			String name = files[i];
			if ( !domainNames2Exclude.contains( name ) && name.indexOf( "." + FileUtils.UP_TO_DATE ) == -1 ) {
				int index = name.lastIndexOf( "." + IStorage.XML );
				domainNames.add( name.substring( 0, index ) );
			}
		}
		return domainNames;
	}

	@Override
	public IDomain getDomain(String appContext, String domainId) {
		IApplication app = this.as.getApplication4Ctx( appContext );
		return (IDomain) app.getDomain( domainId );
	}

	@Override
	public IUser getUser(Principal principal) {
		AuthPrincipal p = (AuthPrincipal) principal;
		return getUser( p.getContext(), p.getDomain(), p.getShortName() );
	}

	@Override
	public IUser getUser(String appContext, String domainId, String userId) {
		IApplication app = this.as.getApplication4Ctx( appContext );
		IDomain domain = (IDomain) app.getDomain( domainId );
		return domain.getStandaloneUser( userId );
	}

	@Override
	public void initApplicationDeployed(String appId, String domainId, 
			Boolean privileged, String contextExtraInfo, 
			IAccessPolicy accessPolicy, IDataSourceMetaData metadata) throws StorageException {
		try {
			boolean updated = false;
			IDomain domain = parseDomainStorage( domainId ).getDomain();
			if ( domain.getAccessPolicy() == null ) {
				domain.setAccessPolicy( accessPolicy );
				updated = true;
			}
			if ( metadata != null && !metadata.getConnectionURL().equals( "" ) ) {
				( (DomainApplication)domain.getDomainApplication(appId) ).setDataSourceMetaData( metadata );
				updated = true;
			}
//			TODO. Asociar a cada aplicacion solamente la parte del objeto IDomain que le interesa
//			Application app = (Application) this.as.getApplication( appId );
//			File resource = new File( this.as.getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + IStorage.XML );
//			DomainStorage es = 
//				(DomainStorage) AstLoader.getInstance().parse( 1, resource.toURL().openStream() );
//			app.replaceDomain( domain.getId(), es.getDomain() );
//			LOGGER.debug( "Domain [" + domain.getId() + "] Loaded and Replaced inside [" + appId + "] Application." );
			if ( updated )
				write( domain );
		} catch (IOException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		} catch (AstException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		}
	}

	@Override
	public URL read(IDomain domain) throws StorageException {
		if (domain == null) {
	    	LOGGER.debug( "Reading deployed applications file" );
			return this.as.read(); // Devuelve la URL de aplicaciones desplegadas.
		}
    	LOGGER.debug( "Reading application: ENTITY[{}]", domain.getId() );
		try {
			IStorage es = getStorage(domain);
			if (es != null) {
				return es.read(); // Devuelve la URL de la entidad.
			}
		} catch (IOException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		}
		return null;
	}

	@Override
	public IDomain removeDomain(String appId, IDomain domain) throws StorageException {
		IDomain _domain = this.as.getApplication( appId ).remove( domain );
		if (_domain != null) {
			write(null); // Actualiza el fichero de aplicaciones desplegadas.
			try {
				DomainStorage es = parseDomainStorage( domain.getId() );
				IDomain storagedDomain = es.getDomain();
				if ( storagedDomain.applications().size() > 1 ) {
					storagedDomain.remove( appId );
					this.as.replaceDomainInApplications( "", storagedDomain );
					write( storagedDomain );
				} else {
					getStorage( domain ).erase(); // Elimina fisicamente el fichero de la entidad.
				}
			} catch (IOException e) {
				String msg = "Unable to replace domain: " + domain.getId() + " in each application. ";
				throw new StorageException( msg + e.getMessage(), e.getCause() );
			} catch (InterruptedException e) {
				String msg = "Unable to remove domain: " + domain.getId() + " from " + appId + " application. ";
				throw new StorageException( msg + e.getMessage(), e.getCause() );
			} catch (AstException e) {
				String msg = "Unable to remove domain: " + domain.getId() + " from " + appId + " application. ";
				throw new StorageException( msg + e.getMessage(), e.getCause() );
			}
		}
        return domain;
	}

	@Override
	public IRelation removeRelation(String appId, String domainId, IRelation relation) 
			throws StorageException {
		try {
			IDomain storagedDomain = parseDomainStorage( domainId ).getDomain();
			storagedDomain.getDomainApplication( appId ).removeUser( relation );
			this.as.replaceDomainInApplications( "", storagedDomain );
			write( storagedDomain ); // Actualiza el fichero para la entidad.
	        return relation;
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (AstException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation removeProfile(String appId, String domainId, IRelation relation) 
			throws StorageException {
		try {
			IDomain storagedDomain = parseDomainStorage( domainId ).getDomain();
			storagedDomain.getDomainApplication( appId ).removeProfile( relation );
			this.as.replaceDomainInApplications( "", storagedDomain );
			write( storagedDomain ); // Actualiza el fichero para la entidad.
	        return relation;
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (AstException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		}
//		IDomain domain = this.as.getApplication(appId).getDomain(domainId);
//		domain.getDomainApplication(appId).removeProfile(relation);
//		write(domain); // Actualiza el fichero para la entidad.
//        return relation;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateAccessPolicy(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy)
	 */
	public void updateAccessPolicy(String appId, String domainId, IAccessPolicy accessPolicy) 
				throws StorageException {
//	TODO	Modificar la carga de dominios, para que sean unicos.
//	Modifica el usuario en todas las instancias de Domain generadas para cada aplicacion.
		Iterator<IApplication> iter = this.as.applications().values().iterator();
		while ( iter.hasNext() ) {
			IApplication app = iter.next();
			app.getDomain( domainId ).setAccessPolicy(accessPolicy);
		}
		IDomain domain = this.as.getApplication( appId ).getDomain( domainId );
//		domain.setAccessPolicy(accessPolicy);
		write(domain);
	}

	@Override
	public void updateDSMD(String appId, String domainId, IDataSourceMetaData metadata) 
				throws StorageException {
		try {
			IDomain storagedDomain = parseDomainStorage( domainId ).getDomain();
			if ( appId != null ) {
				DomainApplication da = 
					(DomainApplication) storagedDomain.getDomainApplication( appId );
				da.setDataSourceMetaData( metadata );
				this.as.replaceDomainInApplications( appId, storagedDomain );
			} else {
				storagedDomain.setDataSourceMetaData( metadata );
				this.as.replaceDomainInApplications( "", storagedDomain );
			}
			write( storagedDomain ); // Actualiza el fichero para la entidad.
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (AstException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		}
	}

	@Override
	public void addDomain(String appId, IDomain domain, Boolean flag) throws StorageException {
		try {
			IDomain storagedDomain = domain;
			try {
				storagedDomain = parseDomainStorage( domain.getId() ).getDomain();
				if ( flag ) {
					( (Domain)storagedDomain ).add( domain.getDomainApplication( appId ) );
				}
			} catch (IOException e) {
			}
			( (Application) this.as.getApplication( appId ) ).addDomain( storagedDomain );
			this.as.replaceDomainInApplications( appId, storagedDomain );
			write( storagedDomain );
			write(null);
		} catch (InterruptedException e) {
			String msg = "Unable to update domain: " + domain.getId() + " inside " + appId + " application";
			LOGGER.error( msg, e );
		} catch (AstException e) {
			String msg = "Unable to update domain: " + domain.getId() + " inside " + appId + " application";
			LOGGER.error( msg, e );
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domain.getId() + " in each application." + e.getMessage();
			LOGGER.error( msg, e );
		}
	}

	@Override
	public void loadDomain(IDomain domain) throws StorageException {
		try {
			Iterator<IDomainApplication> it = domain.applications().iterator();
			while (it.hasNext()) {
				IDomainApplication dApp = it.next();
				Application app = (Application) this.as.getApplication( dApp.getId() );
				app.replaceDomain( domain.getId(), domain );
			}
			this.as.replaceDomainInApplications( "", domain );
			write( domain );
			write(null);
		} catch (AstException e) {
			String msg = "Unable to update domain: " + domain.getId() + " inside each application";
			LOGGER.error( msg, e );
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domain.getId() + " in each application." + e.getMessage();
			LOGGER.error( msg, e );
		}
	}

	@Override
	public void updateDomain(String appId, IDomain domain) throws StorageException {
		IApplication application = this.as.getApplication(appId);
		if ( application.getDomain( domain.getId() ) == null ) {
			addDomain( appId, domain, false );
		} else {
			write(domain); // Actualiza el fichero para la entidad.
		}
	}

	@Override
	public IRelation updateRelation(String appId, String domainId, IRelation relation) 
			throws StorageException {
		try {
			IDomain storagedDomain = parseDomainStorage( domainId ).getDomain();
			IRelation _relation = storagedDomain.getDomainApplication( appId ).updateUser( relation );
			this.as.replaceDomainInApplications( appId, storagedDomain );
			write( storagedDomain ); // Actualiza el fichero para la entidad.
	        return _relation;
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (AstException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		}
	}

	@Override
	public IRelation updateProfile(String appId, String domainId, IRelation relation) 
			throws StorageException {
		try {
			IDomain storagedDomain = parseDomainStorage( domainId ).getDomain();
			IRelation _relation = storagedDomain.getDomainApplication( appId ).updateProfile( relation );
			this.as.replaceDomainInApplications( appId, storagedDomain );
			write( storagedDomain ); // Actualiza el fichero para la entidad.
	        return _relation;
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (AstException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			String msg = "Unable to replace domain: " + domainId + " in each application. ";
			throw new StorageException( msg + e.getMessage(), e.getCause() );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#loadUsers(com.code.aon.jaas.client.ast.IDomain)
	 */
	@Override
	public List<StorageException> loadUsers(IDomain domain) throws StorageException {
		List<StorageException> errors = new ArrayList<StorageException>();
		try {
			IDomain storagedDomain = parseDomainStorage( domain.getId() ).getDomain();
			Iterator<IUser> users = domain.standaloneUsers().values().iterator();
			while (users.hasNext()) {
				IUser user = users.next();
				try {
					storagedDomain.add( user );
				} catch (UserAlreadyExistException e) {
					errors.add( e );
				}
			}
			Iterator<IDomainApplication> apps = domain.applications().iterator();
			while (apps.hasNext()) {
				IDomainApplication app = apps.next();
				Iterator<IRelation> relations = app.users().iterator();
				while (relations.hasNext()) {
					IRelation user = relations.next();
					DomainApplication da = 
						(DomainApplication) storagedDomain.getDomainApplication( app.getId() );
					if ( da.getUser( user.getId() ) == null )
						da.addUser( user );
					else
						errors.add( new StorageException( "aon_security_relation_exist", user.getId() ) );
				}
			}
			this.as.replaceDomainInApplications( "", storagedDomain );
			write( storagedDomain );
			write(null);
		} catch (AstException e) {
			String msg = "Unable to update domain: " + domain.getId() + " inside each application";
			LOGGER.error( msg, e );
		} catch (IOException e) {
			String msg = "Unable to replace domain: " + domain.getId() + " in each application." + e.getMessage();
			LOGGER.error( msg, e );
		} catch (InterruptedException e) {
			LOGGER.error( "Unable to load storaged domain: " + domain.getId(), e );
		}
		return errors;
	}

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#addUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String, java.lang.String)
	 */
	public void addUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException {
//	TODO	Modificar la carga de dominios, para que sean unicos.
//	Modifica el usuario en todas las instancias de Domain generadas para cada aplicacion.
		Iterator<IApplication> iter = this.as.applications().values().iterator();
		while ( iter.hasNext() ) {
			IApplication app = iter.next();
			IDomain domain = app.getDomain( domainId );
			if ( domain != null ) {
				domain.add( user );
			}
		}
		IDomain domain = this.as.getApplication( appId ).getDomain( domainId );
		write( domain ); // Actualiza el fichero para la entidad.
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
	 */
	public IUser updateUser(String appId, String domainId, IUser user, String oldUserId) 
			throws StorageException {
//	TODO	Modificar la carga de dominios, para que sean unicos.
//	Modifica el usuario en todas las instancias de Domain generadas para cada aplicacion.
		Iterator<IApplication> iter = this.as.applications().values().iterator();
		while ( iter.hasNext() ) {
			IApplication app = iter.next();
			IDomain domain = app.getDomain( domainId );
			if ( domain != null )
				domain.update( user, oldUserId );
		}
		IDomain domain = this.as.getApplication( appId ).getDomain( domainId );
		write( domain ); // Actualiza el fichero para la entidad.
		return user;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeUser(java.lang.String, com.code.aon.jaas.client.ast.IUser)
	 */
	public IUser removeUser(String appId, String domainId, IUser user) throws StorageException {
//	TODO	Modificar la carga de dominios, para que sean unicos.
//	Modifica el usuario en todas las instancias de Domain generadas para cada aplicacion.
		Iterator<IApplication> iter = this.as.applications().values().iterator();
		while ( iter.hasNext() ) {
			IApplication app = iter.next();
			IDomain domain = app.getDomain( domainId );
			if ( domain != null )
				domain.remove( user );
		}
		IDomain domain = this.as.getApplication( appId ).getDomain( domainId );
		write( domain ); // Actualiza el fichero para la entidad.
		return user;
	}

	@Override
	public void write(IDomain domain) throws StorageException {
		if (domain == null) {
	    	LOGGER.debug( "Writing deployed applications file" );
			this.as.write(); // Serializa el fichero de aplicaciones desplegadas.
		} else {
	    	LOGGER.debug( "Writing application: DOMAIN[{}]", domain.getId() );
			try {
				IStorage es = getStorage(domain);
				if (es != null) {
					es.write(); // Serializa el fichero de la entidad.
				}
			} catch (IOException e) {
				throw new StorageException( e.getMessage(), e.getCause() );
			}
		}
	}

	/**
	 * Returns the proper storage manager.
	 * 
	 * @param domain
	 * @return
	 * @throws IOException
	 */
	private IStorage getStorage(IDomain domain) throws IOException {
		if (domain != null) {
			DomainStorage es = new DomainStorage();
			es.setDomain(domain);
			URL config = 
				new File( this.as.getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + IStorage.XML ).toURL();
			es.initialize( StorageManager.getInstance( config, DomainRenderer.getInstance() ) );
			return es;
		}
		return null;
	}

	/**
	 * Parse the Domain file resource, and returns the DomainStorage.
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws AstException
	 */
	private DomainStorage parseDomainStorage(String id) 
			throws IOException, InterruptedException, AstException {
		File file = 
			new File( this.as.getStorageDir().getCanonicalPath() + File.separator + id + "." + IStorage.XML );
		URL url = 
			StorageManager.getInstance( file.toURL(), DomainRenderer.getInstance() ).read();
		return (DomainStorage) AstLoader.getInstance().parse( 1, url.openStream() );
	}
}
