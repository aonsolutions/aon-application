package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.MainCreta.isAON;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.CRETA_URL;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.File.DOCUMENTO_CALCULO_LIQUIDACION;
import static com.esferalia.aon.gwt.payroll.shared.CretaService.File.TRABAJADORES_TRAMOS;
import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.DBLCLICK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.payroll.client.MainCreta.JsFileComparator;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBases;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEmployee;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsError;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEvent;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonWordUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public abstract class CretaDetail extends Composite {
	
	private static final Images IMAGES = GWT.create(Images.class);
	
	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";
	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat.getFormat("MMMM 'de' yyyy");
	

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
	
	@UiField
	Button agrarianButton;

	@UiField(provided = true)
	DataGrid<JsFile> dataGrid;
	
	@UiField(provided = true)
	MenuBar viewMenuBar;
	
	@UiField
	MenuItem l00MenuItem;
	@UiField 
	MenuItem l13MenuItem;

	@UiField
	MenuItem nextMonthMenuItem;
	@UiField 
	MenuItem prevMonthMenuItem;

	@UiField 
	MenuItem viewMenuItem;
	@UiField 
	MenuItem pendingMenuItem;
	@UiField 
	MenuItem processingMenuItem;
	@UiField 
	MenuItem errorMenuItem;
	@UiField 
	MenuItem calculatedMenuItem;
	@UiField 
	MenuItem confirmedMenuItem;

	@UiField(provided = true)
	MenuBar moreViewMenuBar;

	@UiField 
	MenuItem r9546MenuItem;
	@UiField 
	MenuItem r9607MenuItem;
	@UiField 
	MenuItem r9650MenuItem;
	@UiField 
	MenuItem r9544MenuItem;


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
		
		class ViewMenuBar extends MenuBar {
			  
			public ViewMenuBar(boolean vertical) {
				super(vertical);
			}

			private MenuItem findItem(Element hItem) {
			    for (MenuItem item : getItems()) {
			      if (item.getElement().isOrHasChild(hItem)) {
			        return item;
			      }
			    }
			    return null;
			  }
			  
			  @Override
			  public void onBrowserEvent(Event event) {
				
				MenuItem item = findItem(DOM.eventGetTarget(event));
			    switch (DOM.eventGetType(event)) {
			      case Event.ONCLICK: {
			    	  if (item != null) {
			    	      // Fire the item's command. The command must be fired in the same event
			    		  // loop or popup blockers will prevent popups from opening.
			    	      final ScheduledCommand cmd = item.getScheduledCommand();
			    	      Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
			    	        @Override
			    	        public void execute() {
			    	          cmd.execute();
			    	        }
			    	      });
			    	      event.stopPropagation();
			    	      event.preventDefault();
			    	  }
			    	  return;
			    	 
			      }
			    }
			    super.onBrowserEvent(event);
			}		
		};
		
		viewMenuBar = new ViewMenuBar(true);
		moreViewMenuBar = new ViewMenuBar(true);

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
						tr.attribute("onmouseout", "javascript:onEmployeeOut();");
						tr.attribute("onmouseover", "javascript:onEmployeeOver('"+jsFile.getId()+"','"+employee.getNaf()+"',event.x,event.y);");
						
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
				CretaDetail.this.onJsFileOut(
						event.getClientX(), event.getClientY());
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
				if ( jsRespuesta != null) {
					
					if ( !isSolicitudTrabajdoresYTramosRespuesta(jsRespuesta, jsFile))
						if ( !exist(jsRespuesta, jsFiles)  && !isAON(jsRespuesta) )
							jsFiles.add(jsRespuesta);
					
					for ( JsFile old: MainCreta.getOld(File.RESPUESTA, jsRespuesta) )
						if ( !isSolicitudTrabajdoresYTramosRespuesta(old, jsFile))
							if ( !exist(old, jsFiles) && !isAON(old))
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
		
		
		
		// View Menu
		viewMenuItem.addStyleName("aon-float-right");
		MenuItem viewMenuItems [] = {
				l00MenuItem,
				l13MenuItem,
				nextMonthMenuItem,
				prevMonthMenuItem,
				pendingMenuItem,
				processingMenuItem,
				errorMenuItem,
				calculatedMenuItem,
				confirmedMenuItem,
				r9544MenuItem,
				r9546MenuItem,
				r9607MenuItem,
				r9650MenuItem
		};
		for ( MenuItem menuItem: viewMenuItems ) {
			setCheckedStyle(menuItem, true);
			menuItem.setScheduledCommand( () -> {
				setCheckedStyle(menuItem, !isChecked(menuItem));
				onTrabajadoresYTramos();
			} );
		}
		
		nextMonthMenuItem.setText(AonWordUtils.capitalize(MONTH_FORMAT.format(getNextMonth())));
		prevMonthMenuItem.setText(AonWordUtils.capitalize(MONTH_FORMAT.format(getPrevMonth())));
		
		setCheckedStyle(r9546MenuItem, false );
		setCheckedStyle(r9650MenuItem, false );
		setCheckedStyle(r9607MenuItem, false );
		setCheckedStyle(prevMonthMenuItem, new Date().getDate() < 5 );
		

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
			respuestasMap = MainCreta.add(File.RESPUESTA, respuestas);

			Map<String, JsFile> filteredMap = new HashMap<String, JsFile>();
			
			Collection<CretaService.JsTrabajadoresYTramos> visibleTrabajadoresYTramos = 
					filterVisible(trabajadoresYTramosMap.values());
			
			
			
			filter(visibleTrabajadoresYTramos).forEach(f -> filteredMap.putIfAbsent(f.getId(), f));
//			filtered.addAll(filter(visibleTrabajadoresYTramos));

			
			Collection<CretaService.JsRespuesta> visibleRespuestas = 
					filterVisible(respuestasMap.values());

			basesMap = MainCreta.add(File.BASES, bases);

			for (JsRespuesta jsRespuesta : visibleRespuestas ) {
				if (
//						!contains(filtered, jsRespuesta)
						!filteredMap.containsKey(jsRespuesta.getId())
//						&& ( hasTrabajadoresYTramos(jsRespuesta) || isAON(jsRespuesta))
					)
					filter(Collections.singleton(jsRespuesta)).forEach(f -> filteredMap.putIfAbsent(f.getId(), f));
//					filtered.addAll(filter(Collections.singleton(jsRespuesta)));
			}
			
			List<JsFile> filtered = new ArrayList<JsFile>(filteredMap.values());
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
	
	public void onEmployeeOut() {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();		
	}

	public void onEmployeeOver(String trabajadoresYTramosId, String naf, int x , int y ) {
		jsFileToolTipTimer = new Timer() {
			
			
			@Override
			public void run() {

				if (CretaDetail.this.popupTooltip != null)
					CretaDetail.this.popupTooltip.hide();
				
				Arrays.stream(trabajadoresYTramosMap.get(trabajadoresYTramosId).getEmployees())
				.filter(e -> e.getNaf().equalsIgnoreCase(naf)).findAny()
				.ifPresent(e -> CretaDetail.this.popupTooltip = MainCreta.showjsEmployeeToolTip(e, x, y));								
				
			}
		};

		jsFileToolTipTimer.schedule(1000);
		
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
		exportOnEmployeeOut();
		exportOnEmployeeOver();
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

	private void onJsFileOut(final int clientX, final int clientY) {
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
		return MainCreta.getIconStyle(t, respuestasMap.get(t.getId()));
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
	
	private <T extends CretaService.JsFile> Collection<T> filterVisible(Collection<T> jsFiles) {
		return
		jsFiles.stream()
		.filter(jsFile -> {
			
			try {
				
				Date from = getDate(jsFile.getFrom());
				if ( from.before(getPrevMonth())) 
					return false;
			
				String type = jsFile.getType();
				if ( AonStringUtils.equalsIgnoreCase("L00", type) && !isChecked(l00MenuItem))
					return false;
				if ( AonStringUtils.equalsIgnoreCase("L13", type) && !isChecked(l13MenuItem))
					return false;
				
				
				int month = Integer.parseInt(jsFile.getFrom().split("-")[1]);
				
				int nextMonth = getNextMonth().getMonth() + 1;
				if ( month == nextMonth && !isChecked(nextMonthMenuItem))
					return false;
				
				int prevMonth = getPrevMonth().getMonth() + 1;
				if ( month == prevMonth && !isChecked(prevMonthMenuItem))
					return false;
				
				JsRespuesta jsRespuesta = respuestasMap.get(jsFile.getId());
//				if ( jsRespuesta == null && !isChecked(pendingMenuItem))
//					return false;
				if ( jsRespuesta == null && !isChecked(processingMenuItem))
					return false;
				
				JsError jsErros[] = jsRespuesta.getErrors();
				if ( jsErros == null && !isChecked(processingMenuItem) )
					return false;
				
				boolean error = false;
				boolean pending = false;
				boolean processing = false;
				boolean confirmed = false;
				boolean calculated = false;
				
				boolean r9544 = false;
				boolean r9546 = false;
				boolean r9607 = false;
				boolean r9650 = false;
					
				for (JsError jsError : jsErros) {
					// Confirmed
					if ( AonStringUtils.equalsIgnoreCase("A9761", jsError.getCode())) {
						confirmed = true ;
					// Calculated
					}
					
					else if ( AonStringUtils.equalsIgnoreCase("R9544", jsError.getCode())) {
						r9544 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9546", jsError.getCode())) {
						r9546 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9607", jsError.getCode())) {
						r9607 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9650", jsError.getCode())) {
						r9650 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9529", jsError.getCode())) {
						calculated = true ;
					}else if ( MainCreta.isTrabajadoressYTramos(jsFile) 
							&& AonStringUtils.equalsIgnoreCase("A9999", jsError.getCode())) {
						processing = true ;
					}else if (/* MainCreta.isTrabajadoressYTramos(jsFile) 
							&& */AonStringUtils.equalsIgnoreCase("R9998", jsError.getCode())) {
						processing = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("A9999", jsError.getCode())) {
						pending = true ;
					}
					else {
						error = true ;
					}
				}
				
				if ( !error && !confirmed && calculated && !isChecked(calculatedMenuItem))
					return false;
				if ( !error && confirmed && !isChecked(confirmedMenuItem))
					return false;
				if ( !error && processing && !isChecked(processingMenuItem))
					return false;
				if ( !error && pending && !isChecked(pendingMenuItem))
					return false;
				
				if ( !error && r9544 && !isChecked(r9544MenuItem))
					return false;
				if ( !error && r9546 && !isChecked(r9546MenuItem))
					return false;
				if ( !error && r9607 && !isChecked(r9607MenuItem))
					return false;				
				if ( !error && r9650 && !isChecked(r9650MenuItem))
					return false;				
				
				if ( error && !isChecked(errorMenuItem))
					return false;
				
				
			} catch ( Throwable e ) {
				
			}
			
			return true;
		})
		.collect(Collectors.toList());
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

	private native void exportOnEmployeeOut() /*-{
		var that = this;
		$wnd.onEmployeeOut = $entry(function() {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onEmployeeOut()();
		});
	}-*/;

	private native void exportOnEmployeeOver() /*-{
		var that = this;
		$wnd.onEmployeeOver = $entry(function(trabajadoresYTramosId, naf, x, y) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onEmployeeOver(Ljava/lang/String;Ljava/lang/String;II)(trabajadoresYTramosId, naf, x, y);
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
	
	private static boolean isChecked(MenuItem menuItem) {
		return AonStringUtils.containsIgnoreCase(menuItem.getStyleName(), STYLENAME_CHECKED_ITEM);
	}

	private static void setCheckedStyle(MenuItem menuItem, boolean checked) {
		if (checked) {
			menuItem.addStyleName(STYLENAME_CHECKED_ITEM);
		} else {
			menuItem.removeStyleName(STYLENAME_CHECKED_ITEM);
		}
	}
	
	private static Date getNextMonth() {
		Date nextMonth = new Date();
		CalendarUtil.setToFirstDayOfMonth(nextMonth);
		CalendarUtil.addMonthsToDate(nextMonth, -1);
		return nextMonth;
	}
	private static Date getPrevMonth() {
		Date prevMonth = new Date();
		CalendarUtil.setToFirstDayOfMonth(prevMonth);
		CalendarUtil.addMonthsToDate(prevMonth, -2);
		return prevMonth;
	}
	
	private static Date getDate ( String text ) {
		return DateTimeFormat.getFormat("yyyy-MM").parse(text);
	}
	
	public static native void log (String message ) /*-{
		console.log(message);
	}-*/
	;

}
