package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.FinishDeclarationPopup.FinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF.IModel390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HFIdentificationData.IModel390HFIdentificationDataCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public abstract class Model390HFBase extends DockLayoutPanel  {
	
	protected class Model390HFBaseCallback implements IModel390HFCallback{
		
		private IModel390HFCallback callback;
		
		public Mod390HF getMod390HF() {
			return Model390HFBase.this.mod390;
		}
		
		public Model390HFBaseCallback(IModel390HFCallback callback) {
			this.callback = callback;
		} 

		public void onAccept(Mod390HF mod390) {
			this.callback.onAccept(mod390);
		}
		public void onCancel() {
			this.callback.onCancel();
		}
		public void onNew() {
			this.callback.onNew();
		}
		public void showBreakdownPanel(String htmlText) {
			this.callback.showBreakdownPanel(htmlText);
		}
		public void cleanBreakdownPanel() {
			this.callback.cleanBreakdownPanel();
		}
		public void cleanErrorPanel() {
			this.callback.cleanErrorPanel();
		}
		public void showError(String msg) {
			this.callback.showError(msg);
		}
	};

	protected static final String DOWNLOAD_FILE_ACTION = "/aon_gwt_fiscal/Model390HFFile";
	private static final String MODEL303_PRINT = "/aon_gwt_fiscal/Model390HFPrint";

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	
	private Mod390HF mod390;
	private Model390HFBaseCallback callback;
	private EnumMap<Mod390Key,DoubleBox> fieldsMap;
	private boolean dirty;
	
	protected InlineLabel documentLabel = new InlineLabel();
	protected InlineLabel nameLabel = new InlineLabel();
	protected InlineLabel surnameLabel = new InlineLabel();
	protected FlowPanel paymentInfo = new FlowPanel();
	protected FlowPanel  dirtyPanel = new FlowPanel ();
	protected InlineLabel statusLabel = new InlineLabel();
	protected InlineLabel replacedLabel = new InlineLabel();
	protected InlineLabel prorataLabel = new InlineLabel();
	protected Button commentsButton = new Button();
	
	protected final Button newButton = new Button();
	protected final Button saveButton = new Button();
	protected final Button cancelButton = new Button();		
	protected final Button deleteButton = new Button();
	protected final Button printButton = new Button();
	protected final Button markAsPendingButton = new Button();
	protected final Button markAsFinishedButton = new Button();
	protected final Button markAsSentButton = new Button();
	protected final Button auditButton = new Button();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod390Hidden = new Hidden("mod390");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");

	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model390HF.FISCAL_SERVICE.mathExpression(expression,callback);
		}
	};
	
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	
	public Model390HFBase(Mod390HF mod390,Model390HFCallback cbk) {
		super(Unit.PX);
		select( mod390 );
		
		addNorth(getToolbarPanel(), 25);
		
		SimplePanel headerPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(headerPanel, this.mod390 );
		addNorth(headerPanel, 65);
		
		SimplePanel declarationHeaderPanel = new SimplePanel();
		paintDeclarationHeaderTable(declarationHeaderPanel);
		addNorth(declarationHeaderPanel , 45);

		fieldsMap = new EnumMap<>(Mod390Key.class);
		this.callback = new Model390HFBaseCallback(cbk);
		
		setStyleName(AON.AON_CSS.aonSelector());
	}
	
	public Model390HFBaseCallback getCallback() {
		return callback;
	}
	protected Mod390HF getMod390HF() {
		return mod390;
	}
	public void setMod390HF(Mod390HF mod390) {
		this.mod390 = mod390;
	}
	
	private Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label( "IVA. Autoliquidaci\u00F3n."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconReset());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.onNew();
			}
		});
		buttonContainer.add(newButton);
		
		
		saveButton.setText(AON.MSG.saveAction());
		saveButton.setTitle(newButton.getText());
		saveButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		saveButton.addStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
		buttonContainer.add(saveButton);
		
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.setTitle(cancelButton.getText());
		cancelButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		cancelButton.addStyleName(AON.AON_CSS.aonIconCancel());
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				if (isDirty()) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new ConfirmDialogCallback() {

						@Override
						public void onAccept() {
							callback.onCancel();
						}

						@Override
						public void onCancel() {
							cancelButton.setEnabled(true);
						}
					});
				} else {
					callback.onCancel();
				}
			}
		});
		buttonContainer.add(cancelButton);

		deleteButton.setText(AON.MSG.deleteAction());
		deleteButton.setTitle(deleteButton.getText());
		deleteButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		deleteButton.addStyleName(AON.AON_CSS.aonIconDelete());
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				delete();
			}
		});
		buttonContainer.add(deleteButton);
		
		markAsFinishedButton.setText(AON.MSG.finish());
		markAsFinishedButton.setTitle(markAsFinishedButton.getText());
		markAsFinishedButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsFinishedButton.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
		markAsFinishedButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onFinalize();
			}
		});
		buttonContainer.add(markAsFinishedButton);

		markAsSentButton.setText(AON.MSG.markAsSent());
		markAsSentButton.setTitle(markAsSentButton.getText());
		markAsSentButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsSentButton.addStyleName(AON.AON_CSS.aonIconPointGreen());
		markAsSentButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsSent();
			}
		});
		buttonContainer.add(markAsSentButton);

		markAsPendingButton.setText(AON.MSG.reopen());
		markAsPendingButton.setTitle(markAsPendingButton.getText());
		markAsPendingButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsPendingButton.addStyleName(AON.AON_CSS.aonIconPointOrange());
		markAsPendingButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reopenDeclaration();
			}
		});
		buttonContainer.add(markAsPendingButton);
		
		printButton.setText(AON.MSG.draft());
		printButton.setTitle(printButton.getText());
		printButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		printButton.addStyleName(AON.AON_CSS.aonIconExcel());
		printButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (isDirty()) {
					new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
							, new ConfirmDialogCallback() {
							
							@Override
							public void onAccept() {
								submitForm(MODEL303_PRINT);
							}
			
							@Override
							public void onCancel() {
								// Nothing
							}
						});
				} else {
					submitForm(MODEL303_PRINT);
				}
			}
		});
		buttonContainer.add(printButton);
		
		auditButton.setText(AON.MSG.audit());
		auditButton.setTitle(auditButton.getText());
		auditButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		auditButton.addStyleName(AON.AON_CSS.aonIconAudit());
		auditButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				audit();
			}
		});
		buttonContainer.add(auditButton);

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	protected void select( Mod390HF mod390) {
		setMod390HF(mod390);
		refreshToolbarState();
		styleStatusLabel(mod390);
	}
	protected void selectAndPopulate( Mod390HF mod390) {
		select(mod390);
		populate(mod390);
	}
	
	private void refreshToolbarState() {
		deleteButton.setVisible(!mod390.isNew());
		auditButton.setVisible(!mod390.isNew());
		newButton.setVisible(!mod390.isNew());
		cancelButton.setVisible(true);
		saveButton.setVisible(!mod390.isFinished() && !mod390.isSent());
		deleteButton.setVisible(!mod390.isFinished() && !mod390.isSent());
		markAsPendingButton.setVisible(!mod390.isNew() &&
				(mod390.getStatus() == FiscalStatus.FINISHED 
				|| mod390.getStatus() == FiscalStatus.BATCHED
				|| mod390.getStatus() == FiscalStatus.SENT
				|| mod390.getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!mod390.isNew() &&
				(mod390.getStatus() == FiscalStatus.PENDING 
				|| mod390.getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!mod390.isNew() &&
				(mod390.getStatus() == FiscalStatus.FINISHED));
	}

	protected void paintDeclarationHeaderTable(SimplePanel panel) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());
		table.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		table.getColumnFormatter().setWidth(0, "auto");
		
		table.getColumnFormatter().setWidth(1, "150px");
		table.getColumnFormatter().setWidth(2, "100px");
		table.getColumnFormatter().setWidth(3, "150px");
		table.getColumnFormatter().setWidth(4, "150px");
		table.getColumnFormatter().setWidth(5, "110px");
		
		FlowPanel cell1 = new FlowPanel();
		FlowPanel cell01 = new FlowPanel();
		cell01.setStyleName(AON.AON_CSS.aonFontBig());
		cell01.addStyleName(AON.AON_CSS.aonTextCenter());
		documentLabel.setText(this.mod390.getDocument());
		cell01.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(this.mod390.getName());
		cell01.add(nameLabel);
		surnameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		surnameLabel.setText(this.mod390.getSurname());
		cell01.add(surnameLabel);
		cell1.add(cell01);
		
		paymentInfo.setStyleName(AON.AON_CSS.aonFontSmall());
		paymentInfo.addStyleName(AON.AON_CSS.aonTextRight());
		FiscalModelUtils.paintPaymentInfo(paymentInfo,mod390);
		cell1.add(paymentInfo);
		
		table.setWidget(0, 0, cell1);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		if (mod390.isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (mod390.isComplementary()) {
			replacedLabel.setText("Complem.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		table.setWidget(0, 1, replacedLabel);
		table.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonTextCenter());
		
		if (AonMathUtils.isNotZero(mod390.getProratePercent()) &&  !AonMathUtils.equals(mod390.getProratePercent(), 100.0)) {
			prorataLabel.setText(AON.MSG.prorrata() + ": " + mod390.getProratePercent() + "%");
			prorataLabel.setStyleName(AON.AON_CSS.aonBold());
		}
		table.setWidget(0, 2, prorataLabel);
		table.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonTextCenter());
		
		styleDirtyLabel();
		table.setWidget(0, 3, dirtyPanel);
		table.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextCenter());
		
		styleStatusLabel(this.mod390);
		table.setWidget(0, 4, statusLabel);
		table.getCellFormatter().setStyleName(0, 4, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 4, AON.AON_CSS.aonNowrap());
		table.getCellFormatter().addStyleName(0, 4, AON.AON_CSS.aonTextCenter());
		
		FlowPanel commentsPanel = new FlowPanel();
		commentsButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		commentsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AonToast toast = new AonToast();
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(mod390.getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						mod390.setComments(event.getValue());
						styleCommentsButton();
						Model390HF.MOD_SERVICE.saveComments(Model390HF.getCurrentDomainName(), mod390, new AsyncCallback<Mod390HF>() {
							@Override
							public void onSuccess(Mod390HF result) {
								toast.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								toast.hide();
								callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							}
						});
						
						
						
					}
				});
				comment.setText(mod390.getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				toast.show(AON.MSG.comments(), commentPanel);
			}
		});
		commentsPanel.add(commentsButton);
		commentsPanel.add(new InlineLabel(AON.MSG.comments()));
		table.setWidget(0, 5, commentsPanel);
		styleCommentsButton();
		table.getCellFormatter().setStyleName(0, 5, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 5, AON.AON_CSS.aonTextCenter());

		panel.setWidget(table);
	}
	private void styleStatusLabel(Mod390HF mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(mod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod390.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod390.getComments());
	}
	
	
	protected EnumMap<Mod390Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	protected void paintAdditionalData(FlexTable table) {
		
	}
	protected void paintDeclaration(FlexTable table, IModelScript<Mod390Key>[] script, int colsNumber) {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		paintScript(table, script, colsNumber);
	}
	
	protected void paintScript(FlexTable table, IModelScript<Mod390Key>[] script, int colsNumber) {
		for (IModelScript<Mod390Key> ms : script) {
			paintRow(table,ms,colsNumber);	
		}
	}

	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
	}
	
	protected int paintEmptyCol(FlexTable table,int row, int col, int colspan) {
		table.setWidget(row, col, new Label());
		table.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().setColSpan(row, col, colspan);
		return ++col;
	}

	protected void paintRow(FlexTable table,IModelScript<Mod390Key> script, int colsNumber) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table,row,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, colsNumber);	
			} else {
				table.getFlexCellFormatter().setColSpan(row, 0, (colsNumber -  ( ( script.getKeys().length * 2) + 1) ) );
				int col = 1;
				for (Mod390Key key : script.getKeys()) {
					if (key == null) {
						col = paintEmptyCol(table,row, col, 2);
					} else {
						col = paintBox(table, row, col, key );
						col = paintField(table, row, col, script, key );
					}
				}
				paintInfoCol(table,row,col,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, IModelScript<Mod390Key> script) {
		
	}
	protected void paintLabel(FlexTable table,int row,IModelScript<Mod390Key> script) {
		paintLabel(table, row, script.getLabel(), script.isTitle());
	}
	protected void paintLabel(FlexTable table,int row,String labelText) {
		paintLabel(table, row, labelText, false);
	}
	protected void paintLabel(FlexTable table,int row,String labelText,boolean title) {
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		if (title) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft20() );
		}
	}
	
	private int paintBox(FlexTable table, int row, int col, Mod390Key key) {
		if (AonStringUtils.isNumeric(key.getBoxCode())) {
			table.setWidget(row, col, new BoxLabel(key.getBoxCode()));
		} else {
			table.setWidget(row, col, new Label());
		}
		return ++col;
	}
	
	protected int paintField(FlexTable table, int row, int col, IModelScript<Mod390Key> script, final Mod390Key key) {
		return paintField(table, row, col, key, script.getFieldSize(key), script.isEnabled( key ));
	}
	protected int paintField(FlexTable table, int row, int col, final Mod390Key key, int fieldSize, boolean enabled) {
		final FiscalModelDetail det1 = this.mod390.ensureDetail(key);
		final DoubleBox input = new DoubleBox(fieldSize);
		input.setResolver(resolver);
		fieldsMap.put(key, input);
		input.setEnabled(enabled); 
		input.addStyleName(AON.AON_CSS.aonPaddingLeft10Important());
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (input.getValue() == null) input.setValue(0.0,false);
				mod390.ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled()) {
					calculateAndRefresh();
				}
				markAsDirty();
			}
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	protected void paintWithoutActivityCheck(FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, AON.MSG.withoutActivity());
		final CheckBox check = new CheckBox();
		check.setValue(this.mod390.isWithoutActivity());
		check.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod390.setWithoutActivity(check.getValue());
				markAsDirty();
			}
		});
		table.setWidget(row, 1, check);
	}

	protected CheckBox paintCheck(Mod390Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		final CheckBox check = new CheckBox();
//		check.setEnabled(this.mod390.isNotFinished());
		check.setValue(this.mod390.ensureDetail(key).getAmount() == 1);
		check.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod390.ensureDetail(key).setAmount(check.getValue()?1.0:0.0);
				markAsDirty();
			}
		});
		table.setWidget(row, 1, check);
		return check;
	}
	
	protected void paintDate(Mod390Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		final DateBoxEx dateBox = new DateBoxEx();
//		dateBox.setEnabled(mod390.isNotFinished());
		if (AonStringUtils.isNotEmpty( mod390.ensureDetail(key).getDescription() ) ) {
			dateBox.setValue( dateBox.parse(mod390.ensureDetail(key).getDescription() , false) );
		}
		dateBox.addValueChangeHandler( new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				mod390.ensureDetail(key).setDescription(dateBox.format());
				markAsDirty();
			}
		});
		table.setWidget(row, 1, dateBox);
	}

	protected void paintListBox(ListBox listBox, Mod390Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
//		listBox.setEnabled(mod390.isNotFinished());
		listBox.setSelectedIndex( (int) mod390.ensureDetail(key).getAmount() );
		listBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				mod390.ensureDetail(key).setAmount(listBox.getSelectedIndex());
				markAsDirty();
			}
		});
		table.setWidget(row, 1, listBox);
	}

	protected FlowPanel addGroupPanel(String label, Widget w) {
		FlowPanel groupPanel = new FlowPanel();
		groupPanel.setStyleName(AON.AON_CSS.aonGroup());
		
			FlowPanel groupHeaderPanel = new FlowPanel();
			groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
			groupHeaderPanel.add (new InlineLabel(label)); 
			groupPanel.add(groupHeaderPanel);
			
			FlowPanel groupBodyPanel = new FlowPanel();
			groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
			groupBodyPanel.add(w);
			groupPanel.add(groupBodyPanel);
		return groupPanel;
	}

	private void paintInfoCol(FlexTable table, int row, int col, final IModelScript<Mod390Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonNowrap());
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			
			if 	(infoKey == FiscalModelKeyInfo.NONE) continue;

			final Button button = new Button("");
			button.setTitle(infoKey.getLabel());
			button.setStyleName(AON.AON_CSS.aonIconCommandButton());
			
			if 	(infoKey == FiscalModelKeyInfo.INVOICE || infoKey == FiscalModelKeyInfo.OUT_ACCRUAL_INVOICE 
			  || infoKey == FiscalModelKeyInfo.IN_ACCRUAL_INVOICE) {
				button.addStyleName(AON.AON_CSS.aonIconInvoice());
			}
			if 	(infoKey == FiscalModelKeyInfo.DIFF_INVOICE || infoKey == FiscalModelKeyInfo.DIFF_IN_ACCRUAL_INVOICE 
			  || infoKey == FiscalModelKeyInfo.DIFF_OUT_ACCRUAL_INVOICE) {
				button.addStyleName(AON.AON_CSS.aonIconDiff());
			}
			if 	(infoKey == FiscalModelKeyInfo.COMPUTE) button.addStyleName(AON.AON_CSS.aonIconCalculator());
			if 	(infoKey == FiscalModelKeyInfo.COMPUTE_KEY) button.addStyleName(AON.AON_CSS.aonIconCompanyData());
			button.setTabIndex(-2); // NO FOCUS
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Model390HF.MOD_SERVICE.getInfo(Model390HF.getCurrentDomainName(),Model390HF.getCurrentDomain(),
							mod390,script, infoKey,new AsyncCallback<String>() {

								@Override
								public void onFailure(Throwable caught) {
									callback.showError(AON.MSG.errorMessage());
								}

								@Override
								public void onSuccess(String result) {
									callback.showBreakdownPanel(result);
								}
						
							}
						);	
				}
			});
			buttonContainer.add(button);
		}
		table.setWidget(row, col, buttonContainer);
	}
	
	protected void populate(Mod390HF mod390) {
		for (Mod390Key key : fieldsMap.keySet()) {
			double d1 = mod390.getAmount(key);
			double d2 = fieldsMap.get(key).getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				fieldsMap.get(key).setValue(d1,true,true);
			}
		}
	}
	
	public void calculateAndRefresh(AsyncCallback<Mod390HF> cbk) {
		Model390HF.MOD_SERVICE.calculate(Model390HF.getCurrentDomainName(),this.mod390,
				new AsyncCallback<Mod390HF>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod390HF result) {
						populate(result);
						if (cbk != null) cbk.onSuccess(result);
					}
			
				}
			);	
	}

	public void calculateAndRefresh() {
		calculateAndRefresh(null);
	}
	
	private void identificationLabelChanged() {
		documentLabel.setText(mod390.getDocument());
		nameLabel.setText(mod390.getName());
		surnameLabel.setText(mod390.getSurname());
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

	protected void styleDirtyLabel() {
		dirtyPanel.clear();
		if ( isDirty()) {
			InlineLabel dirtyLabel = new InlineLabel("[*]");
			dirtyLabel.setStyleName(AON.AON_CSS.aonColorRed());
			dirtyPanel.add(dirtyLabel);
		}
		boolean adjusted = false;
		for (FiscalModelDetail det : this.mod390.getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		if (adjusted) {
			InlineLabel adjLabel = new InlineLabel("[AJUSTES]");
			adjLabel.setStyleName(AON.AON_CSS.aonMarginLeft5());
			adjLabel.addStyleName(AON.AON_CSS.aonColoRoyalblue());
			adjLabel.setTitle("Ajustes realizados");
			dirtyPanel.add(adjLabel);
		}
	}
	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod390HF> cbk) {
		saveButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model390HF.MOD_SERVICE.save(Model390HF.getCurrentDomainName(), this.mod390, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						selectAndPopulate(result);
						popup.hide();
						saveButton.setEnabled(true);
						if (cbk != null) cbk.onSuccess(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						saveButton.setEnabled(true);
					}
				});
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new ConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model390HF.MOD_SERVICE.delete(Model390HF.getCurrentDomainName(),mod390, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						callback.onCancel();
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						callback.showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}
	
	private void onFinalize() {
		markAsFinishedButton.setEnabled(false);
		callback.cleanErrorPanel();
		Model390HF.MOD_SERVICE.initializeForFinish(Model390HF.getCurrentDomainName(),mod390,
				new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						selectAndPopulate(result);
						showFinalizePopup();
						markAsFinishedButton.setEnabled(true);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup() {
		FinishDeclarationPopup finalizeDialog = new FinishDeclarationPopup(this.mod390, getCallback(), new FinishDeclarationPopupCallback() {
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				finish();
			}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private void finish() {
		markAsFinishedButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model390HF.MOD_SERVICE.markAsFinished(Model390HF.getCurrentDomainName(), mod390, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,mod390);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	private void reopenDeclaration() {
		markAsPendingButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model390HF.MOD_SERVICE.markAsPending(Model390HF.getCurrentDomainName(), mod390, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,mod390);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}
	
	private void markAsSent() {
		markAsSentButton.setEnabled(false);
		callback.cleanErrorPanel();
		Model390HF.MOD_SERVICE.markAsSent(Model390HF.getCurrentDomainName(), mod390, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						selectAndPopulate(result);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}

	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(mod390);
	}

	protected class Model390HFIdentificationDataCallback implements IModel390HFIdentificationDataCallback {

		@Override public boolean isFinished() 		{ return getMod390HF().isFinished() || getMod390HF().isSent();}
		
		@Override public String getDocument() 		{return getMod390HF().getDocument();			}
		@Override public String getName() 			{return getMod390HF().getName(); 				}
		@Override public String getSurname() 		{return getMod390HF().getSurname();			}
		@Override public String getPhone() 			{return getMod390HF().getPhone();				}
		@Override public String getStreetInitial() 	{return getMod390HF().getStreetInitial();		}
		@Override public String getStreetName() 	{return getMod390HF().getStreetName();		}
		@Override public String getStreetNumber() 	{return getMod390HF().getStreetNumber();		}
		@Override public String getStreetStair() 	{return getMod390HF().getStreetStair();		}
		@Override public String getStreetFloor() 	{return getMod390HF().getStreetFloor();		}
		@Override public String getStreetDoor() 	{return getMod390HF().getStreetDoor();		}
		@Override public String getTown() 			{return getMod390HF().getTown();				}
		@Override public String getProvince() 		{return getMod390HF().getProvince();			}
		@Override public String getZip() 			{return getMod390HF().getZip();				}
		@Override public String getContactPerson() 	{return getMod390HF().getContactPerson();		}
		@Override public String getContactPhone() 	{return getMod390HF().getContactPhone();		}
		@Override public String getContactCellular(){return getMod390HF().getContactCellular();	}
		@Override public String getContactEmail() 	{return getMod390HF().getContactEmail();		}
	
		@Override public void documentChanged(String value) {
			getMod390HF().setDocument(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void nameChanged(String value) {
			getMod390HF().setName(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void surnameChanged(String value) {
			getMod390HF().setSurname(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void phoneChanged(String value) {
			getMod390HF().setPhone(value);
			markAsDirty();			
		}
		@Override public void streetInitialChanged(String value) {
			getMod390HF().setStreetInitial(value);
			markAsDirty();			
		}
		@Override public void streetNameChanged(String value) {
			getMod390HF().setStreetName(value);
			markAsDirty();			
		}
		@Override public void streetNumberChanged(String value) {
			getMod390HF().setStreetNumber(value);
			markAsDirty();			
		}
		@Override public void streetStairChanged(String value) {
			getMod390HF().setStreetStair(value);
			markAsDirty();			
		}
		@Override public void streetFloorChanged(String value) {
			getMod390HF().setStreetFloor(value);
			markAsDirty();			
		}
		@Override public void streetDoorChanged(String value) {
			getMod390HF().setStreetDoor(value);
			markAsDirty();			
		}
		@Override public void townChanged(String value) {
			getMod390HF().setTown(value);
			markAsDirty();			
		}
		@Override public void provinceChanged(String value) {
			getMod390HF().setProvince(value);
			markAsDirty();			
		}
		@Override public void zipChanged(String value) {
			getMod390HF().setZip(value);
			markAsDirty();			
		}
		@Override public void contactPersonChanged(String value) {
			getMod390HF().setContactPerson(value);
			markAsDirty();			
		}
		@Override public void contactPhoneChanged(String value) {
			getMod390HF().setContactPhone(value);
			markAsDirty();			
		}
		@Override public void contactCellularChanged(String value) {
			getMod390HF().setContactCellular(value);
			markAsDirty();			
		}
		@Override public void contactMailChanged(String value) {
			getMod390HF().setContactEmail(value);
			markAsDirty();			
		}
	}
	
	protected FlowPanel getInformationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(mod390.getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(mod390.getAdministration()));
			tab.setWidget(row, 0, icon );
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.AON_CSS.aonPadding2());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.AON_CSS.aonPaddingLeft());
			p.add(a);
			tab.setWidget(row, 1, p );
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		}
		panel.add(tab);
		return panel;
	}

	protected void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod390Hidden.setValue(String.valueOf(getMod390HF().getId()));
		domainIdHidden.setValue(String.valueOf(Model390HF.getCurrentDomain()));
		domainNameHidden.setValue(Model390HF.getCurrentDomainName());
		diskForm.submit();
	}

	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
}
