package com.tuowei.flink.function;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class MyMapFunction1 implements FlatMapFunction<String, Tuple2<String, Integer>> {


    @Override
    public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) {
        for (String word : sentence.split(",")) {
            out.collect(new Tuple2<>(word, 1));
        }
    }
}
