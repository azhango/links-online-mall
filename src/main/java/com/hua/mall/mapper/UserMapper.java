package com.hua.mall.mapper;

import com.hua.mall.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

/**
 * @author hua
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2022-10-07 19:25:36
 * @Entity com.hua.mall.model.entity.User
 */
public interface UserMapper extends BaseMapper<User> {

    String selectByEmailAddress(@Param("emailAddress") String emailAddress);
}




