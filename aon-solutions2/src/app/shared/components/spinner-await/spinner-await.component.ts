import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-spinner-await',
  templateUrl: './spinner-await.component.html',
  styleUrls: ['./spinner-await.component.scss']
})
export class SpinnerAwaitComponent implements OnInit {
  @Input() color: string = 'primary';
  @Input() diameter: number = 70;
  @Input() strokeWidth: number = 5;

  constructor() { }

  ngOnInit(): void {
    this.validateColor();
  }

  /**
  * Valida el color y establece un valor predeterminado si se proporciona un color no válido.
  *
  * @param {} - Sin parámetros.
  * @return {} - Sin valor de retorno.
  */
  private validateColor(): void {
    const allowedColors = ['black', 'primary', 'positive', 'attention', 'warning', 'alt-primary', 'alt-positive', 'alt-attention', 'alt-warning'];

    if (!allowedColors.includes(this.color)) {
      this.color = 'primary';
    }
  }

}
