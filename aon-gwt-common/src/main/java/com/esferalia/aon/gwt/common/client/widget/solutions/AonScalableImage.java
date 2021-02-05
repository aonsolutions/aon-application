package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AonScalableImage extends SimpleLayoutPanel implements MouseWheelHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, HasLoadHandlers  {

	private static final Logger LOGGER = Logger.getLogger(AonScalableImage.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private Canvas canvas = Canvas.createIfSupported();
	private Context2d context = canvas.getContext2d();

	private Canvas backCanvas = Canvas.createIfSupported();
	private Context2d backContext = backCanvas.getContext2d();


    private int width;
	private int height;
	
	private Image image;
	private ImageElement imageElement; 

	private double zoom = 1;
	private double totalZoom = 1;
	private double offsetX = 0;
	private double offsetY = 0;

	private boolean mouseDown = false;
	private double mouseDownXPos = 0;
	private double mouseDownYPos = 0;

    public AonScalableImage() {
    	setStyleName(AON.CSS.aonWidthAll());
    	addStyleName(AON.CSS.aonHeightAll());
    	canvas.getElement().getStyle().setWidth(100, Unit.PCT);
        canvas.getElement().getStyle().setHeight(100, Unit.PCT);
        canvas.addMouseWheelHandler( this );
        canvas.addMouseDownHandler( this );
        canvas.addMouseMoveHandler( this );
        canvas.addMouseUpHandler( this );
        setWidget(canvas);
    }

	public void setImage(String url) {
		setImage( new Image( url ));
	}

	public void setImage( Image image ) {
		LOGGER.info("setImage");
        this.image = image;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
		    	width = image.getWidth() + 10;
		    	height = image.getHeight() + 10;
		        canvas.setCoordinateSpaceWidth(width);
		        canvas.setCoordinateSpaceHeight(height);
		        backCanvas.setCoordinateSpaceWidth(width);
		        backCanvas.setCoordinateSpaceHeight(height);
				mainDraw();
		}});
	}
	
    public void mainDraw() {
    	this.imageElement = (ImageElement) image.getElement().cast();
        backContext.drawImage(imageElement, 0, 0);
        buffer(backContext, context);
    }

    public void buffer(Context2d back, Context2d front) {
        front.beginPath();
        front.clearRect(0, 0, width, height);
//        front.clearRect(0, 0, front.getCanvas().getWidth(), front.getCanvas().getHeight());
        front.drawImage(back.getCanvas(), 0, 0);
    }
    
    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        int move = event.getDeltaY();
        double xPos = (event.getRelativeX(canvas.getElement()));
        double yPos = (event.getRelativeY(canvas.getElement()));
        if (move < 0) {
            zoom = 1.1;
        } else {
            zoom = 1 / 1.1;
        }
        double newX = (xPos - offsetX) / totalZoom;
        double newY = (yPos - offsetY) / totalZoom;
        double xPosition = (-newX * zoom) + newX;
        double yPosition = (-newY * zoom) + newY;
        backContext.clearRect(0, 0, width, height);
        backContext.translate(xPosition, yPosition);
        backContext.scale(zoom, zoom);
        mainDraw();
        offsetX += (xPosition * totalZoom);
        offsetY += (yPosition * totalZoom);
        totalZoom = totalZoom * zoom;
        buffer(backContext, context);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        this.mouseDown = true;
        mouseDownXPos = event.getRelativeX(image.getElement());
        mouseDownYPos = event.getRelativeY(image.getElement());
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if (mouseDown) {
            backContext.setFillStyle("white");
            backContext.fillRect(-5, -5, width + 5, height + 5);
            backContext.setFillStyle("black");
            double xPos = event.getRelativeX(image.getElement());
            double yPos = event.getRelativeY(image.getElement());
            backContext.translate((xPos - mouseDownXPos) / totalZoom, (yPos - mouseDownYPos) / totalZoom);

            offsetX += (xPos - mouseDownXPos);
            offsetY += (yPos - mouseDownYPos);

            mainDraw();
            mouseDownXPos = xPos;
            mouseDownYPos = yPos;
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        this.mouseDown = false;
    }

	@Override
	public HandlerRegistration addLoadHandler(LoadHandler handler) {
		return image.addLoadHandler(handler);
	}    
}
