package com.zhangzq.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author FeianLing
 * @date 2019/8/21
 */
@Data
public class WordItem implements Serializable {
    /**
     * 关键词
     */
    private String content;
    /**
     * pdf x坐标
     */
    private Float x;
    /**
     * pdf y坐标
     */
    private Float y;
    /**
     * pdf h坐标
     */
    private Float h;
    /**
     * pdf w坐标
     */
    private Float w;
    /**
     * pdf 页码
     */
    private Integer pageNum;
}
