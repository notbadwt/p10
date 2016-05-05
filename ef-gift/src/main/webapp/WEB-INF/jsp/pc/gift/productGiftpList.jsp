<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ming800" uri="http://java.ming800.com/taglib" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--//End--nav2016-->
<div class="gift2016">
  <div class="screen">
    <div class="min-nav">
      <p><a href="">首页</a><i>></i><a href="" class="pitch-on">礼品选购</a></p>
    </div>
    <div class="gift-screen">
      <c:forEach items="${map1}" var="map1">
        <div class="list-group">
          <div class="head">${map1.key} :</div>
          <ul class="body">
              <%
                  if(request.getParameter("minPrice")!=null && !request.getParameter("minPrice").equals("") && request.getParameter("maxPrice")!=null && !request.getParameter("maxPrice").equals("")){

              %>
              <li class="active"><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=<%=request.getParameter("minPrice")%>&maxPrice=<%=request.getParameter("maxPrice")%>">全部</a></li>
              <c:forEach items="${map1.value}" var="productGiftTagValue">
                  <li><a href="<c:url value="/getProductGiftpList?value="/>${productGiftTagValue.value}&minPrice=<%=request.getParameter("minPrice")%>&maxPrice=<%=request.getParameter("maxPrice")%>">${productGiftTagValue.value}</a></li>
              </c:forEach>
              <%
                  }else {
              %>
              <li class="active"><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=&maxPrice=">全部</a></li>
              <c:forEach items="${map1.value}" var="productGiftTagValue">
                  <li><a href="<c:url value="/getProductGiftpList?value="/>${productGiftTagValue.value}&minPrice=&maxPrice=">${productGiftTagValue.value}</a></li>
              </c:forEach>
              <%
                  }
              %>
          </ul>
        </div>
      </c:forEach>
      <div class="list-group">
        <div class="head">商品价格 :</div>
        <ul class="body">
            <%
                if(request.getParameter("value")!=null && !request.getParameter("value").equals("")){

            %>
            <li class="active"><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=&maxPrice=">全部</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=0&maxPrice=100">百元以内</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=100&maxPrice=1000">100~1000</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=1000&maxPrice=5000">1000~5000</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=5000&maxPrice=10000">5000~1万</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=10000&maxPrice=100000">1万~10万</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/><%=request.getParameter("value")%>&minPrice=100000&maxPrice=1000000000">10万以上</a></li>
            <%
                }else {
            %>
            <li class="active"><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=&maxPrice=">全部</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=0&maxPrice=100">百元以内</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=100&maxPrice=1000">100~1000</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=1000&maxPrice=5000">1000~5000</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=5000&maxPrice=10000">5000~1万</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=10000&maxPrice=100000">1万~10万</a></li>
            <li><a href="<c:url value="/getProductGiftpList?value="/>&minPrice=100000&maxPrice=1000000000">10万以上</a></li>
            <%
                }
            %>
        </ul>
      </div>
    </div>
    <div class="gift-sort">
      <ul class="rank">
        <li>综合排序</li>
        <!--带tall是价格上，不带tall是价格下-->
        <li ><a href="javascript:void(0)"    class="tall">价格<i class="icon"></i></a></li>
        <li><a href="">销量</a></li>
        <li><a href="">新品</a></li>
        <li><a href="">人气</a></li>
      </ul>
    </div>
    <div class="gs-list">
      <div class="more">还在为“丈母娘”送礼发愁？<a href="">看看礼品攻略吧 ></a></div>
      <ul class="list">
        <c:forEach items="${productGiftTagList}" var="productGiftTag">
          <li>
            <a href="">
<%--
              <img src="http://gift-oss.efeiyi.com/${productGift.product.picture_url}" alt="">
--%>
              <img src="http://pro.efeiyi.com/${productGiftTag.productGift.productModel.product.picture_url}" alt="">
              <div class="list-content">
                <p class="name">${productGiftTag.productGift.productModel.product.name}</p>
                <p class="title">￥<em>${productGiftTag.productGift.productModel.product.price}</em></p>
              </div>
            </a>
          </li>
        </c:forEach>
      </ul>
    </div>
    <ul class="pg-number">
      <ming800:pcPageList bean="${pageEntity}" url="/getProductGiftpList">
        <ming800:pcPageParam name="value"
                             value='<%=request.getParameter("value")!=null ? request.getParameter("value") : ""%>'/>
        <ming800:pcPageParam name="minPrice"
                             value='<%=request.getParameter("minPrice")!=null ? request.getParameter("minPrice") : ""%>'/>
          <ming800:pcPageParam name="maxPrice"
                               value='<%=request.getParameter("maxPrice")!=null ? request.getParameter("maxPrice") : ""%>'/>
      </ming800:pcPageList>
    </ul>
    <ul class="gift-red">
      <li><img src="shop2016/upload/gift-pic2.jpg" alt=""><a href="">进入店铺</a></li>
      <li><img src="shop2016/upload/carf2016.jpg" alt=""><a href="">进入店铺</a></li>
      <li><img src="shop2016/upload/carf2016.jpg" alt=""><a href="">进入店铺</a></li>
      <li><img src="shop2016/upload/carf2016.jpg" alt=""><a href="">进入店铺</a></li>
    </ul>
  </div>

</div>