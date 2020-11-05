package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;

public interface AonSolutionsResources extends ClientBundle {

	@Source("aon-solutions.css")
	@CssResource.NotStrict
	AonSolutionsCSS css();
	
	@Source("images/aon-ocr.png")
	ImageResource aonOcrDrop();
	
	@Source("images/logo-tedi-gray.png")
	ImageResource aonTediLogoGray();

	@Source("images/logo-tedi-red.png")
	ImageResource aonTediLogoRed();

	@Source("images/logo-tedi-snapshot.png")
	ImageResource aonTediSnapshotLogo();

	@Source("icons/aon-icon-add.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconAdd();

	@Source("icons/aon-icon-save.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconSave();

	@Source("icons/aon-icon-delete.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconDelete();

	@Source("icons/aon-icon-back.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconBack();

	@Source("icons/aon-icon-search.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconSearch();

	@Source("icons/aon-icon-refresh.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconRefresh();

	@Source("icons/aon-icon-clear.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconClear();

	@Source("icons/aon-icon-copy.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconCopy();

	@Source("icons/aon-icon-audit.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconAudit();

	@Source("icons/aon-icon-pdf.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconPdf();

	@Source("icons/aon-icon-excel.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconExcel();
	
	@Source("icons/aon-icon-comments.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconComments();

	@Source("icons/aon-icon-no-comments.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconNoComments();

	@Source("icons/aon-icon-accept.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconAccept();

	@Source("icons/aon-icon-cancel.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconCancel();

	@Source("icons/aon-icon-minimize.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconMinimize();
	
	@Source("icons/aon-icon-maximize.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconMaximize();

	@Source("icons/aon-icon-info.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconInfo();

	@Source("icons/aon-icon-error.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconError();
	
	@Source("icons/aon-icon-warning.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconWarning();

	@Source("icons/aon-icon-euro.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconEuro();
	
	@Source("icons/aon-icon-book.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconBook();

	@Source("icons/aon-icon-list.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconList();

	@Source("icons/aon-icon-history.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconHistory();

	@Source("icons/aon-icon-undo.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconUndo();

	@Source("icons/aon-icon-redo.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconRedo();

	@Source("icons/aon-icon-close.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconClose();

	@Source("icons/aon-icon-check.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconCheck();

	@Source("icons/aon-icon-checked.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconChecked();

	@Source("icons/aon-icon-launch.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconLaunch();
	
	@Source("icons/aon-icon-swap.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconSwap();
	
	@Source("icons/aon-icon-help.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconHelp();

	@Source("icons/aon-icon-calc.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconCalc();

	@Source("icons/aon-icon-changed.svg")
	@MimeType("image/svg+xml")
	DataResource aonIconChanged();
}

