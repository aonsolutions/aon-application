package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonEnterpriseCCCPanel extends HTMLPanel {

	// Callback

	public static interface AonEnterpriseCCCPanelCallback {
		void onAccept(EnterpriseCCC ccc);

		void onCancel();
	}

	// Variables

	private final static String EMPTY_STRING = "";

	private AonEnterpriseCCCPanelCallback callback;
	private EnterpriseCCC enterpriseCCCC;

	// Wrokplace Info

	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);

	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox regime = new AonCustomTextBox("Cuenta");
	private AonCustomTextBox account = new AonCustomTextBox("");
	private AonCustomTextBox geozone = new AonCustomTextBox("Provincia");

	// Constructor

	public AonEnterpriseCCCPanel(Integer domain, Integer enterpriseActivity, AonEnterpriseCCCPanelCallback callback) {
		super(EMPTY_STRING);

		this.enterpriseCCCC = new EnterpriseCCC().setDomain(domain).setEnterpriseActivity(enterpriseActivity);
		this.callback = callback;

		show();
	}

	public AonEnterpriseCCCPanel(EnterpriseCCC enterpriseCCCC, AonEnterpriseCCCPanelCallback callback) {
		super(EMPTY_STRING);

		this.enterpriseCCCC = enterpriseCCCC;
		this.callback = callback;

		show();
	}

	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);

		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");

		// Row 1
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());

		type.clearItems();
		type.addItem("-", "");
		type.addItem("Principal", "0");
		type.addItem("Formacion y aprendizaje", "1");
		type.addItem("Aprendizaje", "2");
		type.addItem("Representantes de comercio", "3");
		type.addItem("Asimilados R.General", "4");
		type.addItem("Becarios", "5");
		type.addItem("Emplead@s de hogar", "6");
		type.addItem("Trabajadores cuenta ajena agrarios", "7");
		type.addItem("Artistas", "8");
		type.addChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(type.getValue())) regime.setValue(getCCCRegimeCode(Byte.parseByte(type.getValue())));
			enterpriseCCCC.setType(AonStringUtils.isBlank(type.getValue()) ? null : Byte.parseByte(type.getValue()));
		});

		row.add(type);
		container.add(row);
		
		// Row 1
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());

		regime.setEnable(false);
		regime.setWidth("5rem");
		
		account.addValueChangeHandler(e -> {
			String accountValue = e.getValue();
			
			if(!AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
				String province = ProvinceContract.getName(accountValue.substring(0, 2));
				String provinceCode = accountValue.substring(0, 2);
				if(checkCCC(accountValue)) {
					geozone.removeWarning();
					account.removeWarning();
				}else {
					geozone.addWarning();
					account.addWarning();
					AonMessagePanel.showWarning(messagePanel, "El CCC " + accountValue + " no es correcto, rev\u00EDselo por favor");
				}
			
				enterpriseCCCC.setCcc(accountValue);
				enterpriseCCCC.setGeozoneCode(provinceCode);
				enterpriseCCCC.setGeozoneDescription(province);
				
				geozone.setValue(province);
			}
		});
		account.getTextBox().addKeyPressHandler(e -> {
			char keyCode = e.getCharCode();
	        if (!Character.isDigit(keyCode)) {
	        	AonMessagePanel.showWarning(messagePanel, "La cuenta de cotizac\u00f3n solo puede contener n\u00fameros");
	        	account.getTextBox().cancelKey();
	        }
		});

		row2.add(regime);
		row2.add(account);
		container.add(row2);

		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());

		geozone.setEnable(false);
		
		row3.add(geozone);
		container.add(row3);
		
		// Fill info
		if(null != this.enterpriseCCCC.getId()) {
			type.setValue(null == enterpriseCCCC.getType() ? null : enterpriseCCCC.getType().toString());
			regime.setValue(getCCCRegimeCode(enterpriseCCCC.getType()));
			account.setValue(enterpriseCCCC.getCcc());
			geozone.setValue(enterpriseCCCC.getGeozoneDescription());
		}

		// Buttons
		container.add(createButtonsPanel());
		add(container);
	}
	
	public static String getCCCRegimeCode(Byte cccRegime) {
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
	
	private boolean checkCCC(String ccc) {
		if(AonStringUtils.isBlank(ccc) || ccc.length() != 11) return false;
				
		String code = ccc.substring(ccc.length() - 2, ccc.length());
		Integer codeInt = Integer.parseInt(code);
		
		String cccStr = ccc.substring(2, ccc.length() - 2);
		if(cccStr.startsWith("0"))
			cccStr = ccc.substring(3, ccc.length()-2);
		cccStr =  ccc.substring(0, 2) + cccStr;
		
		Integer cccInt = Integer.parseInt(cccStr);
		
		return cccInt % 97 == codeInt;
	}

	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");

		Button okButton = new Button();
		okButton.setStyleName(AON.CSS.aonOkButton());
		okButton.setText(AON.MSG.accept());
		okButton.addClickHandler(e -> {
			okButton.setEnabled(false);
			callback.onAccept(enterpriseCCCC);
		});
		buttonsPanel.add(okButton);

		final Button cancelButton = new Button();
		cancelButton.setStyleName(AON.CSS.aonCancelButton());
		cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.addClickHandler(e -> {
			cancelButton.setEnabled(false);
			callback.onCancel();
		});
		buttonsPanel.add(cancelButton);

		return buttonsPanel;
	}

}
