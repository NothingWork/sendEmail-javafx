package com.mycom.mailapp.mailsendsystem.tools;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.web.HTMLEditor;

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

}