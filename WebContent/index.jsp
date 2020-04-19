<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<title>基础数据上传</title>
<script src="${pageContext.request.contextPath}/js/jquery-1.9.1.js"></script>
<style type="text/css">
.bu {
	text-decoration: none;
	background: #4dc86f;
	color: #f2f2f2;
	padding: 10px 30px 10px 30px;
	font-size: 16px;
	font-family: 微软雅黑, 宋体, Arial, Helvetica, Verdana, sans-serif;
	font-weight: bold;
	border-radius: 3px;
	-webkit-transition: all linear 0.30s;
	-moz-transition: all linear 0.30s;
	transition: all linear 0.30s;
}

table {
	border-collapse: collapse;
	margin: 0 auto;
	text-align: center;
}

table td, table th {
	border: 1px solid #cad9ea;
	color: #666;
	height: 30px;
}

table thead th {
	background-color: #CCE8EB;
	width: 100px;
}

table tr:nth-child(odd) {
	background: #fff;
}

table tr:nth-child(even) {
	background: #F5FAFA;
}
</style>
</head>

<body style="text-align: center;">
	<div style="margin-top: 30px;">
		<button onclick="uploadUnit();" class="bu">上报组织机构</button>
		<label id="orgmsg" style="color: red;"></label>
	</div>
	<hr>

	<div style="margin-top: 30px;">
		<dvi>发送公文</dvi>
		<div align="center">
			<table border="1">
				<tr>
					<td>公文标题：</td>
					<td><input type="text" name="title" id="title"
						style="width: 300px;"></td>
				</tr>
				<!-- <tr>
					<td>收文单位ID：</td>
					<td><input type="text" name="recOrgID" id="recOrgID"
						style="width: 300px;"></td>
				</tr>
				<tr>
					<td>收文单位名称：</td>
					<td><input type="text" name="recOrgName" id="recOrgName"
						style="width: 300px;"></td>
				</tr> -->
			</table>
			<button onclick="sendOrg();" class="bu">发送</button>
			<label id="msg" style="color: red;"></label>
		</div>
	</div>
	<hr>

</body>

<script type="text/javascript">
	function uploadUnit() {
		var opt={"type":"org"};
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/uploadUnit",
			contentType : "application/json;charset=utf-8",
			datatype : "JSON",
			data:JSON.stringify(opt),
		
			success : function(obj) {
				if ("success" == obj.msg) {
					$('#orgmsg').html('同步成功');
				} else {
					$('#orgmsg').html('同步失败');
				}

			},
			error : function(obj) {
				alert(obj.msg);
			}
		});
	}

	function sendOrg() {
		$('#msg').html('');
		var title = $('#title').val();
		var data = {"type":"send","title":title,"docMark":"123","createTime":"2019-5-6","is_Content":"1","contentFilePath":"E:/file/zw.docx","is_atta":"1","attaFilePath":"E:/file/help.pdf,E:/file/fj.docx"};
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/uploadUnit",
			contentType : "application/json;charset=utf-8",
			datatype : "JSON",
			data : JSON.stringify(data),
			success : function(obj) {
				if ("success" == obj.msg) {
					$('#msg').html('发送公文成功');
				} else {
					$('#msg').html('发送公文失败');
				}
			},
			error : function(obj) {
				alert(obj.msg);
			}
		});
	}
</script>
</html>