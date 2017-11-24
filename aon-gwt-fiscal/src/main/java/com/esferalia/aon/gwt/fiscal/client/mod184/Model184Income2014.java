package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Base.IModel184Income;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Base.Model184BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model184Income2014 extends DockLayoutPanel implements IModel184Income {
	
	protected interface IModel184IncomeCallback {
		void onTableChanged( Mod184Income income );
		void onValueChanged( Mod184Income income);
	}
	private Model184IncomeTable table;
	
	public Model184Income2014( Model184BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model184IncomeTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod184Income>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod184Income> event) {
				Model1842014IncomePanel panel = new Model1842014IncomePanel(event.getSelectedItem(), new IModel184IncomeCallback() {
					
					@Override
					public void onValueChanged(Mod184Income income) {
						if (!income.isDirty()) {
							income.setDirty(true);
							table.refresh();		
						}
					}
					
					@Override
					public void onTableChanged(Mod184Income income) {
						income.setDirty(true);
						table.refresh();
					}
				});
				container.setWidget(panel);
				
				Scheduler.get().scheduleDeferred(new Command() {
			        public void execute() {
			        	panel.setFocus(true);
			        }
			    });		

			}
		});
		add(container);
	}

	@Override
	public Integer getSelectedIncomeIndex() {
		return table.getSelectionIndex();
	}
	
}
