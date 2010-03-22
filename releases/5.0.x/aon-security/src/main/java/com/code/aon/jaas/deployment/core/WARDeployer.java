package com.code.aon.jaas.deployment.core;

import java.net.URL;

import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.ISubDeployer;
import com.code.aon.jaas.deployment.ast.ISecurityDescriptor;
import com.code.aon.jaas.deployment.ast.core.AstLoader;

import com.code.aon.jaas.vendor.VendorFactoryManager;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * The web application deployer.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 14-may-2004
 * @since 1.0
 * @see ISubDeployer
 *  
 */
public class WARDeployer extends SubDeployer {

    /** WEB.XML deployment descriptor name. */
	public static final String WEB_XML = "web.xml";

    /** WEB.XML deployment descriptor <code>URL</code>. */
	private URL web;

    /** Application server deployment descriptor <code>URL</code>. */
	private URL vendorWEB;

	@Override
	public void init(DeploymentInfo di) throws DeploymentException {
		this.web = di.localCl.findResource( DeploymentInfo.WEB_INF + WEB_XML );
		String vendorWFile = VendorFactoryManager.create(di.appServerName).getVendorWEBFile();
		if (vendorWFile != null)
			this.vendorWEB = di.localCl.findResource( DeploymentInfo.WEB_INF + vendorWFile );
		this.deployed = di.localCl.findResource( DeploymentInfo.WEB_INF + DEPLOYED_FILE );
	}

	@Override
	public void start(DeploymentInfo di) throws DeploymentException {
		try {
			if ( this.web == null ) {
				String msg = "Application WAR file " + di.url + " does not exist, remove from configuration resource file.";
				throw new DeploymentException( msg, DeploymentInfo.WAR_DEPLOYMENT_FILE_NOT_FOUND, null );
			}

//	Parses the web.xml file and the vendor specific xml file: jboss-web.xml, weblogic.xml, ...
			ISecurityDescriptor isd = 
				(ISecurityDescriptor) new AstLoader().parse( this.web.openStream() );
			isd.accept(visitor);
			if (vendorWEB != null) {
				IVendorDescriptor jaas = 
					VendorFactoryManager.parse( di.appServerName, this.vendorWEB.openStream() );
				jaas.accept(visitor);
			}
		} catch (Exception e) {
			throw new DeploymentException(e.getMessage());
		}
		super.start(di);
	}

}