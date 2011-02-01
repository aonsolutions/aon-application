package com.code.aon.jaas.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.code.aon.jaas.deployment.util.FileUtils;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationsStorage.class);

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
			IApplication app = iter.next();
			if ( app.getContext() != null && app.getContext().equals(ctx) )
				return app;
		}
		return null;
	}

	/**
	 * Replaces the domain in all deployed applications that had one, excluding application 
	 * identifier that has been updated previously.
	 *  
	 * @param appId
	 * @param domain
	 * @throws IOException 
	 * @throws AstException
	 * 
	 * TODO. Asociar a cada aplicacion solamente la parte del objeto IDomain que le interesa 
	 */
	public void replaceDomainInApplications(String appId, IDomain domain) throws IOException, AstException {
		Iterator<IApplication> it = this.applications.values().iterator();
		while (it.hasNext()) {
			Application app = (Application) it.next();
			if ( !app.getId().equals( appId ) && app.getDomain( domain.getId() ) != null ) {
				app.replaceDomain( domain.getId(), domain );
				LOGGER.debug( "Domain [{}] Loaded and Replaced inside [{}] Application",  domain.getId(), app.getId() );
			}
		}
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
			return !FileUtils.getUptodateFile( getStorageDir().getCanonicalPath() ).exists();
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
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
			LOGGER.debug( "Deploying an existing application: {} {}", app.getId(), app.hashCode() );
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
					LOGGER.debug( "Domain [{}] Loaded and Replaced inside [{}] Application", domain.getId(), storageApp.getId() );
				} catch (IOException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} catch (AstException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} 
			}
    	} else {
//	Check if deployed application has defined a domain, in other case creates a default one.
			LOGGER.debug( "Deploying a new Application: {} {}", app.getId(), app.hashCode() );
			if ( app.domains().size() == 0 ) {
				( (Application)app ).addDomain( Domain.getInstance(app.getId()) );
			}
			addApplication(app);
			Iterator<IDomain> iter = app.domains().iterator();
			while (iter.hasNext()) {
				IDomain domain = iter.next();
				LOGGER.debug( "Deploying domain: {} {}", domain.getId(), domain.hashCode() );
				try {
					File resource = 
						new File( getStorageDir().getCanonicalPath() + File.separator + domain.getId() + "." + XML);
					DomainStorage es = new DomainStorage();
//	Check if domain file already exist, if true reads the file and adds the new domain data. 
					if ( resource.exists() ) {
						es = (DomainStorage) AstLoader.getInstance().parse( 1, resource.toURL().openStream() );
						Domain existingDomain = (Domain) es.getDomain();
						LOGGER.debug( "Deployed Domain: {} {}", existingDomain.getId(), existingDomain.hashCode() );
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
					LOGGER.debug( "New Domain: {} {}", domain.getId(), domain.hashCode() );
					es.setDomain(domain);
					es.initialize( StorageManager.getInstance( resource.toURL(), DomainRenderer.getInstance() ) );
//					es.initialize( new StorageManager( resource.toURL(), DomainRenderer.getInstance() ) );
					es.write();
					LOGGER.debug( "Domain [{}] updated and wrote", domain.getId() );
				} catch (IOException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} catch (AstException e) {
					throw new DeploymentException(e.getMessage(), e.getCause());
				} catch (StorageException e) {
					LOGGER.warn( e.getMessage(), e );
					throw new DeploymentException(e.getMessage(), e.getCause());
				}
			}
    		try {
				write();
				LOGGER.debug( "New Application [{}] added and wrote", app.getId() );
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
				es.write();
				replaceDomainInApplications( app.getId(), es.getDomain() );
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
