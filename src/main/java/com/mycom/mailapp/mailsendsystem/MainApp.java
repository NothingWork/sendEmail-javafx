package com.mycom.mailapp.mailsendsystem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;


public class MainApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(false);
        this.primaryStage = primaryStage;
        // 设置标题
        primaryStage.setTitle("邮件定时发送器");
        //设置图标
        primaryStage.getIcons().add(new Image("images/sendEmail.png"));
        //生成系统托盘
        SystemTray systemTray = SystemTray.getSystemTray();

        java.awt.Image image = Toolkit.getDefaultToolkit().getImage("src/main/resources/images/icon.png");
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

    public static void main(String[] args) throws ParseException {
        //测试
        //启动主界面窗口
        launch(args);
    }
}