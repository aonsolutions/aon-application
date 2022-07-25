package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTabLayoutPanel extends TabLayoutPanel {

	private final List<String> tabList = new LinkedList<>();

    public AonTabLayoutPanel(double barHeight, Unit barUnit) {
		super(barHeight, barUnit);
	}
    
    @Override
    public void add(Widget widget, final String label) {
    	super.add(widget, label);
        tabList.add(label);
    }
    
    @Override
    public boolean remove(Widget widget) {
    	int i = getWidgetIndex(widget);
    	if (i < 0) return false; 
   		tabList.remove(i);
   		return super.remove(widget);
    }
    
    public Widget getOrCreateWidget(String label,Supplier<Widget> supplier) {
    	int i = getWidgetIndex(label);
    	if (i < 0) {
    		Widget w = supplier.get();
    		this.add(w,label);
    		return w;
    	}
    	return getWidget( i );
    }
    
    public Widget getWidget(String label) {
    	int i = getWidgetIndex(label);
    	if (i < 0) return null;
    	return super.getWidget(i);
    }

    public boolean remove(String label) {
    	int i = getWidgetIndex(label);
    	if (i < 0) return false;
    	tabList.remove(i);
    	return super.remove(i);
    }

    private int getWidgetIndex(final String label) {
        return tabList.indexOf(label);
    }
    
    public void selectTab(final String label) {
    	super.selectTab(getWidgetIndex(label));
    }
}
