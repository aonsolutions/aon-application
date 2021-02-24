import {AonElement} from './AonElement.js';

import './aon-input.js';
import './aon-icon-button.js';

import {setDate} from '../services/utils.js';

export class AonDate extends AonElement {

  date;
  day;
  month;
  year;

  INPUT;
  DATEPICKER;
  DATEPICKER_PREVIOUS;
  DATEPICKER_NEXT;
  DATEPICKER_MONTH;
  DATEPICKER_YEAR;
  DATEPICKER_DAYS;

  static get observedAttributes() {
    return ['value'];
  }

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

  get name() {
		return this.getAttribute('name');
	}

	set name(name) {
		this.setAttribute('name', name);
	}

  get value() {
    return this.getAttribute('value');
  }

  set value(value) {
    this.setAttribute('value', value);
  }

  get title() {
    return this.getAttribute('title');
  }

  set title(title) {
    this.setAttribute('title', title);
  }

  get readonly() {
    return this.getAttribute('readonly');
  }

  set readonly(readonly) {
    this.setAttribute('readonly', readonly);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if('value' === name) {
      let input = this.getElement(this.INPUT);
      if(newValue && input)
        input.value = setDate(newValue);
      else if(input && newValue === '') input.value = '';
   }
  }
	constructor () {
		super();
    this.INPUT = this.id + 'Input';
    this.DATEPICKER = this.id + 'Datepicker';
    this.DATEPICKER_PREVIOUS = this.DATEPICKER + 'Previous';
    this.DATEPICKER_NEXT = this.DATEPICKER + 'Next';
    this.DATEPICKER_MONTH = this.DATEPICKER + 'Month';
    this.DATEPICKER_YEAR = this.DATEPICKER + 'Year';
    this.DATEPICKER_DAYS = this.DATEPICKER + 'Days';

    this.date = new Date(Date.now());
    this.day = this.date.getDate();
    this.month = this.date.getMonth();
    this.year = this.date.getFullYear();
  }

	connectedCallback () {
		this.innerHTML = `
      <aon-input id="${this.INPUT}" description="${this.title}"></aon-input>
		`;
    this.build();
    this.buildDatepicker();
	}

  build() {
    let input = this.getElement(this.INPUT);
    input.setAttribute('readonly', true);
    input.addIconButton('calendar_today', () => this.openDatepicker());
    this.getElement(input.INPUT).style.minWidth = '125px';
  }

  buildDatepicker() {
    let input = this.getElement(this.INPUT);
    let div = this.getElement(input.DIV);
    let span = this.getElement(input.SPAN) || this.createElement('span');
    span.id = input.SPAN;
    div.appendChild(span);

    let datepicker =  this.getElement(this.DATEPICKER) || this.createElement('div');
    datepicker.id = this.DATEPICKER;
    datepicker.className = 'aonDatepicker';
    datepicker.style.width = '250px';
    span.appendChild(datepicker);
    let datepickerHeaderId =this.DATEPICKER +"Header";
    let datepickerHeader = this.getElement(datepickerHeaderId) || this.createElement('div');
    datepickerHeader.style.height = '50px';
    datepickerHeader.id = datepickerHeaderId;

    datepickerHeader.innerHTML = `
    <aon-icon-button id="${this.DATEPICKER_PREVIOUS}" icon="keyboard_arrow_left"></aon-icon-button>
    <span id="${this.DATEPICKER_MONTH}"> ${this.getMonthName()} </span>
    <span id="${this.DATEPICKER_YEAR}"> ${this.year} </span>
    <aon-icon-button id="${this.DATEPICKER_NEXT}" icon="keyboard_arrow_right"></aon-icon-button>
  `;

    datepicker.appendChild(datepickerHeader);

    let previous = this.getElement(this.DATEPICKER_PREVIOUS);
    previous.style.position = 'absolute';
    previous.style.left = '0px';
    previous.style.top = '5px';
    previous.addEventListener('click', (ev) => {
      ev.preventDefault();
      this.previousMonth();
    });

    let datepickerMonth = this.getElement(this.DATEPICKER_MONTH);
    datepickerMonth.style.position = 'absolute';
    datepickerMonth.style.left = '50px';
    datepickerMonth.style.top = '15px';

    let datepickerYear = this.getElement(this.DATEPICKER_YEAR);
    datepickerYear.style.position = 'absolute';
    datepickerYear.style.left = '150px';
    datepickerYear.style.top = '15px';


    let next = this.getElement(this.DATEPICKER_NEXT);
    next.style.position = 'absolute';
    next.style.right = '0px';
    next.style.top = '5px';
    next.addEventListener('click', (ev) => {
      ev.stopPropagation();
      this.nextMonth();
    });

    let datepickerDays = this.createElement('div');
    datepickerDays.innerHTML = `
      <table id="${this.DATEPICKER_DAYS}" style="width:100%"></table>
    `;

    datepicker.appendChild(datepickerDays);
    this.buildCalendar();

    document.addEventListener('click', function(event) {
      let isClickInside = input.contains(event.target);

      if(!isClickInside){
        if(datepicker.classList.contains('is-visible')){
          datepicker.classList.remove('is-visible');
        }
      }
    });
  }

  buildCalendar() {
    this.clearElement(this.DATEPICKER_DAYS);
    let datepickerDaysTable = this.getElement(this.DATEPICKER_DAYS);
    let trDays = this.createElement('tr');
    datepickerDaysTable.appendChild(trDays);
    for(let d = 0; d < 7; d++) {
      let td = this.createElement('td');
      td.className = 'aonDatepickerDays aonDatepickerDaysTitle';
      td.innerHTML = this.getDayName(d);
      trDays.appendChild(td);
    }


    for(let i = 0; i < 6; i++) {
      let tr = this.createElement('tr');
      datepickerDaysTable.appendChild(tr);
      for(let j = 0; j < 7; j++) {
        let td = this.createElement('td');
        td.className = 'aonDatepickerDays'
        td.id = this.DATEPICKER_DAYS + i + j;
        tr.appendChild(td);
      }
    }

    let line = 0;
    let day = 1;
    let date = new Date(this.year, this.month, day);

    while(date.getMonth() === this.month) {
      let actDate = date;
      let pos = (date.getDay() - 1 < 0) ? 6 : date.getDay() - 1;
      let td = this.getElement(this.DATEPICKER_DAYS + line + pos);
      td.style.height = '34px';
      td.style.borderRadius = '50%';
      td.style.cursor = 'pointer';
      if(this.isSameDate(date)){
        td.style.backgroundColor = '#002469';
        td.style.color = 'white';
      }
      td.innerHTML = date.getDate();
      td.addEventListener('mouseover', () => {
        if(!this.isSameDate(actDate))
          td.style.backgroundColor = '#f1f1f1';
      });

      td.addEventListener('mouseleave', () => {
        if(!this.isSameDate(actDate))
          td.style.backgroundColor = 'transparent';
      });

      td.addEventListener('click', () => {
        this.setDate(actDate);
        this.closeDatepicker();
      });

      if(pos === 6) {
        line = line + 1;
      }
      day = day + 1;
      date = new Date(this.year, this.month, day);
    }
  }

  getMonthName() {
    switch (this.month) {
      case 0: return 'ENERO';
      case 1: return 'FEBRERO';
      case 2: return 'MARZO';
      case 3: return 'ABRIL';
      case 4: return 'MAYO';
      case 5: return 'JUNIO';
      case 6: return 'JULIO';
      case 7: return 'AGOSTO';
      case 8: return 'SEPTIEMBRE';
      case 9: return 'OCTUBRE';
      case 10: return 'NOVIEMBRE';
      case 11: return 'DICIEMBRE';
      default: return 'ENERO';
    }
  }

  getDayName(d) {
    switch (d) {
      case 0: return 'L';
      case 1: return 'M';
      case 2: return 'X';
      case 3: return 'J';
      case 4: return 'V';
      case 5: return 'S';
      case 6: return 'D';
    }
  }

  previousMonth() {
    if(this.month === 0) {
       this.month = 11;
       this.year = this.year- 1;
    } else {
      this.month = this.month- 1;
    }
    this.getElement(this.DATEPICKER_MONTH).innerHTML = this.getMonthName();
    this.getElement(this.DATEPICKER_YEAR).innerHTML = this.year;
    this.buildCalendar();
  }

  nextMonth() {
    if(this.month === 11) {
       this.month = 0;
       this.year = this.year+ 1;
    } else {
      this.month = this.month + 1;
    }
    this.getElement(this.DATEPICKER_MONTH).innerHTML = this.getMonthName();
    this.getElement(this.DATEPICKER_YEAR).innerHTML = this.year;
    this.buildCalendar();
  }

  openDatepicker() {
    if(!this.hasAttribute('readonly')) {
      this.getElement(this.DATEPICKER).classList.add('is-visible');
    }
  }

  closeDatepicker() {
    let div = this.getElement(this.DATEPICKER);
    if(div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }

  isSameDate(date) {
    return date.getDate() === this.date.getDate()
      && date.getMonth() === this.date.getMonth()
      && date.getFullYear() === this.date.getFullYear();
  }

  setDate(date) {
    if(date instanceof Date) {
      this.date = date;
      this.day = this.date.getDate();
      this.month = this.date.getMonth();
      this.year = this.date.getFullYear();
      this.value = this.year + '-' + (this.addZero(this.month + 1)) + '-' + this.addZero(this.day);
      this.getElement(this.INPUT).value = this.addZero(this.day) + '/' + (this.addZero(this.month + 1)) + '/' + this.year;
      this.buildCalendar();
    } else if(date){
      this.date = new Date(Date.parse(date));
      this.day = this.date.getDate();
      this.month = this.date.getMonth();
      this.year = this.date.getFullYear();
      this.value = this.year + '-' + (this.addZero(this.month + 1)) + '-' + this.addZero(this.day);
      this.getElement(this.INPUT).value = this.addZero(this.day) + '/' + (this.addZero(this.month + 1)) + '/' + this.year;
      this.buildCalendar();
    }
    this.dispatchEvent(new CustomEvent('change', {detail: this.date}));
  }

  addZero(d){
    if (d <= 9) d = d.toString().padStart(2, "0");
    return d;
  }
}

window.customElements.define('aon-date',  AonDate);
