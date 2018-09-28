package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.codemirror.client.ui.MergeArea;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class MergeEditor extends ResizeComposite {

	interface Binder extends UiBinder<Widget, MergeEditor> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Label titleLabel;

	@UiField
	MergeArea mergeArea;

	@UiField
	MenuItem saveMenuItem;

	@UiField
	HorizontalPanel toolBarPanel;

	private String filename;

	public MergeEditor() {
		initWidget(binder.createAndBindUi(this));
		saveMenuItem.setScheduledCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				save();
			}
		});
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	public void setMode(String mode) {
		mergeArea.setMode(mode);
	}

	public void add(IsWidget widget) {
		toolBarPanel.add(widget);
	}

	public void setFoldGutter(boolean foldGutter) {
		mergeArea.setFoldGutter(foldGutter);
	}

	public void setLineNumbers(boolean lineNumbers) {
		mergeArea.setLineNumbers(lineNumbers);
	}

	public void setText(String content) {
		mergeArea.setValue(content);
	}

	public void setOrig(String content) {
		mergeArea.setOrig(content);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setShowDifferences(boolean showDifferences ){
		mergeArea.setShowDifferences(showDifferences);
	}
	
	public void scrollIntoView(Pos pos) {
		mergeArea.scrollIntoView(pos);
	}
	
	
	public void setSelection(Pos anchor, Pos head) {
		mergeArea.setSelection(anchor, head);
	}
	
	// -----------------------------------------------------------------------

	public void autoRefresh() {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				
				mergeArea.refresh();

				Widget parent = mergeArea.getParent();
				if ( parent != null ) 
					mergeArea.resize(parent.getOffsetHeight());

				return (mergeArea.getOffsetHeight() == 0
						|| mergeArea.getOffsetWidth() == 0);
				
			}
		}, 100);
	}

	// -----------------------------------------------------------------------

	// -----------------------------------------------------------------------

	@UiHandler("saveButton")
	void onSaveClick(ClickEvent event) {
		save();
	}

	@UiHandler("copyButton")
	void onCopyClick(ClickEvent event) {
		copy();
	}

	// -----------------------------------------------------------------------

	private void save() {
		Document doc = Document.get();
		AnchorElement anchor = doc.createAnchorElement();
		anchor.setAttribute("download", filename); // HTML5
		String ext = filename.substring(filename.lastIndexOf('.'));
		anchor.setHref("data:text/" + ext + ";charset=utf-8" + ","
				+ URL.encode(mergeArea.getValue()));

		doc.getBody().appendChild(anchor);
		// Anchor.wrap(anchor).fireEvent(new ClickEvent(){});
		clickElement(anchor);
		anchor.removeFromParent();
		;
	}

	private void copy() {
		mergeArea.setValue(mergeArea.getOrig());
	}
	
	public static native void clickElement(Element elem) /*-{
		elem.click();
	}-*/;

}
