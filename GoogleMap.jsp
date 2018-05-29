
<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ page import = "sensor.AccelerationDAO" %>
<%@ page import = "JSP.LocationDAO" %>
<%@ page import = "JSP.Location" %>
<%@ page import = "java.io.PrintWriter" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.*" %>
<% request.setCharacterEncoding("utf-8");%>


<!DOCTYPE html>
<html>


<head>
<style>
       #map {
        height: 400px;
        width: 100%;
       }
    </style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width" , initial-scale="1">
<title>Smart-Helmet</title>
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/custom.css">
<style type="text/css">
	a, a:hover {
		color: #000000;
		text-decoration: none;
	}
</style>
<script language=javascript>
	function btn_click(str){
		if(str=="search"){
			frm1.action="GoogleMap.jsp";
		}else if(str=="calling"){
			frm1.action="sendMessageforCalling.jsp";
		}else{
			
		}
	}
</script>
</head>
 
<body> 

<% 

String userID = null;
if (session.getAttribute("userID") != null) {
	userID = (String) session.getAttribute("userID");
}

 String url = "jdbc:mysql://localhost/wbkim11";
 String SQLid = "wbkim11";
 String SQLpw = "q1w2e3r4";

 Connection conn = null;
 Statement stmt = null;
 ResultSet rs = null;
 double DataX;
 double DataY; 
 String Name;
    //여기 별표
    Class.forName("com.mysql.jdbc.Driver");
    conn = DriverManager.getConnection(url, SQLid, SQLpw);
    stmt = conn.createStatement();
    if(request.getParameter("condition") == null)
    {
    	rs = stmt.executeQuery("select * from locationinfo WHERE 1;");
    }else{
    	Name = request.getParameter("condition");
    	rs = stmt.executeQuery("select * from locationinfo WHERE userID='"+Name+"';");
    	
    }
    
    boolean flags = false;
   // while(rs.next()){
    	//rs.next()
   // }
    LocationDAO locationdao = new LocationDAO();
    Location user = locationdao.selectLocation(userID);
   
    if(rs.next() == false)
	{
    	PrintWriter script = response.getWriter();
		script.println("<script>");
		script.println("alert('존재하지 않는 ID를 입력하셨습니다.');");
		script.println("history.back();");
		script.println("</script>");
		script.close();
		rs = stmt.executeQuery("select * from locationinfo WHERE 1;");
	}
    Name = rs.getString("name");
    DataX = rs.getDouble("locationx");
    DataY = rs.getDouble("locationy");
%>

<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
					aria-expanded="false">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="main.jsp">Smart-Helmet</a>
			</div>
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav">
					<li><a href="main.jsp">메인</a></li>
					<li><a href="bbs.jsp">관리자게시판</a></li>
					<li><a href="bbs2.jsp">근로자게시판</a></li>
					<li><a href="Attend.jsp">출근부</a></li>
					<li><a href="GoogleMap.jsp">근로자 위치 검색</a></li>
				</ul>
				<%
				if (userID == null) {
				%>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-haspopup="true"
						aria-expanded="false">접속하기<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="login.jsp">로그인</a></li>
							<li><a href="join.jsp">회원가입</a></li>
						</ul>
					</li>
				</ul>
				<%
				}
				else {
				%>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-haspopup="true"
						aria-expanded="false">회원관리<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="logoutAction.jsp">로그아웃</a></li>
						</ul>
					</li>
				</ul>
				<%
				}
				%>
			</div>
		</div>
	</nav>
<form name="frm1" method="post">
			<input type="text" size="20" name="condition"/>&nbsp;
            <input type="submit" value="검색" onclick='btn_click("search");'/>
			<input type="submit" value="호출" onclick='btn_click("calling");'/>
</form>


	

<div id="map"></div>
    <script>
      function initMap() {
        var uluru = {lat: <%=DataX%>, lng: <%=DataY%>};
        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 15,
          center: uluru
        });


    	 
    	 // var map = new google.maps.Map(document.getElementById('map'), initMap);
    	  
    
    	  var marker = new google.maps.Marker({ 
    	            position: uluru, 
    	            map: map,
    	            title: "근로자 " + '<%=Name%>'
    	  }); 
    	  
    	  var infowindow = new google.maps.InfoWindow( 
    	          { 
    	            content: "근로자 " + '<%=Name%>', 
    	            maxWidth: 300 
    	          } 
    	  ); 

    	  google.maps.event.addListener(marker, 'click', function() { 
    	  infowindow.open(map, marker); 
    	  }); 
      }
    	     </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAYB_9N0ldYwuJ9qvlDeHn2RIHwKvsXDMw&callback=initMap">
    </script>

	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="js/bootstrap.js"></script>

</body> 


</html> 
