package com.mycom.mailapp.mailsendsystem.tools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Yun
 * @version 1.0
 * @description: 读取文件数据工具类
 * @date 2023/9/24
 */
public class FileTools {
    //任务配置文件存放路径，附件文件夹，执行日志文件夹
    public final String logPath = "logs";
    public final String taskFilePath="tasks";
    public final String attachesPath="attaches/";
//    public final String taskFilePath="src/main/resources/tasks";
//
//    public final String attachesPath="src/main/resources/attaches/";
//    public final String logPath="src/main/resources/logs";

    //读取某个具体配置文件
    public Map<String,String> readReturnMap(String propertiesFilePath) {
        Properties properties = new Properties();
        Map<String,String> map = new HashMap<>();
        try {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(propertiesFilePath)));
            properties.load(new InputStreamReader(inputStream));
            map.put("sendEmail", properties.getProperty("sendEmail"));
            map.put("code", properties.getProperty("code"));
            map.put("name", properties.getProperty("name"));
            map.put("receiveEmail", properties.getProperty("receiveEmail"));
            map.put("address", properties.getProperty("address"));
            map.put("sendDate",properties.getProperty("sendDate"));
            map.put("content",properties.getProperty("content"));
            map.put("attach",properties.getProperty("attach"));
            map.put("server",properties.getProperty("server"));
            map.put("isRepeat",properties.getProperty("isRepeat"));

            inputStream.close();
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //读取邮件要发送的附件文件
    public File getSelectedFile() {
        String selectedFilePath ="";
        //实例化文件选择器
        FileChooser fileChooser = new FileChooser();
        //打开文件选择框
        File result = fileChooser.showOpenDialog(null);
        // 选择文件的路径
        if(result!=null){
            selectedFilePath = result.getAbsolutePath();
            return new File(selectedFilePath);
        }
        return null;
    }
  //读取文件列表，查看都有什么任务
    public ObservableList<String> getFileList(String filePath){
        ObservableList<String> strList = FXCollections.observableArrayList();
        File folder = new File(filePath);
        File[] fileList = folder.listFiles();
        if (fileList != null) {
            for (File file:fileList
                 ) {
                String fileName = file.getName();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileName = fileName.substring(0, dotIndex);
                }
                strList.add(fileName);
            }
        }
        return strList;
    }
    //删除文件
    public void removeFiles(String filepath){
        File file = new File(filepath);
        file.delete();
    }
    //文件复制
    public void copyFile(String sourcepath,String targetpath) throws IOException {
        Path path1 = Paths.get(sourcepath);
        Path path2 = Paths.get(targetpath);
        //文件复制,名字也会复制过去
        // Files.copy(sourcePath,targetPath.resolve(sourcePath.getFileName()));
        //文件复制,文件名字也会复制过去
        Files.copy(path1,path2.resolve(path1.getFileName()));
    }
    //写入日志文件
    public void writeLogs(String filePath,String text){
        try {
            // 创建一个File对象，指定文件路径和名称
            File file = new File(filePath);
            // 如果文件不存在，则创建一个新文件
            if (!file.exists()) {
                file.createNewFile();
            }
            // 使用FileWriter和BufferedWriter将内容写入文件
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(text+System.lineSeparator());
            // 关闭BufferedWriter
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
