package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AmortizationPanel extends AonLayoutPanel {
	private static final int FORM_IDX = 1;
	
	static interface AmortizationPanelCallback {
		Amortization getAmortization();
		void showError(String message);
		AonToolbarButton paintSaveButton( );
		AonToolbarButton paintDeleteButton( );
		AonToolbarButton paintCalculateButton( );
		AonToolbarButton paintSaleButton( );
		
	}
	
	private DeckLayoutPanel deckPanel = new DeckLayoutPanel();
	private SimpleLayoutPanel tablePanel = new SimpleLayoutPanel();
	private SimpleLayoutPanel formPanel = new SimpleLayoutPanel();
	
	private final AonToolbar toolbar = new AonToolbar(AON.MSG.amortizationModule()); 
	private final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
	private final AonToolbarButton backButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconBack());

	private AonToolbarButton saveButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton calculateButton;
	private AonToolbarButton saleButton;

	public AmortizationPanel( AmortizationModuleOptions opts ) {
		super(Unit.PX);
		AON.ensureInjected();
		
		backButton.addClickHandler(e -> showTable(opts));
		toolbar.add(backButton);
		
		resetButton.addClickHandler(e -> reset(opts));
		toolbar.add(resetButton);
		
		this.addNorth(toolbar, AonToolbar.HEIGTH);
	
		deckPanel.setStyleName(AON.CSS.aonSelector());
		this.add(deckPanel);
		
		// Table Panel
		tablePanel.setStyleName(AON.CSS.aonSelector());
		deckPanel.add(tablePanel);
		
		// Form Panel
		formPanel.setStyleName(AON.CSS.aonSelector());
		deckPanel.add(formPanel);

		showTable( opts );
		
	}
	
	private void reset(AmortizationModuleOptions opts) {
		Amortization newAmortization = new Amortization()
			.setDomain(opts.getDomain());
		showForm(opts, newAmortization);
	}

	private void delete(AmortizationModuleOptions opts, Amortization amortization) {
		AonConfirmDialog.showConfirm(AON.MSG.confirmDeleteAction(), () -> 
			AmortizationModule.SERVICE.delete( opts.getOccam(), amortization, new AsyncCallback<Void>() {
	
				@Override
				public void onFailure(Throwable ex) {
					toolbar.showErrorMessage( ex.getMessage() );
				}
	
				@Override
				public void onSuccess(Void v) {
					toolbar.showInfoMessage( AON.MSG.saveSuccess(), AonToolbar.DEFAULT_DELAY );
					showTable(opts);
				}
			})
		);
	}

	private void save(AmortizationModuleOptions opts , Amortization amortization) {
		AonConfirmDialog.showConfirm(AON.MSG.confirmSaveAction(), () ->
			AmortizationModule.SERVICE.save( opts.getOccam(), amortization, new AsyncCallback<Amortization>() {
	
				@Override
				public void onFailure(Throwable ex) {
					toolbar.showErrorMessage( ex.getMessage() );
				}
	
				@Override
				public void onSuccess(Amortization saved) {
					toolbar.showInfoMessage( AON.MSG.saveSuccess(), AonToolbar.DEFAULT_DELAY );
					showForm(opts, saved);
				}
				
			})
		);	
	}

	private void sale(AmortizationModuleOptions opts , Amortization amortization) {
		AonCustomPopup saleDialog = new AonCustomPopup( true );
		saleDialog.addStyleName(AON.CSS.aonPaddingTop());
		saleDialog.setCaption(AON.MSG.saleAmortizaton());
		saleDialog.setAnimationEnabled(true);
		saleDialog.setGlassEnabled(true);
		saleDialog.setModal(true);
		
		FlowPanel content = new FlowPanel();
		content.setStyleName(AON.CSS.aonWidthAll());
		saleDialog.add(content);
		
		AonDisplayTable saleTab = new AonDisplayTable();
		saleTab.addStyleName(AON.CSS.aonMarginTop());
		saleTab.addStyleName(AON.CSS.aonBlockCenter());

		AonDateBox saleDateBox = new AonDateBox();
		saleDateBox.setValue(amortization.getDeadline() );
		saleDateBox.addValueChangeHandler(e -> amortization.setDeadline( saleDateBox.getValue()));
		
		AonDoubleBox saleAmountBox = new AonDoubleBox();
		saleAmountBox.setValue(amortization.getSaleAmount() );
		saleAmountBox.addValueChangeHandler(e -> amortization.setSaleAmount(saleAmountBox.getValue()));
		
		saleTab
			.addLabelWidgetRow( AON.MSG.saleDate(), saleDateBox )
			.addLabelWidgetRow( AON.MSG.saleAmount(), saleAmountBox );
		content.add(saleTab);
		
		AonDisplayTable buttonsPanel = new AonDisplayTable();
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonBlockCenter());
		
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			AmortizationModule.SERVICE.sale( opts.getOccam(), amortization, new AsyncCallback<Amortization>() {

				@Override
				public void onFailure(Throwable ex) {
					toolbar.showErrorMessage( ex.getMessage() );
					saleDialog.hide();
				}

				@Override
				public void onSuccess(Amortization saved) {
					toolbar.showInfoMessage( AON.MSG.saveSuccess(), AonToolbar.DEFAULT_DELAY );
					saleDialog.hide();
					showForm(opts, saved);
				}
				
			});
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			saleDialog.hide();
		});
		buttonsPanel.add(cancelButton);
		
		content.add(buttonsPanel);
		
		saleDialog.center();
		saleDialog.show();
	}
	
	private void calculate(AmortizationModuleOptions opts , Amortization amortization) {
		AonConfirmDialog.showConfirm(AON.MSG.confirmAmortizationCalculateAction(), () -> 
			AmortizationModule.SERVICE.calculate( opts.getOccam(), amortization, new AsyncCallback<Amortization>() {
	
				@Override
				public void onFailure(Throwable ex) {
					toolbar.showErrorMessage( ex.getMessage() );
				}
	
				@Override
				public void onSuccess(Amortization saved) {
					toolbar.showInfoMessage( AON.MSG.saveSuccess(), AonToolbar.DEFAULT_DELAY );
					showForm(opts, saved);
				}
				
			})
		);
	}

	private AmortizationParams getParams(AmortizationModuleOptions opts) {
		return new AmortizationParams().setDomain(opts.getDomain());
	}

	private void manageButtons() {
		boolean show = deckPanel.getVisibleWidgetIndex() == FORM_IDX;
		backButton.setVisible(show);
	}

	private void show(Widget widget) {
		deckPanel.showWidget(widget);
		manageButtons();
	}
	
	private void showTable(AmortizationModuleOptions opts) {
		if (saveButton != null) saveButton.removeFromParent();
		if (deleteButton != null) deleteButton.removeFromParent();
		if (calculateButton != null) calculateButton.removeFromParent();
		if (saleButton != null) saleButton.removeFromParent();
		
		AmortizationParams params = getParams(opts);
		tablePanel.clear();
		AmortizationTable amortizationTable = new AmortizationTable(opts, params);
		amortizationTable.addSelectionHandler(e -> searchAndShowForm(opts, e.getSelectedItem()));
		tablePanel.setWidget(amortizationTable);
		show(tablePanel);
	}
	
	private void searchAndShowForm(AmortizationModuleOptions opts, Amortization amortization) {
		Integer id = amortization == null ? null : amortization.getId();
		AmortizationModule.SERVICE.get( opts.getOccam(), opts.getDomain(), id, new AsyncCallback<Amortization>() {

			@Override
			public void onFailure(Throwable ex) {
				toolbar.showErrorMessage( ex.getMessage() );
			}

			@Override
			public void onSuccess(Amortization am) {
				showForm(opts, am);
			}
		});
	}
	
	private void showForm(AmortizationModuleOptions opts, Amortization amortization) {
		formPanel.clear();
		AmortizationPanelCallback callback = new AmortizationPanelCallback() {
			@Override
			public void showError(String message) {
				toolbar.showErrorMessage(message);
			}

			@Override
			public Amortization getAmortization() {
				return amortization;
			}

			@Override
			public AonToolbarButton paintSaveButton() {
				if (saveButton != null) saveButton.removeFromParent();
				saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
				saveButton.addClickHandler(e -> save( opts , amortization));
				toolbar.add(saveButton);
				return saveButton;
			}

			@Override
			public AonToolbarButton paintDeleteButton() {
				if (deleteButton != null) deleteButton.removeFromParent();
				deleteButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				deleteButton.addClickHandler(e -> delete( opts , amortization));
				toolbar.add(deleteButton);
				return deleteButton;
			}
			
			@Override
			public AonToolbarButton paintCalculateButton() {
				if (calculateButton != null) calculateButton.removeFromParent();
				calculateButton = new AonToolbarButton(AON.MSG.calculateAction(), AON.CSS.aonIconCalc());
				calculateButton.addClickHandler(e -> calculate( opts , amortization));
				toolbar.add(calculateButton);
				return calculateButton;
			}
			
			@Override
			public AonToolbarButton paintSaleButton() {
				if (saleButton != null) saleButton.removeFromParent();
				saleButton = new AonToolbarButton(AON.MSG.saleAmortizaton(), AON.CSS.aonIconEuro());
				saleButton.addClickHandler(e -> sale( opts , amortization));
				toolbar.add(saleButton);
				return saleButton;
			}
			
		};
		AmortizationFormPanel amortizationFormPanel = new AmortizationFormPanel(opts, callback);
		formPanel.setWidget(amortizationFormPanel);
		show(formPanel);
	}
	
}
