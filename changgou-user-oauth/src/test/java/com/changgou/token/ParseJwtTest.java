package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIwMjE2ODY5MzksInVzZXJfbmFtZSI6InpoYW5nc2FuIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiIzNTUxZmY3ZC0yNmMyLTQ0YjgtODY5My04ODdjNTViYjAzNTMiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInNjb3BlIjpbImFwcCJdfQ.pXNHWQ9JCsvDSonHHeQeYui0LcTNduc6xAFaOhzfxV8fCbjeyTRlHUYC5TvlpDh2O6QJwZsPKeavRUAJ5xtZJcVyAryrD0E75VqLEm-7sybo7lnC8p9MzmbeeAiyzzfEsjmeNuVNMO-7LIibYd-yqlL23nArS8MC_p-SoC1LiPdTsCkamDQJ2T8qMczanrKYY5rSULHE6i9BHD4BPzoRn6P5iRgPwe8SJtZIorVrykJirKUucy3xr2efC3PMOlpGsLW1q4DEpis-tXFCiVdN0yHBo0LbGrTeIE88vdyY067-oGcyXTFPs0crqs-9A_-SaNNihWSkYj3laYz0cX0ngQ";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtEmpJLi/qJWEp6/9NhODsRhxAEg+oYYmRCFZO/DxlQ/4DsWbGCcBcC7AAVfCiAex6fTTxn96Y3Apo6pXCQ1iiVch1fxM0LHTFj0V0/II0HKjM9/yfGrz0CAVt8hUYVQXAnAZbOr5ACOl9EI0oocZM3QZ/vYs8FXriMERaG9LV91oH0fLzVOZHkv5GJGjWb5XzFB7NsdoR7fJ1ZoS/48H+sN7ApV473XJt9P9saA2rcnz/j4IalZw3rU9ZHfmbT1sZ7JhMbTRnGo1DBbxX/CdCG9ZLCu747Rz1MNUNGEK7urcG3uJo2/4grirJyju8W7igEogQezaTxbW/xR6/caL8QIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
