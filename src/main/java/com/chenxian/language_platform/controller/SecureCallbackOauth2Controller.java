package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import com.chenxian.language_platform.util.OAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class SecureCallbackOauth2Controller {
    @Autowired
    private OAuthService oauthService;
    @Autowired
    private UserService userService;
    @GetMapping("/secure/oidc/login/google")
    public String googleLogin() {
        // 向 Google 驗證授權
        String responseType = "code";
        String scope = "email openid profile address phone";
        String authURL = oauthService.getAuthorizationUrl(responseType, scope);
        return "redirect:" + authURL;
    }

    @RequestMapping("/secure/oidc/callback/google")
    public String callbackGoogle(@RequestParam("code") String code, HttpSession session) throws Exception {
        // 已有授權碼(code)之後，可以跟 Google 來得到 token (訪問令牌)
        // 有了 token 就可以得到客戶的公開資訊例如: userInfo
        // 1. 使用 code 獲取 token
        JSONObject tokenInfo = oauthService.getTokenInfo(code);
        String accessToken = tokenInfo.getString("access_token");

        // 2. 使用 People API 獲取用戶資訊
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        PeopleService peopleService = new PeopleService.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Your Application Name")
                .build();

        Person profile = peopleService.people().get("people/me")
                .setPersonFields("names,emailAddresses,birthdays,addresses")
                .execute();
        String authId = profile.getResourceName(); // 唯一識別
        String userName = profile.getNames().get(0).getDisplayName();
        String userEmail = null;
        if (profile.getEmailAddresses() != null && !profile.getEmailAddresses().isEmpty()) {
            userEmail = profile.getEmailAddresses().get(0).getValue(); // 假設用戶至少有一個電子郵件地址
        }
        // 3. 檢查會員資料表中是否有此人, 若無則將該會員資料自動新增到資料表
        Optional<User> userOpt = userService.findALlUsers().stream()
                .filter(user -> user.getAuthType() != null && user.getAuthId() != null && user.getAuthType().equalsIgnoreCase("google") && user.getAuthId().equalsIgnoreCase(authId))
                .findFirst();

        UserRegisterRequest userRegisterRequest = null;
        if(userOpt.isEmpty()) {
            userRegisterRequest = new UserRegisterRequest(userEmail,"None",userName,"None","None","None","google",authId);
            userService.register(userRegisterRequest);
        }
        User user = null;
        user = userService.getUserByEmail(userEmail);

        // 4. 新增成功就自行自動登入 (例如: 建立 user 物件並存放到 session 中)
        session.setAttribute("user", user);

        // 5. 重導到登入成功頁面(前台首頁)
        return "redirect:/enjoyLearning/courses"; // 重定向到主頁面
    }
}
