package fun.jerry.common;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Desc:properties配置文件读取类
 */
public class PropertyConfigurerSupport extends PropertyPlaceholderConfigurer {

	private Properties props; // 存取properties配置文件key-value结果

	private boolean isDebug;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		this.props = props;
	}

	public String getProperty(String key) {
		return this.props.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return this.props.getProperty(key, defaultValue);
	}

	public Object setProperty(String key, String value) {
		return this.props.setProperty(key, value);
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}
}