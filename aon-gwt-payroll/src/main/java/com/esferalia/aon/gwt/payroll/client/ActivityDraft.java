package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActivityDraft extends Composite implements ContextMenuHandler {

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ActivityDraftUiBinder extends UiBinder<Widget, ActivityDraft> {
	
	}
	
	private static ActivityDraftUiBinder uiBinder = GWT.create(ActivityDraftUiBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String columnWidth();
		String columnWidth2();
		String headerStyle();
		String elementWidth80();
		String elementWidth95();
		String hide();
		String widthO();
	}
	
	@UiField
	TextBox activityDescription;
	
	@UiField
	ListBox activityCNAE2009;
	
	@UiField
	Label activityRegime;
	
	@UiField
	CheckBox activityActive;
	
	@UiField
	Grid cccDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid cccDataTable;
	
	@UiField
	Label newAccount;
	
	@UiHandler("cccDataTable")
	public void oncccDataTableClick(ClickEvent event) {
		
		event.preventDefault();
		
		int row = cccDataTable.getCellForEvent(event).getRowIndex();
		int col = cccDataTable.getCellForEvent(event).getCellIndex();
		int pos = (row * 39) + col;
		
//		Window.alert("Row : " + row + ", Col : " + col + ", Pos : " + pos);
	}
	
	@UiHandler("activityDescription")
	public void onActivityDescriptionChange(ChangeEvent event) {
		
	}
	
	@UiHandler("newAccount")
	public void onNewAccountClick(ClickEvent event) {
		if(0 != cccDataTable.getRowCount()) {
			Label firstGeozone = (Label) cccDataTable.getWidget(0, 3);
			if(null != firstGeozone && "" != firstGeozone.getText()) {
				insertNewRow();
			}
		}else
			insertNewRow();
	}
	
	private void insertNewRow() {
		int row = cccDataTable.insertRow(0);
		
		Label id = new Label("");
		
		ListBox types = new ListBox();
		types.addItem("Principal");
		types.addItem("General");
		types.addStyleName("aon-selectOneMenu");
		types.addStyleName(style.elementWidth80());
		
		Label geozone = new Label("");
		geozone.addStyleName(style.elementWidth80());
		
		HorizontalPanel hPanel = new HorizontalPanel();
		Label typeCode = new Label("0111");
		Label accountStatus = new Label();
		TextBox account = new TextBox();
		account.setValue("");
		account.addStyleName("aon-inputText");
		account.addStyleName(style.elementWidth95());
		account.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(account.getValue().length() >= 2) {
					String province = ProvinceContract.getName(account.getValue().substring(0, 2));
					if(null != province && checkCCC(account.getValue())) {
						geozone.setText(province);
						accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
						newId--;
						activityDraftObject.insertCCC(newId, typeCode.getText()+account.getValue(), (byte) types.getSelectedIndex(), province);
					}else {
						accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
						accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					}
					
				}
			}
		});
		hPanel.add(typeCode);
		hPanel.add(account);
		hPanel.add(accountStatus);
		
		Button delete = new Button();
		delete.setStyleName("aon-editDataTable-button aon-icon-delete");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("Borrar Id : " + id.getText());
				initPreview();
			}
		});
		
		cccDataTable.setWidget(row, 0, id);
		cccDataTable.setWidget(row, 1, types);
		cccDataTable.setWidget(row, 2, hPanel);
		cccDataTable.setWidget(row, 3, geozone);
		cccDataTable.setWidget(row, 4, delete);
	}
	
	private boolean checkCCC(String ccc) {
		if(ccc.length() == 11) {
			String code = ccc.substring(ccc.length()-2, ccc.length());
			Integer codeInt = Integer.parseInt(code);
			Integer cccInt = Integer.parseInt(ccc.substring(0, ccc.length()-2));
			Window.alert("CCC : " + cccInt + ", Code : " + codeInt + ", MOD : " + cccInt % 97);
			if(cccInt % 97 == codeInt)
				return true;
			else
				return false;
		}else
			return false;
	}
	
	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public ActivityDraft() {		
		// Inicializamos la vista de la actividad
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initPreview() {
		cccDataTableHeader.clear();
		cccDataTableHeader.resize(0, 0);
		cccDataTable.clear();
		cccDataTable.resize(0, 0);
		cccDataTableHeader.resizeColumns(5);
		cccDataTable.resizeColumns(5);
		paintHeader();
		insertRows();
		calculateScrollPanelHeight();
		hideFirstColumn();
		setColumnWidth();
	}

	// --------------------------------------------------- METODOS DE LA CLASE ----------------------------------------------------

	private void fillActivityInfo() {
		this.activityDescription.setValue(activityDraftObject.getActivityDescription());
		this.activityCNAE2009.addItem(activityDraftObject.getActivityCNAE2009Name());
		this.activityRegime.setText(activityDraftObject.getActivityRegime());
		this.activityActive.setValue(activityDraftObject.getActivityActive());
	}
	
	private void paintHeader() {
		int row = cccDataTableHeader.insertRow(cccDataTableHeader.getRowCount());
		Label id = new Label("");
		Label type = new Label("TIPO");
		Label account = new Label("CUENTA");
		Label geozone = new Label("PROVINCIA");
		Label blank = new Label("");
		
		id.addStyleName(style.headerStyle());
		type.addStyleName(style.headerStyle());
		account.addStyleName(style.headerStyle());
		geozone.addStyleName(style.headerStyle());
		
		cccDataTableHeader.setWidget(row, 0, id);
		cccDataTableHeader.setWidget(row, 1, type);
		cccDataTableHeader.setWidget(row, 2, account);
		cccDataTableHeader.setWidget(row, 3, geozone);
		cccDataTableHeader.setWidget(row, 4, blank);
	}
	
	private void setColumnWidth() {
		cccDataTableHeader.getCellFormatter().addStyleName(0, 0, style.hide());
		cccDataTableHeader.getCellFormatter().addStyleName(0, 0, style.widthO());
		cccDataTableHeader.getCellFormatter().addStyleName(0, 1, style.columnWidth());
		cccDataTableHeader.getCellFormatter().addStyleName(0, 2, style.columnWidth2());
		cccDataTableHeader.getCellFormatter().addStyleName(0, 3, style.columnWidth2());
		
		cccDataTable.getColumnFormatter().addStyleName(1, style.columnWidth());
		cccDataTable.getColumnFormatter().addStyleName(2, style.columnWidth2());
		cccDataTable.getColumnFormatter().addStyleName(3, style.columnWidth2());
	}
	
	private void insertRows() {
		for(CCCInfo cccInfo : activityDraftObject.getCCCs().values()) {
			int row = cccDataTable.insertRow(cccDataTable.getRowCount());
			
			Label id = new Label();
			id.setText(cccInfo.getCccId().toString());
			
			ListBox types = new ListBox();
			types.addItem("Principal");
			types.addItem("General");
			types.addStyleName("aon-selectOneMenu");
			types.addStyleName(style.elementWidth80());
			types.setSelectedIndex(cccInfo.getType());
			
			HorizontalPanel hPanel = new HorizontalPanel();
			Label typeCode = new Label(cccInfo.getCcc().substring(0, 4));
			TextBox account = new TextBox();
			account.setValue(cccInfo.getCcc().substring(4, cccInfo.getCcc().length()));
			account.addStyleName("aon-inputText");
			account.addStyleName(style.elementWidth95());
			hPanel.add(typeCode);
			hPanel.add(account);
			
			Label geozone = new Label(cccInfo.getGeozone());
			geozone.addStyleName(style.elementWidth80());
			
			Button delete = new Button();
			delete.setStyleName("aon-editDataTable-button aon-icon-delete");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.alert("Borrar Id : " + cccInfo.getCccId());
					activityDraftObject.deleteCCC(cccInfo.getCccId());
					initPreview();
				}
			});
		
			cccDataTable.setWidget(row, 0, id);
			cccDataTable.setWidget(row, 1, types);
			cccDataTable.setWidget(row, 2, hPanel);
			cccDataTable.setWidget(row, 3, geozone);
			cccDataTable.setWidget(row, 4, delete);
		}
	}
	
	private void calculateScrollPanelHeight() {
		Integer height = 100;
		Integer extra = 25;
		int rows = cccDataTable.getRowCount();
		if(rows < 4) {
			int mod = rows%4;
			scrollPanel.setHeight((mod*extra+extra)+"px");
		}else {
			int div = rows/4;
			int mod = rows%4;
			if(div < 5)
				scrollPanel.setHeight(((height*div)+(extra*mod)+extra)+"px");
			else
				scrollPanel.setHeight("475px");
		}
	}
	
	private void hideFirstColumn() {
		for(int row = 0; row < cccDataTableHeader.getRowCount(); row++)
			cccDataTableHeader.getWidget(row, 0).addStyleName(style.hide());
		
		for(int row = 0; row < cccDataTable.getRowCount(); row++)
			cccDataTable.getWidget(row, 0).addStyleName(style.hide());
		
	}

	private ActivityDraftObject activityDraftObject;
	private Integer newId;
	
	public void setActivityDraftObject(ActivityDraftObject activityDraftObject) {
		this.activityDraftObject = activityDraftObject;
		this.newId = 0;
		fillActivityInfo();
		initPreview();
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
		
	}

}
