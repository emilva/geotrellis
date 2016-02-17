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

package geotrellis.engine.op.zonal

import geotrellis.raster._
import geotrellis.raster.histogram._
import geotrellis.raster.mapalgebra.zonal._
import geotrellis.engine._

import org.scalatest._

import scala.collection.mutable

import spire.syntax.cfor._

class ZonalHistogramSpec extends FunSpec with Matchers with TestEngine {
  describe("ZonalHistogram") {
    it("gives correct histogram map for example raster sources") {
      val rs = createRasterSource(
        Array(1, 2, 2,   2, 3, 1,   6, 5, 1,
              1, 2, 2,   2, 3, 6,   6, 5, 5,

              1, 3, 5,   3, 6, 6,   6, 5, 5,
              3, 1, 5,   6, 6, 6,   6, 6, 2,

              7, 7, 5,   6, 1, 3,   3, 3, 2,
              7, 7, 5,   5, 5, 4,   3, 4, 2,

              7, 7, 5,   5, 5, 4,   3, 4, 2,
              7, 2, 2,   5, 4, 4,   3, 4, 4),
        3,4,3,2)

      val zonesSource = createRasterSource(
        Array(1, 1, 1,   4, 4, 4,   5, 6, 6,
              1, 1, 1,   4, 4, 5,   5, 6, 6,

              1, 1, 2,   4, 5, 5,   5, 6, 6,
              1, 2, 2,   3, 3, 3,   3, 3, 3,

              2, 2, 2,   3, 3, 3,   3, 3, 3,
              2, 2, 2,   7, 7, 7,   7, 8, 8,

              2, 2, 2,   7, 7, 7,   7, 8, 8,
              2, 2, 2,   7, 7, 7,   7, 8, 8),
        3,4,3,2)

      val r = get(rs)
      val zones = get(zonesSource)

      val (cols,rows) = (zones.cols,zones.rows)

      val zoneValues = mutable.Map[Int,mutable.ListBuffer[Int]]()

      cfor(0)(_ < rows, _ + 1) { row =>
        cfor(0)(_ < cols, _ + 1) { col =>
          val z = zones.get(col,row)
          if(!zoneValues.contains(z)) { zoneValues(z) = mutable.ListBuffer[Int]() }
          zoneValues(z) += r.get(col,row)
        }
      }

      val expected =
        zoneValues.toMap.mapValues { list =>
          list.distinct
              .map { v => (v, list.filter(_ == v).length) }
              .toMap
        }

      val result: Map[Int, Histogram[Int]] = rs.zonalHistogramInt(zonesSource).get

      result.keys should be (expected.keys)

      for(zone <- result.keys) {
        val hist = result(zone)
        for(v <- expected(zone).keys) {
          hist.getItemCount(v) should be (expected(zone)(v))
        }
      }
    }
  }
}
