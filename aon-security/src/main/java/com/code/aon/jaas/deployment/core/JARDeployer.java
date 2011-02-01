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
 * Esta clase realiza el despliege de las politicas de seguridad de un fichero JAR.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 * @see ISubDeployer
 *  
 */
public class JARDeployer extends SubDeployer {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    public static final String META_INF = "META-INF/";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    public static final String EJB_JAR = "ejb-jar.xml";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private URL ejb;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private URL vendorEJB;

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ISubDeployer#init(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public void init(DeploymentInfo di) throws DeploymentException {
        ejb = di.localCl.findResource(META_INF + EJB_JAR);
        vendorEJB = di.localCl.findResource(META_INF
                + VendorFactoryManager.create(di.appServerName)
                        .getVendorEJBFile());
        deployed = di.localCl.findResource(META_INF + DEPLOYED_FILE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ISubDeployer#start(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public void start(DeploymentInfo di) throws DeploymentException {
        //		Parses the ejb-jar.xml and vendor specific xml files:
        //					jboss.xml, weblogic.xml, ...
        try {
            ISecurityDescriptor isd = (ISecurityDescriptor) new AstLoader()
                    .parse(ejb.openStream());
            IVendorDescriptor jaas = VendorFactoryManager.parse(
                    di.appServerName, vendorEJB.openStream());
            isd.accept(visitor);
            jaas.accept(visitor);
        } catch (Exception e) { // $codepro.audit.disable caughtExceptions
            throw new DeploymentException(e.getMessage());
        }
        super.start(di);
    }

}