var config = {
    /*请求接口的基础路径*/
    baseUrl: "http://localhost:81/cmsapi/",
    /*html部署的地址*/
    webUrl:"http://localhost:81/cms/"
};
var loginDataStr = sessionStorage.getItem("loginData");
var loginData = {};
if (loginDataStr) {
    loginData = JSON.parse(loginDataStr);
}else {
    //没有登录的调到登录页面
    if (window.location.href.indexOf("login.html")<0){
        window.location.href = config.webUrl + "login.html";
    }
}


// 对于href连接后边是需要跟token值
function wrapUrl(url) {
    var result = "";
    if (url) {
        if (url.indexOf("?") > 0) {
            result = url+"&TOKEN="+loginData.token;
        }else {
            result = url+"?TOKEN="+loginData.token;
        }
    }
    return result;
}

//layer table全局默认设置
if (layui&&layui.table){
    layui.table.set({
        elem: '#tableData',
        height: 'full-170',
        headers:getLoginHeaders(),
        request: { pageName: 'pageNo',limitName: 'pageSize'},
        response: { statusCode:1},
        page: true,
        limits: [10, 30, 50],
        autoSort: false,
        parseData: function (result) { //res 即为原始返回的数据
            if (result&&result.code&& (result.code==20001 || result.code==20002)) {
                //过期登录
                window.location.href = config.webUrl + "login.html";
                return;
            }
            if (result&&result.code&& (result.code==20003)) {
                //权限不足
                layer.alert(result.message);
            }
            return {
                "code": result.code,
                "msg": result.message,
                "count": result.data&&result.data.totalCount?result.data.totalCount:0,
                "data": result.data&&result.data.result ? result.data.result : []
            };
        }
    });
    layui.table.on('sort(sortTable)', function (obj) {
        table.reload('tableData', {
            initSort: obj,
            where: {
                orderName: toLineName(obj.field),
                order: obj.type
            }
        });
    });
}

var _ajax = $.ajax;
$.ajax = function (opt) {
    var _success = opt && opt.success || function (a, b) {
    };
    var _opt = $.extend(opt, {
        success: function (result, states) {
            if (result&&result.code&& (result.code==20001 || result.code==20002)) {
                window.location.href = config.webUrl + "login.html";
                return;
            }
            if (result&&result.code&& (result.code==20003)) {
                layer.alert(result.message);
                return;
            }
            _success(result, states);

        }
    });
    _opt.headers = getLoginHeaders();
    if (opt['contentType'] == null) {
        //上传的时候contentType为false所以要判断
        _opt.headers['Content-Type'] = "application/json;charset=utf-8";
    }
    _opt.error = function () {
    };
    _ajax(_opt);
}

function getLoginHeaders() {
    return {TOKEN: loginData.token};
}
Vue.prototype.hasPermissions = function (param){
    var permissions = loginData.permissions;
    return permissions.indexOf(param)>=0;
};
Vue.prototype.hashRoles = function (param){
    var userRoles = loginData.userRoles;
    return userRoles.indexOf(param)>=0;
};

