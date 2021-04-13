import { AonElement } from "../../../components/AonElement.js";
import { handleError, setDate } from "../../../services/utils.js";
import { getMovements, getEmployee } from "../../../services/service.js";
import { PAYROLL_VIEWS } from "../PayrollEnums.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";


export class AonMovementsList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter"];
  }

  get filter() {
    return this.getAttribute("filter");
  }

  set filter(filter) {
    this.setAttribute("filter", filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) this.build();
  }

  constructor() {
    super();
    this.id = this.id || PAYROLL_VIEWS.AON_MOVEMENTS_LIST;
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
  }

  connectedCallback() {
    this.build();
  }

  disconnectedCallback() {
    if (this.applicationEl) this.applicationEl.removeFloatOption();
  }

  paintView() {
    if (this.isMobile())
      this.innerHTML = ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    else this.innerHTML = ` <aon-table id='${this.TABLE_ID}' />`;
  }

  async build() {
    this.paintView();
    this.applicationEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonMovementTable = this.getElement(this.TABLE_ID);
    if (aonMovementTable) {
      aonMovementTable.addColumn("Apellidos y nombre", "string", "name", "45%");
      aonMovementTable.addColumn("DNI/NIE", "string", "dni", "15%");
      aonMovementTable.addColumn("Movimiento", "string", "status", "25%");
      aonMovementTable.addColumn("Fecha", "date", "fecha", "15%");
      try {
        const resp = await this.getData();
        aonMovementTable.removeRows();
        resp.map((res) => {
          aonMovementTable.addRow(res, (el) => this.aonMovement(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonMovementTable = this.getElement(this.TABLE_ID);
    if (aonMovementTable) {
      aonMovementTable.createAonDialog();
      try {
        const resp = await this.getData();
        aonMovementTable.removeAllLi();
        resp.map((res, idx) => {
          // let icon = res.situation === "AL" ? 'trending_up' : 'trending_down';
          aonMovementTable.addLi(
            {
              aonIcon: "aon_seg_social",
              title: `${res.name}`,
              subtitle: `${res.status} ${res.fecha}`,
              option: this.applicationParentEl.getOptions(res),
            },
            idx,
            (el) => this.aonMovement(el, res)
          );
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async aonMovement({ target: el }, { regime, ctaCti, nss, prev, situation }) {
    this.applicationEl.startLoader();
    const aonAltaDirecta = await this.applicationParentEl.showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA);
    console.log(aonAltaDirecta, aonAltaDirecta.id);
    try {
      let resp = await getEmployee({ regime, ctaCti, nss });
      if (resp) {
        resp = { ...resp, prev, situation };
        if (aonAltaDirecta) {
          //DISABLED FORMS
          aonAltaDirecta.disabledForm(`${aonAltaDirecta.id}EmpresaCard`);
          aonAltaDirecta.disabledForm(`${aonAltaDirecta.id}TrabajadorCard`, "aon-switch");
          //parseData
          aonAltaDirecta.data = resp;
        }
      }
    } catch (error) {}
    this.applicationEl.stopLoader();
  }

  async getData() {
    let data = [];
    try {
      const movements = this.applicationParentEl._movements;
      const resp = movements || await getMovements(this.getFilter());
      data = resp
        .sort((a, b) => new Date(b.fra) - new Date(a.fra))
        .map((res) => {
        const { ipf, fra, situation } = res;
        const fecha = setDate(fra);
        const date_now = new Date();
        const prev = new Date(fra).getTime() > date_now.getTime();
        const dni = ipf.toString().substring(1);
        let color = "#000";
        let tipo_mov = situation === "AL" ? "Alta" : "Baja";
        if (prev) {
          color = "#488601";
          tipo_mov = `${tipo_mov} previa`;
        } else if (this.applicationParentEl.anularCondition(situation, fra)) {
          color = "#CB8D00";
          tipo_mov = `${tipo_mov} Consolidada`;
        } else {
          tipo_mov = `${tipo_mov} Consolidada`;
        }
        const status = `<span style="font-weight: 700;color: ${color};">${tipo_mov}</span>`;
        return {
          ...res,
          dni,
          fecha,
          status,
          prev,
        };
      });
      this.applicationParentEl._movements = data;
    } catch (error) {
      this.applicationEl.getToast().start(handleError(error));
    }
    return data;
  }

  getFilter = () => JSON.parse(this.getAttribute("filter"));

  setFilter = (filter) => this.setAttribute("filter", JSON.stringify(filter));
}
window.customElements.define("aon-movements-list", AonMovementsList);
