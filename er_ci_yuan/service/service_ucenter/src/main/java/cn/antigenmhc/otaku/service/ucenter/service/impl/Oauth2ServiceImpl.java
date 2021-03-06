package cn.antigenmhc.otaku.service.ucenter.service.impl;

import cn.antigenmhc.otaku.common.base.utils.RedisUtil;
import cn.antigenmhc.otaku.service.ucenter.mapper.MemberMapper;
import cn.antigenmhc.otaku.service.ucenter.pojo.Member;
import cn.antigenmhc.otaku.service.ucenter.service.Oauth2Service;
import cn.antigenmhc.otaku.service.ucenter.utils.JwtUtilEx;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: antigenMHC
 * @Date: 2021/2/2 0:11
 * @Version: 1.0
 **/
@Service
public class Oauth2ServiceImpl implements Oauth2Service {

    @Resource
    private MemberMapper memberMapper;
    @Resource
    private RedisUtil redisUtil;
    @Value("${member.defaultAvatar}")
    private String defaultAvatar;

    @Override
    public String getJwtTokenByOauth2UserInfo(String userInfo, String oauthFrom){
        JSONObject parse = JSONObject.parseObject(userInfo);
        String oauthId = parse.get("id")+"";
        String name = (String)parse.get("name");
        String avatar = (String)parse.get("avatar_url");

        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        if("gitee".equals(oauthFrom)){
            queryWrapper.eq("gitee_id", oauthId);
        }else if("github".equals(oauthFrom)){
            queryWrapper.eq("github_id", oauthId);
        }
        Member one = memberMapper.selectOne(queryWrapper);
        //绑定oauthId的用户如果已经存在，则直接返回
        if(one != null){
            //缓存中是否存在，如果不存在则创建
            if(StringUtils.isEmpty((String)redisUtil.get(one.getId()))){
                redisUtil.set(one.getId(), JwtUtilEx.createJwtToken(one),60*60*24*10);
            }
            return JwtUtilEx.createJwtToken(one);
        }
        //如果不存在，则设置基本信息后创建token并返回
        Member member = new Member();
        if("gitee".equals(oauthFrom)){
            member.setGiteeId(oauthId);
            member.setAvatar(avatar);
        }else if("github".equals(oauthFrom)){
            member.setGithubId(oauthId);
            member.setAvatar(defaultAvatar);
        }
        member.setNickname(name);
        memberMapper.insert(member);
        redisUtil.set(member.getId(), JwtUtilEx.createJwtToken(member), 60*60*24*10);

        return JwtUtilEx.createJwtToken(member);
    }

    @Override
    public boolean fetchCallBackState(String code, String state) {
        if(com.mysql.cj.util.StringUtils.isEmptyOrWhitespaceOnly(code) ||
                com.mysql.cj.util.StringUtils.isEmptyOrWhitespaceOnly(state)){
            return false;
        }
        String redisState = (String)redisUtil.get((String) redisUtil.get(state));
        if(!redisState.equals(state)){
            return false;
        }
        return true;
    }
}
