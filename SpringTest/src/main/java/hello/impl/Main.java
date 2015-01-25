package hello.impl;

import hello.MessagePrinter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String args[]) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("hello/beans.xml");
		MessagePrinter printer = (MessagePrinter)context.getBean("printer");
		printer.printMessage();
	}
}
