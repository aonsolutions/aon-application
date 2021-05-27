import { formatDate } from "../../services/utils.js";
import * as UTILS from "./accounting-utils.js";

let selectedElement,
  chartData,
  accounts,
  arrIncome,
  income,
  arrPurchases,
  purchases,
  arrOutgoings,
  outgoings,
  raw,
  arrAmortizations,
  amortizations,
  liquid,
  data1,
  data2,
  chart,
  options,
  selAccounts,
  position = null;

function getArray(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return arrIncome;
    case 2:
      return arrPurchases;
    case 3:
      return arrOutgoings;
    case 5:
      return arrAmortizations;
    default:
      return null;
  }
}

function getTotalByColumn(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return income;
    case 2:
      return purchases;
    case 3:
      return outgoings;
    case 5:
      return amortizations;
    default:
      return null;
  }
}

export const colChart = (div, data, selectedPeriod, isMobile, filter) => {
  return new Promise((resolve) => {
    //PARA EL TRIMESTRAL

    filter = filter == null ? { show: "yearly" } : filter

    

    if (data.intervals && filter && filter.show === "quarterly") {
      // accounts = data.intervals;
      let dteFrom = UTILS.getDateFromString(selectedPeriod.initiationDate);
      accounts = [];
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
        for (let j = 0; j < data.intervals.length; j++) {
          let interFrom = UTILS.getDateFromString(
            data.intervals[j].interval.fromDate
          );
          let interTo = UTILS.getDateFromString(
            data.intervals[j].interval.toDate
          );

          if (
            interFrom.getTime() != interTo.getTime() &&
            dteFrom <= interFrom &&
            dteTo >= interTo
          ) {
            data.intervals[j].statements.forEach((stm) => {
              let repeatedAccount = stmnts.filter(
                (st) =>
                  JSON.stringify(st.account) == JSON.stringify(stm.account)
              );
              if (repeatedAccount.length > 0) {
                repeatedAccount[0].debit = repeatedAccount[0].debit + stm.debit;
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
            fromDate: formatDate(dteFrom),
            toDate: formatDate(dteTo),
            name: `${i}T`,
          },
          statements: stmnts,
        };
        accounts.push(quarter);
        dteFrom.setMonth(dteFrom.getMonth() + 3);
        dteFrom.setDate(1);
      }

      let totalYear = null;
      try {
        totalYear = data.intervals
          ? data.intervals.filter(
              (acc) =>
                acc.interval.fromDate == acc.interval.toDate &&
                /31\/12\/d*/.test(acc.interval.fromDate)
            )[0]
          : null;
      } catch (error) {
        console.log(error);
      }

      if (totalYear) accounts.push(totalYear);
    } else if (data.intervals && filter && filter.show === "yearly") {
      accounts = data.intervals || [];

      selectedElement = accounts.filter((acc) =>
        /31\/12\/d*/.test(acc.interval.fromDate)
      )[0];
    } else {
      accounts = data.intervals || [];
    }

    const drawChart = () => {
      let divCombo = document.createElement("div");

      if (accounts.length > 1) {
        chartData = [
          ["Month", "Vtas./Ing.", "Cpas./Gtos./Amort.", "Resultado"],
        ];

        if (accounts != null) {
          let elements = accounts.filter(
            (acc) => acc.interval.fromDate != acc.interval.toDate
          );

          if (elements != null) {
            elements.forEach((element) => {
              let result = element.statements.filter(
                (st) => st.account.type == "RESULT"
              )[0];
              let total = result.credit - result.debit;
              let entry = [
                element.interval.name,
                result.credit,
                result.debit,
                total,
              ];
              chartData.push(entry);
            });
          }
        }

        let table = google.visualization.arrayToDataTable(chartData);
        position = isMobile ? "none" : "right";
        let options = {
          title: `Resultados ${selectedPeriod.name}`,
          vAxis: { title: "Cantidad (€)" },
          hAxis: { title: "Mes" },
          seriesType: "bars",
          series: { 2: { type: "line" } },
          height: isMobile ? window.innerHeight / 2 : window.innerWidth / 3,
          legend: "none",
          // legend: {
          //   position: position,
          //   alignment: "center",
          // },
        };

        divCombo.style.width = isMobile ? "100%" : "70%";
        div.style.display = "flex";
        div.style.flexWrap = "wrap";
        div.style.justifyContent = "center";
        div.style.alignItems = "center";
        div.appendChild(divCombo);
        chart = new google.visualization.ComboChart(divCombo);

        google.visualization.events.addListener(chart, "select", callbackYear);

        document.getElementById(
          "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
        ).style.display = "none";

        chart.draw(table, options);

        let mobileLegend = UTILS.getMobileLegend(accounts, isMobile);
        div.appendChild(mobileLegend);
        if (isMobile) {
          divCombo.querySelector(":nth-child(1)").style.marginLeft = "8%";
          divCombo.style.overflow = "hidden";
        }
      } else {
        let divMessage = document.createElement("div");
        divMessage.style.display = "flex";
        divMessage.style.justifyContent = "center";
        divMessage.style.alignItems = "center";
        divMessage.style.paddingTop = "1.5em";
        divMessage.style.maxWidth = "90%";

        let message = isMobile
          ? document.createElement("p")
          : document.createElement("h4");
        if (isMobile) message.style.fontWeight = "bold";
        message.style.textAlign = "center";
        message.innerHTML = "NO HAY DATOS DISPONIBLES PARA ESTA CONSULTA";
        message.style.margin = "10%";

        let infoSpan = document.createElement("span");
        infoSpan.classList.add("material-icons-outlined");
        infoSpan.style.fontSize = isMobile ? "2em" : "3.5em";

        infoSpan.style.display = isMobile ? "block" : "";

        infoSpan.style.marginRight = "10px";

        infoSpan.innerHTML = "info";

        divMessage.appendChild(infoSpan);
        divMessage.appendChild(message);

        div.appendChild(divMessage);

        document
          .getElementById(
            "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
          )
          .addEventListener("click", () => {
            drawChart();
            document
              .querySelector(
                "#aonAccountingSidenavOPCIONESList li:nth-child(1)"
              )
              .click();
          });
      }
    };

    google.charts.load("current", { packages: ["corechart"] });

    google.charts.setOnLoadCallback(
      filter && filter.show == "yearly"
        ? drawPeriodChart(selectedElement)
        : drawChart
    );
    resolve(true);

    const callbackYear = () => {
      if (chart.getSelection()[0] != null) {
        let selectedRow = chart.getSelection()[0].row;

        let intervalName = chartData[selectedRow + 1][0];

        selectedElement = accounts.filter(
          (element) => element.interval.name == intervalName
        )[0];

        drawPeriodChart(selectedElement);
      }
    };

    function listener() {
      if (chart.getSelection()[0] != null) {
        let selectedColumn = chart.getSelection()[0].column;
        drawPieChart(selectedColumn);
      }
    }

    function drawPieChart(selectedColumn) {
      document.getElementById(
        "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
      ).style.display = "";

      let arr = getArray(selectedColumn);
      let sDteStr = selectedElement.interval.fromDate;
      let eDteStr = selectedElement.interval.toDate;

      let title = !(filter && filter.show == "yearly")
        ? `${UTILS.getTitle(selectedColumn)} ${UTILS.getPeriodName(
            sDteStr,
            eDteStr
          )}`
        : `${UTILS.getTitle(selectedColumn)} ${selectedPeriod.name}`;

      if (arr != null) {
        data2 = new google.visualization.DataTable();
        data2.addColumn("string", "Concepto");
        data2.addColumn("number", "Cantidad");

        let rows = new Array();

        let colors = [];

        arr.forEach((entry) => {
          let amount = null;
          if (selectedColumn == 1) amount = entry.credit - entry.debit;
          else amount = entry.debit - entry.credit;

          rows.push([
            entry.account.description,
            {
              v: amount,
              f: UTILS.formatNumber(amount) + " €",
            },
          ]);
          colors.push(UTILS.getRandomColor());
        });

        data2.addRows(rows);

        let opt = {
          // title: title,
          height: (window.innerHeight / 100) * 80,
          // width: (window.innerWidth / 2),
          isStacked: true,
          colors: colors,
          legend: { position: "none" },
        };

        document
          .getElementById(
            "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
          )
          .addEventListener("click", () => {
            drawPeriodChart(selectedElement);
          });

        let pieDiv = document.createElement("div");
        pieDiv.id = "pieDiv";

        console.log(isMobile);
        pieDiv.style.width = isMobile ? "100%" : "60%";

        var chart2 = new google.visualization.PieChart(pieDiv);

        let legend = document.createElement("table");
        legend.id = "legend";
        legend.style.padding = "10px";
        legend.style.marginTop = "auto";
        legend.style.marginBottom = "auto";

        for (let i = 0; i < arr.length; i++) {
          let amount =
            selectedColumn != 1
              ? arr[i].debit - arr[i].credit
              : arr[i].credit - arr[i].debit;

          let legendTr = document.createElement("tr");
          let colorTd = document.createElement("td");
          // colorTd.style.backgroundColor = colors[i];
          // colorTd.style.width = "50px";

          const colorDiv = document.createElement("div");
          colorDiv.style.background = colors[i];

          const pixels = 15;

          colorDiv.style.width = pixels + "px";
          colorDiv.style.height = pixels + "px";
          // colorDiv.style.padding = "5px";
          colorDiv.style.borderRadius = "999px";

          colorTd.appendChild(colorDiv);

          legendTr.appendChild(colorTd);

          let percentTd = document.createElement("td");
          percentTd.id = "percentTd";
          let percent = (amount / getTotalByColumn(selectedColumn)) * 100;
          percentTd.innerHTML = UTILS.formatNumber(percent) + "%";
          percentTd.style.textAlign = "right";
          percentTd.style.width = "4em";
          legendTr.appendChild(percentTd);

          let descTd = document.createElement("td");
          descTd.style.maxWidth = window.innerWidth > 360 ? "10em" : "7em";

          // descTd.style.fontSize = ".8em";
          descTd.style.whiteSpace = "nowrap";
          descTd.style.textOverflow = "ellipsis";
          descTd.style.overflow = "hidden";
          let description = arr[i].account.description;
          descTd.innerText = description;
          descTd.style.padding = "5px";
          descTd.style.cursor = "pointer";
          // descTd.dataset["complete"] = arr[i].account.description;
          descTd.addEventListener("click", () => {
            if (descTd.style.whiteSpace == "nowrap") {
              descTd.style.whiteSpace = "";
              descTd.style.textOverflow = "";
              descTd.style.overflow = "";
              colorTd.style.display = "flex";
              colorTd.style.alignItems = "flex-start";
              colorDiv.style.marginTop = "6.5px";
              percentTd.style.verticalAlign = "text-top";
              percentTd.style.marginTop = "5.5px";
            } else {
              descTd.style.whiteSpace = "nowrap";
              descTd.style.textOverflow = "ellipsis";
              descTd.style.overflow = "hidden";
              colorTd.style.display = "";
              colorTd.style.alignItems = "";
              colorDiv.style.marginTop = "";
              percentTd.style.verticalAlign = "";
              percentTd.style.marginTop = "";
            }
          });

          legendTr.appendChild(descTd);

          let amountTd = document.createElement("td");
          amountTd.style.textAlign = "right";
          // amountTd.style.width = "17em";
          amountTd.innerText =
            window.innerWidth > 350
              ? UTILS.formatNumber(amount) + " €"
              : UTILS.formatNumber(amount) + "€";
          legendTr.appendChild(amountTd);
          legend.appendChild(legendTr);
          legend.style.width = isMobile ? "95%" : "38%";
        }

        let head = document.createElement("h1");
        head.innerHTML = title;
        head.style.fontSize = "1em";

        div.innerHTML = "";
        div.appendChild(pieDiv);
        div.appendChild(legend);
        div.style.marginTop = "3em";
        div.style.marginBottom = "3em";
        chart2.draw(data2, opt);

        if (isMobile) {
          let ratio = window.innerWidth / window.innerHeight;

          let marg = ratio <= 1 ? -1 * 35 * (1 - ratio) * 1.8 : 0;

          document.querySelector("#pieDiv > div:first-of-type").style.overflow =
            "hidden";
          document.querySelector(
            "#pieDiv > div:first-of-type > div:first-of-type"
          ).style.marginTop = `${marg}%`;
          document.querySelector(
            "#pieDiv > div:first-of-type > div:first-of-type"
          ).style.marginBottom = `${marg}%`;
        }

        pieDiv.prepend(head);
      }
    }

    function drawPeriodChart(selectedElement) {
      if (accounts.length > 1) {
        div.innerHTML = "";

        document
          .getElementById(
            "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
          )
          .addEventListener("click", () => {
            drawChart();
            document.getElementById(
              "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
            ).style.display = "none";
          });

        selAccounts = selectedElement.statements;

        arrIncome = selAccounts
          .filter(
            (a) =>
              a.account.code != null &&
              a.account.code.length > 2 &&
              ((a.account.code.substring(0, 1) == "7" &&
                a.credit - a.debit > 0) ||
                (a.account.code.substring(0, 1) == "6" &&
                  a.debit - a.credit < 0))
          )
          .sort((a, b) => b.credit - b.debit - (a.credit - a.debit));

        income =
          arrIncome.length != 0
            ? arrIncome.map((a) => a.credit - a.debit).reduce((a, b) => a + b)
            : 0;

        arrPurchases = selAccounts
          .filter(
            (a) =>
              a.account.code != null &&
              a.account.code.length > 2 &&
              Number.parseInt(a.account.code.substring(0, 2)) >= 60 &&
              Number.parseInt(a.account.code.substring(0, 2)) <= 61 &&
              a.debit - a.credit > 0
          )
          .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

        purchases =
          arrPurchases.length != 0
            ? arrPurchases
                .map((a) => a.debit - a.credit)
                .reduce((a, b) => a + b)
            : 0;

        arrOutgoings = selAccounts
          .filter(
            (a) =>
              a.account.code != null &&
              a.account.code.length > 2 &&
              ((Number.parseInt(a.account.code.substring(0, 2)) >= 62 &&
                Number.parseInt(a.account.code.substring(0, 2)) <= 67 &&
                a.debit - a.credit > 0) ||
                (a.account.code.substring(0, 1) == "7" &&
                  a.credit - a.debit < 0))
          )
          .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

        outgoings =
          arrOutgoings.length != 0
            ? arrOutgoings
                .map((a) => a.debit - a.credit)
                .reduce((a, b) => a + b)
            : 0;

        raw = income - purchases - outgoings;

        arrAmortizations = selAccounts
          .filter(
            (a) =>
              a.account.code != null &&
              a.account.code.length > 2 &&
              a.account.code.substring(0, 2) == "68" &&
              a.debit - a.credit > 0
          )
          .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

        amortizations =
          arrAmortizations.length != 0
            ? arrAmortizations
                .map((a) => a.debit - a.credit)
                .reduce((a, b) => a + b)
            : 0;

        liquid = raw - amortizations;
        google.charts.load("current", { packages: ["corechart"] });
        google.charts.setOnLoadCallback(drawBarChart);
      } else {
        let divMessage = document.createElement("div");
        divMessage.style.display = "flex";
        divMessage.style.justifyContent = "center";
        divMessage.style.alignItems = "center";
        divMessage.style.paddingTop = "1.5em";
        divMessage.style.maxWidth = "90%";

        let message = isMobile
          ? document.createElement("p")
          : document.createElement("h4");
        if (isMobile) message.style.fontWeight = "bold";
        message.style.textAlign = "center";
        message.innerHTML = "NO HAY DATOS DISPONIBLES PARA ESTA CONSULTA";
        message.style.margin = "10%";

        let infoSpan = document.createElement("span");
        infoSpan.classList.add("material-icons-outlined");
        infoSpan.style.fontSize = isMobile ? "2em" : "3.5em";

        infoSpan.style.display = isMobile ? "block" : "";

        infoSpan.style.marginRight = "10px";

        infoSpan.innerHTML = "info";

        divMessage.appendChild(infoSpan);
        divMessage.appendChild(message);

        div.appendChild(divMessage);

        document
          .getElementById(
            "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
          )
          .addEventListener("click", () => {
            drawChart();
            document
              .querySelector(
                "#aonAccountingSidenavOPCIONESList li:nth-child(1)"
              )
              .click();
          });
      }
    }

    function drawBarChart() {
      // Create the data table.

      document.getElementById(
        "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
      ).addEventListener("click", () => {
        div.innerHTML = "";
        drawChart();
      });

      document.getElementById(
        "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton"
      ).style.display = filter && filter.show == "yearly" ? "none" : "";




      data1 = new google.visualization.DataTable();
      data1.addColumn("string", "Concepto");
      data1.addColumn("number", "Ingresos");
      data1.addColumn("number", "Compras");
      data1.addColumn("number", "Gastos");
      data1.addColumn("number", "Resultado bruto");
      data1.addColumn("number", "Amortizaciones");
      data1.addColumn("number", "Resultado neto");

      data1.addRows([
        [
          "Vtas./Ing.",
          { v: income, f: UTILS.formatNumber(income) + " €" },
          null,
          null,
          null,
          null,
          null,
        ],
        [
          "Cpas./Gtos.",
          null,
          {
            v: purchases,
            f: UTILS.formatNumber(purchases) + " €",
          },
          {
            v: outgoings,
            f: UTILS.formatNumber(outgoings) + " €",
          },
          null,
          null,
          null,
        ],
        [
          "Rdo. bruto",
          null,
          null,
          null,
          { v: raw, f: UTILS.formatNumber(raw) + " €" },
          null,
          null,
        ],
        [
          "Amortiz.",
          null,
          null,
          null,
          null,
          {
            v: amortizations,
            f: UTILS.formatNumber(amortizations) + " €",
          },
          null,
        ],
        [
          "Rdo. neto",
          null,
          null,
          null,
          null,
          null,
          { v: liquid, f: UTILS.formatNumber(liquid) + " €" },
        ],
      ]);
      let formatter = Intl.DateTimeFormat("es");

      let sDteStr = selectedElement.interval.fromDate;
      let eDteStr = selectedElement.interval.toDate;
      // Set chart options
      options = {
        title: !(filter && filter.show == "yearly")
          ? `Resultados ${UTILS.getPeriodName(sDteStr, eDteStr)}`
          : `Resultados ${selectedPeriod.name}`,
        vAxis: { title: "Cantidad (€)" },
        height: isMobile ? window.innerHeight / 2 : window.innerWidth / 3,
        isStacked: true,
        legend: {
          position: "none",
        },
      };

      div.style.display = "flex";
      div.style.flexWrap = "wrap";
      div.style.justifyContent = "center";

      let divCol = document.createElement("div");
      divCol.style.width = isMobile ? "100%" : "70%";
      divCol.id = "divCol";
      div.appendChild(divCol);

      chart = new google.visualization.ColumnChart(divCol);
      chart.draw(data1, options);

      if (isMobile) {
        div.querySelector(":nth-child(1)").style.marginLeft = "15%";
        div.style.overflow = "hidden";
      }

      //Adding listener
      google.visualization.events.addListener(chart, "select", listener);

      let legend = getColLegend(isMobile);

      div.appendChild(legend);
    }

    function getColLegend(isMobile) {
      //LEGEND

      let legend = document.createElement("table");
      legend.style.marginTop = "auto";
      legend.style.marginBottom = "auto";

      const colorSize = 15;

      const legendColData = [
        {
          color: "#3366cc",
          name: "Vtas./Ing.",
          amount: income,
          selectedColumn: 1,
        },
        {
          color: "#dc3912",
          name: "Compras",
          amount: purchases,
          selectedColumn: 2,
        },
        {
          color: "#ff9900",
          name: "Gastos",
          amount: outgoings,
          selectedColumn: 3,
        },
        {
          color: "#109618",
          name: "Rdo. bruto",
          amount: raw,
          selectedColumn: null,
        },
        {
          color: "#990099",
          name: "Amortiz.",
          amount: amortizations,
          selectedColumn: 5,
        },
        {
          color: "#0099c6",
          name: "Rdo. neto",
          amount: liquid,
          selectedColumn: null,
        },
      ];

      legendColData.forEach((categ) => {
        let trCateg = document.createElement("tr");
        let tdCategColor = document.createElement("td");
        tdCategColor.style.width = colorSize * 2.5 + "px";

        let divCategColor = document.createElement("div");
        divCategColor.style.width = "100%";
        divCategColor.style.height = colorSize + "px";
        divCategColor.style.borderRadius = "1px";
        divCategColor.style.background = categ.color;

        tdCategColor.appendChild(divCategColor);
        trCateg.appendChild(tdCategColor);

        let tdCategDesc = document.createElement("td");
        tdCategDesc.innerHTML = categ.name;
        tdCategDesc.style.maxWidth = "7em";
        tdCategDesc.style.textIndent = isMobile ? ".3em" : "";
        tdCategDesc.style.overflow = "hidden";
        tdCategDesc.style.whiteSpace = "nowrap";
        // tdCategDesc.style.textOverflow = "ellipsis";

        trCateg.appendChild(tdCategDesc);

        let tdCategAmount = document.createElement("td");
        tdCategAmount.style.textAlign = "right";
        tdCategAmount.style.width = "9em";
        tdCategAmount.innerHTML = `${UTILS.formatNumber(categ.amount)} €`;

        trCateg.appendChild(tdCategAmount);
        if (categ.selectedColumn && categ.amount && categ.amount != 0) {
          trCateg.style.cursor = "pointer";
          trCateg.addEventListener("click", () => {
            drawPieChart(categ.selectedColumn);
          });
        } else {
          trCateg.style.cursor = "default";
        }

        legend.appendChild(trCateg);
      });

      legend.style.width = isMobile ? "80%" : "";

      return legend;
    }
  });
};
