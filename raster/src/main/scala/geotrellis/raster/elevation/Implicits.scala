/*
 * Copyright (c) 2014 Azavea.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.elevation

import geotrellis.raster._

object Implicits extends Implicits

trait Implicits {
  implicit class HillshadeTuple(val tuple: (Tile, Tile)) {
    def hillshade(azimuth: Double, altitude: Double) = {
      val (aspect, slope) = tuple
      Hillshade.indirect(aspect, slope, azimuth, altitude)
    }
  }
}
