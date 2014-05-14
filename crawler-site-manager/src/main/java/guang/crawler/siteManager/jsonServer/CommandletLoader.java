package guang.crawler.siteManager.jsonServer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 从commandlet.xml中加载相关信息和类
 * 
 * @author yang
 * 
 */
public class CommandletLoader
{
	private File	                    configFile;
	private boolean	                    loaded	    = false;	                                        ;
	
	/**
	 * 将commandlet的名字定向到类名中
	 */
	private HashMap<String, String>	    nameToClass	= new HashMap<>();
	private HashMap<String, String>	    urlToName	= new HashMap<>();
	private HashMap<String, Commandlet>	nameToObj	= new HashMap<>();
	
	/**
	 * XML元素的命名空間
	 */
	private static final String	        NS	        = "http://guang.org/distributedCrawler/commandlet";
	private File	                    schemaFile;
	
	public CommandletLoader(File configFile, File schemaFile)
	{
		this.configFile = configFile;
		this.schemaFile = schemaFile;
		
	}
	
	public Commandlet getCommandlet(String url)
	{
		if (url == null)
		{
			return null;
		}
		
		String name = this.urlToName.get(url);
		if (name == null)
		{
			return null;
		}
		
		Commandlet commandlet = this.nameToObj.get(name);
		if (commandlet == null)
		{
			String className = this.nameToClass.get(name);
			if (className == null)
			{
				return null;
			}
			try
			{
				commandlet = (Commandlet) Class.forName(className)
				        .newInstance();
			} catch (InstantiationException | IllegalAccessException
			        | ClassNotFoundException e)
			{
				return null;
			}
		}
		return commandlet;
		
	}
	
	public void load() throws SAXException, IOException,
	        ParserConfigurationException, InstantiationException,
	        IllegalAccessException, ClassNotFoundException
	{
		if (this.loaded)
		{
			return;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(true);
		if (this.schemaFile != null)
		{
			Schema schema = SchemaFactory.newInstance(
			        XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
			        this.schemaFile);
			factory.setSchema(schema);
		}
		Document config = factory.newDocumentBuilder().parse(this.configFile);
		this.loadCommandlet(config);
		this.loadCommandletMapping(config);
		this.loaded = true;
	}
	
	private void loadCommandlet(Document config) throws InstantiationException,
	        IllegalAccessException, ClassNotFoundException
	{
		NodeList commandlets = config.getElementsByTagNameNS(
		        CommandletLoader.NS, "commandlets");
		int size = commandlets.getLength();
		if (size == 1)
		{
			Element cmdlets = (Element) commandlets.item(0);
			commandlets = cmdlets.getElementsByTagNameNS(CommandletLoader.NS,
			        "commandlet");
			size = commandlets.getLength();
			if (size > 0)
			{
				for (int i = 0; i < size; i++)
				{
					Element commandlet = (Element) commandlets.item(i);
					NodeList nodes = commandlet.getElementsByTagNameNS(
					        CommandletLoader.NS, "commandlet-name");
					if (nodes.getLength() == 0)
					{
						continue;
					}
					String name = nodes.item(0).getTextContent();
					nodes = commandlet.getElementsByTagNameNS(
					        CommandletLoader.NS, "commandlet-class");
					if (nodes.getLength() == 0)
					{
						continue;
					}
					String className = nodes.item(0).getTextContent();
					nodes = commandlet.getElementsByTagNameNS(
					        CommandletLoader.NS, "load-on-startup");
					boolean loadOnStartup = false;
					if (nodes.getLength() != 0)
					{
						loadOnStartup = Boolean.parseBoolean(nodes.item(0)
						        .getTextContent());
					}
					this.nameToClass.put(name, className);
					if (loadOnStartup)
					{
						Commandlet command = (Commandlet) Class.forName(
						        className).newInstance();
						this.nameToObj.put(name, command);
					}
					
				}
			}
		}
		
	}
	
	private void loadCommandletMapping(Document config)
	        throws InstantiationException, IllegalAccessException,
	        ClassNotFoundException
	{
		NodeList mappings = config.getElementsByTagNameNS(CommandletLoader.NS,
		        "commandlet-mappings");
		
		int size = mappings.getLength();
		if (size == 1)
		{
			Element cmdMappings = (Element) mappings.item(0);
			mappings = cmdMappings.getElementsByTagNameNS(CommandletLoader.NS,
			        "commandlet-mapping");
			size = mappings.getLength();
		}
		if (size > 0)
		{
			for (int i = 0; i < size; i++)
			{
				Element commandletMapping = (Element) mappings.item(i);
				NodeList nodes = commandletMapping.getElementsByTagNameNS(
				        CommandletLoader.NS, "commandlet-name");
				if (nodes.getLength() == 0)
				{
					continue;
				}
				String name = nodes.item(0).getTextContent();
				nodes = commandletMapping.getElementsByTagNameNS(
				        CommandletLoader.NS, "url-pattern");
				int length = nodes.getLength();
				if (length == 0)
				{
					continue;
				}
				for (int j = 0; j < length; j++)
				{
					String pattern = nodes.item(j).getTextContent();
					this.urlToName.put(pattern, name);
				}
			}
		}
	}
	
}
