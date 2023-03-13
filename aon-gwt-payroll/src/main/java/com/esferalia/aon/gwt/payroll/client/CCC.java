package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CCC extends ResizeComposite {

	// -------------------------------------------- UiBinder

	private static CCCDraftUiBinder uiBinder = GWT.create(CCCDraftUiBinder.class);

	interface CCCDraftUiBinder extends UiBinder<Widget, CCC> {}
	
	// -------------------------------------------- TgssContextMenu
	
	class EmployeesWorkingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmployeesWorking();
		}
	}
	
	class EmployeePrevMovCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmployeePrevMov();
		}
	}
	
	class IDCCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onIdcCC(new Date());
		}
	}
	
	class LaboralLifeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onLaboralLife(new Date());
		}
	}
	
	class TgssContextMenu extends ContextMenu {
				
		private MenuItem employeesWorking = null;
		private MenuItem employeePrevMov = null;
		private MenuItem idc = null;
		private MenuItem laboralLife = null;
		
		public TgssContextMenu() {
			
			employeesWorking = addItem("Trabajadores en situacion de alta", new EmployeesWorkingCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			employeesWorking.ensureDebugId("employeesWorking");
			
			employeePrevMov = addItem("Movimientos previos de trabajadores", new EmployeePrevMovCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			employeePrevMov.ensureDebugId("employeePrevMov");
			
			idc = addItem("IDC", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idc.ensureDebugId("idc");
			
			laboralLife = addItem("Vida Laboral", new LaboralLifeCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			laboralLife.ensureDebugId("laboralLife");
			
		}
	}

	// --------------------------------------------  UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmdBtn();
		String flexEvenly();
		String headerStyle();
		String inputLBHeight();
		String inputTextHeight();
		String warningColor();
		String warningTB();
		String widthAll();		
	}
	
	@UiField
	HTMLPanel centerContainer;
	
	@UiField
	Grid cccDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid cccDataTable;
	
	// -------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private static final String UNKNOWN = "DESCONOCIDA";
	private TgssContextMenu contextMenu;
	private Integer newId;
	private String regime;
	private String ccc;
	private Integer domain;
	
	// -------------------------------------------- Constructor

	protected CCC() {
		initWidget(uiBinder.createAndBindUi(this));
		initPreview();
		calculateScrollPanelHeight();
		contextMenu = new TgssContextMenu();
		showCCCTable();
		
		cccDataTable.ensureDebugId("cccTable");
	}
	
	// -------------------------------------------- DeckPanel
	
	public void showCCCTable() {
		deckPanel.showWidget(0);
	}
	
	public void showCCCMessage() {
		deckPanel.showWidget(1);
	}
	
	// -------------------------------------------- Initialize Preview
	
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
	
	public void calculateScrollPanelHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight/3) + "px");
	}
	
	public void setActivityDraftCCCHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight/3 - 80) + "px");
	}
	
	public void setDialogHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight - 750) + "px");
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
	
	public void insertRow(EnterpriseCCC cccInfo) {
		if(cccInfo.isDeleted()) return;
		
		int row = cccDataTable.insertRow(cccDataTable.getRowCount());
		
		Set<Entry<Integer, String>> activitiesList = getActivities();
		TextBox activityTB = new TextBox();
		activityTB.addStyleName(style.inputTextHeight());
		if(activitiesList.isEmpty()) {
			activityTB.setWidth("95%");
			activityTB.getElement().setPropertyString("placeholder", "Descripci\u00f3n Actividad ...");
			activityTB.addValueChangeHandler(e -> createActivity(e.getValue()));
		}
		
		ListBox activitiesLB = createActivitiesListBox();
		activitiesLB.addStyleName(style.inputLBHeight());
		if(null != cccInfo.getEnterpriseActivity())
			setSelectedValueLB(activitiesLB, cccInfo.getEnterpriseActivity().toString());
		
		ListBox cccRegimeLB = createCCCRegimeListBox();
		cccRegimeLB.addStyleName(style.inputLBHeight());
		cccRegimeLB.ensureDebugId("cccRegime_" + row);
		setSelectedValueLB(cccRegimeLB, cccInfo.getType().toString());
		
		Label geozone = new Label();
		geozone.ensureDebugId("geozone_" + row);
		String geozoneValue = UNKNOWN;
		if(null != cccInfo.getGeozone() || AonStringUtils.isNotBlank(cccInfo.getGeozoneCode())) {
			geozoneValue = ProvinceContract.getName(cccInfo.getGeozoneCode());
			geozone.removeStyleName(style.warningColor());
		}else 
			geozone.addStyleName(style.warningColor());
		
		geozone.setText(geozoneValue);
		
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.setStyleName(style.flexEvenly());
		hPanel.addStyleName(style.widthAll());
		
		Label typeCode = new Label(getCCCRegimeCode(cccInfo.getType()));
		typeCode.ensureDebugId("regimeCode_" + row);

		AonTableButton accountStatus = new AonTableButton("", AON.CSS.aonIconValid());
		accountStatus.ensureDebugId("accountStatus_" + row);
		
		TextBox account = new TextBox();
		account.ensureDebugId("account_" + row);
		account.setMaxLength(11);
		account.setValue(cccInfo.getCcc());
		account.addStyleName("aon-inputText");
		account.addStyleName(style.inputTextHeight());
		account.getElement().getStyle().setProperty("width", "65%");
		account.addKeyPressHandler(e -> {
			char keyCode = e.getCharCode();
	        if (!Character.isDigit(keyCode)) {
	        	fireWarningMessage(new HashMap<String, String>(){{ put("Error formato", "La cuenta de cotizac\u00f3n solo puede contener n\u00fameros"); }});
	        	account.cancelKey();
	        }
		});
		account.addValueChangeHandler(e -> {
			String accountValue = e.getValue();
			if(!AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
				String provinceAux = ProvinceContract.getName(accountValue.substring(0, 2));
				String provinceCodeAux = accountValue.substring(0, 2);
				if(checkCCC(accountValue)) {
					geozone.setTitle("");
					geozone.setText(provinceAux);
					geozone.removeStyleName(style.warningColor());
					
					accountStatus.setTitle("");
					accountStatus.removeStyleName(AON.CSS.aonIconInvalid());
					accountStatus.addStyleName(AON.CSS.aonIconValid());
					
					account.setTitle("");
					account.removeStyleName(style.warningTB());
					
					checkCCCGeozones();
				}else {
					geozone.setTitle("CCC incorrecto");
					geozone.setText(provinceAux);
					geozone.addStyleName(style.warningColor());
					
					accountStatus.setTitle("CCC incorrecto");
					accountStatus.removeStyleName(AON.CSS.aonIconValid());
					accountStatus.addStyleName(AON.CSS.aonIconInvalid());
					
					account.setTitle("CCC incorrecto");
					account.addStyleName(style.warningTB());
					
					fireWarningMessage(new HashMap<String, String>(){{ put("Formato CCC", "El CCC " + accountValue + " no es correcto, rev\u00EDselo por favor"); }});
				}
				
				createEnterpriseCCC(
						cccInfo.getId(), 
						(null == activitiesLB || activitiesLB.getItemCount() == 0) ? null : Integer.parseInt(activitiesLB.getSelectedValue()), 
						Byte.parseByte(cccRegimeLB.getSelectedValue()), 
						account.getValue(), 
						provinceAux, 
						provinceCodeAux);
			}
		});
		
		if(checkCCC(cccInfo.getCcc())) {
			accountStatus.setTitle("");
			accountStatus.removeStyleName(AON.CSS.aonIconInvalid());
			accountStatus.addStyleName(AON.CSS.aonIconValid());
			
			account.setTitle("");
			account.removeStyleName(style.warningTB());
		}else {
			accountStatus.setTitle("CCC incorrecto");
			accountStatus.removeStyleName(AON.CSS.aonIconValid());
			accountStatus.addStyleName(AON.CSS.aonIconInvalid());
			
			account.setTitle("CCC incorrecto");
			account.addStyleName(style.warningTB());
		}
		
		hPanel.add(typeCode);
		hPanel.add(account);
		hPanel.add(accountStatus);
		
		activitiesLB.addChangeHandler(e -> {
			String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
			typeCode.setText(newCCCRegimeCode);
			String provinceAux = ProvinceContract.getName(account.getValue().substring(0, 2));
			String provinceCodeAux = account.getValue().substring(0, 2);
			createEnterpriseCCC(
					cccInfo.getId(), 
					(null == activitiesLB || activitiesLB.getItemCount() == 0) ? null : Integer.parseInt(activitiesLB.getSelectedValue()), 
					Byte.parseByte(cccRegimeLB.getSelectedValue()), 
					account.getValue(), 
					provinceAux, 
					provinceCodeAux);
		});
		
		cccRegimeLB.addChangeHandler(e -> {
			String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
			typeCode.setText(newCCCRegimeCode);
			String provinceAux = ProvinceContract.getName(account.getValue().substring(0, 2));
			String provinceCodeAux = account.getValue().substring(0, 2);
			createEnterpriseCCC(
					cccInfo.getId(), 
					(null == activitiesLB || activitiesLB.getItemCount() == 0) ? null : Integer.parseInt(activitiesLB.getSelectedValue()), 
					Byte.parseByte(cccRegimeLB.getSelectedValue()), 
					account.getValue(), 
					provinceAux, 
					provinceCodeAux);
		});
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flexEvenly());
		
		AonTableButton delete = new AonTableButton("Eliminar CCC", AON.CSS.aonIconDelete());
		delete.ensureDebugId("delete_" + row);
		delete.addClickHandler(e -> {
			if(Boolean.TRUE.equals(cccInfo.isUseByContracts())) 
				fireWarningMessage(new HashMap<String, String>(){{ put("AVISO: Contratos asociados",  "No se puede eliminar una cuenta de cotizaci\u00F3n que esta "
						+ "siendo usada por un centro de trabajo y/o por un contrato"); }});
			else if(Boolean.TRUE.equals(cccInfo.isUseByCra())) 
				fireWarningMessage(new HashMap<String, String>(){{ put("AVISO: CRAs asociados",  "No se puede eliminar una cuenta de cotizaci\u00F3n que esta "
						+ "siendo referenciada desde un CRA existente"); }});
			else {
				AonDialog deleteDialog = new AonDialog("Eliminar concepto",
						new HTML("\u00BFDesea eliminar el CCC seleccionado\u003F"));
				deleteDialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						// Nothing to do here
					}

					@Override
					public void onAccept() {
						onDeleteCCC(cccInfo.getId());
						initPreview();
						onInsertRows();
					}
				});
			}
		});
		
		buttonsPanel.add(delete);
	
		AonTableButton tgssMenu = new AonTableButton("TGSS", AON.CSS.aonIconMoreVertical());
		tgssMenu.ensureDebugId("tgssMenu_" + row);
		tgssMenu.addClickHandler(e -> {
			this.regime = getCCCRegimeCode(cccInfo.getType());
			this.ccc = cccInfo.getCcc();
			NativeEvent nativeEvent = e.getNativeEvent();
			contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
			contextMenu.show();
		});
		buttonsPanel.add(tgssMenu);
		
		cccDataTable.setWidget(row, 0, activitiesList.isEmpty() ? activityTB : activitiesLB);
		cccDataTable.setWidget(row, 1, cccRegimeLB);
		cccDataTable.setWidget(row, 2, hPanel);
		cccDataTable.setWidget(row, 3, geozone);
		cccDataTable.setWidget(row, 4, buttonsPanel);
		
	}
	
	private void createActivity(String activityDescription) {
		int activityId = new Random().nextInt();
		if(activityId > 0) activityId = activityId * -1;
		
		Activity activity = (Activity) new Activity()
				.setId(activityId)
				.setDescription(activityDescription);
		
		activity.setDomain(this.domain);
		
		onInsertActivity(activity);
		
		int newCCCId = new Random().nextInt();
		if(newCCCId > 0) newCCCId = newCCCId * -1;
		
		EnterpriseCCC ccc = new EnterpriseCCC()
				.setId(newCCCId)
				.setDomain(this.domain)
				.setEnterpriseActivity(activityId)
				.setType((byte)0)
				.setCcc(null)
				.setGeozone(null)
				.setGeozoneCode(null)
				.setGeozoneDescription(null)
				.setDeleted(false)
				.setUseByContracts(false)
				.setUseByCra(false);
		
		onInsertCCC(ccc);
		
		initPreview();
		onInsertRows();
	}

	public void insertNewRow() {
		showCCCTable();
		
		int row = cccDataTable.insertRow(cccDataTable.getRowCount());
		newId = new Random().nextInt();
		if(newId > 0) newId = newId * -1;
		
		Set<Entry<Integer, String>> activitiesList = getActivities();
		TextBox activityTB = new TextBox();
		activityTB.addStyleName(style.inputTextHeight());
		if(activitiesList.isEmpty()) {
			activityTB.setWidth("95%");
			activityTB.getElement().setPropertyString("placeholder", "Descripci\u00f3n Actividad ...");
			activityTB.addValueChangeHandler(e -> createActivity(e.getValue()));
		}
		
		ListBox activitiesLB = createActivitiesListBox();
		activitiesLB.addStyleName(style.inputLBHeight());
		
		ListBox cccRegimeLB = createCCCRegimeListBox();
		cccRegimeLB.ensureDebugId("cccRegime_" + row);
		cccRegimeLB.addStyleName(style.inputLBHeight());
		
		Label geozone = new Label("");

		geozone.ensureDebugId("geozone_" + row);
		
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.setStyleName(style.flexEvenly());
		hPanel.addStyleName(style.widthAll());
		
		Label typeCode = new Label("");
		typeCode.ensureDebugId("regimeCode_" + row);
		String newCCCRegimeCode = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
		typeCode.setText(newCCCRegimeCode);
		
		AonTableButton accountStatus = new AonTableButton("", AON.CSS.aonIconValid());
		accountStatus.ensureDebugId("accountStatus_" + row);
		
		TextBox account = new TextBox();
		account.ensureDebugId("account_" + row);
		account.setMaxLength(11);
		account.addStyleName("aon-inputText");
		account.addStyleName(style.inputTextHeight());
		account.getElement().getStyle().setProperty("width", "65%");
		account.addKeyPressHandler(e -> {
			char keyCode = e.getCharCode();
	        if (!Character.isDigit(keyCode)) {
	        	fireWarningMessage(new HashMap<String, String>(){{ put("Error formato", "La cuenta de cotizac\u00f3n solo puede contener n\u00fameros"); }});
	        	account.cancelKey();
	        }
		});
		account.addValueChangeHandler(e -> {
			String accountValue = e.getValue();
			if(!AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
				String province = ProvinceContract.getName(accountValue.substring(0, 2));
				String provinceCode = accountValue.substring(0, 2);
				if(checkCCC(accountValue)) {
					geozone.setTitle("");
					geozone.setText(province);
					geozone.removeStyleName(style.warningColor());
					
					accountStatus.setTitle("");
					accountStatus.removeStyleName(AON.CSS.aonIconInvalid());
					accountStatus.addStyleName(AON.CSS.aonIconValid());
					
					account.setTitle("");
					account.removeStyleName(style.warningTB());
					
					checkCCCGeozones();
				}else {
					province = null == province ? UNKNOWN : province;
					
					geozone.setTitle("CCC incorrecto");
					geozone.setText(province);
					geozone.addStyleName(style.warningColor());
					
					accountStatus.setTitle("CCC incorrecto");
					accountStatus.removeStyleName(AON.CSS.aonIconValid());
					accountStatus.addStyleName(AON.CSS.aonIconInvalid());
					
					account.setTitle("CCC incorrecto");
					account.addStyleName(style.warningTB());
					
					fireWarningMessage(new HashMap<String, String>(){{ put("Formato CCC", "El CCC " + accountValue + " no es correcto, rev\u00EDselo por favor"); }});
				}
				
				createEnterpriseCCC(
						newId, 
						((null == activitiesLB || activitiesLB.getItemCount() == 0 || AonStringUtils.isBlank(activitiesLB.getSelectedValue())) ? null : Integer.parseInt(activitiesLB.getSelectedValue())), 
						Byte.parseByte(cccRegimeLB.getSelectedValue()), 
						accountValue, 
						province, 
						provinceCode);
			}
		});
		
		hPanel.add(typeCode);
		hPanel.add(account);
		hPanel.add(accountStatus);
		
		activitiesLB.addChangeHandler(e ->{
			String province = "";
			String provinceCode = "";
			String accountValue = account.getValue();
			if(null != accountValue && !AonStringUtils.isBlank(accountValue) && accountValue.length() >= 2) {
				province = ProvinceContract.getName(accountValue.substring(0, 2));
				provinceCode = accountValue.substring(0, 2);
			}
			
			if(null == cccRegimeLB.getSelectedValue())
				cccRegimeLB.setSelectedIndex(0);
			
			String newCCCRegimeCodeAux = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
			typeCode.setText(newCCCRegimeCodeAux);
		
			createEnterpriseCCC(
					newId, 
					(null == activitiesLB || activitiesLB.getItemCount() == 0) ? null : Integer.parseInt(activitiesLB.getSelectedValue()), 
					Byte.parseByte(cccRegimeLB.getSelectedValue()), 
					account.getValue(), 
					province, 
					provinceCode);
		});
		
		cccRegimeLB.addChangeHandler(e -> {
			String province = "";
			String provinceCode = "";
			if(!AonStringUtils.isBlank(account.getValue()) && account.getValue().length() >= 2) {
				province = ProvinceContract.getName(account.getValue().substring(0, 2));
				provinceCode = account.getValue().substring(0, 2);
			}
			String newCCCRegimeCodeAux = getCCCRegimeCode(Byte.parseByte(cccRegimeLB.getSelectedValue()));
			typeCode.setText(newCCCRegimeCodeAux);
			createEnterpriseCCC(
					newId, 
					(null == activitiesLB || activitiesLB.getItemCount() == 0) ? null : Integer.parseInt(activitiesLB.getSelectedValue()), 
					Byte.parseByte(cccRegimeLB.getSelectedValue()), 
					account.getValue(), 
					province, 
					provinceCode);
		});
		
		AonTableButton delete = new AonTableButton("Eliminar CCC", AON.CSS.aonIconDelete());
		delete.ensureDebugId("delete_" + row);
		delete.addClickHandler(e -> {
			onDeleteCCC(newId);
			initPreview();
			onInsertRows();
		});
	
		cccDataTable.setWidget(row, 0, activitiesList.isEmpty() ? activityTB : activitiesLB);
		cccDataTable.setWidget(row, 1, cccRegimeLB);
		cccDataTable.setWidget(row, 2, hPanel);
		cccDataTable.setWidget(row, 3, geozone);
		cccDataTable.setWidget(row, 4, delete);
		
		if(null != getActivities() && getActivities().size() == 1)
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), activitiesLB);
		
		onInsertRow();
	}
	
	// -------------------------------------------- Auxiliar Methods
	
	private void checkCCCGeozones(){
		List<EnterpriseCCC> cccs = getEnterpriseCCCs();
		if(!cccs.isEmpty() && cccs.size() > 1) {
			String geozoneCode = cccs.get(0).getGeozoneCode();
			Optional<EnterpriseCCC> noSameGeozoneCCC = cccs.stream().filter(ccc -> !AonStringUtils.equalsIgnoreCase(ccc.getGeozoneCode(), geozoneCode)).findAny();
			if(noSameGeozoneCCC.isPresent())
				fireInfoMessage(new HashMap<String, String>(){{ put("Provincia CCC", "Las provincias de los CCCs no coinciden"); }});
		}
	}
	
	private void createEnterpriseCCC(Integer cccId, Integer activity, Byte type, String cccAccount, String province, String provinceCode) {
		EnterpriseCCC ccc = new EnterpriseCCC()
				.setId(cccId)
				.setDomain(this.domain)
				.setEnterpriseActivity(activity)
				.setType(type)
				.setCcc(cccAccount)
				.setGeozone(null)
				.setGeozoneCode(provinceCode)
				.setGeozoneDescription(province)
				.setDeleted(false)
				.setUseByContracts(false)
				.setUseByCra(false);
		
		onInsertCCC(ccc);
	}
	
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
		
		Set<Entry<Integer, String>> activitiesList = getActivities();
		if(null != activitiesList)
			for(Entry<Integer, String> entry : activitiesList)
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
		cccRegime.addItem("Emplead@s de hogar", "6");
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

	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	// -------------------------------------------- Abstract Methods
	
	protected abstract void onInsertRow();
	
	protected abstract void onInsertRows();

	protected abstract void onDeleteCCC(Integer cccId);

	protected abstract void onInsertCCC(EnterpriseCCC ccc);
	
	protected abstract void onInsertActivity(Activity activity);

	protected abstract Set<Entry<Integer, String>> getActivities();
	protected abstract List<EnterpriseCCC> getEnterpriseCCCs();
	
	protected abstract void fireWarningMessage(Map<String, String> warningMap);
	protected abstract void fireInfoMessage(Map<String, String> warningMap);
	protected abstract void fireLoadingMessage(String message);
	protected abstract void hideMessage();
	
	protected abstract void showPDF(String dataURI, boolean isLaboralLife);

	// -------------------------------------------- Footer Panel
	
	public void onAddNewCCC() {
		if(0 != cccDataTable.getRowCount()) {
			Label firstGeozone = (Label) cccDataTable.getWidget(0, 3);
			if(null != firstGeozone && AonStringUtils.isNotBlank(firstGeozone.getText()))
				insertNewRow();
		} else
			insertNewRow();
	}
	
	// -------------------------------------------- Footer Panel TGSS
	
	private void onEmployeesWorking() {
		fireLoadingMessage("Obteniendo trabajadores en situacion de alta ...");
		impl.getEmployeesWorking(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, false);
				hideMessage();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String, String>(){{ put("TGSS Trabajadores Alta", caught.getMessage()); }});
			}
		});
	}
	
	private void onEmployeePrevMov() {
		fireLoadingMessage("Obteniendo movimientos previos de trabajadores ...");
		impl.getEmployeePrevMov(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, false);
				hideMessage();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String, String>(){{ put("TGSS Movimientos Previos", caught.getMessage()); }});
			}
		});
	}
	
	public void onIdcCC(Date date) {
		fireLoadingMessage("Obteniendo IDC ...");
		impl.getIdcCCC(regime, ccc, date, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, false);
				hideMessage();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String, String>(){{ put("TGSS Idc", caught.getMessage()); }});
			}
		});
	}
	
	public void onLaboralLife(Date date) {
		fireLoadingMessage("Obteniendo vida laboral ...");
		impl.getCCCLaboralLife(regime, ccc, date, new Date(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, true);
				hideMessage();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String, String>(){{ put("TGSS Vida Laboral", caught.getMessage()); }});
			}
		});
	}
	
}
