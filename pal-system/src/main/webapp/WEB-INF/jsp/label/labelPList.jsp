<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/6/25
  Time: 14:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ming800" uri="http://java.ming800.com/taglib" %>

<html>
<head>
    <title></title>
    <script type="text/javascript" src="<c:url value='/resources/jquery/jquery-1.11.1.min.js'/>"></script>
</head>
<body>
<div class="am-cf am-padding">
    <div class="am-fl am-cf"><strong class="am-text-primary am-text-lg">标签列表</strong> / <small>LabelList</small></div>
</div>
<table class="am-table am-table-bordered am-table-radius am-table-striped">
    <tr>
        <td>操作</td>
        <td>序列号</td>
        <td>标签批次</td>
        <td>商品</td>
        <td>商户</td>
        <td>状态</td>
        <td>第一次查询时间</td>
        <td>最近一次查询时间</td>
        <td>查询次数</td>
    </tr>

    <c:forEach items="${requestScope.pageInfo.list}" var="label">
        <tr>
            <td>
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-xs" style="width: 100%;" >
                        <button onclick="window.location.href='<c:url value="/basic/xm.do?qm=plistLabelCheckRecord_default&conditions=label.id:${label.id}"/>'"
                                class="am-btn am-btn-default am-btn-xs">查询记录</button>
                    </div>
                </div>
            </td>
            <td><a href="<c:url value='/basic/xm.do?qm=viewLabel&id=${label.id}'/>">${label.serial}</a></td>
            <td>${label.labelBatch.setting}</td>
            <td>${label.product.name}</td>
            <td>${label.seller.name}</td>
            <td>
                <c:if test="${label.status == '2'}">
                    <font color="red">已验证</font>
                </c:if>
                <c:if test="${label.status == '1'}">
                    <font color="green">未验证</font>
                </c:if>
            </td>
            <td><fmt:formatDate value="${label.firstCheckDateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td><fmt:formatDate value="${label.lastCheckDateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td>${label.checkCount} 次</td>
        </tr>
    </c:forEach>
</table>
<div style="clear: both">
    <ming800:pcPageList bean="${requestScope.pageInfo.pageEntity}" url="/basic/xm.do">
        <ming800:pcPageParam name="qm" value="${requestScope.qm}"/>
        <ming800:pcPageParam name="conditions" value="${requestScope.conditions}"/>
    </ming800:pcPageList>
</div>

</body>
</html>