package com.code.aon.jaas.storage;

import java.net.URL;

public interface IStorage {

	/**
	 * Inicializa el responsable de serializar el contenedor.
	 *  
	 * @param storageManager
	 */
	public void initialize(StorageManager storageManager);

	/**
	 * Elimina el almacenamiento físico.
	 * @return
	 *  
	 * @throws StorageException
	 */
	public boolean erase() throws StorageException;

	/**
	 * Devuelve la URL con el contenido del contenedor.
	 * 
	 * @throws StorageException
	 */
	public URL read() throws StorageException;

	/**
	 * Serializa el contenido del contenedor.
	 * 
	 * @throws StorageException
	 */
	public void write() throws StorageException;
}
