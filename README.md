專題名稱 : 享學 EnjoyLearing ( 線上語言學習平台 ) 

專題動機   
1.個性化學習體驗：每個人的學習方式和興趣都有所不同。透過個性化的課程和內容，讓學習者根據自己的喜好、學習速度和目標來學習語言。 
2.社群互動性：語言學習不只是學習單詞和語法，更是一種文化和社會互動的過程。希望能提供一個討論區，讓學習者可以交流學習心得，分享文化經驗，甚至找到語言學習的夥伴。 

專案架構 
SpringBoot -> Controller，Dao，Service 三層式架構 
渲染模板 Thymeleaf 
主要分為前台 ( 用戶 ) ，後台 ( 管理員 ) 

使用技術 
後端 : Java , SpringBoot , Thymeleaf 
前端 : Html Css JavaScript , JQuery , AJAX , BootStrap 5  
套件 : Lombok  , SweetAlert2  
伺服器 : Tomcat 
資料庫 : MySQL 
資料庫交互 : NamedParameterJdbcTemplate , JPA  

伺服器佈署 ( 使用 AWS EC2 ) -> 第三方登入在 EC2 無法使用 ( 授權問題 ) 
請使用網站註冊登入 

前台首頁 
http://13.208.186.216:8080/enjoyLearning 

 
後台首頁 
http://13.208.186.216:8080/admin/login 
管理員帳號密碼

帳號 : 555@gmail.com  
密碼 : 111 


所有功能流程
====================================================

訪客: 首頁 ->  所有課程 -> 課程資訊 ( 加入購物車時導向登入頁面 ) ( 只能查看三個頁面 ) 

====================================================

前台所有功能 ( 用戶 )  

1. 註冊登入功能 

註冊( 使用 Spring Form Valid ) 

-> 註冊失敗導向註冊頁面顯示錯誤訊息 

-> 註冊成功導向登入頁面  

 

登入( 網站 Local ) ( 第三方登入 Google / Github )    

-> Local 需輸入驗證碼 ( 使用Java 2D ) / 第三方登入不用驗證碼 

-> 登入失敗導向燈入頁面顯示錯誤訊息 

-> 登入成功導向所有課程頁面 


忘記密碼 ( 使用 JavaMailSender / 有效 token 30秒連結 )  

-> 點擊信箱連結 

-> 連結失效導向登入頁面  

-> 連結有效前往更改密碼頁面 

-> 新密碼驗證失敗導向忘記密碼頁面顯示錯誤訊息 ( 空密碼 / 確認密碼驗證 ) 

-> 新密碼驗證成功( 使用AES加密存入資料庫 ) 

------------------------------------------------------------------------------------------------------------------------- 

2. 所有課程頁面功能 

自定義搜尋 ( 課程名稱 / 種類  ) 

收尋結果為空 -> 返回查無任何結果 

固定排序 ( 最熱門 ( 依課程購買人數 ) /  上架日期 / 課程時長 / 價格 ) 


點擊課程 

-> 前往課程資訊頁面 

-> 若用戶尚未購買則導向課程資訊頁面  

-> 加入購物車 

-> 若購物車已有該課程，點擊按鈕前往購物車 

-> 若購物車無該課程，點擊按鈕則成功添加課程至購物車 

-> 若用戶已購買則導向上課頁面 

-> 若課程已被後台刪除 ( 課程資訊頁面導向顯示課程不存在頁面 )  

------------------------------------------------------------------------------------------------------------------------- 

3. 購物車結帳功能 

購物車頁面 

-> 若購物車無任何商品時，顯示購物車一無所有 ( 前往所有課程頁面按鈕 ) 

-> 若購物車有商品時，顯示所有現有的購物細項 

-> 刪除購物車中課程功能 

-> 點擊結帳2秒後跳轉 

-> 若購物車包含被刪除的課程但也包含其他未被刪除的課程，結帳會成功但在訂單資訊不會看到被刪除的課程跟金額顯示 

-> 若購物車只有被刪除的課程會結帳失敗導向該課程不存在頁面 ( 課程已被後台刪除 ) 

-> 結帳成功導向完整訂單資訊頁面 

------------------------------------------------------------------------------------------------------------------------- 
 
4.優惠券結帳功能 

優惠券資訊頁面 

-> 判斷優惠券顯示 ( 判斷庫存 ( 若無庫存顯示已領完 ) / 判斷時間 ( 若超過截止日期則顯示已過期 ) ) ( 判斷時間後端使用精確的開始與結束時間 ) 


點擊領取 

-> 領取成功 ( 保存該優惠券到用戶持有的優惠券中 ) 

-> 領取失敗 ( 用戶已擁有該優惠券， 必須使用過該優惠券後才能再次領取 ) 

 
購物車頁面 

-> 若有商品時顯示查看優惠券 

-> 點擊查看列出當前用戶所持有的優惠券 

-> 持有的優惠券 ( 過期顯示白色 /  未開始顯示黃色 / 以過期顯示藍色 ) 

-> 點擊後使用 ( 折扣金額和折扣後的金額即時顯示在購物車頁面 ) 

-> 點擊取消使用 ( 顯示原價格在購物車頁面 ) 

-> 點擊使用優惠券結帳成功導向完整訂單資訊頁面 ( 包含折扣金額和折扣後的總金額 ) 

-> 若使用優惠券結帳已被刪除的課程，會導向課程不存在頁面，並且該次使用的優惠券也不會被扣除 

------------------------------------------------------------------------------------------------------------------------- 

5. 討論區功能 

討論區頁面 

-> 發布討論 ( 可輸入標題 / 語言 / 內容 ) 

-> 發布時間利用後端修改成 ( 1分鐘以下為剛剛發布，以上為分鐘，超過60分鐘為小時 ，超過24小時為天 ) 

-> 貼文底下留言功能 

-> 修改 / 刪除( 軟刪除 )討論留言 ( 依照發布者判斷，只有發布該討論或該留言的用戶才能操作 ) 

-> 按讚 ( 可以按讚也可以收回讚，貼文顯示讚數 ) 

------------------------------------------------------------------------------------------------------------------------- 

6. 用戶個人功能 

個人資訊頁面 

->  自動帶入用戶資訊 

-> 修改用戶資訊 


訂單紀錄頁面 

-> 顯示歷史訂單紀錄 

-> 紀錄為空顯示無任何訂單  

 
我的優惠券頁面 

-> 顯示所有該用戶所持有的優惠券 ( 判斷若過期為白色 / 未開始為黃色 / 有效為藍色 ) 

-> 刪除優惠券 

-> 優惠券為空顯示無任何優惠券 

====================================================

後台所有功能 ( 管理員 ) 

後台所有表單皆已套用 DataTables，提供多種匯出資訊功能 

1. 後台登入 

-> 判斷該帳戶權限是否為管理員 ( level 2 ) 

-> 成功登入導向後台管理頁面 

-> 登入失敗導向後台登入頁面顯示錯誤訊息 ( 無權限登入或帳號密碼錯誤 ) 

------------------------------------------------------------------------------------------------------------------------- 

2.  用戶資訊管理 

-> 顯示所有用戶資訊 ( 不包含密碼 ) 

-> 停權功能 ( 遭停權用戶無法登入，包括第三方登入用戶 ) 

------------------------------------------------------------------------------------------------------------------------- 

3. 訂單資訊 / 訂單詳情 

-> 顯示所有訂單資訊  

-> 點擊詳情按鈕 ( 列出該訂單的詳細項目資訊，包含優惠券折扣 ) 

------------------------------------------------------------------------------------------------------------------------- 

4. 課程管理 

-> 顯示所有當前未被刪除的課程 

-> 新增課程功能 ( 使用後端錯誤判斷 ) 

-> 新增失敗在新增 model 中顯示所有錯誤資訊 

-> 新增成功前端所有課程頁面可以查看 

-> 修改課程功能 ( 將資料庫資訊帶入 model ，送出修改後使用後端錯誤判斷 ) 

-> 修改失敗在修改 model 中顯示所有錯誤資訊 

-> 修改成功 ( 課程資訊列表中的修改時間欄位會記錄當前時間 ) 

-> 刪除課程功能 ( 使用軟刪除 ) 

------------------------------------------------------------------------------------------------------------------------- 

5. 優惠券管理 

-> 顯示所有當前未被刪除的優惠券 

-> 新增優惠券功能 ( 提供 FIXED / PERCENTAGE ) ( 使用後端錯誤判斷 ) 

-> 新增失敗在新增 model 中顯示所有錯誤資訊 

-> 刪除優惠券功能 ( 使用軟刪除 ) 

-> 上下架優惠券功能 ( 下架的優惠券不會顯示在前台頁面 ) 

------------------------------------------------------------------------------------------------------------------------- 

6. 發送優惠券 

-> 顯示所有用戶列表 ( 提供全選 ) 

-> 點擊發送優惠券 ( 列出當前所有未過期且上架中的優惠券 ) ( 提供全選 ) 

-> 發送錯誤判斷 

-> 若無選擇任何用戶則顯示用戶不可為空 

-> 若無選擇任何優惠券顯示優惠券不可為空 

-> 若無任何選擇的用戶和優惠券則顯示用戶和優惠券不可為空 

-> 三層判斷若無錯誤則發送成功 

------------------------------------------------------------------------------------------------------------------------- 

7. 課程銷售圖表分析 ( 使用 Google Charts ) 

-> 課程語言排行 ( 使用3D圓餅圖，依照當前用戶所購買的課程種類數量顯示 ) 

-> 課程銷售現況 ( 使用柱狀圖 ，依照課程被購買的數量顯示 ) 
