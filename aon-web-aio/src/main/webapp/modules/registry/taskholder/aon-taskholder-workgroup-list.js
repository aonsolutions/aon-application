
import * as ACTION from "../../actions.js";
import {
  CSS,
  EVENT,
  MATERIAL_ICONS,
  MSG,
  TAG,
} from "../../../environments/environments.js";
import { AonTable } from "../../../components/aon-table.js";
import {
  deleteTaskHolderWorkGroup,
  getTaskHolderWorkGroups,
  saveTaskHolderWorkGroups,
} from "../../../services/taskHolderService.js";
import { AonDate } from "../../../components/aon-date.js";
import { AonSelect } from "../../../components/aon-select.js";
import { getWorkgroups } from "../../../services/workgroupService.js";
import { AonElement } from "../../../components/AonElement.js";

export class AonTaskholderWorkgroupList extends AonElement {
  more;
  filter;
  TABLE;
  holders;
  projectTypes;
  workgroups;
  taskHolders;

  taskHolder;

  constructor(_taskHolder) {
    super();
    this.taskHolder = _taskHolder;
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.more = true;
    this.holders = [];
    this.projectTypes = [];
    this.workgroups = [];
    this.taskHolders = [];
    this.filter = this.filter || {
      page: 1,
      perPage: 50,
    };
  }

  build() {
    this.TABLE = new AonTable();
    this.TABLE.id = "aonTaskholderWorkgroupListTable";
    this.TABLE.selectedColor = true;

    this.TABLE.style.width = "100%";

    this.buildTaskHolderWorkgroupTable();
    
    this.TABLE.addEventListener("more", () => {
      if (this.more) {
        this.loadMore();
      }
    });

    this.init();
  }

  buildTaskHolderWorkgroupTable() {
    this.appendChild(this.TABLE);
    this.TABLE.addColumn(MSG.NAME, "string", "name", "40%");
    this.TABLE.addColumn("Tipo Usuario", "string", "type", "20%");
    this.TABLE.addColumn("F. Inicio", "date", "startDate", "15%");
    this.TABLE.addColumn("F. Fin", "date", "endDate", "15%");
    this.TABLE.addColumnIcon(
      {
        title: MSG.ADD + " grupo de trabajo",
        name: MATERIAL_ICONS.ADD,
        type: "string",
        width: "5%",
        id: "option",
      },
      () => {
        this.buildDialogTaskholderWorkgroup(this, {
          task_holder: {
            id: this.taskHolder,
          },
          start_date: new Date(),
          task_holder_workgroup_type: "USER",
        });
      }
    );

  }

  loadMore() {
    this.more = false;
    if (this.TABLE && this.filter.page) {
      this.filter.page = this.filter.page + 1;
      this.getData(this.filter).then((projects) => {
        if (projects.length > 0) this.more = true;
        projects.forEach((project) => {
          project.option = this.getOptions(project);
          this.TABLE.addRow(project);
        });
      });
    }
  }

  init() {
    if (this.TABLE) {
      this.getData(this.getFilter()).then((projects) => {
        this.TABLE.removeRows();
        projects.forEach((project) => {
          project.option = this.getOptions(project);
          this.TABLE.addRow(project);
        });
      });
    }
  }

  async getData(filter) {
    try {
      const data = await getTaskHolderWorkGroups(filter);

    	data
        .sort(
          (a, b) =>
            (a.workgroup.description > b.workgroup.description) -
            (a.workgroup.description < b.workgroup.description)
        )
        .map((p) => {
          p.name = p.workgroup.description;
          p.type = p.task_holder_workgroup_type == "USER" ? "USUARIO" : "RESPONSABLE";
          p.startDate = p.start_date;
          p.endDate = p.end_date;

          return p;
        });

		

		return data;
    } catch (error) {
      this.showError(error);
    }
    return [];
  }

  getFilter() {
    return this.filter || {};
  }

  setFilter(filter) {
    this.filter = filter;
    this.init();
  }

  async onSaveProject(p) {
    this.getApplication().startLoading();
    try {
      let project = await saveTaskHolderWorkGroups(p);
      this.showMessage();
      this.init();
    } catch (error) {
      this.showError(error);
    }
    this.getApplication().stopLoading();
  }

  onDeleteProject(project) {
    this.getApplication().confirmDialog(
      MSG.DELETE,
      MSG.DELETE_CONFIRM,
      async () => {
        this.getApplication().startLoading();
        try {
          await deleteTaskHolderWorkGroup({
            task_holder: this.taskHolder,
            workgroup: project.workgroup.id,
          });
          this.showToast({ message: MSG.DELETED_DATA });
          this.init();
        } catch (error) {
          this.showError(error);
        }
        this.getApplication().stopLoading();
      }
    );
  }

  getOptions(project) {
    let option = [
      {
        ...ACTION.EDIT,
        fn: () => this.buildDialogTaskholderWorkgroup(this, project),
      },
      {
        ...ACTION.DELETE,
        fn: () => this.onDeleteProject(project),
      },
    ];

    return option;
  }

  async getWorkgroupsList() {
    if (this.workgroups.length == 0) {
      let wgs = await getWorkgroups().catch((error) => {
        this.showError(error);
        return [];
      });
      this.workgroups = wgs.map((r) => ({
        ...r,
        name: r.description,
        value: r.id,
      }));
    }
    return this.workgroups;
  }

  async buildDialogTaskholderWorkgroup(parent, project) {
    const application = parent.getApplication();
    const dialog = application.getDialog();
    dialog.clear();

    if (parent.isMobile()) {
      dialog.type = "fullscreen";
    } else {
      dialog.width = "40%";
    }

    dialog.autoclose = false;

    const title =
      (project && project.id ? MSG.EDIT : MSG.ASSIGN) + " grupo de trabajo";
    dialog.setTitle(title);

    let div = document.createElement("div");
    div.style.display = "flex";
    div.style.flexDirection = "column";
    dialog.setContent(div);

    await this.buildFormProject(parent, project, div);

    dialog.addSendAction(async () => {
      await this.onSaveProject(project);

      dialog.close();
    }, MSG.SAVE);

    dialog.open();
  }

  async buildFormProject(parent, project, div) {
	let workgroups = await this.getWorkgroupsList();

    const idRandom = Math.floor(Math.random() * 10000000) + 1;

    let workgroupSelect = new AonSelect();
    workgroupSelect.title = MSG.WORKGROUP;
    workgroupSelect.autocomplete = true;
    workgroupSelect.id = "workgroupSelect2" + idRandom;
    div.appendChild(workgroupSelect);

    let taskHolderWorkgroupType = new AonSelect();
    taskHolderWorkgroupType.title = "Tipo Usuario";
    taskHolderWorkgroupType.autocomplete = true;
    taskHolderWorkgroupType.id = "taskHolderWorkgroupType2" + idRandom;
    div.appendChild(taskHolderWorkgroupType);

    let startDate = new AonDate();
    startDate.title = "F. Inicio";
    startDate.id = "startDate2" + idRandom;
    div.appendChild(startDate);

    let endDate = new AonDate();
    endDate.title = "F. Fin";
    endDate.id = "endDate2" + idRandom;
    div.appendChild(endDate);

    workgroupSelect.setOptions(workgroups);
	taskHolderWorkgroupType.setOptions(
		[
			{name: 'USUARIO', value: 'USER'},
			{name: 'RESPONSABLE', value: 'ADMIN'}
		]
	);

	workgroupSelect.addEventListener(EVENT.CHANGE, () => {
       //console.log(workgroupSelect.getDetail());
	   if(!project.workgroup) project.workgroup = {};
	   project.workgroup.id = workgroupSelect.getDetail().id;
	});

	taskHolderWorkgroupType.addEventListener(EVENT.CHANGE, () => {
		//console.log(taskHolderWorkgroupType.getDetail());
		project.task_holder_workgroup_type = taskHolderWorkgroupType.getDetail().value;
	});

	startDate.addEventListener(EVENT.CHANGE, () => {
		//console.log(startDate.getValue());
		project.start_date = startDate.getValue();
	});

	endDate.addEventListener(EVENT.CHANGE, () => {
		//console.log(endDate.getValue());
		project.end_date = endDate.getValue();
	});

	if(project){
		if(project.workgroup) workgroupSelect.setValue(project.workgroup.id);
		taskHolderWorkgroupType.setValue(project.task_holder_workgroup_type);
		startDate.setDate(project.start_date);
		endDate.setDate(project.end_date);
	}
  }
}

if (!window.customElements.get("aon-taskholder-workgroup-list")) {
  window.customElements.define(
    "aon-taskholder-workgroup-list",
    AonTaskholderWorkgroupList
  );
}
