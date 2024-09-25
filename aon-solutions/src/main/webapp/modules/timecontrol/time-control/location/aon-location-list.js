import { AonElement } from "../../../../components/AonElement.js";
import { getLocation } from "../../../../services/service.js";
import { SigninSidenav, SIGNIN_VIEWS } from "../../signinEnums.js";
import { CONSTANT, MSG } from "../../../../environments/environments.js";
import { AonMobileList } from "../../../../components/aon-mobile-list.js";
import { AonTable } from "../../../../components/aon-table.js";
import { TIMECONTROL } from "../../../../services/app.js";


export class AonLocationList extends AonElement {
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
    this.id = this.id || SIGNIN_VIEWS.AON_LOCATION_LIST;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.aonSigninToolbar = this.getElement(
      `${this.applicationEl.id}Toolbar`
    );
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.build();
  }

  disconnectedCallback() {
    if (this.applicationEl) this.applicationEl.removeFloatOption();
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

    if (this.isMobile()) 
      this.applicationEl.addFloatOption(SigninSidenav.ADD,() => this.add());
    else 
      this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.add());
  }

  async getTable() {
    this.aonSigninToolbar.setAttribute("option", "Ubicaciones");
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
      aonTable.addColumn(MSG.RADIO, "number", "radio", "30%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow({ ...res }, (el) => this.add(el, res));
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
            icon: 'location_on',
            title: `${res.description}`,
            subtitle: `${MSG.RADIO} (${res.radio})`,
          };
          aonTable.addLi(options, idx, (el) => this.add(el, res));
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
      let resp = await getLocation(this.data);
      resp.map(({ id, description, coordinates, radio }) => {
        data.push({
          id,
          description,
          coordinates,
          radio,
          latitude: coordinates.latitude,
          longitude: coordinates.longitude,
        });
      });
    } catch (e) {
      console.log(e);
    }
    this.applicationEl.stopLoader();
    return data;
  }

  add(el, data) {
    let newData = data;
    if (!data) newData = {add:true};
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_LOCATION_ADD, newData);
  }
}
window.customElements.define("aon-location-list", AonLocationList);
