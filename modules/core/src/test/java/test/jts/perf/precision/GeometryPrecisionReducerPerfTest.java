/*
 * Copyright (c) 2016 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package test.jts.perf.precision;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.util.SineStarFactory;
import org.locationtech.jts.precision.GeometryPrecisionReducer;

import test.jts.perf.PerformanceTestCase;
import test.jts.perf.PerformanceTestRunner;

/**
 * This test revealed a scaling issue with the {@link org.locationtech.jts.noding.snapround.SnapRoundingNoder}:
 * the {@link org.locationtech.jts.noding.snapround.HotPixelIndex} could not handle very large numbers
 * of points due to kdTree becoming unbalanced.
 * 
 * @author Martin Davis
 *
 */
public class GeometryPrecisionReducerPerfTest
extends PerformanceTestCase
{

  private static final int N_ITER = 1;

  static double ORG_X = 100;
  static double ORG_Y = 100;
  static double SIZE = 100;
  static int N_ARMS = 20;
  static double ARM_RATIO = 0.3;
  
  public static void main(String args[]) {
    PerformanceTestRunner.run(GeometryPrecisionReducerPerfTest.class);
  }

  private Geometry sineStar;

  private PrecisionModel pm;


  public GeometryPrecisionReducerPerfTest(String name)
  {
    super(name);
    setRunSize(new int[] { 100, 200, 400, 1000, 2000, 4000, 8000, 10000, 
        100_000, 200_000, 400_000
        , 1000_000, 2000_000 });
    setRunIterations(N_ITER);
  }

  public void setUp()
  {
    System.out.println("Geometry Precision Reducer perf test");
    System.out.println("SineStar: origin: ("
        + ORG_X + ", " + ORG_Y + ")  size: " + SIZE
        + "  # arms: " + N_ARMS + "  arm ratio: " + ARM_RATIO);   
    System.out.println("# Iterations: " + N_ITER);
  }
  
  public void startRun(int npts)
  {
    iter = 0;
    sineStar = SineStarFactory.create(new Coordinate(ORG_X, ORG_Y), SIZE, npts, N_ARMS, ARM_RATIO);
    
    double scale = npts / SIZE;
    pm = new PrecisionModel(scale);
    System.out.format("\n# pts = %d, Scale = %f\n", npts, scale);
    
    if (npts <= 1000) System.out.println(sineStar);
  }
  
  private int iter = 0;
  
  public void runReduce()
  {
    Geometry sinePolyCrinkly = GeometryPrecisionReducer.reduce(sineStar, pm);
  }

}
