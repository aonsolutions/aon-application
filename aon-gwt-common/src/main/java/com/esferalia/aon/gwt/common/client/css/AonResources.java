package com.esferalia.aon.gwt.common.client.css;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface AonResources extends ClientBundle, Images {
	
	@Source("aon.css")
	@CssResource.NotStrict
	AonCSS css();
	
	@Source("aonCalendar.css")
	@CssResource.NotStrict
	AonCalendarResources aonCalendar();
	
	@Source("aonDataGrid.css")
	@CssResource.NotStrict
	AonDataGrid aonDataGrid();

	@Source("images/aon-icon-rowSelector.png")
	ImageResource aonIconRowSelector();

	@Source("images/aon-icon-trash.png")
	ImageResource aonIconTrash();

	@Source("images/aon-icon-paste.png")
	ImageResource aonIconPaste();

	@Source("images/aon-icon-moveUp.png")
	ImageResource aonIconMoveUp();
	
	@Source("images/aon-icon-duplicate.png")
	ImageResource aonIconDuplicate();
	
	@Source("images/aon-icon-delete.png")
	ImageResource aonIconDelete();
	
	@Source("images/aon-icon-cleartrash.png")
	ImageResource aonIconClearTrash();

	@Source("images/aon-icon-deletetrash.png")
	ImageResource aonIconDeleteTrash();
	
	@Source("images/aon-icon-draft.png")
	ImageResource aonIconDraft();

	@Source("images/aon-icon-check.png")
	ImageResource aonIconCheck();
	
	@Source("images/aon-icon-checked.png")
	ImageResource aonIconChecked();
	
	@Source("images/aon-icon-enterprise.png")
	ImageResource aonIconEnterprise();
	
	@Source("images/data.png")
	ImageResource aonIconCompanyData();
	
	@Source("images/aon-icon-list-data.png")
	ImageResource aonListData();

	@Source("images/aon-timer.gif")
	ImageResource aonTimer();
	
	@Source("images/aon-aeat.png")
	ImageResource aonAeat();
	@Source("images/aon-aeat-bw.png")
	ImageResource aonAeatBW();
	
	@Source("images/aon-icon-araba.png")
	ImageResource aonIconAraba();
	@Source("images/aon-icon-araba-bw.png")
	ImageResource aonIconArabaBW();
	
	@Source("images/aon-icon-bizkaia.png")
	ImageResource aonIconBizkaia();
	@Source("images/aon-icon-bizkaia-bw.png")
	ImageResource aonIconBizkaiaBW();
	
	@Source("images/aon-icon-gipuzkoa.png")
	ImageResource aonIconGipuzkoa();
	@Source("images/aon-icon-gipuzkoa-bw.png")
	ImageResource aonIconGipuzkoaBW();
	
	@Source("images/aon-icon-navarra.png")
	ImageResource aonIconNavarra();
	@Source("images/aon-icon-navarra-bw.png")
	ImageResource aonIconNavarraBW();

	@Source("images/changed.png")
	ImageResource aonChanged();

	@Source("images/input-warn.png")
	ImageResource aonInputError();
	
	@Source("images/curly-lt.png")
	ImageResource aonCurlyLT();

	@Source("images/aon-icon-m111.png")
	ImageResource aonIconM111();

	@Source("images/aon-icon-m115.png")
	ImageResource aonIconM115();
	
	@Source("images/aon-icon-m123.png")
	ImageResource aonIconM123();
	
	@Source("images/aon-icon-m130.png")
	ImageResource aonIconM130();
	
	@Source("images/aon-icon-m131.png")
	ImageResource aonIconM131();
	
	@Source("images/aon-icon-m140.png")
	ImageResource aonIconM140();
	
	@Source("images/aon-icon-m180.png")
	ImageResource aonIconM180();
	
	@Source("images/aon-icon-m184.png")
	ImageResource aonIconM184();
	
	@Source("images/aon-icon-m190.png")
	ImageResource aonIconM190();
	
	@Source("images/aon-icon-m193.png")
	ImageResource aonIconM193();
	
	@Source("images/aon-icon-m200.png")
	ImageResource aonIconM200();
	
	@Source("images/aon-icon-m303.png")
	ImageResource aonIconM303();
	
	@Source("images/aon-icon-m340.png")
	ImageResource aonIconM340();
	
	@Source("images/aon-icon-m347.png")
	ImageResource aonIconM347();
	
	@Source("images/aon-icon-m349.png")
	ImageResource aonIconM349();
	
	@Source("images/aon-icon-m390.png")
	ImageResource aonIconM390();
	
	@Source("images/family.png")
	ImageResource aonIconActivities();
	
	@Source("images/public.png")
	ImageResource aonIconPublic();
	
	@Source("images/aon-icon-point-green.png")
	ImageResource aonIconPointGreen();
	
	@Source("images/aon-icon-point-light-green.png")
	ImageResource aonIconPointLightGreen();
	
	@Source("images/aon-icon-point-orange.png")
	ImageResource aonIconPointOrange();
	
	@Source("images/aon-icon-point-red.png")
	ImageResource aonIconPointRed();
	
	@Source("images/aon-icon-point-yellow.png")
	ImageResource aonIconPointYellow();
	
	@Source("images/aon-icon-loupe.png")
	ImageResource aonIconLoupe();
	
	@Source("images/aon-icon-question.png")
	ImageResource aonIconQuestion();
	
	// ------------------------------------------------------------------------
}

