package com.code.aon.jaas.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.xml.ConfigurationRenderer;

/**
 * Configuration storage manager. This class saves context <b>xml</b> file.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19-oct-2006
 * @since 1.0
 */
public class ConfigurationStorage implements INode, IStorage {

	private static final long serialVersionUID = -6127147371658566439L;

	/** ConfigurationStorage Logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationStorage.class);

    /** Path label. */
	private static final String PATH = "path";

	/** Reloadable label. */
	private static final String RELOADABLE = "reloadable";
    
	/** Privileged label. */
	private static final String PRIVILEGED = "privileged";

    /** Tells storage manager of serializing context resource
     * <b>Catalina/localhost/</b><code>application.getId()</code><b>.xml</b> 
     */
	private transient StorageManager storageManager;

	IApplication application;
	String contextExtraInfo;

	/**
	 * Assign application.
	 * 
	 * @param application
	 */
	public void setApplication(IApplication application) {
		this.application = application;
	}

	/**
	 * Return Context configuration file properties.
	 * 
	 * @return
	 */
	public Properties getAttributes() {
		Properties props = new Properties();
		props.put( RELOADABLE, "true" );
		props.put( PATH, this.application.getContext() );
		props.put( PRIVILEGED, "true" );
		return props;
	}

	/**
	 * @param contextExtraInfo the contextExtraInfo to set
	 */
	public void setContextExtraInfo(String contextExtraInfo) {
		this.contextExtraInfo = contextExtraInfo;
	}

	/**
	 * @return the contextExtraInfo
	 */
	public String getContextExtraInfo() {
		return contextExtraInfo;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
		visitor.visitConfiguration(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	public String getId() {
		return ConfigurationStorage.class.getName();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IStorage#getStorageDir()
	 */
	@Override
	public File getStorageDir() {
		return 
			new File ( System.getProperty("catalina.home") + File.separator + "/conf/Catalina/localhost/" );
	}

	@Override
	public boolean erase() throws StorageException {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void initialize(StorageManager storageManager) {
		try {
			File file = 
				new File( getStorageDir().getCanonicalPath() + this.application.getContext() + "." + XML );
			file.createNewFile();
			this.storageManager = StorageManager.getInstance( file.toURL(), ConfigurationRenderer.getInstance() );
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean isDirty() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public URL read() throws StorageException {
		try {
			return this.storageManager.read();
	    } catch (IOException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
	    } catch (InterruptedException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
	    }
	}

	@Override
	public void write() throws StorageException {
		this.storageManager.write(this);
	}

}
