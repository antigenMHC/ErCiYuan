package cn.antigenmhc.otaku.service.cms.mapper;

import cn.antigenmhc.otaku.service.cms.pojo.Ad;
import cn.antigenmhc.otaku.service.cms.pojo.vo.AdVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 广告推荐 Mapper 接口
 * </p>
 *
 * @author antigenmhc
 * @since 2021-02-07
 */
public interface AdMapper extends BaseMapper<Ad> {

    IPage<AdVo> selectPage(Page<AdVo> pageParam);
}
