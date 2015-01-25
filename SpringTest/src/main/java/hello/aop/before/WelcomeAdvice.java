package hello.aop.before;

import hello.aop.Customer;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;

public class WelcomeAdvice implements MethodBeforeAdvice {
	public void before(Method method, Object[] args, Object target) {
		Customer customer = (Customer)args[0];
		System.out.println("Welcome " + customer.getName());
	}
}
