package cn.antigenmhc.otaku.common.base.utils;

import io.jsonwebtoken.*;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @Author: antigenMHC
 * @Date: 2021/1/30 12:28
 * @Version: 1.0
 **/
public class JwtUtil {

    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";
    private static final long ADVANCE_EXPIRE_TIME = 300000;
    /**
     * 获得 key
     * @return ：加密后的 key
     */
    private static Key getKeyInstance(){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(APP_SECRET);
        return new SecretKeySpec(bytes,signatureAlgorithm.getJcaName());
    }

    /**
     * 生成 token
     * @param jwtInfo：基本用户信息，作为有效荷载
     * @param expire：过期时间(s)
     * @return ：token
     */
    public static String getJwtToken(JwtInfo jwtInfo, int expire){

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //主题
                .setSubject("er_ci_yuan")
                //颁发时间
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                //用户id
                .claim("id", jwtInfo.getId())
                //用户昵称
                .claim("nickname", jwtInfo.getNickname())
                //用户头像
                .claim("avatar", jwtInfo.getAvatar())
                .signWith(SignatureAlgorithm.HS256, getKeyInstance())
                .compact();

        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken:token
     * @return : 是否有效
     */
    public static boolean checkJwtTToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkJwtTToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if(StringUtils.isEmpty(jwtToken)){
            return false;
        }
        try {
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解析 token，根据 token 获取会员id
     * @return ：用户基本信息
     */
    public static JwtInfo getMemberByJwtToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) {
            return null;
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();

        JwtInfo jwtInfo = new JwtInfo(claims.get("id").toString(), claims.get("nickname").toString(), claims.get("avatar").toString());
        return jwtInfo;
    }

}