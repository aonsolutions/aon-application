import { AonElement } from "../../../../components/AonElement.js";
import { getLocation } from "../../../../services/service.js";
import { AonLocationAdd } from "./aon-location-add.js";
import "../../../../components/aon-table.js";
import "../../../../components/aon-mobile-list.js";

export class AonLocationList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter"];
  }

  get filter() {
    return JSON.parse(this.getAttribute("filter"));
  }

  set filter(filter) {
    this.setAttribute("filter", JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) this.getTable();
  }

  constructor() {
    super();
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninToolbar = this.getElement(
      `${this.aonSigninEl.id}Toolbar`
    );
    this.id = this.id || "aonLocationList";
    this.TABLE_ID = this.id + "Table";
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.buildToolbar();
  }

  disconnectedCallback() {
    if (this.aonSigninEl) this.aonSigninEl.removeFloatOption();
  }

  paintView() {
    let innerHTML = "";
    if (this.isMobile()) {
      innerHTML = innerHTML + `<aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }
    this.innerHTML = innerHTML;
  }

  build() {
    this.getTable();
  }

  buildToolbar() {
    this.aonSigninEl.removeToolbarOptions();

    if (this.isMobile()) {
      let floatButton = this.getElement(`${this.aonSigninEl.id}FloatSpan`);
      if (!floatButton) {
        this.aonSigninEl.addFloatOption(
          {
            id: "AddLocation",
            name: "addlocation",
            icon: "add",
          },
          () => this.add()
        );
      }
    } else {
      this.aonSigninEl.addToolbarOption("Add", "add", () => this.add());
    }
  }

  async getTable() {
    this.aonSigninToolbar.setAttribute("option", "Ubicación");
    this.aonSigninEl.startLoader();
    if (this.isMobile()) {
      await this.getTableMobile();
    } else {
      await this.getTableDesk();
    }
    this.aonSigninEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Nombre", "string", "description", "70%");
      aonTable.addColumn("Radio", "number", "radio", "30%");
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
            subtitle: `Radio (${res.radio})`,
          };
          aonTable.addLi(options, idx, (el) => this.add(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.aonSigninEl.startLoader();
    let data = [];
    try {
      let resp = await getLocation(this.data);
      resp.map(async ({ id, description, coordinates, radio }) => {
        data.push({
          id,
          description,
          coordinates,
          latitude: coordinates.latitude,
          longitude: coordinates.longitude,
          radio,
        });
      });
    } catch (e) {
      console.log(e);
    }
    this.aonSigninEl.stopLoader();
    return data;
  }

  add(el, data) {
    let aonLocationAdd = new AonLocationAdd();
    aonLocationAdd.id = "aonLocationAdd";
    if (data) aonLocationAdd.data = data;
    else aonLocationAdd.add = true;
    this.aonSigninEl.setContent(aonLocationAdd);
  }
}
window.customElements.define("aon-location-list", AonLocationList);
