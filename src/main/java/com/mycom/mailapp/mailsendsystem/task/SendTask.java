package com.mycom.mailapp.mailsendsystem.task;

import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import com.mycom.mailapp.mailsendsystem.tools.SendMailTools;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Yun
 * @version 1.0
 * @description: 定时发送邮件任务
 * @date 2023/9/24
 */
public class SendTask extends TimerTask {
    private String name;//要执行的任务名称

    public void setName(String name) {
        this.name = name;
    }

    FileTools fileTools = new FileTools();

    //根据邮件信息生成邮件并发送
    public void sendMessage(String name) throws MessagingException, UnsupportedEncodingException {
        Map<String,String> dataMap = fileTools.readReturnMap(fileTools.taskFilePath + "/" + name + ".properties");
        SendMailTools sendMailTools = new SendMailTools();
        sendMailTools.sendEmail(dataMap.get("sendEmail"),
                dataMap.get("receiveEmail"),
                dataMap.get("subject"),
                dataMap.get("content"),
                dataMap.get("code"),
                dataMap.get("server"),
                dataMap.get("attach")
                );
    }
    public void run(){
        //发送邮件
        try {
            sendMessage(this.name);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
