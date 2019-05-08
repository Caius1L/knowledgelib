<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>首页</title>
<style>
.am-g a {
	
}
</style>
<%@include file="../common/head.jsp"%>
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
					<div class="am-fl am-cf">
						<strong class="am-text-primary am-text-lg">首页</strong> / <small>信息概览</small>
					</div>
				</div>

				<ul
					class="am-avg-sm-1 am-avg-md-4 am-margin am-padding am-text-center admin-content-list ">
					<li><a href="user/public/1/1" class="am-text-success"><span
							class="am-icon-btn am-icon-cloud-upload"></span><br />今日录入<br /> <span
							id="count-today">0</span></a></li>
					<li><a href="user/public/1/1" class="am-text-warning"><span
							class="am-icon-btn am-icon-file-word-o"></span><br />可用文档<br /> <span
							id="count-publicfiles">0</span></a></li>
					<li><a href="user/myfiles/1" class="am-text-danger"><span
							class="am-icon-btn am-icon-database"></span><br />我的资源<br /> <span
							id="count-userfiles">0</span></a></li>
					<li><a href="user/myforks/-1/1" class="am-text-secondary"><span
							class="am-icon-btn am-icon-heart-o"></span><br />我的收藏<br /> <span
							id="count-forkfiles">0</span></a></li>
				</ul>
				<script type="text/javascript">
					$.get("user/count-publicfiles", function(data) {
						$("#count-publicfiles").text(data.data);
					})
					$.get("user/count-forkfiles", function(data) {
						$("#count-forkfiles").text(data.data);
					})
					$.get("user/count-today", function(data) {
						$("#count-today").text(data.data);
					})
					$.get("user/count-userfiles", function(data) {
						$("#count-userfiles").text(data.data);
					})
				</script>
				<div class="am-g">
					<div class="am-u-sm-12">
						<table class="am-table am-table-bd am-table-striped">
							<thead>
								<tr>
									<th colspan="5">个性推荐</th>
								</tr>
							</thead>
							<tbody id="person-intr">
							</tbody>
						</table>
					</div>
				</div>
				<script type="text/javascript">
					$(function(){
						var url = "user/recommend";
						var args ={};
						$.post(url,args,function(data){
							console.log(data);
							var list = data.data;
							console.log(list[0]);
							var tbody = $("#person-intr");
							for(var i = 0;i<list.length;i++){
								tbody.append("<tr><td><img src='user/thumbnail/"+list[i].fileUuid+"/png' alt='null' class='am-img-thumbnail' style='width: 50px; height: 50px; overflow: hidden'></td>"+
										"<td><a target='_blank' title='"+list[i].fileName+"."+list[i].fileExt+"' href='user/file/"+list[i].fileUuid+"'>"+list[i].fileName+"."+list[i].fileExt +"</a></td>"+
										"<td style='width: 60%'>简介："+list[i].fileBrief+" </td></tr>");
							}
							
						});
					});
				</script>

				<div class="am-g">
					<div class="am-u-sm-12">
						<table class="am-table am-table-bd am-table-striped">
							<thead>
								<tr>
									<th colspan="5">近期热门</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${hot }" var="f">
									<tr>
										<td><img src="user/thumbnail/${f.fileUuid}/png"
											alt="null" class="am-img-thumbnail"
											style="width: 50px; height: 50px; overflow: hidden"></td>
										<td><a target="_blank"
											title="${f.fileName }.${f.fileExt }"
											href="user/file/${f.fileUuid}">${f.fileName }.${f.fileExt }</a></td>
										<td style="width: 60%">简介：${f.fileBrief }</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>

				<div class="am-g">
					<div class="am-u-md-6">
						<div class="am-panel am-panel-default">
							<div class="am-panel-hd am-cf"
								data-am-collapse="{target: '#collapse-panel-re'}">
								最近阅读<span class="am-icon-chevron-down am-fr"></span>
							</div>
							<div id="collapse-panel-re" class="am-in">
								<table class='am-table am-table-striped am-text-nowrap'>
									<c:forEach items="${recent }" var="f">
										<tr>
											<td><img src="user/thumbnail/${f.fileUuid}/png"
												alt="null" class="am-img-thumbnail"
												style="width: 30px; height: 30px; overflow: hidden"></td>
											<td><a target="_blank"
												title="${f.fileName }.${f.fileExt }"
												href="user/file/${f.fileUuid}">${f.hiddenedFileName }.${f.fileExt }</a></td>
											<td><fmt:formatDate value="${f.fileCreateTime }"
													pattern="yyyy-MM-dd HH:mm:ss" /></td>
										</tr>
									</c:forEach>

								</table>
							</div>
						</div>

					</div>

					<div class="am-u-md-6">
						<div class="am-panel am-panel-default">
							<div class="am-panel-hd am-cf"
								data-am-collapse="{target: '#collapse-panel-sh'}">
								最新分享<span class="am-icon-chevron-down am-fr"></span>
							</div>
							<div id="collapse-panel-sh" class="am-in">
								<table class='am-table am-table-striped am-text-nowrap'>
									<c:forEach items="${share }" var="f">
										<tr>
											<td><img src="user/thumbnail/${f.fileUuid}/png"
												alt="null" class="am-img-thumbnail"
												style="width: 30px; height: 30px; overflow: hidden"></td>
											<td><a target="_blank"
												title="${f.fileName }.${f.fileExt }"
												href="user/file/${f.fileUuid}">${f.hiddenedFileName }.${f.fileExt }</a></td>
											<td><fmt:formatDate value="${f.fileCreateTime }"
													pattern="yyyy-MM-dd HH:mm:ss" /></td>
										</tr>
									</c:forEach>

								</table>
							</div>
						</div>

					</div>
				</div>
			</div>
			<footer class="admin-content-footer">
				<hr>
				<p>© 2019 毕业设计 知识库管理系统 蓝城</p>
			</footer>
			<%@include file="../common/footer.jsp"%>

		</div>
		<!-- content end -->
	</div>

</body>
<script type="text/javascript">
</script>
</html>