package com.mycom.mailapp.mailsendsystem.tools;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Yun
 * @version 1.0
 * @description: 发送邮件
 * @date 2023/9/24
 */
public class SendMailTools {
    //创建不带附件的邮件邮件
    public MimeMessage createSimpleMimeMessage(Session session,
                                               String sendMail,
                                               String receiveMail,
                                               String subject,
                                               String content)
            throws UnsupportedEncodingException, javax.mail.MessagingException {
        // 1.创建一封邮件
        MimeMessage mimeMessage = new MimeMessage(session);
        // 2.From：发件人
        mimeMessage.setFrom(new InternetAddress(sendMail, sendMail, "UTF-8"));
        // 3.To：收件人（可以增加多个收件人、抄送、密送）
        mimeMessage.setRecipients(MimeMessage.RecipientType.TO, new InternetAddress[]{new InternetAddress(receiveMail
                , receiveMail, "UTF-8")});
        // 4.邮件主题
        mimeMessage.setSubject(subject, "UTF-8");
        // 5.Content：邮件正文（可以用HTML标签）
        mimeMessage.setContent(content, "text/html;charset=UTF-8");
        // 6.设置发件时间
        mimeMessage.setSentDate(new Date());
        // 7.保存设置
        mimeMessage.saveChanges();

        return mimeMessage;
    }

    //创建带附件的邮件
    public MimeMessage createConflexMimeMessage(Session session,
                                                String sendMail,
                                                String receiveMail,
                                                String subject,
                                                String content,
                                                String filePath)
            throws UnsupportedEncodingException,MessagingException {
        // 1.创建邮件对象
        MimeMessage message = new MimeMessage(session);

        // 2.Form：发件人
        message.setFrom(new InternetAddress(sendMail, sendMail, "UTF-8"));

        // 3.TO：收件人（可以增加多个收件人、抄送、密送）
        message.addRecipients(MimeMessage.RecipientType.TO, new InternetAddress[]{new InternetAddress(receiveMail,
                receiveMail, "UTF-8")});

        // 4.Subject：邮件主题
        message.setSubject(subject, "UTF-8");

        // 5.创建附件”节点“
        MimeBodyPart attachment = new MimeBodyPart();
        DataHandler dataHandler = new DataHandler(new FileDataSource(filePath));
        attachment.setDataHandler(dataHandler);
        attachment.setFileName(MimeUtility.encodeText(dataHandler.getName()));

        // 6.创建文本”节点“
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(content, "text/html;charset=UTF-8");

        // 7.设置文本和附件的关系（合成一个大的混合的节点）
        MimeMultipart totalPart = new MimeMultipart();
        totalPart.addBodyPart(text);
        totalPart.addBodyPart(attachment);// 如果有多个附件，可以创建多个多次添加
        totalPart.setSubType("mixed");

        // 8.设置整个邮件的关系（将最终的混合节点作为邮件的内容）
        message.setContent(totalPart);

        // 9.设置发件时间
        message.setSentDate(new Date());

        // 10.保存上面的所有设置
        message.saveChanges();

        return message;
    }
    //发送邮件
    public void sendEmail(String sendEmail,
                          String receviceEmail,
                          String subject,
                          String content,
                          String code,
                          String server,
                          String selectedFilePath) throws MessagingException, UnsupportedEncodingException {
        // 1.创建参数配置, 用于连接邮件服务器的参数配置
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", server);
        properties.setProperty("mail.smtp.auth", "true");

        // 2.根据配置创建会话对象，用于和邮件服务器交互
        Session session = Session.getInstance(properties);
        session.setDebug(true);
        // 3.创建一封简单或复杂邮件
        MimeMessage message = null;
        if (selectedFilePath.equals("")) {
            message = createSimpleMimeMessage(session, sendEmail, receviceEmail, subject, content);
        } else {
            message = createConflexMimeMessage(session, sendEmail, receviceEmail, subject, content, selectedFilePath);
        }

        // 4.根据Session获取邮件传输对象
        Transport transport = session.getTransport();

        // 5.使用邮箱账号和密码连接邮件服务器，这里认证的邮箱必须与message中的发件人邮箱一致，否则报错
        transport.connect(sendEmail, code);

        // 6.发送邮件，发到所有的收件地址，message.getAllRecipients()获取到的是在创建邮件对象时添加的所有的收件人、抄送人、密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7.关闭连接
        transport.close();

    }

    //获取多个任务的执行时间和他们是否会被重复发送，放入数组中
    public List<String> getTime(){
        FileTools fileTools = new FileTools();
        //1.获取到各个任务文件名
        String[] tasks = fileTools.getFileList(fileTools.taskFilePath).toArray(new String[0]);
        //2.读取这些文件获取到各个任务的时间节点和他们是否重复发送
        Map<String,String> dataMap;//过渡存放读取信息的map
        List<String> sendDate = new ArrayList<>();//存放多组信息的String数组，包含日期、时、分、秒、是否重复发送、任务名称
        for (String task : tasks) {
            dataMap = fileTools.readReturnMap(fileTools.taskFilePath + "/" + task + ".properties");
            sendDate.add(dataMap.get("sendDate")+" "+dataMap.get("isRepeat")+" "+task);
        }
        return sendDate;
    }

}