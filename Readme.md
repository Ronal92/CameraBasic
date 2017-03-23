[ Date : 2017. 02. 10 ]

					---------------------- Today's Topic -----------------------
									(1) Observer 패턴
									(2) 안드로이드 사진 앱 만들기
					------------------------------------------------------------

프로젝트명 : [CameraBasic]

내용 : 안드로이드 시스템에서 지원하는 카메라 기능과 사진첩(갤러리)에서 사진을 불러오는 기능 구현.

#1. Observer 패턴

Observer 패턴은 문자 그대로 "관찰자"를 나타냅니다. 

**********************************************************************
상태를 가지고 있는 **주체 객체**와 상태의 변경을 알아야 하는 **관찰 객체**가 존재한다.
서로의 정보를 넘기고 받는 과정에서 정보의 단위 / 객체들의 규모 / 각 객체들의 관계 등이 복잡 혹은 커질수록 코드의 복잡성이 증가됩니다. 이 복잡성에 가이드라인을 제시해 주는 것이 바로 Observer 패턴입니다.
************************************************************************

즉 간단히 말해서, Observer는 계속 서버를 관찰하여 서버에 변경사항이 있는지를 체크하고 변경사항이 발생하면 각 Observer들에게 내용을 업데이트 합니다.

##1.1 코드 전체 흐름


간단히 채팅 서버를 구현하였습니다. "KakaoTalkServer"를 통해서 메시지가 전송이 되면 각 "Student" 옵저버에게 메시지가 전달 받습니다. 


![](http://i.imgur.com/nQ3VL5a.png)

(1) New Student() 인스턴스를 생성하면서 Student 생성자는 자기 자신과 서버가 서로 상호 참조를 하게 합니다.

(2) 서버에 메시지를 보냅니다.- sendMessage()

(3) 서버에서 옵저버(Student)에게로  변경사항이 있음을 알려줍니다.(콜백) – update()

(4) 옵저버 (Student) 는 변경사항을 화면에 보여줍니다.(showMessage)


##1.2 코드

[MainDesignPattern.java]
![](http://i.imgur.com/oRrHxCH.png)

[KakaoTalkServer.java]
![](http://i.imgur.com/bJsSIzC.png)

[Student.java]
![](http://i.imgur.com/1fTg1om.png)


-----------------------------------------------------

#2 안드로이드 사진 앱 만들기

![](http://i.imgur.com/oGx4KPS.png)

기본적인 UI는~


##2.1 카메라 접근 권한 설정.

AndroidManifest.xml에 다음 3가지 권한을 추가해야 합니다.


    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.MEDIA_CONTENT_CONTROL"/>
    <uses-permission android:name="android.permission.CAMERA"/>

- WRITE_EXTERNAL_STORAGE : 외부 메모리에 쓰기 권한 부여.

- MEDIA_CONTENT_CONTROL : 애플리케이션이 현재 작동하고 있는 콘텐트 내용을 알게 하고 제어할 수 있게 합니다.

- CAMERA : 카메라 장치에 접근 권한 부여.

##2.2 카메라 앱으로 창 전환하기

안드로이드에서 기본으로 사용되는 사진 어플을 이용할 겁니다.
인텐트를 생성하여 **MediaStore.ACTION_IMAGE_CAPTURE** 를 담아 시스템에 보내면
카메라 앱 화면으로 전환됩니다.
이 때, 사진이 캡쳐되고 저장하는 것을 처리하기 위해 request code로 REQ_CAMERA 키 값을 줍니다.

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQ_CAMERA);

##2.3 누가 버전 이상부터 달라진 카메라 촬영 처리.

안드로이드 7.0 이상 버전부터는 캡쳐된 사진이 갤러리에 자동으로 저장되지 않습니다. 따라서 외부 저장소 접근 권한을 가지고 저장할 Uri를 직접 지정해 주어야 합니다. ㅠㅠ 

여기에는 다른 어플리케이션의 데이터를 가져오는 개념이 들어갑니다. **week4-2.md의 2. A application에서 B application에 있는 데이터**를 사용하는 경우를 참고하면 도움이 됩니다.

[https://github.com/Ronal92/MyFastCampus/blob/master/4%EC%A3%BC%EC%B0%A8/week4-2.md](https://github.com/Ronal92/MyFastCampus/blob/master/4%EC%A3%BC%EC%B0%A8/week4-2.md)

###2.3.1 코드 ( 카메라 액티비티 호출하기 )

[MainActivity.java -- View.OnClickListener anonymous 클래스]
![](http://i.imgur.com/jjl5616.png)

(1) ContentValues는 Cursor와는 반대로 자료를 DB에 저장할 용도입니다. 여기서는 미디어로써 저장할 공간입니다.

(2) put(Column 이름, 입력할 값)으로 데이터베이스 테이블에 파일의 종류를 입력합니다.


(3) getContentResolver()로 ContentResolver를 호출합니다. insert(URI uri, ContentValues values)는 한 개의 record를 추가하는 메소드로서, 추가된 record의 uri를 반환합니다.

(4) putExtra 메소드를 사용해서 카메라 액티비티가 저장할 이미지 경로를 직접 지정한후 액티비티를 호출합니다. 인텐트에 fileUri 정보를 담고 카메라 액티비티는 이 URI 에 입력된 경로에 촬영한 이미지를 저장합니다.

(5) addFlags()는 디바이스의 내장 메모리 교유 영역에 접근하기 때문에 읽기와 쓰기 권한을 요청하기 위한 메소드입니다.

###2.3.2 코드 ( 호출된 카메라 액티비티에서 반환 )

[MainActivity.java -  onActivityResult()]
![](http://i.imgur.com/Dc5kCkX.png)

(1) 캡쳐된 사진을 저장할 경우를 처리하기 위한 코드입니다. REQ_CAMERA(intent가 사진촬영 activity로 전환되었다가 다시 왔다!)와 RESULT_OK(사용자가 사진 저장을 눌렀다)를 확인합니다.

(2) 롤리팝 이하 버전에서는 data.getData() 메소드를 이용하여 사진이 저장되어있는 uri를 가져옵니다.

(3) 롤리팝 이상 버전에서는 호출된 카메라 액티비티가 종료되면 그 반환값을 사용하지 않고 자신이 전달한 이미지 경로에서 이미지 파일을 사용해야 합니다. 저장되어있는 fileUri를 직접 imageView에 세팅해줍니다.


##2.4 갤러리에서 사진 가져오기

###2.4.1 코드 ( 갤러리 액티비티 호출하기 )

[MainActivity.java -- View.OnClickListener anonymous 클래스]
![](http://i.imgur.com/WJ5CnVc.png)

(1) Intent.ACTION_PICK은 선택된 이미지를 가져옵니다. 즉 EXTERNAL_CONTENT_URI에 있는 이미지를 가져올 것을 intent에 알려줍니다.

(2) Intent.EXTRA_ALLOW_MULTIPLE은 갤러리에서 이미지 여러개를 가져오기 위해 사용합니다.

(3) intent.setType("image/*")는 외부 저장소에 있는 이미지만을 가져오기 위해서 image/ 아래 있는 사진들만 가져옵니다.

(4) 결과를 리턴하는 startActivityForResult를 호출합니다.



###2.4.2 코드 ( 호출된 갤러리 액티비티에서 반환 )

[MainActivity.java -  onActivityResult()]

![](http://i.imgur.com/5C68ePK.png)

--> data.getData() 메소드를 이용하여 사진이 저장되어있는 uri를 불러오고 이미지 로딩 라이브로리(glide)로 화면에 출력합니다.

>> Glide 관련 링크 : [https://github.com/Ronal92/MyFastCampus/blob/master/4%EC%A3%BC%EC%B0%A8/week4-3.md](https://github.com/Ronal92/MyFastCampus/blob/master/4%EC%A3%BC%EC%B0%A8/week4-3.md)



