package com.esferalia.aon.gwt.fiscal.client.mod390.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class Model3902022 extends DockLayoutPanel  {
	
	private static final Integer DEFAULT_YEAR = 2022;
	private static final String MOD390_2022_DRAFT = "/aon_gwt_fiscal/Model3902022Draft";
			
	protected static final Mod3902022ServiceAsync MOD3902022_SERVICE;
	static {
		Mod3902022ServiceAsync mod3902022ServiceRaw = GWT.create(Mod3902022Service.class);
		MOD3902022_SERVICE = new Mod3902022ServiceAsyncDecorator(mod3902022ServiceRaw);
	}
	
	class Model3902022Callback {
		void markAsDirty() {
			Model3902022.this.markAsDirty();
		}
		Mod3902022 getModel() {
			return Model3902022.this.getModel(); 
		}
		void setModel(Mod3902022 model) {
			Model3902022.this.setModel(model);			   
		}
		void refreshDeclarationToolbarPanel() {
			Model3902022.this.refreshDeclarationToolbarPanel();			
		}		
	}
	
	private SimpleLayoutPanel contentContainer = new SimpleLayoutPanel();
	private final Model390Callback callback; 
	private Mod3902022 model;
	private boolean dirty;
	protected final InlineLabel dirtyLabel = new InlineLabel();
	private int pageSelected = -1;
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod303Hidden = new Hidden("mod390");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	protected final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	protected final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	protected final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	protected final Label statusLabel = new Label();

	public Model3902022(final Model390Callback mod390Callback, Mod390 mod390) {
		super(Unit.PX);
		
		this.callback = mod390Callback; 
		
		AON.ensureInjected();
		
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		try {
			MOD3902022_SERVICE.get(getCallback().getOptions().getOccam(), mod390, new AsyncCallback<Mod3902022>() {
				@Override
				public void onSuccess(Mod3902022 selected) {
					if (selected == null) {
						mod390Callback.showError(AON.MSG.unableToFindDeclaration());
					} else {
						select(selected);
					}
					popup.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					popup.hide();
					mod390Callback.showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
				}
			});
		} catch (IllegalArgumentException e) {
			popup.hide();
			mod390Callback.showError(e.getMessage());
		}
	}
	public Model390Callback getCallback() {
		return callback;
	}
	public Mod3902022 getModel() {
		return model;
	}
	public void setModel(Mod3902022 model) {
		this.model = model;
	}
	protected void markAsDirty() {
		setDirty(true);
	}
	private boolean isDirty() {
		return this.dirty;
	}
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel();
	}
	
	private Widget getToolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));

		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconBack() );
		if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event -> {
			if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
				getCallback().getOptions().getExternalCallback().onExit(getModel());
			} else {
				getCallback().onCancel( getModel() );
			}
		});
		toolbarPanel.add(cancelButton);
		
		
		AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		newButton.setVisible(!getModel().isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		newButton.addClickHandler(event ->  getCallback().onNew(DEFAULT_YEAR));
		toolbarPanel.add(newButton);
		
		AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.setVisible(!getModel().isFinished() && !getModel().isSent());
		saveButton.addClickHandler(event -> {
			saveButton.setEnabled(false);
			final PopupPanel popup = new PopupPanel(false, true);
			popup.add(new AonSplash());
			popup.setGlassEnabled(true);
			popup.setAnimationEnabled(true);
			popup.center();
			try {
				getModel().setDomain(getCallback().getOptions().getDomain());
				getModel().setConfidential(false);
				getCallback().cleanErrorPanel();
				MOD3902022_SERVICE.save(getCallback().getOptions().getOccam(),getModel(), new AsyncCallback<Mod3902022>() {
					@Override
					public void onSuccess(Mod3902022 result) {
						select(result);
						popup.hide();
						saveButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						saveButton.setEnabled(true);
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			} catch (IllegalArgumentException e) {
				popup.hide();
				getCallback().showError(e.getMessage());
			}
		});
		toolbarPanel.add(saveButton);


		AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		deleteButton.setVisible(!getModel().isNew() && !getModel().isFinished() && !getModel().isSent());
		deleteButton.addClickHandler(event -> {
			deleteButton.setEnabled(false);
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					MOD3902022_SERVICE.delete(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							deleteButton.setEnabled(true);
							getCallback().cleanErrorPanel();
							getCallback().onRemove( getModel() );
						}

						@Override
						public void onFailure(Throwable caught) {
							deleteButton.setEnabled(true);
							getCallback().showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}
				
			});
		});
		toolbarPanel.add(deleteButton);
		
		AonToolbarButton draftButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
		draftButton.addClickHandler(event -> submitForm(MOD390_2022_DRAFT));
		toolbarPanel.add(draftButton);
		
		
		AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
		commentsButton.addClickHandler( event -> {
			final AonToast toast = new AonToast();
			FlowPanel commentPanel = new FlowPanel();
			commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()) );
			commentPanel.addStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				getModel().setComments(event1.getValue());
				styleCommentsButton(commentsButton);
				Model390.MOD390_SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod390>() {
					@Override
					public void onSuccess(Mod390 result) {
						toast.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						toast.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			});
			comment.setText(getModel().getComments());
			comment.setWidth("90%");
			comment.setHeight("5em");
			commentPanel.add(comment);
			toast.show(AON.MSG.comments(), commentPanel);
		});
		styleCommentsButton( commentsButton );
		toolbarPanel.add( commentsButton );
		
		
		AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
		auditButton.setVisible(!getModel().isNew());
		auditButton.addClickHandler( event -> audit());
		toolbarPanel.add(auditButton);
		
		toolbarPanel.add(diskForm);
		
		return toolbarPanel;
	}

	private void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod303Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod303Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}
	
	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
	}

	public void select(Mod3902022 m390) {
		if ("INVALID".equals(m390.getXmlFormat()) ) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm( "La declaraci\u00F3n est\u00E1 creada con un formato anterior al del ejercicio 2018 y es inv\u00E1lida. \u00BFDesea borrarla?"
					, new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					MOD3902022_SERVICE.delete(getCallback().getOptions().getOccam(),
							m390, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							getCallback().cleanErrorPanel();
							getCallback().onRemove( m390 );
						}

						@Override
						public void onFailure(Throwable caught) {
							getCallback().showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public void onCancel() {
					getCallback().cleanErrorPanel();
					getCallback().onCancel( m390 );
				}
			});
		} else {
			setModel(m390);
			clear();
			
			AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(getModel());
			addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
			addNorth(getToolbar(), AonToolbar.HEIGTH);
			addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
			
			addWest( getLinksPanel(), 300 );
			add(contentContainer);
		}
	}

	private Widget getLinksPanel() {
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel pageLinks = new FlowPanel();
		pageLinks.setStyleName(AON.CSS.aonPaddingLeft());
		
		AonLink page00Link = new AonLink(AON.MSG.pasiveSubjectAndAccrual());
		AonLink page01Link = new AonLink(AON.MSG.stadisticalData());
		AonLink page02Link = new AonLink(AON.MSG.representativeData());
		AonLink page03Link = new AonLink(AON.MSG.generalRegimeOperations());
		AonLink page04Link = new AonLink(AON.MSG.simplifiedRegimeOperations());
		AonLink page05Link = new AonLink(AON.MSG.annualLiquidationResult());
		AonLink page06Link = new AonLink(AON.MSG.taxByTerritory());
		AonLink page07Link = new AonLink(AON.MSG.liquidationsResult());
		AonLink page08Link = new AonLink(AON.MSG.operationsVolume());
		AonLink page09Link = new AonLink(AON.MSG.specificOperations());
		AonLink page10Link = new AonLink(AON.MSG.prorrata());
		AonLink page11Link = new AonLink(AON.MSG.difActivitiesRegime());
		AonLink page12Link = new AonLink("Agencia Tributaria");
		
		pageLinks.add(page00Link);
		pageLinks.add(page01Link);
		pageLinks.add(page02Link);
		pageLinks.add(page03Link);
		pageLinks.add(page04Link);
		pageLinks.add(page05Link);
		pageLinks.add(page06Link);
		pageLinks.add(page07Link);
		pageLinks.add(page08Link);
		pageLinks.add(page09Link);
		pageLinks.add(page10Link);
		pageLinks.add(page11Link);
		pageLinks.add(page12Link);
		
		Model3902022Callback cbk = new Model3902022Callback();
		page00Link.addClickHandler(event -> showContent(pageLinks, 0,new Page00(cbk),false)); 
		page01Link.addClickHandler(event -> showContent(pageLinks, 1,new Page01(cbk),false));
		page02Link.addClickHandler(event -> showContent(pageLinks, 2,new Page02(cbk),false));
		page03Link.addClickHandler(event -> showContent(pageLinks, 3,new Page03(cbk),false));
		page04Link.addClickHandler(event -> showContent(pageLinks, 4,new Page04(cbk),false));
		page05Link.addClickHandler(event -> showContent(pageLinks, 5,new Page05(cbk),false));
		page06Link.addClickHandler(event -> showContent(pageLinks, 6,new Page06(cbk),false));
		page07Link.addClickHandler(event -> showContent(pageLinks, 7,new Page07(cbk),false));
		page08Link.addClickHandler(event -> showContent(pageLinks, 8,new Page08(cbk),false));
		page09Link.addClickHandler(event -> showContent(pageLinks, 9,new Page09(cbk),false));
		page10Link.addClickHandler(event -> showContent(pageLinks,10,new Page10(cbk),false));
		page11Link.addClickHandler(event -> showContent(pageLinks,11,new Page11(cbk),false));
		page12Link.addClickHandler(event -> showContent(pageLinks,12,new Page12(getCallback(), cbk),false));
		
		scrollPanel.add(pageLinks);
		if (pageSelected == -1) {
			showContent(pageLinks, 0,new Page00(cbk),false);
		}
		return scrollPanel;
	}
	
	private void showContent( FlowPanel pageLinks,int idx, Widget page, boolean scroll) {
		if (idx != 	pageSelected) {
			pageSelected = idx;
			for ( int i = 0; i < pageLinks.getWidgetCount(); i++) {
				if (i == pageSelected) {
					pageLinks.getWidget(i).addStyleName(AON.CSS.aonBackgroundLigthGray());
				} else {
					pageLinks.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthGray());
				}
			}
			if (scroll) {
				ScrollPanel pageContainer = new ScrollPanel();
				pageContainer.addStyleName(AON.CSS.aonScrollArea());
				pageContainer.setWidget(page);
				contentContainer.setWidget(pageContainer);
			} else {
				contentContainer.setWidget(page);
			}
		}
	}
	
	private static class AonLink extends FocusPanel {
		
		protected AonLink(String label) {
			setStyleName(AON.CSS.aonWidthAll());
			FlowPanel container = new FlowPanel();
			container.setWidth("95%");
			container.setStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonClickableBlock());
			container.addStyleName(AON.CSS.aonFlexBlock());
			container.addStyleName(AON.CSS.aonBorder());
			container.getElement().getStyle().setMarginTop(5.0, Unit.PX);
			container.getElement().getStyle().setProperty("min-height", "30px");
			
			InlineLabel cardLabel = new InlineLabel( label );
			cardLabel.getElement().getStyle().setPaddingLeft(5.0, Unit.PX);
			container.add( cardLabel );
			setWidget(container);
		}
	}

	private AonToolbar getDeclarationToolbarPanel() {
		AonToolbar decToolbar = new AonToolbar();
		
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler(event -> {
			markAsFinishedButton.setEnabled(false);
			MOD3902022_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod3902022>() {
				@Override
				public void onSuccess(Mod3902022 result) {
					getCallback().reload(result.getId());
				}

				@Override
				public void onFailure(Throwable caught) {
					markAsFinishedButton.setEnabled(true);
					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				}
			});
		});
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler(event -> {
			markAsSentButton.setEnabled(false);
			MOD3902022_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.SENT, new AsyncCallback<Mod3902022>() {
				@Override
				public void onSuccess(Mod3902022 result) {
					getCallback().reload(result.getId());
				}

				@Override
				public void onFailure(Throwable caught) {
					markAsSentButton.setEnabled(true);
					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				}
			});
		});
		decToolbar.add(markAsSentButton);
		
		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler(event -> {
			markAsPendingButton.setEnabled(false);
			MOD3902022_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod3902022>() {
				@Override
				public void onSuccess(Mod3902022 result) {
					getCallback().reload(result.getId());
				}

				@Override
				public void onFailure(Throwable caught) {
					markAsPendingButton.setEnabled(true);
					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				}
			});
		});
		decToolbar.add(markAsPendingButton);

		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
	
		InlineLabel replacedLabel = new InlineLabel();		
		if (getModel().isReplacement()) {
			replacedLabel.setText(AON.MSG.replacement());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		if (getModel().isComplementary()) {
			replacedLabel.setText( AON.MSG.complementary());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		marksPanels.add(replacedLabel);

		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		dirtyLabel.getElement().getStyle().setWidth(10, Unit.PX);
		dirtyLabel.getElement().getStyle().setHeight(10, Unit.PX);
		styleDirtyLabel();
		marksPanels.add(dirtyLabel);

		decToolbar.getMessagePanel().add(marksPanels);
		
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
		
		decToolbar.setTitle(statusLabel);
		
		refreshDeclarationToolbarPanel();
		
		return decToolbar;
	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
	}

	private void styleCommentsButton(Button commentsButton) {
		if (AonStringUtils.isEmpty(getModel().getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(getModel().getComments());
	}
	
	private void refreshDeclarationToolbarPanel() {
		
		markAsFinishedButton.setVisible(!getModel().isNew() && (getModel().getStatus() == FiscalStatus.PENDING || getModel().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getModel().isNew() && (getModel().isFinished()));		
		markAsPendingButton.setVisible(!getModel().isNew() && (getModel().isFinished() || getModel().getStatus() == FiscalStatus.BATCHED || getModel().isSent()));
		
		statusLabel.setText(getModel().getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( getModel().getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( getModel().getStatus() ));
		
	}

}
