package com.esferalia.aon.gwt.payroll.client;

import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CCC extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static CCCDraftUiBinder uiBinder = GWT.create(CCCDraftUiBinder.class);

	interface CCCDraftUiBinder extends UiBinder<Widget, CCC> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexEvenly();
		String headerStyle();
		String warningColor();
		String widthAll();
	}
	
	@UiField
	Grid cccDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid cccDataTable;
	
	private Integer newId = -1;
	
	// --------------------------------------------------	 CONSTRUCTOR	--------------------------------------------------------

	public CCC() {
		initWidget(uiBinder.createAndBindUi(this));
		initPreview();
	}
	
	// --------------------------------------------------	   PREVIEW		--------------------------------------------------------
	
	public void resetPreview() {
		initPreview();
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
		scrollPanel.setHeight((clientHeight - 550) + "px");
	}
	
	public void calculateScrollPanelHeightMainCCC() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight - 350) + "px");
	}
	
	private void setColumnWidth() {
		cccDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(35, Unit.PCT);
		cccDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(20, Unit.PCT);
		cccDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(25, Unit.PCT);
		cccDataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(15, Unit.PCT);
		cccDataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(5, Unit.PCT);
		
		cccDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(35, Unit.PCT);
		cccDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(20, Unit.PCT);
		cccDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(25, Unit.PCT);
		cccDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(15, Unit.PCT);
		cccDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(5, Unit.PCT);
	}
	
	// --------------------------------------------------	   INSERT ROWS		--------------------------------------------------------
	
	public void insertRow(CCCInfo cccInfo) {
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
		
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.setStyleName(style.flexEvenly());
		hPanel.addStyleName(style.widthAll());
		
		Label typeCode = new Label(cccInfo.getCccRegimeCode());

		AonTableButton accountStatus = new AonTableButton("", AON.CSS.aonIconValid());
		TextBox account = new TextBox();
		account.setMaxLength(11);
		account.setValue(cccInfo.getCcc());
		account.addStyleName("aon-inputText");
		account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String accountValue = event.getValue();
				if(!AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
					String province = ProvinceContract.getName(accountValue.substring(0, 2));
					String provinceCode = accountValue.substring(0, 2);
					if(checkCCC(accountValue)) {
						geozone.setText(province);
						geozone.removeStyleName(style.warningColor());
						accountStatus.removeStyleName(AON.CSS.aonIconInvalid());
						accountStatus.addStyleName(AON.CSS.aonIconValid());
					}else {
						province = null == province ? "DESCONOCIDA" : province;
						geozone.setText(province);
						geozone.addStyleName(style.warningColor());
						accountStatus.removeStyleName(AON.CSS.aonIconValid());
						accountStatus.addStyleName(AON.CSS.aonIconInvalid());
					}
					
					onInsertCCC(
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
			accountStatus.removeStyleName(AON.CSS.aonIconInvalid());
			accountStatus.addStyleName(AON.CSS.aonIconValid());
		}else {
			accountStatus.removeStyleName(AON.CSS.aonIconValid());
			accountStatus.addStyleName(AON.CSS.aonIconInvalid());
		}
		
		hPanel.add(typeCode);
		hPanel.add(account);
		hPanel.add(accountStatus);
		
		activitiesLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
				typeCode.setText(newCCCRegimeCode);
				onInsertCCC(
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
				onInsertCCC(
						cccInfo.getCccId(), 
						Integer.parseInt(activitiesLB.getSelectedValue()), 
						Byte.parseByte(cccRegimeLB.getSelectedValue()), 
						getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
						account.getValue(), 
						province, 
						provinceCode);
			}
		});
		
		AonTableButton delete = new AonTableButton("Eliminar CCC", AON.CSS.aonIconDelete());
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(cccInfo.isUseByContracts()) {
					WarningDialog warnignDialog = new WarningDialog("AVISO", "No se puede eliminar una cuenta de cotización que esta "
							+ "siendo usada por un centro de trabajo y/o por un contrato");
					warnignDialog.center();
					warnignDialog.show();
				}else {
					onDeleteCCC(cccInfo.getCccId());
					initPreview();
					onInsertRows();
				}
			}
		});
	
		cccDataTable.setWidget(row, 0, activitiesLB);
		cccDataTable.setWidget(row, 1, cccRegimeLB);
		cccDataTable.setWidget(row, 2, hPanel);
		cccDataTable.setWidget(row, 3, geozone);
		cccDataTable.setWidget(row, 4, delete);
		
	}
	
	public Integer insertNewRow(Integer newId) {
		int row = cccDataTable.insertRow(cccDataTable.getRowCount());
		this.newId = newId;
		this.newId--;
		
		ListBox activitiesLB = createActivitiesListBox();
		
		ListBox cccRegimeLB = createCCCRegimeListBox();
		
		Label geozone = new Label("");
		
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.setStyleName(style.flexEvenly());
		hPanel.addStyleName(style.widthAll());
		
		Label typeCode = new Label("");
		String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
		typeCode.setText(newCCCRegimeCode);
		
		AonTableButton accountStatus = new AonTableButton("", AON.CSS.aonIconValid());
		TextBox account = new TextBox();
		account.setMaxLength(11);
		account.addStyleName("aon-inputText");
		account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String accountValue = event.getValue();
				if(!AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
					String province = ProvinceContract.getName(accountValue.substring(0, 2));
					String provinceCode = accountValue.substring(0, 2);
					if(checkCCC(accountValue)) {
						geozone.setText(province);
						geozone.removeStyleName(style.warningColor());
						accountStatus.removeStyleName(AON.CSS.aonIconInvalid());
						accountStatus.addStyleName(AON.CSS.aonIconValid());
					}else {
						province = null == province ? "DESCONOCIDA" : province;
						geozone.setText(province);
						geozone.addStyleName(style.warningColor());
						accountStatus.removeStyleName(AON.CSS.aonIconValid());
						accountStatus.addStyleName(AON.CSS.aonIconInvalid());
					}
					onInsertCCC(
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
				if(null != accountValue && !AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
					province = ProvinceContract.getName(accountValue.substring(0, 2));
					provinceCode = accountValue.substring(0, 2);
				}
				
				if(null == cccRegimeLB.getSelectedValue())
					cccRegimeLB.setSelectedIndex(0);
				
				String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
				typeCode.setText(newCCCRegimeCode);
			
				onInsertCCC(
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
				if(!AonStringUtils.isBlank(account.getValue()) && account.getValue().length() >= 2) {
					province = ProvinceContract.getName(account.getValue().substring(0, 2));
					provinceCode = account.getValue().substring(0, 2);
				}
				String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
				typeCode.setText(newCCCRegimeCode);
				onInsertCCC(
						newId, 
						Integer.parseInt(activitiesLB.getSelectedValue()), 
						Byte.parseByte(cccRegimeLB.getSelectedValue()), 
						getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue())),  
						account.getValue(), 
						province, 
						provinceCode);
			}
		});
		
		AonTableButton delete = new AonTableButton("Eliminar CCC", AON.CSS.aonIconDelete());
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteCCC(newId);
				initPreview();
				onInsertRows();
			}
		});
	
		cccDataTable.setWidget(row, 0, activitiesLB);
		cccDataTable.setWidget(row, 1, cccRegimeLB);
		cccDataTable.setWidget(row, 2, hPanel);
		cccDataTable.setWidget(row, 3, geozone);
		cccDataTable.setWidget(row, 4, delete);
		
		if(null != getActivities() && getActivities().size() == 1)
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), activitiesLB);
		
		return newId;
	}
	
	// --------------------------------------------------	   AUX METHODS		--------------------------------------------------------
	
	public int getRowCount() {
		return cccDataTable.getRowCount();
	}

	public Widget getWidget(int row, int column) {
		return cccDataTable.getWidget(row, column);
	}
	
	public void hideActivityColumn() {
		cccDataTableHeader.getColumnFormatter().getElement(0).getStyle().setDisplay(Display.NONE);
		cccDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setDisplay(Display.NONE);
		cccDataTable.getColumnFormatter().getElement(0).getStyle().setDisplay(Display.NONE);
		for(int row=0; row < cccDataTable.getRowCount(); row++)
			cccDataTable.getCellFormatter().getElement(row, 0).getStyle().setDisplay(Display.NONE);
	}
	
	private ListBox createActivitiesListBox() {
		ListBox activities = new ListBox();
		
		Set<Entry<Integer, String>> activitySet = getActivities();
		if(null == activitySet)
			activities.addItem("", "-1");
		else
			for(Entry<Integer, String> entry : getActivities())
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

	// --------------------------------------------------	   ABSTRACT METHODS		--------------------------------------------------------
	
	protected abstract void onInsertRows();

	protected abstract void onDeleteCCC(Integer cccId);

	protected abstract void onInsertCCC(Integer cccId, int parseInt, byte parseByte, String cccRegimeCode, String value, String province, String provinceCode);

	protected abstract Set<Entry<Integer, String>> getActivities();	

}
