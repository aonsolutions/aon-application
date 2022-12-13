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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.client.MainCreta.JsFileComparator;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBases;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEmployee;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsError;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEvent;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsProgress;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonWordUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
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
import com.google.gwt.resources.client.CssResource;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
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
	
	// ----------------------------------------------- Static Variables 
	
	private static final Images IMAGES = GWT.create(Images.class);
	
	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";
	
	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat.getFormat("MMMM 'de' yyyy");
	
	private static final JsEmployeeTemplate JSEMPLOYEE_TEMPLATE = GWT.create(JsEmployeeTemplate.class);
	
	// ----------------------------------------------- UiBinder
	
	private static CretaDetailUiBinder uiBinder = GWT.create(CretaDetailUiBinder.class);

	interface CretaDetailUiBinder extends UiBinder<Widget, CretaDetail> {}
	
	// ----------------------------------------------- ViewMenuBar
	
	public static class ViewMenuBar extends MenuBar {
		  
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
	
	// ----------------------------------------------- Expand Collapse Cell

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
	
	// ----------------------------------------------- JsEmployeeTemplate

	static interface JsEmployeeTemplate extends SafeHtmlTemplates {
		@Template("<div class=\"aon-nowrap\" ><span class=\"{0}\" style=\"padding-left: 16px;\"></span><span class=\"aon-bold\" style=\"padding-left: 8px;\">{3} {1}</span><span> ({2})</span></div>")
		SafeHtml trabajador(String iconStyle, String naf, String ipf, String caf);
	}
	
	// ----------------------------------------------- ScheduledCommand (SDL-Cret@)
	
	class MsjRecCommand implements ScheduledCommand {

		@Override
		public void execute() {
			fileUpload.click();
		}
	}
	
	class DclCommand implements ScheduledCommand {

		@Override
		public void execute() {}
	}
	
	class BasesCommand implements ScheduledCommand {

		@Override
		public void execute() {}
	}
	
	class TrabajadoresYTramosCommand implements ScheduledCommand {

		@Override
		public void execute() {}
	}
	
	class BorradorCommand implements ScheduledCommand {

		@Override
		public void execute() {}
	}
	
	class ConfirmacionCommand implements ScheduledCommand {

		@Override
		public void execute() {}
	}
	
	class SLDCretaMenu extends ContextMenu {
				
		private MenuItem msjRecMenuItem = null;
		private MenuItem dclMenuItem = null;
		private MenuItem basesMenuItem = null;
		private MenuItem trabajadoresYTramosMenuItem = null;
		private MenuItem borradorMenuItem = null;
		private MenuItem confirmacionMenuItem = null;
		
		public SLDCretaMenu() {
			
			msjRecMenuItem = addItem("Mensajes Recibidos", new MsjRecCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			msjRecMenuItem.ensureDebugId("msjRecMenuItem");
			
			dclMenuItem = addItem(CretaService.File.DOCUMENTO_CALCULO_LIQUIDACION.getFilename(), new DclCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			dclMenuItem.ensureDebugId("dclMenuItem");
			
			basesMenuItem = addItem(CretaService.File.BASES.getFilename(), new BasesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			basesMenuItem.ensureDebugId("basesMenuItem");
			basesMenuItem.setEnabled(false);
			
			basesMenuItem = addItem(CretaService.File.BASES.getFilename(), new BasesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			basesMenuItem.ensureDebugId("basesMenuItem");
			basesMenuItem.setEnabled(false);
			
			trabajadoresYTramosMenuItem = addItem(CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS.getFilename(), new TrabajadoresYTramosCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			trabajadoresYTramosMenuItem.ensureDebugId("trabajadoresYTramosMenuItem");
			trabajadoresYTramosMenuItem.setEnabled(false);
			
			borradorMenuItem = addItem(CretaService.File.SOLICITUD_BORRADOR.getFilename(), new BorradorCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			borradorMenuItem.ensureDebugId("borradorMenuItem");
			borradorMenuItem.setEnabled(false);
			
			confirmacionMenuItem = addItem(CretaService.File.SOLICITUD_CONFIRMACION.getFilename(), new ConfirmacionCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			confirmacionMenuItem.ensureDebugId("confirmacionMenuItem");
			confirmacionMenuItem.setEnabled(false);
		
		}
	}
	
	// ----------------------------------------------- ScheduledCommand (Filter)
	
	class EmptyCommand implements ScheduledCommand {
		@Override
		public void execute() {}
	}
	
	class FilterMenu extends ContextMenu {
				
		private MenuItem l00MenuItem = null;
		private MenuItem l13MenuItem = null;
		private MenuItem nextMonthMenuItem = null;
		private MenuItem prevMonthMenuItem = null;
		private MenuItem pendingMenuItem = null;
		private MenuItem processingMenuItem = null;
		private MenuItem errorMenuItem = null;
		private MenuItem calculatedMenuItem = null;
		private MenuItem confirmedMenuItem = null;
		private MenuItem moreViewMenuItem = null;
		
		private MoreViewMenu moreViewMenu = new MoreViewMenu(true);
		
		public FilterMenu() {
			
			l00MenuItem = addItem("L00 Normal", new EmptyCommand(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			l00MenuItem.ensureDebugId("l00MenuItem");
			
			l13MenuItem = addItem("L13 Vacaciones", new EmptyCommand(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			l13MenuItem.ensureDebugId("l13MenuItem");
			
			addSeparator();
			
			nextMonthMenuItem = addItem("-", new EmptyCommand(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			nextMonthMenuItem.ensureDebugId("nextMonthMenuItem");
			
			prevMonthMenuItem = addItem("-", new EmptyCommand(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			prevMonthMenuItem.ensureDebugId("prevMonthMenuItem");
			
			addSeparator();
			
			pendingMenuItem = addItem("Pendiente", new EmptyCommand(), "aon-icon-warn", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			pendingMenuItem.ensureDebugId("pendingMenuItem");
			
			processingMenuItem = addItem("En Proceso", new EmptyCommand(), "aon-icon-errorwarning", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			processingMenuItem.ensureDebugId("processingMenuItem");
			
			errorMenuItem = addItem("Con Errores", new EmptyCommand(), "aon-icon-exception", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			errorMenuItem.ensureDebugId("errorMenuItem");
			
			calculatedMenuItem = addItem("Calculada", new EmptyCommand(), "aon-icon-okwarning", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			calculatedMenuItem.ensureDebugId("calculatedMenuItem");
			
			confirmedMenuItem = addItem("Confirmada", new EmptyCommand(), "aon-icon-predetermine", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			confirmedMenuItem.ensureDebugId("confirmedMenuItem");
			
			moreViewMenuItem = addItem("M&aacute;s...", moreViewMenu, AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			moreViewMenuItem.ensureDebugId("moreViewMenuItem");
		}

		public MenuItem getL00MenuItem() {
			return l00MenuItem;
		}

		public MenuItem getL13MenuItem() {
			return l13MenuItem;
		}

		public MenuItem getNextMonthMenuItem() {
			return nextMonthMenuItem;
		}

		public MenuItem getPrevMonthMenuItem() {
			return prevMonthMenuItem;
		}

		public MenuItem getPendingMenuItem() {
			return pendingMenuItem;
		}

		public MenuItem getProcessingMenuItem() {
			return processingMenuItem;
		}

		public MenuItem getErrorMenuItem() {
			return errorMenuItem;
		}

		public MenuItem getCalculatedMenuItem() {
			return calculatedMenuItem;
		}

		public MenuItem getConfirmedMenuItem() {
			return confirmedMenuItem;
		}

		public MenuItem getMoreViewMenuItem() {
			return moreViewMenuItem;
		}

		public MenuItem getR9546MenuItem() {
			return moreViewMenu.getR9546MenuItem();
		}

		public MenuItem getR9607MenuItem() {
			return moreViewMenu.getR9607MenuItem();
		}

		public MenuItem getR9650MenuItem() {
			return moreViewMenu.getR9650MenuItem();
		}

		public MenuItem getR9544MenuItem() {
			return moreViewMenu.getR9544MenuItem();
		}
		
	}
	
	class MoreViewMenu extends MenuBar {
		
		private MenuItem r9546MenuItem = null;
		private MenuItem r9607MenuItem = null;
		private MenuItem r9650MenuItem = null;
		private MenuItem r9544MenuItem = null;
		
		public MoreViewMenu(boolean isVertical) {
			
			super(isVertical);
			
			r9546MenuItem = addItem("",  new EmptyCommand());
			r9546MenuItem.getElement().setInnerHTML(getHTML("R9546 CCC no asignado a la autorizaci\u00F3n", "aon-icon-exception", AON.AON_ICON_CMD_BUTTON, style.cmd_btn()));
			r9546MenuItem.ensureDebugId("r9546MenuItem");
			
			r9607MenuItem = addItem("",  new EmptyCommand());
			r9607MenuItem.getElement().setInnerHTML(getHTML("R9607 Liquidaci\u00F3n sin trabajadores en alta", "aon-icon-exception", AON.AON_ICON_CMD_BUTTON, style.cmd_btn()));
			r9607MenuItem.ensureDebugId("r9607MenuItem");
			
			r9650MenuItem = addItem("",  new EmptyCommand());
			r9650MenuItem.getElement().setInnerHTML(getHTML("R9650 No existen trabajadores en alta por vacaciones no disfrutadas", "aon-icon-exception", AON.AON_ICON_CMD_BUTTON, style.cmd_btn()));
			r9650MenuItem.ensureDebugId("r9650MenuItem");
			
			r9544MenuItem = addItem("",  new EmptyCommand());
			r9544MenuItem.getElement().setInnerHTML(getHTML("A9544 Existe obligaci\u00F3n de presentar en este periodo Liquidaci\u00F3n L13 del mes", "aon-icon-exception", AON.AON_ICON_CMD_BUTTON, style.cmd_btn()));
			r9544MenuItem.ensureDebugId("r9544MenuItem");
			
		}

		public MenuItem getR9546MenuItem() {
			return r9546MenuItem;
		}

		public MenuItem getR9607MenuItem() {
			return r9607MenuItem;
		}

		public MenuItem getR9650MenuItem() {
			return r9650MenuItem;
		}

		public MenuItem getR9544MenuItem() {
			return r9544MenuItem;
		}
		
		private String getHTML(String text, String ...styles ) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<span class='");
			for (String style : styles)
				buffer.append(style + ' ' );
			buffer.append("' >");
			buffer.append(text);
			buffer.append("</span>");
			
			return buffer.toString();
		}
		
	}
	
	// ----------------------------------------------- ScheduledCommand (SLD)
	
	class MsjRecButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			fileUpload.setName(TRABAJADORES_TRAMOS.name());
			formPanel.setAction(CRETA_URL + '/' +TRABAJADORES_TRAMOS.name());
			fileUpload.click();
		}
	}
	
	
	
	// Comunicaciones
	
	class BasesButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			submitBases();
		}
	}
	
	class DbaButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			onClickDBAButton(null);
		}
	}
	
	class AgrarianButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {}
	}
	
	// Solicitudes
	
	class TrabajadoresYTramosButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			onClickTrabajadoresYTramosButton(null);
		}
	}
	
	class BorradorButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			onClickBorradorButton(null);
		}
	}
	
	class ConfirmacionButtonCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			onClickConfirmacionButton(null);
		}
	}
	
	class SLDMenu extends ContextMenu {
		
		// Comunicaciones
		private MenuItem basesButtonMenuItem = null;
		private MenuItem dbaButtonMenuItem = null;
		private MenuItem agrarianButtonMenuItem = null;
		
		// Solicitudes
		private MenuItem trabajadoresYTramosButtonMenuItem = null;
		private MenuItem borradorButtonMenuItem = null;
		private MenuItem confirmacionButtonMenuItem = null;
		
		// SLD
		private MenuItem msjRecButtonMenuItem = null;
		private MenuItem dclButtonMenuItem = null;
		
		public SLDMenu() {
			
			basesButtonMenuItem = addItem(CretaService.File.BASES.getFilename(), new BasesButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			basesButtonMenuItem.ensureDebugId("basesButtonMenuItem");
			basesButtonMenuItem.setEnabled(false);
			
			dbaButtonMenuItem = addItem(CretaService.File.COMUNICACION_DATOS_BANCARIOS.getFilename(), new DbaButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			dbaButtonMenuItem.ensureDebugId("dbaButtonMenuItem");
			
			agrarianButtonMenuItem = addItem(CretaService.File.COMUNICACION_DATOS_BANCARIOS.getFilename(), new AgrarianButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			agrarianButtonMenuItem.ensureDebugId("agrarianButtonMenuItem");
			agrarianButtonMenuItem.setVisible(false);
			
			addSeparator();
			
			trabajadoresYTramosButtonMenuItem = addItem(CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS.getFilename(), new TrabajadoresYTramosButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			trabajadoresYTramosButtonMenuItem.ensureDebugId("trabajadoresYTramosButtonMenuItem");
			
			borradorButtonMenuItem = addItem(CretaService.File.SOLICITUD_BORRADOR.getFilename(), new BorradorButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			borradorButtonMenuItem.ensureDebugId("borradorButtonMenuItem");
			
			confirmacionButtonMenuItem = addItem(CretaService.File.SOLICITUD_CONFIRMACION.getFilename(), new ConfirmacionButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			confirmacionButtonMenuItem.ensureDebugId("confirmacionButtonMenuItem");
			
			addSeparator();
			
			msjRecButtonMenuItem = addItem("Mensajes Recibidos", new MsjRecButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			msjRecButtonMenuItem.ensureDebugId("msjRecButtonMenuItem");
			
			dclButtonMenuItem = addItem(DOCUMENTO_CALCULO_LIQUIDACION.getFilename(), new MsjRecButtonCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			dclButtonMenuItem.ensureDebugId("dclButtonMenuItem");
			
		}
		
		public MenuItem getBasesButtonMenuItem() {
			return basesButtonMenuItem;
		}
	}
	
	// ----------------------------------------------- UiFields
	
	@UiField
	static
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmd_btn();
		String grid();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	FormPanel formPanel;
	
	@UiField
	FileUpload fileUpload;
	
	@UiField(provided = true)
	DataGrid<JsFile> dataGrid;

	// ----------------------------------------------- Variables

	private PopupPanel popupTooltip;
	private Timer jsFileToolTipTimer;
	private HashSet<String> showingEmployees; 
	private MultiSelectionModel<JsFile> jsFileSelectionModel;
	private Map<String, CretaService.JsBases> basesMap;
	private Map<String, CretaService.JsRespuesta> respuestasMap;
	private Map<String, CretaService.JsTrabajadoresYTramos> trabajadoresYTramosMap;
	private Map<String, MultiSelectionModel<String>> trabajadoresSelectionModel;
	
	private SLDCretaMenu sldCretaMenu;
	private SLDMenu sldMenu;
	private FilterMenu filterMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton sldCretaBtn;
	private AonExpandButton sldExpandBtn;
	private AonToolbarButton comunicationsBtn;
	private AonToolbarButton requestsBtn;
	private AonToolbarButton filterBtn;
	
	// ----------------------------------------------- Constructor

	public CretaDetail() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		ProvidesKey<JsFile> keyProvider = new HasIdKeyProvider<JsFile>();
		
		dataGrid = new CustomDataGrid<JsFile>(keyProvider) {
			@Override
			protected void onBrowserEvent2(Event event) {
				super.onBrowserEvent2(event);
			}	
		};
		
		toolbar = getToolbarPanel();

		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		sldCretaMenu = new SLDCretaMenu();
		sldMenu = new SLDMenu();
		filterMenu = new FilterMenu();
		
		showingEmployees = new HashSet<String>();

		jsFileSelectionModel = new MultiSelectionModel<JsFile>(keyProvider);
		trabajadoresSelectionModel = new HashMap<String,MultiSelectionModel<String>>();
		
		basesMap = new HashMap<String, CretaService.JsBases>();
		respuestasMap = new HashMap<String, CretaService.JsRespuesta>();
		trabajadoresYTramosMap = new HashMap<String, CretaService.JsTrabajadoresYTramos>();

		fileUpload.getElement().setPropertyString("multiple", "multiple");
		
		// View Menu
		MenuItem viewMenuItems [] = {
				filterMenu.getL00MenuItem(),
				filterMenu.getL13MenuItem(),
				filterMenu.getNextMonthMenuItem(),
				filterMenu.getPrevMonthMenuItem(),
				filterMenu.getPendingMenuItem(),
				filterMenu.getProcessingMenuItem(),
				filterMenu.getErrorMenuItem(),
				filterMenu.getCalculatedMenuItem(),
				filterMenu.getConfirmedMenuItem(),
				filterMenu.getR9544MenuItem(),
				filterMenu.getR9546MenuItem(),
				filterMenu.getR9607MenuItem(),
				filterMenu.getR9650MenuItem()
		};
		
		for ( MenuItem menuItem: viewMenuItems ) {
			setCheckedStyle(menuItem, true);
			menuItem.setScheduledCommand( () -> {
				setCheckedStyle(menuItem, !isChecked(menuItem));
				onTrabajadoresYTramos();
			} );
		}
		
		filterMenu.getNextMonthMenuItem().setText(AonWordUtils.capitalize(MONTH_FORMAT.format(getNextMonth())));
		filterMenu.getPrevMonthMenuItem().setText(AonWordUtils.capitalize(MONTH_FORMAT.format(getPrevMonth())));
		
		setCheckedStyle(filterMenu.getR9544MenuItem(), false );
		setCheckedStyle(filterMenu.getR9650MenuItem(), false );
		setCheckedStyle(filterMenu.getR9607MenuItem(), false );
		setCheckedStyle(filterMenu.getPrevMonthMenuItem(), new Date().getDate() < 5 );
		
		initializeDataGrid();
		dataGrid.setHeight((Window.getClientHeight() - 200) + "px");
		dataGrid.setWidth("98%");
		
	}
	
	// ----------------------------------------------- InitializeDataGrid

	private void initializeDataGrid() {
		
		dataGrid.setSelectionModel(jsFileSelectionModel, DefaultSelectionEventManager.<JsFile> createCheckboxManager(0));

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
			public void onBrowserEvent(Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
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
				return CretaDetail.this.isSelectable(jsFile) && CretaDetail.this.jsFileSelectionModel.isSelected(jsFile);
			}

			@Override
			public void render(Context context, JsFile jsFile, SafeHtmlBuilder sb) {
				if ( CretaDetail.this.isSelectable(jsFile) )
					super.render(context, jsFile, sb);
			}
			
		}, new SelectAllHeader<JsFile>(jsFileSelectionModel, dataGrid) {
			@Override
			protected boolean isSelectable(JsFile item) {
				return CretaDetail.this.isSelectable(item);
			}
		});

		dataGrid.setColumnWidth(0, "40px");
		
		// --------------------------------------------------------------------

		Column<JsFile, JsFile> showEmployeesColumn = new Column<JsFile, JsFile>( new ExpandCollapseCell(CLICK, DBLCLICK) ){
			@Override
			public JsFile getValue(JsFile jsFile) {
				return jsFile;
			}
		};
		
		showEmployeesColumn.setFieldUpdater(new FieldUpdater<JsFile, JsFile>() {
			@Override
			public void update(int index, JsFile jsFile, JsFile value) {
				String id = jsFile.getId();
				if ( showingEmployees.contains(id))
					showingEmployees.remove(id);
				else
					showingEmployees.add(id);
				
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
						tr.attribute("onmouseover", "javascript:onEmployeeOver('"+jsFile.getId()+"','"+employee.getNaf()+"',event.x,event.y, event.ctrlKey);");
						tr.attribute("onclick", "javascript:onEmployeeClick('"+jsFile.getId()+"','"+employee.getNaf()+"',event.x, event.y, event.ctrlKey);");
						
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
					
					for ( JsRespuesta old: MainCreta.getOld(File.RESPUESTA, jsRespuesta) )
						if ( !isSolicitudTrabajdoresYTramosRespuesta(old, jsFile))
							if ( !exist(old, jsFiles) && !isAON(old))
								jsFiles.add(old);
					
					JsBases jsBases = CretaDetail.this.basesMap.get(jsFile.getId());
					if ( jsBases != null ) {
					    	int i = findReplied(jsBases, jsFiles);
						if ( i >= 0 ) {
						    log( i + "-. " + jsBases.getExternalReference());
						    jsFiles.add(i+1, jsBases);
						}
						for ( JsFile old: MainCreta.getOld(File.BASES, jsBases) ) {
						    	i = findReplied(old, jsFiles);
							if ( i >= 0 ) {
							    log( i + "-. " + old.getExternalReference());
							    jsFiles.add(i+1, old);
							}
						}
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

			int findReplied(JsFile jsBases, Collection<JsFile> jsRespuestas) {
			    	int i = 0;
				for ( JsFile jsRespuesta: jsRespuestas ) { 
					if ( jsBases.getExternalReference().equals(jsRespuesta.getExternalReference()) ) {
						return i;
					}
					i++;
				}
				
				return -1;
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
				CretaDetail.this.sldMenu.getBasesButtonMenuItem().setEnabled(selected);

			}
		});
		
		dataGrid.setRowData(new ArrayList<CretaService.JsFile>(0));
	}
	
	// ----------------------------------------------- UIHandlers

	@UiHandler("fileUpload")
	void onFileUploadChange(ChangeEvent event) {
		formPanel.submit();
	}
	
	// ----------------------------------------------- CreataDetail.Auxiliar Methods
	
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
	
	String getEmployeeFullName(JsEmployee jsEmployee) {
		return jsEmployee.getCaf();
	}
	
	void addSLDMenuItem(String text, ScheduledCommand scheduledCommand) {
		sldMenu.addItem(text, scheduledCommand, AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
	}
	
	// ----------------------------------------------- CreataDetail.On Action Methods

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
			
			Collection<CretaService.JsTrabajadoresYTramos> visibleTrabajadoresYTramos = filterVisible(trabajadoresYTramosMap.values());
			
			filter(visibleTrabajadoresYTramos).forEach(f -> filteredMap.putIfAbsent(f.getId(), f));
			
			Collection<CretaService.JsRespuesta> visibleRespuestas = 
					filterVisible(respuestasMap.values());

			basesMap = MainCreta.add(File.BASES, bases);

			for (JsRespuesta jsRespuesta : visibleRespuestas )
				if (!filteredMap.containsKey(jsRespuesta.getId()))
					filter(Collections.singleton(jsRespuesta)).forEach(f -> filteredMap.putIfAbsent(f.getId(), f));
			
			List<JsFile> filtered = new ArrayList<JsFile>(filteredMap.values());
			Collections.sort(filtered, JsFileComparator.newInstace());
			
			dataGrid.setRowData(filtered);

		} catch (UnsupportedOperationException e) {

			respuestasMap = new HashMap<String, JsRespuesta>();
			
			for (CretaService.JsRespuesta respuesta : respuestas)
				respuestasMap.put(respuesta.getCCC() + respuesta.getFrom(), respuesta);

			List<JsTrabajadoresYTramos> filtered = filter(Arrays.asList(trabajadoresYTramos));
			
			dataGrid.setRowData(filtered);
		}

	}
	
	public void onEmployeeOut() {
		if (jsFileToolTipTimer != null)
			jsFileToolTipTimer.cancel();		
	}

	public void onEmployeeOver(String trabajadoresYTramosId, String naf, int x , int y, boolean ctrlKey ) {
		if ( ctrlKey ) {
			return;
		}
			
		jsFileToolTipTimer = new Timer() {
			
			@Override
			public void run() {
				if (CretaDetail.this.popupTooltip != null)
					CretaDetail.this.popupTooltip.hide();
				
				
				JsEmployee[] rEmployees =
				Arrays.stream(respuestasMap.getOrDefault(trabajadoresYTramosId, JsRespuesta.createEmptyRespuesta()).getEmployees())
				.filter(employee -> employee.getNaf().equalsIgnoreCase(naf)).toArray(JsEmployee[]::new);

				JsEmployee[] tEmployees =
				Arrays.stream(trabajadoresYTramosMap.getOrDefault(trabajadoresYTramosId, JsTrabajadoresYTramos.createEmptyTrabajadoresyTramos()).getEmployees())
				.filter(tEmployee -> tEmployee.getNaf().equalsIgnoreCase(naf)).toArray(JsEmployee[]::new);
				
				CretaDetail.this.popupTooltip = MainCreta.showjsEmployeeToolTip(tEmployees, rEmployees, x, y);

			}
		};

		jsFileToolTipTimer.schedule(1000);
	}
	
	public void onEmployeeClick(String trabajadoresYTramosId, String naf,int x , int y,  boolean ctrlKey) {
		if ( ctrlKey ) { 
			onClickEmployee(x, y, naf);
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
	
	// ----------------------------------------------- CreataDetail.On Action JS Methods
	
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
		$wnd.onEmployeeOver = $entry(function(trabajadoresYTramosId, naf, x, y, ctrlKey) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onEmployeeOver(Ljava/lang/String;Ljava/lang/String;IIZ)(trabajadoresYTramosId, naf, x, y, ctrlKey);
		});
	}-*/;
	
	private native void exportOnEmployeeChange() /*-{
		var that = this;
		$wnd.onEmployeeChange = $entry(function(trabajadoresYTramosId, naf,checked) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onEmployeeChange(Ljava/lang/String;Ljava/lang/String;Z)(trabajadoresYTramosId, naf, checked);
		});
	}-*/;
	
	private native void exportOnEmployeeClick() /*-{
		var that = this;
		$wnd.onEmployeeClick = $entry(function(trabajadoresYTramosId, naf, x, y, ctrlKey) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaDetail::onEmployeeClick(Ljava/lang/String;Ljava/lang/String;IIZ)(trabajadoresYTramosId, naf, x, y, ctrlKey);
		});
	}-*/;


	// ----------------------------------------------- CreataDetail.Composite Methods
	
	@Override
	protected void onAttach() {
		exportSubmitComplete();
		exportOnEmployeeOut();
		exportOnEmployeeOver();
		exportOnEmployeeChange();
		exportOnEmployeeClick();
		super.onAttach();
	}

	protected void submitBases() {
		Map<String, Collection<String>> datas = new HashMap<String, Collection<String>>();
		try {
			datas.put(CretaService.Parameter.NAFS.name(), getNafs());
		}catch ( Exception e ) {
		}
		
		datas.put(CretaService.Parameter.USER.name(), Collections.singleton(Wnd.getCurrentUser()));
		datas.put(CretaService.Parameter.DOMAIN.name(), Collections.singleton(Wnd.getCurrentDomainNameURL()));
		
		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.BASES,
				datas,
				jsFileSelectionModel.getSelectedSet(),
				new AsyncCallback<CretaService.JsBasesResult>() {

					@Override
					public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(JsBasesResult result) {
						onBases(result);
					}
				},
				(progress) -> onProgress(progress));
	}
	
	protected boolean isSelectable(JsFile jsFile) {
		JsRespuesta jsRespuesta = respuestasMap.getOrDefault(jsFile.getId(), JsRespuesta.createEmptyRespuesta());
		for ( JsError jsError : jsRespuesta.getErrors() ) { 
			if ( "R9546".equals(jsError.getCode()))
				return false;
			if ( "R9607".equals(jsError.getCode()))
				return false;
			if ( "R9650".equals(jsError.getCode()))
				return false;
		}
		return true;
	}
	

	// ----------------------------------------------- DataGrid.Methods

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
		
		JsTrabajadoresYTramos jsTrabajadoresYTramos = trabajadoresYTramosMap.get(id);
		if ( jsTrabajadoresYTramos != null ) {
			jsFileSelectionModel.setSelected(jsTrabajadoresYTramos, selectionModel.getSelectedSet().size() > 0);
		} else {
			JsRespuesta jsRespuesta = respuestasMap.get(id);
			jsFileSelectionModel.setSelected(jsRespuesta, selectionModel.getSelectedSet().size() > 0);
		}
	}
	
	private <T extends CretaService.JsFile> Collection<T> filterVisible(Collection<T> jsFiles) {
		return
		jsFiles.stream()
		.filter(jsFile -> {
			
			try {
				
				Date date = getDate(jsFile.getDate());
				if ( date.before(getNextMonth())) 
					return false;
			
				String type = jsFile.getType();
				if ( AonStringUtils.equalsIgnoreCase("L00", type) && !isChecked(filterMenu.getL00MenuItem()))
					return false;
				if ( AonStringUtils.equalsIgnoreCase("L13", type) && !isChecked(filterMenu.getL13MenuItem()))
					return false;
				
				
				int month = Integer.parseInt(jsFile.getFrom().split("-")[1]);
				
				int nextMonth = getNextMonth().getMonth() + 1;
				if ( month == nextMonth && !isChecked(filterMenu.getNextMonthMenuItem()))
					return false;
				
				int prevMonth = getPrevMonth().getMonth() + 1;
				if ( month == prevMonth && !isChecked(filterMenu.getPrevMonthMenuItem()))
					return false;
				
				JsRespuesta jsRespuesta = respuestasMap.get(jsFile.getId());

				if ( jsRespuesta == null && !isChecked(filterMenu.getProcessingMenuItem()))
					return false;
				
				JsError jsErros[] = jsRespuesta.getErrors();
				if ( jsErros == null && !isChecked(filterMenu.getProcessingMenuItem()))
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
						confirmed = true ; // Calculated
					} else if ( AonStringUtils.equalsIgnoreCase("R9566", jsError.getCode())) {
						confirmed = true ; // Already confirmed
					} 
					
					else if ( AonStringUtils.equalsIgnoreCase("R9544", jsError.getCode())) {
						r9544 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9546", jsError.getCode())) {
						r9546 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9607", jsError.getCode())) {
						r9607 = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9650", jsError.getCode())) {
						r9650 = true ;
					}
					
					else if ( AonStringUtils.equalsIgnoreCase("R9529", jsError.getCode())) {
						calculated = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("A9708", jsError.getCode())) {
						calculated = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9626", jsError.getCode())) {
						calculated = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9758", jsError.getCode())) {
						calculated = true ; // Partially confirmed
					}else if ( AonStringUtils.equalsIgnoreCase("R9668", jsError.getCode())) {
						calculated = true ; // Draft generated
					}else if ( AonStringUtils.equalsIgnoreCase("R9669", jsError.getCode())) {
						calculated = true ; // Partially draft generated
					}else if ( AonStringUtils.equalsIgnoreCase("R9626", jsError.getCode())) {
						calculated = true ; // Reftification accepted 
					}
					
					else if ( MainCreta.isTrabajadoressYTramos(jsFile) 
							&& AonStringUtils.equalsIgnoreCase("A9999", jsError.getCode())) {
						processing = true ;
					}else if (/* MainCreta.isTrabajadoressYTramos(jsFile) 
							&& */AonStringUtils.equalsIgnoreCase("R9998", jsError.getCode())) {
						processing = true ;
					}else if ( AonStringUtils.equalsIgnoreCase("R9569", jsError.getCode())) {
						processing = true ;
						// Liquidation started.  
					}
										
					else if ( AonStringUtils.equalsIgnoreCase("A9999", jsError.getCode())) {
						pending = true ;
					} 
					else {
						error = true ;
					}
				}
				
				if ( !error && !confirmed && calculated && !isChecked(filterMenu.getCalculatedMenuItem()))
					return false;
				if ( !error && confirmed && !isChecked(filterMenu.getConfirmedMenuItem()))
					return false;
				if ( !error && processing && !isChecked(filterMenu.getProcessingMenuItem()))
					return false;
				if ( !error && pending && !isChecked(filterMenu.getPendingMenuItem()))
					return false;
				
				if ( !error && r9544 && !isChecked(filterMenu.getR9544MenuItem()))
					return false;
				if ( !error && r9546 && !isChecked(filterMenu.getR9546MenuItem()))
					return false;
				if ( !error && r9607 && !isChecked(filterMenu.getR9607MenuItem()))
					return false;				
				if ( !error && r9650 && !isChecked(filterMenu.getR9650MenuItem()))
					return false;				
				
				if ( error && !isChecked(filterMenu.getErrorMenuItem()))
					return false;
				
				
			} catch ( Throwable e ) {
				
			}
			
			return true;
		})
		.collect(Collectors.toList());
	}
	
	private static boolean contains(List<JsFile> jsFiles, String id) {
		for (JsFile jsF : jsFiles)
			if (jsF.getId().equals(id))
				return true;
		return false;
	}

	private static boolean contains(List<JsFile> jsFiles, JsFile jsFile) {
		return contains(jsFiles, jsFile.getId());
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
		text = AonStringUtils.substring(text, 0, 7);
		return DateTimeFormat.getFormat("yyyy-MM").parse(text);
	}
	
	public static native void log (String message ) /*-{
		console.log(message);
	}-*/
	;
	
	// ----------------------------------------------- Checked

	protected static boolean isChecked(MenuItem menuItem) {
		return AonStringUtils.containsIgnoreCase(menuItem.getStyleName(), STYLENAME_CHECKED_ITEM);
	}

	protected static void setCheckedStyle(MenuItem menuItem, boolean checked) {
		if (checked) {
			menuItem.addStyleName(STYLENAME_CHECKED_ITEM);
		} else {
			menuItem.removeStyleName(STYLENAME_CHECKED_ITEM);
		}
	}
	
 	// ----------------------------------------------- Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("SLD-Cret@");
		
		sldCretaBtn = new AonToolbarButton( "SLD-Cret@", AON.CSS.aonIconTgss() );
		sldCretaBtn.addClickHandler(e -> {
			onSldCreta(e);
		});
		sldCretaBtn.setVisible(false);
		toolbar.add(sldCretaBtn);
		
		sldExpandBtn = new AonExpandButton("SLD", AON.CSS.aonIconTgss()) {
			
			@Override
			public void onExpandClick(ClickEvent e) {
				onSld(e);
			}
			
			@Override
			public void onDefaultClick(ClickEvent e) {
				submitBases();
			}
		};
		toolbar.add(sldExpandBtn);
		
//		sldBtn = new AonToolbarButton("SLD", AON.CSS.aonIconTgss());
//		sldBtn.addClickHandler(e -> {
//			onSld(e);
//		});
//		toolbar.add(sldBtn);
		
		filterBtn = new AonToolbarButton( "Filtrar", AON.CSS.aonIconFilter() );
		filterBtn.addClickHandler(e -> {
			onFilter(e);
		});
		toolbar.add(filterBtn);
		
		return toolbar;

	}

	// ----------------------------------------------- Toolbar.Methods

	private void onSldCreta(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		sldCretaMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		sldCretaMenu.show();
	}

	private void onSld(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		sldMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		sldMenu.show();
	}

	private void onFilter(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		filterMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		filterMenu.show();
	}
	
	// ----------------------------------------------- Abstract Methods
	
	protected abstract void onClickDBAButton(ClickEvent e);
	
	protected abstract void onClickTrabajadoresYTramosButton(ClickEvent e);
	
	protected abstract void onClickConfirmacionButton(ClickEvent e);
	
	protected abstract void onClickBorradorButton(ClickEvent e);
	
	protected abstract void onBases(JsBasesResult result);
	
	protected abstract void onProgress(JsProgress progress);

	protected abstract String getDescription(String ccc);

	protected abstract <T extends JsFile> List<T> filter(Collection<T> jsFiles);

	protected abstract void onDCLResults(JsEvent success [], JsEvent errors []);

	protected void onClickEmployee(final int x, final int y, String naf) {
		// NOOP
	}

	protected void onJsFileClick(final String key, final int x, final int y) {
		// NOOP
	}

	protected void onJsFileDblClick(final int x, final int y, JsFile ...jsFile ) {
		// NOOP
	}
	

	
	
}
