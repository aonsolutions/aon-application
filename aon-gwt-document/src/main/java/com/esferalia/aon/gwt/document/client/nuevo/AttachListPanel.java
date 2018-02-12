package com.esferalia.aon.gwt.document.client.nuevo;


import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.issues.client.AonDialog2;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronList;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperToggleButton;

import net.aonsolutions.polymer.aon.widget.AonComboBox;


public class AttachListPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, AttachListPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList grid;
    Documental parent;
    Integer top = 0;
    
    public AttachListPanel(Documental parent, AonJsArray<JsAttach> items) {    
    	this.parent = parent;
    	exportDownload(this);
    	exportEdit(this);
    	exportRemove(this);
        initWidget(binder.createAndBindUi(this));
        
        grid.setItems(items);
      
        autoHeight(grid, 260);
        
        grid.addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = grid.getElement().getScrollTop();
				Integer offsetHeight = grid.getElement().getOffsetHeight();
				Integer physicalSize = grid.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				/*if(scrollTop > maxScrollPosition && parent.more){
					parent.issueFilter.setPage(parent.issueFilter.getPage()+1);
					parent.updateIssueList(parent.issueFilter, true);		
					top = scrollTop;
				}*/
			}
		}, ScrollEvent.getType());

    }
    
   public void updateItems(AonJsArray<JsIssue> issues){
	   grid.setItems(issues);
	   grid.getElement().setScrollTop(top);
	   top = 0;
   }
   
   public AonJsArray<JsIssue> getItems(){
	   return grid.getItems().cast();
   }
   
   public static native int getPhysicalSize(IronList i) /*-{ 
   		return i._physicalSize;
 	}-*/;
   

   public void autoHeight(Widget widget, Integer value){
	   widget.getElement().getStyle().setHeight(Window.getClientHeight() - value, Unit.PX);
	   Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				widget.getElement().getStyle().setHeight(Window.getClientHeight() - value, Unit.PX);
			}
		});
   }
   
   public void download(String id){
	   parent.getAPI().getAttachment().download(id);
   }

   public static native void exportDownload(AttachListPanel thiz) /*-{
		$wnd.download = function(id) {
			thiz.@com.esferalia.aon.gwt.document.client.nuevo.AttachListPanel::download(*)(id);
		}
	}-*/;
   
   public void edit(String id){
	   PaperInput nameBox = new PaperInput();
	   nameBox.setLabel("Nombre");
	   nameBox.setWidth("100%");
	   nameBox.setList("as");
	   
	   AonComboBox categoryBox = new AonComboBox();
	   categoryBox.setLabel("Categor\u00eda");
	   categoryBox.setWidth("100%");
	   categoryBox.setItemLabelPath("name");
	   categoryBox.setItemValuePath("name");
	   parent.getAPI().getAttachment().getCategories(new AsyncCallback<JSON<JsLabel>>() {
				
		   @Override
		   public void onSuccess(JSON<JsLabel> result) {
			   categoryBox.setItems(result.getData());
			}
				
		   @Override public void onFailure(Throwable caught) {}
		});
			
		AonComboBox tagBox = new AonComboBox();
		tagBox.setWidth("100%");
		tagBox.setLabel("Etiqueta");
		tagBox.setItemLabelPath("name");
		tagBox.setItemValuePath("name");
		parent.getAPI().getAttachment().getTags(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				tagBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		AonComboBox scopeBox = new AonComboBox();
		scopeBox.setWidth("100%");
		scopeBox.setLabel("\u00c1mbito");
		scopeBox.setItemLabelPath("name");
		scopeBox.setItemValuePath("name");
		parent.getAPI().getAttachment().getScopes(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				scopeBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		HorizontalPanel hp = new HorizontalPanel();
		Label confidentialLabel = new Label("Confidencial");
		confidentialLabel.getElement().getStyle().setPaddingTop(20, Unit.PX);
		confidentialLabel.getElement().getStyle().setPaddingRight(10, Unit.PX);
		confidentialLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);		
		hp.add(confidentialLabel);
				
		PaperToggleButton confidential = new PaperToggleButton();
		confidential.getElement().getStyle().setPaddingTop(13, Unit.PX);
		hp.add(confidential);
		
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(nameBox);
		vp.add(categoryBox);
		vp.add(tagBox);
		vp.add(scopeBox);
		vp.add(hp);
		AonDialog dialog = new AonDialog("Editar Archivo", vp) {
			
			@Override protected void onCancel() {hide();}
			
			@Override
			protected void onAccept() {

			}	
		};
			
		dialog.setAutoHideEnabled(true);
		dialog.addAutoHidePartner(categoryBox.getElementById("overlay"));
		dialog.addAutoHidePartner(tagBox.getElementById("overlay"));
		dialog.addAutoHidePartner(scopeBox.getElementById("overlay"));
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
   }

   public static native void exportEdit(AttachListPanel thiz) /*-{
		$wnd.edit = function(id) {
			thiz.@com.esferalia.aon.gwt.document.client.nuevo.AttachListPanel::edit(*)(id);
		}
	}-*/;
   
   public void remove(String id) {
		AonDialog2 d = new AonDialog2("Borrar Tarea",new Label("Est\u00e1s seguro de Borrar definitivamente este fichero") ) {
			
			@Override protected void onCancel() {hide();}
			
			@Override
			protected void onAccept() {
				String requestData = "{\"id\":"+ id + ","
						+ "\"attach_type\":\"registry\"}";
				parent.getAPI().getAttachment().removeAttach(requestData, new AsyncCallback<JSON<JsAttach>>() {
					
					@Override
					public void onSuccess(JSON<JsAttach> result) {
						// TODO ACTUALIZAR LIST!!!
						hide();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		d.getElement().getStyle().setWidth(255, Unit.PX);
		d.center();
   }

   public static native void exportRemove(AttachListPanel thiz) /*-{
		$wnd.remove = function(id) {
			thiz.@com.esferalia.aon.gwt.document.client.nuevo.AttachListPanel::remove(*)(id);
		}
	}-*/;
}
