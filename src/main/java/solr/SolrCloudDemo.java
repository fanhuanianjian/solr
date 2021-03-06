package solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrCloudDemo {
	
	private CloudSolrClient client;
	
	/**
	 * 创建Solr云连接
	 */
	@Before
	public void testBefore(){
		
		client = new CloudSolrClient("192.168.1.10:2181,192.168.1.10:2182,192.168.1.10:2183");
		client.setDefaultCollection("collection1");
	}
	
	/**
	 * 提交
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@After
	public void commit() throws SolrServerException, IOException{
		
		client.commit();
		
	}
	
	
	/**
	 * 新增
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test
	public void testInsert() throws SolrServerException, IOException{
		
		for (int i = 1; i < 11; i++) {
			
			SolrInputDocument doc = new SolrInputDocument();
			
			doc.addField("id", "00"+i);
			
			doc.addField("ego", "分布式项目"+i);
			
			client.add(doc);
		}

		
	}
	@Test
	public void testDelete() throws SolrServerException, IOException{
		
		client.deleteById("004");

	}
	
	@Test
	public void testQuery() throws SolrServerException, IOException{
		
		//可视化界面左侧条件
		SolrQuery params = new SolrQuery();
		//设置q
		params.setQuery("ego:*");
		//设置分页
		//从第几条开始查询,从0开始
		params.setStart(0);
		//查询几个
		params.setRows(10);

		
		//启动高亮
		params.setHighlight(true);
		//设置高亮列
		params.addHighlightField("ego");
		//设置前缀
		params.setHighlightSimplePre("<span style='color:red;'>");
		//设置后缀
		params.setHighlightSimplePost("</span>");
		
		//相当于点击查询按钮, 本质,向solr web服务器发送请求,并接收响应. query对象里面包含了返回json数据
		QueryResponse response = client.query(params);
		
		Map<String, Map<String, List<String>>> hh = response.getHighlighting();
		
		
		//取出docs{}
		SolrDocumentList solrList = response.getResults();
		
		for (SolrDocument doc : solrList) {
			System.out.println(doc.getFieldValue("id"));
			System.out.println("未高亮:"+doc.getFieldValue("ego"));
			Map<String, List<String>> map = hh.get(doc.getFieldValue("id"));
			System.out.println(map);
			//list可能为null
			List<String> list = map.get("ego");
			System.out.println(list);
			if(list!=null&&list.size()>0){
				System.out.println("高亮:"+list.get(0));
			}else{
				System.out.println("没有高亮内容");
			}
			System.out.println(doc.getFieldValue("ego"));
		}
		
	}

}
