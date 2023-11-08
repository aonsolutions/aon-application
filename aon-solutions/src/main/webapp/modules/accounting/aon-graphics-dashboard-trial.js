import { AonElement } from "../../components/AonElement.js";
import { CSS, TAG } from "../../environments/environments.js";
import { isEmptyObject } from "../../services/utils.js";
import { AccoutingChartNew } from "./AccoutingChartNew.js";
import { getAccounting, getPeriods } from "../../services/accountingService.js";
import { AonIframe } from "../../components/aon-iframe.js";
import * as UTILS from "./AccountingUtils.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";

export class AonDashboardGraphicsTrial extends AonElement {
  PERIODS;
  ACCOUNTS;
  filter;
  selectedPeriod;
  params = {
    domain: localStorage.getItem("aon_domain_id"),
    domainName: localStorage.getItem("aon_domain_name"),
    user: "",
    level: 5,
    byMonth: true,
  };

  selectedElement;
  chartData;
  accounts;
  arrIncome;
  incomev;
  arrPurchases;
  purchases;
  arrOutgoings;
  outgoings;
  raw;
  arrAmortizations;
  amortizations;
  arrOtherOutgoings;
  otherOutgoings;
  liquid;
  data1;
  data2;
  chart;
  options;
  selAccounts;
  stackedChart;
  pieChart;

  set id(id) {
    this.setAttribute("id", id);
  }

  get id() {
    return this.getAttribute("id");
  }

  constructor(period) {
    super();
    this.id = this.id || "aonGraphicsDashboardTrial";
    this.applicationEl = document.querySelector("#pygContent");
    this.filter = {};
    this.filter.show = period;
    this.filter.year = new Date().getFullYear();
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
    if (!this.params.domain || !this.params.domainName) {
      try {
        let company = JSON.parse(localStorage.getItem("company"));
        this.params.domain = company.id;

        this.params.domainName = company.domain;
      } catch (error) {
        console.log(error);
      }
    }

    this.PERIODS = await getPeriods(this.params).catch((error) => {
      console.log(error);
      return [];
    });

    let lastDateTime = Math.max.apply(
      null,
      this.PERIODS.map((p) => new Date(p.initiationDate).getTime())
    );
    let lastPeriod = this.PERIODS.find(
      (p) => new Date(p.initiationDate).getTime() == lastDateTime
    );

    this.selectedPeriod = lastPeriod;

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

    this.accounts = await this.getData();

    if (this.accounts) {
      if (this.filter.show === "yearly") {
        this.calculateYearlyData(this.accounts);
        this.drawBarChart(canvas);
      } else {
        let accountsCalc = [];
        if (this.filter.show === "quarterly") {
          let dteFrom = UTILS.getDateFromString(this.selectedPeriod.initiationDate);
          for (let i = 1; i <= 4; i++) {
            let dteTo = new Date(dteFrom.getTime());
            dteTo.setMonth(dteTo.getMonth() + 3);
            dteTo.setDate(0);

            let stmnts = [
              {
                debit: 0,
                credit: 0,
                account: {
                  code: "RESULT",
                  description: "RESULTADO",
                  type: "RESULT",
                },
              },
            ];

            for (const element of this.accounts.intervals) {
              let interFrom = UTILS.getDateFromString(
                element.interval.fromDate
              );
              let interTo = UTILS.getDateFromString(element.interval.toDate);

              if (
                interFrom.getTime() != interTo.getTime() &&
                dteFrom <= interFrom &&
                dteTo >= interTo
              ) {
                element.statements.forEach((stm) => {
                  let repeatedAccount = stmnts.filter(
                    (st) =>
                      JSON.stringify(st.account) == JSON.stringify(stm.account)
                  );

                  if (repeatedAccount.length > 0) {
                    repeatedAccount[0].debit =
                      repeatedAccount[0].debit + stm.debit;
                    repeatedAccount[0].credit =
                      repeatedAccount[0].credit + stm.credit;
                  } else {
                    let statement = {
                      account: stm.account,
                      credit: stm.credit,
                      debit: stm.debit,
                    };
                    stmnts.push(statement);
                  }
                });
              }
            }

            let quarter = {
              interval: {
                fromDate: AonDateUtils.formatDate(dteFrom),
                toDate: AonDateUtils.formatDate(dteTo),
                name: `${i}T`,
              },
              statements: stmnts,
            };
            accountsCalc.push(quarter);
            dteFrom.setMonth(dteFrom.getMonth() + 3);
            dteFrom.setDate(1);
          }

          let totalYear = null;
          try {
            totalYear = this.accounts.intervals
              ? this.accounts.intervals.filter(
                  (acc) =>
                    acc.interval.fromDate == acc.interval.toDate &&
                    /31\/12\/d*/.test(acc.interval.fromDate)
                )[0]
              : null;
          } catch (error) {
            console.log(error);
          }

          if (totalYear) accountsCalc.push(totalYear);
        } else {
          accountsCalc = this.accounts.intervals || [];
        }
        accountsCalc = UTILS.getOnly6and7(accountsCalc);
        this.drawBarLineChart(canvas, accountsCalc);
      }
    }
  }

  async getData() {
    if (this.filter) {
      this.selectedPeriod = this.PERIODS.find(
        (p) => p.name == this.filter.year
      );
      this.params.level = this.filter.detail;
    }

    if (!isEmptyObject(this.PERIODS)) {
      if (this.PERIODS && this.PERIODS.length > 0) {
        this.params.period = this.selectedPeriod.id;

        this.params.fromDate = this.selectedPeriod.initiationDate;
        this.params.toDate = this.selectedPeriod.deadline;
      }
      this.ACCOUNTS = await getAccounting(this.params).catch((err) => {
        this.showError(err);
        return null;
      });
    }

    return this.ACCOUNTS;
  }

  calculateYearlyData(accountsData) {
    let accounts = accountsData.intervals || [];
    this.selectedElement = accounts.filter((acc) =>
      /31\/12\/d*/.test(acc.interval.fromDate)
    )[0];

    accounts = UTILS.getOnly6and7(accounts);

    if (accounts.length > 1) {
      this.selAccounts = this.selectedElement.statements.filter(
        (state) =>
          state.account &&
          state.account.code &&
          (state.account.code.substring(0, 1) == "6" ||
            state.account.code.substring(0, 1) == "7")
      );

      this.arrIncome = this.selAccounts
        .filter(
          (a) =>
            a.account.code != null &&
            a.account.code.length > 2 &&
            a.account.code.substring(0, 1) == "7"
        )
        .sort((a, b) => b.credit - b.debit - (a.credit - a.debit));

      this.income =
        this.arrIncome.length != 0
          ? this.arrIncome
              .map((a) => a.credit - a.debit)
              .reduce((a, b) => a + b)
          : 0;

      this.arrPurchases = this.selAccounts
        .filter(
          (a) =>
            a.account.code != null &&
            a.account.code.length > 2 &&
            Number.parseInt(a.account.code.substring(0, 2)) >= 60 &&
            Number.parseInt(a.account.code.substring(0, 2)) <= 61
        )
        .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

      this.purchases =
        this.arrPurchases.length != 0
          ? this.arrPurchases
              .map((a) => a.debit - a.credit)
              .reduce((a, b) => a + b)
          : 0;

      this.arrOutgoings = this.selAccounts
        .filter(
          (a) =>
            a.account.code != null &&
            a.account.code.length > 2 &&
            Number.parseInt(a.account.code.substring(0, 2)) >= 62 &&
            Number.parseInt(a.account.code.substring(0, 2)) <= 67
        )
        .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

      this.outgoings =
        this.arrOutgoings.length != 0
          ? this.arrOutgoings
              .map((a) => a.debit - a.credit)
              .reduce((a, b) => a + b)
          : 0;

      this.raw = this.income - this.purchases - this.outgoings;

      this.arrAmortizations = this.selAccounts
        .filter(
          (a) =>
            a.account.code != null &&
            a.account.code.length > 2 &&
            a.account.code.substring(0, 2) == "68" /* &&
            a.debit - a.credit > 0*/
        )
        .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

      this.amortizations =
        this.arrAmortizations.length != 0
          ? this.arrAmortizations
              .map((a) => a.debit - a.credit)
              .reduce((a, b) => a + b)
          : 0;

      this.arrOtherOutgoings = this.selAccounts
        .filter(
          (a) =>
            a.account.code != null &&
            a.account.code.length > 2 &&
            a.account.code.substring(0, 2) == "69"
        )
        .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

      this.otherOutgoings =
        this.arrOtherOutgoings.length != 0
          ? this.arrOtherOutgoings
              .map((a) => a.debit - a.credit)
              .reduce((a, b) => a + b)
          : 0;

      this.liquid = this.raw - this.otherOutgoings - this.amortizations;
    }
  }

  drawBarChart(canvas) {
    const dataChart = {
      labels: [
        "Vtas./Ing.",
        "Cpas./Gtos.",
        "Otros gastos",
        "Rdo. bruto",
        "Amortiz.",
        "Rdo. neto",
      ],
      datasets: [
        {
          label: "Vtas./Ing.",
          backgroundColor: "rgb(51, 102, 204, 0.7)",
          data: [this.income, null, null, null, null, null],
        },
        {
          label: "Cpas./Gtos.",
          backgroundColor: "rgb(220, 57, 18, 0.7)",
          data: [null, this.purchases, null, null, null, null],
        },
        {
          label: "Cpas./Gtos.",
          backgroundColor: "rgb(255, 153, 0, 0.7)",
          data: [null, this.outgoings, null, null, null, null],
        },
        {
          label: "Otros gastos",
          backgroundColor: "rgb(16, 150, 24, 0.7)",
          data: [null, null, this.otherOutgoings, null, null, null],
        },
        {
          label: "Rdo. bruto",
          backgroundColor: "rgb(153, 0, 153, 0.7)",
          data: [null, null, null, this.raw, null, null],
        },
        {
          label: "Amortiz.",
          backgroundColor: "rgb(0, 153, 198, 0.7)",
          data: [null, null, null, null, this.amortizations, null],
        },
        {
          label: "Rdo. neto",
          backgroundColor: "rgb(221, 68, 119, 0.7)",
          data: [null, null, null, null, null, this.liquid],
        },
      ],
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

    this.stackedChart = new Chart(canvas, config);
  }

  drawBarLineChart(canvas, accounts){
    let dataChart;
    if (accounts.length > 1) {
      if (this.filter.show === "quarterly") {
        dataChart = {
          labels: [
            "1T", 
            "2T", 
            "3T",
            "4T"
          ],
          datasets: []
        };
      } else {
        dataChart = {
          labels: [
            "Enero", 
            "Febrero", 
            "Marzo",
            "Abril",
            "Mayo",
            "Junio",
            "Julio",
            "Agosto",
            "Septiembre",
            "Octubre",
            "Noviembre",
            "Diciembre"
          ],
          datasets: []
        };
      }
      

      if (accounts != null) {
        let elements = accounts.filter((acc) => acc.interval.fromDate != acc.interval.toDate);

        if (elements != null) {
          elements.forEach((element) => {
            let result = element.statements.filter((st) => st.account.type == "RESULT")[0];

            let total = result.credit - result.debit;

            if(dataChart.datasets.length === 0){
              dataChart.datasets.push({
                label: element.interval.name,
                backgroundColor: "rgba(51, 102, 204, 0.7)",
                stack: 'Stack 0',
                data: [result.credit]
              });

              dataChart.datasets.push({
                label: element.interval.name,
                backgroundColor: "rgba(220, 57, 18, 0.7)",
                stack: 'Stack 1',
                data: [result.debit]
              });

              dataChart.datasets.push({
                label: element.interval.name,
                backgroundColor: "rgba(255, 153, 0, 0.7)",
                type: 'line',
                data: [total]
              });
            } else {
              dataChart.datasets[0].data.push(result.credit);
              dataChart.datasets[1].data.push(result.debit);
              dataChart.datasets[2].data.push(total);
            }
          });
        }
      }
  
      const config = {
        type: "bar",
        data: dataChart,
        options: {
          responsive: true,
          maintainAspectRatio: false,
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
  }
}

window.customElements.define(
  "aon-dashboard-graphics-trial",
  AonDashboardGraphicsTrial
);
