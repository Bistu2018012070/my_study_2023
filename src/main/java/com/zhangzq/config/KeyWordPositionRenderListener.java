package com.zhangzq.config;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.zhangzq.pojo.WordItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FeianLing
 * @date 2019/8/21
 * pdf 渲染监听，获取到关键词对应的坐标 x y h w
 */
@Data
public class KeyWordPositionRenderListener implements RenderListener {
    /**
     * 要查找的关键字集合
     */
    private List<String> findText;
    /**
     * 出现无法取到值默认为12
     */
    private float defaultH = 12F;
    /**
     * 可能出现无法完全覆盖，提供修正参数，默认2
     */
    private float fixHeight = 2F;
    /**
     * pdf 渲染页码
     */
    private Integer pageNum;
    /**
     * 匹配的坐标集合
     */
    private List<WordItem> rsWordItems = new ArrayList<>();

    /**
     * 构造器1
     *
     * @param findText
     */
    public KeyWordPositionRenderListener(List<String> findText, Integer pageNum) {
        this.findText = findText;
        this.pageNum = pageNum;
    }

    /**
     * 构造器2
     *
     * @param findText
     * @param defaultH
     * @param fixHeight
     */
    public KeyWordPositionRenderListener(List<String> findText, float defaultH, float fixHeight) {
        this.findText = findText;
        this.defaultH = defaultH;
        this.fixHeight = fixHeight;
    }

    @Override
    public void beginTextBlock() {

    }

    @Override
    public void renderText(TextRenderInfo info) {
        String text = info.getText();
        if (null != text) {
            for (String keyword : findText) {
                if (text.contains(keyword)) {
                    Rectangle2D.Float bound = info.getBaseline().getBoundingRectange();
                    WordItem item = new WordItem();
                    item.setContent(keyword);
                    item.setX(bound.x);
                    item.setY((bound.y - fixHeight));
                    item.setH(bound.height == 0 ? defaultH : bound.height);
                    item.setW(bound.width);
                    item.setPageNum(pageNum);
                    rsWordItems.add(item);
                }
            }
        }
    }

    @Override
    public void endTextBlock() {

    }

    @Override
    public void renderImage(ImageRenderInfo imageRenderInfo) {

    }
}
