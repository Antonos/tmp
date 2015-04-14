package com.casper.java.dataStructures;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by anton on 06.04.2015.
 */
@Aspect
public class PerformanceAspect {

    StopWatch stopWatch = new StopWatch();

    @Pointcut("execution(* com.casper.java.dataStructures..*(..))")
    public void performancePointcut() {}

    @Before("performancePointcut()")
    public void before(){
        stopWatch.start();
    }

    @After("performancePointcut()")
    public void after(ProceedingJoinPoint pjp){
        stopWatch.stop();
        System.out.println(pjp.getSignature().getName()+": "+stopWatch.getTime()+"ms");
    }

}
