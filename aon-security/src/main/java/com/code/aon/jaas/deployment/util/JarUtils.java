package com.code.aon.jaas.deployment.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public class JarUtils {

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param jarURL
     * @param dest
     * @return URL
     * @throws IOException
     */
    public static URL extractNestedJar(URL jarURL, File dest)
            throws IOException {
        // This may not be a jar URL so validate the protocol
        if (!jarURL.getProtocol().equals("jar")) { //$NON-NLS-1$
            return jarURL;
        }
        String destPath = dest.getPath();
        URLConnection urlConn = jarURL.openConnection();
        JarURLConnection jarConn = (JarURLConnection) urlConn;
        // Extract the archive to dest/jarName-contents/archive
        String parentArchiveName = jarConn.getJarFile().getName();
        // Find the longest common prefix between destPath and parentArchiveName
        int length = Math.min(destPath.length(), parentArchiveName.length());
        int n = 0;
        while (n < length) {
            char a = destPath.charAt(n);
            char b = parentArchiveName.charAt(n);
            if (a != b) {
                break;
            }
            n++;
        }
        // Remove any common prefix from parentArchiveName
        parentArchiveName = parentArchiveName.substring(n);

        File archiveDir = new File(dest, parentArchiveName + "-contents"); //$NON-NLS-1$
        if (!archiveDir.exists() && !archiveDir.mkdirs() ) {
            throw new IOException(
                    "Failed to create contents directory for archive, path=" //$NON-NLS-1$
                            + archiveDir.getAbsolutePath());
        }
        String archiveName = jarConn.getEntryName();
        File archiveFile = new File(archiveDir, archiveName);
        File archiveParentDir = archiveFile.getParentFile();
        if (!archiveParentDir.exists() && !archiveParentDir.mkdirs()) {
            throw new IOException(
                    "Failed to create parent directory for archive, path=" //$NON-NLS-1$
                            + archiveParentDir.getAbsolutePath());
        }
        InputStream archiveIS = jarConn.getInputStream();
        FileOutputStream fos = new FileOutputStream(archiveFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] buffer = new byte[4096]; // $codepro.audit.disable numericLiterals
        int read;
        int totalRead = 0; // $codepro.audit.disable variableUsage
        while ((read = archiveIS.read(buffer)) > 0) {
            bos.write(buffer, 0, read);
            totalRead += read;
        }
        archiveIS.close();
        bos.close();

        // Return the file url to the extracted jar
        return archiveFile.toURL();
    }
}