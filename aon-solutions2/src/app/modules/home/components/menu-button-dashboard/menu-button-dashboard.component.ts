import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-menu-button-dashboard',
  templateUrl: './menu-button-dashboard.component.html',
  styleUrls: ['./menu-button-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class MenuButtonDashboardComponent implements OnInit {

  @Input() shape  = '';
  @Input() name   = '';
  @Input() class  = '';

  constructor() {}

  ngOnInit(): void {
  }

}
