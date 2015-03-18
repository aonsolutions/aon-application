package com.esferalia.aon.gwt.fiscal.client.panel;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FiscalPanel extends MainEntryPoint {
	static CommonServiceAsync COMMON_SERVICE;
	static FiscalServiceAsync FISCAL_SERVICE;
	
	
	interface FiscalPanelBinder extends UiBinder<Widget, FiscalPanel> {
	}

	private static final FiscalPanelBinder BINDER = GWT.create(FiscalPanelBinder.class);
	
	protected final static CommonMessages MSG = GWT.create(CommonMessages.class);
	protected final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	protected final static GWTResources GWT_RESOURCES = GWT.create(GWTResources.class);
	protected static final NumberFormat FMT = NumberFormat.getFormat(MSG.decimalPattern(),MSG.currencyCode());

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ScrollPanel sidebar;
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
	SimplePanel content;

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	@Override
	public void onModuleLoad() {
		GWT_RESOURCES.css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		EnterpriseSuggestOracle oracle = new EnterpriseSuggestOracle();
		enterpriseSuggest = new SuggestBox(oracle);
		enterpriseSuggest.setLimit(20);
		enterpriseSuggest.addStyleName(AON_RESOURCES.css().aonFiscalEnterpriseSuggest());
		
		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		COMMON_SERVICE.getParentEnterprises(getCurrentDomainName(),getCurrentDomain(),"%"
				,new AsyncCallback<ArrayList<Enterprise>>() {
					@Override
					public void onSuccess(ArrayList<Enterprise> result) {
						if (result == null || result.size() == 0) {
							Window.alert("ERROR");
						} else if ( result.size() == 1) {
							Enterprise ent = result.get(0); 
							enterpriseSuggest.setText(ent.toString());
							enterpriseSuggest.setEnabled(false);
							initialize(ent);
						} else {
							enterpriseSuggest.setText(MSG.startTyping());
							enterpriseSuggest.setEnabled(true);
							enterpriseSuggest.getValueBox().selectAll();
							enterpriseSuggest.setFocus(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(MSG.unableToShowData(caught
								.getMessage()));
					}
				});
		
		
	}																																																																																																																																																																																																																																																																																																																																																													

	private void initialize(Enterprise enterprise) {
		subtitle.setText(MSG.enterprise());
		NodeType.renderTree(tree,this,enterprise,true);
	}
	
	@UiHandler("tree")
	void onTreeSelecction(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		NodeUserObject<?> type = (NodeUserObject<?>) item.getUserObject();
		type.getNodeType().select(item,this);
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
	static class NodeUserObject<T> {
		T userObject;
		NodeType<T> nodeType;
		public NodeUserObject(NodeType<T> nodeType,T userObject) {
			this.userObject = userObject;
			this.nodeType = nodeType;
		}
		public NodeType<T> getNodeType() {
			return nodeType;
		}
		public T getUserObject() {
			return userObject;
		}
	}
	static abstract class NodeType<T> {
		
		public static NodeType<Enterprise> ENTERPRISE = new NodeType<Enterprise>() {
			private EnterpriseMatrixPanel widget;
			
			@Override
			public void select(TreeItem item, FiscalPanel fiscalPanel) {
				if (widget ==null) {
					widget = new EnterpriseMatrixPanel();
				}
				widget.setDomainId( getCurrentDomain() );
				widget.setDomainName( getCurrentDomainName() );
				Enterprise uo = (Enterprise) item.getUserObject();
				widget.setEnterprise( uo );
				fiscalPanel.content.setWidget(widget);
			}

			@Override
			public TreeItem render(HasTreeItems parent, FiscalPanel fiscalPanel,Enterprise enterprise) {
				TreeItem treeItem = new TreeItem();
				InlineLabel label = new InlineLabel();
				label.setText(enterprise.toString());
				label.addStyleName( AON_RESOURCES.css().aonIconCompany() );
				label.addStyleName( AON_RESOURCES.css().aonTreeIconNode() );
				treeItem.setWidget(label);
				treeItem.setUserObject(enterprise);
				parent.addItem(treeItem);
				return treeItem;
			}
		};
		
		public static NodeType<Enterprise> ENTERPRISE_DATA = new NodeType<Enterprise>() {
			private EnterpriseForm widget; 
			
			@Override
			public void select(TreeItem item, FiscalPanel fiscalPanel) {
				if (widget ==null) {
					widget = new EnterpriseForm();
				}
				widget.setDomainId( getCurrentDomain() );
				widget.setDomainName( getCurrentDomainName() );
				Enterprise uo = (Enterprise) item.getUserObject();
				widget.setEnterprise( uo );
				fiscalPanel.content.setWidget(widget);
			}
			
			@Override
			public TreeItem render(HasTreeItems parent, FiscalPanel fiscalPanel, Enterprise enterprise) {
		    	TreeItem treeItem = new TreeItem();
		    	InlineLabel label = new InlineLabel();
		    	label.setText(MSG.enterpriseData());
		    	label.addStyleName(AON_RESOURCES.css().aonIconCompanyData());
		    	label.addStyleName( AON_RESOURCES.css().aonTreeIconNode() );
		    	treeItem.setWidget(label);
		    	treeItem.setUserObject( (Enterprise) enterprise);
		    	parent.addItem(treeItem);
				return treeItem;
			}
		};			
			
		public static NodeType<Enterprise> FISCAL_ACTIVITY_GROUP = new NodeType<Enterprise>() {
			private VerticalPanel widget;
			
			@Override
			public void select(final TreeItem item, FiscalPanel fiscalPanel) {
				if (widget ==null) {
					widget = new VerticalPanel( );
					widget.add(new Label(MSG.moduleActivities()));
					for (int i = 0; i < item.getChildCount() ; i++) {
						Label label =  new Label(item.getChild(i).getText());
						label.addClickHandler( new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								item.setSelected(true);
							}
						});
						widget.add(label);
					}
				}
				fiscalPanel.content.setWidget(widget);
			}
			
			@Override
			public TreeItem render(HasTreeItems parent, final FiscalPanel fiscalPanel,Enterprise enterprise) {
		    	final TreeItem treeItem = new TreeItem();
		    	InlineLabel label = new InlineLabel();
		    	label.setText(MSG.moduleActivities());
		    	label.addStyleName(AON_RESOURCES.css().aonIconActivities());
		    	label.addStyleName( AON_RESOURCES.css().aonTreeIconNode() );
		    	treeItem.setWidget(label);
		    	treeItem.setUserObject(new NodeUserObject<Enterprise>(FISCAL_ACTIVITY_GROUP,enterprise));
		    	parent.addItem(treeItem);
		    	FISCAL_SERVICE.getFiscalActivities(getCurrentDomainName(), enterprise.getDomain()
		    		, new AsyncCallback<ArrayList<FiscalActivity>>() {
					
					@Override
					public void onSuccess(ArrayList<FiscalActivity> result) {
						for (FiscalActivity fa : result) {
							TreeItem yearNode = null; 
							for (int i = 0 ; i < treeItem.getChildCount() ; i++) {
								Integer year = (Integer) treeItem.getChild(i).getUserObject();
								if (year.intValue() == fa.getYear().intValue()) {
									yearNode = treeItem.getChild(i);
									break;
								}
							}
							if (yearNode == null) {
								yearNode = FISCAL_ACTIVITY_YEAR.render(treeItem,fiscalPanel, fa);
							}
							FISCAL_ACTIVITY.render(yearNode,fiscalPanel, fa);
							yearNode.setState(true);
						}
						treeItem.setState(true);
					}
					
					@Override
					public void onFailure(Throwable caught) {
					}
				});
				return treeItem;
			}
		}
    	;
    	public static NodeType<FiscalActivity> FISCAL_ACTIVITY_YEAR = new NodeType<FiscalActivity>() {
    		
    		private HTMLPanel widget;
    		
			@Override
			public void select(TreeItem item, FiscalPanel fiscalPanel) {
				if (widget ==null) {
					widget = new HTMLPanel( item.getUserObject().toString() );
				}
				fiscalPanel.content.setWidget(widget);
			}

			@Override
			public TreeItem render(HasTreeItems parent,FiscalPanel fiscalPanel, FiscalActivity t) {
		    	TreeItem treeItem = new TreeItem();
		    	InlineLabel label = new InlineLabel();
		    	label.setText(t.getYear().toString());
		    	label.addStyleName(AON_RESOURCES.css().aonIconPointGreen());
		    	label.addStyleName( AON_RESOURCES.css().aonTreeIconNode() );
		    	treeItem.setWidget(label);
		    	treeItem.setUserObject( t.getYear() );
		    	parent.addItem(treeItem);
				return treeItem;
			}
    		
    	};
    	
    	
		public static NodeType<FiscalActivity> FISCAL_ACTIVITY = new NodeType<FiscalActivity>() {
			private ActivityForm widget;
			
			@SuppressWarnings("unchecked")
			@Override
			public void select(TreeItem item, FiscalPanel fiscalPanel) {
				if (widget ==null) {
					widget = new ActivityForm();
				}
				widget.setDomainId( getCurrentDomain() );
				widget.setDomainName( getCurrentDomainName() );
				NodeUserObject<FiscalActivity> uo = (NodeUserObject<FiscalActivity>) item.getUserObject();
				widget.setFiscalActivity( uo.getUserObject() );
				fiscalPanel.content.setWidget(widget);
			}
			
			@Override
			public TreeItem render(HasTreeItems parent, FiscalPanel fiscalPanel, FiscalActivity fa) {
		    	TreeItem treeItem = new TreeItem();
		    	InlineLabel label = new InlineLabel();
		    	label.setText(fa.getEpigraph() + " - " + fa.getDescription());
		    	label.addStyleName(AON_RESOURCES.css().aonIconPointLightGreen());
		    	label.addStyleName( AON_RESOURCES.css().aonTreeIconNode() );
		    	treeItem.setWidget(label);
		    	treeItem.setUserObject(new NodeUserObject<FiscalActivity>(FISCAL_ACTIVITY,fa));
		    	parent.addItem(treeItem);
				return treeItem;
			}
		}
    	;

    	public abstract void select(TreeItem item, FiscalPanel fiscalPanel);
		public abstract TreeItem render(HasTreeItems parent,FiscalPanel fiscalPanel, T t);
		
		public static void renderTree(Tree tree,FiscalPanel fiscalPanel,Enterprise enterprise, boolean removeAll) {
			if (removeAll && tree.getItemCount() > 0) {
				tree.removeItems();
			}
			TreeItem rootNode = ENTERPRISE.render(tree,fiscalPanel, enterprise);
			ENTERPRISE_DATA.render(rootNode,fiscalPanel, enterprise);
			FISCAL_ACTIVITY_GROUP.render(rootNode,fiscalPanel, enterprise);
		    rootNode.setState(true);
		    tree.addItem(rootNode);
		    tree.setSelectedItem(rootNode);
		}
	}
}


//,MOD111(MSG.mod111(),AON_RESOURCES.css().aonIconM111(),new HTMLPanel("MOD111"),null)
//,MOD115(MSG.mod115(),AON_RESOURCES.css().aonIconM115(),new HTMLPanel("MOD115"),null)
//,MOD123(MSG.mod123(),AON_RESOURCES.css().aonIconM123(),new HTMLPanel("MOD123"),null)
//,MOD130(MSG.mod130(),AON_RESOURCES.css().aonIconM130(),new HTMLPanel("MOD130"),null)
//,MOD131(MSG.mod131(),AON_RESOURCES.css().aonIconM131(),new HTMLPanel("MOD131"),null)
//,MOD140(MSG.mod140(),AON_RESOURCES.css().aonIconM140(),new HTMLPanel("MOD140"),null)
//,MOD180(MSG.mod180(),AON_RESOURCES.css().aonIconM180(),new HTMLPanel("MOD180"),null)
//,MOD184(MSG.mod184(),AON_RESOURCES.css().aonIconM184(),new HTMLPanel("MOD184"),null)
//,MOD190(MSG.mod190(),AON_RESOURCES.css().aonIconM190(),new HTMLPanel("MOD190"),null)
//,MOD193(MSG.mod193(),AON_RESOURCES.css().aonIconM193(),new HTMLPanel("MOD193"),null)
//,MOD200(MSG.mod200(),AON_RESOURCES.css().aonIconM200(),new HTMLPanel("MOD200"),null)
//,MOD303(MSG.mod303(),AON_RESOURCES.css().aonIconM303(),new HTMLPanel("MOD303"),null)
//,MOD340(MSG.mod340(),AON_RESOURCES.css().aonIconM340(),new HTMLPanel("MOD340"),null)
//,MOD347(MSG.mod347(),AON_RESOURCES.css().aonIconM347(),new HTMLPanel("MOD347"),null)
//,MOD349(MSG.mod349(),AON_RESOURCES.css().aonIconM349(),new HTMLPanel("MOD349"),null)
//,MOD390(MSG.mod390(),AON_RESOURCES.css().aonIconM390(),new HTMLPanel("MOD390"),null)
