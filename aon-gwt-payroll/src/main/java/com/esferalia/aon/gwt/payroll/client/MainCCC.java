package com.esferalia.aon.gwt.payroll.client;

import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainCCC extends MainEntryPoint{

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, MainCCC> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerStyle();
		String warningColor();
	}
	
	@UiField
	Button saveButton;
	
	@UiField
	Grid cccDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid cccDataTable;
	
	@UiField
	Label newCCC;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private MainCCCObject mainCCCObject;
	private Integer newId;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public MainCCC() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		this.newId = 0;
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("saveButton")
	public void onSaveClick(ClickEvent event) {
		this.mainCCCObject.setMainCCCInfo(s -> {
			initPreview();
			insertRows();
		}, f -> {});
	}
	
	@UiHandler("newCCC")
	public void onNewCCC(ClickEvent event) {
		if(0 != cccDataTable.getRowCount()) {
			Label firstGeozone = (Label) cccDataTable.getWidget(0, 3);
			if(null != firstGeozone && "" != firstGeozone.getText()) {
				insertNewRow();
			}
		}else
			insertNewRow();
	}

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void onModuleLoad(MainCCCObject mainCCCObject) {
		this.mainCCCObject = mainCCCObject;
		this.mainCCCObject.getMainCCCInfo(
				s -> {
					initPreview();
					insertRows();
				}, f -> {});
	}

	private void initPreview() {
		cccDataTableHeader.clear();
		cccDataTableHeader.resize(0, 0);
		cccDataTable.clear();
		cccDataTable.resize(0, 0);
		cccDataTableHeader.resizeColumns(5);
		cccDataTable.resizeColumns(5);
		
		paintHeader();
		calculateScrollPanelHeight();
		setColumnWidth();
	}
	
	private void paintHeader() {
		int row = cccDataTableHeader.insertRow(cccDataTableHeader.getRowCount());
		Label activity = new Label("ACTIVIDAD");
		Label type = new Label("TIPO");
		Label account = new Label("CUENTA");
		Label geozone = new Label("PROVINCIA");
		Label blank = new Label("");
		
		activity.addStyleName(style.headerStyle());
		type.addStyleName(style.headerStyle());
		account.addStyleName(style.headerStyle());
		geozone.addStyleName(style.headerStyle());
		
		cccDataTableHeader.setWidget(row, 0, activity);
		cccDataTableHeader.setWidget(row, 1, type);
		cccDataTableHeader.setWidget(row, 2, account);
		cccDataTableHeader.setWidget(row, 3, geozone);
		cccDataTableHeader.setWidget(row, 4, blank);
	}
	
	private void calculateScrollPanelHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight - 600) + "px");
	}
	
	private void setColumnWidth() {
		//MaxWidth 950px
		cccDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		cccDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		cccDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(250, Unit.PX);
		cccDataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
		cccDataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
		
		cccDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		cccDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		cccDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(250, Unit.PX);
		cccDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
		cccDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
	}
	
	private void insertRows() {
		for(CCCInfo cccInfo : mainCCCObject.getCCCs()) {
			int row = cccDataTable.insertRow(cccDataTable.getRowCount());
			
			ListBox activitiesLB = createActivitiesListBox();
			setSelectedValueLB(activitiesLB, cccInfo.getActivityId().toString());
			
			ListBox cccRegimeLB = createCCCRegimeListBox();
			setSelectedValueLB(cccRegimeLB, cccInfo.getType().toString());
			
			Label geozone = new Label();
			String geozoneValue = "DESCONOCIDA";
			if(null != cccInfo.getGeozone()) {
				geozoneValue = cccInfo.getGeozone();
				geozone.removeStyleName(style.warningColor());
			}else 
				geozone.addStyleName(style.warningColor());
			
			geozone.setText(geozoneValue);
			
			String province = ProvinceContract.getName(cccInfo.getCcc().substring(0, 2));
			String provinceCode = cccInfo.getCcc().substring(0, 2);
			
			HorizontalPanel hPanel = new HorizontalPanel();
			Label typeCode = new Label(cccInfo.getCccRegimeCode());
			typeCode.getElement().getStyle().setPadding(4, Unit.PX);
			typeCode.getElement().getStyle().setMarginLeft(15, Unit.PX);
			Label accountStatus = new Label();
			TextBox account = new TextBox();
			account.setMaxLength(11);
			account.setValue(cccInfo.getCcc());
			account.addStyleName("aon-inputText");
			account.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					String accountValue = event.getValue();
					if(!StringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
						String province = ProvinceContract.getName(accountValue.substring(0, 2));
						String provinceCode = accountValue.substring(0, 2);
						if(checkCCC(accountValue)) {
							geozone.setText(province);
							geozone.removeStyleName(style.warningColor());
							accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
							accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
						}else {
							province = null == province ? "DESCONOCIDA" : province;
							geozone.setText(province);
							geozone.addStyleName(style.warningColor());
							accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
							accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						}
						mainCCCObject.insertCCC(
								cccInfo.getCccId(), 
								Integer.parseInt(activitiesLB.getSelectedValue()), 
								Byte.parseByte(cccRegimeLB.getSelectedValue()), 
								getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
								account.getValue(), 
								province, 
								provinceCode);
					}
				}
			});
			
			if(checkCCC(cccInfo.getCcc())) {
				accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
				accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			}else {
				accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
				accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			}
			
			hPanel.add(typeCode);
			hPanel.add(account);
			hPanel.add(accountStatus);
			
			activitiesLB.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
					typeCode.setText(newCCCRegimeCode);
					mainCCCObject.insertCCC(
							cccInfo.getCccId(), 
							Integer.parseInt(activitiesLB.getSelectedValue()), 
							Byte.parseByte(cccRegimeLB.getSelectedValue()), 
							getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
							account.getValue(), 
							province, 
							provinceCode);
				}
			});
			
			cccRegimeLB.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
					typeCode.setText(newCCCRegimeCode);
					mainCCCObject.insertCCC(
							cccInfo.getCccId(), 
							Integer.parseInt(activitiesLB.getSelectedValue()), 
							Byte.parseByte(cccRegimeLB.getSelectedValue()), 
							getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
							account.getValue(), 
							province, 
							provinceCode);
				}
			});
			
			Button delete = new Button();
			delete.setStyleName("aon-editDataTable-button aon-icon-delete");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if(cccInfo.isUseByContracts()) {
						WarningDialog warnignDialog = new WarningDialog("AVISO", "No se puede eliminar una cuenta de cotización que esta "
								+ "siendo usada por un centro de trabajo y/o por un contrato");
						warnignDialog.center();
						warnignDialog.show();
					}else {
						mainCCCObject.deleteCCC(cccInfo.getCccId());
						initPreview();
						insertRows();
					}
				}
			});
		
			cccDataTable.setWidget(row, 0, activitiesLB);
			cccDataTable.setWidget(row, 1, cccRegimeLB);
			cccDataTable.setWidget(row, 2, hPanel);
			cccDataTable.setWidget(row, 3, geozone);
			cccDataTable.setWidget(row, 4, delete);
		}
	}
	
	private void insertNewRow() {
		int row = cccDataTable.insertRow(cccDataTable.getRowCount());
		this.newId--;
		
		ListBox activitiesLB = createActivitiesListBox();
		
		ListBox cccRegimeLB = createCCCRegimeListBox();
		
		Label geozone = new Label("");
		
		HorizontalPanel hPanel = new HorizontalPanel();
		Label typeCode = new Label("");
		typeCode.getElement().getStyle().setPadding(4, Unit.PX);
		typeCode.getElement().getStyle().setMarginLeft(15, Unit.PX);
		Label accountStatus = new Label();
		TextBox account = new TextBox();
		account.setMaxLength(11);
		account.addStyleName("aon-inputText");
		account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String accountValue = event.getValue();
				if(!StringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
					String province = ProvinceContract.getName(accountValue.substring(0, 2));
					String provinceCode = accountValue.substring(0, 2);
					if(checkCCC(accountValue)) {
						geozone.setText(province);
						geozone.removeStyleName(style.warningColor());
						accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
					}else {
						province = null == province ? "DESCONOCIDA" : province;
						geozone.setText(province);
						geozone.addStyleName(style.warningColor());
						accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
						accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					}
					mainCCCObject.insertCCC(
							newId, 
							Integer.parseInt(activitiesLB.getSelectedValue()), 
							Byte.parseByte(cccRegimeLB.getSelectedValue()), 
							getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
							account.getValue(), 
							province, 
							provinceCode);
				}
			}
		});
		
		hPanel.add(typeCode);
		hPanel.add(account);
		hPanel.add(accountStatus);
		
		activitiesLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String province = "";
				String provinceCode = "";
				String accountValue = account.getValue();
				if(null != accountValue && !StringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
					province = ProvinceContract.getName(accountValue.substring(0, 2));
					provinceCode = accountValue.substring(0, 2);
				}
				
				if(null == cccRegimeLB.getSelectedValue())
					cccRegimeLB.setSelectedIndex(0);
				
				String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
				typeCode.setText(newCCCRegimeCode);
			
				mainCCCObject.insertCCC(
						newId, 
						Integer.parseInt(activitiesLB.getSelectedValue()), 
						Byte.parseByte(cccRegimeLB.getSelectedValue()), 
						getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
						account.getValue(), 
						province, 
						provinceCode);
			}
		});
		
		cccRegimeLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String province = "";
				String provinceCode = "";
				if(!StringUtils.isBlank(account.getValue()) && account.getValue().length() >= 2) {
					province = ProvinceContract.getName(account.getValue().substring(0, 2));
					provinceCode = account.getValue().substring(0, 2);
				}
				String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
				typeCode.setText(newCCCRegimeCode);
				mainCCCObject.insertCCC(
						newId, 
						Integer.parseInt(activitiesLB.getSelectedValue()), 
						Byte.parseByte(cccRegimeLB.getSelectedValue()), 
						getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
						account.getValue(), 
						province, 
						provinceCode);
			}
		});
		
		Button delete = new Button();
		delete.setStyleName("aon-editDataTable-button aon-icon-delete");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mainCCCObject.deleteCCC(newId);
				initPreview();
				insertRows();
			}
		});
	
		cccDataTable.setWidget(row, 0, activitiesLB);
		cccDataTable.setWidget(row, 1, cccRegimeLB);
		cccDataTable.setWidget(row, 2, hPanel);
		cccDataTable.setWidget(row, 3, geozone);
		cccDataTable.setWidget(row, 4, delete);
		
		if(mainCCCObject.getActivities().size() == 1)
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), activitiesLB);
	}
	
	private ListBox createActivitiesListBox() {
		ListBox activities = new ListBox();
		for(Entry<Integer, String> entry : mainCCCObject.getActivities())
			activities.addItem(entry.getValue(), entry.getKey().toString());
		
		activities.addStyleName("aon-selectOneMenu");
		activities.getElement().getStyle().setWidth(98, Unit.PCT);
		
		return activities;
	}

	private ListBox createCCCRegimeListBox(){
		ListBox cccRegime = new ListBox();
		cccRegime.addItem("Principal", "0");
		cccRegime.addItem("Formacion y aprendizaje", "1");
		cccRegime.addItem("Aprendizaje", "2");
		cccRegime.addItem("Representantes de comercio", "3");
		cccRegime.addItem("Asimilados R.General", "4");
		cccRegime.addItem("Becarios", "5");
		cccRegime.addItem("Emploead@s de hogar", "6");
		cccRegime.addItem("Trabajadores cuenta ajena agrarios", "7");
		cccRegime.addItem("Artistas", "8");
		
		cccRegime.getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		cccRegime.addStyleName("aon-selectOneMenu");
		cccRegime.getElement().getStyle().setWidth(98, Unit.PCT);
	
		return cccRegime;
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	private boolean checkCCC(String ccc) {
		if(ccc.length() == 11) {
			String code = ccc.substring(ccc.length()-2, ccc.length());
			Integer codeInt = Integer.parseInt(code);
			Integer cccInt = Integer.parseInt(ccc.substring(0, ccc.length()-2));
//			Window.alert("CCC : " + cccInt + ", Code : " + codeInt + ", MOD : " + cccInt % 97);
			if(cccInt % 97 == codeInt)
				return true;
			else
				return false;
		}else
			return false;
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

}
