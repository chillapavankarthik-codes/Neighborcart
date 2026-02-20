import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-bento-card',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './bento-card.component.html',
  styleUrls: ['./bento-card.component.scss']
})
export class BentoCardComponent {
  @Input({ required: true }) name = '';
  @Input({ required: true }) description = '';
  @Input() metric = '';
  @Input() helperText = '';
  @Input({ required: true }) href = '/';
  @Input({ required: true }) cta = 'Learn more';
  @Input({ required: true }) image = '';
  @Input() className = '';
  @Input() icon: 'file' | 'search' | 'globe' | 'calendar' | 'bell' = 'file';
}
