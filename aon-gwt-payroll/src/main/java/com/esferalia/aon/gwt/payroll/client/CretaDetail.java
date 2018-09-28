package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.MainCreta.hasTrabajadoresYTramos;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.CRETA_URL;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.File.DOCUMENTO_CALCULO_LIQUIDACION;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.File.TRABAJADORES_TRAMOS;
import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.DBLCLICK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.payroll.client.MainCreta.JsFileComparator;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBases;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEmployee;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEvent;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.builder.shared.InputBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DefaultCellTableBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public abstract class CretaDetail extends Composite {
	
	private static final Images IMAGES = GWT.create(Images.class);
	

	private static CretaDetailUiBinder uiBinder = GWT
			.create(CretaDetailUiBinder.class);

	interface CretaDetailUiBinder extends UiBinder<Widget, CretaDetail> {
	}
	
	private class ExpandCollapseCell extends AbstractCell<JsFile> {

		private ImageResourceRenderer renderer;
	  
		public ExpandCollapseCell(String... consumedEvents) {
			super(consumedEvents);
			renderer = new ImageResourceRenderer();
		}

		@Override
		public void render(Context context, JsFile jsFile, SafeHtmlBuilder sb) {
			if ( jsFile.getEmployees() == null || jsFile.getEmployees().length == 0  )
				sb.append(renderer.render(IMAGES.blank()));
			else 
				sb.append(renderer.render(showingEmployees.contains(jsFile.getId()) ? IMAGES.collapse() : IMAGES.expand()));
		}
	  
		@Override
		public void onBrowserEvent(Context context, Element parent, JsFile jsFile, NativeEvent event,
				ValueUpdater<JsFile> valueUpdater) {
			valueUpdater.update(jsFile);
		}
	  
	}
	
	static interface JsEmployeeTemplate extends SafeHtmlTemplates {

		@Template("<div class=\"aon-nowrap\" ><span class=\"{0}\" style=\"padding-left: 16px;\"></span><span class=\"aon-bold\" style=\"padding-left: 8px;\">{3} {1}</span><span> ({2})</span></div>")
		SafeHtml trabajador(String iconStyle, String naf, String ipf, String caf);
	}
	
	private static final JsEmployeeTemplate JSEMPLOYEE_TEMPLATE = GWT
			.create(JsEmployeeTemplate.class);
	

	@UiField
	FormPanel formPanel;
	@UiField
	FileUpload fileUpload;

	@UiField
	Button msjRecButton;
	@UiField
	MenuItem msjRecMenuItem;

	@UiField
	Button dclButton;
	@UiField
	MenuItem dclMenuItem;

	@UiField
	Button basesButton;
	@UiField
	MenuItem basesMenuItem;

	@UiField
	Button trabajadoresYTramosButton;
	@UiField
	MenuItem trabajadoresYTramosMenuItem;

	@UiField
	Button borradorButton;
	@UiField
	MenuItem borradorMenuItem;

	@UiField 
	Button confirmacionButton;
	@UiField
	MenuItem confirmacionMenuItem;

	@UiField(provided = true)
	DataGrid<JsFile> dataGrid;

	private PopupPanel popupTooltip;
	private Timer jsFileToolTipTimer;
	private HashSet<String> showingEmployees; 
	private MultiSelectionModel<JsFile> jsFileSelectionModel;
	private Map<String, CretaService.JsBases> basesMap;
	private Map<String, CretaService.JsRespuesta> respuestasMap;
	private Map<String, CretaService.JsTrabajadoresYTramos> trabajadoresYTramosMap;
	private Map<String, MultiSelectionModel<String>> trabajadoresSelectionModel;
	

	public CretaDetail() {
		ProvidesKey<JsFile> keyProvider = new HasIdKeyProvider<JsFile>();
		dataGrid = new CustomDataGrid<JsFile>(keyProvider) {
			@Override
			protected void onBrowserEvent2(Event event) {
				// TODO Auto-generated method stub
				super.onBrowserEvent2(event);
			}
		};

		initWidget(uiBinder.createAndBindUi(this));
		
		showingEmployees = new HashSet<String>();

		jsFileSelectionModel = new MultiSelectionModel<JsFile>(keyProvider);
		trabajadoresSelectionModel = new HashMap<String,MultiSelectionModel<String>>();

		dataGrid.setSelectionModel(jsFileSelectionModel,
				DefaultSelectionEventManager.<JsFile> createCheckboxManager(0));
		

		// init MSJREC Command
		ScheduledCommand msjRecCommand = new ScheduledCommand() {
			@Override
			public void execute() {
				fileUpload.click();
			}
		};
		msjRecMenuItem.setScheduledCommand(msjRecCommand);

		dataGrid.addColumn(new Column<JsFile, Boolean>(new CheckboxCell() {
			private Set<String> extendedConsumedEvents;
			@Override
			public Set<String> getConsumedEvents() {
				if ( extendedConsumedEvents == null ) { 
					extendedConsumedEvents = new HashSet<String>(super.getConsumedEvents());
					extendedConsumedEvents.add(BrowserEvents.CLICK);
					extendedConsumedEvents.add(BrowserEvents.KEYUP);
				}
				return Collections.unmodifiableSet(extendedConsumedEvents);
			}
			
			@Override
			public void onBrowserEvent(Context context, Element parent, Boolean value, NativeEvent event,
					ValueUpdater<Boolean> valueUpdater) {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				if ( event.getType().equals(BrowserEvents.CLICK) 
					|| event.getType().equals(BrowserEvents.KEYUP) ) 
					setSelectedAllEmployee(context.getKey().toString(), value);
				else if ( event.getType().equals(BrowserEvents.KEYDOWN) 
					&&  event.getKeyCode() == KeyCodes.KEY_ENTER  ) 
					setSelectedAllEmployee(context.getKey().toString(), !value);
				
			}
			
		}) {
			@Override
			public Boolean getValue(JsFile jsFile) {
				return CretaDetail.this.jsFileSelectionModel.isSelected(jsFile);
			}
		}, new SelectAllHeader<JsFile>(jsFileSelectionModel, dataGrid));

		dataGrid.setColumnWidth(0, "40px");
		
		// --------------------------------------------------------------------
		// 
		Column<JsFile, JsFile> showEmployeesColumn = 
		new Column<JsFile, JsFile>( new ExpandCollapseCell(CLICK, DBLCLICK)  )
		{
			@Override
			public JsFile getValue(JsFile jsFile) {
				return jsFile;
			}
			
		};
		showEmployeesColumn.setFieldUpdater(new FieldUpdater<JsFile, JsFile>() {
			@Override
			public void update(int index, JsFile jsFile, JsFile value) {
				String id = jsFile.getId();
				if ( showingEmployees.contains(id)) {
					showingEmployees.remove(id);
				}else {
					showingEmployees.add(id);
				}
				dataGrid.redrawRow(index);
				
			}
		});
		
		dataGrid.setTableBuilder(new DefaultCellTableBuilder<JsFile>(dataGrid) {

		    @Override
			public void buildRowImpl(JsFile jsFile, int absRowIndex) {
				super.buildRowImpl(jsFile, absRowIndex);
				
				if ( showingEmployees.contains(jsFile.getId())) {
					// Cache styles for faster access.
					boolean isEven = absRowIndex % 2 == 0;

					Style style = dataGrid.getResources().style();
					String trStyle = (isEven ? style.evenRow() : style.oddRow());
					String tdStyle = style.cell() + " " + (isEven ? style.evenRowCell() : style.oddRowCell());

					for ( JsEmployee employee: jsFile.getEmployees() ) {
						
						TableRowBuilder tr = startRow();
						tr.className(trStyle);
						
						TableCellBuilder td = tr.startTD();
						td.className(tdStyle);
						td.endTD();
						
						td = tr.startTD();
						
						td.className(tdStyle);
						InputBuilder checkBox = td.startCheckboxInput();
						checkBox.attribute("onclick", "javascript:onEmployeeChange('"+jsFile.getId()+"','"+employee.getNaf()+"',this.checked);");
						if ( isSelectedEmployee(jsFile.getId(), employee.getNaf()) )
							checkBox.checked();
						checkBox.endInput();
						td.endTD();
						
						String iconStyle = AON.AON_ICON_EMPLOYEE;
						JsRespuesta jsRespuesta = respuestasMap.get(jsFile.getId());
						if ( jsRespuesta != null && jsRespuesta.getEmployees() != null )
							for ( JsEmployee e:  jsRespuesta.getEmployees() )
								if ( employee.getNaf().equals(e.getNaf() ))
									iconStyle = AON.AON_ICON_EXCEPTION;
						
						td = tr.startTD();
						td.className(tdStyle);
						td.html(JSEMPLOYEE_TEMPLATE.trabajador(iconStyle, employee.getNaf(), employee.getIpf(), CretaDetail.this.getEmployeeFullName(employee)));
						td.endTD();

						tr.endTR();
					}
				}
			}
			
		});
		dataGrid.addColumn(showEmployeesColumn);
		dataGrid.setColumnWidth(1, "40px");
		// --------------------------------------------------------------------

		dataGrid.addColumn(new JsFileColumn() {

			@Override
			void onJsFileOut(JsFile jsFile, NativeEvent event) {
				CretaDetail.this.onJsFileOut();
			}

			@Override
			void onJsFileOver(JsFile jsFile, NativeEvent event) {
				if (CretaDetail.this.jsFileToolTipTimer != null)
					CretaDetail.this.jsFileToolTipTimer.cancel();
				
				CretaDetail.this.onJsFileOver(jsFile.getId(),
						event.getClientX(), event.getClientY());
			}

			@Override
			void onJsFileClick(JsFile jsFile, NativeEvent event) {
				if (CretaDetail.this.jsFileToolTipTimer != null)
					CretaDetail.this.jsFileToolTipTimer.cancel();
				CretaDetail.this.onJsFileClick(jsFile.getId(),
						event.getClientX(), event.getClientY());

			}

			@Override
			void onJsFileDblClick(JsFile jsFile, NativeEvent event) {
				if (CretaDetail.this.jsFileToolTipTimer != null)
					CretaDetail.this.jsFileToolTipTimer.cancel();
				if (CretaDetail.this.popupTooltip != null)
					CretaDetail.this.popupTooltip.hide();
				
				List<JsFile> jsFiles = new ArrayList<JsFile>(3);
				jsFiles.add(jsFile);
				JsRespuesta jsRespuesta = CretaDetail.this.respuestasMap.get(jsFile.getId());
				if ( jsRespuesta != null ) {
					
					if ( !isSolicitudTrabajdoresYTramosRespuesta(jsRespuesta, jsFile))
						jsFiles.add(jsRespuesta);
					
					for ( JsFile old: MainCreta.getOld(File.RESPUESTA, jsRespuesta) )
						if ( !isSolicitudTrabajdoresYTramosRespuesta(old, jsFile))
							if ( !exist(old, jsFiles))
								jsFiles.add(old);
					
					JsBases jsBases = CretaDetail.this.basesMap.get(jsFile.getId());
					if ( jsBases != null ) {
						if ( replied(jsBases, jsFiles))
							jsFiles.add(jsBases);

						for ( JsFile old: MainCreta.getOld(File.BASES, jsBases) )
							if ( replied(jsBases, jsFiles)) 
								jsFiles.add(old);

					}
				}

				CretaDetail.this.onJsFileDblClick(event.getClientX(),
						event.getClientY(), jsFiles.toArray(new JsFile[jsFiles.size()]));

			}
			
			boolean exist(JsFile jsRespuesta, Collection<JsFile> jsRespuestas) {
				for ( JsFile jsFile: jsRespuestas ) {
					if ( CretaService.File.RESPUESTA.name().equals(jsFile.getName()) 
						&& jsRespuesta.getExternalReference().equals(jsFile.getExternalReference()) ) {
						return true;
					}
				}
				
				return false;
			}

			boolean replied(JsFile jsBases, Collection<JsFile> jsRespuestas) {
				for ( JsFile jsRespuesta: jsRespuestas ) 
					if ( jsBases.getExternalReference().equals(jsRespuesta.getExternalReference()) )
						return true;
				
				return false;
			}

			boolean isSolicitudTrabajdoresYTramosRespuesta(JsFile jsRespuesta, JsFile trabajadoresYTramos) {
				if ( !CretaService.File.TRABAJADORES_TRAMOS.name().equals(trabajadoresYTramos.getName()) )
					return false;
				if ( trabajadoresYTramos.getExternalReference() == null )
					return false;
				return trabajadoresYTramos.getExternalReference().equals(jsRespuesta.getExternalReference());
			}

			@Override
			String getIconStyle(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
				return CretaDetail.this.getIconStyle(jsTrabajadoresYTramos);
			}

			@Override
			String getDescription(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
				return jsTrabajadoresYTramos.getType() + " " + CretaDetail.this
						.getDescription(jsTrabajadoresYTramos.getCCC());
			}

		});

		jsFileSelectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				boolean selected = CretaDetail.this.jsFileSelectionModel
						.getSelectedSet().size() > 0;
				CretaDetail.this.basesButton.setEnabled(selected);

			}
		});

		dataGrid.setRowData(new ArrayList<CretaService.JsFile>(0));

		basesMap = new HashMap<String, CretaService.JsBases>();
		respuestasMap = new HashMap<String, CretaService.JsRespuesta>();
		trabajadoresYTramosMap = new HashMap<String, CretaService.JsTrabajadoresYTramos>();

		fileUpload.getElement().setPropertyString("multiple", "multiple");

	}

	Set<JsFile> getSelected() {
		return jsFileSelectionModel.getSelectedSet();
	}
	
	void setSelected( JsFile jsFile) {
		jsFileSelectionModel.clear();
		jsFileSelectionModel.setSelected(jsFile, true);
	}

	Collection<String> getSelectedNafs() {
		return getNafs();
	}
	// ------------------------------------------------------------- UIHandlers

	@UiHandler("fileUpload")
	void onFileUploadChange(ChangeEvent event) {
		formPanel.submit();
	}

	@UiHandler("msjRecButton")
	void onClickMsjRecButton(ClickEvent e) {
		fileUpload.setName(TRABAJADORES_TRAMOS.name());
		formPanel.setAction(CRETA_URL + '/' +TRABAJADORES_TRAMOS.name());
		fileUpload.click();
	}

	@UiHandler("dclButton")
	void onClickLiquidacionButton(ClickEvent e) {
		fileUpload.setName(DOCUMENTO_CALCULO_LIQUIDACION.name());
		formPanel.setAction(CRETA_URL + '/' +DOCUMENTO_CALCULO_LIQUIDACION.name());
		fileUpload.click();
	}

	@UiHandler("dbaButton")
	void onClickDBAButton(ClickEvent e) {
	}

	@UiHandler("basesButton")
	void onClickBasesButton(ClickEvent e) {
		submitBases();
	}

	@UiHandler("borradorButton")
	void onClickBorradorButton(ClickEvent e) {
	}

	@UiHandler("confirmacionButton")
	void onClickConfirmacionButton(ClickEvent e) {
	}

	@UiHandler("trabajadoresYTramosButton")
	void onClickTrabajadoresYTramosButton(ClickEvent e) {
	}

	// ------------------------------------------------------------------------
	
	String getEmployeeFullName(JsEmployee jsEmployee) {
		return jsEmployee.getCaf();
	}
	
	// ------------------------------------------------------------------------

	public void onTrabajadoresYTramos() {
		onTrabajadoresYTramos(new JsTrabajadoresYTramos[0], new JsRespuesta[0], new JsBases[0]);
	}

	public void onTrabajadoresYTramos(
			CretaService.JsTrabajadoresYTramos trabajadoresYTramos[],
			CretaService.JsRespuesta respuestas[],
			CretaService.JsBases bases []) {

		try {

			trabajadoresYTramosMap = MainCreta
					.add(File.TRABAJADORES_TRAMOS, trabajadoresYTramos);

			List<JsFile> filtered = new ArrayList<JsFile>();
			
			filtered.addAll(filter(trabajadoresYTramosMap.values()));

			respuestasMap = MainCreta.add(File.RESPUESTA, respuestas);

			basesMap = MainCreta.add(File.BASES, bases);

			for (JsRespuesta jsRespuesta : respuestasMap.values()) {
				if (!contains(filtered, jsRespuesta)
						&& hasTrabajadoresYTramos(jsRespuesta))
					filtered.addAll(filter(Collections.singleton(jsRespuesta)));
			}
			
			Collections.sort(filtered, JsFileComparator.newInstace());
			
			dataGrid.setRowData(filtered);

		} catch (UnsupportedOperationException e) {

			respuestasMap = new HashMap<String, JsRespuesta>();
			for (CretaService.JsRespuesta respuesta : respuestas)
				respuestasMap.put(respuesta.getCCC() + respuesta.getFrom(),
						respuesta);

			List<JsTrabajadoresYTramos> filtered = filter(
					Arrays.asList(trabajadoresYTramos));
			dataGrid.setRowData(filtered);
		}

	}
	
	public void onEmployeeChange(String trabajadoresYTramosId, String naf, boolean checked ) {
		setSelectedEmployee(trabajadoresYTramosId, naf, checked);
	}
	
	public void onDocumentoCalculoLiquidacion(
			CretaService.JsDCLResult success[],
			CretaService.JsDCLResult errors[]) {
		
		onDCLResults(success, errors);

	}
	// ------------------------------------------------------------------------

	@Override
	protected void onAttach() {
		exportSubmitComplete();
		exportOnEmployeeChange();
		super.onAttach();
	}

	protected abstract void onBases(JsBasesResult result);

	protected abstract String getDescription(String ccc);

	protected abstract <T extends JsFile> List<T> filter(Collection<T> jsFiles);

	protected abstract void onDCLResults(JsEvent success [], JsEvent errors []);

	protected void onJsFileClick(final String key, final int x, final int y) {
		// NOOP
	}

	protected void onJsFileDblClick(final int x, final int y, JsFile ...jsFile ) {
		// NOOP
	}

	// ------------------------------------------------------------------------

	private void onJsFileOut() {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();
	}

	private void onJsFileOver(final String key, final int x, final int y) {

		jsFileToolTipTimer = new Timer() {

			@Override
			public void run() {

				if (CretaDetail.this.popupTooltip != null)
					CretaDetail.this.popupTooltip.hide();

				CretaDetail.this.popupTooltip = MainCreta
						.showjsRespuestaToolTip(respuestasMap.get(key), x, y);
				
			}
		};

		jsFileToolTipTimer.schedule(1000);
	}

	private String getIconStyle(JsTrabajadoresYTramos t) {
		return MainCreta.getIconStyle(respuestasMap.get(t.getId()));
	}

	private void submitBases() {
		Map<String, Collection<String>> datas = new HashMap<String, Collection<String>>();
		try {
			datas.put(CretaService.Parameter.NAFS.name(), getNafs());
		}catch ( Exception e ) {
		}
		
		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.BASES,
				datas,
				jsFileSelectionModel.getSelectedSet(),
				new AsyncCallback<CretaService.JsBasesResult>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsBasesResult result) {
						onBases(result);
					}
				});
	}
	
	private Collection<String> getNafs() {
		List<String> nafs = new LinkedList<String>();
		jsFileSelectionModel.getSelectedSet()
		.stream()
		.map( jsFile -> jsFile.getId() )
		.map(id -> trabajadoresSelectionModel.get(id))
		.forEach( s -> nafs.addAll(s.getSelectedSet()));
		return nafs;
	}
	
	private Map<String,Collection<String>> getDefaults(){
		return Collections.emptyMap();
	}
	
	private boolean isSelectedEmployee(String id, String naf) {
		MultiSelectionModel<String> selectionModel = trabajadoresSelectionModel.get(id);
		return selectionModel != null && selectionModel.isSelected(naf);
	}

	private void setSelectedAllEmployee(String id, boolean selected) {
		JsFile jsFile = trabajadoresYTramosMap.get(id);
		if ( jsFile == null )
			jsFile = respuestasMap.get(id);
		setSelectedAllEmployee(jsFile, selected);
				
	}

	private void setSelectedAllEmployee(JsFile jsFile, boolean selected) {
		MultiSelectionModel<String> selectionModel = trabajadoresSelectionModel.get(jsFile.getId());
		if ( selectionModel == null )
			trabajadoresSelectionModel.put(jsFile.getId(), selectionModel = new MultiSelectionModel<String>() );
		for ( JsEmployee jsEmployee: jsFile.getEmployees() )
			selectionModel.setSelected(jsEmployee.getNaf(), selected);
	}

	private void setSelectedEmployee(String id, String naf, boolean selected) {
		MultiSelectionModel<String> selectionModel = trabajadoresSelectionModel.get(id);
		if ( selectionModel == null )
			trabajadoresSelectionModel.put(id, selectionModel = new MultiSelectionModel<String>());
		selectionModel.setSelected(naf, selected);
		
		jsFileSelectionModel.setSelected(trabajadoresYTramosMap.get(id), selectionModel.getSelectedSet().size() > 0);
	}
	
	
	// ------------------------------------------------------------------------

	private native void exportSubmitComplete() /*-{
		var that = this;
		$wnd.__onTrabajadoresYTramos = $entry(function(trabajadoresYTramos,
				respuestas,
				bases) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onTrabajadoresYTramos([Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsTrabajadoresYTramos;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsRespuesta;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsBases;)(trabajadoresYTramos, respuestas, bases);
		});
		$wnd.__onDocumentoCalculoLiquidacion = $entry(function(success,
				errors) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onDocumentoCalculoLiquidacion([Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsDCLResult;[Lcom/esferalia/aon/gwt/payroll/shared/CretaService$JsDCLResult;)(success, errors);
		});
	}-*/;

	private native void exportOnEmployeeChange() /*-{
		var that = this;
		$wnd.onEmployeeChange = $entry(function(trabajadoresYTramosId, naf,checked) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onEmployeeChange(Ljava/lang/String;Ljava/lang/String;Z)(trabajadoresYTramosId, naf, checked);
		});
	}-*/;

	// ------------------------------------------------------------------------

	private static boolean contains(List<JsFile> jsFiles, String id) {
		for (JsFile jsF : jsFiles)
			if (jsF.getId().equals(id))
				return true;
		return false;
	}

	private static boolean contains(List<JsFile> jsFiles, JsFile jsFile) {
		return contains(jsFiles, jsFile.getId());
	}

}
