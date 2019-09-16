package org.apache.lucene.analysis;

/**
 * Created by CK on 2019-06-13.
 * 형태소 분석 결과 담는 객체
 */
public class MorphDoc {

    // 키워드
    String text;

    // 시작 위치
    int startOffset;
    // 끝위치
    int endOffset;
    // 텀위치 증가치 ( for lucene )
    int positionInc;
    // 텀위치
    int begin;
    // 순서
    int index;
    // 품사정보 ( 뽑지는 않음.. )
    String type;

    //for 'Unable to invoke no-args constructor for class' err
    public MorphDoc() {

    }

    public MorphDoc(int startOffset, int endOffset, int positionInc, String text, int begin, int index, String type) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.positionInc = positionInc;
        this.text = text;
        this.begin = begin;
        this.index = index;
        this.type = type;
    }


    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPositionInc() {
        return positionInc;
    }

    public void setPositionInc(int positionInc) {
        this.positionInc = positionInc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DSMorphDoc{" +
                "startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", text='" + text + '\'' +
                ", begin=" + begin +
                ", index=" + index +
                ", positionInc=" + positionInc +
                ", type='" + type + '\'' +
                '}';
    }
}
