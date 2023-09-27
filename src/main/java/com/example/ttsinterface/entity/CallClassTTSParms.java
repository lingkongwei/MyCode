package com.example.ttsinterface.entity;

/**
 * @author sodream
 * @date 2022/6/2 10:12
 * @content
 */
public class CallClassTTSParms {

    public String text;
    public String path;
    public String fileName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
