import { TAG } from "../../environments/environments.js";
import { waitEl } from "../../services/utils.js";
import { setStyles } from "../../services/utilsComponents.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import * as UTILS from "./AccountingUtils.js";

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
  arrOtherOutgoings,
  otherOutgoings,
  liquid,
  data1,
  data2,
  chart,
  options,
  selAccounts;

function getArray(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return arrIncome;
    case 2:
      return arrPurchases;
    case 3:
      return arrOutgoings;
    case 4:
      return arrOtherOutgoings;
    case 6:
      return arrAmortizations;
    default:
      return null;
  }
}

function getPositiveArraySum(array, isIncome) {
  if (array) {
    return array
    .filter(inc => inc)
    .map((inc) => isIncome ? inc.credit - inc.debit : inc.debit - inc.credit)
    .filter(amt => amt > 0)
    .reduce((a, b) => a + b, 0);
  } else {
    return 0;
  }
}

function getTotalByColumn(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return getPositiveArraySum(arrIncome, true);
    case 2:
      return getPositiveArraySum(arrPurchases, false);
    case 3:
      return getPositiveArraySum(arrOutgoings, false);
    case 4:
      return getPositiveArraySum(arrOtherOutgoings, false);
    case 6:
      return getPositiveArraySum(arrAmortizations, false);
    default:
      return null;
  }
}

const colChart = (div, data, selectedPeriod, isMobile, filter, aonIframe, leyend) => {
  const doc = aonIframe.getDocument();
  const google = aonIframe.getGoogle();

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
        for (const element of data.intervals) {
          let interFrom = UTILS.getDateFromString(element.interval.fromDate);
          let interTo = UTILS.getDateFromString(element.interval.toDate);

          if (interFrom.getTime() != interTo.getTime() && dteFrom <= interFrom && dteTo >= interTo) {
            element.statements.forEach((stm) => {
              let repeatedAccount = stmnts
              .filter((st) =>JSON.stringify(st.account) == JSON.stringify(stm.account));

              if (repeatedAccount.length > 0) {
                repeatedAccount[0].debit = repeatedAccount[0].debit + stm.debit;
                repeatedAccount[0].credit = repeatedAccount[0].credit + stm.credit;
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

      selectedElement = accounts.filter((acc) =>/31\/12\/d*/.test(acc.interval.fromDate))[0];
    } else {
      accounts = data.intervals || [];
    }

    accounts = UTILS.getOnly6and7(accounts);

    const drawChart = () => {
      let divCombo = document.createElement("div");

      if (accounts.length > 1) {
        chartData = [
          ["Month", "Vtas./Ing.", "Cpas./Gtos./Amort.", "Resultado"],
        ];

        if (accounts != null) {
          let elements = accounts.filter((acc) => acc.interval.fromDate != acc.interval.toDate);

          if (elements != null) {
            elements.forEach((element) => {
              let result = element.statements.filter((st) => st.account.type == "RESULT")[0];

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
        setStyles(div, {display : "flex", flexWrap : "wrap", justifyContent : "center", alignItems : "center"});
        div.appendChild(divCombo);
        chart = new google.visualization.ComboChart(divCombo);

        google.visualization.events.addListener(chart, "select", callbackYear);

        let trialToolbarHeader = document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");

        if (trialToolbarHeader){
          trialToolbarHeader.style.display = "none";
        }
        
        chart.draw(table, options);

        let mobileLegend = UTILS.getMobileLegend(accounts, isMobile);
        div.appendChild(mobileLegend);
        if (isMobile) {
          let firstChild = divCombo.querySelector(":nth-child(1)");
          if (firstChild){
            firstChild.style.marginLeft = "8%";
          }
          divCombo.style.overflow = "hidden";
        }
      } else {
        let divMessage = setStyles(document.createElement(TAG.DIV), {
          display : "flex", justifyContent : "center", alignItems : "center", paddingTop : "1.5em", maxWidth : "90%"
        });

        let message = isMobile ? document.createElement("p") : document.createElement("h4");
        if (isMobile) message.style.fontWeight = "bold";
        message.innerHTML = "NO HAY DATOS DISPONIBLES PARA ESTA CONSULTA";
        setStyles(message, {textAlign : "center", margin : "10%"});

        let infoSpan = setStyles(document.createElement(TAG.SPAN), {
          fontSize : isMobile ? "2em" : "3.5em",
          display : isMobile ? "block" : "",
          marginRight : "10px"
        });
        infoSpan.classList.add("material-icons-outlined");
        infoSpan.innerHTML = "info";

        divMessage.appendChild(infoSpan);
        divMessage.appendChild(message);

        div.appendChild(divMessage);

        let elem = document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");

        if (elem) {
          elem.onclick = () => {
            drawChart();
            let accSidenav = document.querySelector("#aonAccountingSidenavOPCIONESList li:nth-child(1)");
            if (accSidenav)
              accSidenav.click();
          };
        }
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


      let trialToolbar = document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");

      if (trialToolbar)
        trialToolbar.style.display = "";

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
          if (amount >= 0) {
            rows.push([
              entry.account.description,
              {
                v: amount,
                f: UTILS.formatNumber(amount) + " €",
              },
            ]);
            // colors.push(UTILS.getRandomColor());
          }
        });
        for (let i=0; i<rows.length; i++) {
          let index = i > UTILS.googleChartsColors.length - 1 ? i % (UTILS.googleChartsColors.length ) : i;
          colors.push(UTILS.googleChartsColors[index]);
        }

        data2.addRows(rows);

        let opt = {
          // title: title,
          height: (window.innerHeight / 100) * 80,
          // width: (window.innerWidth / 2),
          isStacked: true,
          colors: colors,
          legend: { position: "none" },
        };
        let trialToolbar = document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");
        
        if (trialToolbar) {
          trialToolbar.addEventListener("click", () => {
            drawPeriodChart(selectedElement);
          });
        }
        

        let pieDiv = document.createElement("div");
        pieDiv.id = "pieDiv";

        pieDiv.style.width = isMobile ? "100%" : "55%";

        let chart2 = new google.visualization.PieChart(pieDiv);

        let legend = setStyles(document.createElement("table"), {
          padding : "10px",
          marginTop : "auto",
          marginBottom : "auto",
          zIndex :1
        });
        legend.id = "legend";

        for (let i = 0; i < arr.length; i++) {
          let amount =
            selectedColumn != 1
              ? arr[i].debit - arr[i].credit
              : arr[i].credit - arr[i].debit;

          let legendTr = document.createElement("tr");
          let colorTd = document.createElement("td");

          const pixels = 15;
          const colorDiv = setStyles(document.createElement("div"), {
            background : colors[i],
            width : pixels + "px",
            height : pixels + "px",
            borderRadius : "999px"
          });
          colorTd.appendChild(colorDiv);

          legendTr.appendChild(colorTd);

          let percentTd = document.createElement("td");
          percentTd.id = "percentTd";
          let percent = (amount / getTotalByColumn(selectedColumn)) * 100;
          percentTd.innerHTML = percent > 0 ? UTILS.formatNumber(percent) + "%" : "-";
          setStyles(percentTd, {textAlign : "right", width : "3em"});
          legendTr.appendChild(percentTd);

          let descTd = setStyles(document.createElement("td"), {
            maxWidth : !isMobile ? "10em" : "7em",
            whiteSpace : "nowrap",
            textOverflow : "ellipsis",
            overflow : "hidden",
            padding : "5px",
            cursor : "pointer"
          });
          
          let description = arr[i].account.description;
          descTd.innerText = description;
          descTd.title = description;
          // descTd.dataset["complete"] = arr[i].account.description;
          descTd.addEventListener("click", () => {
            if (descTd.style.whiteSpace == "nowrap") {
              setStyles(descTd, {whiteSpace : "", textOverflow : "", overflow : ""});
              setStyles(colorTd, {display : "flex", alignItems : "flex-start"});
              colorDiv.style.marginTop = "6.5px";
              setStyles(percentTd, {verticalAlign : "text-top", marginTop : "5.5px"});
            } else {
              setStyles(descTd, {whiteSpace : "nowrap", textOverflow : "ellipsis", overflow : "hidden"});
              setStyles(colorTd, {display : "", alignItems : ""});
              colorDiv.style.marginTop = "";
              setStyles(percentTd, {verticalAlign : "", marginTop : ""});
            }
          });

          legendTr.appendChild(descTd);

          let amountTd = document.createElement("td");
          amountTd.style.textAlign = "right";
          amountTd.innerText =
            window.innerWidth > 350
              ? UTILS.formatNumber(amount) + " €"
              : UTILS.formatNumber(amount) + "€";
          legendTr.appendChild(amountTd);
          legend.appendChild(legendTr);
          legend.style.width = isMobile ? "95%" : "43%";
        }

        let head = document.createElement("h1");
        head.innerHTML = title;
        head.style.fontSize = "1em";
        div.innerHTML = "";
        div.appendChild(pieDiv);
        div.appendChild(legend);
        // setStyles(div, {margin : "3em auto"});
        chart2.draw(data2, opt);

        if (isMobile) {
          let ratio = window.innerWidth / window.innerHeight;

          let marg = ratio <= 1 ? -1 * 35 * (1 - ratio) * 1.5 : 0;

          let firstOfPie = pieDiv.querySelector("div:first-of-type");

          if (firstOfPie) {
            firstOfPie.style.overflow = "hidden";

            let firstOfPieSub = pieDiv.querySelector("div:first-of-type > div:first-of-type");

            if (firstOfPieSub) {
              firstOfPieSub.style.marginTop = `${marg}%`;
              firstOfPieSub.style.marginBottom = `${marg}%`;
            }

          }
        }

        pieDiv.prepend(head);
        if (pieDiv){
          waitEl(`#${pieDiv.id} svg g:last-child`, doc).then(el => el.style.pointerEvents = "none").catch(err => null);
        }
      }
    }

    function drawPeriodChart(selectedElement) {
      if (accounts.length > 1) {
        div.innerHTML = "";

      let aonGraphicsTrial = document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");

      if (aonGraphicsTrial)
          aonGraphicsTrial.addEventListener("click", () => {
            drawChart();
            aonGraphicsTrial.style.display = "none";
          });

        selAccounts = selectedElement.statements
        .filter(state => (
            state.account && state.account.code && (state.account.code.substring(0, 1) == "6" || state.account.code.substring(0, 1) == "7")
          ));

        arrIncome = selAccounts
          .filter(
            (a) =>
              a.account.code != null &&
              a.account.code.length > 2 &&
              ((a.account.code.substring(0, 1) == "7" /*&&  a.credit - a.debit > 0*/)/* ||
                (a.account.code.substring(0, 1) == "6" && a.debit - a.credit < 0)*/)
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
              Number.parseInt(a.account.code.substring(0, 2)) <= 61 /*&&
              a.debit - a.credit > 0*/
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
                Number.parseInt(a.account.code.substring(0, 2)) <= 67 /*&&
                a.debit - a.credit > 0*/)/* ||
                (a.account.code.substring(0, 1) == "7" &&
                  a.credit - a.debit < 0)*/)
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
              a.account.code.substring(0, 2) == "68"/* &&
              a.debit - a.credit > 0*/
          )
          .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

        amortizations =
          arrAmortizations.length != 0
            ? arrAmortizations
                .map((a) => a.debit - a.credit)
                .reduce((a, b) => a + b)
            : 0;
        
            
        arrOtherOutgoings = selAccounts
            .filter(
              (a) =>
              a.account.code != null &&
              a.account.code.length > 2 &&
              a.account.code.substring(0, 2) == "69"
            ).sort((a, b) => b.debit - b.credit - (a.debit - a.credit));

        otherOutgoings =
          arrOtherOutgoings.length != 0
            ? arrOtherOutgoings
                .map((a) => a.debit - a.credit)
                .reduce((a, b) => a + b)
            : 0;
            

        liquid = raw - otherOutgoings - amortizations;
        google.charts.load("current", { packages: ["corechart"] });
        google.charts.setOnLoadCallback(drawBarChart);
      } else {
        let divMessage = setStyles(document.createElement("div"), {
          display : "flex", 
          justifyContent : "center",
          alignItems : "center",
          paddingTop : "1.5em",
          maxWidth : "90%"
        });

        let message = isMobile
          ? document.createElement("p")
          : document.createElement("h4");
        if (isMobile) message.style.fontWeight = "bold";
        message.style.textAlign = "center";
        message.innerHTML = "NO HAY DATOS DISPONIBLES PARA ESTA CONSULTA";
        message.style.margin = "10%";

        let infoSpan = setStyles(document.createElement(TAG.SPAN), {
          fontSize : isMobile ? "2em" : "3.5em",
          display : isMobile ? "block" : "",
          marginRight : "10px"
        });
        infoSpan.classList.add("material-icons-outlined");

        infoSpan.innerHTML = "info";

        divMessage.appendChild(infoSpan);
        divMessage.appendChild(message);

        div.appendChild(divMessage);

        let elem = document.getElementById( "aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");

        if (elem) {
          elem.onclick = () => {
            drawChart();
            let elemChild = document.querySelector("#aonAccountingSidenavOPCIONESList li:nth-child(1)");
            if (elemChild){
              elemChild.click();
            }
          };
        }
      }
    }

    function drawBarChart() {
      // Create the data table.

      let trialToolbarH = document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton");

      if (trialToolbarH) {
        trialToolbarH.addEventListener("click", () => {
          div.innerHTML = "";
          drawChart();
        });
        trialToolbarH.style.display = filter && filter.show == "yearly" ? "none" : "";
      }

      data1 = new google.visualization.DataTable();
      data1.addColumn("string", "Concepto");
      data1.addColumn("number", "Ingresos");
      data1.addColumn("number", "Compras");
      data1.addColumn("number", "Gastos");
      data1.addColumn("number", "Otros gastos");
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
          null,
        ],
        [
          "Otros gastos",
          null,
          null,
          null,
          { v: otherOutgoings, f: UTILS.formatNumber(otherOutgoings) + " €" },
          null,
          null,
          null,
        ],
        [
          "Rdo. bruto",
          null,
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
          null,
          { v: liquid, f: UTILS.formatNumber(liquid) + " €" },
        ],
      ]);

      let sDteStr = selectedElement.interval.fromDate;
      let eDteStr = selectedElement.interval.toDate;
      // Set chart options
      options = {
        title: !(filter && filter.show == "yearly")
          ? `Resultados ${UTILS.getPeriodName(sDteStr, eDteStr)}`
          : `Resultados ${selectedPeriod.name}`,
        vAxis: { title: "Cantidad (€)" },
        height: isMobile ? window.innerHeight / 2 : (leyend ? window.innerWidth / 3 : 290),
        // width: leyend ? window.innerWidth / 3 : 200,
        isStacked: true,
        legend: {
          position: "none",
        },
      };

      setStyles(div, {display : "flex", flexWrap : "wrap", justifyContent : "center"});

      let divCol = document.createElement("div");
      divCol.style.width = isMobile ? "100%" : "70%";
      divCol.id = "divCol";
      div.appendChild(divCol);

      chart = new google.visualization.ColumnChart(divCol);
      chart.draw(data1, options);

      if (isMobile) {
        let childone = div.querySelector(":nth-child(1)");
        if (childone){
          childone.style.marginLeft = "15%";
        }
      }

      //Adding listener
      google.visualization.events.addListener(chart, "select", listener);

      // let legend = getColLegend(isMobile);
      // div.appendChild(legend);
    }

    function getColLegend(isMobile) {
      //LEGEND
      let legend = setStyles(document.createElement(TAG.TABLE), {marginTop : "auto", marginBottom : "auto"});

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
          name: "Otros Gastos",
          amount: otherOutgoings,
          selectedColumn: 4,
        },
        {
          color: "#990099",
          name: "Rdo. bruto",
          amount: raw,
          selectedColumn: null,
        },
        {
          color: "#0099c6",
          name: "Amortiz.",
          amount: amortizations,
          selectedColumn: 6,
        },
        {
          color: "#dd4477",
          name: "Rdo. neto",
          amount: liquid,
          selectedColumn: null,
        },
      ];

      legendColData.forEach((categ) => {
        let trCateg = document.createElement("tr");
        let tdCategColor = document.createElement("td");
        tdCategColor.style.width = colorSize * 2.5 + "px";

        let divCategColor = setStyles(document.createElement("div"), {
          width : "100%",
          height : colorSize + "px",
          borderRadius : "1px",
          background : categ.color
        });

        tdCategColor.appendChild(divCategColor);
        trCateg.appendChild(tdCategColor);

        let tdCategDesc = setStyles(document.createElement("td"), {
          maxWidth : "7em",
          textIndent : isMobile ? ".3em" : "",
          overflow : "hidden",
          whiteSpace : "nowrap"
        });
        tdCategDesc.innerHTML = categ.name;
        // tdCategDesc.style.textOverflow = "ellipsis";

        trCateg.appendChild(tdCategDesc);

        let tdCategAmount = setStyles(document.createElement("td"), {
          textAlign : "right", 
          width : "9em"
        });

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

export const AccoutingChart = {
  colChart
}