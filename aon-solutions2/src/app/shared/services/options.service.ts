import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class OptionsService {
  options: any;

  constructor() { }

  setOptions(options: any): void {
    this.options = options;
  }

  getOptions(): any {
    return this.options;
  }

  clearOptions(): void {
    this.options = null;
  }
}
