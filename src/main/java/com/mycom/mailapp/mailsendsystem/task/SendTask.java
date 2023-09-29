package com.mycom.mailapp.mailsendsystem.task;

import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import com.mycom.mailapp.mailsendsystem.tools.SendMailTools;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Yun
 * @version 1.0
 * @description: 定时发送邮件任务
 * @date 2023/9/24
 */
public class SendTask extends TimerTask {
    private String name;//要执行的任务名称
    private int sendCount = 0;//失败邮件重新尝试次数

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
                fileTools.attachesPath + name
                );
    }
    public void run(){
        //编辑日志文本包含事件和执行任务名称
        Date date = new Date();
        // 创建一个SimpleDateFormat对象，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        // 使用format方法将Date对象转换为String
        String dateString = sdf.format(date);
        String log = "任务执行完成";
        //发送邮件
        try {
            //创建日志文件
            sendMessage(this.name);
        } catch (MessagingException | UnsupportedEncodingException e) {
            //处理邮件发送失败事件
            //发送失败后会尝试重新发送最多两次
            if(sendCount<2){
                sendCount++;
                log = "发送失败，重新发送尝试....";
                run();
            }
            else{
                //重新发送失败，编辑错误日志信息
                log = "发送失败,请检查邮件地址，授权码以及服务器地址是否填写正确，并检查attches文件夹保证附件文件完整性";
            }
        }
        finally {
            log ="["+ dateString+"]"+" "+this.name+" "+log;
            //写入日志文件
            fileTools.writeLogs(fileTools.logPath+"/"+this.name+".txt",log);
        }
    }

}
