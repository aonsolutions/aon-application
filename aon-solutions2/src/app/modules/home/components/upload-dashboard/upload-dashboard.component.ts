import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-upload-dashboard',
  templateUrl: './upload-dashboard.component.html',
  styleUrls: ['./upload-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class UploadDashboardComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {}
}
