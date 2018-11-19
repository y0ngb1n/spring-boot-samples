package com.y0ngb1n.swagger2.controller;

import com.y0ngb1n.swagger2.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理.
 *
 * @author y0ngb1n
 * @version 1.0.0
 */
@Api(value = "用户管理", description = "用户信息的「增、删、查、改」操作")
@RestController
@RequestMapping("users")
public class UserController {

  private static Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

  @ApiOperation(value = "用户列表")
  @GetMapping("")
  public List<User> getUserList() {
    return new ArrayList<>(users.values());
  }

  @ApiOperation(value = "创建用户", notes = "根据 User 对象创建用户")
  @ApiImplicitParam(name = "user", value = "用户详细实体", required = true, dataTypeClass = User.class)
  @PostMapping("")
  public User createUser(@RequestBody User user) {
    users.put(user.getId(), user);
    return user;
  }

  @ApiOperation(value = "用户详细信息", notes = "根据 ID 获取用户详细信息")
  @ApiImplicitParam(name = "id", value = "用户 ID", required = true, dataType = "Long")
  @GetMapping("{id}")
  public User getUser(@PathVariable Long id) {
    return users.get(id);
  }

  @ApiOperation(value = "更新用户详细信息", notes = "根据 ID 指定更新对象, 并根据 User 信息来更新用户详细信息")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "用户 ID", required = true, dataTypeClass = Long.class),
      @ApiImplicitParam(name = "user", value = "用户详细实体", required = true, dataType = "User")
  })
  @PutMapping("{id}")
  public User updateUser(@PathVariable Long id, @RequestBody User user) {
    User updateUser = users.get(id);
    updateUser.setName(user.getName());
    updateUser.setAge(user.getAge());
    updateUser.setEmail(user.getEmail());
    users.put(id, updateUser);
    return updateUser;
  }

  @ApiOperation(value = "删除用户", notes = "根据 ID 指定删除对象")
  @ApiImplicitParam(name = "id", value = "用户 ID", required = true, dataType = "Long")
  @DeleteMapping("{id}")
  public String deleteUser(@PathVariable Long id) {
    users.remove(id);
    return "success";
  }
}
