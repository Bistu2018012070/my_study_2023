package com.zhangzq.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.sun.deploy.cache.BaseLocalApplicationProperties;
import com.zhangzq.config.KeyWordPositionRenderListener;
import com.zhangzq.pojo.User;
import com.zhangzq.pojo.WordItem;
import jdk.nashorn.internal.runtime.Context;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pdf")
public class pdfController {

    @GetMapping("/test")
    public void queryUserList() throws IOException, DocumentException {
        Context.ThrowErrorManager Logs = null;
        String distPath = "H:/User/Pictures/document.pdf";
        InputStream inputStream = new ClassPathResource("/document.pdf").getInputStream();
        List<String> findTextList = new ArrayList<>();
        findTextList.add("项目编号");
        //查找所有敏感词的坐标
        List<WordItem> wordItems = new ArrayList<>();
        PdfReader src = new PdfReader(inputStream);
        PdfReaderContentParser parser = new PdfReaderContentParser(src);
        int pageNum = src.getNumberOfPages();
        for (int i = 1; i < pageNum; i++) {
            KeyWordPositionRenderListener listener = new KeyWordPositionRenderListener(findTextList, i);
            parser.processContent(i, listener);
            wordItems.addAll(listener.getRsWordItems());
        }
        if (wordItems.isEmpty()) {
            Logs.error("替换敏感词的坐标集合为空！！！！请排查问题原因");
        }

        PdfReader dest = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(dest, new FileOutputStream(distPath));
        for (WordItem item : wordItems) {
            PdfContentByte canvas = stamper.getOverContent(item.getPageNum());
            //初始阶段完成 开始替换
            canvas.saveState();
            System.out.println("更新背景：" + item);
            //黑色背景覆盖
            canvas.setColorFill(BaseColor.BLACK);
            Map<String,String> keyMap = null;
            String site = keyMap.get(item.getContent());
            Float siteF = 0F;
            if (!ObjectUtils.isEmpty(site)) {
                siteF = Float.valueOf(site);
            }
            // Logs.info("y坐标偏移：{}", siteF);
            //Logs.error("y坐标偏移：{}");
            //定位
            canvas.rectangle(item.getX(), item.getY() + siteF, item.getW(), item.getH());
            //填充
            canvas.fill();
            //还原状态
            canvas.restoreState();
        }


    }
}
