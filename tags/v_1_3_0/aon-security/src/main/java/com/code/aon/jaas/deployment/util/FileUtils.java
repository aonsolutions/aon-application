package com.code.aon.jaas.deployment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.deployment.DeploymentInfo;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 24/10/2007
 * @since 1.2.5
 */
public class FileUtils {

    /** FileUtils Logger. */
    private static final Log LOGGER = LogFactory.getLog( FileUtils.class.getName() );
    /** Tells the <b>updated<b> extension. */
    public static final String UP_TO_DATE = "updated";
    /** The default buffer size to use. */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Copies a file to a new location.
     * <p>
     * This method copies the contents of the specified source file
     * to the specified destination file.
     * The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     *
     * @param srcFile  an existing file to copy, must not be null
     * @param destFile  the new file, must not be null
     * @param preserveFileDate  true if the file date of the copy
     *  should be the same as the original
     *
     * @throws NullPointerException if source or destination is null
     * @throws IOException if source or destination is invalid
     * @throws IOException if an IO error occurs during copying
     * @see #copyFileToDirectory
     */
    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) 
    			throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcFile.exists() == false) {
            throw new FileNotFoundException	("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {
            if (destFile.getParentFile().mkdirs() == false) {
                throw new IOException("Destination '" + destFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }

    /**
     * Internal copy file method.
     * 
     * @param srcFile  the validated source file, not null
     * @param destFile  the validated destination file, not null
     * @param preserveFileDate  whether to preserve the file date
     * @throws IOException if an error occurs
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream input = new FileInputStream(srcFile);
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            try {
                copy(input, output);
            } finally {
                closeQuietly(output);
            }
        } finally {
            closeQuietly(input);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    /**
     * Unconditionally close an <code>InputStream</code>.
     * <p>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input  the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close an <code>OutputStream</code>.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output  the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    // copy from InputStream
    //-----------------------------------------------------------------------
    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static int copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Recursively delete a directory.
     *
     * @param directory  directory to delete
     * @throws IOException in case deletion is unsuccessful
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException in case cleaning is unsuccessful
     */
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Delete a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file  file or directory to delete, must not be <code>null</code>
     * @throws NullPointerException if the directory is <code>null</code>
     * @throws IOException in case deletion is unsuccessful
     */
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            if (!file.exists()) {
                throw new FileNotFoundException("File does not exist: " + file);
            }
            if (!file.delete()) {
                String message =
                    "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * Schedule a file to be deleted when JVM exits.
     * If file is directory delete it and all sub-directories.
     *
     * @param file  file or directory to delete, must not be <code>null</code>
     * @throws NullPointerException if the file is <code>null</code>
     * @throws IOException in case deletion is unsuccessful
     */
    public static void forceDeleteOnExit(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        } else {
            file.deleteOnExit();
        }
    }

    /**
     * Recursively schedule directory for deletion on JVM exit.
     *
     * @param directory  directory to delete, must not be <code>null</code>
     * @throws NullPointerException if the directory is <code>null</code>
     * @throws IOException in case deletion is unsuccessful
     */
    private static void deleteDirectoryOnExit(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectoryOnExit(directory);
        directory.deleteOnExit();
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory  directory to clean, must not be <code>null</code>
     * @throws NullPointerException if the directory is <code>null</code>
     * @throws IOException in case cleaning is unsuccessful
     */
    private static void cleanDirectoryOnExit(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDeleteOnExit(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
	 * Gets current IP up to date file.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public static File getUptodateFile(String parentFilePath) throws IOException {
		String updatedFileName = "." + UP_TO_DATE;
		try {
			updatedFileName = InetAddress.getLocalHost().getHostAddress() + updatedFileName;
		} catch (UnknownHostException e) {
		}
		return new File( parentFilePath + File.separator + updatedFileName );
	}

	/**
	 * Allocates deployment configuration files to /COMMON-RESOURCES/TMP/ folder.
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static final URL allocateDeployment(URL url, String configPath) throws IOException {
		int index = url.getPath().lastIndexOf( "/" );
		String appName = url.getPath().substring( index, url.getPath().length() );
		File destDir = 
			new File( configPath + File.separator + DeploymentInfo.TMP + appName );
		destDir.mkdirs();
		FileUtils.forceDeleteOnExit( destDir );
		boolean isDirectory = new File( url.getPath() ).isDirectory();
		try {
			if ( isDirectory ) {
				File srcDir = new File( url.getPath() + File.separator + DeploymentInfo.WEB_INF );
				File filesDestDir = new File( destDir.getCanonicalPath() + File.separator + DeploymentInfo.WEB_INF );
				filesDestDir.mkdirs();
				File[] files = srcDir.listFiles();
            	for (int i = 0; i < files.length; i++) {
                	File destFile = new File( filesDestDir, files[i].getName() );
                	if ( files[i].isFile() ) { 
                		FileUtils.copyFile( files[i], destFile, true );
                	}
				}
			} else {
				JarUtils.extractWEBINFFiles( url, destDir );
			}
		} catch(FileNotFoundException e) {
			LOGGER.warn( "File " + url.getPath() + " not Found. " + e.getMessage() );
		}
		return destDir.toURL();
	}

	
}
