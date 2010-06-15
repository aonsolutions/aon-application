/**
 * 
 */
package es.code.ecm.repository.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.repository.IProvider;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20/06/2007
 *
 */
public class Path {

	/**
	 * Gets resource bound to the name, if this resource is not found then use the default one.
	 * 
	 * @param resourceName
	 * @param defaultResourceName
	 * @param resourceErrorLabel
	 * @return
	 */
	public static URL getResource(String resourceName, String defaultResourceName, String resourceErrorLabel) {
		if ( StringUtils.isNotEmpty( resourceName ) ) {
			Logger LOGGER = LoggerFactory.getLogger( Path.class.getName() );
			// 1: try to load the configured file from the classpath
			URL url = IProvider.class.getResource( resourceName );
			if ( url != null ) {
				LOGGER.info( "Custom {} definition registered using {}", resourceErrorLabel, resourceName );
				return url;
			}
			// 2: try to load it from the file system
			File configFilePath = new File( Path.getAbsoluteFileSystemPath( resourceName ) );
			if ( configFilePath.exists() ) {
				try {
					return configFilePath.toURI().toURL();
				} catch (MalformedURLException e) {
					// should never happen
					LOGGER.error( "Malformed URL has occurred for: {} file", configFilePath );
				}
			}
			// 3: defaults to standard repository definition
			LOGGER.warn( "Unable to find {} definition: {}", resourceErrorLabel, resourceName );
		}
		return IProvider.class.getResource( defaultResourceName );
	}

	/**
	 * Gets absolute filesystem path, adds root where application is deployed 
	 * if path is not absolute.
	 * 
	 * @param path
	 * @return
	 */
	public static String getAbsoluteFileSystemPath(String path) {
		if (Path.isAbsolute(path)) {
			return path;
		}
//		String root = null;
//		try {
//			IConsoleAdmin console = Utils.getSecurityConsole();
//			root = console.getDeployerHome();
//		} catch (DeploymentException e) {
//		} catch (NullPointerException e) {
//			root = new File( Path.class.getResource("/").getPath() ).getParent();
//		}
//		// using the file() constructor will allow relative paths in the form ../../apps
//		return new File( root, path ).getAbsolutePath();
		return new File( System.getProperty( "catalina.home" ) + "/", path ).getAbsolutePath();
	}

    public static String getAbsolutePath(String path, String label) {
        if (StringUtils.isEmpty(path) || (path.equals("/"))) { 
            return "/" + label; 
        }

        return path + "/" + label; 
    }

    public static String getAbsolutePath(String path) {
        if (!path.startsWith("/")) { 
            return "/" + path; 
        }
        return path;
    }

	public static String getAbsolutePath(URL url) throws IOException {
		String path = url.getFile();
		int index = url.getFile().indexOf( "!/" );
		if ( index > -1 ) {
			String resource = path.substring( index + 1, path.length() );
			path = path.substring( 0, index );
			int start = path.indexOf( "file:/" ) + 6;
			int end = path.lastIndexOf( "lib/" );
//	Creates file inside application deployed directory.
			path = path.substring( start, end ) + "classes" + resource;
			String directory = path.substring( 0, path.lastIndexOf( "/" ) );
			new File( directory ).mkdirs();
			File file = new File( path );
			file.createNewFile();
//	Now we create some variables we will use for writting the file to the response
			int read = 0;
			byte[] bytes = new byte[1024];
//	Streams we will use to read, write the file bytes to our response
			InputStream is = url.openStream();
			FileOutputStream fos = new FileOutputStream( file );
			while((read = is.read(bytes)) != -1){
				fos.write(bytes,0,read);
			}
		}
		return path;
	}

    public static String getNodePath(String path, String label) {
        if (StringUtils.isEmpty(path) || (path.equals("/"))) { 
            return label;
        }
        return getNodePath(path + "/" + label); 
    }

    public static String getNodePath(String path) {
        if (path.startsWith("/")) { 
            return path.replaceFirst("/", StringUtils.EMPTY); 
        }
        return path;
    }

    protected static boolean isAbsolute(String path) {
		if (path == null) {
			return false;
		}
		if (path.startsWith("/") || path.startsWith(File.separator)) { 
			return true;
		}
		// windows c:
		if (path.length() >= 3 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') {
			return true;
		}
		return false;
	}

}
