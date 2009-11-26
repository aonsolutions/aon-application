package com.code.aon.jaas.vendor.jboss.deployment;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.deployment.DeploymentInfo;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.valves.BackDoorAuthenticationValve;
import com.code.aon.jaas.valves.BackDoorPrincipal;
import com.code.aon.jaas.vendor.VendorFactoryManager;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

public class JBossAuthenticationValve extends BackDoorAuthenticationValve {

	/** BackDoorAuthenticationValve Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(JBossAuthenticationValve.class);

	@Override
	protected ObjectName getAonLdap() throws MalformedObjectNameException {
		return new ObjectName( "jboss.admin:service=AonLdap" );
	}

	@Override
	protected ObjectName getAonSessionManager() throws MalformedObjectNameException {
		return new ObjectName( "jboss.admin:service=AonSessionManager" );
	}

	@Override
	protected ObjectName getMainDeployer() throws MalformedObjectNameException {
		return new ObjectName( "jboss.system:service=MainDeployer" );
	}

	@Override
	protected void flushRemoteAccess(String IP, String sessionId)
			throws NamingException, MalformedObjectNameException,
			NullPointerException, InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
		final Hashtable<String, String> env = new Hashtable<String, String>();
		env.put( javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory" );
		env.put( javax.naming.Context.PROVIDER_URL, "jnp://" + IP + ":1099" );
		javax.naming.Context ctx = null;
		try {
			BackDoorPrincipal bdp = backdoorPrincipals.get( sessionId );
			LOGGER.debug( "Flushing REMOTE access using IP: {} session: {} principal: {}", new Object[]{IP, sessionId, bdp.getPrincipal().getName()} );
			ctx = new InitialContext(env);
			RMIAdaptor server = (RMIAdaptor) ctx.lookup( "jmx/invoker/RMIAdaptor" );
			Object[] params = { sessionId, bdp };
			String[] sig = { String.class.getName(), Object.class.getName() };
			server.invoke( getAonSessionManager(), "flushSSOPrincipal", params, sig );
		} finally {
			if ( ctx != null )
				try {
					ctx.close();
				} catch (NamingException e) {
					LOGGER.error( e.getMessage(), e );
				}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void storeDeployed() throws IOException {
		try {
			List<String> l = new ArrayList<String>();
			Collection<DeploymentInfo> c = 
				(Collection<DeploymentInfo>) mserver.invoke( getMainDeployer(), "listDeployed", null, null );
			for (Iterator<DeploymentInfo> iterator = c.iterator(); iterator.hasNext();) {
				DeploymentInfo di = iterator.next();
		        if ( di.shortName.endsWith( "war" ) ) {
		    		String vendorWFile = VendorFactoryManager.create("jboss").getVendorWEBFile();
		    		if (vendorWFile != null) {
		    			URL vendorWEB = di.localCl.findResource( "WEB-INF/" + vendorWFile );
		    			if (vendorWEB != null) {
							try {
								IVendorDescriptor jaas = VendorFactoryManager.parse( "jboss", vendorWEB.openStream() );
								if ( jaas.getContext() != null )
									l.add( jaas.getContext() );
							} catch (Exception e) {
								// Do Nothing
							}
		    			} else {
				        	l.add( "/" + di.shortName );
		    			}
		    		}
		        }
			}
			Util.serialize( l );
			deployed = true;
		} catch (InstanceNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MBeanException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ReflectionException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (NullPointerException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

}
