package com.code.aon.common.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.common.dao.sql.DAOException;

public class HibernateUtil { 

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);
    
	public static final String HIBERNATE_CONFIGURATION_FILE_PROPERTY = "com.code.aon.hibernate.cfg.xml";
    public static final String DEFAULT_SESSION_FACTORY_NAME = "localhost/default";
    
    private static Map<String,SessionFactory> sessionFactory = new HashMap<String, SessionFactory>();
    public static final Map<String,ThreadLocal<Session>> session = new HashMap<String,ThreadLocal<Session>>();
    public static final Map<String,ThreadLocal<Transaction>> transaction = new HashMap<String,ThreadLocal<Transaction>>(); 

    private static ThreadLocal<Boolean> MUST_CLOSE_SESSION = new ThreadLocal<Boolean>(){
    	protected Boolean initialValue() {
    		return new Boolean(true);
    	}
    };
    private static ThreadLocal<Boolean>  MUST_BEGIN_TRANSACTION = new ThreadLocal<Boolean>(){
    	protected Boolean initialValue() {
    		return new Boolean(true);
    	}
    };

    
    private static IConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
    
    private static ISessionFactoryNameProvider sessionFactoryNameProvider = DefaultSessionFactoryNameProvider.getInstance();    

    @Deprecated
    public static Session getSession() throws HibernateException {
    	return getSession(getSessionFactoryName());
    } 
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
    @Deprecated
    public static void startSession() {
    	startSession(getSessionFactoryName());
    }

    public static void startSession( String sessionFactoryName ) {
    	HibernateUtil.getSession(sessionFactoryName);
    }
    
    @Deprecated
    public static void closeSession() { 
    	closeSession(getSessionFactoryName());
    } 

    public static void closeSession( String sessionFactoryName ) { 
        Session s = session.get(sessionFactoryName).get(); 
        session.get(sessionFactoryName).set(null); 
        if (s != null) {
            s.close();
            LOGGER.debug("** Hibernate session closed" );
        } 
    } 

    @Deprecated
    public static SessionFactory getSessionFactory() {
    	return getSessionFactory(getSessionFactoryName());
    }

    public static SessionFactory getSessionFactory( String sessionFactoryName ) {
        SessionFactory sf = sessionFactory.get(sessionFactoryName); 
        // Open a new Session, if this Thread has none yet 
        if (sf == null) { 
            sf = createSessionFactory( sessionFactoryName ); 
        } 
        return sf; 
    }
    
    public static String getSessionFactoryName() {
    	return getSessionFactoryName(null);
    }

    public static String getSessionFactoryName( String pojoClass ) {
//    	return  HibernateUtil.DEFAULT_SESSION_FACTORY_NAME;
    	return sessionFactoryNameProvider.getName(pojoClass);
    }

    public static void beginTransaction( String sessionFactoryName ) throws DAOException {
        Transaction tx = transaction.get(sessionFactoryName).get(); 
        try {
            if (tx == null) {
                LOGGER.debug("Starting new database transaction in this thread.");
                tx = getSession(sessionFactoryName).beginTransaction();
                transaction.get(sessionFactoryName).set(tx);
            }
        } catch (HibernateException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }
    
    public static void commitTransaction( String sessionFactoryName ) throws DAOException {
        Transaction tx = transaction.get(sessionFactoryName).get(); 
        try {
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                LOGGER.debug("Committing database transaction of this thread.");
                tx.commit();
            }
            transaction.get(sessionFactoryName).set(null); 
        } catch (HibernateException ex) {
            rollbackTransaction(sessionFactoryName);
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    public static void rollbackTransaction( String sessionFactoryName ) throws DAOException {
        Transaction tx = transaction.get(sessionFactoryName).get(); 
        try {
            transaction.get(sessionFactoryName).set(null); 
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                LOGGER.debug("Tyring to rollback database transaction of this thread.");
                tx.rollback();
            }
        } catch (HibernateException ex) {
            throw new DAOException(ex.getMessage(), ex);
        } finally {
            closeSession( sessionFactoryName );
        }
    }
    
    
    public static void setCloseSession(boolean mustCloseSession) {
        HibernateUtil.MUST_CLOSE_SESSION.set(new Boolean(mustCloseSession));
    }
    
    public static boolean mustCloseSession() {
        return HibernateUtil.MUST_CLOSE_SESSION.get().booleanValue();
    }

    public static void setBeginTransaction(boolean mustBeginTransaction) {
        HibernateUtil.MUST_BEGIN_TRANSACTION.set(new Boolean(mustBeginTransaction));
    }

    public static boolean mustBeginTransaction() {
        return HibernateUtil.MUST_BEGIN_TRANSACTION.get().booleanValue();
    }

    private synchronized static SessionFactory createSessionFactory( String sessionFactoryName ) {
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
            LOGGER.error(he.getMessage(), he); 
            throw new RuntimeException("Configuration problem: " + he.getMessage(), he); 
        }
        return factory;
    }
	
	public static IConfigurationFactory getConfigurationFactory() {
		return configurationFactory;
	}
	public static void setConfigurationFactory(
			IConfigurationFactory configurationFactory) {
		HibernateUtil.configurationFactory = configurationFactory;
	}

	public static ISessionFactoryNameProvider getSessionFactoryNameProvider() {
		return sessionFactoryNameProvider;
	}
	public static void setSessionFactoryNameProvider(
			ISessionFactoryNameProvider sessionFactoryNameProvider) {
		HibernateUtil.sessionFactoryNameProvider = sessionFactoryNameProvider;
	}
} 
