package com.searchengine;

import com.google.gson.Gson;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.searchengine.proj.InfoRequestContent;
import com.searchengine.proj.QualityFactor;
import com.searchengine.proj.RequestContent;
import com.searchengine.proj.geoss.GeossDataResponse;
import com.searchengine.service.imp.SearchImp;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {
    @Autowired
    SearchImp searchImp;
    @Autowired
    Gson gson;
    @Autowired
    HttpClient client;
    @Test
    void contextLoads() throws Exception{
        String searchContent = "降雨量";
        String requestbody = "{\"pageable\":{\"currentPage\":\"0\",\"pageSize\":\"25\",\"pageNum\":\"\"},\"sort\":{\"sortBy\":\"\",\"sortType\":\"\"},\"requestBody\":{\"type\":\"DATASET\",\"title\":\"" + searchContent+ "\"}}";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://noda.ac.cn/datasharing/allDataSearch")).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestbody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        GeossDataResponse geossDataResponse = gson.fromJson(response.body(), GeossDataResponse.class);
        List<GeossDataResponse.GeossDataContainer> recommendDatas = geossDataResponse.getResponseBody();
        List<QualityFactor> qualityFactors = new ArrayList<>();

        for (GeossDataResponse.GeossDataContainer recommendData : recommendDatas) {
            String dataId = recommendData.getId();
            InfoRequestContent content = new InfoRequestContent(dataId, "国家综合地球观测数据共享平台");
            QualityFactor info = searchImp.getInfo(content);
            qualityFactors.add(info);
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream("E:\\Desktop\\test\\InternetThings.csv"), StandardCharsets.UTF_8)) {
            // 写入中文标题行
            writer.write("访问量,下载量,提交日期,标题,描述,语言,关键字,主题,学科," +
                    "时间分辨率,时间范围,更新周期,坐标系信息,空间位置,比例尺,西经,北纬,东经,南纬," +
                    "地图中心经度,地图中心纬度,地图缩放等级,数据来源,数据生产,url\n");

            for (QualityFactor qualityFactor : qualityFactors) {
                writer.write(qualityFactor.toCsvRow() + "\n");
                System.out.println(qualityFactor.toCsvRow() + "\n");
            }
            System.out.println("CSV 文件已生成 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
