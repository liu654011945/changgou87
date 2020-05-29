package com.changgou.search.service;

import java.util.Map; /***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.service
 * @version 1.0
 * @date 2020/5/10
 */
public interface SkuService {
    void importSku();

    Map search(Map<String,String> searchMap);

}
