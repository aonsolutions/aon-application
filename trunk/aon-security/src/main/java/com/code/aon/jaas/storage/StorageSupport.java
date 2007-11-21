package com.code.aon.jaas.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Application;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.client.ast.core.DomainApplication;
import com.code.aon.jaas.client.xml.DomainRenderer;
import com.code.aon.jaas.deployment.ast.AstException;

public class StorageSupport implements IOperation {

    /** Class Logger. */
    private static final Log LOGGER = LogFactory.getLog( StorageSupport.class.getName() );

    /** Almacena las aplicaciones desplegadas. */
	public ApplicationsStorage as;

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#applications()
	 */
	public Collection applications() {
		return Collections.unmodifiableCollection( this.as.applications().values() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#applications(java.lang.String)
	 */
	public Collection getSDApplications(String securityDomain) {
        List<IApplication> apps = new LinkedList<IApplication>();
        Iterator iter = this.as.applications().values().iterator();
        while (iter.hasNext()) {
			IApplication app = (IApplication) iter.next();
			if ( app.getSecurityDomain().equals(securityDomain) ) {
				apps.add(app);
			}
		}
        return Collections.unmodifiableCollection(apps);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUserApplications(java.lang.String, java.lang.String)
	 */
	public List<IApplication> getUserApplications(String domainId, String userId) {
        List<IApplication> apps = new LinkedList<IApplication>();
        Iterator iter = this.as.applications().values().iterator();
        while (iter.hasNext()) {
			IApplication app = (IApplication) iter.next();
			IDomain domain = app.getDomain( domainId );
			if ( domain != null ) {
				Iterator domainApps = domain.applications().iterator();
				while (domainApps.hasNext()) {
					IDomainApplication ida = (IDomainApplication) domainApps.next();
					IApplication _app = this.as.getApplication( ida.getId() );
					if ( ida.getUser( userId ) != null && !apps.contains( _app ) ) {
						apps.add( _app );
					}
				}
			}
		}
        return Collections.unmodifiableList( apps );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getApplication(java.lang.String)
	 */
	public IApplication getApplication(String appId) {
        return this.as.getApplication(appId);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getApplication4Ctx(java.lang.String)
	 */
	public IApplication getApplication4Ctx(String ctx) {
        return this.as.getApplication4Ctx(ctx);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDSMDProperties(java.security.Principal)
	 */
	public Properties getDSMDProperties(Principal principal) {
		AuthPrincipal p = (AuthPrincipal) principal;
		IApplication app = this.as.getApplication4Ctx( p.getContext() );
		IDomain domain = (IDomain) app.getDomain( p.getDomain() );
		return domain.getDomainApplication( app.getId() ).getDataSourceMetaData().getProperties();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDomain(java.lang.String, java.lang.String)
	 */
	public IDomain getDomain(String appContext, String domainId) {
		IApplication app = this.as.getApplication4Ctx( appContext );
		return (IDomain) app.getDomain( domainId );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUser(java.security.Principal)
	 */
	public IUser getUser(Principal principal) {
		AuthPrincipal p = (AuthPrincipal) principal;
		return getUser( p.getContext(), p.getDomain(), p.getShortName() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public IUser getUser(String appContext, String domainId, String userId) {
		IApplication app = this.as.getApplication4Ctx( appContext );
		IDomain domain = (IDomain) app.getDomain( domainId );
		return domain.getStandaloneUser( userId );
	}

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#initApplicationDeployed(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy, com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 */
	public void initApplicationDeployed(String appId, String domainId, 
			Boolean privileged, String contextExtraInfo, 
			IAccessPolicy accessPolicy, IDataSourceMetaData metadata) throws StorageException {
		try {
			File file = new File( this.as.getParent() + File.separator + domainId + ".xml");
			URL url = 
				StorageManager.getInstance( file.toURL(), DomainRenderer.getInstance() ).read();
			DomainStorage es = (DomainStorage) AstLoader.getInstance().parse( 1, url.openStream() );
			IDomain domain = (IDomain) es.getDomain();
			if ( domain.getAccessPolicy() == null ) {
				domain.setAccessPolicy( accessPolicy );
			}
			if ( metadata != null && !metadata.getConnectionURL().equals( "" ) ) {
				( (DomainApplication)domain.getDomainApplication(appId) ).setDataSourceMetaData( metadata );
			}
//			TODO. Asociar a cada aplicacion solamente la parte del objeto IDomain que le interesa
			this.as.replaceDomainsInApplication( this.as.getApplication( appId ), es.getDomain() );
			write(domain);
		} catch (IOException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		} catch (AstException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
			throw new StorageException( e.getMessage(), e.getCause() );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#read(com.code.aon.jaas.client.ast.IDomain)
	 */
	public URL read(IDomain domain) throws StorageException {
		if (domain == null) {
	    	LOGGER.debug( "Reading deployed applications file" );
			return this.as.read(); // Devuelve la URL de aplicaciones desplegadas.
		}
    	LOGGER.debug( "Reading application: ENTITY[" + domain.getId() + "]" );
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

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
	 */
	public IDomain removeDomain(String appId, IDomain domain) throws StorageException {
		IDomain _domain = this.as.getApplication(appId).remove(domain);
		if (_domain != null) {
			write(null); // Actualiza el fichero de aplicaciones desplegadas.
			try {
				getStorage(domain).erase(); // Elimina fisicamente el fichero de la entidad.
			} catch (IOException e) {
				throw new StorageException( e.getMessage(), e.getCause() );
			}
		}
        return domain;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeRelation(java.lang.String, com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation removeRelation(String appId, String domainId, IRelation relation) 
			throws StorageException {
		IDomain domain = this.as.getApplication(appId).getDomain(domainId);
		domain.getDomainApplication(appId).removeUser(relation);
		write(domain); // Actualiza el fichero para la entidad.
        return relation;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation removeProfile(String appId, String domainId, IRelation relation) 
			throws StorageException {
		IDomain domain = this.as.getApplication(appId).getDomain(domainId);
		domain.getDomainApplication(appId).removeProfile(relation);
		write(domain); // Actualiza el fichero para la entidad.
        return relation;
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
			IDomain domain = app.getDomain( domainId );
			domain.setAccessPolicy(accessPolicy);
		}
		IDomain domain = this.as.getApplication( appId ).getDomain( domainId );
//		domain.setAccessPolicy(accessPolicy);
		write(domain);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateDSMD(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 */
	public void updateDSMD(String appId, String domainId, IDataSourceMetaData metadata) 
				throws StorageException {
		IApplication application = this.as.getApplication(appId);
		IDomain domain = application.getDomain(domainId);
		( (DomainApplication)domain.getDomainApplication(appId) ).setDataSourceMetaData(metadata);
		write(domain);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#addDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
	 */
	public void updateDomain(String appId, IDomain domain) throws StorageException {
		IApplication application = this.as.getApplication(appId);
		boolean update = application.getDomain( domain.getId() ) == null;
		( (Application)application ).replaceDomain( domain.getId(), domain );
		write(domain); // Actualiza el fichero para la entidad.
		if (update) {
			write(null); // Actualiza el fichero aplicaciones desplegadas.
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateRelation(java.lang.String, com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation updateRelation(String appId, String domainId, IRelation relation) 
			throws StorageException {
		IDomain domain = this.as.getApplication(appId).getDomain(domainId);
		IRelation _relation = domain.getDomainApplication(appId).updateUser(relation);
		write(domain); // Actualiza el fichero para la entidad.
        return _relation;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateRelation(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation updateProfile(String appId, String domainId, IRelation relation) 
			throws StorageException {
		IDomain domain = this.as.getApplication(appId).getDomain(domainId);
		IRelation _relation = domain.getDomainApplication(appId).updateProfile(relation);
		write(domain); // Actualiza el fichero para la entidad.
        return _relation;
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
			if ( domain != null )
				domain.add( user );
		}
		IDomain domain = this.as.getApplication( appId ).getDomain( domainId );
//		domain.add( user );
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
//		domain.update( user, oldUserId );
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
//		domain.remove( user );
		write( domain ); // Actualiza el fichero para la entidad.
		return user;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#write(com.code.aon.jaas.client.ast.IDomain)
	 */
	public void write(IDomain domain) throws StorageException {
		if (domain == null) {
	    	LOGGER.debug( "Writing deployed applications file" );
			this.as.write(); // Serializa el fichero de aplicaciones desplegadas.
		} else {
	    	LOGGER.debug( "Writing application: DOMAIN[" + domain.getId() + "]" );
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
	 * Devuelve la interface responsable del almcanamiento de entidades.
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
				new File( this.as.getParent() + File.separator + domain.getId() + ".xml").toURL();
			es.initialize( StorageManager.getInstance( config, DomainRenderer.getInstance() ) );
//			es.initialize( new StorageManager( config, DomainRenderer.getInstance() ) );
			return es;
		}
		return null;
	}

}
