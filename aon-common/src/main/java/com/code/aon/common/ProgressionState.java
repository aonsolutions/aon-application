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
		setProgress(true);
	}
	
	public void finish() {
		setProgress(false);
	}
	
	private void setProgress( boolean enabled ) {
		setProgressionPanelVisible(enabled);
		setProgressionEnabled(enabled);
		long value = (enabled?START_VALUE:CANCEL_VALUE);
		setProgressionCurrentValue(value);		
		if ( enabled ) {
			setProgressionErrorMessage(null);
		}
	}

}
