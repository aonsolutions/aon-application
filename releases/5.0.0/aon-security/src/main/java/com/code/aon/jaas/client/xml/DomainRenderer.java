package com.code.aon.jaas.client.xml;

import java.util.Iterator;

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
 * Serializa a disco la entidad del módulo de seguridad.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public class DomainRenderer extends Renderer {

    /** Etiqueta de entity. */
	private static final String STORAGE = "storage";

    /** Etiqueta de datasource-metadata. */
	private static final String DATASOURCE_METADATA = "datasource-metadata";

	/** Etiqueta de connection-url. */
	private static final String CONNECTION_URL = "connection-url";

	/** Etiqueta de driver-class. */
	private static final String DRIVER_CLASS = "driver-class";

	/** Etiqueta de access-policy. */
	private static final String ACCESS_POLICY = "access-policy";

	/** Etiqueta de max-def-users. */
    private static final String MAX_DEFINED_USERS = "max-def-users";

	/** Etiqueta de max-all-users. */
    private static final String MAX_ALLOWED_USERS = "max-all-users";

	/** Etiqueta de max-sessions_4_user. */
    private static final String MAX_SESSIONS_4_USER = "max-ses-user";

    /** Etiqueta de ex-if-exceeded. */
    private static final String EX_IF_EXCEEDED = "ex-if-exceeded";

    /** Etiqueta de perfiles con roles asociados. */
    private static final String PROFILES = "profiles";

    /** Etiqueta de usuarios con perfiles asociados. */
    private static final String USERS = "users";

    /** Etiqueta de relaciones. */
    private static final String RELATION = "relation";

    /** Etiqueta de lista de Perifles o Roles. */
    private static final String LIST = "list";

	/** Etiqueta de usuarios disponibles. */
    private static final String STANDALONE = "standalone";

    /** Etiqueta de usuario. */
    private static final String USER = "user";

    /** Etiqueta de nombre de usuario. */
    private static final String NAME = "name";

    /** Etiqueta de clave de usuario. */
    private static final String PASSWORD = "password";

    /** Instancia de la clase que escribe el fichero de usuarios. */
	private static DomainRenderer INSTANCE = null;

	/** Constructor privado. */
	private DomainRenderer() {}

	/**
	 * Creador sincronizado para protegerse de posibles problemas  multi-hilo otra prueba para evitar 
	 * instantiación múltiple
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) { 
			INSTANCE = new DomainRenderer();
		}
	}

	public static DomainRenderer getInstance() {
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
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDeployedEntity(com.code.aon.jaas.storage.DomainStorage)
	 */
	public void visitDeployed(DomainStorage storage) {
        println(DECLARATION, deep);
        println( startElement(STORAGE), deep++ );
        storage.getDomain().accept(this);
        print( endElement(STORAGE), --deep );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitApplication(com.code.aon.jaas.client.ast.IApplication)
	 */
	public void visitApplication(IApplication application) {
		throw new UnsupportedOperationException(); 
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitAccessPolicy(com.code.aon.jaas.client.ast.IAccessPolicy)
	 */
	public void visitAccessPolicy(IAccessPolicy accessPolicy) {
		println( startElement(ACCESS_POLICY), deep++ );
		println( startElement(ID) + accessPolicy.getId() + endElement(ID), deep );
		println( startElement(MAX_DEFINED_USERS) + accessPolicy.getMaxDefinedUsers() + endElement(MAX_DEFINED_USERS), deep );
		println( startElement(MAX_ALLOWED_USERS) + accessPolicy.getMaxAllowedUsers() + endElement(MAX_ALLOWED_USERS), deep );
		println( startElement(MAX_SESSIONS_4_USER) + accessPolicy.getMaxSessions4User() + endElement(MAX_SESSIONS_4_USER), deep );
		println( startElement(EX_IF_EXCEEDED) + accessPolicy.isExceptionThrowableIfMaximumExceeded() + endElement(EX_IF_EXCEEDED), deep );
		println( endElement(ACCESS_POLICY), --deep );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDataSourceMetaData(com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 */
	public void visitDataSourceMetaData(IDataSourceMetaData metadata) {
		println( startElement(CONNECTION_URL) + null2Empty( metadata.getConnectionURL() ) + endElement(CONNECTION_URL), deep );
		println( startElement(DRIVER_CLASS) + null2Empty( metadata.getDriverClass() ) + endElement(DRIVER_CLASS), deep );
		println( startElement(USER+NAME) + null2Empty( metadata.getUsername() ) + endElement(USER+NAME), deep );
		println( startElement(PASSWORD) + null2Empty( metadata.getPassword() ) + endElement(PASSWORD), deep );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDomain(com.code.aon.jaas.client.ast.IDomain)
	 */
	public void visitDomain(IDomain domain) {
        println(startElement(DOMAIN), deep++);
		println( startElement(ID) + domain.getId() + endElement(ID), deep );
		domain.getAccessPolicy().accept(this);
		IDataSourceMetaData dsmd = domain.getDataSourceMetaData();
		if ( dsmd != null ) {
			println( startElement(DATASOURCE_METADATA), deep++ );
			dsmd.accept(this);
			println( endElement(DATASOURCE_METADATA), --deep );
		} else {
			println( startElement(DATASOURCE_METADATA), deep++ );
			println( endElement(DATASOURCE_METADATA), --deep );
		}
		Iterator<IDomainApplication> applications = domain.applications().iterator();
		while (applications.hasNext()) {
			applications.next().accept(this);
		}
		println(startElement(STANDALONE), deep++);
		Iterator<IUser> users = domain.standaloneUsers().values().iterator();
		while (users.hasNext()) {
			users.next().accept(this);
		}
		println(endElement(STANDALONE), --deep);
		println( endElement(DOMAIN), --deep );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitDomainApplication(com.code.aon.jaas.client.ast.IDomainApplication)
	 */
	public void visitDomainApplication(IDomainApplication application) {
		println( startElement(APPLICATION), deep++ );
		println(startElement(ID) + application.getId() + endElement(ID), deep);
		IDataSourceMetaData dsmd = application.getDataSourceMetaData();
		if ( dsmd != null ) {
			println( startElement(DATASOURCE_METADATA), deep++ );
			dsmd.accept(this);
			println( endElement(DATASOURCE_METADATA), --deep );
		} else {
			println( startElement(DATASOURCE_METADATA), deep++ );
			println( endElement(DATASOURCE_METADATA), --deep );
		}
		println(startElement(PROFILES), deep++);
		Iterator<IRelation> profiles = application.profiles().iterator();
		while (profiles.hasNext()) {
			profiles.next().accept(this);
		}
		println(endElement(PROFILES), --deep);
		println(startElement(USERS), deep++);
		Iterator<IRelation> users = application.users().iterator();
		while (users.hasNext()) {
			users.next().accept(this);
		}
		println(endElement(USERS), --deep);
		println( endElement(APPLICATION), --deep );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitRelation(com.code.aon.jaas.client.ast.IRelation)
	 */
	public void visitRelation(IRelation relation) {
		println(startElement(RELATION), deep);
		println(startElement(ID) + relation.getId() + endElement(ID), ++deep);
		println(startElement(LIST), deep++);
		Iterator<String> iter = relation.relations().iterator();
		while (iter.hasNext()) {
			println(startElement(ID) + iter.next() + endElement(ID), deep);
		}
		println(endElement(LIST), --deep);
		println(endElement(RELATION), --deep);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INodeVisitor#visitUser(com.code.aon.jaas.client.ast.IUser)
	 */
	public void visitUser(IUser user) {
		println(startElement(USER), deep++);
		println(startElement(ID) + user.getId() + endElement(ID), deep);
		println(startElement(NAME) + user.getName() + endElement(NAME), deep);
		println(startElement(DESCRIPTION) + user.getDescription() + endElement(DESCRIPTION), deep);
		String passwd = user.getPasswd();
		passwd = (passwd != null)? passwd: "";
		println(startElement(PASSWORD) + passwd + endElement(PASSWORD), deep);
		println(endElement(USER), --deep);
	}

}