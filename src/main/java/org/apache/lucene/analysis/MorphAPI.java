package org.apache.lucene.analysis;

import com.github.chokolet.morph.MorphFactory;
import com.github.chokolet.morph.MorphInterface;
import com.github.chokolet.morph.vo.MorphInfoVo;
import com.github.chokolet.util.InvalidFormatException;

import java.util.List;

/**
 * Created by CK on 2019-06-13.
 * jni 연계 및 singleton
 */
public class MorphAPI {

    private MorphInterface morphInterface;

    public static MorphAPI getInstance(String morphName) throws InvalidFormatException {
        return DSMorphAPIHolder.initialize(morphName);
    }

    private static class DSMorphAPIHolder {

        public static MorphAPI morphAPI;


        public static MorphAPI initialize(String morphName) throws InvalidFormatException {
            if (morphAPI == null) {
                morphAPI = new MorphAPI();
                morphAPI.morphInterface = MorphFactory.getInstance().reuseFactoryMorph(morphName);
            }
            return morphAPI;
        }
    }


    public List<MorphInfoVo> doAnalyze(String targetText, String tokenType) {
        switch (tokenType.toLowerCase()) {
            case "token":
                return morphInterface.getToken(targetText);
            case "lemma":
            default:
                return morphInterface.getLemma(targetText);
        }
    }
}
