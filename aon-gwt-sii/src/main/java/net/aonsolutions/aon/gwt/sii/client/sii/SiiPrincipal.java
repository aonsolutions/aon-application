package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.sii.client.ISii;
import net.aonsolutions.aon.gwt.sii.client.ISiiAsync;

public class SiiPrincipal extends Composite{
	
	final ISiiAsync impl = GWT.create(ISii.class);
	interface Binder extends UiBinder<Widget, SiiPrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField SplitLayoutPanel splitLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabLayout; 
	@UiField ScrollPanel errorPanel;
	@UiField ScrollPanel historyPanel;

	private SiiMain parent;
	private SiiPrincipal me;
	
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
	
	public SiiPrincipal(SiiMain parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		this.me = this;
		filterContent();
		gridContent();

		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> arg0) {
				Integer value  = arg0.getSelectedItem();
				if(value == 1){
					openFootPanel();
					getAPI().getSii().getSiiHistory(new AsyncCallback<JSON<JsDataResponse>>() {
						
						@Override
						public void onSuccess(JSON<JsDataResponse> result) {
							historyPanel.add(new HistoryPanel(me, result.getData()));
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
	}
	
	public void filterContent(){			
		northContent.setWidget(new FilterPanel(this));
	}
	
	public void gridContent(){
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		FilterPanel fp = (FilterPanel) northContent.getWidget();
		fp.enabledAllFilter(false);
		getAPI().getFinance().getInvoices(getFilterMap(), new AsyncCallback<JSON<JsInvoice>>() {
			
			@Override
			public void onSuccess(JSON<JsInvoice> result) {
				fp.enabledAllFilter(true);
				content.setWidget(new InvoiceGrid(me, result.getData().toLinkedList()));
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	public void initializeFilterMap(){
		parent.initializeFilterMap();
	}
	
	public Button getSendAll() {
		return parent.getSendAll();
	}
	
	public Button getBaja() {
		return parent.getBaja();
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
