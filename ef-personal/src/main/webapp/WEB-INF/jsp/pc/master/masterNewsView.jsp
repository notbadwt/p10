<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/6/29
  Time: 15:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script src="<c:url value='/resources/plugins/ckeditor/ckeditor.js'/>" ></script>
    <script src="<c:url value='/scripts/jquery-form.js'/>" ></script>
    <link href="<c:url value="/scripts/upload/uploadify.css"/>" rel="stylesheet"/>
    <style>
        .am-modal-dialog {
            background: transparent
        }

        .am-modal-dialog img {
            width: 100%
        }
    </style>
</head>
<body>
<div class="am-cf am-padding">
    <div class="am-fl am-cf"><strong class="am-text-primary am-text-lg">传承人资讯</strong>/
        <small>Master News</small>
    </div>
</div>

<hr/>

<div class="am-g">

    <div class="am-u-sm-12 am-u-md-4 am-u-md-push-8"><%--少了这个div就出错--%>
    </div>

    <div class="am-u-sm-12 am-u-md-8 am-u-md-pull-4">
        <form action="<c:url value="/basic/xm.do"/>" method="post" class="am-form am-form-horizontal">
            <input type="hidden" name="masterId" value="${master.id}">
            <div class="am-form-group">
                <label for="title" class="am-u-sm-3 am-form-label" >标题</label>

                <div class="am-u-sm-9">
                    <input type="text" id="title" name="title" disabled="disabled" placeholder="标题" value="${object.title}" required>
                    <%--<small>输入你要保存的类型</small>--%>
                </div>
            </div>
            <div class="am-form-group">
                <label for="dataSource" class="am-u-sm-3 am-form-label">来源</label>
                <div class="am-u-sm-9">
                    <input type="text" id="dataSource" name="dataSource" disabled="disabled" placeholder="资讯来源" value="${object.dataSource}" >
                </div>
            </div>
            <div class="am-form-group">
                <label for="brief" class="am-u-sm-3 am-form-label">简介</label>
                <div class="am-u-sm-9">
                        <textarea id="brief" name="brief" disabled="disabled" placeholder="请输入简介" required
                                  value="${object.brief}">${object.brief}</textarea>
                </div>
            </div>

            <div class="am-form-group">
                <label for="content" class="am-u-sm-3 am-form-label">资讯内容</label>

                <div class="am-u-sm-9">
                        <textarea id="content" name="content" disabled="disabled" class="ckeditor" placeholder="请输入资讯内容" required
                                  value="${object.content}">${object.content}</textarea>
                </div>
                <br>
            </div>

            <div id="tagGroup">

            </div>
            <div class="am-form-group">
                <div class="am-u-sm-9 am-u-sm-push-3">
                    <button type="submit" disabled="disabled" class="am-btn am-btn-primary">保存</button>
                </div>
            </div>
        </form>

    </div>
</div>
<script src="<c:url value="/scripts/upload/jquery.uploadify.js"/>"></script>
<script>


    var  option = {
        type:"post",
        clearForm:true,
        dataType:"json",
        success:function(data){
            var json = parseJSON(data);
            if(json.result=="true"){
                alert("保存成功!");
            }
        }
    };

    function  toSub(){
        var f = confirm("保存成功后将不允许修改，若需修改请联系客服!");
        if(f) {
            $("form").submit();
        }
    }
    $(function(){
        $('#btn_upload').uploadify({
            uploader: '<c:url value="/master/uploadify.do?id=${id}"/>',            // 服务器处理地址
            swf: '<c:url value="/scripts/upload/uploadify.swf"/>',
            buttonText: "大师轮播图",                 //按钮文字
            buttonClass: "am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only",         //按钮样式
            buttonCursor: "hand",                    //鼠标指针悬停在按钮上的样子
            height: 34,                             //按钮高度
            width: 140,                              //按钮宽度
            auto: true,                          //自动上传
            multi: true,                            //多个文件上传
            scriptDate: {'status': '3'},
            checkExisting: true,                    //检查文件重复
            successTimeout: 1000000,                 //超时
            fileSizeLimit: '20MB',
            removeTimeout: 1,                        //移除时间
            fileTypeExts: "*.jpg;*.png;*.gif",           //允许的文件类型
            fileTypeDesc: "请选择图片文件",           //文件说明
            formData: {"imgType": "normal"}, //提交给服务器端的参数
            onUploadSuccess: function (file, data, response) {   //一个文件上传成功后的响应事件处理
                data = data.substring(1, data.length - 1)
                var masterBannerId = data.split(":")[0].trim();
                var imgUrl = data.split(":")[1];
                var imgName = data.split(":")[2];
                var sort = data.split(":")[3];
                var url = "http://tenant.efeiyi.com/" + imgUrl ;
                var trueUrl = "http://tenant.efeiyi.com/" + imgUrl ;

                var img = '<li style="float: left;margin-right: 10px;width:200px;" sort="'+sort+'" name="' + masterBannerId + '">' +
                          ' <dl style="margin-top: 6px;" >' +
                          '  <dt  style="width: 100%">' +
                          '    <a title="点击查看原图" href="javascript:void (0);" onclick="bigPic(\'' + trueUrl + '\')">' +
                          '      <img width="100%" name="' + masterBannerId + '"  src="' + url + '" alt="轮播图片">' +
                          '   </a>' +
                          '  </dt>' +
                          '  <dd style="width: 100%;text-align:center" >' +
                          '   <a href="javascript:void(0);" onclick="deleteBannerPicture(this,\'' + masterBannerId + '\')">' +
                          ' 删除' +
                          '</a>' +
                          '</dd>' +
                          '<dd style="width: 100%;text-align: center;" >' + imgName +
                          '</dd>' +
                          '</dl>' +
                          '</li>';

                $("#collapse-panel-1 ul").append(img);
            }
        });
        $("#btn_upload-button").css({"padding": "0em 0em", "text-align": "center"});
    });

    function getMasterProjectTag() {
        $.ajax({
            type: "get",
            url: '<c:url value="/basic/xmj.do?qm=listProjectTag_default&conditions=project.id:${projectId}"/>',
            cache: false,
            dataType: "json",
            success: function (data) {
                $("#tagGroup").append(generateTagSelect(data));
            }
        });
    }

    function generateTagSelect(data) {
        var html = "    <div class=\"am-form-group\" style='text-align: left'><label  class=\"am-u-sm-3 am-form-label\">作品标签</label><div class=\"am-u-sm-9\">"
        for (var i = 0; i < data.length; i++) {
            html += "<label class=\"am-checkbox-inline\"><input type=\"checkbox\" name=\"tag" + i + "\" value=\"" + data[i].id + "\" > " + data[i].value + "</label>"
        }
        html += "</div></div>"
        return html;
    }

    $().ready(function () {
        getMasterProjectTag();
    });



    function bigPic(url) {
        var img = new Image();
        img.src = url;
        img.onload = function () {
            // alert(img.width+"-->"+img.height);
            //  $("#your-modal .am-modal-dialog ").css({"margin-top":img.width/2,"margin-left":});
            $("#your-modal .am-modal-bd img").attr("title", "原图" + img.width + "x" + img.height);
        };
        $("#your-modal .am-modal-bd img").attr("src", url);

        $("#your-modal").modal();
    }

    function deleteBannerPicture(obj, divId) {

            $.ajax({
                type: "get",
                url: '<c:url value="/master/removeMasterBannerPicture.do"/>',
                cache: false,
                dataType: "json",
                data: {id: divId.trim()},
                success: function (data) {
                    if(data){
                       $("#collapse-panel-1 li[name='" + divId + "']").remove();
                    }else {
                        alert("删除失败，请联系客服!");
                    }
                }
            });
    }



</script>

</body>
</html>
