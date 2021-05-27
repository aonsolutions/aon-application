package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
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
		Analytical getAnalytical();
		void onCreate(AnalyticalCostCenter costCenter);
		void onUpdate(String originalName, AnalyticalCostCenter costCenter);
		void onRemove(AnalyticalCostCenter costCenter);
		void onCancel();
	}
	
	private final AonErrorPanel errorPanel = new AonErrorPanel();
	private String originalName = null;
	private TextBox nameBox = new TextBox();
	private AonDoubleBox percentBox = new AonDoubleBox();
	private CheckBox mainCheck = new CheckBox();
	
	public AnalyticalCostCenterPanel(AnalyticalCostCenter cc, final AnalyticalCostCenterPanelCallback callback) {
		setWidth("500px");
		setHeight("150px");
		
		boolean isNew = (cc==null);
		final AnalyticalCostCenter costCenter = (isNew)?new AnalyticalCostCenter():cc;
		if (isNew) {
			double percent = 100;
			boolean main = false;
			for (AnalyticalCostCenter acc : callback.getAnalytical().getCostCenters().values()) {
				main = main || acc.isMain();
				percent = percent - acc.getPercent();
			}
			costCenter.setMain(!main)
					.setPercent(percent);
		}
		
		originalName = costCenter.getName();
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAll());

				
		table.setWidget(0,0,new InlineLabel(AON.MSG.name()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		 
		nameBox.setValue(costCenter.getName());
		nameBox.setVisibleLength(30);
		nameBox.setMaxLength(30);
		nameBox.setStyleName(AON.CSS.aonInputText());
		nameBox.addKeyUpHandler( keyUpHandler);
		table.setWidget(0,1,nameBox);
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.percent()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		percentBox.setValue(costCenter.getPercent());
		percentBox.addKeyUpHandler( keyUpHandler);
		table.setWidget(1,1,percentBox);
		
		table.setWidget(2,0,new InlineLabel());
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		mainCheck.setText(AON.MSG.defaultMainCostCenter());
		mainCheck.setValue(costCenter.isMain());
		mainCheck.addKeyUpHandler( keyUpHandler);
		table.setWidget(2,1,mainCheck);
		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	buttons.addStyleName(AON.CSS.aonMarginBottom());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				costCenter.setName(nameBox.getValue());
				costCenter.setPercent(percentBox.getValue());
				costCenter.setMain(mainCheck.getValue());
				if (validate(isNew,costCenter )) {
					okButton.setEnabled(false);
					if (isNew) {
						callback.onCreate(costCenter);
					} else {
						callback.onUpdate(originalName,costCenter);
					}
				}
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
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
    		removeButton.setStyleName(AON.CSS.aonCancelButton());
    		removeButton.addStyleName(AON.CSS.aonMarginLeft());
    		removeButton.setText( AON.MSG.deleteAction());
    		removeButton.addKeyUpHandler( keyUpHandler);
    		removeButton.addClickHandler(new ClickHandler() {
    			
    			@Override
    			public void onClick(ClickEvent event) {
    				removeButton.setEnabled(false);
    				AonConfirmDialog cd = new AonConfirmDialog();
    				cd.confirm(AON.MSG.confirmRemoveCostCenter(), new AonConfirmDialogCallback() {
    					
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

	protected boolean validate(boolean isNew, AnalyticalCostCenter costCenter) {
		boolean ret = true;
		errorPanel.initialize();
		if ( AonMathUtils.isLessThanZero( costCenter.getPercent()) || AonMathUtils.isGreatherThan(costCenter.getPercent(),100) ) {
			errorPanel.addError("Porcentaje incorrecto");
			setHeight("160px");
			ret = false;
		}
		if ( AonStringUtils.isBlank( AonStringUtils.trim(costCenter.getName()))) {
			errorPanel.addError("El nombre del centro de costo no es v\u00E1lido");
			setHeight(ret?"160px":"200px");
			ret = false;
		}
		if (!ret) {
			errorPanel.show();
		}
		return ret;
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
