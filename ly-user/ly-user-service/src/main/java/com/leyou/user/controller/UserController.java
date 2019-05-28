package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/5/26$
 * @description 用户$
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    /***
    * @Description: 验证用户是否存在
    * @Param: [data, type]
    * @return: org.springframework.http.ResponseEntity<java.lang.Boolean>
    * @Author: zhoukx
    * @Date: 2019/5/26
    */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean>  checkData(@PathVariable("data") String data,@PathVariable("type") Integer type){
        return ResponseEntity.ok(userService.checkData(data,type));
    }
     
    /*** 
    * @Description:  发送 短信
    * @Param:  
    * @return:  
    * @Author: zhoukx
    * @Date: 2019/5/26 
    */ 
    @PostMapping("code")
    public ResponseEntity<Void>   sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /*** 
    * @Description: 注册功能
    * @Param: [user, code] 
    * @return: org.springframework.http.ResponseEntity<java.lang.Void> 
    * @Author: zhoukx
    * @Date: 2019/5/27 
    */ 
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user,  @RequestParam("code") String code){
        //自定义  异常处理
//       if(result.hasFieldErrors()){BindingResult result,
//           throw  new RuntimeException(result.getFieldErrors().stream()
//                   .map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
//       }
        userService.register(user,code);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /***
    * @Description:  根据用户名和密码查询用户
    * @Param: [username, password]
    * @return: org.springframework.http.ResponseEntity<com.leyou.user.pojo.User>
    * @Author: zhoukx
    * @Date: 2019/5/28
    */
    @GetMapping("/query")
    public ResponseEntity<User> queryByUsernameAndPassWord(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){

        return ResponseEntity.ok(userService.queryByUsernameAndPassWord(username,password));
    }

}
