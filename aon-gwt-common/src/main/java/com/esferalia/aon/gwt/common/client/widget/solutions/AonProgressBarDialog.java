package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonProgressBar;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonProgressBarDialog extends AonCustomDialog {

	class ProgressBarCallBack extends Timer {
		public ProgressBarCallBack() {}

		@Override
		public void run() {
			int progress = apb.getProgress() + 1;
			if (progress > 100)
				cancel();
			else if(progress < 100)	
				apb.setProgress(progress);
		}
	}
	
	interface Binder extends UiBinder<Widget, AonProgressBarDialog>{}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField(provided = true) SimplePanel panel;
	private ProgressBarCallBack progressBarCallback;
	AonProgressBar apb;
	
	public AonProgressBarDialog(Double d, Double d2) {
		setCaption("Importando...");
		setWidth("400px");
		panel = new SimplePanel();
		apb = new AonProgressBar();
		panel.add(apb);
		panel.setStyleName(AON.AON_CSS.aonProgressBarDialogPanel());
		this.progressBarCallback = new ProgressBarCallBack();
		evalProgressBar((d*d2)/4);
		setWidget(binder.createAndBindUi(this));
	}
	
	public AonProgressBarDialog(String title) {
		setCaption(title);
		setWidth("400px");
		panel = new SimplePanel();
		apb = new AonProgressBar();
		panel.add(apb);
		panel.setStyleName(AON.AON_CSS.aonProgressBarDialogPanel());
		this.progressBarCallback = new ProgressBarCallBack();
		setWidget(binder.createAndBindUi(this));
	}
	
	public AonProgressBarDialog(Double d, Double d2, String title) {
		setCaption(title);
		setWidth("400px");
		panel = new SimplePanel();
		apb = new AonProgressBar();
		panel.add(apb);
		panel.setStyleName(AON.AON_CSS.aonProgressBarDialogPanel());
		this.progressBarCallback = new ProgressBarCallBack();
		evalProgressBar((d*d2)/4);
		setWidget(binder.createAndBindUi(this));
	}
	
	private void evalProgressBar(Double cargaTrabajo) {
		if(cargaTrabajo.intValue() == 0)
			progressBarCallback.scheduleRepeating(1);
		else progressBarCallback.scheduleRepeating(cargaTrabajo.intValue());
	}
	
	public void completed(){
		apb.setProgress(100);
	}
	
	public void updateProgress(Integer progress) {
		apb.setProgress(progress);
	}
}