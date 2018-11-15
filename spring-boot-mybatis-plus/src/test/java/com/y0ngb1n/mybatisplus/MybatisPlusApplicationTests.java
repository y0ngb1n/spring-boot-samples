package com.y0ngb1n.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y0ngb1n.mybatisplus.mapper.UserMapper;
import com.y0ngb1n.mybatisplus.domain.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusApplicationTests {

  @Autowired
  private UserMapper userMapper;

  @Test
  public void testSelectAll() {
    List<User> users = userMapper.selectList(null);
    Assert.assertEquals(5, users.size());
    log.debug("users: {}", users);
  }

  @Test
  public void testInsert() {
    User user = new User();
    user.setName("y0ngb1n");
    user.setAge(18);
    user.setEmail("y0ngb1n.me@gmail.com");

    // User(id=1063006743143497729, name=y0ngb1n, age=18, email=y0ngb1n.me@gmail.com)
    int result = userMapper.insert(user);
    Assert.assertEquals(1, result);
  }

  @Test
  public void testUpdate() {
    // 按 ID 更新
    User user = new User();
    user.setId(3L);
    user.setName("Tony");
    int result = userMapper.updateById(user);
    Assert.assertEquals(1, result);

    // 按条件更新
    user = new User();
    user.setName("Apple");
    user.setAge(66);

    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
    updateWrapper.likeRight("email", "test");
    updateWrapper.le("age", 20);

    result = userMapper.update(user, updateWrapper);
    Assert.assertEquals(2, result);
  }

  @Test
  public void testDelete() {
    // 按 ID 删除
    int result = userMapper.deleteById(1L);
    Assert.assertEquals(1, result);

    // 批量删除
    Set<Long> ids = new HashSet<>();
    ids.add(2L);
    ids.add(3L);
    result = userMapper.deleteBatchIds(ids);
    Assert.assertEquals(2, result);

    // 按条件删除
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("name", "Sandy");
    columnMap.put("age", 21L);
    result = userMapper.deleteByMap(columnMap);
    Assert.assertEquals(1, result);

    // 按条件删除
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("name", "Billie");
    queryWrapper.gt("age", 18L);
    result = userMapper.delete(queryWrapper);
    Assert.assertEquals(1, result);
  }

  @Test
  public void testSelectPage() {
    // 无条件分页查询
    Page<User> page = new Page<>(2, 3);
    // SELECT COUNT(1) FROM user
    // SELECT id,name,age,email FROM user limit 3 offset 3
    // total: 5, users: [User(id=4, name=Sandy, age=21, email=test4@baomidou.com), ...]
    IPage<User> result = userMapper.selectPage(page, null);
    log.debug("total: {}, users: {}", result.getTotal(), result.getRecords());

    // 带条件分页查询
    page = new Page<>(0, 3);
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.gt("id", 3);
    // SELECT COUNT(1) FROM user WHERE id > ?
    // SELECT id,name,age,email FROM user WHERE id > ? limit 3
    // total: 2, users: [User(id=4, name=Sandy, age=21, email=test4@baomidou.com), ...]
    result = userMapper.selectPage(page, queryWrapper);
    log.debug("total: {}, users: {}", result.getTotal(), result.getRecords());

    // 以上操作都会先查询总数, 再进行分页查询
    // 当 total 不为 0 时分页插件不会进行 count 查询
    page = new Page<>(0, 3, 1);
    // SELECT id,name,age,email FROM user limit 3
    // total: 1, users: [User(id=1, name=Jone, age=18, email=test1@baomidou.com), ...]
    result = userMapper.selectPage(page, null);
    log.debug("total: {}, users: {}", result.getTotal(), result.getRecords());
  }
}
