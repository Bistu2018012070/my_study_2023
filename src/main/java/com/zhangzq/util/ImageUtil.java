package com.zhangzq.util;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ImageUtil {


    /**
     * pdf插入电子印章水印
     * @param templatePath 原pdf文件路径
     * @param targetPath	生成文件输出路径
     * @param imagePath	图片文件路径
     * @param keyword		关键字
     */
    public static void imageWaterMark(String templatePath,String targetPath,String imagePath,String keyword) {

        try {
            File pdfFile = new File(templatePath);
            byte[] pdfData = new byte[(int) pdfFile.length()];
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(pdfFile);
                inputStream.read(pdfData);
            } catch (IOException e) {
                log.error("PDF读流异常：" + e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error("IO流关闭发生异常：" + e);
                    }
                }
            }
            //指定关键字
//			String keyword = "计算机";
            List<float[]> positions = findKeywordPostions(pdfData, keyword);
            InputStream input = new FileInputStream(templatePath);
            if (positions != null && positions.size() > 0) {
                log.info("发现关键字总条数：" + positions.size());
                log.info("最后一次出现关键字的位置信息：页码=" + (int)positions.get(positions.size()-1)[0] +
                        "，X轴=" + positions.get(positions.size()-1)[1] + "，Y轴=" + positions.get(positions.size()-1)[2]);
                PdfImage(targetPath,input,imagePath,(int)positions.get(positions.size()-1)[1],
                        (int)positions.get(positions.size()-1)[2],(int) positions.get(positions.size()-1)[0]);
            }else {
                log.info("未发现关键字信息");
            }

        } catch (Exception e) {
            log.error("PDF插入图片发生意外异常：" + e);
        }

    }
    /**
     * 添加电子签章水印
     * @param newPdfPath
     * @param srcPdfPath
     * @param imagePath
     * @param width
     * @param height
     * @param page
     */
    private static void PdfImage(String newPdfPath,InputStream srcPdfPath,String imagePath, int width, int height, int page) {

        PdfReader pdfReader = null;
        PdfStamper pdfStamper = null;

        try {
            pdfReader = new PdfReader(srcPdfPath);
            //获取最后一页（计算页面插入）
//			System.out.println("页数"+pdfReader.getNumberOfPages());

            FileOutputStream out = new FileOutputStream(newPdfPath);

            pdfStamper = new PdfStamper(pdfReader, out);
            //设置最后一页
            PdfContentByte pdfContentByte = pdfStamper.getUnderContent(page);
            //压缩图片
            float qrWidth = 42.75f / 35.4f * 72;

            Image qrcodeImage = Image.getInstance(imagePath);
            //设置图片宽高
            qrcodeImage.scaleToFit(qrWidth, qrWidth);

            //水印图片位置坐标
            qrcodeImage.setAbsolutePosition(width + 40, height - 45);
            //插入图片
            pdfContentByte.addImage(qrcodeImage);
            pdfStamper.close();
        }catch (Exception e) {
            log.error("PDF插入图片发生未知异常：" + e);
        }finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }
    }

    /**
     * 查找关键字集合
     * @param pdfData
     * @param keyword
     * @return
     * @throws IOException
     */
    public static List<float[]> findKeywordPostions(byte[] pdfData, String keyword) {
        List<float[]> result = new ArrayList<>();
        List<PdfPageContentPositions> pdfPageContentPositions = getPdfContentPostionsList(pdfData);
        for (PdfPageContentPositions pdfPageContentPosition : pdfPageContentPositions) {
            List<float[]> charPositions = findPositions(keyword, pdfPageContentPosition);
            if (charPositions == null || charPositions.size() < 1) {
                continue;
            }
            result.addAll(charPositions);
        }
        return result;
    }
    /**
     * 读取文件内容
     * @param pdfData
     * @return
     */
    private static List<PdfPageContentPositions> getPdfContentPostionsList(byte[] pdfData) {

        try {
            PdfReader reader = new PdfReader(pdfData);
            List<PdfPageContentPositions> result = new ArrayList<>();
            int pages = reader.getNumberOfPages();
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PdfRenderListener pdfRenderListener = new PdfRenderListener(pageNum);
                //解析pdf，定位位置
                PdfContentStreamProcessor processor = new PdfContentStreamProcessor(pdfRenderListener);
                PdfDictionary pageDic = reader.getPageN(pageNum);
                PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
                try {
                    processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNum), resourcesDic);
                } catch (Exception e) {
                    reader.close();
                    log.error("读取文件内容发生未知异常：" + e);
                }
                String content = pdfRenderListener.getContent();
                List<CharPosition> charPositions = pdfRenderListener.getcharPositions();
                List<float[]> positionsList = new ArrayList<>();
                for (CharPosition charPosition : charPositions) {
                    float[] positions = new float[]{charPosition.getPageNum(), charPosition.getX(), charPosition.getY()};
                    positionsList.add(positions);
                }
                PdfPageContentPositions pdfPageContentPositions = new PdfPageContentPositions();
                pdfPageContentPositions.setContent(content);
                pdfPageContentPositions.setPostions(positionsList);
                result.add(pdfPageContentPositions);
            }
            reader.close();
            return result;
        } catch (Exception e) {
            log.error("查找关键字发生未知异常：" + e);
        }
        return null;
    }
    /**
     * 定位关键字位置
     * @param keyword
     * @param pdfPageContentPositions
     * @return
     */
    private static List<float[]> findPositions(String keyword, PdfPageContentPositions pdfPageContentPositions) {
        List<float[]> result = new ArrayList<>();
        String content = pdfPageContentPositions.getContent();
        List<float[]> charPositions = pdfPageContentPositions.getPositions();
        for (int pos = 0; pos < content.length(); ) {
            int positionIndex = content.indexOf(keyword, pos);
            if (positionIndex == -1) {
                break;
            }
            float[] postions = charPositions.get(positionIndex);
            result.add(postions);
            pos = positionIndex + 1;
        }
        return result;
    }

    static class CharPosition{

        private int pageNum = 0;
        private float x = 0;
        private float y = 0;

        public CharPosition(int pageNum, float x, float y) {

            this.pageNum = pageNum;
            this.x = x;
            this.y = y;
        }
        public int getPageNum() {

            return pageNum;
        }
        public float getX() {
            return x;
        }
        public float getY() {
            return y;
        }
        @Override
        public String toString() {

            return "[pageNum=" + this.pageNum + ",x=" + this.x + ",y=" + this.y + "]";
        }
    }
    private static class PdfPageContentPositions {

        private String content;
        private List<float[]> positions;
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public List<float[]> getPositions() {
            return positions;
        }
        public void setPostions(List<float[]> positions) {
            this.positions = positions;
        }
    }
    private static class PdfRenderListener implements RenderListener {

        private int pageNum;
        private StringBuilder contentBuilder = new StringBuilder();
        private List<CharPosition> charPositions = new ArrayList<>();
        public PdfRenderListener(int pageNum) {
            this.pageNum = pageNum;
        }
        public void beginTextBlock() {
        }
        public void renderText(TextRenderInfo renderInfo) {

            List<TextRenderInfo> characterRenderInfos = renderInfo.getCharacterRenderInfos();
            for (TextRenderInfo textRenderInfo : characterRenderInfos) {
                String word = textRenderInfo.getText();
                if (word.length() > 1) {
                    word = word.substring(word.length() - 1, word.length());
                }
                Rectangle2D.Float rectangle = textRenderInfo.getAscentLine().getBoundingRectange();
                float x = (float)rectangle.getX();
                float y = (float)rectangle.getY();
                CharPosition charPosition = new CharPosition(pageNum, (float)x, (float)y);
                charPositions.add(charPosition);
                contentBuilder.append(word);
            }
        }
        public void endTextBlock() {
        }
        public void renderImage(ImageRenderInfo renderInfo) {
        }
        public String getContent() {
            return contentBuilder.toString();
        }
        public List<CharPosition> getcharPositions() {
            return charPositions;
        }
    }

}
