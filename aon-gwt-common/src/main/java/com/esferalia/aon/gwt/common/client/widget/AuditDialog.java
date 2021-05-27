package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AuditDialog extends CustomDialog {

	public void show(HasAudit auditable) {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.audit());
		FlowPanel panel = new FlowPanel();
		Label created = new Label();
		created.setStyleName(AON.AON_CSS.aonLinkListItem());
		created.addStyleName(AON.AON_CSS.aonMargin());
		created.setText(AonStringUtils.isEmpty(auditable.getCreationUser()) ? AON.MSG
				.emptyCreatedBy() : AON.MSG.createdBy(
				auditable.getCreationUser(), auditable.getCreationDate()));
		panel.add(created);
		Label modified = new Label();
		modified.setText(AonStringUtils.isEmpty(auditable.getModificationUser()) ? AON.MSG
				.emptyModifiedBy() : AON.MSG.modifiedBy(
				auditable.getModificationUser(),
				auditable.getModificationDate()));
		modified.setStyleName(AON.AON_CSS.aonLinkListItem());
		modified.addStyleName(AON.AON_CSS.aonMargin());
		panel.add(modified);
		add(panel);
		center();
		show();
	}

}
