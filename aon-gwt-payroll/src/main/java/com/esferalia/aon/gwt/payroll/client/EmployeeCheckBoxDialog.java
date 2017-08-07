package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCheckBoxDialog extends CustomDialog {
	
	public class CheckBoxLabelWidget extends HorizontalPanel{
		private Label label;
		private CheckBox checkBox;
		
		public CheckBoxLabelWidget(String name, CheckBox newCheckBox){
			checkBox = newCheckBox;
			label = new Label(name);
			this.add(checkBox);
			this.add(label);
		}

		public void setCheckBoxEnsureDebudId(String id) {
			checkBox.ensureDebugId(id);
		}
		
		public String getLabel(){
			return label.getText();
		}
		
		public void setLabel(String text){
			label.setText(text);
		}
		
		@SuppressWarnings("deprecation")
		public Boolean isChecked(){
			return checkBox.isChecked();
		}
		
		@SuppressWarnings("deprecation")
		public void setCheck(Boolean check){
			checkBox.setChecked(check);
		}
			
	}

	interface Binder extends UiBinder<Widget, EmployeeCheckBoxDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Label filterLabel;
	
	@UiField
	FlexTable checkBoxTable;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;

	public EmployeeCheckBoxDialog(int size) {
		setCaption("Nuevo valor");
		
		setWidget(binder.createAndBindUi(this));
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
		//this.valueCheckBox.ensureDebugId("value_checkbox_1");
	}

	protected abstract void onAccept();

	public void setHeaderLabel(String headerText) {
		filterLabel.setText(headerText);
	}

	public Boolean getValueCheck(String var) {
		for(int i=0; i < checkBoxTable.getRowCount(); i++ ){
			CheckBoxLabelWidget widget = (CheckBoxLabelWidget) checkBoxTable.getWidget(i, 0);
			if(widget.getLabel().equals(var))
				return widget.isChecked();
		}
		return false;
	}

	public void setVisibleFilterLabel(boolean bool) {
		filterLabel.setVisible(bool);
	}
	
	public void addNewCheckBox(String name, CheckBox checkBox){
		int newRow = checkBoxTable.insertRow(checkBoxTable.getRowCount());
		
		CheckBoxLabelWidget widget = new CheckBoxLabelWidget(name, checkBox);
		widget.setCheckBoxEnsureDebudId("value_checkbox_"+newRow);
		
		checkBoxTable.insertCell(newRow, 0);
		checkBoxTable.setWidget(newRow, 0, widget);
	}

}
