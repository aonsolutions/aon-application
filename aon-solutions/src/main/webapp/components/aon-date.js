import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../environments/environments.js';
import { AonInput } from './aon-input.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonDateUtils } from '../modules/utils/AonDateUtils.js';

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
    return [CONSTANT.VALUE];
  }

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get name() {
		return this.getAttribute(CONSTANT.NAME);
	}

	set name(name) {
		this.setAttribute(CONSTANT.NAME, name);
	}

  get value() {
    return this.getAttribute(CONSTANT.VALUE);
  }

  set value(value) {
    this.setAttribute(CONSTANT.VALUE, value);
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get readonly() {
    return this.getAttribute(CONSTANT.READONLY);
  }

  set readonly(readonly) {
    this.setAttribute(CONSTANT.READONLY, readonly);
  }

  get required() {
    return this.getAttribute(CONSTANT.REQUIRED) == CONSTANT.TRUE;
  }

  set required(required) {
    this.setAttribute(CONSTANT.REQUIRED, required);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if(CONSTANT.VALUE === name) {
      let input = this.getElement(this.INPUT);
      if(newValue && input){
        input.value = AonDateUtils.setDate(newValue);
      } else if(input && newValue === '') {
        input.value = '';
      }
   }
  }
	constructor () {
		super();
  }

	connectedCallback () {
    this.initialize();
    let ai = new AonInput();
    ai.id = this.INPUT;
    ai.description  = this.title;
    ai.autocomplete = "off";
    if(this.required) ai.required  = this.required;
    
    this.appendChild(ai);
    this.build();
    this.buildDatepicker();
	}

  initialize() {
    this.id = this.id || 'aonDate';
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

  build() {
    let input = this.getElement(this.INPUT);
    input.addIconButton('calendar_today', () => this.openDatepicker());
    input.readonly = this.isReadonly();
    // input.addEventListener(EVENT.CHANGE, () => {
    //   let date = this.parseDateStr(input.value);
    //   this.setDate(date);
    // });
    this.autoFormat();
    this.getElement(input.INPUT).style.minWidth = '125px';
  }

  autoFormat(){
    let date = this.getElement(this.INPUT);
    date.maxlength = 10;
    date.onkeypress = ({key})=>{
      if (key === "Enter")
       this.closeDatepicker();
    }

    const aonIconButton = date.querySelector(`aon-icon-button[icon="calendar_today"]`);
    if(aonIconButton){
      date.onkeyup =  (ev) =>{
        const dateString = ev.target.value;
        const regex = /(((0|1)[0-9]|2[0-9]|3[0-1])\/(0[1-9]|1[0-2])\/((19|20)\d\d))$/;
        if (regex.test(dateString) || dateString.length == 0) {
          aonIconButton.color = "#5f6365";
        } else {
          aonIconButton.color = "#E82829";
        }
      }
    }

    date.addEventListener(EVENT.INPUT, ({target})=> {
      let value = target.value;
      if (/\D\/$/.test(value)) value = value.substr(0, value.length - 3);
      const values = value.split('/').map((v)=>  v.replace(/\D/g, ''));
      if (values[0]) values[0] = this.checkValue(values[0], 31);
      if (values[1]) values[1] = this.checkValue(values[1], 12);
      const output = values.map((v, i)=> v.length == 2 && i < 2 ? v + '/' : v);
      target.value = output.join('').substr(0, 14);
      if(value.length>=14) target.blur();
    });
    
    date.addEventListener(EVENT.BLUR, ({target}) =>{
      const input = target.value;
      const values = input ? input.split('/').map((v)  => v.replace(/\D/g, '')) : [];
      let output = '';

      if (values.length === 3) {
        let year = parseInt(values[2]);
        if(values[2].length === 2) {
          let now = new Date(Date.now());
          let y = now.getFullYear() + '';
          let a = parseInt(y.substring(2,4));
          year = a >= year ? year + 2000 : year + 1900;
        }
        const month = parseInt(values[1]) - 1;
        const day = parseInt(values[0]);
        
        const d = new Date(year, month, day);
        
        if (!isNaN(d)) {          
          this.setDate(d);
          const dates = [d.getDate(), d.getMonth() + 1, d.getFullYear()];
          output = dates.map((v) =>{
            v = v.toString();
            return v.length === 1 ? '0' + v : v;
          }).join('/');
        }
      }
      target.value = output.replaceAll(" ", "");
      
      this.date = date.value;
      this.value = date.value;
      this.setDate(date.value);
    });
  }

  checkValue(str, max){
    if (str.charAt(0) !== '0' || str == '00') {
      let num = parseInt(str);
      if (isNaN(num) || num <= 0 || num > max) num = 1;
      str = num > parseInt(max.toString().charAt(0)) && num.toString().length == 1 ? '0' + num : num.toString();
    }
    return str;
  }
  

  buildDatepicker() {
    let input = this.getElement(this.INPUT);
    let div = this.getElement(input.DIV);
    let span = this.getElement(input.SPAN) || this.createElement(TAG.SPAN);
    span.id = input.SPAN;
    div.appendChild(span);

    let datepicker =  this.getElement(this.DATEPICKER) || this.createElement(TAG.DIV);
    datepicker.id = this.DATEPICKER;
    datepicker.classList.add('aonDatepicker', CSS.AON_BOX_SHADOW);
    datepicker.style.width = this.width || '250px';
    span.appendChild(datepicker);
    let datepickerHeaderId =this.DATEPICKER +"Header";
    let datepickerHeader = this.getElement(datepickerHeaderId) || this.createElement(TAG.DIV);
    datepickerHeader.style.height = '50px';
    datepickerHeader.id = datepickerHeaderId;

    let aib1 = new AonIconButton();
    aib1.id = this.DATEPICKER_PREVIOUS;
    aib1.icon = "keyboard_arrow_left";
    datepickerHeader.appendChild(aib1);

    let span1 = this.createElement(TAG.SPAN);
    span1.id = this.DATEPICKER_MONTH;
    span1.textContent = this.getMonthName();
    datepickerHeader.appendChild(span1);

    let span2 = this.createElement(TAG.SPAN);
    span2.id = this.DATEPICKER_YEAR;
    span2.textContent = this.year;
    datepickerHeader.appendChild(span2);

    let aib2 = new AonIconButton();
    aib2.id = this.DATEPICKER_NEXT;
    aib2.icon = "keyboard_arrow_right";
    datepickerHeader.appendChild(aib2);

    datepicker.appendChild(datepickerHeader);

    let previous = this.getElement(this.DATEPICKER_PREVIOUS);
    previous.style.position = 'absolute';
    previous.style.left = '0px';
    previous.style.top = '5px';
    previous.addEventListener(EVENT.CLICK, (ev) => {
      ev.stopPropagation();
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
    next.addEventListener(EVENT.CLICK, (ev) => {
      ev.stopPropagation();
      ev.preventDefault();
      this.nextMonth();
    });

    let datepickerDays = this.createElement(TAG.DIV);
    let table = this.createElement(TAG.TABLE);
    table.id = this.DATEPICKER_DAYS;
    table.style.width = "100%";
    datepickerDays.appendChild(table);

    datepicker.appendChild(datepickerDays);
    this.buildCalendar();

    document.addEventListener(EVENT.CLICK, function(event) {
      let isClickInside = input.contains(event.target);

      if(!isClickInside){
        if(datepicker.classList.contains('is-visible')){
          datepicker.classList.remove('is-visible');
        }
      }
    });
  }

  buildCalendar() {
    this.clearElementById(this.DATEPICKER_DAYS);
    let datepickerDaysTable = this.getElement(this.DATEPICKER_DAYS);
    let trDays = this.createElement(TAG.TR);
    datepickerDaysTable.appendChild(trDays);
    for(let d = 0; d < 7; d++) {
      let td = this.createElement(TAG.TD);
      td.className = 'aonDatepickerDays aonDatepickerDaysTitle';
      td.innerHTML = this.getDayName(d);
      trDays.appendChild(td);
    }


    for(let i = 0; i < 6; i++) {
      let tr = this.createElement(TAG.TR);
      datepickerDaysTable.appendChild(tr);
      for(let j = 0; j < 7; j++) {
        let td = this.createElement(TAG.TD);
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

      td.addEventListener(EVENT.CLICK, () => {
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
      case 0: return MSG.JANUARY.toUpperCase();
      case 1: return MSG.FEBRUARY.toUpperCase();
      case 2: return MSG.MARCH.toUpperCase();
      case 3: return MSG.APRIL.toUpperCase();
      case 4: return MSG.MAY.toUpperCase();
      case 5: return MSG.JUNE.toUpperCase();
      case 6: return MSG.JULY.toUpperCase();
      case 7: return MSG.AUGUST.toUpperCase();
      case 8: return MSG.SEPTEMBER.toUpperCase();
      case 9: return MSG.OCTOBER.toUpperCase();
      case 10: return MSG.NOVEMBER.toUpperCase();
      case 11: return MSG.DECEMBER.toUpperCase();
      default: return MSG.JANUARY.toUpperCase();
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
    const datePicker = this.getElement(this.DATEPICKER);
    if(datePicker){
      if(!this.isReadonly()) {
        datePicker.classList.add('is-visible');
      }
      if(this.isMobile()){
        datePicker.classList.add('is-mobile');
      }
    }
  }

  closeDatepicker() {
    let div = this.getElement(this.DATEPICKER);
    if(div && div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }

  isSameDate(date) {
    return date.getDate() === this.date.getDate()
      && date.getMonth() === this.date.getMonth()
      && date.getFullYear() === this.date.getFullYear();
  }

  setDate(date) {
    let input = this.getElement(this.INPUT);
    if(date instanceof Date) {
      this.date = date;
      this.day = this.date.getDate();
      this.month = this.date.getMonth();
      this.year = this.date.getFullYear();
      this.value = this.year + '-' + (this.addZero(this.month + 1)) + '-' + this.addZero(this.day);
      input.value = this.addZero(this.day) + '/' + (this.addZero(this.month + 1)) + '/' + this.year;
      this.buildCalendar();
    } else if(date){
      this.date = new Date(Date.parse(date));
      this.day = this.date.getDate();
      this.month = this.date.getMonth();
      this.year = this.date.getFullYear();
      this.value = this.year + '-' + (this.addZero(this.month + 1)) + '-' + this.addZero(this.day);
      input.value = this.addZero(this.day) + '/' + (this.addZero(this.month + 1)) + '/' + this.year;
      this.buildCalendar();
    } else this.date = date;

    this.dispatchEvent(new CustomEvent(EVENT.CHANGE, {detail: this.date}));
  }

  addZero(d){
    return d.toString().padStart(2, "0");
  }

  addError(e) {

  }

  focus() {
    this.getElement(this.INPUT).focus();
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.UNDEFINED !== this.getAttribute(CONSTANT.READONLY) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  disabledDate(b){
    let input = this.getElement(this.INPUT);
    if(input) {
      input.readonly = b;
      input.disabled = b;
    }
  }

  setDisabled(b){
    this.disabledDate(b);
    this.readonly = "true";
  }

  setVisible(visible){
    if(visible){
      this.setAttribute(CONSTANT.HIDDEN, visible);
    } else {
      this.removeAttribute(CONSTANT.HIDDEN);
    }
  }

  // parseDateStr(dateStr) {
  //     if(dateStr.includes('/')){
  //       let dateArr = dateStr.split('/');
  //       let a = dateArr[0].length === 1 
  //           ? '0' + dateArr[0] : dateArr[0];
  //       let b = dateArr[1].length === 1 
  //           ? '0' + dateArr[1] : dateArr[1];
  //       let c = dateArr[2];
  //       dateStr = a + b + c;       
  //     } 
      
  //     if(dateStr.includes('-')){
  //       let dateArr = dateStr.split('-');
  //       let a = dateArr[0].length === 1 
  //           ? '0' + dateArr[0] : dateArr[0];
  //       let b = dateArr[1].length === 1 
  //           ? '0' + dateArr[1] : dateArr[1];
  //       let c = dateArr[2];
  //       dateStr = a + b + c;       
  //     } 
 
  //     let day = dateStr.substring(0, 2);
  //     let month = dateStr.substring(2, 4);
  //     let year = dateStr.substring(4);

  //     if(Number(month) > 12 || Number(day) > 31 || year.length > 4){
  //       return this.date;
  //     } else {
  //       let d = month + '/' + day + '/' + year;
  //       return new Date(d);
  //     }
  // }

  clear(){
    
  }

  getValue() {
    return this.value;
  }
  

}
if(!window.customElements.get('aon-date')){
  window.customElements.define('aon-date',  AonDate);
}
