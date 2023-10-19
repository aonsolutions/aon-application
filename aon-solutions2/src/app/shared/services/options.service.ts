import { EventEmitter, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class OptionsService {
  options: any;
  optionsUpdated: EventEmitter<any> = new EventEmitter<any>();

  constructor() { }

  setOptions(options: any): void {
    this.options = options;
    this.optionsUpdated.emit();
  }

  getOptions(): any {
    return this.options;
  }

  clearOptions(): void {
    this.options = null;
  }
}
