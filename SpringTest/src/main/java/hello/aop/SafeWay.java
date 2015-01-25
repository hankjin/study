package hello.aop;

public class SafeWay implements SuperMarket {
	public String buy(Customer customer) {
		System.out.println(customer.getName() + " buy item");
		return "Hello";
	}
}
