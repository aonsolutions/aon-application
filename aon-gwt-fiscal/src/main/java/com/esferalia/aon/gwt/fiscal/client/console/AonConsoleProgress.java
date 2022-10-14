package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageFixType;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType.Visitor;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AonConsoleProgress extends DockLayoutPanel {
	
	private final FlowPanel headerPanel; 
	private final ScrollPanel scrollPanel;
	private final FlowPanel topContainer;
	private final FlowPanel bottomContainer;
	private final InlineLabel title; 
	private final AonConsoleProgressPanel main;
	private final HashMap<String,AonConsoleProgressPanel> labels = new HashMap<>();
	private AonDisplayGrid grid = new AonDisplayGrid();
	private int gridCount;
	private boolean gridDisabled;
	
	AonConsoleProgress() {
		super(Unit.PX);
		
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
		
		grid.addStyleName(AON.CSS.aonMarginTop());
		initializeGrid();
		
		slp.setWidget(scrollPanel);
		add(slp);
		
	}

	public void reset() {
		title.setTitle(null);
		main.setVisible(false);
		main.setProgress(0,null);
		topContainer.clear();
		grid.clear();
		bottomContainer.clear();
		initializeGrid();
	}

	private void initializeGrid() {
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
		grid.setVisible(false);
		bottomContainer.add(grid);
	}

	public void setMainProgress(double percent, String msg) {
		main.setVisible(true);
		main.setProgress(percent,msg);
	}

	void log(JsConsoleMessage message) {
		
		if (message != null) {
			message.getType().visit( new Visitor() {
				
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
				public void visitMessage() {
					topContainer.add(new Label(message.getMessage()));
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
					topContainer.add(messageLabel);
				}
				
				class VisitorCallback implements AsyncCallback<Boolean> {
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
				
				@Override
				public void visitConsoleMessage() {
					if (!gridDisabled) {
						if (message.getConsoleDomainMessage() != null) {
							JsConsoleDomainMessage domainMessage = message.getConsoleDomainMessage();
							if (domainMessage.getType() == ConsoleDomainMessageType.INTEGRITY) {
								grid.setVisible(true);
								FlowPanel buttons = new FlowPanel();
								AonTableButton setNullButton = new AonTableButton("Poner la columna \"" + domainMessage.getFkColumn() + "\" a NULL", AON.CSS.aonIconBlock());
								setNullButton.addClickHandler( event -> {
									setNullButton.setEnabled(false);
									ConsoleModule.CONSOLE_SERVICE.fix(getConsoleDomainMessage(domainMessage
										, ConsoleDomainMessageFixType.SET_NULL)
										,new VisitorCallback(buttons,setNullButton));
								});
								
								AonTableButton deleteButton = new AonTableButton("Borrar fila", AON.CSS.aonIconDelete());
								deleteButton.addClickHandler( event -> {
									deleteButton.setEnabled(false);
									ConsoleModule.CONSOLE_SERVICE.fix(getConsoleDomainMessage(domainMessage
										, ConsoleDomainMessageFixType.DELETE)
										,new VisitorCallback(buttons,deleteButton));
								});
								
								buttons.add(setNullButton);
								buttons.add(deleteButton);
								
								FlowPanel idPanel = new FlowPanel();
								idPanel.setStyleName(AON.CSS.aonNowrap());
								idPanel.addStyleName(AON.CSS.aonDisplayFlex());
								InlineLabel idLabel = new InlineLabel( ""+domainMessage.getPkId());
								idLabel.addStyleName(AON.CSS.aonFlexGrow1());
								idPanel.add( idLabel);
								AonTableButton idSearch = new AonTableButton("Ver Fila", AON.CSS.aonIconSearch() );
								idSearch.addClickHandler(e -> showPkRow(domainMessage));
								idPanel.add( idSearch );
								
								FlowPanel fkPanel = new FlowPanel();
								fkPanel.setStyleName(AON.CSS.aonNowrap());
								AonTableButton fkSearch = new AonTableButton("Ver Fila", AON.CSS.aonIconSearch() );
								fkSearch.addClickHandler(e -> showFkRow(domainMessage));
								fkPanel.add( fkSearch );
								AonIntegerBox fkIdBox = new AonIntegerBox();
								fkIdBox.setMaxLength( 10 );
								fkIdBox.setVisibleLength( 6 );
								fkIdBox.setValue( AonNumberUtils.toInteger( ""+domainMessage.getFkId() ));
								fkPanel.add( fkIdBox);
								AonTableButton fkChange = new AonTableButton("Modificar Dato", AON.CSS.aonIconSave() );
								fkChange.addClickHandler(e -> {
									Window.alert("Cambio de valor. Accion pendiente");
								});
								fkPanel.add( fkChange );
								
								grid.addRow()
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
								gridCount++;
								if (gridCount >= 500) {
									gridDisabled = true;
									Label messageLabel = new Label("Solo se muestran 500 mensajes");
									messageLabel.setStyleName(AON.CSS.aonColorRed());
									messageLabel.addStyleName(AON.CSS.aonBold());
									bottomContainer.add(messageLabel);
								}
							}
						} else { 
							Label messageLabel = new Label(message.getMessage());
							messageLabel.setStyleName(AON.CSS.aonColorRed());
							messageLabel.addStyleName(AON.CSS.aonBold());
							bottomContainer.add(messageLabel);
						}
					}
				}

				private void showPkRow(JsConsoleDomainMessage domainMessage) {
					ConsoleDomainMessage cm = getConsoleDomainMessage(domainMessage, null);
					showRow(cm.getSchema(), cm.getTable(),cm.getPkId());
				}

				private void showFkRow(JsConsoleDomainMessage domainMessage) {
					ConsoleDomainMessage cm = getConsoleDomainMessage(domainMessage, null);
					showRow(cm.getSchema(), cm.getFkTable(),cm.getFkId());
				}

				private void showRow(String schema, String tableName, Integer id) {
					
					ConsoleModule.CONSOLE_SERVICE.viewRow(schema, tableName, id
							,new AsyncCallback<LinkedHashMap<String, Object>>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessageDialog.error(caught.getMessage());
								}

								@Override
								public void onSuccess(LinkedHashMap<String, Object> result) {
									if (result == null || result.isEmpty()) {
										AonMessageDialog.error("Fila no encontrada");	
									} else {
										AonConsoleProgress.this.showRow(tableName, id ,result);
									}
								}

					});
					
				}

				private ConsoleDomainMessage getConsoleDomainMessage(JsConsoleDomainMessage domainMessage, ConsoleDomainMessageFixType fixType) {
					Integer domainId = domainMessage.getDomainId() == null? null :  AonNumberUtils.toInteger("" + domainMessage.getDomainId());
					Integer pkId =  domainMessage.getPkId() == null? null : AonNumberUtils.toInteger("" + domainMessage.getPkId());
					Integer fkId =  domainMessage.getFkId() == null? null : AonNumberUtils.toInteger("" + domainMessage.getFkId());
					Integer wrongDomainId =  domainMessage.getWrongDomainId() == null? null : AonNumberUtils.toInteger("" + domainMessage.getWrongDomainId());
					return new ConsoleDomainMessage()
						.setSchema(domainMessage.getSchema())
						.setType(domainMessage.getType())
						.setFixType(fixType)
						.setDomainId(domainId)
						.setTable(domainMessage.getTable())
						.setPkId(pkId)
						.setPkCode(domainMessage.getPkCode())
						.setFkTable(domainMessage.getFkTable())
						.setFkColumn(domainMessage.getFkColumn())
						.setFkId(fkId)
						.setWrongDomainId(wrongDomainId)
						.setMessage(domainMessage.getMessage())
					;
				}
			});
		}
		scrollPanel.scrollToBottom();
	}
	
	protected void showWarning(String message) {
		Label messageLabel = new Label(message);
		messageLabel.setStyleName(AON.CSS.aonColorOrange());
		messageLabel.addStyleName(AON.CSS.aonBold());
		topContainer.add(messageLabel);
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
	
	private void showRow(String table, Integer id, LinkedHashMap<String, Object> result) {
		AonCustomPopup popup = new AonCustomPopup();
		popup.setWidth("600px");
		popup.setHeight("300px");
		popup.setCaption("Detalles de la fila");
		popup.add( new AonConsoleRowViewer(table, id, result));
		popup.center();
		popup.show();
	}
	
	static class AonConsoleRowViewer extends SimpleLayoutPanel {

		public AonConsoleRowViewer(String table, Integer id, LinkedHashMap<String, Object> result) {
			setStyleName(AON.CSS.aonScrollArea());
			
			ScrollPanel scroll = new ScrollPanel();
			add( scroll);
			FlowPanel container = new FlowPanel();
			container.setStyleName(AON.CSS.aonMarginBottom());
			scroll.setWidget( container );
			
			Label title = new Label( "Tabla: " + table + " ID: " + id);
			title.setStyleName(AON.CSS.aonFontLarger());
			title.addStyleName(AON.CSS.aonBold());
			title.addStyleName(AON.CSS.aonTextCenter());
			title.addStyleName(AON.CSS.aonMarginBottom());
			container.add(title);
			
			AonDisplayGrid grid = new AonDisplayGrid();
			grid.addStyleName(AON.CSS.aonBlockCenter());
			container.add(grid);
			result.keySet()
				.stream()
				.forEach( column  -> grid.addRow()
					.addCell( new Label(column) , AON.CSS.aonTableLabel())
					.addCell( new Label(Objects.toString(result.get(column),"<NULL>")) )
			);
		}
		
	}
	
}
