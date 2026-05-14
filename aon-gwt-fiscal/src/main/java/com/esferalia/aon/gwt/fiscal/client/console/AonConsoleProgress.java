package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType.ConsoleDomainMessageTypeVisitor;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType.Visitor;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonConsoleProgress extends DockLayoutPanel implements IConsoleLogger {
	
	private static final int MAX_MESSAGES_PER_GRID = 500;
	private static final String PX600 = "600px";
	
	private final FlowPanel headerPanel; 
	private final ScrollPanel scrollPanel;
	private final FlowPanel topContainer;
	private final FlowPanel bottomContainer;
	private final InlineLabel title; 
	private final AonConsoleProgressPanel main;
	private final HashMap<String,AonConsoleProgressPanel> labels = new HashMap<>();
	private final boolean advancedMode;
	
	private final EnumMap<ConsoleDomainMessageType,AonDisplayGrid> grids = new EnumMap<>(ConsoleDomainMessageType.class);
	
	public AonConsoleProgress() {
		this(false);
	}
	
	public AonConsoleProgress(boolean advancedMode) {
		super(Unit.PX);
		this.advancedMode = advancedMode;
		
		headerPanel = new FlowPanel();
		headerPanel.setStyleName(AON.CSS.aonBorderBottom());
		headerPanel.addStyleName(AON.CSS.aonPadding());
		
		title = new InlineLabel();
		title.setStyleName(AON.CSS.aonFontLarger());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUnderline());
		title.addStyleName(AON.CSS.aonMarginBottom());
		title.addStyleName(AON.CSS.aonMarginRight());
		headerPanel.add(title);

		main = new AonConsoleProgressPanel();
		main.addStyleName(AON.CSS.aonWidthAlmostAll());
		main.addStyleName(AON.CSS.aonInline());
		main.setVisible(false);
		headerPanel.add(main);
		
		addNorth(headerPanel, 35);
		
		SimpleLayoutPanel slp = new SimpleLayoutPanel();
		scrollPanel = new ScrollPanel();
		setStyleName(AON.CSS.aonScrollArea());
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonPadding());
		scrollPanel.add(container);
		topContainer = new FlowPanel();
		container.add(topContainer);
		bottomContainer = new FlowPanel();
		container.add(bottomContainer);
		slp.setWidget(scrollPanel);
		add(slp);
		
	}

	public void reset() {
		title.setTitle(null);
		main.setVisible(false);
		main.setProgress(0,null);
		topContainer.clear();
		grids.clear();
		bottomContainer.clear();
	}
	
	public void setMainProgress(double percent, String msg) {
		main.setVisible(true);
		main.setProgress(percent,msg);
	}

	@Override
	public void log(JsConsoleMessage message) {
		if (message == null) return;
		
		message.getType().visit( new ConsoleMessageVisitor(message));
		scrollPanel.scrollToBottom();
	}
	
	protected void showWarning(String message) {
		Label messageLabel = new Label(message);
		messageLabel.setStyleName(AON.CSS.aonColorOrange());
		messageLabel.addStyleName(AON.CSS.aonBold());
		messageLabel.addStyleName(AON.CSS.aonMarginLeft());
		topContainer.add(messageLabel);
	}



	private final class ConsoleMessageVisitor implements Visitor {
		private final JsConsoleMessage message;

		private ConsoleMessageVisitor(JsConsoleMessage message) {
			this.message = message;
		}

		private AonConsoleProgressPanel getProgressPanel() {
			return labels.computeIfAbsent(message.getProcessId()
				, k -> {
					AonConsoleProgressPanel w = new AonConsoleProgressPanel();
					topContainer.add(w);
					return w;
			});
		}

		@Override
		public void visitTitle() {
			title.setText(message.getMessage());
		}
		
		@Override
		public void visitSubtitle() {
			Label messageLabel = new Label(message.getMessage());
			messageLabel.setStyleName(AON.CSS.aonMarginTop());
			messageLabel.addStyleName(AON.CSS.aonBold());
			topContainer.add(messageLabel);
		}
		@Override
		public void visitMessage() {
			Label label = new Label(message.getMessage());
			label.setStyleName(AON.CSS.aonMarginLeft());
			topContainer.add(label);
		}

		@Override
		public void visitMainProgress() {
			setMainProgress(message.getPercent(), message.getMessage());
		}

		@Override
		public void visitProgress() {
			getProgressPanel().setProgress(message.getPercent(), message.getMessage());
		}

		@Override
		public void visitOk() {
			Label messageLabel = new Label(message.getMessage());
			messageLabel.setStyleName(AON.CSS.aonColorGreen());
			messageLabel.addStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonMarginLeft());
			topContainer.add(messageLabel);
		}

		@Override
		public void visitWarning() {
			AonConsoleProgress.this.showWarning( message.getMessage() );
		}

		@Override
		public void visitError() {
			Label messageLabel = new Label(message.getMessage());
			messageLabel.setStyleName(AON.CSS.aonColorRed());
			messageLabel.addStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonMarginLeft());
			topContainer.add(messageLabel);
		}

		@Override
		public void visitConsoleMessage() {
			if (message.getConsoleDomainMessage() != null) {
				JsConsoleDomainMessage jsDomainMessage = message.getConsoleDomainMessage();
				ConsoleDomainMessage domainMessage = jsDomainMessage.getConsoleDomainMessage();
				ConsoleDomainMessageType type = domainMessage.getType(); 
				type.visit(domainMessage, new MessageTypeVisitor(type));
			}
		}

	}


	static class AonConsoleProgressPanel extends FlowPanel {
		private InlineLabel messageLabel = new InlineLabel();
		private AonConsoleProgressBar bar = new AonConsoleProgressBar();
		
		AonConsoleProgressPanel() {
			setStyleName(AON.CSS.aonMargin());
			messageLabel.setStyleName(AON.CSS.aonItalic());
			messageLabel.addStyleName(AON.CSS.aonPaddingLeft());
			bar.setWidth("200px");
			add(bar);
			add(messageLabel);
		}
		
		void setMesssageLabel(String message) {
			messageLabel.setText(message);
		}
		
		public void setProgress(double percent, String msg) {
			bar.setProgress(percent);
			messageLabel.setText(" (" + AON.FMT.format(percent) +  "% ) " + msg);
		}
		
	}
	
	
	static class AonConsoleProgressBar extends Widget {
		private static final double MAX = 100;
	    private final Element progress;

	    public AonConsoleProgressBar() {
	    	this(0.0);
	    }
	    public AonConsoleProgressBar(double  percent) {
	        progress = DOM.createElement("progress");
	        progress.setAttribute("max", Double.toString(MAX));
	        progress.setAttribute("value", Double.toString(percent));
	        setElement(progress);
	    }

	    public void setProgress(double  percent) {
	        progress.setAttribute("value", Double.toString(percent));
	    }

	}

	
	private final class GridProvider implements ConsoleDomainMessageTypeVisitor<AonDisplayGrid> {
		private final AonDisplayGrid grid;

		private GridProvider(AonDisplayGrid grid) {
			this.grid = grid;
		}

		@Override
		public AonDisplayGrid visitIntegrity(ConsoleDomainMessage cdm) {
			grid.addHeaderRow()
				.addCell(new Label("Tipo"),AON.CSS.aonWidth80())
				.addCell(new Label(""),AON.CSS.aonWidth20())
				.addCell(new Label("Tabla"),AON.CSS.aonWidth120())
				.addCell(new Label("ID"),AON.CSS.aonWidth120())
				.addCell(new Label("Columna"),AON.CSS.aonWidth120())
				.addCell(new Label("Tabla Ref."),AON.CSS.aonWidth150())
				.addCell(new Label("ID Ref."),AON.CSS.aonWidth120())
				.addCell(new Label("ID Dominio Error."),AON.CSS.aonWidth40())
				.addCell(new Label("Mensaje."));
			return grid;
		}

		@Override
		public AonDisplayGrid visitScopeIntegrity(ConsoleDomainMessage cdm) {
			grid.addHeaderRow()
				.addCell(new Label("Tipo"),AON.CSS.aonWidth80())
				.addCell(new Label(""),AON.CSS.aonWidth20())
				.addCell(new Label("Tabla"),AON.CSS.aonWidth120())
				.addCell(new Label("Count"),AON.CSS.aonWidth120())
				.addCell(new Label("ID Scope Error."),AON.CSS.aonWidth100(), AON.CSS.aonNowrap() )
				.addCell(new Label(""),AON.CSS.aonWidth20())
				.addCell(new Label("Mensaje."));
			return grid;
		}

		@Override public AonDisplayGrid visitProduct(ConsoleDomainMessage cdm) { return grid; }
		@Override public AonDisplayGrid visitAgreement(ConsoleDomainMessage cdm) { return grid; }
		@Override public AonDisplayGrid visitInvoiceProcessOutput(ConsoleDomainMessage cdm) { return grid; }
	}
	
	private final class MessageTypeVisitor implements ConsoleDomainMessageTypeVisitor<Void> {
		private final ConsoleDomainMessageType type;

		private MessageTypeVisitor(ConsoleDomainMessageType type) {
			this.type = type;
		}

		@Override
		public Void visitIntegrity(ConsoleDomainMessage domainMessage) {
			if (getGrid(domainMessage, type ).getWidgetCount() < MAX_MESSAGES_PER_GRID) {
				getGrid(domainMessage, type ).setVisible(true);
				FlowPanel buttons = new FlowPanel();
				
				if (AonConsoleProgress.this.advancedMode) {
					AonTableButton deleteButton = new AonTableButton("Borrar fila", AON.CSS.aonIconDelete());
					deleteButton.addClickHandler( event -> {
						deleteButton.setEnabled(false);
						ConsoleModule.CONSOLE_SERVICE.delete(getConsoleTableRow(domainMessage), new VisitorCallback(buttons,deleteButton));
					});
					buttons.add(deleteButton);
				}
				
				FlowPanel idPanel = new FlowPanel();
				idPanel.setStyleName(AON.CSS.aonNowrap());
				idPanel.addStyleName(AON.CSS.aonDisplayFlex());
				InlineLabel idLabel = new InlineLabel( ""+domainMessage.getPkId());
				idLabel.addStyleName(AON.CSS.aonFlexGrow1());
				idPanel.add( idLabel);
				if (AonConsoleProgress.this.advancedMode) {
					AonTableButton idSearch = new AonTableButton("Ver/Modificar Fila", AON.CSS.aonIconSwap() );
					idSearch.addClickHandler(e -> showPkRow(domainMessage));
					idPanel.add( idSearch );
				}
				
				FlowPanel fkPanel = new FlowPanel();
				fkPanel.setStyleName(AON.CSS.aonNowrap());
				fkPanel.addStyleName(AON.CSS.aonDisplayFlex());
				InlineLabel fkLabel = new InlineLabel( ""+domainMessage.getFkId());
				fkLabel.addStyleName(AON.CSS.aonFlexGrow1());
				fkPanel.add( fkLabel);
				if (AonConsoleProgress.this.advancedMode) {
					AonTableButton fkChange = new AonTableButton("Ver/Modificar datos", AON.CSS.aonIconSwap() );
					fkChange.addClickHandler(e -> showFkRow(domainMessage));
					fkPanel.add( fkChange );
				}
				
				getGrid(domainMessage, type ).addRow()
					.addCell(new Label("Integridad"))
					.addCell(buttons)
					.addCell(new Label(domainMessage.getTable()))
					.addCell(idPanel)
					.addCell(new Label(domainMessage.getFkColumn()))
					.addCell(new Label(domainMessage.getFkTable()))
					.addCell(fkPanel)
					.addCell(new Label(""+domainMessage.getWrongDomainId()))
					.addCell(new Label(domainMessage.getMessage()))
				;
				checkMaximun( getGrid(domainMessage, type ) );
			}
			return null;
		}

		@Override
		public Void visitScopeIntegrity(ConsoleDomainMessage domainMessage) {
			if (getGrid(domainMessage, type ).getWidgetCount() < MAX_MESSAGES_PER_GRID) {
				getGrid(domainMessage, type ).setVisible(true);
				
				FlowPanel fixPanel = new FlowPanel();
				fixPanel.setStyleName(AON.CSS.aonNowrap());
				fixPanel.addStyleName(AON.CSS.aonDisplayFlex());
				if (AonConsoleProgress.this.advancedMode) {
					AonTableButton fixButton = new AonTableButton("Ver/Modificar datos", AON.CSS.aonIconSwap() );
					fixButton.addClickHandler(e -> showScopes(domainMessage));
					fixPanel.add( fixButton );
				}

				FlowPanel buttons = new FlowPanel();
				getGrid(domainMessage, type ).addRow()
					.addCell(new Label("Scope"))
					.addCell(buttons)
					.addCell(new Label(domainMessage.getTable()))
					.addCell(new AonIntegerLabel(domainMessage.getCount()))
					.addCell(new AonIntegerLabel(domainMessage.getWrongScopeId()))
					.addCell(fixPanel)
					.addCell(new Label(domainMessage.getMessage()))
				;
				checkMaximun( getGrid(domainMessage, type ) );
			}
			return null;
		}

		private void showScopes(ConsoleDomainMessage message) {
			ScopeSelectorPanel popup = new ScopeSelectorPanel( message );
			popup.center();
			popup.show();
		}

		@Override
		public Void visitProduct(ConsoleDomainMessage domainMessage) {
			return visitCommon(domainMessage);
		}

		@Override
		public Void visitAgreement(ConsoleDomainMessage domainMessage) {
			return visitCommon(domainMessage);
		}
		@Override
		public Void visitInvoiceProcessOutput(ConsoleDomainMessage domainMessage) {
			return visitCommon(domainMessage);
		}
		
		private Void visitCommon(ConsoleDomainMessage domainMessage) {
			Label messageLabel = new Label(domainMessage.getMessage());
			messageLabel.setStyleName(AON.CSS.aonColorRed());
			messageLabel.addStyleName(AON.CSS.aonBold());
			bottomContainer.add(messageLabel);
			return null;
		}

		private void showPkRow(ConsoleDomainMessage domainMessage) {
			showRow( getConsoleTableRow(domainMessage) );
		}

		private void showFkRow(ConsoleDomainMessage domainMessage) {
			ConsoleTableRow row = new ConsoleTableRow()
				.setSchema(domainMessage.getSchema())
				.setTable(domainMessage.getFkTable())
				.setId(domainMessage.getFkId());
			showRow(row);
		}

		private void showRow(ConsoleTableRow row) {
			ConsoleModule.CONSOLE_SERVICE.getTableRow(row ,new AsyncCallback<ConsoleTableRow>() {
				@Override
				public void onFailure(Throwable caught) {
					AonMessageDialog.error(caught.getMessage());
				}

				@Override
				public void onSuccess(ConsoleTableRow tableRow) {
					if (tableRow == null) {
						AonMessageDialog.error("Fila no encontrada");	
					} else {
						AonCustomPopup popup = new AonCustomPopup(true); 
						popup.setWidth(PX600);
						popup.setHeight(PX600);
						popup.add(new ConsoleRowQueryViewer( tableRow ));
						popup.center();
						popup.show();
					}
				}
				
			});
			
		}

		private ConsoleTableRow getConsoleTableRow(ConsoleDomainMessage domainMessage) {
			Integer domainId = domainMessage.getDomainId() == null? null :  AonNumberUtils.toInteger("" + domainMessage.getDomainId());
			Integer pkId =  domainMessage.getPkId() == null? null : AonNumberUtils.toInteger("" + domainMessage.getPkId());
			return new ConsoleTableRow()
				.setSchema(domainMessage.getSchema())
				.setTable(domainMessage.getTable())
				.setId(pkId)
				.setDomain(domainId);
		}
		
		private void checkMaximun(AonDisplayGrid grid) {
			if (grid.getWidgetCount() >= MAX_MESSAGES_PER_GRID) {
				Label messageLabel = new Label("Solo se muestran 500 mensajes");
				messageLabel.setStyleName(AON.CSS.aonColorRed());
				messageLabel.addStyleName(AON.CSS.aonBold());
				bottomContainer.add(messageLabel);
			}
		}
		
		private AonDisplayGrid getGrid(ConsoleDomainMessage cdm, ConsoleDomainMessageType type ) {
			return grids.computeIfAbsent( type, k -> {
				AonDisplayGrid grid = new AonDisplayGrid();
				grid.addStyleName(AON.CSS.aonMarginTop());
				grid.setVisible(false);
				bottomContainer.add(grid);
				type.visit(cdm,  new GridProvider(grid));
				return grid;
			});
		}
		
	}
	
	private final class VisitorCallback implements AsyncCallback<Boolean> {
		private FlowPanel buttons;
		private AonTableButton source; 
		VisitorCallback(FlowPanel buttons, AonTableButton source ) {
			this.buttons = buttons;
			this.source = source; 
		}
		@Override
		public void onSuccess(Boolean result) {
			buttons.clear();
			Label done = new Label();
			done.setStyleName(AON.CSS.aonIconValid());
			done.addStyleName(AON.CSS.aonLabelWithIcon());
			done.addStyleName(AON.CSS.aonColorGreen());
			buttons.add(done);
		}
		
		@Override
		public void onFailure(Throwable caught) {
			source.setEnabled(false);
			AonMessageDialog.show("ERROR", caught.getMessage());
		}
	}
	
	private final class ScopeSelectorPanel extends AonCustomPopup {
		private boolean accepted;
		
		public ScopeSelectorPanel(ConsoleDomainMessage message) {
			setWidth(PX600);
			setHeight(PX600);
			DockLayoutPanel root = new DockLayoutPanel( Unit.PX );
			String sch = message.getSchema();
			Integer dom = AonNumberUtils.toInteger("" + message.getDomainId());
			Integer wrongScope = AonNumberUtils.toInteger("" + message.getWrongScopeId());
			
			FlowPanel headerPanel = new FlowPanel();
			Label titleLabel = new Label("Modificaci\u00F3n del scope incorrecto [" + wrongScope + "] en el dominio [" + dom +"]");
			titleLabel.setStyleName( AON.CSS.aonFontMedium());
			titleLabel.addStyleName( AON.CSS.aonBold());
			titleLabel.addStyleName( AON.CSS.aonTextCenter());
			Label subtitleLabel = new Label("Seleccione un scope de la lista");
			subtitleLabel.setStyleName( AON.CSS.aonItalic());
			subtitleLabel.addStyleName( AON.CSS.aonTextCenter());
			headerPanel.add( titleLabel );
			headerPanel.add( subtitleLabel );
			root.addNorth( headerPanel, 50); 
			
			FlowPanel footerPanel = new FlowPanel();
			footerPanel.setStyleName( AON.CSS.aonTextCenter() );
			footerPanel.addStyleName( AON.CSS.aonMarginTop() );	
			FlowPanel footerLabel = new FlowPanel();
			footerLabel.addStyleName( AON.CSS.aonBold() );
			footerLabel.addStyleName( AON.CSS.aonFontMedium() );
			InlineLabel footerLabel1 = new InlineLabel("\u00BFDesea modificar el scope incorrecto [" + wrongScope + "] en el dominio [" + dom +"] por el scope ");
			Scope selectedScope = new Scope();
			InlineLabel newScopeLabel = new InlineLabel("");
			newScopeLabel.setStyleName( AON.CSS.aonBold() );
			InlineLabel footerLabel2 = new InlineLabel("?");
			footerLabel.setVisible( false );
			
			footerLabel.add( footerLabel1 );
			footerLabel.add( newScopeLabel );
			footerLabel.add( footerLabel2 );
			
			FlowPanel buttonsPanel = new FlowPanel();
			buttonsPanel.setStyleName(AON.CSS.aonPadding());
			buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
			buttonsPanel.addStyleName(AON.CSS.aonTextCenter());

			
			Button acceptButton = new Button();		
			acceptButton.setStyleName(AON.CSS.aonOkButton());
			acceptButton.setText( AON.MSG.accept());
			acceptButton.addClickHandler(event -> {
				acceptButton.setEnabled(false);
				if (selectedScope == null || selectedScope.getId() == null) {
					Window.alert("Seleccione un scope");
					acceptButton.setEnabled(true);
				} else {
					if (!accepted) {
						accepted = true;
						Label acceptLabel = new Label("Pulse \"Aceptar\" si desea continuar");
						acceptLabel.setStyleName(AON.CSS.aonBold());
						acceptLabel.addStyleName(AON.CSS.aonMarginTop());
						acceptLabel.addStyleName(AON.CSS.aonColorBlue());
						buttonsPanel.add(acceptLabel);				
						acceptButton.setEnabled(true);
					} else {
						ConsoleModule.CONSOLE_SERVICE.updateScopes( sch, dom , wrongScope, selectedScope.getId(), new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable arg0) {
								AonMessageDialog.error(arg0.getMessage(), () -> {
									acceptButton.setEnabled(true);
								});								
							}

							@Override
							public void onSuccess(String msg) {
								AonMessageDialog di = new AonMessageDialog();
								HTMLPanel html = new HTMLPanel( msg );
								di.show(AON.MSG.information(),html, () -> {
									hide();
									acceptButton.setEnabled(true);
								});
							}
						});
					}
				}
			});
			buttonsPanel.add(acceptButton);
			Button cancelButton = new Button();
	    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
	    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
	    	cancelButton.setText( AON.MSG.cancelAction());
			cancelButton.addClickHandler(event -> hide());
			buttonsPanel.add(cancelButton);

			footerPanel.add(footerLabel);
			footerPanel.add(buttonsPanel);
			root.addSouth( footerPanel, 150);
			
			ScrollPanel scrPanel = new ScrollPanel();
			scrPanel.setStyleName(AON.CSS.aonScrollArea());
			FlowPanel container = new FlowPanel();
			ConsoleModule.CONSOLE_SERVICE.getScopes( sch, dom , new AsyncCallback<LinkedList<Scope>>() {
					
				@Override
				public void onSuccess(LinkedList<Scope> scopes) {
					if (AonCollectionUtils.isEmpty( scopes)) {
						Label msgLabel = new Label("No se pudo recuperar ningún scope");
						container.add(msgLabel);			
					} else {
						AonDisplayGrid scopeGrid = new AonDisplayGrid();
						scopeGrid.addStyleName( AON.CSS.aonBlockCenter());
						scopeGrid.addHeaderRow()
							.addCell( new Label(""))
							.addCell( new Label("DOMAIN"))
							.addCell( new Label("ID"))
							.addCell( new Label("DESCRIPTION"));
						AonCollectionUtils
							.stream( scopes)
							.forEach( scp -> {
								Label topLevel = new Label();
								AonDisplayGridRow row = scopeGrid.addRow()
									.addCell( topLevel )
									.addCell( new AonIntegerLabel( scp.getDomain() ))
									.addCell( new AonIntegerLabel( scp.getId() ))
									.addCell( new Label( scp.getDescription()));
								if (AonNumberUtils.notEquals( dom, scp.getDomain()) ) {
									topLevel.setStyleName( AON.CSS.aonIconLevelTop());
									topLevel.addStyleName( AON.CSS.aonIconLabel());
								} else {
									row.addStyleName( AON.CSS.aonBackgroundHighlightedGreen() ); 
								}
								row.addClickHandler( e -> {
										selectedScope.setId( scp.getId() );
										selectedScope.setDomain( scp.getDomain() );
										selectedScope.setDescription( scp.getDescription() );
										newScopeLabel.setText( scp.getId() +" - " +scp.getDescription() );
										footerLabel.setVisible( true );
									});
							});
						container.add(scopeGrid);
					}
				}
				
				@Override
				public void onFailure(Throwable err) {
					Label msgLabel = new Label("No se pudo recuperar ningún scope");
					container.add(msgLabel);			
					Label errLabel = new Label(err.getMessage());
					errLabel.setStyleName(AON.CSS.aonColorRed());
					container.add(errLabel);
				}
			});			
			scrPanel.setWidget( container );
			root.add( scrPanel );
			
			add( root );
		}
		
	}
	
}
