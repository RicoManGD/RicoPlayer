package com.example.administrator.ricoplayer.bean;

/**
 * Created by Administrator on 2018/1/5.
 */

public class Songs {
    public Songs(String name,String path){
        this.name=name;
        this.path=path;
    }

    private String name;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //重写toString（）是ListView显示的内容
    @Override
    public String toString() {
        return getName();
    }


}
