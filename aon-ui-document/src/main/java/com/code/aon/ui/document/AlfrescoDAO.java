package com.code.aon.ui.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentFault;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * The Class LdapDAO.
 */
public abstract class AlfrescoDAO implements IDAO  {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoDAO.class);

	/** The store used throughout the samples */
	public static final Store STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
	
	public static final String UUID = "{" + Constants.NAMESPACE_SYSTEM_MODEL + "}node-uuid";
	
	public static final String PATH = "{" + Constants.NAMESPACE_CONTENT_MODEL + "}path";

	private File ALFRESCO_PROPERTIES = new File("/home/COMMON-RESOURCES/aon-document/config.properties");

	private Class<? extends ITransferObject> pojoClass;
	
	private String serverURL;
	private String serverUserName;
	private String serverPassword;	
	
	protected abstract ParentReference getParentReference();
	
	protected abstract ITransferObject convert( NamedValue[] values );
	
	protected abstract NamedValue[] insertValues( ITransferObject to );
	
	protected abstract NamedValue[] updateValues( ITransferObject to );
	
	protected abstract CMLAddAspect getAddAspect(ITransferObject to );
	
	public AlfrescoDAO( Class<? extends ITransferObject> pojoClass, String user, String password ) {
		this.pojoClass = pojoClass;
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
			LOGGER.info("Connecting to: " + serverURL);
			WebServiceFactory.setEndpointAddress(serverURL);
			AuthenticationUtils.startSession(serverUserName, serverPassword);
		} catch (Exception e) {
			LOGGER.error("Can not initiate session with Alfresco server", e);
		}
	}

	public void endSession() {
		LOGGER.info("Closing connection");
		AuthenticationUtils.endSession();
	}

	protected ParentReference getCompanyHome() {
		ParentReference companyHomeParent = new ParentReference(STORE, null,
				"/app:company_home", Constants.ASSOC_CONTAINS, null);
		return companyHomeParent;
	}

	private RepositoryServiceSoapBindingStub getRepositoryService() {
		return WebServiceFactory.getRepositoryService();
	}

	private ContentServiceSoapBindingStub getContentService() {
		return WebServiceFactory.getContentService();
	}
	
	private String getQueryExpression( Criteria criteria ){
		String expression = "TYPE:\"cm:content\""; 
		if ( criteria != null ) { 
			AlfrescoRenderer renderer = new AlfrescoRenderer();
			renderer.visitCriteria(criteria);
			String filter = renderer.getExpression();
			if (! StringUtils.isEmpty(filter) ) {
				expression += " AND " + filter;	
			}
		}
		return expression;
	}	
	
	private int getCount( String expression ) throws RepositoryFault, RemoteException {
		Query query = new Query( Constants.QUERY_LANG_LUCENE, expression );  
		QueryResult result = getRepositoryService().query(STORE, query, false);
		int count = ArrayUtils.getLength( result.getResultSet().getRows() );
		if ( (count > 0) && (! StringUtils.isEmpty(result.getQuerySession())) ) {
			QueryResult result2 = getRepositoryService().fetchMore(result.getQuerySession());
			count += ArrayUtils.getLength( result2.getResultSet().getRows() );			
		}
		return count;
	}

	private List<ResultSetRow> getList( String expression ) throws RepositoryFault, RemoteException {
		List<ResultSetRow> list = new LinkedList<ResultSetRow>();
		Query query = new Query( Constants.QUERY_LANG_LUCENE, expression );  
		QueryResult result = getRepositoryService().query(STORE, query, false);
		if ( result.getResultSet().getRows() != null ) {
			for( ResultSetRow row : result.getResultSet().getRows() ) {
				list.add(row);
			}
			if ( ! StringUtils.isEmpty(result.getQuerySession()) ) {
				QueryResult result2 = getRepositoryService().fetchMore(result.getQuerySession());
				for( ResultSetRow row : result2.getResultSet().getRows() ) {
					list.add(row);
				}		
			}			
		}
		return list;
	}
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		int count = 0;
		try {
			startSession();
			String expression = getQueryExpression(criteria);
			LOGGER.debug( "getCount, expression={}", expression );
			count = getCount(expression);
		} catch ( Throwable e ) {
			throw new DAOException( "Error getting count of " + pojoClass, e );
		} finally {
			endSession();
		}
		return count;		
	}

	@Override
	public String getFieldName(String alias) throws DAOException {
		return alias;
	}
	
	protected ParentReference getReferenceToParent(Reference spaceref) {
		ParentReference parent = new ParentReference();

		parent.setStore(STORE);
		parent.setPath(spaceref.getPath());
		parent.setUuid(spaceref.getUuid());
		parent.setAssociationType(Constants.ASSOC_CONTAINS);

		return parent;
	}
	
	private void addContent( ParentReference parent, NamedValue[] values, CMLAddAspect aspect,
			IAlfrescoTransferObject ato ) throws ContentFault, RemoteException {

		// Asignamos un nombre para el nodo que vamos a crea en company_home
		String _name = StringUtils.replace(ato.getName(), " ", "_");
		parent.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, _name));
		
		// Comienza la construcción de nodo
		NamedValue[] contentProps = new NamedValue[1];
		contentProps[0] = Utils.createNamedValue(Constants.PROP_NAME, ato.getName());
		CMLCreate create = new CMLCreate("1", parent, null, null,
				null, Constants.TYPE_CONTENT, contentProps);

		// Añadimos aspectos al nodo
		CMLAddAspect addAspect = new CMLAddAspect(Constants.ASPECT_TITLED,
				values, null, "1");
		
		// Contruimos CML Block, con el nodo y sus aspectos
		CML cml = new CML();
		cml.setCreate(new CMLCreate[] { create });
		cml.setAddAspect(new CMLAddAspect[] {addAspect, aspect});

		// Creamos y recuperamos el contenido vía Repository Web Service
		UpdateResult[] result = getRepositoryService().update(cml);
		Reference content = result[0].getDestination();

		// Escribimos el contenido
		ContentFormat contentFormat = new ContentFormat(ato.getMimeType().getName(), "UTF-8");
		LOGGER.info("Setting the content of the document");
		getContentService().write(content, Constants.PROP_CONTENT, ato.getData(), contentFormat);
		
		ato.setId( content );
	}
	
	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
		try {
			startSession();
			IAlfrescoTransferObject ato = (IAlfrescoTransferObject) to;
			ParentReference parent = getParentReference();
			CMLAddAspect aspect = getAddAspect( ato );
			NamedValue[] values = insertValues(ato);
			addContent(parent, values, aspect, ato);
		} catch ( Throwable e ) {
			throw new DAOException( "Error in insert of " + pojoClass, e );
		} finally {
			endSession();
		}
		return to;
	}
	
	private Predicate getPredicate( ITransferObject to ) {
		IAlfrescoTransferObject ato = (IAlfrescoTransferObject) to;
		return new Predicate( new Reference[]{ato.getId()}, STORE, null);		
	}
	
	@Override
	public boolean remove(ITransferObject to) throws DAOException {
		boolean removed = false;
		Predicate predicate = getPredicate(to);
		CMLDelete delete = new CMLDelete(predicate);
		CML cml = new CML();
		cml.setDelete(new CMLDelete[] { delete });

		try {
			startSession();
			getRepositoryService().update(cml);
			removed = true;
		} catch ( Throwable e ) {
			throw new DAOException( "Error in remove of " + pojoClass, e );
		} finally {
			endSession();
		}	
		return removed;
	}
	
	private List<ResultSetRow> getSubList( List<ResultSetRow> list, int offset, int count ) {
		if ( (offset >= 0) && (count >= 0) ) {
			int toIndex = Math.min( offset+count, list.size() );
			LOGGER.debug( "SubList, offset={},toIndex={}", offset, toIndex );
			return list.subList( offset, toIndex );	
		}
		return list;
	}
	
	private List<ITransferObject> convertList( List<ResultSetRow> list ) throws DAOException {
		List<ITransferObject> tos = new ArrayList<ITransferObject>();
		for( ResultSetRow row : list ) {
			ITransferObject to = convert(row.getColumns());
			tos.add(to);
		}
		return tos;
	}	
	
	@Override
	public List<ITransferObject> getList(Criteria criteria, int offset,
			int count) throws DAOException {
		List<ITransferObject> tos = null;
		try {
			startSession();
			String expression = getQueryExpression(criteria);
			LOGGER.debug( "getList, expression={}", expression );
			List<ResultSetRow> list = getList(expression);
			// list = sortList(list, criteria);
			list = getSubList(list, offset, count);
			tos = convertList(list);
		} catch ( Throwable e ) {
			throw new DAOException( "Error in getList of " + pojoClass, e );
		} finally {
			endSession();
		}
		return tos;			
	}

	@Override
	public List<ITransferObject> getList(Criteria criteria) throws DAOException {
		return getList(criteria, -1, -1);
	}
	
	@Override
	public void setId(ITransferObject to, Serializable id) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");		
	}
	
	@Override
	public ITransferObject get(Serializable pk) throws DAOException {
		Reference reference = (Reference) pk;
		Predicate predicate = new Predicate( new Reference[]{reference}, STORE, null);
		try {
			startSession();
			Node[] nodes = getRepositoryService().get(predicate);
			if (! ArrayUtils.isEmpty(nodes) ) {
				ITransferObject to = convert(nodes[0].getProperties());
				return to;
			}
		} catch ( Throwable e ) {
			throw new DAOException( "Error in get of " + pojoClass );
		} finally {
			endSession();
		}
		return null;		
	}
	
	@Override
	public ITransferObject update(ITransferObject to) throws DAOException {
		try {
			startSession();
			Predicate predicate = getPredicate(to);
			NamedValue[] values = updateValues(to);
			CMLUpdate update = new CMLUpdate(values, predicate, null);
			CML cml = new CML();
			cml.setUpdate(new CMLUpdate[] { update });

			getRepositoryService().update(cml);
		} catch ( Throwable e ) {
			throw new DAOException( "Error in update of " + pojoClass, e );
		} finally {
			endSession();
		}
		return to;
	}

	@Override
	public ITransferObject insertOrUpdate(ITransferObject to)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");		
	}

	@Override
	public ITransferObject replicate(ITransferObject to, ReplicationMode mode)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void setProperty(ITransferObject to, String propertyName,
			Object value) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}
	
	@Override
	public List getList(ProjectionList projectionList, Criteria criteria)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Object getUniqueResult(Projection projection, Criteria criteria)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Class getPOJOClass() {
		return this.pojoClass;
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
			throw new DAOException( "Error in getContent " + pojoClass, e );
		} finally {
			endSession();
		}
		return data;		
	}

}
