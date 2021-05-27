package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeArea;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Pos;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FileEditor extends ResizeComposite {

	interface Binder extends UiBinder<Widget, FileEditor> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Label titleLabel;

	@UiField
	Button saveButton;

	@UiField
	CodeArea codeArea;

	@UiField
	MenuItem saveMenuItem;
	
	@UiField
	FlowPanel barsFlowPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HorizontalPanel toolBarPanel;
	

	private String filename;

	public FileEditor() {
		initWidget(binder.createAndBindUi(this));
		saveMenuItem.setScheduledCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				save();
			}
		});
	}
	
	public FileEditor(boolean showBars) {
		this();
		barsFlowPanel.setVisible(false);
		dockLayoutPanel.setWidgetSize(barsFlowPanel, 0);
	}
	
	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	public void setMode(String mode) {
		codeArea.setMode(mode);
	}

	public void setFoldGutter(boolean foldGutter) {
		codeArea.setFoldGutter(foldGutter);
	}

	public void setLineNumbers(boolean lineNumbers) {
		codeArea.setLineNumbers(lineNumbers);
	}

	public void setText(String content) {
		codeArea.setText(content);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void scrollIntoView(Pos pos) {
		codeArea.scrollIntoView(pos);
	}
	
	public void setSelection(Pos anchor, Pos head) {
		codeArea.setSelection(anchor, head);
	}
	
	public void add(IsWidget widget) {
		toolBarPanel.add(widget);
	}
	
	// -----------------------------------------------------------------------

	public void autoRefresh() {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				codeArea.refresh();
				return (codeArea.getOffsetHeight() == 0
						|| codeArea.getOffsetWidth() == 0);
			}
		}, 100);
	}

	// -----------------------------------------------------------------------

	public void setFocus(boolean focused) {
		codeArea.setFocus(focused);
	}
	
	
	// -----------------------------------------------------------------------

	@UiHandler("saveButton")
	void onSaveClick(ClickEvent event) {
		save();
	}

	// -----------------------------------------------------------------------

	protected void save() {
		Document doc = Document.get();
		AnchorElement anchor = doc.createAnchorElement();
		anchor.setAttribute("download", filename); // HTML5
		String ext = filename.substring(filename.lastIndexOf('.'));
		anchor.setHref("data:text/" + ext + ";charset=utf-8" + ","
				+ URL.encode(codeArea.getText()));

		doc.getBody().appendChild(anchor);
		// Anchor.wrap(anchor).fireEvent(new ClickEvent(){});
		clickElement(anchor);
		anchor.removeFromParent();
		;
	}

	public static native void clickElement(Element elem) /*-{
		elem.click();
	}-*/;

}
