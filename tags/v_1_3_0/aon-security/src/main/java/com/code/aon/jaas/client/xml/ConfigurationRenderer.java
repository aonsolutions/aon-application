/*
 * Created on 13-sep-2006
 *
 */
package com.code.aon.jaas.client.xml;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;

import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.ConfigurationStorage;
import com.code.aon.jaas.storage.DomainStorage;

/**
 * Escribe el fichero de contexto de la aplicación desplegada. De esta manera, en Tomcat, 
 * la aplicación podrá utilizar las interfaces de seguridad.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 20-jul-2006
 * @since 1.0
 *  
 */
public class ConfigurationRenderer extends Renderer {

    /** Context label. */
	private static final String CONTEXT = "Context";

    /** Singleton instance of this class. */
	private static ConfigurationRenderer INSTANCE = null;

	/** Constructor. */
	private ConfigurationRenderer() {}

	/**
	 * Singleton constructor, in order to avoid multi-threading. 
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) { 
			INSTANCE = new ConfigurationRenderer();
		}
	}

	/**
	 * Return the instance of this class. 
	 */
	public static ConfigurationRenderer getInstance() {
		if (INSTANCE == null) createInstance();
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitAccessPolicy(com.code.aon.jaas.client.ast.IAccessPolicy)
	 */
	public void visitAccessPolicy(IAccessPolicy accessPolicy) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitApplication(com.code.aon.jaas.client.ast.IApplication)
	 */
	public void visitApplication(IApplication application) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitConfiguration()
	 */
	public void visitConfiguration(ConfigurationStorage storage) {
		println( startElement( CONTEXT, storage.getAttributes() ), deep );
		println( storage.getContextExtraInfo(), deep );
		println( endElement(CONTEXT), deep );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDataSourceMetaData(com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 */
	public void visitDataSourceMetaData(IDataSourceMetaData metadata) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDeployed(com.code.aon.jaas.storage.ApplicationsStorage)
	 */
	public void visitDeployed(ApplicationsStorage storage) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDeployed(com.code.aon.jaas.storage.DomainStorage)
	 */
	public void visitDeployed(DomainStorage storage) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDomain(com.code.aon.jaas.client.ast.IDomain)
	 */
	public void visitDomain(IDomain domain) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDomainApplication(com.code.aon.jaas.client.ast.IDomainApplication)
	 */
	public void visitDomainApplication(IDomainApplication application) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitRelation(com.code.aon.jaas.client.ast.IRelation)
	 */
	public void visitRelation(IRelation relation) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitUser(com.code.aon.jaas.client.ast.IUser)
	 */
	public void visitUser(IUser user) {
		throw new UnsupportedOperationException(); 
	}

}