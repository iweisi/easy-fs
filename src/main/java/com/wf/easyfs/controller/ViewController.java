package com.wf.easyfs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by wangfan on 2018-12-24 下午 4:59.
 */
@RequestMapping("/api")
public class ViewController {
    @Value("${fs.dir}")
    private String fileDir;
    @Value("${fs.useDB}")
    private Boolean useDB;

    @RequestMapping({"/", "/index"})
    public String index() {
        return "index.html";
    }

    /**
     * 获取全部文件
     */
    @ResponseBody
    @RequestMapping("/list")
    public Map list() {
        return null;
    }
}
