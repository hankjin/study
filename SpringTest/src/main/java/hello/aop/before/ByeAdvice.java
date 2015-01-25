package hello.aop.before;

import hello.aop.Customer;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;

public class ByeAdvice implements AfterReturningAdvice {
	/**
	 * 
	 */
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target)  {
		Customer customer = (Customer)args[0];
		System.out.println("Bye " + customer.getName());
	}
}
