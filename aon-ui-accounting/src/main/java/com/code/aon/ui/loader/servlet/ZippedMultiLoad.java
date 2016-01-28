package com.code.aon.ui.loader.servlet;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ILogger;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.AonSQLFile;
import com.code.aon.dbutils.AonSQLScript;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.master.VersionManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.admin.controller.NewDomainController;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.DomainData;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.controller.AonZipLoaderController.HTMLLogger;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ZippedMultiLoad {
	
	private static String ENTERPRISE_FILENAME = "EMPRESA.TXT";
	private LoaderParams params;
	
	public ZippedMultiLoad(LoaderParams params) {
		this.params = params; 
	}

	public void load(InputStream input, ILogger log) throws AonException {
		log.info("Comienza el proceso de importación");
		try {
			unzip(input,log);
		} finally {
			log.info("Final del proceso de importación");
		}
	}

	private void unzip(InputStream input, ILogger log) throws AonException {
		try {
			Path destinationFolder = Files.createTempDirectory("importDSI", new FileAttribute<?>[] {});
			log.info(MessageFormat.format("Creado Directorio de trabajo: {0}", destinationFolder));
			unzip(destinationFolder, input, log);
			readFolders(destinationFolder, log);
			AonFileUtils.deleteDirectory(destinationFolder.toFile());
			log.info(MessageFormat.format("Borrado de la carpeta de trabajo: {0}", destinationFolder));		
		} catch (IOException e) {
			log.error(MessageFormat.format("Error fatal: '{0}'", e.getMessage()));
			e.printStackTrace();
			throw new AonException("I/O Probleam.",e);
		}
	}

	private void unzip(Path destinationFolder, InputStream input, ILogger log) throws ManagerBeanException {
		ZipInputStream zipIn = null;
		FileOutputStream out = null;
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		AuditLevel oldAuditLevel = null;
		Object servletRequest = ec.getRequest();
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpSession httpSession = request.getSession(false);
		try {
			Object al = httpSession.getAttribute( AuditManager.AUDIT_LEVEL_PROPERTY);
			if (al != null) {
				oldAuditLevel = (AuditLevel) al;   	
			}
			File directory = destinationFolder.toFile();
			if (!directory.exists()) {
				directory.mkdirs();
			}
			zipIn = new ZipInputStream(input);
			boolean empty = true;
			for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
				empty = false;
				String entryName = entry.getName();
				File file = new File(destinationFolder + File.separator + entryName);
				if (entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());					
					if (!newDir.exists()) {
						log.info(MessageFormat.format("Creando carpeta..... {0}",newDir.getName()));
						boolean success = newDir.mkdirs();
						if (!success) {
							String msg = MessageFormat.format("Problema al crear la carpeta \'{0}\'",newDir.getAbsolutePath());
							log.error(msg);
							throw new ManagerBeanException(msg);
						}
					}
				} else {					
					// Comprobar que ya exista el directorio creado, si no es as�, se crea
					// En el ZIP, no tiene por que venir una entrada por el directorio tambien, 
					// en ocasiones viene unicamente la entrada por el fichero, donde se incluye 
					// el nombre del directorio, por lo que al llegar aqui, es posible que el 
					// directorio no est� creado a�n
					File newDir = new File(file.getParent());
					if (!newDir.exists()) {
						log.info(MessageFormat.format("Creando carpeta..... {0}",newDir.getName()));
						boolean success = newDir.mkdirs();
						if (!success) {
							String msg = MessageFormat.format("Problema al crear la carpeta \'{0}\'",newDir.getAbsolutePath());
							log.error(msg);
							throw new ManagerBeanException(msg);
						}
					}					
					
					// Ahora se descomprime el fichero
					log.info(MessageFormat.format("Descomprimiendo ..... {0}",entryName));
					out = new FileOutputStream(file);
					AonIOUtils.copy(zipIn, out);
					out.close();
				}
			}
			if (empty) {
				log.warn("El archivo está vacio o no es un ZIP válido.");
			}
		} catch (IOException e) {
			throw new AonCoreException(e.getMessage());
		} finally {
			AonIOUtils.closeQuietly(out);
			if (oldAuditLevel == null) {
				httpSession.removeAttribute( AuditManager.AUDIT_LEVEL_PROPERTY );
			} else {
				httpSession.setAttribute( AuditManager.AUDIT_LEVEL_PROPERTY, oldAuditLevel );
			}
		}
	}

	private void readFolders(Path destinationFolder, ILogger log) throws AonException {
		File[] folders = destinationFolder.toFile().listFiles( new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		int row = 0;
		for (File folder : folders) {
			((HTMLLogger) log).print("<div style=\""
					+ "margin-top: 5px;"
					+ "padding-left: 10px;"
					+ "border-top: solid black 1px;"
					+ "background-color:" + (row%2==0?"LightYellow":"AliceBlue")+";"
					+ "\">");
			log.info(MessageFormat.format("Leyendo la carpeta: {0}", folder.getName()));
			readFiles(folder, log);
			((HTMLLogger) log).print("</div>");
			++row;
		}
	}
	private DomainData searchDomain(Integer parentDomain,String domainName) throws ManagerBeanException {
		IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
		Criteria c = new Criteria();
		c.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID) , parentDomain);
		c.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_NAME) , domainName);
		List<ITransferObject> list = domainBean.getList(c);
		if (list == null || list.size() == 0 ) {
			return null;
		}
		Domain domain = (Domain) list.get(0);
		return new DomainData(
				domain.getId()
				, domain.getName()
				, domain.getDescription()
				, domain.getExpirationDate()
				, domain.isActive()
				, domain.isEnableHeredity());
	}
	
	private void readFiles(File folder, ILogger log) throws AonException {
		File[] enterpriseFiles = folder.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return ENTERPRISE_FILENAME.equalsIgnoreCase(AonStringUtils.trim(name));
			}
		});
		File[] files = folder.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !ENTERPRISE_FILENAME.equalsIgnoreCase(AonStringUtils.trim(name));
			}
		});
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
		Integer parentDomain = DomainManager.getCurrentDomain();
		String name = folder.getName();
		((HTMLLogger) log).print("<div style=\""
				+ "font-size: 1.4em;"
				+ "text-align: center;"
				+ "\">Dominio: " + name + "</div>");
		params.setScope(null);
		params.setWorkPlace(null);
		params.setCategory(null);
		params.setAccountPeriod(null);
		
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		String domainName = "";
		Connection connection = null;
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object servletRequest = ec.getRequest();
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpSession httpSession = request.getSession(false);
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			log.info("\tTRANSACTION BEGIN: Inicio punto transaccional.");
			Session session = HibernateUtil.getSession(sessionName);
			connection = session.connection();
			NewDomainControllerExt ndc = new  NewDomainControllerExt(connection,log);
			ndc.onInit(null);
			name = StringUtils.lowerCase(name);
			domainName = name + StringUtils.defaultString(ndc.getDomainSuffix());
			DomainData selected = searchDomain(parentDomain,domainName);
			if (selected != null) {
				log.info(MessageFormat.format("Encontrado dominio {0} en el entorno.",name));
				if (params.isIgnoreExistingDomains()) {
					
					try {
						HibernateUtil.rollbackTransaction(sessionName);
						log.info(MessageFormat.format("Ignorando la carga del dominio {0}.",name));
						log.info("\tTRANSACTION ROLLBACK: Final punto transaccional. (Ignorar dominio existente)");
						((HTMLLogger) log).print("<div style=\""
								+ "font-size: 1.4em;"
								+ "text-align: center;"
								+ "color: red;"
								+ "\">Dominio: " + domainName + " <b>NO</b> cargado correctamente! :(</div>");
					} catch (DAOException daoe) {
						log.error("Unable to rollback transaction!");
					}
					return;
				}
				httpSession.setAttribute( AuditManager.AUDIT_LEVEL_PROPERTY, AuditLevel.NONE );
				ds.select(selected.getId(), selected.getDescription());
				log.info("Dominio cambiado");
			} else {
				try {
					log.info(MessageFormat.format("Dominio {0} no encontrado. Se debe crear",name));
					ndc.setPassword(params.getPassword());
					ndc.setDomainName(name);
					ndc.setDomainDescription(name);
					ndc.setOwner( "" );					
					ndc.setEnableHeredity(true);  // Se crea el dominio vinculado al entorno
					ndc.onSave(null);
					DomainData newDomain = searchDomain(parentDomain,domainName);
					httpSession.setAttribute( AuditManager.AUDIT_LEVEL_PROPERTY, AuditLevel.NONE );
					ds.select(newDomain.getId(), newDomain.getDescription());
					log.info("Dominio cambiado");
				} catch (Throwable t) {
					log.error(t.getMessage());
					throw new AonException(t);
				}
			}
		
			LoaderParams params = new LoaderParams();
			IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
			List<ITransferObject> scopes = scopeBean.getList(null);
			if (scopes == null || scopes.size() == 0) {
				log.error("No se ha encontrado ningún ámbito válido");	
			}
			params.setScope((Scope) scopes.get(0));
			for (File file : enterpriseFiles) {
				log.info(MessageFormat.format("\tEncontrado fichero {0}",file.getName()));
				Loader loader = new Loader(params,log);
				InputStream input = new FileInputStream(file);
				loader.loadMetadata(input);
				input = new FileInputStream(file);
				loader.load(input, session);
			}
			for (File file : files) {
				log.info(MessageFormat.format("\tLeyendo el archivo: {0}", file.getName()));
				Loader loader = new Loader(params,log);
				InputStream input = new FileInputStream(file);
				loader.loadMetadata(input);
				input = new FileInputStream(file);
				loader.load(input, session);
			}
			HibernateUtil.commitTransaction(sessionName);
			log.info("\tTRANSACTION COMMIT: Final punto transaccional. Grabación en base de datos");
			((HTMLLogger) log).print("<div style=\""
					+ "font-size: 1.4em;"
					+ "text-align: center;"
					+ "color: green;"
					+ "\">Dominio: " + domainName + " cargado correctamente! ;)</div>");
			
		} catch (Exception e) {
			log.error(MessageFormat.format("Error durante la carga de datos. {0}",e.getMessage()));
			try {
				HibernateUtil.rollbackTransaction(sessionName);
				log.info("\tTRANSACTION ROLLBACK: Final punto transaccional. Se deshacen las inserciones realizadas.");
				((HTMLLogger) log).print("<div style=\""
						+ "font-size: 1.4em;"
						+ "text-align: center;"
						+ "color: red;"
						+ "\">Dominio: " + domainName + " <b>NO</b> cargado correctamente! :(</div>");
			} catch (DAOException daoe) {
				log.error("Unable to rollback transaction!");
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			if (parentDomain != DomainManager.getCurrentDomain()) {
				log.info("\tSe cambia a dominio padre");
				ds.onUpperDomain(null);
			}
			if (connection != null) {
				DatabaseUtil.closeQuietly(connection);
			}
			ds.setModel(null);
		}
	}
	
	private class NewDomainControllerExt extends NewDomainController {
		
		private static final long serialVersionUID = 6445028792097706679L;
		 
		//private static final String INSERT_DOMAIN_DEFAULTS_SCRIPT = 
		//		"com/code/aon/ui/loader/servlet/insert.database.aon.domain.sql";
		private static final String INSERT_DOMAIN_DEFAULTS_SCRIPT = 
				"com/code/aon/ui/loader/servlet/insert.database.aon.domain.from.parent.sql";
		
		private Connection connection;
		private ILogger log;
		
		public NewDomainControllerExt(Connection connection, ILogger log) {
			this.connection = connection;
			this.log = log;
		}
		
		public void onSave( ActionEvent event) {
			String domainFinalName = getDomainName() + StringUtils.defaultString(getDomainSuffix()); 
			try {
				DomainController.checkDomainName(domainFinalName, 3);
			} catch (AonException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage());			
			}		
			try {
				if ( existsDomainName(domainFinalName) ) {
					String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.DOMAIN_NAME_DUPLICATED, domainFinalName);
					throw new AbortProcessingException(message);			
				}			
			} catch ( ManagerBeanException e ) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage());			
			}
			
			validateUserPassword(getPassword());
			validateExpirationDate();
			
			try {			
				Domain newDomain = createDomain(domainFinalName);
				log.info(MessageFormat.format("Dominio {0} creado.",domainFinalName));
				URL script = VersionManager.getScript(INSERT_DOMAIN_DEFAULTS_SCRIPT);
				AonSQLFile file = new AonSQLFile(script.openStream(), CharEncoding.ISO_8859_1);
				file.setFileName(INSERT_DOMAIN_DEFAULTS_SCRIPT);
				AonSQLScriptExt sqlScript = new AonSQLScriptExt(file, connection);
				sqlScript.setDomain(newDomain.getId());
				log.info("Insertando valores por defecto ....");
				sqlScript.execute();
				log.info("Insertando customización ....");
				copyCustomizeId(newDomain);
				log.info("Insertando company ....");
				addCompany(newDomain);
				log.info("Guardando historial ....");				 
				saveHistory(newDomain);	 // Se guarda el historial y se envian los emails a AON y propietario		
			} catch (Throwable e) {
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}
	
	private class AonSQLScriptExt extends AonSQLScript {

		public AonSQLScriptExt(AonSQLFile file, Connection c) {
			super(file, c);
		}
		
		private void execute( String sql ) throws SQLException {
			Statement statement = null;
			try {
				statement = getConnection().createStatement();
				statement.executeUpdate(sql);
			} finally {
				DbUtils.closeQuietly(statement);
			}

		}
		public void execute() throws AonSQLException {
			try {
				if ( getDomain() != null ) {
					execute( "SET @Domain = " + getDomain() );
				}
				while (getFile().ready()) {
					String stmt = getFile().getStatement();
					if ( stmt != null ) {
						execute(stmt);
					}
				}
				getFile().close();
			} catch (SQLException e) {
				StringBuffer sw = new StringBuffer();
				if (!StringUtils.isEmpty( getFile().getFileName())) {
					sw.append(" File: ");
					File file = new File(getFile().getFileName());
					sw.append(file.getName());
				}
				sw.append(" Line: ").append( getFile().getLineNumber() ).append(": ");
				sw.append( e.getMessage() );
				throw new AonSQLException(sw.toString() , e);
			}
		}
		
	}
	
}
