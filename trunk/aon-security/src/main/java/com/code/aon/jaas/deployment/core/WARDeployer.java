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
 * Esta clase realiza el despliege de las politicas de seguridad de una aplicación WEB.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 14-may-2004
 * @since 1.0
 * @see ISubDeployer
 *  
 */
public class WARDeployer extends SubDeployer {

    /**
     * Indica el directorio donde buscar los ficheros XML.
     */
	public static final String WEB_INF = "WEB-INF/";

    /**
     * Indica el nombre del fichero WEB.
     */
	public static final String WEB_XML = "web.xml";

    /**
     * Indica la URL del fichero web.xml.
     */
	private URL web;

    /**
     * Indica la URL del fichero WEB del servidor de aplicaciones.
     */
	private URL vendorWEB;

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.deployment.ISubDeployer#init(com.code.aon.jaas.deployment.DeploymentInfo)
	 */
	public void init(DeploymentInfo di) throws DeploymentException {
		this.web = di.localCl.findResource(WEB_INF + WEB_XML);
		String vendorWFile = VendorFactoryManager.create(di.appServerName).getVendorWEBFile();
		if (vendorWFile != null)
			this.vendorWEB = di.localCl.findResource(WEB_INF + vendorWFile);
		this.deployed = di.localCl.findResource(WEB_INF + DEPLOYED_FILE);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.deployment.ISubDeployer#start(com.code.aon.jaas.deployment.DeploymentInfo)
	 */
	public void start(DeploymentInfo di) throws DeploymentException {
		try {
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