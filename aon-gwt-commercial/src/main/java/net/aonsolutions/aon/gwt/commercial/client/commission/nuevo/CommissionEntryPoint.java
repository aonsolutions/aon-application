package net.aonsolutions.aon.gwt.commercial.client.commission.nuevo;

import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

public class CommissionEntryPoint extends AonTemplate2 {

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		load();
	}

	private void load() {
		loadToolbar();
		loadContent();
		loadMenu();
	}

	private void loadToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		AonToolbar toolbar = new AonToolbar("Comisiones");
		setToolbar(toolbar);
	}

	public void loadContent() {
		setContent(new SimplePanel());
	}
	
	private void loadMenu() {
		
		AonMenuItem calculo = new AonMenuItem()
		.setTitle("Calculo de Comisiones")
		.setHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("CALCULO");
			}
		});
		
		AonMenuItem control = new AonMenuItem()
		.setTitle("Control de Comisiones Calculadas")
		.setHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("control");
			}
		});
		AonMenu aonMenu = new AonMenu();
		aonMenu.addItem(calculo);
		aonMenu.addItem(control);
		setWestContent(aonMenu);
	}
}
