package com.code.aon.common.dao.hibernate;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.common.dao.sql.DAOException;

/**
 * Hibernate utilities class.
 * 
 * @author Consulting & Development.
 *
 */

public class HibernateUtil { 

    /**
     * TODO
     */
    public static final String HIBERNATE_CONFIGURATION_FILE_PROPERTY = "com.code.aon.hibernate.cfg.xml";
    /**
     * TODO
     */
    public static final String DEFAULT_SESSION_FACTORY_NAME = "localhost/default";

    /**
     * Obtain a suitable Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName()); 

    /**
     * Map that holds sessionFactory.
     */
    private static Map<String,SessionFactory> sessionFactory = new HashMap<String,SessionFactory>(); 
    
    /**
     * Map that holds current open sessions.
     */
    public static final Map<String,ThreadLocal<Session>>session = new HashMap<String,ThreadLocal<Session>>(); 

    /**
     * Map that holds current open transaction.
     */
    public static final Map<String,ThreadLocal<Transaction>>transaction = new HashMap<String,ThreadLocal<Transaction>>(); 

    /**
     * 
     */
    private static ThreadLocal<Boolean> MUST_CLOSE_SESSION = new ThreadLocal<Boolean>(){
    	
    	/**
    	 * @return B
    	 */
    	protected Boolean initialValue() {
    		return new Boolean(true);
    	}
    };
    /**
     * 
     */
    private static ThreadLocal<Boolean>  MUST_BEGIN_TRANSACTION = new ThreadLocal<Boolean>(){
    	
    	/**
    	 * @return B
    	 */
    	protected Boolean initialValue() {
    		return new Boolean(true);
    	}
    };

    
    private static IConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
    
    private static ISessionFactoryNameProvider sessionFactoryNameProvider = DefaultSessionFactoryNameProvider.getInstance();    

    /**
     * Open session from named instance.
     * 
     * @return The session.
     * @throws HibernateException
     */
    @Deprecated
    public static Session getSession() throws HibernateException {
    	return getSession(getSessionFactoryName());
    } 

    /**
     * Open session from named instance.
     * 
     * @param sessionFactoryName  
     * @return The session.
     * @throws HibernateException
     */
    public static Session getSession( String sessionFactoryName ) throws HibernateException {
        if (session.get(sessionFactoryName) == null) {
            getSessionFactory(sessionFactoryName);
        }
        Session s = session.get(sessionFactoryName).get(); 
        //Open a new Session, if this Thread has none yet 
        if (s == null) { 
            s = sessionFactory.get(sessionFactoryName).openSession(); 
            session.get(sessionFactoryName).set(s); 
        } 
        return s; 
    } 
    
    /**
     * Open session from named instance.
     * 
     * @throws HibernateException
     */
    @Deprecated
    public static void startSession() {
    	startSession(getSessionFactoryName());
    }

    /**
     * Open session from named instance.
     * 
     * @param sessionFactoryName  
     * @throws HibernateException
     */
    public static void startSession( String sessionFactoryName ) {
    	HibernateUtil.getSession(sessionFactoryName);
    }
    
    /**
     *  Close session from named instance.
     * 
     * @throws HibernateException
     */
    @Deprecated
    public static void closeSession() { 
    	closeSession(getSessionFactoryName());
    } 

    /**
     *  Close session from named instance.
     *  
     * @param sessionFactoryName  
     * @throws HibernateException
     */
    public static void closeSession( String sessionFactoryName ) { 
        Session s = session.get(sessionFactoryName).get(); 
        session.get(sessionFactoryName).set(null); 
        if (s != null) {
            s.close();
            LOGGER.finest("** Hibernate session closed" );
        } 
    } 
    
    /**
     * Retrieve a sessionFactory from named instance.
     * 
     * @return The SessionFactory. 
     */
    @Deprecated
    public static SessionFactory getSessionFactory() {
    	return getSessionFactory(getSessionFactoryName());
    }

    /**
     * Retrieve a sessionFactory from named instance.
     * 
     * @param sessionFactoryName  
     * @return The SessionFactory. 
     */
    public static SessionFactory getSessionFactory( String sessionFactoryName ) {
        SessionFactory sf = sessionFactory.get(sessionFactoryName); 
        // Open a new Session, if this Thread has none yet 
        if (sf == null) { 
            sf = createSessionFactory( sessionFactoryName ); 
        } 
        return sf; 
    }
    
    /**
     * Retrieve the sessionFactory named instance.
     * 
     * @return The SessionFactory. 
     */
    @Deprecated
    public static String getSessionFactoryName() {
    	return getSessionFactoryName(null);
    }

    /**
     * Retrieve the sessionFactory named instance.
     * 
     * @param pojoClass  
     * @return The SessionFactory. 
     */
    public static String getSessionFactoryName( String pojoClass ) {
    	return sessionFactoryNameProvider.getName(pojoClass);
    }
    
    /**
     * Start a new database transaction.
     * 
     * @throws DAOException
     */
    @Deprecated
    public static void beginTransaction() throws DAOException {
    	beginTransaction(getSessionFactoryName());
    }

    /**
     * Start a new database transaction.
     * 
     * @param sessionFactoryName  
     * @throws DAOException
     */
    public static void beginTransaction( String sessionFactoryName ) throws DAOException {
        Transaction tx = transaction.get(sessionFactoryName).get(); 
        try {
            if (tx == null) {
                LOGGER.fine("Starting new database transaction in this thread.");
                tx = getSession(sessionFactoryName).beginTransaction();
                transaction.get(sessionFactoryName).set(tx);
            }
        } catch (HibernateException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Commit the database transaction.
     * 
     * @throws DAOException
     */
    @Deprecated
    public static void commitTransaction() throws DAOException {
    	commitTransaction(getSessionFactoryName()); 
    }

    /**
     * Commit the database transaction.
     * 
     * @param sessionFactoryName  
     * @throws DAOException
     */
    public static void commitTransaction( String sessionFactoryName ) throws DAOException {
        Transaction tx = transaction.get(sessionFactoryName).get(); 
        try {
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                LOGGER.fine("Committing database transaction of this thread.");
                tx.commit();
            }
            transaction.get(sessionFactoryName).set(null); 
        } catch (HibernateException ex) {
            rollbackTransaction();
            throw new DAOException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Commit the database transaction.
     * 
     * @throws DAOException
     */
    @Deprecated
    public static void rollbackTransaction() throws DAOException {
    	rollbackTransaction( getSessionFactoryName() );
    }

    /**
     * Commit the database transaction.
     * 
     * @param sessionFactoryName 
     * @throws DAOException
     */
    public static void rollbackTransaction( String sessionFactoryName ) throws DAOException {
        Transaction tx = transaction.get(sessionFactoryName).get(); 
        try {
            transaction.get(sessionFactoryName).set(null); 
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                LOGGER.fine("Tyring to rollback database transaction of this thread.");
                tx.rollback();
            }
        } catch (HibernateException ex) {
            throw new DAOException(ex.getMessage(), ex);
        } finally {
            closeSession( sessionFactoryName );
        }
    }
    
    /**
     * TODO
     * 
     * @param cmd
     * @param associationPath
     * @return <code>true</code> if is an identifier.  
     */
    public static boolean isIdentifier(ClassMetadata cmd, String associationPath) {
        String idName = cmd.getIdentifierPropertyName();
        int pos = StringUtils.indexOfAny( associationPath, HibernateRenderer.SEPARATORS );
        if (pos != -1) {
            String property = associationPath.substring(0, pos);
            if ( idName.equals(property) ) {
                return true;
            }
            Type type = cmd.getPropertyType( property );
            if ( type != null ) {
                ClassMetadata propertyCmd = null;
                if (type.isEntityType()) {
                    EntityType et = (EntityType) type;
                    propertyCmd = getSessionFactory().getClassMetadata( et.getAssociatedEntityName() );
                } else if (type.isComponentType()) {
                    ComponentType ct = (ComponentType) type;
                    propertyCmd = getSessionFactory().getClassMetadata( ct.getReturnedClass() );
                }
                return isIdentifier( propertyCmd, associationPath.substring(pos+1) );
            }
        } else {
            return associationPath.equals(idName);
        }
        return false;
    }

    /**
     * Checks if is componsite identifier.
     * 
     * @param cmd the cmd
     * @param property the property
     * 
     * @return true, if is componsite identifier
     */
    public static boolean isComponsiteIdentifier(ClassMetadata cmd, String property) {
        String idName = cmd.getIdentifierPropertyName();
        if ( idName.equals(property) ) {
        	Type type = cmd.getIdentifierType();
        	return type.isComponentType();
        }
        return false;
    }
    
    /**
     * TODO
     * @param mustCloseSession
     */
    public static void setCloseSession(boolean mustCloseSession) {
        HibernateUtil.MUST_CLOSE_SESSION.set(new Boolean(mustCloseSession));
    }
    
    /**
     * TODO
     * @return boolean
     */
    public static boolean mustCloseSession() {
        return HibernateUtil.MUST_CLOSE_SESSION.get().booleanValue();
    }

    /**
     * TODO
     * @param mustBeginTransaction
     */
    public static void setBeginTransaction(boolean mustBeginTransaction) {
        HibernateUtil.MUST_BEGIN_TRANSACTION.set(new Boolean(mustBeginTransaction));
    }

    /**
     * TODO
     * @return boolean
     */
    public static boolean mustBeginTransaction() {
        return HibernateUtil.MUST_BEGIN_TRANSACTION.get().booleanValue();
    }

    /**
     * Creates a SessionFactory for a context and domain. 
     * 
     * @return a SessionFactory.
     * @see com.code.aon.common.dao.hibernate.HibernateUtil#getSessionFactoryName()
     */
    private static SessionFactory createSessionFactory( String sessionFactoryName ) {
        SessionFactory factory = null;
        try {
            Configuration configuration = configurationFactory.getConfiguration(sessionFactoryName);
            factory = configuration.buildSessionFactory();
            sessionFactory.put(sessionFactoryName, factory); 
            session.put(sessionFactoryName, new ThreadLocal<Session>()); 
            transaction.put(sessionFactoryName, new ThreadLocal<Transaction>()); 
            DAOConstantsResolver resolver = new DAOConstantsResolver(configuration);
            resolver.createDAOConstants();
        } catch (HibernateException he) {
            LOGGER.severe(he.getMessage()); 
            throw new RuntimeException("Configuration problem: " + he.getMessage(), he); 
        }
        return factory;
    }

    /**
     * Returns the java.sql.Connection returned by <code>HibernateUtil.getSession().connection()</code> method.
     * 
     * @return A java.sql.Connection.
     */
    @Deprecated
	public static Connection getSQLConnection() {
		return getSQLConnection( getSessionFactoryName() );
	}

    /**
     * Returns the java.sql.Connection returned by <code>HibernateUtil.getSession().connection()</code> method.
     * 
     * @param sessionFactoryName  
     * @return A java.sql.Connection.
     */
    @Deprecated
	public static Connection getSQLConnection( String sessionFactoryName ) {
		return HibernateUtil.getSession(sessionFactoryName).connection();
	}
	
	/**
	 * Gets the configuration factory.
	 * 
	 * @return the configuration factory
	 */
	public static IConfigurationFactory getConfigurationFactory() {
		return configurationFactory;
	}

	/**
	 * Sets the configuration factory.
	 * 
	 * @param configurationFactory the new configuration factory
	 */
	public static void setConfigurationFactory(
			IConfigurationFactory configurationFactory) {
		HibernateUtil.configurationFactory = configurationFactory;
	}

	/**
	 * Gets the SessionFactory name provider.
	 * 
	 * @return the SessionFactory name provider
	 */
	public static ISessionFactoryNameProvider getSessionFactoryNameProvider() {
		return sessionFactoryNameProvider;
	}

	/**
	 * Sets the SessionFactory name provider.
	 * 
	 * @param sessionFactoryNameProvider the new SessionFactory name provider
	 */
	public static void setSessionFactoryNameProvider(
			ISessionFactoryNameProvider sessionFactoryNameProvider) {
		HibernateUtil.sessionFactoryNameProvider = sessionFactoryNameProvider;
	}
	
} 
