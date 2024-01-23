package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import com.chenxian.language_platform.util.OAuth2Service;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

@Controller
public class GitHubOAuthController {

    @Autowired
    private OAuth2Service OAuth2Service;
    @Autowired
    private UserService userService;

    // 跳转到 GitHub 授权页面
    @RequestMapping("/login/github")
    public String loginGithub() {
        // 向 Github 驗證授權
        String auth = OAuth2Service.AUTH_URL;
        return "redirect:" + auth;
    }

    // GitHub 授权回调
    @RequestMapping("/secure/oauth2/callback/github")
    //@ResponseBody
    public String callbackGithub(@RequestParam("code") String code, HttpSession session) throws IOException {
        // 已有授權碼(code)之後，可以跟 Github 來得到 token (訪問令牌)
        // 有了 token 就可以得到客戶的公開資訊例如: userInfo
        // 1. 根據 code 得到 token
        String token = OAuth2Service.getGitHubAccessToken(code);

        // 2. 透過 token 裡面的 access_token 來取的用戶資訊
        String accessToken = OAuth2Service.parseAccessToken(token);

        // 3. 得到用戶在 Github 上的公開資料
        String userInfo = OAuth2Service.getUserInfoFromGitHub(accessToken);

        // 4. 利用 Gson 來分析資料
        GithubUser githubUser = new Gson().fromJson(userInfo, GithubUser.class);
        // 5. 檢查會員資料表中是否有此人, 若無則將該會員資料自動新增到資料表
        Optional<User> userOpt = userService.findALlUsers().stream()
                .filter(user -> user.getAuthType() != null && user.getAuthId() != null && user.getAuthType().equalsIgnoreCase("github") && user.getAuthId().equalsIgnoreCase(githubUser.id))
                .findFirst();

        UserRegisterRequest userRegisterRequest = null;
        if(userOpt.isEmpty()) {
            userRegisterRequest = new UserRegisterRequest(githubUser.email,"None",githubUser.name,"None","None","None","github",githubUser.id);
            userService.register(userRegisterRequest);
        }
        User user = null;
        user = userService.getUserByEmail(githubUser.email);

        // 6. 新增成功就自行自動登入 (例如: 建立 user 物件並存放到 session 中)
        session.setAttribute("user", user);

        // 7. 重導到登入成功頁面(前台首頁)
        return "redirect:/enjoyLearning/courses";
    }

    class GithubUser {
        public String login;
        public String id;
        public String name;
        public String email;

        @Override
        public String toString() {
            return "GithubUser [login=" + login + ", id=" + id + ", name=" + name + ", email=" + email + "]";
        }
    }
}

