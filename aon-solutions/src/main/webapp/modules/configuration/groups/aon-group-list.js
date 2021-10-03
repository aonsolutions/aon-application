import { AonElement } from "../../../components/AonElement.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { AonMobileList } from "../../../components/aon-mobile-list.js";
import { AonTable } from "../../../components/aon-table.js";
import {getWorkgroups, saveWorkgroup} from '../../../services/workgroupService.js';
import * as ACTION from '../../actions.js';
import { AonInput } from "../../../components/aon-input.js";
import { Workgroup } from "../../../models/project/Workgroup.js";

export class AonGroupList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return JSON.parse(this.getAttribute(CONSTANT.FILTER));
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILTER === name) this.getTable();
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {
    if (this.applicationEl) this.applicationEl.removeFloatOption();
  }

  initialize() {
    this.id = this.id || "aonGroupList";
    this.applicationEl = this.getApplication();
    this.aonSigninToolbar = this.getElement(`${this.applicationEl.id}Toolbar`);
    this.TABLE_ID = this.id + "Table";
  }

  paintView() {
    let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  build() {
    this.paintView();
    this.getTable();
    this.buildToolbar();
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();

    if (this.isMobile()) {
        this.applicationEl.addFloatOption(ACTION.ADD,() => this.add());
    } else {
      this.applicationEl.addToolbarOption2(ACTION.ADD, () => this.add());
    }
  }

  async getTable() {
    this.aonSigninToolbar.setAttribute("option", MSG.GROUPS);
    this.applicationEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn(MSG.NAME, "string", "description", "70%");
      aonTable.addColumn(MSG.STATUS, "number", "statusText", "30%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow({ ...res }, () => this.dispatchEvent(new CustomEvent(EVENT.SELECT, {detail: res})));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();
        resp.map((res, idx) => {
          let options = {
            title: `${res.description}`,
            subtitle: `${statusText})`,
          };
          aonTable.addLi(options, idx, () => this.add(res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.applicationEl.startLoader();
    let data = [];

    try {
      let resp = await getWorkgroups();
      data = resp.map(res =>{
        return {
          ...res,
          statusText: res.active ? MSG.ACTIVE : MSG.INACTIVE 
        };
      });
    } catch (e) {
      console.log(e);
    }
    this.applicationEl.stopLoader();
    return data;
  }

  add() {
    let input = new AonInput();
    input.id = this.id + 'AddWorkgroup';
    input.title = MSG.NAME;
    input.description = MSG.NAME;
    let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_WORKGROUP);
    d.setContent(input);
		d.addAcceptAction(() => {
      this.save(new Workgroup().setDescription(input.value));
		});
		d.open();
  }

  save(data) {
    saveWorkgroup(data).then(r => {
			this.getApplication().getToast().start({
				type: CONSTANT.SUCCESS,
				message: MSG.SAVED_DATA
			});
      this.getTableDesk();
		}).catch(e => this.showError(e));
  }
}
if(!window.customElements.get(TAG.AON_GROUP_LIST)){
  window.customElements.define(TAG.AON_GROUP_LIST, AonGroupList);
}