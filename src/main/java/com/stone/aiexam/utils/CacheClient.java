package com.stone.aiexam.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class CacheClient {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置缓存
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }


    /**
     * 查询，防止缓存穿透
     * @param prefix
     * @param id
     * @param type
     * @param sqlFunc
     * @param time
     * @param unit
     * @return
     * @param <T>
     * @param <R>
     */
    public <T,R> T queryWithNullCache(String prefix, R id, Class<T> type, Function<R,T> sqlFunc,Long time,TimeUnit unit){
        String key = prefix + id;

        //1. 查询redis,如果redis存在，直接返回
        String json = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(json)){
            return JSONUtil.toBean(json,type);
        }
        //isBlank又不等于null,那就是"",是用于防止缓存穿透的，直接返回null
        if(json!=null){
            return null; //返回null
        }
        //2. 如果redis不存在，查询mysql
        T t = sqlFunc.apply(id);

        //3. mysql如果存在，写入redis缓存重建，并返回
        if(t!=null){
            this.set(key,t,time,unit);
            return t;
        }
        //4. mysql如果不存在，设置缓存为null防止缓存穿透 ,并返回Null
        stringRedisTemplate.opsForValue().set(key,"",2,TimeUnit.MINUTES);
        return null;
    }


    /**
     * 删除缓存
     * @param key
     */
    public void evict(String key){
        stringRedisTemplate.delete(key);
    }


}
