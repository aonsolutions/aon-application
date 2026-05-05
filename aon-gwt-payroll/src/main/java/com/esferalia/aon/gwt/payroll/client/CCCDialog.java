package com.esferalia.aon.gwt.payroll.client;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public class CCCDialog extends AonCustomDialog {
	
	public static interface CCCDialogCallback {
		void onAccept(EnterpriseCCC ccc);
	}
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox regime = new AonCustomTextBox("Cuenta");
	private AonCustomTextBox account = new AonCustomTextBox("");
	private AonCustomTextBox geozone = new AonCustomTextBox("Provincia");
	
	private HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private Button acceptBtnDialog;
	
	private EnterpriseCCC ccc;
	private CCCDialogCallback callback;

	public CCCDialog(Integer domainId, Integer activityId, CCCDialogCallback callback) {
		setCaption("Nuevo CCC");
		this.ccc = new EnterpriseCCC().setDomain(domainId).setEnterpriseActivity(activityId);
		this.callback = callback;
		initView();
	}
	
	public CCCDialog(EnterpriseCCC ccc, CCCDialogCallback callback) {
		setCaption("Editar CCC");
		this.ccc = ccc;
		this.callback = callback;
		initView();
	}

	private void initView() {
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem");
		content.getElement().getStyle().setProperty("min-width", "20rem");
		
		content.add(messagePanel);
		
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
			ccc.setType(AonStringUtils.isBlank(type.getValue()) ? null : Byte.parseByte(type.getValue()));
		});
		content.add(type);
		
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		regime.setEnable(false);
		regime.setWidth("4rem");
		
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
			
				ccc.setCcc(accountValue);
				ccc.setGeozoneCode(provinceCode);
				ccc.setGeozoneDescription(province);
				
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
		
		row.add(regime);
		row.add(account);
		
		content.add(row);
		
		geozone.setEnable(false);
		content.add(geozone);
		
		content.add(createButtonsPanel());
		
		if(null != this.ccc.getId()) {
			type.setValue(null == ccc.getType() ? null : ccc.getType().toString());
			regime.setValue(getCCCRegimeCode(ccc.getType()));
			account.setValue(ccc.getCcc());
			geozone.setValue(ccc.getGeozoneDescription());
		}
		
		Scheduler.get().scheduleDeferred(() -> {
			content.ensureDebugId("cccDialog_Content");
			type.ensureDebugId("cccDialog_Type");
			regime.ensureDebugId("cccDialog_Regime");
			account.ensureDebugId("cccDialog_Account");
			geozone.ensureDebugId("cccDialog_Geozone");
			
			acceptBtnDialog.ensureDebugId("cccDialog_Accept");
			
			setWidget(content);
			center();
			show();
		});
	}
	
	private HTMLPanel createButtonsPanel(){
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonsPanel.getElement().getStyle().setProperty("margin", "1rem");
		
		Button closeBtnDialog = createButton("Cancelar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = createButton(null == ccc.getId() ? "Crear" : "Actualizar");
		acceptBtnDialog.getElement().getStyle().setProperty("color", "green");
		acceptBtnDialog.addClickHandler(e -> {
			acceptBtnDialog.setEnabled(false);
    		
    		saveCCC(ccc -> {
    			hide();
    			callback.onAccept(ccc);
    		}, f -> acceptBtnDialog.setEnabled(true));
		});
		
    	buttonsPanel.add(acceptBtnDialog);
    	
    	return buttonsPanel;
	}
	
	private Button createButton(String text) {
		Button button = new Button(text);
		button.getElement().getStyle().setProperty("background", "none");
		button.getElement().getStyle().setProperty("background-color", "#fafafa");
		button.getElement().getStyle().setProperty("padding", "5px");
		button.getElement().getStyle().setProperty("height", "auto");
		button.getElement().getStyle().setProperty("font-size", "12px");
		button.getElement().getStyle().setProperty("text-transform", "inherit");
		button.getElement().getStyle().setProperty("font-weight", "bold");
		button.getElement().getStyle().setProperty("border", "1px solid #d0d0d0");
		button.getElement().getStyle().setProperty("border-radius", "5px");
		
		return button;
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
	
	private void saveCCC(Consumer<EnterpriseCCC> saved, Consumer<Throwable> error) {
		impl.saveCCC(ccc, new AsyncCallback<EnterpriseCCC>() {
			
			@Override
			public void onSuccess(EnterpriseCCC result) {
				saved.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
				error.accept(caught);
			}
		
		});
	}
	
}
