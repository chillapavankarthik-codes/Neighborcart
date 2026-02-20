import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import { ParallaxScrollDirective } from '../../../directives/parallax-scroll.directive';
import { RevealOnScrollDirective } from '../../../directives/reveal-on-scroll.directive';
import { CardComponent } from '../card/card.component';
import { SplineSceneComponent } from '../spline-scene/spline-scene.component';
import { SpotlightComponent } from '../spotlight/spotlight.component';

@Component({
  selector: 'app-spline-scene-basic',
  standalone: true,
  imports: [
    CommonModule,
    CardComponent,
    SplineSceneComponent,
    SpotlightComponent,
    RevealOnScrollDirective,
    ParallaxScrollDirective
  ],
  templateUrl: './spline-scene-basic.component.html',
  styleUrls: ['./spline-scene-basic.component.scss']
})
export class SplineSceneBasicComponent {
  readonly scene = 'https://prod.spline.design/kZDDjO5HuC9GJUM2/scene.splinecode';
}
