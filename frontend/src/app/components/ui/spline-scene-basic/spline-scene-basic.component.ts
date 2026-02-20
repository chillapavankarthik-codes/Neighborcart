import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import { CardComponent } from '../card/card.component';
import { SplineSceneComponent } from '../spline-scene/spline-scene.component';
import { SpotlightComponent } from '../spotlight/spotlight.component';

@Component({
  selector: 'app-spline-scene-basic',
  standalone: true,
  imports: [CommonModule, CardComponent, SplineSceneComponent, SpotlightComponent],
  templateUrl: './spline-scene-basic.component.html',
  styleUrls: ['./spline-scene-basic.component.scss']
})
export class SplineSceneBasicComponent {
  readonly scene = 'https://prod.spline.design/kZDDjO5HuC9GJUM2/scene.splinecode';
}
