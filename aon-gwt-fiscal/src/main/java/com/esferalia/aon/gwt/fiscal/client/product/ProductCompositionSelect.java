package com.esferalia.aon.gwt.fiscal.client.product;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.product.ProductComposition;
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

public class ProductCompositionSelect extends HTMLPanel {
	
	// ------------------------------------------------- ScheduledCommand (Status)
	
		class CompositionCommand implements ScheduledCommand {
	
			@Override
			public void execute() {
				productComposition = ProductComposition.COMPOSITION;
				addStatusInput();
			}
		}
		
		class DivisibleCommand implements ScheduledCommand {

			@Override
			public void execute() {
				productComposition = ProductComposition.DIVISIBLE;
				addStatusInput();
			}
		}
		
		class StatusContextMenu extends AonContextMenu {

			public StatusContextMenu() {

				addMenuItem(ProductComposition.COMPOSITION.getDescription(), new CompositionCommand(), AON.CSS.aonIconCircleGreen(), "composition");
				addMenuItem(ProductComposition.DIVISIBLE.getDescription(), new DivisibleCommand(), AON.CSS.aonIconCircleRed(), "divisible");
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
		private ProductComposition productComposition;
		
		public ProductCompositionSelect(ProductComposition productComposition) {
			super(EMPTY_STRING);
			addStyleName(AON.CSS.aonItemFlex());
			getElement().getStyle().setProperty("border", "1px solid lightgray");
			getElement().getStyle().setProperty("border-radius", "10px");
			getElement().getStyle().setProperty("padding", "5px");
			getElement().getStyle().setProperty("cursor", "pointer");
			
			this.productComposition = productComposition;

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
			
			addStatusInput();
			
		}

		private void addStatusInput() {
			clear();
			
			HTMLPanel circleStatus = new HTMLPanel(EMPTY_STRING);
			circleStatus.setStyleName(productComposition == ProductComposition.COMPOSITION ? AON.AON_CIRCLE_GREEN : AON.AON_CIRCLE_RED);
			add(circleStatus);
			
			Label status = new Label(productComposition == ProductComposition.COMPOSITION ? ProductComposition.COMPOSITION.getDescription() : ProductComposition.DIVISIBLE.getDescription());
			add(status);
			
			AonToolbarSmallButton arrowDown = new AonToolbarSmallButton("", AON.CSS.aonIconDown());
			add(arrowDown);
			
			fireBlurEvent();
		}

		public ProductComposition getValue() {
			return this.productComposition;
		}
		
		private void fireBlurEvent() {
	        BlurEvent blurEvent = new BlurEvent() {};
	        fireEvent(blurEvent);
	    }

	    public HandlerRegistration addBlurHandler(BlurHandler handler) {
	        return addDomHandler(handler, BlurEvent.getType());
	    }
}
