package com.code.aon.document;

import static com.code.aon.document.IAlfrescoConstants.COMPANY_HOME_PATH;
import static org.alfresco.webservice.util.Constants.QUERY_LANG_LUCENE;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub;
import org.alfresco.webservice.classification.ClassificationServiceSoapBindingStub;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.document.dao.AlfrescoDAO;

public class BasicAlfresco {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoDAO.class);

	/** The store used throughout the samples */
	public static final Store STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
	
	private File ALFRESCO_PROPERTIES = new File("/home/COMMON-RESOURCES/aon-document/config.properties");

	private String serverURL;
	private String serverUserName;
	private String serverPassword;	
	
	public BasicAlfresco( String user, String password ) {
		this.serverUserName = user;
		this.serverPassword = password;
		setSessionProperties();
	}

	private void setSessionProperties() {
		Properties properties = PropertiesUtil
				.loadProperties(ALFRESCO_PROPERTIES);
		String serverHost = properties.getProperty("server.host");
		String serverPort = properties.getProperty("server.port");
		serverURL = "http://" + serverHost
				+ (!StringUtils.isEmpty(serverPort) ? ":" + serverPort : "")
				+ "/alfresco/api";
	}

	public void startSession() {
		try {
			LOGGER.debug("Connecting to: " + serverURL);
			WebServiceFactory.setEndpointAddress(serverURL);
			AuthenticationUtils.startSession(serverUserName, serverPassword);
		} catch (Exception e) {
			LOGGER.error("Can not initiate session with Alfresco server", e);
		}
	}

	public void endSession() {
		LOGGER.debug("Closing connection");
		AuthenticationUtils.endSession();
	}

	public static ParentReference getCompanyHome() {
		ParentReference companyHomeParent = new ParentReference(STORE, null,
				COMPANY_HOME_PATH, Constants.ASSOC_CONTAINS, null);
		return companyHomeParent;
	}

	protected RepositoryServiceSoapBindingStub getRepositoryService() {
		return WebServiceFactory.getRepositoryService();
	}

	protected ContentServiceSoapBindingStub getContentService() {
		return WebServiceFactory.getContentService();
	}

	protected ClassificationServiceSoapBindingStub getClassificationService() {
		return WebServiceFactory.getClassificationService();
	}

	protected AdministrationServiceSoapBindingStub getAdministrationService() {
		return WebServiceFactory.getAdministrationService();
	}

	protected AccessControlServiceSoapBindingStub getAccessControlService() {
		return WebServiceFactory.getAccessControlService();
	}
	
	public Reference getReference( String path ) {
		try {
			startSession();
	        Query query = new Query(QUERY_LANG_LUCENE, "PATH:\"" + path + "\"");
			QueryResult result = WebServiceFactory.getRepositoryService().query(STORE, query, true);
	        ResultSet rs = result.getResultSet();
	        if(rs.getTotalRowCount()>0) {
	        	ResultSetRowNode node = rs.getRows()[0].getNode();
		        return new Reference(STORE, node.getId(), path);		
	        }
		} catch ( Throwable e ) {
			LOGGER.error("Error getting reference of " + path, e);
		} finally {
			endSession();
		}
		return null;
	}
	
	public ParentReference getReferenceToParent(Reference  ref) {
		ParentReference parent = new ParentReference(STORE,
				ref.getUuid(), ref.getPath(), Constants.ASSOC_CONTAINS, null );
		return parent;
	}
	
	public byte[] getContent(Reference node) throws DAOException {
		byte[] data = null;
		try {
			startSession();
			Predicate predicate = new Predicate( new Reference[]{node}, STORE, null);
			Content[] contents = getContentService().read(predicate, Constants.PROP_CONTENT);
			if (! ArrayUtils.isEmpty(contents) ) {
				InputStream in = ContentUtils.getContentAsInputStream(contents[0]);
				data = IOUtils.toByteArray(in);
				in.close();		
			}
		} catch ( Throwable e ) {
			throw new DAOException( "Error in getContent " + node.getPath(), e );
		} finally {
			endSession();
		}
		return data;		
	}

	public static String formatId( String id ) {
		String _id = StringUtils.replace(id, ":", "\\:");
		_id = StringUtils.replace(_id, "{", "\\{");
		_id = StringUtils.replace(_id, "}", "\\}");	
		return _id;
	}

	public static boolean equals(Reference r1, Reference r2) {
		if (r1 == r2) return true;
		if (r1 != null && r2 != null) {
			return new EqualsBuilder()
				.append(r1.getUuid(), r2.getUuid())
				.append(r1.getStore(), r2.getStore())
				.isEquals();
		}
		return false;		
	}

	public static String getId( Reference reference ) {
		Store store = reference.getStore();
		String id = store.getScheme() + "\\://" + store.getAddress() + "/" + reference.getUuid();
		return id;
	}

	public String normalizeNodeName( String name ) {
		return StringUtils.replace( name, " ", "_" );
	}
	
	public Reference createSpace( ParentReference parent, String spaceName, String description ) throws DAOException {
		Reference spaceRef = null;
		try {
			startSession();
		
			// Asignamos un nombre para el nodo que vamos a crea en company_home
			String _name = normalizeNodeName(spaceName);
			parent.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, _name));
			
			// Comienza la construcción de nodo
			NamedValue[] contentProps = new NamedValue[2];
			contentProps[0] = Utils.createNamedValue(Constants.PROP_NAME, spaceName);
			contentProps[1] = Utils.createNamedValue(Constants.PROP_DESCRIPTION, description);
			CMLCreate create = new CMLCreate("1", parent, null, null,
					null, Constants.TYPE_FOLDER, contentProps);
	
			// Contruimos CML Block, con el nodo y sus aspectos
			CML cml = new CML();
			cml.setCreate(new CMLCreate[] { create });
	
			// Creamos y recuperamos el contenido vía Repository Web Service
			UpdateResult[] result = getRepositoryService().update(cml);
			spaceRef = result[0].getDestination();
		} catch ( Throwable e ) {
			throw new DAOException( "Error creating space " + spaceName, e );
		} finally {
			endSession();
		}
		return spaceRef;
	}	
	
	public void removeSpace(Reference reference) throws DAOException {
		Predicate predicate = new Predicate( new Reference[]{reference}, STORE, null);
		CMLDelete delete = new CMLDelete(predicate);
		CML cml = new CML();
		cml.setDelete(new CMLDelete[] { delete });
		try {
			startSession();
			getRepositoryService().update(cml);
		} catch ( Throwable e ) {
			throw new DAOException( "Error removing space " + reference.getPath(), e );
		} finally {
			endSession();
		}	
	}
	
}