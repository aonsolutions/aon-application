package com.code.aon.jaas.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.jaas.client.ast.INode;

import com.code.aon.jaas.client.xml.Renderer;
import com.code.aon.jaas.deployment.util.FileUtils;

/**
 * Serializes applications and domains xml files deployed inside.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2005
 * @since 1.0
 *
 */
public class StorageManager {

	/** Map of <code>StorageManager</code> by <code>URL</code> */
	private static final Map<URL, StorageManager> MANAGERS = new HashMap<URL, StorageManager>();

    /** XML file renderer. */
    Renderer renderer;

    /** Where the resoure must read or write to. */
    URL url;

    /** Tells if the current resource is writing. */
    boolean writing = false;

    /**
     * Returns a <code>StorageManager</code> instance.
     */
	public static final StorageManager getInstance(URL url, Renderer renderer) {
		if ( MANAGERS.containsKey( url ) ) {
			return MANAGERS.get( url );
		}
		StorageManager sm = new StorageManager();
		sm.url = url;
		sm.renderer = renderer;
		MANAGERS.put( url, sm );
		return sm;
	}
   
	/**
	 * @return Returns the url.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
     * Erases current resource.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean erase() throws IOException, InterruptedException {
        synchronized (renderer) {
        	return new File( this.url.getPath() ).delete();
        }
    }

    /**
     * Read current resource.
     * 
     * @return URL
     * @throws IOException
     * @throws InterruptedException
     */
    public URL read() throws IOException, InterruptedException {
        synchronized (renderer) {
            if (writing) {
                renderer.wait();
            }
            return url;
        }
    }

    /**
     * Writes current resource file.
     * 
     * @param policy
     */
    public void write(INode node) {
        setWriting(true);
        RendererThread thread = new RendererThread(node);
        thread.start();
    }

    /**
     * Sets the currrent resource starts writing.
     * 
     * @param b
     */
    public void setWriting(boolean b) {
        writing = b;
    }

    /**
     * Returns the currrent resource is writing.
     * 
     * @return boolean
     */
    public boolean isWriting() {
        return writing;
    }

    /**
     * Renders application or domains resource file.
     * 
     * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
     * @since 1.0
     *  
     */
	protected class RendererThread extends Thread {

		/** Application or Domain instance this class will render. */
		INode node;

        /**
         * Constructor
         * 
         * @param policy
         */
		public RendererThread(INode node) {
			this.node = node;
		}

		@Override
		public void run() {
			synchronized (renderer) {
				try {
					renderer.render(node, new FileOutputStream(url.getPath()));
					setWriting(false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					deleteAllUptodateFiles();
					createUptodateFile();
				} catch (IOException e) {
					// Ignore
				}
				renderer.notify();
			}
		}
	}

	/**
	 * Creates a 0 size file that tells other application servers using the same 
	 * COMMON-RESOURCE directory, the application has changed. 
	 * @throws IOException 
	 */
	protected void createUptodateFile() throws IOException {
		String uptodateFile = new File( this.url.getFile() ).getParentFile().getCanonicalPath();
		FileUtils.getUptodateFile( uptodateFile ).createNewFile();
	}

	/**
	 * Deletes all files using <b>updated<b> extension.
	 */
	private void deleteAllUptodateFiles() {
		File[] dirtyFiles = 
			new File ( this.url.getFile() ).getParentFile().listFiles( new UptodateFileFilter() );
		for (int i = 0; i < dirtyFiles.length; i++) {
			dirtyFiles[i].delete();
		}
	}

	/**
	 * Defines the up to date file filter.
	 * 
	 * @author Consulting & Development. Iñaki Ayerbe - 14-jan-2008
	 * @since 1.0
	 */
	class UptodateFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().indexOf( FileUtils.UP_TO_DATE ) > -1 ;
		}

	}
}
