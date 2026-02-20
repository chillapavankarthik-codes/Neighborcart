import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-background-paper-shaders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './background-paper-shaders.component.html',
  styleUrls: ['./background-paper-shaders.component.scss']
})
export class BackgroundPaperShadersComponent {
  intensity = 1.5;
  speed = 1;
  isInteracting = false;
  activeEffect: 'mesh' | 'dots' | 'combined' = 'mesh';
  copied = false;

  async copyToClipboard(): Promise<void> {
    try {
      await navigator.clipboard.writeText('Angular + Spring Boot + PostgreSQL + OTP + Real-time order chat');
      this.copied = true;
      setTimeout(() => {
        this.copied = false;
      }, 1800);
    } catch {
      this.copied = false;
    }
  }

  setEffect(effect: 'mesh' | 'dots' | 'combined'): void {
    this.activeEffect = effect;
  }
}
