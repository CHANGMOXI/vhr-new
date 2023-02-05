package org.changmoxi.vhr.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

/**
 * @author CZS
 * @create 2023-01-30 15:05
 **/
public class EasyExcelUtil {
    /**
     * web写
     *
     * @param response
     * @param fileNameWithoutSuffix
     * @param sheetName
     * @param head
     * @param data
     * @throws IOException
     */
    public static void webWrite(HttpServletResponse response, String fileNameWithoutSuffix, String sheetName, Class head, Collection<?> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // URLEncoder.encode可以防止中文乱码，这里和EasyExcel没有关系
        String fileName = URLEncoder.encode(fileNameWithoutSuffix, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), head).sheet(sheetName).doWrite(data);
    }

    /**
     * web写，失败返回JSON提示
     *
     * @param response
     * @param data
     * @throws IOException
     */
    public static void webWriteFailedReturnJson(HttpServletResponse response, String fileNameWithoutSuffix, String sheetName, Class head, Collection<?> data, CustomizeStatusCode errorStatusCode) throws IOException {
        try {
            // xlsx的MIME类型
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // URLEncoder.encode可以防止中文乱码，这里和EasyExcel没有关系
            String fileName = URLEncoder.encode(fileNameWithoutSuffix, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), head).autoCloseStream(Boolean.FALSE).sheet(sheetName).doWrite(data);
        } catch (Exception e) {
            // 失败，重置response，返回JSON提示
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = response.getWriter();
            RespBean exportError = RespBean.error(errorStatusCode);
            //TODO log.error(e.getMessage(), e); 由于还没集成日志框架，暂且放着
            writer.write(JSON.toJSONString(exportError));
            writer.flush();
            writer.close();
        }
    }

    /**
     * 不创建对象的web写
     *
     * @param response
     * @param fileNameWithoutSuffix
     * @param sheetName
     * @param head
     * @param data
     * @throws IOException
     */
    public static void noModelWebWrite(HttpServletResponse response, String fileNameWithoutSuffix, String sheetName, List<List<String>> head, List<List<Object>> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // URLEncoder.encode可以防止中文乱码，这里和EasyExcel没有关系
        String fileName = URLEncoder.encode(fileNameWithoutSuffix, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream()).head(head).autoCloseStream(Boolean.TRUE).sheet(sheetName).doWrite(data);
    }

    /**
     * 不创建对象的web写，失败返回JSON提示
     *
     * @param response
     * @param fileNameWithoutSuffix
     * @param sheetName
     * @param head
     * @param data
     * @param errorStatusCode
     * @throws IOException
     */
    public static void noModelWebWriteFailedReturnJson(HttpServletResponse response, String fileNameWithoutSuffix, String sheetName, List<List<String>> head, List<List<Object>> data, CustomizeStatusCode errorStatusCode) throws IOException {
        try {
            // xlsx的MIME类型
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // URLEncoder.encode可以防止中文乱码，这里和EasyExcel没有关系
            String fileName = URLEncoder.encode(fileNameWithoutSuffix, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream()).head(head).autoCloseStream(Boolean.FALSE).sheet(sheetName).doWrite(data);
        } catch (Exception e) {
            // 失败，重置response，返回JSON提示
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = response.getWriter();
            RespBean exportError = RespBean.error(errorStatusCode);
            //TODO log.error(e.getMessage(), e); 由于还没集成日志框架，暂且放着
            writer.write(JSON.toJSONString(exportError));
            writer.flush();
            writer.close();
        }
    }
}