package com.code.aon.ui.hyperview.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.myfaces.custom.tree2.TreeModel;

import com.code.aon.common.tree.ITreeNodeTO;
import com.code.aon.hyperview.HyperViewException;
import com.code.aon.hyperview.enumeration.DataType;
import com.code.aon.hyperview.enumeration.LayoutType;
import com.code.aon.hyperview.model.HyperView;
import com.code.aon.hyperview.model.HyperViewContents;
import com.code.aon.hyperview.model.HyperViewLayout;
import com.code.aon.hyperview.model.HyperViewNode;
import com.code.aon.hyperview.model.HyperViewParameter;
import com.code.aon.hyperview.player.ContextItem;
import com.code.aon.hyperview.player.HyperViewPlayer;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;
import com.code.aon.hyperview.player.IHyperViewPlayerNodeFactory;
import com.code.aon.ui.form.tree.controller.AbstractTreeController;
import com.code.aon.ui.form.tree.controller.AonTreeException;
import com.code.aon.ui.form.tree.controller.TreeNode;
import com.code.aon.ui.hyperview.controller.myfaces.MyFacesHyperViewPlayerNode;
import com.code.aon.ui.hyperview.controller.myfaces.MyFacesHyperViewPlayerNodeFactory;
import com.code.aon.ui.util.AonUtil;

public class HyperViewRunnableController extends AbstractTreeController {

	private boolean hidden;

	private HyperView hyperView;

	private ListDataModel parametersModel;

	private HyperViewPlayer player;

	private Properties connectionProperties;

	private static final String PATH_SEPARATOR = " \u00BB ";

	private static final String TODAY = "today";
	private static final String CURRENT_YEAR = "currentYear";
	private static final String CURRENT_MONTH = "currentMonth";
	private static final String CURRENT_YEAR_FIRST_DAY = "currentYearFirstDay";
	private static final String CURRENT_YEAR_LAST_DAY = "currentYearLastDay";
	private static final String CURRENT_MONTH_FIRST_DAY= "currentMonthFirstDay";
	private static final String CURRENT_MONTH_LAST_DAY= "currentMonthLastDay";
	
	private static final String ZERO = "0";
	private static final String EMPTY_STRING = "";
	private static final String REQUEST_CONTEXT_PATH = "REQUEST_CONTEXT_PATH";
	private static final String REQUEST_CONTEXT_FULL_PATH = "REQUEST_CONTEXT_FULL_PATH";
	
	private static final List<Object> UNIQUE_ROW_LIST = new LinkedList<Object>();
	
	static {
		if (UNIQUE_ROW_LIST.isEmpty()) {
			UNIQUE_ROW_LIST.add( new Object() );	
		}
		
	}

	public HyperView getHyperView() {
		return hyperView;
	}

	public String getHyperViewLabel() {
		try {
			if(player == null){ // not set yet
				return EMPTY_STRING;
			}
			return player.getHyperViewLabel();
		} catch (Exception e) {
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public String getHyperViewDescription() {
		try {
			if(player == null){ // not set yet
				return EMPTY_STRING;
			}
			return player.getHyperViewDescription();
		} catch (Exception e) {
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void setHyperView(HyperView hyperView) {
		if (this.hyperView != hyperView) {
			this.hyperView = hyperView;
			parametersModel = null;
			player = new HyperViewPlayer();
			player.setConnectionProperties(this.connectionProperties);
			FacesContext ctx = FacesContext.getCurrentInstance();
			Application app = ctx.getApplication();
			String baseName = app.getMessageBundle();
			Locale locale = ctx.getViewRoot().getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
			player.setMessageBundle(bundle);
			player.setHyperView(hyperView);
			IHyperViewPlayerNodeFactory factory = new MyFacesHyperViewPlayerNodeFactory();
			player.setHyperViewNodeFactory(factory);
		}
	}

	public void setConnectionProperties(Properties props) {
		this.connectionProperties = props;
	}

	public HyperViewPlayer getPlayer() {
		return player;
	}

	protected TreeModel initializeTreeModel() throws AonTreeException {
		try {
			clearNodesMap();
			javax.swing.tree.TreeModel defModel = player
					.getDefinitionTreeModel();
			DefaultMutableTreeNode defRoot = (DefaultMutableTreeNode) defModel
					.getRoot();
			MyFacesHyperViewPlayerNode rootNode = new MyFacesHyperViewPlayerNode(
					ZERO, defRoot);
			createTreeModel(rootNode);
			rootNode.getParams().putAll(player.getParams());
			player.fillModel(defRoot, null, rootNode);
			setCurrentNode(getRootNode());
			if (!getRootNode().isDeployed()) {
				player.deployNode((IHyperViewPlayerNode) getRootNode());
			}
			executeTreeNode((IHyperViewPlayerNode) getRootNode());
			selectRootNode();
			return treeModel;
		} catch (HyperViewException e) {
			throw new AonTreeException(e.getMessage(), e);
		}
	}

	public DataModel getParametersModel() throws Exception {
		if (parametersModel == null) {
			List<HyperViewParameter> params = getParameters();
			if (params == null) {
				params = new LinkedList<HyperViewParameter>();
			}
			parseDefaultValues(params);
			parametersModel = new ListDataModel(params);
		}
		return parametersModel;
	}

	@SuppressWarnings("unchecked")
	private void parseDefaultValues(List<HyperViewParameter> params)
			throws Exception {
		for (HyperViewParameter param : params) {
			if (param.getDefaultValue() != null) {
				Map map = new HashMap();
				ContextItem ci = new ContextItem(TODAY);
				Date today = new Date();
				ci.setValue(player.getDateFormatter().format(today));
				map.put(TODAY, ci);
				Calendar gc = GregorianCalendar.getInstance();
				gc.setTime( today );
				
				int currentYear = gc.get( Calendar.YEAR ); 
				ContextItem ci0 = new ContextItem(CURRENT_YEAR);
				ci0.setValue(Integer.toString(currentYear));
				map.put(CURRENT_YEAR, ci0);
				
				int currentMonth = gc.get( Calendar.MONTH ); 
				ContextItem ci10 = new ContextItem(CURRENT_MONTH);
				ci10.setValue(Integer.toString(currentMonth + 1));
				map.put(CURRENT_MONTH, ci10);
				
				ContextItem ci1 = new ContextItem(CURRENT_YEAR_FIRST_DAY);
				gc.set(Calendar.DAY_OF_MONTH , 1);
				gc.set(Calendar.MONTH , 0);
				gc.set(Calendar.YEAR , currentYear);
				Date yearFirstDay = gc.getTime();
				ci1.setValue(player.getDateFormatter().format(yearFirstDay));
				map.put(CURRENT_YEAR_FIRST_DAY, ci1);
				
				ContextItem ci2 = new ContextItem(CURRENT_YEAR_LAST_DAY);
				gc.set(Calendar.DAY_OF_MONTH , 31);
				gc.set(Calendar.MONTH , 11);
				gc.set(Calendar.YEAR , currentYear);
				Date yearLastDay = gc.getTime();
				ci2.setValue(player.getDateFormatter().format(yearLastDay));
				map.put(CURRENT_YEAR_LAST_DAY, ci2);
				
				ContextItem ci12 = new ContextItem(CURRENT_MONTH_FIRST_DAY);
				gc.set(Calendar.DAY_OF_MONTH , 1);
				gc.set(Calendar.MONTH , currentMonth);
				gc.set(Calendar.YEAR , currentYear);
				Date monthFirstDay = gc.getTime();
				ci12.setValue(player.getDateFormatter().format(monthFirstDay));
				map.put(CURRENT_MONTH_FIRST_DAY, ci12);
				
				ContextItem ci13 = new ContextItem(CURRENT_MONTH_LAST_DAY);
				gc.set(Calendar.DAY_OF_MONTH , gc.getActualMaximum(Calendar.DAY_OF_MONTH));
				gc.set(Calendar.MONTH , currentMonth);
				gc.set(Calendar.YEAR , currentYear);
				Date monthLastDay = gc.getTime();
				ci13.setValue(player.getDateFormatter().format(monthLastDay));
				map.put(CURRENT_MONTH_LAST_DAY, ci13);

				String parsed = player.velocityParse(map, param
						.getDefaultValue());
				param.setDefaultValue(parsed);
			}
		}
	}

	private List<HyperViewParameter> getParameters() {
		List<HyperViewParameter> parameters = getHyperView().getParameters();
		return parameters;
	}

	@SuppressWarnings("unused")
	public void onRun(ActionEvent event) {
		try {
			initializeController();
			List requestedParameters = (List) getParametersModel()
					.getWrappedData();
			Map<String, ContextItem> params = fillParameters(requestedParameters);
			player.setParams(params);
			player.setBuiltInParameters(getBuiltInParameters());
		} catch (Throwable e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private Map<String, ContextItem> getBuiltInParameters() {
		Map<String, ContextItem> params = new HashMap<String, ContextItem>();

		ContextItem item = new ContextItem(REQUEST_CONTEXT_PATH);
		item.setType(DataType.STRING);
		String context = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestContextPath();
		item.setValue(context);
		params.put(item.getKey(), item);

		ContextItem item1 = new ContextItem(REQUEST_CONTEXT_FULL_PATH);
		ExternalContext ec = FacesContext.getCurrentInstance()
				.getExternalContext();
		if (ec.getRequest() instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) ec.getRequest();
			String scheme = req.getScheme();
			String serverName = req.getServerName();
			int serverPort = req.getServerPort();
			String path = scheme + "://" + serverName + ":" + serverPort
					+ context;
			item1.setType(DataType.STRING);
			item1.setValue(path);
		}
		params.put(item1.getKey(), item1);

		return params;
	}

	protected void setTo(ITreeNodeTO to) {
	}

	private Map<String, ContextItem> fillParameters(List parameters) {
		Map<String, ContextItem> params = new HashMap<String, ContextItem>();
		if (parameters != null && !parameters.isEmpty()) {
			Iterator iter = parameters.iterator();
			while (iter.hasNext()) {
				HyperViewParameter param = (HyperViewParameter) iter.next();
				ContextItem item = new ContextItem(param.getName());
				item.setType(param.getDataType());
				item.setValue(param.getValue());
				params.put(item.getKey(), item);
			}
		}
		return params;
	}

	@Override
	public void onNodeSelected(ActionEvent event) {
		try {
			super.onNodeSelected(event);
			runNodeSelected();
		} catch (Throwable e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void runNodeSelected() throws HyperViewException {
		IHyperViewPlayerNode node = (IHyperViewPlayerNode) getCurrentNode();
		boolean wasDeployed = node.isDeployed();
		if (!node.isDeployed()) {
			player.deployNode(node);
		}
		executeTreeNode(node);
		if (!wasDeployed && node.getChildCount() > 0) {
			expandNode((TreeNode) node);
		}
	}

	public List getUniqueRowList() {
		return UNIQUE_ROW_LIST;
	}

	public List getContentsModel() {
		IHyperViewPlayerNode playerNode = (IHyperViewPlayerNode) getCurrentNode();
		HyperViewNode node = playerNode.getHyperViewNode();
		if (node != null) {
			HyperViewContents contents = node.getContents();
			if (contents != null) {
				return contents.getContents();
			}
		}
		return null;
	}

	public int getContentsCount() {
		List list = getContentsModel();
		return (list == null) ? 0 : list.size();
	}

	public boolean isColumnLayoutContent() {
		IHyperViewPlayerNode playerNode = (IHyperViewPlayerNode) getCurrentNode();
		HyperViewNode node = playerNode.getHyperViewNode();
		boolean ret = false;
		if (node != null) {
			HyperViewLayout layout = node.getLayout();
			ret = (layout.getType() == LayoutType.COLUMN);
		}
		return ret;
	}

	public boolean isRowLayoutContent() {
		IHyperViewPlayerNode playerNode = (IHyperViewPlayerNode) getCurrentNode();
		HyperViewNode node = playerNode.getHyperViewNode();
		boolean ret = false;
		if (node != null) {
			HyperViewLayout layout = node.getLayout();
			ret = (layout.getType() == LayoutType.ROW);
		}
		return ret;
	}

	private void executeTreeNode(IHyperViewPlayerNode node) {
		try {
			HyperViewContents contents = node.getHyperViewNode().getContents();
			if (contents != null && contents.getContents() != null) {
				player.executeNode(node);
			}
		} catch (HyperViewException e) {
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public String getCurrentNodeLabel() {
		TreeNode treeNode = getCurrentNode();
		return (treeNode != null) ? treeNode.getDescription() : null;
	}

	public String getCurrentNodePathToRoot() {
		StringBuffer buf = new StringBuffer();
		IHyperViewPlayerNode treeNode = (IHyperViewPlayerNode) getCurrentNode();
		IHyperViewPlayerNode parent = treeNode.getParent();
		while (parent != null) {
			buf.insert(0, parent.getDescription());
			parent = parent.getParent();
			if (parent != null) {
				buf.insert(0, PATH_SEPARATOR);
			}
		}
		return buf.toString();
	}

	public boolean isCurrentNodeHyperCube() {
		IHyperViewPlayerNode treeNode = (IHyperViewPlayerNode) getCurrentNode();
		HyperViewNode parent = (HyperViewNode) treeNode.getHyperViewNode()
				.getParent();
		if (parent == null) {
			return false;
		}
		return parent.isHyperCube();
	}

	public String getHyperCubePreviousOption() {
		IHyperViewPlayerNode treeNode = (IHyperViewPlayerNode) getCurrentNode();
		try {
			return player.getHyperCubePreviousLabel(treeNode);
		} catch (Exception e) {
			return "Previous?";
		}
	}

	public String getHyperCubeNextOption() {
		try {
			IHyperViewPlayerNode treeNode = (IHyperViewPlayerNode) getCurrentNode();
			return player.getHyperCubeNextLabel(treeNode);
		} catch (Exception e) {
			return "Next?";

		}
	}

	@SuppressWarnings("unused")
	public void onHyperCubePrevious(ActionEvent event) {
		try {
			IHyperViewPlayerNode node = (IHyperViewPlayerNode) getCurrentNode();
			IHyperViewPlayerNode parent = node.getParent();
			collapseNode((TreeNode) node);
			expandNode((TreeNode) parent);
			IHyperViewPlayerNode previous = player.getHyperCubePreviousNode(
					parent, node);
			setCurrentNode((TreeNode) previous);
			runNodeSelected();
		} catch (Throwable e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	public void onHyperCubeNext(ActionEvent event) {
		try {
			IHyperViewPlayerNode node = (IHyperViewPlayerNode) getCurrentNode();
			IHyperViewPlayerNode parent = node.getParent();
			collapseNode((TreeNode) node);
			expandNode((TreeNode) parent);
			IHyperViewPlayerNode next = player.getHyperCubeNextNode(parent,
					node);
			setCurrentNode((TreeNode) next);
			runNodeSelected();
		} catch (Throwable e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@SuppressWarnings("unused")
	public void toggleVisibility(ActionEvent e) {
		setHidden(! isHidden() );
	}
}
