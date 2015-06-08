package old.impuesto.sociedades.e2013;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.google.gwt.user.client.ui.Label;

public interface IMod200Page {
	
	Map<Mod2002013Key, Label> getLabels();
	

}
