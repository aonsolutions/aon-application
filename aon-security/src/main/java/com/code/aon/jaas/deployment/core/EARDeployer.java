package com.code.aon.jaas.deployment.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.deployment.DeployerFactoryManager;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.DeploymentState;
import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.ast.IApplicationDescriptor;
import com.code.aon.jaas.deployment.ast.core.AstLoader;
import com.code.aon.jaas.deployment.util.JarUtils;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 14-may-2004
 * @since 1.0
 *  
 */
public class EARDeployer extends SubDeployer {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    public static final String META_INF = "META-INF/";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    public static final String EAR_JAR = "application.xml";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    public static final String JAR_SUFFIX = ".jar";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    public static final String WAR_SUFFIX = ".war";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private static Log LOGGER = LogFactory.getLog( EARDeployer.class.getName() );

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private URL ear;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    Map deploymentMap = new LinkedHashMap();

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ISubDeployer#init(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public void init(DeploymentInfo di) throws DeploymentException {
        //		visitor = new EARDeployerVisitor(di);
        //		visitor.addSubDeployerListener(di.application);
        ear = di.localCl.findResource(META_INF + EAR_JAR);
        try {
            AstLoader loader = new AstLoader();
            IApplicationDescriptor iad = (IApplicationDescriptor) loader
                    .parse(ear.openStream());
            iad.accept(visitor);
        } catch (AstException e) {
            throw new DeploymentException(e.getMessage());
        } catch (IOException e) {
            throw new DeploymentException(e.getMessage());
        }
        super.init(di);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ISubDeployer#start(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public void start(DeploymentInfo di) throws DeploymentException {
        Iterator iter = deploymentMap.values().iterator();
        while (iter.hasNext()) {
            DeploymentInfo element = (DeploymentInfo) iter.next();
            element.deployer.start(element);
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @author Consulting & Development. Iñaki Ayerbe - 22-feb-2005
     * @since 1.0
     *  
     */
    private class EARDeployerVisitor extends SubDeployerVisitor {

        /**
         * // TODO [iayerbe] Documéntame!
         */
        DeploymentInfo di;

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param di
         */
        public EARDeployerVisitor(DeploymentInfo di) {
            this.di = di;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitApplicationDescriptor(com.code.aon.jaas.deployment.ast.IApplicationDescriptor)
         */
        public void visitApplicationDescriptor(IApplicationDescriptor descriptor) {
            Iterator iter = descriptor.jars().iterator();
            while (iter.hasNext()) {
                String element = (String) iter.next();
                URL dd = di.localCl.findResource(element);
                try {
                    /*
                     * Extract each entry so that deployment modules can be
                     * processed and any manifest entries referenced by the ear
                     * modules are located in the same unpacked directory
                     * structure.
                     */
                    String urlPrefix = "jar:" + di.localUrl + "!/"; //$NON-NLS-1$ //$NON-NLS-2$ 
                    JarFile jarFile = ((JarURLConnection) dd.openConnection())
                            .getJarFile();
                    // For each entry, test if deployable, if so
                    // extract it and store the related URL in map
                    for (Enumeration e = jarFile.entries(); e.hasMoreElements();) {
                        JarEntry entry = (JarEntry) e.nextElement();
                        String name = entry.getName();
                        try {
                            URL url = new URL(urlPrefix + name);
                            if (name.endsWith(JAR_SUFFIX)
                                    || name.endsWith(WAR_SUFFIX)) {
                                // Obtain a jar url for the nested jar
                                // TODO [iayerbe] Quitar el path absoluto.
                                URL nestedURL = JarUtils.extractNestedJar(url,
                                        new File("C:/tmp/jw")); 
                                // and store in it in map
                                if (nestedURL == null) {
                                    throw new DeploymentException(
                                            "Failed to find module file: " + name); //$NON-NLS-1$
                                }
                                DeploymentInfo deployment = new DeploymentInfo(
                                        nestedURL);
                                //	Set the application that must hold roles and
                                // permissions deployment.application =
                                // di.application;
                                //	Create a local copy of that File, the sdi
                                // keeps track of the copy directory
                                if (deployment.localUrl == null) {
                                    deployment.localUrl = deployment.url;
                                    URL[] localCP = { deployment.localUrl };
                                    deployment.localCl = new URLClassLoader(
                                            localCP);
                                } // end of if ()

                                //	What deployer is able to deploy this file
                                deployment.deployer = DeployerFactoryManager
                                        .createDeployer(deployment);
                                deployment.state = DeploymentState.INIT_DEPLOYER;
                                //	we have the deployer, continue deployment.
                                deployment.deployer.init(deployment);
                                //	initialize the unified classloaders for this
                                // deployment
                                deployment.createClassLoaders();
                                deployment.state = DeploymentState.INITIALIZED;

                                deploymentMap.put(nestedURL, deployment);
                            }
                        } catch (Exception mue) {
                            // TODO [iayerbe] Mirar que se debe hacer con esta
                            // excepción.
                            LOGGER.fatal(mue.getMessage());
                        }
                    }
                } catch (Exception e1) {
                    // TODO [iayerbe] Mirar que se debe hacer con esta
                    // excepción.
                    LOGGER.fatal(e1.getMessage());
                }
            }
        }
    }

}