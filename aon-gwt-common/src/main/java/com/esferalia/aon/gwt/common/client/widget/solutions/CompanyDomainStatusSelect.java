package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class CompanyDomainStatusSelect extends HTMLPanel implements HasChangeHandlers {

    class ActiveCommand implements ScheduledCommand {
        @Override
        public void execute() {
            setRegistryStatus(RegistryStatus.ACTIVE);
        }
    }

    class InactiveCommand implements ScheduledCommand {
        @Override
        public void execute() {
            setRegistryStatus(RegistryStatus.INACTIVE);
        }
    }

    class BlockedCommand implements ScheduledCommand {
        @Override
        public void execute() {
            askExpirationDate();
        }
    }

    class StatusContextMenu extends AonContextMenu {
        public StatusContextMenu() {
            addMenuItem("Activo", new ActiveCommand(), AON.CSS.aonIconCircleGreen(), "active");
            addMenuItem("Inactivo", new InactiveCommand(), AON.CSS.aonIconCircleRed(), "inactive");
            addMenuItem("Bloqueado", new BlockedCommand(), AON.CSS.aonIconCircleOrange(), "blocked");
        }

        private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
            MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.AON_CMD_BUTTON);
            item.ensureDebugId(debugId);
            return item;
        }
    }

    private static final String EMPTY_STRING = "";

    private StatusContextMenu statusContextMenu;
    private RegistryStatus registryStatus;
    private Date expirationDate;
    
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");

    public CompanyDomainStatusSelect(RegistryStatus registryStatus, Date expirationDate) {
        super(EMPTY_STRING);

        addStyleName(AON.CSS.aonItemFlex());
        getElement().getStyle().setProperty("border", "1px solid lightgray");
        getElement().getStyle().setProperty("border-radius", "10px");
        getElement().getStyle().setProperty("padding", "5px");
        getElement().getStyle().setProperty("cursor", "pointer");

        this.registryStatus = registryStatus;
        this.expirationDate = expirationDate;

        statusContextMenu = new StatusContextMenu();

        addDomHandler(event -> {
            NativeEvent nativeEvent = event.getNativeEvent();
            statusContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
            statusContextMenu.show();
        }, ClickEvent.getType());

        applySpecialFutureExpirationRule();
        addStatusInput();
    }

    private void addStatusInput() {
        clear();

        boolean futureExpiration = expirationDate != null && expirationDate.after(new Date());

        HTMLPanel circleStatus = new HTMLPanel(EMPTY_STRING);
        circleStatus.setStyleName(
            futureExpiration
                ? AON.AON_CIRCLE_ORANGE
                : (registryStatus == RegistryStatus.ACTIVE
                    ? AON.AON_CIRCLE_GREEN
                    : (registryStatus == RegistryStatus.INACTIVE
                        ? AON.AON_CIRCLE_RED
                        : AON.AON_CIRCLE_ORANGE))
        );
        add(circleStatus);

        Label status = new Label(
            futureExpiration
                ? "Activo"
                : (registryStatus == RegistryStatus.ACTIVE
                    ? "Activo"
                    : (registryStatus == RegistryStatus.INACTIVE ? "Inactivo" : "Bloqueado"))
        );
        add(status);

        if (expirationDate != null) {
            AonToolbarSmallButton calendar = new AonToolbarSmallButton("Fecha Expiraci\u00f3n : " + formatDate.format(expirationDate), AON.CSS.aonIconCalendarClock());
            add(calendar);
        }

        AonToolbarSmallButton arrowDown = new AonToolbarSmallButton("", AON.CSS.aonIconDown());
        add(arrowDown);
    }

    private void askExpirationDate() {

        AonCustomDialog dialog = new AonCustomDialog();
        dialog.setCaption("Fecha Expiraci\u00f3n");

        HTMLPanel content = new HTMLPanel("");
        content.addStyleName(AON.CSS.aonFlexColumn());

        AonCustomDateBox date = new AonCustomDateBox("F. Expiraci\u00f3n");
        content.add(date);

        HTMLPanel buttonsPanel = new HTMLPanel("");
        buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
        buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");

        Button okButton = new Button();
        okButton.setStyleName(AON.CSS.aonOkButton());
        okButton.setText(AON.MSG.accept());
        okButton.addClickHandler(e -> {

            Date selected = date.getValue();
            if (selected != null) {
                expirationDate = selected;
                registryStatus = RegistryStatus.BLOCKED;
                addStatusInput();
                fireChangeEvent();
            }

            dialog.hide();
        });
        buttonsPanel.add(okButton);

        Button cancelButton = new Button();
        cancelButton.setStyleName(AON.CSS.aonCancelButton());
        cancelButton.addStyleName(AON.CSS.aonMarginLeft());
        cancelButton.setText(AON.MSG.cancelAction());
        cancelButton.addClickHandler(e -> dialog.hide());
        buttonsPanel.add(cancelButton);

        content.add(buttonsPanel);

        dialog.add(content);
        dialog.center();
        dialog.show();
    }


    private void applySpecialFutureExpirationRule() {
        if (expirationDate != null && expirationDate.after(new Date())) {
            registryStatus = RegistryStatus.ACTIVE;
        }
    }

    private void fireChangeEvent() {
        ChangeEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addHandler(handler, ChangeEvent.getType());
    }

    public RegistryStatus getValue() {
        return this.registryStatus;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setRegistryStatus(RegistryStatus registryStatus) {
        this.registryStatus = registryStatus;

        if (registryStatus == RegistryStatus.ACTIVE || registryStatus == RegistryStatus.INACTIVE) {
            expirationDate = null;
        }

        applySpecialFutureExpirationRule();
        addStatusInput();
        fireChangeEvent();
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        applySpecialFutureExpirationRule();
        addStatusInput();
        fireChangeEvent();
    }
}
