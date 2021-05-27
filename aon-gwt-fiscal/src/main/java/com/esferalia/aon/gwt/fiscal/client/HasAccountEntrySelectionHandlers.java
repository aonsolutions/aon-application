package com.esferalia.aon.gwt.fiscal.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAccountEntrySelectionHandlers extends HasHandlers {

  HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler);
  
}
