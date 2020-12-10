package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, SecondaryUserDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String toolbar();
	}
	
	@UiField
	HTMLPanel toolBarPanel;
	
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
	HTMLPanel buttonsPanel;
	
	private boolean isDNI = false;
	
	private AonToolbar toolbar;
	private AonToolbarButton refresh;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;

	// -------------------------------------------------------------------------------------------
	// ----------------------------------- CONSTRUCTOR -------------------------------------------
	// -------------------------------------------------------------------------------------------
	
	public SecondaryUserDialog() {
		setCaption("Usuario Secundario");
		
		setWidget(binder.createAndBindUi(this));
		
		toolbar = getToolbarPanel();
		toolBarPanel.add(toolbar);
		
		getButtonsPanel();
		this.secondaryUserTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		initTableStyles();
		initView();
	}
	
	private void initTableStyles() {
		for(int i=0; i<this.secondaryUserTable.getRows().getLength(); i++){
			this.secondaryUserTable.getRows().getItem(i).getStyle().setMarginTop(5, Unit.PX);
		}
		
	}

	@UiHandler("dniBtn")
	public void onDniBtnClick(ClickEvent event) {
		isDNI = !isDNI;
		if(isDNI) this.secondaryUserTable.getRows().getItem(1).getStyle().clearDisplay();
		initView();
	}
	
	@UiHandler("nss")
	public void onNSSValueChange(ValueChangeEvent<String> event) {
		if(AonStringUtils.isNotBlank(event.getValue())) {
			ArrayList<String> nssList = new ArrayList<String>();
			nssList.add(event.getValue());
			impl.getIpfxNaf(nssList, new AsyncCallback<EmployeeSegSocial>() {
				
				@Override
				public void onSuccess(EmployeeSegSocial result) {
					initEmployeeSegSocial(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
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
	
	private void checkCanFindByIPF() {
		String nieStr = nie.getValue();
		String surnameStr = surname.getValue();
		String secondSurnameStr = secondSurname.getValue();
		
		if(	AonStringUtils.isNotBlank(nieStr) &&
			AonStringUtils.isNotBlank(surnameStr) &&
			AonStringUtils.isNotBlank(secondSurnameStr)) {
			
			impl.getNafxIpf(nieStr, surnameStr, secondSurnameStr, new AsyncCallback<EmployeeSegSocial>() {
				
				@Override
				public void onSuccess(EmployeeSegSocial result) {
					initEmployeeSegSocial(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}
			});
		}
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
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Busqueda");

		refresh = new AonToolbarButton( "Reiniciar Busqueda", AON.CSS.aonIconRefresh() );
		refresh.setAccessKey('R');
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onRefresh(event);
			}
		});
		toolbar.add(refresh);
		toolbar.addStyleName(style.toolbar());

		return toolbar;
	}
	
	private void onRefresh(ClickEvent event) {
		isDNI = false;
		this.secondaryUserTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		initView();
	}

	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonSendButtonSmall());
		acceptBtnDialog.setText( "COMUNICAR" );
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		String nieStr = nie.getValue();
		String nafStr = nss.getValue();
		
		if(AonStringUtils.isNotBlank(nieStr) && AonStringUtils.isNotBlank(nafStr))
			impl.createSecondaryUser(checkIPFType(nieStr), nieStr, nafStr, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						hide();
						onAccept();
					}
	
					@Override
					public void onFailure(Throwable caught) {
						AonConfirmDialog dialog = new AonConfirmDialog();
						dialog.info("ERROR: Creacion", "El usuario secundario no se ha podido crear.");
						hide();
					}
				});
	}

	protected abstract void onAccept();
	
	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "1";
		else
			return "2";
	}

}
