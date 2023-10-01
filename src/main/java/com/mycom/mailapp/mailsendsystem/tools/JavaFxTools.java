package com.mycom.mailapp.mailsendsystem.tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yun
 * @version 1.0
 * @description: 工具类
 * @date 2023/9/24
 */
public class JavaFxTools {
    //重置文本框
    public void reset(TextInputControl... textInputControls) {
        for (TextInputControl textInputControl : textInputControls) {
            textInputControl.setText("");
        }
    }
    //检查文本框是否为空
    public boolean checkText(TextInputControl... textInputControls) {
        for (TextInputControl textInputControl : textInputControls) {
            if(textInputControl.getText().isEmpty()){
                return false;
            }
        }
        return true;
    }
    //重置时间
    public void resetTime(TextInputControl... textInputControls){
        for (TextInputControl textInputControl : textInputControls) {
            textInputControl.setText("00");
        }
    }
    //检查输入时间是否合法
    public boolean checkTime(String str) {
        Pattern pattern = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    //重置html编辑器
    public void resetHTML(HTMLEditor... htmlEditors) {
        for (HTMLEditor htmlEditor : htmlEditors) {
            htmlEditor.setHtmlText("");
        }
    }
    //重置label
    public void resetLabel(Label... labels){
        for (Label label : labels) {
            label.setText("");
        }
    }
    //为控件设置图标方法
    public void setLabeledImage(Labeled[] labeleds, String[] imagePaths) {
        for (int i = 0; i < labeleds.length; i++) {
            Image image = new Image(imagePaths[i]);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(25);
            imageView.setFitWidth(25);
            labeleds[i].setGraphic(imageView);
        }
    }
    //根据各个任务的执行情况，填入对应的状态
    public ObservableList<String> setTaskStage(ObservableList<String> oldlist) throws IOException {
        ObservableList<String> newlist = FXCollections.observableArrayList();
        for (String taskname:oldlist
             ) {
            File logfile = new File(new FileTools().logPath+"/"+taskname+".txt");
            if(!logfile.exists()){
                newlist.add(taskname+" "+"(未执行)");
            }
            else{
                //获取到最后一行文本,进行状态判断
                String line;String lastline = null;
                try (BufferedReader br = new BufferedReader(new FileReader(logfile))) {
                    while ((line = br.readLine()) != null) {
                        //不是最后一行，接着读取
                        lastline = line;
                    }
                }
                String[] strs = lastline.split(" ");
                if(strs[strs.length-1].equals("任务执行完成")){newlist.add(taskname+" "+"(发送成功)");}
                else{newlist.add(taskname+" "+"(发送失败)");}
            }
        }
        return newlist;
    }

}
