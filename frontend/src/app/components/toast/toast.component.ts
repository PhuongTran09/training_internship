import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss']
})
export class ToastComponent {
  heading = '';
  description = '';
  isVisible = false;

  show(heading: string, description: string = '', duration: number = 3000) {
    this.heading = heading;
    this.description = description;
    this.isVisible = true;

    setTimeout(() => {
      this.isVisible = false;
    }, duration);
  }
}

