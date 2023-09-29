package com.mycom.mailapp.mailsendsystem.controller;

import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;

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
        if (files != null) {
            for (File file: files
                 ) {
                try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // 处理每一行文本
                        logs.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logsText.setText(String.valueOf(logs));
    }

}
