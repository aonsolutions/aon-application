package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MediaTypeListBox;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MediaType.IMediaTypeVisitor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class AonRegistryMediaGrid extends AonDisplayGrid {
	
	private RegistryFull<?> registryFull;
	private FlowPanel rootPanel;
	
	/**
	 * The constructor
	 * @param registryFull the registry with the addresses
	 * @param rootPanel
	 */
	public AonRegistryMediaGrid(RegistryFull<?> registryFull, FlowPanel rootPanel) {
		this.registryFull = registryFull;
		this.rootPanel = rootPanel;
	}
	
	/**
	 * Shows the medias of a registry
	 */
	public void getMediasGrid() {
		FlowPanel labelContainer = new FlowPanel();
		labelContainer.setStyleName(AON.CSS.aonFlexBlock());
		labelContainer.addStyleName(AON.CSS.aonBorderBottom());
		
		Label mediasLabel = new Label(AON.MSG.contacts());
		mediasLabel.addStyleName(AON.CSS.aonFlexGrow1());
		mediasLabel.addStyleName(AON.CSS.aonBold());
		labelContainer.add(mediasLabel);
		
		AonTableButton addMedia = new AonTableButton( AON.MSG.addContact(), AON.CSS.aonIconAdd() );
		rootPanel.add(labelContainer);
		
		this.addStyleName(AON.CSS.aonWidthAlmostAll());
		this.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(this);

		addMedia.addClickHandler(event -> {
			if ( registryFull.getMedias() == null || registryFull.getMedias().isEmpty()) {
				paintMediaHeader(this);		
			}
			RegistryMedia registryMedia = new RegistryMedia();
			registryMedia.setMedia(MediaType.FIXED_PHONE)
				.setDirty(false);
			registryFull.addMedia(registryMedia);
			paintRow(this, registryMedia );
		});
		
		labelContainer.add(addMedia);
		if (registryFull.getMedias() != null ) {
			if (!registryFull.getMedias().isEmpty()) {
				paintMediaHeader(this);		
			}
			for (RegistryMedia registryMedia : registryFull.getMedias() ) {
				paintRow(this, registryMedia );
			}
		}
	}
	
	/**
	 * Adds the cells of the media table
	 * @param displayTab
	 */
	private void paintMediaHeader(AonRegistryMediaGrid displayTab) {
		displayTab.addHeaderRow()
			.addCell(new InlineLabel(AON.MSG.type()))
			.addCell(new InlineLabel(AON.MSG.data()))
			.addCell(new InlineLabel(AON.MSG.comments()))
			.addCell(new InlineLabel("ADM."))
			.addCell(new InlineLabel("COM."))
			.addCell(new InlineLabel("TEC."))
			.addCell(new InlineLabel(""));
	}

	/**
	 * Paint one media
	 * @param displayTab
	 * @param options the module options
	 * @param registryMedia
	 */
	private void paintRow(AonRegistryMediaGrid displayTab, RegistryMedia registryMedia) {
		final MediaTypeListBox mediaBox = new MediaTypeListBox();
		mediaBox.setValue(registryMedia.getMedia());
		final AonTextBox valueBox = new AonTextBox();
		valueBox.setValue(registryMedia.getValue());
		valueBox.addValueChangeHandler(event -> registryMedia.setValue(valueBox.getValue()));
		
		final AonTextBox commentsBox = new AonTextBox();
		commentsBox.setValue(registryMedia.getComment());
		commentsBox.setVisibleLength(35);
		commentsBox.setMaxLength(64);
		commentsBox.addValueChangeHandler(event -> registryMedia.setComment(commentsBox.getValue()));
		
		final CheckBox admBox = new CheckBox();
		admBox.setValue(registryMedia.isAdministrative());
		admBox.addClickHandler(event -> registryMedia.setAdministrative(admBox.getValue()));
		
		final CheckBox comBox = new CheckBox();
		comBox.setValue(registryMedia.isCommercial());
		comBox.addClickHandler(event -> registryMedia.setCommercial(comBox.getValue()));
		
		final CheckBox tecBox = new CheckBox();
		tecBox.setValue(registryMedia.isTechnical());
		tecBox.addClickHandler(event -> registryMedia.setTechnical(tecBox.getValue()));

		final AonTableButton deleteMediaButton = new AonTableButton( AON.MSG.deleteContact(),AON.CSS.aonIconDelete());
		final AonTableButton restoreMediaButton = new AonTableButton( AON.MSG.restoreAction(),AON.CSS.aonIconRestoreDeleted());
		
		deleteMediaButton.setVisible(!registryMedia.isRemoved());
		restoreMediaButton.setVisible(registryMedia.isRemoved());
		
		FlowPanel buttons = new FlowPanel();
		buttons.add(deleteMediaButton);
		buttons.add(restoreMediaButton);
		
		deleteMediaButton.addClickHandler(event -> {
			registryMedia.setRemoved(true);
			deleteMediaButton.setVisible(false);
			restoreMediaButton.setVisible(true);
			mediaBox.addStyleName(AON.CSS.aonTextLineThrough());
			mediaBox.setEnabled(false);
			valueBox.addStyleName(AON.CSS.aonTextLineThrough());
			valueBox.setEnabled(false);
			commentsBox.addStyleName(AON.CSS.aonTextLineThrough());
			commentsBox.setEnabled(false);
			admBox.setEnabled(false); 
			comBox.setEnabled(false);
			tecBox.setEnabled(false);
		});
		
		restoreMediaButton.addClickHandler(event -> {
			registryMedia.setRemoved(false);
			restoreMediaButton.setVisible(false);
			deleteMediaButton.setVisible(true);
			mediaBox.removeStyleName(AON.CSS.aonTextLineThrough());
			mediaBox.setEnabled(true);
			valueBox.removeStyleName(AON.CSS.aonTextLineThrough());
			valueBox.setEnabled(true);
			commentsBox.removeStyleName(AON.CSS.aonTextLineThrough());
			commentsBox.setEnabled(true);
			admBox.setEnabled(true); 
			comBox.setEnabled(true);
			tecBox.setEnabled(true);
		});
		
		
		
		IMediaTypeVisitor mediaVisitor = new IMediaTypeVisitor() {
			@Override public void visitUnknown() {
				// Empty method
			}
			@Override public void visitFixedPhone() {
				valueBox.setVisibleLength(15);
				valueBox.setMaxLength(15);
			}
			@Override public void visitCellular() { 
				valueBox.setVisibleLength(15);
				valueBox.setMaxLength(15);
			}
			@Override public void visitFax() {
				valueBox.setVisibleLength(15);
				valueBox.setMaxLength(15);
			}
			@Override public void visitEmail() { 
				valueBox.setVisibleLength(35);
				valueBox.setMaxLength(64);
			}
			@Override public void visitWeb() { 
				valueBox.setVisibleLength(35);
				valueBox.setMaxLength(64);
			} 
		};
		
		mediaBox.addChangeHandler(event -> {
			registryMedia.setMedia(mediaBox.getValue());
			registryMedia.getMedia().visit( mediaVisitor );
		});
		
		registryMedia.getMedia().visit( mediaVisitor );
		displayTab.addRow()
			.addCell(mediaBox)
			.addCell(valueBox)
			.addCell(commentsBox)
			.addCell(admBox)
			.addCell(comBox)
			.addCell(tecBox)
			.addCell(buttons)
			;
		
	}
}
