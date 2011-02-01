package com.code.aon.jaas.vendor.deployment.ast;

import java.io.IOException;
import java.io.InputStream;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.ast.INode;

/**
 * Vendor file loader.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public interface IAstLoader {

	/**
	 * Parse a vendor file.
	 * 
	 * @param in
	 * @return INode
	 * @throws AstException
	 * @throws IOException
	 */
	INode parse(InputStream in) throws AstException, IOException;

}