package com.indicsoft.merchandiseservices.api;

import android.content.Context;

/**
 * Created by anand on 28/1/16.
 */
public class ApiConstants {

    private Context context;

    public ApiConstants(Context context) {

        this.context = context;
    }

    //192.168.1.133:9000
    //
    public static final Long timeoutSeconds = 60l * 10l;
    //cloud url   http://205.147.101.217:9000/
    //local url http://192.168.1.107:9000/http://www.payrollhander.com
    public static final String BASEURL = "http://192.168.1.133:9000/";
    public static final String LOGIN_AUTHENTICATE = "authenticate";
    public static final String FORGOT_PASSWORD = "resetPassword";
    public static final String CHANGE_PASSWORD = "api/changePassword";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String DEVICEID = "deviceId";
    public static final String LOGOUT = "api/logout";
    public static final String PENDING_TASK = "api/pendingTask";
    public static final String COMPLETED_TASK = "api/completeSurvey";
    public static final String SUBMIT_SERVEY = "api/submitSurvey";
    public static final String QUESTION_DETAILS = "api/getQuestion?taskId=";
    public static final String GETPROJECT_LIST = "api/getProjectList";
    public static final String AddOutlet = "api/addOutlet";
    public static final String GETUSER_DATA = "api/getUserData";
    public static final String UPDAT = "api/updateProfile";

    public static final String BASE_CLINIC_APP = "http://192.168.1.132:8080";


    public ApiConstants() {
    }
}
