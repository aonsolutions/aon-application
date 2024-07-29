import { AonElement } from "../../components/AonElement.js";
import { AonApplication } from "../../components/aon-application.js";
import { OfficeEnums } from "./OfficeEnums.js";
import { EVENT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";
import {
  deleteActivityType,
  getActivitiesType,
  getProjectTypes,
  saveProject,
} from "../../services/projectService.js";
import { DocumentalSidenav } from "../documental/DocumentalEnums.js";
import { AonCustomer } from "../registry/customer/aon-customer.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { AonTaskHolder } from "../registry/taskholder/aon-taskholder.js";
import { AonTaskHolderList } from "../registry/taskholder/aon-taskholder-list.js";
import { OfficeUtils } from "./OfficeUtils.js";
import { getTastHolders } from "../../services/taskHolderService.js";
import { getWorkgroups } from "../../services/workgroupService.js";
import { ProjectUtils } from "../project/ProjectUtils.js";
import { saveRelationShip } from "../../services/registryService.js";
import { BOOKING_PANEL, LINK_DOMAINS } from "./ConsoleOptions.js";
import { AonLinkDomains } from "../domains/aon-link-domains.js";

import * as GWT from "../../gwt/gwt.js";
import { AonTarget } from "../registry/target/aon-target.js";
import { AonWorkgroup } from "../configuration/groups/aon-workgroup.js";

export class AonOfficePanel extends AonElement {
  projectTypes;
  workgroups;
  taskHolders;
  activitiesType;

  _customerSelected;
  _customerSelectedAll;

  _filterCustomers;
  _filterTaskHolders;

  setCustomerSelected(customerSelected) {
    this._customerSelected = customerSelected;
  }

  getCustomerSelected() {
    return this._customerSelected.filter(
      (
        value,
        index // remove repeated customersSelected
      ) => this._customerSelected.findIndex((m) => m.id === value.id) === index
    );
  }

  setCustomerSelectedAll(customerSelectedAll) {
    this._customerSelectedAll = customerSelectedAll;
  }

  getCustomerSelectedAll() {
    return this._customerSelectedAll;
  }

  addFilterCustomers(filter) {
    this._filterCustomers = { ...this._filterCustomers, ...filter };
  }

  setFilterCustomers(filter) {
    this._filterCustomers = filter;
  }

  getFilterCustomers() {
    return this._filterCustomers;
  }

  addFilterTaskHolders(filter) {
    this._filterTaskHolders = { ...this._filterTaskHolders, ...filter };
  }

  setFilterTaskHolders(filter) {
    this._filterTaskHolders = filter;
  }

  getFilterTaskHolders() {
    return this._filterTaskHolders;
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || OfficeEnums.OfficeViews.AON_OFFICE_PANEL;
    this.projectTypes = [];
    this.workgroups = [];
    this.taskHolders = [];
    this.setCustomerSelected([]);

    if(!this.getFilterCustomers()) {
        this.setFilterCustomers({
          page: 1,
          perPage: 50,
          status: ["ACTIVE", "BLOCKED"],
          target: false
        });
    }

    if(!this.getFilterTaskHolders()) {
      this.setFilterTaskHolders({
        page: 1,
        perPage: 50
      });
  }
  }

  build() {
    this.createApplication(this.id, MSG.OFFICE, new AonApplication());
    this.buildSidenav();

    this.showView(OfficeEnums.OfficeViews.AON_CUSTOMER_LIST, undefined, {
      ...this.getFilterCustomers(),
      page: 1,
    });
  }

  buildSidenav() {
    const application = this.getApplication();

    const { OfficeViews, OfficeOptions } = OfficeEnums;

    let options = [];

    let customer = OfficeOptions.AON_CUSTOMER;
    customer.fn = () =>
      this.showView(OfficeViews.AON_CUSTOMER_LIST, undefined, {
        ...this.getFilterCustomers(),
        page: 1,
      });
    options.push(customer);

    let taskHolder = OfficeOptions.AON_TASK_HOLDER;
    taskHolder.fn = () => this.showView(OfficeViews.AON_TASK_HOLDER_LIST, undefined, {
      ...this.getFilterTaskHolders(),
      page: 1,
    });
    options.push(taskHolder);

    let workgroups = OfficeOptions.AON_WORKGROUP_LIST
    workgroups.fn = () => this.showView(OfficeViews.AON_WORKGROUP_LIST);
    options.push(workgroups);

    // Hide for 03/07 OPENGES meet
    // if(this.isSig()){
    //     let consoleOptions = [];
    //     let linkDomain = LINK_DOMAINS;
    //     linkDomain.fn = () => this.showView(LINK_DOMAINS.id);
    //     consoleOptions.push(linkDomain);

    //     let bookingPanel = BOOKING_PANEL;
    //     bookingPanel.fn = () => this.showView(BOOKING_PANEL.id);
    //     consoleOptions.push(bookingPanel);

    //     application.addSidenavOptions(MSG.CONSOLE, consoleOptions);
    // }

    application.addSidenavOptions(MSG.OFFICE, options);

    let types = {
      id: "Types",
      name: "Tipos de expediente",
      options: [],
    };

    application.addSidenavOptions3(types, ({ target }) =>
      this.getApplication().buildOptionsMenu(target, [
        {
          name: "Añadir expediente",
          icon: MATERIAL_ICONS.OPEN_IN_NEW,
          fn: () => ProjectUtils.buildDialogProjectType(this),
        },
        {
          name: "Añadir actividad",
          icon: MATERIAL_ICONS.OPEN_IN_NEW,
          fn: () => ProjectUtils.buildDialogActivityType(this),
        },
      ])
    );

    this.loadProjectType();
  }

  async loadProjectType() {
    const [types, activitiesType] = await Promise.all([
      getProjectTypes({}),
      getActivitiesType({}),
    ]);

    this.projectTypes = (types || []).map((r) => ({
      ...r,
      name: r.description,
      value: r.id,
    }));
    this.activitiesType = (activitiesType || []).map((r) => ({
      ...r,
      name: r.description,
      value: r.id,
    }));

    this.loadActivitiesType();

    this.clearElementById(
      this.getApplication().SIDENAV + DocumentalSidenav.TYPES.id + "List"
    );

    this.projectTypes.forEach((item) => {
      let option = {
        name: item.description,
        icon: MATERIAL_ICONS.LABEL,
        actions: [
          {
            id: MATERIAL_ICONS.MORE_VERT,
            icon: MATERIAL_ICONS.MORE_VERT,
            action: ({ target }) =>
              this.getApplication().buildOptionsMenu(target, [
                {
                  name: "Añadir actividad",
                  icon: MATERIAL_ICONS.OPEN_IN_NEW,
                  fn: () =>
                    ProjectUtils.buildDialogActivityType(this, null, item.id),
                },
                {
                  name: MSG.EDIT,
                  icon: MATERIAL_ICONS.EDIT,
                  fn: () => ProjectUtils.buildDialogProjectType(this, item),
                },
                {
                  name: MSG.DELETE,
                  icon: MATERIAL_ICONS.DELETE,
                  fn: () => ProjectUtils.projectTypeDelete(this, item),
                },
              ]),
          },
        ],
      };

      const opt = this.activitiesType.filter((a) => a.projectType == item.id);
      if (opt.length) {
        option.options = opt.map((activity) => ({
          name: activity.description,
          icon: MATERIAL_ICONS.HDR_AUTO,
          actions: [
            {
              id: MATERIAL_ICONS.MORE_VERT + "2",
              icon: MATERIAL_ICONS.MORE_VERT,
              action: ({ target }) =>
                this.getApplication().buildOptionsMenu(target, [
                  {
                    name: MSG.EDIT,
                    icon: MATERIAL_ICONS.EDIT,
                    fn: () =>
                      ProjectUtils.buildDialogActivityType(
                        this,
                        activity,
                        activity.projectType
                      ),
                  },
                  {
                    name: MSG.DELETE,
                    icon: MATERIAL_ICONS.DELETE,
                    fn: () => {
                      this.getApplication().confirmDialog(
                        MSG.DELETE,
                        MSG.DELETE_CONFIRM,
                        async () => {
                          this.getApplication().startLoading();
                          try {
                            await deleteActivityType(activity);
                            this.showToast({ message: MSG.DELETED_DATA });
                            this.loadProjectType();
                          } catch (error) {
                            this.showToast(error);
                          }
                          this.getApplication().stopLoading();
                        }
                      );
                    },
                  },
                ]),
            },
          ],
        }));
      }

      this.getApplication().addSidenavOptionsListValue(
        DocumentalSidenav.TYPES,
        option
      );
    });
  }

  loadActivitiesType() {
    let sidenav = this.getElement(
      this.getApplication().SIDENAV + "ActivitiesAll"
    );
    if (sidenav) sidenav.remove();

    const opt = this.activitiesType.filter((a) => !a.projectType);
    if (opt.length) {
      let options = opt.map((activity) => ({
        name: activity.description,
        icon: MATERIAL_ICONS.SHARE,
        actions: [
          {
            id: MATERIAL_ICONS.MORE_VERT + "2",
            icon: MATERIAL_ICONS.MORE_VERT,
            action: ({ target }) =>
              this.getApplication().buildOptionsMenu(target, [
                {
                  name: MSG.EDIT,
                  icon: MATERIAL_ICONS.EDIT,
                  fn: () =>
                    ProjectUtils.buildDialogActivityType(
                      this,
                      activity,
                      activity.projectType
                    ),
                },
                {
                  name: MSG.DELETE,
                  icon: MATERIAL_ICONS.DELETE,
                  fn: () => {
                    this.getApplication().confirmDialog(
                      MSG.DELETE,
                      MSG.DELETE_CONFIRM,
                      async () => {
                        this.getApplication().startLoading();
                        try {
                          await deleteActivityType(activity);
                          this.showToast({ message: MSG.DELETED_DATA });
                          this.loadProjectType();
                        } catch (error) {
                          this.showToast(error);
                        }
                        this.getApplication().stopLoading();
                      }
                    );
                  },
                },
              ]),
          },
        ],
      }));

      this.getApplication().addSidenavOptions3(
        { id: "ActivitiesAll", name: "Otras actividades", options },
        () => {
          ProjectUtils.buildDialogActivityType(this);
        }
      );
    }
  }

  addCustomerListSelectable(view) {
    const application = this.getApplication();
    view.selectable = true;
    view.addEventListener(EVENT.SELECT, ({ detail }) => {
      let {
        table: { selected, selectedAll },
      } = detail;

      this.setCustomerSelected(selected);
      this.setCustomerSelectedAll(selectedAll);

      if (selected.length > 0) {
        application.addToolbarOption2(
          OfficeEnums.OfficeSidenav.MORE_VERT,
          ({ target }) => {
            application.buildOptionsMenu(target, [
              {
                name: "Asignar expediente",
                value: "assignedExpediente",
                icon: MATERIAL_ICONS.OPEN_IN_NEW,
                fn: () => {
                  OfficeUtils.buildDialogExpediente(this);
                },
              },
              {
                name: "Asignar productos",
                value: "assignedProduct",
                icon: MATERIAL_ICONS.OPEN_IN_NEW,
                fn: () => {
                  OfficeUtils.buildDialogProducts(this);
                },
              },
              {
                name: "Actualizar productos",
                value: "assignedProduct",
                icon: MATERIAL_ICONS.AUTORENEW,
                fn: () => {
                  OfficeUtils.buildDialogProductsUpdate(this);
                },
              },
              {
                name: "Vincular empresa",
                value: "LINK",
                icon: MATERIAL_ICONS.LINK,
                fn: () => {
                  this.onSaveRelationByCustomers(true);
                },
              },
              {
                name: "Desvincular Empresa",
                value: "UNLINK",
                icon: MATERIAL_ICONS.LINK_OFF,
                fn: () => {
                  this.onSaveRelationByCustomers(false);
                },
              },
            ]);
          }
        );
      } else {
        this.removeActionFolder();
      }
    });
  }

  removeActionFolder() {
    this.setCustomerSelected([]);
    this.setCustomerSelectedAll(false);
    this.getApplication().removeToolbarOption(
      OfficeEnums.OfficeSidenav.MORE_VERT
    );
  }

  async onSaveExpedientes(project) {
    let selected = this.getCustomerSelected();
    let projects = selected.map((registry) =>
      project.clone().setRegistry(registry)
    );

    if (projects.length) {
      await saveProject({ projects });

      this.showMessage();
    }
  }

  onSaveRelationByCustomers(add) {
    const aonView = this.getElement(OfficeEnums.OfficeViews.AON_CUSTOMER_LIST);

    if (aonView) {
      this.getApplication().startLoading();

      saveRelationShip({ add, customers: this.getCustomerSelected() })
        .then((resp) => {
          if (resp.length) {
            OfficeUtils.builDialogRelationship(this, resp);
          } else {
            this.showMessage();
          }

          const table = aonView.TABLE;
          if (table) {
            table.clearSelected();
            aonView.setFilter({ ...this.getFilterCustomers(), page: 1 });
            this.setCustomerSelected([]);
          }
        })
        .catch((err) => this.showError(err))
        .finally(() => {
          this.getApplication().stopLoading();
        });
    }
  }

  openDialogRelationship(data) {
    //DELETE
    OfficeUtils.builDialogRelationship(this, data);
  }

  async getProjectTypes() {
    return this.projectTypes;
  }

  async getWorkgroups() {
    if (this.workgroups.length == 0) {
      let wgs = await getWorkgroups().catch(() => []);
      this.workgroups = wgs.map((r) => ({
        ...r,
        name: r.description,
        value: r.id,
      }));
    }
    return this.workgroups;
  }

  async getTaskHolders() {
    if (this.taskHolders.length == 0) {
      let ths = await getTastHolders().catch(() => []);
      this.taskHolders = ths.map((r) => ({ ...r, value: r.id }));
    }
    return this.taskHolders;
  }

  showView(view, data = undefined, filter = undefined) {
    const officeViews = OfficeEnums.OfficeViews;
    const application = this.getApplication();
    this.removeActionFolder();
    
    return new Promise(async (resolve) => {
      let aonView = undefined;
      switch (view) {
        case BOOKING_PANEL.id:
          GWT.load(GWT.BOOKING_PANEL, this.getApplication().CONTENT);
          break;
        case LINK_DOMAINS.id:
          aonView = new AonLinkDomains();
          break;
        case officeViews.AON_OFFICE_PANEL:
          aonView = new AonOfficePanel();
          break;
        case officeViews.AON_CUSTOMER:
          let searchPanel = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearch");
          searchPanel.style.display = "none";

          if (this.getFilterCustomers().type == "false") {
            aonView = new AonTarget();
            aonView.setCustomer();
            aonView.back = () => {
              searchPanel.style.display = "block";
              this.showView(officeViews.AON_CUSTOMER_LIST, undefined, {
                ...this.getFilterCustomers(),
                page: 1,
              }); // overwrite function
            };
          } else {
            aonView = new AonCustomer();
            aonView.setCustomer();
            aonView.back = () => {
              searchPanel.style.display = "block";
              this.showView(officeViews.AON_CUSTOMER_LIST, undefined, {
                ...this.getFilterCustomers(),
                page: 1,
              }); // overwrite function
            };
          }
          break;
        case officeViews.AON_CUSTOMER_LIST:
          aonView = new AonCustomerList(this);

          this.addCustomerListSelectable(aonView);

          aonView.buildRegistry = (registry) => {
            // overwrite function
            application.startLoader();
            aonView
              .getCustomerCustom(registry)
              .then((customer) =>
                this.showView(officeViews.AON_CUSTOMER, { customer })
              )
              .catch((err) => this.showError(err))
              .finally(() => application.stopLoader());
          };
          break;
        case officeViews.AON_TASK_HOLDER:
          aonView = new AonTaskHolder();
          break;
        case officeViews.AON_TASK_HOLDER_LIST:
          aonView = new AonTaskHolderList(this);
          break;
        case officeViews.AON_WORKGROUP_LIST:
          aonView = new AonWorkgroup();
          break;
      }
      if (aonView) {
        aonView.id = view;

        if (filter) {
          aonView.filter = filter;
          aonView.setFilter(filter);
        }

        if (data) {
          if (data.customer) {
            aonView.setCustomer(data.customer);
          } else {
            aonView.data = data;
          }
        }

        application.setContent(aonView);

        if (
          [officeViews.AON_CUSTOMER_LIST].includes(view) &&
          aonView.buildToolbar
        ) {
          aonView.buildToolbar();
        }
      }

      resolve(aonView);
    });
  }
}
if (!window.customElements.get("aon-office-panel")) {
  window.customElements.define("aon-office-panel", AonOfficePanel);
}
