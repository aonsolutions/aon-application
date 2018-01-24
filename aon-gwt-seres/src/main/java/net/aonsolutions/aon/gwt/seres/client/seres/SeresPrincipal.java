package net.aonsolutions.aon.gwt.seres.client.seres;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.seres.JsAttachFile;
import com.esferalia.aon.gwt.api.client.seres.JsSeresFile;
import com.esferalia.aon.gwt.api.client.seres.JsSummary;
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

import net.aonsolutions.aon.gwt.seres.shared.CommunicationTarget;


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

	SeresMain parent;
	SeresPrincipal me;
	CommunicationTarget command;
	
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
	
	FilterPanel filterPanel;
	public void filterContent(){
		filterPanel = new FilterPanel(this);
		northContent.setWidget(filterPanel);
	}
	
	public void gridContent(){
		gridContent(command);
	}
	
	public void gridContent(CommunicationTarget command){
		this.command = command;
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		
		if(command!=null) { 
			getFilterMap().put("seres",new LinkedList<>(Arrays.asList(new String[] {command.getValue()})));
		} else {
			getFilterMap().put("pending", new LinkedList<>(Arrays.asList(new String[] {"true"})));
			getFilterMap().put("processed", new LinkedList<>(Arrays.asList(new String[] {"true"})));
			getFilterMap().put("error", new LinkedList<>(Arrays.asList(new String[] {"true"})));
		}
		
		filterPanel.setCheckVisible(command==CommunicationTarget.OUTCOME_DELIVERY
				|| command==CommunicationTarget.OUTCOME_INVOICE
				|| command==CommunicationTarget.INGENET_DELIVERY);
		
		Widget content = null;
		if (CommunicationTarget.OUTCOME_DELIVERY==command || CommunicationTarget.OUTCOME_INVOICE==command
				|| CommunicationTarget.INCOME_SALES==command || CommunicationTarget.INCOME_INVOICE==command) {
			ContentGrid contentGrid = (new ContentGrid(me));
			reloadContentGrid(contentGrid);
			content = contentGrid;
		} else if(CommunicationTarget.INGENET_DELIVERY==command) {
			ContentGridIngenet contentGrid = (new ContentGridIngenet(me));
			reloadContentGrid(contentGrid);
			content = contentGrid;
		} else {
			content = (createSummaryPanel());
		}
		getContent().setWidget(content);
		closeFootPanel();

	}
	
	private Widget createSummaryPanel() {
		VerticalPanel panel = new VerticalPanel(); 
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		getAPI().getSeres().getSummary(getFilterMap(), new AsyncCallback<JSON<JsSummary>>() {

			@Override
			public void onSuccess(JSON<JsSummary> result) {
				for(JsSummary o: result.getData().toLinkedList()){
					VerticalPanel innerPanel = new VerticalPanel();
					SimplePanel titlePanel = new SimplePanel();
					SimplePanel bodyPanel = new SimplePanel();
					innerPanel.setStyleName(AON.AON_CSS.aonGroup());
					titlePanel.setStyleName(AON.AON_CSS.aonGroupTitle());
					bodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
					innerPanel.add(titlePanel);
					innerPanel.add(bodyPanel);
					
					FlexTable table = new FlexTable();
					for(int i=0; i<7; i++)
						table.getFlexCellFormatter().setWidth(0, i, "200px");
					
					String name = null;
					if(CommunicationTarget.OUTCOME_DELIVERY.getValue().equals(o.getLabel())){
						name = "ALBARANES";
					} else if(CommunicationTarget.OUTCOME_INVOICE.getValue().equals(o.getLabel())){
						name = "FACTURAS";
					} else if(CommunicationTarget.INCOME_SALES.getValue().equals(o.getLabel())){
						name = "PEDIDOS";
					} else if(CommunicationTarget.INCOME_INVOICE.getValue().equals(o.getLabel())){
						name = "FACTURAS";
					} else if(CommunicationTarget.INGENET_DELIVERY.getValue().equals(o.getLabel())){
						name = "ALBARANES ";
					}
					table.setWidget(0, 0, boldLabel(name));
					table.setWidget(0, 1, new Label("Total"));
					table.setWidget(0, 2, boldLabel(o.getQuantity()));
					table.setWidget(0, 3, new Label("Pendiente"));
					table.setWidget(0, 4, boldLabel(o.getPending()));
					table.setWidget(0, 5, new Label("Errores"));
					table.setWidget(0, 6, boldLabel(o.getError()));
					
					InlineLabel title = null;
//					if(parent.OUTCOME_DELIVERY.equals(o.getLabel())){
//						title = new InlineLabel( "Albaranes enviados" );
//					} else if(parent.OUTCOME_INVOICE.equals(o.getLabel())){
//						title = new InlineLabel( "Facturas enviadas" );
//					} else if(parent.INCOME_SALES.equals(o.getLabel())){
//						title = new InlineLabel( "Pedidos recibidos" );
//					} else if(parent.INCOME_INVOICE.equals(o.getLabel())){
//						title = new InlineLabel( "Facturas recibidas" );
//					} else if(parent.INGENET_DELIVERY.equals(o.getLabel())){
//						title = new InlineLabel( "Ingenet - Albaranes recibidos" );
//					}
					if(CommunicationTarget.OUTCOME_DELIVERY.getValue().equals(o.getLabel())
							|| CommunicationTarget.OUTCOME_INVOICE.getValue().equals(o.getLabel())){
						title = new InlineLabel( "SERES - Env\u00EDo de Ficheros" );
					} else if(CommunicationTarget.INCOME_SALES.getValue().equals(o.getLabel())
							|| CommunicationTarget.INCOME_INVOICE.getValue().equals(o.getLabel())){
						title = new InlineLabel( "SERES - Recepci\u00F3n de Ficheros" );
					} else if(CommunicationTarget.INGENET_DELIVERY.getValue().equals(o.getLabel())){
						title = new InlineLabel( "Ingenet - Recepci\u00F3n de Ficheros" );
					}
					
					titlePanel.add(title);
					bodyPanel.add(table);
					panel.add(innerPanel);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		return panel;
	}
	
	public void reloadContentGrid() {
		ContentGrid contentGrid = (ContentGrid) getContent().getWidget();
		reloadContentGrid(contentGrid);
	}
		
	public void reloadContentGrid(ContentGrid contentGrid) {
		if(CommunicationTarget.OUTCOME_DELIVERY==command){
			getAPI().getSeres().getOutcomeDelivery(getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
				
				@Override
				public void onSuccess(JSON<JsSeresFile> result) {
					contentGrid.addAll(result.getData().toLinkedList());
					contentGrid.dataGrid.redraw();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else if(CommunicationTarget.OUTCOME_INVOICE==command){
			getAPI().getSeres().getOutcomeInvoice(getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
				
				@Override
				public void onSuccess(JSON<JsSeresFile> result) {
					contentGrid.addAll(result.getData().toLinkedList());
					contentGrid.dataGrid.redraw();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else if(CommunicationTarget.INCOME_SALES==command){
			getAPI().getSeres().getIncomeSales(getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
				
				@Override
				public void onSuccess(JSON<JsSeresFile> result) {
					contentGrid.addAll(result.getData().toLinkedList());
					contentGrid.dataGrid.redraw();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else if(CommunicationTarget.INCOME_INVOICE==command){
			getAPI().getSeres().getIncomeInvoice(getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
				
				@Override
				public void onSuccess(JSON<JsSeresFile> result) {
					contentGrid.addAll(result.getData().toLinkedList());
					contentGrid.dataGrid.redraw();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			
		}
	}
	
	public void reloadContentGrid(ContentGridIngenet contentGrid) {
		if(CommunicationTarget.INGENET_DELIVERY==command){
			getAPI().getSeres().getIngenetDeliveryAttach(getFilterMap(), new AsyncCallback<JSON<JsAttachFile>>() {
				
				@Override
				public void onSuccess(JSON<JsAttachFile> result) {
					contentGrid.addAll(result.getData().toLinkedList());
					contentGrid.dataGrid.redraw();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			
		}
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
	
	public Button getRetrieveAll() {
		return parent.getRetrieveAll();
	}

	public Button getProcessAll() {
		return parent.getProcessAll();
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

	public void consoleLog(String string) {
		SeresMain.consoleLog(string);
	}
	
}
