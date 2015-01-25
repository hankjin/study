package hello.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String args[]) {
		ApplicationContext context = new ClassPathXmlApplicationContext("hello/beans.xml");
		SuperMarket market = (SuperMarket)context.getBean("safeWay");
		market.buy(new Customer());
	}
}
