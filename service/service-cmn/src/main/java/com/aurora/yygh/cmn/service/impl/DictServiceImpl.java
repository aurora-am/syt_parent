package com.aurora.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.aurora.yygh.cmn.listener.DictListener;
import com.aurora.yygh.cmn.mapper.DictMapper;
import com.aurora.yygh.cmn.service.DictService;
import com.aurora.yygh.model.cmn.Dict;
import com.aurora.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置hasChildren
        for (Dict dict:dictList) {      //遍历 dictList 集合
            Long dictId = dict.getId();     //得到集合中的每个对象
            boolean isChild = this.isChildren(dictId);  //根据id查里面有没有子节点
            dict.setHasChildren(isChild);       //把返回的结果set到 字段里面
        }
        return dictList;
    }

    //导出数据字典接口
    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        //Dict -- DictEeVo  因为写入的不是Dict这个实体 而是DictEeVo
        List<DictEeVo> dictVoList = new ArrayList<>();
        for(Dict dict : dictList) {     //得到dicList中的每个对象 再把dictEeVo赋值进去
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);    //copyProperties 相当于 dictEeVo.setId(dict.getId()); 拿得dict的对象再set给dictEeVo
            dictVoList.add(dictEeVo);
            System.out.println(dictEeVo);
        }

        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //导入数据字典数据
    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //判断id下面是否有子节点  因为Dict实体类中有一个单独的字段  hasChildren 所以要进行一下判断
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);   //selectCount() 查看有没有数据 0没数据
        // count>0    0>0 =>false     1>0 => true
        return count>0;
    }
}
