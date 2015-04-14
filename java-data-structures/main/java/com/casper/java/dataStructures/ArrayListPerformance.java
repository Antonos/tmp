package com.casper.java.dataStructures;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by anton on 05.04.2015.
 */
public class ArrayListPerformance {

    private ArrayList<Integer> values;
    private Random generator;

    {
        values = new ArrayList<Integer>();
        generator = new Random();
        for (int i = 0; i < PerformanceParams.INITIAL_SIZE; i++) {
            values.add(generator.nextInt(PerformanceParams.MAX_VALUE));
        }
    }

    public void timeArrayListInsert(){
        for (int i = 0; i < PerformanceParams.ITERATIONS_AMOUNT; i++) {
            values.add((int)values.size()/2,generator.nextInt(PerformanceParams.MAX_VALUE));
        }
    }

    public void timeArrayListPush(){
        for (int i = 0; i < PerformanceParams.ITERATIONS_AMOUNT; i++) {
            values.add(generator.nextInt(PerformanceParams.MAX_VALUE));
        }
    }

    public void timeArrayListRemove(){
        for (int i = 0; i < PerformanceParams.ITERATIONS_AMOUNT; i++) {
            values.remove((int) values.size() / 2);
        }
    }
}
