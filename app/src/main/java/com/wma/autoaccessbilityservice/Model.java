package com.wma.autoaccessbilityservice;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 王明骜 on 19-11-4 下午4:36.
 */
public class Model implements Serializable {


    public static final String ACTION = "update data";
    public static final String NAME = "model";


    private String packageName;
    private List<String> contentList;


    public Model(String packageName, List<String> contentList) {
        this.packageName = packageName;
        this.contentList = contentList;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }
}
