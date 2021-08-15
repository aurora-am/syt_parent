package com.aurora.yygh.hosp.service.impl;

import com.aurora.yygh.hosp.mapper.HospitalSetMapper;
import com.aurora.yygh.model.hosp.HospitalSet;
import com.aurora.yygh.hosp.service.HospitalSetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Autowired  //注入mapper
    private HospitalSetMapper hospitalSetMapper;
}
