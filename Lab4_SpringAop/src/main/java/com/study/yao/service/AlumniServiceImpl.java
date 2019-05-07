package com.study.yao.service;

import com.study.yao.model.Alumni;
import com.study.yao.model.AlumniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Y Jiang
 * Alumni 业务层实现
 */
@Service
public class AlumniServiceImpl implements AlumniService {

    @Autowired
    AlumniRepository AlumniRepository;

    @Override
    public List<Alumni> findAll() {
        return AlumniRepository.findAll();
    }

    @Override
    public Alumni insertByAlumni(Alumni alumni) {
        return AlumniRepository.save(alumni);
    }

    @Override
    public Alumni update(Alumni alumni) {
        return AlumniRepository.save(alumni);
    }

    @Override
    public Alumni delete(Long id) {
        Alumni alumni;
        try {
            alumni = AlumniRepository.findById(id).get();
            AlumniRepository.delete(alumni);
        } catch (Exception e) {
            return null;
        }
        return alumni;
    }

    @Override
    public Alumni findById(Long id) {
        return AlumniRepository.findById(id).get();
    }
}
