package com.esferalia.aon.gwt.stat.client.panel;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;

public class StatFilter extends FlowPanel {

	static StatServiceAsync statService;

	public StatParams params;

	public StatFilter() {
		super();
	}

	public StatParams getParams() {
		return params;
	}

	public void paintFilter(final AsyncCallback<StatParams> callback, final SelectionHandler<StatFilterItem> next) {
		AON.ensureInjected();
		StatServiceAsync serviceRaw = GWT.create(StatService.class);
		statService = new StatServiceAsyncDecorator(serviceRaw);
		
		VisualizationUtils.loadVisualizationApi(new Runnable() {
			@Override
			public void run() {
				statService.createStatParams(getCurrentDomainName(), getCurrentDomain(),
					new AsyncCallback<StatParams>() {
		
						@Override
						public void onSuccess(StatParams result) {
							
							params = result;
							
							final HashMap<StatFilterType,FilterMenu> menus = new HashMap<StatFilterType,FilterMenu>();					
							for (final StatFilterItem item : result.getFilterItems()) {
								if (!menus.containsKey( item.getType())) {
									final FilterMenu menu = new FilterMenu();
									menus.put(item.getType(), menu);								
									
									final Button button = new Button(item.getType().getName() );
									button.setStyleName(AON.AON_CSS.aonDropButton());
									
									button.addClickHandler(new ClickHandler() {
										
										@Override
										public void onClick(ClickEvent event) {
											int x =  button.getAbsoluteLeft();
											int y = button.getAbsoluteTop() + button.getOffsetHeight() + 2;
											menu.setPopupPosition(x,y);
											menu.show();
										}
									});
									
									
//									Añadir una X y Borrar todos los filtros. Recorrer los item activos y 
//									ponerlos a false. No tengo muy claro si va aquí esto...
//									Table x = new Table();
//									x.setTitle(AON.MSG.statFilterDelete());
//									x.addStyleName(AON.AON_CSS.aonIconDeleteTrash());
//									menu.add(x);
									
									add(button);
									menu.addSelectionHandler(new SelectionHandler<StatFilterItem>() {
										
										@Override
										public void onSelection(SelectionEvent<StatFilterItem> event) {
											next.onSelection(event);
										}
									});
								}
								FilterMenu parent = menus.get( item.getType());								
								parent.addItem( item , next);						
								
							}
							callback.onSuccess(result);
						}
		
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							callback.onFailure(caught);
						}
				});
			}}, CoreChart.PACKAGE, Table.PACKAGE);
			
	}
	

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	public class FilterMenu extends PopupPanel implements HasSelectionHandlers<StatFilterItem>{
		private FlowPanel container;
		
		public FilterMenu() {
			super();

//			DockPanel dockPanel = new DockPanel();	
//			dockPanel.add(new );
//			dockPanel.onResize();
//			
//			ScrollPanel scroll = new ScrollPanel();
//		    dockPanel.add(scroll , DockPanel.CENTER);
				
			ScrollPanel scroll = new ScrollPanel();			
			container = new FlowPanel();
			scroll.add(container);
			add(scroll);
			setStyleName( AON.AON_CSS.aonStatPopUpPanel());
			setAutoHideEnabled(true);
			
			scroll.addStyleName(AON.AON_CSS.aonPadding2Left());
			scroll.addStyleName(AON.AON_CSS.aonPadding2Top());
			scroll.addStyleName(AON.AON_CSS.aonPadding2Bottom());
			scroll.addStyleName(AON.AON_CSS.aonStatMenuStyle());
			// cuando añadas el dock, hacer onResize
			// doc.onResize();
		}

		public void addItem(final StatFilterItem item, SelectionHandler<StatFilterItem> next) {
			final FocusPanel itemPanel = new FocusPanel();
			itemPanel.setStyleName(AON.AON_CSS.aonStatItemPanel());
			if(item.isSelected()){
				itemPanel.addStyleName(AON.AON_CSS.aonStatCheckyes());
			}else{
				itemPanel.addStyleName(AON.AON_CSS.aonListStat());	
			}
			String description = item.getLabel();
			description = AonStringUtils.abbreviate(description, 35);
			Label label = new Label(description);
			label.setTitle(item.getLabel());
			itemPanel.setWidget(label);
			itemPanel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					item.setSelected( !item.isSelected() );
					if(item.isSelected()){
						itemPanel.addStyleName(AON.AON_CSS.aonStatCheckyes());
						itemPanel.removeStyleName(AON.AON_CSS.aonListStat());
					}else{
						itemPanel.removeStyleName(AON.AON_CSS.aonStatCheckyes());
						itemPanel.addStyleName(AON.AON_CSS.aonListStat());
					}
					SelectionEvent.<StatFilterItem>fire(FilterMenu.this, item);
				}
			});
			container.add(itemPanel);
			
		}
		
		@Override
	    public HandlerRegistration addSelectionHandler(SelectionHandler<StatFilterItem> handler) {
	            return super.addHandler(handler, SelectionEvent.getType());
	    }       
		
	}
	
}
