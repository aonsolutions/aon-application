/*
 * Created on 13-sep-2006
 *
 */
package com.code.aon.jaas.client.xml;

import java.util.Iterator;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;

import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.ConfigurationStorage;
import com.code.aon.jaas.storage.DomainStorage;

/**
 * Escribe el fichero "deployed.xml" con las aplicaciones registradas.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 20-jul-2006
 * @since 1.0
 *  
 */
public class ApplicationsRenderer extends Renderer {

    /** Etiqueta de aplicaciones. */
	private static final String APPLICATIONS = "applications";

    /** Etiqueta de opciones. */
	private static final String OPTIONS = "options";

    /** Etiqueta de las opciones. */
	private static final String OPTION = "option";

    /** Singleton instance of this class. */
	private static ApplicationsRenderer INSTANCE = null;

	/** Constructor privado. */
	private ApplicationsRenderer() {}

	/**
	 * Creador sincronizado para protegerse de posibles problemas  multi-hilo otra prueba para evitar 
	 * instantiación múltiple
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) { 
			INSTANCE = new ApplicationsRenderer();
		}
	}

	public static ApplicationsRenderer getInstance() {
		if (INSTANCE == null) createInstance();
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitConfiguration(com.code.aon.jaas.storage.ConfigurationStorage)
	 */
	public void visitConfiguration(ConfigurationStorage storage) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDeployed(com.code.aon.jaas.storage.ApplicationsStorage)
	 */
	public void visitDeployed(ApplicationsStorage storage) {
        println(DECLARATION, deep);
        println(startElement(APPLICATIONS), deep++);
        println(startElement(OPTIONS), deep++);
        Iterator<IOption> optIter = storage.options().values().iterator();
        while (optIter.hasNext()) {
        	IOption option = optIter.next();
    		println( startElement( OPTION, option.toProperties() ) + endElement(OPTION), deep );
        }
        println(endElement(OPTIONS), --deep);
        Iterator<IApplication> appIter = storage.applications().values().iterator();
        while (appIter.hasNext()) {
            appIter.next().accept(this);
        }
        print(endElement(APPLICATIONS), --deep);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDeployedEntity(com.code.aon.jaas.storage.DomainStorage)
	 */
	public void visitDeployed(DomainStorage storage) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
     * 
     * @see com.code.aon.jaas.client.ast.INodeVisitor#visitApplication(com.code.aon.jaas.client.ast.IApplication)
     */
    public void visitApplication(IApplication application) {
        println(startElement(APPLICATION), deep);
        println(startElement(ID) + application.getId() + endElement(ID), ++deep);
        println(startElement(DESCRIPTION) + application.getDescription() + endElement(DESCRIPTION), deep);
        Iterator<IDomain> iter = application.domains().iterator();
        while (iter.hasNext()) {
            iter.next().accept(this);
        }
        println(endElement(APPLICATION), --deep);
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitAccessPolicy(com.code.aon.jaas.client.ast.IAccessPolicy)
	 */
	public void visitAccessPolicy(IAccessPolicy accessPolicy) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDataSourceMetaData(com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 */
	public void visitDataSourceMetaData(IDataSourceMetaData metadata) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDomain(com.code.aon.jaas.client.ast.IDomain)
	 */
	public void visitDomain(IDomain domain) {
        println(startElement(DOMAIN), deep++);
		println( startElement(ID) + domain.getId() + endElement(ID), deep );
		println( endElement(DOMAIN), --deep );
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