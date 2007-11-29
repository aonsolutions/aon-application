/*
 * Created on 13-sep-2006
 *
 */
package com.code.aon.jaas.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.jaas.client.ast.INode;

import com.code.aon.jaas.client.xml.Renderer;

/**
 * Clase responsable de serializar los ficheros XML con la información de las aplicaciones y entidades
 * desplegadas en el módulo de seguridad.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2005
 * @since 1.0
 *
 */

public class StorageManager {

	/** Map of <code>StorageManager</code> by <code>URL</code> */
	private static final Map<URL, StorageManager> MANAGERS = new HashMap<URL, StorageManager>();

    /** Indica el traductor del fichero de seguridad. */
    Renderer renderer;

    /** Indica la <code>URL</code> donde leer y escribir. */
    URL url;

    /** Indica el estado del monitor. */
    boolean writing = false;

//    /**
//     * Construye un objeto de la clase.
//     * 
//     * @param url
//     * @param renderer
//     */
//    public StorageManager(URL url, Renderer renderer) {
//        this.renderer = renderer;
//        this.url = url;
//    }

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
     * Elimina fisicamente el fichero de seguridad.
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
     * Lee el fichero de seguridad.
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
     * Escribe en el fichero cada una de las políticas de seguridad de cada aplicación 
     * junto con los usuarios definidos.
     * 
     * @param policy
     */
    public void write(INode node) {
        setWriting(true);
        RendererThread thread = new RendererThread(node);
        thread.start();
    }

    /**
     * Establece el estado del monitor.
     * 
     * @param b
     */
    public void setWriting(boolean b) {
        writing = b;
    }

    /**
     * Devuelve el estado del monitor.
     * 
     * @return boolean
     */
    public boolean isWriting() {
        return writing;
    }

    /**
     *  Pinta el fichero de seguridad basandose en el traductor adecuado.
     * 
     * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
     * @since 1.0
     *  
     */
    protected class RendererThread extends Thread {

        /**
         * // TODO [iayerbe] Documéntame!
         */
        INode node;

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param policy
         */
        public RendererThread(INode node) {
            this.node = node;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            synchronized (renderer) {
                try {
                    renderer.render(node, new FileOutputStream(url.getPath()));
                    setWriting(false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                renderer.notify();
            }
        }
    }

}
