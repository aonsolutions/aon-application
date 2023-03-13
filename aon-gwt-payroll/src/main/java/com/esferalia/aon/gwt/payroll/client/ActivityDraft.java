package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class ActivityDraft extends Composite{
	
	// ---------------------------------------------- Activity

	private class ActivityImplementation extends Activity{

		@Override
		public void onActivityDescriptionChange() {
			activityDraftObject.setActivityDescription(activityDescription.getValue());
			toolbar.setTitle(activityDescription.getValue());
			setHasChange(true);
		}

		@Override
		public void onActivityCNAE2009Change(Integer cnaeId, String cnaeCode, String cnaeTitle) {
			 activityDraftObject.setActivityCNAE2009(cnaeId, cnaeCode, cnaeTitle);
			setHasChange(true);
		}

		@Override
		public void onActivityStartDateChange() {
			activityDraftObject.setActivityStartDate(startDate.getValue());
			setHasChange(true);
		}

		@Override
		public void onActivityEndDateChange() {
			activityDraftObject.setActivityEndDate(endDate.getValue());
			setHasChange(true);
		}

		@Override
		public void onActivityActiveChange() {
			activityDraftObject.setActivityIsPrincipal(activityActive.getValue());
			setHasChange(true);
		}
		
		@Override
		public void onInsertRow() {
			activity.hideActivityColumn();
		}
		
		@Override
		public void onInsertRows() {
			if(activityDraftObject.getCCCs().isEmpty())
				activity.showCCCMessage();
			else {
				activity.showCCCTable();
				activityDraftObject.getCCCs().forEach(ccc -> this.cccWidget.insertRow(ccc));
			}
			
			activity.hideActivityColumn();
		}

		@Override
		public void onDeleteCCC(Integer cccId) {
			activityDraftObject.deleteCCC(cccId);
			setHasChange(true);
		}

		@Override
		public void onInsertCCC(EnterpriseCCC ccc) {
			activityDraftObject.insertCCC(ccc);
			setHasChange(true);
		}

		@Override
		public Set<Entry<Integer, String>> getActivities() {
			return activityDraftObject.getActivities();
		}
		
		@Override
		public List<EnterpriseCCC> getEnterpriseCCCs() {
			return activityDraftObject.getActiveCCCs();
		}
		
		@Override
		public void fireErrorMessage(Map<String, String> messages) {
			showErrorMessage(messages);
		}

		@Override
		public void fireWarningMessage(Map<String, String> messages) {
			showWarningMessage(messages);
			
			// Need to create specific method, but this fire when file fail loading
			setPDFLoadedEnsureDebugId("pdfNotLoaded");
		}
		
		@Override
		public void fireInfoMessage(Map<String, String> messages) {
			showInfoMessage(messages);
		}

		@Override
		protected void fireLoadingMessage(String message) {
			showLoadingMessage(message);
		}

		@Override
		protected void fireHideMessage() {
			hideMessage();
		}

		@Override
		public void showPDF(String dataURI, boolean isLaboralLife) {
			setPDFLoadedEnsureDebugId("pdfLoaded");
			showPdf(isLaboralLife);
			pdfViewer.open(dataURI);
		}
		
	}
	
	// ---------------------------------------------- UiBinder
	
	interface ActivityDraftUiBinder extends UiBinder<Widget, ActivityDraft> {}
	
	private static ActivityDraftUiBinder uiBinder = GWT.create(ActivityDraftUiBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	DeckPanel toolbarDeckPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField(provided = true)
	AonToolbar toolbarPDFViewer;
	
	@UiField
	DeckPanel mainDeckPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	SimpleLayoutPanel scrolledPDFPanel;

	@UiField
	FullViewer pdfViewer;
	
	
	// ---------------------------------------------- Variables
	
	private ActivityDraftObject activityDraftObject;
	
	private Activity activity;
	
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;
	
	private Label pdfLoaded;
	
	// ---------------------------------------------- Constructor

	public ActivityDraft() {	
		activity = new ActivityImplementation();
		
		this.toolbar = new AonToolbar("Actividad");
		this.toolbarPDFViewer = new AonToolbar("Actividad");
		
		// Inicializamos la vista de la actividad
		initWidget(uiBinder.createAndBindUi(this));
		
		showActivity();
		
		getToolbarPanel();
		getToolbarPDFViewerPanel();
		
		dockLayoutPanel.addStyleName(style.container());

		scrolledPDFPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 185.00, Unit.PX);
		
		activity.setActivityDraftCCCHeight();
		
		mainPanel.add(activity);
	}
	
	// ---------------------------------------------- setActivityDraftObject

	public void setActivityDraftObject(ActivityDraftObject activityDraftObject) {
		this.activityDraftObject = activityDraftObject;
		initializeActivity();
	}
	
	private void initializeActivity() {
		activityDraftObject.initializeActivity(
				r -> {
					activity.initializeView();
					fillActivityInfo();
					activity.cccWidget.setDomain(activityDraftObject.getDomain());
					activity.onInsertRows();
					setHasChange(false);
					toolbar.setTitle(activityDraftObject.getActivityDescription());
					hideMessage();
				}, t -> {}
			);
	}
	
	private void fillActivityInfo() {
		activity.activityDescription.setValue(activityDraftObject.getActivityDescription());
		activity.activityCNAE2009.setValue(activityDraftObject.getActivityCNAE2009());
		activity.startDate.setValue(activityDraftObject.getActivityStartDate());
		activity.endDate.setValue(activityDraftObject.getActivityEndDate());
		activity.activityActive.setValue(activityDraftObject.getActivityPrincipal());
	}
	
	// ---------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> onAccept());
		acceptButton.ensureDebugId("activityAcceptBtn");
		toolbar.add(acceptButton);
		
		undoAllButton = new AonToolbarButton( AON.MSG.undo() + " todo", AON.CSS.aonIconUndoAll() );
		undoAllButton.addClickHandler(e -> {
			AonDialog confirmDialog =  new AonDialog("Restaurar CCCs", new HTMLPanel("\u00bfDesea realmente deshacer los cambios realizados sobre la actividad <b>" + activityDraftObject.getActivityDescription() + "</b>\u003f <br>Este proceso es irreversible."));
			confirmDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					showLoadingMessage("Deshaciendo los cambios realizados ...");
					initializeActivity();
				}
			});
		});
		undoAllButton.ensureDebugId("undoAllBtn");
		toolbar.add(undoAllButton);
		
		AonToolbarButton checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.ensureDebugId("checkUpdateCert");
		checkUpdateCert.addClickHandler(e -> onCheckUpdateCert());
		toolbar.add(checkUpdateCert);
		
		AonToolbarButton createCCCBtn = new AonToolbarButton(AON.MSG.newAction() + " CCC", AON.CSS.aonIconAdd() );
		createCCCBtn.addClickHandler(e -> activity.onAddNewCCC());
		createCCCBtn.ensureDebugId("createCCCBtn");
		toolbar.add(createCCCBtn);
	}
	
	// ------------------------------------------------- Toolbar PDFViewer panel
	
	private AonToolbar getToolbarPDFViewerPanel() {

		AonToolbarButton closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		closePDF.ensureDebugId("closePDFViewerBtn");
		closePDF.addClickHandler(e -> {
			setPDFLoadedEnsureDebugId("pdfNotLoaded");
			onClosePDF();
		});
		toolbarPDFViewer.add(closePDF);
		
		pdfLoaded = new Label("");
		setPDFLoadedEnsureDebugId("pdfNotLoaded");
		toolbarPDFViewer.add(pdfLoaded);
		
		return toolbarPDFViewer;
	}
	
	public void setPDFLoadedEnsureDebugId(String debugId) {
		this.pdfLoaded.ensureDebugId(debugId);
	}
	
	// ---------------------------------------------- Toolbar.Methods

	private void onAccept() {
		showLoadingMessage("Guardando los cambios realizados en la actividad " + activityDraftObject.getActivityDescription() + " ...");
		if(checkIfSaveIsPossible()) updateActivity();
	}

	private void updateActivity() {
		activityDraftObject.updateActivity(
			s -> {
				activityDraftObject.initializeActivity(
						r -> {
							fillActivityInfo();
							activity.cccWidget.setDomain(activityDraftObject.getDomain());
							activity.cccWidget.resetPreview();
							activity.onInsertRows();
							setHasChange(false);
							toolbar.setTitle(activityDraftObject.getActivityDescription());
							
							showSuccessMessage(createMapMessage("Actividad actualizada", "Los cambios realizados se han guardado correctamente"));
						}, t -> {}
					);
			},
			f -> {}
		);
	}

	private boolean checkIfSaveIsPossible() {
		return !AonStringUtils.isBlank(activity.activityDescription.getValue());
	}
	
	// ---------------------------------------------- Toolbar.Methods TGSS
	
	public void onCheckUpdateCert() {
		showLoadingMessage("Obteniendo Cert. de estar al corriente con TGSS ...");
		Pair<String, String> completeCCC = activityDraftObject.getPrincipalAccount();
		
		activityDraftObject.getUpdateCert(completeCCC.getKey(), completeCCC.getValue(),
				dataURI -> {
					showPdf(false);
					setPDFLoadedEnsureDebugId("pdfLoaded");
					pdfViewer.open(dataURI);
					hideMessage();
				}, f -> {
					showWarningMessage(createMapMessage("Error obtenci\u00f3n TGSS", f.getMessage()));
				});
	}
	
	// ------------------------------------------------- Show/Hide Employee/PDF

	private void showActivity() {
		toolbarDeckPanel.showWidget(0);
		mainDeckPanel.showWidget(0);
	}

	private void showPdf(boolean isLaboralLife) {
		toolbarDeckPanel.showWidget(1);
		mainDeckPanel.showWidget(1);
		
		checkPDFToolbar(isLaboralLife);
	}
	
	private void checkPDFToolbar(boolean isLaboralLife) {
		if(isLaboralLife && toolbarPDFViewer.getButtonContainer().getWidgetCount() == 1) {
			MonthListBox monthListBox = new MonthListBox();
			Date lastMonth = DateUtils.getFirstDayOfMonth(); 
			Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -4);
			monthListBox.setFirstMonth(firstMonth);
			monthListBox.setLastMonth(lastMonth);
			monthListBox.setPageSize(52);
			monthListBox.setVisibleRange(0, 52);
			monthListBox.addChangeHandler(e -> this.activity.cccWidget.onLaboralLife(monthListBox.getSelected()));
			monthListBox.setSelected(DateUtils.getFirstDayOfMonth(), true);
			monthListBox.setWidth("200px");
			toolbarPDFViewer.add(monthListBox);
		} else if(!isLaboralLife && toolbarPDFViewer.getButtonContainer().getWidgetCount() > 2)
			toolbarPDFViewer.getButtonContainer().remove(toolbarPDFViewer.getButtonContainer().getWidgetCount()-1);
		
	}

	private void onClosePDF() {
		showActivity();
	}
	
	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		acceptButton.setEnabled(this.hasChange);
		undoAllButton.setEnabled(this.hasChange);
	}
	
	@SuppressWarnings("serial")
	private Map<String, String> createMapMessage(String title, String message) {
		return new HashMap<String, String>() {{ put(title, message); }};
	}
	
	protected abstract void showSuccessMessage(Map<String, String> messages);
	protected abstract void showErrorMessage(Map<String, String> messages);
	protected abstract void showWarningMessage(Map<String, String> messages);
	protected abstract void showInfoMessage(Map<String, String> messages);
	protected abstract void showLoadingMessage(String message);
	protected abstract void hideMessage();
	
}
