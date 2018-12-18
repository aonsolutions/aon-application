package com.esferalia.aon.gwt.common.client.polymer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public abstract class AonToolbar extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, AonToolbar> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField PaperButton titleButton;
    @UiField Label  titleLabel;

    @UiField PaperIconButton sendButton;
    @UiField PaperIconButton downloadButton;
    @UiField PaperIconButton fastFilterButton;
    @UiField PaperIconButton statsButton;
    @UiField PaperIconButton menuButton;
    @UiField PaperIconButton infoButton;
    @UiField PaperIconButton refreshButton;
    @UiField PaperIconButton addButton;
    @UiField PaperIconButton editButton;
    @UiField PaperIconButton deleteButton;
    @UiField PaperIconButton moreOptionButton;
    
    // ----------- CONSTRUCTORS
    
    public AonToolbar() {
    	initWidget(binder.createAndBindUi(this));
    }
    
    public AonToolbar(String title) {
        initWidget(binder.createAndBindUi(this));

        this.titleButton.setNoink(true);
        this.titleButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onTitleClick();
			}
		});
        
        setTitle(title);
    }
    
    // ----------- TITLE LABEL
    protected abstract void onTitleClick();
    
    public void setTitle(String title){
    	this.titleLabel.setText(title);
    }
    
    // ----------- MENU BUTTON    
    
	protected abstract void onMenuButtonClick();

    public AonToolbar setVisibleMenuButton(Boolean isVisible){
    	menuButton.setVisible(isVisible);
    	return this;
    }

    @UiHandler("menuButton")
	void menuButtonClick(ClickEvent event) {
    	onMenuButtonClick();
    }
    
    // ----------- REFRESH BUTTON
    
	protected abstract void onRefreshButtonClick();
    
    public AonToolbar setVisibleRefreshButton(Boolean isVisible){
    	refreshButton.setVisible(isVisible);
    	return this;
    }
    
    @UiHandler("refreshButton")
	void refreshButtonClick(ClickEvent event) {
    	onRefreshButtonClick();
    }
    
    // ----------- ADD BUTTON
    
	protected abstract void onAddButtonClick();
    
    public AonToolbar setVisibleAddButton(Boolean isVisible){
    	addButton.setVisible(isVisible);
    	return this;
    }
    
    @UiHandler("addButton")
  	void addButtonClick(ClickEvent event) {
      	onAddButtonClick();
    }
    
    // ----------- EDIT BUTTON
    
	protected abstract void onEditButtonClick();
    
    public AonToolbar setVisibleEditButton(Boolean isVisible){
    	editButton.setVisible(isVisible);
    	return this;
    }
    
    @UiHandler("editButton")
  	void editButtonClick(ClickEvent event) {
      	onEditButtonClick(); 
    }
    
    // ----------- DELETE BUTTON
    
	protected abstract void onDeleteButtonClick();

    public AonToolbar setVisibleDeleteButton(Boolean isVisible){
    	deleteButton.setVisible(isVisible);
    	return this;
    }
    
    @UiHandler("deleteButton")
  	void deleteButtonClick(ClickEvent event) {
      	onDeleteButtonClick();
    }
    
    // ----------- MORE OPTION BUTTON
    
	protected abstract void onMoreOptionButtonClick();

    public AonToolbar setVisibleMoreOptionButton(Boolean isVisible){
    	moreOptionButton.setVisible(isVisible);
    	return this;
    }

    @UiHandler("moreOptionButton")
  	void moreOptionButtonClick(ClickEvent event) {
      	onMoreOptionButtonClick();
    }
    
    // ----------- INFO BUTTON
    
	protected abstract void onInfoButtonClick();

    public AonToolbar setVisibleInfoButton(Boolean isVisible){
    	infoButton.setVisible(isVisible);
    	return this;
    }

    @UiHandler("infoButton")
  	void infoButtonClick(ClickEvent event) {
      	onInfoButtonClick();
    }
    
    // ----------- STATS BUTTON
    
	protected abstract void onStatsButtonClick();

    public AonToolbar setVisibleStatsButton(Boolean isVisible){
    	statsButton.setVisible(isVisible);
    	return this;
    }

    @UiHandler("statsButton")
  	void statsButtonClick(ClickEvent event) {
      	onStatsButtonClick();
    }
    
    // ----------- FAST FILTER BUTTON
    
   	protected abstract void onFastFilterButtonClick();

   	public AonToolbar setVisibleFastFilterButton(Boolean isVisible){
   		fastFilterButton.setVisible(isVisible);
       	return this;
    }

   	@UiHandler("fastFilterButton")
   	void fastFilterButtonClick(ClickEvent event) {
   		onFastFilterButtonClick();
   	}
   	
// ----------- DOWNLOAD BUTTON
    
   	protected abstract void onDownloadButtonClick();

   	public AonToolbar setVisibleDownloadButton(Boolean isVisible){
   		downloadButton.setVisible(isVisible);
       	return this;
    }

   	@UiHandler("downloadButton")
   	void downloadButtonClick(ClickEvent event) {
   		onDownloadButtonClick();
   	}
   	
// ----------- DOWNLOAD BUTTON
    
   	protected abstract void onSendButtonClick();

   	public AonToolbar setVisibleSendButton(Boolean isVisible){
   		sendButton.setVisible(isVisible);
       	return this;
    }

   	@UiHandler("sendButton")
   	void sendButtonClick(ClickEvent event) {
   		onSendButtonClick();
   	}
   
 // ----------- ALL BUTTONS

   	public AonToolbar setVisibleAllButton(Boolean isVisible){
   		menuButton.setVisible(isVisible);
   		refreshButton.setVisible(isVisible);
   		addButton.setVisible(isVisible);
   		editButton.setVisible(isVisible);
   		deleteButton.setVisible(isVisible);
   		moreOptionButton.setVisible(isVisible);
   		infoButton.setVisible(isVisible);
   		statsButton.setVisible(isVisible);
   		fastFilterButton.setVisible(isVisible);
   		downloadButton.setVisible(isVisible);
   		sendButton.setVisible(isVisible);
       	return this;
    }
}
