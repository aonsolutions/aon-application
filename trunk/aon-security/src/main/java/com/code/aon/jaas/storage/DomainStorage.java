package com.code.aon.jaas.storage;

import java.io.IOException;
import java.net.URL;

import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.INodeVisitor;

public class DomainStorage implements INode, IStorage {

	private static final long serialVersionUID = 5736121033648728101L;

	/** Indica el encargado de serializar la seguridad para la entidad. */
	private transient StorageManager storageManager;

	/** Indica el Dominio. */
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

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IStorage#initialize(com.code.aon.jaas.storage.StorageManager)
	 */
	public void initialize(StorageManager storageManager) {
		this.storageManager = storageManager;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IStorage#erase()
	 */
	public boolean erase() throws StorageException {
		try {
			return this.storageManager.erase();
		} catch (IOException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
		} catch (InterruptedException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IStorage#read()
	 */
	public URL read() throws StorageException {
		try {
			return this.storageManager.read();
	    } catch (IOException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
	    } catch (InterruptedException e) {
	        throw new StorageException( e.getMessage(), e.getCause() );
	    }
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IStorage#write()
	 */
	public void write() throws StorageException {
		this.storageManager.write(this);
	}
}
