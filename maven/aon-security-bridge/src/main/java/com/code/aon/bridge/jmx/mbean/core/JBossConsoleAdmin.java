package com.code.aon.bridge.jmx.mbean.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;

import org.jboss.mx.util.MBeanServerLocator;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IMBeanInfo;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.jmx.mbean.MBeanFilter;
import com.code.aon.bridge.jmx.mbean.Messages;
import com.code.aon.bridge.jmx.mbean.SecurityMBeanException;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * JBoss Console Administration class.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
 */
public class JBossConsoleAdmin implements IConsoleAdmin {

	/** Field MAIN_DEPLOYER (value is ""jboss.system:service=MainDeployer"") */
	static final String MAIN_DEPLOYER = "jboss.system:service=MainDeployer";
	/** Field JAAS_SECURITY (value is ""jboss.security:service=JaasSecurityManager"") */
	static final String JAAS_SECURITY = "jboss.security:service=JaasSecurityManager";
	/** Field SERVER_CONFIG (value is ""jboss.system:type=ServerConfig"") */
	static final String SERVER_CONFIG = "jboss.system:type=ServerConfig";

	/** Field AON_MAIN_DEPLOYER (value is ""jboss.admin:service=AonMainDeployer"") */
	static final String AON_MAIN_DEPLOYER = "jboss.admin:service=AonMainDeployer";
	/** Field AON_SECURITY (value is ""jboss.admin:service=AonSecurity"") */
	static final String AON_SECURITY = "jboss.admin:service=AonSecurity";
	/** Field AON_LDAP (value is ""jboss.admin:service=AonLdap"") */
	static final String AON_LDAP = "jboss.admin:service=AonLdap";
	/** Field AON_SESSION_MANAGER (value is ""jboss.admin:service=AonSessionManager"") */
	static final String AON_SESSION_MANAGER = "jboss.admin:service=AonSessionManager";

	/** Field DELIM (value is "","") */
	static final String DELIM = ",";
	/** Field MBEANS_DELIM (value is ""org.jboss.deployment.DeploymentInfo"") */
	static final String MBEANS_DELIM = "org.jboss.deployment.DeploymentInfo";
	/** Field TEN (value is 10) */
	static final int TEN = 10;
	/** Field EIGHT (value is 8) */
	static final int EIGHT = 8;
	/** Field SEVEN (value is 7) */
	static final int SEVEN = 7;
	/** Field FOURTEEN (value is 14) */
	static final int FOURTEEN = 14;

	/** Field mbeans */
	private Map<String, IMBeanInfo> mbeans;
	
	private String aonSecurityName;

	/**
	 * Constructor for JBossConsoleAdmin
	 * 
	 * @throws DeploymentException
	 */
	public JBossConsoleAdmin() throws DeploymentException {
		load();
		initAonSecurityName();
	}

	@Override
	public String getAonMainDeployerName() {
		return AON_MAIN_DEPLOYER;
	}

	@Override
	public String getAonSecurityName() {
		return this.aonSecurityName;
	}

	@Override
	public String getAonSessionManagerName() {
		return AON_SESSION_MANAGER;
	}

	@Override
	public String listDeployedAsString() throws DeploymentException {
		try {
			ObjectName name = new ObjectName(MAIN_DEPLOYER);
			return (String) getMBeanServer().invoke(name, "listDeployedAsString", null, null);
		} catch (Exception e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isDeployed(String url) throws DeploymentException {
		try {
			ObjectName name = new ObjectName(MAIN_DEPLOYER);
			Object[] params = { url };
			String[] sig = { String.class.getName() };
			Boolean bol = 
				(Boolean) getMBeanServer().invoke( name, IOperation.ISDEPLOYED, params, sig ); 
			return bol.booleanValue();
		} catch (Exception e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public void flushAuthenticationCache(String domain, Principal principal)
			throws DeploymentException {
		try {
			ObjectName name = new ObjectName(JAAS_SECURITY);
			Object[] params = { domain, principal };
			String[] sig = { String.class.getName(), Principal.class.getName() };
			getMBeanServer().invoke(name, "flushAuthenticationCache", params, sig);
		} catch (Exception e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public Subject getActiveSubject() throws DeploymentException {
		try {
			ObjectName name = new ObjectName( JAAS_SECURITY );
			return (Subject) getMBeanServer().invoke( name, "getActiveSubject", null, null );
		} catch (Exception e) {
			throw new DeploymentException( e.getMessage(), e );
		}		
	}

	@Override
	public URL getServerHomeURL() throws DeploymentException {
		try {
			ObjectName name = new ObjectName(SERVER_CONFIG);
			return (URL) getMBeanServer().getAttribute(name, "ServerHomeURL");
		} catch (Exception e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public String getDeployerHome() throws DeploymentException {
		return getServerHomeURL().getPath() + "deploy/";
	}

	@Override
	public String getServerInfo() throws DeploymentException {
		Object[] params = { EMPTY_STRING };
		String[] sig = { String.class.getName() };
		return (String) invoke( getAonMainDeployerName(), IOperation.GET_DEPLOYER_INFO, params, sig );
	}

	@Override
	public void deploy(String name) throws DeploymentException {
		try {
          Object[] params = { new File( getDeployerHome() + name ).toURL() };
          String[] sig = { URL.class.getName() };
          invoke( getAonMainDeployerName(), IOperation.DEPLOY, params, sig);
		} catch (IOException e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public Object invoke(String oname, String method, Object[] params, String[] sig) 
			throws DeploymentException {
		try {
			return getMBeanServer().invoke( new ObjectName( oname ), method, params, sig );
		} catch (Exception e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public List<IMBeanInfo> getMBeans(MBeanFilter filter) {
		List<IMBeanInfo> list = new LinkedList<IMBeanInfo>();
		Iterator<IMBeanInfo> iter = mbeans.values().iterator();
		while (iter.hasNext()) {
			IMBeanInfo info = iter.next();
			if (filter.accept(info)) {
				list.add( info );
			}
		}
		return list;
	}

	@Override
	public void load() throws DeploymentException {
		StringTokenizer st = new StringTokenizer(listDeployedAsString(), DELIM);
		mbeans = new HashMap<String, IMBeanInfo>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.indexOf(MBEANS_DELIM) > -1) {
				try {
					JBossMBeanInfo info = new JBossMBeanInfo(token);
					mbeans.put(info.getName(), info);
				} catch (SecurityMBeanException e) {
					//	TODO [iayerbe] Mirar si se debe propagar esta excepción.
				}
			}
		}
	}
	
	public void initAonSecurityName() {
		this.aonSecurityName = AON_SECURITY;
		try {
			ObjectName name = new ObjectName(AON_LDAP);
			if ( getMBeanServer().isRegistered(name) ) {
				this.aonSecurityName = AON_LDAP;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Get MBeanServer.
	 * 
	 * @return MBeanServer
	 * @throws JMException
	 */
	protected MBeanServer getMBeanServer() throws JMException {
		return MBeanServerLocator.locateJBoss();
    }

    /**
     * JBoss MBeanInfo
     * 
     * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
     * @since 1.0
     *  
     */
    protected class JBossMBeanInfo implements IMBeanInfo {

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private String name;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private String url;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private String deployer;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private String status;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private String state;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private String watch;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private long lastDeployed;

        /**
         * // TODO [iayerbe] Documéntame!
         */
        private long lastModified;

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         * @throws SecurityMBeanException
         */
        public JBossMBeanInfo(String deployed) throws SecurityMBeanException {
            init(deployed);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getName()
         */
        public String getName() {
            return name;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getURL()
         */
        public String getURL() {
            return url;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getDeployer()
         */
        public String getDeployer() {
            return deployer;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getStatus()
         */
        public String getStatus() {
            return status;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getState()
         */
        public String getState() {
            return state;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getWatch()
         */
        public String getWatch() {
            return watch;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getLastDeployed()
         */
        public long getLastDeployed() {
            return lastDeployed;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aon.security.policy.IDeployedMBeanInfo#getLastModified()
         */
        public long getLastModified() {
            return lastModified;
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         * @throws SecurityMBeanException
         */
        private void init(String deployed) throws SecurityMBeanException {
            checkURL(deployed);
            checkDeployer(deployed);
            checkStatus(deployed);
            checkState(deployed);
            checkWatch(deployed);
            checkLastDeployed(deployed);
            checkLastModified(deployed);
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         * @throws SecurityMBeanException
         */
        private void checkURL(String deployed) throws SecurityMBeanException {
            int start = deployed.indexOf("=") + 1; //$NON-NLS-1$
            int end = deployed.indexOf("}"); //$NON-NLS-1$
            url = deployed.substring(start, end).trim();
            if (checkName(url) <= 0) {
                throw new SecurityMBeanException(Messages.getString("JBossConsoleAdmin.0")); //$NON-NLS-1$
            }
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param url
         *            String
         * @return int
         */
        private int checkName(String url) {
            int start = url.lastIndexOf("/") + 1; //$NON-NLS-1$
            int end = url.length();
            name = url.substring(start, end).trim();
            return name.length();
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         */
        private void checkDeployer(String deployed) throws SecurityMBeanException {
            int start = deployed.indexOf("deployer:") + TEN; //$NON-NLS-1$
            String tmp = deployed.substring(start, deployed.length()).trim();
            int end = tmp.indexOf("@");
            if ( end > -1 ) {
            	deployer = tmp.substring( 0, end ).trim();
            } else {
            	end = tmp.indexOf("]") + 1;
            	deployer = tmp.substring( 0, end ).trim();
            }
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         */
        private void checkStatus(String deployed) {
            int start = deployed.indexOf("status:") + EIGHT; //$NON-NLS-1$
            int end = deployed.indexOf("state:"); //$NON-NLS-1$
            status = deployed.substring(start, end).trim();
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         */
        private void checkState(String deployed) {
            int start = deployed.indexOf("state:") + SEVEN; //$NON-NLS-1$
            int end = deployed.indexOf("watch:"); //$NON-NLS-1$
            state = deployed.substring(start, end).trim();
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         */
        private void checkWatch(String deployed) {
            int start = deployed.indexOf("watch:") + SEVEN; //$NON-NLS-1$
            int end = deployed.indexOf("lastDeployed:"); //$NON-NLS-1$
            watch = deployed.substring(start, end).trim();
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         */
        private void checkLastDeployed(String deployed) {
            int start = deployed.indexOf("lastDeployed:") + FOURTEEN; //$NON-NLS-1$
            int end = deployed.indexOf("lastModified"); //$NON-NLS-1$
            lastDeployed = Long.parseLong(deployed.substring(start, end).trim());
        }

        /**
         * // TODO [iayerbe] Documéntame!
         * 
         * @param deployed
         *            String
         */
        private void checkLastModified(String deployed) {
            int start = deployed.indexOf("lastModified:") + FOURTEEN; //$NON-NLS-1$
            int end = deployed.indexOf("mbeans:"); //$NON-NLS-1$
            lastModified = Long.parseLong(deployed.substring(start, end).trim());
        }
    }

}