package com.study.yao.service;

import com.study.yao.model.Alumni;

import java.util.List;

/**
 * Alumni 业务接口层
 *
 * Created by bysocket on 30/09/2017.
 */

public interface AlumniService {
    /**
     * 获取所有 Alumni
     */
    List<Alumni> findAll();

    /**
     * 新增 Alumni
     */
    Alumni insertByAlumni(Alumni Alumni);

    /**
     * 更新 Alumni
     *
     * @param Alumni {@link Alumni}
     */
    Alumni update(Alumni Alumni);

    /**
     * 删除 Alumni
     *
     * @param id 编号
     */
    Alumni delete(Long id);

    /**
     * 获取 Alumni
     *
     * @param id 编号
     */
    Alumni findById(Long id);
}
