import { AonElement } from "../../components/AonElement.js";
import { CSS, TAG } from "../../environments/environments.js";
import { isEmptyObject } from "../../services/utils.js";
import { getAccounting, getPeriods } from "../../services/accountingService.js";
import * as UTILS from "./AccountingUtils.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";

export class AonDashboardChargePayments extends AonElement {
  filter;
  cypData;
  stackedChart;

  set id(id) {
    this.setAttribute("id", id);
  }

  get id() {
    return this.getAttribute("id");
  }

  constructor(period) {
    super();
    this.id = this.id || "aonDashboardChargePayments";
    this.applicationEl = document.querySelector("#cypContent");
    this.filter = {};
    this.filter.period = period;
    this.style.width = "100%";
    this.style.display = "flex";
    this.style.height = "100%";
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {}

  async build() {
    this.draw();
  }

  async draw() {
    this.innerHTML = "";

    let canvasDiv = this.createElement(TAG.DIV);
    canvasDiv.id = "pygCardCanvasDiv";
    canvasDiv.style.width = "100%";
    this.appendChild(canvasDiv);

    let canvas = this.createElement(TAG.CANVAS);
    canvas.id = "pygCardCanvas";
    canvasDiv.appendChild(canvas);

    this.cypData = this.getData();

    this.drawBarLineChart(canvas, this.cypData);
  }

  drawBarLineChart(canvas, cypData){
    let dataChart = {
      labels: [
        "Anteriores", 
        "Periodo", 
        "Acumulado"
      ],
      datasets: [
        {
          label: "Cobros (Pedientes)",
          backgroundColor: "rgba(51, 168, 255, 0.7)",
          data: [cypData.charges.previous.pending, null, null]
        },
        {
          label: "Cobros (Devuelto)",
          backgroundColor: "rgba(10, 150, 255, 0.9)",
          data: [cypData.charges.previous.returns, null, null]
        },
        {
          label: "Pagos (Pedientes)",
          backgroundColor: "rgb(255, 116, 37, 0.7)",
          data: [cypData.payments.previous.pending, null, null]
        },
        {
          label: "Pagos (Devuelto)",
          backgroundColor: "rgb(255, 99, 9, 0.9)",
          data: [cypData.payments.previous.returns, null, null]
        },

        {
          label: "Cobros (Pedientes)",
          backgroundColor: "rgba(51, 168, 255, 0.7)",
          data: [null, cypData.charges.period.pending, null]
        },
        {
          label: "Cobros (Devuelto)",
          backgroundColor: "rgba(10, 150, 255, 0.9)",
          data: [null, cypData.charges.period.returns, null]
        },
        {
          label: "Pagos (Pedientes)",
          backgroundColor: "rgb(255, 116, 37, 0.7)",
          data: [null, cypData.payments.period.pending, null]
        },
        {
          label: "Pagos (Devuelto)",
          backgroundColor: "rgb(255, 99, 9, 0.9)",
          data: [null, cypData.payments.period.returns, null]
        },

        {
          label: "Cobros (Pedientes)",
          backgroundColor: "rgba(51, 168, 255, 0.7)",
          data: [null, null, cypData.charges.accumulate.pending]
        },
        {
          label: "Cobros (Devuelto)",
          backgroundColor: "rgba(10, 150, 255, 0.9)",
          data: [null, null, cypData.charges.accumulate.returns]
        },
        {
          label: "Pagos (Pedientes)",
          backgroundColor: "rgb(255, 116, 37, 0.7)",
          data: [null, null, cypData.payments.accumulate.pending]
        },
        {
          label: "Pagos (Devuelto)",
          backgroundColor: "rgb(255, 99, 9, 0.9)",
          data: [null, null, cypData.payments.accumulate.returns]
        },

        {
          label: "CashFlow",
          backgroundColor: "rgb(153, 0, 153, 0.9)",
          type: 'line',
          data: [cypData.cashFlow.previous, cypData.cashFlow.period, cypData.cashFlow.accumulate]
        }
      ]
    };
    
    const config = {
      type: "bar",
      data: dataChart,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            stacked: true,
          },
          y: {
            stacked: true,
          },
        },
        interaction: {
          intersect: false,
        },
        plugins: {
          legend: {
            display: false, // This hides all text in the legend and also the labels.
          },
        },
      },
    };

    if (this.stackedChart != undefined) {
      this.stackedChart.destroy();
    }

    console.log(config);

    this.stackedChart = new Chart(canvas, config);
  }
  

  getData(){
    return {
      charges: {
        previous: {
          pending: 7000.00,
          returns: 3000.00
        },
        period: {
          pending: 14000.00,
          returns: 6000.00
        },
        accumulate: {
          pending: 21000.00,
          returns: 9000.00
        }
      },
      payments: {
        previous: {
          pending: -7000.00,
          returns: -4000.00
        },
        period: {
          pending: -5000.00,
          returns: -1000.00
        },
        accumulate: {
          pending: -12000.00,
          returns: -5000.00
        }
      },
      cashFlow: {
        previous: -1000.00,
        period: 14000.00,
        accumulate: 13000.00
      }
    }
  }
}

window.customElements.define(
  "aon-dashboard-charge-payments",
  AonDashboardChargePayments
);
