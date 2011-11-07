package com.code.aon.document;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.webservice.classification.ClassificationServiceSoapBindingStub;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.document.dao.AlfrescoDAO;

public class BasicAlfresco {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoDAO.class);

	/** The store used throughout the samples */
	public static final Store STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

	public static final String UUID = "{" + Constants.NAMESPACE_SYSTEM_MODEL + "}node-uuid";
	
	public static final String PATH = "{" + Constants.NAMESPACE_CONTENT_MODEL + "}path";
	
	public static final String CATEGORIES = "{" + Constants.NAMESPACE_CONTENT_MODEL + "}categories";
	
	public static final String MIME_TYPE = Constants.PROP_CONTENT + ".mimetype";
	
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

	public ParentReference getCompanyHome() {
		ParentReference companyHomeParent = new ParentReference(STORE, null,
				"/app:company_home", Constants.ASSOC_CONTAINS, null);
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
	
	protected ParentReference getReferenceToParent(Reference spaceref, String path ) {
		ParentReference parent = new ParentReference();

		parent.setStore(STORE);
		parent.setPath(spaceref.getPath() + "/" + path);
		parent.setUuid(spaceref.getUuid());
		parent.setAssociationType(Constants.ASSOC_CONTAINS);

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
	
}
