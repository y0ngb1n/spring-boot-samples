package com.y0ngb1n.swagger2.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户模型", description = "用户详细信息实体类")
public class User {

  @ApiModelProperty(value = "用户 ID")
  private Long id;

  @ApiModelProperty(value = "名字", allowableValues = "y0ngb1n, tony")
  private String name;

  @ApiModelProperty(value = "年龄", allowableValues = "range[1, 120]")
  private Integer age;

  @ApiModelProperty(value = "邮箱")
  private String email;
}
