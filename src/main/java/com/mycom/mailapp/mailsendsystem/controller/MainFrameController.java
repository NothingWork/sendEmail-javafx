package com.mycom.mailapp.mailsendsystem.controller;

import com.mycom.mailapp.mailsendsystem.MainApp;
import com.mycom.mailapp.mailsendsystem.task.SendTask;
import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import com.mycom.mailapp.mailsendsystem.tools.JavaFxTools;
import com.mycom.mailapp.mailsendsystem.tools.SendMailTools;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @author Yun
 * @version 1.0
 * @description: 处理主界面事件
 * @date 2023/9/23
 */
public class MainFrameController {
    //存放附件文件路径的数组
    private List<String> selectedFilePath = new ArrayList<>();
    //
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
    private Button addApplication;
    @FXML
    private Button clear;
    @FXML
    private Button removeBtn;
    @FXML
    private TextField reciveNameText;
    @FXML
    private TextField subjectText;
    @FXML
    private TextField serverText;
    @FXML
    private ListView<String> tasklist;
    @FXML
    private CheckBox isRepeat;
    @FXML
    private Button refreshBtn;
    @FXML
    private Label week;
    @FXML
    private HBox hbox;

    @FXML
    private Button showLogsBtn;

    JavaFxTools javaFxTools = new JavaFxTools();
    FileTools fileTools = new FileTools();

    //对页面进行初始化

    @FXML
    void initialize() throws ParseException {
        //为按钮渲染图标
        javaFxTools.setLabeledImage(
                new Labeled[]{
                        attachments, removeBtn, addApplication, clear,refreshBtn,showLogsBtn},
                new String[]{
                        "images/addAttaches.png",
                        "images/delete.png",
                        "images/addTask.png",
                        "images/clear.png",
                        "images/refresh.png",
                        "images/logs.png"});
        //初始化填充任务列表
        showTasks();
        //设置删除任务按钮和更新任务按钮为禁用状态
        removeBtn.setDisable(true);
        refreshBtn.setDisable(true);
        //读取任务信息,添加定时任务
        setTimer();
    }
    //重置按纽，清空页面文本域和附件
    @FXML
    void resetFields(ActionEvent event) {
        javaFxTools.reset(reciveEmailText,reciveNameText,addressText,sendEmailText,codeText,serverText);
        javaFxTools.resetTime(hourText,minuteText,secondText);
        javaFxTools.resetHTML(contentText);
        if(selectedFilePath!=null){selectedFilePath.clear();}
        dateText.setValue(null);
        isRepeat.setSelected(false);
        hbox.getChildren().clear();

        //测试新功能
        example();

    }

    //点击列表中某项，改变页面展示
    @FXML
    void change(MouseEvent event) {
        //填充页面
        showMail(tasklist.getSelectionModel().getSelectedItem());
        //启用删除按钮和更新任务按钮
        removeBtn.setDisable(false);
        refreshBtn.setDisable(false);
    }

    //点击完成配置按钮,会创建一个properties文件存放邮件数据
    @FXML
    void addApplication(ActionEvent Event) throws MessagingException, IOException {
        //调用创建任务方法，新建任务传入任务名称为空
        addTasks("");
    }
    //点击添加附件按钮，将附件复制到attaches文件夹中
    @FXML
    void attach(ActionEvent event){
        //1.获取文件信息
        File selectedFile = fileTools.getSelectedFile();
        if(selectedFile!=null){

            String filename = selectedFile.getName();
            String filepath = selectedFile.getAbsolutePath();
            //生成包含附件信息的toolbar
            addToolbar(filename,filepath);
            //3.添加附件路径到数组中进行存储
            selectedFilePath.add(filepath);
        }
    }

    //删除任务按钮
    @FXML
    void removeTask(ActionEvent event) {
        deleteTask(tasklist.getSelectionModel().getSelectedItem(),true);
    }

    //点击更新当前任务按钮，根据页面信息更新当前选中任务
    @FXML
    void refreshTask(ActionEvent event) {
        if(tasklist.getSelectionModel().getSelectedItem()!=null){
            //1.调用新建任务，将当前选中任务名称传入
            String str = tasklist.getSelectionModel().getSelectedItem();
            String[] strs = str.split(" ");
            int before = tasklist.getItems().size();
            addTasks(strs[0]);
            //2.刷新任务列表，检查任务数量是否加一
            int after = tasklist.getItems().size();
            //3.数量加一则删除旧的任务，数量不变则直接覆盖旧的任务
            if(after>before){
                deleteTask(str,false);
                showTasks();
            }
        }
    }
    //当日期控件被选择时，填充选中的日期为周几的提示信息
    @FXML
    void datePicked(ActionEvent event) {
        if(!dateText.getValue().toString().isEmpty()){
            String Week = dateText.getValue().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            week.setText(Week);
        }
    }

    //点击日志按钮，显示日志窗口
    @FXML
    void showLogs(ActionEvent event) {
        // 加载设置界面
        new MainApp().initLogsFrame();
    }

    //删除任务方法，接受一个任务名称参数,和是否进行提示参数，删除附件文件夹
    void deleteTask(String taskName,Boolean bool){
        if(!bool){
            fileTools.removeFiles(fileTools.taskFilePath+"/"+taskName+".properties");
            fileTools.removeFiles(fileTools.attachesPath+taskName);
        }
        else{
            // 创建一个确认弹窗
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认");
            alert.setHeaderText("您确定要删除吗？");

            // 设置弹窗的按钮
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            // 添加事件处理器
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // 执行删除操作,删除任务文件，附件文件，日志文件
                    fileTools.removeFiles(fileTools.taskFilePath+"/"+taskName+".properties");
                    fileTools.removeFiles(fileTools.attachesPath+taskName);
                    fileTools.removeFiles(fileTools.logPath+"/"+taskName+".txt");
                }
            });
        }
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


    //填充右侧任务列表,并将两个操作按钮设为禁用状态
    void showTasks(){
        //填充任务名称
        ObservableList<String> fileList = fileTools.getFileList(fileTools.taskFilePath);
        tasklist.setItems(fileList);
        refreshBtn.setDisable(true);
        removeBtn.setDisable(true);
    }

    //向页面中填充邮件信息
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

        //生成附件相关信息
        hbox.getChildren().clear();
        File folder = new File(fileTools.attachesPath+fileName);
        if(folder.exists()){
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file:files
                     ) {
                    addToolbar(file.getName(),file.getPath());
                    selectedFilePath.add(file.getPath());
                }
            }
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
        //获取邮件特殊信息
        LocalDate localDate = dateText.getValue();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sendDate = localDate.format(fmt);
        String str = Boolean.toString(isRepeat.isSelected());

        //写入properties文件,附件路径不进行写入，向文件中存放附件
        FileWriter writer = new FileWriter(fileTools.taskFilePath+"/"+fileName+".properties");
        Properties pro = new Properties();
        pro.setProperty("sendEmail",sendEmailText.getText());
        pro.setProperty("code", codeText.getText());
        pro.setProperty("receiveEmail", reciveEmailText.getText());
        pro.setProperty("subject", subjectText.getText());
        pro.setProperty("content", contentText.getHtmlText());
        pro.setProperty("server", serverText.getText());
        pro.setProperty("sendDate",sendDate+" "+hourText.getText()+" "+minuteText.getText()+" "+secondText.getText());
        pro.setProperty("name",reciveNameText.getText());
        pro.setProperty("address",addressText.getText());
        pro.setProperty("isRepeat",str);
        pro.store(writer, sendEmailText.getText());
        writer.close();
    }
    //添加任务按钮，接收一个字符串作为任务名
    void addTasks(String taskName){
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
            alert.setHeaderText("请确认任务名称");
            alert.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);
            //获取到日期是周几
            String Week = dateText.getValue().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            //创建一个包含是否重复发送信息的label
            Label label = new Label();
            if(isRepeat.isSelected()){label.setText(" (每"+Week+"发送)");}
            //创建一个布局节点
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(label,1,0);
            //创建一个文本输入框
            TextField textField = new TextField();
            textField.setPrefSize(300,30);
            //为文本框填入传来的任务名字参数（用于更新任务）
            textField.setText(taskName);
            //文本框符合文件命名的命名规范
            grid.add(textField,0,0);
            alert.getDialogPane().setContent(grid);
            alert.getDialogPane().setMinSize(500,200);
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
                            saveMail(name+label.getText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //向文件夹中存放附件
                        String folderName = textField.getText().replaceAll(" ","")+label.getText();
                        File folder = new File(fileTools.attachesPath+folderName);
                        if(!folder.exists()){folder.mkdir();}
                        for (String path:selectedFilePath
                        ) {
                            try {
                                File sourceFile = new File(path);
                                String fileName = sourceFile.getName();
                                //存在同名文件则直接进行删除替换
                                String targetFile = fileTools.attachesPath+folderName+"/"+fileName;
                                if(!path.equals(targetFile)){
                                    if(new File(targetFile).exists()){
                                        fileTools.removeFiles(targetFile);
                                    }
                                    fileTools.copyFile(path,fileTools.attachesPath+folderName);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                // 取消创建
            });
            //清空附件路径数组
            selectedFilePath.clear();
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
                        sendTask.setName(strs[5]);//执行对应名称的任务
                        timer.schedule(sendTask,date,7 * 24 * 60 * 60 * 1000L);
                        //这里设置的每隔7天发送一次，如果此程序不能单次执行时间超过7天则没有意义
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

    //添加一个带标签和删除按钮的toolbar控件，接收一个string作为标签内容,一个文件路径用于删除
    void addToolbar(String str,String filepath){
        //添加一个toolbar展示添加的附件
        ToolBar toolBar = new ToolBar();
        Button button = new Button();
        Label label = new Label(str);
        javaFxTools.setLabeledImage(new Labeled[]{button},new String[]{"/images/delete.png"});
        toolBar.getItems().addAll(
                label,
                button
        );
        //将toolbar添加到hBox中
        hbox.getChildren().add(toolBar);
        hbox.setAlignment(Pos.CENTER);
        //设置按钮删除事件
        button.setOnAction(event -> {
            // 删除ToolBar控件
            hbox.getChildren().remove(toolBar);
            // 删除附件附件路径数组中的对应路径
            selectedFilePath.removeIf(s -> s.equals(filepath));
        });
    }

    //测试方法
    void example(){
        System.out.println(selectedFilePath.toString());
    }


}
