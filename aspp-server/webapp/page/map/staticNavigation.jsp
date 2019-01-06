<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport"> 
<title>地图导航结果</title>
<link href="<%=request.getContextPath()%>/page/map/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=9ZqUjBdvxyLHxdl2vnv0XhSe"></script>
</head>

<body>
<div class="main">
  <div class="map_box">
    <div id="container" class="main_map">
      
    </div>
  </div>
</div>
</body>
</html>
<script type="text/javascript">
	var map = new BMap.Map("container");
	var statLocation = new BMap.Point(${statLocation});
    var currentLocation = new BMap.Point(${currentLocation});
    var endLocation = new BMap.Point(${endLocation});
  //出发地到当前地路线
	var driving = new BMap.DrivingRoute(map,{
			 renderOptions: {map: map, autoViewport: true},
    		 onPolylinesSet: function(routes){   
				var route = routes[0];     
				var polyline = route.getPolyline();
				polyline.setStrokeColor("green");
			 },
			 onMarkersSet:function(pois){  
				 //移除目的地的覆盖物
				var poi = pois[1];  
				var marker = poi.marker;
			    map.removeOverlay(marker);
			    if(endLocation.lng==0&&endLocation.lat==0){
			    	 //添加自己的覆盖物到起始地点
					var poi = pois[1];  
					var marker = poi.marker;
					var icon = new BMap.Icon("<%=request.getContextPath()%>/page/map/images/car.png",marker.getIcon().size,{anchor:new BMap.Size(marker.getIcon().anchor.width+3,marker.getIcon().anchor.height-2)});
					marker.setIcon(icon);
					map.addOverlay(marker);
			    }
			 }
		 });
	driving.search(statLocation, currentLocation);
	if(endLocation.lng!=0&&endLocation.lat!=0){
		 //当前路线到目的地路线
		var driving1 = new BMap.DrivingRoute(map, {
				 renderOptions: {map: map, autoViewport: true},
				 onPolylinesSet: function(routes){   
						var route = routes[0];     
						var polyline = route.getPolyline();
						polyline.setStrokeColor("red");
				 },
				 onMarkersSet:function(pois){ 
					 //添加自己的覆盖物到起始地点
					var poi = pois[0];  
					var marker = poi.marker;
					var icon = new BMap.Icon("<%=request.getContextPath()%>/page/map/images/car.png",marker.getIcon().size,{anchor:new BMap.Size(marker.getIcon().anchor.width+3,marker.getIcon().anchor.height-2)});
					marker.setIcon(icon);
					map.addOverlay(marker);
				 }
			 });
		driving1.search(currentLocation, endLocation);	
	}	
</script>