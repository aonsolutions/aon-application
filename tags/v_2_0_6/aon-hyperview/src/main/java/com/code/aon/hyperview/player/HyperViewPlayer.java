package com.code.aon.hyperview.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.code.aon.common.tree.enumeration.TreeNodeType;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.hyperview.HyperViewException;
import com.code.aon.hyperview.data.ConnectionProviderException;
import com.code.aon.hyperview.data.ConnectionProviderFactoryManager;
import com.code.aon.hyperview.data.IConnectionProvider;
import com.code.aon.hyperview.data.IConnectionProviderFactory;
import com.code.aon.hyperview.enumeration.DataType;
import com.code.aon.hyperview.model.HyperView;
import com.code.aon.hyperview.model.HyperViewAttribute;
import com.code.aon.hyperview.model.HyperViewCompositeView;
import com.code.aon.hyperview.model.HyperViewContents;
import com.code.aon.hyperview.model.HyperViewFormSQLNode;
import com.code.aon.hyperview.model.HyperViewImageContent;
import com.code.aon.hyperview.model.HyperViewLinkContent;
import com.code.aon.hyperview.model.HyperViewListSQLNode;
import com.code.aon.hyperview.model.HyperViewNode;
import com.code.aon.hyperview.model.HyperViewParameter;
import com.code.aon.hyperview.model.HyperViewSQLNode;
import com.code.aon.hyperview.model.IHyperViewContent;
import com.code.aon.hyperview.renderer.HyperViewRendererException;
import com.code.aon.hyperview.renderer.HyperViewRendererUtils;

public class HyperViewPlayer {

	/**
	 * La constante SEPARATOR ha de ser igual que TreeNode.Separator de Myfaces,
	 * no se hace referencia a ella para evitar dependencias en el proyecto.
	 * Pero a la hora de pintar el árbol, si se utiliza la via MyFaces, hay que
	 * tenerlo en cuenta.
	 */
	private static final String SEPARATOR = ":";

	/** Se obtiene el Logger adecuado */
	// private static final Logger LOGGER =
	// Logger.getLogger(HyperViewPlayer.class.getName());
	private boolean recursive = true;

	private boolean emptyNodePrintable = true;

	private HyperView hyperView;

	private IHyperViewPlayerNodeFactory nodeFactory;

	private DefaultTreeModel defTreeModel;

	private Map<String, ContextItem> globalParams = new HashMap<String, ContextItem>();

	private VelocityHelper vh;

	private ResourceBundle bundle;

	private HyperViewRendererUtils rendererUtils;

	private Connection connection;

	private Properties connectionProperties;

	public HyperView getHyperView() {
		return hyperView;
	}

	public void setHyperView(HyperView hyperView) {
		this.hyperView = hyperView;
	}

	public void setConnectionProperties(Properties props) {
		this.connectionProperties = props;
	}

	private Properties getConnectionProperties() {
		return this.connectionProperties;
	}

	public String getHyperViewLabel() throws Exception {
		String label = null;
		if (getHyperView() != null) {
			label = getHyperView().getLabel();
			if (label != null && label.indexOf('$') > -1) {
				label = velocityParse(getParams(), label);
			}
		}
		return label;
	}

	public String getHyperViewDescription() throws Exception {
		String description = null;
		if (getHyperView() != null) {
			description = getHyperView().getDescription();
			if (description != null && description.indexOf('$') > -1) {
				description = velocityParse(getParams(), description);
			}
		}
		return description;
	}

	public void setMessageBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public DateFormat getDateFormatter() {
		return getHyperViewRendererUtils().getDateFormatter();
	}

	public NumberFormat getDecimalFormatter() {
		return getHyperViewRendererUtils().getDecimalFormatter();
	}

	public NumberFormat getIntegerFormatter() {
		return getHyperViewRendererUtils().getIntegerFormatter();
	}

	public DateFormat getTimeFormatter() {
		return getHyperViewRendererUtils().getTimeFormatter();
	}

	public DateFormat getTimestampFormatter() {
		return getHyperViewRendererUtils().getTimestampFormatter();
	}

	public IHyperViewPlayerNodeFactory getHyperViewNodeFactory() {
		return nodeFactory;
	}

	public void setHyperViewNodeFactory(IHyperViewPlayerNodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
	}

	public boolean isEmptyNodePrintable() {
		return emptyNodePrintable;
	}

	public void setEmptyNodePrintable(boolean emptyNodePrintable) {
		this.emptyNodePrintable = emptyNodePrintable;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	private List executeDynaNode(HyperViewNode wtree,
			Map<String, ContextItem> params) throws HyperViewException {
		HyperViewSQLNode dynaNode = wtree.getDynamicNode();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getQueryFilled(dynaNode, params);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			LinkedList<Object[]> list = new LinkedList<Object[]>();
			int limit = getHyperView().getDynamicNodesPerPage();
			int count = 1;
			while (rs.next()) {
				Object[] o = getObjectArray(rs, columns);
				list.add(o);
				count++;
				if (limit > 0 && count > limit) {
					break;
				}
			}
			return list;
		} catch (Throwable e) {
			throw new HyperViewException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// Nothing
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// Nothing
				}
			}
		}
	}

	public void deployNode(IHyperViewPlayerNode treeNode)
			throws HyperViewException {
		DefaultMutableTreeNode defNode = treeNode.getDefinitionNode();
		if (defNode.getChildCount() > 0) {
			fillModel((DefaultMutableTreeNode) defNode.getFirstChild(),
					treeNode, null);
		}
		treeNode.setDeployed(true);
	}

	public void executeNode(IHyperViewPlayerNode treeNode)
			throws HyperViewException {
		HyperViewNode node = treeNode.getHyperViewNode();
		HyperViewContents contents = node.getContents();
		for (IHyperViewContent content : contents.getContents()) {
			resolveContents(treeNode, content);
		}
	}

	private void resolveContents(IHyperViewPlayerNode treeNode,
			IHyperViewContent content) throws HyperViewException {
		if (content instanceof HyperViewFormSQLNode) {
			resolveFormContent(treeNode, (HyperViewFormSQLNode) content);
		} else if (content instanceof HyperViewListSQLNode) {
			resolveListContent(treeNode, (HyperViewListSQLNode) content);
		}
		if (content instanceof HyperViewLinkContent) {
			resolveLinkContent(treeNode, (HyperViewLinkContent) content);
		} else if (content instanceof HyperViewImageContent) {
			resolveImageContent(treeNode, (HyperViewImageContent) content);
		} else if (content instanceof HyperViewCompositeView) {
			HyperViewContents contents = ((HyperViewCompositeView) content)
					.getContents();
			for (IHyperViewContent c : contents.getContents()) {
				resolveContents(treeNode, c);
			}
		}
	}

	public void resolveLinkContent(IHyperViewPlayerNode treeNode,
			HyperViewLinkContent content) throws HyperViewException {
		try {
			Map<String, ContextItem> map = new HashMap<String, ContextItem>(
					treeNode.getParams());
			String pattern = content.getLinkUrlPattern();
			String linkUrl = velocityParse(map, pattern);
			content.setLinkUrl( linkUrl );
		} catch (Throwable e) {
			throw new HyperViewException(e.getMessage(), e);
		}
	}

	public void resolveImageContent(IHyperViewPlayerNode treeNode,
			HyperViewImageContent content) throws HyperViewException {
		try {
			Map<String, ContextItem> map = new HashMap<String, ContextItem>(
					treeNode.getParams());
			String pattern = content.getImageUrlPattern();
			String imagePath = velocityParse(map, pattern);
			if (imagePath != null) {
				if (!imagePath.startsWith("http") && !imagePath.startsWith("/")) {
					imagePath = "/" + imagePath + ".hyperviewIcon";
				}
			}
			content.setImageUrl(imagePath);
		} catch (Throwable e) {
			throw new HyperViewException(e.getMessage(), e);
		}
	}

	public void resolveFormContent(IHyperViewPlayerNode treeNode,
			HyperViewFormSQLNode content) throws HyperViewException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getQueryFilled(content, treeNode.getParams());
			ps.setMaxRows(1);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			if (rs.next()) {
				ViewResult result = new ViewResult();
				Object[] values = null;
				if (columns == 1) {
					values = new Object[] { rs.getObject(1) };
				} else {
					values = getObjectArray(rs, columns);
				}
				result.setValues(values);
				String url = content.getLinkUrl();
				if (url != null && url.indexOf("$") > 0) {
					result.setLinkLabel(content.getLinkLabel());
					Map<String, ContextItem> map = new HashMap<String, ContextItem>(
							treeNode.getParams());
					List<HyperViewAttribute> attrs = content.getMetadata();
					fillMapWithExportedAttributes(map, attrs, values);
					result.setLinkUrl(velocityParse(map, content.getLinkUrl()));
				}
				content.setResult(result);
			}
		} catch (Throwable e) {
			throw new HyperViewException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// Nothing
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// Nothing
				}
			}
		}
	}

	public void resolveListContent(IHyperViewPlayerNode treeNode,
			HyperViewListSQLNode content) throws HyperViewException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getQueryFilled(content, treeNode.getParams());
			List<ViewResult> queryResult = new LinkedList<ViewResult>();
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			Object[] values = null;
			int limit = getHyperView().getListLinesPerPage();
			int count = 1;
			while (rs.next()) {
				if (columns == 1) {
					values = new Object[] { rs.getObject(1) };
				} else {
					values = getObjectArray(rs, columns);
				}
				ViewResult result = new ViewResult();
				result.setValues(values);
				String url = content.getLinkUrl();
				if (url != null && url.indexOf("$") > 0) {
					result.setLinkLabel(content.getLinkLabel());
					Map<String, ContextItem> map = new HashMap<String, ContextItem>(
							treeNode.getParams());
					List<HyperViewAttribute> attrs = content.getMetadata();
					fillMapWithExportedAttributes(map, attrs, values);
					result.setLinkUrl(velocityParse(map, content.getLinkUrl()));
				}
				queryResult.add(result);
				count++;
				if (limit > 0 && count > limit) {
					break;
				}
			}
			content.setQueryResult(queryResult);
		} catch (Throwable e) {
			throw new HyperViewException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// Nothing
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// Nothing
				}
			}
		}
	}

	private Object[] getObjectArray(ResultSet rs, int columns)
			throws SQLException {
		LinkedList<Object> ll = new LinkedList<Object>();
		for (int i = 1; i < (columns + 1); i++) {
			ll.add(rs.getObject(i));
		}
		return ll.toArray();
	}

	private PreparedStatement getQueryFilled(HyperViewSQLNode node,
			Map<String, ContextItem> params) throws HyperViewException {
		try {
			String statement = node.getSentence();
			statement = statement.replace('\r', ' ');
			statement = statement.replace('\n', ' ');
			statement = statement.replace('\t', ' ');
			PreparedStatement query = getConnection(getConnectionProperties())
					.prepareStatement(statement,
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			List<HyperViewParameter> variables = node.getParameters();
			if (variables != null) {
				for (int i = 1; i < (variables.size() + 1); i++) {
					HyperViewParameter param = variables.get(i - 1);
					String variable = param.getName();
					ContextItem item = params.get(variable);
					if (item == null) {
						item = globalParams.get(variable);
					}
					if (item == null) {
						throw new HyperViewException("No value for '"
								+ variable + "' host variable!");
					}
					String value = item.getValue();
					if (item.getType() == DataType.STRING) {
						query.setString(i, value);
					}
					if (item.getType() == DataType.INTEGER) {
						query.setInt(i, Integer.parseInt(value));
					}
					if (item.getType() == DataType.DOUBLE) {
						query.setDouble(i, Double.parseDouble(value));
					}
					if (item.getType() == DataType.BOOLEAN) {
						query.setBoolean(i, Boolean.getBoolean(value));
					}
					if (item.getType() == DataType.DATE) {
						try {
							Date date = getDateFormatter().parse(value);
							java.sql.Date sqlDate = new java.sql.Date(date
									.getTime());
							query.setDate(i, sqlDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if (item.getType() == DataType.TIME) {
						try {
							Date date = getTimeFormatter().parse(value);
							Time sqlTime = new Time(date.getTime());
							query.setTime(i, sqlTime);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if (item.getType() == DataType.TIMESTAMP) {
						try {
							Date date = getTimestampFormatter().parse(value);
							Timestamp sqlTimestamp = new Timestamp(date
									.getTime());
							query.setTimestamp(i, sqlTimestamp);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return query;
		} catch (Throwable e) {
			throw new HyperViewException(e.getMessage(), e);
		}
	}

	public void fillModel(DefaultMutableTreeNode defNode,
			IHyperViewPlayerNode parent, IHyperViewPlayerNode rootNode)
			throws HyperViewException {
		HyperViewNode wtree = (HyperViewNode) defNode.getUserObject();
		if (wtree.isDynamic()) {
			createDynamicNodes(defNode, parent, rootNode);
		} else {
			createStaticNode(defNode, parent, rootNode);
		}
		boolean hyperCube = (parent != null) ? parent.isHyperCube() : false;
		if (!hyperCube && defNode.getNextSibling() != null) {
			fillModel(defNode.getNextSibling(), parent, rootNode);
		}
	}

	private void createStaticNode(DefaultMutableTreeNode defNode,
			IHyperViewPlayerNode parent, IHyperViewPlayerNode rootNode)
			throws HyperViewException {
		IHyperViewPlayerNode childNode;
		if (parent == null) {
			childNode = rootNode;
		} else {
			childNode = createNode(parent, defNode);
		}
		if (parent != null) {
			childNode.getParams().putAll(parent.getParams());
		}
		parseLabel(childNode);
		childNode.setDeployed(true);
		if (defNode.getChildCount() > 0) {
			fillModel((DefaultMutableTreeNode) defNode.getFirstChild(),
					childNode, rootNode);
		}
	}

	private void createDynamicNodes(DefaultMutableTreeNode defNode,
			IHyperViewPlayerNode parent, IHyperViewPlayerNode rootNode)
			throws HyperViewException {
		HyperViewNode xmlNode = (HyperViewNode) defNode.getUserObject();
		if (xmlNode.getDynamicNode() == null
				|| xmlNode.getDynamicNode().getSentence() == null) {
			throw new HyperViewException("No dynamic sentence specified for "
					+ xmlNode.getLabel() + " node!.");
		}
		List sqlResult = executeDynaNode(xmlNode, parent.getParams());
		Iterator iter = sqlResult.iterator();
		int i = 0;
		while (iter.hasNext()) {
			/*
			 * if (i == 20) { IHyperViewPlayerNode childNode =
			 * createNode(parent, defNode);
			 * childNode.setDescription(bundle.getString(AON_MORE_KEY)); break; }
			 */
			Object[] values = (Object[]) iter.next();
			IHyperViewPlayerNode childNode = createNode(parent, defNode);
			childNode.setDeployed(true);
			exportAttributes(childNode, values);
			parseLabel(childNode);
			i++;
			if (defNode.getChildCount() > 0) {
				fillModel((DefaultMutableTreeNode) defNode.getFirstChild(),
						childNode, rootNode);
			}
		}
		parent.setDeployed(true);
	}

	private VelocityHelper getVelocityHelper() throws Exception {
		if (vh == null) {
			vh = new VelocityHelper();
			vh.init();
		}
		return vh;
	}

	@SuppressWarnings("unchecked")
	private IHyperViewPlayerNode createNode(IHyperViewPlayerNode parent,
			DefaultMutableTreeNode defNode) {
		StringBuffer identifier = new StringBuffer();
		identifier.append(parent.getIdentifier());
		identifier.append(SEPARATOR);
		identifier.append(parent.getChildCount());
		IHyperViewPlayerNodeFactory factory = getHyperViewNodeFactory();
		IHyperViewPlayerNode node = factory.newTreeNode(identifier.toString(),
				defNode);
		node.setDeployed(false);
		node.setParent(parent);
		List children = parent.getChildren();
		children.add(node);
		if (parent != null) {
			node.getParams().putAll(parent.getParams());
		}
		return node;
	}

	private void exportAttributes(IHyperViewPlayerNode treeNode, Object[] values) {
		List<HyperViewAttribute> attrs = treeNode.getHyperViewNode()
				.getDynamicNode().getMetadata();
		Map<String, ContextItem> ctx = treeNode.getParams();
		fillMapWithExportedAttributes(ctx, attrs, values);
	}

	private void fillMapWithExportedAttributes(Map<String, ContextItem> ctx,
			List<HyperViewAttribute> attrs, Object[] values) {
		for (HyperViewAttribute attr : attrs) {
			if (attr.isAccessible()) {
				Object value = values[attr.getPosition()];
				String v = value == null ? null : value.toString();
				ContextItem item = new ContextItem(attr.getName());
				item.setType(attr.getDataType());
				item.setValue(v);
				String fv;
				try {
					fv = getHyperViewRendererUtils().getValue(attr, value);
				} catch (HyperViewRendererException e) {
					fv = v;
				}
				item.setFormattedValue(fv);
				ctx.put(item.getKey(), item);
			}
		}
	}

	private void parseLabel(IHyperViewPlayerNode treeNode) {
		try {
			String description = treeNode.getDescription();
			if (description != null && description.indexOf('$') > -1) {
				String parsed = velocityParse(treeNode.getParams(), description);
				treeNode.setDescription(parsed);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public String velocityParse(Map initialContext, String text)
			throws Exception {
		VelocityHelper vHelper = getVelocityHelper();
		Map<String, Object> velocityMap = getVelocityMap(initialContext);
		TemplateHelper th = vHelper.getTemplateHelper(velocityMap);
		return th.processTemplate(text, "go!");
	}

	private Map<String, Object> getVelocityMap(Map<String, ContextItem> params) {
		Map<String, Object> map = new HashMap<String, Object>(getParams());
		Set<String> keys = params.keySet();
		for (String key : keys) {
			ContextItem item = params.get(key);
			String value = null;
			if (item != null) {
				value = item.toString();
			}
			map.put(key, value);
		}
		return map;
	}

	public Map<String, ContextItem> getParams() {
		return globalParams;
	}

	public void setParams(Map<String, ContextItem> params) {
		this.globalParams = params;
	}
	
	public void setBuiltInParameters(Map<String, ContextItem> builtInParameters) {
		this.globalParams.putAll(  builtInParameters );
	}

	public void addParam(ContextItem param) {
		this.globalParams.put(param.getKey(), param);
	}

	public javax.swing.tree.TreeModel getDefinitionTreeModel() {
		if (defTreeModel == null) {
			HyperViewNode root = getHyperView().getRoot();
			root.setType(TreeNodeType.ROOT);
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
			defTreeModel = new DefaultTreeModel(rootNode);
			if (root.getChildren() != null && !root.getChildren().isEmpty()) {
				fillDefTreeModel(rootNode);
			}
		}
		return defTreeModel;
	}

	private void fillDefTreeModel(DefaultMutableTreeNode parent) {
		HyperViewNode node = (HyperViewNode) parent.getUserObject();
		List<HyperViewNode> children = node.getChildren();
		for (HyperViewNode child : children) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
			parent.add(childNode);
			if (child.getChildren() != null && !child.getChildren().isEmpty()) {
				fillDefTreeModel(childNode);
			}
		}
	}

	public HyperViewRendererUtils getHyperViewRendererUtils() {
		if (rendererUtils == null) {
			rendererUtils = new HyperViewRendererUtils(bundle);
		}
		return rendererUtils;
	}

	private Connection getConnection(Properties props)
			throws ConnectionProviderException {
		if (connection == null) {
			IConnectionProviderFactory factory = ConnectionProviderFactoryManager
					.getConnectionProviderFactory(props);
			IConnectionProvider provider = factory.getConnectionProvider(props);
			connection = provider.getConnection();
		}
		return connection;
	}

	public IHyperViewPlayerNode getHyperCubePreviousNode(
			IHyperViewPlayerNode parent, IHyperViewPlayerNode node)
			throws HyperViewException {
		removeChildren(parent);
		DefaultMutableTreeNode defNode = node.getDefinitionNode();
		DefaultMutableTreeNode previous = getHypercubePreviousNode(defNode);
		fillModel(previous, parent, null);
		return (IHyperViewPlayerNode) parent.getChildren().get(0);
	}

	public DefaultMutableTreeNode getHypercubePreviousNode(
			DefaultMutableTreeNode defNode) {
		DefaultMutableTreeNode previous = defNode.getPreviousSibling();
		if (previous == null) {
			int count = defNode.getSiblingCount();
			previous = (DefaultMutableTreeNode) defNode.getParent().getChildAt(
					count - 1);
		}
		return previous;
	}

	public IHyperViewPlayerNode getHyperCubeNextNode(
			IHyperViewPlayerNode parent, IHyperViewPlayerNode node)
			throws HyperViewException {
		removeChildren(parent);
		DefaultMutableTreeNode defNode = node.getDefinitionNode();
		DefaultMutableTreeNode next = getHypercubeNextNode(defNode);
		fillModel(next, parent, null);
		return (IHyperViewPlayerNode) parent.getChildren().get(0);
	}

	public String getHyperCubeNextLabel(IHyperViewPlayerNode node)
			throws Exception {
		DefaultMutableTreeNode defNode = node.getDefinitionNode();
		DefaultMutableTreeNode next = getHypercubeNextNode(defNode);
		HyperViewNode wtree = (HyperViewNode) next.getUserObject();
		String description = wtree.getLabel();
		if (description != null && description.indexOf('$') > -1) {
			description = velocityParse(node.getParams(), description);
		}
		return description;
	}

	public String getHyperCubePreviousLabel(IHyperViewPlayerNode node)
			throws Exception {
		DefaultMutableTreeNode defNode = node.getDefinitionNode();
		DefaultMutableTreeNode previous = getHypercubePreviousNode(defNode);
		HyperViewNode wtree = (HyperViewNode) previous.getUserObject();
		String description = wtree.getLabel();
		if (description != null && description.indexOf('$') > -1) {
			description = velocityParse(node.getParams(), description);
		}
		return description;
	}

	public DefaultMutableTreeNode getHypercubeNextNode(
			DefaultMutableTreeNode defNode) {
		DefaultMutableTreeNode next = defNode.getNextSibling();
		if (next == null) {
			next = (DefaultMutableTreeNode) defNode.getParent().getChildAt(0);
		}
		return next;
	}

	private void removeChildren(IHyperViewPlayerNode node) {
		node.getChildren().clear();
	}


}
