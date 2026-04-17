package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AmortizationPanel extends AonLayoutPanel {
	private static final int FORM_IDX = 1;
	
	static interface AmortizationPanelCallback {
		Amortization getAmortization();
		void showError(String message);
		AonToolbarButton paintSaveButton( );
		AonToolbarButton paintDeleteButton( );
	}
	
	private DeckLayoutPanel deckPanel = new DeckLayoutPanel();
	private SimpleLayoutPanel tablePanel = new SimpleLayoutPanel();
	private SimpleLayoutPanel formPanel = new SimpleLayoutPanel();
	
	private final AonToolbar toolbar = new AonToolbar(AON.MSG.amortizationModule()); 
	private final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
	private final AonToolbarButton backButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconCancel());
	AonToolbarButton saveButton;
	AonToolbarButton deleteButton;

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
			
		});
	}

	private void save(AmortizationModuleOptions opts , Amortization amortization) {
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
			
		});
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
				saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
				saveButton.addClickHandler(e -> save( opts , amortization));
				toolbar.add(saveButton);
				return saveButton;
			}

			@Override
			public AonToolbarButton paintDeleteButton() {
				deleteButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				deleteButton.addClickHandler(e -> delete( opts , amortization));
				toolbar.add(deleteButton);
				return deleteButton;
			}
			
		};
		AmortizationFormPanel amortizationFormPanel = new AmortizationFormPanel(opts, callback);
		formPanel.setWidget(amortizationFormPanel);
		show(formPanel);
	}
	
}
