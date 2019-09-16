package org.elasticsearch.plugins.analysis;

import java.util.Map;
import static java.util.Collections.singletonMap;

import org.elasticsearch.index.analysis.MorphTokenizerFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class MorphAnalysisPlugin extends Plugin implements AnalysisPlugin {


    /**
     * 플러그인 설정 시 tokenizer 관련 참조하는 부분
     * @return
     */
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("MorphTokenizer", MorphTokenizerFactory::new);
    }

}
