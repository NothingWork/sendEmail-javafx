package com.mycom.mailapp.mailsendsystem;

import com.mycom.mailapp.mailsendsystem.controller.LogsFrameController;
import com.mycom.mailapp.mailsendsystem.tools.FileTools;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


public class MainApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws MalformedURLException {
        Platform.setImplicitExit(false);
        this.primaryStage = primaryStage;
        // 设置标题
        primaryStage.setTitle("邮件定时发送器");
        //设置图标
        primaryStage.getIcons().add(new Image("images/sendEmail.png"));
        //生成系统托盘
        SystemTray systemTray = SystemTray.getSystemTray();
        java.awt.Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

        PopupMenu popupMenu = new PopupMenu();
        MenuItem item1 = new MenuItem("主界面");
        MenuItem item2 = new MenuItem("退出");
        popupMenu.add(item1);
        popupMenu.add(item2);
        String str = "邮件定时发送器";
        TrayIcon tray = new TrayIcon(image,str,popupMenu);
        tray.setImageAutoSize(true);

        //窗口关闭事件,生成系统托盘区
        primaryStage.setOnCloseRequest(event ->{
            try {
                systemTray.add(tray);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });
        //点击显示主界面
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.show();
                    }
                });
                systemTray.remove(tray);
            }
        });
        //点击退出按钮，结束进程
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 在程序启动的时候加载主界面
        initMainFrame();
    }

    /**
     * 加载主界面
     */
    public void initMainFrame() {
        try {
            // 加载FXML界面文件
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainFrame.fxml"));
            Parent root = loader.load();
            // 实例化场景
            Scene scene = new Scene(root);
            // 将场景设置到舞台
            primaryStage.setScene(scene);
            // 展示舞台
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //加载日志窗口
    /**
     * 加载设置界面
     */
    public void initLogsFrame(){
        try {
            // 加载FXML文件
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/LogsFrame.fxml"));
            Parent root = loader.load();
            // 实例化舞台
            Stage stage = new Stage();
            // 设置标题
            stage.setTitle("日志");
            // 设置该界面不可缩放
            stage.setResizable(true);
            // 设置该界面总是处于最顶端
            stage.setAlwaysOnTop(true);
            // 设置模态窗口,该窗口阻止事件传递到任何其他应用程序窗口
            stage.initModality(Modality.APPLICATION_MODAL);
            // 设置主容器为主界面舞台
            stage.initOwner(primaryStage);

            // 实例化场景
            Scene scene = new Scene(root);
            // 将场景设置到舞台上
            stage.setScene(scene);
            //设置controller
            LogsFrameController controller = loader.getController();
            controller.setStage(stage);
            // 展示舞台
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //检查资源完整性
        FileTools fileTools = new FileTools();
        String[] files = {fileTools.taskFilePath, fileTools.attachesPath, fileTools.logPath};
        for (String str:files
             ) {
            File folder = new File(str);
            if(!folder.exists()){
                folder.mkdir();
            }
        }
        //启动主界面窗口
        launch(args);
    }
}