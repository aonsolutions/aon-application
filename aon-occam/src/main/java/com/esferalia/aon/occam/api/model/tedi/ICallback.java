package com.esferalia.aon.occam.api.model.tedi;

import com.esferalia.aon.occam.api.model.AonConfiguration;

public interface ICallback {
	AonConfiguration getConfiguration();
	TediResult getResult();
	void onAccept(TediResult result);
	void onCancel();
}
