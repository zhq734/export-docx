import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhenghuiqiang on 17/10/17.
 */
public class ExportDocUtils {

	private static final Configuration JACKSON_CONFIGURATION = Configuration
			.builder().mappingProvider(new JacksonMappingProvider())
			.jsonProvider(new JacksonJsonProvider()).build();

	/**
	 *
	 * @param jsonData
	 * @param templateName
	 * @param outputFileName
	 * @throws JSONException
	 */
	public static void reportDoc(String jsonData,
								 String templateName,
								 String outputFileName) throws JSONException {
		JSONObject reportData = new JSONObject(jsonData);
		DocumentContext jsonDC = JsonPath.using(JACKSON_CONFIGURATION)
				.parse(jsonData);

		reportDoc(reportData, jsonDC, templateName, outputFileName);
	}

	/**
	 *
	 * @param jsonData
	 * @param in
	 * @param out
	 * @throws JSONException
	 */
	public static void reportDoc(String jsonData,
								 InputStream in,
								 OutputStream out) throws JSONException {
		JSONObject reportData = new JSONObject(jsonData);
		DocumentContext jsonDC = JsonPath.using(JACKSON_CONFIGURATION)
				.parse(jsonData);

		reportDoc(reportData, jsonDC, in, out);
	}

	/**
	 * 根据模板导出word文件
	 *
	 * @param reportData  ReportData对象为数据对象，里面存储JSON数据
	 * @param jsonDC
	 * @param templateName  模板文件路径
	 * @param outputFileName  输出文件路径
	 */
	public static void reportDoc(JSONObject reportData,
								 DocumentContext jsonDC,
								 String templateName,
								 String outputFileName) {
		try {
			//读模版信息
			InputStream in = new FileInputStream(templateName);
			OutputStream out = new FileOutputStream(outputFileName);

			reportDoc(reportData, jsonDC, in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param reportData
	 * @param jsonDC
	 * @param in
	 * @param out
	 */
	public static void reportDoc(JSONObject reportData,
								 DocumentContext jsonDC,
								 InputStream in,
								 OutputStream out) {
		try {
			//读取模板信息  这边采用的是Velocity的模板引擎,支持Velocity的语法
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity );

			//初始化并封装数据
			IContext context = getReportContext(report, reportData, jsonDC);

			//合并替换数据,并输出到out上
			report.process(context, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成模板上需要的context数据
	 *
	 * @param report
	 * @param params
	 * @param jsonDC
	 * @return
	 * @throws Exception
	 */
	private static IContext getReportContext(IXDocReport report,
											 JSONObject params,
											 DocumentContext jsonDC) throws Exception {
		IContext context = null;
		if (report != null) {
			context = report.createContext();
			FieldsMetadata metadata = report.createFieldsMetadata();

			//TODO 要准备一张空白图片
			IImageProvider blankImageProvider = new ByteArrayImageProvider(ImageHelper.produceUnitBlankPng(), true);

			String imagePath = "/Users/zhenghuiqiang/Downloads/1.jpg";
			IImageProvider imageProvider1 = new ByteArrayImageProvider(new FileInputStream(imagePath));

			Iterator it = params.keys();
			while(it.hasNext()) {
				String key = it.next().toString();
				Object value = jsonDC.read(key);

				if (value instanceof List) {
					List list = (List) value;
					if (list.size() != 0) {
						Object firstItem = list.get(0);
						if (firstItem instanceof Map) {
							List<Map<String, Object>> shenheList = (List<Map<String, Object>>) value;
							int i = 0;
							for(Map<String, Object> shenheMap : shenheList) {
								if (i % 2 == 0) {
									shenheMap.put("image", imageProvider1);
									shenheMap.put("creatorName", "");
								} else {
									shenheMap.put("image", blankImageProvider);
								}
								i ++;
							}

							metadata.addFieldAsImage(key, "item.image");
						}
					}
				}
				context.put(key, value);
			}

			report.setFieldsMetadata(metadata);
		}
		return context;
	}

	public static void main(String[] args) throws Exception {
		// 表单数据json字符串
		String formDataJsonString = "{\n" +
				"    \"xiangmumingcheng\": \"\",\n" +
				"    \"sheng\": \"浙江\",\n" +
				"    \"shi\": \"杭州\",\n" +
				"    \"qu\": \"西湖\",\t\n" +
				"    \"xiangmuxingzhi\": [\"新建\"],\n" +
				"    \"guobojine\": \"2\",\n" +
				"    \"qitajine\": \"4\",\n" +
				"    \"keyangusuan\": \"10\",\n" +
				"    \"daikuanjine\": \"3\",\n" +
				"    \"xiangmufuzeren\": \"\",\n" +
				"    \"fabaofangshi\": [\"EPC总承包\"],\n" +
				"    \"tedianzhongdian\": \"1\\n2\\n3\\n4\\n5\\n5\\n6\\n7\\n8\\n8\\n9\\n\\nasef\\n阿斯顿\",\n" +
				"    \"jianshezhouqi\": \"30\",\n" +
				"    \"zhouqishuoming\": [\"是\", \"12\"],\n" +
				"    \"yewudanyuan\": \"撒旦反而文化\",\n" +
				"    \"jishufuzeren\": \"\",\n" +
				"    \"jszqdanwei\": \"1\",\n" +
				"    \"chushegaisuan\": \"11\",\n" +
				"    \"touzikongzhijia\": \"11\",\n" +
				"    \"chanyebankuai\": \"环保\",\n" +
				"    \"neirongbianjie\": \"啊是否啊士大夫\\nasdfasdf \\n啊士大夫\\n啊士大夫\\n啊士大夫\\nasdf \\n\\n\\n\\nasdf啊士大夫\\n啊士大夫\",\n" +
				"    \"zichoujine\": \"1\",\n" +
				"    \"shenheyijian\":[\n" +
//                    "        {\"id\":\"dmadmin-管理001\",\"message\":\"哈时间看sdkfjhskld LKhsdklfh kjshdf k看经核实对方客户老客户快乐是东方红的哈迪斯\",\"creatorName\":\"管理001\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//                    "        {\"id\":\"dmadmin-郑辉强\",\"message\":\"啦啦啦啦\",\"creatorName\":\"郑辉强\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//                    "        {\"message\":\"水电费水电费水电费水电费\",\"creatorName\":\"超级管理员\",\"created\":\"2017-07-03\",\"dateTime\":1499040000000,\"creator\":\"dmadmin\"}\n" +
				"    ],\n" +

				"    \"shenhekuang\":[\n" +
				"        {\"id\":\"dmadmin-管理001\",\"message\":\"哈时间看sdkfs就开始对方和了看哈萨克的了返回老客户速度快发货了客户水电费jhskld LKhsdklfh kjshdf k看经核实对方客户老客户快乐是东方红的哈迪斯\",\"creatorName\":\"管理001\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
				"        {\"id\":\"dmadmin-郑辉强\",\"message\":\"啦啦啦啦\",\"creatorName\":\"郑辉强\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
				"        {\"message\":\"水电费水电费水电费水电费\",\"creatorName\":\"超级管理员\",\"created\":\"2017-07-03\",\"dateTime\":1499040000000,\"creator\":\"dmadmin\"}\n" +
				"    ],\n" +
				"    \"guquanbili\": [\n" +
				"        [\"序号\", \"股东名称\", \"出资额度(万元)\", \"比例(%)\"],\n" +
				"        [\"1\", \"发股东1\", \"2\", \"20\"],\n" +
				"        [\"2\", \"发股东2\", \"2\", \"20\"],\n" +
				"        [\"3\", \"发股东3\", \"2\", \"20\"],\n" +
				"        [\"4\", \"发股东32\", \"2\", \"20\"],\n" +
				"        [\"5\", \"发股东324\", \"2\", \"20\"],\n" +
				"        [\"合计\", \"\", \"10\", \"\"]\n" +
				"    ]\n" +
				"}";


		ExportDocUtils.reportDoc(formDataJsonString,
				"/Users/zhenghuiqiang/项目/newProj/export-doc/测试模板Velocity1.docx",
				"/Users/zhenghuiqiang/项目/newProj/export-doc/测试模板Velocity1-out.docx");


//		// 表单数据json字符串
//		String formDataJsonString = "{\n" +
//				"    \"ssq_date\": \"2017年09月21日\",\n" +
//				"    \"ssq_creator\": \"超级管理员\",\n" +
//				"    \"ssq_department\": \"杭州容方信息有限公司\",\n" +
//				"    \"ssq_telephone\": \"15659189372\",\t\n" +
//				"    \"ssq_title\": \"这是一个送审签\",\n" +
//				"    \"publishDate\": \"2017年10月01日\",\n" +
//				"    \"ssq_type\": [\"制度文件\"],\n" +
//				"    \"ssq_opinion1\":[\n" +
//				"        {\"id\":\"dmadmin-管理001\",\"message\":\"了开始的恢复离开家看来速度快发货看水电费水电费\",\"creatorName\":\"管理001\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//				"        {\"id\":\"dmadmin-郑辉强\",\"message\":\"kjsdhf 卡还是贷款就范德萨会计法 水电费\",\"creatorName\":\"郑辉强\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//				"        {\"message\":\"删掉了看风景数据掉色今晚开幕\",\"creatorName\":\"超级管理员\",\"created\":\"2017-07-03\",\"dateTime\":1499040000000,\"creator\":\"dmadmin\"}\n" +
//				"    ],\n" +
//				"    \"ssq_opinion2\":[\n" +
//				"        {\"id\":\"dmadmin-管理001\",\"message\":\"水电费路径删掉了看风景昆仑决圣诞快乐发就水电费 LKhsdklfh kjshdf k看经核实对方客户老客户快乐是东方红的哈迪斯\",\"creatorName\":\"管理001\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//				"        {\"id\":\"dmadmin-郑辉强\",\"message\":\"水电费就是的李开复\",\"creatorName\":\"郑辉强\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//				"        {\"id\":\"dmadmin-超级管理员\",\"message\":\"水电费ISO杜甫iOS大家反馈了\",\"creatorName\":\"超级管理员\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//				"        {\"message\":\"水电费水电费水电费水电费\",\"creatorName\":\"水电费客户水电费接口好的风景\",\"created\":\"2017-07-03\",\"dateTime\":1499040000000,\"creator\":\"dmadmin\"}\n" +
//				"    ],\n" +
//				"    \"ssq_opinion3\":[\n" +
//				"        {\"id\":\"dmadmin-管理001\",\"message\":\"水电费卡萨丁或减法空间设计地方看见了红的哈迪斯\",\"creatorName\":\"管理001\",\"dateTime\":1499817600000,\"created\":\"2017-07-12\",\"creator\":\"dmadmin\"},\n" +
//				"        {\"message\":\"水电费上岛咖啡就是地方\",\"creatorName\":\"超级管理员\",\"created\":\"2017-07-03\",\"dateTime\":1499040000000,\"creator\":\"dmadmin\"}\n" +
//				"    ]\n" +
//				"}";
//
//
//		JSONObject reportData = new JSONObject(formDataJsonString);
//		DocumentContext jsonDC = JsonPath.using(JACKSON_CONFIGURATION)
//				.parse(formDataJsonString);
//
//		ExportDocUtils.reportDoc(reportData, jsonDC,
//				"/Users/zhenghuiqiang/项目/newProj/export-doc/测试模板Velocity.docx",
//				"/Users/zhenghuiqiang/项目/newProj/export-doc/测试模板Velocity-out.docx");
	}

}
