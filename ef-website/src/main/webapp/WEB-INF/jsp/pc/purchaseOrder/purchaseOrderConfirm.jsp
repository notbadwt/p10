<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/8/20
  Time: 16:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link href="<c:url value="/scripts/css/myorder.css"/>" type="text/css " rel="stylesheet">
    <style>
        .my-clearing .m-form li .dj-btn {
            padding: 7px 33px;
        }

        .my-clearing .m-form {
            padding: 15px 37px 32px;
        }
    </style>
</head>
<body>
<%--<script src="/scripts/js/jquery-1.4.2.min.js"></script>--%>
<script src="http://malsup.github.io/jquery.form.js"></script>
<div class="wr wh">
    <!--结算-->
    <div class="my-clearing">
        <h1>填写并核对订单信息</h1>
        <!--地址-->
        <div class="clearing-site">
            <span class="clearing-left">收货人信息</span>
        <span class="clearing-right">
            <a href="#" class="btn-cart-add">新增收货地址</a>
            <div class="active-pop" style="display: none">
                <div class="pop-up">
                    <div class="pop-h">编辑收货人信息
                        <i class="clase" title="关闭"></i>
                    </div>
                    <div class="m-form">
                        <form id="newAddress">
                            <ul>
                                <li>
                                    <label>收货人：</label>
                                    <input type="text" name="consignee" required>
                                </li>
                                <li>
                                    <label>所在地区：</label>

                                    <form>
                                        <select class="cars" id="province" name="province.id"
                                                onchange="provinceChange(this)" required>
                                            <option value="请选择">请选择所在省</option>
                                        </select>
                                        <select class="car1" id="city" name="city.id" onchange="cityChange(this)"
                                                required>
                                            <option value="请选择">请选择所在市</option>
                                        </select>
                                        <select class="car1" id="district" name="district.id" required>
                                            <option value="请选择">请选择所在区县</option>
                                        </select>
                                    </form>
                                </li>
                                <li>
                                    <label>详细地址：</label>
                                    <input style="width: 255px" type="text" name="details" required>
                                </li>
                                <li>
                                    <label>手机号码：</label>
                                    <input type="text" name="phone" required>
                                </li>
                                <li>
                                    <label></label>
                                    <a class="dj-btn" onclick="submitNewAddress()" value="保存收货人信息"
                                       href="javascript:void(0)">保存收货人信息</a>
                                    <input type="reset" style="display: none" id="reset">
                                    <span id="ts" style="border: 0"></span>
                                </li>
                            </ul>
                        </form>
                    </div>
                    <div class="sh-bg"></div>
                </div>
            </div>
        </span>
        </div>
        <div class="page-clearing" id="address">
            <div class="page-box-m">
                <c:forEach items="${addressList}" var="address">

                    <div class="page-default" style="position:relative">
                    <span>
                        <c:if test="${address.status=='2'}">
                        <div id="${address.id}" class="default-text triangle activeFlag" name="addressItem"
                             onclick="chooseAddress(this,'${address.id}')">
                            </c:if>
                            <c:if test="${address.status=='1'}">
                            <div class="default-text" name="addressItem" onclick="chooseAddress(this,'${address.id}')">
                                </c:if>
                                <strong>${address.consignee} ${address.province.name}</strong>
                                    <%--<i class="triangle" style="display: block"></i>--%>
                                </a>
                            </div>
                    </span>
                        <span>${address.consignee}</span>
                        <span>${address.province.name}</span>
                        <span>${address.city.name}</span>
                        <span>${address.district.name}</span>
                        <span>${address.details}</span>
                        <span>${address.phone}</span>

                        <div class="jc-hc" style="display: none;height: 40px;">
                            <a href="#" onclick="removeAddress('${address.id}');">删除</a>
                            <a href="#" class="edit-act" onclick="openAddress('${address.id}','${address.consignee}','${address.province.name}','${address.province.id}','${address.city.name}','${address.city.id}','${address.district.name}','${address.district.id}','${address.details}','${address.phone}')">编辑</a>
                            <a href="#" onclick="defaultAddress('${address.id}');">设为默认地址</a>
                        </div>
                    </div>
                </c:forEach>
                <div class="apt" style="display: none">
                    <div class="pop-up">
                        <div class="pop-h">编辑收货人信息
                            <i class="clase" title="关闭"></i>
                        </div>
                        <div class="m-form">
                            <form class="aaa" id="updateAddress"
                                  action="<c:url value="/myEfeiyi/addAddress.do"/>"
                                  method="post">
                                <ul>
                                    <li>
                                        <label>收货人：</label>
                                        <input type="text" name="consignee" id="consignee"
                                               value="${address.consignee}" required>
                                    </li>
                                    <input type="hidden" name="id" id="addressId" value="${address.id}">
                                    <li>
                                        <label>所在地区：</label>

                                        <form>
                                            <select id="provinceVal" name="province.id"
                                                    class="cars" onclick="provinceChange(this)"
                                                    required>
                                                <option value="${address.province.id}" id="provinceOption">${address.province.name}</option>
                                            </select>
                                            <select id="cityVal" name="city.id" class="car1"
                                                    onclick="cityChange(this)" required>
                                                <option value="${address.city.id}" id="cityOption">${address.city.name}</option>
                                            </select>
                                            <select id="districtVal" name="district.id"
                                                    class="car1" required>
                                                <option value="${address.district.id}" id="districtOption">${address.district.name}</option>
                                            </select>
                                        </form>
                                    </li>
                                    <li>
                                        <label>详细地址：</label>
                                        <input style="width: 255px;" type="text" name="details"
                                               id="details" value="${address.details}"
                                               required>
                                    </li>
                                    <li>
                                        <label>手机号码：</label>
                                        <input type="text" name="phone" id="phone"
                                               value="${address.phone}" required>
                                    </li>
                                    <li>
                                        <label></label>
                                        <input type="button" onclick="submitForm('${address.id}');"
                                               class="dj-btn" value="保存收货人信息">
                                        <span id="ts_update" style="border: 0"></span>
                                    </li>
                                </ul>
                            </form>
                        </div>
                        <div class="sh-bg"></div>
                    </div>
                </div>
            </div>
            <div class="dj-p">
                <a href="#" id="pg-box">展开地址
                    <span class="triangle-bg"></span>
                </a>
            </div>
            <%-- <div class="dj-p">
             <a href="#"  style="display: block">展开地址
             <span class="triangle-bg2"></span>
             </a>
             </div>--%>
        </div>


        <!--支付-->

        <div class="clearing-site divtop">
            <span class="clearing-left">支付方式</span>
        </div>
        <div class="page-clearing ">
            <li id="zhifubao" class="alipay triangle " onclick="zhifubao(this)">
                <span>支 付 宝</span>
            </li>
            <li id="weixin" class="alipay " onclick="weixin(this)">
                <span>微   信</span>
            </li>
        </div>
        <!--支付-->
        <!--订货清单-->
        <div class="clearing-site divtop">
            <span class="clearing-left">订货清单</span>
        <span class="clearing-right">
            <c:if test="${!isEasyBuy}">
                <a href="<c:url value="/cart/view"/>"
                   onclick="window.location.href='<c:url value="/cart/view"/>'">返回修改购物车</a>
            </c:if>
        </span>
        </div>
        <c:forEach items="${tenantList}" var="tenant">

            <div class="page-inventory">
                <div class="page-store">
                        ${tenant.name}
                        <%--<a href="#">收起店铺--%>
                        <%--<span class="triangle-bg"></span>--%>
                        <%--</a>--%>
                </div>
                <div class="page-Commodity">
                    <table>
                        <c:forEach items="${productMap.get(tenant.id)}" var="product">
                            <tr>
                                <td width="542">
                                    <div class="cols1 page-pdl">
                                        <a href="/product/productModel/${product.productModel.id}" target="_blank">
                                            <img src="http://pro.efeiyi.com/${product.productModel.productModel_url}@!product-icon"
                                                 alt=""/>
                                        </a>

                                        <div class="info">
                                            <p><a href="/product/productModel/${product.productModel.id}"
                                                  target="_blank">${product.productModel.product.project.name}</a>
                                            </p>

                                            <p><a href="/product/productModel/${product.productModel.id}"
                                                  target="_blank">${product.productModel.product.name}
                                                <c:if test="${product.productModel.product.productModelList.size()>1}">
                                                    [${product.productModel.name}]
                                                </c:if>
                                                    <%--<c:if test="${product.productModel.productPropertyValueList.size()>1}">--%>
                                                    <%--[--%>
                                                    <%--<c:forEach--%>
                                                    <%--items="${product.productModel.productPropertyValueList}"--%>
                                                    <%--var="ppv">${ppv.projectPropertyValue.value}</c:forEach>]--%>
                                                    <%--</c:if>--%>
                                            </a>
                                            </p>
                                        </div>
                                    </div>
                                </td>
                                <td width="171"><span
                                        class="moneycl">￥${product.purchasePrice}</span>
                                </td>
                                <td width="137">
                                    <span>x${product.purchaseAmount}</span>
                                </td>
                                <td width="102"><span>有货</span></td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <div class="page-leaveword">
                    <label>给店家留言</label>
                    <input id="${tenant.id}Message" name="message" type="text" placeholder="(如需开具发票，请在此输入开票信息)"
                           maxlength="45" oninput="updateCount(this)" style="color: black">
                    <span id="${tenant.id}Count">0/45</span>
                </div>
            </div>
        </c:forEach>
        <!--订货清单-->
        <!--发票-->
        <div class="invoice divtop">
            <%--<div class="title"><input type="checkbox" class="middle-active" value="">开具发票</div>--%>
            <div class="con-info">
                <div class="tbar">
                    <%--<input type="checkbox" class="middle-active" value="">--%>
                    <span class="name">使用优惠券</span>
                    <%--<span class="txt">已减</span>--%>
                    <%--<span class="price">￥20</span>--%>
                </div>
                <ul class="list ul-list" id="couponList">
                    <%--{{ for(var i = 0 ; i<it.length ; i++){ }}--%>
                    <%--<li>--%>
                    <%--<input type="checkbox" name="coupon">--%>
                    <%--<span class="t1">满{{=it.couponBatch.priceLimit}}元立减{{=it.couponBatch.price}}元</span>--%>
                    <%--<span class="t2">{{=it.rangeLabel}}</span>--%>
                    <%--<span class="t3">{{=it.startDate}}至{{=it.endDate}}</span>--%>
                    <%--</li>--%>
                    <%--{{ } }}--%>
                </ul>
                <div class="list div-list" id="couponExchange">
                    <span>使用兑换码</span>
                    <input class="ipt-txt" type="text" value="" id="cdkey">
                    <a class="btn-submit" value="确定使用" onclick="exchangeCoupon()">确定使用</a>
                    <span class="red" id="exchangeError" style="display: none">订单不满足该优惠券使用条件</span>
                </div>
                <ul class="list ul-list ul-list-last" id="exchangeCouponList">
                    <%--<li>--%>
                    <%--<input type="checkbox" name="coupon">--%>
                    <%--<span class="t1">满{{=it.couponBatch.priceLimit}}元立减{{=it.couponBatch.price}}元</span>--%>
                    <%--<span class="t2">{{=it.rangeLabel}}</span>--%>
                    <%--<span class="t3">{{=it.startDate}}至{{=it.endDate}}</span>--%>
                    <%--</li>--%>
                </ul>
            </div>
        </div>
        <!--发票-->
        <!--余额支付-->
        <c:if test="${null != consumer.balance && 0<consumer.balance}">
            <div class="balance">
                <form>
                    <div class="balance-titie">余额支付</div>
                    <div class="balance-text"><input type="checkbox" onclick="useBalance(this)" id="banlanceCheckbox">使用余额支付
                        <span id="usefulBalance"><c:if
                                test="${consumer.balance>purchaseOrder.total}">${purchaseOrder.total}</c:if><c:if
                                test="${consumer.balance<=purchaseOrder.total}">${consumer.balance}</c:if></span>元
                    </div>
                </form>
            </div>
        </c:if>
        <!--结算-->
        <div class="System">
            <div class="System-text">
                <span class="btns"><a target="_blank" onclick="submitOrder(this,'${purchaseOrder.id}')">提交订单</a></span>
            <span class="price-info">
                <%--js 取回来  第一次也是js取 统一js取--%>
                <p class="price1">总金额：<em id="totalPrice">${purchaseOrder.total}</em> 元</p>
                <p class="price2">优惠：-<em id="couponPrice">0.00</em>元</p>
                <p class="price2">余额：-<em id="balance">0.00</em>元</p>
                <p class="price3">应付金额：<strong id="finalPrice">${purchaseOrder.total}</strong> 元</p>
            </span>
            </div>
        </div>
    </div>
</div>
<script>
    //GA统计用的商品map
    var productMap = {};
    <c:forEach items="${purchaseOrder.purchaseOrderProductList}" var="purchaseOrderProduct">
    productMap['${purchaseOrderProduct.productModel.id}'] = ${purchaseOrderProduct.purchaseAmount};
    </c:forEach>

    var payment = "1";
    var consumerAddress = "";
    if (typeof $(".activeFlag").attr("id") != "undefined") {
        consumerAddress = $(".activeFlag").attr("id");
    }

    function zhifubao(element) {
        $(element).attr("class", "alipay triangle");
        $("#weixin").attr("class", "alipay");
        payment = "1";
    }

    function updateCount(element) {
        var Id = $(element).attr("id");
        var tenantId = Id.substring(0, Id.length - 7);
        var str = $(element).val();
        var count = str.length;
        $("#" + tenantId + "Count").html(count + "/45");
    }

    function weixin(element) {
        $(element).attr("class", "alipay triangle");
        $("#zhifubao").attr("class", "alipay");
        payment = "3";
    }
    function submitOrder(element, orderId) {
        var messageObject = new Object();
        var balance = $("#balance").text();
        var finalPrice = $("#finalPrice").text();
        $("input[name=message]").each(function () {
            messageObject[$(this).attr("id")] = $(this).val();
        })
        var message1 = "";
        for (var key in messageObject) {
            message1 += key + ":" + messageObject[key] + ";"
        }
        var message = encodeURIComponent(message1);
        $.ajax({
            type: 'post',
            async: false,
            url: '<c:url value="/order/checkInventory/${purchaseOrder.id}"/>',
            dataType: 'json',
            success: function (data) {
                if (data) {
                    if (consumerAddress == "") {
                        showAlert("提示", "请选择一个收货地址！");
                    } else {
                        $.ajax({
                            type: 'post',
                            async: false,
                            url: '<c:url value="/order/checkBalance"/>' + '?balance=' + balance,
                            dataType: 'json',
                            success: function (data1) {
                                if (data1) {
                                    var url = "<c:url value="/order/confirm/"/>";
                                    url += orderId + "?payment=" + payment + "&address=" + consumerAddress + "&message=" + message + "&balance=" + balance;
                                    element.onclick = null;
                                    $(element).attr("href", url);
                                    $(element).click();

                                    //逐条发送商品下单记录给GA
                                    $.each(productMap, function (key, value) {
                                        ga('ec:setAction', 'purchase', {
                                            'id': key,
                                            'affiliation': 'efeiyi',
                                            'revenue': ${purchaseOrder.total},
                                            'step': 1,
                                            'option': payment,
//                            'tax': '2.85',
//                            'shipping': '5.34',
//                            'coupon': 'SUMMER2013'    // User added a coupon at checkout.
                                        });
                                    });
                                    ga('send', 'pageview');

                                    if (finalPrice != 0) {
                                        showChooseConfirm("提示", "是否支付成功？", function () {
                                            window.location.href = "http://i.efeiyi.com/order/myEfeiyi/view/" + orderId;
                                        }, function () {
                                            window.location.href = "http://i.efeiyi.com/order/myEfeiyi/view/" + orderId;
                                        })
                                    }
                                } else {
                                    showAlert("提示", "抱歉，余额不足！")
                                }
                            }
                        });

                    }
                } else {
                    showAlert("提示", "抱歉，该商品已售罄！")
                }
            },

        });
    }


    $(function () {
        $('.clearing-site span a').click(function () {
            $(".header-new,.topbar,.footernew").css("z-index", "-1")
            $(this).siblings('.active-pop').show();
            $('.clase, .sh-bg').click(function () {
                $(this).parents('.active-pop').hide();
                $(".header-new,.topbar,.footernew").css("z-index", "")
            })
            return false;
        })

        //网页加载的时候把城市的数据取回来
        ajaxRequest("<c:url value="/myEfeiyi/address/listProvince.do"/>", {}, function (data) {
            var out = '<option value="">请选择所在省市</option>';
            var out1 = "";
            for (var i = 0; i < data.length; i++) {
                out += '<option value="' + data[i]["id"] + '">' + data[i]["name"] + '</option>';
                out1 += '<option value="' + data[i]["id"] + '">' + data[i]["name"] + '</option>';
            }
            $("#province").html(out);
            $("#provinceVal").html(out1);
        }, function () {
        }, "post")

    })

    function provinceChange(element) {
        var provinceId = $(element).val();
        ajaxRequest("<c:url value="/myEfeiyi/address/listCity.do"/>", {provinceId: provinceId}, function (data) {
            var out = '<option value="">请选择所在市</option>';
            for (var i = 0; i < data.length; i++) {
                out += '<option value="' + data[i]["id"] + '">' + data[i]["name"] + '</option>';
            }
            $("#city").html(out);
            $("#cityVal").html(out);
        }, function () {
        }, "post")
    }

    function cityChange(element) {
        var cityId = $(element).val();
        ajaxRequest("<c:url value="/myEfeiyi/address/listDistrict.do"/>", {cityId: cityId}, function (data) {
            var out = '<option value="">请选择所在区县</option>';
            for (var i = 0; i < data.length; i++) {
                out += '<option value="' + data[i]["id"] + '">' + data[i]["name"] + '</option>';
            }
            $("#district").html(out);
            $("#districtVal").html(out);
        }, function () {
        }, "post")
    }

    function newAddress(it) {
        var out = ' <div class="page-default"> <span> <div id="' + (it.id) + '" class="default-text" name="addressItem" onclick="chooseAddress(this,\'' + it.id + '\')"> <strong>' + (it.consignee) + ' ' + (it.province.name) + '</strong> </a> </div> </span> <span>' + (it.consignee) + '</span> <span>' + (it.province.name) + '</span> <span>' + (it.city.name) + '</span> <span>' + (it.district.name) + '</span> <span>' + (it.details) + '</span> <span>' + (it.phone) + '</span> </div>';
        return out;
    }

    function submitNewAddress() {
        var consignee = $(":input[name='consignee']").val();
        var phone = $(":input[name='phone']").val();
        var details = $(":input[name='details']").val();
        if (consignee == "" || phone == "" || details == "") {
            $("#ts").text("请完善您的新增地址");
        } else {
            var param = $("#newAddress").serialize();

            var success = function (data) {
//                window.location.href=window.location.href;
                var html = newAddress(data);
                $("#address").append(html);
                $(".active-pop").hide();
                $("#reset").click();
                $("#" + data.id).click();
            }
            ajaxRequest("<c:url value="/order/addAddress.do"/>", param, success, function () {
            }, "post")
        }

    }

    function submitForm() {
        var consignee = $("#consignee").val();
        var provinceVal = $("#provinceVal").val();
        var cityVal = $("#cityVal").val();
        var districtVal = $("#districtVal").val();
        var details = $("#details").val();
        var phone = $("#phone").val();
        if (consignee != "" && (typeof (consignee) != 'undefined') && provinceVal != "" && cityVal != "" && districtVal != "" && details != "" && typeof (details) != 'undefined' && phone != "" && (typeof (phone) != 'undefined')) {
            $.ajax({
                cache: true,
                type: "POST",
                url: '<c:url value="/order/updateAddress.do"/>',
                data: $('#updateAddress').serialize(),
                success: function (data) {
                    if (data) {
                        window.location.reload();
                    }
                },
            })
        } else {
            $("#ts_update").text("请完善信息");
        }
    }

    function chooseAddress(element, addressId) {
        consumerAddress = addressId
        $("div[name=addressItem]").each(function () {
            $(this).attr("class", "default-text");
        })
        $(element).attr("class", "default-text triangle")
    }

    function couponListHtml(it /**/) {
        var out = ' ';
        for (var i = 0; i < it.length; i++) {
            if (it[i]["couponBatch"]["type"] == "1") {
                out += ' <li> <input type="checkbox" onclick="chooseUsefulCoupon(this)" name="coupon" id="' + it[i].id + '"> <span class="t1">满' + (it[i]["couponBatch"]["priceLimit"]) + '元立减' + (it[i].couponBatch.price) + '元</span>  <span class="t3">' + ((it[i]["startTimeL"])) + '至' + ((it[i]["endTimeL"])) + '</span> </li> ';
            } else {
                out += ' <li> <input type="checkbox" onclick="chooseUsefulCoupon(this)" name="coupon" id="' + it[i].id + '"> <span class="t1">减' + (it[i].couponBatch.price) + '元</span>  <span class="t3">' + ((it[i]["startTimeL"])) + '至' + ((it[i]["endTimeL"])) + '</span> </li> ';
            }
        }
        return out;
    }

    function getLocalTime(nS) {
        return new Date(parseInt(nS) * 1000).toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
    }

    function exchangeCoupon() {
        var success = function (data) {
            if (data == "null") {
                $("#exchangeError").show();
            } else {
                $("#exchangeError").hide();
                var html = couponListHtml(data);
                $("#exchangeCouponList").html(html);
                setTimeout(function () {
                    $("input[name=coupon]").each(function () {
                        $(this).click(function () {
                            var id = $(this).attr("id");
                            $("input[name=coupon]").each(function () {
                                if ($(this).attr("id") != id) {
                                    $(this).removeAttr("checked")
                                }
                            });
                            $(this).attr("checked", "checked");
                        });
                    });
                }, 1000);

                //判断优惠券是否可用
                if (data[0]["isUseful"] == 1) {
                    $("#exchangeCouponList").attr("class", "list ul-list");
                } else {
                    $("#exchangeCouponList").attr("class", "list ul-list ul-list-last");
                    $("#" + data[0]["id"]).remove();
                }
            }
        }
        var param = {"cdkey": $("#cdkey").val(), "purchaseOrderId": "${purchaseOrder.id}"}
        //初始化优惠券列表
        ajaxRequest("<c:url value="/exchangeCoupon.do"/>", param, success, function () {
        }, "post");


    }

    function chooseUsefulCoupon(element) {
        var status = "1";
        console.log($(element).is(':checked'));
        if ($(element).is(':checked') == true) {
            status = "1";
        } else {
            status = "2";
        }
        //选中优惠卷以后会发送一个请求就是把优惠券绑定到订单当中，绑定完成之后再发送一个请求来更新价格返回的是一个拼装好的json字符串
        var param = {"purchaseOrderId": "${purchaseOrder.id}", "couponId": $(element).attr("id"), "status": status};
        var success = function (data) {
            if (data) {
                var param = {"purchaseOrderId": "${purchaseOrder.id}"};
                var success = function (data) {
                    //输出新的订单价格
                    data = JSON.parse(data);
                    console.log(data);
                    console.log(data["totalPrice"]);
                    console.log(data["couponPrice"]);
                    console.log(data["finalPrice"]);
                    $("#totalPrice").html(data["totalPrice"]);
                    $("#couponPrice").html(data["couponPrice"]);
                    $("#finalPrice").html(data["finalPrice"]);
                    var finalPrice = parseFloat(data["finalPrice"]);
                    if (finalPrice < parseFloat(${consumer.balance})) {
                        $("#usefulBalance").html(finalPrice.toFixed(2));
                    }
                    $("#balance").html("0.00");
                    $("#banlanceCheckbox").attr("checked", false);
                }
                ajaxRequest("<c:url value="/order/getPurchaseOrderPrice.do"/>", param, success, function () {
                }, "post");
            }
        }
        ajaxRequest("<c:url value="/useCoupon.do"/>", param, success, function () {
        }, "post");
    }


    $().ready(function () {
        var success = function (data) {
            var html = couponListHtml(data);
            $("#couponList").html(html);
            setTimeout(function () {
                $("input[name=coupon]").each(function () {
                    $(this).click(function () {
                        var id = $(this).attr("id");
                        $("input[name=coupon]").each(function () {
                            if ($(this).attr("id") != id) {
                                $(this).removeAttr("checked")
                            }
                        });
                        $(this).attr("checked", "checked");
                    });
                });
            }, 1000);

        }
        var param = {"price": "${purchaseOrder.total}", "purchaseOrderId": "${purchaseOrder.id}"}
        //初始化优惠券列表
        ajaxRequest("<c:url value="/listUserCoupon.do"/>", param, success, function () {
        }, "post");


    });

    function df(addressId, consumerId) {
        $.ajax({
            type: 'post',
            async: false,
            url: '<c:url value="/myEfeiyi/defaultAddress.do"/>',
            dataType: 'json',
            data: {
                status: 2,
                id: addressId,
                consumerId: consumerId,

            },
            success: function (data) {
                if (data == true) {
                    window.location.reload();
                }
            },

        });
    }

    //使用余额
    function useBalance(element) {
        var totalPrice = $("#totalPrice").text();
        var couponPrice = $("#couponPrice").text();
        var balance = $("#usefulBalance").text();
        if ($(element).is(':checked') == true) {
            $("#balance").html(balance);
            $("#finalPrice").html((totalPrice - balance - couponPrice).toFixed(2));
        } else if ($(element).is(':checked') == false) {
            $("#balance").html("0.00");
            $("#finalPrice").html((totalPrice - couponPrice).toFixed(2));
        }
    }

    function province(obj, addressId) {
        var v = $(obj).val();

        //alert(provinceId);
        $.ajax({
            type: 'post',
            async: false,
            url: '<c:url value="/myEfeiyi/address/listProvince.do"/>',
            dataType: 'json',
            success: function (data) {
                $("#provinceVal" + addressId).empty();
                var obj = eval(data);
                var rowHtml = "";
                rowHtml += "<option value='请选择'>请选择所在省市</option>";
                for (var i = 0; i < obj.length; i++) {
                    rowHtml += "<option value='" + obj[i].id + "'>" + obj[i].name + "</option>";

                }
                $("#provinceVal" + addressId).append(rowHtml);
                $("#provinceVal" + addressId + " option[value='" + v + "']").attr("selected", "selected");
                city(v, addressId);
            },

        });
    }

    function district(obj, addressId) {

        var cityId = $("#cityVal" + addressId).val();
        var v = $(obj).val();
        $("#districtVal" + addressId).empty();

        $.ajax({
            type: 'post',
            async: 'false',
            url: '<c:url value="/myEfeiyi/address/listDistrict.do"/>',
            dataType: 'json',
            data: {
                cityId: cityId
            },
            success: function (data) {
                var obj = eval(data);
                var rowHtml = "";
                rowHtml += "<option value='请选择'>请选择所在地区</option>"
                for (var i = 0; i < obj.length; i++) {
                    rowHtml += "<option value='" + obj[i].id + "'>" + obj[i].name + "</option>";
                }
                $("#districtVal" + addressId).append(rowHtml);
                $("#distrtictVal" + addressId + " option[value='" + v + "']").attr("selected", "selected");
            }

        })

    }
    function city(obj, addressId) {
        var pid = $("#provinceVal" + addressId).val();
        var v = $(obj).val();
        $("#cityVal" + addressId).empty();
        $.ajax({
            type: 'post',
            async: false,
            url: '<c:url value="/myEfeiyi/address/listCity.do"/>',
            dataType: 'json',
            data: {
                provinceId: pid
            },
            success: function (data) {
                var obj = eval(data);
                var rowHtml = "";
                rowHtml += "<option value='请选择'>请选择所在区县</option>";
                for (var i = 0; i < obj.length; i++) {
                    rowHtml += "<option value='" + obj[i].id + "'>" + obj[i].name + "</option>";
                }
                $("#cityVal" + addressId).append(rowHtml);
                $("#cityVal" + addressId + " option[value='" + v + "']").attr("selected", "selected");
                district(v, addressId);
            },
        });
    }

    //编辑收货地址
    function openAddress(addressId,consignee,province,provinceId,city,cityId,district,districtId,details,phone) {
        $("#consignee").val(consignee);
        $("#provinceOption").text(province);
        $("#provinceOption").val(provinceId);
        $("#cityOption").val(cityId);
        $("#cityOption").text(city);
        $("#districtOption").text(district);
        $("#districtOption").val(districtId);
        $("#details").val(details);
        $("#phone").val(phone);
        $("#addressId").val(addressId);
    }

    //删除地址
    function removeAddress(addressId){
        showConfirm('提示','是否删除',function(){
            $.ajax({
                type: 'post',
                async: false,
                url: '<c:url value="/order/removeAddress.do?addressId="/>'+addressId,
                dataType: 'json',
                data: {},
                success: function (data) {
                    if(data){
                        window.location.reload();
                    }
                },
            });
        })
    }

    //设置为默认地址
    function defaultAddress(addressId,consumerId) {
        $.ajax({
            type: 'post',
            async: false,
            url: '<c:url value="/order/defaultAddress.do"/>',
            dataType: 'json',
            data: {
                status: 2,
                id: addressId,

            },
            success: function (data) {
                if (data == true) {
                    window.location.reload();
                }
            },

        });
    }
</script>
</body>
</html>
