package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.polymer.iron.widget.IronList;

public abstract class AonListDialog   extends PopupPanel {
	
    interface Binder extends UiBinder<HTMLPanel, AonListDialog> {}
  
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField TextBox textBox;
    @UiField IronList list;
    
    Integer top = 0;

	public static final AonGwtIssuesCSS CSS = GWT.<AonGwtIssuesResources> create(AonResources.class).css();

	protected abstract void onSelect(JavaScriptObject item);

	protected abstract void onFilter(String filter);

	public AonListDialog(AonJsArray<JsLabel> labels
			, AonJsArray<JsUser> users) {
		setWidget(binder.createAndBindUi(this));
		
		textBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onFilter(textBox.getText());
			}
		});
		textBox.setFocus(true);
		if(labels != null ){
			list.setItems(labels);
		} else if(users != null ){
			list.setItems(users);
		}
		
		list.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				JavaScriptObject item = list.getSelectedItem().cast();
				onSelect(item);
			}
		});
		
		list.addDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				// TODO
			}
		}, MouseOverEvent.getType());
		
		list.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				// TODO 
			}
		}, MouseOutEvent.getType());
		
/*		VerticalPanel vp = new VerticalPanel();
		TextBox tb = new TextBox();
		vp.add(tb);
		
		if(labels != null ){
			LinkedList<JsLabel> labelList =  labels.toLinkedList();
			for (JsLabel jsLabel : labelList) {
				Label label = new Label(jsLabel.getName());
				label.addClickHandler(new ClickHandler() {
				
					@Override
					public void onClick(ClickEvent event) {
						onSelect(jsLabel);
					}
				});
				vp.add(label);
			}
		}else if(users != null ){
			LinkedList<JsUser> userList =  users.toLinkedList();
			for (JsUser jsUser :userList) {
				Label label = new Label(jsUser.getLogin());
				label.addClickHandler(new ClickHandler() {
				
					@Override
					public void onClick(ClickEvent event) {
						onSelect(jsUser);
					}
				});
				vp.add(label);
			}
		}
		panel.add(vp);
	*/
	}
	
	public void updateLabels(AonJsArray<JsLabel> labels){
		list.setItems(labels);
		list.getElement().setScrollTop(top);
		top = 0;
	}
	
	public void updateUsers(AonJsArray<JsUser> users){
		list.setItems(users);
		list.getElement().setScrollTop(top);
		top = 0;
	}
}
