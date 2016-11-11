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
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.dump.shared.DSIImportService;
import com.esferalia.aon.gwt.dump.shared.Domain;
import com.esferalia.aon.gwt.dump.shared.Progress;
import com.esferalia.aon.gwt.dump.shared.Task;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DSIImportForm implements EntryPoint, DSIImportService {

	private final class ProcessCommand implements RepeatingCommand {

		private final Task result;
		private Integer lastIDtask;

		private Label progressTab;
		private com.esferalia.aon.gwt.dump.client.Progress progressBar;

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

		public ProcessCommand setProgressBar(com.esferalia.aon.gwt.dump.client.Progress progressBar) {
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
							setProgress(0);
							setBackgroundColor("red");
						} else if (result.getStatus() == 3) {
							setProgressText("Proceso descarga: descarga finalizada.");
							setProgress(0);
							setBackgroundColor("green");
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
			progressBar.setProgress(progress);
		}

		private void setProgressText(String text) {
			progressTab.setText(text);
			;
		}

		private void setBackgroundColor(String color) {
			progressBar.getElement().getStyle().setBackgroundColor(color);
		}
	}

	interface Binder extends UiBinder<Widget, DSIImportForm> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	TabLayoutPanel footTabPanel;

	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	MinimizePanel footPanel;

	@UiField
	Button sendButton;

	@UiField
	HorizontalPanel barPanel;

	@UiField
	ListBox downloadListBox;

	@UiField(provided = true)
	SuggestBox suggestBox;

	@UiField
	TextBox nombreEmpresa;

	@UiField
	TextBox nombreUsuario;

	@UiField
	TextBox nuevaContrasena;

	@UiField
	CheckBox checkComment;

	@UiField
	CheckBox checkLocks;

	@UiField
	CheckBox checkErase;

	@UiField
	CheckBox checkFK;

	@UiField
	Label errorString;

	@UiField
	Label sufijoEmpresa;

	private ResultsPanel importsPanel;

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

					footTabPanel.add(progressInfo, t.getDescription());
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

		this.nombreEmpresa.getElement().setPropertyString("placeholder", "Nuevo Nombre Empresa");
		this.nombreUsuario.getElement().setPropertyString("placeholder", "Nombre Usuario");
		this.nuevaContrasena.getElement().setPropertyString("placeholder", "Nueva Password");
		this.nombreEmpresa.getElement().setPropertyString("pattern", "/^[a-zA-Z0-9._-]+\\[a-zA-Z0-9.-]$/");
		this.nombreUsuario.setEnabled(false);
		this.nuevaContrasena.setEnabled(false);
		this.sendButton.setEnabled(false);
		this.nombreEmpresa.setEnabled(false);

		this.downloadListBox.addItem("BackUp");
		this.downloadListBox.addItem("Duplicar");
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

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		// TODO: ver como minimizar el foot panel
		closeFootPanel();
	}

	@UiHandler("sendButton")
	void onClickSendButton(ClickEvent event) {

		MessageBoxDialog.showInfoDialog("Iniciando descarga.");

		// ------ Recogida parametros de entrada --------
		String domainNuevo = this.nombreEmpresa.getText();
		Boolean comentarios = this.checkComment.getValue();
		Boolean locks = this.checkLocks.getValue();
		Boolean fk = this.checkFK.getValue();
		Boolean eraseUser = this.checkErase.getValue();
		String nombreNuevo = this.nombreUsuario.getText();
		String passNueva = this.nuevaContrasena.getText();

		String suggestBoxValue = this.suggestBox.getValue();
		String domain = this.domainsMap.get(Integer.parseInt(suggestBoxValue.split(" ")[0]));

		final ProgressInfo progressInfo = new ProgressInfo();

		progressInfo.addEraseHandler(new EraseHandlder() {

			@Override
			public void onErase(EraseEvent event) {
				footTabPanel.remove(progressInfo);
			}

		});

		footTabPanel.add(progressInfo, domain);

		connectServiceAsync.dumpDomain(domain, new AsyncCallback<Task>() {

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
		DSIImportForm.this.sendButton.setEnabled(false);

	}

	@UiHandler("suggestBox")
	void onSelectSuggestBox(SelectionEvent<Suggestion> event) {

		String suggestBoxValue = this.suggestBox.getValue();
		Integer id = Integer.parseInt(suggestBoxValue.charAt(0) + "");

		String sufix = sufixMap.get(id);
		this.sufijoEmpresa.setText("." + sufix);

		this.nombreEmpresa.setEnabled(true);
		this.sendButton.setEnabled(true);
	}

	@UiHandler("nombreEmpresa")
	void onKeyUpCheck(KeyUpEvent even) {

		RegExp regExp = RegExp.compile("^[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9]$");
		String nomEmpresa = "";
		Integer textlength = 0;

		if (!regExp.test(this.nombreEmpresa.getText())) {

			nomEmpresa = this.nombreEmpresa.getText();
			textlength = nomEmpresa.length();

			if (textlength == 0) {
				this.errorString.setText("Ok");
				this.errorString.getElement().getStyle().setColor("transparent");
				this.nombreEmpresa.getElement().getStyle().setColor("black");
			}

			else if (textlength < 3) {
				this.errorString.setText("Nombre demasiado corto, al menos 3 caracteres.");
				this.errorString.getElement().getStyle().setColor("red");
				this.nombreEmpresa.getElement().getStyle().setColor("red");
			} else {
				this.errorString.setText(
						"Nombre invalido, caracter invalido: " + this.nombreEmpresa.getText().charAt(textlength - 1));
				this.errorString.getElement().getStyle().setColor("red");
				this.nombreEmpresa.getElement().getStyle().setColor("red");
			}

		} else {
			this.errorString.setText("Ok");
			this.errorString.getElement().getStyle().setColor("transparent");
			this.nombreEmpresa.getElement().getStyle().setColor("black");
		}
	}

	@UiHandler("downloadListBox")
	void onDownloadListBoxChange(ChangeEvent event) {

		Integer select = downloadListBox.getSelectedIndex();

		switch (select) {

		case 0:
			this.checkComment.setValue(false);
			this.checkErase.setValue(false);
			this.checkFK.setValue(false);
			this.checkLocks.setValue(false);
			this.checkComment.setEnabled(true);
			this.checkErase.setEnabled(true);
			this.checkFK.setEnabled(true);
			this.checkLocks.setEnabled(true);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;

		case 1:
			this.checkComment.setValue(false);
			this.checkErase.setValue(false);
			this.checkFK.setValue(false);
			this.checkLocks.setValue(false);
			this.checkComment.setEnabled(false);
			this.checkErase.setEnabled(true);
			this.checkFK.setEnabled(true);
			this.checkLocks.setEnabled(true);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;

		default:
			this.checkComment.setValue(false);
			this.checkErase.setValue(false);
			this.checkFK.setValue(false);
			this.checkLocks.setValue(false);
			this.checkComment.setEnabled(false);
			this.checkErase.setEnabled(false);
			this.checkFK.setEnabled(false);
			this.checkLocks.setEnabled(false);
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
			break;
		}
	}

	@UiHandler("checkErase")
	void onClickCheckErase(ClickEvent event) {

		if (checkErase.getValue()) {
			this.nombreUsuario.setEnabled(true);
			this.nuevaContrasena.setEnabled(true);
		} else {
			this.nombreUsuario.setEnabled(false);
			this.nuevaContrasena.setEnabled(false);
			this.nombreUsuario.setText("");
			this.nuevaContrasena.setText("");
		}
	}

	// ------------------------------ Metodo Auxiliares
	// ----------------------------------

	private void closeFootPanel() {
		Window.alert("Minimizar");
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

}
