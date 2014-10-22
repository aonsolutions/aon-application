package com.code.aon.common;

import java.io.Serializable;

import com.code.aon.AonVersion;


public class ProgressionState implements IProgression, Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean progressionPanelVisible;
	private boolean progressionEnabled;
	private Long progressionCurrentValue;
	private String progressionErrorMessage;
	
	public ProgressionState() {
		setProgressionCurrentValue(IProgression.CANCEL_VALUE);
	}

	public boolean isProgressionPanelVisible() {
		return progressionPanelVisible;
	}
	
	public void setProgressionPanelVisible(boolean progressionPanelVisible) {
		this.progressionPanelVisible = progressionPanelVisible;
	}
	
	public boolean isProgressionEnabled() {
		return progressionEnabled;
	}
	
	public void setProgressionEnabled(boolean progressionEnabled) {
		this.progressionEnabled = progressionEnabled;
	}
	
	public Long getProgressionCurrentValue() {
		return progressionCurrentValue;
	}
	
	public void setProgressionCurrentValue(Long progressionCurrentValue) {
		this.progressionCurrentValue = progressionCurrentValue;
	}
	
	public String getProgressionErrorMessage() {
		return progressionErrorMessage;
	}
	
	public void setProgressionErrorMessage(String progressionErrorMessage) {
		this.progressionErrorMessage = progressionErrorMessage;
	}
	
	public boolean isFinish() {
		return getProgressionCurrentValue() == IProgression.FINISH_VALUE;	
	}

	public void start() {
		start(true);
	}

	public void start( boolean inmediately ) {
		setProgress(true, inmediately?START_VALUE:CANCEL_VALUE);
	}
	
	public void finish() {
		setProgress(false, CANCEL_VALUE);
	}
	
	private void setProgress( boolean enabled, long value ) {
		setProgressionPanelVisible(enabled);
		setProgressionEnabled(enabled);
		setProgressionCurrentValue(value);		
		if ( enabled ) {
			setProgressionErrorMessage(null);
		}
	}

}
