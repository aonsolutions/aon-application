package com.code.aon.ui.ebackoffice.event;

import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.ebackoffice.controller.EcconfigController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;

public class EcConfigControllerListener extends ControllerAdapter implements
		IItemConstants {
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event
				.getController();
		AonFile header = controller.getHeaderImage();
		AonFile leftBanner = controller.getLeftBanner();
		AonFile rightBanner = controller.getRightBanner();
		AonFile welcomeBanner = controller.getWelcomeBanner();

		if (header != null) {
			((Ecconfig) controller.getTo()).setHeaderImg(header.getData());

		}
		if (leftBanner != null) {
			((Ecconfig) controller.getTo()).setLeftBanner(leftBanner.getData());

		}
		if (rightBanner != null) {
			((Ecconfig) controller.getTo()).setRightBanner(rightBanner
					.getData());

		}
		if (welcomeBanner != null) {
			((Ecconfig) controller.getTo()).setWelcomeBanner(welcomeBanner
					.getData());

		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event
				.getController();
		
		AonFile header = controller.getHeaderImage();
		AonFile leftBanner = controller.getLeftBanner();
		AonFile rightBanner = controller.getRightBanner();
		AonFile welcomeBanner = controller.getWelcomeBanner();
		if (header != null) {
			((Ecconfig) controller.getTo()).setHeaderImg(header.getData());

		}
		if (leftBanner != null) {
			((Ecconfig) controller.getTo()).setLeftBanner(leftBanner.getData());

		}
		if (rightBanner != null) {
			((Ecconfig) controller.getTo()).setRightBanner(rightBanner
					.getData());

		}
		if (welcomeBanner != null) {
			((Ecconfig) controller.getTo()).setWelcomeBanner(welcomeBanner
					.getData());

		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {

		EcconfigController controller = (EcconfigController) event
				.getController();
		Ecconfig config = (Ecconfig) getController().getTo();

		if (config.getHeaderImg() != null) {
			AonFile header = new AonFile();
			header.setData(config.getHeaderImg());
			controller.setHeaderImage(header);
		}

		if (config.getRightBanner() != null) {
			AonFile rightBanner = new AonFile();
			rightBanner.setData(config.getRightBanner());
			controller.setRightBanner(rightBanner);
		}

		if (config.getLeftBanner() != null) {
			AonFile leftBanner = new AonFile();
			leftBanner.setData(config.getLeftBanner());
			controller.setLeftBanner(leftBanner);
		}

		if (config.getWelcomeBanner() != null) {
			AonFile welcomeBanner = new AonFile();
			welcomeBanner.setData(config.getWelcomeBanner());
			controller.setWelcomeBanner(welcomeBanner);
		}

	}

}