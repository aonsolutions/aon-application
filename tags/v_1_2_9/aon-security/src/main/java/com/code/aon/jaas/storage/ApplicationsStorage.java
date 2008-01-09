package com.code.aon.jaas.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Application;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.client.ast.core.Domain;
import com.code.aon.jaas.client.xml.DomainRenderer;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.event.DeployerEvent;
import com.code.aon.jaas.deployment.event.IDeployerListener;
import com.code.aon.jaas.deployment.util.FileUtis;

/**
 * Applications storage manager. This class saves <b>deployed.xml</b> file.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2006
 * @since 1.0
 *  
 */
public class ApplicationsStorage implements INode, IStorage, IDeployerListener {

	private static final long serialVersionUID = -3828039834213639137L;

	/** ApplicationsStorage Logger. */
    private static final Log LOGGER = LogFactory.getLog( ApplicationsStorage.class.getName() );

    /** Tells storage manager of serializing configuration resource <b>deployed.xml</b> file. */
	private transient StorageManager storageManager;

	/** Deployment options. */
	private Map<String, IOption> options = new HashMap<String, IOption>();

	/** Deployment applications. */
	private Map<String, IApplication> applications = new LinkedHashMap<String, IApplication>();

	/**
	 * Adds an option. 
	 * 
	 * @param application
	 */
	public void addOption(IOption option) {
		this.options.put( option.getId(), option );
	}

    /**
     * Returns deployment options.
     * 
     * @return
     */
	public Map<String, IOption> options() {
		return Collections.unmodifiableMap( this.options );
	}

	/**
	 * Adds an application. 
	 * 
	 * @param application
	 */
	public void addApplication(IApplication application) {
		if ( !this.applications.containsKey( application.getId() ) ) {
			this.applications.put( application.getId(), application );
		}
	}

    /**
     * Returns a map of deployment applications.
     * 
     * @return
     */
	public Map<String, IApplication> applications() {
		return Collections.unmodifiableMap( this.applications );
	}

    /**
     * Returns the application for given name. <tt>Null</tt> if application does not exist.
     * 
     * @param name
     * @return
     */
	public IApplication getApplication(String name) {
		return this.applications.get(name);
	}

    /**
     * Returns the application for given context name. <tt>Null</tt> 
     * if application does not exist.
     * 
     * @param ctx
     * @return
     */
	public IApplication getApplication4Ctx(String ctx) {
		Iterator<IApplication> iter = this.applications.values().iterator();
		while (iter.hasNext()) {
			IApplication element = iter.next();
			if ( element.getContext().equals(ctx) )
				return element;
		}
		return null;
	}

	/**
	 * Replaces a domain in all deployed applications.
	 *  
	 * @param app
	 * @param domain
	 * @throws IOException 
	 * @throws AstException
	 * 
	 * TODO. Asociar a cada aplicacion solamente la parte del objeto IDomain que le interesa 
	 */
	public void replaceDomainsInApplication(IApplication app, IDomain domain) 
				throws IOException, AstException {
		File resource = new File( getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + XML );
		DomainStorage es = 
			(DomainStorage) AstLoader.getInstance().parse( 1, resource.toURL().openStream() );
		( (Application)app ).replaceDomain( domain.getId(), es.getDomain() );
		LOGGER.debug( "Domain [" + domain.getId() + "] Loaded and Replaced inside [" + app.getId() + "] Application." );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	@Override
	public void accept(INodeVisitor visitor) {
	    visitor.visitDeployed(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	@Override
	public String getId() {
		return ApplicationsStorage.class.getName();
	}

	@Override
	public void initialize(StorageManager storageManager) {
		this.storageManager = storageManager;
	}

	@Override
	public boolean isDirty() {
		try {
			LOGGER.fatal( "isDirty " + FileUtis.getDirtyFile( getStorageDir().getCanonicalPath() ) + " " + FileUtis.getDirtyFile( getStorageDir().getCanonicalPath() ).exists() ) ;
			return !FileUtis.getDirtyFile( getStorageDir().getCanonicalPath() ).exists();
		} catch (IOException e) {
			LOGGER.fatal( e.getMessage() );
			return true;
		}
	}

	@Override
	public File getStorageDir() {
		return new File( this.storageManager.getUrl().getFile() ).getParentFile();
	}

	@Override
	public boolean erase() throws StorageException {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public URL read() throws StorageException {
		try {
			return this.storageManager.read();
	    } catch (IOException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
	    } catch (InterruptedException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
	    }
	}

	@Override
	public void write() throws StorageException {
		this.storageManager.write(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.deployment.event.IDeployerListener#applicationDeployed(com.code.aon.jaas.deployment.event.DeployerEvent)
	 */
	public void applicationDeployed(DeployerEvent event) throws DeploymentException {
    	Application app = (Application)event.getSource();
    	if ( options.get( IConstants.ALGORITHM ) != null )
    		app.setHashAlgorithm( options.get( IConstants.ALGORITHM ).getValue() );
    	if ( options.get( IConstants.ENCODING ) != null )
    		app.setHashEncoding( options.get( IConstants.ENCODING ).getValue() );
//	Check if deployed application already exist inside "deployed.xml" file.
    	if ( this.applications.containsKey( app.getId() ) ) {
			LOGGER.debug( "applicationDeployed despliegue de una applicacion existente:" + app.getId() + " " + app.hashCode() );
        	Application storageApp = (Application) this.applications.get( app.getId() );
	    	storageApp.actualize(app);
	    	Iterator<IDomain> iter = storageApp.domains().iterator();
	    	while (iter.hasNext()) {
				IDomain domain = iter.next();
				try {
					File resource = 
						new File( getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + XML);
					DomainStorage es = 
						(DomainStorage) AstLoader.getInstance().parse( 1, resource.toURL().openStream() );
//	TODO. Asociar a cada aplicacion solamente la parte del objeto IDomain que le interesa
					storageApp.replaceDomain( domain.getId(), es.getDomain() );
					LOGGER.debug( "Domain [" + domain.getId() + "] Loaded and Replaced inside [" + storageApp.getId() + "] Application." );
				} catch (IOException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} catch (AstException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} 
			}
    	} else {
//	Check if deployed application has defined a domain, in other case creates a default one.
			LOGGER.debug( "applicationDeployed despliegue de nueva applicacion:" + app.getId() + " " + app.hashCode() );
			if ( app.domains().size() == 0 ) {
				( (Application)app ).addDomain( Domain.getInstance(app.getId()) );
			}
			addApplication(app);
			Iterator<IDomain> iter = app.domains().iterator();
			while (iter.hasNext()) {
				IDomain domain = iter.next();
				LOGGER.debug( "applicationDeployed dominios a desplegar:" + domain.getId() + " " + domain.hashCode() );
				try {
					File resource = 
						new File( getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + XML);
					DomainStorage es = new DomainStorage();
//	Check if domain file already exist, if true reads the file and adds the new domain data. 
					if ( resource.exists() ) {
						es = (DomainStorage) AstLoader.getInstance().parse( 1, resource.toURL().openStream() );
						Domain existingDomain = (Domain) es.getDomain();
						LOGGER.debug( "applicationDeployed dominio ya desplegado:" + existingDomain.getId() + " " + existingDomain.hashCode() );
						existingDomain.add( domain.getDomainApplication( app.getId() ) );
						Iterator<IUser> iterator = domain.standaloneUsers().values().iterator();
						while (iterator.hasNext()) {
							IUser user = iterator.next();
							//	Check if user already exist.
							if ( !existingDomain.standaloneUsers().containsKey(user.getId()) ) {
								existingDomain.add( user );
							}
						}
						domain = existingDomain;
					} else {
						resource.createNewFile();
					}
					LOGGER.debug( "applicationDeployed dominio nuevo:" + domain.getId() + " " + domain.hashCode() );
					es.setDomain(domain);
					es.initialize( StorageManager.getInstance( resource.toURL(), DomainRenderer.getInstance() ) );
//					es.initialize( new StorageManager( resource.toURL(), DomainRenderer.getInstance() ) );
					es.write();
					LOGGER.debug( "Domain [" + domain.getId() + "] updated and wrote." );
				} catch (IOException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} catch (AstException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} catch (StorageException e) {
					LOGGER.warn( e );
					throw new DeploymentException(e.getMessage(), e.getCause());
				}
			}
    		try {
				write();
				LOGGER.debug( "New Application [" + app.getId() + "] added and wrote." );
			} catch (StorageException e) {
				throw new DeploymentException(e.getMessage(), e.getCause());
			}
    	}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.deployment.event.IDeployerListener#applicationUndeployed(com.code.aon.jaas.deployment.event.DeployerEvent)
	 */
	public void applicationUndeployed(DeployerEvent event) throws DeploymentException {
		String appName = (String) event.getSource();
		IApplication app = this.applications.remove(appName);
		Iterator<IDomain> iter = app.domains().iterator();
		while (iter.hasNext()) {
			IDomain domain = iter.next();
			try {
				File resource = 
					new File( getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + XML );
				DomainStorage es = (DomainStorage) AstLoader.getInstance().parse( 1, resource.toURL().openStream() );
				es.getDomain().remove(appName);
				es.initialize( StorageManager.getInstance( resource.toURL(), DomainRenderer.getInstance() ) );
//				es.initialize( new StorageManager( resource.toURL(), DomainRenderer.getInstance() ) );
				es.write();
				replaceDomainsInApplication( app, es.getDomain() );
			} catch (IOException e) {
				throw new DeploymentException(e.getMessage(), e);
			} catch (AstException e) {
				throw new DeploymentException(e.getMessage(), e);
			} catch (StorageException e) {
				throw new DeploymentException(e.getMessage(), e.getCause());
			}
		}
		try {
			write();
		} catch (StorageException e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

}
