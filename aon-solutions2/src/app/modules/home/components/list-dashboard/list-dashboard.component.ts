import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-list-dashboard',
  templateUrl: './list-dashboard.component.html',
  styleUrls: ['./list-dashboard.component.scss']
})
export class ListDashboardComponent implements OnInit {
  @Input() type: number = 0;
  @Input() name: string = "";
  @Input() description: string = "";
  @Input() title: string = "";
  @Input() date: any = "";
  public icon: string = "";

  ngOnInit(): void {
    this.setIcon();
  }

  private setIcon(): void {
    switch (this.type) {
      case 1:
        this.icon = "playlist_add_check";
        break;
      case 2:
        this.icon = "message";
        break;
      case 3:
        this.icon = "notifications";
        break;
      default:
        this.icon = "";
        break;
    }
  }
}
