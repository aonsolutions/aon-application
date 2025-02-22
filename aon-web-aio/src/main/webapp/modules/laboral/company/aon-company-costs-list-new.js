import { AonElement } from "../../../components/AonElement.js";
import { formatNumber, isEmptyObject, sortBy, waitEl, setValueName } from "../../../services/utils.js";
import { getWorkplaceCCCs, getCompanyCosts, getCompanyCostsExcel, getPeriodLaboral } from "../../../services/service.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../timecontrol/signinEnums.js";
import { PAYROLL_FILTER, PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";

let chartCanva;
let cardFilter;

export class AonCompanyCostsListNew extends AonElement {
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

  constructor(cardFilterIn) {
    super();
    cardFilter = cardFilterIn;
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST;
    this.TABLE_ID = this.id + "Table";
    this.getApplication().addToolbarTitle(MSG.LABORAL_COSTS);
  }

  async build() {
    this.buildToolbar();
    await this.getTable();

	const buildEvent = new CustomEvent(EVENT.BUILD, { panel: this });
	this.dispatchEvent(buildEvent);

  }

  buildToolbar() {
    this.getApplication().removeToolbarOptions();
    if (!this.isMobile()) {
      this.getApplication().addToolbarOption2(SigninSidenav.EXCEL, () =>
        this.getCompanyCostsExcel()
      );
    }
    this.buildToolbarSearch();
  }

  buildToolbarSearch() {
    let btnSearch = this.getApplication().addSearchOption();
    btnSearch.disabled = true;
    const searchValueFn = ({ detail }) => {
      if (detail) getApplicationParent().setDataFilter(detail);
      paintCompanyCostPieChart();
    };

    btnSearch.addEventListener(EVENT.SEARCH_NEW, searchValueFn);

    btnSearch.buildOptionsFilter([PAYROLL_FILTER[0], ...PRESENCE_FILTER]); //INPUTS
    this.searchValueDefault();
  }

  async searchValueDefault() {
    // ----------WORKPLACES ------------
    let workplaces = await getWorkplaceCCCs();
    let workplaceEl = this.getElement("workplace");
    workplaceEl.options = JSON.stringify(
      workplaces.map(({ workplace }) => ({
        name: workplace.description,
        value: workplace.id,
      }))
    );
    // ----------WORKPLACES END ------------

    //------------------PERIOD---------
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriodLaboral());
    periodEl.addEventListener(EVENT.CHANGE, ({ detail }) => {
      if (detail) {
        const { startDate, endDate } = detail;
        setValueName("startDate", startDate);
        setValueName("endDate", endDate);
      }
    });
    // ----------PERIOD END ------------
    let startDateEl = this.getElement("startDate");
    startDateEl.addEventListener(
      EVENT.CHANGE,
      () => (periodEl.value = "personalized")
    );
    let endDateEl = this.getElement("endDate");
    endDateEl.addEventListener(
      EVENT.CHANGE,
      () => (periodEl.value = "personalized")
    );
  }

  async getTable() {
    await waitEl("#" + this.getApplication().id);
    this.getApplication().startLoader();
    await this.paintPieChar();
    this.getApplication().stopLoader();
  }

  async paintPieChar() {
    const main = document.createElement(TAG.DIV);
    main.style.display = "flex";
    main.style.flexDirection = "column";
    main.style.gap = "2rem";
    main.style.alignItems = "center";
    main.style.textAlign = "center";
    main.style.marginTop = "2rem";
    this.appendChild(main);

    //-----TITLE--------------
    let divTitle = this.createElement(TAG.DIV);
    divTitle.id = this.id + "titleDiv";
    divTitle.classList.add("aonCompanyCostListDivTitle");
    main.appendChild(divTitle);

    //-----CHART--------------
    let canvasDiv = this.createElement(TAG.DIV);
    canvasDiv.id = this.id + "canvasDiv";
    canvasDiv.style.display = "flex";
    canvasDiv.style.justifyContent = "center";
    canvasDiv.style.maxHeight = "20rem";
    canvasDiv.style.height = "100%";
    main.appendChild(canvasDiv);

    let canvasChart = this.createElement(TAG.CANVAS);
    canvasChart.id = this.id + "Chart";
    canvasDiv.appendChild(canvasChart);

    //-----LEYEND--------------
    let leyendDiv = this.createElement(TAG.DIV);
    leyendDiv.id = this.id + "Leyend";
    leyendDiv.style.display = "flex";
    leyendDiv.style.flexDirection = "column";
    leyendDiv.style.gap = "1rem";
    main.appendChild(leyendDiv);
  }

  async getCompanyCostsExcel() {
    let filter = this.getApplicationParent()._filter;
    this.getApplication().startLoading();
    await getCompanyCostsExcel({ ...filter }).catch((e) => this.showToast(e));
    this.getApplication().stopLoading();
  }
}

const paintCompanyCostPieChart = async () => {
  try {
    const data = await getData();

    let startDate = new Date();
    let endDate = new Date();
    let title = MSG.LABORAL_COSTS;
    let workplaceText = "";
    let total = 0;
    
    if(data && data.length > 0 ){
      startDate = new Date(data[0].startDate);
      endDate   = new Date(data[0].endDate);
      
      const sumEnterpriseSs = data.reduce((sum,key)=> sum + (parseFloat(key.enterpriseSS) - parseFloat(key.bonuses)),0); 
      const sumEmployeeSs = data.reduce((sum,key)=>sum + (parseFloat(key.employeeSS) + parseFloat(key.otherDeductions)), 0); 
      const importIrpf = data.reduce((sum,key)=>sum + parseFloat(key.irpf), 0); 
      const totalLiquid = data.reduce((sum,key)=>sum + parseFloat(key.liquid), 0); 
      const totalSS = sumEnterpriseSs + sumEmployeeSs;
      total = totalSS + importIrpf + totalLiquid;

      const dataChart = {
        labels: [
          // 'SS Empresa',
          // 'SS Empleado',
          'Total SS',
          'Total IRPF',
          'Total Nominas'
        ],
        datasets: [{
          // label: 'My First Dataset',
          data: [/*sumEnterpriseSs, sumEmployeeSs,*/ totalSS, importIrpf, totalLiquid],
          backgroundColor: [
            // '#0051C6',
            // '#db4437',
            'black',
            '#B3B3B3',
            '#5e97f6'
          ],
          hoverOffset: 4
        }]
      };

      const config = {
        type: 'pie',
        plugins: [ChartDataLabels],
        data: dataChart,
        options: {
          plugins: {
            legend: {
                display: false // This hides all text in the legend and also the labels.
            },
            datalabels: {
              display: 'auto',
              color: 'white',
              align: 'end',
              font: {
                size: 14,
                weight: 'bold'
              },
              formatter: (value, ctx) => {
                const total = ctx.chart.getDatasetMeta(0).total;
                let percentage = (value * 100 / total).toFixed(2) + "%";
                return percentage;
              },
            }
          }
        }
      }

      let canvasChart = document.getElementById("aon-company-costs-listChart");

      if(chartCanva){
        chartCanva.destroy();
      }

      chartCanva = new Chart(canvasChart, config);

      createLeyend([
        {color: 'black', title: 'Total SS', amount: totalSS, breakdown:[
          {color: '#0051C6', title: 'SS Empresa', amount: sumEnterpriseSs},
          {color: '#db4437', title: 'SS Empleado', amount: sumEmployeeSs},
        ]},
        {color: '#B3B3B3', title: 'Total IRPF', amount: importIrpf},
        {color: '#5e97f6', title: 'Total Nominas', amount: totalLiquid},
      ]);
    }

    let startDateText = AonDateUtils.getMonthYear(startDate),
    endDateText = AonDateUtils.getMonthYear(endDate);
    
    if(startDateText === endDateText){
      title = title + " "+ startDateText;
    } else {
      title = `${title} ${startDateText} - ${endDateText}`;
    }

    title = `${title}<br> ${workplaceText} <span class="aonCompanyCostListNewTitle";">${formatNumber(total, 2, "EUR")}<span>`;

    let divTitle = document.getElementById("aon-company-costs-listtitleDiv");
    divTitle.innerHTML = title;
  } catch (error) {
    console.log(error);
  }
}

const createLeyend = (leyends) => {
  let leyendDiv =  document.getElementById("aon-company-costs-listLeyend");
  leyendDiv.innerHTML = "";

  for (let index = 0; index < leyends.length; index++) {
    const leyend = leyends[index];

    let row = createElement(TAG.DIV);
    row.style.display = "flex";
    row.style.gap = "1rem";
    leyendDiv.appendChild(row);
  
    let color = createElement(TAG.DIV);
    color.style.width = "15px"
    color.style.height = "15px"
    color.style.borderRadius = "50%"
    color.style.backgroundColor = leyend.color;
    row.appendChild(color);
  
    let title = createElement(TAG.DIV);
    title.style.minWidth = "7rem";
    title.style.textAlign = "left";
    title.innerHTML = leyend.title;
    row.appendChild(title);
  
    let amount = createElement(TAG.DIV);
    amount.style.minWidth = "7rem";
    amount.style.textAlign = "right";
    amount.innerHTML = formatNumber(leyend.amount, 2, "EUR");
    row.appendChild(amount);

    if(leyend.breakdown){

      for (let indexBreakdown = 0; indexBreakdown < leyend.breakdown.length; indexBreakdown++) {
        const leyendBreakdown = leyend.breakdown[indexBreakdown];

        let row = createElement(TAG.DIV);
        row.style.display = "flex";
        row.style.gap = "1rem";
        row.style.marginLeft = "2rem";
        leyendDiv.appendChild(row);
      
        // let color = createElement(TAG.DIV);
        // color.style.width = "15px"
        // color.style.height = "15px"
        // color.style.borderRadius = "50%"
        // color.style.backgroundColor = leyend.color;
        // row.appendChild(color);
      
        let title = createElement(TAG.DIV);
        title.style.minWidth = "7rem";
        title.style.textAlign = "left";
        title.innerHTML = leyendBreakdown.title;
        row.appendChild(title);
      
        let amount = createElement(TAG.DIV);
        amount.style.minWidth = "7rem";
        amount.style.textAlign = "right";
        amount.innerHTML = formatNumber(leyendBreakdown.amount, 2, "EUR");
        row.appendChild(amount);
      }

    }
  }

  let button = createElement(TAG.BUTTON);
  button.className = CSS.AON_BUTTON;
  button.id = `aon-company-costs-listNomina`;
  button.innerHTML = MSG.VIEW_PAYROLLS;
  leyendDiv.appendChild(button);
 
   button.onclick = () => {
    document.getElementById("aonLaboralSidenavPaysheet").click();
  }
}

const createElement = (tag, id, className) => {
  let el = document.createElement(tag);
  if(id) el.id = id;
  if(className) el.className = className;
  return el;
}

const getData = async () => {
  getApplication().startLoader();
  let data = [];
  try {
    let filter = null;
    try {
      filter = { ...getApplicationParent()._filter };
    } catch (error) {}

    if(cardFilter){
      filter = {};
      filter.period = cardFilter.period;
      filter.startDate = cardFilter.startDate;
      filter.endDate = cardFilter.endDate;
      filter.event = "click";
      filter.search = "";
      cardFilter = undefined;
    }

    let datos = await getCompanyCosts(filter);
    if (!isEmptyObject(datos)) {
      if (datos[0] && datos[0].startDate) {
        changeFilterTime({
          startDate: datos[0].startDate,
          endDate: datos[0].endDate,
          value: "personalized",
        });
      }
      sortBy(datos, "employee", "asc").map((resp) => {
        const lettersType = getTypeSalaryText(
          resp.salaryType
        );
        const lettersHtml = `<div class="profile-letters ${lettersType.color}">${lettersType.typeReduce}</div>`;
        const obj = {
          ...resp,
          lettersHtml,
          name: resp.employee,
          salaryType: lettersType.type,
          workplaceName: resp.workplace,
        };
        data.push(obj);
      });
    } else {
      changeFilterTime();
    }
  } catch (e) {
    console.log(e);
  }
  getApplicationParent().changeFilter();
  getApplication().stopLoader();
  return data;
}

const getApplication = () => {
  return document.querySelector(TAG.AON_APPLICATION);
}

const getApplicationParent = () => {
  return getApplication() ? getApplication().getParent() : null;
}

const changeFilterTime = (obj = undefined) => {
  const value = !obj ? getPeriodLaboral("last_month") : obj;
  getApplicationParent()._filter = {
    period: value.value,
    startDate: value.startDate,
    endDate: value.endDate,
  };
}

const getTypeSalaryText = (type) => {
  let obj = { color: "", type: "", typeReduce: "" };
  switch (type) {
    case "SALARY":
      obj.type = "NOMINA";
      break;
    case "EXTRA":
      obj.type = "EXTRA";
      obj.color = "in";
      break;
    case "SETTLE":
      obj.type = "FINIQUITO";
      obj.color = "fin";
      break;
    case "DELAY":
      obj.type = "ATRASOS";
      obj.color = "pause";
      break;
  }
  if (obj.type) obj.typeReduce = obj.type.toString().substr(0, 1);
  return obj;
}

export { paintCompanyCostPieChart }
window.customElements.define("aon-company-costs-list-new", AonCompanyCostsListNew);
