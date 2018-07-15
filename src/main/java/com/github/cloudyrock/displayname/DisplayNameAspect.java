package com.github.cloudyrock.displayname;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Aspect
public class DisplayNameAspect {

    private static final boolean ENABLED = true;
    private static final String RED = (char) 27 + "[31m";
    private static final String GREEN = (char) 27 + "[32m";
    private static final String BLUE = (char) 27 + "[34m";
    private static final String DEFAULT = (char) 27 + "[39m";

    private static final Set<String> loggedClasses = new HashSet<>();

    private ProxyFactory proxyFactory = new ProxyFactory(
            (PreInterceptor) () -> {
            },
            Collections.emptySet(),
            Collections.emptySet());

    @Pointcut("@annotation(displayNameAnn) && execution(* *(..))")
    public void displayNamePointCutDef(DisplayName displayNameAnn) {
    }

    @Around("displayNamePointCutDef(displayNameAnn)")
    public Object dimmerFeatureAdvice(ProceedingJoinPoint joinPoint,
                                      DisplayName displayNameAnn) throws Throwable {
        return ENABLED ? processDisplayName(joinPoint, displayNameAnn) : joinPoint
                .proceed();
    }

    private Object processDisplayName(ProceedingJoinPoint joinPoint,
                                      DisplayName displayNameAnn) throws Throwable {

        printClass(joinPoint);
        final String methodName = joinPoint.getSignature().getName();
        try {
            Object result = joinPoint.proceed();
            printSuccess(displayNameAnn.value(), methodName);
            return result;
        } catch (Throwable thrownException) {
            printFail(displayNameAnn.value(), methodName);
            throw thrownException;
        }
    }

    private void printClass(ProceedingJoinPoint joinPoint) {
        final String declaringClass =
                joinPoint.getSignature().getDeclaringType().getSimpleName();
        if (!loggedClasses.contains(declaringClass)) {
            System.out.println(BLUE + ">>> " + declaringClass + DEFAULT);
            loggedClasses.add(declaringClass);
        }
    }



    private String getStartingMessage(String displayNameMsg) {
        return "\t- " + displayNameMsg;
    }

    private void printSuccess(String displayNameMsg, String methodName) {
        System.out.println(
                GREEN + getStartingMessage(
                        displayNameMsg) + " (" + methodName + ")" + DEFAULT);
    }

    private void printFail(String displayNameMsg, String methodName) {
        System.out.println(
                RED + getStartingMessage(
                        displayNameMsg) + " (" + methodName + ")" + DEFAULT);
    }


}
