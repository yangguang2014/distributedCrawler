package guang.crawler.crawlWorker.pageProcessor;

import guang.crawler.commons.Page;
import guang.crawler.crawlWorker.WorkerConfig;
import guang.crawler.extension.filedExtractor.FieldsExtractor;
import guang.crawler.localConfig.ComponentLoader;

import java.io.File;

/**
 * 从页面中获取需要保存的信息
 *
 * @author sun
 *
 */
public class ExtractDataToSavePlugin implements DownloadPlugin {
	
	private ComponentLoader<FieldsExtractor>	fieldsExtractorLoader;

	public ExtractDataToSavePlugin() throws ConfigLoadException {
		String configFileName = WorkerConfig.me()
		                                    .getCrawlerHome()
		        + "/conf/crawler-worker/filed-extractors.xml";
		File configFile = new File(configFileName);
		String schemaFileName = WorkerConfig.me()
		                                    .getCrawlerHome()
		        + "/etc/xsd/components.xsd";
		File schemaFile = new File(schemaFileName);
		this.fieldsExtractorLoader = new ComponentLoader<FieldsExtractor>(
		        configFile, schemaFile);
		try {
			this.fieldsExtractorLoader.load();
		} catch (Exception e) {
			throw new ConfigLoadException(
			        "load fileds-extractors.xml file failed!", e);
		}
	}
	
	@Override
	public boolean work(final Page page) {
		if (page != null) {
			// 获取URLExtractor
			FieldsExtractor extractor = this.fieldsExtractorLoader.getComponent(page.getWebURL()
			                                                                        .getURL());
			if (extractor != null) {
				// 利用URLExtractor抽取URL列表
				extractor.extractFields(page);
			}
			return true;
		}
		return false;
	}

}
