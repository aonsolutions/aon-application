package com.code.aon.jaas.deployment;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

/**
 * This class manages deployment process properties.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */
public class DeploymentInfo {

	/** Tells If application only exits in the configuration resource file. */
	public static final int WAR_DEPLOYMENT_FILE_NOT_FOUND = 0;

	/** Application deployment temporal File. */
	public static final String TMP = "TMP";
    /** Web Deployment descriptor files directory. */
	public static final String WEB_INF = "WEB-INF/";

    /** Tells deployment status is Starting. */
	public static final String STARTING = "Starting";
	/** Tells deployment status is Deployed. */
	public static final String DEPLOYED = "Deployed";
	/** Tells deployment status is Inconsistent. */
	public static final String INCONSISTENT = "Inconsistent";

    /** The initial construction timestamp. */
	public Date date = new Date();

    /** the URL identifing this SDI. */
	public URL url;

    /** An optional URL to a local copy of the deployment. */
	public URL localUrl;

    /** The suffix of the deployment url. */
	public String shortName;

	/** Tell if deployment url is a file or a directory */
	public boolean isFile;

    /** The last system time the deployment inited by the MainDeployer. */
	public long lastDeployed = 0;

    /** Use for "should we redeploy failed". */
	public long lastModified = 0;

    /** A free form status for the "state" can be Deployed/Failed/Starting etc. */
	public String status;

    /** The current state of the deployment. */
	public DeploymentState state = DeploymentState.CONSTRUCTED;

    /** The deployer that handles the deployment. */
	public ISubDeployer deployer;

    /** The application server name which this deployment is. */
	public String appServerName;

	/** The Application security domain. */
	public String securityDomain;

    /**
     * local Cl is a CL that is used for metadata
     * loading, if ejb-jar.xml is left in the parent CL through old deployments,
     * this makes ensures that we use the local version. You must use the
     * URLClassLoader.findResource method to restrict loading to the deployment
     * URL.
     */
	public URLClassLoader localCl;

    /**
     * Constructor.
     * 
     * @param url
     * @throws DeploymentException
     */
    public DeploymentInfo(final URL url) throws DeploymentException {
        //	The key url the deployment comes from
    	this.url = url;
//        try {
//        	this.url.openStream().close();
//        } catch (IOException e) {
//        	LOGGER.warn( e.getMessage() );
//            File file = new File( this.url.getPath() );
//            if ( file.isDirectory() ) {
//            	File srcDir = new File( url.getPath() + File.separator + WEB_INF );
//            	File destDir = 
//            		new File( System.getProperty( "java.io.tmpdir" ) + File.separator + 
//                				file.getName() + File.separator + WEB_INF );
//            	destDir.mkdirs();
//                try {
//                	FileFilter filter = new InfoFileFilter();
//                	File[] files = srcDir.listFiles( filter );
//                	for (int i = 0; i < files.length; i++) {
//                    	File destFile = new File( destDir, files[i].getName() );
//    					FileUtis.copyFile( files[i], destFile, true );
//					}
//					this.url = destDir.getParentFile().toURL();
//		        	this.url.openStream().close();
//				} catch (IOException e1) {
//					LOGGER.fatal( "Unable to copy: " + srcDir + " in: " + destDir + ". " + e1.getMessage() );
//				}
//            } else {
//            	LOGGER.fatal( "Unable to load: " + url + ". " + e.getMessage() );
//            }
//        }
        shortName = getShortName( this.url.getFile() );
        isFile = new File( this.url.getFile() ).isFile();
    }

    
    /**
     * Create a UnifiedClassLoader for the
     * deployment that loads from the localUrl and uses its parent deployments
     * url as its orignal url. Previously xml descriptors simply used the TCL
     * but since the UCLs are now registered as mbeans each must be unique.
     */
	public void createClassLoaders() {
//	create a local classloader for local files, don't go with the UCL for ejb-jar.xml
		if (localCl == null) {
			localCl = new URLClassLoader(new URL[] { localUrl });
		}
	}

	/**
	 * Clean localUrl attribute.
	 */
	public void cleanup() {
		localUrl = null;
	}

	/*(non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return url.hashCode();
	}

	/*(non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof DeploymentInfo) {
			return ((DeploymentInfo) other).url.equals(this.url);
		}
		return false;
	}

	/*(non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" { url=" + url + " }\n");
		s.append("  lastDeployed: " + lastDeployed + "\n");
		s.append("  lastModified: " + lastModified + "\n");
		s.append("  mbeans:\n");
		return s.toString();
	}

    /**
     * Return the "short name" for the deployment http://myserver/mybean.ear should yield 
     * "mybean.ear"
     * 
     * @param name
     * @return String
     */
	public static String getShortName(String name) {
		if (name.endsWith("/")) {
    		name = name.substring(0, name.length() - 1);
    	}
		name = name.substring(name.lastIndexOf("/") + 1);
		return name;
	}

//	/**
//	 * This class filters <b>web</b> application servers deployment descriptors.
//	 * 
//	 * @author Consulting & Development. Iñaki Ayerbe - 25/10/2007
//	 */
//	class InfoFileFilter implements FileFilter {
//
//		public boolean accept(File pathname) {
//			return pathname.getName().indexOf( "web" ) > -1;
//		}
//		
//	}
}