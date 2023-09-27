package com.example.ttsinterface.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author sodream
 * @date 2022/6/2 11:03
 * @content
 */
public class FtpUpload {
    /**
     * 上传文件
     *
     * @param record_path
     */
    public void fileUpload(String record_path) {
        String ftp_ip = "172.28.25.5";
        String ftp_ip1 = "172.28.25.6";
        if (fileUploadServer(ftp_ip, record_path)) {
            fileUploadServer(ftp_ip1, record_path);
        } else {
            fileUploadServer(ftp_ip1, record_path);
        }
    }


    public boolean fileUploadServer(String ftp_ip, String record_path) {
        String ftp_port = "21";
        String ftp_username = "jhn";
        String ftp_password = "callcenter";
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        //System.out.println("--------- ftp_ip:" + ftp_ip + ";ftp_port:" + ftp_port + ";ftp_username:" + ftp_username + ";ftp_password=" + ftp_password);
        try {
            ftpClient.connect(ftp_ip, Integer.parseInt(ftp_port));
            ftpClient.login(ftp_username, ftp_password);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("FTP连接失败");
                return false;
            } else {

                File srcFile = new File(record_path);
                fis = new FileInputStream(srcFile);
                ftpClient.enterLocalPassiveMode();
                String pathname = "/soft/bea/tomcat4.0/webapps/tianr4/upload/mp3/";
                createDir(ftpClient, pathname);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.storeFile(srcFile.getName(), fis);
                System.out.println(record_path + "   上传成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTP客户端出错！" + e.getMessage());
            return false;
        } finally {
            try {
                fis.close();
                ftpClient.logout();
                ftpClient.disconnect();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭FTP连接发生异常！" + e.getMessage());
                return false;
            }
        }
    }


    /**
     * 创建目录(有则切换目录，没有则创建目录)
     *
     * @param dir
     * @return
     */
    public boolean createDir(FTPClient ftp, String dir) {
        try {
            if (StringUtils.isNotBlank(dir)) {
                //目录编码，解决中文路径问题
                String d = new String(dir.toString().getBytes("GBK"), "iso-8859-1");
                //尝试切入目录
                if (!ftp.changeWorkingDirectory(d)) {
                    dir = trimFirstAndLastChar(dir, '/');
                    String[] arr = dir.split("/");
                    StringBuffer sbfDir = new StringBuffer();
                    //循环生成子目录
                    for (String s : arr) {
                        sbfDir.append("/");
                        sbfDir.append(s);
                        //目录编码，解决中文路径问题
                        d = new String(sbfDir.toString().getBytes("GBK"), "iso-8859-1");
                        //尝试切入目录
                        if (ftp.changeWorkingDirectory(d)) {
                            continue;
                        }
                        if (!ftp.makeDirectory(d)) {
                            System.out.println("[失败]ftp创建目录：" + sbfDir.toString());
                            return false;
                        }
                        System.out.println("[成功]创建ftp目录：" + sbfDir.toString());
                    }
                }
                //将目录切换至指定路径
                ftp.changeWorkingDirectory(d);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //去掉首尾指定字符串
    public String trimFirstAndLastChar(String source, char element) {
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do {
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }
}
