import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-spline-scene',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './spline-scene.component.html',
  styleUrls: ['./spline-scene.component.scss'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SplineSceneComponent implements OnInit {
  @Input({ required: true }) scene = '';
  @Input() className = '';

  viewerReady = false;

  private static viewerScriptPromise: Promise<void> | null = null;

  ngOnInit(): void {
    this.ensureViewerScript()
      .then(() => {
        this.viewerReady = true;
      })
      .catch(() => {
        this.viewerReady = false;
      });
  }

  private ensureViewerScript(): Promise<void> {
    if ((window as typeof window & { customElements: CustomElementRegistry }).customElements.get('spline-viewer')) {
      return Promise.resolve();
    }

    if (SplineSceneComponent.viewerScriptPromise) {
      return SplineSceneComponent.viewerScriptPromise;
    }

    SplineSceneComponent.viewerScriptPromise = new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.type = 'module';
      script.src = 'https://unpkg.com/@splinetool/viewer@1.9.66/build/spline-viewer.js';
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Failed to load spline viewer'));
      document.body.appendChild(script);
    });

    return SplineSceneComponent.viewerScriptPromise;
  }
}
