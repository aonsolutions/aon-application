package com.code.aon.ui.audit.session;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajax4jsf.application.AjaxViewHandler;
import org.ajax4jsf.webapp.WebXml;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.Classpath;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;
import com.code.aon.ui.common.session.MockHttpServletRequest;
import com.code.aon.ui.common.session.MockHttpServletResponse;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;

public class JSFStartupUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(JSFStartupUtil.class);	
	
	private static final String FACES_APPLICATION = "com.sun.faces.ApplicationImpl";
	
	private static final String STARTED_PROPERTY = "com.code.aon.audit.started";
	
	private static final String RICHFACES_FILTER_NAME = "richfaces";	
	
	private final String[] BASIC_TEMPLATES_PROJECTS = new String[] {
			"aon.rich.components", "aon.ui.audit", "aon.ui.config",
			"aon.ui.resources", "aon.ui.webmail"
	};
	
	private ServletContext servletContext;

	public JSFStartupUtil(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private Application getApplication() {
		return (Application) servletContext.getAttribute(FACES_APPLICATION);
	}
	
	private FacesContext getFacesContext( AuthPrincipal principal ) {
		// Get current FacesContext.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// Check current FacesContext.
		if (facesContext == null) {
			// Create new Lifecycle.
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			// Create new FacesContext.
			FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
			request.setUserPrincipal( principal );
			HttpServletResponse response = new MockHttpServletResponse();
			facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);
		}
		return facesContext;
	}
	
	private AuthPrincipal getAuthPrincipal( String db ) {
		AuthPrincipal principal = null;
		Connection connection = null;
		QueryRunner run = new QueryRunner();
		try {
			connection = DatabaseUtil.getConnection(db);
			ResultSetHandler<Object[]> h = new ArrayHandler();
			Object[] result = run.query( connection,
				"SELECT u.id, u.login, u.domain, d.id, d.name " +
				"FROM user AS u, domain AS d, application_user AS au, application_user_profile AS aup " +
				"WHERE d.type <> 5 AND d.parent is null AND u.domain=d.id AND " +
				"au.user_id=u.id AND aup.application_user=au.id", h);
			if (! ArrayUtils.isEmpty(result) ) {
				principal = new AuthPrincipal((String)result[1]);
				principal.setApplicationId(28);
				principal.setUserId((Integer) result[0]);
				principal.setUserDomainId((Integer) result[2]);
				principal.setDatabaseName(db);
				principal.setDomain((String)result[4]);
				principal.setDomainId((Integer)result[3]);
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		return principal;
	}	
	
	private AuthPrincipal getAuthPrincipal( ConnectionInfo ci ) {
		AuthPrincipal principal = null;
		Connection connection = null;
		QueryRunner run = new QueryRunner();
		try {
			connection = ci.getMetadataConnection();
			ResultSetHandler<List<String>> h = new ColumnListHandler<String>();
			List<String> dbs = run.query( connection,
				"SELECT t.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as t "+
				"WHERE t.TABLE_NAME IN ('domain','db_version','profile') GROUP BY t.TABLE_SCHEMA", h);
			if ( dbs != null ) {
				for( String db : dbs ) {
					principal = getAuthPrincipal(db);
					if ( principal != null ) {
						break;
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		return principal;
	}	
	
	public AuthPrincipal getAuthPrincipal() {
		AuthPrincipal principal = null;
		try {
			ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
			principal = getAuthPrincipal(ci);
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return principal;
	}

	private void renderView( FacesContext fc, String viewId ) {
		ViewHandler viewHandler = fc.getApplication().getViewHandler();
		UIViewRoot viewRoot = viewHandler.createView(fc, viewId);
		try {
			fc.setViewRoot(viewRoot);
			HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			HttpServletRequestValve.setHttpServletRequest(request);
			WebXml webXml = new WebXml();
			webXml.init(servletContext, RICHFACES_FILTER_NAME);
			viewHandler.renderView(fc, viewRoot);
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}	
	
	public boolean isStarted() {
		Object value = this.servletContext.getAttribute(STARTED_PROPERTY);
		return (value == null) ? false : (Boolean) value;
	}

	private void setStarted() {
		this.servletContext.setAttribute(STARTED_PROPERTY, Boolean.TRUE);
	}

	public void initFacelets() {
		Application application = getApplication();
		if ( application != null ) {
			FacesContext fc = getFacesContext(null);	
			if ( fc != null ) {
				loadFacelets(application, fc);
			}
		}
	}
	
	public void init( String host ) {
		//TODO A revisar funcionamiento en TOMCAT8
		/*
		AuthPrincipal principal = getAuthPrincipal(host);
		if ( principal != null ) {
			FacesContext fc = getFacesContext(principal);	
			if ( fc != null ) {
				renderView(fc, "/home.xhtml");						
			}
		}
		*/
		setStarted();
	}
	
	private void loadFacelets( Application application, FacesContext fc ) {
		AjaxViewHandler ajaxViewHandler = null;
		ViewHandler viewHandler = application.getViewHandler();
		if (!(viewHandler instanceof AjaxViewHandler)) {
			FaceletViewHandler faceletHandler = new FaceletViewHandler(viewHandler);			
			ajaxViewHandler = new AjaxViewHandler(faceletHandler);
			application.setViewHandler(ajaxViewHandler);
			if ( initialize(faceletHandler, fc) ) {
				FaceletFactory factory = getFaceletFactory(faceletHandler);
				if ( factory != null ) {
					loadFacelets(factory, getWebTemplates());
					loadFacelets(factory, getBasicTemplates());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private String[] getWebTemplates() {
		String path = this.servletContext.getRealPath("/");
		if (! StringUtils.isEmpty(path) ) {
			File directory = new File(path);
			if ( directory.exists() && directory.isDirectory() && directory.canRead() ) {
				List<String> list = new LinkedList<String>();
				Collection<File> files = FileUtils.listFiles(directory, new String[]{"xhtml"}, true);
				for ( File file : files ) {
					String relativePath = StringUtils.substringAfter(file.getAbsolutePath(), SystemUtils.FILE_SEPARATOR + "aon-aio");
					list.add(relativePath);
				}
				return (String[]) list.toArray(new String[list.size()]);
			}
		}
        return null;
	}

	private URL[] getBasicTemplates() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        JarFile[] jarFiles = Classpath.searchJars(cl, BASIC_TEMPLATES_PROJECTS);
	        if (! ArrayUtils.isEmpty(jarFiles) ) {
	        	List<URL> list = new LinkedList<URL>();
	        	for( JarFile jarFile : jarFiles ) {
	        		list.addAll(Classpath.searchJar(cl, jarFile, "", ".xhtml"));
	        		jarFile.close();
	        	}
	        	return (URL[]) list.toArray(new URL[list.size()]);
	        }
		} catch (IOException e) {
        	LOGGER.error("Error searching basic templates", e);
        }		
        return null;
	}
	
	private boolean initialize( FaceletViewHandler handler, FacesContext fc ) {
		boolean ok = false;
		try {
			Method m = handler.getClass().getDeclaredMethod("initialize", FacesContext.class);
			m.setAccessible(true);
			m.invoke(handler, fc);
			ok = true;
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
		return ok;
	}
	
	private FaceletFactory getFaceletFactory( FaceletViewHandler handler ) {
		FaceletFactory ff = null;
		try {
			Field f = handler.getClass().getDeclaredField("faceletFactory");
			f.setAccessible(true);
			ff = (FaceletFactory) f.get(handler);				
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
		return ff;
	}

	private void loadFacelets( FaceletFactory factory, String[] templates ) {
		if (! ArrayUtils.isEmpty(templates) ) {
			LOGGER.info( "Loading {} templates", templates.length );
			for( String template : templates ) {
				try {
					factory.getFacelet(template);
					LOGGER.debug("Facelet loaded: {}", template );
				} catch (Throwable e) {
					LOGGER.error( e.getMessage(), e );
				}			
			}			
		}
	}	

	private void loadFacelets( FaceletFactory factory, URL[] templates ) {
		if (! ArrayUtils.isEmpty(templates) ) {
			LOGGER.info( "Loading {} URL templates", templates.length );
			for( URL template : templates ) {
				try {
					factory.getFacelet(template);
					LOGGER.debug("Facelet loaded: {}", template );
				} catch (Throwable e) {
					LOGGER.error( e.getMessage(), e );
				}			
			}			
		}
	}	
	
}
