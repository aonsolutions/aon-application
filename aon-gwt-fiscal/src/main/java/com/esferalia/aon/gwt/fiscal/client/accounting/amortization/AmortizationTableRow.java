package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

class AmortizationTableRow extends AonDisplayGridRow {
	
	AmortizationTableRow(AmortizationModuleOptions opts, Amortization am) {
		String initialDate = ensure(am.getInitialDate(), () -> AON.DATE_FORMAT.format(am.getInitialDate()), AonStringUtils.EMPTY);
		Label initialDateLabel = new Label(initialDate);
		String deadline = ensure(am.getDeadline(), () -> AON.DATE_FORMAT.format(am.getDeadline()), AonStringUtils.EMPTY);
		Label deadlineLabel = new Label(deadline);
		String description = ensure(am.getDescription(), am::getDescription, AonStringUtils.EMPTY);
		description = AonStringUtils.abbreviate(description, 60);
		Label descriptionLabel = new Label(description);
		String fixedAssetAccount = ensure(am.getFixedAssetAccount(), () -> am.getFixedAssetAccount().getFullName(), AonStringUtils.EMPTY);
		fixedAssetAccount = AonStringUtils.abbreviate(fixedAssetAccount, 40);
		Label fixedAssetAccountLabel = new Label(fixedAssetAccount);
		String comments = ensure(am.getComments(), am::getComments, AonStringUtils.EMPTY);
		Label commentsLabel = new Label();
		commentsLabel.setTitle(comments);
		commentsLabel.setText(AonStringUtils.abbreviate(comments, 30));
		this
			.addCell(initialDateLabel)
			.addCell(deadlineLabel)
			.addCell(descriptionLabel)
			.addCell(fixedAssetAccountLabel,AON.CSS.aonNowrap())
			.addCell(new AonDoubleLabel(am.getAmount()), AON.CSS.aonTextRight())
			.addCell(commentsLabel,AON.CSS.aonNowrap())
		;
	}
	
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	static void fillHeader(AonDisplayGridHeaderRow aonDisplayTableHeaderRow) {
		aonDisplayTableHeaderRow
			.addCell(new Label(AON.MSG.from()),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label(AON.MSG.until()),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidth300())
			.addCell(new Label(AON.MSG.fixedAssetAccount()),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
			.addCell(new Label(AON.MSG.amount()),AON.CSS.aonWidth120(),AON.CSS.aonTextRight())
			.addCell(new Label(AON.MSG.comments()),AON.CSS.aonWidthAuto(),AON.CSS.aonNowrap())
		;
	}
	
}
