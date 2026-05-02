package com.esferalia.aon.gwt.payroll.client;

import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AonCCCPanel extends SimplePanel {
	
	public static interface AonCCCPanelCallback {
		void onAccept(EnterpriseCCC ccc);
		void onCancel();
	}

	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	// Message Panel
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	// CCC
	private AonCustomTextBox activityName = new AonCustomTextBox("Actividad");
	private AonCustomListBox activitySelect = new AonCustomListBox("Actividad");
	
	private AonCustomListBox cccRegimeSelect = new AonCustomListBox("Tipo");
	
	private AonCustomTextBox regimeCode = new AonCustomTextBox("Cuenta");
	private AonCustomTextBox account = new AonCustomTextBox(null);
	private AonCustomTextBox geozone = new AonCustomTextBox(null);
	
	// Variables
	private Integer domain;
	private Set<Entry<Integer, String>> activities;
	
	public AonCCCPanel(Integer domain, Set<Entry<Integer, String>> activities, AonCCCPanelCallback callback) {
		this.domain = domain;
		this.activities = activities;
		
		show(new EnterpriseCCC(), callback);
	}
	
	public AonCCCPanel(Integer domain, Set<Entry<Integer, String>> activities, EnterpriseCCC ccc, AonCCCPanelCallback callback) {
		this.domain = domain;
		this.activities = activities;
		
		show(ccc, callback);
	}

	public void show(EnterpriseCCC ccc, AonCCCPanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		rootPanel.add(messagePanel);
		
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

		FlexTable table1 = new FlexTable();
		table1.setStyleName(AON.CSS.aonTable());
		table1.setWidth("100%");
		
		// Activity
		
		activities.forEach(entry -> activitySelect.addItem(entry.getValue(), entry.getKey().toString()));
		table1.setWidget(0, 0, activities.isEmpty() ? activityName : activitySelect);
		table1.getFlexCellFormatter().setColSpan(0, 0, 3);
		
		// CCCC
		
		HTMLPanel cccPanel = new HTMLPanel("");
		cccPanel.addStyleName(AON.CSS.aonItemFlex());
		
		cccRegimeSelect.addItem("Principal", "0");
		cccRegimeSelect.addItem("Formacion y aprendizaje", "1");
		cccRegimeSelect.addItem("Aprendizaje", "2");
		cccRegimeSelect.addItem("Representantes de comercio", "3");
		cccRegimeSelect.addItem("Asimilados R.General", "4");
		cccRegimeSelect.addItem("Becarios", "5");
		cccRegimeSelect.addItem("Emplead@s de hogar", "6");
		cccRegimeSelect.addItem("Trabajadores cuenta ajena agrarios", "7");
		cccRegimeSelect.addItem("Artistas", "8");
		cccRegimeSelect.getListBox().getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		
		regimeCode.setEnable(false);
		regimeCode.setValue(getCCCRegimeCode(Byte.parseByte(cccRegimeSelect.getValue())));
		regimeCode.getElement().getStyle().setProperty("max-width", "3rem");
		
		cccRegimeSelect.getListBox().addChangeHandler(e -> regimeCode.setValue(getCCCRegimeCode(Byte.parseByte(cccRegimeSelect.getValue()))));
		
		geozone.setEnable(false);
		geozone.getTextBox().getElement().setPropertyString("placeholder", "Provincia CCC");
		
		account.getElement().getStyle().setProperty("max-width", "6rem");
		account.getTextBox().setMaxLength(11);
		account.getTextBox().getElement().setPropertyString("placeholder", "CCC");
		account.getTextBox().addKeyPressHandler(e -> {
			 if (!Character.isDigit(e.getCharCode())) account.getTextBox().cancelKey();
		});
		account.getTextBox().addValueChangeHandler(e -> {
			String accountValue = e.getValue();
			if(!AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
				String provinceCode = accountValue.substring(0, 2);
				String province = ProvinceContract.getName(provinceCode);
				geozone.setValue(province);
				
				if(!checkCCC(accountValue)) AonMessagePanel.showWarning(messagePanel, "La cuenta de cotizaci\u00f3n no es correcta.");
			}
		});
		
		cccPanel.add(regimeCode);	
		cccPanel.add(account);	
		
		table1.setWidget(1, 0, cccRegimeSelect);
		table1.setWidget(1, 1, cccPanel);
		table1.setWidget(1, 2, geozone);
		
		if(ccc.getId() != null) {
			activitySelect.setValue(ccc.getEnterpriseActivity().toString());
			cccRegimeSelect.setValue(ccc.getType().toString());
			regimeCode.setValue(getCCCRegimeCode(ccc.getType()));
			account.setValue(ccc.getCcc());
			geozone.setValue(ccc.getGeozoneDescription());
		} 
		
		tablePanel.add( table1 );
		
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				if(AonStringUtils.isBlank(account.getValue()) || account.getValue().length() < 2) {
					AonMessagePanel.showWarning(messagePanel, "La cuenta de cotizaci\u00f3n es obligatoria.");
					okButton.setEnabled(true);
				} else {
					// Create EnterpriseCCC
					String geozoneCode = account.getValue().substring(0, 2);
					
					if(ccc.getId() != null) {
						
						ccc.setEnterpriseActivity(Integer.parseInt(activitySelect.getValue()))
							.setType(Byte.parseByte(cccRegimeSelect.getValue()))
							.setCcc(account.getValue())
							.setGeozoneCode(geozoneCode)
							.setGeozoneDescription(ProvinceContract.getName(geozoneCode));
						
						callback.onAccept(ccc);
						
					} else {
						EnterpriseCCC newCCC = new EnterpriseCCC()
								.setId(null)
								.setDomain(domain)
								.setEnterpriseActivity(Integer.parseInt(activitySelect.getValue()))
								.setType(Byte.parseByte(cccRegimeSelect.getValue()))
								.setCcc(account.getValue())
								.setGeozone(null)
								.setGeozoneCode(geozoneCode)
								.setGeozoneDescription(ProvinceContract.getName(geozoneCode))
								.setDeleted(false)
								.setUseByContracts(false)
								.setUseByCra(false);
						
						callback.onAccept(newCCC);
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
    	rootPanel.add(buttons);
		setWidget(rootPanel);
	}
	
	private boolean checkCCC(String ccc) {
		if(AonStringUtils.isBlank(ccc) || ccc.length() != 11) return false;
				
		String code = ccc.substring(ccc.length()-2, ccc.length());
		Integer codeInt = Integer.parseInt(code);
		
		String cccStr = ccc.substring(2, ccc.length()-2);
		if(cccStr.startsWith("0"))
			cccStr = ccc.substring(3, ccc.length()-2);
		cccStr =  ccc.substring(0, 2) + cccStr;
		
		Integer cccInt = Integer.parseInt(cccStr);
		
		return cccInt % 97 == codeInt;
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}

	protected abstract void onResize();

}
