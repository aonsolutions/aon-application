package com.code.aon.document.dao;

import static com.code.aon.document.IAlfrescoConstants.INSERT_ERROR;
import static com.code.aon.document.IAlfrescoConstants.REMOVE_ERROR;
import static com.code.aon.document.IAlfrescoConstants.TYPE_CONTENT;
import static com.code.aon.document.IAlfrescoConstants.UPDATE_ERROR;
import static com.code.aon.document.IAlfrescoConstants.UUID_SHORT;
import static org.alfresco.webservice.util.Constants.QUERY_LANG_LUCENE;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.alfresco.webservice.content.ContentFault;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.document.AlfrescoComparator;
import com.code.aon.document.AlfrescoRenderer;
import com.code.aon.document.BasicAlfresco;
import com.code.aon.document.IAlfrescoTransferObject;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * The Class LdapDAO.
 */
public abstract class AlfrescoDAO extends BasicAlfresco implements IDAO  {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoDAO.class);
	
    public static final String BASE_NAME = "com.code.aon.document.i18n.messages";
    
    private ResourceBundle bundle;

	private Class<? extends ITransferObject> pojoClass;
	
	private ParentReference parentReference;
	
	public abstract Object getValue( NamedValue value );
	
	protected abstract ITransferObject convert( NamedValue[] values ) throws DAOException;
	
	protected abstract NamedValue[] updateValues( ITransferObject to );
	
	protected NamedValue[] getInsertAdditionalValues( ITransferObject to ) {
		return null;
	}
	
	protected CMLAddAspect[] getAddAspects( ParentReference parent, ITransferObject to ) {
		return null;
	}
	
	protected void afterInsert( ITransferObject to ) throws Exception {
	}

	protected void afterUpdate( ITransferObject to ) throws Exception {
	}
	
	public AlfrescoDAO( Class<? extends ITransferObject> pojoClass, String user, String password ) {
		super( user, password );
		this.pojoClass = pojoClass;
		this.bundle = ResourceBundle.getBundle(BASE_NAME); 
	}

	public void setPath( String path ) {
		Reference reference = getReference(path);
		setParentReference( getReferenceToParent(reference) );		
	}

	public ParentReference getParentReference(ITransferObject to) {
		return parentReference;
	}
	
	public ParentReference getParentReference() {
		return parentReference;
	}

	public void setParentReference(ParentReference parentReference) {
		this.parentReference = parentReference;
	}

	@Override
	public Serializable getId(ITransferObject to) throws DAOException {
		IAlfrescoTransferObject ato = (IAlfrescoTransferObject) to;
		return ato.getId();
	}

	protected String getQueryPath() {
		return "TYPE:\"" + TYPE_CONTENT + "\" AND PATH:\"" + getParentReference().getPath() + "//*\"";
	}
	
	private String getQueryExpression( Criteria criteria ){
		String expression = getQueryPath(); 
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

	protected List<ResultSetRow> getList( String expression ) throws RepositoryFault, RemoteException {
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
			String message = getErrorMessage( "Error getting count of " + pojoClass, e );
			throw new DAOException( message, e );
		} finally {
			endSession();
		}
		return count;		
	}

	@Override
	public String getFieldName(String alias) throws DAOException {
		return StringUtils.replace(alias, "_", ":" );
	}
	
	protected String getType() {
		return Constants.TYPE_CONTENT;
	}
	
	private void addContent( ParentReference parent, IAlfrescoTransferObject ato ) throws ContentFault, RemoteException {

		// Asignamos un nombre para el nodo que vamos a crea en company_home
		String _name = normalizeNodeName(ato.getName());
		parent.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, _name));
		
		// Comienza la construcción de nodo
		NamedValue[] contentProps = new NamedValue[2];
		contentProps[0] = Utils.createNamedValue(Constants.PROP_NAME, ato.getName());
		contentProps[1] = Utils.createNamedValue(Constants.PROP_DESCRIPTION, ato.getDescription());
		contentProps = (NamedValue[]) ArrayUtils.addAll(contentProps, getInsertAdditionalValues(ato));
		CMLCreate create = new CMLCreate("1", parent, null, null,
				null, getType(), contentProps);

		// Contruimos CML Block, con el nodo y sus aspectos
		CML cml = new CML();
		cml.setCreate(new CMLCreate[] { create });
		CMLAddAspect[] aspects = getAddAspects(parent, ato);
		if (! ArrayUtils.isEmpty(aspects) ) {
			cml.setAddAspect(aspects);	
		}

		// Creamos y recuperamos el contenido vía Repository Web Service
		UpdateResult[] result = getRepositoryService().update(cml);
		Reference content = result[0].getDestination();
		
		ato.setId( content );
	}
	
	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
		try {
			startSession();
			setCloseSession(false);
			ParentReference parent = getParentReference( to );
			addContent(parent, (IAlfrescoTransferObject) to);
			afterInsert(to);
		} catch ( Throwable e ) {
			String message = getErrorMessage(to, e, INSERT_ERROR);
			throw new DAOException( message, e );			
		} finally {
			setCloseSession(true);
			endSession();
		}
		return to;
	}
	
	protected Predicate getPredicate( ITransferObject to ) {
		IAlfrescoTransferObject ad = (IAlfrescoTransferObject) to;
		return getPredicate( ad.getId() );		
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
			String message = getErrorMessage(to, e, REMOVE_ERROR);
			throw new DAOException( message, e );
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
	
	protected List<ITransferObject> convertList( List<ResultSetRow> list ) throws DAOException {
		List<ITransferObject> tos = new ArrayList<ITransferObject>();
		for( ResultSetRow row : list ) {
			ITransferObject to = convert(row.getColumns());
			tos.add(to);
		}
		return tos;
	}	
	
	protected List<ResultSetRow> sortList( List<ResultSetRow> list, Criteria criteria ) {
		if ( (criteria != null) && (criteria.getOrderByList() != null) ) {
			List<Order> orderList = criteria.getOrderByList().getOrders();
			for( int i = orderList.size()-1; i >= 0; i-- ) {
				Order order = orderList.get(i);
				String attribute = order.getExpression().getName();
				AlfrescoComparator comparator = new AlfrescoComparator( this, attribute, order.isAscending() );
				LOGGER.debug( "Sorting {}", order );
				Collections.sort( list, comparator );
			}
		}
		return list;
	}

	protected void afterList() {
	}
	
	@Override
	public List<ITransferObject> getList(Criteria criteria, int offset,
			int count) throws DAOException {
		List<ITransferObject> tos = null;
		try {
			startSession();
			setCloseSession(false);
			String expression = getQueryExpression(criteria);
			LOGGER.debug( "getList, expression={}", expression );			
			List<ResultSetRow> list = getList(expression);
			list = sortList(list, criteria);
			list = getSubList(list, offset, count);
			tos = convertList(list);
		} catch ( Throwable e ) {
			throw new DAOException( "Error in getList of " + pojoClass, e );
		} finally {
			afterList();
			setCloseSession(true);
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
	
	private Predicate getPredicate(Serializable pk ) {
		if ( pk instanceof Reference ) {
			Reference reference = (Reference) pk;
			return new Predicate( new Reference[]{reference}, STORE, null);			
		}
		String id =  "@" + BasicAlfresco.formatId(UUID_SHORT);
		Query query = new Query(QUERY_LANG_LUCENE, id + ":" + pk.toString() );
		return new Predicate( null, STORE, query);
	}
	
	@Override
	public ITransferObject get(Serializable pk) throws DAOException {
		try {
			startSession();
			setCloseSession(false);
			Predicate predicate = getPredicate(pk);
			Node[] nodes = getRepositoryService().get(predicate);
			if (! ArrayUtils.isEmpty(nodes) ) {
				ITransferObject to = convert(nodes[0].getProperties());
				return to;
			}
		} catch ( Throwable e ) {
			throw new DAOException( "Error in get of " + pojoClass );
		} finally {
			setCloseSession(true);
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

			afterUpdate(to);		
		} catch ( Throwable e ) {
			String message = getErrorMessage(to, e, UPDATE_ERROR);
			throw new DAOException( message, e );			
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
	public List<?> getList(ProjectionList projectionList, Criteria criteria)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Object getUniqueResult(Projection projection, Criteria criteria)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}
	
	@Override
	public Object getUniqueResult(ProjectionList projectionList,
			Criteria criteria) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Class<? extends ITransferObject> getPOJOClass() {
		return this.pojoClass;
	}

	protected String getErrorMessage( ITransferObject to, Throwable t, String key  ) {
		String name = ((IAlfrescoTransferObject)to).getName();
		String errorMessage = t.getMessage();
		if ( StringUtils.isBlank(errorMessage) ) {
			if ( t instanceof RepositoryFault ) {
				errorMessage = StringUtils.substringAfter( ((RepositoryFault) t).getMessage1(), ": " );
			}
		}
		return MessageFormat.format(bundle.getString(key), name, errorMessage);
	}

	private String getErrorMessage( String message, Throwable t ) {
		String errorMessage = t.getMessage();
		if ( StringUtils.isBlank(errorMessage) ) {
			if ( t instanceof RepositoryFault ) {
				errorMessage = StringUtils.substringAfterLast( ((RepositoryFault) t).getMessage1(), ": " );
			}
		}
		return message + ". " + errorMessage;
	}
	
}
