package com.code.aon.ui.fiscal.controller.mod347;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.ui.form.LinesController;

public class Mod347DetailController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean extraInfoPanelVisible;

	public boolean isExtraInfoPanelVisible() {
		return extraInfoPanelVisible;
	}

	public void setExtraInfoPanelVisible(boolean extraInfoPanelVisible) {
		this.extraInfoPanelVisible = extraInfoPanelVisible;
	}

	public void onHideExtraInfoPanel(ActionEvent event) {
		setExtraInfoPanelVisible(false);
	}

	public void onShowExtraInfoPanel(ActionEvent event) {
		super.onSelect(event);
		setExtraInfoPanelVisible(true);
	}

	public void onAcceptExtraInfoPanel(ActionEvent event) {
		super.onAccept(event);
		onHideExtraInfoPanel(event);
	}

	public void onCancelExtraInfoPanel(ActionEvent event) {
		super.onCancel(event);
		onHideExtraInfoPanel(event);
	}

	public void onChangeAssetAmount(ActionEvent event) {
		Mod347Detail detail = (Mod347Detail) getTo();
		detail.setAssetAmount(CommonUtil.round(detail
				.getAssetFirstQuarterAmount()
				+ detail.getAssetSecondQuarterAmount()
				+ detail.getAssetThirdQuarterAmount()
				+ detail.getAssetFourthQuarterAmount()));
	}

}
