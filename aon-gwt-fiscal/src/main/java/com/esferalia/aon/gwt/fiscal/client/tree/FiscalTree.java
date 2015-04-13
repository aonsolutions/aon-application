package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.ArrayList;
import java.util.Stack;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.tree.TreeNode.TreeNodeCallback;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class FiscalTree extends MainEntryPoint {
	static CommonServiceAsync COMMON_SERVICE;
	static FiscalServiceAsync FISCAL_SERVICE;
	
	interface FiscalNodeWidget<T> {
		void select( T t);
		void setCallback( TreeNodeCallback<T> callback);
		void newMod202( TreeNodeCallback<T> callback);
	}
	
	interface FiscalTreeBinder extends UiBinder<Widget, FiscalTree> {
	}

	private static final FiscalTreeBinder BINDER = GWT.create(FiscalTreeBinder.class);
	
	public static final int CURRENT_YEAR = 2015;

	Enterprise enterprise;
	
	TreeNode<Enterprise> rootNode;
	TreeNode<Enterprise> enterpriseDataNode;
	TreeNode<Enterprise> fiscalModelsNode;
	NewContextMenu newContextMenu = new NewContextMenu();
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ScrollPanel sidebar;
	@UiField
	OptionsToolbar toolbar;
	@UiField
	Tree tree;
	
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	
	@UiField 
	Label subtitle;
	
	@UiField(provided = true)
	SuggestBox enterpriseSuggest;
	
	@UiField
	SimpleLayoutPanel content;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;


//	private static Throwable getExceptionToDisplay(Throwable throwable) {
//		Throwable result = throwable;
//		if (throwable instanceof UmbrellaException
//				&& ((UmbrellaException) throwable).getCauses().size() == 1) {
//			result = ((UmbrellaException) throwable).getCauses().iterator()
//					.next();
//		}
//		return result;
//	}
	
	@Override
	public void onModuleLoad() {
		AON.GWT_RESOURCES.css().ensureInjected();
		AON.AON_RESOURCES.css().ensureInjected();

//		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
//			@Override
//			public void onUncaughtException(Throwable e) {
//				Throwable exceptionToDisplay = getExceptionToDisplay(e);
//				Window.alert(exceptionToDisplay.getMessage());
//			}
//		});
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		EnterpriseSuggestOracle oracle = new EnterpriseSuggestOracle();
		enterpriseSuggest = new SuggestBox(oracle);
		enterpriseSuggest.setLimit(20);
		enterpriseSuggest.addStyleName(AON.AON_RESOURCES.css().aonFiscalEnterpriseSuggest());
		
		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		toolbar.setVisibleViewButton(false);
		toolbar.setVisibleCopyButton(false);
		toolbar.setVisibleDraftButton(false);
		toolbar.setVisiblePasteButton(false);
		
		toolbar.setVisible(false);
		
		COMMON_SERVICE.getParentEnterprises(getCurrentDomainName(),getCurrentDomain(),"%"
				,new AsyncCallback<ArrayList<Enterprise>>() {
					@Override
					public void onSuccess(ArrayList<Enterprise> result) {
						if (result == null || result.size() == 0) {
							//TODO manage
							Window.alert("ERROR");
						} else if ( result.size() == 1) {
							enterpriseSuggest.setText(result.get(0).toString());
							enterpriseSuggest.setEnabled(false);
							initialize(result.get(0));
						} else {
							enterpriseSuggest.setText(AON.MSG.startTyping());
							enterpriseSuggest.setEnabled(true);
							enterpriseSuggest.getValueBox().selectAll();
							enterpriseSuggest.setFocus(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToShowData(caught
								.getMessage()));
					}
				});
		
		
	}																																																																																																																																																																																																																																																																																																																																																													

	private void initialize(Enterprise enterprise) {
		this.enterprise = enterprise;
		subtitle.setText(AON.MSG.enterprise());
		toolbar.setVisible(true);
		tree.removeItems();
		rootNode = TreeNodeTypes.ENTERPRISE.getInstance();
		rootNode.render(tree, this ,enterprise);
		enterpriseDataNode = TreeNodeTypes.ENTERPRISE_DATA.getInstance().render(rootNode, this ,enterprise);
//		TreeNodeTypes.FISCAL_ACTIVITY_GROUP.getInstance().render(rootNode,fiscalTree, enterprise);
		fiscalModelsNode = TreeNodeTypes.FISCAL_MODELS.getInstance().render(rootNode, this, enterprise);
		rootNode.setState(true);
		tree.addItem(rootNode);
		tree.setSelectedItem(rootNode);
	}
	
	@UiHandler("tree")
	void onTreeSelecction(SelectionEvent<TreeItem> event) {
		TreeNode<?> node = (TreeNode<?>) event.getSelectedItem();
		node.select(this);
		sidebar.scrollToLeft();
	}
	
	// -------------------------------------------------------------- UiHandler
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		resultsPanel.clearFlowPanel();
		resultsPanel.setWidget(new SimplePanel());
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		showResultsPanel();
		addErrorMessage(msg);
	}

	private void addErrorMessage(String msg) {
		SimplePanel panel = new SimplePanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> suggestion) {
		EnterpriseSuggestion sugg = (EnterpriseSuggestion) suggestion.getSelectedItem();
		initialize( sugg.getEnterprise() );
	}
	
	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {

		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			String query = '%' + request.getQuery() + '%';
			   
			COMMON_SERVICE.getParentEnterprises(getCurrentDomainName()
					,getCurrentDomain(),
					query
					,new AsyncCallback<ArrayList<Enterprise>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(ArrayList<Enterprise> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
							if (result != null) {
								for (final Enterprise enterprise : result) {
									suggestions.add(new EnterpriseSuggestion(enterprise));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}

	public class EnterpriseSuggestion implements Suggestion {
		private Enterprise enterprise;

		public EnterpriseSuggestion(Enterprise enterprise) {
			this.enterprise = enterprise;
		}

		public Enterprise getEnterprise() {
			return enterprise;
		}

		@Override
		public String getDisplayString() {
			return enterprise.toString();
		}

		@Override
		public String getReplacementString() {
			return enterprise.toString();
		}
	}

	public static Widget renderBreadcrumb(final FiscalTree fiscalTree, TreeItem item) {
		FlowPanel widget = new FlowPanel();
		widget.setStyleName(AON.AON_CSS.aonFiscalTreeBreadcrumb());
		Stack<TreeItem> stack = new Stack<TreeItem>();
		TreeItem child = item; 
		while (child.getParentItem() != null) {
			stack.push(child);
			child = child.getParentItem();
		}
		stack.push(fiscalTree.tree.getItem(0));
		while (!stack.empty()) {
			final TreeItem it = stack.pop();
			InlineLabel title = new InlineLabel( it.getText());
			title.setStyleName(AON.AON_CSS.aonFiscalTreeTitle());
			widget.add(title);
			if (it != item) {
				title.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						it.setState(true);
						fiscalTree.tree.setSelectedItem(it);
					}
				});
				InlineLabel sep = new InlineLabel( ">" );
				sep.addStyleName(AON.AON_CSS.aonMarginLeft());
				sep.addStyleName(AON.AON_CSS.aonMarginRight());
				widget.add(sep);	
			}
		}
		return widget;
	}

	public class NewMod200Command implements ScheduledCommand {

		@Override
		public void execute() {
			FiscalTree.FISCAL_SERVICE.initializeMod202(FiscalTree.getCurrentDomainName()
        		, enterprise.getDomain(), new AsyncCallback<Mod202>() {

					@Override
					public void onSuccess(Mod202 mod202) {
						TreeNode<Mod202> node = TreeNodeTypes.MODEL_202
							.getInstance()
							.render(fiscalModelsNode, FiscalTree.this, mod202);
						fiscalModelsNode.setState(true);
						FiscalTree.this.tree.setSelectedItem(node);
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				
			});
		}
	}
	
	
	public class NewContextMenu extends ContextMenu {

		public NewContextMenu() {
			NewMod200Command newMod200Command = getNewMod200Command();  
			addItem(AON.MSG.mod202()
					, newMod200Command,
					  AON.AON_CSS.aonIconModule()
					, AON.AON_CSS.aonIconCommandButton());
			addSeparator();
			addStyleName(AON.AON_CSS.aonSelector());
		}			
	}

	public NewMod200Command getNewMod200Command() {
		return new NewMod200Command();
	}
}
