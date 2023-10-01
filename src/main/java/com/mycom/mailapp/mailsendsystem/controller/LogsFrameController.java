package com.mycom.mailapp.mailsendsystem.controller;

import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * @author Yun
 * @version 1.0
 * @description: TODO
 * @date 2023/9/29
 */
public class LogsFrameController {
    private Stage stage;
    @FXML
    private TextArea logsText;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    //初始化，读取日志文件
    @FXML
    void initialize(){
        StringBuilder logs = new StringBuilder();
        File folder = new File(new FileTools().logPath);
        File[] files = folder.listFiles();
        //创建一个按时间降序排序的treemap用来存储日志
        Map<String,String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String obj1, String obj2) {
                // 降序排序
                return obj2.compareTo(obj1);
            }
        });

        if (files != null) {
            for (File file: files
                 ) {
                try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // 处理每一行文本
                        map.put(line.substring(1,20),line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //这里将map.entrySet()转换成list,用于遍历将日志写入窗体中
        List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(map.entrySet());
        for(Map.Entry<String,String> mapping:list){
            logs.append(mapping.getValue()).append(System.lineSeparator());
        }

        logsText.setText(String.valueOf(logs));
    }

}
