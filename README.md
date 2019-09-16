Korean Analysis Plugin for ElasticSearch
==================================

The Korean Analysis plugin integrates Lucene Korean analysis module into elasticsearch.

### Install plugin

create plugin zip file

```bash
(base)  chokolet@icheol-uui-MacBookPro  ~/[path]/[to]/elasticsearch-7.3.1/zipfiles  ls -altr
total 48
-rw-r--r--@  1 chokolet  staff    231  9  9 00:15 plguin-security.policy
-rw-r--r--   1 chokolet  staff  13257  9  9 00:17 elastic-morph-plugin-0.1.jar
-rw-r--r--   1 chokolet  staff   1400  9  9 00:19 plugin-descriptor.properties
(base)  chokolet@icheol-uui-MacBookPro  ~/ck/elasticsearch/elasticsearch-7.3.1/zipfiles  zip elastic-morph-plugin-0.1.zip ./*
```

install plugin
```bash
./bin/elasticsearch-plugin install file://[path]/[to]/[file]/elastic-morph-plugin-0.1.zip
-> Downloading file://[path]/[to]/[file]/elastic-morph-plugin-0.1.zip
[=================================================] 100%
-> Installed custom-morph

-rw-r--r--  1 chokolet  staff       231  9  9 00:08 plguin-security.policy
-rw-r--r--  1 chokolet  staff      1465  9  9 00:17 plugin-descriptor.properties
drwxr-xr-x@ 3 chokolet  staff        96  9  9 00:19 ..
drwxr-xr-x  6 chokolet  staff       192  9  9 00:39 .
-rw-r--r--  1 chokolet  staff     13259  9  9 00:52 elastic-morph-plugin-0.1.jar

```

And restart elasticsearch service:
```
base)  chokolet@icheol-uui-MacBookPro  ~/ck/elasticsearch/elasticsearch-7.3.1/bin  ./elasticsearch
.....

```

### test elasticseach korean analysis

```bash
curl -XDELETE "127.0.0.1:9200/test"

curl -XPUT "http://127.0.0.1:9200/test/?pretty" -d '{
  "settings": {
    "analysis": {
      "analyzer": {
        "eun_lemma": {
          "type": "custom",
          "tokenizer": "lemma"
        },
        "eun_token": {
          "type": "custom",
          "tokenizer": "token"
        }

      },
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

      }
    }
  }
}' -H 'Content-Type: application/json'

curl -XGET "http://127.0.0.1:9200/test/_analyze/?pretty" -H 'Content-Type: application/json' -d '{ "analyzer": "eun_lemma", "text": "아버지가 방에 들어가신다." }'
curl -XGET "http://127.0.0.1:9200/test/_analyze/?pretty" -H 'Content-Type: application/json' -d '{ "analyzer": "eun_token", "text": "아버지가 방에 들어가신다." }'
```



Result:

```
{
  "tokens" : [
    {
      "token" : "아버지",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "NNG",
      "position" : 1
    },
    {
      "token" : "가",
      "start_offset" : 3,
      "end_offset" : 3,
      "type" : "JKS",
      "position" : 2
    },
    {
      "token" : "방",
      "start_offset" : 5,
      "end_offset" : 5,
      "type" : "NNG",
      "position" : 3
    },
    {
      "token" : "에",
      "start_offset" : 6,
      "end_offset" : 6,
      "type" : "JKB",
      "position" : 4
    },
    {
      "token" : "들어가",
      "start_offset" : 8,
      "end_offset" : 10,
      "type" : "VV",
      "position" : 5
    },
    {
      "token" : "시",
      "start_offset" : 11,
      "end_offset" : 11,
      "type" : "EP",
      "position" : 6
    },
    {
      "token" : "ᆫ다",
      "start_offset" : 11,
      "end_offset" : 12,
      "type" : "EF",
      "position" : 6
    },
    {
      "token" : ".",
      "start_offset" : 13,
      "end_offset" : 13,
      "type" : "SF",
      "position" : 7
    }
  ]
}
....
```



- Use analyzer : https://github.com/chokolet/kor-nlp-lib/tree/no-nori
- see more [plugin_note.md](./plugin_note.md)




## Built With

- Maven 
- Java 1.8 이상
- Lombok
- elastic search 7.3.1




## Authors

chickin7@gmail.com