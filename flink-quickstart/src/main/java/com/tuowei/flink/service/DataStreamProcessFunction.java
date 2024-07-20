package com.tuowei.flink.service;

public interface DataStreamProcessFunction {
    String doSomeThing() throws Exception;

    void mySqlProcess() throws Exception;

    void mySqlProcessBatch() throws Exception;

    void mongoProcess();

    void zhuiJiaHeBing() throws Exception;

    void hengXiangLianjie() throws Exception;

    void fenZuHuiZong() throws Exception;

    void shuJuShaiXuan();

    void ziDuanSheZhi() throws Exception;

    void hangZhuanlie();

    void quChong();
}
