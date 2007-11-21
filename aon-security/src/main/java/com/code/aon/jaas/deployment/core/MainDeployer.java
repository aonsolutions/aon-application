/*
 * Created on 11-nov-2004
 *
 */
package com.code.aon.jaas.deployment.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.core.Application;

import com.code.aon.jaas.deployment.DeployerFactoryManager;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.DeploymentState;
import com.code.aon.jaas.deployment.IDeployer;

import com.code.aon.jaas.deployment.event.DeployerEvent;
import com.code.aon.jaas.deployment.event.IDeployerListener;

import com.code.aon.jaas.vendor.VendorFactoryManager;

/**
 * Esta clase despliega una aplicación dentro del módulo de seguridad, previamente la copia en
 * el contenedor de servlets o servidor de aplicaciones para el despliegue en el mismo.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 *  
 */
public class MainDeployer implements IDeployer {

    /** Obtiene un logger apropiado. */
    private static final Log LOGGER = LogFactory.getLog( MainDeployer.class.getName() );

    /** Defualt Config Resource name. */
	public static final String DEFAULT_CONFIG_RESOURCE_NAME = "aon.workspace/deployed.xml";

	/** Mapa de URL -> DeploymentInfo */
    private final Map<URL, DeploymentInfo> deploymentMap = new LinkedHashMap<URL, DeploymentInfo>();

    /** Una lista de <code>DeployerListener</code>. */
    private List<IDeployerListener> listeners = new LinkedList<IDeployerListener>();

    /** Aplicación que está siendo desplegada. */
    IApplication app;

    /** Identificador del servicio. */
    String deployerInfo = "service=AonMainDeployer";

    /** Nombre del recurso por defecto. */
    String configResource = DEFAULT_CONFIG_RESOURCE_NAME;

    /** 
     * Constructor sin parámetros. 
     */
    public MainDeployer() {

    }

    /**
     * Constructor de la clase a partir de un identificador de servicio.
     * 
     * @param deployerInfo
     */
    public MainDeployer(String deployerInfo) {
        this.deployerInfo = deployerInfo;
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#addDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener)
     */
    public void addDeployerListener(IDeployerListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#removeDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener)
     */
    public void removeDeployerListener(IDeployerListener l) {
        listeners.remove(l);
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#isDeployed(java.net.URL)
     */
    public boolean isDeployed(URL url) {
        DeploymentInfo di = getDeployment(url);
        if (di == null) {
            return false;
        }
        return di.state == DeploymentState.STARTED;
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#getDeployerInfo(java.lang.String)
     */
    public String getDeployerInfo(String info) {
        return deployerInfo;
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#getConfigResource(java.lang.String)
     */
    public URL getConfigResource(String resource) throws IOException {
        try {
            // Try as a URL
			URL url = new URL( this.configResource );
			File file = new File( url.getPath() );
			if ( !file.exists() ) {
				setConfigResource( DEFAULT_CONFIG_RESOURCE_NAME );
				throw new MalformedURLException();
			}

            return url;
        } catch (MalformedURLException e) {
            // Try as a resource
            ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            URL loginConfigURL = tcl.getResource( this.configResource );
            if ( loginConfigURL == null ) {
                // Try as a file
                String path = VendorFactoryManager.getVendorFactory( getDeployerInfo(null) ).create().getSecurityPath();
                return new File(path + this.configResource).toURL();
            }
            return loginConfigURL;
        }
    }

    /**
     * Devuelve la información de despliegue asociada a la URL. 
     * 
     * @param url
     * @return DeploymentInfo
     */
    public DeploymentInfo getDeployment(URL url) {
        return deploymentMap.get(url);
    }

    /**
     * Asigna el recurso contenedor de las aplicaciones desplegadas.
     * 
     * @param configResource
     */
    public void setConfigResource(String configResource) {
        this.configResource = configResource;
    }

    /**
     * Devuelve la aplicación que está siendo desplegada.
     * 
     * @return IApplication
     */
    public IApplication getApp() {
        return this.app;
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#deploy(java.net.URL)
     */
    public void deploy(URL url) throws DeploymentException {
        DeploymentInfo sdi = getDeployment(url);
        // if it does not exist create a new deployment
        if (sdi == null) {
            sdi = new DeploymentInfo(url);
            deploy(sdi);
        }

        if (sdi.state != DeploymentState.STARTED) {
            throw new DeploymentException("Incomplete state:" + sdi.state
                    + ", deployment URL:" + sdi.url.getFile());
        }
        fireApplicationDeployed(new DeployerEvent(app));
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#undeploy(java.net.URL)
     */
    public void undeploy(URL url) throws DeploymentException {
        DeploymentInfo sdi = getDeployment(url);
        if (sdi != null) {
            undeploy(sdi);
            fireApplicationUndeployed( new DeployerEvent(sdi.shortName) );
        }
    }

    /**
     * Despliega la aplicación.
     * 
     * @param deployment
     * @throws DeploymentException
     */
    protected void deploy(DeploymentInfo deployment) throws DeploymentException {
//	If we are already deployed return
        if (isDeployed(deployment.url)) {
            LOGGER.debug( "Package: " + deployment.url + " is already deployed" );
            return;
        }
        LOGGER.debug( "Starting deployment of package: " + deployment.url );
		if (init(deployment)) {
		    start(deployment);
		    LOGGER.debug("Deployed package: " + deployment.url);
		} else {
		    LOGGER.debug( "Deployment of package: " + deployment.url + " is waiting for an appropriate deployer." );
		}
    }

    /**
     * Elimina la aplicación.
     * 
     * @param di
     */
    protected void undeploy(DeploymentInfo di) {
        destroy(di);
    }

    /**
     * Lanza un evento cada vez que una aplicación es desplegada.
     * 
     * @param event
     */
    protected void fireApplicationDeployed(DeployerEvent event) {
        Iterator<IDeployerListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            IDeployerListener element = iter.next();
            try {
				element.applicationDeployed(event);
			} catch (DeploymentException e) {
				LOGGER.fatal( "Deploying exception:" + e.getMessage() );
			}
        }
    }

    /**
     * Lanza un evento cada vez que una aplicación es eliminada.
     * 
     * @param event
     */
    protected void fireApplicationUndeployed(DeployerEvent event) {
        Iterator<IDeployerListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            IDeployerListener element = iter.next();
            try {
				element.applicationUndeployed(event);
			} catch (DeploymentException e) {
				LOGGER.fatal( "Undeploying exception:" + e.getMessage() );
			}
        }
    }

    /**
     * El método <code>init</code> es el primer paso del despliegue. Las tareas son las de copiar
     * los fuentes si es necesario, los cargadores, e identificar el paquete encargade del despliegue.  
     * 
     * @param deployment
     * @return boolean
     * @throws DeploymentException, if an error occurs
     */
    private boolean init(DeploymentInfo deployment) throws DeploymentException {
        //	If we are already deployed return
        if (isDeployed(deployment.url)) {
            LOGGER.debug( "Package: " + deployment.url + " is already deployed" );
            return false;
        }

        try {
            //	Create a local copy of that File, the sdi keeps track of the copy directory
            if (deployment.localUrl == null) {
                makeLocalCopy(deployment);
                URL[] localCP = { deployment.localUrl };
                deployment.localCl = new URLClassLoader(localCP);
               }
            //	What deployer is able to deploy this file
            deployment.deployer = DeployerFactoryManager.createDeployer(deployment);
            deployment.state = DeploymentState.INIT_DEPLOYER;
            deployment.appServerName = getDeployerInfo(null);
            //	we have the deployer, continue deployment.
            deployment.deployer.init(deployment);
            deployment.deployer.addSubDeployerListener(
                    app = Application.createApplication( deployment.shortName, deployment.isFile ) );
            //	initialize the unified classloaders for this deployment
            deployment.createClassLoaders();
            deployment.state = DeploymentState.INITIALIZED;
            deploymentMap.put(deployment.url, deployment);
        } catch (Exception e) {
            deployment.state = DeploymentState.FAILED;
            throw new DeploymentException("exception in init of " + deployment.url, e);
        } finally {
            //	whether you do it or not, for the autodeployer
            try {
                URL url = (deployment.localUrl == null) ? deployment.url
                        : deployment.localUrl;
                long lastModified = -1;
                if (url.getProtocol().equals("file")) {
                    lastModified = new File(url.getFile()).lastModified();
                } else {
                    lastModified = url.openConnection().getLastModified();
                }
                deployment.lastModified = lastModified;
                deployment.lastDeployed = System.currentTimeMillis();
            } catch (IOException ignore) {
                deployment.lastModified = System.currentTimeMillis();
                deployment.lastDeployed = System.currentTimeMillis();
            }
        }
        return true;
    }

    /**
     * Descarga el fichero jar o directorio al que apunta la URL. En caso de ser un directorio, se 
     * empaqueta en un fichero jar. 
     * 
     * @param sdi
     * @throws DeploymentException, if an error occurs
     */
    private void makeLocalCopy(DeploymentInfo sdi) throws DeploymentException {
        sdi.localUrl = sdi.url;
    }

    /**
     * El método <code>start</code> es el tercer y último paso del despliegue. 
     * El propósito de este paso, es el de generar las relaciones entre los componentes. 
     * Por ejemplo, los links de ejb son generados aquí.
     * 
     * @param deployment
     * @throws DeploymentException, if an error occurs
     */
    private void start(DeploymentInfo deployment) throws DeploymentException {
        deployment.status = "Starting";
        try {
            //	Deploy this SDI, if it is a deployable type
            if (deployment.deployer != null) {
                deployment.state = DeploymentState.START_DEPLOYER;
                deployment.deployer.start(deployment);
                deployment.state = DeploymentState.STARTED;
                deployment.status = "Deployed";
            } else {
                LOGGER.fatal( "Still no deployer for package in start step: " + deployment.shortName );
            }
        } catch (Throwable t) {
            deployment.state = DeploymentState.FAILED;
            deployment.status = "Deployment FAILED reason: " + t.getMessage();
            if (t instanceof DeploymentException) {
                throw (DeploymentException) t;
            }
            throw new DeploymentException("Could not create deployment: " + deployment.url, t);
        }
    }

    /**
     * El método <code>destroy</code> es el segundo y último paso de la eliminación de una aplicación.
     * 
     * @param di
     */
    private void destroy(DeploymentInfo di) {
        deploymentMap.remove(di.url);
//	Nuke my stuff, this includes the class loader
        di.cleanup();
        LOGGER.debug( "Undeployed " + di.url );
    }

    static {
        try {
            Class.forName(EARDeployerFactory.class.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.fatal( "EARDeployerFactory:" + e.getMessage() );
        }
        try {
            Class.forName(JARDeployerFactory.class.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.fatal( "JARDeployerFactory:" + e.getMessage() );
        }
        try {
            Class.forName(WARDeployerFactory.class.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.fatal( "WARDeployerFactory:" + e.getMessage() );
        }
    }

}