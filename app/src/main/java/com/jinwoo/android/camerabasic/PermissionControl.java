package com.jinwoo.android.camerabasic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * 권한 처리를 담당하는 클래스
 *
 * 권한 변경시 PERMISSION_ARRAY의 값만 변경해주면 된다.
 *
 */

public class PermissionControl {
    // 요청할 권한 목록
    public static final String PERMISSION_ARRAY[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    //권한 처리 수정
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(Activity activity, int req_permission){
        // 1.1 런타임 권한 체크
        // 위에 설정할 권한을 반복문을 돌면서 처리한다.
        boolean permCheck = true;
        for(String perm : PERMISSION_ARRAY){
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                permCheck = false;
                break;
            }
        }
        // 1.2 퍼미션이 하나라도 false이면 사용자에게 권한 체크를 요청한다.
        if(!permCheck){
            activity.requestPermissions(PERMISSION_ARRAY, req_permission); // 리퀘스트 창을 팝업해서 보여준다.
            return false;
        } else {
            return true;
        }
    }

    // 권한체크 후 콜백처리
    public static boolean onCheckResult(int[] grantResults) {
        boolean checkResult = true;
        // 권한처리 결과값울 반복문을 돌면서 확인한 후 하나라도 승인되지 않았다면 false를 리턴해준다.
        for(int result : grantResults){
            if(result != PackageManager.PERMISSION_GRANTED){
                checkResult = false;
                break;
            }
        }
        return checkResult;
    }


}
