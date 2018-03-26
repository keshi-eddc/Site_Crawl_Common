package com.edmi.site.dianping.crawl;

import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.common.LogSupport;

@Component
public class Test {
	
	private static Logger log = LogSupport.getJdlog();
	
	public static void main(String[] args) {
		WebDriver driver = WebDriverSupport.getChromeDriverInstance(null);
		driver.manage().deleteAllCookies();
//		driver.manage().addCookie(new Cookie("bcookie", "\"v=2&346cbeef-10f4-454d-82cc-068fdabd931c\""));
//		driver.manage().addCookie(new Cookie("bscookie", "\"v=1&201803200400446db4d492-707f-43d6-8f36-6d3e4d244058AQGcrMcMXiEZKddlhBMZzxwxaNHPgOdJ\""));
//		driver.manage().addCookie(new Cookie("visit", "\"v=1&G\""));
//		driver.manage().addCookie(new Cookie("lang", "v=2&lang=zh-cn"));
//		driver.manage().addCookie(new Cookie("liap", "true"));
//		driver.manage().addCookie(new Cookie("JSESSIONID", "ajax:1875872609262722676"));
//		driver.manage().addCookie(new Cookie("li_at", "AQEDASaA3poEmsYxAAABYlGfB5UAAAFidauLlVEAyr2D4aKgkZcSX-5ltamxnL3sXOK5WJ66f2e_kZKdtDH1Nosfi6EQk34KjkyflFHyPeX5P8tJriddrGJCK8Nyg3KOjEJuEYIQRjS4IfwEy-cXvnXY;"));
//		driver.manage().addCookie(new Cookie("_lipt", "CwEAAAFiUaVcyna1_BtjoSQjGDNQ40ENpBz_J9zVc2vyiW4rMgIZko3O1czuAB0VbbdspC0KRR82SyI5hL_yVyQfiTM5e4qrUi_7IlDsjWsfSi6QCB49vfKnCdY"));
//		driver.manage().addCookie(new Cookie("lidc", "b=SGST01:g=3:u=1:i=1521790348:t=1521874073:s=AQEiH40P7myxAdVxPMNRJ1T2P7oa6XLn"));
		driver.get("https://www.linkedin.com/in/%E4%BA%AC%E6%B6%9B-%E5%BC%A0-886639b8/?trk=public-profile-join-page");
		log.info(driver.getPageSource());
		driver.close();
		driver.quit();
	}
}
