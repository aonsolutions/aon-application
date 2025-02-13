package com.esferalia.aon.gwt.fiscal.client.product;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.product.ProductConsole;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class ProductConsoleSelect extends HTMLPanel {
	
	// ------------------------------------------------- ScheduledCommand (Status)
	
		class SelfContractCommand implements ScheduledCommand {

			@Override
			public void execute() {
				productConsole = ProductConsole.SELF_CONTRACT;
				addStatusInput();
			}
		}
		
		class ConcoleCommand implements ScheduledCommand {

			@Override
			public void execute() {
				productConsole = ProductConsole.CONSOLE;
				addStatusInput();
			}
		}
		
		class StatusContextMenu extends AonContextMenu {

			public StatusContextMenu() {

				addMenuItem("Auto-Contrataci\u00f3n", new SelfContractCommand(), AON.CSS.aonIconCircleGreen(), "active");
				addMenuItem("Console", new ConcoleCommand(), AON.CSS.aonIconCircleRed(), "discontinued");
			}
			
			private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
				MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.AON_CMD_BUTTON);
				item.getElement().getStyle().setProperty("padding", ".5rem");
				item.ensureDebugId(debugId);
				return item;
			}

		}

		private static final String EMPTY_STRING = "";
		
		private StatusContextMenu statusContextMenu;
		private ProductConsole productConsole;
		
		public ProductConsoleSelect(ProductConsole productConsole) {
			super(EMPTY_STRING);
			addStyleName(AON.CSS.aonItemFlex());
			getElement().getStyle().setProperty("border", "1px solid lightgray");
			getElement().getStyle().setProperty("border-radius", "10px");
			getElement().getStyle().setProperty("padding", "5px");
			getElement().getStyle().setProperty("cursor", "pointer");
			
			this.productConsole = productConsole;

			statusContextMenu = new StatusContextMenu();
			
			addDomHandler(event -> {
			    NativeEvent nativeEvent = event.getNativeEvent();
			    int clickX = nativeEvent.getClientX();
			    int clickY = nativeEvent.getClientY();

			    // Tamaño del viewport (ventana)
			    int viewportWidth = Window.getClientWidth();
			    int viewportHeight = Window.getClientHeight();

			    // Dimensiones del menú
			    statusContextMenu.show(); // Necesario para calcular dimensiones reales
			    int menuWidth = statusContextMenu.getOffsetWidth();
			    int menuHeight = statusContextMenu.getOffsetHeight();

			    // Ajustar posición X
			    int positionX = clickX;
			    if (clickX + menuWidth > viewportWidth) {
			        positionX = Math.max(0, clickX - menuWidth); // Si no cabe, mover hacia la izquierda
			    }

			    // Ajustar posición Y
			    int positionY = clickY;
			    if (clickY + menuHeight > viewportHeight) {
			        positionY = Math.max(0, clickY - menuHeight); // Si no cabe, mover hacia arriba
			    }

			    // Establecer la posición final del menú
			    statusContextMenu.setPopupPosition(positionX, positionY);
			    statusContextMenu.getElement().getStyle().setProperty("z-index", "2");
			}, ClickEvent.getType());

			
//			addDomHandler(event -> {
//				NativeEvent nativeEvent = event.getNativeEvent();
//				statusContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
//				statusContextMenu.getElement().getStyle().setProperty("z-index", "2");
//				statusContextMenu.show();
//			}, ClickEvent.getType());
			
			addStatusInput();
			
		}

		private void addStatusInput() {
			clear();
			
			HTMLPanel circleStatus = new HTMLPanel(EMPTY_STRING);
			circleStatus.setStyleName(productConsole == ProductConsole.SELF_CONTRACT ? AON.AON_CIRCLE_GREEN : AON.AON_CIRCLE_RED);
			add(circleStatus);
			
			Label status = new Label(productConsole == ProductConsole.SELF_CONTRACT ? "Auto-Contrataci\u00f3n" : "Console");
			add(status);
			
			AonToolbarSmallButton arrowDown = new AonToolbarSmallButton("", AON.CSS.aonIconDown());
			add(arrowDown);
			
			fireBlurEvent();
		}

		public ProductConsole getValue() {
			return this.productConsole;
		}
		
		private void fireBlurEvent() {
	        BlurEvent blurEvent = new BlurEvent() {};
	        fireEvent(blurEvent);
	    }

	    public HandlerRegistration addBlurHandler(BlurHandler handler) {
	        return addDomHandler(handler, BlurEvent.getType());
	    }
}
