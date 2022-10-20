package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class ConsoleRowQuery extends DockLayoutPanel {
	
	public interface AonConsoleRowQueryCallback {
		void onExit();
	}	
	private ConsoleRowQueryFilter filterPanel;
	
	private final DomainParams params;
	private final AonConsoleRowQueryCallback cbk;
	private final SimpleLayoutPanel container;

	ConsoleRowQuery(DomainParams params, AonConsoleRowQueryCallback cbk) {
		super( Unit.PX );
		this.params = params;
		this.cbk = cbk;
		addNorth( getToolbar(), AonToolbar.HEIGTH);
		filterPanel = new ConsoleRowQueryFilter( params );
		
		addWest( filterPanel, ConsoleRowQueryFilter.WIDTH);
		
		container = new SimpleLayoutPanel();
		container.setStyleName(AON.CSS.aonMarginBottom());
		add( container );
		
		filterPanel.addValueChangeHandler( e -> search(e.getValue()) );
	}
	
	private void search(ConsoleTableRow crt) {
		ConsoleModule.CONSOLE_SERVICE.getTableRow(crt ,new AsyncCallback<ConsoleTableRow>() {
			@Override
			public void onFailure(Throwable caught) {
				Label label = new Label( caught.getMessage() );
				label.setStyleName(AON.CSS.aonMargin());
				label.addStyleName(AON.CSS.aonBorder());
				label.addStyleName(AON.CSS.aonBold());
				label.addStyleName(AON.CSS.aonTextCenter());
				container.setWidget( label );
			}

			@Override
			public void onSuccess(ConsoleTableRow tableRow) {
				if (tableRow == null) {
					Label label = new Label("Fila no encontrada");
					label.setStyleName(AON.CSS.aonMargin());
					label.addStyleName(AON.CSS.aonBorder());
					label.addStyleName(AON.CSS.aonBold());
					label.addStyleName(AON.CSS.aonTextCenter());
					container.setWidget( label );
				} else {
					container.setWidget( new ConsoleRowCompositeQueryViewer(tableRow) );
				}
			}

		});

	}
	
	private AonToolbar getToolbar() {
		AonToolbar toolbar = new AonToolbar(params.getDescription());
		AonToolbarButton backButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
		backButton.setText(AON.MSG.backAction());
		backButton.addClickHandler( e -> cbk.onExit());
		toolbar.add( backButton );
		
		return toolbar;
	}
	
}
