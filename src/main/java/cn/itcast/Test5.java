package cn.itcast;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Test5 {

    public static void main(String[] args) throws IOException {

        //1.连接rest接口
        HttpHost http=new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder builder= RestClient.builder(http);//rest构建器
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(builder);//高级客户端对象 （连接）

        //2.封装查询请求
        SearchRequest searchRequest=new SearchRequest("sku");
        searchRequest.types("doc"); //设置查询的类型
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchSourceBuilder.size(0);
        
        searchRequest.source(searchSourceBuilder);
        
        //3.获取查询结果
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();

        Terms terms = (Terms) aggregationMap.get("sku_category");

        List<? extends Terms.Bucket> buckets =  terms.getBuckets();
        for( Terms.Bucket bucket:buckets ){
            System.out.println( bucket.getKeyAsString()+":"+bucket.getDocCount() );
        }
        restHighLevelClient.close();
    }

}
