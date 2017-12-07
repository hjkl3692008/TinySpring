package us.codecraft.tinyioc;

import java.util.Map;
import org.junit.Test;
import us.codecraft.tinyioc.aop.AdvisedSupport;
import us.codecraft.tinyioc.aop.JdkDynamicAopProxy;
import us.codecraft.tinyioc.aop.TargetSource;
import us.codecraft.tinyioc.aop.TimerInterceptor;
import us.codecraft.tinyioc.beans.BeanDefinition;
import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;
import us.codecraft.tinyioc.beans.factory.AutowireCapableBeanFactory;
import us.codecraft.tinyioc.beans.io.ResourceLoader;
import us.codecraft.tinyioc.beans.xml.XmlBeanDefinitionReader;

/**
 * @author yihua.huang@dianping.com
 */
public class BeanFactoryTest {

    @Test
    public void testLazy() throws Exception {
        // 1.读取配置
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

        // 2.初始化BeanFactory并注册bean
        AbstractBeanFactory beanFactory = new AutowireCapableBeanFactory();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }

        // 3.获取bean
        HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");
        helloWorldService.helloWorld();
    }

	@Test
	public void testPreInstantiate() throws Exception {
		// 1.读取配置
		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
		xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

		// 2.初始化BeanFactory并注册bean
		AbstractBeanFactory beanFactory = new AutowireCapableBeanFactory();
		for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
			beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
		}

        // 3.初始化bean
        beanFactory.preInstantiateSingletons();

		// 4.获取bean
		HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");

		//without aop
		helloWorldService.helloWorld();

		//with aop
        //设置目标
        AdvisedSupport advised = new AdvisedSupport();
        TargetSource target = new TargetSource(helloWorldService,HelloWorldService.class);
        advised.setTargetSource(target);

        //设置拦截
        advised.setMethodInterceptor(new TimerInterceptor());

        //设置代理
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(advised);
        HelloWorldService helloWorldServiceProxy = (HelloWorldService)proxy.getProxy();

        //代理调用方法
        helloWorldServiceProxy.helloWorld();


	}
}
