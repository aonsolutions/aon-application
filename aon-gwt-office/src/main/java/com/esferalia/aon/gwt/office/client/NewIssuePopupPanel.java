package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NewIssuePopupPanel extends CustomDialog {

	private static NewIssuePopupPanelUiBinder uiBinder = GWT
			.create(NewIssuePopupPanelUiBinder.class);

	interface NewIssuePopupPanelUiBinder extends
			UiBinder<Widget, NewIssuePopupPanel> {
	}
	
	@UiField
	TextBox titleTextBox;
	@UiField
	TextArea bodyTextArea;
	@UiField
	Button addLabelsButton;
	
	private NewContextMenu newContextMenu;
	

	public NewIssuePopupPanel() {
		
		setCaption("Nueva Incidencia");
		
		setWidget(uiBinder.createAndBindUi(this));
		
		setAnimationEnabled(false);
		setGlassEnabled(true);
		
		newContextMenu = getContextMenu();
		
		initAddLabelsButton();
		
	}
	
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}
	
	public void setTitle (String title) {
		titleTextBox.setText(title);
	}
	
	public void setBodyTextArea (String body) {
		bodyTextArea.setText(body);
	}
	
	public class NewContextMenu extends ContextMenu {
		
		public NewContextMenu() {
			addBugLabel();
			addDuplicateLabel();
			addEnhancementteLabel();
			
			addStyleName(AON.AON_CSS.aonSelector());
		}
		
		public void addItem(LabelValue label, String text, ScheduledCommand cmd) {
			super.addItem(label.getValue(), text, cmd);
		}
		
		protected NewContextMenu addBugLabel() {
			addItem(LabelValue.BUG, 
					AON.MSG.newSomething("BUG"), new ScheduledCommand() {
						
						@Override
						public void execute() {
							// TODO Auto-generated method stub
							
						}						
					});
			return this;
		}
		
		protected NewContextMenu addDuplicateLabel() {
			addItem(LabelValue.BUG, 
					AON.MSG.newSomething("Duplicate - 2"), new ScheduledCommand() {
						
						@Override
						public void execute() {
							// TODO Auto-generated method stub
							
						}						
					});
			return this;
		}

		protected NewContextMenu addEnhancementteLabel() {
			addItem(LabelValue.BUG, 
					AON.MSG.newSomething("Enhancement - 2"), new ScheduledCommand() {
						
						@Override
						public void execute() {
							// TODO Auto-generated method stub
							
						}						
					});
			return this;
		}
	}
	
	private void initAddLabelsButton () {
		addLabelsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				newContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				newContextMenu.show();
			}
		});
	}
	
	private NewContextMenu getContextMenu() {
		if(newContextMenu == null)
			newContextMenu = new NewContextMenu();
		
		return newContextMenu;
	}
	
	
	
	public void showPopUpPanel() {
		center();
	}
	
	@Override
	public void center() {
		super.center();
	}
	
	@Override
	public void show() {
		super.show();
	}

}
