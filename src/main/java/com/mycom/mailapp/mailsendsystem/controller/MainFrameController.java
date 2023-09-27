package com.mycom.mailapp.mailsendsystem.controller;

import com.mycom.mailapp.mailsendsystem.task.SendTask;
import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import com.mycom.mailapp.mailsendsystem.tools.JavaFxTools;
import com.mycom.mailapp.mailsendsystem.tools.SendMailTools;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Yun
 * @version 1.0
 * @description: 处理主界面事件
 * @date 2023/9/23
 */
public class MainFrameController {
    //附件文件路径
    private String selectedFilePath="";
    //任务信息文件储存路径
    //定时器
    private Timer timer;
    @FXML
    private DatePicker dateText;

    @FXML
    private TextField hourText;

    @FXML
    private TextField secondText;

    @FXML
    private TextField reciveEmailText;

    @FXML
    private HTMLEditor contentText;

    @FXML
    private TextField addressText;

    @FXML
    private TextField codeText;

    @FXML
    private TextField minuteText;

    @FXML
    private TextField sendEmailText;
    @FXML
    private Button attachments;
    @FXML
    private Label attachLabel;
    @FXML
    private TextField reciveNameText;
    @FXML
    private TextField subjectText;
    @FXML
    private TextField serverText;
    @FXML
    private ListView<String> tasklist;
    @FXML
    private Button removeBtn;
    @FXML
    private CheckBox isRepeat;

    JavaFxTools javaFxTools = new JavaFxTools();
    FileTools fileTools = new FileTools();
    //重置按纽
    @FXML
    void resetFields(ActionEvent event) {
        javaFxTools.reset(reciveEmailText,reciveNameText,addressText,sendEmailText,codeText,serverText);
        javaFxTools.resetTime(hourText,minuteText,secondText);
        javaFxTools.resetHTML(contentText);
        javaFxTools.resetLabel(attachLabel);
        selectedFilePath = "";
        dateText.setValue(null);
        isRepeat.setSelected(false);
    }
    //界面初始化
    //从配置文件中读取配置，若想手动输入，则直接注释此方法

    @FXML
    void initialize() throws ParseException {
        //初始化填充任务列表
        showTasks();
        //设置删除任务按钮为禁用状态
        removeBtn.setDisable(true);
        //添加定时任务
        setTimer();
    }
    //填充右侧任务列表
    void showTasks(){
        ObservableList<String> fileList = fileTools.getFileList(fileTools.taskFilePath);
        tasklist.setItems(fileList);
    }
    //点击列表中某项，改变页面展示
    @FXML
    void change(MouseEvent event) {
        //填充页面
        showMail(tasklist.getSelectionModel().getSelectedItem());
        //启用删除按钮
        removeBtn.setDisable(false);
    }

    //填充邮件信息
    void showMail(String fileName){
        Map<String,String> dataMap = new FileTools().readReturnMap(fileTools.taskFilePath+"/"+fileName+".properties");

        //是否重复发送选项
        boolean repeat;
        repeat =Boolean.parseBoolean(dataMap.get("isRepeat"));

        //对日期时间单独处理
        String str =  dataMap.get("sendDate");
        String[] strs=str.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(strs[0],formatter);

        //对附件获取文件名用于展示
        String attach = "";
        String filePath = dataMap.get("attach");
        if(filePath!=null){
            File file = new File(filePath);
            attach = file.getName();
        }

        sendEmailText.setText(dataMap.get("sendEmail"));
        codeText.setText(dataMap.get("code"));
        reciveEmailText.setText(dataMap.get("receiveEmail"));
        reciveNameText.setText(dataMap.get("name"));
        addressText.setText(dataMap.get("address"));
        dateText.setValue(localDate);
        hourText.setText(strs[1]);
        minuteText.setText(strs[2]);
        secondText.setText(strs[3]);
        contentText.setHtmlText(dataMap.get("content"));
        attachLabel.setText(attach);
        serverText.setText(dataMap.get("server"));
        isRepeat.setSelected(repeat);
    }

    //检查邮件信息填写是否完整无误
    boolean checkMail(){
        //时间
        String str = hourText.getText()+":"+minuteText.getText()+":"+secondText.getText();
       if(javaFxTools.checkText(reciveEmailText,serverText,sendEmailText,codeText)&&
               dateText.getValue()!=null&&javaFxTools.checkTime(str)){
           return true;
       };
       return false;
    }

    //保存邮件数据
    void saveMail(String fileName) throws IOException {
        //获取邮件信息
        LocalDate localDate = dateText.getValue();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sendDate = localDate.format(fmt);
        String str = Boolean.toString(isRepeat.isSelected());

        //写入properties文件
        FileWriter writer = new FileWriter(fileTools.taskFilePath+"/"+fileName+".properties");
        Properties pro = new Properties();
        pro.setProperty("sendEmail",sendEmailText.getText());
        pro.setProperty("code", codeText.getText());
        pro.setProperty("receiveEmail", reciveEmailText.getText());
        pro.setProperty("subject", subjectText.getText());
        pro.setProperty("content", contentText.getHtmlText());
        pro.setProperty("server", serverText.getText());
        pro.setProperty("attach",selectedFilePath);
        pro.setProperty("sendDate",sendDate+" "+hourText.getText()+" "+minuteText.getText()+" "+secondText.getText());
        pro.setProperty("name",reciveNameText.getText());
        pro.setProperty("address",addressText.getText());
        pro.setProperty("isRepeat",str);
        pro.store(writer, sendEmailText.getText());
        writer.close();
    }
    //点击完成配置按钮,会创建一个properties文件存放邮件数据
    @FXML
        void addApplication(ActionEvent Event) throws MessagingException, IOException {
        //创建一个弹窗组件，用于展示添加任务结果
        //检查邮件信息
        if(!checkMail()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION); // 创建一个信息类型的对话框
            alert.setTitle("提示");
            alert.setHeaderText("添加任务失败");
            alert.setContentText("请检查必填选项和发送时间格式！");
            alert.showAndWait();
        }
        else{
            //创建一个为任务命名的弹窗
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // 创建一个信息类型的对话框
            alert.setTitle("提示");
            alert.setHeaderText("请为这个任务命名");
            alert.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
            //创建一个文本输入框
            TextField textField = new TextField();
            textField.setPrefSize(300, 30);
            textField.setPadding(new Insets(10));
            //文本框符合文件命名的命名规范
            alert.getDialogPane().setContent(textField);
            // 添加事件处理器
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.APPLY) {
                    // 检查命名规范
                    String str = textField.getText().trim();
                    if(!Pattern.matches("^[^\\\\/?%<>.*:|\"]+$",str)&&str.isEmpty()){
                        Alert failed = new Alert(Alert.AlertType.INFORMATION); // 创建一个信息类型的对话框
                        failed.setTitle("提示");
                        failed.setHeaderText("任务名称为空或含有非法字符?*%<>./\\\\|:");
                        failed.showAndWait();
                    }
                     else{
                        try {
                            //清除文件名中的空格
                            String name = textField.getText().replaceAll(" ","");
                            saveMail(name);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                // 取消创建

            });
            //刷新任务列表
            showTasks();
            //清除现有定时器
            this.timer.cancel();
            //设定定时器
            try {
                setTimer();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }


 //点击添加附件按钮，将附件复制到attaches文件夹中
 @FXML
 void attach(ActionEvent event) throws IOException {
     File selectedFile = fileTools.getSelectedFile();
    //源路径和目标路径
     Path sourcePath = Paths.get(selectedFile.getAbsolutePath());
     Path targetPath = Paths.get("attaches/");
     //文件复制,名字也会复制过去
     Files.copy(sourcePath,targetPath.resolve(sourcePath.getFileName()));

     attachLabel.setText(selectedFile.getName());
     selectedFilePath = targetPath+selectedFile.getName();
 }
 //删除任务按钮
 @FXML
 void removeTask(ActionEvent event) {
     // 创建一个确认弹窗
     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
     alert.setTitle("确认");
     alert.setHeaderText("您确定要删除吗？");

     // 设置弹窗的按钮
     alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

     // 添加事件处理器
     alert.showAndWait().ifPresent(response -> {
         if (response == ButtonType.YES) {
             // 用户点击了“是”，执行删除操作
             fileTools.removeTasks(fileTools.taskFilePath+"/"+tasklist.getSelectionModel().getSelectedItem()+".properties");
         } else {
             // 用户点击了“否”，不执行任何操作
         }
     });
        //更新任务列表
        showTasks();
        //清除现有定时器
     this.timer.cancel();
     //设定定时器
     try {
         setTimer();
     } catch (ParseException e) {
         throw new RuntimeException(e);
     }
 }

 //更新执行邮件发送的定时器，在初始化界面、添加新任务后执行一次
    void setTimer() throws ParseException {
        Date date;
        // 定义日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHHmmss");
        //1.获取到各个任务的时间节点，存放到list数组中
        List<String> list =new SendMailTools().getTime();
        //2.设定定时器,执行发送邮件任务
        this.timer = new Timer();
        if(list!=null){
            //在每条信息中以空格分离出日期、时、分、秒、是否重复、任务名
            for (String str:list
            ) {
                SendTask sendTask = new SendTask();
                String[] strs=str.split(" ");
                date = sdf.parse(strs[0]+strs[1]+strs[2]+strs[3]);
                //发送日期与当前日期比较,如果不是重复发送，当日期已经经过时不再执行
                Date currentDate = new Date();
                int result = date.compareTo(currentDate);
                //日期超过，检查是否重复发送
                if(result<=0){
                    //为重复发送，则将日期加7到超过现在日期
                    if(strs[4].equals("true")){
                        while (date.compareTo(currentDate)<0){
                            //借助Calendar类增加7天
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_MONTH, 7);
                            date = calendar.getTime();
                        }
                        sendTask.setName(strs[5]);
                        timer.schedule(sendTask,date,7 * 24 * 60 * 60 * 1000L);
                        //这里设置的每隔7天发送一次，如果此程序不能单次执行时间超过7天则没有意义o.o
                    }
                }
                //日期未超过则直接新建定时任务
                else{
                    sendTask.setName(strs[5]);
                    timer.schedule(sendTask,date);
                }
            }
        }
    }

}
