package old.impuesto.sociedades.e2013;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ValueBoxBase;

public class Mod200ChangedEvent<T extends ValueBoxBase<?>> extends ChangeEvent {
	private Mod2002013Key key;
	private T sourceWidget;
	
	public Mod200ChangedEvent(Mod2002013Key key, T sourceWidget) {
		this.key = key;
		this.sourceWidget = sourceWidget;
	}

	public Mod2002013Key getKey() {
		return key;
	}

	public T getSourceWidget() {
		return sourceWidget;
	}
	
}
