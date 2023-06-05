import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-grid-layout',
  templateUrl: './grid-layout.component.html',
  styleUrls: ['./grid-layout.component.scss']
})
export class GridLayoutComponent implements OnInit, OnChanges {

  liveContentCols: number = 0;
  liveDetailCols: number = 0;
  @Input() headerHeight: string = '';
  @Input() navCols: number = 0;
  @Input() contentCols: number = 0;
  @Input() detailCols: number = 0;
  @Input() showDetail: boolean = false;

  constructor() { }

  ngOnInit(): void {
    this.liveContentCols = this.contentCols + this.detailCols;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.showDetail.currentValue){
      this.liveContentCols = this.contentCols;
      this.liveDetailCols = this.detailCols;
    }else{
      this.liveContentCols = this.contentCols + this.detailCols;
      this.liveDetailCols = 0;
    }
  }

}
