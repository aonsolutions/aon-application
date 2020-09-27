package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public abstract class ITDialog extends CustomDialog {

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ITDialogUiBinder extends UiBinder<Widget, ITDialog> {}
	
	private static ITDialogUiBinder binder = GWT.create(ITDialogUiBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String warningColor();
		String headerStyle();
		String hide();
		String widthO();
		String columnWidth();
	}
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Button deleteITButton;
	
	@UiField
	Button listButton;
	
	@UiField
	Button backButton;
	
	@UiField
	Button newITButton;
	
	@UiField
	TextBox itObservations;
	
	@UiField
	ListBox raggedList;
	
	@UiField
	DateBoxEx itStartDate;
	
	@UiField
	TextBox collegiateNumberHighPart;
	
	@UiField
	ListBox causeHighPart;
	
	@UiField
	TextBox ciasHighPart;
	
	@UiField
	DateBoxEx itEndDate;
	
	@UiField
	TextBox collegiateNumberLowPart;
	
	@UiField
	ListBox causeLowPart;
	
	@UiField
	TextBox ciasLowPart;
	
	@UiField
	Grid confirmationPartDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid confirmationPartDataTable;
	
	@UiField
	Label newConfirmationPart;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	VerticalPanel itDataTable;
	
	@UiField
	VerticalPanel lowDataTable;
	
	@UiField
	VerticalPanel highDataTable;
	
	@UiField
	VerticalPanel confirmationsDataTable;
	
	@UiField(provided = true)
	DataGrid<IT> itDataGrid;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private ITDialogObject itDialogObject;
	private IT it;
	private NoSelectionModel<IT> selectionITModel;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private List<IT> itList = Collections.emptyList();
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ITDialog(String caption) {	
		initPreView(caption);
	}
	
	public ITDialog(String caption, Boolean advanced) {	
		initPreView(caption);
		
		if(advanced)
			showListOption();
		else
			hideListOption();
			
	}
	
	public void setITDialogObject(ITDialogObject itDialogObject) {
		this.itDialogObject = itDialogObject;
		
		initRaggedListBox();
		initPreview();
		
		// Check if exist IT
		this.it = this.itDialogObject.checkIfIsOpenIt();
		
		if(null != this.it) {
			paintSelectedIT(this.it, false);
			showDeleteOption();
		} else
			hideDeleteOption();
		
		// Check type of part
		if(this.itDialogObject.getEmployeeStatus()) {
			hideHighPartAndConfirmationParts();
		} else
			hideLowPart();
	}
	
	// --------------------------------------------------------------------------------------------
	// 									PROVIDE SALARY DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideITDataGrid() {
		itList  = Collections.emptyList();
		
		// Resource Style CellTable
		itDataGrid = new CustomDataGrid<IT>(Integer.MAX_VALUE, IT.KEY_PROVIDER);
		itDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		itDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		itDataGrid.setEmptyTableWidget(new Label("No existen ITs".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionITModel = new NoSelectionModel<IT>(IT.KEY_PROVIDER);
	    itDataGrid.setSelectionModel(this.selectionITModel);
		
	    // Initialize the columns.
	    addEmployeeInfoColumns(this.selectionITModel);
	    
	    new ListDataProvider<IT>(Collections.emptyList()).addDataDisplay(itDataGrid);
	}
	
	private void addEmployeeInfoColumns(NoSelectionModel<IT> selectionITModel) {
		selectionITModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	        	IT itAux = selectionITModel.getLastSelectedObject();
	        	ITDialog.this.it = itAux;
	        	
	        	paintSelectedIT(ITDialog.this.it, true);
	        }
	    });
	    
	    // Add Selection Column to table
	    itDataGrid.setSelectionModel(selectionITModel);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<IT> lowCauseColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return parseLowCauseByte(it.getTypeLowPart());
	      }
	    };

	    lowCauseColumn.setSortable(true);
	    itDataGrid.setColumnWidth(lowCauseColumn, 200, Unit.PX);
	    
	    TextColumn<IT> lowDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return formatFullDate.format(it.getStartDate());
	      }
	    };

	    lowDateColumn.setSortable(true);
	    lowDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowDateColumn, 100, Unit.PX);
	     
	    TextColumn<IT> highCauseColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return parseHighCauseByte(it.getTypeHighPart());
	      }
	    };

	    highCauseColumn.setSortable(true);
	    highCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(highCauseColumn, 200, Unit.PX);
	    
	    TextColumn<IT> highDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return null == it.getEndDate() ? "-" : formatFullDate.format(it.getEndDate());
	      }
	    };

	    highDateColumn.setSortable(true);
	    highDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(highDateColumn, 100, Unit.PX);
	    
	    TextColumn<IT> rechargeColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	    	  return it.getParent() == null ? "NO" : "SI";
	      }

	    };

	    rechargeColumn.setSortable(true);
	    rechargeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(rechargeColumn, 50, Unit.PX);
	    
	    // Add the columns.
	    itDataGrid.addColumn(lowCauseColumn, "Causa Baja");
	    itDataGrid.addColumn(lowDateColumn, "Fecha Baja");
	    itDataGrid.addColumn(highCauseColumn, "Causa Alta");
	    itDataGrid.addColumn(highDateColumn, "Fecha Baja");
	    itDataGrid.addColumn(rechargeColumn, "Recaida");
	      
	}

	// --------------------------------------------------------------------------------------------
	// 									HEADER STYLES
	// --------------------------------------------------------------------------------------------
	
	public void addStyleToHeader() {
		itDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("cancelButton")
	public void onCancelClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptButton")
	public void onSaveClick(ClickEvent event) {
		if(checkIfSaveIsPossible()) {
			if(null == this.it.getId())
				itDialogObject.addIT(this.it);
			onAccept();
			hide();
		} else {
			WarningDialog dialog = new WarningDialog("ERROR", "La fecha y la causa de baja deben estar rellenadas.");
			dialog.setModal(true);
			dialog.setAnimationEnabled(true);
			dialog.show();
			dialog.center();
		}
	}
	
	@UiHandler("deleteITButton")
	public void onDeleteITButtonClick(ClickEvent event) {
		if(null != this.it.getId()) {
			if(this.it.getIsParent()) {
				WarningDialog dialog = new WarningDialog("ERROR", "No se puede eliminar un parte que tiene reca" + String.valueOf("\u00ED") + "da.");
				dialog.setModal(true);
				dialog.setAnimationEnabled(true);
				dialog.show();
				dialog.center();
			} else {
				AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", String.valueOf("\u00BF") + "Realmente desea eliminar el parte IT?") {
					@Override
					protected void onAccept() {
						onDelete(it);	
						hide();
						ITDialog.this.hide();
					}
				};
				dialog.setModal(true);
				dialog.setAnimationEnabled(true);
				dialog.show();
				dialog.center();
			}
		}
	}
	
	@UiHandler("listButton")
	public void onListButtonClick(ClickEvent event) {
		// Check type of part
		if(this.itDialogObject.getEmployeeStatus()) {
			this.backButton.getElement().getStyle().setDisplay(Display.NONE);
			this.newITButton.getElement().getStyle().clearDisplay();
		} else {
			this.newITButton.getElement().getStyle().setDisplay(Display.NONE);
			this.backButton.getElement().getStyle().clearDisplay();
		}
		
		deckPanel.showWidget(1);
		deckPanel.setWidth("850px");
		initITTable();
		setTableHeights();
	}
	
	@UiHandler("backButton")
	public void onBackButtonClick(ClickEvent event) {
		deckPanel.showWidget(0);
		deckPanel.setWidth("770px");
	}
	
	@UiHandler("newITButton")
	public void onNewITButtonClick(ClickEvent event) {
		clearITPage();
		this.it = new IT();
		deckPanel.showWidget(0);
		deckPanel.setWidth("770px");
		
		if(this.itDialogObject.getEmployeeStatus()) {
			hideHighPartAndConfirmationParts();
		} else {
			hideLowPart();
		}
	}
	
	@UiHandler("itObservations")
	public void onItObservationsChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		this.it.setDescription(event.getValue());
	}
	
	@UiHandler("raggedList")
	public void onRaggedListChange(ChangeEvent event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		this.it.setParent(Integer.parseInt(raggedList.getSelectedValue()));
	}
	
	@UiHandler("itStartDate")
	public void onItStartDateChange(ValueChangeEvent<Date> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		this.it.setStartDate(event.getValue());
		
		setDateLowPart(event.getValue());
	}
	
	@UiHandler("collegiateNumberLowPart")
	public void onCollegiateNumberLowPartChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		setCollegiateNumberLowPart(event.getValue());
	}
	
	@UiHandler("causeLowPart")
	public void onCauseLowPartChange(ChangeEvent event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		this.it.setTypeLowPart(Byte.parseByte(causeLowPart.getSelectedValue()));
		
		setCauseLowPart();
	}
	
	@UiHandler("ciasLowPart")
	public void onCiasLowPartChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		setCiasLowPart(event.getValue());
	}

	@UiHandler("itEndDate")
	public void onItEndDateChange(ValueChangeEvent<Date> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		this.it.setEndDate(event.getValue());
		
		setDateHighPart(event.getValue());
	}
	
	@UiHandler("collegiateNumberHighPart")
	public void onCollegiateNumberHighPartChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		setCollegiateNumberHighPart(event.getValue());
	}
	
	@UiHandler("causeHighPart")
	public void onCauseHighPartChange(ChangeEvent event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		this.it.setTypeHighPart(Byte.parseByte(causeHighPart.getSelectedValue()));
		
		setCauseHighPart();
	}
	
	@UiHandler("ciasHighPart")
	public void onCiasHighPartChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		setCiasHighPart(event.getValue());
	}
	
	@UiHandler("newConfirmationPart")
	public void onNewConfirmationPartClick(ClickEvent event) {
		if(this.it == null) {
			this.it = new IT();
		}
		
		ITPart newITPart = new ITPart();
		newITPart.setType((byte)1);
		this.itDialogObject.addITPart(this.it, newITPart);
		addRowITPart(newITPart);
		calculateScrollPanelHeight();
	}
	
	// ----------------------------------------------- METODOS ABSTRACTOS -------------------------------------------------
	
	protected abstract void onDelete(IT it);
	protected abstract void onAccept();

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	private void initPreView(String caption){
		// ITDataGrid
		provideITDataGrid();
		addStyleToHeader();
	    
		setCaption("Parte IT : " + caption);
		setWidget(binder.createAndBindUi(this));
		
		initListBox();
		deckPanel.showWidget(0);
		deckPanel.setWidth("770px");
	}
	
	private void hideListOption() {
		this.listButton.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showListOption() {
		this.listButton.getElement().getStyle().clearDisplay();
	}
	
	private void hideDeleteOption() {
		this.deleteITButton.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showDeleteOption() {
		this.deleteITButton.getElement().getStyle().clearDisplay();
	}
	
	private void initListBox(){
		raggedList.clear();
		raggedList.addItem("NO", "0");
		
		causeLowPart.clear();
		causeLowPart.addItem("-", "-1");
		causeLowPart.addItem("Enfermedad Com" + String.valueOf("\u00FA") + "n", "0");
		causeLowPart.addItem("Accidente de trabajo", "1");
		causeLowPart.addItem("Maternidad", "2");
		causeLowPart.addItem("Paternidad", "3");
		causeLowPart.addItem("Riesgo para el embarazo", "4");
		causeLowPart.addItem("Riesgo durante la lactancia", "5");
		causeLowPart.addItem("Accidente no laboral", "6");
		causeLowPart.addItem("Enfermedad com" + String.valueOf("\u00FA") + "n periodo de carencia", "7");
		causeLowPart.addItem("Enfermedad com" + String.valueOf("\u00FA") + "n, prestaci" + String.valueOf("\u00F3") + "n profesional (COVID-19)", "8");
		
		causeHighPart.clear();
		causeHighPart.addItem("-", "-1");
		causeHighPart.addItem("Curaci" + String.valueOf("\u00F3") + "n", "0");
		causeHighPart.addItem("Fallecimiento", "1");
		causeHighPart.addItem("Inspecci" + String.valueOf("\u00F3") + "n m" + String.valueOf("\u00E9") + "dica", "2");
		causeHighPart.addItem("Propuesta incapacidad", "3");
		causeHighPart.addItem("Agotamiento de plazo", "4");
		causeHighPart.addItem("Mejor" + String.valueOf("\u00ED") + "a que permite realizar el trabajo habitual", "5");
		causeHighPart.addItem("Incompareciencia", "6");
		causeHighPart.addItem("Control INSS duraci" + String.valueOf("\u00F3") + "n 12 meses", "7");
		causeHighPart.addItem("Recuperaci" + String.valueOf("\u00F3") + "n capacidad profesional", "8");
		causeHighPart.addItem("Incompareciencia contratos de formaci" + String.valueOf("\u00F3") + "n", "9");
		
	}
	
	private void initPreview() {
		confirmationPartDataTableHeader.clear();
		confirmationPartDataTableHeader.resize(0, 0);
		confirmationPartDataTableHeader.resizeColumns(5);
		confirmationPartDataTable.clear();
		confirmationPartDataTable.resize(0, 0);
		confirmationPartDataTable.resizeColumns(5);
		paintHeader();
		calculateScrollPanelHeight();
		setColumnWidth();
		center();
	}
	
	private void hideLowPart() {
		this.itDataTable.getElement().getStyle().setDisplay(Display.NONE);
		this.lowDataTable.getElement().getStyle().setDisplay(Display.NONE);
		this.highDataTable.getElement().getStyle().clearDisplay();
		this.confirmationsDataTable.getElement().getStyle().clearDisplay();
	}

	private void hideHighPartAndConfirmationParts() {
		this.itDataTable.getElement().getStyle().clearDisplay();
		this.lowDataTable.getElement().getStyle().clearDisplay();
		this.highDataTable.getElement().getStyle().setDisplay(Display.NONE);
		this.confirmationsDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void clearITPage() {
		this.itObservations.setText("");
		this.raggedList.setSelectedIndex(0);
		
		this.itStartDate.setValue(null);
		this.collegiateNumberLowPart.setText("");
		this.causeLowPart.setSelectedIndex(0);
		this.ciasLowPart.setText("");
		
		this.itEndDate.setValue(null);
		this.collegiateNumberHighPart.setText("");
		this.causeHighPart.setSelectedIndex(0);
		this.ciasHighPart.setText("");
		
		this.confirmationPartDataTable.clear();
	}
	
	private void setDateLowPart(Date date) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 0); // BAJA
			itPart.setDate(date);
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 0) {
					itPart.setDate(date);
				}
			}
		}
	}
	
	private void setCollegiateNumberLowPart(String collegiateNumberLowPart) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 0); // BAJA
			itPart.setCollegeNumber(collegiateNumberLowPart);
			itPart.setDate(itStartDate.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 0) {
					itPart.setCollegeNumber(collegiateNumberLowPart);
				}
			}
		}
	}
	
	private void setCauseLowPart() {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 0); // BAJA
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		}
	}
	
	private void setCiasLowPart(String ciasLowPart) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 0); // BAJA
			itPart.setCias(ciasLowPart);
			itPart.setDate(itStartDate.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 0) {
					itPart.setCias(ciasLowPart);
				}
			}
		}
	}
	
	private void setDateHighPart(Date date) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 2); // ALTA
			itPart.setDate(date);
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {
					itPart.setDate(date);
					added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart();
				itPart.setType((byte) 2); // ALTA
				itPart.setDate(date);
				
				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCollegiateNumberHighPart(String collegiateNumberHighPart) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 2); // ALTA
			itPart.setCollegeNumber(collegiateNumberHighPart);
			itPart.setDate(itStartDate.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {
					itPart.setCollegeNumber(collegiateNumberHighPart);
					added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart();
				itPart.setType((byte) 2); // ALTA
				itPart.setCollegeNumber(collegiateNumberHighPart);
				
				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCauseHighPart() {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 2); // ALTA
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {
					added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart();
				itPart.setType((byte) 2); // ALTA
				
				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCiasHighPart(String ciasHighPart) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 2); // ALTA
			itPart.setCias(ciasHighPart);
			itPart.setDate(itStartDate.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {
					itPart.setCias(ciasHighPart);
					added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart();
				itPart.setType((byte) 2); // ALTA
				itPart.setCias(ciasHighPart);
				
				this.it.addITPart(itPart);
			}
		}
	}

	private boolean checkIfSaveIsPossible() {
		if(causeLowPart.getSelectedIndex() != 0 && null != itStartDate.getValue())
			return true;
		
		return false;
	}
	
	// --------------------------------------------------------------------------------------------
	// 										INIT CCCs TABLE
	// --------------------------------------------------------------------------------------------

	private void initITTable() {		
		// Create a data provider.
	    ListDataProvider<IT> dataProvider = new ListDataProvider<IT>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(itDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<IT> itListAux = dataProvider.getList();
	    itListAux.clear();
	    
	    this.itList = itDialogObject.getITList();
	    
	    for (IT it : this.itList) {
	    	itListAux.add(it);
	    } 
	    
	    // Set page size
	    itDataGrid.setPageSize(itList.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortColums(itListAux); 
		
	}
	
	private void addSortColums(List<IT> itList) {
		ListHandler<IT> columnSortHandler = new ListHandler<IT>(itList);
		
	    columnSortHandler.setComparator(itDataGrid.getColumn(0), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getTypeLowPart().compareTo(o2.getTypeLowPart()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(1), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getStartDate().compareTo(o2.getEndDate()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(2), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getTypeHighPart().compareTo(o2.getTypeHighPart()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(3), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(4), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getParent().compareTo(o2.getParent()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    itDataGrid.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    itDataGrid.getColumn(1).setDefaultSortAscending(false);
	    itDataGrid.getColumnSortList().push(itDataGrid.getColumn(1));   
	}
	
	private void paintHeader() {
		int row = confirmationPartDataTableHeader.insertRow(confirmationPartDataTableHeader.getRowCount());
		Label orderNumber = new Label("N" + String.valueOf("\u00B0") + " DE ORDEN");
		Label date = new Label("FECHA");
		Label collegeNumber = new Label("N" + String.valueOf("\u00B0") + " COLEGIADO");
		Label cias = new Label("CIAS");
		Label blank = new Label("");
		
		orderNumber.addStyleName(style.headerStyle());
		date.addStyleName(style.headerStyle());
		collegeNumber.addStyleName(style.headerStyle());
		cias.addStyleName(style.headerStyle());
		blank.addStyleName(style.headerStyle());
		
		confirmationPartDataTableHeader.setWidget(row, 0, orderNumber);
		confirmationPartDataTableHeader.setWidget(row, 1, date);
		confirmationPartDataTableHeader.setWidget(row, 2, collegeNumber);
		confirmationPartDataTableHeader.setWidget(row, 3, cias);
		confirmationPartDataTableHeader.setWidget(row, 4, blank);
	}
	
	private void calculateScrollPanelHeight() {
		Integer height = 100;
		Integer extra = 30;
		int rows = confirmationPartDataTable.getRowCount();
		Integer newHeight = 0;
		if(rows < 4) {
			int mod = rows%4;
			newHeight = mod*extra+extra;
		}else {
			int div = rows/4;
			int mod = rows%4;
			if(div < 2)
				newHeight = (height*div)+(extra*mod)+extra;
			else
				newHeight = 220;
		}
		
		if(newHeight > 100)
			newHeight = 95;
		
		scrollPanel.setHeight(newHeight + "px");
	}
	
	private void setColumnWidth() {
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 0, style.columnWidth());
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 1, style.columnWidth());
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 2, style.columnWidth());
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 3, style.columnWidth());
		
		confirmationPartDataTable.getColumnFormatter().addStyleName(0, style.columnWidth());
		confirmationPartDataTable.getColumnFormatter().addStyleName(1, style.columnWidth());
		confirmationPartDataTable.getColumnFormatter().addStyleName(2, style.columnWidth());
		confirmationPartDataTable.getColumnFormatter().addStyleName(3, style.columnWidth());
	}
	
	// ----------------------------------------------- METODOS AUXILIARES ------------------------------------------------
	
	private void initRaggedListBox(){
		raggedList.clear();
		raggedList.addItem("NO", "0");
		
		for(IT it : itDialogObject.getITList()) {
			if(null != it.getEndDate() && notSelectedId(it.getId())) {
				String item = it.getDescription() + " (" + formatFullDate.format(it.getStartDate()) + " / " + formatFullDate.format(it.getEndDate()) + ")";
				raggedList.addItem(item, it.getId().toString());
			}
		}
	}
	
	private boolean notSelectedId(Integer itId) {
		return (null == this.it || null == this.it.getId()) ? true : (this.it.getId() == itId || this.it.getId().equals(itId));
	}

	private String parseLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "Enfermedad Com" + String.valueOf("\u00FA") + "n";
			case (byte)1:
				return "Accidente de trabajo";
			case (byte)2:
				return "Maternidad";
			case (byte)3:
				return "Paternidad";
			case (byte)4:
				return "Riesgo para el embarazo";
			case (byte)5:
				return "Riesgo durante la lactancia";
			case (byte)6:
				return "Accidente no laboral";
			case (byte)7:
				return "Enfermedad com" + String.valueOf("\u00FA") + "n periodo de carencia";
			case (byte)8:
				return "Enfermedad com" + String.valueOf("\u00FA") + "n, prestaci" + String.valueOf("\u00F3") + "n profesional (COVID-19)";
			default:
				return "-";
		}
	}
	
	private String parseHighCauseByte(Byte typeHighPart) {
		if(null == typeHighPart)
			return "-";
		
		switch (typeHighPart) {
			case (byte)0:
				return "Curaci" + String.valueOf("\u00F3") + "n";
			case (byte)1:
				return "Fallecimiento";
			case (byte)2:
				return "Inspecci" + String.valueOf("\u00F3") + "n m" + String.valueOf("\u00E9") + "dica";
			case (byte)3:
				return "Propuesta incapacidad";
			case (byte)4:
				return "Agotamiento de plazo";
			case (byte)5:
				return "Mejor" + String.valueOf("\u00ED") + "a que permite realizar el trabajo habitual";
			case (byte)6:
				return "Incompareciencia";
			case (byte)7:
				return "Control INSS duraci" + String.valueOf("\u00F3") + "n 12 meses";
			case (byte)8:
				return "Recuperaci" + String.valueOf("\u00F3") + "n capacidad profesional";
			case (byte)9:
				return "Incompareciencia contratos de formaci" + String.valueOf("\u00F3") + "n";
			default:
				return "-";
		}
	}
	
	private void setTableHeights() {
		itDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 400), Unit.PX);
	}
	
	private void paintSelectedIT(IT it, boolean showAll) {
		itObservations.setText(it.getDescription());
		setSelectedValueLB(raggedList, it.getParent().toString());
		
		itStartDate.setValue(it.getStartDate());
		setSelectedValueLB(causeLowPart, it.getTypeLowPart().toString());
		
		itEndDate.setValue(it.getEndDate());
		setSelectedValueLB(causeHighPart, null == it.getTypeHighPart() ? "-1" : it.getTypeHighPart().toString());
		
		initPreview();
		
		for(ITPart itPart : it.getITParts()) {
			switch (itPart.getType()) {
				case (byte)0:
					collegiateNumberLowPart.setValue(itPart.getCollegeNumber());
					ciasLowPart.setValue(itPart.getCias());
					continue;
				case (byte)2:
					collegiateNumberHighPart.setValue(itPart.getCollegeNumber());
					ciasHighPart.setValue(itPart.getCias());
					continue;
				default:
					addRowITPart(itPart);
					continue;
			}
		}
		
		calculateScrollPanelHeight();
		
		if(showAll) {
			itDataTable.getElement().getStyle().clearDisplay();
			lowDataTable.getElement().getStyle().clearDisplay();
			highDataTable.getElement().getStyle().clearDisplay();
			confirmationsDataTable.getElement().getStyle().clearDisplay();
			deleteITButton.getElement().getStyle().clearDisplay();
		}
		
		deckPanel.showWidget(0);
		deckPanel.setWidth("770px");
	}
	
	private void addRowITPart(ITPart itPart) {
		int row = confirmationPartDataTable.insertRow(confirmationPartDataTable.getRowCount());
		
		TextBox orderNumberTB = new TextBox();
		orderNumberTB.setStyleName("aon-inputText");
		orderNumberTB.setText(null == itPart.getConfirmOrderNumber() ? "" : itPart.getConfirmOrderNumber().toString());
		
		orderNumberTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				Byte value = Byte.parseByte(event.getValue());
				itPart.setConfirmOrderNumber(value);
			}
		});
		
		DateBoxEx dateBox = new DateBoxEx();
		dateBox.setValue(itPart.getDate());
		
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				itPart.setDate(event.getValue());
			}
		});

		TextBox collegeNumberTB = new TextBox();
		collegeNumberTB.setStyleName("aon-inputText");
		collegeNumberTB.setText(itPart.getCollegeNumber());
		
		collegeNumberTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				itPart.setCollegeNumber(event.getValue());
			}
		});
		
		TextBox ciasTB = new TextBox();
		ciasTB.setStyleName("aon-inputText");
		ciasTB.setText(itPart.getCias());
		
		ciasTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				itPart.setCias(event.getValue());
			}
		});
		
		Button deleteBTN = new Button();
		deleteBTN.setStyleName("aon-editDataTable-button aon-icon-delete");
		deleteBTN.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				itDialogObject.deleteConfirmationPart(itPart.getIt(), itPart);
				redrawConfirmationPartTable(itPart.getIt());
			}
		});
		
		confirmationPartDataTable.setWidget(row, 0, orderNumberTB);
		confirmationPartDataTable.setWidget(row, 1, dateBox);
		confirmationPartDataTable.setWidget(row, 2, collegeNumberTB);
		confirmationPartDataTable.setWidget(row, 3, ciasTB);
		confirmationPartDataTable.setWidget(row, 4, deleteBTN);
	}
	
	private void redrawConfirmationPartTable(Integer itId) {
		IT it = this.itDialogObject.getIT(itId);
		confirmationPartDataTable.clear();
		confirmationPartDataTable.resize(0, 0);
		confirmationPartDataTable.resizeColumns(5);
		paintSelectedIT(it, false);
		calculateScrollPanelHeight();
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

}
