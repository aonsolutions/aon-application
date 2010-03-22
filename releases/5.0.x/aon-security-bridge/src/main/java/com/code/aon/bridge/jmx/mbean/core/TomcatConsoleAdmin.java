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
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.security.auth.Subject;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IMBeanInfo;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.jmx.mbean.MBeanFilter;
import com.code.aon.jaas.deployment.DeploymentException;

@SuppressWarnings("unchecked")
public class TomcatConsoleAdmin implements IConsoleAdmin {

    /** Field DELIM (value is "";"") */
    static final String DELIM = ";";
    /** Field AON_MAIN_DEPLOYER (value is ""Catalina:type=Security,name=AonMainDeployer"") */
    static final String AON_MAIN_DEPLOYER = "Catalina:type=Security,name=AonMainDeployer";
    /** Field AON_SECURITY (value is ""Catalina:type=Security,name=AonSecurity"") */
    static final String AON_SECURITY = "Catalina:type=Security,name=AonSecurity";
    /** Field AON_SESSION_MANAGER (value is ""Catalina:type=Security,name=AonSessionManager"") */
    static final String AON_SESSION_MANAGER = "Catalina:type=Security,name=AonSessionManager";

    /** Map of applications deployed in Tomcat webapps diretory. */
    private Map<String, IMBeanInfo> mbeans;

	@Override
	public String getAonMainDeployerName() {
		return AON_MAIN_DEPLOYER;
	}

	@Override
	public String getAonSecurityName() {
		return AON_SECURITY;
	}

	@Override
	public String getAonSessionManagerName() {
		return AON_SESSION_MANAGER;
	}

	@Override
	public String listDeployedAsString() throws DeploymentException {
    	try {
    		StringBuffer sb = new StringBuffer(); 
            Iterator iter = getMBeanServer().queryNames( new ObjectName("*:*"), null).iterator();
			while (iter.hasNext()) {
				ObjectName oname = (ObjectName) iter.next();
				String type = oname.getKeyProperty("type");
				if ( type != null && type.equals("Manager") ) {
					String value = oname.toString();
					value = value.substring( value.indexOf(",") + 1, value.length() );
					sb.append( value + DELIM );
				}
			}
			return sb.toString();
		} catch (MalformedObjectNameException e) {
            throw new DeploymentException(e.getMessage(), e);
		} catch (NullPointerException e) {
            throw new DeploymentException(e.getMessage(), e);
		} catch (JMException e) {
            throw new DeploymentException(e.getMessage(), e);
		} 
	}

	@Override
	public boolean isDeployed(String name) throws DeploymentException {
	    Object[] params = { name };
	    String[] sig = { String.class.getName() };
	    Boolean bol = (Boolean) invoke( getAonMainDeployerName(), IOperation.ISDEPLOYED, params, sig );
	    return bol.booleanValue();
	}

	@Override
	public void flushAuthenticationCache(String domain, Principal principal) throws DeploymentException {
		// TODO Auto-generated method stub
	}

	@Override
	public Subject getActiveSubject() throws DeploymentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getServerHomeURL() throws DeploymentException {
		try {
			Object[] params = { EMPTY_STRING };
			String[] sig = { String.class.getName() };
			String url = 
				(String) invoke( getAonMainDeployerName(), IOperation.CATALINA_HOME, params, sig );
			return new File(url).toURL();
		} catch (IOException e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public String getDeployerHome() throws DeploymentException {
		return getServerHomeURL().getPath() + "webapps/";
	}

	@Override
	public String getServerInfo() throws DeploymentException {
		Object[] params = { EMPTY_STRING };
		String[] sig = { String.class.getName() };
		return (String) invoke( getAonMainDeployerName(), IOperation.GET_DEPLOYER_INFO, params, sig );
	}

	@Override
	public void deploy(String name) throws DeploymentException {
       Object[] params = { name, EMPTY_STRING };
       String[] sig = { String.class.getName(), String.class.getName() };
       invoke( getAonMainDeployerName(), IOperation.DEPLOY, params, sig );
	}

	@Override
	public Object invoke(String oname, String method, Object[] params, String[] sig)
			throws DeploymentException {
		try {
			return getMBeanServer().invoke( new ObjectName( oname ), method, params, sig );
		} catch (MalformedObjectNameException e) {
			throw new DeploymentException(e.getMessage(), e);
		} catch (NullPointerException e) {
			throw new DeploymentException(e.getMessage(), e);
		} catch (ReflectionException e) {
			throw new DeploymentException(e.getMessage(), e);
		} catch (JMException e) {
			throw new DeploymentException(e.getMessage(), e);
		}
	}

	@Override
	public List getMBeans(MBeanFilter filter) {
		List<IMBeanInfo> list = new LinkedList<IMBeanInfo>();
		Iterator iter = mbeans.values().iterator();
		while (iter.hasNext()) {
			IMBeanInfo info = (IMBeanInfo) iter.next();
			if (filter.accept(info)) {
				list.add(info);
			}
		}
		return list;
	}

	@Override
	public void load() throws DeploymentException {
		String home = getDeployerHome();
		StringTokenizer st = new StringTokenizer(listDeployedAsString(), DELIM);
		mbeans = new HashMap<String, IMBeanInfo>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			TomcatMBeanInfo info = new TomcatMBeanInfo(token, home);
			if ( info.exists() )
				mbeans.put(info.getName(), info);
		}
	}

	/**
	 * Connecting to the JMX Agent Programmatically.
	 * 
	 * @return MBeanServer
	 * @throws JMException
	 */
	protected MBeanServer getMBeanServer() throws JMException {
    	MBeanServer server = null;
    	List servers = MBeanServerFactory.findMBeanServer(null);
    	if (servers.size() > 0) {
    		server = (MBeanServer) servers.get(0);
    	}
    	return server;
	}

	/**
		Name: Catalina:type=Manager,path=/gtm,host=localhost
		modelerType: org.apache.catalina.session.StandardManager
		algorithm: MD5
		randomFile: /dev/urandom
		className: org.apache.catalina.session.StandardManager
		distributable: false
		entropy: org.apache.catalina.session.StandardManager@1d144a4
		maxActiveSessions: -1
		maxInactiveInterval: 1800
		processExpiresFrequency: 6
		sessionIdLength: 16
		name: StandardManager
		pathname: SESSIONS.ser
		activeSessions: 0
		sessionCounter: 0
		maxActive: 0
		sessionMaxAliveTime: 0
		sessionAverageAliveTime: 0
		rejectedSessions: 0
		expiredSessions: 0
		processingTime: 0
		duplicates: 0
	 * 
	 * @author iayerbe
	 *
	 */
	class TomcatMBeanInfo implements IMBeanInfo {

		private String name;
		private String url;

		public TomcatMBeanInfo(String deployed, String home) {
	        StringTokenizer st = new StringTokenizer(deployed, ",");
	        while (st.hasMoreTokens()) {
	            String token = st.nextToken();
	            int index = token.indexOf('/');
	            if (index > -1 && token.indexOf("path=") > -1 ) {
		            name = token.substring( index + 1, token.length() ) + ".war";
		            url = home + name;
	            }
	        }
		}

		public boolean exists() {
            File file = new File( url );
            return file.exists();
		}

		public String getDeployer() {
			return MBeanFilter.TOMCAT_MANAGER;
		}

		public long getLastDeployed() {
			throw new UnsupportedOperationException(); 
		}

		public long getLastModified() {
			throw new UnsupportedOperationException(); 
		}

		public String getName() {
			return name;
		}

		public String getState() {
			throw new UnsupportedOperationException(); 
		}

		public String getStatus() {
			throw new UnsupportedOperationException(); 
		}

		public String getURL() {
			return url; 
		}

		public String getWatch() {
			throw new UnsupportedOperationException(); 
		}

	}
}
