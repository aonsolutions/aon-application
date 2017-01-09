package com.esferalia.aon.gwt.dump.client;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.dump.shared.DSIImportService;
import com.esferalia.aon.gwt.dump.shared.Domain;
import com.esferalia.aon.gwt.dump.shared.Parameters;
import com.esferalia.aon.gwt.dump.shared.Progress;
import com.esferalia.aon.gwt.dump.shared.Task;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.iron.widget.IronLabel;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperCheckbox;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperProgress;

public class DSIImportForm implements EntryPoint, DSIImportService {

	private final class ProcessCommand implements RepeatingCommand {
		
		private final Task result;
		private Integer lastIDtask;

		private Label progressTab;
		private PaperProgress progressBar;
		
		private FlexTable infoTab;
		private ScrollPanel scrollInfo;

		private ProcessCommand(Task result) {
			this.result = result;
			this.lastIDtask = 0;
		}

		public ProcessCommand setInfoTab(FlexTable infoTab) {
			this.infoTab = infoTab;
			return this;
		}

		public ProcessCommand setProgressBar(PaperProgress progressBar) {
			this.progressBar = progressBar;
			return this;
		}

		public ProcessCommand setScrollInfo(ScrollPanel scrollInfo) {
			this.scrollInfo = scrollInfo;
			return this;
		}

		public ProcessCommand setProgressTab(Label progressTab) {
			this.progressTab = progressTab;
			return this;
		}

		@Override
		public boolean execute() {

			connectServiceAsync.process(result.getId(), this.lastIDtask, new AsyncCallback<Progress>() {

				@Override
				public void onFailure(Throwable caught) {
					// Process doesn't work
				}

				@Override
				public void onSuccess(Progress result) {

					ProcessCommand.this.lastIDtask = result.getLastId();

					Iterator<String> it = result.getComments().iterator();

					setProgressText("Proceso descarga: " + result.getPercent().toString() + " %");
					setProgress(result.getPercent());

					while (it.hasNext())
						addInfoText(it.next());

					if (result.getPercent() < 100)
						Scheduler.get().scheduleFixedPeriod(ProcessCommand.this, 7000);
					else {
						if (result.getStatus() == 0) {
							setProgressText("Proceso descarga: descarga cancelada.");
							setProgress(100);
							DSIImportForm.this.setCanceled(progressBar);
							scrollInfo.scrollToBottom();
						} else if (result.getStatus() == 3) {
							setProgressText("Proceso descarga: descarga finalizada.");
							setProgress(100);
							DSIImportForm.this.setCompleted(progressBar);
							scrollInfo.scrollToBottom();
						}	
					}
				}
			});

			return false;
		}

		private void addInfoText(String text) {
			int row = infoTab.getRowCount();
			infoTab.setHTML(row, 0, text);
			scrollInfo.scrollToBottom();
		}

		private void setProgress(int progress) {
			progressBar.setValue(progress);
		}

		private void setProgressText(String text) {
			progressTab.setText(text);
		}

	}

	interface Binder extends UiBinder<Widget, DSIImportForm> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
    MyStyle style;
	
	interface MyStyle extends CssResource {
        String deleteHeight();
        String crossHeight();
        String completed();
        String canceled();
	}
	
	@UiField
	TabLayoutPanel footTabPanel;

	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	MinimizePanel footPanel;

	@UiField
	PaperButton sendButton;

	@UiField
	ListBox downloadListBox;
	
	@UiField(provided = true)
	SuggestBox suggestBox;

	@UiField
	PaperInput nombreEmpresa;
	
	@UiField
	PaperInput descripcionEmpresa;

	@UiField
	TextBox nombreUsuario;

	@UiField
	TextBox nuevaContrasena;

	@UiField
	PaperCheckbox checkComment;

	@UiField
	PaperCheckbox checkErase;

	@UiField
	DivElement sufijoEmpresa;
	
	@UiField
	HTMLPanel warnSuffix;

	private MultiWordSuggestOracle oracle;

	private ConnectServiceAsync connectServiceAsync;
	private HashMap<Integer, String> domainsMap;
	private HashMap<Integer, String> sufixMap;
	private Integer idTask;
	private List<String> aviableDomain;

	@Override
	public void onModuleLoad() {

		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();

		ConnectServiceAsync connectServiceRaw = GWT.create(ConnectService.class);
		connectServiceAsync = new ConnectServiceAsyncDecorator(connectServiceRaw);

		this.aviableDomain = new ArrayList<>();
		this.oracle = new MultiWordSuggestOracle();
		this.suggestBox = new SuggestBox(oracle);
		this.domainsMap = new HashMap<>();
		this.sufixMap = new HashMap<>();
		
		connectServiceAsync.getAvailableDomains(new AsyncCallback<List<Domain>>() {

			@Override
			public void onSuccess(List<Domain> domains) {
				for (Domain d : domains) {
					domainsMap.put(d.getId(), d.getName());
					sufixMap.put(d.getId(), d.getSuffix());
					oracle.add(d.getId() + " " + d.getName() + " " + d.getDescription());
					aviableDomain.add(d.getId() + " " + d.getName() + " " + d.getDescription());
				}
				
				oracle.setDefaultSuggestionsFromText(aviableDomain);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("onFailure " + caught);
			}
		});
		
		connectServiceAsync.getTaskPending(new AsyncCallback<List<Task>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TaskPending doesn't work
			}

			@Override
			public void onSuccess(List<Task> result) {
				ConnectServiceAsync connectServiceRaw = GWT.create(ConnectService.class);
				connectServiceAsync = new ConnectServiceAsyncDecorator(connectServiceRaw);

				for (Task t : result) {

					final ProgressInfo progressInfo = new ProgressInfo();
					progressInfo.addEraseHandler(new EraseHandlder() {

						@Override
						public void onErase(EraseEvent event) {
							footTabPanel.remove(progressInfo);

						}

					});
					
					// Creamos el tab con la tarea pendiente
					HorizontalPanel panel = new HorizontalPanel();
					IronLabel label = new IronLabel();
					IronIcon iconInfo = new IronIcon();
					iconInfo.setIcon("icons:tab");
					iconInfo.addStyleName(style.deleteHeight());
					label.add(iconInfo);
					panel.add(label);
					
					panel.add(new Label(t.getDescription()));
					
					PaperButton boton = new PaperButton();
					IronIcon icon = new IronIcon();
					icon.setIcon("icons:close");
					icon.addStyleName(style.crossHeight());
					boton.addStyleName(style.deleteHeight());
					boton.setTitle("Eliminar Descarga");
					boton.add(icon);
					
					boton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							connectServiceAsync.eraseDownload(progressInfo.getIdTask(), new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									// Fallo al borrar una descarga
								}

								@Override
								public void onSuccess(Boolean result) {
									if (result){
										MessageBoxDialog.showTrashDialog("Tarea eliminada.");
										footTabPanel.remove(progressInfo);
									}
								}
							});
							
						}
					});
					
					panel.add(boton);
					footTabPanel.add(progressInfo, panel);
					progressInfo.setIdTask(t.getId());

					Scheduler.get()
							.scheduleFixedPeriod(new ProcessCommand(t).setInfoTab(progressInfo.infoTab)
									.setScrollInfo(progressInfo.scrollInfo).setProgressBar(progressInfo.progressBar)
									.setProgressTab(progressInfo.progressTab), 5000);
				}

			}
		});

		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
			
		this.nombreUsuario.getElement().setPropertyString("placeholder", "Nombre Usuario");
		this.nuevaContrasena.getElement().setPropertyString("placeholder", "Nueva Password");
		this.nombreUsuario.setEnabled(false);
		this.nuevaContrasena.setEnabled(false);
		this.sendButton.setDisabled(true);
		setVisible(this.warnSuffix,false);

		this.downloadListBox.addItem("BackUp");
		this.downloadListBox.addItem("BackUp (Stand-alone)");
		this.downloadListBox.addItem("Duplicar");
		this.downloadListBox.addItem("Duplicar (Stand-Alone)");
		this.downloadListBox.addItem("Comprobar Integridad");

		this.suggestBox.addKeyUpHandler(new KeyUpHandler() {		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((event.getNativeKeyCode() == KeyEvent.CTRL_MASK) && 
						(event.getNativeKeyCode() == KeyEvent.VK_SPACE))
						suggestBox.showSuggestionList();
			}
		});
		
		this.suggestBox.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				suggestBox.showSuggestionList();
			}
		}, DoubleClickEvent.getType());

	}
	
	// ----------------------------- UiHandlers --------------------------------

	@UiHandler("footTabPanel")
	void onScrollDownTab(SelectionEvent<Integer> event) {
		
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				((ProgressInfo) footTabPanel.getWidget(event.getSelectedItem())).scrollInfo.scrollToBottom();
			}
		});
		
	}

	@UiHandler("footPanel")
    void onFootMinimize(MinimizeEvent event) {
            closeFootPanel();
    }

    @UiHandler("footPanel")
    void onFootMaximize(MaximizeEvent event) {
            splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
            splitLayoutPanel.animate(500);
    }

    private void closeFootPanel() {
            splitLayoutPanel.setWidgetSize(footPanel, 26);
            splitLayoutPanel.animate(500);
    }
	
	@UiHandler("sendButton")
	void onClickSendButton(ClickEvent event) {

		MessageBoxDialog.showInfoDialog("Iniciando descarga.");

		// ---- Recogida parametros de entrada ------
		String domainNuevo = this.nombreEmpresa.getValue()+this.sufijoEmpresa.getInnerText();
		Boolean comentarios = this.checkComment.getChecked();
		Boolean eraseUser = this.checkErase.getChecked();
		String nombreNuevo = this.nombreUsuario.getText();
		String passNueva = this.nuevaContrasena.getText();
		String descripcionEmpresa = this.descripcionEmpresa.getValue();
		int tipoDescarga = this.downloadListBox.getSelectedIndex();
		
		
		Parameters parameters = new Parameters();
		parameters.setNewDomain(domainNuevo).setDescripcionEmpresa(descripcionEmpresa).setComments(comentarios).setEraseUsers(eraseUser)
					.setNewUserName(nombreNuevo).setNewUserPass(passNueva).setDownloadType(tipoDescarga);

		String suggestBoxValue = this.suggestBox.getValue();
		String domain = this.domainsMap.get(Integer.parseInt(suggestBoxValue.split(" ")[0]));

		final ProgressInfo progressInfo = new ProgressInfo();

		progressInfo.addEraseHandler(new EraseHandlder() {

			@Override
			public void onErase(EraseEvent event) {
				footTabPanel.remove(progressInfo);
			}

		});
		
		HorizontalPanel panel = new HorizontalPanel();
		IronLabel label = new IronLabel();
		IronIcon iconInfo = new IronIcon();
		iconInfo.setIcon("icons:tab");
		iconInfo.addStyleName(style.deleteHeight());
		label.add(iconInfo);
		panel.add(label);
		panel.add(new Label(domain));
		PaperButton boton = new PaperButton();
		IronIcon icon = new IronIcon();
		icon.setIcon("icons:close");
		icon.addStyleName(style.crossHeight());
		boton.addStyleName(style.deleteHeight());
		boton.setTitle("Eliminar Descarga");
		boton.add(icon);
		
		boton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				connectServiceAsync.eraseDownload(progressInfo.getIdTask(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Boolean result) {
						if (result){
							MessageBoxDialog.showTrashDialog("Tarea eliminada.");
							footTabPanel.remove(progressInfo);
						}
					}
				});
				
			}
		});

		panel.add(boton);
		footTabPanel.insert(progressInfo, panel, 0);
		//footTabPanel.add(progressInfo, panel);
		int index = footTabPanel.getWidgetIndex(progressInfo);
		footTabPanel.selectTab(index, false);
		
		connectServiceAsync.dumpDomain(domain, parameters, new AsyncCallback<Task>() {

			@Override
			public void onSuccess(final Task result) {
				DSIImportForm.this.idTask = result.getId();
				progressInfo.setIdTask(DSIImportForm.this.idTask);
				Scheduler.get()
						.scheduleFixedPeriod(new ProcessCommand(result).setInfoTab(progressInfo.infoTab)
								.setScrollInfo(progressInfo.scrollInfo).setProgressBar(progressInfo.progressBar)
								.setProgressTab(progressInfo.progressTab), 9000);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});

		DSIImportForm.this.suggestBox.setText("");
		DSIImportForm.this.sendButton.setDisabled(true);
		DSIImportForm.this.sufijoEmpresa.setInnerText("");
		DSIImportForm.this.nombreEmpresa.setValue("");
		DSIImportForm.this.descripcionEmpresa.setValue("");
		DSIImportForm.setVisible(this.warnSuffix,false);
		

	}

	@UiHandler("suggestBox")
	void onSelectSuggestBox(SelectionEvent<Suggestion> event) {

		String suggestBoxValue = this.suggestBox.getValue();
		String[] idString = suggestBoxValue.split(" ");
		Integer id = Integer.parseInt(idString[0]);

		String sufix = sufixMap.get(id);
		
		if (sufix == null){
			setVisible(this.warnSuffix,true);
			this.nombreEmpresa.setPattern(".{3,}");
			this.sufijoEmpresa.setInnerText("");
		}else{
			setVisible(this.warnSuffix,false);
			this.nombreEmpresa.setPattern("[a-zA-Z0-9_-]{3,}");
			this.sufijoEmpresa.setInnerText("." + sufix);
		}
			

		this.sendButton.setDisabled(false);
		
		
	}


	@UiHandler("downloadListBox")
	void onDownloadListBoxChange(ChangeEvent event) {

		int select = downloadListBox.getSelectedIndex();

		switch (select) {

		case 0:
			this.checkComment.setChecked(false);
			this.checkErase.setChecked(false);
			this.checkComment.setDisabled(false);
			this.checkErase.setDisabled(false);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;

		case 1:
			this.checkComment.setChecked(false);
			this.checkErase.setChecked(false);
			this.checkComment.setDisabled(false);
			this.checkErase.setDisabled(false);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;
			
		case 2:
			this.checkComment.setChecked(false);
			this.checkErase.setChecked(false);
			this.checkComment.setDisabled(true);
			this.checkErase.setDisabled(false);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;
			
		case 3:
			this.checkComment.setChecked(false);
			this.checkErase.setChecked(false);
			this.checkComment.setDisabled(true);
			this.checkErase.setDisabled(false);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;

		default:
			this.checkComment.setChecked(false);
			this.checkErase.setChecked(false);
			this.checkComment.setDisabled(true);
			this.checkErase.setDisabled(true);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;
		}
		
	}

	@UiHandler("checkErase")
	void onClickCheckErase(com.vaadin.polymer.paper.widget.event.ChangeEvent event) {
		if (checkErase.getChecked()) {
			this.nombreUsuario.setEnabled(true);
			this.nuevaContrasena.setEnabled(true);
		} else {
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
		}
	}

	// ------------------------------ Metodo Auxiliares ----------------------------------

	public void setCompleted(PaperProgress progressBar){
		progressBar.addStyleName(style.completed());
		progressBar.updateStyles();
		
	}
	
	public void setCanceled(PaperProgress progressBar){
		progressBar.addStyleName(style.canceled());
		progressBar.updateStyles();
	}
	
	private static void setVisible(IsWidget widget, boolean visible ){
		widget.asWidget().getElement().getStyle().setVisibility(visible?Visibility.VISIBLE:Visibility.HIDDEN);
	}
}
