package com.esferalia.aon.gwt.template.client;






import com.esferalia.aon.gwt.common.client.ProgressBar;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ProgressBarDialog extends CustomDialogB {

	class ProgressBarCallBack extends Timer {

		ProgressBar progressBar = null;

		public ProgressBarCallBack() {
			progressBar = new ProgressBar(40, ProgressBar.SHOW_TIME_REMAINING
					+ ProgressBar.SHOW_TEXT);
			this.progressBar.setText("Importando...");
			barPanel.clear();
			barPanel.add(progressBar);
		}

		@Override
		public void run() {
			int progress = progressBar.getProgress() + 4;
			if (progress > 100)
				cancel();
			progressBar.setProgress(progress);
		}

		private void setText(String text) {
			progressBar.setCompletedMessage(text);
		}
	}
	
	interface Binder extends UiBinder<Widget, ProgressBarDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	

	@UiField(provided = true) HorizontalPanel barPanel;
	private ProgressBarCallBack progressBarCallback;

	
	
	public ProgressBarDialog(Double d, Double d2) {
		setCaption("Importando...");
		setWidth("400px");
		barPanel = new HorizontalPanel();
		this.progressBarCallback = new ProgressBarCallBack();
		evalProgressBar(d*d2);//0.101);
		setWidget(binder.createAndBindUi(this));
	}
	
	private void evalProgressBar(Double cargaTrabajo) {
		progressBarCallback.scheduleRepeating(cargaTrabajo.intValue());
	}
}