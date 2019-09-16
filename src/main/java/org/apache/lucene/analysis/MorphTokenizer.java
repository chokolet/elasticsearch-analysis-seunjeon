package org.apache.lucene.analysis;

import com.github.chokolet.morph.vo.MorphInfoVo;
import com.github.chokolet.util.InvalidFormatException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.lucene.analysis.tokenattributes.*;
import org.elasticsearch.index.analysis.TokenizerOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Lucene Tokenizer 상속 받아서 형태소 분석기와 연동 하는 부분
 */
public class MorphTokenizer extends Tokenizer {

    protected CharTermAttribute charTermAttribute;
    protected TypeAttribute typeAttribute;
    protected OffsetAttribute offsetAttribute;
    protected String stringToTokenize;
    protected int sent_idx = 0;
    protected final int BUF_SIZE = 1024;

    protected PositionIncrementAttribute posIncAtt;
    protected PositionLengthAttribute posLenAtt;


    protected List<MorphDoc> docs;

    MorphAPI moranMaAPI;
    String tokenOpt;

    public MorphTokenizer(TokenizerOption options) throws InvalidFormatException {

        moranMaAPI = MorphAPI.getInstance(options.getMorphName());
        tokenOpt = options.getTokenOpt();
        // 키워드 관련 속성
        charTermAttribute = addAttribute(CharTermAttribute.class);
        // 테그 관련 속성
        typeAttribute = addAttribute(TypeAttribute.class);
        // Offset  관련 속성
        offsetAttribute = addAttribute(OffsetAttribute.class);
        // 텀위치 증가치
        posIncAtt = addAttribute(PositionIncrementAttribute.class);
        posLenAtt = addAttribute(PositionLengthAttribute.class);

    }

    /**
     * doc 을 증가 하면서 lucene 에 맞게 저장
     *
     * @return
     * @throws IOException
     */
    @Override
    public boolean incrementToken() throws IOException {

        this.charTermAttribute.setEmpty();

        if (this.sent_idx >= docs.size()) return false;

        MorphDoc doc = docs.get(this.sent_idx);
        this.charTermAttribute.append(doc.getText()); // string
        this.typeAttribute.setType(doc.getType()); // type
        System.out.println(doc);
        this.offsetAttribute.setOffset(doc.getStartOffset(), doc.getEndOffset()); // offset ( start, end )
        if (sent_idx <= 0) {
            this.posIncAtt.setPositionIncrement(2);
        } else {
            this.posIncAtt.setPositionIncrement(doc.getPositionInc());
        }
        sent_idx++;


        return true;
    }

    /**
     * 분석 수행 및 초기화 / 파싱
     *
     * @throws IOException
     */
    @Override
    public void reset() throws IOException {
        super.reset();

        char[] buffer = new char[BUF_SIZE];
        int numChars;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while ((numChars =
                    this.input.read(buffer, 0, buffer.length)) != -1) {
                stringBuilder.append(buffer, 0, numChars);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stringToTokenize = stringBuilder.toString();


        List<MorphInfoVo> morphInfoVoList = moranMaAPI.doAnalyze(stringToTokenize, tokenOpt);
        List<MorphDoc> morphDocList = convertMorphInfo(morphInfoVoList);

        docs = this.getDSMorphDoc(stringToTokenize, morphDocList);

        this.sent_idx = 0;
    }

    private List<MorphDoc> convertMorphInfo(List<MorphInfoVo> morphInfoVoList) {
        return morphInfoVoList.stream().map(elem -> {
            MorphDoc morphDoc = new MorphDoc();
            morphDoc.setText(elem.getKeyword());
            morphDoc.setType(elem.getMorphType());
            morphDoc.setIndex(elem.getTokIdx());
            morphDoc.setBegin(elem.getOrgBegin());
            return morphDoc;
        }).collect(Collectors.toList());
    }

    /**
     * 정리
     *
     * @throws IOException
     */
    public void end() throws IOException {
        super.end();
        int finalOffset = this.correctOffset(this.sent_idx);
        this.offsetAttribute.setOffset(finalOffset, finalOffset);
        docs = null;
    }

    /**
     * DSMorph에 Lucene에 맞는 값 추가 적재를 위한 로직
     *
     * @param targetData
     * @param tmpDSNlpDoc
     * @return
     */
    public static List<MorphDoc> getDSMorphDoc(String targetData, List<MorphDoc> tmpDSNlpDoc) {

        int incIndex;
        int locIdx = 1;
        int fromIdx = 0;
        int startIdx;
        int lastIdx = 0;
        int endIdx;
        int positionInc;
        MorphDoc morphDoc;

        List<MorphDoc> retDSNlp = new ArrayList<>();

        for (incIndex = 0; incIndex < tmpDSNlpDoc.size(); incIndex++) {

            morphDoc = tmpDSNlpDoc.get(incIndex);

            if (locIdx != morphDoc.getBegin()) {
                locIdx = morphDoc.getBegin();
//                fromIdx = lastIdx;
                positionInc = 1;
            } else {
                positionInc = 0;
            }

            startIdx = morphDoc.getBegin();
            endIdx = morphDoc.getText().length() + startIdx - 1;

            if (endIdx >= lastIdx) {
                lastIdx = endIdx;
            }

            morphDoc = new MorphDoc(startIdx, endIdx, positionInc, morphDoc.getText(), morphDoc.getBegin(), morphDoc.getIndex(), morphDoc.getType());
            retDSNlp.add(morphDoc);
        }
        return retDSNlp;
    }
}
