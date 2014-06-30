package guang.crawler.crawlWorker.plugin;

import guang.crawler.commons.DataFields;
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
public class ExtractFiledToSavePlugin implements DownloadPlugin {

	private ComponentLoader<FieldsExtractor>	fieldsExtractorLoader;

	public ExtractFiledToSavePlugin() throws ConfigLoadException {
		String configFileName = WorkerConfig.me().getCrawlerHome()
		        + "/conf/crawler-worker/filed-extractors.xml";
		File configFile = new File(configFileName);
		String schemaFileName = WorkerConfig.me().getCrawlerHome()
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
		DataFields result = new DataFields();
		if (page != null) {
			// 获取URLExtractor
			FieldsExtractor extractor = this.fieldsExtractorLoader
					.getComponent(page.getWebURL().getURL());
			if (extractor != null) {
				// 利用URLExtractor抽取URL列表
				extractor.extractFields(page, result);
			}
			page.setDataToSave(result);
			return true;
		}
		return false;
	}

}
