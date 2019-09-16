package org.elasticsearch.index.analysis;

/**
 * Created by CK on 2019-06-17.
 * 설정
 */
public class TokenizerOption {

    private String morphName;
    private String tokenOpt;

    public String getMorphName() {
        return morphName;
    }

    public void setMorphName(String morphName) {
        this.morphName = morphName;
    }

    public String getTokenOpt() {
        return tokenOpt;
    }

    public void setTokenOpt(String tokenOpt) {
        this.tokenOpt = tokenOpt;
    }
}
