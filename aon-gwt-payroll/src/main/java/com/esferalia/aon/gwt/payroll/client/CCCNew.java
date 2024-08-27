package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class CCCNew extends ResizeComposite {

	// -------------------------------------------- UiBinder

	private static CCCDraftUiBinder uiBinder = GWT.create(CCCDraftUiBinder.class);

	interface CCCDraftUiBinder extends UiBinder<Widget, CCCNew> {}
	
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
			onLaboralLife(DateUtils.getFirstDayOfMonth(new Date()));
		}
	}
	
	class TgssContextMenu extends ContextMenu {
				
		private MenuItem employeesWorking = null;
		private MenuItem employeePrevMov = null;
		private MenuItem idc = null;
		private MenuItem laboralLife = null;
		
		public TgssContextMenu() {
			
			employeesWorking = addItem("Trabajadores en situacion de alta", new EmployeesWorkingCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn(), AON.CSS.aonNowrap());
			employeesWorking.ensureDebugId("employeesWorking");
			
			employeePrevMov = addItem("Movimientos previos de trabajadores", new EmployeePrevMovCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn(), AON.CSS.aonNowrap());
			employeePrevMov.ensureDebugId("employeePrevMov");
			
			idc = addItem("IDC", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn(), AON.CSS.aonNowrap());
			idc.ensureDebugId("idc");
			
			laboralLife = addItem("Vida Laboral", new LaboralLifeCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn(), AON.CSS.aonNowrap());
			laboralLife.ensureDebugId("laboralLife");
			
		}
	}

	// --------------------------------------------  UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmdBtn();		
	}
	
	@UiField
	ScrollPanel scrollPanel;
	
	// ----------------------------------------------- Cols
	
	private static enum COLS {
		  ACT("Actividad"					,"-moz-available")
		, TYP("Tipo"						,"50rem")
		, ACC("Cuenta"						,"50rem")
		, PRO("Provincia"					,"30rem")
		, BUT(AonStringUtils.EMPTY			,"10rem")
		;

		String headerLabel;
		String colWidth;

		private COLS(String headerLabel,String colWidth) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
	}
	
	// -------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private AonCustomTable cccTable;
	
	private TgssContextMenu contextMenu;
	private String regime;
	private String ccc;
	
	// -------------------------------------------- Constructor

	protected CCCNew() {
		initWidget(uiBinder.createAndBindUi(this));
		initPreview();
		contextMenu = new TgssContextMenu();
	}
	
	// -------------------------------------------- Initialize Preview
	
	public void initPreview() {
		cccTable = new AonCustomTable();
		cccTable.setMaxHeight((Window.getClientHeight() - Window.getClientHeight()/3) + "px");
		
		paintCCCHeader();
		scrollPanel.clear();
		scrollPanel.add(cccTable);
		
	}
	
	private void paintCCCHeader() {
		cccTable.createHeader();
		for ( COLS col : COLS.values()) 
			cccTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth());
	}
	
	// --------------------------------------------------	   INSERT ROWS		--------------------------------------------------------
	
	public void removeTableRows() {
		int rows = cccTable.getRowsCount();
		while(rows >= 0) {
			cccTable.remove(rows);
			rows--;
		}
	}
	
	public void paintNoDataRow() {
		HTMLPanel row = cccTable.createRow();
		Label noData = new Label("No existen cuentas de cotizaci\u00f3n");
		noData.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		cccTable.addRow(row, noData, "100%");
	}
	
	public void insertRow(EnterpriseCCC cccInfo) {
		if(cccInfo.isDeleted()) return;
		
		HTMLPanel row = cccTable.createRow();
		row.addDomHandler(e -> onCCCOpen(cccInfo), ClickEvent.getType());
		
		Label activity = new Label( getActivities().stream().filter(entry -> entry.getKey().equals(cccInfo.getEnterpriseActivity()) || entry.getKey() == cccInfo.getEnterpriseActivity()).findFirst().get().getValue() );
		Label regime = new Label( getRegime(cccInfo.getType()) );
		
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.setStyleName(AON.CSS.aonItemFlex());
		
		Label regimeCode = new Label( getCCCRegimeCode(cccInfo.getType()) );
		Label account = new Label( cccInfo.getCcc() );
		AonTableButton accountStatus = new AonTableButton("", checkCCC(cccInfo.getCcc()) ? AON.CSS.aonIconValid() : AON.CSS.aonIconInvalid());
		
		hPanel.add(regimeCode);
		hPanel.add(account);
		hPanel.add(accountStatus);
		
		Label geozone = new Label( cccInfo.getGeozoneDescription() );
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton delete = new AonTableButton("Eliminar CCC", AON.CSS.aonIconDelete());
		delete.addStyleName(AON.CSS.aonCustomRowButtom());
		delete.ensureDebugId("delete_" + row);
		delete.addClickHandler(e -> {
			e.stopPropagation();
			
			if(Boolean.TRUE.equals(cccInfo.isUseByContracts())) 
				fireWarningMessage(new HashMap<String, String>(){{ put("AVISO: Contratos asociados",  "No se puede eliminar una cuenta de cotizaci\u00F3n que esta "
						+ "siendo usada por un centro de trabajo y/o por un contrato"); }});
			else if(Boolean.TRUE.equals(cccInfo.isUseByCra())) 
				fireWarningMessage(new HashMap<String, String>(){{ put("AVISO: CRAs asociados",  "No se puede eliminar una cuenta de cotizaci\u00F3n que esta "
						+ "siendo referenciada desde un CRA existente"); }});
			else {
				AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n Cuenta Cotizaci\u00f3n",
						new HTML("Se va a proceder a eliminar la cuenta de cotizaci\u00f3n <b>" + getRegime(cccInfo.getType()) + " - " + getCCCRegimeCode(cccInfo.getType()) + " " + cccInfo.getCcc() + " (" + cccInfo.getGeozoneDescription()  + ")"  + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				deleteDialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						// Nothing to do here
					}

					@Override
					public void onAccept() {
						onDeleteCCC(cccInfo.getId());
					}
				});
			}
		});
		
		buttonsPanel.add(delete);
	
		AonTableButton tgssMenu = new AonTableButton("TGSS", AON.CSS.aonIconMoreVertical());
		tgssMenu.addStyleName(AON.CSS.aonCustomRowButtom());
		tgssMenu.ensureDebugId("tgssMenu_" + row);
		tgssMenu.addClickHandler(e -> {
			e.stopPropagation();
			
			this.regime = getCCCRegimeCode(cccInfo.getType());
			this.ccc = cccInfo.getCcc();
			NativeEvent nativeEvent = e.getNativeEvent();
			contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
			contextMenu.setPopupPositionAndShow((offsetWidth, offsetHeight) ->  {
				int clientX = e.getClientX();
				int clientY = e.getClientY();
				
				int clientWidth = Window.getClientWidth();
				int clientHeight = Window.getClientHeight();
				
				int left = Math.min(clientX, clientWidth - ( offsetWidth + 10 )  );
				int top = Math.min(clientY, clientHeight - ( offsetHeight + 10 ) );
				
				contextMenu.setPopupPosition(left, top);
			});
		});
		buttonsPanel.add(tgssMenu);
		
		cccTable.addRow(row, activity, COLS.ACT.getColWidth());
		cccTable.addRow(row, regime, COLS.TYP.getColWidth());
		cccTable.addRow(row, hPanel, COLS.ACC.getColWidth());
		cccTable.addRow(row, geozone, COLS.PRO.getColWidth());
		cccTable.addRow(row, buttonsPanel, COLS.BUT.getColWidth());
		
	}
	
	// -------------------------------------------- Auxiliar Methods
	
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
	
	private String getRegime(Byte cccType){
		switch (cccType) {
			case 0:
				return "Principal";
			case 1:
				return "Formacion y aprendizaje";
			case 2:
				return "Aprendizaje";
			case 3:
				return "Representantes de comercio";
			case 4:
				return "Asimilados R.General";
			case 5:
				return "Becarios";
			case 6:
				return "Emplead@s de hogar";
			case 7:
				return "Trabajadores cuenta ajena agrarios";
			case 8:
				return "Artistas";
			default:
				return "Principal";
		}
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
	
	// -------------------------------------------- Abstract Methods
	
	protected abstract void onInsertRows();

	protected abstract void onDeleteCCC(Integer cccId);
	
	protected abstract void onCCCOpen(EnterpriseCCC cccInfo);

	protected abstract Set<Entry<Integer, String>> getActivities();
	
	protected abstract <T> void fireWarningMessage(Map<String, T> warningMap);
	protected abstract <T> void fireInfoMessage(Map<String, T> warningMap);
	protected abstract <T> void fireLoadingMessage(T message);

	protected abstract void hideMessage();
	
	protected abstract void showPDF(String dataURI, String title, boolean isLaboralLife);
	
	// -------------------------------------------- Footer Panel TGSS
	
	private void onEmployeesWorking() {
		fireLoadingMessage("Obteniendo trabajadores en situacion de alta ...");
		impl.getEmployeesWorking(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, "Trabajadores en situaci\u00f3n de alta", false);
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
				showPDF(dataURI, "Movimientos previos de trabajadores", false);
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
				showPDF(dataURI, "IDC", false);
				hideMessage();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String, String>(){{ put("TGSS Idc", caught.getMessage()); }});
			}
		});
	}
	
	public void onLaboralLife(Date date) {
		fireLoadingMessage("Obteniendo Informe de Vida Laboral ...");
		impl.getCCCLaboralLife(regime, ccc, date, new Date(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, "Informe de Vida Laboral", true);
				hideMessage();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String, String>(){{ put("TGSS Vida Laboral", caught.getMessage()); }});
			}
		});
	}
	
}
