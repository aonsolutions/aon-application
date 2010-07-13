package com.code.aon.jaas.client.ast;

import java.io.IOException;
import java.io.InputStream;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.storage.IStorage;

/**
 * Interfaz de carga de la estructura de árbol sintáctica de los ficheros XML de aplicaciones y 
 * entidades desplegadas en el módulo de seguridad. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06-feb-2004
 * @since 1.0
 *  
 */
public interface IAstLoader {

	/**
	 * Parseador del fichero con la estructura dependiendo del tipo.
	 * 
	 * @param type
	 * @param in
	 * @return
	 * @throws AstException
	 * @throws IOException
	 */
    IStorage parse(int type, InputStream in) throws AstException, IOException;
}