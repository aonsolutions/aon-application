package com.code.aon.ui.common.listener;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;

public class LogPhaseListener implements PhaseListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public long startTime;

	private final static Logger LOGGER = LoggerFactory.getLogger(LogPhaseListener.class);

	public void afterPhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			long endTime = System.nanoTime();
			long diffMs = (long) ((endTime - startTime) * 0.000001);
			LOGGER.info("Execution Time = {}ms", diffMs);
		}
	}

	public void beforePhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
			startTime = System.nanoTime();
		}
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}

