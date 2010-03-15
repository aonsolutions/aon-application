package com.code.aon.jaas.deployment.core;

import java.io.IOException;
import java.net.URL;

import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.ISubDeployer;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.event.ISubDeployerListener;
import com.code.aon.jaas.deployment.event.SubDeployerEvent;
import com.code.aon.jaas.storage.DomainStorage;

/**
 * This class implements the <code>ISubDeployer</code> that initializes and starts any kind of this
 * application, EAR, WAR, o JAR deployment.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 * @see ISubDeployer
 *  
 */
public class SubDeployer implements ISubDeployer {

    /** XML file name with the DataSource, profiles, users of the application being deployed. */
    static final String DEPLOYED_FILE = "profiles.xml";

    /** Application domains source URL. */
    URL deployed;

    /** Application deployer visitor. */
    SubDeployerVisitor visitor;

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.ISubDeployer#addSubDeployerListener(com.code.aon.jaas.deployment.event.ISubDeployerListener)
     */
    public void addSubDeployerListener(ISubDeployerListener l) {
        if (visitor == null) {
            visitor = new SubDeployerVisitor();
        }
        visitor.addSubDeployerListener(l);
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.ISubDeployer#removeSubDeployerListener(com.code.aon.jaas.deployment.event.ISubDeployerListener)
     */
    public void removeSubDeployerListener(ISubDeployerListener l) {
        visitor.removeSubDeployerListener(l);
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.ISubDeployer#init(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public void init(DeploymentInfo di) throws DeploymentException {
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.ISubDeployer#start(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public void start(DeploymentInfo di) throws DeploymentException {
        if (deployed != null) {
        	try {
        		DomainStorage storage = 
        			(DomainStorage) AstLoader.getInstance().parse( 1, this.deployed.openStream() );
				IDomain domain = storage.getDomain();
				visitor.fireDomainFound( new SubDeployerEvent(domain) );
			} catch (AstException e) {
				throw new DeploymentException(e.getMessage());
			} catch (IOException e) {
				throw new DeploymentException(e.getMessage());
			}
        }
    }

}