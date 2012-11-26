package com.code.aon.jaas.storage;

import java.io.File;
import java.net.URL;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2006
 * @since 1.0
 */
public interface IStorage {

	/** Indicates <b>xml<b> storage files extension. */
	static final String XML = "xml";

	/**
	 * Initializes storage manager.
	 *  
	 * @param storageManager
	 */
	void initialize(StorageManager storageManager);

	/**
	 * Tells if the application deployed file or domain files have been updated outside.
	 * 
	 * @return
	 */
	boolean isDirty();

	/**
	 * Gets the directory where application resources are stored.
	 * 
	 * @throws StorageException
	 */
	File getStorageDir();

	/**
	 * Removes physical storage.
	 * 
	 * @return
	 * @throws StorageException
	 */
	boolean erase() throws StorageException;

	/**
	 * Returns the application server URL content.
	 * 
	 * @throws StorageException
	 */
	URL read() throws StorageException;

	/**
	 * Serializes content.
	 * 
	 * @throws StorageException
	 */
	void write() throws StorageException;

}
