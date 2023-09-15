package net.aonsolutions.aon.gwt.communication.client.nuevo;

import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSearchBox;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class CommunicationMain extends AonTemplate2{

	public CommunicationMain() {
		
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		startApplication();
	}
	
	private void startApplication() {
		toolbar();
		westContent();
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 50);
		AonToolbar toolbar = new AonToolbar("Consola de comunicaciones");
		
//		AonToolbarButton newButton = new AonToolbarButton("Nuevo", AON.CSS.aonIconAdd());
//		newButton.addClickHandler(event -> createCarrierPacking());
//		newButton.setVisible(true);
//		toolbar.add(newButton);
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {

			}
		};
		toolbar.showSearchPanel(searchBox);
		setToolbar(toolbar);
	}
	
	private void westContent() {
		getWestContent().getElement().getStyle().setBackgroundColor("white");
		getWestContent().getElement().getStyle().setBorderWidth(0, Unit.PX);

		getDockLayoutPanel().setWidgetSize(getWestContent(), 250);
		
		AonMenu aonMenu = new AonMenu();
		
		AonMenuItem general = new AonMenuItem()
				.setTitle("Visi\u00f3n Global")
				.setHandler(generalHandler());
		aonMenu.addItem(general);
		
		AonMenuItem ingenet = new AonMenuItem()
				.setTitle("INGENET")
				.setOpened(true)
				.addItem(new AonMenuItem().setTitle("Visi\u00f3n global")
						.setHandler(ingenetGeneralHandler()))
				.addItem(new AonMenuItem().setTitle("Env\u00edo de Pedidos")
						.setHandler(ingenetSalesHandler()))
				.addItem(new AonMenuItem().setTitle("Recepci\u00f3n de Albaranes")
						.setHandler(ingenetDeliveryHandler()));
		aonMenu.addItem(ingenet);

		AonMenuItem seres = new AonMenuItem()
				.setTitle("SERES")
				.setOpened(true)
				.addItem(new AonMenuItem().setTitle("Visi\u00f3n global")
						.setHandler(seresGeneralHandler()))
				.addItem(new AonMenuItem().setTitle("Env\u00edo de Albaranes")
						.setHandler(seresSalesHandler()))
				.addItem(new AonMenuItem().setTitle("Env\u00edo de Facturas")
						.setHandler(seresDeliveryHandler()));
		aonMenu.addItem(seres);

		setWestContent(aonMenu);
	}

	private ClickHandler generalHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}
	
	private ClickHandler ingenetGeneralHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}
	
	private ClickHandler ingenetSalesHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}
	
	private ClickHandler ingenetDeliveryHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}
	
	private ClickHandler seresGeneralHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}
	
	private ClickHandler seresSalesHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}
	
	private ClickHandler seresDeliveryHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				
			}
		};
	}

	
	private void content() {
//		setContent(new CommunicationPrincipal(this));
	}



}
