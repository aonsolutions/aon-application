package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public abstract class PaymentsCleanDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface PaymentsCleanDialogUIBinder extends UiBinder<Widget, PaymentsCleanDialog> {}

	private static final PaymentsCleanDialogUIBinder binder = GWT.create(PaymentsCleanDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexColumn();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	DataGrid<Payment> paymentsDG;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	// Button
	private Button acceptBtnDialog;
	
	private MultiSelectionModel<Payment> selectionModel;
	private List<Payment> paymentsCleanProvider;
	
	// ------------------------------------------------- Constructor
	
	protected PaymentsCleanDialog(Set<Payment> paymentsIn) {
		
		providePaymentDataGrid();
		
		setCaption("Gesti\u00f3n de devengos");
		
		setWidget(binder.createAndBindUi(this));
		
		this.paymentsCleanProvider = new ArrayList<>();
		paymentsCleanProvider.addAll(paymentsIn);
		
		// Buttons footer
		getButtonsPanel();
		
		// Show close dialog button
		this.showCloseButton(true);
		
		setGridHeight();
		initAgreementsTable();
		
		showDialog();
	}
	
	private void setGridHeight() {
		this.paymentsDG.setHeight("350px");
	}
	
	// ------------------------------------------ Provied DataGrid

	private void providePaymentDataGrid() {
		paymentsCleanProvider = Collections.emptyList();
		
		// Resource Style CellTable
		paymentsDG = new CustomDataGrid<>(Integer.MAX_VALUE, Payment.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		paymentsDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		paymentsDG.setEmptyTableWidget(new Label(("No existen devengos").toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionModel = new MultiSelectionModel<>(Payment.KEY_PROVIDER);
	    paymentsDG.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<Payment> createCheckboxManager());
	    
	    // Initialize the columns.
	    addColumns(this.selectionModel);
	    
	    new ListDataProvider<Payment>(Collections.emptyList()).addDataDisplay(paymentsDG);
	    
	}
	
	private void addColumns(MultiSelectionModel<Payment> selectionModel) {
		
//		selectionModel.addSelectionChangeHandler(e -> acceptBtnDialog.setVisible(!selectionModel.getSelectedSet().isEmpty()));
	    
		Column<Payment, Boolean> checkColumn = new Column<Payment, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Payment agreement) {
				return selectionModel.isSelected(agreement);
			}
	    };
    
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true);
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB) {
	    	@Override
	    	public Boolean getValue() {
	    		if(null == paymentsCleanProvider) return false;
	    		return selectionModel.getSelectedSet().size() == paymentsCleanProvider.size();
	    	}
	    	
	    };
	    
	    selectAllHeader.setUpdater(value -> paymentsCleanProvider.forEach(payment -> selectionModel.setSelected(payment, value)));
	    
	    paymentsDG.addColumn(checkColumn, selectAllHeader);
	    paymentsDG.setColumnWidth(checkColumn, 5, Unit.PCT);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    
	    // Payment type column.
	    TextColumn<Payment> paymentTypeColumn = new TextColumn<Payment>() {
	    	@Override
	    	public String getValue(Payment payment) {
	    		return null == payment.getType() ? "" : AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0');
	    	}
	    };

	    paymentTypeColumn.setSortable(true);
	    paymentsDG.setColumnWidth(paymentTypeColumn, 10, Unit.PCT);
	    
	    // Payment description column.
	    TextColumn<Payment> paymentDescriptionColumn = new TextColumn<Payment>() {
	    	@Override
	    	public String getValue(Payment payment) {
	    		return payment.getDescription();
	    	}
	    };

	    paymentDescriptionColumn.setSortable(true);
	    paymentsDG.setColumnWidth(paymentDescriptionColumn, 40, Unit.PCT);
	    
	    // Payment description column.
	    TextColumn<Payment> paymentExpressionColumn = new TextColumn<Payment>() {
	    	@Override
	    	public String getValue(Payment payment) {
	    		return getParsedExpression(payment.getExpression());
	    	}
	    };

	    paymentsDG.setColumnWidth(paymentExpressionColumn, 45, Unit.PCT);
		
	    // Add the columns.
	    paymentsDG.addColumn(paymentTypeColumn, "CRA");
	    paymentsDG.addColumn(paymentDescriptionColumn, "Descripci\u00f3n");
	    paymentsDG.addColumn(paymentExpressionColumn, "Expresi\u00f3n");   
	}
	
	private String getParsedExpression(String expression) {
		expression = AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
		return SpecialExpresion.parse(expression).getInput();
	}

	// ------------------------------------------ Init SalaryDG
	
	public void initAgreementsTable() {		
		//Reset Selection Model 
		selectionModel.clear();
		
		// Create a data provider.
	    ListDataProvider<Payment> dataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(paymentsDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<Payment> paymentList = dataProvider.getList();
	    paymentList.clear();
	    
	    for (Payment payment : this.paymentsCleanProvider) {
	    	paymentList.add(payment);
	    }   
		
		addSortColums(paymentList);
	    
		// Set page size
	    paymentsDG.setPageSize(paymentsCleanProvider.size());
	    
	    // Set all selected by default
	    paymentsCleanProvider.forEach(payment -> selectionModel.setSelected(payment, true));
	}

	private void addSortColums(List<Payment> paymentsList) {
		ListHandler<Payment> columnSortHandler = new ListHandler<>(paymentsList);
		
	    columnSortHandler.setComparator(paymentsDG.getColumn(1),
	    		(o1, o2) -> compareString(o1, o2, 
	    				null == o1.getType() ? "" : AonStringUtils.leftPad(o1.getType().getCode() + "", 4, '0'), 
	    				null == o2.getType() ? "" : AonStringUtils.leftPad(o2.getType().getCode() + "", 4, '0')));
	    
	    columnSortHandler.setComparator(paymentsDG.getColumn(2),
	    		(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    paymentsDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    paymentsDG.getColumn(1).setDefaultSortAscending(false);
	    paymentsDG.getColumnSortList().push(paymentsDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Borrar y actualizar");
		acceptBtnDialog.addClickHandler(e -> accept());
		
		buttonsPanel.add(acceptBtnDialog);
	}

	private void accept() {
		List<Integer> paymentIds = new ArrayList<>();
		this.selectionModel.getSelectedSet().forEach(payment -> paymentIds.add(payment.getId()));
		
		if(paymentIds.isEmpty()) {
			hide();
			onAccept();
		} else {
			showLoading("Borrando devengos seleccionados...");
			
			impl.deletePayments(paymentIds, new AsyncCallback<Void>() {
	
				@Override
				public void onFailure(Throwable caught) {
					showError("Borrado devengos", caught.getMessage());
				}
	
				@Override
				public void onSuccess(Void result) {
					showSuccess("Borrado devengos", "Los devengos han sido elimiandos correctamente");
					Timer timer = new Timer() {
						@Override
						public void run() {
							hide();
							onAccept();
						}
					};
					timer.schedule(1500);
				}
				
			});
		}
	}
	
	// ------------------------------------------------- Abstract methods
	
	public abstract void onAccept();
	
	// ------------------------------------------------- Aon Messages panel
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
}
