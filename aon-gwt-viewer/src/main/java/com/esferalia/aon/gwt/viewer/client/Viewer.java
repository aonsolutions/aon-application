package com.esferalia.aon.gwt.viewer.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.css.ViewerResources;
import com.esferalia.aon.gwt.viewer.shared.Icon;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.type.MimeType;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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
	public void removeOldIcon(String old){
		this.title.removeStyleName(old);
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
	
	public static Viewer getViewer(String domainName, Integer domainId, String driveId, MimeType mimetype){
		Domain domain = new Domain().setName(domainName).setId(domainId);
		Attach attach = new Attach()
				.setDomain(domain)
				.setDriveId(driveId)
				.setDescription("Data")
				.setIcon(Icon.icon(mimetype.getName()))
				.setMimeType(mimetype);
		
		LinkedList<Attach> attachList = new LinkedList<Attach>();
		attachList.add(attach);
		return getViewer(attach, 0, attachList);
	}
	
	public static Viewer getViewer(String domainName, Integer domainId, byte[] data, MimeType mimetype){
		Domain domain = new Domain().setName(domainName).setId(domainId);
		Attach attach = new Attach()
				.setDomain(domain)
				.setData(data)
				.setDescription("Data")
				.setIcon(Icon.icon(mimetype.getName()));
		LinkedList<Attach> attachList = new LinkedList<Attach>();
		attachList.add(attach);
		return getViewer(attach, 0, attachList);
	}

	public static Viewer getViewer(final Attach attach){
		LinkedList<Attach> attachList = new LinkedList<Attach>();
		attachList.add(attach);
		return getViewer(attach,0, attachList);
	}
	
	public static Viewer getViewer(final Attach attach, final Integer index, final LinkedList<Attach> attachList) {
		
		final IViewerAsync VIEWER_IMPL = GWT.create(IViewer.class);
		final Viewer viewer = new Viewer(DEFAULT_ZOOM) {
			int viewerIndex = index;
			Attach viewerAttach = attach;
			String icon;
			@Override
			protected void onZoomPlus(int zoom) {
				showLoad();
				VIEWER_IMPL.getAsHTML( viewerAttach, zoom , new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						hideLoad();
						setHTML(result);	
					}
					@Override
					public void onFailure(Throwable caught) {
						print(caught.getMessage());
						hideLoad();
					}
				});
			}
			
			@Override
			protected void onZoomMinus(int zoom) {
				showLoad();
				VIEWER_IMPL.getAsHTML(viewerAttach, zoom , new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						hideLoad();
						setHTML(result);	
					}
					@Override
					public void onFailure(Throwable caught) {
						print(caught.getMessage());
						hideLoad();
					}
				});
			}
			
			@Override
			protected void onShare() {
				SharePanel sp = new SharePanel(viewerAttach) {
					@Override
					protected void onAccept() {
						if(isDrive){
							TextBox tb = (TextBox) content.getWidget(0, 1);
							hide();
							VIEWER_IMPL.share(tb.getText(), viewerAttach, new AsyncCallback<Void>() {
								@Override public void onSuccess(Void result) {}
								@Override public void onFailure(Throwable caught) {
									print(caught.getMessage());
								}
							});
						} else if(isGmail){
							TextBox tb1 = (TextBox) content.getWidget(0, 1);
							TextBox tb2 = (TextBox) content.getWidget(1, 1);
							TextArea ta = (TextArea) content.getWidget(2, 1);
							hide();
							VIEWER_IMPL.sendGmail(tb1.getText(), tb2.getText(), ta.getText(), viewerAttach, new AsyncCallback<Void>() {
								@Override public void onSuccess(Void result) {}
								@Override public void onFailure(Throwable caught) {
									print(caught.getMessage());
								}
							});						
						} else if(isEmail){
							ListBox lb = (ListBox) content.getWidget(0, 1);
							TextBox tb1 = (TextBox) content.getWidget(1, 1);
							TextBox tb2 = (TextBox) content.getWidget(2, 1);
							TextArea ta = (TextArea) content.getWidget(3, 1);
							hide();
							VIEWER_IMPL.sendEmail(attach.getDomain(), lb.getSelectedValue(), tb1.getText(), tb2.getText(), ta.getText(), viewerAttach, new AsyncCallback<Void>() {
								@Override public void onSuccess(Void result) {}
								@Override public void onFailure(Throwable caught) {
									print(caught.getMessage());
								}
							});	
						}
					}

					@Override
					protected void onCancel() {
						hide();
					}};
				sp.show();
			}
			
			@Override
			protected void onPrint() {
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_print/"
						+ "?drive_id=" + viewerAttach.getDriveId()
						+ "&attach_id=" + viewerAttach.getId()
						+ "&attach_type=" + viewerAttach.getAttachType().value()
						+ "&attach_name=" + viewerAttach.getDescription()
						+ "&mimetype=" + viewerAttach.getMimeType().value()
						+ "&domain_name=" + viewerAttach.getDomain().getName()
						+ "&domain_id=" + viewerAttach.getDomain().getId();
				PrintWindow.open(fileDownloadURL, "_blank", null);
			}
			
			@Override
			protected void onPrev() {
				icon = Icon.icon(viewerAttach.getMimeType().getName());
				viewerAttach = attachList.get(--viewerIndex);
				setNextEnabled(true);
				setPrevEnabled(viewerIndex > 0);
				onChange();				
			}
			
			@Override
			protected void onNext() {
				icon = Icon.icon(viewerAttach.getMimeType().getName());
				viewerAttach = attachList.get(++viewerIndex);
				setPrevEnabled(true);
				setNextEnabled(viewerIndex < (attachList.size() - 1));
				onChange();				
			}
			
			@Override
			protected void onDownload() {
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_viewer/"
						+ "?drive_id=" + viewerAttach.getDriveId()
						+ "&attach_id=" + viewerAttach.getId()
						+ "&attach_type=" + viewerAttach.getAttachType().value()
						+ "&attach_name=" + viewerAttach.getDescription()
						+ "&mimetype=" + viewerAttach.getMimeType().value()
						+ "&domain_name=" + viewerAttach.getDomain().getName()
						+ "&domain_id=" + viewerAttach.getDomain().getId();
				Window.open(fileDownloadURL, "_blank", null);
			}
			
			@Override
			protected void onChange() {
				showLoad();
				removeOldIcon(icon);
				setTitle(viewerAttach.getDescription(), viewerAttach.getIcon());
				VIEWER_IMPL.getAsHTML(viewerAttach, DEFAULT_ZOOM , new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						hideLoad();
						setHTML(result);
						show();
					}
					@Override
					public void onFailure(Throwable caught) {
						print(caught.getMessage());
						hideLoad();
					}
				});				
			}
		};
		viewer.setTitle(attach.getDescription(), Icon.icon(attach.getMimeType().getName()));
		viewer.setPrevEnabled(index > 0);
		viewer.setNextEnabled(index < (attachList.size() -1 ));
		viewer.show();
		viewer.showLoad();
		
		VIEWER_IMPL.getAsHTML(attach, DEFAULT_ZOOM , new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				viewer.hideLoad();
				viewer.setHTML(result);	
			}
			@Override
			public void onFailure(Throwable caught) {
				print(caught.getMessage());
				viewer.hideLoad();
			}
		});
		return viewer;
	}
	
	private static void print(String msg) {
		final IViewerAsync VIEWER_IMPL = GWT.create(IViewer.class);
		VIEWER_IMPL.print(msg, new AsyncCallback<Void>() {
			@Override public void onSuccess(Void result) {}
			@Override public void onFailure(Throwable caught) {}
		});
	}
}
