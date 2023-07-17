import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-grid-layout-alt',
  templateUrl: './grid-layout-alt.component.html',
  styleUrls: ['./grid-layout-alt.component.scss']
})
export class GridLayoutAltComponent implements OnInit, OnChanges {

  liveContentCols: number = 0;
  liveDetailCols: number = 0;
  liveNavCols: number = 0;
  @Input() headerHeight: string = '';
  @Input() navCols: number = 0;
  @Input() contentCols: number = 0;
  @Input() detailCols: number = 0;
  @Input() showDetail: boolean = false;
  @Input() showMenu: boolean = false;

  constructor() { }

  ngOnInit(): void {
    this.liveContentCols = this.contentCols + this.detailCols + this.navCols;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.showMenu && !changes.showMenu.firstChange) {
      if (this.showMenu) {
        this.liveNavCols = this.navCols; // Restaurar el tamaño original del bloque izquierdo
        this.liveContentCols = this.contentCols + this.detailCols; // Ajustar el tamaño del bloque central y derecho
      } else {
        this.liveNavCols = 0; // Establecer el tamaño del bloque izquierdo en 0
        this.liveContentCols = this.contentCols + this.detailCols; // Ajustar el tamaño del bloque central y derecho
      }
    }

    if (changes.showDetail && !changes.showDetail.firstChange) {
      if (this.showDetail) {
        this.liveNavCols = this.navCols;
        this.liveContentCols = this.contentCols;
        this.liveDetailCols = this.detailCols;
      } else {
        this.liveNavCols = 0;
        this.liveContentCols = this.contentCols;
        this.detailCols = 0;
      }
    }
  }





}
