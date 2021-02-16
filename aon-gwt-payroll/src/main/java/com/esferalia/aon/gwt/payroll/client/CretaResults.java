package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsUnknownDato;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CretaResults extends Composite implements RequiresResize{

	static interface Binder extends UiBinder<Widget, CretaResults> {

	}

	static interface Template extends SafeHtmlTemplates {

		@Template("<span style=\"{0}\">{1}</span>")
		SafeHtml treeItem(SafeStyles style, String message);
	}
	

	private class EventsSelectionHandler implements SelectionHandler<TreeItem> {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			Object userObject = event.getSelectedItem().getUserObject();
			CretaResults.this.selectionHandler.accept((CretaService.JsEvent) userObject);
		}
	}

	private class EventsContextMenuHandler implements ContextMenuHandler {
		@Override
		public void onContextMenu(ContextMenuEvent event) {
			// stop the browser from opening the context menu
			event.preventDefault();
			event.stopPropagation();

			TreeItem item = CretaResults.this.eventsTree.getSelectedItem();
			Object userObject = item.getUserObject();

			// TODO : I know that's so ugly and not Object oriented. But it's 
			// much more clear than anything else. I promise to change ( even improve ) it soon.
			if ( AonStringUtils.isNotBlank(((JsUnknownDato)userObject).getCode()) ) {
				onUnknownDatoContextMenu((JsUnknownDato) userObject, event);
			}
		}
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final Template TEMPLATE = GWT.create(Template.class);

	private static Map<String, Collection<String>> DATA = new HashMap<String,Collection<String>>();

	private Images images;

	@UiField
	Tree eventsTree;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;

	private TreeItem errorsItem;
	private TreeItem warningsItem;
	
	private TreeItem messagesItem;

	private Set<JsFile> jsFiles;
	
	private Consumer<CretaService.JsEvent> selectionHandler;
	
	public CretaResults() {
		
		images = GWT.create(Images.class);

		initWidget(binder.createAndBindUi(this));

		selectionHandler = e -> {};

		errorsItem = new TreeItem(imageItemHTML(images._error(), "ERRORES"));
		eventsTree.addItem(errorsItem);

		warningsItem = new TreeItem(imageItemHTML(images.warn(), "AVISOS"));
		eventsTree.addItem(warningsItem);

		messagesItem = new TreeItem(imageItemHTML(images.info(), "MENSAJES"));
		eventsTree.addItem(messagesItem);
		

		eventsTree.addSelectionHandler(new EventsSelectionHandler());
		eventsTree.addDomHandler(new EventsContextMenuHandler(),
				ContextMenuEvent.getType());
		
		errorsItem.setVisible(false);
		warningsItem.setVisible(false);
		messagesItem.setVisible(false);
		
	}
	
	public void setJsFiles(Set<JsFile> jsFiles) {
		this.jsFiles = jsFiles;
	}

	public void addErrors(CretaService.JsEvent errors[]) {
		for (CretaService.JsEvent error : errors)
			addError(error);

		syncErrors();
	}

	public void addWarnings(CretaService.JsEvent warnings[]) {
		for (CretaService.JsEvent warning : warnings)
			addWarning(warning);

		syncWarnings();
	}

	public void addMessages(CretaService.JsEvent infos[]) {
		for (CretaService.JsEvent info : infos) {
			addInfo(info);
		}

		syncMessages();
	}

	public void addUnknown(CretaService.JsUnknownDato unknowns[]) {
		for (CretaService.JsUnknownDato unknown : unknowns) {
			if (!unknown.isMandatory())
				addWarning(unknown);
			else if (unknown.getValue() != null )
				addWarning(unknown);
			else
				addError(unknown);

		}

		syncErrors();
		syncWarnings();
		syncMessages();
	}

	public  void run() {
		MainCreta.submit(CretaService.CRETA_URL + "/" + CretaService.File.BASES, 
				DATA, jsFiles, 
				new AsyncCallback<CretaService.JsBasesResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				//Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(JsBasesResult result) {
				CretaResults.this.onBases(result);
			}
		});
	}
	
	
	public void removeParameter( CretaService.Parameter parameter) {
		DATA.remove(parameter.name());
	}
	public void setParameter( CretaService.Parameter parameter, String value) {
		DATA.put(parameter.name(), Collections.singleton(value));
	}

	public void setParameter( CretaService.Parameter parameter, Collection<String> values) {
		DATA.put(parameter.name(), values);
	}

	public Optional<String> getParameter(CretaService.Parameter parameter){
		return DATA.getOrDefault(parameter.name(), Collections.emptyList())
		.stream()
		.findFirst();
	}
	
	
	public void setSelectionHandler ( Consumer<CretaService.JsEvent> selectionHandler) {
		this.selectionHandler = selectionHandler;
	}
	
	public void addWarnings(String ...messages ) {
		for (String message : messages)
			addWarning(message);

		syncWarnings();
	}


	// ------------------------------------------------------------ @UiHandlers

	@UiHandler("runButton")
	void onClickRunButton(ClickEvent event ){
		run();
	}

	@UiHandler("clearButton")
	void onClickClearButton(ClickEvent event ){
		removeAll();
	}

	@UiHandler("expandAllButton")
	void onClickExpandAllButton(ClickEvent event ){
		expandAll();
	}
	
	@UiHandler("collapseAllButton")
	void onClickCollapseAllButton(ClickEvent event ){
		collapseAll();
	}
	
	// --------------------------------------------------------- RequiresResize
	
	@Override
	public void onResize() {
		dockLayoutPanel.onResize();
	}

	// ------------------------------------------------------------------------
	
	protected void removeAll() {
		errorsItem.removeItems();
		syncErrors();
		warningsItem.removeItems();
		syncWarnings();
	}
	

	protected void onBases(JsBasesResult result) {
		
	}
	
	protected void fix(JsUnknownDato unknownDato, String value ) {
		addDefault(unknownDato, value);
		run();
	}

	protected void onUnknownDatoContextMenu(JsUnknownDato unknownDato,
			ContextMenuEvent event) {
		ContextMenu contextMenu = createContextMenu(unknownDato, this);
		contextMenu.setPopupPosition(event.getNativeEvent().getClientX(),
				event.getNativeEvent().getClientY());
		contextMenu.show();
	}

	// ------------------------------------------------------------------------

	private void syncErrors() {
		errorsItem.setHTML(imageItemHTML(images._error(),
				"ERRORES (" + errorsItem.getChildCount() + ")"));
		errorsItem.setVisible(errorsItem.getChildCount() > 0);
		errorsItem.setState(errorsItem.getChildCount() > 0);
	}

	private void syncWarnings() {
		warningsItem.setHTML(imageItemHTML(images.warn(),
				"AVISOS (" + warningsItem.getChildCount() + ")"));
		warningsItem.setVisible(warningsItem.getChildCount() > 0);
		warningsItem.setState(warningsItem.getChildCount() > 0);
	}

	private void syncMessages() {
		messagesItem.setHTML(imageItemHTML(images.info(),
				"AVISOS (" + messagesItem.getChildCount() + ")"));
		messagesItem.setVisible(messagesItem.getChildCount() > 0);
		messagesItem.setState(messagesItem.getChildCount() > 0);
	}

	private TreeItem addError(CretaService.JsEvent error) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images._error(), error.getMessage()));
		errorsItem.addItem(treeItem);
		treeItem.setUserObject(error);
		return treeItem;
	}

	private TreeItem addWarning(CretaService.JsEvent warning) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.warn(), warning.getMessage()));
		warningsItem.addItem(treeItem);
		treeItem.setUserObject(warning);
		return treeItem;
	}
	
	private TreeItem addWarning(String  message) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.warn(), message));
		warningsItem.addItem(treeItem);
		treeItem.setUserObject(message);
		return treeItem;
	}

	private TreeItem addInfo(CretaService.JsEvent info) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.info(), info.getMessage()));

		messagesItem.addItem(treeItem);
		treeItem.setUserObject(info);
		return treeItem;
	}

	private void addDefault(JsUnknownDato unknownDato, String value){
		Collection<String> defaults =  DATA.get(CretaService.Parameter.DEFAULTS.name());
		if ( defaults == null )
			DATA.put(CretaService.Parameter.DEFAULTS.name(), defaults = new ArrayList<String>());
		
		String naf = unknownDato.getNaf() ;
		String code = unknownDato.getCode();
		defaults.add(code + ( naf != null ? naf : "" ) + "="+value);
	}
	
	
	private void expandAll() {
		errorsItem.setState(true);
		warningsItem.setState(true);
	}


	private void collapseAll() {
		errorsItem.setState(false);
		warningsItem.setState(false);
	}


	// ------------------------------------------------------------------------

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private static SafeHtml imageItemHTML(ImageResource imageProto,
			String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		// builder.appendHtmlConstant(title);

		builder.append(TEMPLATE
				.treeItem(SafeStylesUtils.forFontSize(12, Unit.PX), title));
		return builder.toSafeHtml();
	}

	private static ContextMenu createContextMenu(final JsUnknownDato unknownDato, final CretaResults cretaResults) {
		ContextMenu contextMenu = new ContextMenu();

		contextMenu.addItem("A\u00F1adir", new ScheduledCommand() {
			@Override
			public void execute() {

				String code = unknownDato.getCode();
				AsyncCallback<String> callback = new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
					}
					@Override
					public void onSuccess(String result) {
						cretaResults.fix(unknownDato, result );
					}
				};
				
				if ( code.equals("54"))
					CustomDialog.showInputDialog(
							"Introduce la causa que da lugar a la obligaci\u00F3n de cotizar, indicador \"" + code +"\" ", 
							"A\u00F1adir", 
							new String []{
									"1",
									"2",
									"3",
									"4",
									"5"},
							new String []{
									"1-Atrasos de convenio",
									"2-Normativa (disposici\u00F3n legal)",
									"3-Acta de conciliaci\u00F3n",
									"4-Sentencia judicial",
									"5-Cualquier otro t\u00EDtulo leg\u00EDtimo.",
									},
							"1",
							callback);
				else
					CustomDialog.showInputDialog(
							"Introduce el concepto econ\u00F3mico de cotizaci\u00f3n " + code, 
							"A\u00F1adir", 
							callback);

			}

		}, AON.AON_ICON_ACCEPT, AON.AON_ICON_CMD_BUTTON);
		
		if ( unknownDato.isMandatory() ) 
			return contextMenu;
		if ( AonStringUtils.isBlank(unknownDato.getValue()) ) 
			return contextMenu;
		
		contextMenu.addItem("Eliminar", new ScheduledCommand() {
			@Override
			public void execute() {
				cretaResults.fix(unknownDato, " " );
			}
		}, AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);

		return contextMenu;
	}

}
