<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script src="../../../resource/highchart/code/highcharts.js"></script>
<script src="../../../resource/highchart/code/modules/exporting.js"></script>
<script src="../../../resource/highchart/code/modules/wordcloud.js"></script>
<script src="../../../resource/highchart/code/modules/oldie.js"></script>
<script src="../../../resource/highchart/code/modules/sankey.js"></script>
<script src="../../../resource/highchart/code/modules/dependency-wheel.js"></script>
<title>知识网络</title>
<%@include file="../common/head.jsp"%>
<style type="text/css">
.am-slider-c3 .am-slider-desc {
	background-color: #0e90d2;
	filter: alpha(opacity = 80); /*IE滤镜，透明度50%*/
	-moz-opacity: 0.8; /*Firefox私有，透明度50%*/
	opacity: 0.8; /*其他，透明度50%*/
}

.title-header a .a-black {
	color: #333;
}
</style>
</head>
<body>
	<%@include file="../common/header.jsp"%>
	<div class="am-cf admin-main">
		<!-- sidebar start -->
		<%@include file="../common/slidebar.jsp"%>
		<!-- sidebar end -->
		<!-- content start -->
		<div class="admin-content">
			<div class="admin-content-body">
				<div class="am-cf am-padding">
					<div class="am-fl am-cf title-header">
						<strong class="am-text-primary am-text-lg">公共知识库</strong> <small>
							<c:forEach items="${plist}" var="c">
							/ <a href="user/public/${c.classificationId}/1">${c.classificationName}</a>
							</c:forEach>
						</small>
					</div>
				</div>


				<div class="am-u-sm-12">
					<div id="my-slider" data-am-widget="slider"
						class="am-slider  am-slider-c3"
						data-am-slider='{&quot;animation&quot;:&quot;slide&quot;,&quot;animationLoop&quot;:true,&quot;itemWidth&quot;:150,&quot;itemHeight&quot;:150,&quot;itemMargin&quot;:5}'>
						<ul class="am-slides">
							<c:forEach items="${list}" var="c">
								<li><a href="user/public/${c.classificationId}/1"
									title="${c.classificationName} : ${c.classificationBrief}"><img
										style="width: 130px; height: 130px;"
										src="thumbnail/${c.classificationPicture}" class="am-circle"></a>
									<div class="am-slider-desc">${c.classificationName}</div></li>
							</c:forEach>
						</ul>
					</div>
				</div>

				<c:if test="${page.pageNum == 1}">
					<div data-am-widget="intro"
						class="am-intro am-cf am-intro-default am-u-sm-12">
						<div class="am-g am-intro-bd">
							<div class="am-intro-left am-u-sm-3">
								<img src="thumbnail/${classi.classificationPicture}" alt="小娜"
									style="width: 160px; height: 160px;" />
							</div>
							<div class="am-intro-right am-u-sm-9">
								<h1>${classi.classificationName}</h1>
								<p>${classi.classificationBrief}</p>
								<button class="am-icon-btn am-icon-link" onclick="getLinks()"
									title="点击查看此知识点图谱"
									data-am-modal="{target: '#class-links', closeViaDimmer: true, width: 800, height: 500}"></button>
								<button class="am-icon-btn am-icon-link" onclick="getkey()"
									title="点击查看此知识点热图"
									data-am-modal="{target: '#links', closeViaDimmer: true, width: 800, height: 500}"></button>
								<button class="am-icon-btn am-icon-link" onclick="getkey1(1)"
									title="点击查看此知识和弦图"
									data-am-modal="{target: '#links1', closeViaDimmer: true, width: 800, height: 500}"></button>
							</div>
						</div>
					</div>
				</c:if>

				<div class="am-u-sm-12">
					<hr>
					<h3>最新收录</h3>
				</div>
				<div class="am-u-sm-12">
					<c:forEach items="${page.list }" var="f">
						<article>
							<div class="am-comment-bd">
								<div class="am-g am-intro-bd">
									<div class="am-intro-left am-u-sm-2">
										<img src="user/thumbnail/${f.fileUuid}/png" alt="null"
											class="am-img-thumbnail"
											style="width: 100px; height: 100px; overflow: hidden">
									</div>
									<div class="am-intro-right am-u-sm-10">
										<h3 class="am-comment-title">
											<a class="a-black" target="_blank"
												href="user/file/${f.fileUuid}">${f.fileName }.${f.fileExt}</a>
										</h3>
										<p>简介：${f.fileBrief }</p>
										<a href="#link-to-user" class="am-comment-author">用户:
											${f.userName }</a>
										<!-- 作者 -->
										创建于
										<time>
											<fmt:formatDate value="${f.fileCreateTime }"
												pattern="yyyy-MM-dd HH:mm:ss" />
										</time>
									</div>

								</div>

							</div>
						</article>


					</c:forEach>
				</div>
				<c:if test="${fn:length(page.list) == 0}">
					<div class="am-u-sm-12">
						<div class="am-alert am-alert-secondary">
							<p>此知识节点下没有资源，欢迎录入</p>
						</div>
					</div>
				</c:if>
				<div class="am-fr">
					<ul class="am-pagination">
						<c:if test="${page.pageNum > 1}">
							<li><a onclick="gotoPage(${page.prePage })">«</a></li>
						</c:if>

						<c:forEach items="${page.navigatepageNums}" var="p">
							<c:if test="${page.pageNum==p}">
								<li class="am-active"><a onclick="gotoPage(${p})">${p}</a></li>
							</c:if>
							<c:if test="${page.pageNum!=p}">
								<li><a onclick="gotoPage(${p})">${p}</a></li>
							</c:if>
						</c:forEach>
						<c:if test="${page.pageNum < page.pages}">
							<li><a onclick="gotoPage(${page.nextPage })">»</a></li>
						</c:if>
					</ul>
				</div>

				<script type="text/javascript">
					var id=${classi.classificationId}
					function gotoPage(page) {
						window.location.href = "user/public/"+id+"/"+page;
					}
				</script>

			</div>
		</div>
	</div>

	<%@include file="../common/footer.jsp"%>
	<!-- content end -->
	<script type="text/javascript" src="resource/script/echarts.min.js"></script>
	<div class="am-modal am-modal-no-btn" tabindex="-1" id="class-links">
		<div class="am-modal-dialog">
			<div class="am-modal-hd">
				<h3 class="am-text-left">
					<a title="点击显示全部" href="javascript:nodeId=1;getLinks();">知识图谱</a>
				</h3>
				<a href="javascript: void(0)" class="am-close am-close-spin"
					data-am-modal-close>&times;</a>
			</div>
			<div class="am-modal-bd" id="class-charts"
				style="width: 800px; height: 400px;"></div>
		</div>
	</div>
    //新增知识热度文字云效果
	<div class="am-modal am-modal-no-btn" tabindex="-1" id="links">
		<div class="am-modal-dialog">
			<div class="am-modal-hd">
				<h3 class="am-text-left">
					<a >知识热度趋势</a>
				</h3>
			</div>
			<div class="am-modal-bd" id="keycharts"
				style="width: 800px; height: 400px;">
			</div>
		</div>
	</div>
	//新增知识和弦图
	<div class="am-modal am-modal-no-btn" tabindex="-1" id="links1">
		<div class="am-modal-dialog">
			<div class="am-modal-hd">
				<h3 class="am-text-left">
					<a >知识和弦</a>
				</h3>
				<button id="button1" onclick="getkey1(2)" class="am-btn am-btn-primary">上一个月</button>
				<button id="button2" onclick="getkey1(1)" class="am-btn am-btn-primary">这一个月</button>
				<button id="button3" onclick="getkey1(1)" class="am-btn am-btn-primary">最近半年</button>
			</div>
			<div class="am-modal-bd" id="charts1"
				style="width: 800px; height: 400px;">
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	var nodeId = ${classi.classificationId};
	function getLinks(){
	$.get("child-file-class-all/" + nodeId, function(res) {
		//console.log(res)
		var getData = {
			data : [],
			links : []
		};
		var s = res.data;
		getData.data.push({
			name : s[0].classificationName,
			x : 6,
			y : 0,
			value: s[0].classificationId
		});
		var re = [];
		for (var i = 0; i < s.length; i++) {
			re.push({
				parentId : s[i].parentId,
				classificationId : s[i].classificationId,
				classificationName : (s[i].classificationName)
			})
		}

		var denlen=new Array();;
		var fn = {
			getclass : function(nid, name, depth,index) {
				//console.log("relen:" + re.length+" nid:"+nid)
				if (re.length == 1) {
					return;
				}
				if(denlen[depth] == null){
					denlen[depth] = 0;
				}
				
				for (var i = 0; i < re.length; i++) {
					if (re[i].parentId == nid) {
						denlen[depth] = denlen[depth]+2;
						//console.log(denlen[depth]+' '+re[i].classificationName)
						var di = {
							name : re[i].classificationName,
							x : denlen[depth],
							y : depth * 3,
							value:re[i].classificationId
						}
						var li = {
							source : name,
							target : re[i].classificationName
						}

						getData.data.push(di);
						getData.links.push(li);
						//console.log(re[i].parentId+" "+re[i].classificationId)
						fn.getclass(re[i].classificationId, re[i].classificationName, depth+1,0)
					}

				}
			}
		}
		fn.getclass(s[0].classificationId, s[0].classificationName, 1,0)


		var option = {
			title : {
			},
			tooltip : {},
			animationDurationUpdate : 1500,
			animationEasingUpdate : 'quinticInOut',
			series : [
				{
					type : 'graph',
					layout : 'none',
					symbolSize : 60,
					roam : true,
					label : {
						normal : {
							show : true
						}
					},
					edgeSymbol : [ 'circle', 'arrow' ],
					edgeSymbolSize : [ 4, 10 ],
					edgeLabel : {
						normal : {
							textStyle : {
								fontSize : 20
							}
						}
					},
					data : getData.data,
					links : getData.links,
					lineStyle : {
						normal : {
							opacity : 0.9,
							width : 2,
							curveness : 0
						}
					}
				}
			]
		};
		var myChart = echarts.init(document.getElementById('class-charts'));
		myChart.setOption(option);
		myChart.on('click', function (params) {
		   window.location.href="user/public/"+params.value+"/1"
		});
	})
	}
</script>
<script >
	 function getkey(){
		 var text = ' 任务 队列 策略 使用 内存 处理 变量 发现 调用  任务 队列 策略 使用 内存 处理 变量 发现 调用 openoffice synchronized synchronized synchronized synchronized synchronized  java java java java kotlin kotlin 线程 线程 线程 多线程 商务智能 商务智能 商务智能 红黑树 红黑树 链表 二维数组 二维数组 可重入锁 可重入锁 ';
		// 注意：这里的代码只是对上面的句子进行分词并计算权重（重复次数）并构建词云图需要的数据，其中 arr.find 和 
		var data = text.split(/[,\. ]+/g)
		.reduce(function (arr, word) {
			var obj = arr.find(function (obj) {
				return obj.name === word;
			});
			if (obj) {
				obj.weight += 1;
			} else {
				obj = {
					name: word,
					weight: 1
				};
				arr.push(obj);
			}
			return arr;
		}, []);
		Highcharts.chart('keycharts', {
			series: [{
				type: 'wordcloud',
				data: data
			}],
			title: {
				text: '近期关键词热度'
			}
		});
  }
</script>
<script type="text/javascript">
  function getkey1(flag){
	  console.log(flag)
	  if(flag==1){
     Highcharts.chart('charts1', {
	 title: {
		text: '最近30天'
	},
	series: [{
		keys: ['from', 'to', 'weight'],
		data: [
			['线程', '商务智能', 5],
			['线程', '链表', 1],
			['线程', '队列', 1],
			['线程', '二维数组', 1],
			['数据结构', '商务智能', 1],
			['数据结构', '链表', 5],
			['数据结构', '二维数组', 1],
			['红黑树', '商务智能', 1],
			['红黑树', '链表', 1],
			['红黑树', '队列', 5],
			['红黑树', '二维数组', 1],
			['贪心算法', '商务智能', 1],
			['贪心算法', '链表', 1],
			['贪心算法', '队列', 1],
			['贪心算法', '二维数组', 5],
			['商务智能', 'Redis', 2],
			['商务智能', 'HashMap', 1],
			['商务智能', '排序', 1],
			['商务智能', 'MyAql', 3],
			['链表', 'Redis', 1],
			['链表', 'HashMap', 3],
			['链表', '线程同步', 3],
			['链表', '排序', 3],
			['链表', 'MyAql', 1],
			['队列', 'HashMap', 1],
			['队列', '排序', 3],
			['队列', 'MyAql', 1],
			['二维数组', 'Redis', 1],
			['二维数组', 'HashMap', 1],
			['二维数组', '排序', 2],
			['二维数组', 'MyAql', 7],
			['MyAql', 'China', 5],
			['MyAql', 'India', 1],
			['MyAql', 'JVM', 3],
			['Redis', 'China', 5],
			['Redis', 'India', 1],
			['Redis', 'JVM', 3],
			['HashMap', 'China', 5],
			['HashMap', 'India', 1],
			['HashMap', 'JVM', 3],
			['线程同步', 'China', 5],
			['线程同步', 'India', 1],
			['线程同步', 'JVM', 3],
			['排序', 'China', 5],
			['排序', 'India', 1],
			['排序', 'JVM', 3],
			['JVM', '线程', 1]
		],
		type: 'dependencywheel',
		name: 'Dependency wheel series',
		dataLabels: {
			color: '#333',
			textPath: {
				enabled: true,
				attributes: {
					dy: 5
				}
			},
			distance: 10
		},
		size: '95%'
	}]
});
}else{
	  Highcharts.chart('charts1', {
			 title: {
				text: '上一个月'
			},
			series: [{
				keys: ['from', 'to', 'weight'],
				data: [
					['红黑树', '队列', 8],
					['红黑树', '二维数组', 1],
					['线程', '队列', 3],
					['线程', '二维数组', 3],
					['数据结构', '商务智能', 3],
					['数据结构', '链表', 2],
					['数据结构', '二维数组', 4],
					['红黑树', '商务智能', 4],
					['红黑树', '链表', 1],
					['二维数组', 'MyAql', 7],
					['MyAql', 'China', 5],
					['MyAql', 'India', 1],
					['MyAql', 'JVM', 3],
					['线程', '商务智能', 1],
					['线程', '链表', 2],
					['HashMap', 'India', 1],
					['HashMap', 'JVM', 3],
					['线程同步', 'China', 5],
					['线程同步', 'India', 1],
					['线程同步', 'JVM', 3],
					['排序', 'China', 5],
					['排序', 'India', 1],
					['排序', 'JVM', 3],
					['Redis', 'China', 5],
					['Redis', 'India', 1],
					['Redis', 'JVM', 3],
					['HashMap', 'China', 5],
					['贪心算法', '商务智能', 5],
					['贪心算法', '链表', 1],
					['贪心算法', '队列', 5],
					['贪心算法', '二维数组', 1],
					['商务智能', 'Redis', 2],
					['商务智能', 'HashMap', 3],
					['商务智能', '排序', 3],
					['商务智能', 'MyAql', 4],
					['链表', 'Redis', 1],
					['链表', 'HashMap', 2],
					['链表', '线程同步', 5],
					['链表', '排序', 2],
					['链表', 'MyAql', 4],
					['队列', 'HashMap', 1],
					['队列', '排序', 3],
					['队列', 'MyAql', 4],
					['二维数组', 'Redis', 4],
					['二维数组', 'HashMap', 4],
					['二维数组', '排序', 2],
					['JVM', '线程', 1]
				],
				type: 'dependencywheel',
				name: 'Dependency wheel series',
				dataLabels: {
					color: '#333',
					textPath: {
						enabled: true,
						attributes: {
							dy: 5
						}
					},
					distance: 10
				},
				size: '95%'
			}]
		});  
  }
}
</script>
</html>