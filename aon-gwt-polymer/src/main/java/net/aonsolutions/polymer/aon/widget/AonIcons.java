package net.aonsolutions.polymer.aon.widget;

import com.vaadin.polymer.PolymerWidget;

import net.aonsolutions.polymer.aon.AonIconsElement;

public class AonIcons extends PolymerWidget {
    /**
     * Default Constructor.
     */
    public AonIcons() {
       this("");
    }

    /**
     * Constructor used by UIBinder to create widgets with content.
     */
    public AonIcons(String html) {
        super(AonIconsElement.TAG, AonIconsElement.SRC, html);
    }

    /**
     * Gets a handle to the Polymer object's underlying DOM element.
     */
    public AonIconsElement getPolymerElement() {
        return (AonIconsElement) getElement();
    }





}
