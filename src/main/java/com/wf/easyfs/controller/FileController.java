package com.wf.easyfs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wangfan on 2018-12-24 下午 4:10.
 */
@RequestMapping("/file")
public class FileController {
    @Value("${fs.dir}")
    private String fileDir;
    @Value("${fs.useDB}")
    private Boolean useDB;
    @Value("${fs.uuidName}")
    private Boolean uuidName;

    /**
     * 上传文件
     */
    @ResponseBody
    @PostMapping("/upload")
    public Map upload(@RequestParam MultipartFile file) {
        // 文件原始名称
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String prefix = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        // 保存到磁盘
        File outFile;
        String path;
        if (uuidName != null && uuidName) {
            path = getDate() + UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            outFile = new File(File.listRoots()[0], fileDir + path);
        } else {
            int index = 1;
            path = getDate() + originalFileName;
            outFile = new File(File.listRoots()[0], fileDir + path);
            while (outFile.exists()) {
                path = getDate() + prefix + "(" + index + ")." + suffix;
                outFile = new File(File.listRoots()[0], fileDir + path);
                index++;
            }
        }
        try {
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            file.transferTo(outFile);
            return getRS(0, "上传成功", path);
        } catch (Exception e) {
            e.printStackTrace();
            return getRS(500, e.getMessage());
        }
    }

    /**
     * 查看文件
     */
    @GetMapping("/{file:.+}")
    public void file(@PathVariable("file") String filename, HttpServletResponse response) {
        // 判断文件是否存在
        File inFile = new File(File.listRoots()[0], fileDir + filename);
        if (!inFile.exists()) {
            PrintWriter writer = null;
            try {
                response.setContentType("text/html;charset=UTF-8");
                writer = response.getWriter();
                writer.write("<!doctype html><title>404 Not Found</title><h1 style=\"text-align: center\">404 Not Found</h1><hr/><p style=\"text-align: center\">Easy File Server</p>");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        // 获取文件类型
        Path path = Paths.get(filename);
        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType != null) {
            response.setContentType(contentType);
        } else {
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
        }
        // 输出文件流
        OutputStream os = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(inFile);
            os = response.getOutputStream();
            byte[] bytes = new byte[1024];
            while (is.read(bytes) != -1) {
                os.write(bytes);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        return sdf.format(new Date());
    }

    private Map getRS(int code, String msg, String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        if (code == 500) {
            map.put("msg", "上传失败");
            map.put("details", msg);
        }
        if (url != null) {
            map.put("url", url);
        }
        return map;
    }

    private Map getRS(int code, String msg) {
        return getRS(code, msg, null);
    }
}
