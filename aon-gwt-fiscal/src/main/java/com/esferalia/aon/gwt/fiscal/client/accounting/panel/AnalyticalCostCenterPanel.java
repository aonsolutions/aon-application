package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class AnalyticalCostCenterPanel extends SimplePanel implements Focusable {
	
	public static interface AnalyticalCostCenterPanelCallback {
		void onCreate(AnalyticalCostCenter costCenter);
		void onUpdate(String originalName, AnalyticalCostCenter costCenter);
		void onRemove(AnalyticalCostCenter costCenter);
		void onCancel();
	}

	private String originalName = null;
	private TextBox nameBox = new TextBox();
	private DoubleBox percentBox = new DoubleBox();
	private CheckBox mainCheck = new CheckBox();
	
	public AnalyticalCostCenterPanel(AnalyticalCostCenter cc, final AnalyticalCostCenterPanelCallback callback) {
		setWidth("500px");
		setHeight("120px");
		
		boolean isNew = (cc==null);
		AnalyticalCostCenter costCenter = cc==null?new AnalyticalCostCenter().setMain(true):cc;
		originalName = costCenter.getName();
		FlowPanel rootPanel = new FlowPanel();
		
		final ErrorPanel errorPanel = new ErrorPanel();
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());

				
		table.setWidget(0,0,new InlineLabel(AON.MSG.name()));
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridOdd());
		 
		nameBox.setValue(costCenter.getName());
		nameBox.setVisibleLength(30);
		nameBox.setMaxLength(30);
		nameBox.setStyleName(AON.AON_CSS.aonInputText());
		nameBox.addKeyUpHandler( keyUpHandler);
		table.setWidget(0,1,nameBox);
		table.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.percent()));
		table.getCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonPanelGridOdd());
		percentBox.setValue(costCenter.getPercent());
		percentBox.addKeyUpHandler( keyUpHandler);
		table.setWidget(1,1,percentBox);
		table.getCellFormatter().setStyleName(1, 1, AON.AON_CSS.aonPanelGridEven());
		
		table.setWidget(2,0,new InlineLabel());
		table.getCellFormatter().setStyleName(2, 0, AON.AON_CSS.aonPanelGridOdd());
		mainCheck.setText(AON.MSG.defaultMainCostCenter());
		mainCheck.setValue(costCenter.isMain());
		mainCheck.addKeyUpHandler( keyUpHandler);
		table.setWidget(2,1,mainCheck);
		table.getCellFormatter().setStyleName(2, 1, AON.AON_CSS.aonPanelGridEven());
		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.AON_CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				costCenter.setName(nameBox.getValue());
				costCenter.setPercent(percentBox.getValue());
				costCenter.setMain(mainCheck.getValue());
				if (isNew) {
					callback.onCreate(costCenter);
				} else {
					callback.onUpdate(originalName,costCenter);
				}
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);

    	if (!isNew) {
    		final Button removeButton = new Button();
    		removeButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    		removeButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    		removeButton.setText( AON.MSG.deleteAction());
    		removeButton.addKeyUpHandler( keyUpHandler);
    		removeButton.addClickHandler(new ClickHandler() {
    			
    			@Override
    			public void onClick(ClickEvent event) {
    				removeButton.setEnabled(false);
    				ConfirmDialog cd = new ConfirmDialog();
    				cd.confirm(AON.MSG.confirmRemoveCostCenter(), new ConfirmDialogCallback() {
    					
    					@Override
    					public void onCancel() {
    					}
    					
    					@Override
    					public void onAccept() {
    						callback.onRemove(costCenter);
    					}
    				});
    			}
    		});
    		buttons.add(removeButton);
    	}
    	
    	rootPanel.add(buttons);
		setWidget(rootPanel);
	}

	@Override
	public int getTabIndex() {
		return nameBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		nameBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		nameBox.selectAll();
		nameBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		nameBox.setTabIndex(index);
	}

}
