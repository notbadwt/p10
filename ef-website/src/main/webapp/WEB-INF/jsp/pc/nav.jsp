<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/8/17
  Time: 10:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="topbar wh">
    <div class="hd">
        <ul class="ul-item">
            <li><a href="" title="商家入驻">商家入驻</a></li>
            <li><a href="" title="手机e飞蚁">手机e飞蚁</a></li>
            <li><a href="" title="请登录">请登录</a></li>
            <li><a href="" title="快速注册">快速注册</a></li>
            <li class="cart">
                <a href="" title="购物车"><i class="icon"></i>购物车</a>
                <span class="tips"><em>0</em></span>
            </li>
        </ul>
    </div>
</div>
<!-- //End--topbar-->
<div class="header wh">
    <div class="hd">
        <div class="logo"><a class="icon" href="" target="_blank" title="e飞蚁-爱非遗"></a></div>
        <div class="nav">
            <ul>
                <c:forEach items="${jnode.children}" var="child">
                    <li><a class="${child.jnodeMatch("cur",currentJnode)}" href="<c:url value="${child.url}"/>" title="${child.text_zh_CN}">${child.text_zh_CN}</a></li>
                </c:forEach>
                <%--<li><a href="" title="商品分类">商品分类</a></li>--%>
                <%--<li><a href="" title="传承人">传承人</a></li>--%>
                <%--<li><a href="" title="展览">展览</a></li>--%>
                <%--<li><a href="" title="资讯">资讯</a></li>--%>
            </ul>
        </div>
    </div>
</div>
