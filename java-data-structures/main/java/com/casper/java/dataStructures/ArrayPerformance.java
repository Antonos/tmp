package com.casper.java.dataStructures;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

/**
 * Created by anton on 05.04.2015.
 */
public class ArrayPerformance {

    private int[] values;
    private Random generator;

    {
        values = new int[PerformanceParams.INITIAL_SIZE];
        generator = new Random();
        for (int i = 0; i < values.length; i++) {
            values[i] = generator.nextInt(PerformanceParams.MAX_VALUE);
        }
    }

    public void timeArrayInsert(){
        for (int i = 0; i < PerformanceParams.ITERATIONS_AMOUNT; i++) {
            ArrayUtils.add(values,(int)values.length/2,generator.nextInt(PerformanceParams.MAX_VALUE));
        }
    }

    public void timeArrayPush(){
        for (int i = 0; i < PerformanceParams.ITERATIONS_AMOUNT; i++) {
            ArrayUtils.add(values,generator.nextInt(PerformanceParams.MAX_VALUE));
        }
    }

    public void timeArrayRemove(){
        for (int i = 0; i < PerformanceParams.ITERATIONS_AMOUNT; i++) {
            ArrayUtils.remove(values, (int) values.length / 2);
        }
    }
}
