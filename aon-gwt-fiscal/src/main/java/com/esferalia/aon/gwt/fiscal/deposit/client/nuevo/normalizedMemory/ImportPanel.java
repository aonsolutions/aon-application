package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.event.IronSelectEvent;
import com.vaadin.polymer.iron.widget.event.IronSelectEventHandler;
import com.vaadin.polymer.paper.widget.PaperTabs;
import com.vaadin.polymer.vaadin.widget.VaadinUpload;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class ImportPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, ImportPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField HTMLPanel panel;
    @UiField HTMLPanel tabContent;
    @UiField PaperTabs tabs;

    Deposit parent; 
    AonComboBox memoryBox;
    AonComboBox yearBox;
    AonComboBox socBox;
    VerticalPanel sociedades;
    VaadinUpload upload;
    
    Vector<MemoryTemplate> mts;
    
    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String TWO = "2";
    
    public ImportPanel(Deposit parent, Vector<MemoryTemplate> result) {
    	this.mts = result;
    	this.parent = parent;
		initWidget(binder.createAndBindUi(this));
    
		memoria(result);
		sociedades();
		d2();
		tabs.setSelected("0");
		tabs.addIronSelectHandler(new IronSelectEventHandler() {
			
			@Override
			public void onIronSelect(IronSelectEvent event) {
				for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
					tabContent.remove(i);
				ScrollPanel sp = new ScrollPanel();
				sp.getElement().getStyle().setHeight(100, Unit.PX);
    	
				if(tabs.getSelected().toString().equals(ZERO)
						|| tabs.getSelected().toString() == ZERO){
					tabContent.add(memoryBox);
				} else if(tabs.getSelected().toString().equals(ONE)
						|| tabs.getSelected().toString() == ONE){	
			    	tabContent.add(sociedades);
				} else if(tabs.getSelected().toString().equals(TWO)
						|| tabs.getSelected().toString() == TWO){
					tabContent.add(upload);
				}
			}
		});       

	}
    
    private void memoria(Vector<MemoryTemplate> result) {
		JsArray<JavaScriptObject> ar = JsArray.createArray().cast();
		for (Integer i = 0 ; i < result.size(); i++) {
			MemoryTemplate memoryTemplate = result.get(i);
			JSONObject json = new JSONObject();
			json.put("id", new JSONNumber(memoryTemplate.getId()));
			json.put("name",new JSONString(memoryTemplate.getName()));
			ar.push(json.getJavaScriptObject());
		}
			
		memoryBox = new AonComboBox();
				
		memoryBox.setItemLabelPath("name");
	    memoryBox.setItemValuePath("name");
	    memoryBox.setItems(ar);
    	memoryBox.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:250px;");
    	memoryBox.setLabel("Memoria Predefinida");
	}
    
    private void sociedades() {    	
    	sociedades = new VerticalPanel();
    	
    	JsArray<JavaScriptObject> ar1 = JsArray.createArray().cast();

		JSONObject json1 = new JSONObject();
		json1.put("name",new JSONString("Balance"));
		ar1.push(json1.getJavaScriptObject());
		
		JSONObject json2 = new JSONObject();
		json2.put("name",new JSONString("Perdidas y Ganancias"));
		ar1.push(json2.getJavaScriptObject());
		
		socBox = new AonComboBox();
		
		socBox.setItemLabelPath("name");
		socBox.setItemValuePath("name");
		socBox.setItems(ar1);
		socBox.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:250px;");
		socBox.setLabel("Impuesto de Sociedades");
		
    	sociedades.add(socBox);
    	
    	JsArray<JavaScriptObject> ar2 = JsArray.createArray().cast();
    	
		JSONObject json3 = new JSONObject();
		json3.put("id", new JSONNumber(parent.getYear()));
		json3.put("name",new JSONString(parent.getYear().toString()));
		ar2.push(json3.getJavaScriptObject());
		
		JSONObject json4 = new JSONObject();
		json4.put("id", new JSONNumber(parent.getYear() - 1));
		json4.put("name",new JSONString(Integer.toString(parent.getYear() - 1)));
		ar2.push(json4.getJavaScriptObject());

		yearBox = new AonComboBox();
		
		yearBox.setItemLabelPath("name");
		yearBox.setItemValuePath("name");
		yearBox.setItems(ar2);
		yearBox.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:250px;");
		yearBox.setLabel("Ejercicio");
		
		sociedades.add(yearBox);
	}
    
    private void d2() {
    	upload = new VaadinUpload();
    	String dataRequest = "?domain_name="+ parent.getAonData().getDomain().getName() 
				+ "&domain_id="+ parent.getAonData().getDomain().getId()
				+ "&login="+ parent.getAonData().getUser().getLogin()
				+ "&year="+ parent.getYear();
		
		upload.setTarget(GWT.getModuleBaseURL() + "uploadD2" + dataRequest);
	}
    
    public void action() {
    	if(tabs.getSelected().toString().equals(ZERO)
				|| tabs.getSelected().toString() == ZERO){
			memoriaAction();
		} else if(tabs.getSelected().toString().equals(ONE)
				|| tabs.getSelected().toString() == ONE){	
	    	sociedadesAction();
		} else if(tabs.getSelected().toString().equals(TWO)
				|| tabs.getSelected().toString() == TWO){
			d2Action();
		}
	}
    
    private void memoriaAction() {
    	JsObject a = memoryBox.getSelectedItem().cast();
    	MemoryTemplate m = null;
    	for(MemoryTemplate mt : mts) {	
    		if(mt.getId().equals(a.getId()) || mt.getId() == a.getId()) m = mt;
    	}
    	if(m != null)
    	
    	parent.getInma().updateTexts(parent.getAonData(),m, parent.getAonData().getDomain().getId(), parent.getCompany().getDocument(), parent.getDeposit(), new AsyncCallback<Map<String, String>>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Map<String, String> result) {
				Map<String, String> m = new HashMap<String, String>();
				for (String k : parent.getDeposit().keySet()) { 
					m.put(k, parent.getDeposit().get(k));
				}
				parent.getUndoStack().push(m);
				parent.getRedoStack().clear();
				
				parent.setDeposit(result);
				parent.getInma().saveDeposit(parent.getAonData(), parent.getDeposit(), parent.getYear(), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						parent.refreshPage();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
	}

    private void sociedadesAction() {
    	JsObject a = socBox.getSelectedItem().cast();
    	JsObject b = yearBox.getSelectedItem().cast();
    	
    	parent.getInma().importAll(parent.getAonData(), a.getName(), b.getName(), null, parent.getCompany().getDocument(), parent.getDeposit(), parent.getYear(), new AsyncCallback<Map<String, String>>() {
			@Override public void onFailure(Throwable caught) {}
					
			@Override
			public void onSuccess(Map<String, String> result) {		
				Map<String, String> m = new HashMap<String, String>();
				for (String k : parent.getDeposit().keySet()) {
					m.put(k, parent.getDeposit().get(k));
				}
				parent.getUndoStack().push(m);
				parent.getRedoStack().clear();
				
				parent.setDeposit(result);
				parent.getInma().saveDeposit(parent.getAonData(), parent.getDeposit(), parent.getYear(), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						parent.refreshPage();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
	}
    
    private void d2Action() {
    	parent.getInma().getSchema(parent.getAonData(), parent.getCompany(), parent.getYear(), false, new AsyncCallback<Map<String, String>>() {

			@Override public void onFailure(Throwable caught) {}

			@Override 
			public void onSuccess(Map<String, String> result) {
				Map<String, String> m = new HashMap<String, String>();
				for (String k : parent.getDeposit().keySet()) {
					m.put(k, parent.getDeposit().get(k));
				}
				parent.getUndoStack().push(m);
				parent.getRedoStack().clear();
				
				parent.setDeposit(result);
				parent.refreshPage();	
			}
		});
	}
    
    
    public AonComboBox getMemoryBox() {
		return memoryBox;
	}
    
    public AonComboBox getYearBox() {
		return yearBox;
	}
    
    public AonComboBox getSocBox() {
		return socBox;
	}
}
