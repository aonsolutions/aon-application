package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class SecondaryUserDialog extends AonCustomDialog {
	
	// -------------------------------------------------- UI BINDER
	
	interface Binder extends UiBinder<Widget, SecondaryUserDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// -------------------------------------------------- UI FIELDS
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String toolbar();
		String pr20();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	TableElement secondaryUserTable;
	
	@UiField
	Button dniBtn;
	
	@UiField
	TextBox nss; 
	
	@UiField
	TextBox nie; 
	
	@UiField
	TextBox surname; 
	
	@UiField
	TextBox secondSurname; 
	
	@UiField
	TextBox name; 
	
	@UiField
	HTMLPanel mainButtonsPanel;
	
	@UiField
	HTMLPanel loadingPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------------- VARIABLES

	private final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private Button acceptBtnDialog;
	private boolean isDNI = false;
	private Integer rattachId;

	// -------------------------------------------------- CONSTRUCTOR
	
	protected SecondaryUserDialog(Integer rattachId) {
		setCaption("Alta usuario secundario");
		
		setWidget(binder.createAndBindUi(this));
		
		this.rattachId = rattachId;
		getButtonsPanel();
		this.secondaryUserTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		loadingPanel.setVisible(false);
		initTableStyles();
		initView();
	}
	
	protected SecondaryUserDialog(Integer rattachId, String naf) {
		setCaption("Alta usuario secundario");
		
		setWidget(binder.createAndBindUi(this));
		
		this.rattachId = rattachId;
		getButtonsPanel();
		this.secondaryUserTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		loadingPanel.setVisible(false);
		initTableStyles();
		initView();
		nss.setValue(naf, true);
	}
	
	// -------------------------------------------------- UI HANDLERS
	
	@UiHandler("dniBtn")
	public void onDniBtnClick(ClickEvent event) {
		isDNI = !isDNI;
		if(isDNI) 
			this.secondaryUserTable.getRows().getItem(1).getStyle().clearDisplay();
		else
			this.secondaryUserTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		
		initView();
	}
	
	@UiHandler("nss")
	public void onNSSValueChange(ValueChangeEvent<String> event) {
		if(AonStringUtils.isNotBlank(event.getValue())) {
			ArrayList<String> nssList = new ArrayList<>();
			nssList.add(event.getValue());
			acceptBtnDialog.setEnabled(false);
			loadingPanel.setVisible(true);
			impl.getIpfxNaf(nssList, new AsyncCallback<EmployeeSegSocial>() {
				
				@Override
				public void onSuccess(EmployeeSegSocial result) {
					initEmployeeSegSocial(result);
					acceptBtnDialog.setEnabled(true);
					loadingPanel.setVisible(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					showWarning("Error Ipf x Naf", caught.getMessage());
				}
			});
		}
	}
	
	@UiHandler("nie")
	public void onNIEValueChange(ValueChangeEvent<String> event) {
			checkCanFindByIPF();
	}
	
	@UiHandler("surname")
	public void onSurnameValueChange(ValueChangeEvent<String> event) {
			checkCanFindByIPF();
	}
	
	@UiHandler("secondSurname")
	public void onSecondSurnameValueChange(ValueChangeEvent<String> event) {
			checkCanFindByIPF();
	}
	
	// -------------------------------------------------- UI HANDLERS (AUX METHODS)
	
	private void checkCanFindByIPF() {
		String nieStr = nie.getValue();
		String surnameStr = surname.getValue();
		String secondSurnameStr = secondSurname.getValue();
		
		if(	AonStringUtils.isNotBlank(nieStr) &&
			AonStringUtils.isNotBlank(surnameStr) &&
			AonStringUtils.isNotBlank(secondSurnameStr)) {
			
			loadingPanel.setVisible(true);
			acceptBtnDialog.setEnabled(false);
			
			impl.getNafxIpf(nieStr, surnameStr, secondSurnameStr, new AsyncCallback<EmployeeSegSocial>() {
				
				@Override
				public void onSuccess(EmployeeSegSocial result) {
					initEmployeeSegSocial(result);
					acceptBtnDialog.setEnabled(true);
					loadingPanel.setVisible(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					showWarning("Error Naf x Ipf", caught.getMessage());
				}
			});
		}
	}
	
	// -------------------------------------------------- AUX METHODS
	
	private void initTableStyles() {
		for(int i=0; i<this.secondaryUserTable.getRows().getLength(); i++)
			this.secondaryUserTable.getRows().getItem(i).getStyle().setMarginTop(5, Unit.PX);
	}

	private void initEmployeeSegSocial(EmployeeSegSocial employeeSegSocial) {
		this.nss.setText(employeeSegSocial.getNss());
		if(AonStringUtils.isBlank(this.nie.getValue())) this.nie.setText(employeeSegSocial.getIpf());
		this.name.setText(employeeSegSocial.getName());
		this.secondaryUserTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
	}
	
	private void initView() {
		getEnableDisableButton(isDNI);
		nss.setText("");
		nie.setText("");
		surname.setText("");
		secondSurname.setText("");
		name.setText("");
		nss.getElement().setPropertyString("placeholder", "NSS/NAF");
		nie.getElement().setPropertyString("placeholder", "NIE/DNI");
		surname.getElement().setPropertyString("placeholder", "1er Apellido");
		secondSurname.getElement().setPropertyString("placeholder", "2do Apellido"); 
		name.getElement().setPropertyString("placeholder", "NOMBRE COMPLETO");
		nss.setEnabled(!isDNI);
		nie.setEnabled(isDNI);
		surname.setEnabled(isDNI);
		secondSurname.setEnabled(isDNI);
	}
	
	private void getEnableDisableButton(boolean isDNI) {
		dniBtn.setStyleName(!isDNI ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		dniBtn.setStyleName(AON.AON_NO_MARGIN, true);
		dniBtn.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "1";
		else
			return "6";
	}

	private void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messagePanel, warningMap);
	}
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	// -------------------------------------------------- BUTTONS PANEL
	
	private void getButtonsPanel() {
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingPanel.add(loadingBtn);
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonSendButtonSmall());
		acceptBtnDialog.setText( "COMUNICAR" );
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onAcceptDialog() {
		String nieStr = nie.getValue();
		String nafStr = nss.getValue();
		
		if(AonStringUtils.isNotBlank(nieStr) && AonStringUtils.isNotBlank(nafStr))
			impl.createSecondaryUser(this.rattachId, checkIPFType(nieStr), nieStr, nafStr, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						hide();
						onAccept();
					}
	
					@Override
					public void onFailure(Throwable caught) {
						showError("ERROR: Creacion", caught.getMessage());
					}
				});
	}
	
	// -------------------------------------------------- ABSTRACT METHODS

	protected abstract void onAccept();

}
