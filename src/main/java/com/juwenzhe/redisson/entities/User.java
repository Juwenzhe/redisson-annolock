
package com.juwenzhe.redisson.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 测试User类
 * @author juwenzhe123@163.com
 * @date 2020/6/8 23:17
 */
@NoArgsConstructor
@Data
@ToString
@Accessors(chain = true)
public class User implements Serializable {
    private static final long serialVersionUID = -8189421749835908467L;
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户年龄
     */
    private int age;

}


