package com.changgou.goods.config;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.config
 * @version 1.0
 * @date 2020/5/17
 */
/*
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    */
/***
     * 定义JwtTokenStore
     * @param jwtAccessTokenConverter
     * @return
     *//*

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    */
/***
     * 定义JJwtAccessTokenConverter
     * @return
     *//*

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());
        return converter;
    }
    */
/**
     * 获取非对称加密公钥 Key
     * @return 公钥 Key
     *//*

    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    */
/***
     * Http安全配置，对每个到达系统的http请求链接进行校验
     * @param http
     * @throws Exception
     *//*

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //所有请求必须认证通过
        http.authorizeRequests()
                //下边的路径放行
                .anyRequest().//所有的请求都需要进行校验 只有登录了才可以访问
                authenticated();    //其他地址需要认证授权
    }
}*/
