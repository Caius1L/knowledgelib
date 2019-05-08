package com.lib.utils;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.bson.util.StringRangeSet;
import org.codehaus.plexus.util.FileUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;
import com.hankcs.hanlp.HanLP;
import com.hankcs.lucene.HanLPAnalyzer;
import com.hankcs.lucene.HanLPTokenizer;
import com.lib.dto.LuceneSearchVo;
import com.lib.dto.SerResult;
import com.lib.entity.FileInfo;
import com.lib.enums.Const;

/**
 * 搜索索引 Lucene 5.5+
 * 
 * @author Administrator
 * 
 */
public class LuceneSearchUtil {
	/**
	 * 根据索引搜索doc
	 * 
	 * @param text
	 * @param pageNo
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	// 上一次检索条件
	private static Object oldBooleanQuery = null;
	// 索引存放路径
	private static String indexPath = Const.ROOT_PATH+"lucene";
	// 保存索引结果，分页中使用
	private static TopDocs result = null;
	// 分词器
	// private static Analyzer analyzer = new HanLPAnalyzer();
	// 词法分析器
	private static Analyzer analyzer = new HanLPAnalyzer() {
		@Override
		protected TokenStreamComponents createComponents(String arg0) {
			Tokenizer tokenizer = new HanLPTokenizer(
					HanLP.newSegment().enableMultithreading(true).enableAllNamedEntityRecognize(true).enableOffset(true).enableIndexMode(true),
					null, true);
			return new TokenStreamComponents(tokenizer);
		}
	};
	// 关键字查询条件
	private static Query queryText = null;

	// 知识点查找条件
	private static TermQuery queryKeyWord = null;

	/**
	 * 
	 * @param file
	 * @param pageNo
	 *            页数
	 * @param fileClassId
	 *            类型id
	 * @param flag
	 *            //是否二次查询条件
	 * @return
	 */
	public static List<LuceneSearchVo> indexFileSearch(FileInfo file, String keyWord, Date endTime, Integer pageNo,
			Integer pageSize, List<Long> fileClassId, Integer flag) {
		if (pageNo > 1) {
			page(pageNo, pageSize);
		}
		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;

		try {

			directory = FSDirectory.open(new File(indexPath).toPath());
			ireader = DirectoryReader.open(directory);
			indexSearch = new IndexSearcher(ireader);
			// 多个条件组合查询
			BooleanQuery booleanQuery = new BooleanQuery();

			// 判断是否二次查询
			if (flag == 1)
				booleanQuery.add((BooleanQuery) oldBooleanQuery, BooleanClause.Occur.MUST);
			else {
				oldBooleanQuery = null;
			}

			// 查询条件一 字段查询
			queryText = null;
			if (file.getFileName() != null && !"".equals(file.getFileName())) {

				String[] fields = { "fileName", "fileText", "fileBrief", "fileKeyWords" };
				Map<String, Float> boost = new HashMap<String, Float>();
				boost.put("fileKeyWords", 2.0f);
				boost.put("fileName", 1.0f);
				boost.put("fileBrief", 1.0f);
				boost.put("fileText", 1.0f);
				// 创建QueryParser对象,第一个表示搜索Field的字段,第二个表示搜索使用分词器
				QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer, boost);
				// 生成Query对象
				queryText = queryParser.parse(file.getFileName());
				booleanQuery.add(queryText, BooleanClause.Occur.MUST);
			}

			queryKeyWord = null;
			if (keyWord != null && !"".equals(keyWord)) {

				Term term = new Term("fileKeyWords", keyWord);
				queryKeyWord = new TermQuery(term);
				booleanQuery.add(queryKeyWord, BooleanClause.Occur.MUST);

			}

			if (file.getFileCreateTime() != null || endTime != null) {

				// 查询条件二日期
				Date sDate = file.getFileCreateTime();
				if ((sDate == null || "".equals(sDate))) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(1900, 0, 1);
					sDate = calendar.getTime();
				}
				Date eDate = endTime;
				// 若只有起始值结束值默认为当天
				if ((eDate == null || "".equals(eDate))) {
					eDate = new Date();
				}

				if ((sDate != null && !"".equals(sDate)) && (eDate != null || !"".equals(eDate))) {

					// Lucene日期转换格式不准，改用format格式
					// sDateStr=DateTools.dateToString(sDate,
					// DateTools.Resolution.MINUTE);
					// eDateStr=DateTools.dateToString(eDate,
					// DateTools.Resolution.MINUTE);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					BytesRef sDateStr = new BytesRef(sdf.format(sDate));
					BytesRef eDateStr = new BytesRef(sdf.format(eDate));

					// 时间范围查询
					Query timeQuery = new TermRangeQuery("fileCreateTime", sDateStr, eDateStr, true, true);
					booleanQuery.add(timeQuery, BooleanClause.Occur.MUST);
				}
			}
			// 查询条件三分类查询
			if (fileClassId != null) {
				BooleanQuery queryClassId = new BooleanQuery();

				for (Long id : fileClassId) {

					TermQuery termQuery = new TermQuery(new Term("fileClassId", id + ""));
					queryClassId.add(termQuery, BooleanClause.Occur.SHOULD);
				}
				booleanQuery.add(queryClassId, BooleanClause.Occur.MUST);
			}
			// 查询条件四类型查询

			if (file.getFileExt() != null && !"".equals(file.getFileExt()) && !file.getFileExt().equals("all")) {

				BooleanQuery queryFileExt = new BooleanQuery();

				List<String> typeList = null;
				if (file.getFileExt().equals("office")) {
					typeList = JudgeUtils.officeFile;
				}
				if (file.getFileExt().equals("video")) {
					typeList = JudgeUtils.videoFile;
					typeList.addAll(JudgeUtils.audioFile);
				}
				if (file.getFileExt().equals("img")) {
					typeList = JudgeUtils.imageFile;
				} else if (file.getFileExt().equals("else")) {
					typeList = JudgeUtils.elseFile;
				}

				if (typeList != null && !typeList.equals("")) {
					for (String type : typeList) {
						TermQuery termQuery = new TermQuery(new Term("fileExt", type));
						queryFileExt.add(termQuery, BooleanClause.Occur.SHOULD);
					}
				}
				booleanQuery.add(queryFileExt, BooleanClause.Occur.MUST);
			}
			// 查询条件五公开状态
			TermQuery termQuery = new TermQuery(new Term("fileState", "5"));
			booleanQuery.add(termQuery, BooleanClause.Occur.MUST);
			oldBooleanQuery = booleanQuery;
			// 搜索结果 TopDocs里面有scoreDocs[]数组，里面保存着索引值
			//System.out.println(booleanQuery);
			result = indexSearch.search(booleanQuery, 100000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return page(pageNo, pageSize);

	}

	/**
	 * 包装每页的file
	 * 
	 * @param pageNo
	 *            页数
	 * @param num
	 *            每页数量
	 * @return
	 */
	private static List<LuceneSearchVo> page(Integer pageNo, Integer pageSize) {

		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;

		// 结果集
		List<LuceneSearchVo> page = new ArrayList<LuceneSearchVo>();

		// 每页的file
		try {

			directory = FSDirectory.open(new File(indexPath).toPath());

			ireader = DirectoryReader.open(directory);

			indexSearch = new IndexSearcher(ireader);
			
			//ScoreDoc last = getLastScoreDoc(pageNo, pageSize, (Query)oldBooleanQuery, indexSearch);
			//TopDocs result = indexSearch.searchAfter(last, (Query)oldBooleanQuery, pageSize);
			
			for (int i = (pageNo - 1) * pageSize, j = 0; i < result.scoreDocs.length && j < pageSize; i++, j++) {
				LuceneSearchVo vo = new LuceneSearchVo();
				int fileId = result.scoreDocs[i].doc;
				Document file = indexSearch.doc(fileId);

				// vo.setFileClassId(Long.valueOf(file.get("fileClassId")));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					vo.setFileCreateTime(sdf.parse(file.get("fileCreateTime")));
				} catch (Exception e) {
					// TODO: handle exception
				}
				vo.setFileId(Long.valueOf(file.get("fileId")));
				vo.setFileExt(file.get("fileExt"));

				vo.setFilePath(file.get("filePath"));

				// vo.setFileUserId(Long.valueOf(file.get("fileUserId")));

				// vo.setFileState(Integer.parseInt(file.get("fileState")));

				vo.setUserName(file.get("fileUserName"));

				vo.setFileUuid(file.get("fileUuid"));

				vo.setFileName(file.get("fileName"));

				if (file.get("fileBrief") != null && !"".equals(file.get("fileBrief")))
					vo.setFileBrief(file.get("fileBrief"));
				else {
					vo.setFileBrief(file.get("fileSummarys"));
				}

				if (file.get("fileText") != null && !"".equals(file.get("fileText")))
					vo.setFileText(file.get("fileText").substring(0,
							file.get("fileText").length() > 150 ? 150 : file.get("fileText").length()));

				if (file.get("fileKeyWords") != null && !"".equals(file.get("fileKeyWords")))

				{
					String[] keyWords = file.get("fileKeyWords").split(",");
					List<String> keyWordList = Arrays.asList(keyWords);
					vo.setFileKeyWords(keyWordList);
				}

				try {
					if (queryText != null) {

						String fileName = "";
						if (file.get("fileName") != null && !"".equals(file.get("fileName"))) {
							fileName = displayHtmlHighlight(queryText, analyzer, "fileName", file.get("fileName"), 100);
							if (!"".equals(fileName) && fileName != null)
								vo.setFileName(fileName);
						}

						String fileBrief = "";
						if (file.get("fileBrief") != null && !"".equals(file.get("fileBrief"))) {
							fileBrief = displayHtmlHighlight(queryText, analyzer, "fileBrief", file.get("fileBrief"),
									100);
							if (!"".equals(fileBrief) && fileBrief != null)
								vo.setFileBrief(fileBrief);
						}

						String fileText = "";
						if (file.get("fileText") != null && !"".equals(file.get("fileText"))) {
							fileText = displayHtmlHighlight(queryText, analyzer, "fileText", file.get("fileText"), 100);
							if (!"".equals(fileText) && fileText != null)
								vo.setFileText(fileText);
						}

						String fileKeyWords = "";
						if (file.get("fileKeyWords") != null && !"".equals(file.get("fileKeyWords"))) {
							fileKeyWords = displayHtmlHighlight(queryText, analyzer, "fileKeyWords",
									file.get("fileKeyWords"), 100);
							if (!"".equals(fileKeyWords) && fileKeyWords != null) {

								String[] keyWords = fileKeyWords.split(",");

								List<String> keyWordList = Arrays.asList(keyWords);

								vo.setFileKeyWords(keyWordList);

							}

						}

					}
					if (queryKeyWord != null) {

						String fileKeyWords = "";
						if (file.get("fileKeyWords") != null && !"".equals(file.get("fileKeyWords"))) {
							fileKeyWords = displayHtmlHighlight(queryKeyWord, analyzer, "fileKeyWords",
									file.get("fileKeyWords"), 100);

							if (!"".equals(fileKeyWords) && fileKeyWords != null) {

								String[] keyWords = fileKeyWords.split(",");

								List<String> keyWordList = Arrays.asList(keyWords);

								vo.setFileKeyWords(keyWordList);

							}

						}

					}
				} catch (Exception e) {
					e.printStackTrace();// TODO
				}
				// System.out.println(vo);
				page.add(vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return page;
	}

	/**
	 * 返回总页数
	 * 
	 * @return
	 */
	public static Integer totalPage() {
		if (result != null)
			return result.totalHits;
		else
			return 0;
	}


	/**
	 * 判断并获取索引内容
	 * @param fileId
	 * @return
	 */
	public static String judge(Long fileId) {

		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;
		// 生成Query对象
		Query query = null;
		// new一个文档对象
		Document document = new Document();
		// 文件简介
		String fileText = null;
		try {
			directory = FSDirectory.open(new File(indexPath).toPath());

			ireader = DirectoryReader.open(directory);

			indexSearch = new IndexSearcher(ireader);

			// Term对象
			Term term = new Term("fileId", fileId.toString());

			TermQuery termQuery = new TermQuery(term);

			TopDocs topdocs = indexSearch.search(termQuery, 1);

			if (topdocs.totalHits != 0) {
				
				document = indexSearch.doc(topdocs.scoreDocs[0].doc);
				fileText = document.get("fileText");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return fileText;

	}

	
	/**
	 * 获取简介
	 * 
	 * @param docId
	 * @param docName
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 * @throws ParseException
	 */
	public static String extractSummary(Long fileId, long size) {

		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;
		// new一个文档对象
		Document document = new Document();
		
		String  fileSummarys=null;
		try {
			directory = FSDirectory.open(new File(indexPath).toPath());

			ireader = DirectoryReader.open(directory);

			indexSearch = new IndexSearcher(ireader);

			// Term对象
			Term term = new Term("fileId", fileId.toString());

			TermQuery termQuery = new TermQuery(term);

			TopDocs topdocs = indexSearch.search(termQuery, 1);

			document = indexSearch.doc(topdocs.scoreDocs[0].doc);

			fileSummarys = document.get("fileSummarys");
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return fileSummarys;

	}

	/**
	 * /** 获取主题词
	 * 
	 * @param docId
	 * @return
	 * @throws IOException
	 */
	public static List<String> extractKeyword(Long fileId, long size) {

		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;
		// new一个文档对象
		Document document = new Document();
		// 主题词变量
		List<String> fileKeyWords = new ArrayList<String>();
		try {
			directory = FSDirectory.open(new File(indexPath).toPath());

			ireader = DirectoryReader.open(directory);

			indexSearch = new IndexSearcher(ireader);

			Term term = new Term("fileId", fileId.toString());
			TermQuery termQuery = new TermQuery(term);
			// System.out.println(termQuery);
			TopDocs topdocs = indexSearch.search(termQuery, 1);

			document = indexSearch.doc(topdocs.scoreDocs[0].doc);

			String[] keyWords = document.get("fileKeyWords").split(",");
			long total = 1;
			for (String keyWord : keyWords) {
				total++;
				fileKeyWords.add(keyWord);
				if (total == size)
				{
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return fileKeyWords;
	}

	/**
	 * 查找关联文档ids
	 * 
	 * @param strs
	 *            关联词
	 * @return
	 * @throws IOException
	 */
	public static List<Long> extractRelation(Long fileId) {
		// new一个文档对象
		Document document = new Document();
		// 关联文档的id
		List<Long> fileIds = new ArrayList<Long>();
		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;
		// 多个条件组合查询

		try {
			directory = FSDirectory.open(new File(indexPath).toPath());

			ireader = DirectoryReader.open(directory);

			indexSearch = new IndexSearcher(ireader);


			
			List<String> filekeyWords=extractKeyword(fileId, 10);
			
			QueryParser queryParser = new QueryParser("fileKeyWords", analyzer);
			BooleanQuery booleanQuery1=new BooleanQuery();
			for(String str:filekeyWords)
			{
				Term term = new Term("fileKeyWords", str);
				booleanQuery1.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
			}
			//System.out.println(booleanQuery1);
			TopDocs topdocs = indexSearch.search(booleanQuery1, 15);
			// System.out.println("共找到" + topdocs.scoreDocs.length + ":条记录");
			List<List<String>> listl=new ArrayList<List<String>>();
			for (ScoreDoc scoreDocs : topdocs.scoreDocs) {
				int documentId = scoreDocs.doc;
				document = indexSearch.doc(documentId);
				String[] keyWords = document.get("fileKeyWords").split(",");
				List<String> list=new ArrayList<String>();
				for (String keyWord : keyWords) {
					
					list.add(keyWord);
			
				}
				boolean flag=false;
				if(Tfidf.Tfidf(list,filekeyWords)>0.3)
				{
					flag=true;
				}
				for(List<String> str:listl)
				{
					if(Tfidf.Tfidf(str,list)>0.95)
					{
						flag=false;
					}
				}
				if(flag)
				{	
					listl.add(list);
					fileIds.add(Long.valueOf(document.get("fileId")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return fileIds;
	}

	/**
	 * 获取相关段落
	 * 
	 * @param fileId
	 * @param keyWord
	 * @return
	 */
	public static List<SerResult> extractParagrap(String keyWord,Long size) {

		// 保存索引文件的地方
		Directory directory = null;
		// IndexReader reader=DirectoryReader
		DirectoryReader ireader = null;
		// 创建 IndexSearcher对象，相比IndexWriter对象，这个参数就要提供一个索引的目录就行了
		IndexSearcher indexSearch = null;
		// new一个文档对象
		Document document = new Document();
		// 相关段落
		List<SerResult> list = new ArrayList<SerResult>();
		try {
			directory = FSDirectory.open(new File(indexPath).toPath());

			ireader = DirectoryReader.open(directory);

			indexSearch = new IndexSearcher(ireader);

			String[] fields = { "fileName", "fileText", "fileBrief", "fileKeyWords" };
			Map<String, Float> boost = new HashMap<String, Float>();
			boost.put("fileName", 1.0f);
			boost.put("fileBrief", 1.0f);
			boost.put("fileText", 1.0f);
			boost.put("fileKeyWords", 2.0f);
			// 创建QueryParser对象,第一个表示搜索Field的字段,第二个表示搜索使用分词器
			QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer, boost);
			// 生成Query对象
			Query query = queryParser.parse(keyWord);
			// System.out.println(termQuery);
			TopDocs topdocs = indexSearch.search(query, 10);
			
			
			//System.out.println(topdocs.scoreDocs.length);
			for (int i = 0; i < topdocs.scoreDocs.length; i++) {

				document = indexSearch.doc(topdocs.scoreDocs[i].doc);

				Long fileId = Long.valueOf(document.get("fileId"));
				String fileName = document.get("fileName");
				String fileUuid = document.get("fileUuid");
				String result = document.get("fileText");
				//System.out.println(result);
				String fileExt = document.get("fileExt");
				String filePath = document.get("filePath");
				if (JudgeUtils.imageFile.contains(fileExt)) {
					String uuid = StringValueUtil.getUUID();
					FileUtils.copyFile(new File(Const.ROOT_PATH + filePath + "." + fileExt),
							new File(Const.CONTAINER_PATH + "resource/temp/" + uuid + ".png"));
					list.add(new SerResult(
							"<img width='400' src='" + Const.HEAD_URL + "resource/temp/" + uuid + ".png' />", fileId,
							fileName, fileUuid));

				} else if (result != null) {

					List<String> paragraphs = ParagraphUtil.toParagraphList(result);
					
					Map<String, Integer> maps = new LinkedHashMap<String, Integer>();
					
					/*if(paragraphs==null)
					{
						maps.put(result, 1);
					}*/
					for (String paragrap : paragraphs) {
						// size 表示查找多少关键字
						//System.out.println(HanLP.extractKeyword(keyWord, 10));
						
						Integer total=0;
						for(String str1:HanLP.extractKeyword(paragrap,3))
						{	Integer index=3;
							for(String str2:HanLP.segment1(keyWord))
							{
								
								if(str1.equals(str2))
								{
									total=total+index;
								}
								index--;
							}
							
						}
						if(total>0)
						maps.put(paragrap, total);
						
					}
					List<Map.Entry<String,Integer>> KeyWordRank = new ArrayList<Map.Entry<String,Integer>>(maps.entrySet());
					
					Collections.sort(KeyWordRank,new Comparator<Map.Entry<String,Integer>>() {

						@Override
						public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
							
							return o2.getValue().compareTo(o1.getValue());
						}
			        });
					for (Entry<String, Integer> map : KeyWordRank) {
							
							//System.out.println(map.getKey());
							//TF-IDF判重
							boolean flag=true;
							for(int j=0;j<list.size();j++)
							{
								if(Tfidf.Tfidf(HanLP.segment1(list.get(j).getContent()), HanLP.segment1(map.getKey()))>0.95)
								{
									flag=false;
								};
							}
							if(flag)
							list.add(new SerResult(map.getKey(), fileId, fileName, fileUuid));
							if(list.size()>=size)
								return list;
					}
				}
				if(list.size()>=size)
					return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(ireader, directory);
		}
		return list;
	}

	static String displayHtmlHighlight(Query query, Analyzer analyzer, String fieldName, String fieldContent,
			int fragmentSize) throws IOException, InvalidTokenOffsetsException {
		// 创建一个高亮器
		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<font color='#c00'>", "</font>"),
				new QueryScorer(query));
		Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
		highlighter.setTextFragmenter(fragmenter);
		return highlighter.getBestFragment(analyzer, fieldName, fieldContent);
	}

	public static void close(DirectoryReader ireader, Directory directory) {
		try {
			if (ireader != null) {
				ireader.close();
			}
			if (directory != null) {
				directory.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unused")
	private static ScoreDoc getLastScoreDoc(int pageNum, int pageSize, Query query,
            IndexSearcher searcher) throws IOException {
        if (pageNum == 1) {
            return null;
        }
        int num = pageSize * (pageNum - 1);
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num - 1];
    }
	
}