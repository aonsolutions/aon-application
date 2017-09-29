package net.aonsolutions.aon.gwt.seres.client.seres;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.seres.JsSeresFile;
import com.esferalia.aon.gwt.api.client.seres.JsSummary;
import com.esferalia.aon.gwt.api.client.warehouse.JsDelivery;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class SeresPrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, SeresPrincipal> {}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField SplitLayoutPanel splitLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabLayout; 
	@UiField ScrollPanel errorPanel;
	@UiField ScrollPanel historyPanel;

	private SeresMain parent;
	private SeresPrincipal me;
	private String command;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public SimpleLayoutPanel getContent(){
		return content;
	}
	
	public SimpleLayoutPanel getNorthContent(){
		return northContent;
	}
	
	
	public HashMap<String, LinkedList<String>> getFilterMap(){
		return parent.getFilterMap();
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap){
		parent.setFilterMap(filterMap);
	}
	
	public SeresPrincipal(SeresMain parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		this.me = this;
		this.command = null;
		
		filterContent();
		gridContent();

		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> arg0) {
				Integer value  = arg0.getSelectedItem();
				if(value == 1){
					openFootPanel();
					// TODO History
//					getAPI().getSii().getSiiHistory(new AsyncCallback<JSON<JsDataResponse>>() {
//						
//						@Override
//						public void onSuccess(JSON<JsDataResponse> result) {
//							historyPanel.add(new HistoryPanel(me, result.getData()));
//						}
//						
//						@Override public void onFailure(Throwable caught) {}
//					});
				}
			}
		});
	}
	
	public void filterContent(){			
		northContent.setWidget(new FilterPanel(this));
	}
	
	public void gridContent(){
		gridContent(command);
	}
	
	public void gridContent(String command){
		this.command = command;
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);

		if(parent.OUTCOME_DELIVERY.equals(command)){
			getAPI().getSeres().getOutcomeDelivery(getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
				
				@Override
				public void onSuccess(JSON<JsSeresFile> result) {
					content.setWidget(new ContentGrid(me, result.getData().toLinkedList()));
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		} else if(parent.OUTCOME_INVOICE.equals(command)){
			getAPI().getSeres().getOutcomeInvoice(getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
				
				@Override
				public void onSuccess(JSON<JsSeresFile> result) {
					content.setWidget(new ContentGrid(me, result.getData().toLinkedList()));
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		} else if(parent.INCOME_SALES.equals(command)){
			// TODO
			content.setWidget(new Label("En desarrollo."));
			Window.alert("En desarrollo.");
		} else if(parent.INCOME_INVOICE.equals(command)){
			// TODO
			content.setWidget(new Label("En desarrollo."));
			Window.alert("En desarrollo.");
		} else if(parent.INGENET_DELIVERY.equals(command)){
			// TODO
			content.setWidget(new Label("En desarrollo."));
			Window.alert("En desarrollo.");
		} else {
			getAPI().getSeres().getSummary(getFilterMap(), new AsyncCallback<JSON<JsSummary>>() {
				
				@Override
				public void onSuccess(JSON<JsSummary> result) {
					content.setWidget(createSummaryPanel(result.getData().toLinkedList()));
				}

				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		}
		
	}
	
	private Widget createSummaryPanel(LinkedList<JsSummary> list) {
		VerticalPanel panel = new VerticalPanel(); 
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		for(JsSummary o: list){
			VerticalPanel innerPanel = new VerticalPanel();
			SimplePanel titlePanel = new SimplePanel();
			SimplePanel bodyPanel = new SimplePanel();
			innerPanel.setStyleName(AON.AON_CSS.aonGroup());
			titlePanel.setStyleName(AON.AON_CSS.aonGroupTitle());
			bodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
			innerPanel.add(titlePanel);
			innerPanel.add(bodyPanel);
			
			FlexTable table = new FlexTable();
			for(int i=0; i<6; i++)
				table.getFlexCellFormatter().setWidth(0, i, "200px");
			table.setWidget(0, 0, new Label("Total"));
			table.setWidget(0, 1, boldLabel(o.getQuantity()));
			table.setWidget(0, 2, new Label("Pendiente"));
			table.setWidget(0, 3, boldLabel(o.getPending()));
			table.setWidget(0, 4, new Label("Errores"));
			table.setWidget(0, 5, boldLabel(o.getError()));
			
			InlineLabel title = null;
			if(parent.OUTCOME_DELIVERY.equals(o.getLabel())){
				title = new InlineLabel( "Albaranes enviados" );
			} else if(parent.OUTCOME_INVOICE.equals(o.getLabel())){
				title = new InlineLabel( "Facturas enviadas" );
			} else if(parent.INCOME_SALES.equals(o.getLabel())){
				title = new InlineLabel( "Pedidos recibidos" );
			} else if(parent.INCOME_INVOICE.equals(o.getLabel())){
				title = new InlineLabel( "Facturas recibidas" );
			} else if(parent.INGENET_DELIVERY.equals(o.getLabel())){
				title = new InlineLabel( "Ingenet - Albaranes recibidos" );
			}
		
			titlePanel.add(title);
			bodyPanel.add(table);
			panel.add(innerPanel);
		}
		return panel;
	}

	private Label boldLabel(String value){
		Label label = new Label(value);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		return label;
	}
	private Label boldLabel(Integer value) {
		return boldLabel(""+value);
	}

	public void initializeFilterMap(){
		parent.initializeFilterMap();
	}
	
	public Button getSendAll() {
		return parent.getSendAll();
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	public void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 3);
		splitLayoutPanel.animate(500);
	}
	
	public void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}
	
}
