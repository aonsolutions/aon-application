package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.css.ViewerResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public abstract class Viewer extends PopupPanel {

	@UiField
	HTML html;
	@UiField
	Label title;
	@UiField
	Button prev;
	@UiField
	Button next;
	@UiField
	Grid menu;
	@UiField
	FocusPanel focusPanel;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	Label load;

	private Timer eraser;

	private String titleStyles[];

	private int zoom = DEFAULT_ZOOM;

	public Viewer() {
		this(DEFAULT_ZOOM);
	}

	public Viewer(Integer zoom) {
		super(true, true);
		this.zoom = zoom;
		GWT.<ViewerResources> create(ViewerResources.class).css().ensureInjected();		
		setWidget(binder.createAndBindUi(this));
		addStyleName("gwt-PopupPanel-viewer");
		eraser = new Timer() {
			@Override
			public void run() {
				hide(menu, next, prev);
			}
		};
		focusPanel.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				Viewer.this.eraser.cancel();
				Viewer.this.show(menu, next, prev);
				if (!isAt(event.getClientX(), event.getClientY(), menu, next,
						prev))
					Viewer.this.eraser.schedule(4000);
			}
		});
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if ( Viewer.this.isShowing()){ 
					Viewer.this.setPopupPosition(Document.get().getScrollLeft(),
							Document.get().getScrollTop());
					Viewer.this.scrollPanel.setHeight(Window.getClientHeight()  + "px");
				}
					
			}
		});

	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
	}

	private static final int DEFAULT_ZOOM = 130;

	interface Binder extends UiBinder<Widget, Viewer> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	protected abstract void onNext();

	protected abstract void onPrev();

	protected abstract void onPrint();

	protected abstract void onShare();

	protected abstract void onChange();

	protected abstract void onDownload();

	protected abstract void onZoomPlus(int zoom);

	protected abstract void onZoomMinus(int zoom);

	@Override
	public void show() {
		unloadScrollBars();
		this.scrollPanel.setHeight(Window.getClientHeight() + "px");
		setPopupPosition(Document.get().getScrollLeft(),
				Document.get().getScrollTop());
		super.show();
	}
	
	@Override
	public void hide() {
		reloadScrollBars();
		super.hide();
	}

	// ------------------------------------------------------------------------

	@UiHandler("close")
	void close(ClickEvent event) {
		hide();
	}

	@UiHandler("download")
	void vdownload(ClickEvent event) {
		onDownload();
	}

	@UiHandler("print")
	void vprint(ClickEvent event) {
		onPrint();
	}

	@UiHandler("share")
	void vshare(ClickEvent event) {
		onShare();
	}

	@UiHandler("prev")
	void vprev(ClickEvent event) {
		resetTitle();
		onPrev();
	}

	@UiHandler("next")
	void vnext(ClickEvent event) {
		resetTitle();
		onNext();
	}

	@UiHandler("zoom_plus")
	void vzoom_plus(ClickEvent event) {
		zoom += 10;
		onZoomPlus(zoom);
	}

	@UiHandler("zoom_minus")
	void vzoom_minus(ClickEvent event) {
		zoom -= 10;
		onZoomMinus(zoom);
	}

	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------

	public void setHTML(String html) {
		this.html.setHTML(html);
	}

	public void setTitle(String title, String... styles) {
		this.title.setText(title);
		for (String style : styles)
			this.title.addStyleName(style);
		titleStyles = styles;
	}

	public void setPrevEnabled(boolean enabled) {
		this.prev.setEnabled(enabled);
	}

	public void setNextEnabled(boolean enabled) {
		this.next.setEnabled(enabled);
	}
	
	public void showLoad() {
		load.setVisible(true);
	}

	public void hideLoad() {
		load.setVisible(false);
	}

	// -----------------------------------------------------------------------

	private void resetTitle() {
		this.title.setText("");
		for (String style : titleStyles)
			this.title.addStyleName(style);
		titleStyles = new String[0];
	}

	private void show(final UIObject... uiObjects) {

		for (UIObject uiObject : uiObjects) {
			uiObject.getElement().getStyle().setOpacity(0.8);
			if (uiObject instanceof HasEnabled)
				uiObject.setVisible(((HasEnabled) uiObject).isEnabled());
			else
				uiObject.setVisible(true);
		}

	}

	private void hide(final UIObject... uiObjects) {

		new Timer() {
			double opacity = 0.8;

			@Override
			public void run() {
				opacity -= 0.8 / 10;
				for (UIObject uiObject : uiObjects)
					uiObject.getElement().getStyle().setOpacity(opacity);

				if (opacity > 0.00) {
					schedule(1000 / 10);
				} else {
					for (UIObject uiObject : uiObjects)
						uiObject.setVisible(false);
					cancel();
				}
			}
		}.schedule(1000 / 10);

	}

	private boolean isAt(int x, int y, UIObject... uiObjects) {
		for (UIObject uiObject : uiObjects)
			if (isAt(x, y, uiObject))
				return true;
		return false;
	}

	private boolean isAt(int x, int y, UIObject uiObject) {
		if (x < uiObject.getAbsoluteLeft())
			return false;
		if (y < uiObject.getAbsoluteTop())
			return false;

		if (x > (uiObject.getAbsoluteLeft() + uiObject.getOffsetWidth()))
			return false;

		if (y > (uiObject.getAbsoluteTop() + uiObject.getOffsetHeight()))
			return false;

		return true;
	}
	// ------------------------------------------------------------------------
	
	private static void reloadScrollBars() {
		Document.get().getDocumentElement().getStyle().setOverflow(Overflow.AUTO);
		Document.get().getBody().setPropertyString("scroll", "yes");
	}

	private static void unloadScrollBars() {
		Document.get().getDocumentElement().getStyle().setOverflow(Overflow.HIDDEN);
		Document.get().getBody().setPropertyString("scroll", "no");
	}


	

}
