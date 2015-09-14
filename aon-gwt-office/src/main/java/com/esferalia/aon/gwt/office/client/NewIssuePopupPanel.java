package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
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
	
	private Images images;
	
	private boolean academy = false;
	private boolean bug = false;
	private boolean comercial = false;
	private boolean configuration = false;

	public NewIssuePopupPanel() {
		
		images = GWT.create(Images.class);
		
		

		setCaption("Nueva Incidencia");
		setWidget(uiBinder.createAndBindUi(this));
		setAnimationEnabled(false);
		setGlassEnabled(true);
		
		initAddLabels();

	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}

	public void setTitle(String title) {
		titleTextBox.setText(title);
	}

	public void setBodyTextArea(String body) {
		bodyTextArea.setText(body);
	}
	
	private void initAddLabels() {
		addLabelsButton.addClickHandler(new ClickHandler() {
			
			private PopupPanel popupPanel = new PopupPanel();
			
			private MenuItem academyMenuItem;
			private MenuItem bugMenuItem;
			private MenuItem comercialMenuItem;
			private MenuItem configurationMenuItem;
			
			{
				MenuBar menuBar = new MenuBar(true);	
				
				academyMenuItem = new MenuItem(LabelValue.ACADEMY.getName(), new Command() {
					
					@Override
					public void execute() {
						academy = !academy;
						academyMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, academy);
						academyMenuItem.setStyleName(AON.AON_ICON_OK, academy);

						
					}
				});
				academyMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, academy);
				academyMenuItem.setStyleName(AON.AON_ICON_OK, academy);
				menuBar.addItem(academyMenuItem);
				
				bugMenuItem = new MenuItem(LabelValue.BUG.getName(), new Command() {
					
					@Override
					public void execute() {
						bug = !bug;
						bugMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, bug);
						bugMenuItem.setStyleName(AON.AON_ICON_OK, bug);
					}
				});
				bugMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, bug);
				bugMenuItem.setStyleName(AON.AON_ICON_OK, bug);
				menuBar.addItem(bugMenuItem);
				
				comercialMenuItem = new MenuItem(LabelValue.COMERCIAL.getName(), new Command() {
					
					@Override
					public void execute() {
						comercial = !comercial;
						comercialMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, comercial);
						comercialMenuItem.setStyleName(AON.AON_ICON_OK, comercial);
					}
				});
				comercialMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, comercial);
				comercialMenuItem.setStyleName(AON.AON_ICON_OK, comercial);
				menuBar.addItem(comercialMenuItem);
				
				configurationMenuItem= new MenuItem(LabelValue.CONFIGURACION.getName(), new Command() {
					
					@Override
					public void execute() {
						configuration = !configuration;
						configurationMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, configuration);
						configurationMenuItem.setStyleName(AON.AON_ICON_OK, configuration);
						
					}
				});
				configurationMenuItem.setStyleName(AON.AON_ICON_CMD_BUTTON, configuration);
				configurationMenuItem.setStyleName(AON.AON_ICON_OK, configuration);
				menuBar.addItem(configurationMenuItem);
				
				popupPanel.add(menuBar);
				popupPanel.setStyleName(AON.AON_CSS.aonSelector());
				popupPanel.setAutoHideEnabled(true);
			}
			
			@Override
			public void onClick(ClickEvent event) {
				
				int left = addLabelsButton.getAbsoluteLeft();
				int top = addLabelsButton.getAbsoluteTop() + addLabelsButton.getOffsetHeight();
				
				popupPanel.setPopupPosition(left, top);
				popupPanel.show();
			}
		});
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
