package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AnnualCalendarWidget;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.api.model.calendar.HolidayDetail;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class WorkplaceCalendar extends AonCustomDockLayout {

    private static final Logger LOGGER = Logger.getLogger(WorkplaceCalendar.class.getName());
    static { LOGGER.addHandler(new ConsoleLogHandler()); }

    private HTMLPanel container;
    private HTMLPanel messagePanel = new HTMLPanel("");

    private SimpleLayoutPanel centerPanel;
    private ScrollPanel westPanel = new ScrollPanel();
    FlowPanel form = new FlowPanel();

    private AonCustomTextBox txtDescription = new AonCustomTextBox("Descripci\u00f3n");
    private AonCustomTextBox txtComment = new AonCustomTextBox("Comentarios");
    private AonCustomNumberBox txtAnnualHours = new AonCustomNumberBox("Horas anuales");
    private AonCustomListBox lbHoliday = new AonCustomListBox("Festivo asociado");

    private AonToolbarButton save = new AonToolbarButton("Guardar", AON.CSS.aonIconSave());
    private AonToolbarButton createFestive = new AonToolbarButton("Nuevo Festivo", AON.CSS.aonIconEditCalendar());
    private AonToolbarButton showHide = new AonToolbarButton("", AON.CSS.aonIconVisibilityOff());

    private Integer domain;
    private Workplace workplace;
    private Calendar calendar;
    private List<Holiday> holidaysList;

    private Integer currentYear = Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date()));
    private boolean westShow = true;

    private final DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();

    // Loader overlay
    private HTMLPanel loaderOverlay = new HTMLPanel(
        "<div class='loader'></div><div class='loader-text'>Cargando calendario</div>"
    );

    public WorkplaceCalendar(Workplace workplace) {
        super(workplace.getDescription());
        this.workplace = workplace;

        hideToolbarFilterMessages();
        hideSearchWidget();

        loaderOverlay.setStyleName("loader-overlay");
        add(loaderOverlay);

        container = new HTMLPanel("");
        container.addStyleName(AON.CSS.aonFlexColumn());
        container.add(messagePanel);

        centerPanel = new SimpleLayoutPanel();
        centerPanel.setHeight("100%");
        centerPanel.getElement().getStyle().setProperty("position", "relative");

        container.add(centerPanel);

        westPanel.addStyleName(AON.CSS.aonFlexColumn());
        addWest(westPanel, 300);

        add(container);

        loadData();
    }

    @Override
    protected void onClearFilter() {}

    private void addButtonsToolbar() {
        showHide.addClickHandler(e -> {
            if (!westShow) {
                setWidgetSize(westPanel, 300);
                animate(300);
                showHide.removeStyleName(AON.CSS.aonIconVisibility());
                showHide.addStyleName(AON.CSS.aonIconVisibilityOff());
            } else {
                setWidgetSize(westPanel, 0);
                animate(300);
                showHide.removeStyleName(AON.CSS.aonIconVisibilityOff());
                showHide.addStyleName(AON.CSS.aonIconVisibility());
            }
            westShow = !westShow;
        });

        save.setEnabled(false);
        createFestive.setEnabled(false);

        addToolbarButton(save);
        addToolbarButton(createFestive);
        addToolbarButton(showHide);
    }

    private void loadData() {
        getDomain(d -> {
            getHolidays(h -> {
                getCalendar(c -> {
                    calendar = c.get(0);

                    addButtonsToolbar();
                    setToolbarTitle(calendar.getDescription());

                    buildForm();
                    buildHolidayCollapsibles();

                    westPanel.setWidget(form);

                    buildAnnualCalendar();

                    loaderOverlay.setVisible(false);
                });
            });
        });
    }

    private void getDomain(Consumer<Integer> success) {
        service.getDomain(new AsyncCallback<Integer>() {
            @Override public void onSuccess(Integer result) {
                domain = result;
                success.accept(result);
            }
            @Override public void onFailure(Throwable caught) {
                AonMessagePanel.showError(messagePanel, "Error obteniendo dominio: " + caught.getMessage());
            }
        });
    }

    private void getCalendar(Consumer<List<Calendar>> success) {
        service.getCalendar(workplace.getId(), new AsyncCallback<List<Calendar>>() {
            @Override public void onSuccess(List<Calendar> result) {
                success.accept(result);
            }
            @Override public void onFailure(Throwable caught) {
                AonMessagePanel.showError(messagePanel, "Error obteniendo calendario: " + caught.getMessage());
            }
        });
    }

    private void getHolidays(Consumer<List<Holiday>> success) {
        service.getHolidays(new AsyncCallback<List<Holiday>>() {
            @Override public void onSuccess(List<Holiday> result) {
                holidaysList = result;
                success.accept(result);
            }
            @Override public void onFailure(Throwable caught) {
                AonMessagePanel.showError(messagePanel, "Error obteniendo festivos: " + caught.getMessage());
            }
        });
    }

    private void buildForm() {
        westPanel.clear();
        westPanel.getElement().getStyle().setProperty("margin-left", "1rem");

        form.clear();

        txtAnnualHours.hideNearBy();

        txtDescription.setValue(calendar.getDescription());
        txtComment.setValue(calendar.getComment());
        txtAnnualHours.setValue(calendar.getAnualHours());

        lbHoliday.clearItems();
        for (Holiday h : holidaysList) {
            lbHoliday.addItem(h.getDescription(), h.getId().toString());
        }
        lbHoliday.setValue(calendar.getHoliday().getId().toString());

        form.add(txtDescription);
        form.add(txtComment);
        form.add(txtAnnualHours);
        form.add(lbHoliday);

        Label festiveLabel = new Label("Festivos:");
        festiveLabel.getElement().getStyle().setProperty("margin", "1rem 0 .5rem 0");
        form.add(festiveLabel);
    }

    private void buildHolidayCollapsibles() {
        Holiday selected = calendar.getHoliday();
        List<Holiday> chain = buildHolidayChain(selected, holidaysList);

        Date startYear = DateUtils.getFirstDayOfYear(currentYear - 1900);
        Date endYear = DateUtils.getLastDayOfYear(currentYear - 1900);

        for (Holiday h : chain) {

            DisclosurePanel dp = new DisclosurePanel();
            dp.setWidth("100%");
            dp.setOpen(false);

            FlowPanel header = new FlowPanel();
            header.addStyleName("holiday-header");

            AonToolbarSmallButton icon = new AonToolbarSmallButton("", AON.CSS.aonIconRight());
            icon.addStyleName("holiday-chevron");

            int count = 0;
            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (!d.getDate().before(startYear) && !d.getDate().after(endYear)) {
                        count++;
                    }
                }
            }

            Label title = new Label(h.getDescription() + " (" + count + ")");
            title.addStyleName("holiday-title");

            header.add(icon);
            header.add(title);

            dp.setHeader(header);

            dp.addOpenHandler(e -> icon.addStyleName("open"));
            dp.addCloseHandler(e -> icon.removeStyleName("open"));

            FlowPanel detailsPanel = new FlowPanel();

            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (!d.getDate().before(startYear) && !d.getDate().after(endYear)) {
                        detailsPanel.add(createHolidayDetailRow(d));
                    }
                }
            }

            if (detailsPanel.getWidgetCount() == 0) {
                Label empty = new Label("No hay festivos para el a\u00f1o en curso");
                empty.addStyleName("holiday-empty");
                detailsPanel.add(empty);
            }

            dp.setContent(detailsPanel);

            if (h.getId().equals(calendar.getHoliday().getId())) {
                dp.setOpen(true);
                icon.addStyleName("open");
            }

            form.add(dp);
        }
    }

    private List<Holiday> buildHolidayChain(Holiday h, List<Holiday> all) {
        List<Holiday> chain = new ArrayList<>();
        Holiday current = h;

        while (current != null) {
            chain.add(0, current);
            current = findParent(current, all);
        }
        return chain;
    }

    private Holiday findParent(Holiday h, List<Holiday> all) {
        if (h.getHolidayParent() == null) return null;
        for (Holiday x : all) {
            if (x.getId().equals(h.getHolidayParent())) return x;
        }
        return null;
    }

    private FlowPanel createHolidayDetailRow(HolidayDetail d) {
        FlowPanel row = new FlowPanel();
        row.addStyleName("holiday-detail-row");

        boolean editable = d.getDomain().equals(domain);

        row.addStyleName(editable ? "holiday-own" : "holiday-inherited");

        Label dateLbl = new Label(DateTimeFormat.getFormat("dd/MM/yyyy").format(d.getDate()));
        dateLbl.addStyleName("holiday-detail-date");

        Label descLbl = new Label(d.getDescription());
        descLbl.addStyleName("holiday-detail-desc");

        row.add(dateLbl);
        row.add(descLbl);

        if (editable) {
            AonTableButton edit = new AonTableButton("Editar", AON.CSS.aonIconEdit());
            AonTableButton delete = new AonTableButton("Eliminar", AON.CSS.aonIconDelete());

            edit.addClickHandler(e -> editHolidayDetail(d));
            delete.addClickHandler(e -> deleteHolidayDetail(d));

            row.add(edit);
            row.add(delete);
        }

        return row;
    }

    private void editHolidayDetail(HolidayDetail d) {
        // Aquí puedes abrir tu diálogo de edici\u00f3n
        AonMessagePanel.showInfo(messagePanel, "Editar festivo no implementado aún");
    }

    private void deleteHolidayDetail(HolidayDetail d) {
        AonDialog dialog = new AonDialog("Festivo", new Label("¿Eliminar este festivo?"));
        dialog.confirm(new AonAcceptDialogCallback() {

            @Override
            public void onCancel() {}

            @Override
            public void onAccept() {
                service.deleteHolidayDetail(d.getId(), new AsyncCallback<Void>() {
                    @Override public void onSuccess(Void result) {
                        AonMessagePanel.showInfo(messagePanel, "Festivo eliminado");
                        loadData();
                    }
                    @Override public void onFailure(Throwable caught) {
                        AonMessagePanel.showError(messagePanel, "Error eliminando festivo: " + caught.getMessage());
                    }
                });
            }
        });
    }

    private void buildAnnualCalendar() {
        centerPanel.clear();

        AnnualCalendarWidget annual = new AnnualCalendarWidget(
            calendar,
            holidaysList,
            currentYear,
            newYear -> {
                currentYear = newYear;
                buildAnnualCalendar();
            }
        );

        ScrollPanel scroll = new ScrollPanel(annual);
        scroll.setHeight("100%");

        centerPanel.setWidget(scroll);
    }
}
