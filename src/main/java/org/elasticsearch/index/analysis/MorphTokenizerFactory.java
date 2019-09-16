package org.elasticsearch.index.analysis;

import com.github.chokolet.util.InvalidFormatException;
import org.apache.lucene.analysis.MorphTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * 토크나이저 공장
 */
public class MorphTokenizerFactory extends AbstractTokenizerFactory {

    private TokenizerOption options;

    /**
     * 색인 시 받아오는 정보에 대해 설정값으로 저장
     *
     * @param indexSettings
     * @param env
     * @param name
     * @param settings
     */
    @Inject
    public MorphTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {

        super(indexSettings, settings);
        String pluginName = "elastic-morph";
        options = new TokenizerOption();
        options.setMorphName(settings.get("morphName"));
        options.setTokenOpt(settings.get("tokenOption"));

    }

    /**
     * 생성
     *
     * @return
     */
    @Override
    public Tokenizer create() {
        try {
            return new MorphTokenizer(options);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
