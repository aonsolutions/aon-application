package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class ActivityDraft extends AonCustomDockLayout {
	
	// ---------------------------------------------- Activity

	private class ActivityImplementation extends Activity{

		protected ActivityImplementation(com.esferalia.aon.occam.api.model.payroll.Activity activity) {
			super(activity);
		}

		@Override
		public void onActivityDescriptionChange(String description) {
			activityDraftObject.setActivityDescription(description);
			setToolbarTitle(description);
			setHasChange(true);
		}

		@Override
		public void onActivityCNAE2009Change(Integer cnaeId, String cnaeCode, String cnaeTitle) {
			activityDraftObject.setActivityCNAE2009(cnaeId, cnaeCode, cnaeTitle);
			setHasChange(true);
		}

		@Override
		public void onActivityStartDateChange(Date startDate) {
			activityDraftObject.setActivityStartDate(startDate);
			setHasChange(true);
		}

		@Override
		public void onActivityEndDateChange(Date endDate) {
			activityDraftObject.setActivityEndDate(endDate);
			setHasChange(true);
		}

		@Override
		public void onActivityActiveChange(Boolean principal) {
			activityDraftObject.setActivityIsPrincipal(principal);
			setHasChange(true);
		}

		@Override
		public void showPDF(String dataURI, boolean isLaboralLife) {
			setPDFLoadedEnsureDebugId("pdfLoaded");
			showPdf(isLaboralLife);
			pdfViewer.open(dataURI);
		}
		
	}
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private DeckPanel mainDeckPanel;
	private Activity activity;
	
	private HTMLPanel pdfContent = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePdf = new HTMLPanel(AonStringUtils.EMPTY);
	private FullViewer pdfViewer;
	
	// ---------------------------------------------- Variables
	
	private ActivityDraftObject activityDraftObject;
	
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton checkUpdateCert;
	private AonToolbarButton createCCCBtn;
	private AonToolbarButton closePDF;
	private Label pdfLoaded;
	private MonthListBox monthListBox;
	
	private boolean hasChange;
	
	// ---------------------------------------------- Constructor

	public ActivityDraft() {	
		super("Actividad");
		createToolbar();
		hideSearchWidget();
	}
	
	@Override
	protected void onClearFilter() {}
	
	// ---------------------------------------------- setActivityDraftObject

	public void setActivityDraftObject(ActivityDraftObject activityDraftObject) {
		this.activityDraftObject = activityDraftObject;
		
		activityDraftObject.initializeActivity(r -> {
			if(null != mainDeckPanel) remove(mainDeckPanel);
			
			mainDeckPanel = new DeckPanel();
			activity = new ActivityImplementation(activityDraftObject.getActivity());
			
			pdfContent.addStyleName(AON.CSS.aonFlexColumn());
			pdfViewer = new FullViewer();
			
			pdfContent.add(messagePdf);
			pdfContent.add(pdfViewer);
			
			mainDeckPanel.add(activity);
			mainDeckPanel.add(pdfContent);
			
			showActivity();
			setHasChange(false);
			setToolbarTitle(activityDraftObject.getActivityDescription());
			activity.hideMessage();
			
			Scheduler.get().scheduleDeferred(() -> {
				add(mainDeckPanel);
			});
			
		}, f -> {});
	}
	
	// ---------------------------------------------- Toolbar
	
	private void createToolbar() {
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> onAccept());
		addToolbarButton(acceptButton);
		
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
					activity.showLoading("Deshaciendo los cambios realizados ...");
					setActivityDraftObject(activityDraftObject);
				}
			});
		});
		undoAllButton.ensureDebugId("undoAllBtn");
		addToolbarButton(undoAllButton);
		
		checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.ensureDebugId("checkUpdateCert");
		checkUpdateCert.addClickHandler(e -> onCheckUpdateCert());
		addToolbarButton(checkUpdateCert);
		
		createCCCBtn = new AonToolbarButton(AON.MSG.newAction() + " CCC", AON.CSS.aonIconAdd() );
		createCCCBtn.addClickHandler(e -> activity.createCCC());
		createCCCBtn.ensureDebugId("createCCCBtn");
		addToolbarButton(createCCCBtn);
		
		closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		closePDF.setVisible(false);
		closePDF.ensureDebugId("closePDFViewerBtn");
		closePDF.addClickHandler(e -> {
			setPDFLoadedEnsureDebugId("pdfNotLoaded");
			onClosePDF();
		});
		addToolbarButton(closePDF);
		
		pdfLoaded = new Label("");
		pdfLoaded.setVisible(false);
		setPDFLoadedEnsureDebugId("pdfNotLoaded");
		addToolbarButton(pdfLoaded);
		
		monthListBox = new MonthListBox();
		Date lastMonth = DateUtils.getFirstDayOfMonth(); 
		Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -4);
		monthListBox.setFirstMonth(firstMonth);
		monthListBox.setLastMonth(lastMonth);
		monthListBox.setPageSize(52);
		monthListBox.setVisibleRange(0, 52);
		monthListBox.addChangeHandler(e -> onLaboralLife(monthListBox.getSelected()));
		monthListBox.setSelected(DateUtils.getFirstDayOfMonth(), true);
		monthListBox.setWidth("200px");
		monthListBox.setVisible(false);
		
		monthListBox.getElement().getStyle().setProperty("background", "none");
		monthListBox.getElement().getStyle().setProperty("border", "none");
		monthListBox.getElement().getStyle().setProperty("border-bottom", "1px solid #c3c3c3");
		monthListBox.getElement().getStyle().setProperty("padding-bottom", ".2rem");
		
		addToolbarButton(monthListBox);
	}
	public void setPDFLoadedEnsureDebugId(String debugId) {
		this.pdfLoaded.ensureDebugId(debugId);
	}
	
	// ---------------------------------------------- Toolbar.Methods

	private void onAccept() {
		activity.showLoading("Guardando los cambios realizados en la actividad " + activityDraftObject.getActivityDescription() + " ...");
		if(checkIfSaveIsPossible()) updateActivity();
	}

	private void updateActivity() {
		activityDraftObject.updateActivity(
			s -> {
				activityDraftObject.initializeActivity(
						r -> {
							activity.showSuccess("Los cambios realizados se han guardado correctamente");
							setActivityDraftObject(activityDraftObject);
						}, t -> {}
					);
			},
			f -> {}
		);
	}

	private boolean checkIfSaveIsPossible() {
		return !AonStringUtils.isBlank(activityDraftObject.getActivityDescription());
	}
	
	// ---------------------------------------------- Toolbar.Methods TGSS
	
	public void onCheckUpdateCert() {
		activity.showLoading("Obteniendo Cert. de estar al corriente con TGSS ...");
		Pair<String, String> completeCCC = activityDraftObject.getPrincipalAccount();
		
		activityDraftObject.getUpdateCert(completeCCC.getKey(), completeCCC.getValue(),
				dataURI -> {
					showPdf(false);
					setPDFLoadedEnsureDebugId("pdfLoaded");
					pdfViewer.open(dataURI);
					activity.hideMessage();
				}, f -> 
					activity.showError("Error obtenci\u00f3n TGSS : " + f.getMessage())
				);
	}
	
	public void onLaboralLife(Date date) {
		AonMessagePanel.showLoading(messagePdf, "Obteniendo Informe de Vida Laboral ...");
		impl.getCCCLaboralLife(activity.getCCCWidget().getRegimen(), activity.getCCCWidget().getCcc(), date, new Date(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				setPDFLoadedEnsureDebugId("pdfLoaded");
				showPdf(true);
				pdfViewer.open(dataURI);
				
				AonMessagePanel.hideMessage(messagePdf);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showWarning(messagePdf, "TGSS Vida Laboral : " + caught.getMessage());
			}
		});
	}
	
	// ------------------------------------------------- Show/Hide Employee/PDF

	private void showActivity() {
		acceptButton.setVisible(true);
		undoAllButton.setVisible(true);
		checkUpdateCert.setVisible(true);
		createCCCBtn.setVisible(true);
		closePDF.setVisible(false);
		pdfLoaded.setVisible(false);
		monthListBox.setVisible(false);
		
		mainDeckPanel.showWidget(0);
	}

	private void showPdf(boolean isLaboralLife) {
		acceptButton.setVisible(false);
		undoAllButton.setVisible(false);
		checkUpdateCert.setVisible(false);
		createCCCBtn.setVisible(false);
		closePDF.setVisible(true);
		pdfLoaded.setVisible(true);
		
		mainDeckPanel.showWidget(1);
		
		checkPDFToolbar(isLaboralLife);
	}
	
	private void checkPDFToolbar(boolean isLaboralLife) {
		monthListBox.setVisible(isLaboralLife);
	}

	private void onClosePDF() {
		showActivity();
	}
	
	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		acceptButton.setEnabled(this.hasChange);
		undoAllButton.setEnabled(this.hasChange);
	}
	
}
