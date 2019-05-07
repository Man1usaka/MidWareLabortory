package com.study.yao.controller;


import com.study.yao.model.Alumni;
import com.study.yao.service.AlumniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/alumni")
public class AlumniController {

    @Autowired
    AlumniService alumniService;

    /**
     * 获取 Alumni
     * 处理 "/Alumni" 的 GET 请求，用来获取 Alumni 列表
     */
    @GetMapping(value = "/{id}")
    public Alumni getAlumniList(@PathVariable Long id) {
        return alumniService.findById(id);
    }

    /**
     * 获取 Alumni 列表
     * 处理 "/Alumni" 的 GET 请求，用来获取 Alumni 列表
     */
    @GetMapping(value = "/all")
    public List<Alumni> getAlumniList() {
        return alumniService.findAll();
    }


    /**
     * 创建 Alumni
     * 处理 "/Alumni/create" 的 POST 请求，用来新建 Alumni 信息
     * 通过 @ModelAttribute 绑定表单实体参数，也通过 @RequestParam 传递参数
     */
    @PostMapping(value = "")
    public Alumni postAlumni(@RequestBody Alumni alumni) {
        return alumniService.insertByAlumni(alumni);
    }


    /**
     * 更新 Alumni
     * 处理 "/update" 的 PUT 请求，用来更新 Alumni 信息
     */
    @PutMapping(value = "")
    public Alumni putAlumni(@RequestBody Alumni alumni) {
        return alumniService.update(alumni);
    }

    /**
     * 删除 Alumni
     * 处理 "/Alumni/{id}" 的 GET 请求，用来删除 Alumni 信息
     */
    @DeleteMapping(value = "")
    public Alumni deleteAlumni(@RequestBody Alumni alumni) {
        return alumniService.delete(alumni.getId());
    }

}

