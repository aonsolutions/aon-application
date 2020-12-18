package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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

public abstract class ITDialog extends AonCustomDialog {

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
		String columnDeleteWidth();
	}
	
	@UiField
	HTMLPanel north;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	VerticalPanel itDataTable;
	
	@UiField
	DateBoxEx itStartDate;
	
	@UiField
	Label realStartDate;
	
	@UiField
	ListBox causeHighPart;
	
	@UiField
	DateBoxEx itEndDate;
	
	@UiField
	ListBox causeLowPart;
	
	@UiField
	VerticalPanel informationDataTable;
	
	@UiField
	TextBox collegiateNumberITPart;
	
	@UiField
	TextBox observationTB;
	
	@UiField
	ListBox raggedList;
	
	@UiField
	TextBox ciasITPart;
	
	@UiField
	DateBoxEx directPayDate;
	
	@UiField
	VerticalPanel confirmationsDataTable;
	
	@UiField
	Grid confirmationPartDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid confirmationPartDataTable;
	
	@UiField
	Label newConfirmationPart;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	VerticalPanel maternityDataTable;
	
	@UiField
	ListBox applicantTypeList;
	
	@UiField
	ListBox applicantReasonList;
	
	@UiField
	DoubleBox baseRDBx;
	
	@UiField
	DoubleBox partialityCoefDBx;
	
	@UiField(provided = true)
	DataGrid<IT> itDataGrid;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private ITDialogObject itDialogObject;
	private IT it;
	private NoSelectionModel<IT> selectionITModel;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private List<IT> itList = Collections.emptyList();
	
	private AonToolbar toolbar;
	private AonToolbarButton deleteIT;
	private AonToolbarButton listIT;
	private AonToolbarButton backListIT;
	private AonToolbarButton newIT;
	private AonToolbarButton showCertificate;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ITDialog(String caption) {	
		initPreView(caption);
		
		toolbar = getToolbarPanel();
		north.add(toolbar);
		north.setHeight("50px");
		
		getButtonsPanel();
	}
	
	public ITDialog(String caption, Boolean advanced) {	
		initPreView(caption);
		
		toolbar = getToolbarPanel();
		north.add(toolbar);
		north.setHeight("50px");
		
		getButtonsPanel();
		
		if(advanced)
			showListOption();
		else
			hideListOption();	
		
	}
	
	public void setITDialogObject(ITDialogObject itDialogObject) {
		this.itDialogObject = itDialogObject;
		
		initRaggedListBox();
		initConfirmationsTable();
		
		// Check if exist IT
		this.it = this.itDialogObject.checkIfIsOpenIt();
		
		if(null != this.it) {
			paintSelectedIT(this.it, false);
			showDeleteOption();
			if(null != this.it.isComunicate() && this.it.isComunicate())
				showCertificate.setVisible(true);
		} else
			hideDeleteOption();
		
		// Check type of part
		if(this.itDialogObject.getEmployeeStatus()) {
			hideConfirmationParts();
			hideMaternityTable();
		} else {
			if(null != this.it && (this.it.getTypeLowPart() == (byte)2 || this.it.getTypeLowPart() == (byte)3)) {
				showMaternityTable();
				hideConfirmationParts();
			} else {
				hideMaternityTable();
				showConfirmationParts();
			}
		}
	}
	
	public void setITDialogObject(ITDialogObject itDialogObject, IT it) {
		this.itDialogObject = itDialogObject;
		
		initRaggedListBox();
		initConfirmationsTable();
		
		// Check if exist IT
		this.it = it;
		
		if(null != this.it) {
			paintSelectedIT(this.it, false);
			showDeleteOption();
			if(null != this.it.isComunicate() && this.it.isComunicate())
				showCertificate.setVisible(true);
		} else
			hideDeleteOption();
		
		// Check type of part
		if(this.itDialogObject.getEmployeeStatus()) {
			hideConfirmationParts();
			hideMaternityTable();
		} else {
			if(null != this.it && (this.it.getTypeLowPart() == (byte)2 || this.it.getTypeLowPart() == (byte)3)) {
				showMaternityTable();
				hideConfirmationParts();
			} else {
				hideMaternityTable();
				showConfirmationParts();
			}
		}
	}
	
	public void setITDialogObject(ITDialogObject itDialogObject, IT it, boolean showAll) {
		this.itDialogObject = itDialogObject;
		
		initRaggedListBox();
		initConfirmationsTable();
		
		// Check if exist IT
		this.it = it;
		
		if(null != this.it) {
			paintSelectedIT(this.it, false);
			showDeleteOption();
			if(null != this.it.isComunicate() && this.it.isComunicate())
				showCertificate.setVisible(true);
		} else
			hideDeleteOption();
		
		// Check type of part
		if(!showAll) {
			hideConfirmationParts();
			hideMaternityTable();
		} else {
			if(null != this.it && (this.it.getTypeLowPart() == (byte)2 || this.it.getTypeLowPart() == (byte)3)) {
				showMaternityTable();
				hideConfirmationParts();
			} else {
				hideMaternityTable();
				showConfirmationParts();
			}
		}
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
	        	
	        	backListIT.setVisible(false);
	    		newIT.setVisible(false);
	    		listIT.setVisible(true);
	    		deleteIT.setVisible(true);
	        }
	    });
	    
	    // Add Selection Column to table
	    itDataGrid.setSelectionModel(selectionITModel);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<IT> lowDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return formatFullDate.format(it.getStartDate());
	      }
	    };

	    lowDateColumn.setSortable(true);
	    lowDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowDateColumn, 90, Unit.PX);
	    
	    TextColumn<IT> lowCauseColumn = new TextColumn<IT>() {

			@Override
			public String getValue(IT it) {
				return parseShortLowCauseByte(it.getTypeLowPart());
			}
			
			@Override
			public void render(Context context, IT it, SafeHtmlBuilder sb) {
				if(null != it) {
					sb.appendHtmlConstant("<span title=\"" + parseLowCauseByte(it.getTypeLowPart()) + "\">" + parseShortLowCauseByte(it.getTypeLowPart()) + "</span>");
				}
			}
		};
	    
	    lowCauseColumn.setSortable(true);
	    lowCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowCauseColumn, 90, Unit.PX);
	    
	    TextColumn<IT> highDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return null == it.getEndDate() ? "-" : formatFullDate.format(it.getEndDate());
	      }
	    };

	    highDateColumn.setSortable(true);
	    highDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(highDateColumn, 90, Unit.PX);
		    
	    TextColumn<IT> highCauseColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return parseHighCauseByte(it.getTypeHighPart());
	      }
	    };

	    highCauseColumn.setSortable(true);
	    highCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    
	   TextColumn<IT> rechargeColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	    	  return it.getParent() == null || it.getParent() == 0 ? "NO" : "SI";
	      }

	    };

	    rechargeColumn.setSortable(true);
	    rechargeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(rechargeColumn, 50, Unit.PX);
	    
	    // Add the columns.
	    itDataGrid.addColumn(lowDateColumn, "F. Baja");
	    itDataGrid.addColumn(lowCauseColumn, "Causa Baja");
	    itDataGrid.addColumn(highDateColumn, "F. Alta");
	    itDataGrid.addColumn(highCauseColumn, "Causa Alta");
	    itDataGrid.addColumn(rechargeColumn, "Rec.");
	      
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
	
	@UiHandler("itStartDate")
	public void onItStartDateChange(ValueChangeEvent<Date> event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setStartDate(event.getValue());
		
		setDateLowPart(event.getValue());
		
		createRealStartDate();
		setDirectPayDate();
		showConfirmationParts();
	}
	
	@UiHandler("causeLowPart")
	public void onCauseLowPartChange(ChangeEvent event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setTypeLowPart(Byte.parseByte(causeLowPart.getSelectedValue()));
		
		setCauseLowPart();
		createRealStartDate();
		
		Byte causeLowPartB = Byte.parseByte(causeLowPart.getSelectedValue());
		if(causeLowPartB == (byte)2 || causeLowPartB == (byte)3) {
			checkConfirmationParts();
			showMaternityTable();
			hideConfirmationParts();
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantTypeList);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantReasonList);
		}else {
			hideMaternityTable();
			showConfirmationParts();
		}
	}

	private void checkConfirmationParts() {
		List<ITPart> newITParts = new ArrayList<ITPart>();
		
		for(ITPart itPart : this.it.getITParts()) {
			if(itPart.getType() == (byte)1 || itPart.getType().equals((byte)1))
				continue;
			newITParts.add(itPart);
		}
		
		this.it.setITParts(newITParts);
	}

	@UiHandler("itEndDate")
	public void onItEndDateChange(ValueChangeEvent<Date> event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setEndDate(event.getValue());
		
		setDateHighPart(event.getValue());
	}
	
	@UiHandler("causeHighPart")
	public void onCauseHighPartChange(ChangeEvent event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setTypeHighPart(Byte.parseByte(causeHighPart.getSelectedValue()));
		
		setCauseHighPart();
	}
	
	@UiHandler("collegiateNumberITPart")
	public void onCollegiateNumberLowPartChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		setCollegiateNumberITPart(event.getValue());
	}
	
	@UiHandler("ciasITPart")
	public void onCiasLowPartChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		setCiasITPart(event.getValue());
	}
	
	@UiHandler("observationTB")
	public void onObservationTBChange(ValueChangeEvent<String> event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setDescription(observationTB.getValue());
	}
	
	@UiHandler("directPayDate")
	public void onDirectPayDateChange(ValueChangeEvent<Date> event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setDirectPayDate(directPayDate.getValue());
	}
	
	@UiHandler("raggedList")
	public void onRaggedListChange(ChangeEvent event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		this.it.setParent(Integer.parseInt(raggedList.getSelectedValue()));
		
		Date raggedDate = this.itDialogObject.getRaggedDate(raggedList.getSelectedValue());
		
		if(null != raggedDate)
			realStartDate.setText(formatFullDate.format(raggedDate));
		else {
			if(null == itStartDate.getValue())
				realStartDate.setText("");
			else
				createRealStartDate();
		}
			
	}
	
	@UiHandler("newConfirmationPart")
	public void onNewConfirmationPartClick(ClickEvent event) {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
		
		ITPart newITPart = new ITPart();
		newITPart.setType((byte)1);
		newITPart.setIt(this.it.getId());
		newITPart.setConfirmOrderNumber(getDefaultConfirmOrder());
		newITPart.setCollegeNumber(collegiateNumberITPart.getValue());
		newITPart.setCias(ciasITPart.getValue());
		this.itDialogObject.addITPart(this.it, newITPart);
		addRowITPart(newITPart);
		calculateScrollPanelHeight();
	}
	
	@UiHandler("applicantTypeList")
	public void onApplicantTypeListChange(ChangeEvent event) {
		createApplicantReasonList();
		this.it.setMaternityType(Byte.parseByte(applicantTypeList.getSelectedValue()));
	}
	
	@UiHandler("applicantReasonList")
	public void onApplicantReasonListChange(ChangeEvent event) {
		this.it.setMaternityReason(Byte.parseByte(applicantReasonList.getSelectedValue()));
	}
	
	@UiHandler("baseRDBx")
	public void onBaseRDBxChange(ValueChangeEvent<Double> event) {
		this.it.setRegulationBase(event.getValue());
		this.it.setDailyCGCBase(event.getValue());
		this.it.setDailyCGPBase(event.getValue());
	}
	
	@UiHandler("partialityCoefDBx")
	public void onPartialityCoefDBxChange(ValueChangeEvent<Double> event) {
		this.it.setPartialityCoef(event.getValue());
	}

	private void createApplicantReasonList() {
		Integer selectedIdx = applicantTypeList.getSelectedIndex();
		applicantReasonList.clear();
		switch (selectedIdx) {
		case 1:
			applicantReasonList.addItem("Nacimiento de hijo", "0");
			applicantReasonList.addItem("Parto multiple", "3");
			break;
		case 2:
			applicantReasonList.addItem("Adopcion/Tutela/Acogimiento", "5");
			break;
		case 3:
			applicantReasonList.addItem("Adopcion/Tutela/Acogimiento", "5");
			break;
		default:
			applicantReasonList.addItem("Nacimiento de hijo", "0");
			applicantReasonList.addItem("Fallecimiento de la madre", "1");
			applicantReasonList.addItem("Cesion/Opcion en favor del otro progenitor", "2");
			applicantReasonList.addItem("Parto multiple", "3");
			applicantReasonList.addItem("Inicio del descanso antes del parto (solo para madre biologica ET)", "4");
			break;
		}
	}

	// ----------------------------------------------- METODOS ABSTRACTOS -------------------------------------------------
	protected abstract void onShowCertitificateIT(IT it);
	protected abstract void onDelete(IT it);
	protected abstract void onAccept();
	protected abstract void onAcceptIT(IT it);

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	private void initPreView(String caption){
		// ITDataGrid
		provideITDataGrid();
		addStyleToHeader();
	    
		setCaption("Parte IT : " + caption);
		setWidget(binder.createAndBindUi(this));
		
		initListBox();
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
	}
	
	private void initConfirmationsTable() {
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

	private void clearITPage() {
		this.raggedList.setSelectedIndex(0);
		
		this.itStartDate.setValue(null);
		this.collegiateNumberITPart.setText("");
		this.causeLowPart.setSelectedIndex(0);
		this.ciasITPart.setText("");
		
		this.itEndDate.setValue(null);
		this.causeHighPart.setSelectedIndex(0);
		
		this.realStartDate.setText("");
		this.directPayDate.setValue(null);
		this.observationTB.setText("");
		
		this.confirmationPartDataTable.clear();
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
	
	private void paintSelectedIT(IT it, boolean showAll) {
		itStartDate.setValue(it.getStartDate());
		setSelectedValueLB(causeLowPart, it.getTypeLowPart().toString());
		
		itEndDate.setValue(it.getEndDate());
		setSelectedValueLB(causeHighPart, null == it.getTypeHighPart() ? "-1" : it.getTypeHighPart().toString());
		
		setSelectedValueLB(raggedList, it.getParent().toString());
		
		observationTB.setValue(it.getDescription());
		
		directPayDate.setValue(it.getDirectPayDate());
		
		initConfirmationsTable();
		
		for(ITPart itPart : it.getITParts()) {
			switch (itPart.getType()) {
				case (byte)0:
					collegiateNumberITPart.setValue(itPart.getCollegeNumber());
					ciasITPart.setValue(itPart.getCias());
					continue;
				case (byte)2:
					continue;
				default:
					addRowITPart(itPart);
					continue;
			}
		}
		
		calculateScrollPanelHeight();
		
//		setDirectPayDate();
		createRealStartDate();
		
		if(it.getTypeLowPart() == (byte) 2 || it.getTypeLowPart() == (byte)3) {
			showMaternityTable();
			setSelectedValueLB(applicantTypeList, null == it.getMaternityType() ? "-1" : it.getMaternityType().toString());
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantTypeList);
			setSelectedValueLB(applicantReasonList, null == it.getMaternityReason() ? "-1" : it.getMaternityReason().toString());
			this.baseRDBx.setValue(it.getRegulationBase());
			this.partialityCoefDBx.setValue(it.getPartialityCoef());
		}
		
		if(showAll) {
			informationDataTable.getElement().getStyle().clearDisplay();
			itDataTable.getElement().getStyle().clearDisplay();
			deleteIT.setVisible(true);
			if(it.getTypeLowPart() == (byte) 2 || it.getTypeLowPart() == (byte)3) {
				showMaternityTable();
//				setSelectedValueLB(applicantTypeList, null == it.getMaternityType() ? "-1" : it.getMaternityType().toString());
//				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantTypeList);
//				setSelectedValueLB(applicantReasonList, null == it.getMaternityReason() ? "-1" : it.getMaternityReason().toString());
			} else
				showConfirmationParts();
		}
		
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
	}
	
	private void addRowITPart(ITPart itPart) {
		int row = confirmationPartDataTable.insertRow(confirmationPartDataTable.getRowCount());
		
		TextBox orderNumberTB = new TextBox();
		orderNumberTB.setStyleName("aon-inputText");
		orderNumberTB.getElement().getStyle().setWidth(50, Unit.PX);
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
		collegeNumberTB.getElement().getStyle().setWidth(80, Unit.PX);
		collegeNumberTB.setText(itPart.getCollegeNumber());
		
		collegeNumberTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				itPart.setCollegeNumber(event.getValue());
			}
		});
		
		TextBox ciasTB = new TextBox();
		ciasTB.setStyleName("aon-inputText");
		ciasTB.getElement().getStyle().setWidth(80, Unit.PX);
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
				if(itPart.getIt() != null && null != dateBox.getValue()) {
					itDialogObject.deleteConfirmationPart(itPart.getIt(), itPart);
					redrawConfirmationPartTable(itPart.getIt());
				}
			}
		});
		
		confirmationPartDataTable.setWidget(row, 0, orderNumberTB);
		confirmationPartDataTable.setWidget(row, 1, dateBox);
		confirmationPartDataTable.setWidget(row, 2, collegeNumberTB);
		confirmationPartDataTable.setWidget(row, 3, ciasTB);
		confirmationPartDataTable.setWidget(row, 4, deleteBTN);
	}
	
	// ----------------------------------------------- METODOS SHOW/HIDE ------------------------------------------------
	
	private void hideListOption() {
		listIT.setVisible(false);
	}
	
	private void showListOption() {
		listIT.setVisible(true);
	}
	
	private void hideDeleteOption() {
		deleteIT.setVisible(false);
	}
	
	private void showDeleteOption() {
		deleteIT.setVisible(true);
	}
	
	private void hideConfirmationParts() {
		this.confirmationsDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showConfirmationParts() {
		this.confirmationsDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void showMaternityTable() {
		this.maternityDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideMaternityTable() {
		this.maternityDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// -------------------------------------------------- METODOS AUX --------------------------------------------------
	
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
		causeHighPart.addItem("Incomparecencia", "6");
		causeHighPart.addItem("Control INSS duraci" + String.valueOf("\u00F3") + "n 12 meses", "7");
		causeHighPart.addItem("Recuperaci" + String.valueOf("\u00F3") + "n capacidad profesional", "8");
		causeHighPart.addItem("Incomparecencia contratos de formaci" + String.valueOf("\u00F3") + "n", "9");
		
		applicantTypeList.addItem("Madre biologica", "0");
		applicantTypeList.addItem("Otro progenitor", "1");
		applicantTypeList.addItem("Primer adoptante", "2");
		applicantTypeList.addItem("Segundo adoptante", "3");
	}
	
	private void initRaggedListBox(){
		raggedList.clear();
		raggedList.addItem("NO", "0");
		
		for(IT it : itDialogObject.getITList()) {
			if(null != it.getEndDate() && notSelectedId(it.getId())) {
				String item = parseShortLowCauseByte(it.getTypeLowPart()) + " (" + formatFullDate.format(it.getStartDate()) + " - " + formatFullDate.format(it.getEndDate()) + ")";
				raggedList.addItem(item, it.getId().toString());
			}
		}
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
	
	private void setCauseLowPart() {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 0); // BAJA
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		}
	}
	
	private void setDateHighPart(Date date) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 2); // ALTA
			itPart.setDate(date);
			itPart.setCollegeNumber(collegiateNumberITPart.getValue());
			itPart.setCias(ciasITPart.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {
					itPart.setDate(date);
					itPart.setCollegeNumber(collegiateNumberITPart.getValue());
					itPart.setCias(ciasITPart.getValue());
					
					added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart();
				itPart.setType((byte) 2); // ALTA
				itPart.setDate(date);
				itPart.setCollegeNumber(collegiateNumberITPart.getValue());
				itPart.setCias(ciasITPart.getValue());
				
				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCauseHighPart() {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<ITPart>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 2); // ALTA
			itPart.setCollegeNumber(collegiateNumberITPart.getValue());
			itPart.setCias(ciasITPart.getValue());
			
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
				itPart.setCollegeNumber(collegiateNumberITPart.getValue());
				itPart.setCias(ciasITPart.getValue());
				
				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCollegiateNumberITPart(String collegiateNumberLowPart) {
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
				if(itPart.getType() == (byte) 0 || itPart.getType() == (byte) 2) {
					itPart.setCollegeNumber(collegiateNumberLowPart);
				}
			}
		}
	}
	
	private void setCiasITPart(String ciasLowPart) {
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
				if(itPart.getType() == (byte) 0 || itPart.getType() == (byte) 2) {
					itPart.setCias(ciasLowPart);
				}
			}
		}
	}
	
	private void setDirectPayDate() {
		Date date = itStartDate.getValue();
		if(null != date) {
			date = DateUtils.addDays2Date(date, 365);
			directPayDate.setValue(date);
			this.it.setDirectPayDate(date);
		}
	}

	private void createRealStartDate() {
		Date date = itStartDate.getValue();
		if(null != date) {
			if(null != this.it.getParent() && 0 != this.it.getParent()) {
				Date oldStartDate = itDialogObject.getRaggedDate(this.it.getParent().toString());
				realStartDate.setText(formatFullDate.format(oldStartDate));
				return;
			} else {
			
				if((byte) 1 == Byte.parseByte(causeLowPart.getSelectedValue())) {
					date = DateUtils.addDays2Date(date, 1);
					realStartDate.setText(formatFullDate.format(date));
				} else
					realStartDate.setText(formatFullDate.format(date));
				
			}
		}
	}
	
	private boolean checkIfSaveIsPossible() {
		if(causeLowPart.getSelectedIndex() != 0 && null != itStartDate.getValue())
			return true;
		
		return false;
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
	
	private String parseShortLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "ECC";
			case (byte)1:
				return "ATT";
			case (byte)2:
				return "MAT";
			case (byte)3:
				return "PAT";
			case (byte)4:
				return "REM";
			case (byte)5:
				return "RLA";
			case (byte)6:
				return "ANL";
			case (byte)7:
				return "ECC";
			case (byte)8:
				return "COV";
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
				return "Incomparecencia";
			case (byte)7:
				return "Control INSS duraci" + String.valueOf("\u00F3") + "n 12 meses";
			case (byte)8:
				return "Recuperaci" + String.valueOf("\u00F3") + "n capacidad profesional";
			case (byte)9:
				return "Incomparecencia contratos de formaci" + String.valueOf("\u00F3") + "n";
			default:
				return "-";
		}
	}
	
	private void setTableHeights() {
		itDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 400), Unit.PX);
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
	
	private Byte getDefaultConfirmOrder() {
		int newConfirmOrder = 1;
		
		for(ITPart itPart : this.it.getITParts()) {
			if(itPart.getType() == (byte)0 || itPart.getType() == (byte)2)
				continue;
			
			newConfirmOrder++;
		}
		return (byte) newConfirmOrder;
	}
	
	// --------------------------------------------------------------------------------------------
	// 										INIT ITs TABLE
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
		              return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
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
		              return (o2 != null) ? o1.getTypeLowPart().compareTo(o2.getTypeLowPart()) : 1;
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
		              return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
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
		              return (o2 != null) ? o1.getTypeHighPart().compareTo(o2.getTypeHighPart()) : 1;
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
	    itDataGrid.getColumn(0).setDefaultSortAscending(false);
	    itDataGrid.getColumnSortList().push(itDataGrid.getColumn(0));   
	}
	
	private AonToolbar getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("Baja IT");
		
		listIT = new AonToolbarButton( "Listar ITs", AON.CSS.aonIconList() );
		listIT.setAccessKey('L');
		listIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onListIT(event);
			}
		});
		toolbar.add(listIT);
		
		backListIT = new AonToolbarButton( "Volver a ITs", AON.CSS.aonIconBack() );
		backListIT.setAccessKey('B');
		backListIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onBackListIT(event);
			}
		});
		toolbar.add(backListIT);
		
		newIT = new AonToolbarButton( "Nueva IT", AON.CSS.aonIconAdd() );
		newIT.setAccessKey('N');
		newIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNewIT(event);
			}
		});
		toolbar.add(newIT);
		
		deleteIT = new AonToolbarButton( "Borrar IT", AON.CSS.aonIconDelete() );
		deleteIT.setAccessKey('D');
		deleteIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteIT(event);
			}
		});
		toolbar.add(deleteIT);
		
		showCertificate = new AonToolbarButton( "Certificado IT", AON.CSS.aonIconPdf());
		showCertificate.setAccessKey('C');
		showCertificate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onShowCertitificateIT(event);
			}
		});
		toolbar.add(showCertificate);
		showCertificate.setVisible(false);
		
		backListIT.setVisible(false);
		newIT.setVisible(false);
		deleteIT.setVisible(false);

		return toolbar;

	}
	
	private void onShowCertitificateIT(ClickEvent event) {
		if(null != this.it.getId()) {
			onShowCertitificateIT(it);
			hide();
		}
	}
	
	private void onDeleteIT(ClickEvent event) {
		if(null != this.it.getId()) {
			if(this.it.getIsParent()) {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: Reca"+ String.valueOf("\u00ED") + "da", "No se puede eliminar este parte por que tiene reca" + String.valueOf("\u00ED") + "da.");
			} else {
				AonConfirmDialog confirmDialog = new AonConfirmDialog();
				confirmDialog.confirm(
						"BORRADO", 
						String.valueOf("\u00BF") + "Realmente desea eliminar el parte IT?",
						new AonConfirmDialogCallback() {

							@Override
							public void onAccept() {
								onDelete(it);	
								hide();
								ITDialog.this.hide();
							}

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub
							}});
			}
		}
	}
	
	private void onListIT(ClickEvent event) {
		// Check type of part
		if(this.itDialogObject.getEmployeeStatus()) {
			newIT.setVisible(true);
		} else {
			newIT.setVisible(false);
		}
		
		backListIT.setVisible(true);
		listIT.setVisible(false);
		deleteIT.setVisible(false);
		
		deckPanel.showWidget(1);
		deckPanel.setWidth("620px");
		initITTable();
		setTableHeights();
	}
	
	private void onBackListIT(ClickEvent event) {
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
		
		backListIT.setVisible(false);
		newIT.setVisible(false);
		deleteIT.setVisible(false);
		listIT.setVisible(true);
	}
	
	private void onNewIT(ClickEvent event) {
		clearITPage();
		this.it = new IT();
		this.it.setId(-1);
		hideConfirmationParts();
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
		
		backListIT.setVisible(false);
		newIT.setVisible(false);
		deleteIT.setVisible(false);
		listIT.setVisible(true);
	}
	
	private void getButtonsPanel() {
		buttonsPanel.clear();
		
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		if(checkIfSaveIsPossible()) {
			if(null == this.it.getId() || -1 == this.it.getId())
				itDialogObject.addIT(this.it);
			
			if((null == this.it.getId() || -1 == this.it.getId()) && (this.it.getTypeLowPart() == (byte)2 || (this.it.getTypeLowPart() == (byte)3)))
					comunicateIT(it);
			else {
				onAccept();
				hide();
			}
		} else {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO: Fechas", "La fecha y la causa de baja deben estar rellenadas.");
		}
	}
	
	private void comunicateIT(IT it) {
		onAcceptIT(it);
		hide();
	}

	
}
