package net.hankjohn.bean;

import java.util.List;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class TestSpringTag {
	@Test
	public void test() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"beans.xml");

		SimpleBean bean = context.getBean(SimpleBean.class);

		SimpleController controller = bean.getSimpleController();

		List<ComplexController> controllers = bean.getControllers();
		Assert.assertEquals(3, controllers.size());
	}
}
