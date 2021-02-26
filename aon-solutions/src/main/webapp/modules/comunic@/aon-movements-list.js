import { AonElement } from "../../components/AonElement.js";
import { setDate } from "../../services/utils.js";
import { getMovements, getEmployee } from "../../services/service.js";
import "../../components/aon-table.js";
import "../../components/aon-mobile-list.js";

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
    this.TABLE_ID = "aonMovementTable";
    this.aonComunicaEl = this.getElement("aonComunica");
    this.aonComunicaParentEl = this.aonComunicaEl.getParent();
    this.toastEl = this.getElement(this.aonComunicaEl.TOAST);
  }

  connectedCallback() {
    this.build();
  }

  disconnectedCallback() {
    if (this.aonComunicaEl) this.aonComunicaEl.removeFloatOption();
  }

  paintView() {
    if (this.isMobile())
      this.innerHTML = ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    else this.innerHTML = ` <aon-table id='${this.TABLE_ID}' />`;
  }

  async build() {
    this.paintView();
    this.aonComunicaEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonComunicaEl.stopLoader();
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
              option: this.aonComunicaParentEl.getOptions(res),
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
    let id = "aonAltaDirecta";
    this.aonComunicaEl.startLoader();
    this.aonComunicaEl.setContentHTML(
      `<aon-alta-directa id="${id}"></aon-alta-directa>`
    );
    try {
      let resp = await getEmployee({ regime, ctaCti, nss });
      if (resp) {
        resp = { ...resp, prev, situation };
        const aonAltaDirecta = this.getElement(id);
        if (aonAltaDirecta) {
          //DISABLED FORMS
          aonAltaDirecta.disabledForm(`${id}EmpresaCard`);
          aonAltaDirecta.disabledForm(`${id}TrabajadorCard`, "aon-switch");
          //parseData
          aonAltaDirecta.data = resp;
        }
      }
    } catch (error) {}
    this.aonComunicaEl.stopLoader();
  }

  async getData() {
    let data = [];
    try {
      const movements = this.aonComunicaParentEl._movements;
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
        } else if (this.aonComunicaParentEl.anularCondition(situation, fra)) {
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
      this.aonComunicaParentEl._movements = data;
    } catch (e) {
      console.log(e);
      if ("InvalidCertificateException" === e || "CertificateNotFoundException" === e){
        this.toastEl.start({ message: e, type: "error" });
      }
    }
    return data;
  }

  getFilter = () => JSON.parse(this.getAttribute("filter"));

  setFilter = (filter) => this.setAttribute("filter", JSON.stringify(filter));
}
window.customElements.define("aon-movements-list", AonMovementsList);
