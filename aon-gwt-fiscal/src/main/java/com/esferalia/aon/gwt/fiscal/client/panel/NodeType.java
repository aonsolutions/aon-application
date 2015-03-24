package com.esferalia.aon.gwt.fiscal.client.panel;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

abstract class NodeType<T> {
	
	// *****************************************************
	// *****************************************  ENTERPRISE
	// *****************************************************
	public static NodeType<Enterprise> ENTERPRISE = new NodeType<Enterprise>() {
		private EnterpriseMatrixPanel widget;
		
		@SuppressWarnings("unchecked")
		@Override
		public void select(TreeItem item, FiscalPanel fiscalPanel) {
			if (widget ==null) {
				widget = new EnterpriseMatrixPanel();
			}
			NodeUserObject<Enterprise> uo = (NodeUserObject<Enterprise>) item.getUserObject();
			widget.setEnterprise( uo.getUserObject() );
			fiscalPanel.content.setWidget(widget);
		}

		@Override
		public TreeItem render(HasTreeItems parent, FiscalPanel fiscalPanel,Enterprise enterprise) {
			TreeItem treeItem = new TreeItem();
			InlineLabel label = new InlineLabel();
			label.setText(enterprise.toString());
			label.addStyleName( FiscalPanel.AON_RESOURCES.css().aonIconCompany() );
			label.addStyleName( FiscalPanel.AON_RESOURCES.css().aonTreeIconNode() );
			treeItem.setWidget(label);
			treeItem.setUserObject(new NodeUserObject<Enterprise>(ENTERPRISE,enterprise));
			parent.addItem(treeItem);
			return treeItem;
		}
	};
	
	// *****************************************************
	// ************************************  ENTERPRISE_DATA
	// *****************************************************
	public static NodeType<Enterprise> ENTERPRISE_DATA = new NodeType<Enterprise>() {
		private EnterpriseForm widget; 
		
		@SuppressWarnings("unchecked")
		@Override
		public void select(TreeItem item, FiscalPanel fiscalPanel) {
			if (widget ==null) {
				
				widget = new EnterpriseForm();
			}
			NodeUserObject<Enterprise> uo = (NodeUserObject<Enterprise>) item.getUserObject();
			widget.setEnterprise( uo.getUserObject() );
			fiscalPanel.content.setWidget(widget);
		}
		
		@Override
		public TreeItem render(HasTreeItems parent, FiscalPanel fiscalPanel, Enterprise enterprise) {
	    	TreeItem treeItem = new TreeItem();
	    	InlineLabel label = new InlineLabel();
	    	label.setText(FiscalPanel.MSG.enterpriseData());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconCompanyData());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonTreeIconNode() );
	    	treeItem.setWidget(label);
	    	treeItem.setUserObject(new NodeUserObject<Enterprise>(ENTERPRISE_DATA,enterprise));
	    	parent.addItem(treeItem);
			return treeItem;
		}
	};			
		
	// *****************************************************
	// ******************************  FISCAL_ACTIVITY_GROUP
	// *****************************************************
	public static NodeType<Enterprise> FISCAL_ACTIVITY_GROUP = new NodeType<Enterprise>() {
		private VerticalPanel widget;
		
		@Override
		public void select(final TreeItem item, FiscalPanel fiscalPanel) {
			if (widget ==null) {
				widget = new VerticalPanel( );
				widget.add(new Label(FiscalPanel.MSG.moduleActivities()));
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
	    	label.setText(FiscalPanel.MSG.moduleActivities());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconActivities());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonTreeIconNode() );
	    	treeItem.setWidget(label);
	    	treeItem.setUserObject(new NodeUserObject<Enterprise>(FISCAL_ACTIVITY_GROUP,enterprise));
	    	parent.addItem(treeItem);
	    	FiscalPanel.FISCAL_SERVICE.getFiscalActivities(FiscalPanel.getCurrentDomainName()
	    		, enterprise.getDomain(), new AsyncCallback<ArrayList<FiscalActivity>>() {
				
				@Override
				public void onSuccess(ArrayList<FiscalActivity> result) {
					boolean currentYearRendered = false;
					for (FiscalActivity fa : result) {
						TreeItem yearNode = null; 
						for (int i = 0 ; i < treeItem.getChildCount() ; i++) {
							NodeUserObject<?> nodeUser = (NodeUserObject<?>) treeItem.getChild(i).getUserObject();
							if (nodeUser.getNodeType() == FISCAL_ACTIVITY_YEAR) {
								Integer year = (Integer) nodeUser.getUserObject(); 
								if (year.intValue() == fa.getYear().intValue()) {
									yearNode = treeItem.getChild(i);
									break;
								}
							}
							//Integer year = (Integer);
						}
						if (yearNode == null) {
							yearNode = FISCAL_ACTIVITY_YEAR.render(treeItem,fiscalPanel, fa.getYear());
							currentYearRendered = currentYearRendered || (fa.getYear() == FiscalPanel.CURRENT_YEAR);
						}
						FISCAL_ACTIVITY.render(yearNode,fiscalPanel, fa);
						yearNode.setState(true);
					} 
					if (!currentYearRendered) {
						FISCAL_ACTIVITY_YEAR.render(treeItem,fiscalPanel, FiscalPanel.CURRENT_YEAR);
					}
					treeItem.setState(true);
				}
				
				@Override
				public void onFailure(Throwable caught) {
				}
			});
			return treeItem;
		}
	};
	
	// *****************************************************
	// *******************************  FISCAL_ACTIVITY_YEAR
	// *****************************************************
	public static NodeType<Integer> FISCAL_ACTIVITY_YEAR = new NodeType<Integer>() {
		
		private HTMLPanel widget;
		
		@SuppressWarnings("unchecked")
		@Override
		public void select(TreeItem item, FiscalPanel fiscalPanel) {
			NodeUserObject<Integer> uo = (NodeUserObject<Integer>) item.getUserObject();
			if (widget ==null) {
				widget = new HTMLPanel( uo.getUserObject().toString() );
			}
			fiscalPanel.content.setWidget(widget);
		}

		@Override
		public TreeItem render(HasTreeItems parent,final FiscalPanel fiscalPanel, final Integer year) {
	    	final TreeItem treeItem = new TreeItem();
	    	InlineLabel label = new InlineLabel();
	    	label.setText(year.toString());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconPointGreen());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonTreeIconNode() );
	    	treeItem.setWidget(label);
	    	treeItem.setUserObject( new NodeUserObject<Integer>(FISCAL_ACTIVITY_YEAR, year) );
	    	parent.addItem(treeItem);
	    	linkContextMenu(treeItem,label,fiscalPanel,year);
			return treeItem;
		}

		private void linkContextMenu(final TreeItem treeItem, Label label, final FiscalPanel fiscalPanel, final Integer year) {
	        final PopupPanel popupPanel = new PopupPanel();
	        popupPanel.hide();
	        popupPanel.setAutoHideEnabled(true);
	        popupPanel.setStyleName(FiscalPanel.AON_RESOURCES.css().aonContextMenuPopup());
	    	Command newActivityCommand = new Command() {
				@Override
				public void execute() {
					FiscalActivity fa = new FiscalActivity();
					fa.setYear(year);
					fa.setEpigraph(AonStringUtils.EMPTY);
					fa.setDescription("NUEVA ACTIVIDAD");
					TreeItem newAct = FISCAL_ACTIVITY.render(treeItem,fiscalPanel, fa);
					treeItem.setState(true);
					fiscalPanel.tree.setSelectedItem(newAct,true);
					popupPanel.hide();
				}
			};
	    	MenuBar popup = new MenuBar(true);
	    	popup.setAnimationEnabled(true);
	    	popup.setStyleName(FiscalPanel.AON_RESOURCES.css().aonContextMenu());
	        MenuItem addItem = new MenuItem(FiscalPanel.MSG.newAction(), true, newActivityCommand);
	        addItem.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconReset());
	        popup.addItem(addItem);
	        popupPanel.setWidget(popup);
	        label.sinkEvents(Event.ONCONTEXTMENU);
	        label.addHandler(new ContextMenuHandler() {
				@Override
				public void onContextMenu(ContextMenuEvent event) {
					event.preventDefault();
					event.stopPropagation();
					popupPanel.setPopupPosition(event.getNativeEvent()
							.getClientX(), event.getNativeEvent()
							.getClientY());
					popupPanel.show();
				}
				
			}, ContextMenuEvent.getType());
		}
		
	};
	
	
	// *****************************************************
	// ************************************  FISCAL_ACTIVITY
	// *****************************************************
	public static NodeType<FiscalActivity> FISCAL_ACTIVITY = new NodeType<FiscalActivity>() {
		private ActivityForm widget;
		
		@SuppressWarnings("unchecked")
		@Override
		public void select(TreeItem item, FiscalPanel fiscalPanel) {
			if (widget ==null) {
				widget = new ActivityForm();
			}
			NodeUserObject<FiscalActivity> uo = (NodeUserObject<FiscalActivity>) item.getUserObject();
			widget.select(item, uo.getUserObject() );
			fiscalPanel.content.setWidget(widget);
		}
		
		@Override
		public TreeItem render(HasTreeItems parent, FiscalPanel fiscalPanel, FiscalActivity fa) {
	    	TreeItem treeItem = new TreeItem();
	    	InlineLabel label = new InlineLabel();
	    	label.setText((AonStringUtils.isBlank(fa.getEpigraph())?AonStringUtils.EMPTY:fa.getEpigraph() + " - ") 
	    			+ AonStringUtils.abbreviate(fa.getDescription(), 40));
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconPointLightGreen());
	    	label.addStyleName(FiscalPanel.AON_RESOURCES.css().aonTreeIconNode() );
	    	treeItem.setWidget(label);
	    	treeItem.setUserObject(new NodeUserObject<FiscalActivity>(FISCAL_ACTIVITY,fa));
	    	parent.addItem(treeItem);
	    	linkContextMenu(treeItem, label, fiscalPanel, fa);
			return treeItem;
		}
		
		private void linkContextMenu(final TreeItem treeItem, Label label, final FiscalPanel fiscalPanel, final FiscalActivity fa) {
	        final PopupPanel popupPanel = new PopupPanel();
	        popupPanel.hide();
	        popupPanel.setAutoHideEnabled(true);
	        popupPanel.setStyleName(FiscalPanel.AON_RESOURCES.css().aonContextMenuPopup());
			Command deleteActivityCommand = new Command() {
				@Override
				public void execute() {
					if (Window.confirm( FiscalPanel.MSG.deleteAction() )) {
						if (fa.getId() == null) {
							fiscalPanel.tree.setSelectedItem(treeItem.getParentItem(),true);
							treeItem.getParentItem().removeItem(treeItem);							
						}
					}
					popupPanel.hide();
				}
			};
	    	MenuBar popup = new MenuBar(true);
	    	popup.setAnimationEnabled(true);
	    	popup.setStyleName(FiscalPanel.AON_RESOURCES.css().aonContextMenu());
	        MenuItem deleteItem = new MenuItem(FiscalPanel.MSG.deleteAction(), true, deleteActivityCommand);
	        deleteItem.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconDelete());
	        popup.addItem(deleteItem);
	        popupPanel.setWidget(popup);
	        label.sinkEvents(Event.ONCONTEXTMENU);
	        label.addHandler(new ContextMenuHandler() {
				@Override
				public void onContextMenu(ContextMenuEvent event) {
					event.preventDefault();
					event.stopPropagation();
					popupPanel.setPopupPosition(event.getNativeEvent()
							.getClientX(), event.getNativeEvent()
							.getClientY());
					popupPanel.show();
				}
				
			}, ContextMenuEvent.getType());
		}
		
	};
	

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
