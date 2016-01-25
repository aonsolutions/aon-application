package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseYear;
import com.esferalia.aon.gwt.fiscal.client.tree.node.FiscalModelsTreeNode;
import com.esferalia.aon.gwt.fiscal.client.tree.node.FiscalModelsTreeNode.TreeNodeFiscalModelTypes;
import com.esferalia.aon.gwt.fiscal.client.tree.node.Mod2002013TreeObject;
import com.esferalia.aon.gwt.fiscal.client.tree.node.Mod2002014TreeObject;
import com.esferalia.aon.gwt.fiscal.client.tree.node.Model2002014TreeNode;
import com.esferalia.aon.gwt.fiscal.client.tree.node.ModelTreeNode;
import com.esferalia.aon.gwt.fiscal.client.tree.node.TreeNode;
import com.esferalia.aon.gwt.fiscal.client.tree.node.TreeNodeTypes;
import com.esferalia.aon.gwt.fiscal.client.tree.node.YearTreeNode;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FiscalTree extends MainEntryPoint implements OptionsToolbar.Listener {
	public static CommonServiceAsync COMMON_SERVICE;
	public static FiscalServiceAsync FISCAL_SERVICE;
	
	public static interface FiscalTreeCallback<T> {
		void changeLabel( T treeObject);
		void remove( T treeObject);
		void onError( T treeObject);
	}
	
	interface FiscalTreeBinder extends UiBinder<Widget, FiscalTree> {
	}

	private static final FiscalTreeBinder BINDER = GWT.create(FiscalTreeBinder.class);
	
	public static final int CURRENT_YEAR = 2016;

	Enterprise enterprise;
	
	TreeNode<Enterprise> rootNode;
	TreeNode<Enterprise> enterpriseDataNode;

	NewContextMenu newContextMenu;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ScrollPanel sidebar;
	@UiField
	OptionsToolbar toolbar;
	@UiField
	Tree tree;
	
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


	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

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
		toolbar.addListener(this);
		
		COMMON_SERVICE.getParentEnterprises(getCurrentDomainName(),getCurrentDomain(),"%"
				,new AsyncCallback<ArrayList<Enterprise>>() {
					@Override
					public void onSuccess(ArrayList<Enterprise> result) {
						if (result == null || result.size() == 0) {
							PopupPanel box = DialogMessages.alertErrorWidget(AON.MSG.noData());
							box.center();
							box.show();
						} else if ( result.size() == 1) {
							enterpriseSuggest.setText(result.get(0).toString());
							enterpriseSuggest.setEnabled(false);
							initialize(result.get(0), false);
						} else {
							enterpriseSuggest.setText(AON.MSG.startTyping());
							enterpriseSuggest.setEnabled(true);
							enterpriseSuggest.getValueBox().selectAll();
							enterpriseSuggest.setFocus(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						PopupPanel box = DialogMessages
								.alertErrorWidget(AON.MSG.unableToShowData(caught
								.getMessage()));
						box.center();
						box.show();
					}
				});
		
		
	}																																																																																																																																																																																																																																																																																																																																																													
	public SimpleLayoutPanel getContent() {
		return this.content;
	}
	public void setContent(Widget widget) {
		this.content.setWidget(widget);
	}
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public NewContextMenu getNewContextMenu() {
		return newContextMenu;
	}
	public FiscalModelsTreeNode getFiscalModelsNode(Integer year) {
		Boolean bool = true;
		Boolean bool2 = true;
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			if (rootNode.getChild(i) instanceof YearTreeNode){
				YearTreeNode yearTreeNode = (YearTreeNode) rootNode.getChild(i);
				yearTreeNode.setState(true);
				if(yearTreeNode.getTreeObject().getYear().equals(year)){
					bool = false;
					for(Integer j = 0 ; j< yearTreeNode.getChildCount();j++){
						if (yearTreeNode.getChild(j) instanceof FiscalModelsTreeNode) {
							bool2 = false;
							FiscalModelsTreeNode node = (FiscalModelsTreeNode) yearTreeNode.getChild(j);
							node.setState(true);
							return node;
						}
					}
					if(bool2){
						EnterpriseYear ey = new EnterpriseYear();
						ey.setEnterprise(enterprise);
						ey.setYear(year);
						FiscalModelsTreeNode fiscalModelsNode = 
								(FiscalModelsTreeNode) TreeNodeTypes.FISCAL_MODELS.getInstance().render(yearTreeNode, ey);
						fiscalModelsNode.setState(true);
						return fiscalModelsNode;
					}
				}
			}
			/*if (rootNode.getChild(i) instanceof FiscalModelsTreeNode) {
				
				FiscalModelsTreeNode node = (FiscalModelsTreeNode) rootNode.getChild(i);
				node.setState(true);
				return node;
			}*/
		}
		if(bool){
			EnterpriseYear ey = new EnterpriseYear();
			ey.setEnterprise(enterprise);
			ey.setYear(year);
			TreeNode<EnterpriseYear> yearTreeNode = TreeNodeTypes.YEAR.getInstance().render(rootNode, ey);
			yearTreeNode.setState(true);
			// Nodo:  "Modelos Fiscales"
			FiscalModelsTreeNode fiscalModelsNode = 
				(FiscalModelsTreeNode) TreeNodeTypes.FISCAL_MODELS.getInstance().render(yearTreeNode, ey);
			fiscalModelsNode.setState(true);
			return fiscalModelsNode;
		}
		
		// Nunca deberia llegar aqui.
		Window.alert("Nodo Modelos Fiscales no agregado");
		return null;
	}
	
	public YearTreeNode getYearNode(Integer year) {
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			if (rootNode.getChild(i) instanceof YearTreeNode){
				YearTreeNode yearTreeNode = (YearTreeNode) rootNode.getChild(i);
				yearTreeNode.setState(true);
				return yearTreeNode;
			}
		}
		
		// Nunca deberia llegar aqui.
		Window.alert("Nodo Modelos Fiscales no agregado");
		return null;
	}
	
	Integer yearAux, yearAux2;Boolean boolAux;
	private void initialize(final Enterprise enterprise, Boolean isParent) {
		newContextMenu = new NewContextMenu();
		this.enterprise = enterprise;
		subtitle.setText(AON.MSG.enterprise());
		toolbar.setVisible(true);
		tree.removeItems();
		rootNode = TreeNodeTypes.ENTERPRISE.getInstance();
		rootNode.render(tree, enterprise);
		
		// Nodo:  "Datos de la empresa"
		enterpriseDataNode = TreeNodeTypes.ENTERPRISE_DATA.getInstance().render(rootNode, enterprise);
		
		for(Integer y = 2013; y <= CURRENT_YEAR; y++){
			yearAux = y;
			FiscalTree.FISCAL_SERVICE.getAllModels(FiscalTree.getCurrentDomainName(), enterprise.getDomain(), y, new AsyncCallback<LinkedList<IFiscalModel>>() {
				Integer year = yearAux;
		    	@Override
		    	public void onSuccess(LinkedList<IFiscalModel> result) {
		    		Boolean hasFiscalModels = result.size()>0;
		    		if(hasFiscalModels){// Has Fiscal Model or deposit
		    			// Nodo:  "Ejercicio YEAR"
		    			EnterpriseYear ey = new EnterpriseYear();
		    			ey.setEnterprise(enterprise);
						ey.setYear(year);
						TreeNode<EnterpriseYear> yearTreeNode = TreeNodeTypes.YEAR.getInstance().render(rootNode, ey);
						yearTreeNode.setState(true);
						// Nodo:  "Modelos Fiscales"
						//if(hasFiscalModels){ // has model 
						TreeNode<EnterpriseYear> fiscalModelsNode = 
								TreeNodeTypes.FISCAL_MODELS.getInstance().render(yearTreeNode, ey);
						fiscalModelsNode.setState(true);
						//}
		    		}						
		    	}
		  
		    	@Override
		    	public void onFailure(Throwable caught) {};
			});
		}
		
		// Nodo:  "Modulo: Actividades empresariales."
		// TreeNodeTypes.FISCAL_ACTIVITY_GROUP.getInstance().render(rootNode,enterprise);
		
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

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> suggestion) {
		EnterpriseSuggestion sugg = (EnterpriseSuggestion) suggestion.getSelectedItem();
		initialize( sugg.getEnterprise(), true);
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
							PopupPanel box = DialogMessages
									.alertErrorWidget(caught.getMessage());
							box.center();
							box.show();
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

	public class NewContextMenu extends ContextMenu {
//		private boolean[] models = new boolean[FiscalModelType.values().length];
		
		public NewContextMenu() {
//			addNewMod131();
			addNewMod2002013();
			addSeparator();
			addNewMod202();
			addNewMod2002014();
			addStyleName(AON.AON_CSS.aonSelector());
		}
		
		public void addItem(FiscalModelType model, String text, ScheduledCommand cmd) {
//			if ( !models[model.ordinal()] ) {
				super.addItem(model.getValue(), text, cmd);
//				models[model.ordinal()] = true;
//			}
		}

		protected NewContextMenu addNewMod202() {
			addItem(FiscalModelType.M202 
					,AON.MSG.newSomething( AON.MSG.fiscalModelType( FiscalModelType.M202 ) )  
					, new ScheduledCommand() {
						
						@Override
						public void execute() {
							Mod202 mod202 = new Mod202();
							mod202.setDomain(getEnterprise().getDomain());
							mod202.setYear(2015);
							mod202.setModel(FiscalModelType.M202);
							mod202.setPeriod(Period.T1);
							FiscalTree.FISCAL_SERVICE.initializeMod202(FiscalTree.getCurrentDomainName()
			        		, getEnterprise().getDomain(), mod202
			        		, new AsyncCallback<Mod202>() {
			
								@Override
								public void onSuccess(Mod202 mod202) {
									TreeNode<Mod202> node = TreeNodeFiscalModelTypes.MODEL_202.getInstance().render(
										getFiscalModelsNode(mod202.getYear()).getModelNode(mod202.getYear(),mod202.getModel())
										, mod202);
									getFiscalModelsNode(mod202.getYear()).setState(true);
									tree.setSelectedItem(node);
								}
			
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
								}
							});
						}
					});
			return this; 
		}
		
		protected NewContextMenu addNewMod2002013() {
			addItem(FiscalModelType.M200 
					,AON.MSG.newSomething( AON.MSG.fiscalModelType( FiscalModelType.M200 ) + " - 2013" )  
					, new ScheduledCommand() {
						
						@Override
						public void execute() {
							Mod2002013 mod200 = new Mod2002013();
							mod200.setDomain(getEnterprise().getDomain());
							mod200.setYear(2013);
							FiscalTree.FISCAL_SERVICE.initializeNewMod2002013(FiscalTree.getCurrentDomainName()
			        		, getEnterprise().getDomain(), mod200
			        		, new AsyncCallback<Mod2002013>() {
			
								@Override
								public void onSuccess(Mod2002013 mod200) {
									final FiscalModelsTreeNode fiscalModelsNode = getFiscalModelsNode(mod200.getYear());
									fiscalModelsNode.setState(true);
		        					final ModelTreeNode parentNode = fiscalModelsNode.getModelNode(
		        							mod200.getYear(),FiscalModelType.M200);
		        					parentNode.setState(true);
		        					TreeNode<Mod2002013TreeObject> node = 
		        						TreeNodeFiscalModelTypes.CORPORATE_TAX_2013.getInstance().render(parentNode
		            		    			,TreeNodeFiscalModelTypes.MODEL_200_2013.getFiscalModel(mod200));
		        					node.setState(true);
	            		    		tree.setSelectedItem(node);	
								}
			
								@Override
								public void onFailure(Throwable caught) {
								}
							});
						}
					});
			return this; 
		}
		
		protected NewContextMenu addNewMod2002014() {
			addItem(FiscalModelType.M200 
					,AON.MSG.newSomething( AON.MSG.fiscalModelType( FiscalModelType.M200 ) + " - 2014" )  
					, new ScheduledCommand() {
						
						@Override
						public void execute() {
							Mod2002014 mod200 = new Mod2002014();
							mod200.setDomain(getEnterprise().getDomain());
							mod200.setYear(2014);
							FiscalTree.FISCAL_SERVICE.initializeNewMod2002014(FiscalTree.getCurrentDomainName()
			        		, getEnterprise().getDomain(), mod200
			        		, new AsyncCallback<Mod2002014>() {
			
								@Override
								public void onSuccess(Mod2002014 mod200) {
		        					final ModelTreeNode parentNode = getFiscalModelsNode(mod200.getYear()).getModelNode(
		        							mod200.getYear(),FiscalModelType.M200);
		        					final Model2002014TreeNode node = (Model2002014TreeNode)  
		        						TreeNodeFiscalModelTypes.CORPORATE_TAX_2014.getInstance().render(parentNode
										,TreeNodeFiscalModelTypes.MODEL_200_2014.getFiscalModel(mod200));
		        					node.getTreeObject().setFiscalTreeCallback(new FiscalTreeCallback<Mod2002014TreeObject>() {
										
										@Override
										public void remove(Mod2002014TreeObject treeObject) {
											node.remove();
											parentNode.getTree().setSelectedItem(parentNode);
										}
										@Override
										public void onError(Mod2002014TreeObject treeObject) {}
										
										@Override
										public void changeLabel(Mod2002014TreeObject treeObject) {
											node.setLabel(treeObject);
										}
									});

		        					getFiscalModelsNode(mod200.getYear()).setState(true);
		            		    	parentNode.setState(true);
	            		    		tree.setSelectedItem(node);
								}
			
								@Override
								public void onFailure(Throwable caught) {
								}
							});
						}
					});
			return this; 
		}

		//		protected NewContextMenu addNewMod131() {
//			addItem(
//    			FiscalModelType.M131
//    			,AON.MSG.newSomething( AON.MSG.fiscalModelType( FiscalModelType.M131 ) )  
//    			, new ScheduledCommand() {
//					
//					@Override
//					public void execute() {
//						Mod131 mod131 = new Mod131();
//						mod131.setDomain(getEnterprise().getDomain());
//						mod131.setModel(FiscalModelType.M131);
//						mod131.setYear(2015);
//						mod131.setPeriod(Period.T1);
//						FiscalTree.FISCAL_SERVICE.initializeMod131(FiscalTree.getCurrentDomainName()
//		        		, getEnterprise().getDomain(), mod131
//		        		, new AsyncCallback<Mod131>() {
//		
//							@Override
//							public void onSuccess(Mod131 mod131) {
//								TreeNode<Mod131> node = TreeNodeFiscalModelTypes.MODEL_131.getInstance().render(
//									getFiscalModelsNode().getModelNode(mod131.getYear(),mod131.getModel())
//									, mod131);
//								getFiscalModelsNode().setState(true);
//								tree.setSelectedItem(node);
//							}
//		
//							@Override
//							public void onFailure(Throwable caught) {
//							}
//						});
//					}
//				});
//			return this;
//		}
		
	}

	
	

	
	
	@Override
	public void onNewButtonClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		newContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		newContextMenu.show();
	}

	@Override
	public void onPasteButtonClick(ClickEvent event) {
	}

	@Override
	public void onCopyButtonClick(ClickEvent event) {
	}

	@Override
	public void onDraftButtonClick(ClickEvent event) {
	}

	@Override
	public void onCollapseAllButtonClick(ClickEvent event) {
	}

	public void renderGenericContent(final TreeItem item) {
		setContent(getGenericContent(item));
	}
	
	public Widget getGenericContent(final TreeItem item) {
		VerticalPanel widget = new VerticalPanel( );
		widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
		widget.add(FiscalTree.renderBreadcrumb(this, item));
		for (int i = 0; i < item.getChildCount() ; i++) {
			final TreeItem child = item.getChild(i); 
			InlineLabel label =  new InlineLabel( "\u2022 " + item.getChild(i).getText());
			label.setStyleName(AON.AON_CSS.aonFiscalTreeItem());
			label.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					TreeItem parent = child.getParentItem();
					while (parent != null) {
						parent.setState(true);
						parent = parent.getParentItem();	
					}
					tree.setSelectedItem(child);
				}
			});
			widget.add(label);
		}
		return widget;
	}
	
	public Boolean hasFiscalModels(Integer year){
		// TODO 
		return true;
	}
}
