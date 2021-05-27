package com.code.aon.ui.registry.controller.event;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryNoteControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController controller = event.getController();
			controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.OBSERVATION);
			controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.FACTURAE);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryNote note = (RegistryNote)event.getController().getTo();
		note.setNoteDate(new Date());
	}

}