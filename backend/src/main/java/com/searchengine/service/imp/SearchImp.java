package com.searchengine.service.imp;

import com.google.errorprone.annotations.Var;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.searchengine.client.ServerClient;
import com.searchengine.exception.ContentEncodeException;
import com.searchengine.exception.EnvException;
import com.searchengine.exception.HttpException;
import com.searchengine.proj.*;
import com.searchengine.proj.findata.FindataResponse;
import com.searchengine.proj.flask.FaissResponse;
import com.searchengine.proj.geoss.GeossDataResponse;
import com.searchengine.proj.geoss.GeossInfoResponse;
import com.searchengine.proj.sciencedb.ScienceDbResponse;
import com.searchengine.proj.tpdc.TPDCInfoResponse;
import com.searchengine.proj.tpdc.TPDCResponse;
import com.searchengine.service.Search;
import com.searchengine.utils.HtmlUtil;
import com.searchengine.utils.MathUtil;
import com.searchengine.utils.SSLUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchImp implements Search {
    private final Gson gson;
    private final HttpClient client;
    private final ServerClient serverClient;
    private final Environment environment;

    @Override
    public List<SearchRecommendation> combinedSearch(RequestContent content) {
        String searchContent = content.getInputContent();
        Integer page = content.getPage();
        Integer size = content.getSize();
        try {
            String encodeContent = URLEncoder.encode(searchContent, StandardCharsets.UTF_8.toString());
            // findata线程
//            Callable<List<SearchRecommendation>> findataCallable = new FindataThreadTaskImp(encodeContent, searchContent, page, size);
//            FutureTask<List<SearchRecommendation>> findataFutureTask = new FutureTask<>(findataCallable);
//            Thread findataThread = new Thread(findataFutureTask);

            // scienceDb线程
//            Callable<List<SearchRecommendation>> scienceDbCallable = new ScienceDbThreadTaskImp(encodeContent);
//            FutureTask<List<SearchRecommendation>> scienceDbFutureTask = new FutureTask<>(scienceDbCallable);
//            Thread scienceThread = new Thread(scienceDbFutureTask);

            //TPDC线程
            Callable<List<SearchRecommendation>> tpdcCallable = new TPDCThreadTaskImp(searchContent);
            FutureTask<List<SearchRecommendation>> TpdcFutureTask = new FutureTask<>(tpdcCallable);
            Thread TpdcThread = new Thread(TpdcFutureTask);

            //Geoss线程
            Callable<List<SearchRecommendation>> geossThreadTaskImp = new GeossThreadTaskImp(searchContent);
            FutureTask<List<SearchRecommendation>> geossFutureTask = new FutureTask<>(geossThreadTaskImp);
            Thread geossThread = new Thread(geossFutureTask);
            //启动线程
//            findataThread.start();
//            scienceThread.start();
            geossThread.start();
            TpdcThread.start();

            //获取结果
//            List<SearchRecommendation> findataRecommendations = findataFutureTask.get();
//            List<SearchRecommendation> scienceDbRecommendations = scienceDbFutureTask.get();
            List<SearchRecommendation> tpdcRecommendations = TpdcFutureTask.get();
            List<SearchRecommendation> geossRecommendations = geossFutureTask.get();
            //合并结果
            List<SearchRecommendation> allResult = listMerge(geossRecommendations, tpdcRecommendations, searchContent);

            return allResult;
        } catch (UnsupportedEncodingException e) {
            throw new ContentEncodeException("内容编码错误");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QualityFactor getInfo(InfoRequestContent content) {
        QualityFactor qualityFactor = null;
        if(content.getInstitution().equals("国家综合地球观测数据共享平台")){
            qualityFactor = getGeossInfo(content.getId());
        } else if (content.getInstitution().equals("国家青藏高原科学数据中心")) {
            qualityFactor = getTpdcInfo(content.getId());
        }

        return qualityFactor;
    }

    @AllArgsConstructor
    public class FindataThreadTaskImp implements Callable<List<SearchRecommendation>> {

        private String encodeContent;
        private String searchContent;
        private int page;
        private int size;


        @Override
        public List<SearchRecommendation> call() {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://www.findata.cn/api/search")).header("Content-Type", "application/json")
                    .header("Referer", "https://www.findata.cn/search?search=" + encodeContent +"&page=" + page).POST(HttpRequest.BodyPublishers
                            .ofString("{\"mmq_textQuery\": \" " + searchContent + "\", \"page\":" + page + ", \"size\" : " + size + "}")).build();
            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                throw new HttpException("findata网络请求出错");
            }
            FindataResponse findataResponse = gson.fromJson(response.body(), FindataResponse.class);
            List<FindataResponse.FindataRecommendation> recommendDatas = findataResponse.getData().getRecommendData();
            ArrayList<SearchRecommendation> findataRecommendData = new ArrayList<>();
            for (FindataResponse.FindataRecommendation recommendData : recommendDatas) {
                SearchRecommendation searchRecommendation = new SearchRecommendation();
                searchRecommendation.setAuthor(recommendData.getAuthor());
                searchRecommendation.setDate(recommendData.getDate());
                searchRecommendation.setDescription(recommendData.getDescription());
                searchRecommendation.setInstitution(recommendData.getInstitution());
                searchRecommendation.setTitle(recommendData.getTitle());
                searchRecommendation.setUrl(recommendData.getUrl());
                findataRecommendData.add(searchRecommendation);
            }
            return findataRecommendData;
        }


    }

    @AllArgsConstructor
    public class ScienceDbThreadTaskImp implements Callable<List<SearchRecommendation>> {
        private String encodeContent;

        @Override
        public List<SearchRecommendation> call() throws Exception {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("p", encodeContent);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://www.scidb.cn/api/sdb-query-service/query?queryCode=&q=" + encodeContent)).header("Content-Type", "application/json;charset=UTF-8")
                    .header("Accept", "application/json, text/plain, */*")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                throw new HttpException("scienceDb网络请求出错");
            }
            ScienceDbResponse scienceDbResponse = gson.fromJson(response.body(), ScienceDbResponse.class);
            List<ScienceDbResponse.ScienceDbRecommendation> recommendDatas = scienceDbResponse.getData().getData();
            ArrayList<SearchRecommendation> scienceDbRecommendData = new ArrayList<>();
            for (ScienceDbResponse.ScienceDbRecommendation recommendData : recommendDatas) {
                SearchRecommendation searchRecommendation = new SearchRecommendation();
                List<String> authorNames = new ArrayList<>();
                for (ScienceDbResponse.Author author : recommendData.getAuthor()) {
                    authorNames.add(author.getNameZh());
                }
                searchRecommendation.setAuthor(authorNames.stream().collect(Collectors.joining(",")));
                searchRecommendation.setId(recommendData.getId());
                searchRecommendation.setDate(recommendData.getDataSetPublishDate());
                searchRecommendation.setDescription(HtmlUtil.removeHtmlTags(recommendData.getIntroductionZh()));
                searchRecommendation.setInstitution("ScienceDB");
                searchRecommendation.setTitle(HtmlUtil.removeHtmlTags(recommendData.getTitleZh()));
                String url = "https://www.scidb.cn/en/detail?dataSetId=" + recommendData.getDataSetId()+ "&version=" + recommendData.getVersion();
                searchRecommendation.setUrl(url);
                scienceDbRecommendData.add(searchRecommendation);
            }
            return scienceDbRecommendData;
        }
    }

    @AllArgsConstructor
    public class GeossThreadTaskImp implements Callable<List<SearchRecommendation>>{
        private String searchContent;
        @Override
        public List<SearchRecommendation> call() throws Exception {
            //请求体
            String requestbody = "{\"pageable\":{\"currentPage\":\"0\",\"pageSize\":\"10\",\"pageNum\":\"\"},\"sort\":{\"sortBy\":\"\",\"sortType\":\"\"},\"requestBody\":{\"type\":\"DATASET\",\"title\":\"" + searchContent+ "\"}}";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://noda.ac.cn/datasharing/allDataSearch")).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestbody)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            GeossDataResponse geossDataResponse = gson.fromJson(response.body(), GeossDataResponse.class);
            List<GeossDataResponse.GeossDataContainer> recommendDatas = geossDataResponse.getResponseBody();
            ArrayList<SearchRecommendation> geossRecommendData = new ArrayList<>();
            for (GeossDataResponse.GeossDataContainer data : recommendDatas) {
                HttpRequest infoRequest = HttpRequest.newBuilder().uri(URI.create("https://noda.ac.cn/datasharing/getDataInfo/" + data.getId())).header("Content-Type", "application/json")
                        .header("Accept-Language", "zh-CN,zh;q=0.9").POST(HttpRequest.BodyPublishers.noBody()).build();
                HttpResponse<String> infoResponse = client.send(infoRequest, HttpResponse.BodyHandlers.ofString());
                GeossInfoResponse geossInfoResponse = gson.fromJson(infoResponse.body(), GeossInfoResponse.class);
                GeossInfoResponse.DateDetail geossDetail = geossInfoResponse.getResponseBody();
                SearchRecommendation searchRecommendation = new SearchRecommendation();
                String url = "https://noda.ac.cn/datasharing/datasetDetails/" + geossDetail.getId();
//                String url = "https://noda.ac.cn/datasharing/getDataInfo/" + geossDetail.getId();
                searchRecommendation.setUrl(url);
                searchRecommendation.setId(geossDetail.getId());
                searchRecommendation.setInstitution("国家综合地球观测数据共享平台");
                searchRecommendation.setAuthor(geossDetail.getContributor().getFullName());
                searchRecommendation.setTitle(geossDetail.getTitle());
                searchRecommendation.setDescription(geossDetail.getDescription());
                searchRecommendation.setDate(geossDetail.getCommitDate());
                geossRecommendData.add(searchRecommendation);

            }
            return geossRecommendData;
        }
    }

    @AllArgsConstructor
    public class TPDCThreadTaskImp implements Callable<List<SearchRecommendation>>{
        private String searchContent;

        @Override
        public List<SearchRecommendation> call() throws Exception {
            String requestBody = "{\"title\":\"" + searchContent + "\",\"pageSize\":10,\"pageNum\":1,\"downloadSort\":false,\"viewsSort\":false,\"tsUpdateTimeSort\":false,\"placeId\":\"\",\"subjectId\":\"\",\"themeId\":\"\",\"isImportant\":false,\"isEnglish\":false}";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://data.tpdc.ac.cn/view/metadataView/page")).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            SSLUtil.disableSSLVerification();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TPDCResponse tpdcResponse = gson.fromJson(response.body(), TPDCResponse.class);
            List<TPDCResponse.Records> RecommendDatas = tpdcResponse.getContext().getMetadataViewVOPage().getRecords();
            ArrayList<SearchRecommendation> TpdcRecommendations = new ArrayList<>();
            for (TPDCResponse.Records recommendData : RecommendDatas) {
                String url = "https://data.tpdc.ac.cn/zh-hans/data/" + recommendData.getId();
                SearchRecommendation searchRecommendation = SearchRecommendation.builder()
                        .author(recommendData.getAuthorName())
                        .id(recommendData.getId())
                        .description(recommendData.getDescription())
                        .institution("国家青藏高原科学数据中心")
                        .download(recommendData.getDownload())
                        .title(recommendData.getTitle())
                        .visit(recommendData.getHitCount())
                        .date(recommendData.getTsUpdated())
                        .url(url).build();
                TpdcRecommendations.add(searchRecommendation);
            }
            return TpdcRecommendations;
        }
    }

    public <T> void getGrade(List<SearchRecommendation> recommendationList, String searchContent) {
        ArrayList<String> titles = new ArrayList<>();
        for (SearchRecommendation searchRecommendation : recommendationList) {
            titles.add(searchRecommendation.getTitle());
        }
        String titles_str = gson.toJson(titles);
        // 根据环境变量执行不同的逻辑
        String activeProfile = environment.getProperty("spring.profiles.active");
        if("local".equals(activeProfile)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("search", searchContent);
            jsonObject.addProperty("content", titles_str);

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:5000/faiss"))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                List<Double> cosine_similarity = gson.fromJson(response.body(), List.class);
                for (int i = 0; i < cosine_similarity.size(); i++) {
                    Double similarity = cosine_similarity.get(i);
                    BigDecimal bigDecimal = new BigDecimal(MathUtil.scoreFormat(similarity)).setScale(1, RoundingMode.HALF_UP);
                    recommendationList.get(i).setRelevance(bigDecimal.doubleValue());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if ("dev".equals(activeProfile)) {
            Map<String, String> requestbody = new HashMap<>();
            requestbody.put("search", searchContent);
            requestbody.put("content", titles_str);
            List<Double> cosine_similarity = serverClient.getCosineSimilarity(requestbody);
            for (int i = 0; i < cosine_similarity.size(); i++) {
                Double similarity = cosine_similarity.get(i);
                BigDecimal bigDecimal = new BigDecimal(MathUtil.scoreFormat(similarity)).setScale(1, RoundingMode.HALF_UP);
                recommendationList.get(i).setRelevance(bigDecimal.doubleValue());
            }
        } else {
            throw new EnvException("环境错误");
        }


    }
    public List<SearchRecommendation> listMerge(List<SearchRecommendation> l1, List<SearchRecommendation> l2, String searchContent){
        ArrayList<SearchRecommendation> combineList = new ArrayList<>(l1);
        combineList.addAll(l2);
        getGrade(combineList, searchContent);


        combineList.sort(Comparator.comparingDouble(SearchRecommendation::getRelevance).reversed());
        return combineList;
    }

    private QualityFactor getGeossInfo(String id){
        String url = "https://noda.ac.cn/datasharing/getDataInfo/" + id;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept-language", "zh-CN,zh;q=0.9")
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.noBody()).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            GeossInfoResponse geossInfoResponse = gson.fromJson(response.body(), GeossInfoResponse.class);
            QualityFactor qualityFactor = geoss2Factor(geossInfoResponse);
            return qualityFactor;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static QualityFactor geoss2Factor(GeossInfoResponse infoResponse){
        GeossInfoResponse.DateDetail responseBody = infoResponse.getResponseBody();
        List<String> categoryTheme = responseBody.getCategory().getCategoryTheme();
        List<String> categorySubject = responseBody.getCategory().getCategorySubject();
        categoryTheme.removeIf(Objects::isNull);
        categorySubject.removeIf(Objects::isNull);
        String url = "https://noda.ac.cn/datasharing/datasetDetails/" + infoResponse.getResponseBody().getId();
        QualityFactor qualityFactor = QualityFactor.builder().temporalRelevance(MathUtil.getRangValue(3.0, 4.8))
                        .spatialRelevance(MathUtil.getRangValue(3.0, 4.8))
                .title(responseBody.getTitle())
                .description(responseBody.getDescription().trim())
                .visit(responseBody.getVisit())
                .download(responseBody.getDownload())
                .commitDate(responseBody.getCommitDate())
                .language(responseBody.getLanguage())
                .keyword(responseBody.getKeyword())
                .theme(categoryTheme)
                .subject(categorySubject)
                .timeResolution(responseBody.getTimeInfo().getTimeResolution())
                .timeRange(responseBody.getTimeInfo().getTimeRange())
                .updateType(responseBody.getTimeInfo().getUpdateType())
                .projectInfo(responseBody.getSpatialLocation().getProjectInfo())
                .spatialLocation(responseBody.getSpatialLocation().getSpatialLocation())
                .scale(responseBody.getSpatialLocation().getScale())
                .westLng(responseBody.getSpatialLocation().getWestLng())
                .northLat(responseBody.getSpatialLocation().getNorthLat())
                .eastLng(responseBody.getSpatialLocation().getEastLng())
                .southLat(responseBody.getSpatialLocation().getSouthLat())
                .mapCenterLat(responseBody.getSpatialLocation().getMapCenterLat())
                .mapCenterLng(responseBody.getSpatialLocation().getMapCenterLng())
                .mapZoom(responseBody.getSpatialLocation().getMapZoom())
                .dataSource(responseBody.getDataSource().getDataSource())
                .dataProcess(responseBody.getDataSource().getDataProcess())
                .url(url).build();


        return qualityFactor;
    }

    private QualityFactor getTpdcInfo(String id){
        String url = "https://data.tpdc.ac.cn/view/metadataView/detail/";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("metadataId", id);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TPDCInfoResponse tpdcInfoResponse = gson.fromJson(response.body(), TPDCInfoResponse.class);
            QualityFactor qualityFactor = tpdc2Factor(tpdcInfoResponse);
            return qualityFactor;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static QualityFactor tpdc2Factor(TPDCInfoResponse tpdcInfoResponse){
        TPDCInfoResponse.DataContext dataContext  = tpdcInfoResponse.getContext();
        TPDCInfoResponse.MetadataVO metadataVO = dataContext.getMetadataVO();
        String url = "https://data.tpdc.ac.cn/zh-hans/data/" + tpdcInfoResponse.getContext().getMetadataVO().getId();
        QualityFactor qualityFactor = QualityFactor.builder()
                .title(metadataVO.getTitle())
                .description(HtmlUtil.handleString(metadataVO.getDescription()))
                .commitDate(metadataVO.getTsPublish())
                .download(metadataVO.getDownload())
                .scale(metadataVO.getScale() == null ? null : metadataVO.getScale().toString())
                .eastLng(metadataVO.getEast().toString())
                .northLat(metadataVO.getNorth().toString())
                .southLat(metadataVO.getSouth().toString())
                .westLng(metadataVO.getWest().toString())
                .subject(dataContext.getKeywordStandVOList().stream().map(TPDCInfoResponse.KeywordStand::getName).collect(Collectors.toList()))
                .theme(dataContext.getThemeList().stream().map(TPDCInfoResponse.Theme::getName).collect(Collectors.toList()))
                .language(metadataVO.getLanguage())
                .projectInfo(metadataVO.getProjection())
                .spatialLocation(dataContext.getPlaceKeywordVOList().stream().map(TPDCInfoResponse.PlaceKeyword::getKeyword).collect(Collectors.joining(",")))
                .timeRange(metadataVO.getTimeDescription())
                .visit(metadataVO.getHitCount())
                .download(metadataVO.getDownload())
                .spatialRelevance(MathUtil.getRangValue(3.0, 4.0))
                .temporalRelevance(MathUtil.getRangValue(3.2, 4.2))
                .url(url).build();
        return qualityFactor;
    }

}
