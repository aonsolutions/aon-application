package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.EmployeeTree.showBases;
import static com.esferalia.aon.gwt.payroll.client.EmployeeTree.showResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.IndeterminateTask;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.payroll.client.EmployeeTree.CretaCommand;
import com.esferalia.aon.gwt.payroll.client.EmployeeTree.EnterpriseCretaRequestCommand;
import com.esferalia.aon.gwt.payroll.client.EmployeeTree.EnterpriseDBACommand;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.BankAccount;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEmployee;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsError;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEvent;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.esferalia.aon.gwt.payroll.shared.CretaService.Parameter;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ErrorDescription;
import com.esferalia.aon.gwt.payroll.shared.HttpException;
import com.esferalia.aon.gwt.payroll.shared.Province;
import com.esferalia.aon.gwt.payroll.shared.SaveService;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
//import com.google.gwt.storage.client.Storage;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.typedarrays.shared.Uint8Array;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class MainCreta extends MainEntryPoint implements Enterprises.Listener {
	
	public static <T extends JsFile> T[] get(File file, T[] ts) {
		Map<String, T> map = get(file.name());
		return map.values().toArray(ts);
	}

	public static <T extends JsFile> Map<String, T> add(File file, T ts[]) {
		return add(file.name(), ts);
	}
	
	private final class MainCretaSyncCallback implements SyncCallback {

		private IndeterminateTask syncTask ;
		private List<JsRespuesta> jsRespuestas;
		private List<JsTrabajadoresYTramos> jsTrabajadoresYTramoss ;
		
		public MainCretaSyncCallback(IndeterminateTask syncTask) {
			this.syncTask = syncTask;
			this.jsRespuestas = new ArrayList<JsRespuesta>(5);
			this.jsTrabajadoresYTramoss = new ArrayList<JsTrabajadoresYTramos>(5);
		}
		
		@Override
		public void onEnd() {
			syncTask.messageChanged("Sincronizaci\u00F3n completada");
			syncTask.finished();
			MainCreta.this.closeFootPanel();
			
			if ( !jsRespuestas.isEmpty() )
				MainCreta.add(File.RESPUESTA, jsRespuestas.toArray(new JsRespuesta[jsRespuestas.size()]));
			if ( !jsTrabajadoresYTramoss.isEmpty() )
				MainCreta.add(File.TRABAJADORES_TRAMOS, jsTrabajadoresYTramoss.toArray(new JsTrabajadoresYTramos[jsTrabajadoresYTramoss.size()]));
			
			jsRespuestas.clear();
			jsTrabajadoresYTramoss.clear();
			
			MainCreta.this.enterprisesCretaDetail.onTrabajadoresYTramos();
			
		}

		@Override
		public void onBegin() {
			syncTask.setDescription("Sincronizando mensajes");
		}

		@Override
		public void onMsg(String msg) {
			syncTask.messageChanged(msg);
		}

		@Override
		public void onError(Throwable caught) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRespuesta(JsRespuesta jsRespuesta ){
			syncTask.messageChanged(
					File.RESPUESTA.getFilename()
					+ " " + jsRespuesta.getDate()
					+ " " + jsRespuesta.getType() 
					+ " " + Province.getName(jsRespuesta.getCCC().substring(4, 6))
					+ " (" + jsRespuesta.getCCC().substring(6) + ")"
					+ " Sincronizado"
					) ;

			jsRespuestas.add(jsRespuesta);
			if ( jsRespuestas.size() < 5) 
				return;
			
			MainCreta.add(File.RESPUESTA, jsRespuestas.toArray(new JsRespuesta[5]));
			jsRespuestas.clear();
		
		}

		@Override
		public void onTrabajadoresYTramos(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
			syncTask.messageChanged(
					File.TRABAJADORES_TRAMOS.getFilename()
					+ " " + jsTrabajadoresYTramos.getDate()
					+ " " + jsTrabajadoresYTramos.getType() 
					+ " " + Province.getName(jsTrabajadoresYTramos.getCCC().substring(4, 6))
					+ " (" + jsTrabajadoresYTramos.getCCC().substring(6) + ")"
					+ " Sincronizado"
					) ;

			jsTrabajadoresYTramoss.add(jsTrabajadoresYTramos);
			if ( jsTrabajadoresYTramoss.size() < 5) 
				return;
			
			MainCreta.add(File.TRABAJADORES_TRAMOS, jsTrabajadoresYTramoss.toArray(new JsTrabajadoresYTramos[5]));
			jsTrabajadoresYTramoss.clear();
		}
	}

	static interface Binder extends UiBinder<Widget, MainCreta> {
	}
	
	public static interface SyncCallback {
		void onEnd();
		void onBegin();
		void onMsg(String msg);
		void onError(Throwable caught);
		void onRespuesta(JsRespuesta respuesta);
		void onTrabajadoresYTramos(JsTrabajadoresYTramos trabajadoresYTramos);
	}
	
	@SuppressWarnings("serial")
	private static final Map<String,Map<String,JsFile>> BACKUP_STORAGE = 
			new HashMap<String,Map<String,JsFile>>(2);
	
	private static final Binder binder = GWT.create(Binder.class);

	private static final String AGREEMENT = "c-agreement";

	private ResultsPanel resultsPanel;
	private ProgressPanel progressPanel;

	@UiField
	MinimizePanel footPanel;
	@UiField
	TabLayoutPanel footTabPanel;
	@UiField
	Enterprises enterprises;
	@UiField
	DetailPanel detailPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;

	private CCCCretaDetail cccCretaDetail;
	private ActivityCretaDetail activityCretaDetail;
	private EnterpriseCretaDetail enterpriseCretaDetail;
	private EnterprisesCretaDetail enterprisesCretaDetail;

	private CCCContextMenu cccContextMenu;
	private ActivityContextMenu activityContextMenu;
	private EnterpriseContextMenu enterpriseContextMenu;
	private EnterprisesContextMenu enterprisesContextMenu;
	

	@Override
	public void onModuleLoad() {
		
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources> create(MainEntryPoint.CodeMirrorResources.class).css()
				.ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		resultsPanel = new ResultsPanel();
		progressPanel = new ProgressPanel();

		enterprises.addListener(this);

		cccCretaDetail = new CCCCretaDetail();
		activityCretaDetail = new ActivityCretaDetail();
		enterpriseCretaDetail = new EnterpriseCretaDetail();
		enterprisesCretaDetail = new EnterprisesCretaDetail();
		
		progressPanel.addAttachHandler(e ->  {
			// Synchronize cret@ messages. 
			IndeterminateTask syncTask = new IndeterminateTask();
			syncTask.setDescription("Sincronizando mensajes");
			progressPanel.showIndeterminateTask(syncTask);
			sync( new MainCretaSyncCallback(syncTask));
		});
		showProgressPanel();
		
		footPanel.addMinimizeHandler(e -> closeFootPanel());
		footPanel.addMaximizeHandler(e -> maximizeFootPanel());
		
		MainCreta.this.enterprisesCretaDetail.addAttachHandler( e -> MainCreta.this.enterprisesCretaDetail.onTrabajadoresYTramos());
	}
	
	public void run(Map<CretaService.Parameter, String> params) {
		CretaResults results = ( CretaResults ) resultsPanel.getChild();
		for ( Map.Entry<CretaService.Parameter, String> entry: params.entrySet() )
			results.setParameter(entry.getKey(), entry.getValue());
		results.run();
	}
	

	public void reftification() {
		run(Collections.singletonMap(CretaService.Parameter.INDICADOR_RECTIFICACION, isReftification() ? "off" : "on"));
	}
	
	public boolean isReftification() {
		return 
		(( CretaResults ) resultsPanel.getChild())
		.getParameter(CretaService.Parameter.INDICADOR_RECTIFICACION)
		.map( s -> "on".equalsIgnoreCase(s))
		.orElse(false)
		;
	}
	// --------------------------------------------------- Enterprises.Listener

	@Override
	public void onCCCSelected(CCC ccc) {
		cccCretaDetail.setCCC(ccc);
		cccCretaDetail.onTrabajadoresYTramos();
		detailPanel.setWidget(cccCretaDetail);
	}

	@Override
	public void onActivitySelected(Activity activity) {
		activityCretaDetail.setActivity(activity);
		activityCretaDetail.onTrabajadoresYTramos();
		detailPanel.setWidget(activityCretaDetail);
	}

	@Override
	public void onEnterpriseSelected(Enterprise enterprise) {
		enterpriseCretaDetail.setEnterprise(enterprise);
		enterpriseCretaDetail.onTrabajadoresYTramos();
		detailPanel.setWidget(enterpriseCretaDetail);
	}

	@Override
	public void onEnterprisesSelected(List<Enterprise> enterprises) {
		enterprisesCretaDetail.setEnterprises(enterprises);
		enterprisesCretaDetail.onTrabajadoresYTramos();
		detailPanel.setWidget(enterprisesCretaDetail);
	}

	@Override
	public void onCCCContextMenu(CCC ccc, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		if (cccContextMenu == null)
			cccContextMenu = new CCCContextMenu();
		cccContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		cccContextMenu.setCCC(ccc);
		cccContextMenu.show();
	}

	@Override
	public void onActivityContextMenu(Activity activity, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		if (activityContextMenu == null)
			activityContextMenu = new ActivityContextMenu();
		activityContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		activityContextMenu.setActivity(activity);
		activityContextMenu.show();
	}

	@Override
	public void onEnterpriseContextMenu(Enterprise enterprise, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		if (enterpriseContextMenu == null)
			enterpriseContextMenu = new EnterpriseContextMenu();
		enterpriseContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		enterpriseContextMenu.setEnterprise(enterprise);
		enterpriseContextMenu.show();
	}

	@Override
	public void onEnterprisesContextMenu(List<Enterprise> enterprises, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		if (enterprisesContextMenu == null)
			enterprisesContextMenu = new EnterprisesContextMenu();
		enterprisesContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		enterprisesContextMenu.setEnterprises(enterprises);
		enterprisesContextMenu.show();
	}

	// ------------------------------------------------------- UiHandler methods

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {

	}

	// ---------------------------------------------------------------- Private

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, splitLayoutPanel.getOffsetHeight());
	}

	private void showResultsPanel() {

		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
	}

	private void showProgressPanel() {
		InlineLabel tab = new InlineLabel("Progreso");
		tab.addStyleName(AON.AON_ICON_PROGRESS_BAR);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(progressPanel, tab);
		footTabPanel.selectTab(progressPanel);
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
	}


	public static boolean hasTrabajadoresYTramos(JsRespuesta jsRespuesta) {
		JsEmployee jsEmployees[] = jsRespuesta.getEmployees();
		return jsEmployees != null && jsEmployees.length > 0;
	}

	public static String getIconStyle(JsRespuesta respuesta) {
		if (respuesta == null)
			return AON.AON_ICON_ERRORWARNING;

		JsError jsErros[] = respuesta.getErrors();

		byte icon = 0x0; // 00000000
		for (JsError jsError : jsErros) {
			ErrorDescription error = ErrorDescription.getErrorDescription(jsError.getCode());
			if (error == null)
				icon |= 0x03b;
			else
				icon |= error.accept(new ErrorDescription.Visitor<Byte>() {
					public Byte visitError(ErrorDescription error) {
						return 0x01b;
					}

					public Byte visitWarning(ErrorDescription.WarningDescription error) {
						return 0x02b;
					}

					public Byte visitSuccess(ErrorDescription.SuccessDescription error) {
						return 0x04b;
					}
				});
		}

		if ((icon & 0x01b) == 0x01b)
			return AON.AON_ICON_EXCEPTION;
		if ((icon & 0x02b) == 0x02b)
			return AON.AON_ICON_OKWARNING;
		if ((icon & 0x04b) == 0x04b)
			return AON.AON_ICON_OK;

		return AON.AON_ICON_WARN;
	}

	public static PopupPanel showjsRespuestaToolTip(final JsRespuesta respuesta, final int x, final int y) {

		final DecoratedPopupPanel popupPanel = new DecoratedPopupPanel();
		popupPanel.setAutoHideEnabled(true);
		popupPanel.getElement().getStyle().setZIndex(70);

		JsError errors[] = respuesta.getErrors();

		Grid grid = new Grid(errors.length + 1, 4);
		grid.setBorderWidth(1);
		grid.getElement().getStyle().setProperty("borderCollapse", "collapse");

		// Header
		grid.setText(0, 0, "CODIGO");
		grid.setText(0, 1, "MENSAJE");
		grid.setText(0, 2, "MOTIVO");
		grid.setText(0, 3, "SOLUCION");
		for (int col = 0; col < 4; col++) {
			grid.getCellFormatter().addStyleName(0, col, AON.AON_BOLD);
			grid.getCellFormatter().addStyleName(0, col, AON.AON_TEXT_CENTER);
		}

		for (int i = 0; i < errors.length; i++) {
			String code = errors[i].getCode();

			grid.setText(i + 1, 0, errors[i].getCode());

			ErrorDescription errorDescription = ErrorDescription.getErrorDescription(code);
			if (errorDescription != null) {
				grid.setText(i + 1, 1, errorDescription.getMessage());
				grid.setText(i + 1, 2, errorDescription.getCause());
				grid.setText(i + 1, 3, errorDescription.getSolution());
			} else {
				grid.setText(i + 1, 1, errors[i].getMessage());

			}

		}

		popupPanel.add(grid);

		popupPanel.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				popupPanel.setPopupPosition(x, y);
			}
		});
		return popupPanel;
	}

	private static interface EnterpriseCommand extends ScheduledCommand {
		void setEnterprise(Enterprise enterprise);
	}

	private static interface CCCCommand extends ScheduledCommand {
		void setCCC(CCC ccc);
	}

	private static interface ActivityCommand extends ScheduledCommand {
		void setActivity(Activity activity);
	}

	private static interface EnterprisesCommand extends ScheduledCommand {
		void setEnterprises(List<Enterprise> enterprise);
	}

	private class ShowResultsCommand implements ScheduledCommand {
		@Override
		public void execute() {
			MainCreta.this.showResultsPanel();
		}
	}
	
	protected static class BasesFileEditor extends FileEditor {
		@Override
		void onSaveClick(ClickEvent event) {
			super.onSaveClick(event);
			submit(SaveService.SAVE_URL+ "/" + CretaService.File.BASES, codeArea.getText());
		}
		
	}

	protected  static class BasesMergeEditor extends MergeEditor {
		@Override
		void onSaveClick(ClickEvent event) {
			super.onSaveClick(event);
			submit(SaveService.SAVE_URL+ "/" + CretaService.File.BASES, mergeArea.getText());
		}
	}
	private abstract class BaseCretaDetail extends CretaDetail {

		@Override
		public void onBases(CretaService.JsBasesResult result) {
			
			Window.alert("onBases()");
			

			MergeEditor mergeEditor = new BasesMergeEditor();
			mergeEditor.setOrig(result.getBasesFile());
			mergeEditor.setMode("text/xml");
			mergeEditor.setFoldGutter(true);
			mergeEditor.setLineNumbers(true);
			mergeEditor.setOrig(result.getBasesFile());

			String suffix = 
					getSelected()
					.stream()
					.map( f -> f.getCCC() + " " + f.getFrom() )
					.findFirst()
					.orElse("")
				;

			try {
				mergeEditor.setText(result.getChangedBasesFile());
				mergeEditor.setTitle(CretaService.File.BASES.getFilename());
				mergeEditor.setFilename(CretaService.File.BASES.getFilename() + suffix + ".xml");
				detailPanel.setWidget(mergeEditor);
				mergeEditor.autoRefresh();

			} catch (NoSuchElementException e1) {
				try {
					mergeEditor.setShowDifferences(false);
					mergeEditor.setText(result.getDraftRequestFile());
					mergeEditor.setTitle(CretaService.File.BASES.getFilename());
					mergeEditor.setFilename(CretaService.File.BASES.getFilename() + suffix + ".xml");
					detailPanel.setWidget(mergeEditor);
					mergeEditor.autoRefresh();
				} catch ( NoSuchElementException e2 ){
					FileEditor basesEditor = new BasesFileEditor();
					basesEditor.setMode("text/xml");
					basesEditor.setFoldGutter(true);
					basesEditor.setLineNumbers(true);
					basesEditor.setText(result.getBasesFile());
					basesEditor.setTitle(CretaService.File.BASES.getFilename());
					basesEditor.setFilename(CretaService.File.BASES.getFilename() + suffix + ".xml");
					detailPanel.setWidget(basesEditor);
					basesEditor.autoRefresh();

					CheckBox reftification = new CheckBox("Reftificativa");
					reftification.setValue(result.isRectifying());
					reftification.setStyleName("aon-finding-toolbar-item");
					reftification.addClickHandler(e->MainCreta.this.reftification());
					basesEditor.add(reftification);
				}
			}	

			CretaResults cretaResults = new CretaResults() {
				@Override
				protected void onBases(JsBasesResult result) {
					BaseCretaDetail.this.onBases(result);
				}
			};
			cretaResults.setJsFiles(getSelected());
			cretaResults.addErrors(result.getErrors());
			cretaResults.addWarnings(result.getWarnings());
			cretaResults.addUnknown(result.getUnknown());
			cretaResults.addMessages(new JsEvent[]{});
			cretaResults.setParameter(CretaService.Parameter.NAFS, getSelectedNafs());;

			resultsPanel.setWidget(cretaResults);

			if (result.getErrors().length > 0 || 
				result.getWarnings().length > 0 || 
				result.getUnknown().length > 0)
				showResultsPanel();

			CheckBox reftification = new CheckBox("Reftificativa");
			reftification.setValue(result.isRectifying());
			//reftification.setValue(isReftification());
			reftification.setStyleName("aon-finding-toolbar-item");
			reftification.addClickHandler(e->MainCreta.this.reftification());
			mergeEditor.add(reftification);
		}
		
		@Override
		protected void onDCLResults(JsEvent[] msgs, JsEvent[] errors) {
			CretaResults cretaResults = new CretaResults() {
				@Override
				protected void onBases(JsBasesResult result) {
					// TODO ???
				}
			};
			cretaResults.addErrors(errors);
			cretaResults.addMessages(msgs);
			resultsPanel.setWidget(cretaResults);

			if (errors.length > 0 || msgs.length > 0 )
				showResultsPanel();
		}

		@Override
		protected void onJsFileDblClick(int x, int y, JsFile... jsFiles) {
			FilesEditor filesEditor = new FilesEditor();
			
			Button basesButton = new Button("SLD-FICHERO DE BASES");
			basesButton.setStyleName("aon-finding-toolbar-item");
			basesButton.addStyleName(AON.AON_ICON_SEGSOCIAL_SMALL);
			basesButton.addClickHandler( e-> {
				setSelected(jsFiles[filesEditor.getSelectedIndex()]);
				bases(jsFiles[filesEditor.getSelectedIndex()], this::onBases ); 
				});
			
			filesEditor.add(basesButton);
			
			for (JsFile jsFile : jsFiles) {
				
				FileEditor fileEditor = new FileEditor(false);
				fileEditor.setMode("text/xml");
				fileEditor.setFoldGutter(true);
				fileEditor.setLineNumbers(true);
				fileEditor.setText(jsFile.getXML());

				try {
					CretaService.File file = CretaService.File.valueOf(jsFile.getName());
					filesEditor.add(fileEditor, file.getFilename(), AON.AON_ICON_SEGSOCIAL_SMALL);
				} catch (Exception e) {
					String name = jsFile.getFile().indexOf("TrabajadoresTramos") >= 0
							? CretaService.File.TRABAJADORES_TRAMOS.getFilename()
							: CretaService.File.RESPUESTA.getFilename();
					filesEditor.add(fileEditor, name, AON.AON_ICON_SEGSOCIAL_SMALL);
				}

				fileEditor.autoRefresh();
			}

			// tabLayoutPanel.setVisible(tabLayoutPanel.getTabWidget(0), true);
			detailPanel.setWidget(filesEditor);

		}
		
		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			return super.getEmployeeFullName(jsEmployee);
		}

	}

	private class MainEnterpriseCretaRequestCommand extends EnterpriseCretaRequestCommand implements EnterpriseCommand {

		public MainEnterpriseCretaRequestCommand(File file) {
			super(file, MainCreta.this.detailPanel);
		}

		@Override
		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
			dialog.setData(getCCs(enterprise));
		}

	}

	private class MainEnterpriseDBACommand extends EnterpriseDBACommand implements EnterpriseCommand {

		public MainEnterpriseDBACommand() {
			super(MainCreta.this.detailPanel);
		}

		@Override
		public void setEnterprise(Enterprise enterprise) {
			this.enterpr1se = enterprise;
			setData(getCCs(enterprise));
			setBankAccounts(enterprise.getBankAccounts());

			if (AonStringUtils.isBlank(getHolder()))
				setHolder(enterprise.getName());

		}

	}

	private abstract class MainCretaResponseCommand extends EmployeeTree.CreateResponseCommand {
		public MainCretaResponseCommand(File outFile, File inFile) {
			super(outFile, inFile, MainCreta.this.detailPanel, MainCreta.this.resultsPanel);
		}

		@Override
		protected void showResultsPanel() {
			MainCreta.this.showResultsPanel();
		}

	}

	private class MainEnterpriseCreateResponseCommand extends MainCretaResponseCommand implements EnterpriseCommand {

		private Enterprise enterprise;

		public MainEnterpriseCreateResponseCommand(File outFile, File inFile) {
			super(outFile, inFile);
		}

		@Override
		protected String getDescription(String fullccc) {
			return MainCreta.getDescription(enterprise, fullccc);
		}

		@Override
		protected boolean accept(String fullccc) {
			return MainCreta.accept(enterprise, fullccc);
		}

		// -------------------------------------------------- EnterpriseCommand

		@Override
		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
		}

	}

	private class EnterpriseContextMenu extends ContextMenu {

		EnterpriseCommand enterpriseCommands[] = new EnterpriseCommand[6];

		public EnterpriseContextMenu() {

			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					enterpriseCommands[0] = new MainEnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					enterpriseCommands[1] = new MainEnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_CONFIRMACION),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					enterpriseCommands[2] = new MainEnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_CALCULOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					enterpriseCommands[3] = new MainEnterpriseDBACommand(), AON.AON_ICON_SEGSOCIAL_SMALL,
					AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)", 
					enterpriseCommands[4] = new MainEnterpriseCretaRequestCommand(File.BASES){
						@Override
						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth,
								int toYear, String tipo, Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel, this);
							showResults(result, 
									resultsPanel, 
									r -> { /*TODO: */},  
									r -> showResultsPanel() );
						}				
						
					}, 
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)", 
					enterpriseCommands[5] = new MainEnterpriseCreateResponseCommand(File.BASES,
					File.TRABAJADORES_TRAMOS), AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);

		}

		void setEnterprise(Enterprise enterprise) {
			for (EnterpriseCommand cmd : enterpriseCommands)
				if (cmd != null)
					cmd.setEnterprise(enterprise);
		}

	}

	private class EnterpriseCretaDetail extends BaseCretaDetail {

		private Enterprise enterprise;

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			List<T> filtered = new ArrayList<T>();

			for (T jsFile : jsFiles)
				if (MainCreta.accept(enterprise, jsFile.getCCC()))
					filtered.add(jsFile);

			return filtered;
		}

		@Override
		protected String getDescription(String fullccc) {
			return MainCreta.getDescription(enterprise, fullccc);
		}

		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
		}

		@Override
		void onClickBorradorButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_BORRADOR);
		}

		@Override
		void onClickConfirmacionButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_CONFIRMACION);
		}

		@Override
		void onClickTrabajadoresYTramosButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_TRABAJADORES_TRAMOS);
		}

		@Override
		void onClickDBAButton(ClickEvent e) {
			MainEnterpriseDBACommand cmd = new MainEnterpriseDBACommand();
			cmd.setEnterprise(enterprise);
			cmd.execute();
		}

		protected void onRequestCommand(File file) {
			MainEnterpriseCretaRequestCommand cmd = new MainEnterpriseCretaRequestCommand(file);
			cmd.setEnterprise(enterprise);
			cmd.execute();
		}

		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			for ( Activity activity : enterprise.getActivities() )
				for ( CCC ccc : activity.getCccs() )
					for ( Employee e : ccc.getEmployees() )
						if ( jsEmployee.getNaf().equals(e.getSocialSecurity())) 
							return e.getFullname();
			
			return super.getEmployeeFullName(jsEmployee);
		}
	}

	private class ActivityContextMenu extends ContextMenu {

		ActivityCommand activityCommands[] = new ActivityCommand[7];

		public ActivityContextMenu() {

			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					activityCommands[0] = new MainActivityCretaRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					activityCommands[1] = new MainActivityCretaRequestCommand(CretaService.File.SOLICITUD_BORRADOR),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					activityCommands[2] = new MainActivityCretaRequestCommand(CretaService.File.SOLICITUD_CONFIRMACION),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					activityCommands[3] = new MainActivityCretaRequestCommand(
							CretaService.File.SOLICITUD_CALCULOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					activityCommands[4] = new MainActivityDBACommand(), AON.AON_ICON_SEGSOCIAL_SMALL,
					AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					activityCommands[5] = new MainActivityCretaRequestCommand(File.BASES){
						@Override
						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth,
								int toYear, String tipo, Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel,this);
							showResults(result, 
									resultsPanel, 
									r -> { /*TODO: */},  
									r -> showResultsPanel() );
						}				
						
					},
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)",
					activityCommands[6] = new MainActivityCreateResponseCommand(File.BASES, File.TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);
		}

		public void setActivity(Activity activity) {
			for (ActivityCommand cmd : activityCommands)
				if (cmd != null)
					cmd.setActivity(activity);
		}

	}

	private class MainActivityCretaRequestCommand extends EmployeeTree.CreateRequestCommand implements ActivityCommand {

		private Activity activity;

		public MainActivityCretaRequestCommand(File file) {
			super(file, MainCreta.this.detailPanel);
		}

		// ---------------------------------------------------- ActivityCommand
		@Override
		protected String getDescription(CCC ccc) {
			return activity.getDescription() + ", " + Province.getName(ccc.getGeozone()) + " " + ccc.getCode();
		}

		// ---------------------------------------------------- ActivityCommand
		@Override
		public void setActivity(Activity activity) {
			this.activity = activity;
			dialog.setData(getCCCs(activity));
		}

		protected List<CCC> getCCCs(Activity activity) {
			return activity.getCccs();
		}

	}

	private class MainActivityDBACommand extends EmployeeTree.DBACommand implements ActivityCommand {

		private Activity activity;

		public MainActivityDBACommand() {
			super(MainCreta.this.detailPanel);
		}

		// --------------------------------------------------------------------
		@Override
		protected String getDescription(CCC ccc) {
			return activity.getDescription() + ", " + Province.getName(ccc.getGeozone()) + " " + ccc.getCode();
		}

		// ---------------------------------------------------- ActivityCommand
		@Override
		public void setActivity(Activity activity) {
			this.activity = activity;
			setData(getCCCs(activity));
			setBankAccounts(getBankAccounts(activity));

			if (AonStringUtils.isBlank(getHolder()))
				setHolder(getEnterprise(activity).getName());
		}

		protected List<CCC> getCCCs(Activity activity) {
			return activity.getCccs();
		}

	}

	private class MainActivityCreateResponseCommand extends MainCretaResponseCommand implements ActivityCommand {

		private Activity activity;

		public MainActivityCreateResponseCommand(File outFile, File inFile) {
			super(outFile, inFile);
		}

		@Override
		protected String getDescription(String fullccc) {
			return activity.getDescription();
		}

		@Override
		protected boolean accept(String fullccc) {
			for (CCC ccc : activity.getCccs())
				if (fullccc.endsWith(ccc.getCode()))
					return true;

			return false;
		}
		// ---------------------------------------------------- ActivityCommand

		@Override
		public void setActivity(Activity activity) {
			this.activity = activity;
		}

	}

	private class ActivityCretaDetail extends BaseCretaDetail {

		private Activity activity;

		public void setActivity(Activity enterprise) {
			this.activity = enterprise;
		}

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			List<T> filtered = new ArrayList<T>();

			for (T jsFile : jsFiles)
				if (MainCreta.accept(activity, jsFile.getCCC()))
					filtered.add(jsFile);

			return filtered;
		}

		@Override
		protected String getDescription(String fullccc) {
			return MainCreta.getDescription(activity, fullccc);
		}

		@Override
		void onClickBorradorButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_BORRADOR);
		}

		@Override
		void onClickConfirmacionButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_CONFIRMACION);
		}

		@Override
		void onClickTrabajadoresYTramosButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_TRABAJADORES_TRAMOS);
		}

		@Override
		void onClickDBAButton(ClickEvent e) {
			MainActivityDBACommand cmd = new MainActivityDBACommand();
			cmd.setActivity(activity);
			cmd.execute();
		}

		protected void onRequestCommand(File file) {
			MainActivityCretaRequestCommand cmd = new MainActivityCretaRequestCommand(file);
			cmd.setActivity(activity);
			cmd.execute();
		}

		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			for ( CCC ccc : activity.getCccs() )
				for ( Employee e : ccc.getEmployees() )
					if ( jsEmployee.getNaf().equals(e.getSocialSecurity())) 
						return e.getFullname();
			
			return super.getEmployeeFullName(jsEmployee);
		}
	}

	private class CCCContextMenu extends ContextMenu {

		CCCCommand cccCommands[] = new CCCCommand[7];

		public CCCContextMenu() {

			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					cccCommands[0] = new MainCCCCretaRequestCommand(CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					cccCommands[1] = new MainCCCCretaRequestCommand(CretaService.File.SOLICITUD_BORRADOR),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					cccCommands[2] = new MainCCCCretaRequestCommand(CretaService.File.SOLICITUD_CONFIRMACION),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					cccCommands[3] = new MainCCCCretaRequestCommand(CretaService.File.SOLICITUD_CALCULOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios", 
					cccCommands[4] = new MainCCCDBACommand(),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					cccCommands[5] = new BasesCCCCretaRequestCommand(File.BASES) { // new MainCCCCretaRequestCommand(File.BASES){
						@Override
						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth,
								int toYear, String tipo, Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel, this);
							showResults(result, 
									resultsPanel, 
									r -> { /*TODO: */},  
									r -> showResultsPanel() );
						}				
						
					},
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)",
					cccCommands[6] = new MainCCCCreateResponseCommand(File.BASES, File.TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);
		}

		void setCCC(CCC ccc) {
			for (CCCCommand cmd : cccCommands)
				if (cmd != null)
					cmd.setCCC(ccc);
		}

	}

	private class MainCCCCretaRequestCommand extends EmployeeTree.CreateRequestCommand implements CCCCommand {

		public MainCCCCretaRequestCommand(File file) {
			super(file, MainCreta.this.detailPanel);

			dialog.selectLabel.setVisible(false);
			dialog.selectDataGrid.setVisible(false);
		}

		@Override
		protected String getDescription(CCC ccc) {
			return Province.getName(ccc.getGeozone()) + " " + ccc.getCode();
		}

		@Override
		public void setCCC(CCC ccc) {
			dialog.setData(Collections.singletonList(ccc));
			dialog.setSelectedData(Collections.singletonList(ccc));
		}

	}

	protected class BasesCCCCretaRequestCommand extends CretaCommand
		implements CretaRequestDialog.Callback<Employee>, CCCCommand {
		
		private CCC ccc;
		private CretaRequestDialog<Employee> dialog;

		public BasesCCCCretaRequestCommand(File file) {
			super(file, MainCreta.this.detailPanel);
			dialog = new CretaRequestDialog.CretaEmployeeRequestDialog(this) {
				
				
				
				@Override
				void onMonthChanged( ChangeEvent e ){
					BasesCCCCretaRequestCommand.this.onMonthChanged(monthListBox.getSelected());
				}
				
			};
			dialog.setVisibleReftificationMark(true);
			dialog.typeListBox.addChangeHandler(event -> dialog.setVisibleI54("L03".equals(dialog.getType())));
		}

		// ---------------------------------------------------------- CCCCommand
		
		@Override
		public void setCCC(CCC ccc) {
			this.ccc = ccc;
			onMonthChanged(dialog.getFromMonth());
		}

		// ------------------------------- CretaRequestDialog.Callback<Employee>
		
		@Override
		public void execute() {
			dialog.center();
			dialog.show();
		}

		@Override
		public boolean onAccept(CretaRequestDialog<Employee> dialog) {
			String tipo = dialog.getType();
			Date fromMonth = dialog.getFromMonth();
			Date toMonth = dialog.getToMonth();
			Date ctrlMonth = dialog.getToMonth();
			int desdeMes = fromMonth.getMonth() + 1;
			int desdeAnyo = fromMonth.getYear() + 1900;
			int hastaMes = toMonth.getMonth() + 1;
			int hastaAnyo = toMonth.getYear() + 1900;
			int ctrlMes = ctrlMonth.getMonth() + 1;
			int ctrlAnyo = ctrlMonth.getYear() + 1900;
			long autorizado = dialog.getAuthorized();
			boolean basesMesAnterior = dialog.previousBases();
			boolean calcsDetailed = dialog.calcsDetailed();
			String i54 = dialog.getI54();
			boolean reftificationMark = dialog.reftificationMark();
			

			CCC cccCopy  = new CCC();
			cccCopy.setId(ccc.getId());
			cccCopy.setCode(ccc.getCode());
			cccCopy.setRegime(ccc.getRegime());
			cccCopy.setGeozone(ccc.getGeozone());
			dialog.getSelectedData().forEach(e -> cccCopy.addEmployee(e));
			
			send(autorizado, 
				desdeMes, 
				desdeAnyo, 
				hastaMes, 
				hastaAnyo, 
				ctrlMes, 
				ctrlAnyo, 
				tipo, 
				Collections.singleton(cccCopy), 
				basesMesAnterior, 
				calcsDetailed,
				i54,
				reftificationMark);

			return true;
		}
		
		// --------------------------------------------------------------------
		
		public void reexecute(Consumer<CretaRequestDialog<Employee>> consumer) {
			consumer.accept(dialog);
			onAccept(dialog);
		}

		void onMonthChanged( Date month ){
			List<Employee> employees = new LinkedList<Employee>();
			for ( Employee e: ccc.getEmployees() ) {
				if ( month.before(e.getStartDate()))
					continue;
				if ( month.after(e.getEndDate()))
					continue;
				employees.add(e);
			}
			dialog.setData(employees);
		}


	}

	private class MainCCCDBACommand extends EmployeeTree.DBACommand implements CCCCommand {

		public MainCCCDBACommand() {
			super(MainCreta.this.detailPanel);

		}

		@Override
		protected String getDescription(CCC ccc) {
			return Province.getName(ccc.getGeozone()) + " " + ccc.getCode();
		}

		@Override
		public void setCCC(CCC ccc) {
			setData(Collections.singletonList(ccc));
			setSelectedData(Collections.singletonList(ccc));
			setBankAccounts(getBankAccounts(ccc));

			if (AonStringUtils.isBlank(getHolder()))
				setHolder(getEnterprise(ccc).getName());
		}

	}

	private class MainCCCCreateResponseCommand extends MainCretaResponseCommand implements CCCCommand {

		private CCC ccc;

		public MainCCCCreateResponseCommand(File outFile, File inFile) {
			super(outFile, inFile);
		}

		@Override
		protected String getDescription(String fullccc) {
			String province = fullccc.substring(4, 6);
			return Province.getName(province);
		}

		@Override
		protected boolean accept(String fullccc) {
			return fullccc.endsWith(ccc.getCode());
		}

		// ---------------------------------------------------- ActivityCommand

		@Override
		public void setCCC(CCC ccc) {
			this.ccc = ccc;
		}

	}

	private class CCCCretaDetail extends BaseCretaDetail {

		private CCC ccc;

		public void setCCC(CCC ccc) {
			this.ccc = ccc;
		}

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			List<T> filtered = new ArrayList<T>();

			for (T jsFile : jsFiles)
				if (MainCreta.accept(ccc, jsFile.getCCC()))
					filtered.add(jsFile);

			return filtered;
		}

		@Override
		protected String getDescription(String fullccc) {
			return MainCreta.getDescription(ccc, fullccc);
		}

		@Override
		void onClickBorradorButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_BORRADOR);
		}

		@Override
		void onClickConfirmacionButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_CONFIRMACION);
		}

		@Override
		void onClickTrabajadoresYTramosButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_TRABAJADORES_TRAMOS);
		}

		@Override
		void onClickDBAButton(ClickEvent e) {
			MainCCCDBACommand cmd = new MainCCCDBACommand();
			cmd.setCCC(ccc);
			cmd.execute();
		}

		protected void onRequestCommand(File file) {
			MainCCCCretaRequestCommand cmd = new MainCCCCretaRequestCommand(file);
			cmd.setCCC(ccc);
			cmd.execute();
		}

		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			for ( Employee e : ccc.getEmployees() )
				if ( jsEmployee.getNaf().equals(e.getSocialSecurity())) 
					return e.getFullname();
			
			return super.getEmployeeFullName(jsEmployee);
		}
	}

	private class EnterprisesContextMenu extends ContextMenu {

		EnterprisesCommand cretaRequestCommands[] = new EnterprisesCommand[7];

		public EnterprisesContextMenu() {

			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					cretaRequestCommands[0] = new EnterprisesCretaRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					cretaRequestCommands[1] = new EnterprisesCretaRequestCommand(CretaService.File.SOLICITUD_BORRADOR),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					cretaRequestCommands[2] = new EnterprisesCretaRequestCommand(
							CretaService.File.SOLICITUD_CONFIRMACION),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					cretaRequestCommands[3] = new EnterprisesCretaRequestCommand(
							CretaService.File.SOLICITUD_CALCULOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					cretaRequestCommands[4] = new EnterprisesDBACommand(), AON.AON_ICON_SEGSOCIAL_SMALL,
					AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					cretaRequestCommands[5] = new EnterprisesCretaRequestCommand(
							File.BASES){
						@Override
						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth,
								int toYear, String tipo, Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel, this);
							showResults(result, 
									resultsPanel, 
									r -> { /*TODO: */},  
									r -> showResultsPanel() );
						}				
					},
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)",
					cretaRequestCommands[6] = new MainEnterprisesCreateResponseCommand(File.BASES,
							File.TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);

		}

		void setEnterprises(List<Enterprise> enterprises) {
			for (EnterprisesCommand cmd : cretaRequestCommands)
				if (cmd != null)
					cmd.setEnterprises(enterprises);
		}

	}

	private class EnterprisesCretaRequestCommand extends EmployeeTree.CreateRequestCommand
			implements EnterprisesCommand {

		private List<Enterprise> enterprises;

		public EnterprisesCretaRequestCommand(File file) {
			super(file, MainCreta.this.detailPanel);
		}

		void setSelected(List<CCC> cccs) {
			dialog.setSelectedData(cccs);
		}

		@Override
		protected String getDescription(CCC ccc) {
			return MainCreta.getDescription(ccc, enterprises);
		}

		@Override
		public void setEnterprises(List<Enterprise> enterprises) {
			this.enterprises = enterprises;
			dialog.setData(getCCCs(enterprises));
		}

	}

	private class EnterprisesDBACommand extends EmployeeTree.DBACommand implements EnterprisesCommand {

		private List<Enterprise> enterprises;

		public EnterprisesDBACommand() {
			super(MainCreta.this.detailPanel);
		}

		void setSelected(List<CCC> cccs) {
			setSelectedData(cccs);
		}

		@Override
		protected String getDescription(CCC ccc) {
			return MainCreta.getDescription(ccc, enterprises);
		}

		@Override
		public void setEnterprises(List<Enterprise> enterprises) {
			this.enterprises = enterprises;
			setData(getCCCs(enterprises));
			setBankAccounts(getBankAccounts(enterprises));
		}

	}

	private class MainEnterprisesCreateResponseCommand extends MainCretaResponseCommand implements EnterprisesCommand {

		private List<Enterprise> enterprises;

		public MainEnterprisesCreateResponseCommand(File outFile, File inFile) {
			super(outFile, inFile);
		}

		@Override
		protected String getDescription(String fullccc) {

			String province = fullccc.substring(4, 6);

			for (Enterprise enterprise : enterprises)
				for (Activity activity : enterprise.getActivities())
					for (CCC ccc : activity.getCccs())
						if (fullccc.endsWith(ccc.getCode()))
							return enterprise.getName() + " " + activity.getDescription() + ", "
									+ Province.getName(province);

			return "";
		}

		@Override
		protected boolean accept(String fullccc) {

			for (Enterprise enterprise : enterprises)
				for (Activity activity : enterprise.getActivities())
					for (CCC ccc : activity.getCccs())
						if (fullccc.endsWith(ccc.getCode()))
							return true;

			return false;
		}

		// -------------------------------------------------- EnterpriseCommand

		@Override
		public void setEnterprises(List<Enterprise> enterprises) {
			this.enterprises = enterprises;
		}

	}

	private class EnterprisesCretaDetail extends BaseCretaDetail {

		private List<Enterprise> enterprises;

		public void setEnterprises(List<Enterprise> enterprises) {
			this.enterprises = enterprises;
		}

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			List<T> filtered = new ArrayList<T>();

			for (T jsFile : jsFiles)
				if (MainCreta.accept(enterprises, jsFile.getCCC()))
					filtered.add(jsFile);

			Collections.sort(filtered, JsFileComparator.newInstace());

			return filtered;
		}

		@Override
		protected String getDescription(String fullccc) {
			return MainCreta.getDescription(enterprises, fullccc);
		}

		@Override
		void onClickBorradorButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_BORRADOR);
		}

		@Override
		void onClickConfirmacionButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_CONFIRMACION);
		}

		@Override
		void onClickTrabajadoresYTramosButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_TRABAJADORES_TRAMOS);
		}

		@Override
		void onClickDBAButton(ClickEvent e) {
			EnterprisesDBACommand cmd = new EnterprisesDBACommand();
			cmd.setEnterprises(enterprises);
			cmd.execute();
		}

		protected void onRequestCommand(File file) {
			EnterprisesCretaRequestCommand cmd = new EnterprisesCretaRequestCommand(file);
			cmd.setEnterprises(enterprises);
			cmd.execute();
		}

		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			for ( int i = 0; i < enterprises.size(); i++ )
				for ( Activity activity : enterprises.get(i).getActivities() )
					for ( CCC ccc : activity.getCccs() )
						for ( Employee e : ccc.getEmployees() )
							if ( jsEmployee.getNaf().equals(e.getSocialSecurity())) 
								return e.getFullname();
			
			return super.getEmployeeFullName(jsEmployee);
		}
	}

	private Enterprise getEnterprise(CCC ccc) {
		return enterprises.getEnterprise(ccc);
	}

	private Enterprise getEnterprise(Activity activity) {
		return enterprises.getEnterprise(activity);
	}

	private Collection<BankAccount> getBankAccounts(CCC ccc) {
		return enterprises.getEnterprise(ccc).getBankAccounts();
	}

	private Collection<BankAccount> getBankAccounts(Activity activity) {
		return enterprises.getEnterprise(activity).getBankAccounts();
	}

	private Collection<BankAccount> getBankAccounts(Workplace workplace) {
		return enterprises.getEnterprise(workplace).getBankAccounts();
	}


	// ------------------------------------------------------------------------

	protected static void submit(String url, String xml) {
		XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();
		
		//be silent
		//xmlHttpRequest.setOnReadyStateChange(handler); 

		xmlHttpRequest.open("POST", url);

		xmlHttpRequest.setRequestHeader("Content-Type", "text/xml");

		StringBuffer requestBuffer = new StringBuffer();
		// We said it's form data (it could be something else)
		requestBuffer.append("Content-Disposition: attachment\r\n");
		requestBuffer.append("Content-Lengh: "+xml.length()+"\r\n");
		// There is always a blank line between the meta-data and the data
		requestBuffer.append("\r\n");
		requestBuffer.append(xml);
		requestBuffer.append("\r\n");


		xmlHttpRequest.send(requestBuffer.toString());
	}


	protected static void submit(String url, Map<String, Collection<String>> datas, Collection<JsFile> jsFiles,
			final AsyncCallback<JsBasesResult> cb) {

		submit(url, datas, jsFiles, new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				try {
					int state = xhr.getReadyState();
					if (state != XMLHttpRequest.DONE)
						return;
					String json = xhr.getResponseText();
					JsBasesResult result = eval("(" + json + ")");
					cb.onSuccess(result);
				} catch (Throwable caught) {
					String json = xhr.getResponseText();
					Window.alert(json);
					cb.onFailure(caught);
				}

			}
		});
	}

	protected static void submit(String url, Map<String, Collection<String>> datas, Collection<JsFile> jsFiles,
			final ReadyStateChangeHandler handler) {

		XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();

		xmlHttpRequest.setOnReadyStateChange(handler);

		xmlHttpRequest.open("POST", url);

		/* enctype is multipart/form-data */
		String boundary = "---------------------------" + Long.toHexString(System.currentTimeMillis());
		xmlHttpRequest.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

		StringBuffer requestBuffer = new StringBuffer();

		for (Map.Entry<String, Collection<String>> entry : datas.entrySet()) {
			for (String value : entry.getValue()) {
				// We start a new part in our body's request
				requestBuffer.append("--" + boundary + "\r\n");
				// We said it's form data (it could be something else)
				requestBuffer.append("Content-Disposition: form-data; "
						// We define the name of the form data
						+ "name=\"" + entry.getKey() + "\"\r\n");
				// There is always a blank line between the meta-data and the
				// data
				requestBuffer.append("\r\n");

				requestBuffer.append(value);

				requestBuffer.append("\r\n");
			}
		}

		for (JsFile jsFile : jsFiles) {
			// We start a new part in our body's request
			requestBuffer.append("--" + boundary + "\r\n");
			// We said it's form data (it could be something else)
			requestBuffer.append("Content-Disposition: form-data; "
					// We define the name of the form data
					+ "name=\"" + jsFile.getName() + "\"; "
					// We provide the 'real' name of the file
					+ "filename=\"" + jsFile.getId() + ".xml" + "\"\r\n");
			// We provide the mime type of the file
			requestBuffer.append("Content-Type: text/xml\r\n");
			// There is always a blank line between the meta-data and the data
			requestBuffer.append("\r\n");

			requestBuffer.append(jsFile.getXML());

			requestBuffer.append("\r\n");
		}


		// Once we are done, we "close" the body's request
		requestBuffer.append("--" + boundary + "--\r\n");

		xmlHttpRequest.send(requestBuffer.toString());

	}


	protected static void __sync(final AsyncCallback<Void> cb) {

		JsFile respuestas[] = MainCreta.get(CretaService.File.RESPUESTA, new JsFile[] {});
		JsFile trabajadoresYTramos[] = MainCreta.get(CretaService.File.TRABAJADORES_TRAMOS, new JsFile[] {});

		ArrayList<JsFile> jsFiles = new ArrayList<JsFile>(respuestas.length + trabajadoresYTramos.length);
		Collections.addAll(jsFiles, respuestas);
		Collections.addAll(jsFiles, trabajadoresYTramos);

		Map<String, Collection<String>> options = Collections.emptyMap();

		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.TRABAJADORES_TRAMOS, options, jsFiles,
				new ReadyStateChangeHandler() {
					@Override
					public void onReadyStateChange(XMLHttpRequest xhr) {
						try {
							int state = xhr.getReadyState();
							if (state != XMLHttpRequest.DONE)
								return;
							// TODO: Errors !!!
							String html = xhr.getResponseText();
							RegExp regExp = RegExp.compile(
									"parent.__onTrabajadoresYTramos\\s*\\(\\s*(\\[(.|[\\r\\n])*\\])\\s*,\\s*(\\[(.|[\\r\\n])*\\])\\s*\\)",
									"gim");
							MatchResult matchResult = regExp.exec(html);
							JsArray<JsFile> trabajadoresYTramosArr = eval("("+matchResult.getGroup(1)+")");
	
							JsFile trabajadoresYTramos [] = new JsFile[trabajadoresYTramosArr.length()];
							for ( int i = 0; i < trabajadoresYTramos.length; i++ )
								trabajadoresYTramos[i] = trabajadoresYTramosArr.get(i);
							MainCreta.add(File.TRABAJADORES_TRAMOS, trabajadoresYTramos);
							
							JsArray<JsFile> respuestasArr = eval("("+matchResult.getGroup(3)+")");
							JsFile respuestas [] = new JsFile[respuestasArr.length()];
							
							for ( int i = 0; i < respuestas.length; i++ )
								respuestas[i] = respuestasArr.get(i);
							
							MainCreta.add(File.RESPUESTA, respuestas);
							
							cb.onSuccess(null);
						} catch ( Throwable caught) {
							cb.onFailure(caught);
						}
					}

				});

	}
	
	protected static void sync(final AsyncCallback<Void> cb) {

		JsFile respuestas[] = MainCreta.get(CretaService.File.RESPUESTA, new JsFile[] {});
		JsFile trabajadoresYTramos[] = MainCreta.get(CretaService.File.TRABAJADORES_TRAMOS, new JsFile[] {});

		ArrayList<JsFile> jsFiles = new ArrayList<JsFile>(respuestas.length + trabajadoresYTramos.length);
		Collections.addAll(jsFiles, respuestas);
		Collections.addAll(jsFiles, trabajadoresYTramos);

		Map<String, Collection<String>> options = Collections.emptyMap();

		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.TRABAJADORES_TRAMOS, options, jsFiles,
				new ReadyStateChangeHandler() {
					@Override
					public void onReadyStateChange(XMLHttpRequest xhr) {
						try {
							int state = xhr.getReadyState();
							if (state != XMLHttpRequest.DONE)
								return;
							// TODO: Errors !!!
							String html = xhr.getResponseText();
							
							int open = html.indexOf('[', 0);
							int close = html.indexOf(']', open+1);
							JsArray<JsFile> trabajadoresYTramosArr = eval("("+html.substring(open, close+1)+")");
	
							JsFile trabajadoresYTramos [] = new JsFile[trabajadoresYTramosArr.length()];
							for ( int i = 0; i < trabajadoresYTramos.length; i++ )
								trabajadoresYTramos[i] = trabajadoresYTramosArr.get(i);
							MainCreta.add(File.TRABAJADORES_TRAMOS, trabajadoresYTramos);
							
							open = html.indexOf('[', close+1);
							close = html.indexOf(')', open+1);
							JsArray<JsFile> respuestasArr = eval("("+html.substring(open, close)+")");
							JsFile respuestas [] = new JsFile[respuestasArr.length()];
							
							for ( int i = 0; i < respuestas.length; i++ )
								respuestas[i] = respuestasArr.get(i);
							
							MainCreta.add(File.RESPUESTA, respuestas);
							
							cb.onSuccess(null);
						} catch ( Throwable caught) {
							Window.alert(caught.getMessage());
							cb.onFailure(caught);
						}
					}

				});

	}

	protected static void sync(final SyncCallback cb) {

		JsFile respuestas[] = MainCreta.get(CretaService.File.RESPUESTA, new JsFile[] {});
		JsFile trabajadoresYTramos[] = MainCreta.get(CretaService.File.TRABAJADORES_TRAMOS, new JsFile[] {});

		ArrayList<JsFile> jsFiles = new ArrayList<JsFile>(respuestas.length + trabajadoresYTramos.length);
		Collections.addAll(jsFiles, respuestas);
		Collections.addAll(jsFiles, trabajadoresYTramos);

		cb.onBegin();

		Map<String, Collection<String>> options = Collections.emptyMap();

		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.TRABAJADORES_TRAMOS, options, jsFiles,
				new ReadyStateChangeHandler() {
					
					private int read = 0;
					private Consumer<String> consumer = this::beginTrabajadoresYTramos;
					
					@Override
					public void onReadyStateChange(XMLHttpRequest xhr) {
						int state = xhr.getReadyState();
						if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
							String responseText = xhr.getResponseText();
							try {
								
								String line = readLine(responseText);
								while ( null != line ) {
									consumer.accept(line);
									line = readLine(responseText);
								}

							} catch (Throwable caught) {
								cb.onError(caught);
							}
						}
					}
					
					private void end(String line) {
					}


					private void respuestas(String line){
						if ( line == null)
							return;
						if ( line.isEmpty())
							return;
						if ( line.startsWith(","))
							return;
						
						if ( line.startsWith("//MESSAGE ")){
							cb.onMsg(line.substring(10));
						}
						else if ( line.startsWith("//END_RESPUESTAS")){
							consumer = this::end;
							cb.onEnd();
						}
						else {
							try {
								JsRespuesta respuesta = eval("("+line+")");
								cb.onRespuesta(respuesta);
							} catch ( Throwable t ) {
								cb.onError(t);
							}
						}
					}

					private void trabajadoresYTramos(String line){
						if ( line == null)
							return;
						if ( line.isEmpty())
							return;
						if ( line.startsWith(","))
							return;
						
						if ( line.startsWith("//MESSAGE ")){
							cb.onMsg(line.substring(10));
						}
						else if ( line.startsWith("//END_TRABAJADORES_TRAMOS")){
							consumer = this::beginRespuestas;
						}
						else { 
							try {
								JsTrabajadoresYTramos trabajadoresYTramos = eval("("+line+")");
								cb.onTrabajadoresYTramos(trabajadoresYTramos);
							} catch ( Throwable t) {
								cb.onError(t);
							}
						}
					}
					
					private void beginRespuestas(String line) {
						if ( line.startsWith("//BEGIN_RESPUESTAS"))
							consumer = this::respuestas;
					}

					private void beginTrabajadoresYTramos(String line) {
						if ( line.startsWith("//BEGIN_TRABAJADORES_TRAMOS"))
							consumer = this::trabajadoresYTramos;
					}
					
					private String readLine( String responseText ) {
						StringBuffer line = new StringBuffer();
						
						for ( int start = read;  start < responseText.length(); start++){
							char ch = responseText.charAt(start);
							
							if ( ch == '\r'){ 
								skipCRLF(start++, responseText);
								return line.toString();
							}
							else if ( ch == '\n'){
								skipCRLF(start++, responseText);
								return line.toString();
							}
							else {
								line.append(ch);
							}
						}
						
						return null ;
					}
					
					private void skipCRLF(int last, String responseText) {
						for ( read = last;  read < responseText.length(); read++){
							char ch = responseText.charAt(read);
							if ( ch == '\r'){
								continue;
							}
							else if ( ch == '\n'){
								continue;
							}
							return;
						}
					}

				});

	}
	
	protected void bases(JsFile jsFile, Consumer<JsBasesResult> onBases) {
		
		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.BASES,
				Collections.emptyMap(),
				Collections.singletonList(jsFile),
				new AsyncCallback<CretaService.JsBasesResult>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsBasesResult result) {
						onBases.accept(result);
					}
				});
		
		
		
	}
	
	// ------------------------------------------------------------------------


	private static String getDescription(CCC ccc, String fullccc) {
		String province = fullccc.substring(4, 6);
		return Province.getName(province);
	}

	private static boolean accept(CCC ccc, String fullccc) {
		return fullccc.endsWith(ccc.getCode());
	}

	private static String getDescription(Activity activity, String fullccc) {
		String province = fullccc.substring(4, 6);
		return activity.getDescription() + ", " + Province.getName(province);

	}

	private static boolean accept(Activity activity, String fullccc) {

		for (CCC ccc : activity.getCccs())
			if (fullccc.endsWith(ccc.getCode()))
				return true;

		return false;
	}

	private static String getDescription(Enterprise enterprise, String fullccc) {

		String province = fullccc.substring(4, 6);

		for (Activity activity : enterprise.getActivities())
			for (CCC ccc : activity.getCccs())
				if (fullccc.endsWith(ccc.getCode()))
					return activity.getDescription() + ", " + Province.getName(province);

		return "";
	}

	private static boolean accept(Enterprise enterprise, String fullccc) {

		for (Activity activity : enterprise.getActivities())
			for (CCC ccc : activity.getCccs())
				if (fullccc.endsWith(ccc.getCode()))
					return true;

		return false;
	}

	private static String getDescription(Collection<Enterprise> enterprises, String fullccc) {

		String province = fullccc.substring(4, 6);

		for (Enterprise enterprise : enterprises)
			for (Activity activity : enterprise.getActivities())
				for (CCC ccc : activity.getCccs())
					if (fullccc.endsWith(ccc.getCode()))
						return enterprise.getName() + " " + activity.getDescription() + ", "
								+ Province.getName(province);

		return "";
	}

	private static boolean accept(Collection<Enterprise> enterprises, String fullccc) {
		for (Enterprise enterprise : enterprises)
			for (Activity activity : enterprise.getActivities())
				for (CCC ccc : activity.getCccs())
					if (fullccc.endsWith(ccc.getCode()))
						return true;

		return false;
	}

	private static List<String> getCCCs(Collection<Enterprise> enterprises) {
		List<String> cccs = new ArrayList<String>();
		for (Enterprise enterprise : enterprises)
			for (Activity activity : enterprise.getActivities())
				for (CCC ccc : activity.getCccs())
					cccs.add(ccc.getRegime() + ccc.getGeozone() + ccc.getCode());
		return cccs;
	}

	private static <T extends JsFile> Collection<T> merge(T t1[], T t2[]) {
		Map<String, T> map = new HashMap<String, T>();
		for (T t : t1)
			map.put(t.getId(), t);
		for (T t : t2)
			map.put(t.getId(), t);

		return map.values();
	}


	private static <T extends JsFile> Map<String, T> add(String key, T ts[]) {

		Map<String, T> map = get(key);
		for (T t : ts) {
			if ( isOlder(t, map.get(t.getId())) )
				continue;
			
			map.put(t.getId(), t);
		}

		return Collections.unmodifiableMap(map);
	}
	

	private static <T extends JsFile> Map<String, T> get(String key) {
		Map<String, T> map ;
		try {
			map = getFromBackup(key);
		} catch ( Exception backupException ) {
			map = new HashMap<String,T>();
			saveAtBackup(key, map);
		}
		
		return map;
	}

	private static <T extends JsFile> void saveAtBackup(String key, Map<String, T>  map) {
		BACKUP_STORAGE.put(key,(Map<String, JsFile>) map); 
	}

	private static <T extends JsFile> Map<String, T> getFromBackup(String key) {
		if ( BACKUP_STORAGE.containsKey(key)) 
		 return (Map<String, T>) BACKUP_STORAGE.get(key) ;
		throw new UnsupportedOperationException();
	}


	public static class JsFileComparator<T extends JsFile> implements Comparator<T> {

		public static <T extends JsFile> JsFileComparator<T> newInstace() {
			return new JsFileComparator<T>();
		}

		private JsFileComparator() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int compare(T t1, T t2) {

			// Dates DESC
			int compare = t2.getFrom().compareTo(t1.getFrom());
			if (compare == 0)
				compare = t2.getTo().compareTo(t1.getTo());

			if (compare == 0)
				compare = t1.getCCC().compareTo(t2.getCCC());
			return compare;
		}
	}

	public static void send(File file, Map<Parameter, Collection<String>> params, final AsyncCallback<String> cb) {
		StringBuffer requestDataBuffer = new StringBuffer();

		for (Entry<Parameter, Collection<String>> entry : params.entrySet())
			for (String value : entry.getValue())
				requestDataBuffer.append("&" + entry.getKey() + "=" + value);

		// Send request to server and catch any errors.

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", CretaService.CRETA_URL + "/" + file.name());
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();

				if (state != XMLHttpRequest.DONE)
					return;

				int status = xhr.getStatus();

				// Successful 2xx
				if (status >= 200 && status < 300)
					cb.onSuccess(xhr.getResponseText());
				else
					cb.onFailure(new HttpException(status, xhr.getResponseText()));
			}

		});

		xhr.send(requestDataBuffer.toString());

	}

	// ------------------------------------------------------------------------

	private static void sendAsBinary(XMLHttpRequest xmlHttpRequest, String sData) {
		int nBytes = sData.length();
		Uint8Array ui8Data = Uint8ArrayNative.create(nBytes);
		for (int i = 0; i < nBytes; i++)
			ui8Data.set(i, sData.charAt(i) & 0xFF);

		/* send as ArrayBufferView...: */
		// xmlHttpRequest.send(ui8Data);
		/* ...or as ArrayBuffer (legacy)...: this.send(ui8Data.buffer); */
		// xmlHttpRequest.send(ui8Data.buffer());
	}

	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

	private static List<CCC> getCCs(Enterprise enterprise) {
		List<CCC> cccs = new LinkedList<CCC>();
		for (Activity activity : enterprise.getActivities())
			cccs.addAll(activity.getCccs());
		return cccs;
	}

	private static List<CCC> getCCCs(List<Enterprise> enterprises) {
		List<CCC> cccs = new ArrayList<CCC>();
		for (Enterprise enterprise : enterprises)
			for (Activity activity : enterprise.getActivities())
				cccs.addAll(activity.getCccs());
		return cccs;

	}

	private static Collection<BankAccount> getBankAccounts(Collection<Enterprise> enterprises) {
		List<BankAccount> bankAccounts = new ArrayList<BankAccount>();
		for (Enterprise enterprise : enterprises)
			bankAccounts.addAll(enterprise.getBankAccounts());
		return bankAccounts;
	}

	private static String getDescription(CCC ccc, List<Enterprise> enterprises) {
		String province = ccc.getGeozone();

		for (Enterprise enterprise : enterprises)
			for (Activity activity : enterprise.getActivities())
				for (CCC cc : activity.getCccs())
					if (ccc.getCode().equals(cc.getCode()))
						return enterprise.getName() + " " + activity.getDescription() + ", "
								+ Province.getName(province) + " " + ccc.getCode();

		return ccc.getCode();
	}

	private static <T extends JsFile> Map<String, T> upload(String key, T ts[]) {

		Map<String, T> map = get(key);

		for (T t : ts)
			map.put(t.getId(), t);

		return Collections.unmodifiableMap(map);
	}
	
	private static boolean isOlder(JsFile f1, JsFile f2) {
		if (f2 == null)
			return false;
		
		int compare = f1.getDate().compareTo(f2.getDate());
		if ( compare == 0 )
			compare = f1.getTime().compareTo(f2.getTime());
		
		return compare < 0;
	}

	// ------------------------------------------------------------------------

}
