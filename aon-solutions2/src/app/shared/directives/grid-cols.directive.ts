import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Directive, Input, OnInit } from '@angular/core';
import { MatGridTile } from '@angular/material/grid-list';

export interface GridColumns {
  xs: number;
  sm: number;
  md: number;
  lg: number;
  xl: number;
}

@Directive({
  selector: '[gridCols]'
})
export class GridColsDirective implements OnInit {

  private gridCols: GridColumns = {xs:0,sm:0,md:0,lg:0,xl:0};

  @Input('gridCols')
  public set cols(map: GridColumns) {
    if (map && ('object' === (typeof map))) {
      this.gridCols = map;
    }
  }

  constructor(private tile: MatGridTile, private breakpointObserver: BreakpointObserver) { }

  ngOnInit(){
    this.breakpointObserver.observe([
      Breakpoints.XSmall,
      Breakpoints.Small,
      Breakpoints.Medium,
      Breakpoints.Large,
      Breakpoints.XLarge
    ]).subscribe(result => {

      if (result.breakpoints[Breakpoints.XSmall]) {
        if(this.gridCols.xs){
          this.tile.colspan = this.gridCols.xs;
//          console.log('---> XSmall <----');
        }
      }
      if (result.breakpoints[Breakpoints.Small]) {
        if(this.gridCols.sm){
          this.tile.colspan = this.gridCols.sm;
//          console.log('---> Small <----');
        }
      }
      if (result.breakpoints[Breakpoints.Medium]) {
        if(this.gridCols.md){
          this.tile.colspan = this.gridCols.md;
//          console.log('---> Medium <----');
        }
      }
      if (result.breakpoints[Breakpoints.Large]) {
        if(this.gridCols.lg){
          this.tile.colspan = this.gridCols.lg;
//          console.log('---> large <----');
        }
      }
      if (result.breakpoints[Breakpoints.XLarge]) {
        if(this.gridCols.xl){
          this.tile.colspan = this.gridCols.xl;
//          console.log('---> XLarge <----');
        }
      }
    });
  }

}
