package es.code.ecm.security.jaas.spi;

/**
 * Constantes del paquete <code>com.code.aon.jaas.auth.spi</code> 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 */
public interface IConstants {

    /**
     * Indica el estado en el que se encuentra el proceso de identificación.
     */
    static final String INITIALIZE_STATE = "initialize";

    /**
     * Indica el estado en el que se encuentra el proceso de identificación.
     */
    static final String LOGIN_STATE = "login";

    /**
     * Indica el estado en el que se encuentra el proceso de identificación.
     */
    static final String ABORT_STATE = "abort";

    /**
     * Indica el estado en el que se encuentra el proceso de identificación.
     */
    static final String LOGOUT_STATE = "logout";

    /**
     * Indica el nombre del grupo de roles.
     */
    static final String ROLES_GROUP_NAME = "Roles";

    /**
     * Indica el nombre del grupo por defecto al que perteneceran los <code>Principal</code>.
     */
    static final String CALLERPRINCIPAL_GROUP_NAME = "CallerPrincipal";

    /** Indica la estrategia de politica de seguridad. */
    static final String NOMINAL_USER = "Nominal";

    /** Indica la estrategia de politica de seguridad. */
    static final String CONCURRENT_USER = "Concurrent";

	/** Separador entre el usuario y el dominio. */
	static final String IDENTITY_SEPARATOR = "@";

	/** Separador entre el dominio y el contexto. */
	static final String CONTEXT_SEPARATOR = "/";

	/** Etiqueta que indica la Identidad sin autetificar. */
    static final String UNAUTHENTICATED_IDENTITY = "unauthenticatedIdentity";

	/** Etiqueta que indica el Dominio de seguridad. */
    static final String SECURITY_DOMAIN = "securityDomain";

	/** Etiqueta que indica el Nombre del objeto JMX. */
    static final String DEPLOYER_OBJECT_NAME = "objectName";

	/** Etiqueta que indica el Nombre del objeto JMX. */
    static final String SESSION_MANAGER_OBJECT_NAME = "sessionManagerObjectName";

	/** Etiqueta que indica si se usa un algoritmo de encriptación. */
    static final String ALGORITHM = "hashAlgorithm";

	/** Etiqueta que indica el tipo de codificación. */
    static final String ENCODING = "hashEncoding";

    /** Etiqueta que indica el conjunto de caracteres. */
    static final String CHARSET = "hashCharset";

}