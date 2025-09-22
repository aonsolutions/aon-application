import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonNewInput } from './aon-new-input.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonDateUtils } from '../modules/utils/AonDateUtils.js';

export class AonNewDate extends AonNewInput {
  date;
  day;
  month;
  year;
  today = new Date();
  INPUT;
  SPAN;
  DATEPICKER;
  DATEPICKER_PREVIOUS;
  DATEPICKER_NEXT;
  DATEPICKER_MONTH;
  DATEPICKER_YEAR;
  DATEPICKER_DAYS;
  startYear;
  YEARPICKER;
  MOTHPICKER;

  connectedCallback () {
    this.initialize();
    this.build();
    this.buildDate();
    this.buildDatepicker();
  }

  initialize() {
    super.initialize();
    this.id = this.id || 'aonDate';
    this.DATEPICKER = this.id + 'Datepicker';
    this.SPAN = this.DATEPICKER + 'Span';
    this.DATEPICKER_PREVIOUS = this.DATEPICKER + 'Previous';
    this.DATEPICKER_NEXT = this.DATEPICKER + 'Next';
    this.DATEPICKER_MONTH = this.DATEPICKER + 'Month';
    this.DATEPICKER_YEAR = this.DATEPICKER + 'Year';
    this.DATEPICKER_DAYS = this.DATEPICKER + 'Days';
	this.YEARPICKER = this.DATEPICKER + 'YearPicker';
	this.MONTHPICKER = this.MONTHPICKER + 'MonthPicker';
    this.getDate();
    // Si no tiene date, cogemos la fecha de hoy para montar el calendario
    this.day   = this.date ? this.date.getDate()    : this.today.getDate();
    this.month = this.date ? this.date.getMonth()   : this.today.getMonth();
    this.year  = this.date ? this.date.getFullYear(): this.today.getFullYear();
	this.startYear = this.date ? this.date.getFullYear(): this.today.getFullYear();
	this.startYear = this.startYear - (this.startYear % 16);
    this.maxlength = 10;
  }

  getDate(){
    // Si le pasamos un date
    const dateAttr = this.getAttribute('date');
    if (dateAttr && dateAttr.trim() !== ''){
      // Si tiene fecha pasada
      const parsedDate = new Date(dateAttr);
      if (!isNaN(parsedDate.getTime())) {
        this.date = parsedDate;
      }
    } else if (dateAttr === null || dateAttr.trim() !== ''){
      // Si no se le pasa el valor date
      this.date  = this.date || this.today;
    }
  }

  buildDate() {
    let rootDiv = this.getElement(this.ROOT);
    rootDiv.style.minWidth = '75px';
    this.addIcon(MATERIAL_ICONS.CALENDAR_TODAY, undefined, () => this.openDatepicker());
  }

  onBlur = ({target}) => {
    this.checkRequired();

    const input = target.value || "";
    if (input) {
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

      this.dispatchEvent(new Event(EVENT.BLUR));
    } else {
      target.value = "";
    }
  };

  onInput = ({target}) => {
    let value = target.value || "";
    if (value) {
      if (/\D\/$/.test(value)) value = value.substr(0, value.length - 3);
      const values = value.split('/').map((v) => v.replace(/\D/g, ''));
      if (values[0]) values[0] = this.checkValue(values[0], 31);
      if (values[1]) values[1] = this.checkValue(values[1], 12);
      const output = values.map((v, i) => v.length === 2 && i < 2 ? v + '/' : v);
      target.value = output.join('').substr(0, 14);
      if (value.length >= 14) target.blur();
    } else {
      target.value = "";
    }
  };

  checkValue(str, max){
    if (str.charAt(0) !== '0' || str == '00') {
      let num = parseInt(str);
      if (isNaN(num) || num <= 0 || num > max) num = 1;
      str = num > parseInt(max.toString().charAt(0)) && num.toString().length == 1 ? '0' + num : num.toString();
    }
    return str;
  }
  
  buildDatepicker() {
    let rootDiv = this.getElement(this.ROOT);

    let span = this.createElement(TAG.SPAN);
    span.id = this.SPAN;
    rootDiv.appendChild(span);

    let datepicker =  this.getElement(this.DATEPICKER) || this.createElement(TAG.DIV);
    datepicker.id = this.DATEPICKER;
    datepicker.classList.add('aonDatepicker', CSS.AON_BOX_SHADOW);
    if(this.width)
      datepicker.style.width = this.width;
    span.appendChild(datepicker);
    let datepickerHeaderId =this.DATEPICKER +"Header";
    let datepickerHeader = this.getElement(datepickerHeaderId) || this.createElement(TAG.DIV);
    datepickerHeader.id = datepickerHeaderId;
    datepickerHeader.className = "date-picker-title";

    let aib1 = new AonIconButton();
    aib1.id = this.DATEPICKER_PREVIOUS;
    aib1.className = "date-picker-title-icon";
    aib1.icon = "keyboard_arrow_left";
    datepickerHeader.appendChild(aib1);

    aib1.addEventListener(EVENT.CLICK, (ev) => {
      ev.stopPropagation();
      ev.preventDefault();
      this.previousMonth();
    });

    let span1 = this.createElement(TAG.SPAN);
    span1.id = this.DATEPICKER_MONTH;
    span1.textContent = this.getMonthName();
    datepickerHeader.appendChild(span1);

    let span2 = this.createElement(TAG.SPAN);
    span2.id = this.DATEPICKER_YEAR;
    span2.textContent = this.year;
    datepickerHeader.appendChild(span2);
	
	span1.addEventListener(EVENT.CLICK, () => {
		this.showHidePicker(this.MONTHPICKER);
	});
	
	span2.addEventListener(EVENT.CLICK, () => {
		this.showHidePicker(this.YEARPICKER);
	});
	
	let monthPicker = this.buildMonthPicker();
	datepickerHeader.appendChild(monthPicker);
	
	let yearPicker = this.buildYearPicker();
	datepickerHeader.appendChild(yearPicker);

    let aib2 = new AonIconButton();
    aib2.id = this.DATEPICKER_NEXT;
    aib2.className = "date-picker-title-icon";
    aib2.icon = "keyboard_arrow_right";
    datepickerHeader.appendChild(aib2);

    aib2.addEventListener(EVENT.CLICK, (ev) => {
      ev.stopPropagation();
      ev.preventDefault();
      this.nextMonth();
    });

    datepicker.appendChild(datepickerHeader);

    let datepickerDays = this.createElement(TAG.DIV);
    let table = this.createElement(TAG.TABLE);
    table.id = this.DATEPICKER_DAYS;
    table.style.width = "100%";
    datepickerDays.appendChild(table);

    datepicker.appendChild(datepickerDays);
    this.buildCalendar();

    document.addEventListener(EVENT.CLICK, (event) => {
      let isClickInside = this.contains(event.target);
      if(!isClickInside){
        if(datepicker.classList.contains('is-visible')){
          datepicker.classList.remove('is-visible');
        }
      }
    });
	this.renderYears();
	this.renderMonths();
  }

  buildCalendar() {
    this.clearElementById(this.DATEPICKER_DAYS);
    let datepickerDaysTable = this.getElement(this.DATEPICKER_DAYS);
    let trDays = this.createElement(TAG.TR);
    datepickerDaysTable.appendChild(trDays);
    for(let d = 0; d < 7; d++) {
      let td = this.createElement(TAG.TD);
      td.className = 'aonDatepickerDays aonDatepickerDaysTitle';
      let innerDiv = this.createElement(TAG.DIV);
      innerDiv.className = 'day-content-title';
      innerDiv.innerHTML = this.getDayName(d);

      td.appendChild(innerDiv);
      trDays.appendChild(td);
    }

    for(let i = 0; i < 6; i++) {
      let tr = this.createElement(TAG.TR);
      datepickerDaysTable.appendChild(tr);
      for(let j = 0; j < 7; j++) {
        let td = this.createElement(TAG.TD);
        td.className = 'aonDatepickerDays';
        td.id = this.DATEPICKER_DAYS + i + j;

        let innerDiv = this.createElement(TAG.DIV);
        innerDiv.className = 'day-content';

        td.appendChild(innerDiv);
        tr.appendChild(td);
      }
    }

    let line = 0;
    let day  = 1;
    let date = new Date(this.year, this.month, day);

    while(date.getMonth() === this.month) {
      let actDate = date;
      let pos = (date.getDay() - 1 < 0) ? 6 : date.getDay() - 1;
      let td = this.getElement(this.DATEPICKER_DAYS + line + pos);
      // Crear el div contenedor
      let innerDiv = document.createElement(TAG.DIV);
      innerDiv.className = 'day-content';  // La clase para aplicar flex y estilos

      // Poner la fecha como texto dentro del div
      innerDiv.textContent = date.getDate();

      // Limpiar contenido anterior y a�adir el div al td
      td.innerHTML = '';
      td.appendChild(innerDiv);

      if (this.isSameDate(date)) {
        innerDiv.classList.add('day-selected');
      } else {
        innerDiv.classList.remove('day-selected');
      }

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

  getMonthName(value) {
    switch (value != undefined ? value : this.month) {
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
    // Tenga en cuenta la fecha de hoy si no se le pasa datos
    const reference = this.date || this.today;
    return date.getDate() === reference.getDate() &&
           date.getMonth() === reference.getMonth() &&
           date.getFullYear() === reference.getFullYear();
  }

  setDate(date) {
    let input = this.getElement(this.INPUT);
    if ( date ) {
      this.date = (date instanceof Date)
        ? date : AonDateUtils.parse(date);
      if (input)
        input.value = AonDateUtils.formatDate(this.date, '/');
    } else {
      // Reseteamos el input
      this.date   = '';
      this.day   = this.today.getDate();
      this.month = this.today.getMonth();
      this.year  = this.today.getFullYear();
      input.value = '';
    }

    let datepickerDays = this.getElement(this.DATEPICKER_DAYS);
    if(datepickerDays)
      this.buildCalendar();
    if(input && datepickerDays)
      this.dispatchEvent(new CustomEvent(EVENT.CHANGE, {detail: this.date}));
  }

  setValue() { 

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

  clear(){
    
  }

  getValue() {
    return AonDateUtils.formatDate(this.date);
  } 
  
  getDateValue() {
    return AonDateUtils.formatDate(this.date, 'yyyy-MM-dd');
  }
  
  buildYearPicker(){
	/*
	<div class="year-picker">
	    <div class="controls">
	      <button id="prev">Anterior</button>
	      <span id="range"></span>
	      <button id="next">Siguiente</button>
	    </div>
	    <div class="grid" id="yearGrid"></div>
  	</div>
	*/
	const div = this.createElement(TAG.DIV);
	div.id = this.YEARPICKER;
	div.classList.add("hidden");
	const controls = this.createElement(TAG.DIV);
	const prev = this.createElement(TAG.AON_ICON_BUTTON);
	prev.className = "date-picker-title-icon";
	prev.icon = "keyboard_arrow_left";
	const next = this.createElement(TAG.AON_ICON_BUTTON);
	next.className = "date-picker-title-icon";
    next.icon = "keyboard_arrow_right";
	const range = this.createElement(TAG.SPAN);
	range.id = "rangeText"
	controls.appendChild(prev);
	controls.appendChild(range);
	controls.appendChild(next);
	const grid = this.createElement(TAG.DIV);
	grid.id = "yearGrid";
	div.appendChild(controls);
	div.appendChild(grid);
	prev.addEventListener(EVENT.CLICK, () => {
      this.startYear -= 16;
      this.renderYears();
    });
    next.addEventListener(EVENT.CLICK, () => {
      this.startYear += 16;
      this.renderYears();
    });
	return div;
  }
  
  renderYears() {
	let yearGrid = this.getElement("yearGrid");
    yearGrid.innerHTML = "";
    for (let i = 0; i < 16; i++) {
      const year = this.startYear + i;
      const btn = document.createElement("button");
      btn.textContent = year;
      btn.className = "year-btn";
      yearGrid.appendChild(btn);
	  btn.addEventListener(EVENT.CLICK, (event) => {
		this.year = +event.target.innerHTML;
		this.startYear = +event.target.innerHTML - 8;
		this.getElement(this.DATEPICKER_YEAR).innerHTML = this.year;
		this.showHidePicker(this.YEARPICKER);
		this.buildCalendar();
		const rangeText = this.getElement("rangeText");
		rangeText.textContent = `${this.startYear} - ${this.startYear + 15}`;
		this.renderYears();
		event.stopPropagation();
      });
    }
    const rangeText = this.getElement("rangeText");
    rangeText.textContent = `${this.startYear} - ${this.startYear + 15}`;
  }
  
  buildMonthPicker(){
	const div = this.createElement(TAG.DIV);
	div.id = this.MONTHPICKER;
	div.classList.add("hidden");
	const grid = this.createElement(TAG.DIV);
	grid.id = "monthGrid";
	div.appendChild(grid);
	return div;
  }
  
  renderMonths(){
	let monthGrid = this.getElement("monthGrid");
    monthGrid.innerHTML = "";
    for (let i = 0; i < 12; i++) {
      const btn = document.createElement("button");
      btn.textContent = this.getMonthName(i);
	  btn.value = i;
      btn.className = "year-btn";
      monthGrid.appendChild(btn);
	  btn.addEventListener(EVENT.CLICK, (event) => {
		this.month = +event.target.value;
		this.getElement(this.DATEPICKER_MONTH).innerHTML = this.getMonthName();
		this.showHidePicker(this.MONTHPICKER);
		this.buildCalendar();
		event.stopPropagation();
      });
    }
  }
  
  showHidePicker(elementPicker){
  	const element = this.getElement(elementPicker);
  	if(element.classList.contains("hidden")){
  		element.classList.remove("hidden");
  	}else{
  		element.classList.add("hidden");
  	}
  }

}
if(!window.customElements.get(TAG.AON_NEW_DATE)){
  window.customElements.define(TAG.AON_NEW_DATE,  AonNewDate);
}
