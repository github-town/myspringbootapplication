package com.tuowei.flink.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuowei.flink.function.UserVo;
import com.tuowei.flink.service.DataStreamProcessFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.calcite.shaded.com.google.common.collect.Maps;
import org.apache.flink.cdc.connectors.mysql.source.MySqlSource;
import org.apache.flink.connector.jdbc.JdbcInputFormat;


import org.apache.flink.cdc.debezium.JsonDebeziumDeserializationSchema;
//import org.apache.flink.connector.mongodb.source.MongoSource;
//import org.apache.flink.connector.mongodb.source.enumerator.splitter.PartitionStrategy;
//import org.apache.flink.connector.mongodb.source.reader.deserializer.MongoDeserializationSchema;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;
//import org.bson.BsonDocument;
import org.springframework.stereotype.Service;
import org.apache.flink.types.Row;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@Service
public class DataStreamProcessFunctionService implements DataStreamProcessFunction {

    @Override
    public String doSomeThing() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        List<UserVo> userVos = Arrays.asList(new UserVo("xj1", 10),
                new UserVo("xj2", 8),
                new UserVo("xj3", 22),
                new UserVo("xj4", 19));
        final DataStreamSource<UserVo> stream = env.fromCollection(userVos);

        stream.print();
        final SingleOutputStreamOperator<UserVo> stream2 = stream.map(new MapFunction<UserVo, UserVo>() {

            @Override
            public UserVo map(UserVo userVo) throws Exception {
                Integer newAge = userVo.getAge() * 2;
                userVo.setAge(newAge);
                return userVo;
            }
        });
        stream2.print();
        final DataStream<UserVo> union = stream.union(stream2);
        union.print();
        env.execute("CollectionSink");
        return "doSomeThing";
    }

    @Override
    public void mySqlProcess() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setBufferTimeout(100);

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db8501") // set captured database
                .tableList("db8501.dept") // set captured table
                .username("root")
                .password("1234")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        // enable checkpoint
        env.enableCheckpointing(3000);

        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                .setParallelism(3)
                .writeAsText("file/flinkSinkFileSingleTable.text", FileSystem.WriteMode.OVERWRITE)
//                .print()
                .setParallelism(1);

//        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
//                .setParallelism(3)
//                .flatMap(new MyMapFunction1())
//                .keyBy(key -> key.f0)
////                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
//                .sum(1 )
//                .print()
//                .setParallelism(1);

        env.execute("Print MySQL Snapshot + Binlog");
    }

    @Override
    public void mySqlProcessBatch() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String sqlDept = "SELECT * FROM dept";
        JdbcInputFormat jdbcInputFormatDept = JdbcInputFormat.buildJdbcInputFormat()
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://localhost:3306/db8501")
                .setUsername("root")
                .setPassword("1234")
                .setQuery(sqlDept)
                .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO))
                .finish();

        DataStream<Row> streamDept = env.createInput(jdbcInputFormatDept).setParallelism(3);

        SingleOutputStreamOperator<JSONObject> deptMap = streamDept.map(new MapFunction<Row, JSONObject>() {
            @Override
            public JSONObject map(Row row) throws Exception {
                JSONObject jsonObject = new JSONObject();
                for (String name : row.getFieldNames(true)) {
                    jsonObject.put(name, row.getField(name));
                }
                return jsonObject;
            }
        }).name("map");

//        String sqlEmp = "SELECT * FROM emp";
//        JdbcInputFormat jdbcInputFormatEmp = JdbcInputFormat.buildJdbcInputFormat()
//                .setDrivername("com.mysql.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://localhost:3306/db8501")
//                .setUsername("root")
//                .setPassword("1234")
//                .setQuery(sqlEmp)
//                .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO))
//                .finish();
//
//        DataStream<Row> streamEmp = env.createInput(jdbcInputFormatEmp).setParallelism(3);
//
//        streamDept.map(new MapFunction<Row, String>() {
//        })

        deptMap.writeAsText("file/flinkSinkFileSingleTableBatch.text", FileSystem.WriteMode.OVERWRITE)
                .setParallelism(1);

        // 进行流批处理
        // 例如，对数据进行处理后写入另一个表
        // 注意：这里需要有相应的输出格式和输出 sink

        env.execute("MySql Stream Batch Processing");
    }

    @Override
    public void mongoProcess() {
////        com.mongodb.client.model.Filters.empty()Lorg/bson/conversions/Bson;
//        MongoSource<String> source = MongoSource.<String>builder()
//                .setUri("mongodb://admin:123456@192.10.51.90:27017")
//                .setDatabase("micro_dynamic_test_yzk")
//                .setCollection("table_element")
////                .setProjectedFields("_id")
//                .setFetchSize(2048)
//                .setLimit(10000)
//                .setNoCursorTimeout(true)
//                .setPartitionStrategy(PartitionStrategy.SAMPLE)
//                .setPartitionSize(MemorySize.ofMebiBytes(64))
//                .setSamplesPerPartition(10)
//                .setDeserializationSchema(new MongoDeserializationSchema<String>() {
//                    @Override
//                    public String deserialize(BsonDocument document) {
//                        return document.toString();
//                    }
//
//                    @Override
//                    public TypeInformation<String> getProducedType() {
//                        return BasicTypeInfo.STRING_TYPE_INFO;
//                    }
//                })
//                .build();
//
//        // 配置MongoDBSource
////        MongoDBSource<Document> mongoSource = MongoDBSource.<Document>builder()
////                .collection("yourCollectionName") // 替换为你的集合名
////                .host("localhost") // 替换为你的MongoDB地址
////                .port(27017) // 替换为你的MongoDB端口
////                .build();
////
////        DataStream<Tuple2<String, Integer>> dataStream = env
////                .addSource(mongoSource)
////                .map(new MapFunction<Document, Tuple2<String, Integer>>() {
////                    @Override
////                    public Tuple2<String, Integer> map(Document value) throws Exception {
////                        // 假设你的文档有一个名为"name"和"age"的字段
////                        return Tuple2.of(value.getString("name"), value.getInteger("age"));
////                    }
////                });
////
////        // 进一步处理dataStream...
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        env.fromSource(source, WatermarkStrategy.noWatermarks(), "MongoDB-Source")
//                .setParallelism(2).
//                writeAsText("file/flinkSinkFileMongo.text", FileSystem.WriteMode.OVERWRITE)
//                .setParallelism(1);
    }


    @Override
    public void zhuiJiaHeBing() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setBufferTimeout(100);
        env.enableCheckpointing(3000);
        // env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        MySqlSource<String> deptSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db8501") // set captured database
                .tableList("db8501.dept") // set captured table
                .username("root")
                .password("1234")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        MySqlSource<String> empSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db8501") // set captured database
                .tableList("db8501.emp") // set captured table
                .username("root")
                .password("1234")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        DataStreamSource<String> dept = env.fromSource(deptSource, WatermarkStrategy.noWatermarks(), "dept");
        DataStreamSource<String> emp = env.fromSource(empSource, WatermarkStrategy.noWatermarks(), "emp");

        DataStream<JSONObject> zhuijiahebin = zhuiJiaHeBingStreams(dept, emp);

        zhuijiahebin.writeAsText("file/SinkFileZhuiJiaHeBing.text", FileSystem.WriteMode.OVERWRITE)
                .setParallelism(1);
        env.executeAsync("hengXiangLianjie");
    }

    private DataStream<JSONObject> zhuiJiaHeBingStreams(DataStreamSource<String> streamOne, DataStreamSource<String> StreamTwo) {
        // mysql导出数据特殊处理
        SingleOutputStreamOperator<JSONObject> streamOneJsonObj = streamOne.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(JSONObject.parseObject(s).get("after").toString());
            }
        });
        SingleOutputStreamOperator<JSONObject> streamTwoJsonObj = StreamTwo.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(JSONObject.parseObject(s).get("after").toString());
            }
        });
//        return streamOneJsonObj.union(streamTwoJsonObj);
        return streamOneJsonObj.connect(streamTwoJsonObj).map(new CoMapFunction<JSONObject, JSONObject, JSONObject>() {
            @Override
            public JSONObject map1(JSONObject object) throws Exception {
                object.put("connect","connect1");
                return object;
            }

            @Override
            public JSONObject map2(JSONObject object) throws Exception {
                object.put("connect","connect2");
                return object;
            }
        });
    }

    @Override
    public void hengXiangLianjie() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setBufferTimeout(100);
        env.enableCheckpointing(3000);
//        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        MySqlSource<String> deptSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db8501") // set captured database
                .tableList("db8501.dept") // set captured table
                .username("root")
                .password("1234")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        MySqlSource<String> empSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db8501") // set captured database
                .tableList("db8501.emp") // set captured table
                .username("root")
                .password("1234")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        DataStreamSource<String> dept = env.fromSource(deptSource, WatermarkStrategy.noWatermarks(), "dept");
        DataStreamSource<String> emp = env.fromSource(empSource, WatermarkStrategy.noWatermarks(), "emp");

        hengXiangLianJieStreams(dept, emp, Arrays.asList("deptNo"), Arrays.asList("dNo"))
                .writeAsText("file/SinkFileHengXiangLianJie.text", FileSystem.WriteMode.OVERWRITE)
                .setParallelism(1);
        env.executeAsync("hengXiangLianjie");
    }

    private DataStream<String> hengXiangLianJieStreams(DataStream<String> streamOne, DataStream<String> StreamTwo, List<String> fieldOneList, List<String> fieldTwoList) {
        // mysql导出数据特殊处理
        SingleOutputStreamOperator<JSONObject> streamOneJsonObj = streamOne.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(JSONObject.parseObject(s).get("after").toString());
            }
        });
        SingleOutputStreamOperator<JSONObject> streamTwoJsonObj = StreamTwo.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(JSONObject.parseObject(s).get("after").toString());
            }
        });

//        SingleOutputStreamOperator<JSONObject> streamOneJsonObj = streamOne.map(new MapFunction<String, JSONObject>() {
//            @Override
//            public JSONObject map(String jsonString) {
//                return JSONObject.parseObject(jsonString);
//            }
//        });
//        SingleOutputStreamOperator<JSONObject> streamTwoJsonObj = StreamTwo.map(new MapFunction<String, JSONObject>() {
//            @Override
//            public JSONObject map(String jsonString) {
//                return JSONObject.parseObject(jsonString);
//            }
//        });

        return streamOneJsonObj.join(streamTwoJsonObj).where(
                new KeySelector<JSONObject, String>() {
            @Override
            public String getKey(JSONObject object) {
                StringBuilder builder = new StringBuilder();
                fieldOneList.forEach(item -> builder.append(object.get(item)).append("&"));
                return builder.toString();
            }
        }
        ).equalTo(new KeySelector<JSONObject, String>() {
            @Override
            public String getKey(JSONObject object) {
                StringBuilder builder = new StringBuilder();
                fieldTwoList.forEach(item -> builder.append(object.get(item)).append("&"));
                return builder.toString();
            }
        }).window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .apply(new JoinFunction<JSONObject, JSONObject, String>() {
                    @Override
                    public String join(JSONObject first, JSONObject second) throws Exception {
                        first.putAll(second);
                        return first.toJSONString();
                    }
                });
    }

    @Override
    public void fenZuHuiZong() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setBufferTimeout(100);
        env.enableCheckpointing(3000);
        SingleOutputStreamOperator<String> mapedStream = getMySqlDataStreamEmp(env);

        // 构造合并计算参数
        List<String> keyByFields = Arrays.asList("dNo","sex","empNo");
        Map<String, String> summaryParamsMap = new HashMap<>();
//        summaryParamsMap.put("sal","count");
        summaryParamsMap.put("sal","sum");
//        summaryParamsMap.put("sal","max");
//        summaryParamsMap.put("sal","min");
//        summaryParamsMap.put("sal","average");

        fenZuHuiZongStream(mapedStream, keyByFields, summaryParamsMap).writeAsText("file/SinkFileFenZuHuiZong.text", FileSystem.WriteMode.OVERWRITE)
                .setParallelism(1);
        env.executeAsync("fenZuHuiZong");
    }

    private DataStream<JSONObject> fenZuHuiZongStream(DataStream<String> dataStream, List<String> keyByFields, Map<String, String> summaryParamsMap) {

        // 转换为JSONObject格式
        DataStream<JSONObject> mapedStream = dataStream.map((MapFunction<String, JSONObject>) s -> JSONObject.parseObject(s));

        // 字段分组
        mapedStream = mapedStream.keyBy(item -> {
            StringBuilder keyBuilder = new StringBuilder();
            keyByFields.forEach(field -> keyBuilder.append(String.valueOf(item.get(field))).append("&"));
            return keyBuilder.toString();
        });

        // 合并操作
        SingleOutputStreamOperator result = ((KeyedStream) mapedStream).window(TumblingProcessingTimeWindows.of(Time.seconds(5))).apply(new WindowFunction<JSONObject, JSONObject, String, Window>() {
            @Override
            public void apply(String key, Window window, Iterable<JSONObject> iterable, Collector<JSONObject> collector) throws Exception {
                log.info("integer:" + key);
                JSONObject jsonObject = null;
                int num = 0;
                for (JSONObject keyToJsonObject : iterable) {
                    ++num;
                    log.info(keyToJsonObject.toJSONString());

                    // 设置分组字段值
                    if (jsonObject == null) {
                        jsonObject = new JSONObject();
                        for (String keyField : keyByFields) {
                            jsonObject.put(keyField, keyToJsonObject.get(keyField));
                        }
                    }

                    for (Map.Entry<String, String> entry : summaryParamsMap.entrySet()) {
                        if ("sum".equals(entry.getValue())) {
                            jsonObject.computeIfPresent(entry.getKey() + "-" + entry.getValue(), new BiFunction<String, Object, Object>() {
                                @Override
                                public Object apply(String key, Object value) {
                                    // 注意value的类型
                                    if (value instanceof Double) {
                                        return ((Double) value) + (Double) keyToJsonObject.get(entry.getKey());
                                    }
                                    if (value instanceof Float) {
                                        return ((Float) value) + (Float) keyToJsonObject.get(entry.getKey());
                                    }
                                    if (value instanceof Integer) {
                                        return ((Integer) value) + (Integer) keyToJsonObject.get(entry.getKey());
                                    }
                                    if (value instanceof String) {
                                        return Double.valueOf((String) value) + Double.valueOf((String) keyToJsonObject.get(entry.getKey()));
                                    }
                                    if (value instanceof BigDecimal) {
                                        return ((BigDecimal) value).add((BigDecimal) keyToJsonObject.get(entry.getKey()));
                                    }
                                    return ((BigDecimal) value).add((BigDecimal) keyToJsonObject.get(entry.getKey()));
                                }
                            });
                            jsonObject.putIfAbsent(entry.getKey() + "-" + entry.getValue(), keyToJsonObject.get(entry.getKey()));
                        }
                        if ("count".equals(entry.getValue())) {
                            jsonObject.put(entry.getKey() + "-" + "总数", num);
                        }
                        if ("max".equals(entry.getValue())) {
                            jsonObject.putIfAbsent(entry.getKey() + "-" + entry.getValue(), keyToJsonObject.get(entry.getKey()));
                            jsonObject.computeIfPresent(entry.getKey() + "-" + entry.getValue(), new BiFunction<String, Object, Object>() {
                                @Override
                                public Object apply(String key, Object value) {
                                    // 这里先默认数据都为Double类型
                                    return ((BigDecimal) value).compareTo((BigDecimal) keyToJsonObject.get(entry.getKey())) > 0 ? value : keyToJsonObject.get(entry.getKey());
                                }
                            });
                        }
                        if ("min".equals(entry.getValue())) {
                            jsonObject.putIfAbsent(entry.getKey() + "-" + entry.getValue(), keyToJsonObject.get(entry.getKey()));
                            jsonObject.computeIfPresent(entry.getKey() + "-" + entry.getValue(), new BiFunction<String, Object, Object>() {
                                @Override
                                public Object apply(String key, Object value) {
                                    // 这里先默认数据都为Double类型
                                    return ((BigDecimal) value).compareTo((BigDecimal) keyToJsonObject.get(entry.getKey())) < 0 ? value : keyToJsonObject.get(entry.getKey());
                                }
                            });
                        }
                        if ("average".equals(entry.getValue())) {
                            jsonObject.putIfAbsent(entry.getKey() + "-" + entry.getValue(), keyToJsonObject.get(entry.getKey()));
                            int finalNum = num;
                            jsonObject.computeIfPresent(entry.getKey() + "-" + entry.getValue(), new BiFunction<String, Object, Object>() {
                                @Override
                                public Object apply(String key, Object value) {
                                    // 这里先默认数据都为Double类型
                                    return ((BigDecimal) value).multiply(BigDecimal.valueOf(finalNum - 1)).add((BigDecimal) keyToJsonObject.get(entry.getKey())).divide(BigDecimal.valueOf(finalNum), 2, RoundingMode.HALF_UP);
                                }
                            });
                        }
                    }
                }
                collector.collect(jsonObject);
            }
        });
        return result;
    }

    @Override
    public void shuJuShaiXuan() {

    }

    @Override
    public void ziDuanSheZhi() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setBufferTimeout(100);
        env.enableCheckpointing(3000);
        SingleOutputStreamOperator<String> mapedStream = getMySqlDataStreamEmp(env);

        // 构造字段设置配置参数
        HashMap<String, String> map = Maps.newHashMap();
        map.put("empNo", null);
        map.put("empName", null);
        map.put("birthday", null);
        map.put("dNo", null);
        map.put("sex", "性别");
        map.put("sal", null);
        map.put("公式字段&formula", "SUM(sal,1000)");
        map.put("排名字段&ranking", "RANKING(dNo asc,sex desc)&GROUPBY(dNo,birthday yyyy-MM-dd)");
        map.put("累计字段&cumulative", "CUMULATIVE(dNo asc,sex desc)&GROUPBY(dNo,birthday yyyy-MM-dd)");

        // 字段设置处理
        ziDuanSheZhiStream(mapedStream).writeAsText("file/SinkFileZiDuanSheZhi.text", FileSystem.WriteMode.OVERWRITE)
                .setParallelism(1).name("ziDuanSheZhi");
        env.executeAsync("ziDuanSheZhi");
    }

    private SingleOutputStreamOperator<String> getMySqlDataStreamEmp(StreamExecutionEnvironment env) {
        MySqlSource<String> empSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("db8501") // set captured database
                .tableList("db8501.emp") // set captured table
                .username("root")
                .password("1234")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        DataStreamSource<String> emp = env.fromSource(empSource, WatermarkStrategy.noWatermarks(), "emp");
        return emp.map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return JSONObject.parseObject(s).get("after").toString();
            }
        });
    }

    private DataStream<String> ziDuanSheZhiStream(DataStream<String> mapedStream) {
        return mapedStream;
    }

    @Override
    public void hangZhuanlie() {

    }

    @Override
    public void quChong() {

    }
}
