import { CONSTANT, EVENT, MSG } from "../../../environments/environments.js";
import { TaskHolder } from "../../../models/registry/TaskHolder.js";
import { CreateComponent } from "../../../components/CreateComponent.js";
import {
  getTaskHolderNoCache,
  getTastHoldersList,
  getUserList,
  saveTastHolder,
} from "../../../services/service.js";
import { AonRegistryList } from "../aon-registry-list.js";
import { AonTaskHolder } from "./aon-taskholder.js";
import * as ACTION from "../../actions.js";
import { getWorkgroups } from "../../../services/workgroupService.js";

export class AonTaskHolderList extends AonRegistryList {
  constructor(defaultFilter) {
    super();

    if (!this.filter) {
      this.filter = defaultFilter;
    }
  }

  build() {
    super.build();
    this.buildToolbar();
  }

  getRegistries() {
   return getTastHoldersList(this.filter);
  }

  async buildRegistry(registry) {
    let data = {
      id: registry.id,
      workgroups: true,
      additional_info: ["ADDRESSES", "MEDIA"],
    };
    const r = await getTaskHolderNoCache(data);
    let aonTaskHolder = new AonTaskHolder(this.filter);
    aonTaskHolder.id = this.getApplication().id + "TaskHolder";
    aonTaskHolder.setTaskHolder(r);
    this.getApplication().setContent(aonTaskHolder);
    let searchPanel = this.getElement(
      "aonOfficePanelToolbarHeaderToolSectionSearch"
    );
    searchPanel.style.display = "none";
  }

  buildToolbar() {
    this.getApplication().removeToolbarOptions();
    this.getApplication().addToolbarOption2(ACTION.ADD, () => this.add());
    this.buildSearch();
  }

  buildSearch() {
    let timeOut = null;

    let btnSearch = this.getApplication().addSearchOption(true);

    btnSearch.addEventListener(EVENT.SEARCH_NEW, ({ detail }) => {
      clearTimeout(timeOut);
      timeOut = setTimeout(() => {
        this.filter = {
          ...this.filter,
          page: 1,
          search: detail.search,
          active: detail.status,
          workgroup: detail.workgroup,
        };
        this.setFilter(this.filter);
        let filterCount = this.getElement(
          "aonOfficePanelToolbarHeaderToolSectionSearchCountFilter"
        );
        if (filterCount) filterCount.style.display = "none";
      }, 300);
    });

    btnSearch.addEventListener(EVENT.RESET_FILTER, ({ detail }) => {
      clearTimeout(timeOut);
      timeOut = setTimeout(() => {
        this.filter = {
          perPage: 50,
          page: 1,
        };
        this.setFilter(this.filter);
        this.setSearchValues();
      }, 300);
    });

    let searchInput = this.getElement(
      "aonOfficePanelToolbarHeaderToolSectionSearchSearchInput"
    );
    searchInput.placeholder = "Buscar por nombre, documento o alias";
    searchInput.focus();

    btnSearch.buildOptionsFilter([
      {
        type: CONSTANT.SELECT,
        id: "status",
        name: "status",
        title: MSG.STATUS,
        autocomplete: true,
        default: true,
        emptyclear: true,
      },
      {
        type: CONSTANT.SELECT,
        id: "workgroup",
        name: "workgroup",
        title: MSG.WORKGROUP,
        autocomplete: true,
        default: true,
        emptyclear: true,
      },
    ]); //INPUTS
    this.setSearchValues();
  }

  async setSearchValues() {
    let searchInput = this.getElement(
      "aonOfficePanelToolbarHeaderToolSectionSearchSearchInput"
    );
    let searchValue = this.filter.search;
    searchInput.value = searchValue ? searchValue : "";

    let workgroupEl = this.getElement("workgroup");
    let workgroups = await getWorkgroups();
    workgroupEl.setOptions(
      workgroups.map((c) => ({ ...c, name: c.description, value: c.id }))
    );
    const value = this.filter.workgroup;
    workgroupEl.setValue(value);

    const statusEl = this.getElement("status");
    statusEl.setOptions([
      { name: "Activo", value: true },
      { name: "Inactivo", value: false },
    ]);
    const status = this.filter.active;
    let statusInput = statusEl.getInput();
    if (status) {
      statusInput.value = status == "false" ? "Inactivo" : "Activo";
      statusEl.setValue(status);
    } else {
      statusInput.value = "";
      statusEl.setValue("");
    }
  }

  add() {
    const application = this.getApplication();
    const dialog = application.getDialog();
    dialog.clear();
    dialog.setTitle("Registrar operario");

    if (this.isMobile()) {
      dialog.type = "fullscreen";
    } else {
      dialog.width = "300px";
    }

    const div = document.createElement("div");
    dialog.setContent(div);

    const aonSelect = CreateComponent.createAonSelect(
      {
        attributes: {
          name: "user",
          id: "user",
          autocomplete: "off",
          title: MSG.USERS,
        },
      },
      div
    );

    aonSelect.loading(true);

    getUserList({ task_holder_empty: true, filter: "all" })
      .then((users) => {
        const options = users.map((user) => ({
          ...user,
          value: user.id,
          name: user.name + " " + user.surname,
        }));
        aonSelect.setOptions(options);
      })
      .finally(() => {
        aonSelect.loading(false);
      });

    dialog.addSendAction(() => {
      const detail = aonSelect.getDetail();
      if (detail.id) {
        application.startLoading();

        let taskHolder = new TaskHolder();
        taskHolder.setUser(detail.id);
        taskHolder.setDocument(detail.document);
        taskHolder.setName(detail.name);

        saveTastHolder(taskHolder)
          .then((th) => {
            this.showMessage();
            this.buildRegistry(th);
          })
          .catch((err) => this.showError(err))
          .finally(() => {
            dialog.close();
            application.stopLoading();
          });
      }
    }, MSG.SAVE);

    dialog.open();
  }
}

if (!window.customElements.get("aon-taskholder-list")) {
  window.customElements.define("aon-taskholder-list", AonTaskHolderList);
}
