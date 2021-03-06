package org.stagemonitor.core.configuration.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stagemonitor.core.elasticsearch.ElasticsearchClient;

public class ElasticsearchConfigurationSource extends AbstractConfigurationSource {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String path;
	private final ElasticsearchClient elasticsearchClient;
	private final String configurationId;
	private Map<String, String> configuration = new ConcurrentHashMap<String, String>();

	public ElasticsearchConfigurationSource(ElasticsearchClient elasticsearchClient, String configurationId) {
		this.elasticsearchClient = elasticsearchClient;
		this.configurationId = configurationId;
		this.path = "/stagemonitor/configuration/" + this.configurationId;
		reload();
	}

	@Override
	public String getValue(String key) {
		return configuration.get(key);
	}

	@Override
	public String getName() {
		return "Elasticsearch (" + configurationId + ")";
	}

	@Override
	public boolean isSavingPossible() {
		return true;
	}

	@Override
	public boolean isSavingPersistent() {
		return true;
	}

	@Override
	public void save(String key, String value) throws IOException {
		final HashMap<String, String> configToSend = new HashMap<String, String>(configuration);
		configToSend.put(key, value);
		elasticsearchClient.sendAsJson("PUT", path, configToSend);
		configuration.put(key, value);
	}

	@Override
	public void reload() {
		try {
			final JsonNode source = elasticsearchClient.getJson(path).get("_source");
			Map<String, String> conf = new HashMap<String, String>((int) Math.ceil(source.size() / 0.75));
			final Iterator<Map.Entry<String, JsonNode>> it = source.fields();
			while (it.hasNext()) {
				Map.Entry<String, JsonNode> next = it.next();
				conf.put(next.getKey(), next.getValue().asText());
			}
			configuration = conf;
		} catch (IOException e) {
			logger.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
		}
	}
}
