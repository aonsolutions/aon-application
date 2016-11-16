package com.esferalia.aon.gwt.issues.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public abstract class AonToolbar extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, AonToolbar> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField Label  title;
 
    @UiField PaperIconButton temporalButton;
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
        setTitle(title);
    }
    
    // ----------- TITLE LABEL
    
    public void setTitle(String title){
    	this.title.setText(title);
    }

    // ----------- TEMPORAL BUTTON    
    
	protected abstract void onTemporalButtonClick();

    public AonToolbar setVisibleTemporalButton(Boolean isVisible){
    	menuButton.setVisible(isVisible);
    	return this;
    }

    @UiHandler("temporalButton")
	void temporalButtonClick(ClickEvent event) {
    	onTemporalButtonClick();
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
    	moreOptionButton.setVisible(isVisible);
    	return this;
    }

    @UiHandler("infoButton")
  	void infoButtonClick(ClickEvent event) {
      	onInfoButtonClick();
    }
}
