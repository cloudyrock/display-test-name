package com.github.cloudyrock.displayname;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

/**
 * @author Antonio Perez Dieppa
 * @since 04/04/2018
 */
class ProxyMethodInterceptor implements MethodInterceptor {

  private final Object original;
  private final ProxyFactory proxyFactory;
  private final Set<String> proxyCreatorMethods;
  private final Set<String> uncheckedMethods;
  private final PreInterceptor lockChecker;

  ProxyMethodInterceptor(Object original,
                         ProxyFactory proxyFactory,
                         PreInterceptor preInterceptor,
                         Set<String> proxyCreatorMethods,
                         Set<String> uncheckedMethods) {
    this.original = original;
    this.lockChecker = preInterceptor;
    this.proxyFactory = proxyFactory;
    this.proxyCreatorMethods = proxyCreatorMethods != null ? proxyCreatorMethods : Collections.<String>emptySet();
    this.uncheckedMethods = uncheckedMethods != null ? uncheckedMethods : Collections.<String>emptySet();
  }

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    checkMethod(method);
    return invokeMethod(method, objects);

  }

  private void checkMethod(Method method) {
    if (!uncheckedMethods.contains(method.getName())) {
      lockChecker.before();
    }
  }

  private Object invokeMethod(Method method, Object[] objects) throws IllegalAccessException, InvocationTargetException {
    if (proxyCreatorMethods.contains(method.getName())) {
      return proxyFactory.createProxyFromOriginal(method.invoke(original, objects));
    } else {
      return method.invoke(original, objects);
    }
  }
}
