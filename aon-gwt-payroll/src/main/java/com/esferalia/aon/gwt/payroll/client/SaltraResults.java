package com.esferalia.aon.gwt.payroll.client;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedCCC;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedContractType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedOccupation;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedPartialFactor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedQuoteGroup;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedStartDate;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AffiliatedNotFound;
import com.esferalia.aon.gwt.payroll.shared.SaltraService.JsSaltraResults;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class SaltraResults extends Composite implements RequiresResize, EmployeeStatus.Visitor, EnterpriseStatus.Visitor {


	static interface Binder extends UiBinder<Widget, SaltraResults> {

	}

	static interface Template extends SafeHtmlTemplates {

		@Template("<span style=\"{0}\">{1}</span>")
		SafeHtml treeItem(SafeStyles style, String message);
		
		@Template("<span style=\"{0}\">"
				+ "Fecha de baja no encontrada."
				+ " Pulse <a style=\"{1}\" onclick='javascript:unRegister();'  >aqu\u00ed</a> para registar la baja en el SISTEMA RED.</span>")
		SafeHtml endDateNotFound(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Alta de trabajador no encontrada."
				+ " Pulse <a style=\"{1}\" onclick='javascript:register();'  >aqu\u00ed</a> para registar el alta en el SISTEMA RED.</span>")
		SafeHtml employeeNotFound(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Alta de trabajador en un fecha diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateStartDate();'  >aqu\u00ed</a> para modificar el alta en el SISTEMA RED.</span>")
		SafeHtml startDateMismatched(SafeStyles mainStyle, SafeStyles anchorStyle, String date);

		@Template("<span style=\"{0}\">"
				+ "Acceso no autorizado. Certificado no aceptado por la Seguridad Social."
				+ " Pulse <a style=\"{1}\" onclick='javascript:importCertificate();'  >aqu\u00ed</a> para importar un certificado.</span>")
		SafeHtml forbiddenTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Para acceder al SISTEMA RED se requiere un certificado aceptado por la Seguridad Social."
				+ " Pulse <a style=\"{1}\" onclick='javascript:importCertificate();'  >aqu\u00ed</a> para importar un certificado.</span>")
		SafeHtml noCertificateTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Alta de trabajador en una Cuenta de cotizac\u00f3in diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateCCC();'  >aqu\u00ed</a> para modificar el alta en el SISTEMA RED.</span>")
		SafeHtml cccMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String ccc);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con un Grupo de Cotizaci\u00f3n diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateContractType();'  >aqu\u00ed</a> para cambiar el Grupo de Cotizaci\u00f3n en el SISTEMA RED.</span>")
		SafeHtml quoteGroupMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con un Tipo de Contrato diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateContractType();'  >aqu\u00ed</a> para cambiar el Tipo de Contrato en el SISTEMA RED.</span>")
		SafeHtml contractTypeMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con un Coeficiente de Parcialidad diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updatePartialFactor();'  >aqu\u00ed</a> para cambiar el Coeficiente de Parcialidad en el SISTEMA RED.</span>")
		SafeHtml partialFactorMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

		@Template("<span style=\"{0}\">"
				+ "Trabajador sin Ocupaci\u00F3n de A.T."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updatePartialFactor();'  >aqu\u00ed</a> para registar la Ocupaci\u00f3n de A.T  en el SISTEMA RED.</span>")
		SafeHtml occupationNotFoundTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con una Ocupaci\u00F3n de A.T diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updatePartialFactor();'  >aqu\u00ed</a> para cambiar la Ocupaci\u00f3n de A.T  en el SISTEMA RED.</span>")
		SafeHtml occupationMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

	}
	
	static interface SaltraEvent  {
		
		default String getMessage() { return ""; } ;
		
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
	
	// Certificate 
	@UiField
	FormPanel certificateFormPanel;

	@UiField
	FileUpload certificateFileUpload;
	
	@UiField
	Hidden userNameHidden;

	@UiField
	Hidden domainNameHidden;
	
	@UiField
	Hidden certificatePasswordHidden;
	
	
	@UiField
	FormPanel employeeFormPanel;
	@UiField
	Hidden cccHidden;
	@UiField
	Hidden nifHidden;
	@UiField
	Hidden dateHidden;
	@UiField
	Hidden regimeHidden;
	@UiField
	Hidden userHidden;
	@UiField
	Hidden domainHidden;

	private Images images;

	private TreeItem errorsItem;
	private TreeItem warningsItem;
	
	private TreeItem messagesItem;
	
	
	public SaltraResults() {
		
		images = GWT.create(Images.class);

		initWidget(binder.createAndBindUi(this));

		errorsItem = new TreeItem(imageItemHTML(images._error(), "ERRORES"));
		eventsTree.addItem(errorsItem);

		warningsItem = new TreeItem(imageItemHTML(images.warn(), "AVISOS"));
		eventsTree.addItem(warningsItem);

		messagesItem = new TreeItem(imageItemHTML(images.info(), "MENSAJES"));
		eventsTree.addItem(messagesItem);
		

//		eventsTree.addSelectionHandler(new EventsSelectionHandler());
//		eventsTree.addDomHandler(new EventsContextMenuHandler(),
//				ContextMenuEvent.getType());
		
		errorsItem.setVisible(false);
		warningsItem.setVisible(false);
		messagesItem.setVisible(false);
		
		export2JS(this);
		
		userNameHidden.setValue(Wnd.getCurrentUser());
		domainNameHidden.setValue(Wnd.getCurrentDomainNameURL());
		certificateFileUpload.getElement().setPropertyString("accept", ".pfx,.p12");
		
		userHidden.setValue(Wnd.getCurrentUser());
		domainHidden.setValue(Wnd.getCurrentDomainNameURL());
		
	}

	public  void run() {
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
	
	@UiHandler("certificateFileUpload")
	void onFileUploadChange(ChangeEvent event) {
		
		showCertificatePasswordDialog(password -> {
			certificatePasswordHidden.setValue(password);
			certificateFormPanel.submit();
			certificateFormPanel.addSubmitCompleteHandler((e) -> {
				saltraCredentialsFound();
			});
		});	
	}

	// --------------------------------------------------------- RequiresResize
	
	@Override
	public void onResize() {
		dockLayoutPanel.onResize();
	}
	
	// --------------------------------------------------------- EmployeeStatus
	
	@Override
	public void up2Date() {
		addInfo("Trabajador actualizado, no existen movimientos y/o cambios nuevos.");
		syncMessages();
	}
	
	@Override
	public void forbidden() {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.forbiddenTreeItem(getMainStyle(), getAnchorStyle()));
			}
		});
		
		syncErrors();
	}
	
	@Override
	public void invalidData() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void employeeNotFound() {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.employeeNotFound(getMainStyle(), getAnchorStyle()));
			}


		});
		
		syncErrors();		
	}
	
	@Override
	public void saltraCredentialsNotFound() {
		addError(new SaltraEvent() {
					
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.noCertificateTreeItem(getMainStyle(), getAnchorStyle()));
			}
		});
		
		syncErrors();
	}

	@Override
	public void endDateNotFound() {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.endDateNotFound(getMainStyle(), getAnchorStyle()));
			}
		});
		
		syncErrors();
	}
	
	@Override
	public void mismatchedCCC(MismatchedCCC status) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.cccMismatchedTreeItem(getMainStyle(), getAnchorStyle(), status.getSsCCC()));
			}
		});
		
		syncErrors();
	}
		
	@Override
	public void mismatchedStartDate(MismatchedStartDate status) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.startDateMismatched(getMainStyle(), getAnchorStyle(), DateTimeFormat.getFormat("dd/MM/YYYY").format(status.getSsStartDate())));
			}
		});
		
		syncErrors();
	}

	@Override
	public void mismatchedContractType(MismatchedContractType status) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.contractTypeMismatchedTreeItem(getMainStyle(), getAnchorStyle(), status.getSsContractType()));
			}
		});
		
		syncErrors();
	}
	
	@Override
	public void occupationNotFound() {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.occupationNotFoundTreeItem(getMainStyle(), getAnchorStyle()));
			}
		});
		
		syncErrors();
	}
	
	@Override
	public void mismatchedOccupation(MismatchedOccupation status) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.occupationMismatchedTreeItem(getMainStyle(), getAnchorStyle(), status.getSsOccupation()));
			}
		});
		
		syncErrors();		
	}
	
	@Override
	public void mismatchedPartialFactor(MismatchedPartialFactor status) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.partialFactorMismatchedTreeItem(getMainStyle(), getAnchorStyle(), status.getSsPartialFactor()));
			}
		});
		
		syncErrors();	
	}
	
	@Override
	public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.quoteGroupMismatchedTreeItem(getMainStyle(), getAnchorStyle(), status.getSsQuoteGroup()));
			}
		});
		
		syncErrors();
		}
	// ------------------------------------------------------ EnterprisesStatus
	
	@Override
	public void affiliatedNotFound(AffiliatedNotFound status) {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Afiliado '"
			+status.getName()
			+"' ( "+ DateTimeFormat.getFormat("dd-MM-yyy").format(status.getDate()) 
			+" ) no encontrado en aon Solutions."
			+" Pulse"
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> newEmployee(status));
		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para a\u00f1adirlo en aon Solutions."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addWarning(horizontalPanel);
		syncWarnings();
	}
	
	// ------------------------------------------------------------------------
	
	
	protected void saltraCredentialsFound() {
		
	}

	protected void newAffiliated(JsSaltraResults jsSaltraResults) {
		
	}
	// ------------------------------------------------------------------------
	
	protected void removeAll() {
		errorsItem.removeItems();
		syncErrors();
		warningsItem.removeItems();
		syncWarnings();
		messagesItem.removeItems();
		syncMessages();
	}
	


	protected void importCertificate() {
		certificateFileUpload.click(); 
	}
	
	protected void newEmployee(AffiliatedNotFound affiliatedNotFound) {
		
		cccHidden.setValue(affiliatedNotFound.getCcc());
		nifHidden.setValue(affiliatedNotFound.getDni());
		regimeHidden.setValue(affiliatedNotFound.getRegime());
		dateHidden.setValue( DateTimeFormat.getFormat("dd-MM-yyyy").format(affiliatedNotFound.getDate()) );
		
		employeeFormPanel.addSubmitCompleteHandler((e) -> {
			JsSaltraResults jsSaltraResults = eval("("+ e.getResults() +")");
			newAffiliated(jsSaltraResults);
		});
		employeeFormPanel.submit(); 	

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
	
	protected void expandAll() {
		errorsItem.setState(true);
		warningsItem.setState(true);
	}


	protected void collapseAll() {
		errorsItem.setState(false);
		warningsItem.setState(false);
	}


	// ------------------------------------------------------------------------

	private static SafeStyles getAnchorStyle() {
		return new SafeStylesBuilder()
		.trustedColor("blue")
		.textDecoration(TextDecoration.UNDERLINE)
		.toSafeStyles();
	}

	private static SafeStyles getMainStyle() {
		SafeStyles mainStyle = 
		SafeStylesUtils
		.forFontSize(12, Unit.PX)
		;
		return mainStyle;
	}
	
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
	
	private static void showCertificatePasswordDialog(Consumer<String> consumer)  {
		PasswordDialog inputDialog = new PasswordDialog(
				"Contrase\u00f1a requerida - AON Solutions", 
				"Por favor, introduzca la contrase\u00f1a que se utiliz\u00f3 para cifrar esta copia de respaldo del certificado.")
				{
			@Override
			public void onAccept() {
				consumer.accept( getPasswordValue());
			}
		};
		
		
		inputDialog.center();
		inputDialog.show();
	}

	private static native void export2JS(SaltraResults saltraResults) /*-{
		$wnd.importCertificate = $entry(function() {
			saltraResults.@com.esferalia.aon.gwt.payroll.client.SaltraResults::importCertificate()();
		});		
			
	}-*/;
	
	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

}
