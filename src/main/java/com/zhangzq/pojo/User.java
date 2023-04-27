package com.zhangzq.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户实体类")
public class User {
    @ApiModelProperty("用户ID")
    private int id;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("密码")
    private String pwd;
}
