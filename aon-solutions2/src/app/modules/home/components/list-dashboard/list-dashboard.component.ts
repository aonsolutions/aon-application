import { Component, OnInit ,Input} from '@angular/core';
export interface ITile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}
@Component({
  selector: 'app-list-dashboard',
  templateUrl: './list-dashboard.component.html',
  styleUrls: ['./list-dashboard.component.scss']
})
export class ListDashboardComponent implements OnInit {

  @Input() type : number = 0;
  @Input() name : string ="";
  @Input() description : string = "";
  @Input() subject : string = "";
  @Input() date : string = "";

  constructor() {

   }

  ngOnInit(): void {
  }
}
