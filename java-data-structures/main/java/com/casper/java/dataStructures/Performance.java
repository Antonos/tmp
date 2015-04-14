package com.casper.java.dataStructures;

/**
 * Created by anton on 06.04.2015.
 */
public class Performance {

    public static void main(String[] args){
        new ArrayPerformance().timeArrayPush();
        new ArrayPerformance().timeArrayInsert();
        new ArrayPerformance().timeArrayRemove();

        new ArrayListPerformance().timeArrayListPush();
        new ArrayListPerformance().timeArrayListInsert();
        new ArrayListPerformance().timeArrayListRemove();
    }

}
