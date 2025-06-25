package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.CCCDialog.CCCDialogCallback;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class CCC extends ScrollPanel {

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
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem(), AON.CSS.aonNowrap());
			employeesWorking.ensureDebugId("employeesWorking");
			
			employeePrevMov = addItem("Movimientos previos de trabajadores", new EmployeePrevMovCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem(), AON.CSS.aonNowrap());
			employeePrevMov.ensureDebugId("employeePrevMov");
			
			idc = addItem("IDC", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem(), AON.CSS.aonNowrap());
			idc.ensureDebugId("idc");
			
			laboralLife = addItem("Vida Laboral", new LaboralLifeCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem(), AON.CSS.aonNowrap());
			laboralLife.ensureDebugId("laboralLife");
			
		}
	}

	// --------------------------------------------  UiFields
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	
	// -------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private TgssContextMenu contextMenu;
	private String regime;
	private String ccc;
	
	private List<EnterpriseCCC> cccs;
	private Integer activityId;
	
	private static enum COLS {
		  TYP("Tipo"								, "-moz-available" 	, "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ADD("Cuenta"								, "9rem"			, "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA(AonStringUtils.EMPTY					, "3rem"			, "")
		, PRO(AON.MSG.province()					, "7rem"			, "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"	)
		, BUT(AonStringUtils.EMPTY					, "5rem"			, "")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return styles;
		}
	}
	
	// -------------------------------------------- Constructor

	protected CCC(Integer activityId) {
		this.activityId = activityId;
		
		contextMenu = new TgssContextMenu();
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);
		
		onSearch();
	}
	
	// -------------------------------------------- onSearch
	
	private void onSearch() {
		container.clear();
		tab = new AonCustomTable();
		tab.ensureDebugId("cccTable");
		tab.setMaxHeight("160x");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}

	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		getActivityCCCs(cccsDb -> {
			if(cccs.isEmpty()) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
			} else
				cccs.forEach(ccc -> paintRow(ccc));
		});
	}
	
	private void paintRow(EnterpriseCCC ccc) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton deleteButton = new AonTableButton("Borrar CCC", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.ensureDebugId("delete");
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			
			if(Boolean.TRUE.equals(ccc.isUseByContracts())) 
				fireWarning("No se puede eliminar una cuenta de cotizaci\u00F3n que esta siendo usada por un centro de trabajo y/o por un contrato");
			else if(Boolean.TRUE.equals(ccc.isUseByCra())) 
				fireWarning("No se puede eliminar una cuenta de cotizaci\u00F3n que esta  siendo referenciada desde un CRA existente");
			else {
				AonDialog deleteDialog = new AonDialog("Eliminar CCC", new HTML("\u00BFDesea eliminar el CCC seleccionado\u003F"));
				deleteDialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						deleteButton.setEnabled(true);
					}

					@Override
					public void onAccept() {
						deleteCCC(ccc.getId(), end -> onSearch(), f -> deleteButton.setEnabled(true));
					}
				});
			}
		});
		buttonContainer.add(deleteButton);
		
		AonTableButton tgssMenu = new AonTableButton("TGSS", AON.CSS.aonIconMoreVertical());
		tgssMenu.addClickHandler(e -> {
			e.stopPropagation();
			
			this.regime = getCCCRegimeCode(ccc.getType());
			this.ccc = ccc.getCcc();
			
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
		buttonContainer.add(tgssMenu);
	
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateCCC(ccc), ClickEvent.getType());
		
		Label regime = new Label(getRegime(ccc.getType()));
		tab.addInlineStyle(regime, COLS.TYP.getStyles());
		tab.addRow(row, regime, COLS.TYP.getColWidth());
		
		tab.addRow(row, new Label(getCCCRegimeCode(ccc.getType()) + ccc.getCcc()), COLS.ADD.getColWidth());
		
		AonTableButton statusBtn = new AonTableButton("Estado", checkCCC(ccc.getCcc()) ? AON.CSS.aonIconValid() : AON.CSS.aonIconInvalid());
		tab.addRow(row, statusBtn, COLS.STA.getColWidth());
		
		tab.addRow(row, new Label(ccc.getGeozoneDescription()), COLS.PRO.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void onUpdateCCC(EnterpriseCCC ccc) {
		new CCCDialog(ccc, new CCCDialogCallback() {
			@Override
			public void onAccept(EnterpriseCCC ccc) {
				onSearch();
			}
		});
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
	
	private String getRegime(Byte cccRegime){
		switch (cccRegime) {
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
	
	public String getRegimen() {
		return this.regime;
	}
	
	public String getCcc() {
		return this.ccc;
	}
	
	// -------------------------------------------- Footer Panel TGSS
	
	private void onEmployeesWorking() {
		fireLoading("Obteniendo trabajadores en situacion de alta ...");
		impl.getEmployeesWorking(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, "Trabajadores en situaci\u00f3n de alta", false);
				hideMessagePanel();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarning("TGSS Trabajadores Alta : "  + caught.getMessage());
			}
		});
	}
	
	private void onEmployeePrevMov() {
		fireLoading("Obteniendo movimientos previos de trabajadores ...");
		impl.getEmployeePrevMov(regime, ccc, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, "Movimientos previos de trabajadores", false);
				hideMessagePanel();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarning("TGSS Movimientos Previos : " + caught.getMessage());
			}
		});
	}
	
	public void onIdcCC(Date date) {
		fireLoading("Obteniendo IDC ...");
		impl.getIdcCCC(regime, ccc, date, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, "IDC", false);
				hideMessagePanel();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarning("TGSS Idc : " + caught.getMessage());
			}
		});
	}
	
	public void onLaboralLife(Date date) {
		fireLoading("Obteniendo Informe de Vida Laboral ...");
		impl.getCCCLaboralLife(regime, ccc, date, new Date(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				showPDF(dataURI, "Informe de Vida Laboral", true);
				hideMessagePanel();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarning("TGSS Vida Laboral : " + caught.getMessage());
			}
		});
	}
	
	private void getActivityCCCs(Consumer<List<EnterpriseCCC>> consumer) {
		impl.getActivityCCCList(activityId, new AsyncCallback<List<EnterpriseCCC>>() {

			@Override
			public void onFailure(Throwable caught) {
				fireError(caught.getMessage());
			}

			@Override
			public void onSuccess(List<EnterpriseCCC> result) {
				cccs = result;
				consumer.accept(cccs);
			}
			
		});
	}
	
	private void deleteCCC(Integer cccId, Consumer<Void> consumer, Consumer<Throwable> fail) {
		impl.deleteCCC(cccId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				fireError(caught.getMessage());
				fail.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				consumer.accept(result);
			}
			
		});
	}
	
	protected abstract void fireError(String message);
	protected abstract void fireWarning(String message);
	protected abstract void fireLoading(String message);
	protected abstract void hideMessagePanel();
	protected abstract void showPDF(String dataURI, String title, boolean isLaboralLife);

	
}
