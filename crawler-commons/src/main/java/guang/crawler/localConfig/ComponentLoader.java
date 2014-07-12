package guang.crawler.localConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 从组件配置xml中加载相关信息和类.该过程类似于web.xml中定义servlet的过程.
 *
 * @author yang
 *
 */
public class ComponentLoader<T> {
	/**
	 * 默认组件的名称
	 */
	private static final String	    DEFAULT_COMPONENT_NAME	= "default";
	/**
	 * 配置文件
	 */
	private File	                configFile;
	/**
	 * 是否已经加载过了.只允许加载一次,不能重复加载
	 */
	private boolean	                loaded	               = false;
	/**
	 * 组件名称和类名的映射
	 */
	private HashMap<String, String>	nameToClass	           = new HashMap<String, String>();
	/**
	 * url与组件名称的映射
	 */
	private HashMap<String, String>	urlToName	           = new HashMap<String, String>();
	/**
	 * 组件名称和组件实例的映射
	 */
	private HashMap<String, T>	    nameToObj	           = new HashMap<String, T>();

	/**
	 * XML元素的命名空間
	 */
	private static final String	    NS	                   = "http://guang.org/distributedCrawler/components";

	private File	                schemaFile;

	/**
	 * 创建一个组件加载器
	 *
	 * @param configFile
	 *            组件的配置文件
	 * @param schemaFile
	 *            XSD模板文件
	 */
	public ComponentLoader(final File configFile, final File schemaFile) {
		this.configFile = configFile;

	}

	/**
	 * 根据URL的值,找到匹配该URL的第一个组件.
	 *
	 * @param url
	 * @return
	 */
	public T getComponent(final String url) {
		if (url == null) {
			return null;
		}
		Iterator<String> patterns = this.urlToName.keySet()
		                                          .iterator();
		String name = null;
		while (patterns.hasNext()) {
			String pattern = patterns.next();
			if (url.matches(pattern)) {
				name = this.urlToName.get(pattern);
				break;
			}
		}
		if (name == null) {
			name = ComponentLoader.DEFAULT_COMPONENT_NAME;
		}

		T component = this.nameToObj.get(name);
		if (component == null) {
			String className = this.nameToClass.get(name);
			if (className == null) {
				return null;
			}
			try {
				@SuppressWarnings("unchecked")
				T newInstance = (T) Class.forName(className)
				                         .newInstance();
				component = newInstance;
			} catch (Exception e) {
				return null;
			}

		}
		return component;

	}

	/**
	 * 从配置文件中加载组件
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void load() throws SAXException, IOException,
	        ParserConfigurationException, InstantiationException,
	        IllegalAccessException, ClassNotFoundException {
		if (this.loaded) {
			return;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(true);
		if (this.schemaFile != null) {
			Schema schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
			                             .newSchema(this.schemaFile);
			factory.setSchema(schema);
		}
		Document config = factory.newDocumentBuilder()
		                         .parse(this.configFile);
		this.loadComponentDefines(config);
		this.loadComponentMappings(config);
		this.loaded = true;
	}

	/**
	 * 加载配置文件中组件定义部分
	 * 
	 * @param config
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private void loadComponentDefines(final Document config)
	        throws InstantiationException, IllegalAccessException,
	        ClassNotFoundException {
		NodeList nodelist = config.getElementsByTagNameNS(ComponentLoader.NS,
		                                                  "component-defines");
		int size = nodelist.getLength();
		if (size == 1) {
			Element componentDefines = (Element) nodelist.item(0);
			nodelist = componentDefines.getElementsByTagNameNS(ComponentLoader.NS,
			                                                   "component");
			size = nodelist.getLength();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					Element componentElement = (Element) nodelist.item(i);
					NodeList nodes = componentElement.getElementsByTagNameNS(ComponentLoader.NS,
					                                                         "component-name");
					if (nodes.getLength() == 0) {
						continue;
					}
					String name = nodes.item(0)
					                   .getTextContent()
					                   .trim();
					nodes = componentElement.getElementsByTagNameNS(ComponentLoader.NS,
					                                                "component-class");
					if (nodes.getLength() == 0) {
						continue;
					}
					String className = nodes.item(0)
					                        .getTextContent()
					                        .trim();
					nodes = componentElement.getElementsByTagNameNS(ComponentLoader.NS,
					                                                "load-on-startup");
					boolean loadOnStartup = false;
					if (nodes.getLength() != 0) {
						loadOnStartup = Boolean.parseBoolean(nodes.item(0)
						                                          .getTextContent()
						                                          .trim());
					}
					this.nameToClass.put(name, className);
					if (loadOnStartup) {
						@SuppressWarnings("unchecked")
						T component = (T) Class.forName(className)
						                       .newInstance();
						this.nameToObj.put(name, component);
					}

				}
			}
		}

	}

	/**
	 * 加载组件配置文件中组件映射部分
	 * 
	 * @param config
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private void loadComponentMappings(final Document config)
	        throws InstantiationException, IllegalAccessException,
	        ClassNotFoundException {
		NodeList nodeList = config.getElementsByTagNameNS(ComponentLoader.NS,
		                                                  "component-mappings");

		int size = nodeList.getLength();
		if (size == 1) {
			Element componentMappings = (Element) nodeList.item(0);
			nodeList = componentMappings.getElementsByTagNameNS(ComponentLoader.NS,
			                                                    "component-mapping");
			size = nodeList.getLength();
		}
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Element componentMapping = (Element) nodeList.item(i);
				NodeList nodes = componentMapping.getElementsByTagNameNS(ComponentLoader.NS,
				                                                         "component-name");
				if (nodes.getLength() == 0) {
					continue;
				}
				String name = nodes.item(0)
				                   .getTextContent()
				                   .trim();
				nodes = componentMapping.getElementsByTagNameNS(ComponentLoader.NS,
				                                                "url-pattern");
				int length = nodes.getLength();
				if (length == 0) {
					continue;
				}
				for (int j = 0; j < length; j++) {
					String pattern = nodes.item(j)
					                      .getTextContent()
					                      .trim();
					this.urlToName.put(pattern, name);
				}
			}
		}
	}

}
