package com.frankeleyn.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.listener.ExcelDictDTOListener;
import com.frankeleyn.srb.core.mapper.DictMapper;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;
import com.frankeleyn.srb.core.pojo.entity.Dict;
import com.frankeleyn.srb.core.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict parent = baseMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
        return this.listByParentId(parent.getId());
    }

    @Override
    public boolean importData(MultipartFile file) {

        try {
            EasyExcel.read(file.getInputStream(), ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet("数据字典").doRead();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);

        List<ExcelDictDTO> list = dictList.stream().map(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            return excelDictDTO;
        }).collect(Collectors.toList());

        return list;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {

        List<Dict> dictList;

        // 1. 先查询 Redis 缓存中中是否有数据字典
        dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
        if (Objects.nonNull(dictList)) {
            return dictList;
        }

        // 2. 缓存为空就查询  DB
        dictList = baseMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", parentId));
        dictList.forEach(dict -> {
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });

        // 3. 数据库不为空，同步缓存
        if (Objects.nonNull(dictList)) {
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList);
        }

        return dictList;
    }

    // 判断是否有子节点
    private boolean hasChildren(Long id) {
        Integer count = baseMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id));
        if (count > 0) {
            return true;
        }
        return false;
    }

}
