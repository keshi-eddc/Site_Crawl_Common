package com.edmi.site.dianping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 对应config.properties
 * @author conner
 *
 */
@Component("dianping")
public class DianpingConfig {
	
	/**
	 * 已登录的手机帐号
	 */
	@Value(value = "${long_phone_queue}")
	private String[] long_phone_queue;
	
	@Value(value = "${phone}")
	private String phone;

	public String[] getLong_phone_queue() {
		return long_phone_queue;
	}

	public String getPhone() {
		return phone;
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(context.getBean(DianpingConfig.class).getPhone());
		for (String phone : context.getBean(DianpingConfig.class).getLong_phone_queue()) {
			System.out.println(phone);
		}
	}

}
