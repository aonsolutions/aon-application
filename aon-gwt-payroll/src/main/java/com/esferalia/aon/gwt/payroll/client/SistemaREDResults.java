package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.SistemaREDService.EMPLOYEES;
import static com.esferalia.aon.gwt.payroll.shared.SistemaREDService.SISTEMA_RED_URL;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedCCC;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedContractType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedOccupation;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedPartialFactor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedQuoteGroup;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedStartDate;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AffiliatedAtTrash;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AffiliatedNotFound;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class SistemaREDResults extends Composite implements RequiresResize, EmployeeStatus.Visitor, EnterpriseStatus.Visitor {


	static interface Binder extends UiBinder<Widget, SistemaREDResults> {

	}

	static interface Template extends SafeHtmlTemplates {

		@Template("<span style=\"{0}\">{1}</span>")
		SafeHtml treeItem(SafeStyles style, String message);
		
		@Template("<span style=\"{0}\">"
				+ "Fecha de baja no encontrada en el SISTEMA RED."
				+ " Pulse <a style=\"{1}\" onclick='javascript:cleanEndDate();'  >aqu\u00ed</a> para eliminar la fecha de baja en AON.</span>"
				)
		SafeHtml endDateNotFound(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Alta de trabajador no encontrada en el SISTEMA RED."
				//+ " Pulse <a style=\"{1}\" onclick='javascript:register();'  >aqu\u00ed</a> para registar el alta en el SISTEMA RED.</span>"
				)
		SafeHtml employeeNotFound(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Alta de trabajador en un fecha diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateStartDate();'  >aqu\u00ed</a> para actualizar la fecha en AON.</span>")
		SafeHtml startDateMismatched(SafeStyles mainStyle, SafeStyles anchorStyle, String date);

		@Template("<span style=\"{0}\">"
				+ "Se ha producido un error."
				+ " {1}.</span>")
		SafeHtml unknownError(SafeStyles mainStyle, String message);

		@Template("<span style=\"{0}\">"
				+ "Acceso no autorizado. Certificado no aceptado por la Seguridad Social."
				+ " Pulse <a style=\"{1}\" onclick='javascript:importCertificate();'  >aqu\u00ed</a> para importar un certificado.</span>")
		SafeHtml forbiddenTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Para acceder al SISTEMA RED se requiere un certificado aceptado por la Seguridad Social."
				+ " Pulse <a style=\"{1}\" onclick='javascript:importCertificate();'  >aqu\u00ed</a> para importar un certificado.</span>")
		SafeHtml noCertificateTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Cuenta de cotizaci\u00f3n ( C.C.C ) no autorizada."
				+ " Pulse <a style=\"{1}\" onclick='javascript:importCertificate();'  >aqu\u00ed</a> para importar un nuevo certificado.</span>")
		SafeHtml notAuthorizedCCC(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Alta de trabajador en una Cuenta de cotizac\u00f3in diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateCCC();'  >aqu\u00ed</a> para actualizar la Cuenta de cotizac\u00f3in en AON.</span>"
				)
		SafeHtml cccMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String ccc);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con un Grupo de Cotizaci\u00f3n diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateQuoteGroup();'  >aqu\u00ed</a> para actualizar el Grupo de Cotizaci\u00f3n en AON.</span>")
		SafeHtml quoteGroupMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con un Tipo de Contrato diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateContractType();'  >aqu\u00ed</a> para actualizar el Tipo de Contrato en AON.</span>")
		SafeHtml contractTypeMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con un Coeficiente de Parcialidad diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updatePartialFactor();'  >aqu\u00ed</a> para actualizar el Coeficiente de Parcialidad en AON.</span>")
		SafeHtml partialFactorMismatchedTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle, String type);

		@Template("<span style=\"{0}\">"
				+ "Trabajador sin Ocupaci\u00F3n de A.T."
				+ " Pulse <a style=\"{1}\" onclick='javascript:cleanOcupation();'  >aqu\u00ed</a> para eliminar la Ocupaci\u00f3n de A.T  en AON.</span>")
		SafeHtml occupationNotFoundTreeItem(SafeStyles mainStyle, SafeStyles anchorStyle);

		@Template("<span style=\"{0}\">"
				+ "Trabajador con una Ocupaci\u00F3n de A.T diferente {2}."
				+ " Pulse <a style=\"{1}\" onclick='javascript:updateOcupation();'  >aqu\u00ed</a> para actualizar la Ocupaci\u00f3n de A.T  en AON.</span>")
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
	
	@UiField
	Button clearButton;
	
	@UiField
	Button runButton;
	
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
	Hidden idHidden;
	@UiField
	Hidden cccHidden;
	@UiField
	Hidden nafHidden;
	@UiField
	Hidden dateHidden;
	@UiField
	Hidden regimeHidden;
	@UiField
	Hidden userHidden;
	@UiField
	Hidden domainHidden;
	
	@UiField
	FlowPanel menuBarFlowPanel;

	private Images images;

	private TreeItem errorsItem;
	private TreeItem warningsItem;	
	private TreeItem messagesItem;	
	private TreeItem notFoundItem;
	
	public SistemaREDResults() {
		
		images = GWT.create(Images.class);

		initWidget(binder.createAndBindUi(this));

		errorsItem = new TreeItem(imageItemHTML(images._error(), "ERRORES"));
		eventsTree.addItem(errorsItem);

		warningsItem = new TreeItem(imageItemHTML(images.warn(), "AVISOS"));
		eventsTree.addItem(warningsItem);

		messagesItem = new TreeItem(imageItemHTML(images.info(), "MENSAJES"));
		eventsTree.addItem(messagesItem);
		
		notFoundItem = new TreeItem(imageItemHTML(images.warn(), "AFILIADOS NO ENCONTRADOS EN AON SOLUTIONS"));
		eventsTree.addItem(notFoundItem);

//		eventsTree.addSelectionHandler(new EventsSelectionHandler());
//		eventsTree.addDomHandler(new EventsContextMenuHandler(),
//				ContextMenuEvent.getType());
		
		errorsItem.setVisible(false);
		warningsItem.setVisible(false);
		messagesItem.setVisible(false);
		notFoundItem.setVisible(false);
		
		export2JS(this);
		
		userNameHidden.setValue(Wnd.getCurrentUser());
		domainNameHidden.setValue(Wnd.getCurrentDomainNameURL());
		certificateFileUpload.getElement().setPropertyString("accept", ".pfx,.p12");
		
		userHidden.setValue(Wnd.getCurrentUser());
		domainHidden.setValue(Wnd.getCurrentDomainNameURL());
		
	}

	public  void run() {
	}
	
	public boolean hasMessages() {
		return errorsItem.getChildCount() > 0 || warningsItem.getChildCount() > 0 || notFoundItem.getChildCount() > 0;
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
	
	public void hideRunButton() {
		runButton.setVisible(false);
	}
	
	public void hideClearButton() {
		clearButton.setVisible(false);
	}

//	@UiHandler("expandAllButton")
//	void onClickExpandAllButton(ClickEvent event ){
//		expandAll();
//	}
////	
//	@UiHandler("collapseAllButton")
//	void onClickCollapseAllButton(ClickEvent event ){
//		collapseAll();
//	}
	
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
	public void noQueryData(String message) {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.unknownError(getMainStyle(), message));
			}
		});
		
		syncErrors();
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
	public void notAuthorizedCCC() {
		addError(new SaltraEvent() {
			
			@Override
			public void append(SafeHtmlBuilder builder) {
				builder.append(TEMPLATE.notAuthorizedCCC(getMainStyle(), getAnchorStyle()));
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
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Alta de trabajador en un fecha diferente "
			+"'" + DateTimeFormat.getFormat("dd-MM-yyy").format(status.getSsStartDate()) +"'."
			+" Pulse "
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> updateStartDate(status));		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para actualizar la fecha en AON."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addError(horizontalPanel).setUserObject(status);

		syncErrors();
	}

	@Override
	public void mismatchedContractType(MismatchedContractType status) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Trabajador con un Tipo de Contrato diferente "
			+"'" + status.getSsContractType() +"'."
			+" Pulse "
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> updateContractType(status));		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para actualizar el Tipo de Contrato en AON."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addError(horizontalPanel).setUserObject(status);

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
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Trabajador con un Ocupaci\u00f3n diferente "
			+"'" + status.getSsOccupation() +"'."
			+" Pulse "
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> updateOccupation(status));		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para actualizar la Ocupaci\u00f3n en AON."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addError(horizontalPanel).setUserObject(status);

		syncErrors();
	}
	
	@Override
	public void mismatchedPartialFactor(MismatchedPartialFactor status) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Trabajador con un Coeficiente de Parcialidad diferente "
			+"'" + status.getSsPartialFactor() +"'."
			+" Pulse "
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> updatePartialFactor(status));		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para actualizar el Coeficiente de Parcialidad en AON."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addError(horizontalPanel).setUserObject(status);

		syncErrors();
	}
	
	@Override
	public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Trabajador con un Grupo de Cotizaci\u00f3n diferente "
			+"'" + status.getSsQuoteGroup() +"'."
			+" Pulse "
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> updateQuoteGroup(status));		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para actualizar el Grupo de Cotizaci\u00f3n en AON."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addError(horizontalPanel).setUserObject(status);

		syncErrors();
		}
	// ------------------------------------------------------ EnterprisesStatus
	
	@Override
	public void up2DateEnterprise() {
		addInfo("Contratos actualizados, no existen movimientos y/o cambios nuevos.");
		syncMessages();
	}
	
	protected void setUp2DateEnterprise() {
		addInfo("Contratos actualizados, no existen movimientos y/o cambios nuevos.");
		syncMessages();
	}
	
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
		
		addNotFound(horizontalPanel).setUserObject(status);
		syncNotFound();
	}
	
	@Override
	public void affiliatedAtTrash(AffiliatedAtTrash status) {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"Afiliado '"
			+status.getName()
			+"' ( "+ DateTimeFormat.getFormat("dd-MM-yyy").format(status.getDate()) 
			+" ) en la papelera."
			+" Pulse"
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("aqu\u00ed");
		anchor.addClickHandler(e -> restoreEmployee(status));
		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("para resturarlo."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
		
		addWarning(horizontalPanel);
		syncWarnings();
	}
	
	
	// ------------------------------------------------------------------------
	
	protected void cleanEndDate() {
	}
	
	protected void cleanOcupation() {
	}
		
	protected void saltraCredentialsFound() {
		
	}

	protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {
		
	}
	
	protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults) {
		
	}

	protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults, int total) {
		
	}

	protected void updateStartDate(MismatchedStartDate mismatchedStartDate) {
	}
		
	protected void updateOccupation(MismatchedOccupation mismatchedOccupation) {
	}

	protected void updateQuoteGroup(MismatchedQuoteGroup mismatchedQuoteGroup) {
	}

	protected void updateContractType(MismatchedContractType mismatchedContractType) {		
	}

	protected void updatePartialFactor(MismatchedPartialFactor mismatchedPartialFactor) {
	}
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
	


	protected void importCertificate() {
		certificateFileUpload.click(); 
	}

	protected void newEmployee(AffiliatedNotFound affiliatedNotFound) {
		init();
		cccHidden.setValue(affiliatedNotFound.getCcc());
		nafHidden.setValue(affiliatedNotFound.getNaf());
		regimeHidden.setValue(affiliatedNotFound.getRegime());
		dateHidden.setValue( DateTimeFormat.getFormat("dd-MM-yyyy").format(affiliatedNotFound.getDate()) );
		
		employeeFormPanel.addSubmitCompleteHandler((e) -> {
			JsSistemaREDResults jsSistemaREDResults = eval("("+ e.getResults() +")");
			newAffiliated(jsSistemaREDResults);
		});
		employeeFormPanel.submit(); 	
 
	}
	protected void newEmployees() {
		newEmployees(getAllAffiliatedNotFound());
	}

	private AffiliatedNotFound[] getAllAffiliatedNotFound() {
		AffiliatedNotFound affiliatedNotFound [] = new AffiliatedNotFound [notFoundItem.getChildCount()];
		for ( int i = 0; i < notFoundItem.getChildCount(); i++ ) {
			affiliatedNotFound[i] = (AffiliatedNotFound)notFoundItem.getChild(i).getUserObject();
		}
		return affiliatedNotFound;
	}
	
	protected void newEmployees(AffiliatedNotFound affiliatedNotFound []) {
		
//		FormPanel employeesFormPanel = createEmployeesFormPanel( 
//		affiliatedNotFound, 
//		(event,formPanel)  -> {
//			JsArray<JsSistemaREDResults> jsSistemaREDResults = eval("("+ event.getResults() +")");
//			AON.stop();
//			newAffiliated(jsSistemaREDResults);
//			formPanel.removeFromParent();
//		});
//		
//		menuBarFlowPanel.add(employeesFormPanel);
//		AON.start();
//		employeesFormPanel.submit();
		
		init();
		AON.start();
		submit(affiliatedNotFound, 
		results -> {
			AON.stop();
			newAffiliated(results);
		},
		results -> {
			newAffiliated(results, affiliatedNotFound.length );
		}
		);
		
	}

	protected void restoreEmployee(AffiliatedAtTrash affiliatedAtTrash) {
		idHidden.setValue(affiliatedAtTrash.getId().toString());
		cccHidden.setValue(affiliatedAtTrash.getCcc());
		nafHidden.setValue(affiliatedAtTrash.getNaf());
		regimeHidden.setValue(affiliatedAtTrash.getRegime());
		dateHidden.setValue( DateTimeFormat.getFormat("dd-MM-yyyy").format(affiliatedAtTrash.getDate()) );
		
		employeeFormPanel.addSubmitCompleteHandler((e) -> {
			JsSistemaREDResults jsSaltraResults = eval("("+ e.getResults() +")");
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

	protected void syncNotFound() {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(
		new Label(
			"(" + notFoundItem.getChildCount() + ")"
			+ " AFILIADOS NO ENCONTRADOS."
			+" PULSE"
			)
		);
		horizontalPanel.add(new HTML("&nbsp;"));
		Anchor anchor = new Anchor("AQU\u00CD");
		anchor.addClickHandler(e -> newEmployees());
		
		anchor.getElement().getStyle().setColor("blue");
		anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		
		horizontalPanel.add(anchor);
		horizontalPanel.add(new HTML("&nbsp;"));
		horizontalPanel.add(new Label("PARA A\u00D1ADIRLOS EN AON SOLUTIONS."));
		
		horizontalPanel.getElement().getStyle().setFontSize(12, Unit.PX);
				
		notFoundItem.setWidget(imageItemWidget(images.warn(),
				horizontalPanel));
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

	protected void expandAll() {
		errorsItem.setState(true);
		warningsItem.setState(true);
	}


	protected void collapseAll() {
		errorsItem.setState(false);
		warningsItem.setState(false);
	}

	
	private FormPanel createEmployeesFormPanel(AffiliatedNotFound affiliatedNotFound [], BiConsumer<SubmitCompleteEvent, FormPanel> handler) {
		//Create formPanel to UploadFiles-
		FlowPanel flowPanel = new FlowPanel();
		
		FormPanel formPanel = new FormPanel();
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setAction(SISTEMA_RED_URL + "/" + EMPLOYEES);
		
		Hidden userLogin = new Hidden(SistemaREDService.Parameter.USER.name(), Wnd.getCurrentUser());
		Hidden currentDomain = new Hidden(SistemaREDService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
		
		formPanel.addSubmitCompleteHandler(e -> handler.accept(e, formPanel));
		
		flowPanel.add(userLogin);
		flowPanel.add(currentDomain);
		
		flowPanel.add(new Hidden(SistemaREDService.Parameter.COUNT.name(), String.valueOf(affiliatedNotFound.length)));
		
		for ( int i = 0; i < affiliatedNotFound.length; i++ ) {
			flowPanel.add(new Hidden(SistemaREDService.Parameter.CCC.name() + i, affiliatedNotFound[i].getCcc()));
			flowPanel.add(new Hidden(SistemaREDService.Parameter.NAF.name() + i, affiliatedNotFound[i].getNaf()));
			flowPanel.add(new Hidden(SistemaREDService.Parameter.REGIME.name() + i, affiliatedNotFound[i].getRegime()));
		}
		
		formPanel.add(flowPanel);
			
		return formPanel;
	}	

	private void submit(AffiliatedNotFound affiliatedNotFound [], Consumer<JsArray<JsSistemaREDResults>> success, Consumer<JsArray<JsSistemaREDResults>> progress) {
		
		StringBuffer requestDataBuffer = new StringBuffer();
		
		requestDataBuffer
		.append("&" + SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser() );
		requestDataBuffer
		.append("&" + SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL() );

		requestDataBuffer
		.append("&" + SistemaREDService.Parameter.COUNT.name() + "=" + affiliatedNotFound.length );

		for ( int i = 0; i < affiliatedNotFound.length; i++ ) {
			requestDataBuffer
			.append("&" + SistemaREDService.Parameter.CCC.name() + i + "=" + affiliatedNotFound[i].getCcc());
			requestDataBuffer
			.append("&" + SistemaREDService.Parameter.NAF.name() + i  + "=" + affiliatedNotFound[i].getNaf());
			requestDataBuffer
			.append("&" + SistemaREDService.Parameter.REGIME.name() + i + "=" + affiliatedNotFound[i].getRegime());
		}
		
		// Send request to server and catch any errors.
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SISTEMA_RED_URL + "/" + EMPLOYEES);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
		
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
	
				if (state == XMLHttpRequest.DONE) {
					JsArray<JsSistemaREDResults> jsSistemaREDResults = eval("("+ xhr.getResponseText() +")");
					success.accept(jsSistemaREDResults);
				} else if ( state == XMLHttpRequest.LOADING ) {
					try {
						JsArray<JsSistemaREDResults> jsSistemaREDResults = eval("("+ xhr.getResponseText() +"])");
						if ( jsSistemaREDResults.length() > 0 )
							progress.accept( jsSistemaREDResults );
					} catch ( Exception e ) {
						
					}
				} 
	
			}
		});
		
		xhr.send(requestDataBuffer.toString());
		
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
	
	public Integer getTreeItems() {
		Integer events = eventsTree.getItemCount();
		Integer errors = errorsItem.getChildCount();
		Integer warnings = warningsItem.getChildCount();
		Integer messages = messagesItem.getChildCount();
		Integer notFound = notFoundItem.getChildCount();
		
		return events + errors + warnings + messages + notFound;
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
	
	protected void init() {}
	
	protected void finish() {}

	private static native void export2JS(SistemaREDResults saltraResults) /*-{
		$wnd.cleanEndDate = $entry(function() {
			saltraResults.@com.esferalia.aon.gwt.payroll.client.SistemaREDResults::cleanEndDate()();
		});		
		$wnd.cleanOcupation = $entry(function() {
			saltraResults.@com.esferalia.aon.gwt.payroll.client.SistemaREDResults::cleanOcupation()();
		});		
		$wnd.importCertificate = $entry(function() {
			saltraResults.@com.esferalia.aon.gwt.payroll.client.SistemaREDResults::importCertificate()();
		});		
			
	}-*/;
	
	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

}
