import { TAG } from "../../environments/environments.js";
import { TOTAL_SUPPLIED } from "../../environments/msg-en.js";
export function getRandomColor() {
  var letters = "0123456789ABCDEF";
  var color = "#";
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}
export function getDateFromString(strDate) {
  if (typeof strDate == "string") {
    let splittedDate = strDate.split("/");

    if (splittedDate.length != 3) return null;

    return new Date(`${splittedDate[1]}/${splittedDate[0]}/${splittedDate[2]}`);
  } else return null;
}

export function formatNumber(number) {
  if (number != null && !Number.isNaN(number)) {
    let nmbr = Math.round(number * 100) / 100;

    let formatted = nmbr.toLocaleString("es-ES", { minimumFractionDigits: 2 });

    if (formatted && formatted.length == 7 && formatted.charAt(0) != "-") {
      formatted = formatted.charAt(0) + "." + formatted.substring(1);
    } else if (
      formatted &&
      formatted.length == 8 &&
      formatted.charAt(0) == "-"
    ) {
      formatted = formatted.substring(0, 2) + "." + formatted.substring(2);
    }

    return formatted;
  }
  return null;
}

export function getTitle(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return "Ingresos";
    case 2:
      return "Compras";
    case 3:
      return "Gastos";
    case 5:
      return "Amortizaciones";
    default:
      return "";
  }
}

export function getPeriodName(dateFromStr, dateToStr) {
  let spaMonths = [
    "enero",
    "febrero",
    "marzo",
    "abril",
    "mayo",
    "junio",
    "julio",
    "agosto",
    "septiembre",
    "octubre",
    "noviembre",
    "diciembre",
  ];

  if (typeof dateFromStr == "string" && typeof dateToStr == "string") {
    let splittedFrom = dateFromStr.split("/");
    let splittedTo = dateToStr.split("/");
    if (splittedFrom.length == 3 && splittedTo.length == 3) {
      if (splittedFrom[1] == splittedTo[1]) {
        let monthNmbr = Number.parseInt(splittedFrom[1]);
        return monthNmbr &&
          !Number.isNaN(monthNmbr) &&
          monthNmbr > 0 &&
          monthNmbr < 13
          ? `${spaMonths[monthNmbr - 1]} de ${splittedFrom[2]}`
          : `del ${dateFromStr} al ${dateToStr}`;
      } else if (
        /0?1\/0?1\/d*/.test(dateFromStr) &&
        /31\/0?3\/d*/.test(dateToStr)
      ) {
        return `1T/${splittedFrom[2]}`;
      } else if (
        /0?1\/0?4\/d*/.test(dateFromStr) &&
        /30\/0?6\/d*/.test(dateToStr)
      ) {
        return `2T/${splittedFrom[2]}`;
      } else if (
        /0?1\/0?7\/d*/.test(dateFromStr) &&
        /30\/0?9\/d*/.test(dateToStr)
      ) {
        return `3T/${splittedFrom[2]}`;
      } else if (
        /0?1\/10\/d*/.test(dateFromStr) &&
        /31\/12\/d*/.test(dateToStr)
      ) {
        return `4T/${splittedFrom[2]}`;
      } else {
        return `del ${dateFromStr} al ${dateToStr}`;
      }
    }
  }
  return "";
}

export function getMobileLegend(accounts, isMobile) {
  const finalRegex = /31\/12\/d*/;
  let totalYear = null;
  try {
    totalYear = accounts
      ? accounts
          .filter(
            (acc) =>
              acc.interval.fromDate == acc.interval.toDate &&
              finalRegex.test(acc.interval.fromDate)
          )
          .map((acc) => acc.statements)[0]
          .filter((st) => st.account.type == "RESULT")[0]
      : null;
  } catch (error) {
    console.log(error);
  }

  let mobileLegend = document.createElement("table");
  // mobileLegend.style.margin = "auto";
  mobileLegend.style.width = isMobile ? "90%" : "26%";
  mobileLegend.style.cursor = "default";

  const colorSize = 15;

  const totals = getTotals(accounts);

  const totalsTables = getTotalsTables(totals, {credit: totalYear.credit,
    debit: totalYear.debit,
    result: totalYear.credit - totalYear.debit});

  const legendData = [
    {
      color: "#3366cc",
      name: "Vtas./Ing.",
      amount: totalYear ? formatNumber(totalYear.credit) : "0,00",
      table: totalsTables.income,
    },
    {
      color: "#dc3912",
      name: "Cpas./Gtos./Amort.",
      amount: totalYear ? formatNumber(totalYear.debit) : "0,00",
      table: totalsTables.outgoing,
    },
    {
      color: "#ff9900",
      name: "Resultado",
      amount: totalYear
        ? formatNumber(totalYear.credit - totalYear.debit)
        : "0,00",
      table: totalsTables.result,
    },
  ];

  legendData.forEach((d) => {
    let trColumn = document.createElement("tr");
    trColumn.style.cursor = "pointer";
    let tdColumnColor = document.createElement("td");
    tdColumnColor.style.width = colorSize * 2.5 + "px";

    let divColumnColor = document.createElement("div");
    divColumnColor.style.width = "100%";
    divColumnColor.style.height = colorSize + "px";
    divColumnColor.style.borderRadius = "1px";
    divColumnColor.style.background = d.color;

    tdColumnColor.appendChild(divColumnColor);

    tdColumnColor.style.minWidth = "2em";

    trColumn.appendChild(tdColumnColor);

    let tdColumnDescriptor = document.createElement("td");
    tdColumnDescriptor.innerHTML = d.name;
    tdColumnDescriptor.style.textIndent = ".4em";
    tdColumnDescriptor.style.maxWidth = "4em";
    tdColumnDescriptor.style.whiteSpace = "nowrap";
    tdColumnDescriptor.style.overflow = "hidden";
    tdColumnDescriptor.style.textOverflow = "ellipsis";
    tdColumnDescriptor.colSpan = 2;
    // tdColumnDescriptor.style.maxWidth = "40%";

    trColumn.appendChild(tdColumnDescriptor);



    let tdColumnAmount = document.createElement("td");
    tdColumnAmount.innerHTML =
      window.innerWidth > 320 ? `${d.amount} €` : `${d.amount}€`;
    tdColumnAmount.style.textAlign = "right";
    tdColumnAmount.style.width = "8em";

    trColumn.appendChild(tdColumnAmount);

    trColumn.addEventListener("click", () => {
      let elems = document.getElementsByClassName(`table_${d.name}`);
      for (let i = 0; i < elems.length; i++) {
        elems[i].style.display != "none"
          ? (elems[i].style.display = "none")
          : (elems[i].style.display = "");
      }
    });

    mobileLegend.appendChild(trColumn);

    d.table.forEach((tr) => {
      tr.style.display = "none";
      tr.classList.add(`table_${d.name}`);
      mobileLegend.appendChild(tr);
    });
  });

  return mobileLegend;
}

function getTotals(accounts) {
  let mainTable = document.createElement("table");
  try {
    let monthlyTotals = accounts
      .filter(
        (acc) =>
          !/31\/12\/d*/.test(acc.interval.fromDate) &&
          acc.interval.fromDate != acc.interval.toDate
      )
      .map((acc) => {
        return {
          interval: acc.interval,
          statements: acc.statements.filter(
            (st) => st.account.type == "RESULT"
          ),
        };
      });

    return monthlyTotals;
  } catch (error) {
    console.log(error);
  }
  return null;
}

function periodChooser(periodName) {
  const monthRegexp = /\s*(?<month>\d{1,2})\/(?<year>\d{2,4})\s*/;

  if (monthRegexp.test(periodName)) {
    let month = periodName.match(monthRegexp).groups.month;

    switch (month) {
      case "01":
        return "Ene.";
      case "02":
        return "Feb.";
      case "03":
        return "Mar.";
      case "04":
        return "Abr.";
      case "05":
        return "May.";
      case "06":
        return "Jun.";
      case "07":
        return "Jul.";
      case "08":
        return "Ago.";
      case "09":
        return "Sep.";
      case "10":
        return "Oct.";
      case "11":
        return "Nov.";
      case "12":
        return "Dic.";
    }
  }

  return periodName;
}

function getTotalsTables(totals, totalAmounts) {
  let ret = { income: [], outgoing: [], result: [] };

  let date;
  let i = 0;
  do {
    let elem = totals[i];

    const periodName = elem.interval.name;
    let trIncome = document.createElement("tr");
    let trOutgoing = document.createElement("tr");
    let trResult = document.createElement("tr");
    let tdAmountIncome = document.createElement("td");
    let tdAmountOutgoing = document.createElement("td");
    let tdAmountResult = document.createElement("td");

    let tdIncomePercent = document.createElement("td");
    tdIncomePercent.style.textAlign = "right";
    let tdOutgoingPercent = document.createElement("td");
    tdOutgoingPercent.style.textAlign = "right";
    let tdResultPercent = document.createElement("td");
    tdResultPercent.style.textAlign = "right";
    if (elem.statements != null && elem.statements.length != 0) {
      const credit = elem.statements[0].credit;
      const debit = elem.statements[0].debit;
      const result = credit - debit;

      tdAmountIncome.innerHTML = `${formatNumber(credit)} €`;
      tdAmountIncome.style.textAlign = "right";
      tdAmountOutgoing.innerHTML = `${formatNumber(debit)} €`;
      tdAmountOutgoing.style.textAlign = "right";
      tdAmountResult.innerHTML = `${formatNumber(result)} €`;
      tdAmountResult.style.textAlign = "right";

      let incomePercent = credit / totalAmounts.credit * 100;
      let outgoingPercent = debit / totalAmounts.debit * 100;
      let resultPercent = result / totalAmounts.result * 100;
  
      tdIncomePercent.innerHTML = `${formatNumber(incomePercent)}%`;
      
      tdOutgoingPercent.innerHTML = `${formatNumber(outgoingPercent)}%`;
      
      tdResultPercent.innerHTML = `${formatNumber(resultPercent)}%`;

    } else {
      tdAmountIncome.innerHTML = "-";
      tdAmountOutgoing.innerHTML = "-";
      tdAmountResult.innerHTML = "-";
    }

    let tdNameIncome = document.createElement("td");
    tdNameIncome.style.textIndent = ".4em";
    tdNameIncome.innerHTML = periodChooser(periodName);
    let tdNameOutgoing = document.createElement("td");
    tdNameOutgoing.innerHTML = periodChooser(periodName);
    tdNameOutgoing.style.textIndent = ".4em";
    let tdNameResult = document.createElement("td");
    tdNameResult.innerHTML = periodChooser(periodName);
    tdNameResult.style.textIndent = ".4em";


    trIncome.appendChild(document.createElement("td"));
    trIncome.appendChild(tdNameIncome);
    trIncome.appendChild(tdIncomePercent);
    trIncome.appendChild(tdAmountIncome);
    ret.income.push(trIncome);
    trOutgoing.appendChild(document.createElement("td"));
    trOutgoing.appendChild(tdNameOutgoing);
    trOutgoing.appendChild(tdOutgoingPercent);
    trOutgoing.appendChild(tdAmountOutgoing);
    ret.outgoing.push(trOutgoing);
    trResult.appendChild(document.createElement("td"));
    trResult.appendChild(tdNameResult);
    trResult.appendChild(tdResultPercent);
    trResult.appendChild(tdAmountResult);
    ret.result.push(trResult);

    date = getDateFromString(elem.interval.fromDate);
    i++;
  } while (
    i < totals.length &&
    (date.getFullYear() < new Date().getFullYear() ||
      (date.getFullYear() == new Date().getFullYear() &&
        date.getMonth() < new Date().getMonth()))
  );

  return ret;
}
