package com.code.aon.jaas.storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.deployment.util.FileUtils;

/**
 * Domain storage manager. This class saves each domain <b>xml</b> file.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2006
 * @since 1.0
 *  
 */
public class DomainStorage implements INode, IStorage {

	private static final long serialVersionUID = 3475179974522618164L;

	/** Tells storage manager of serializing each domain resource <b>xml</b> file. */
	private transient StorageManager storageManager;

	/** Domain to serialize. */
	private IDomain domain;

	/**
	 * @param domain The domain to set.
	 */
	public void setDomain(IDomain domain) {
		this.domain = domain;
	}

	/**
	 * @return Returns the domain.
	 */
	public IDomain getDomain() {
		return domain;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
	    visitor.visitDeployed(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	public String getId() {
		return DomainStorage.class.getName();
	}

	@Override
	public File getStorageDir() {
		return new File( this.storageManager.getUrl().getFile() ).getParentFile();
	}

	@Override
	public boolean erase() throws StorageException {
		try {
			return this.storageManager.erase();
		} catch (IOException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
		}
	}

	@Override
	public void initialize(StorageManager storageManager) {
		this.storageManager = storageManager;
	}

	@Override
	public boolean isDirty() {
		try {
			return !FileUtils.getUptodateFile( getStorageDir().getCanonicalPath() ).exists();
		} catch (IOException e) {
			return false;
		}
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
