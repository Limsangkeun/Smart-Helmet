<%@page import="java.io.PrintWriter"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.google.android.gcm.server.*"%>
 
<%
	String name = "";
	if(request.getParameter("condition") == ""){
		PrintWriter script = response.getWriter();
		script.println("<script>");
		script.println("alert('ID를 입력하지 않으셨습니다..');");
		script.println("history.back();");
		script.println("</script>");
		script.close();
	}else{
		name = request.getParameter("condition");
	}

    ArrayList<String> token = new ArrayList<String>();    //token값을 ArrayList에 저장
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);    //메시지 고유 ID
    boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지
    int LIVE_TIME = 1;    //옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간
    int RETRY = 2;    //메시지 전송실패시 재시도 횟수
 
    
    String simpleApiKey = "AAAAcJQlc_E:APA91bEVI-b8I6nMV-M3CbKSfSVgxRl3CyPo-Okl2jRlPJNePeh21yyeNfvXT4qFuaOy7LdK52ZGdtDvq2T5MiUZPX7JWJi3Q4FZ68b9ALoHSC8LRi1Vh_BXkPS-K5F2S7O0l-zdnMIQ";
    String gcmURL = "https://android.googleapis.com/fcm/send";    
    Connection conn = null; 
    Statement stmt = null; 
    ResultSet rs = null;
    
    String msg = "야 튀어와";
    if(msg==null || msg.equals("")){
        msg="";
    }
  //  String[] TempMessage = msg.split("/");
  //  if(TempMessage[1].equals("CO")){
  //  	TempMessage[1] = "CO 농도가 높습니다 주의 요망";
   // }else if(TempMessage[1].equals("fall")){
    //	TempMessage[1] = "추락 가능성이 높습니다 주의 요망";
   // }
   // String Sendmessage = TempMessage[0]+"의 "+TempMessage[1];
    msg = new String(msg.getBytes("UTF-8"), "UTF-8");   //메시지 한글깨짐 처리
    
    try {
    	String jdbcUrl = "jdbc:mysql://localhost/wbkim11"; // MySQL 계정
    	String dbId = "wbkim11"; // MySQL 계정
    	String dbPw = "q1w2e3r4"; // 비밀번호        
        String sql = "SELECT * FROM `user` WHERE userID = '"+name+"' AND userAuthority = '근로자'"; // 등록된 token을 찾아오도록 하는 sql문
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
        stmt = conn.prepareStatement(sql);    
        rs = stmt.executeQuery(sql);
        //모든 등록ID를 리스트로 묶음
        if(rs.next()==false){
        	PrintWriter script = response.getWriter();
    		script.println("<script>");
    		script.println("alert('존재하지않는 ID나 관리자를 입력하셨습니다.');");
    		script.println("history.back();");
    		script.println("</script>");
    		script.close();
        }
        

        token.add(rs.getString("token"));

        conn.close();
        Sender sender = new Sender(simpleApiKey);
        Message message = new Message.Builder()
        .collapseKey(MESSAGE_ID)
        .delayWhileIdle(SHOW_ON_IDLE)
        .timeToLive(LIVE_TIME)
        .addData("message",msg)
        .build();
        MulticastResult result1 = sender.send(message,token,RETRY);
        if (result1 != null) {
            List<Result> resultList = result1.getResults();
            for (Result result : resultList) {
                System.out.println(result.getErrorCodeName()); 
            }
        }

        PrintWriter script = response.getWriter();
    	script.println("<script>");
    	script.println("alert('호출하였습니다.');");
    	script.println("history.back();");
    	script.println("</script>");
    	script.close();
    }catch (Exception e) {
    	//PrintWriter script = response.getWriter();
    	//script.println("<script>");
    	//script.println("alert('에러');");
    	//script.println("history.back();");
    	//script.println("</script>");
    	//script.close();
        e.printStackTrace();
    }
    
%>

