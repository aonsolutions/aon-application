package net.aonsolutions.aon.gwt.sii.client.sii;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperItem;

public class HistoryPanel extends SouthPanel {
	
	
	private Boolean isButton = false;
	public HistoryPanel(SiiPrincipal parent, AonJsArray<JsDataResponse> items) {   
    	super(parent);
        vertical.setWidth("100%");
        if(items.length() > 0){
        	addItems(items);
        } else {
        	vertical.add(new Label(nothing));
        }
    }
	
	public void addItems(AonJsArray<JsDataResponse> items) {
		items.stream().forEach(js -> {
    		PaperItem pi = buildProduct(js);
    		VerticalPanel vp = new VerticalPanel();
    		vp.setWidth("100%");
    		vp.setVisible(false);
    		Integer id = js.getId();
    		
    		pi.addClickHandler(new ClickHandler() {
    			
    			@Override
    			public void onClick(ClickEvent arg0) {
    				if(!isButton){
    					if(vp.getWidgetCount() > 0){
    						if(vp.isVisible()) vp.setVisible(false);
    						else vp.setVisible(true);
    					} else {
    						getAPI().getSii().getSiiHistoryDetail(id, new AsyncCallback<JSON<JsInvoice>>() {
						
    							@Override
    							public void onSuccess(JSON<JsInvoice> result) {
    								if(result.getData().length() > 0){
    									if(vp.isVisible()) vp.setVisible(false);
    									else vp.setVisible(true);
    									vp.getElement().getStyle().setPaddingLeft(24, Unit.PX);
    									result.getData().stream().forEach(js2 -> {
    										PaperItem pi2 = buildPaperItem(js2);
    										vp.add(pi2);
    									});
    								}
    							}
						
    							@Override public void onFailure(Throwable caught) {}
    						});
    					}
    				} else {
    					isButton = false;
    				}
    			}
    		});
    		vertical.add(pi);
    		vertical.add(vp);
    	});
	}
    
    public PaperItem buildProduct(JsDataResponse js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("schedule");
    	ironIcon.setWidth("5%");
    	pi.add(ironIcon);
    	String str = AonDateUtils.formatDate(AonDateUtils.parseDateTime(js.getIssueDate())) + " - " + js.getNumber();
    	Label label = new Label(str);
    	label.setWidth("100%");
    	pi.add(label);
    	
    	PaperIconButton reqDownloadButton = new PaperIconButton();
    	reqDownloadButton.setIcon("file-download");
    	reqDownloadButton.setTitle("Envio");
    	reqDownloadButton.setWidth("5%");
    	reqDownloadButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				isButton = true;
				getAPI().getSii().downloadSiiXml(js.getId(), "request");
			}
		});
    	pi.add(reqDownloadButton);

    	PaperIconButton respDownloadButton = new PaperIconButton();
    	respDownloadButton.setIcon("file-download");
    	respDownloadButton.setTitle("Respuesta");
    	respDownloadButton.setWidth("5%");
    	respDownloadButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				isButton = true;
				getAPI().getSii().downloadSiiXml(js.getId(), "response");
			}
		});
    	pi.add(respDownloadButton);
    	
    	pi.setStyle("min-height:24px;height:24px;font-size:12px;padding:0px;");
    	
    	return pi;
    }
   
    public PaperItem buildPaperItem(JsInvoice js){
    	PaperItem pi = new PaperItem();
    	pi.setWidth("100%");
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("receipt");
    	ironIcon.setWidth("5%");
    	pi.add(ironIcon);
    	String str = js.getReferenceCode();
    	Label label = new Label(str);
    	label.setWidth("100%");
    	pi.add(label);
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
	
}
