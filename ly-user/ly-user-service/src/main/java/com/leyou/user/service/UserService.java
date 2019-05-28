package com.leyou.user.service;

import com.alibaba.fastjson.JSONObject;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.omg.IOP.Codec;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/5/26$
 * @description $
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static  final  String KEY_PREFIX="user:verify:phone:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Boolean checkData(String data, Integer type) {
        //判断数据类型
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALD_USE_DATA_TYPE);
        }

        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {
        //生成key
        String key = KEY_PREFIX+phone;
        String code = generateCode(6);
        //发送消息到mq中
        HashMap<Object, Object> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        //发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code", JSONObject.toJSON(msg));
        //保存验证码
        stringRedisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
    }

    /**
     * 生成指定位数的随机数字
     * @param len 随机数的位数
     * @return 生成的随机数
     */
    public static String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(
                Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }

    public void register(User user, String code) {
        //校验验证码
        String cachePhone = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //校验验证码
        if (!StringUtils.equals(code, cachePhone)) {
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }

        //对密码进行加密
        user.setPassword(CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword()));
        user.setSalt(user.getUsername());
        user.setCreated(new Date());
        //写入数据库
        userMapper.insert(user);


    }

    public User queryByUsernameAndPassWord(String username, String password) {
          User record = new User();
          record.setUsername(username);
        User user = userMapper.selectOne(record);
        //校验
        if(user == null){
            throw  new  LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //校验密码
       if( !StringUtils.equals(user.getPassword(),CodecUtils.passwordBcryptEncode(user.getUsername(),password))){
           throw  new  LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
       }
       //用户密码正确
        return user;
    }
}
