package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.ArrayList;
import java.util.Stack;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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

public class Deposit implements EntryPoint {
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	interface DepositBinder extends UiBinder<Widget, Deposit> {
	}

	private static final DepositBinder BINDER = GWT.create(DepositBinder.class);
	
	public static final int CURRENT_YEAR = 2016;
	Enterprise enterprise;
	
	TreeNode<Enterprise> rootNode;
	TreeNode<Enterprise> enterpriseDataNode;
	
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
				
		inma.getParentEnterprises(getCurrentDomainName(),getCurrentDomain(),"%"
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
							memoryInitialize();
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

	public DigitalDepositTreeNode getDepositNode(Integer year) {
		Boolean bool = true;
		Boolean bool2 = true;
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			if (rootNode.getChild(i) instanceof YearTreeNode){
				YearTreeNode yearTreeNode = (YearTreeNode) rootNode.getChild(i);
				yearTreeNode.setState(true);
				if(yearTreeNode.getTreeObject().getYear().equals(year)){
					bool = false;
					for(Integer j = 0 ; j< yearTreeNode.getChildCount();j++){
						if (yearTreeNode.getChild(j) instanceof DigitalDepositTreeNode) {
							bool2 = false;
							DigitalDepositTreeNode node = (DigitalDepositTreeNode) yearTreeNode.getChild(j);
							node.setState(true);
							return node;
						}
					}
					if(bool2){
						EnterpriseYear ey = new EnterpriseYear();
						ey.setEnterprise(enterprise);
						ey.setYear(year);
						DigitalDepositTreeNode digitalDepositNode = 
								(DigitalDepositTreeNode) TreeNodeTypes.DIGITAL_DEPOSIT.getInstance().render(yearTreeNode, new D2DepositTreeObject(enterprise, year));
						digitalDepositNode.setState(true);
						return digitalDepositNode;
					}
				}
			}
		}
		if(bool){
			EnterpriseYear ey = new EnterpriseYear();
			ey.setEnterprise(enterprise);
			ey.setYear(year);
			TreeNode<EnterpriseYear> yearTreeNode = TreeNodeTypes.YEAR.getInstance().render(rootNode, ey);
			yearTreeNode.setState(true);
			// Nodo:  "Cuentas Anuales"
			DigitalDepositTreeNode digitalDepositNode = 
				(DigitalDepositTreeNode) TreeNodeTypes.DIGITAL_DEPOSIT.getInstance().render(yearTreeNode, new D2DepositTreeObject(enterprise, year));
			digitalDepositNode.setState(true);
			return digitalDepositNode;
		}
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
	
	Integer yearAux;
	private void initialize(final Enterprise enterprise, Boolean isParent) {
		this.enterprise = enterprise;
		subtitle.setText(AON.MSG.enterprise());
		tree.removeItems();
		rootNode = TreeNodeTypes.ENTERPRISE.getInstance();
		rootNode.render(tree, enterprise);

		// Nodo:  "Datos de la empresa"
		//enterpriseDataNode = TreeNodeTypes.ENTERPRISE_DATA.getInstance().render(rootNode, enterprise);
		
		for(Integer y = 2013; y <= CURRENT_YEAR; y++){
			yearAux = y;
			inma.isDigitalDeposit(enterprise.getDomain(), y, new AsyncCallback<Boolean>() {
				Integer year = yearAux;
				@Override
				public void onSuccess(Boolean result) {
					if(result || year == CURRENT_YEAR -1){// Has Fiscal Model or deposit
						// Nodo:  "Ejercicio YEAR"
						EnterpriseYear ey = new EnterpriseYear();
						ey.setEnterprise(enterprise);
						ey.setYear(year);
						TreeNode<EnterpriseYear> yearTreeNode = TreeNodeTypes.YEAR.getInstance().render(rootNode, ey);
						yearTreeNode.setState(true);
								
						// Nodo:  "Deposito Digital"
						if(result || year == CURRENT_YEAR -1){ // has deposit || last year
							TreeNode<D2DepositTreeObject> digitalDepositNode = TreeNodeTypes.DIGITAL_DEPOSIT.getInstance().render(yearTreeNode, new D2DepositTreeObject(enterprise, year));
							digitalDepositNode.setState(true);
							if(year == CURRENT_YEAR -1)
								tree.setSelectedItem(digitalDepositNode);
						}
						if(year == CURRENT_YEAR - 1)
							yearTreeNode.setState(true);
					}						
		    	}
		  
		    	@Override
		    	public void onFailure(Throwable caught) {};
			});
		}
		rootNode.setState(true);
		tree.addItem(rootNode);
		

		if(isParent){
			TreeNode<Integer> rootNode2 =  TreeNodeTypes.DIGITAL_DEPOSIT_FREETEXT.getInstance();
			rootNode2.render(tree, getCurrentDomain());
			tree.addItem(rootNode2);
			tree.setSelectedItem(rootNode2);
		}
	}
	
	private void memoryInitialize() {
		subtitle.setText(AON.MSG.enterprise());
		tree.removeItems();
		TreeNode<Integer> rootNode =  TreeNodeTypes.DIGITAL_DEPOSIT_FREETEXT.getInstance();
		rootNode.render(tree, getCurrentDomain());
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
			   
			inma.getParentEnterprises(getCurrentDomainName()
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

	public static Widget renderBreadcrumb(final Deposit fiscalTree, TreeItem item) {
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

	public Widget getGenericContent(final TreeItem item) {
		VerticalPanel widget = new VerticalPanel( );
		widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
		widget.add(Deposit.renderBreadcrumb(this, item));
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
}
