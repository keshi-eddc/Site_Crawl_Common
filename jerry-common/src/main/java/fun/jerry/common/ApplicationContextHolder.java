package fun.jerry.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextHolder implements ApplicationContextAware {
  
    private static ApplicationContext applicationContext;
    @SuppressWarnings("all")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if(this.applicationContext != null) {
            throw new IllegalStateException("ApplicationContextHolder already holded 'applicationContext'.");
        }
        this.applicationContext = context;
    }
     
    public static ApplicationContext getApplicationContext() {
        if(applicationContext == null)
            throw new IllegalStateException("'applicationContext' property is null,ApplicationContextHolder not yet init.");
        return applicationContext;
    }
    
    public static Object getBean(Class<?> clazz) {
    	return null == applicationContext ? null : applicationContext.getBean(clazz);
    }
     
    public static Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }
     
    public static void cleanHolder() {
        applicationContext = null;
    }
}