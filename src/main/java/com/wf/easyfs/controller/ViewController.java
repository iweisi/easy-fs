package com.wf.easyfs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

/**
 * Created by wangfan on 2018-12-24 下午 4:59.
 */
@RequestMapping("/api")
public class ViewController {
    @Value("${fs.dir}")
    private String fileDir;

    @RequestMapping({"/", "/index"})
    public String index() {
        return "index.html";
    }

    /**
     * 获取全部文件
     */
    @ResponseBody
    @RequestMapping("/list")
    public Map list(String dir) {
        if (dir == null) {
            dir = "";
        }
        File file = new File(File.listRoots()[0], fileDir + "/" + dir);
        File[] listFiles = file.listFiles();
        return null;
    }
}
