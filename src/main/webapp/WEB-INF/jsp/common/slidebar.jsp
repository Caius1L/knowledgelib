<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<div class="admin-sidebar am-offcanvas" id="admin-offcanvas">
	<div class="am-offcanvas-bar admin-offcanvas-bar">
		<ul class="am-list admin-sidebar-list">
			<li><a href="user/index"><span class="am-icon-home"></span>首页</a></li>
			<li class="admin-parent"><a class="am-cf"
				data-am-collapse="{target: '#collapse-nav-public'}"><span
					class="am-icon-users"></span> 公共知识库<span
					class="am-icon-angle-right am-fr am-margin-right"></span></a>
				<ul class="am-list am-collapse admin-sidebar-sub am-in"
					id="collapse-nav-public">
					<li><a href="user/public/1/1" class="am-cf"><span
							class="am-icon-sitemap"></span> 知识网络</a></li>
					<li><a href="user/mySearch/0/1"><span
							class="am-icon-search"></span> 智能检索</a></li>
				</ul></li>

			<li class="admin-parent"><a class="am-cf"
				data-am-collapse="{target: '#collapse-nav-person'}"><span
					class="am-icon-user"></span> 个人知识库<span
					class="am-icon-angle-right am-fr am-margin-right"></span></a>
				<ul class="am-list am-collapse admin-sidebar-sub am-in"
					id="collapse-nav-person">
					<li><a href="user/myfiles/1?searchNULL=1" class="am-cf"><span
							class="am-icon-database"></span> 我的资源</a></li>
					<li><a href="user/myforks/-1/1"><span
							class="am-icon-heart-o"></span> 知识收藏</a></li>
					<li><a href="user/upload"><span
							class="am-icon-cloud-upload"></span> 知识导入</a></li>
					<li><a href="user/newfile"><span
							class="am-icon-pencil-square-o"></span> 知识归纳</a></li>
				</ul></li>

			<li class="admin-parent"><a class="am-cf"
				data-am-collapse="{target: '#collapse-nav2'}"><span
					class="am-icon-info"></span> 个人信息<span
					class="am-icon-angle-right am-fr am-margin-right"></span></a>
				<ul class="am-list am-collapse admin-sidebar-sub am-in"
					id="collapse-nav2">
					<li><a href="user/userinfo"><span
							class="am-icon-user"></span> 个人资料</a></li>
					<li><a href="user/user-account"><span
							class="am-icon-lock"></span> 账户安全</a></li>
				</ul></li>
			<li>
		</ul>

		<div class="am-panel am-panel-default admin-sidebar-panel">
			<div class="am-panel-bd">
				<p>
					<span class="am-icon-bookmark"></span> 公告
				</p>
				<p>毕业设计真难 —— CAIUSLIB</p>
			</div>
		</div>

	</div>
</div>