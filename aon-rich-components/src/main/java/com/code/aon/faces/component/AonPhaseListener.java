package com.code.aon.faces.component;

import static com.code.aon.faces.controller.IRichConstants.LABELS_MAP;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class AonPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -849797114333232289L;
	
	@Override
	public void afterPhase(PhaseEvent event) {
		UIViewRoot root = event.getFacesContext().getViewRoot(); 
		Map map = (Map) root.getAttributes().get(LABELS_MAP);
		if ( map != null ) {
			map.clear();
			root.getAttributes().remove(LABELS_MAP);
		}
	}

	@Override
	public void beforePhase(PhaseEvent event) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
