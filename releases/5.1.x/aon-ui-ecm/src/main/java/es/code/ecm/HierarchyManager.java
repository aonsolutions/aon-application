package es.code.ecm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.jcr.query.Query;

import org.apache.jackrabbit.core.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;

import es.code.ecm.event.PermissionEvent;
import es.code.ecm.event.PermissionListener;
import es.code.ecm.nodes.Category;
import es.code.ecm.nodes.Folder;
import es.code.ecm.nodes.User;
import es.code.ecm.security.AccessManagerImpl;
import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclObjectIdentity;
import es.code.ecm.security.acl.core.AclProviderImpl;
import es.code.ecm.security.acl.core.AclProviderManager;
import es.code.ecm.security.acl.core.NodeEntry;
import es.code.ecm.security.acl.core.NodeObjectIdentity;
import es.code.ecm.security.acl.core.NodePermission;
import es.code.ecm.util.ECMUtil;

public class HierarchyManager implements PermissionListener {

	/** HierarchyManager Logger. */
	private static Logger log = LoggerFactory.getLogger(HierarchyManager.class);
	private static final String LDAP_ORGANIZATION_NAME_ATTRIBUTE = "o";

	/**
	 * root of this hierarchy
	 */
	private Node rootNode;

	/**
	 * workspace for this hierarchy
	 */
	private Workspace workspace;

	/**
	 * user who created this hierarchy
	 */
	private String userId;

	/**
	 * access manager for this hierarchy
	 */
	protected AccessManagerImpl accessManager;

	/**
	 * query manager for this hierarchy
	 */
	private QueryManager queryManager;

	/**
	 * constructor
	 */
	public HierarchyManager() {
	   this.userId = "anonymous";
	}

	public HierarchyManager(String userId) {
		this.userId = userId;
	}

	@Override
	public void permissionAdded(PermissionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void permissionChanged(PermissionEvent event) {
		AclProviderManager aclManager = (AclProviderManager) ContentRepository.getAclManager();
		AclObjectIdentity aclObjectIdentity = 
			new NodeObjectIdentity( (String) event.getSource(), (String) event.getSource() );
		try {
			Map<String, List<AclEntry>> entries = getAclEntries( "//element(*," + aclObjectIdentity.getNodeType() + ")" );
			if ( entries.size() > 0 ) {
				aclManager.registerChangedAclEntries( workspace.getName(), aclObjectIdentity, entries.get( aclObjectIdentity.getNodeType() ) );
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	/**
    * initialize hierarchy manager and sets default workspace
    * 
    * @param rootNode
    * @throws PathNotFoundException
    * @throws RepositoryException
    */
	public void init(Node rootNode) 
				throws PathNotFoundException, RepositoryException {
		this.rootNode = rootNode;
//TODO Look for another way to retrieve Authenticated user roles.
		SessionImpl session = (SessionImpl) this.rootNode.getSession();
		this.workspace = session.getWorkspace();
		this.accessManager = (AccessManagerImpl) session.getAccessManager();
// Register root Group node.
		Node groupNode = null;
		String groupRelPath = this.workspace.getName() + "_" + ECMQName.AON_GROUP_SUFFIX;
		try {
			groupNode = this.rootNode.getNode( groupRelPath );
		} catch (PathNotFoundException e) {
			String groupNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_GROUP );
			groupNode = this.rootNode.addNode( groupRelPath, groupNodeTypeName );
			this.rootNode.save();
		}
		User user = null;
		NodeIterator ni = groupNode.getNodes( userId );
// New logged users are registered.
		if ( ni.getSize() == 0 ) { 
			user = new User( groupNode.addNode( userId, User.getNodeType() ) );
			user.setLogin( this.accessManager.getPrincipal().getShortName() );
			Entry entry = ECMUtil.getAonUser( this.workspace.getName(), userId );
	    	String userName = entry.getAsString( ILdapConstants.COMMON_NAME_ATTRIBUTE );
	    	if ( entry.containsKey( ILdapConstants.SURNAME_ATTRIBUTE ) ) {
	    		userName += " " + entry.getAsString( ILdapConstants.SURNAME_ATTRIBUTE );
	    	}
			user.setName( userName );
	    	if ( entry.containsKey( LDAP_ORGANIZATION_NAME_ATTRIBUTE ) ) {
	    		user.setDomain( entry.getAsString( LDAP_ORGANIZATION_NAME_ATTRIBUTE ) );
	    	}    	
			user.setRoles( this.accessManager.getPrincipalRoles() );
			groupNode.save();
		}
// Register root Folder node.
		String folderRootRelPath = this.workspace.getName() + "_" + ECMQName.AON_FOLDER_SUFFIX;
		try {
			this.rootNode.getNode( folderRootRelPath );
		} catch (PathNotFoundException e) {
			String primaryNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_FOLDER );
			Folder folder = new Folder( this.rootNode.addNode( folderRootRelPath, primaryNodeTypeName ) );
			folder.setName( this.workspace.getName() );
			folder.setUser( user.getNode().getUUID() );
			this.rootNode.save();
		}
// Register root Category node.
		String categoryRelPath = this.workspace.getName() + "_" + ECMQName.AON_CATEGORY_SUFFIX;
		try {
			this.rootNode.getNode( categoryRelPath );
		} catch (PathNotFoundException e) {
			Category category = 
				new Category( this.rootNode.addNode( categoryRelPath, Category.getNodeName() ) );
			category.fillName( this.workspace.getName() );
			category.setUser( user.getNode().getUUID() );
			this.rootNode.save();
		}
		AclProviderManager aclManager = (AclProviderManager) ContentRepository.getAclManager();
		registerAclEntries( aclManager, "//element(*,mix:accesscontrol)" );
   }

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the rootNode
	 */
	public Node getRootNode() {
		return rootNode;
	}

	/**
    * gets currently used workspace for this hierarchy manager
    */
   public Workspace getWorkspace() {
       return this.workspace;
   }

   /**
    * Set query manager for this hierarchy
    * @param queryManager
    */
   public void setQueryManager(QueryManager queryManager) {
       this.queryManager = queryManager;
   }

   public QueryManager getQueryManager() {
       return this.queryManager;
   }

   /**
    * Persists all changes to the repository if validation succeds
    * @throws RepositoryException
    */
   public void save() throws RepositoryException {
       try {
           this.rootNode.getSession().save();
       }
       catch (RepositoryException re) {
           log.error(re.getMessage(), re);
           throw re;
       }
   }

   /**
    * Returns true if the session has pending (unsaved) changes.
    */
   public boolean hasPendingChanges() throws RepositoryException {
       return this.rootNode.getSession().hasPendingChanges();
   }

   /**
    * Refreshes this session
    * @param keepChanges
    * @throws RepositoryException
    * @see javax.jcr.Session#refresh(boolean)
    */
   public void refresh(boolean keepChanges) throws RepositoryException {
       this.workspace.getSession().refresh(keepChanges);
   }

	/**
	 * Caching workspace ACL entries.
	 * 
	 * @param aclManager
	 * @param statement
	 * @throws RepositoryException
	 */
   private void registerAclEntries(AclProviderManager aclManager, String statement) throws RepositoryException {
		AclProviderImpl aclProvider = new AclProviderImpl( workspace.getName(), aclManager.getCache() );
		Map<String, List<AclEntry>> entries = getAclEntries( statement );
		if ( entries.size() > 0 ) {
			Iterator<String> iter = entries.keySet().iterator();
			while (iter.hasNext()) {
				String elem = iter.next();
				aclProvider.getAclEntryCache().putEntriesInCache( entries.get( elem ), elem );
			}
		}
		aclManager.add( aclProvider );
	}

	private Map<String, List<AclEntry>> getAclEntries(String statement) throws RepositoryException {
		String userNodeTypeName = User.getNodeType();
		Map<String, List<AclEntry>> entries = new HashMap<String, List<AclEntry>>();
		Query q = workspace.getQueryManager().createQuery( statement, Query.XPATH );
		NodeIterator ni = q.execute().getNodes();
		while (ni.hasNext()) {
			Node elem = ni.nextNode();
			if ( elem.isNodeType( IConstants.MIX_REFERENCEABLE ) ) {
				Long accessLevel = 
					elem.getProperty( ContentRepository.getNodeName( ECMQName.AON_LEVEL ) ).getLong();
				AclEntry entry = new NodeEntry();
				String uuid = null;
				if ( elem.isNodeType( userNodeTypeName ) ) {
					uuid  = elem.getProperty( ContentRepository.getNodeName( ECMQName.AON_LOGIN ) ).getString();
				} else {
					uuid = elem.getUUID();
				}
				String nodeType = elem.getPrimaryNodeType().getName();
				AclObjectIdentity aclObjectIdentity = new NodeObjectIdentity( nodeType, uuid );
				entry.setAclObjectIdentity( aclObjectIdentity );
				entry.setAclPermission( new NodePermission( accessLevel ) );
				List<AclEntry> l = entries.get( nodeType );
				if ( l == null ) {
					l = new ArrayList<AclEntry>();
					l.add( entry );
					entries.put( nodeType, l );
				} else {
					l.add( entry );
				}
			}
		}
		return entries;
	}
// /**
// * move content to the specified location
// * @param source source node path
// * @param destination node where the node has to be moved
// * @throws javax.jcr.PathNotFoundException
// * @throws javax.jcr.RepositoryException
// */
//public void moveTo(String source, String destination) throws PathNotFoundException, RepositoryException,
//    AccessDeniedException {
//    Access.isGranted(this.accessManager, source, Permission.REMOVE);
//    Access.isGranted(this.accessManager, destination, Permission.WRITE);
//    this.workSpace.move(source, destination);
//}
//
///**
// * copy content to the specified location
// * @param source source node path
// * @param destination node where the node has to be copied
// * @throws javax.jcr.PathNotFoundException
// * @throws javax.jcr.RepositoryException
// */
//public void copyTo(String source, String destination) throws PathNotFoundException, RepositoryException,
//    AccessDeniedException {
//    Access.isGranted(this.accessManager, source, Permission.READ);
//    Access.isGranted(this.accessManager, destination, Permission.WRITE);
//    this.workSpace.copy(source, destination);
//}
//   private String getNodePath(String path, String label) {
//       if (StringUtils.isEmpty(path) || (path.equals("/"))) { //$NON-NLS-1$
//           return label;
//       }
//       return getNodePath(path + "/" + label); //$NON-NLS-1$
//   }
//
//   private String getNodePath(String path) {
//       if (path != null && path.startsWith("/")) { //$NON-NLS-1$
//           return path.replaceFirst("/", StringUtils.EMPTY); //$NON-NLS-1$
//       }
//       return path;
//   }
//
//   /**
//    * Like getContent() but creates the node if not yet existing. Attention save is not called!
//    * @param path the path of the node
//    * @param create true if the node should get created
//    * @param type the node type of the created node
//    * @return the node
//    * @throws AccessDeniedException
//    * @throws RepositoryException
//    */
//   public Content getContent(String path, boolean create, ItemType type) throws AccessDeniedException,
//       RepositoryException {
//       Content node;
//       try {
//           node = getContent(path);
//       }
//       catch (PathNotFoundException e) {
//           if (create) {
//               node = this.createContent(StringUtils.substringBeforeLast(path, "/"), StringUtils.substringAfterLast(
//                   path,
//                   "/"), type.toString());
//           }
//           else {
//               throw e;
//           }
//       }
//       return node;
//   }
//
//   /**
//    * get content node object of the requested URI
//    * @param path of the content (container / containerlist) to be initialized
//    * @return ContentNode
//    * @throws javax.jcr.PathNotFoundException
//    * @throws javax.jcr.RepositoryException
//    * @deprecated use getContent(String path) instead
//    */
//   public Content getContentNode(String path) throws PathNotFoundException, RepositoryException, AccessDeniedException {
//       return new Content(this.rootNode, getNodePath(path), this.accessManager);
//   }
//
//   /**
//    * get NodeData object of the requested URI
//    * @param path of the atom to be initialized
//    * @return NodeData
//    * @throws javax.jcr.PathNotFoundException
//    * @throws javax.jcr.RepositoryException
//    */
//   public NodeData getNodeData(String path) throws PathNotFoundException, RepositoryException, AccessDeniedException {
//       String nodePath = getNodePath(path);
//       if (StringUtils.isEmpty(nodePath)) {
//           return null;
//       }
//
//       return new NodeData(this.rootNode, nodePath, this.accessManager);
//   }
//
//   /**
//    * returns the first page with a given template name that is found in tree that starts from the page given py the
//    * path (including this page)
//    * @param path handle of the page from where the search should start
//    * @param templateName template name to search for
//    * @return first Content hierarchy node that has the specified template name assigned
//    * @throws javax.jcr.PathNotFoundException
//    * @throws javax.jcr.RepositoryException
//    */
//   public Content getPage(String path, String templateName) throws PathNotFoundException, RepositoryException,
//       AccessDeniedException {
//       Content page = getContent(path);
//       if (page.getTemplate().equals(templateName)) {
//           return page;
//       }
//       Content pageToBeFound = null;
//       try {
//           if (page.hasChildren()) {
//               Collection children = page.getChildren(ItemType.CONTENT.getSystemName());
//               Iterator iterator = children.iterator();
//               while (iterator.hasNext()) {
//                   Content child = (Content) iterator.next();
//                   if (child.getTemplate().equals(templateName)) {
//                       return child;
//                   }
//                   if (child.hasChildren()) {
//                       pageToBeFound = getPage(child.getHandle(), templateName);
//                   }
//                   if (pageToBeFound != null) {
//                       return pageToBeFound;
//                   }
//               }
//           }
//       }
//       catch (Exception e) {
//           log.error("Failed to get - " + path); //$NON-NLS-1$
//           log.error(e.getMessage(), e);
//       }
//       return pageToBeFound;
//   }
//
//   /**
//    * removes specified path, it can be either node or property
//    * @param path to be removed
//    * @throws javax.jcr.PathNotFoundException
//    * @throws javax.jcr.RepositoryException
//    * @throws AccessDeniedException
//    */
//   public void delete(String path) throws PathNotFoundException, RepositoryException, AccessDeniedException {
//       if (this.isNodeData(path)) {
//           this.getNodeData(makeRelative(path)).delete();
//       }
//       else {
//           this.rootNode.getNode(makeRelative(path)).remove();
//       }
//
//   }
//
//   private String makeRelative(String path) {
//       return StringUtils.stripStart(path, "/"); //$NON-NLS-1$
//   }
//
//   /**
//    * @return startPage of the current working repository-workspace
//    */
//   public Content getRoot() throws RepositoryException, AccessDeniedException {
//       return (new Content(this.rootNode, this.accessManager));
//   }
//
//   /**
//    * Checks if the requested resource is a page (hierarchy Node).
//    * @param path of the requested content
//    * @return boolean true is the requested content is a Hierarchy Node todo remove this method, instead use
//    * (getContent(PATH) is NodeType)
//    */
//   public boolean isPage(String path) throws AccessDeniedException {
//       Access.isGranted(this.accessManager, path, Permission.READ);
//
//       String nodePath = getNodePath(path);
//       if (StringUtils.isEmpty(nodePath)) {
//           return false;
//       }
//
//       try {
//           Node n = this.rootNode.getNode(nodePath);
//           return (n.isNodeType(ItemType.CONTENT.getSystemName()));
//       }
//       catch (RepositoryException re) {
//           // ignore, not existing?
//       }
//       return false;
//   }
//
//   /**
//    * check is either the node or property exists with the specified path
//    * @param path
//    */
//   public boolean isExist(String path) {
//       try {
//           Access.isGranted(this.accessManager, path, Permission.READ);
//       }
//       catch (AccessDeniedException e) {
//           log.error(e.getMessage());
//           return false;
//       }
//       boolean isExist = false;
//       try {
//           isExist = this.workSpace.getSession().itemExists(path);
//       }
//       catch (RepositoryException re) {
//           log.error("Exception caught", re);
//       }
//       return isExist;
//   }
//
//   /**
//    * Evaluate primary node type of the node at the given path.
//    */
//   public boolean isNodeType(String path, String type) {
//       try {
//           Node n = this.rootNode.getNode(getNodePath(path));
//           return n.isNodeType(type);
//       }
//       catch (RepositoryException re) {
//           log.error(re.getMessage());
//           log.debug(re.getMessage(), re);
//       }
//       return false;
//   }
//
//   /**
//    * Evaluate primary node type of the node at the given path.
//    */
//   public boolean isNodeType(String path, ItemType type) {
//       return isNodeType(path, type.getSystemName());
//   }
//
//   /**
//    * checks if the requested resource is an NodeData (Property)
//    * @param path of the requested NodeData
//    * @return boolean true is the requested content is an NodeData
//    */
//   public boolean isNodeData(String path) throws AccessDeniedException {
//       Access.isGranted(this.accessManager, path, Permission.READ);
//       boolean result = false;
//       String nodePath = getNodePath(path);
//       if (StringUtils.isEmpty(nodePath)) {
//           return false;
//       }
//       try {
//           result = this.rootNode.hasProperty(nodePath);
//           if (!result) {
//               // check if its a nt:resource
//               result = this.rootNode.hasProperty(nodePath + "/" + ItemType.JCR_DATA);
//           }
//       }
//       catch (RepositoryException e) {
//           // ignore, no property
//       }
//       return result;
//   }
//
//   /**
//    * This method can be used to retrieve Content which has UUID assigned to it, in other words only those nodes which
//    * has mixin type mix:referenceable.
//    * @param uuid
//    */
//   public Content getContentByUUID(String uuid) throws ItemNotFoundException, RepositoryException,
//       AccessDeniedException {
//       return new Content(this.rootNode.getSession().getNodeByUUID(uuid), this.accessManager);
//   }
}
