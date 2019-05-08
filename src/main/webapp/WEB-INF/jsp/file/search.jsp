<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>智能检索</title>
<style>
#key a{
cursor:pointer;
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

				<div class="am-cf am-padding am-padding-bottom-0">
					<div class="am-fl am-cf">
						<strong class="am-text-primary am-text-lg">智能检索</strong> / <small>搜索知识</small>
					</div>
				</div>

				<div class="am-tabs am-margin" data-am-tabs>
					<ul class="am-tabs-nav am-nav am-nav-tabs">
						<li class="am-active"><a href="#tab1">检索信息</a></li>
					</ul>
					<form method="post" id="search">
						<div class="am-tabs-bd">
							<div class="am-tab-panel am-fade am-in am-active">
								<div class="am-g am-margin-top">
									<div class="am-u-sm-4  am-text-right">关键信息</div>
									<div class="am-u-sm-8 ">
										<input type="text" name="fileName" value="${file.fileName}"
											style="width: 32%" class="am-input-lg">
									</div>

								</div>

								<div class="am-g am-margin-top">
									<div class="am-u-sm-4  am-text-right">关键词</div>
									<div class="am-u-sm-8 ">
										<input type="text" id="keyWord" name="keyWord" value="${keyWord}"
											style="width: 32%" class="am-input-lg">
									</div>

								</div>

								<div class="am-g am-margin-top">
									<div class="am-u-sm-4  am-text-right">文件类型</div>
									<div class="am-u-sm-8 ">
										<select name="fileExt" id="fileExt">
											<option value="all">所有</option>
											<option value="office">office</option>
											<option value="video">视频</option>
											<option value="img">图片</option>
											<option value="else">其他</option>
										</select>
									</div>
								</div>

								<div class="am-g am-margin-top">
									<div class="am-u-sm-4  am-text-right">文件分类</div>
									<input type="hidden" value="1" name="fileClassId"
										id="fileClassId">
									<div class="am-u-sm-8 " id="selectId">
								
									
									</div>
								</div>


								<div class="am-g am-margin-top">
									<div class="am-u-sm-4  am-text-right">时间范围</div>


									<div class="am-u-sm-2">

										<div class="am-form-group am-form-icon">
											<i class="am-icon-calendar"></i> <input name="fileCreateTime"
												id="fileCreateTime" type="date" style="width:110%"
												class="am-form-field am-input-sm" placeholder="日期"
												value="<fmt:formatDate value="${file.fileCreateTime }"
													pattern="yyyy-MM-dd" />">



										</div>
									</div>
									<div class="am-u-sm-2">
										<div class="am-form-group am-form-icon ">
											<i class="am-icon-calendar"></i> <input name="endTime"
												id="endTime" type="date" style="width:110%"
												value="<fmt:formatDate value="${endTime}"  
													pattern="yyyy-MM-dd" />"
												class="am-form-field am-input-sm" placeholder="日期">

										</div>
									</div>

									<div class="am-u-sm-4 "></div>
								</div>

								<div class="am-g am-margin-top">
									<div class="am-u-sm-4 am-text-right">&nbsp</div>

									<div class="am-u-sm-2">
										<button type="button" id="search1" style="margin-top: -30px"
											class="am-btn am-btn-primary ">智能搜索</button>
									</div>
									<div class="am-u-sm-2">
										<button type="button" id="search2" style="margin-top: -30px"
											class="am-btn am-btn-primary ">结果搜索</button>
									</div>
									<div class="am-u-sm-4 "></div>
								</div>
							</div>
					</form>
				</div>
				<c:if test="${num!=null}">
				<div><span style="color:#999;font-size:14px;">&nbsp&nbsp&nbsp相关结果约${num}个</span>    <span style="color:#999;font-size:14px;">时间约${time}毫秒</span></div>
				</c:if>
				<div class="am-u-sm-12">
					<c:forEach items="${page.data}" var="f">
						<article>
							<div class="am-comment-bd">
								<div class="am-g am-intro-bd">
									<div class="am-intro-left am-u-sm-2">
										<img src="user/thumbnail/${f.fileUuid}/png" alt="null"
											class="am-img-thumbnail"
											style="width: 150px; height: 150px; overflow: hidden">
									</div>
									<div class="am-intro-right am-u-sm-10">
										<h3 class="am-comment-title">
											<a class="a-black" target="_blank"
												href="user/file/${f.fileUuid}">${f.fileName }.${f.fileExt}</a>
										</h3>
										<p id="key" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
											<span style="font-weight: 700;color: #999;">关键词:&nbsp</span>
											<c:forEach items="${f.fileKeyWords}" var="key">
											 <a  type="button">${key}</a>
											</c:forEach>
										</p>
										<p
											style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"><span style="font-weight: 700;color:#999;">内容:&nbsp</span>${f.fileText}</p>
										<a href="#link-to-user" class="am-comment-author">用户:
											${f.userName}</a>
										<!-- 作者 -->
										创建于
										<time id="time">
											<fmt:formatDate value="${f.fileCreateTime }"
												pattern="yyyy-MM-dd" />
										</time>
									</div>

								</div>

							</div>
						</article>


					</c:forEach>
				</div>

				<div class="am-fr">
					<ul class="am-pagination">
						<c:if test="${page.pageNum > 1}">
							<li><a onclick="gotoPage(${page.prePage})">«</a></li>
						</c:if>

						<c:forEach items="${page.navigatepageNums}" var="p">
							<c:if test="${page.pageNum==p}">
								<li class="am-active"><a onclick="gotoPage(${p})">${p}</a></li>
							</c:if>
							<c:if test="${page.pageNum!=p}">
								<li><a onclick="gotoPage(${p})">${p}</a></li>
							</c:if>
						</c:forEach>

						<c:if test="${page.pageNum < page.totalPage}">
							<li><a onclick="gotoPage(${page.nextPage})">»</a></li>
						</c:if>
					</ul>
				</div>
			</div>
		</div>
	</div>
	</div>
	</div>

	<%@include file="../common/footer.jsp"%>

</body>
<script type="text/javascript">
$(window).load(function() {
	$("#selectId").find("select").remove();
	echo();
});
$("#search1").click(function(){
	
	$("#search").attr("action","user/search/0/1")
	$("#search").submit();
	
	
})
$("#search2").click(function(){
	
	$("#search").attr("action","user/search/1/1")
	$("#search").submit();
	
})
function getChild(object)
{
	$(object).nextAll().remove();
	var classId=$(object).find("option:selected").val();
	$("#fileClassId").val(classId);
	if($(object).find("option:selected").html()=="所有分类")
	{
		return ;
	}
	$.ajax({
		url : "user/getOneClass",
		type : "POST",
		data:{classId:classId},
		datatype : "json",
		//ansyn : false,
		success : function(JsonResult) {
			
			var len=JsonResult.data.length;
			if(len==0)
				return ;
			var classification=JsonResult.data;
			var innerStr="<option value="+classId+">"+"所有分类" + "</option>";
			for(var i=0;i<len;i++)
			{	
					innerStr += "<option value='" + classification[i].classificationId+"'>"
					+ classification[i].classificationName + "</option>"
			}
			var str = "<select style='margin-left:10px;'  onchange='getChild(this)'>"

			+ innerStr

			+ "</select>";
			$("#selectId").append(str);
		}
	}) 
}
function echoClass()
{	
	$("selctedId").find("select").remove();
	var classId="${classIds}";
	if(classId=="")
	{
		classId+="1";
		
	}
	var classIds=classId.split(".");
	$("#fileClassId").val(classIds[classIds.length-1]);
	$.ajax({
		url : "user/getAllClass",
		type : "POST",
		data:{classIds:classId},
		datatype : "json",
		ansyn : true,
		success : function(JsonResult) {
			
			for(var j=0;j<JsonResult.data.length;j++)
			{	
				var list=new Array(100);
				list=JsonResult.data[j];
				if(list.length==0)
					return ;
				var innerStr="<option value="+classIds[j]+" >"+"所有分类" + "</option>";
				for(var i in list){
					 if(classIds[j+1]==list[i].classificationId)
					 {
							innerStr += "<option selected='selected' value='" + list[i].classificationId+"'>"
							+ list[i].classificationName + "</option>"
					
					 }else{
						   innerStr += "<option value='" + list[i].classificationId+"'>"
							+ list[i].classificationName + "</option>"
					 } 
				}
				
				var str = "<select style='margin-right:10px;'  onchange='getChild(this)'>"+ innerStr+ "</select>";
				$("#selectId").append(str);
			}
			
		}
	}) 
}
//回显
function echo()
{   
	
	var fileExt="${file.fileExt}";
	for(var i=0;i<$("#fileExt").find("option").length;i++)
	{	
		$("#fileExt").find("option").eq(i).removeAttr("selected");
		if($("#fileExt").find("option").eq(i).val()==fileExt)
		{  
			$("#fileExt").find("option").eq(i).attr("selected",true);
		}
	}
	echoClass();
	
}
function gotoPage(page) {
	var url=window.location.pathname;
	var str=url.split("/");
	$("#search").attr("action","user/search/"+str[4]+"/"+page)
	$("#search").submit();
}
$("#key a").click(function (){
	$this = $(this);
	$("#keyWord").val();
	$("#keyWord").val($this.text());
	gotoPage(1);
})
</script>

</html>