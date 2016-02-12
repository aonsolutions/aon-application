package com.esferalia.aon.gwt.viewer.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.css.ViewerResources;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class SharePanel extends PopupPanel {

	@UiField FocusPanel focusPanel;
	@UiField ScrollPanel scrollPanel;
	
	@UiField Button driveButton;
	@UiField Button gmailButton;
	@UiField Button emailButton;
	
	@UiField FlexTable content;

	@UiField Button acceptButton;
	@UiField Button cancelButton;
	
	Boolean isDrive;
	Boolean isGmail;
	Boolean isEmail;
		
	public SharePanel(final Attach attach) {
		super(true, true);

		GWT.<ViewerResources> create(ViewerResources.class).css().ensureInjected();		
		setWidget(binder.createAndBindUi(this));
		addStyleName("gwt-PopupPanel-viewer");
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if ( SharePanel.this.isShowing()){ 
					SharePanel.this.setPopupPosition(Document.get().getScrollLeft(),
							Document.get().getScrollTop());
					SharePanel.this.scrollPanel.setHeight(Window.getClientHeight()  + "px");
				}	
			}
		});


		if(attach.getDriveId() != null){
			driveButton.setVisible(true);
			isDrive = true; isGmail = false; isEmail = false;
			driveButton.addStyleName("aon-dataTable-footerClass");
			gmailButton.removeStyleName("aon-dataTable-footerClass");
			emailButton.removeStyleName("aon-dataTable-footerClass");
			paintDrivePanel(attach);
		} else{
			isDrive = false; isGmail = false; isEmail = true;
			driveButton.setVisible(false);
			driveButton.removeStyleName("aon-dataTable-footerClass");
			gmailButton.removeStyleName("aon-dataTable-footerClass");
			emailButton.addStyleName("aon-dataTable-footerClass");
			content.setWidget(0, 0, new Label("EMAIL"));
			paintEmailPanel(attach);
		}
		
		driveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isDrive = true; isGmail = false; isEmail = false;
				driveButton.addStyleName("aon-dataTable-footerClass");
				gmailButton.removeStyleName("aon-dataTable-footerClass");
				emailButton.removeStyleName("aon-dataTable-footerClass");
				paintDrivePanel(attach);
			}
		});
		
		gmailButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isDrive = false; isGmail = true; isEmail = false;
				driveButton.removeStyleName("aon-dataTable-footerClass");
				gmailButton.addStyleName("aon-dataTable-footerClass");
				emailButton.removeStyleName("aon-dataTable-footerClass");
				paintGmailPanel(attach);
			}
		});
		
		emailButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isDrive = false; isGmail = false; isEmail = true;
				driveButton.removeStyleName("aon-dataTable-footerClass");
				gmailButton.removeStyleName("aon-dataTable-footerClass");
				emailButton.addStyleName("aon-dataTable-footerClass");
				content.setWidget(0, 0, new Label("EMAIL"));
				paintEmailPanel(attach);
			}
		});

		acceptButton.setText("Compartir");
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
			}
		});

		cancelButton.setText("Cancelar");
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
	}

	interface Binder extends UiBinder<Widget, SharePanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

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
	
	private void paintDrivePanel(Attach attach) {
		content.removeAllRows();
		
		Label label = new Label("Email");
		label.addStyleName("aon-bold");
		TextBox textBox = new TextBox();
		textBox.setStyleName("aon-inputText");
		textBox.setWidth("300px");
		content.setWidget(0, 0, label);
		content.setWidget(0, 1, textBox);
	}
	
	private void paintGmailPanel(Attach attach) {
		content.removeAllRows();

		Label l1 = new Label("Para");
		l1.addStyleName("aon-bold");
		TextBox tb1 = new TextBox();
		tb1.setStyleName("aon-inputText");
		tb1.setWidth("300px");
		content.setWidget(0, 0, l1);
		content.setWidget(0, 1, tb1);
		
		Label l2 = new Label("Asunto");
		l2.addStyleName("aon-bold");
		TextBox tb2 = new TextBox();
		tb2.setStyleName("aon-inputText");
		tb2.setWidth("300px");
		content.setWidget(1, 0, l2);
		content.setWidget(1, 1, tb2);
		
		Label l3 = new Label("Mensaje");
		l3.addStyleName("aon-bold");
		TextArea ta = new TextArea();
		ta.setStyleName("aon-inputText");
		ta.setWidth("300px");
		content.setWidget(2, 0, l3);
		content.setWidget(2, 1, ta);
	}
	
	private void paintEmailPanel(Attach attach) {
		final IViewerAsync VIEWER_IMPL = GWT.create(IViewer.class);
		content.removeAllRows();
		
		VIEWER_IMPL.getMailAccountList(attach.getDomain(), new AsyncCallback<LinkedList<MailAccount>>() {
			
			@Override
			public void onSuccess(LinkedList<MailAccount> result) {
				Label l1 = new Label("De");
				l1.addStyleName("aon-bold");
				ListBox lb = new ListBox();
				for (MailAccount mailAccount : result) {
					lb.addItem(mailAccount.getName(), mailAccount.getId().toString());
				}
				lb.addStyleName("aon-inputText");
				content.setWidget(0, 0, l1);
				content.setWidget(0, 1, lb);
				
				Label l2 = new Label("Para");
				l2.addStyleName("aon-bold");
				TextBox tb1 = new TextBox();
				tb1.setStyleName("aon-inputText");
				tb1.setWidth("300px");
				content.setWidget(1, 0, l2);
				content.setWidget(1, 1, tb1);
				
				Label l3 = new Label("Asunto");
				l3.addStyleName("aon-bold");
				TextBox tb2 = new TextBox();
				tb2.setStyleName("aon-inputText");
				tb2.setWidth("300px");
				content.setWidget(2, 0, l3);
				content.setWidget(2, 1, tb2);
				
				Label l4 = new Label("Mensaje");
				l4.addStyleName("aon-bold");
				TextArea ta = new TextArea();
				ta.setStyleName("aon-inputText");
				ta.setWidth("300px");
				content.setWidget(3, 0, l4);
				content.setWidget(3, 1, ta);				
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				String head = "com.esferalia.aon.gwt.viewer.client.SharePanel"
						+ " - paintEmailPanel(Attach attach)"
						+ " - getMailAccountList";
				print(head, caught.getMessage());
			}
		});
	}
	
	private static void reloadScrollBars() {
		Document.get().getDocumentElement().getStyle().setOverflow(Overflow.AUTO);
		Document.get().getBody().setPropertyString("scroll", "yes");
	}

	private static void unloadScrollBars() {
		Document.get().getDocumentElement().getStyle().setOverflow(Overflow.HIDDEN);
		Document.get().getBody().setPropertyString("scroll", "no");
	}
	
	private static void print(String head, String msg) {
		final IViewerAsync VIEWER_IMPL = GWT.create(IViewer.class);
		VIEWER_IMPL.print(head, msg, new AsyncCallback<Void>() {
			@Override public void onSuccess(Void result) {}
			@Override public void onFailure(Throwable caught) {}
		});
	}
}
