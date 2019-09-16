# plugin 정리



## Analysis plugin 주요 소스

```
      "tokenizer": {
        "lemma": {
          "type": "MorphTokenizer",
          "morphName": "SEUNJEON",
          "tokenOption": "lemma"
        },
        "token": {
          "type": "MorphTokenizer",
          "morphName": "SEUNJEON",
          "tokenOption": "token"
        }
```

- plugin tokenizer name ( type ) : [source](https://github.com/chokolet/elasticsearch-analysis-seunjeon/blob/master/src/main/java/org/elasticsearch/plugins/analysis/MorphAnalysisPlugin.java#L20)

- option ( morphName, tokenOption ) : [source](https://github.com/chokolet/elasticsearch-analysis-seunjeon/blob/master/src/main/java/org/elasticsearch/index/analysis/MorphTokenizerFactory.java#L27)

- Initialize ( create method ) : [source](https://github.com/chokolet/elasticsearch-analysis-seunjeon/blob/master/src/main/java/org/elasticsearch/index/analysis/MorphTokenizerFactory.java#L43)

- extend tokenizer class : [source](https://github.com/chokolet/elasticsearch-analysis-seunjeon/blob/master/src/main/java/org/apache/lucene/analysis/MorphTokenizer.java#L19)

  - 주요 class

    ```java
    // 키워드 관련 속성
    charTermAttribute = addAttribute(CharTermAttribute.class);
    // 테그 관련 속성
    typeAttribute = addAttribute(TypeAttribute.class);
    // Offset  관련 속성
    offsetAttribute = addAttribute(OffsetAttribute.class);
    // 텀위치 증가치
    posIncAtt = addAttribute(PositionIncrementAttribute.class);
    ```

  - 주요 method 

    ```java
    // factory patern & class reflection 
    class initialize
    // 하나씩 불러서 사용
    public boolean incrementToken() 
    // List 에 분석 결과 담기
    public void reset()
    //초기화
    public void end()
    ```

  - 흐름 

    - Initialize class -> reset method -> incrementToken -> end

## Plugin 추가 방법

plugin 설치 전 필요 파일

```bash
elastic-morph-plugin-0.1.jar              plguin-security.policy
kor-nlp-lib-1.0-jar-with-dependencies.jar plugin-descriptor.properties
```

> 1. elasticsearch - plugin package ( elastic-morph-plugin-0.1.jar )
> 2. 보안 설정 ( plguin-security.policy )
>    - java 보안 설정 참고 해서 작성 ''[oracle document](https://docs.oracle.com/javase/7/docs/technotes/guides/security/PolicyFiles.html)''
> 3. 플러그인 설명 ( plugin-descriptor.properties )
>    - name : 플러그인 명
>    - classname : reflection 시 사용되는 classname
>    - java.version : elasticsearch 에서 제공하는 java version
>    - elasthcsearch.version : elastic search 버전
> 4. 기타 dependencies jar 들



NOTICE

- Elasticsearch - plugin 개발 시 elasticsearch에서 제공하고 있는 라이브러리 ( ex > nori, collections ) 를 재 패키징 해서 플러그인으로 사용 시 오류가 생길 수 있음



plugin 설치 binary 실행

> ./elasticsearch-plugin install [file/url_path_to_zip_path]

```bash
#>./elasticsearch-plugin install file:///Users/chokolet/ck/elasticsearch/elasticsearch-7.3.1/custom-morph.zip
-> Downloading file:///Users/chokolet/ck/elasticsearch/elasticsearch-7.3.1/custom-morph.zip
[=================================================] 100%
-> Installed custom-morph
#> cd ../plugins/custom-morph
#> ls
elastic-morph-plugin-0.1.jar              plguin-security.policy
kor-nlp-lib-1.0-jar-with-dependencies.jar plugin-descriptor.properties
```



## C / C++ 사용 시 적용 how to

__mecab - eunjeon elasticsearch 적용 방법 참고함__

1. 서버에 맞는 gcc 컴파일 및 so file package 
   - JNI 혹은 JNA 사용, 아래는 JNI 예제
   - elasticsearch 에서 포팅하기 쉽게 c / c++ 에서 pre coding 하면 좀 편함
   - 사전 혹은 local file이 c / c++ 소스에서 필요한 경우 변수로 접근할 수 있도록 초기화 부분에 개발 하면 됨
     - mecab - eunjeon morph 에서는 다양한 방법 ( ex $home 을 본다던지.. )으로 접근
     - [mecab 사전 로드 소스](https://bitbucket.org/eunjeon/mecab-ko/src/908db8de3cb5f4931b4e3a7a5a3894daefb98c37/src/utils.cpp#lines-292) 참조
2. elasticsearch plugin 로드 시 so file load 하는 init 부분 추가
   - [eunjeon JNI So Load 소스 참조](https://bitbucket.org/eunjeon/mecab-ko-lucene-analyzer/src/00e20d7de481d355affdc1a9537cbc896401555a/mecab-loader/src/main/java/org/bitbucket/eunjeon/mecab_ko_mecab_loader/MeCabLoader.java#lines-28)

```java
 /**
     * 초기화
     * @param maHome
     * @param soPath
     */
    private void initialize(String maHome, String soPath) {
        int retValue;
      
      // elasticsearch so file load 시 파일 접근 보안 설정
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            if (!new File(soPath).isFile()) {
                return null;
            }

            try {
                System.load(soPath);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
                throw new UnsatisfiedLinkError(
                        "Cannot load the native code.\n"
                                + "Make sure your LD_LIBRARY_PATH contains " + soPath + " path.\n" + e);
            }
            return null;
        });
        moranMorph = new JNIMorphAnalyzer();
        retValue = moranMorph.all_initialize(maHome);

        if (retValue != 1) {
            System.out.println("Korean Morph InitializeFail");
        }
    }
```

3. elasticsearch 규격에 맞게 class 상속 받아 개발 후 패키징

4. zip 생성 시 dependency로 jni 프로젝트 패키징 후 같이 반영

5. package 설치 후 so 파일을 못 불러오는 경우 java security policy 파일 ( plguin-security.policy ) 확인 및 수정

   

## 소스 수정 시

1. 이미 설치가 끝났고, 소스 추가 및 수정 시 ~/elasticsearch/plugin/[플로그인명]/ 안에 있는 jar 파일 및 기타 설정 파일을 변경해도 반영 된다.
2. Elasticsearch 가 multi cluster 인 경우 전 서버에 다 반영해줘야 된다.