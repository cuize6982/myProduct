<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport"> 
<title>位置poi</title>
<link href="<%=request.getContextPath()%>/page/map/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=9ZqUjBdvxyLHxdl2vnv0XhSe"></script>
</head>

<body>
<div id="main11" class="main" style="display: none">
  <div class="map_box">
    <div id="container" class="main_map">
      
    </div>
  </div>
</div>
</body>
</html>
<script type="text/javascript">
	document.getElementById("main11").style.display="block";
	var map = new BMap.Map("container");
	var point = new BMap.Point(${location});
	var marker = new BMap.Marker(point, { icon: new BMap.Icon("<%=request.getContextPath()%>/page/map/images/huoche_marker.gif", new BMap.Size(40, 40), { imageOffset: new BMap.Size(-5, 0) }) });//定义覆盖物
    map.centerAndZoom(point, 15);
    map.addOverlay(marker);
    var opts = {
        enableAutoPan: true,
        enableMessage: false
    }
    var pointInfo="<div style='height:22px;line-height:22px;color:#333;padding-top:5px;'>"+
    			  "<li style='width:240px;list-style:none;padding-top:5px;'><span>${poi}</span></li>"+
    			  "</div>";
    infoWindow = new BMap.InfoWindow(pointInfo, opts);
    map.openInfoWindow(infoWindow, point);
    marker.addEventListener("click", function () {
        map.openInfoWindow(infoWindow, point);
    });	  
</script>