package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class SistemaREDITResults extends Composite implements RequiresResize, EnterpriseITStatus.Visitor {


	static interface Binder extends UiBinder<Widget, SistemaREDITResults> {}

	static interface Template extends SafeHtmlTemplates {

		@Template("<span style=\"{0}\">{1}</span>")
		SafeHtml treeItem(SafeStyles style, String message);

		@Template("<span style=\"{0}\">"
				+ "Se ha producido un error."
				+ " {1}.</span>")
		SafeHtml unknownError(SafeStyles mainStyle, String message);

		@Template("<span style=\"{0}\">"
				+ "Para acceder al SISTEMA RED se requiere un certificado aceptado por la Seguridad Social."
//				+ " Pulse <a style=\"{1}\" onclick='javascript:importCertificate();'  >aqu\u00ed</a> para importar un certificado.</span>"
				)
		SafeHtml noCertificateTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

	}
	
	static interface SaltraEvent  {
		
		default String getMessage() { 
			return ""; 
		} 
		
		default void append (SafeHtmlBuilder builder) {
			builder.append(TEMPLATE.treeItem(SafeStylesUtils.forFontSize(12, Unit.PX), getMessage()));			
		}
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final Template TEMPLATE = GWT.create(Template.class);

	@UiField
	Tree eventsTree;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	FlowPanel menuBarFlowPanel;

	private Images images;

	private TreeItem errorsItem;
	private TreeItem warningsItem;	
	private TreeItem messagesItem;	
	private TreeItem notFoundItem;

	private boolean isUserComunica;
	
	public SistemaREDITResults() {
		
		images = GWT.create(Images.class);

		initWidget(binder.createAndBindUi(this));

		errorsItem = new TreeItem(imageItemHTML(images._error(), "ERRORES"));
		eventsTree.addItem(errorsItem);

		warningsItem = new TreeItem(imageItemHTML(images.warn(), "AVISOS"));
		eventsTree.addItem(warningsItem);

		messagesItem = new TreeItem(imageItemHTML(images.info(), "MENSAJES"));
		eventsTree.addItem(messagesItem);
		
		notFoundItem = new TreeItem(imageItemHTML(images.warn(), "PARTES NO ENCONTRADAS EN AON SOLUTIONS"));
		eventsTree.addItem(notFoundItem);

		errorsItem.setVisible(false);
		warningsItem.setVisible(false);
		messagesItem.setVisible(false);
		notFoundItem.setVisible(false);		
	}

	public void run() {}
	
	protected void setIsUserComunica(boolean isUserComunica) {
		this.isUserComunica = isUserComunica;
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

	// @UiHandler("expandAllButton")
	// void onClickExpandAllButton(ClickEvent event ){
	// 	collapse(false);
	// }
	
	// @UiHandler("collapseAllButton")
	// void onClickCollapseAllButton(ClickEvent event ){
	// 	collapse(true);
	// }
	
	// --------------------------------------------------------- RequiresResize
	
	@Override
	public void onResize() {
		dockLayoutPanel.onResize();
	}
	
	public void hideNorth() {
		dockLayoutPanel.setWidgetSize(menuBarFlowPanel, 0);
	}
	
	// ------------------------------------------------------ EnterprisesStatus
	
	@Override
	public void up2DateEnterprise() {
		addInfo("ITs actualizadas, no existen cambios nuevos.");
		syncMessages();
	}
	
	protected void setUp2DateEnterprise() {
		addInfo("ITs actualizadas, no existen cambios nuevos.");
		syncMessages();
	}
	
	protected void credentialsFound() {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.noCertificateTreeItem(getMainStyle(), getAnchorStyle()));
			}
		});
		
		syncErrors();
	}

	@Override
	public void itNotExist(ItNotExist status) {

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);	
		
		if(status.getIdPart().isPresent())
			itNoExistToSS(horizontalPanel, status);
		else 
			itNoExistToAon(horizontalPanel, status);
		
		addNotFound(horizontalPanel).setUserObject(status);
		syncNotFound();
	}
	
	private void itNoExistToAon(HorizontalPanel horizontalPanel, ItNotExist status) {
		String reason = status.getType()!=null ? "por "+status.getType().getName(): "";
		horizontalPanel.add(
			new Label(
				"Afiliado '"
				+status.getName()
				+" it "+ reason 
				+"' ( "+ DateTimeFormat.getFormat("dd-MM-yyy").format(status.getStartDate()) 
				+" ) no encontrada en aon Solutions."
//			   +" Pulse"
			)
		);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		Anchor add = new Anchor("aqu\u00ed");
//		add.addClickHandler(e -> onSaveITPart(status));
//		
//		add.getElement().getStyle().setColor("blue");
//		add.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
//		
//		horizontalPanel.add(add);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		horizontalPanel.add(new Label("para guardar en aon Solutions o"));
//		
//		horizontalPanel.add(new HTML("&nbsp;"));
//		Anchor remove = new Anchor("aqu\u00ed");
//		if(this.isUserComunica) {
//			remove.addClickHandler(e -> onRemoveITPartToSS(status));
//			remove.getElement().getStyle().setColor("red");
//		} else {
//			remove.getElement().getStyle().setColor("grey");
//		}
//
//		remove.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
//		
//		horizontalPanel.add(remove);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		horizontalPanel.add(new Label("para eliminar en SISTEMA RED."));
	}
	
	
	private void itNoExistToSS(HorizontalPanel horizontalPanel, ItNotExist status) {
		String reason = status.getType()!=null ? "por "+status.getType().getName(): "";
		horizontalPanel.add(
			new Label(
				"Afiliado '"
				+status.getName()
				+" it " + reason
				+"' ( "+ DateTimeFormat.getFormat("dd-MM-yyy").format(status.getStartDate()) 
				+" ) no encontrada en SISTEMA RED."
//			   +" Pulse"
			)
		);
		
//		horizontalPanel.add(new HTML("&nbsp;"));
//		Anchor anchor = new Anchor("aqu\u00ed");
//		if(this.isUserComunica) {
//			anchor.addClickHandler(e -> onOpenITPart(status) );
//			anchor.getElement().getStyle().setColor("blue");
//		} else {
//			anchor.getElement().getStyle().setColor("grey");
//		}
//		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
//		
//		horizontalPanel.add(anchor);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		horizontalPanel.add(new Label("para comunicar o"));
//		
//		horizontalPanel.add(new HTML("&nbsp;"));
//		Anchor remove = new Anchor("aqu\u00ed");
//		
//		remove.addClickHandler(e -> onRemoveITPartToAon(status));
//		remove.getElement().getStyle().setColor("red");
//		remove.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
//		horizontalPanel.add(remove);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		horizontalPanel.add(new Label("para eliminar en aon Solutions."));
	}

	@Override
	public void unknownError(String message) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.unknownError(getMainStyle(), message));
			}
		});
		
		syncErrors();
	}
	
	@Override
	public void credentialsNotFound() {
		addError(new SaltraEvent() {
					
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.noCertificateTreeItem(getMainStyle(), getAnchorStyle()));
			}
		});
		
		syncErrors();
	}

	public void updatedEnterprise() {}
	
	public void onFinish() {}
	
	private boolean isPaternity(ContractLeaveType type) {
		return type !=null && ( type.equals(ContractLeaveType.MATERNIDAD) || type.equals(ContractLeaveType.PATERNIDAD) );
	}
	
	protected void onOpenITPart(ItNotExist itNotExist) {}
	
	protected void onSaveITPart(ItNotExist itNotExist) {}
	
	protected void onRemoveITPartToAon(ItNotExist itNotExist) {}
	
	protected void onRemoveITPartToSS(ItNotExist itNotExist) {}
	
	// protected void collapse(boolean collapse) {}
	
	// ------------------------------------------------------------------------
	
	protected void removeAll() {
		errorsItem.removeItems();
		syncErrors();
		warningsItem.removeItems();
		syncWarnings();
		messagesItem.removeItems();
		syncMessages();
		notFoundItem.removeItems();
		syncNotFound();
	}
	
	private static String getPartStr(Byte part) {
		String str = "";
		switch (part) {
			case 0:
				str ="Baja";
			break;
			case 1:
				str ="Confirmaci\u00f3n";
			break;
			case 2:
				str ="Alta";
			break;
		}
		return str;
	}

	// ------------------------------------------------------------------------

	protected void syncErrors() {
		errorsItem.setHTML(imageItemHTML(images._error(),
				"ERRORES (" + errorsItem.getChildCount() + ")"));
		errorsItem.setVisible(errorsItem.getChildCount() > 0);
		errorsItem.setState(errorsItem.getChildCount() > 0);
	}

	protected void syncWarnings() {
		warningsItem.setHTML(imageItemHTML(images.warn(),
				"AVISOS (" + warningsItem.getChildCount() + ")"));
		warningsItem.setVisible(warningsItem.getChildCount() > 0);
		warningsItem.setState(warningsItem.getChildCount() > 0);
	}

	protected void syncMessages() {
		messagesItem.setHTML(imageItemHTML(images.info(),
				"AVISOS (" + messagesItem.getChildCount() + ")"));
		messagesItem.setVisible(messagesItem.getChildCount() > 0);
		messagesItem.setState(messagesItem.getChildCount() > 0);
	}

	protected void syncNotFound() {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"(" + notFoundItem.getChildCount() + ")"
			+ " IT NO ENCONTRADAS."
//			+" PULSE"
			)
		);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		Anchor anchor = new Anchor("AQU\u00CD");
//		anchor.addClickHandler(e -> newEmployees());
		
//		anchor.getElement().getStyle().setColor("blue");
//		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
//		horizontalPanel.add(anchor);
//		horizontalPanel.add(new HTML("&nbsp;"));
//		horizontalPanel.add(new Label("PARA A\u00D1ADIRLOS EN AON SOLUTIONS."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
				
		notFoundItem.setWidget(imageItemWidget(images.warn(),horizontalPanel));
		notFoundItem.setVisible(notFoundItem.getChildCount() > 0);
		notFoundItem.setState(notFoundItem.getChildCount() > 0);
	}

	protected TreeItem addError(Widget  widget) {
		TreeItem treeItem = new TreeItem(
				imageItemWidget(images._error(), widget));
		errorsItem.addItem(treeItem);
		return treeItem;
	}
	
	protected TreeItem addError(SaltraEvent error) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images._error(), error));
		errorsItem.addItem(treeItem);
		treeItem.setUserObject(error);
		return treeItem;
	}

	protected TreeItem addWarning(SaltraEvent warning) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.warn(), warning.getMessage()));
		warningsItem.addItem(treeItem);
		treeItem.setUserObject(warning);
		return treeItem;
	}
	
	protected TreeItem addWarning(String  message) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.warn(), message));
		warningsItem.addItem(treeItem);
		treeItem.setUserObject(message);
		return treeItem;
	}

	protected TreeItem addWarning(Widget  widget) {
		TreeItem treeItem = new TreeItem(
				imageItemWidget(images.warn(), widget));
		warningsItem.addItem(treeItem);
		return treeItem;
	}

	protected TreeItem addInfo(SaltraEvent info) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.info(), info.getMessage()));

		messagesItem.addItem(treeItem);
		treeItem.setUserObject(info);
		return treeItem;
	}

	protected TreeItem addInfo(String  message) {
		TreeItem treeItem = new TreeItem(
				imageItemHTML(images.info(), message));
		messagesItem.addItem(treeItem);
		treeItem.setUserObject(message);
		return treeItem;
	}	
	

	protected TreeItem addNotFound(Widget  widget) {
		TreeItem treeItem = new TreeItem(
				imageItemWidget(images.warn(), widget));
		notFoundItem.addItem(treeItem);
		return treeItem;
	}
	
	// ------------------------------------------------------------------------

	private static SafeStyles getAnchorStyle() {
		return new SafeStylesBuilder().trustedColor("blue").textDecoration(TextDecoration.UNDERLINE).toSafeStyles();
	}

	private static SafeStyles getMainStyle() {
		return SafeStylesUtils.forFontSize(12, Unit.PX);
	}
	
	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private static SafeHtml imageItemHTML(ImageResource imageProto,
			String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		builder.append(TEMPLATE
				.treeItem(SafeStylesUtils.forFontSize(12, Unit.PX), title));
		return builder.toSafeHtml();
	}

	private static Widget imageItemWidget(ImageResource imageProto,
			String title) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new Image(imageProto));
		horizontalPanel.add(new Label(" "));
		Label titleLabel = new Label(title);
		titleLabel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		horizontalPanel.add(titleLabel);

		return horizontalPanel;
	}

	private static Widget imageItemWidget(ImageResource imageProto,
			Widget widget) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new Image(imageProto));
		horizontalPanel.add(new Label(" "));		
		horizontalPanel.add(widget);

		return horizontalPanel;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private static SafeHtml imageItemHTML(ImageResource imageProto,
			SaltraEvent saltraEvent ) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		saltraEvent.append(builder);
		return builder.toSafeHtml();
	}

	
	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

    @Override
    public void unknownErrorAnd(String message) {
        unknownError(message);
    }

	public int getTreeItems() {
		Integer events = eventsTree.getItemCount();
		Integer errors = errorsItem.getChildCount();
		Integer warnings = warningsItem.getChildCount();
		Integer messages = messagesItem.getChildCount();
		Integer notFound = notFoundItem.getChildCount();
		
		return events + errors + warnings + messages + notFound;
	}
}
