package com.jinwoo.android.camerabasic;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private final int REQ_PERMISSION = 100; // 권한 요청코드
    private final int REQ_CAMERA = 101; // 카메라 사용권한
    private final int REQ_GALLERY = 102; // 카메라 사용권한



    ImageView imageView;
    Button btnCamera, btnGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1 . 위젯을 세팅
        setWidget();
        // 2. 버튼관련 컨트롤러 활성화처리
        buttonDisable();
        // 3. 리스너계열을 등록
        setListener();
        // 4. 권한처리
        checkPermission();


    }


    // 위젯 세팅
    private void setWidget(){
        imageView = (ImageView)findViewById(R.id.imageView);
        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnGallery = (Button)findViewById(R.id.btnGallery);
    }
    // 리스터 세팅
    private void setListener(){
        btnCamera.setOnClickListener(clickListener);
        btnGallery.setOnClickListener(clickListener);
    }
    // 버튼 활성화하기
    private void buttonEnable(){
        btnCamera.setEnabled(true);
    }

    // 버튼 비활성화하기
    private void buttonDisable(){
        btnCamera.setEnabled(false);
    }

    private void init(){
        // 권한처리가 통과 되었을때만 버튼을 활성화 시켜준다.
        buttonEnable();
    }

    // 권한관리
    private void checkPermission() {
        // 버전체크해서 마시멜로우 보다 낮으면 런타임권한 체크를 하지 않는다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PermissionControl.checkPermission(this, REQ_PERMISSION)){
                init();
            }
        } else {
            init();
        }

    }



    // 사진촬영후 임시로 저장할 파일 공간
    Uri fileUri = null;
    // 리스너 정의
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.btnCamera:
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 누가 버전부터 기본 Action Image Capture 로는 처리안됨
                    // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // 저장할 미디어 속성을 정의하는 클래스
                        ContentValues values = new ContentValues(1);
                        // 속성중에 파일의 종류를 정의
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        // 전역변수로 정의한 fileUri에 외부저장소 컨텐츠가 있는 Uri를 임시로 생성해서 넣어준다.
                        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        // 위에서 생성한 fileUri를 사진저장공간으로 사용하겠다고 설정 (인텐트에 담는다.)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        // Uri에 읽기와 쓰기 권한을 시스템에 요청(시스템으로 넘겨준다.)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    // --- 여기 까지 컨텐트 uri 강제세팅 ---
                    startActivityForResult(intent, REQ_CAMERA);
                    break;
                case R.id.btnGallery :
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // 외부저장소에 있는 content uri를 다 가져오겠다!! Action_PICK : 액티비티

                    // 이미지 여러개 가져오는 플래그
                     intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                    intent.setType("image/*"); // 외부 저장소에 있는 이미지만 가져오기 위한 필터링.
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQ_GALLERY);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQ_CAMERA :
                if(requestCode == REQ_CAMERA && resultCode == RESULT_OK){ // 사진 확인처리
                    // 누가 버전일 경우만 getData()에 null이 넘어올 것이다.
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            fileUri = data.getData();
                    }
                    if(fileUri != null) {
                        imageView.setImageURI(fileUri);
                    }else{
                        Toast.makeText(this,"사진 파일이 없습니다",Toast.LENGTH_LONG);
                    }
                }
                break;
            case REQ_GALLERY:
                if( resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    Glide.with(this).load(fileUri).into(imageView);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_PERMISSION){
            if(PermissionControl.onCheckResult(grantResults)){
                init();
            } else {
                Toast.makeText(this, "권한을 허용하지 않으면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
