package com.esferalia.aon.gwt.common.client.css;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface AonResources extends ClientBundle, Images {

	@Source("aon.css")
	@CssResource.NotStrict
	AonCSS css();
	
	@Source("images/aon-dataTable-header.png")
	ImageResource aonDataTableHeader();
	
	@Source("aonCalendar.css")
	@CssResource.NotStrict
	AonCalendarResources aonCalendar();
	
	@Source("images/aon-menuBar.png")
	ImageResource menuBar();

	@Source("images/aon-icon-rowSelector.png")
	ImageResource aonIconRowSelector();

	@Source("images/aon-icon-trash.png")
	ImageResource aonIconTrash();

	@Source("images/aon-icon-paste.png")
	ImageResource aonIconPaste();
	
	@Source("images/aon-icon-printer.png")
	ImageResource aonIconPrinter();
	
	@Source("images/aon-icon-validate.png")
	ImageResource aonIconValidate();
	
	@Source("images/aon-icon-moveUp.png")
	ImageResource aonIconMoveUp();
	
	@Source("images/aon-icon-duplicate.png")
	ImageResource aonIconDuplicate();
	
	@Source("images/aon-icon-save.png")
	ImageResource aonIconSave();
	
	@Source("images/aon-icon-rubber.png")
	ImageResource aonIconRubber();
	
	@Source("images/aon-icon-goto.png")
	ImageResource aonIconGoto();

	@Source("images/aon-icon-europe.png")
	ImageResource aonIconEurope();

	@Source("images/aon-icon-minus.png")
	ImageResource aonIconMinus();
	
	@Source("images/aon-icon-attach.png")
	ImageResource aonIconAttach();

	@Source("images/aon-icon-plus.png")
	ImageResource aonIconPlus();

	@Source("images/aon-icon-delete.png")
	ImageResource aonIconDelete();

	@Source("images/aon-icon-cancel.png")
	ImageResource aonIconCancel();

	@Source("images/aon-icon-search.png")
	ImageResource aonIconSearch();

	@Source("images/aon-icon-previous.png")
	ImageResource aonIconPrevious();
	
	@Source("images/aon-icon-next.png")
	ImageResource aonIconNext();
	
	@Source("images/aon-icon-reset.png")
	ImageResource aonIconReset();

	@Source("images/aon-icon-error.png")
	ImageResource aonIconError();

	@Source("images/aon-icon-audit.png")
	ImageResource aonIconAudit();

	@Source("images/aon-icon-import.png")
	ImageResource aonIconImport();

	@Source("images/aon-icon-wizard.png")
	ImageResource aonIconWizard();
	
	@Source("images/aon-icon-excel.png")
	ImageResource aonIconExcel();

	@Source("images/aon-icon-cleartrash.png")
	ImageResource aonIconClearTrash();

	@Source("images/aon-icon-deletetrash.png")
	ImageResource aonIconDeleteTrash();
	
	@Source("images/aon-icon-draft.png")
	ImageResource aonIconDraft();
	
	@Source("images/aon-icon-notices.png")
	ImageResource aonIconNotices();
	
	@Source("images/aon-icon-title.png")
	ImageResource aonIconTitle();
	
	@Source("images/aon-icon-issue-opened.png")
	ImageResource aonIconIssueOpened();
	
	@Source("images/aon-icon-issue-closed.png")
	ImageResource aonIconIssueClosed();
	
	@Source("images/aon-icon-issue-reopened-blue.png")
	ImageResource aonIconIssueReOpenedBlue();
	
	@Source("images/aon-icon-issue-reopened-break.png")
	ImageResource aonIconIssueReOpenedBreak();
	
	@Source("images/aon-icon-issue-opened-green.png")
	ImageResource aonIconIssueOpenedGreen();
	
	@Source("images/aon-icon-issue-duplicated.png")
	ImageResource aonIconIssueDuplicated();

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
	
	@Source("images/logo-tedi.png")
	ImageResource aonTediTimer();
	
	@Source("images/aon-aeat-header-image.png")
	ImageResource aonAeatHeaderImage();
	@Source("images/aon-aeat.png")
	ImageResource aonAeat();
	@Source("images/aon-aeat-signed.png")
	ImageResource aonAeatSigned();
	@Source("images/aon-aeat-bw.png")
	ImageResource aonAeatBW();
	
	@Source("images/aon-araba-header-image.png")
	ImageResource aonArabaHeaderImage();
	@Source("images/aon-icon-araba.png")
	ImageResource aonIconAraba();
	@Source("images/aon-icon-araba-bw.png")
	ImageResource aonIconArabaBW();
	
	@Source("images/aon-bizkaia-header-image.png")
	ImageResource aonBizkaiaHeaderImage();
	@Source("images/aon-icon-bizkaia.png")
	ImageResource aonIconBizkaia();
	@Source("images/aon-icon-bizkaia-bw.png")
	ImageResource aonIconBizkaiaBW();
	
	@Source("images/aon-gipuzkoa-header-image.png")
	ImageResource aonGipuzkoaHeaderImage();
	@Source("images/aon-icon-gipuzkoa.png")
	ImageResource aonIconGipuzkoa();
	@Source("images/aon-icon-gipuzkoa-bw.png")
	ImageResource aonIconGipuzkoaBW();
	
	@Source("images/aon-navarra-header-image.png")
	ImageResource aonNavarraHeaderImage();
	@Source("images/aon-icon-navarra.png")
	ImageResource aonIconNavarra();
	@Source("images/aon-icon-navarra-bw.png")
	ImageResource aonIconNavarraBW();

	@Source("images/changed.png")
	ImageResource aonChanged();

	@Source("images/close.png")
	ImageResource aonIconClose();

	@Source("images/input-warn.png")
	ImageResource aonInputError();

	@Source("images/input-calc.png")
	ImageResource aonInputCalc();
	
	@Source("images/curly-lt.png")
	ImageResource aonCurlyLT();

	@Source("images/curly-gt.png")
	ImageResource aonCurlyGT();

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
	
	@Source("images/aon-icon-invoice.png")
	ImageResource aonIconInvoice();
	
	@Source("images/aon-icon-diff.png")
	ImageResource aonIconDiff();
	
	@Source("images/aon-icon-payroll.png")
	ImageResource aonIconPayroll();
	
	@Source("images/aon-icon-calculator.png")
	ImageResource aonIconCalculator();

	@Source("images/aon-icon-modules.png")
	ImageResource aonIconModules();

	@Source("images/aon-icon-module.png")
	ImageResource aonIconModule();

	@Source("images/aon-icon-model.png")
	ImageResource aonIconModel();
	
	@Source("images/aon-icon-blocked.png")
	ImageResource aonIconBlocked();

	@Source("images/aon-icon-lock.png")
	ImageResource aonIconLock();

	@Source("images/aon-icon-unlock.png")
	ImageResource aonIconUnlock();

	@Source("images/aon-icon-info.png")
	ImageResource aonIconInfo();

	@Source("images/aon-icon-warning.png")
	ImageResource aonIconWarn();
	
	@Source("images/aon-icon-exception.png")
	ImageResource aonIconException();

	@Source("images/aon-registro-mercantil-image.png")
	ImageResource aonRegistroMercantilImage();

	@Source("images/aon-content-subTitle.png")
	ImageResource aonContentSubtitle();
	
	@Source("images/aon-content-title.png")
	ImageResource aonContentTitle();
	
	@Source("images/aon-content-title-internal.png")
	ImageResource aonContentTitleInternal();

	@Source("images/aon-icon-journal.png")
	ImageResource aonIconJournal();

	@Source("images/aon-icon-statement.png")
	ImageResource aonIconStatement();

	@Source("images/aon-icon-euro.png")
	ImageResource aonIconEuro();

	@Source("images/aon-icon-comment.gif")
	ImageResource aonIconComment();

	@Source("images/aon-icon-comment-red.gif")
	ImageResource aonIconCommentRed();
	
	@Source("images/aon-icon-root.png")
	ImageResource aonIconRoot();

	@Source("images/aon-icon-check-no.png")
	ImageResource aonIconCheckNo();

	@Source("images/aon-icon-check-yes.png")
	ImageResource aonIconCheckYes();

	@Source("images/aon-icon-progress-bar.png")
	ImageResource aonIconProgressBar();

	@Source("images/aon-icon-refresh.png")
	ImageResource aonIconRefresh();
	
	@Source("images/aon-icon-settings.png")
	ImageResource aonIconSettings();
	
	@Source("images/aon-icon-synchronize.png")
	ImageResource aonIconSynchronize();
	// ------------------------------------------------------------------------

//	@Source("images/checkyes.png")
//	ImageResource aonStatCheckyes();
	@Source("images/view.png")
	ImageResource aonStatView();
	
	@Source("images/checkyes.png")
	ImageResource aonStatCheckyes();
	
	@Source("images/aon-icon-list-data.png")
	ImageResource aonListStat();
	
	//-------------------- CUSTOM DIALOG BAR 
	
	@Source("images/close.gif")
	ImageResource close();
	
	@Source("images/header.png")
	ImageResource header();
	
	@Source("images/headerBack.png")
	ImageResource headerBack();
	

	@Source("images/aon-letter-a-orange-icon.png")
	ImageResource aonLetterAOrangeIcon();

	@Source("images/aon-letter-c-green-icon.png")
	ImageResource aonLetterCGreenIcon();
	
	@Source("images/aon-letter-p-blue-icon.png")
	ImageResource aonLetterPBlueIcon();
}

