package com.tuowei.flink.controller;

import com.tuowei.flink.service.DataStreamProcessFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/flink")
public class MyFirstController {

    @Resource
    DataStreamProcessFunction dataStreamProcessFunctionService;

    @GetMapping("/firstProcess")
    public String firstProcess() throws Exception {
        return dataStreamProcessFunctionService.doSomeThing();
    }

    @GetMapping("/mySqlProcess")
    public void mySqlProcess() throws Exception {
        dataStreamProcessFunctionService.mySqlProcess();
    }

    @GetMapping("/mySqlProcessBatch")
    public void mySqlProcessBatch() throws Exception {
        dataStreamProcessFunctionService.mySqlProcessBatch();
    }

    @GetMapping("/mongoProcess")
    public void mongoProcess() throws Exception {
        dataStreamProcessFunctionService.mongoProcess();
    }

    @GetMapping("/hengXiangLianjie")
    public void hengXiangLianjie() throws Exception {
        dataStreamProcessFunctionService.hengXiangLianjie();
    }

    @GetMapping("/zhuiJiaHeBing")
    public void zhuiJiaHeBing() throws Exception {
        dataStreamProcessFunctionService.zhuiJiaHeBing();
    }

    @GetMapping("/fenZuHuiZong")
    public void fenZuHuiZong() throws Exception {
        dataStreamProcessFunctionService.fenZuHuiZong();
    }

    @GetMapping("/ziDuanSheZhi")
    public void ziDuanSheZhi() throws Exception {
        dataStreamProcessFunctionService.ziDuanSheZhi();
    }


}
